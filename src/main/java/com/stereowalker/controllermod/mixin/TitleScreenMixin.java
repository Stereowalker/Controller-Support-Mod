package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.controller.ControllerUtil.ListeningMode;
import com.stereowalker.controllermod.client.gui.screen.ControllerSettingsScreen;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Component pTitle) {
		super(pTitle);
	}

	@Inject(method = "init", at = @At("TAIL"))
	public void init_inject(CallbackInfo ci) {
		ControllerUtil.listeningMode = ListeningMode.LISTEN_TO_MAPPINGS;
	}
}
