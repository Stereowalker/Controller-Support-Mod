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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.MouseSettingsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;

@Mixin(ControlsScreen.class)
public abstract class ControlsScreenMixin extends OptionsSubScreen {

	public ControlsScreenMixin(Screen pLastScreen, Options pOptions, Component pTitle) {
		super(pLastScreen, pOptions, pTitle);
	}
	
	@Inject(method = "addOptions", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall(Lnet/minecraft/client/gui/components/AbstractWidget;Lnet/minecraft/client/gui/components/AbstractWidget;)V"))
	public void init_inject(CallbackInfo ci){
//		Button.builder(Component.translatable("options.mouse_settings"), button -> this.minecraft.setScreen(new MouseSettingsScreen(this, this.options))).build(),
//		Button.builder(Component.translatable("controls.keybinds"), button -> this.minecraft.setScreen(new KeyBindsScreen(this, this.options))).build()
		this.list
		.addSmall(
			ScreenHelper.buttonBuilder(Component.translatable("options.controller_settings"), (p_213088_1_) -> {
				this.minecraft.setScreen(new ControllerSettingsScreen(this));
			}).bounds(0, 0, 150, 20).build(),
			ScreenHelper.buttonBuilder(Component.translatable("controls.controllerbinds"), (p_212984_1_) -> {
				this.minecraft.setScreen(new ControllerInputOptionsScreen(this, null, new int[] {0}));
			}).bounds(0, 0, 150, 20).build()
		);
	}

}
