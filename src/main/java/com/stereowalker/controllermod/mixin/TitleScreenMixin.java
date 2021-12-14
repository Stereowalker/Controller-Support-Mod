package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.gui.screen.ControllerOptionsScreen;

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
		ControllerUtil.isListening = true;
		this.addRenderableWidget(new ImageButton(this.width / 2 + 104, this.height / 4 + 24 + 24 * 2, 20, 20, 0, 0, 20, ControllerMod.CONTROLLER_BUTTON_TEXTURES, 20, 40, (p_213088_1_) -> {
			this.getMinecraft().setScreen(new ControllerOptionsScreen(this));
		}, new TranslatableComponent("menu.button.controllers")));
	}
}
