package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.client.PaperDollOptions;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class GuiMixin {
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	public void render2(PoseStack arg0, float arg1, CallbackInfo ci, Font font, float f) {
		PaperDollOptions.renderPlayerDoll((Gui)(Object)this, arg0);
	}
}
