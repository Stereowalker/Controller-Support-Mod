package com.stereowalker.controllermod.compat;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.Window;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.controller.ControllerUtil;

import net.minecraft.client.Minecraft;

public class MapAtlasesCompat {

	public static boolean isFullscreenMapActive() {
		try {
			Class.forName("pepjebs.mapatlases.screen.MapAtlasesAtlasOverviewScreen");
			if (Minecraft.getInstance().screen instanceof pepjebs.mapatlases.screen.MapAtlasesAtlasOverviewScreen)
				return true;
			else
				return false;
		}
		catch (ClassNotFoundException e){return false;}
	}
	
	public static float[] Pan(ControllerOptions options) {
		Window window = Minecraft.getInstance().getWindow();
		ControllerUtil.virtualmouse.xpos = window.getScreenWidth() / 2;
		ControllerUtil.virtualmouse.ypos = window.getScreenHeight() / 2;
		float[] pan = new float[2];
		pan[0] = -options.controllerBindMapAtlasesPanHorizontal.getAxis();
		pan[1] = -options.controllerBindMapAtlasesPanVertical.getAxis();
		
		if (pan[0] == 0 && pan[1] == 0) {
			ControllerUtil.virtualmouse.onPress(window.getWindow(), GLFW.GLFW_MOUSE_BUTTON_1, 0, 0);
		}
		if (pan[0] != 0 || pan[1] != 0) {
			ControllerUtil.virtualmouse.onPress(window.getWindow(), GLFW.GLFW_MOUSE_BUTTON_1, 1, 0);
		}
		if (ControllerMod.getInstance().controllerOptions.controllerBindMapAtlasesZoomIn.isDown(ControllerMod.getInstance().getActiveController().getModel())) {
			ControllerUtil.virtualmouse.scrollCallback(Minecraft.getInstance().getWindow().getWindow(), 0.0D, 4.0D);
		}
		if (ControllerMod.getInstance().controllerOptions.controllerBindMapAtlasesZoomOut.isDown(ControllerMod.getInstance().getActiveController().getModel())) {
			ControllerUtil.virtualmouse.scrollCallback(Minecraft.getInstance().getWindow().getWindow(), 0.0D, -4.0D);
		}
		return pan;
	}
}
