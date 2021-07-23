package com.stereowalker.controllermod;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stereowalker.controllermod.client.ControllerSettings;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerBindings;
import com.stereowalker.controllermod.client.controller.ControllerHelper;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.config.Config;
import com.stereowalker.controllermod.config.ConfigBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(value = "controllermod")
@OnlyIn(Dist.CLIENT)
public class ControllerMod 
{
	private static ControllerMod instance;
	public static boolean debugMode;
	public static final String MOD_ID = "controllermod";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public final ControllerHelper controllerHelper;
	public List<Controller> controllers;
	public ControllerSettings controllerSettings;
	public static final Controller EMPTY_CONTROLLER = new Controller(-1, "Empty", "Empty", 0);

	public ControllerMod() 
	{
		instance = this;
		controllers = new ArrayList<Controller>();
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigBuilder.client_config, "controllermod.client.toml");
		ConfigBuilder.loadConfig(ConfigBuilder.client_config, FMLPaths.CONFIGDIR.get().resolve("controllermod.client.toml").toString());
		
		controllerSettings = new ControllerSettings(Minecraft.getInstance(), Minecraft.getInstance().gameDir);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientRegistries);
		MinecraftForge.EVENT_BUS.register(this);
		debugMode = Config.debug_mode.get();
		controllerHelper = new ControllerHelper(Minecraft.getInstance());
		controllerHelper.registerCallbacks(Minecraft.getInstance().getMainWindow().getHandle());
		System.out.println("total Connected Controllers "+getTotalConnectedControllers());
		for (int i = 0; i < getTotalConnectedControllers(); i++) {
			if (ControllerUtil.isControllerAvailable(i)) {
				System.out.println("Added Controller "+i);
				controllers.add(new Controller(i));
			}
		}
		controllerSettings.lastGUID = getActiveController().getGUID();
	}

	public int getTotalConnectedControllers() {
		int maxControllers = 0;
		for (int i = 0; i < 16; i++) {
			if (ControllerUtil.isControllerAvailable(i)) {
				System.out.println("Controller "+(i+1)+" Is Available");
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
		return getController(ControllerUtil.controller) == null ? EMPTY_CONTROLLER : getController(ControllerUtil.controller);
	}
	

	public static ControllerMod getInstance() {
		return instance;
	}

	public static void debug(String message) {
		if (debugMode)LOGGER.debug(message);
	}

	public void clientRegistries(final FMLClientSetupEvent event){
		ControllerBindings.registerAll();

		controllerSettings.loadOptions();
	}

	public static ResourceLocation location(String name)
	{
		return new ResourceLocation(MOD_ID, name);
	}
}
