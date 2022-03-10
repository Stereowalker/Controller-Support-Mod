package com.stereowalker.controllermod.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.controller.ControllerUtil.ListeningMode;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;

@Environment(EnvType.CLIENT)
public class OnScreenKeyboard {

	public int currentKey = GLFW.GLFW_KEY_A;
	public boolean isCapsLocked = false;
	public int switchCooldown;
	public int changeKeyCooldown;

	public void switchKeyboard() {
		switchCooldown = 20;
		ControllerUtil.listeningMode = ControllerUtil.listeningMode == ListeningMode.KEYBOARD ? ListeningMode.LISTEN_TO_MAPPINGS : ListeningMode.KEYBOARD;
		currentKey = GLFW.GLFW_KEY_A;
		isCapsLocked = false;
		changeKeyCooldown = 0;
		ControllerMapping.releaseAll();
	}

	public void drawKeyboard(PoseStack poseStack, Font font, int x, int y) {
		String s = GLFW.glfwGetKeyName(currentKey, 0);
		font.drawShadow(poseStack, isCapsLocked ? s.toUpperCase() : s.toLowerCase(), x, y+5, 0xffffff);
	}

	public int getUnicodeKey() {
		if (currentKey >= GLFW.GLFW_KEY_A && currentKey <= GLFW.GLFW_KEY_Z)
			return isCapsLocked ? currentKey : currentKey+32;
		else 
			return currentKey;
	}

	public void changeKey(boolean left, boolean right) {
		int oldKey = currentKey;
		if (changeKeyCooldown == 0)
			if (left && currentKey > GLFW.GLFW_KEY_A)
				currentKey--;
			else if (right && currentKey < GLFW.GLFW_KEY_Z)
				currentKey++;

		if (oldKey != currentKey)
			changeKeyCooldown = 2;
		else if (changeKeyCooldown > 0 && !left && !right)
			changeKeyCooldown--;
	}
}
