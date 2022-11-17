package com.stereowalker.controllermod.client.controller;


import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;

import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControllerMapping implements Comparable<ControllerMapping> {

	private static final Map<UseCase, Map<ControllerMap.Button, ControllerMapping>> MAP = Maps.newHashMap();
	private static final Map<String, ControllerMapping> ALL = Maps.newHashMap();

	private final String descri;
	private final String category;
	//	private final InputConstants.Type type;
	public InputConstants.Key buttonOnKeyboardMouse; 
	//private final int buttonOnKeyboardMouse;
	private ImmutableMap<ControllerModel,String> buttonOnController;
	private final ImmutableMap<ControllerModel,String> defaultButtonOnController;
	private ImmutableMap<ControllerModel,InputType> inputType;
	private final UseCase useCase;
	private final boolean fromKeybind;

	private final boolean isAxis;
	private ImmutableMap<ControllerModel,Boolean> axisInverted;
	////
	public int downTicks;
	public boolean toggled;
	public boolean buttonDown;
	public float axis;

	private static final Map<String, Integer> CATEGORY_ORDER = Util.make(Maps.newHashMap(), (p_205215_0_) -> {
		p_205215_0_.put("key.categories.controller", 1);
		p_205215_0_.put("key.categories.on_screen_keyboard", 2);
		p_205215_0_.put("key.categories.movement", 3);
		p_205215_0_.put("key.categories.gameplay", 4);
		p_205215_0_.put("key.categories.inventory", 5);
		p_205215_0_.put("key.categories.creative", 6);
		p_205215_0_.put("key.categories.multiplayer", 7);
		p_205215_0_.put("key.categories.ui", 8);
		p_205215_0_.put("key.categories.misc", 9);
	});

	private ControllerMapping(String category, String description, InputConstants.Key buttonOnKeyboardMouse, Consumer<Map<ControllerModel,String>> buttonId, InputType inputType, boolean isAxisIn, boolean isAxisInvertedIn, UseCase useCase, boolean fromKeybindIn) {
		this.category = category;
		this.descri = description;
		ImmutableMap.Builder<ControllerModel,String> builder = ImmutableMap.builder();
		ImmutableMap.Builder<ControllerModel,InputType> inputTypeBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<ControllerModel,Boolean> axisInvertedBuilder = ImmutableMap.builder();
		Map<ControllerModel,String> builder2 = Maps.newHashMap();
		buttonId.accept(builder2);
		for (ControllerModel model : ControllerModel.values()) {
			if (!builder2.containsKey(model)) {
				builder2.put(model, " ");
			} else if (builder2.get(model).charAt(0) == '#') {
				builder2.put(model, model.getIdFromAlias(builder2.get(model).replace('#', ' ').strip()));
			}
			inputTypeBuilder.put(model, inputType == null ? InputType.PRESS : inputType);
			axisInvertedBuilder.put(model, isAxisInvertedIn);

		}
		builder.putAll(builder2);
		this.buttonOnController = builder.build();
		this.defaultButtonOnController = buttonOnController;
		this.inputType = inputTypeBuilder.build();
		this.buttonOnKeyboardMouse = buttonOnKeyboardMouse;
		this.useCase = useCase;
		this.fromKeybind = fromKeybindIn;
		this.isAxis = isAxisIn;
		this.axisInverted = axisInvertedBuilder.build();
		ALL.put(description, this);
		if (!MAP.containsKey(useCase))
			MAP.put(useCase, Maps.newHashMap());
		this.buttonOnController.forEach((key, val) -> MAP.get(useCase).put(key.getOrCreate(val), this));
	}

	public ControllerMapping(String category, String desc, InputConstants.Key buttonOnKeyboardMouse, Consumer<Map<ControllerModel,String>> buttonId, InputType inputType, UseCase useCase) {
		this(category, desc, buttonOnKeyboardMouse, buttonId, inputType, false, false, useCase, false);
	}

	public ControllerMapping(String category, String desc, Consumer<Map<ControllerModel,String>> buttonId, InputType inputType, UseCase useCase) {
		this(category, desc, null, buttonId, inputType, false, false, useCase, false);
	}

	public ControllerMapping(KeyMapping keybind, UseCase useCase) {
		this(keybind.getCategory(), keybind.getName(), keybind.key, (builder) -> {
			for (ControllerModel model : ControllerModel.values()) {
				builder.put(model, ControllerUtil.getControllerInputId(0));
			}
		}, InputType.PRESS, false, false, useCase, true);
	}

	/**
	 * Setup for keybind binding
	 * @param keybind
	 * @param buttonId
	 */
	public ControllerMapping(KeyMapping keybind, String newDesc, Consumer<Map<ControllerModel,String>> buttonId, UseCase useCase) {
		this(keybind.getCategory(), newDesc, keybind.key, buttonId, InputType.PRESS, false, false, useCase, true);
	}

	public ControllerMapping(KeyMapping keybind, Consumer<Map<ControllerModel,String>> buttonId, UseCase useCase) {
		this(keybind, keybind.getName(), buttonId, useCase);
	}

	/**
	 * Setup for axis binding
	 * @param category
	 * @param desc
	 * @param buttonId
	 * @param conflictContext
	 */
	public ControllerMapping(String category, String desc, Consumer<Map<ControllerModel,String>> buttonId, boolean isAxisInvertedIn, UseCase useCase) {
		this(category, desc, null, buttonId, null, true, isAxisInvertedIn, useCase, false);
	}

	@SuppressWarnings("resource")
	public KeyMapping getAttachedKebinding() {
		for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
			if (key.getName().equals(getDescripti())) {
				return key;
			}
		}
		return null;
	}

	public void bind(ControllerModel model, String key) {
		if (!(key.contains("button") || key.contains("axis_pos") || key.contains("axis_neg") || key.equals(" ") || key.contains("axis"))) {
			key = " ";
		}
		Map<ControllerModel,String> builder = Maps.newHashMap();
		builder.putAll(buttonOnController);
		builder.remove(model);
		builder.put(model, key);

		ImmutableMap.Builder<ControllerModel,String> builder2 = ImmutableMap.builder();
		builder2.putAll(builder);
		buttonOnController = builder2.build();
	}

	public void setInputType(ControllerModel model, InputType type) {
		Map<ControllerModel,InputType> builder = Maps.newHashMap();
		builder.putAll(inputType);
		builder.remove(model);
		builder.put(model, type);

		ImmutableMap.Builder<ControllerModel,InputType> builder2 = ImmutableMap.builder();
		builder2.putAll(builder);
		inputType = builder2.build();
	}

	/**
	 * Returns true if the supplied ControllerMapping conflicts with this
	 */
	public boolean same(ControllerMapping pBinding, ControllerModel model) {
		if (this.useCase != pBinding.useCase) {
			return false;
		}
		else return this.buttonOnController.get(model).equals(pBinding.buttonOnController.get(model));
	}

	public boolean isBoundToButton(ControllerModel model) {
		return !getButtonOnController(model).equals(" ") && !getButtonOnController(model).equals("> <");
	}

	public boolean isBoundToKey() {
		return buttonOnKeyboardMouse.getType() != null && buttonOnKeyboardMouse.getValue() != 0;
	}

	public String getDescripti() {
		return descri;
	}

	public InputConstants.Key getButtonOnKeyboardOrMouse() {
		return buttonOnKeyboardMouse;
	}

	public InputType getInputType(ControllerModel model) {
		return inputType.get(model);
	}

	public String getButtonOnController(ControllerModel model) {
		return buttonOnController.get(model);
	}

	public int getButtonOnControllerID(ControllerModel model) {
		return ControllerUtil.getControllerInputCode(getButtonOnController(model));
	}


	public void tick() {
		if (downTicks >= 0) downTicks++;
		buttonDown = true;
		if (isPressed()) toggled = !toggled;
	}

	public boolean isDown(ControllerModel model) {
		switch (inputType.get(model)) {
		case PRESS: return isPressed();
		case TOGGLE: return isToggled();
		case HOLD: return isHeld();
		}
		return false;
	}

	public boolean isHeld() {
		if (isAxis) return false;
		return buttonDown;
	}

	public boolean isPressed() {
		if (isAxis) return false;
		return downTicks == 1;
	}

	public boolean isToggled() {
		if (isAxis) return false;
		return toggled;
	}

	public boolean isAxis() {
		return isAxis;
	}

	public float getAxis() {
		return axis;
	}

	public void release() {
		downTicks = 0;
		buttonDown = false;
	}

	public boolean isFromKeybind() {
		return fromKeybind;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the defaultButtonOnController
	 */
	public String getDefault(ControllerModel model) {
		return defaultButtonOnController.get(model);
	}

	public void setToDefault() {
		buttonOnController = defaultButtonOnController;
	}

	public void setToDefault(ControllerModel model) {
		String key = defaultButtonOnController.get(model);
		Map<ControllerModel,String> builder = Maps.newHashMap();
		builder.putAll(buttonOnController);
		builder.remove(model);
		builder.put(model, key);

		ImmutableMap.Builder<ControllerModel,String> builder2 = ImmutableMap.builder();
		builder2.putAll(builder);
		buttonOnController = builder2.build();
	}

	public boolean isDefault(ControllerModel model) {
		return buttonOnController.get(model).equalsIgnoreCase(defaultButtonOnController.get(model));
	}

	@Override
	public int compareTo(ControllerMapping p_compareTo_1_) {
		if (this.category.equals(p_compareTo_1_.category)) return I18n.get(this.descri).compareTo(I18n.get(p_compareTo_1_.descri));
		Integer tCat = CATEGORY_ORDER.get(this.category);
		Integer oCat = CATEGORY_ORDER.get(p_compareTo_1_.category);
		if (tCat == null && oCat != null) return 1;
		if (tCat != null && oCat == null) return -1;
		if (tCat == null && oCat == null) return I18n.get(this.category).compareTo(I18n.get(p_compareTo_1_.category));
		return  tCat.compareTo(oCat);
	}

	/**
	 * @return the isAxisInverted
	 */
	public boolean isAxisInverted(ControllerModel model) {
		return axisInverted.get(model);
	}

	/**
	 * @param isAxisInverted the isAxisInverted to set
	 */
	public void setAxisInverted(ControllerModel model, boolean isAxisInverted) {
		Map<ControllerModel,Boolean> builder = Maps.newHashMap();
		builder.putAll(this.axisInverted);
		builder.remove(model);
		builder.put(model, isAxisInverted);

		ImmutableMap.Builder<ControllerModel,Boolean> builder2 = ImmutableMap.builder();
		builder2.putAll(builder);
		this.axisInverted = builder2.build();
	}

	public UseCase getUseCase() {
		return useCase;
	}

	public static void releaseAll() {
		for (ControllerMapping controllerMapping : ALL.values()) {
			controllerMapping.release();
		}
	}

	public static void handleUnbindAll() {
		for (ControllerMapping controllerMapping : ALL.values()) {
			ControllerMod.getInstance().getControllerHandler().addToPrevoiuslyUsed(controllerMapping);
		}
	}

	public static void resetMapping() {
		MAP.forEach((use, submap) -> submap.clear());
		for (ControllerMapping controllerMapping : ALL.values()) {
			controllerMapping.buttonOnController.forEach((key, val) -> MAP.get(controllerMapping.useCase).put(key.getOrCreate(val), controllerMapping));
		}
	}

	public static List<ControllerMapping> retrieveActiveMappings(Controller controller, List<UseCase> cases) {
		List<ControllerMapping> down = Lists.newArrayList();
		List<String> interactions = Lists.newArrayList();
		interactions.addAll(controller.getButtonsDown());
		interactions.addAll(controller.getAxesMoved());
		interactions.forEach(interaction -> cases.forEach(use -> {if (MAP.get(use) != null)down.add(MAP.get(use).get(controller.getModel().getOrCreate(interaction)));}));
		down.removeIf((bind) -> bind == null);
		return down;
	}

	@Override
	public String toString() {
		return getDescripti();
	}
}
