package com.stereowalker.controllermod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stereowalker.controllermod.client.ControllerHandler;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.OnScreenKeyboard;
import com.stereowalker.controllermod.client.PaperDollOptions;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerBindings;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.config.Config;
import com.stereowalker.controllermod.resources.ControllerModelManager;
import com.stereowalker.unionlib.client.gui.screens.config.ConfigScreen;
import com.stereowalker.unionlib.config.ConfigBuilder;
import com.stereowalker.unionlib.mod.MinecraftMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(value = ControllerMod.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class ControllerMod extends MinecraftMod
{
	//
	public static int getSafeArea() {return Mth.ceil((1.0f - (CONFIG.safe_area/100.0f)) * 10.0f);}
	//
	public static ControllerMod instance;
	private ControllerHandler controllerHandler;
	public ControllerModelManager controllerModelManager;
	public OnScreenKeyboard onScreenKeyboard;
	public static final String MOD_ID = "controllermod";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Config CONFIG = new Config();
	public List<Controller> controllers;
	public ControllerOptions controllerOptions;
	public static final Controller EMPTY_CONTROLLER = new Controller(-1, "Empty", "Empty", 0);
	public static final ResourceLocation CONTROLLER_BUTTON_TEXTURES = new ResourceLocation(ControllerMod.MOD_ID, "textures/gui/controller_button.png");

	public ControllerMod() 
	{
		super(MOD_ID, new ResourceLocation(MOD_ID, "textures/gui/controller_icon2.png"), LoadType.CLIENT);
		MinecraftForge.EVENT_BUS.register(this);
		instance = this;
		ConfigBuilder.registerConfig(CONFIG);
		controllers = new ArrayList<Controller>();
	}

	@Override
	public void onModStartupInClient() {
		eventBus().addListener((Consumer<RegisterGuiOverlaysEvent>)event -> {
			event.registerAboveAll("tired", (gui, mStack, partialTicks, screenWidth, screenHeight) -> {
				gui.setupOverlayRenderState(true, false);
				PaperDollOptions.renderPlayerDoll(gui, mStack);
			});
		});
	}

	@Override
	public void registerClientRelaodableResources(ReloadListeners listeners) {
		this.controllerModelManager = new ControllerModelManager();
		listeners.listenTo(this.controllerModelManager);
	}

	@Override
	public void initClientAfterMinecraft(Minecraft mc) {
		LOGGER.info("Setting up all connected controlllers");
		this.controllerHandler = new ControllerHandler(this, mc);
		this.controllerHandler.setup(mc.getWindow().getWindow());
		this.onScreenKeyboard = new OnScreenKeyboard(mc);
		this.controllerOptions = new ControllerOptions(mc, mc.gameDirectory);
		ControllerBindings.registerAll();
		this.controllerOptions.loadOptions();
	}

	public void disconnectControllers() {
		LOGGER.info("Disconnecting all "+this.getTotalConnectedControllers()+" controllers");
		this.controllers.clear();
	}

	public void connectControllers() {
		LOGGER.info("Total Connected Controllers "+this.getTotalConnectedControllers());
		for (int i = 0; i < this.getTotalConnectedControllers(); i++) {
			if (ControllerUtil.isControllerAvailable(i)) {
				Controller cont = new Controller(i);
				if (cont.getActualModel() == null) {
					LOGGER.info("Added ("+cont.getName()+") as Controller "+(i+1)+". This is a custom controller and it's ID is "+cont.getGUID());
				} else {
					LOGGER.info("Added ("+cont.getName()+") as Controller "+(i+1)+". This is a registered "+cont.getActualModel()+" controller");
				}
				this.controllers.add(new Controller(i));
			}
		}
	}

	public ControllerHandler getControllerHandler() {
		return controllerHandler;
	}

	@Override
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new ConfigScreen(previousScreen, CONFIG);
	}

	public int getTotalConnectedControllers() {
		int maxControllers = 0;
		for (int i = 0; i < 16; i++) {
			if (ControllerUtil.isControllerAvailable(i)) {
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
		return getController(controllerOptions.controllerNumber) == null ? EMPTY_CONTROLLER : getController(controllerOptions.controllerNumber);
	}


	public static ControllerMod getInstance() {
		return instance;
	}

	public static void debug(String message) {
		if (CONFIG.debug)LOGGER.info(message);
	}

	public static void debug(String message, Object o1) {
		if (CONFIG.debug)LOGGER.info(message, o1);
	}

	public ResourceLocation location(String name)
	{
		return new ResourceLocation(MOD_ID, name);
	}

	public static class Locations {
		public static final ResourceLocation CURSOR = new ResourceLocation(ControllerMod.MOD_ID, "textures/gui/pointer.png");
	}
}
