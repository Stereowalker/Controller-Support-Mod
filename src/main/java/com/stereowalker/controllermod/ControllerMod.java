package com.stereowalker.controllermod;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerHelper;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.config.Config;
import com.stereowalker.unionlib.client.gui.screens.config.ConfigScreen;
import com.stereowalker.unionlib.config.ConfigBuilder;
import com.stereowalker.unionlib.mod.MinecraftMod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ControllerMod extends MinecraftMod
{
	public static ControllerMod instance;
	public static final String MOD_ID = "controllermod";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public final ControllerHelper controllerHelper;
	public static final Config CONFIG = new Config();
	public List<Controller> controllers;
	public ControllerOptions controllerSettings;
	public static final Controller EMPTY_CONTROLLER = new Controller(-1, "Empty", "Empty", 0);
	public static final ResourceLocation CONTROLLER_BUTTON_TEXTURES = new ResourceLocation(ControllerMod.MOD_ID, "textures/gui/controller_button.png");

	public ControllerMod() 
	{
		super(MOD_ID, new ResourceLocation(MOD_ID, "textures/gui/controller_icon2.png"), LoadType.CLIENT);
		instance = this;
		controllerHelper = new ControllerHelper(Minecraft.getInstance());
		ConfigBuilder.registerConfig(MOD_ID, CONFIG);
		controllers = new ArrayList<Controller>();
	}

	@Override
	public void onModStartupInClient() {
	}
	
	@Override
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new ConfigScreen(previousScreen, CONFIG);
	}

	public int getTotalConnectedControllers() {
		int maxControllers = 0;
		for (int i = 0; i < 16; i++) {
			if (ControllerUtil.isControllerAvailable(i)) {
				//				System.out.println("Controller "+(i+1)+" Is Available");
				maxControllers = i+1;
			}
		}
		return maxControllers;
	}

	public Controller getController(int id) {
		for(Controller controller : controllers) {
			if (controller.getId() == id) {
				return controller;
			}
		}
		return EMPTY_CONTROLLER;
	}

	public Controller getActiveController() {
		return getController(controllerSettings.controllerNumber) == null ? EMPTY_CONTROLLER : getController(controllerSettings.controllerNumber);
	}


	public static ControllerMod getInstance() {
		return instance;
	}

	public static void debug(String message) {
		if (CONFIG.debug)LOGGER.debug(message);
	}

	public ResourceLocation location(String name)
	{
		return new ResourceLocation(MOD_ID, name);
	}
	
	public static class Locations {
		public static final ResourceLocation CURSOR = new ResourceLocation(ControllerMod.MOD_ID, "textures/gui/cursor.png");
	}
}
