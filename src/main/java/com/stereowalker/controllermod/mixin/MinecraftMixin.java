package com.stereowalker.controllermod.mixin;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.controller.ControllerUtil.ListeningMode;
import com.stereowalker.controllermod.client.controller.UseCase;
import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {

	@Shadow @Nullable public LocalPlayer player;
	@Shadow @Nullable public Screen screen;
	@Shadow @Final public MouseHandler mouseHandler;
	@Shadow @Final public File gameDirectory;
	@Shadow @Final private Window window;

	public MinecraftMixin(String p_18765_) {
		super(p_18765_);
	}
	
	@Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;releaseAll()V"))
	public void setScreen_inject(CallbackInfo ci) {
		ControllerMapping.releaseAll();
	}

	boolean fromGame = false;
	@Inject(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
	public void tick_inject(CallbackInfo ci) {
		if(ControllerMod.getInstance().hasInitializedClient && ControllerUtil.isControllerAvailable(ControllerMod.getInstance().controllerOptions.controllerNumber) && ControllerMod.getInstance().controllerOptions.enableController) {
			ControllerOptions settings = ControllerMod.getInstance().controllerOptions;
			if (mouseHandler.isMouseGrabbed()) ControllerUtil.virtualmouse.grabMouse();
			else ControllerUtil.virtualmouse.ungrabMouse();
			if(!Minecraft.getInstance().isWindowActive()) Minecraft.getInstance().setWindowActive(true);
			Controller controller = ControllerMod.getInstance().getActiveController();
			if (controller != null) {
				List<UseCase> case1 = null;
				//Inventory Keybinds
				if(screen instanceof AbstractContainerScreen) {
					//					if(fromGame) {
					//						ControllerUtil.unpressAllKeys();
					//						fromGame = false;
					//					}
					case1 = Lists.newArrayList(UseCase.CONTAINER, UseCase.ANY_SCREEN, UseCase.ANYWHERE);
				}

				//Ingame Menu Keybinds
				else if(screen instanceof PauseScreen) {
					if(fromGame) {
						ControllerUtil.unpressAllKeys();
						fromGame = false;
					}
					case1 = Lists.newArrayList(UseCase.ANY_SCREEN, UseCase.ANYWHERE);
				}

				//Any other menu
				else if(screen !=null && !(screen instanceof AbstractContainerScreen) && !(screen instanceof PauseScreen)) {
					if(screen instanceof ControllerInputOptionsScreen) {
						if(!((ControllerInputOptionsScreen)screen).isAwaitingInput()) {
							if(fromGame) {
								ControllerUtil.unpressAllKeys();
								fromGame = false;
							}
							case1 = Lists.newArrayList(UseCase.ANY_SCREEN, UseCase.ANYWHERE);
						}
					} else {
						if(fromGame) {
							ControllerUtil.unpressAllKeys();
							fromGame = false;
						}
						case1 = Lists.newArrayList(UseCase.ANY_SCREEN, UseCase.ANYWHERE);
					}
				}

				//Ingame Keybinds
				else if(screen == null) {
					fromGame = true;
					case1 = Lists.newArrayList(UseCase.INGAME, UseCase.ANYWHERE);
				}
				if (case1 != null) {
					if (!case1.contains(UseCase.INGAME) && ControllerUtil.listeningMode == ListeningMode.KEYBOARD) {
						case1 = Lists.newArrayList(UseCase.KEYBOARD);
					}
					ControllerMod.getInstance().getControllerHandler().processControllerInput(controller, case1);
				}

				if (ControllerMod.getInstance().onScreenKeyboard.switchCooldown > 0) {
					ControllerMod.getInstance().onScreenKeyboard.switchCooldown--;
				}
				if (ControllerMod.getInstance().controllerOptions.controllerBindKeyboard.isDown(ControllerMod.getInstance().controllerOptions.controllerModel)) {
					if (ControllerUtil.listeningMode == ListeningMode.LISTEN_TO_MAPPINGS && ControllerMod.getInstance().onScreenKeyboard.switchCooldown == 0) {
						ControllerMod.getInstance().onScreenKeyboard.switchKeyboard();
					}
				}
				
				float scrollAxis = settings.controllerBindScroll.getAxis();
				float mouseXAxis = settings.controllerBindMouseHorizontal.getAxis();
				float mouseYAxis = settings.controllerBindMouseVertical.getAxis();

				if (case1.contains(UseCase.CONTAINER)) {
					ControllerUtil.updateMousePosition(mouseXAxis, mouseYAxis, controller, false, false);
					if (scrollAxis >= -1.0F && scrollAxis < -0.1F)
						mouseHandler.onScroll(window.getWindow(), 0.0D, -scrollAxis * ControllerMod.CONFIG.menu_sensitivity * 100.0D / 20.0D); 
					if (scrollAxis <= 1.0F && scrollAxis > 0.1F)
						mouseHandler.onScroll(window.getWindow(), 0.0D, -scrollAxis * ControllerMod.CONFIG.menu_sensitivity * 100.0D / 20.0D);
				}
				else if (case1.contains(UseCase.ANY_SCREEN)) {
					ControllerUtil.updateMousePosition(mouseXAxis, mouseYAxis, controller, false, ControllerUtil.listeningMode == ListeningMode.LISTEN_TO_MAPPINGS);
					if (scrollAxis >= -1.0F && scrollAxis < -0.1D)
						mouseHandler.onScroll(window.getWindow(), 0.0D, -scrollAxis * ControllerMod.CONFIG.menu_sensitivity * 100.0D / 20.0D); 
					if (scrollAxis <= 1.0F && scrollAxis > 0.1D)
						mouseHandler.onScroll(window.getWindow(), 0.0D, -scrollAxis * ControllerMod.CONFIG.menu_sensitivity * 100.0D / 20.0D); 
				}
			}
		}
	}

	@Inject(method = "handleKeybinds", at = @At("TAIL"))
	public void handleKeybinds_inject(CallbackInfo ci) {
		if(this.screen==null) {
			if (ControllerMod.getInstance().controllerOptions.controllerBindHotbarLeft.isDown(ControllerMod.getInstance().controllerOptions.controllerModel)) {
				ControllerUtil.virtualmouse.scrollCallback(Minecraft.getInstance().getWindow().getWindow(), 0.0D, 1.0D);
			}
			if (ControllerMod.getInstance().controllerOptions.controllerBindHotbarRight.isDown(ControllerMod.getInstance().controllerOptions.controllerModel)) {
				ControllerUtil.virtualmouse.scrollCallback(Minecraft.getInstance().getWindow().getWindow(), 0.0D, -1.0D);
			}
		}
	}

}
