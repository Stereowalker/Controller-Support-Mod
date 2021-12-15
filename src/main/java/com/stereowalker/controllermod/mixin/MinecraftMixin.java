package com.stereowalker.controllermod.mixin;

import java.io.File;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.WindowEventHandler;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerBindings;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {

	@Shadow @Nullable public LocalPlayer player;
	@Shadow @Nullable public Screen screen;
	@Shadow @Final public MouseHandler mouseHandler;
	@Shadow @Final public File gameDirectory;

	public MinecraftMixin(String p_18765_) {
		super(p_18765_);
	}
	
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void init_inject(GameConfig gameConfig, CallbackInfo ci) {
		ControllerMod.getInstance().controllerSettings = new ControllerOptions((Minecraft)(Object)this, this.gameDirectory);
		ControllerMod.getInstance().controllerSettings.lastGUID = ControllerMod.getInstance().getActiveController().getGUID();
		System.out.println("total Connected Controllers "+ControllerMod.getInstance().getTotalConnectedControllers());
		for (int i = 0; i < ControllerMod.getInstance().getTotalConnectedControllers(); i++) {
			if (ControllerUtil.isControllerAvailable(i)) {
				System.out.println("Added Controller "+i);
				ControllerMod.getInstance().controllers.add(new Controller(i));
			}
		}
		ControllerBindings.registerAll();
		ControllerMod.getInstance().controllerSettings.loadOptions();
	}

	boolean fromGame = false;
	@Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
	public void tick_inject(CallbackInfo ci) {
		if(ControllerUtil.isControllerAvailable(ControllerMod.getInstance().controllerSettings.controllerNumber) && ControllerMod.getInstance().controllerSettings.enableController) {
			ControllerOptions settings = ControllerMod.getInstance().controllerSettings;
			if (mouseHandler.isMouseGrabbed()) ControllerUtil.virtualmouse.grabMouse();
			else ControllerUtil.virtualmouse.ungrabMouse();
			if(!Minecraft.getInstance().isWindowActive()) Minecraft.getInstance().setWindowActive(true);
			Controller controller = ControllerMod.getInstance().getActiveController();
			if (controller != null) {
				float scrollAxis = settings.controllerBindScroll.getAxis();
				float cameraXAxis = settings.controllerBindCameraHorizontal.getAxis();
				float cameraYAxis = settings.controllerBindCameraVertical.getAxis();
				float mouseXAxis = settings.controllerBindMouseHorizontal.getAxis();
				float mouseYAxis = settings.controllerBindMouseVertical.getAxis();
				//Inventory Keybinds
				if(screen instanceof AbstractContainerScreen) {
					//					if(fromGame) {
					//						ControllerUtil.unpressAllKeys();
					//						fromGame = false;
					//					}
					ControllerUtil.handleContainerInput(controller, mouseXAxis, mouseYAxis, scrollAxis);
				}

				//Ingame Menu Keybinds
				if(screen instanceof PauseScreen) {
					if(fromGame) {
						ControllerUtil.unpressAllKeys();
						fromGame = false;
					}
					ControllerUtil.handleScreenInput(controller, mouseXAxis, mouseYAxis, scrollAxis, true);
				}

				//Any other menu
				if(screen !=null && !(screen instanceof AbstractContainerScreen) && !(screen instanceof PauseScreen)) {
					if(screen instanceof ControllerInputOptionsScreen) {
						if(!((ControllerInputOptionsScreen)screen).isAwaitingInput()) {
							if(fromGame) {
								ControllerUtil.unpressAllKeys();
								fromGame = false;
							}
							ControllerUtil.handleScreenInput(controller, mouseXAxis, mouseYAxis, scrollAxis, false);
						}
					} else {
						if(fromGame) {
							ControllerUtil.unpressAllKeys();
							fromGame = false;
						}
						ControllerUtil.handleScreenInput(controller, mouseXAxis, mouseYAxis, scrollAxis, false);
					}
				}

				//Ingame Keybinds
				if(screen == null) {
					fromGame = true;
					ControllerUtil.handleIngameInput(controller, cameraXAxis, cameraYAxis);
				}
			}
		}
	}
	
	@Inject(method = "handleKeybinds", at = @At("TAIL"))
	public void handleKeybinds_inject(CallbackInfo ci) {
		if(this.screen==null) {
			if (ControllerMod.getInstance().controllerSettings.controllerBindHotbarLeft.isDown(ControllerMod.getInstance().controllerSettings.controllerModel)) {
				ControllerUtil.virtualmouse.scrollCallback(Minecraft.getInstance().getWindow().getWindow(), 0.0D, 1.0D);
			}
			if (ControllerMod.getInstance().controllerSettings.controllerBindHotbarRight.isDown(ControllerMod.getInstance().controllerSettings.controllerModel)) {
				ControllerUtil.virtualmouse.scrollCallback(Minecraft.getInstance().getWindow().getWindow(), 0.0D, -1.0D);
			}
		}
	}

}
