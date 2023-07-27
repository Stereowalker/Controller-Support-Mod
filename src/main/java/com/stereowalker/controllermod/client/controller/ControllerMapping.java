package com.stereowalker.controllermod.client.controller;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;
import com.stereowalker.controllermod.resources.ControllerModelManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ControllerMapping implements Comparable<ControllerMapping> {

	private static final Map<UseCase, Map<List<ControllerMap.Button>, ControllerMapping>> MAP = Maps.newHashMap();
	private static final Map<String, ControllerMapping> ALL = Maps.newHashMap();

	private final String descri;
	private final String category;
	//	private final InputConstants.Type type;
	public InputConstants.Key buttonOnKeyboardMouse; 
	//private final int buttonOnKeyboardMouse;
	private ImmutableMap<ResourceLocation,List<String>> buttonOnController;
	private final ImmutableMap<ResourceLocation,List<String>> defaultButtonOnController;
	private ImmutableMap<ResourceLocation,InputType> inputType;
	private final UseCase useCase;
	private final boolean fromKeybind;

	private final boolean isAxis;
	private ImmutableMap<ResourceLocation,Boolean> axisInverted;
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

	private ControllerMapping(String category, String description, InputConstants.Key buttonOnKeyboardMouse, Consumer<Map<ResourceLocation,List<String>>> buttonId, InputType inputType, boolean isAxisIn, boolean isAxisInvertedIn, UseCase useCase, boolean fromKeybindIn) {
		this.category = category;
		this.descri = description;
		ImmutableMap.Builder<ResourceLocation,List<String>> builder = ImmutableMap.builder();
		ImmutableMap.Builder<ResourceLocation,InputType> inputTypeBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<ResourceLocation,Boolean> axisInvertedBuilder = ImmutableMap.builder();
		Map<ResourceLocation,List<String>> builder2 = Maps.newHashMap();
		buttonId.accept(builder2);
		//System.out.println(description);
		builder2.put(ControllerModel.CUSTOM.defaultName, Lists.newArrayList(" "));
		inputTypeBuilder.put(ControllerModel.CUSTOM.defaultName, InputType.PRESS);
		for (Entry<ResourceLocation, ControllerModel> model : ControllerModelManager.ALL_MODELS.entrySet()) {
			if (model.getValue() == null) {
				ControllerMod.LOGGER.error("Cannot create mapping for "+description+" because "+model.getKey()+" is not regsitered in the controller model registry");
			} else if (model.getValue() != ControllerModel.CUSTOM) {
				if (!builder2.containsKey(model.getKey())) {
					builder2.put(model.getKey(), Lists.newArrayList(" "));
				} else {
					/* We're creating a new list object to sever the connection between the list
					 * here and the list the map has. This allows us to modify the list without affecting 
					 * the copy owned by the map. If we don't do this, any modifications to this list will modify
					 * every copy in the map
					*/
					List<String> before = Lists.newArrayList(builder2.get(model.getKey()));
					for (int i = 0; i < before.size(); i++) {
						if (before.get(i).charAt(0) == '#') {
							before.set(i, model.getValue().getIdFromAlias(before.get(i).replace('#', ' ').strip()));
						}
					}
					builder2.put(model.getKey(), before);
				}
				inputTypeBuilder.put(model.getKey(), inputType == null ? InputType.PRESS : inputType);
				System.out.println(model.getKey());
				axisInvertedBuilder.put(model.getKey(), isAxisInvertedIn);
			}
		}
		builder.putAll(builder2);
		this.buttonOnController = builder.build();
		System.out.println(this.buttonOnController.size());
		System.out.println();
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
		this.buttonOnController.forEach((key, val) -> {
			if (ControllerModelManager.ALL_MODELS.get(key) == null) {
				ControllerMod.LOGGER.error("Cannot create mapping for "+description+" because "+key+" is not regsitered in the controller model registry");
			} else {
				MAP.get(useCase).put(Lists.newArrayList(ControllerModelManager.ALL_MODELS.get(key).getOrCreate(Lists.newArrayList(val))), this);
			}
		});
	}

	public ControllerMapping(String category, String desc, InputConstants.Key buttonOnKeyboardMouse, Consumer<Map<ResourceLocation,List<String>>> buttonId, InputType inputType, UseCase useCase) {
		this(category, desc, buttonOnKeyboardMouse, buttonId, inputType, false, false, useCase, false);
	}

	public ControllerMapping(String category, String desc, Consumer<Map<ResourceLocation,List<String>>> buttonId, InputType inputType, UseCase useCase) {
		this(category, desc, null, buttonId, inputType, false, false, useCase, false);
	}

	public ControllerMapping(KeyMapping keybind, UseCase useCase) {
		this(keybind.getCategory(), keybind.getName(), keybind.key, (builder) -> {
			for (ResourceLocation model : ControllerModelManager.ALL_MODELS.keySet()) {
				builder.put(model, Lists.newArrayList(ControllerUtil.getControllerInputId(0)));
			}
		}, InputType.PRESS, false, false, useCase, true);
	}

	/**
	 * Setup for keybind binding
	 * @param keybind
	 * @param buttonId
	 */
	public ControllerMapping(KeyMapping keybind, String newDesc, Consumer<Map<ResourceLocation,List<String>>> buttonId, UseCase useCase) {
		this(keybind.getCategory(), newDesc, keybind.key, buttonId, InputType.PRESS, false, false, useCase, true);
	}

	public ControllerMapping(KeyMapping keybind, Consumer<Map<ResourceLocation,List<String>>> buttonId, UseCase useCase) {
		this(keybind, keybind.getName(), buttonId, useCase);
	}

	/**
	 * Setup for axis binding
	 * @param category
	 * @param desc
	 * @param buttonId
	 * @param conflictContext
	 */
	public ControllerMapping(String category, String desc, Consumer<Map<ResourceLocation,List<String>>> buttonId, boolean isAxisInvertedIn, UseCase useCase) {
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

	public void bind(ResourceLocation model, List<String> key) {
		for (int i = 0; i < key.size(); i++) {
			if (!(key.get(i).contains("button") || key.get(i).contains("axis_pos") || key.get(i).contains("axis_neg") || key.get(i).equals(" ") || key.get(i).contains("axis")
					|| key.get(i).contains("dpadup") || key.get(i).contains("dpaddo") || key.get(i).contains("dpadle") || key.get(i).contains("dpadri"))) {
				key.set(i, " ");
			}
		}
		Map<ResourceLocation,List<String>> builder = Maps.newHashMap();
		builder.putAll(buttonOnController);
		builder.remove(model);
		builder.put(model, key);

		ImmutableMap.Builder<ResourceLocation,List<String>> builder2 = ImmutableMap.builder();
		builder.forEach((k, v) -> {
			if (k != null) builder2.put(k,v);
			else ControllerMod.LOGGER.warn("Caught a null key with values {}", v);
		});
		buttonOnController = builder2.build();
	}

	public void setInputType(ControllerModel model, InputType type) {
		Map<ResourceLocation,InputType> builder = Maps.newHashMap();
		builder.putAll(inputType);
		builder.remove(modelKey(model));
		builder.put(modelKey(model), type);

		ImmutableMap.Builder<ResourceLocation,InputType> builder2 = ImmutableMap.builder();
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
		else return this.buttonOnController.get(model.getKey()).equals(pBinding.buttonOnController.get(model.getKey()));
	}

	public boolean isBoundToButton(ControllerModel model) {
		if (model == null)
			model = ControllerModel.CUSTOM;
		if (getButtonOnController(model) != null)
			return !getButtonOnController(model).get(0).equals(" ") && !getButtonOnController(model).get(0).equals("> <");
		else return false;
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
	
	ResourceLocation modelKey(ControllerModel model) {
		return model == null ? ControllerModel.CUSTOM.getKey() : model.getKey();
	}

	public InputType getInputType(ControllerModel model) {
		return inputType.get(modelKey(model));
	}

	public List<String> getButtonOnController(ControllerModel model) {
		List<String> buttons = buttonOnController.get(modelKey(model));
		if (buttons == null || buttons.isEmpty()) {
			return Lists.newArrayList(" ");
		}
		else {
			return buttons;
		}
	}

	public int[] getButtonOnControllerID(ControllerModel model) {
		int[] ids = new int[getButtonOnController(model).size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = ControllerUtil.getControllerInputCode(getButtonOnController(model).get(i));
		}
		return ids;
	}


	public void tick() {
		if (downTicks >= 0) downTicks++;
		buttonDown = true;
		if (isPressed()) toggled = !toggled;
	}

	public boolean isDown(ControllerModel model) {
		if (model != null && inputType.get(modelKey(model)) != null) {
			switch (inputType.get(modelKey(model))) {
			case PRESS: return isPressed();
			case TOGGLE: return isToggled();
			case HOLD: return isHeld();
			}
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
	public List<String> getDefault(ControllerModel model) {
		List<String> buttons = defaultButtonOnController.get(model == null ? ControllerModel.CUSTOM.getKey() : model.getKey());
		if (buttons == null || buttons.isEmpty()) {
			return Lists.newArrayList(" ");
		}
		else {
			return buttons;
		}
	}

	public void setToDefault() {
		buttonOnController = defaultButtonOnController;
	}

	public void setToDefault(ControllerModel model) {
		List<String> key = getDefault(model);
		Map<ResourceLocation,List<String>> builder = Maps.newHashMap();
		builder.putAll(buttonOnController);
		builder.remove(model.getKey());
		builder.put(model.getKey(), key);

		ImmutableMap.Builder<ResourceLocation,List<String>> builder2 = ImmutableMap.builder();
		builder2.putAll(builder);
		buttonOnController = builder2.build();
	}

	public boolean isDefault(ControllerModel model) {
		if (getButtonOnController(model).size() != getDefault(model).size()) return false;
		else {
			for (int i = 0; i < getButtonOnController(model).size(); i++) {
				if (!getButtonOnController(model).get(i).equalsIgnoreCase(getDefault(model).get(i))) {
					return false;
				}
			}
			return true;
		}
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
		Boolean inverted = axisInverted.get(model == null ? ControllerModel.CUSTOM.getKey() : model.getKey());
		if (inverted == null) {
			return false;
		}
		else {
			return inverted.booleanValue();
		}
	}

	/**
	 * @param isAxisInverted the isAxisInverted to set
	 */
	public void setAxisInverted(ControllerModel model, boolean isAxisInverted) {
		Map<ResourceLocation,Boolean> builder = Maps.newHashMap();
		builder.putAll(this.axisInverted);
		builder.remove(modelKey(model));
		builder.put(modelKey(model), isAxisInverted);

		ImmutableMap.Builder<ResourceLocation,Boolean> builder2 = ImmutableMap.builder();
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
			controllerMapping.buttonOnController.forEach((key, val) -> MAP.get(controllerMapping.useCase).put(Lists.newArrayList(ControllerModelManager.ALL_MODELS.get(key).getOrCreate(Lists.newArrayList(val))), controllerMapping));
		}
	}

	public static List<ControllerMapping> retrieveActiveMappings(Controller controller, List<UseCase> cases) {
		List<ControllerMapping> down = Lists.newArrayList();
		List<String> interactions = Lists.newArrayList();
		interactions.addAll(controller.getButtonsDown());
		interactions.addAll(controller.getAxesMoved());
		interactions.forEach(interaction -> cases.forEach(use -> {
			if (MAP.get(use) != null) {
				down.add(MAP.get(use).get(Lists.newArrayList(controller.getModel().getOrCreate(Lists.newArrayList(interaction)))));

				interactions.forEach(interaction2 -> {
					down.add(MAP.get(use).get(Lists.newArrayList(controller.getModel().getOrCreate(Lists.newArrayList(interaction, interaction2)))));
				});
			}

		}));
		down.removeIf((bind) -> bind == null);
		return down;
	}

	@Override
	public String toString() {
		return getDescripti();
	}
}
