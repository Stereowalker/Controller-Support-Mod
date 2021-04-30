package com.stereowalker.controllermod.client.controller;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.client.settings.KeyConflictContext;

public class ControllerBindings {

	public static final String NEW = "new";
	public static final ControllerBinding SELECT_INPUT = new ControllerBinding(NEW, "select", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button0");
		builder.put(ControllerModel.PS4, "button1");
	}, InputType.PRESS, KeyConflictContext.GUI);
	
	public static final ControllerBinding SHIFT_MOVE_INPUT = new ControllerBinding(NEW, "shift_move", Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button2");
		builder.put(ControllerModel.PS4, "button0");
	}, InputType.PRESS, ControllerConflictContext.CONTAINER);
	
//	public static final ControllerBinding CLOSE_INVENTORY_INPUT = new ControllerBinding("close_invenetory", "button3", InputType.PRESS, ControllerConflictContext.CONTAINER);
	public static final List<KeyBinding> excludedKeybinds = Lists.newArrayList();

	public static void registerAll() {
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindInventory);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindJump);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindAttack);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindUseItem);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindSneak);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindTogglePerspective);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindDrop);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindLeft);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindRight);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindForward);
		excludeKeybind(Minecraft.getInstance().gameSettings.keyBindBack);
		
		registerControllerBinding(SELECT_INPUT);
		registerControllerBinding(SHIFT_MOVE_INPUT);
//		BINDINGS.add(CLOSE_INVENTORY_INPUT);
		for (KeyBinding key : Minecraft.getInstance().gameSettings.keyBindings) {
			if (!excludedKeybinds.contains(key)) registerControllerBinding(new ControllerBinding(key));
		}
	}

    /**
     * Registers a KeyBinding.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized void registerControllerBinding(ControllerBinding key)
    {
        ControllerMod.getInstance().controllerSettings.controllerBindings = ArrayUtils.add(ControllerMod.getInstance().controllerSettings.controllerBindings, key);
    }
    
    public static synchronized void excludeKeybind(KeyBinding binding) {
    	if (!excludedKeybinds.contains(binding)) {
    		excludedKeybinds.add(binding);
    	}
    }
}
