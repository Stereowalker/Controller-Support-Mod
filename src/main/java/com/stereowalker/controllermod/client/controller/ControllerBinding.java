package com.stereowalker.controllermod.client.controller;


import java.util.Map;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.IKeyConflictContext;

@OnlyIn(Dist.CLIENT)
public class ControllerBinding implements Comparable<ControllerBinding> {
	private static final Map<String, ControllerBinding> CONTROLLERBIND_ARRAY = Maps.newHashMap();
	
	private final String descri;
	private final String category;
	private final InputMappings.Type type; 
	private final int buttonOnKeyboardMouse;
	private ImmutableMap<ControllerModel,String> buttonOnController;
	private final ImmutableMap<ControllerModel,String> defaultButtonOnController;
	private ImmutableMap<ControllerModel,InputType> inputType;
	private final IKeyConflictContext conflict;
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
		p_205215_0_.put("key.categories.movement", 2);
		p_205215_0_.put("key.categories.gameplay", 3);
		p_205215_0_.put("key.categories.inventory", 4);
		p_205215_0_.put("key.categories.creative", 5);
		p_205215_0_.put("key.categories.multiplayer", 6);
		p_205215_0_.put("key.categories.ui", 7);
		p_205215_0_.put("key.categories.misc", 8);
	});

	private ControllerBinding(String category, String description, InputMappings.Type type, int buttonOnKeyboardMouse, Consumer<Map<ControllerModel,String>> buttonId, InputType inputType, boolean isAxisIn, boolean isAxisInvertedIn, IKeyConflictContext conflictContext, boolean fromKeybindIn) {
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
			}
			inputTypeBuilder.put(model, inputType == null ? InputType.PRESS : inputType);
			axisInvertedBuilder.put(model, isAxisInvertedIn);
			
		}
		builder.putAll(builder2);
		this.buttonOnController = builder.build();
		this.defaultButtonOnController = buttonOnController;
		this.type = type;
		this.inputType = inputTypeBuilder.build();
		this.buttonOnKeyboardMouse = buttonOnKeyboardMouse;
		this.conflict = conflictContext;
		this.fromKeybind = fromKeybindIn;
		this.isAxis = isAxisIn;
		this.axisInverted = axisInvertedBuilder.build();
		CONTROLLERBIND_ARRAY.put(description, this);
	}

	public ControllerBinding(String category, String desc, InputMappings.Type type, int buttonOnKeyboardMouse, Consumer<Map<ControllerModel,String>> buttonId, InputType inputType, IKeyConflictContext conflictContext) {
		this(category, desc, type, buttonOnKeyboardMouse, buttonId, inputType, false, false, conflictContext, false);
	}

	public ControllerBinding(String category, String desc, Consumer<Map<ControllerModel,String>> buttonId, InputType inputType, IKeyConflictContext conflictContext) {
		this(category, desc, null, 0, buttonId, inputType, false, false, conflictContext, false);
	}

	public ControllerBinding(KeyBinding keybind) {
		this(keybind.getKeyCategory(), keybind.getKeyDescription(), keybind.getKey().getType(), ControllerUtil.getKeybindCode(keybind), (builder) -> {
			builder.put(ControllerModel.XBOX_360, ControllerUtil.getControllerInputId(0));
			builder.put(ControllerModel.PS4, ControllerUtil.getControllerInputId(0));
			builder.put(ControllerModel.CUSTOM, ControllerUtil.getControllerInputId(0));
		}, InputType.PRESS, false, false, keybind.getKeyConflictContext(), true);
	}

	/**
	 * Setup for keybind binding
	 * @param keybind
	 * @param buttonId
	 */
	public ControllerBinding(KeyBinding keybind, Consumer<Map<ControllerModel,String>> buttonId) {
		this(keybind.getKeyCategory(), keybind.getKeyDescription(), keybind.getKey().getType(), ControllerUtil.getKeybindCode(keybind), buttonId, InputType.PRESS, false, false, keybind.getKeyConflictContext(), true);
	}

	/**
	 * Setup for axis binding
	 * @param category
	 * @param desc
	 * @param buttonId
	 * @param conflictContext
	 */
	public ControllerBinding(String category, String desc, Consumer<Map<ControllerModel,String>> buttonId, boolean isAxisInvertedIn, IKeyConflictContext conflictContext) {
		this(category, desc, null, 0, buttonId, null, true, isAxisInvertedIn, conflictContext, false);
	}

	public KeyBinding getAttachedKebinding() {
		for (KeyBinding key : Minecraft.getInstance().gameSettings.keyBindings) {
			if (key.getKeyDescription().equals(getDescripti())) {
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

	public boolean isBoundToButton(ControllerModel model) {
		return !getButtonOnController(model).equals(" ") && !getButtonOnController(model).equals("> <");
	}

	public boolean isBoundToKey() {
		return type != null && buttonOnKeyboardMouse != 0;
	}

	public String getDescripti() {
		return descri;
	}

	public int getButtonOnKeyboardOrMouse() {
		return buttonOnKeyboardMouse;
	}

	public InputMappings.Type getKeyType() {
		return type;
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

	public IKeyConflictContext getConflict() {
		return conflict;
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
	public int compareTo(ControllerBinding p_compareTo_1_) {
		if (this.category.equals(p_compareTo_1_.category)) return I18n.format(this.descri).compareTo(I18n.format(p_compareTo_1_.descri));
		Integer tCat = CATEGORY_ORDER.get(this.category);
		Integer oCat = CATEGORY_ORDER.get(p_compareTo_1_.category);
		if (tCat == null && oCat != null) return 1;
		if (tCat != null && oCat == null) return -1;
		if (tCat == null && oCat == null) return I18n.format(this.category).compareTo(I18n.format(p_compareTo_1_.category));
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
}
