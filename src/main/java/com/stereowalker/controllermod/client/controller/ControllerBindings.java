package com.stereowalker.controllermod.client.controller;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;

public class ControllerBindings {

	public static final String NEW = "new";
	public static final ControllerMapping SELECT_INPUT = new ControllerMapping(NEW, "select", Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_LEFT), (builder) -> {
		builder.put(ControllerModel.XBOX_360_WINDOWS.defaultName, Lists.newArrayList("#face_button_down"));
		builder.put(ControllerModel.XBOX_360_LINUX.defaultName, Lists.newArrayList("#face_button_down"));
		builder.put(ControllerModel.PS4_WINDOWS.defaultName, Lists.newArrayList("#face_button_down"));
		builder.put(new ResourceLocation("controllermod:ps4_linux"), Lists.newArrayList("#face_button_down"));
		builder.put(new ResourceLocation("controllermod:nintendo_wii_u_pro"), Lists.newArrayList("#face_button_down"));
	}, InputType.PRESS, UseCase.ANY_SCREEN);
	
//	public static final ControllerBinding CLOSE_INVENTORY_INPUT = new ControllerBinding("close_invenetory", "button3", InputType.PRESS, ControllerConflictContext.CONTAINER);
	public static final List<KeyMapping> excludedKeybinds = Lists.newArrayList();

	@SuppressWarnings("resource")
	public static void registerAll() {
		excludeKeybind(Minecraft.getInstance().options.keyInventory);
		excludeKeybind(Minecraft.getInstance().options.keyJump);
		excludeKeybind(Minecraft.getInstance().options.keyAttack);
		excludeKeybind(Minecraft.getInstance().options.keyUse);
		excludeKeybind(Minecraft.getInstance().options.keyShift);
		excludeKeybind(Minecraft.getInstance().options.keyTogglePerspective);
		excludeKeybind(Minecraft.getInstance().options.keyDrop);
		excludeKeybind(Minecraft.getInstance().options.keyLeft);
		excludeKeybind(Minecraft.getInstance().options.keyRight);
		excludeKeybind(Minecraft.getInstance().options.keyUp);
		excludeKeybind(Minecraft.getInstance().options.keyDown);
		excludeKeybind(Minecraft.getInstance().options.keySprint);
		excludeKeybind(Minecraft.getInstance().options.keyChat);
		
		registerControllerBinding(SELECT_INPUT);
		for (KeyMapping key : Minecraft.getInstance().options.keyMappings) {
			if (!excludedKeybinds.contains(key)) registerControllerBinding(new ControllerMapping(key, key.getKeyConflictContext() == KeyConflictContext.IN_GAME ? UseCase.INGAME : key.getKeyConflictContext() == KeyConflictContext.GUI ? UseCase.ANY_SCREEN : UseCase.ANYWHERE));
		}
	}

    /**
     * Registers a KeyMapping.
     * Call this during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized void registerControllerBinding(ControllerMapping key)
    {
        ControllerMod.getInstance().controllerOptions.controllerBindings = ArrayUtils.add(ControllerMod.getInstance().controllerOptions.controllerBindings, key);
    }
    
    public static synchronized void excludeKeybind(KeyMapping binding) {
    	if (!excludedKeybinds.contains(binding)) {
    		excludedKeybinds.add(binding);
    	}
    }
}
