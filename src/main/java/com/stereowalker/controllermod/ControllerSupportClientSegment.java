package com.stereowalker.controllermod;

import com.stereowalker.controllermod.client.ControllerHandler;
import com.stereowalker.controllermod.client.OnScreenKeyboard;
import com.stereowalker.unionlib.client.gui.screens.config.ConfigScreen;
import com.stereowalker.unionlib.mod.ClientSegment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class ControllerSupportClientSegment extends ClientSegment {

	@Override
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new ConfigScreen(previousScreen, ControllerMod.CONFIG);
	}
	
	@Override
	public ResourceLocation getModIcon() {
		return new ResourceLocation(ControllerMod.MOD_ID, "textures/gui/controller_icon2.png");
	}

	@Override
	public void initClientAfterMinecraft(Minecraft mc) {
		ControllerMod.LOGGER.info("Setting up all connected controlllers");
		ControllerMod.instance.controllerHandler = new ControllerHandler(ControllerMod.instance, mc);
		ControllerMod.instance.controllerHandler.setup(mc.getWindow().getWindow());
		ControllerMod.instance.onScreenKeyboard = new OnScreenKeyboard(mc);
	}
}
