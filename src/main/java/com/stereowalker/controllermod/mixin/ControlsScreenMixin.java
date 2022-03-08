package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

@Mixin(ControlsScreen.class)
@Environment(EnvType.CLIENT)
public abstract class ControlsScreenMixin extends OptionsSubScreen {

	public ControlsScreenMixin(Screen pLastScreen, Options pOptions, Component pTitle) {
		super(pLastScreen, pOptions, pTitle);
	}

	@Inject(method = "init", at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 4, target = "Lnet/minecraft/client/gui/screens/controls/ControlsScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void init_inject(CallbackInfo ci, int i, int j, int k){
		this.addRenderableWidget(new Button(j, k, 150, 20, new TranslatableComponent("gui.editControllerInput"), (p_212984_1_) -> {
			this.minecraft.setScreen(new ControllerInputOptionsScreen(this, null, 0));
		}));
	}

}
