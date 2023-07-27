package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;
import com.stereowalker.controllermod.client.gui.screen.ControllerSettingsScreen;
import com.stereowalker.unionlib.util.ScreenHelper;

import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.network.chat.Component;

@Mixin(ControlsScreen.class)
public abstract class ControlsScreenMixin extends OptionsSubScreen {

	public ControlsScreenMixin(Screen pLastScreen, Options pOptions, Component pTitle) {
		super(pLastScreen, pOptions, pTitle);
	}
	
	@ModifyVariable(method = "init", ordinal = 2, at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 4, target = "Lnet/minecraft/client/gui/screens/controls/ControlsScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
	public int init_inject(int k){
		return k+24;
	}

	@Inject(method = "init", at = @At(value = "INVOKE", shift = Shift.AFTER, ordinal = 4, target = "Lnet/minecraft/client/gui/screens/controls/ControlsScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void init_inject(CallbackInfo ci, int i, int j, int k){
		this.addRenderableWidget(ScreenHelper.buttonBuilder(Component.translatable("options.controller_settings"), (p_213088_1_) -> {
			this.minecraft.setScreen(new ControllerSettingsScreen(this));
		}).bounds(j, k-24, 150, 20).build());
		this.addRenderableWidget(ScreenHelper.buttonBuilder(Component.translatable("controls.controllerbinds"), (p_212984_1_) -> {
			this.minecraft.setScreen(new ControllerInputOptionsScreen(this, null, new int[] {0}));
		}).bounds(i, k, 150, 20).build());
	}

}
