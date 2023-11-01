package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.platform.Window;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.PaperDollOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Gui.class)
public class GuiMixin {

	@Shadow public int screenWidth;
    @Shadow public int screenHeight;
    @Shadow @Final private Minecraft minecraft;
	@Shadow private void renderSlot(GuiGraphics guiGraphics, int x, int y, float partialTick, Player player, ItemStack stack, int seed) {}
	
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	public void render2(GuiGraphics guiGraphics, float arg1, CallbackInfo ci, Window w, Font font) {
		PaperDollOptions.renderPlayerDoll(guiGraphics);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScaledWidth()I"))
	public int render_reditect_width(Window window) {
		return window.getGuiScaledWidth() - (ControllerMod.getSafeArea()*2);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScaledHeight()I"))
	public int render_reditect_height(Window window) {
		return window.getGuiScaledHeight() - (ControllerMod.getSafeArea()*2);
	}
	
//	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"))
//	public void render_all_blit(PoseStack poseStack, int minX, int minY, int maxX, int maxY, int color) {
//		Gui.fill(poseStack, minX, minY, maxX + (ControllerMod.getSafeArea()*2), maxY + (ControllerMod.getSafeArea()*2), color);
//	}
	
//	@ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/MobEffectTextureManager;get(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"), name = "i")
//	public int render_all_blit_i(int i) {
//		return i + ControllerMod.getSafeArea();
//	}
	
//	@ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/MobEffectTextureManager;get(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"), name = "j")
//	public int render_all_blit_j(int j) {
//		return j + ControllerMod.getSafeArea();
//	}
	
	@Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IIFLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"))
	public void renderSlot_rediect(Gui gui, GuiGraphics guiGraphics, int x, int y, float partialTick, Player player, ItemStack stack, int seed) {
		renderSlot(guiGraphics, x + ControllerMod.getSafeArea(), y + ControllerMod.getSafeArea(), partialTick, player, stack, seed);
	}
	
	@Inject(method = {"renderVignette","renderSpyglassOverlay","renderTextureOverlay"},  at = @At("HEAD"))
	public void disableSafeZone(CallbackInfo ci) {
        this.screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        this.screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
	}
	
	@Inject(method = {"renderVignette","renderSpyglassOverlay","renderTextureOverlay"},  at = @At("TAIL"))
	public void enableSafeZone(CallbackInfo ci) {
        this.screenWidth = this.minecraft.getWindow().getGuiScaledWidth() - (ControllerMod.getSafeArea()*2);
        this.screenHeight = this.minecraft.getWindow().getGuiScaledHeight() - (ControllerMod.getSafeArea()*2);
	}
	
//	@Override
//	public void blit(PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
//		super.blit(poseStack, x + ControllerMod.getSafeArea(), y + ControllerMod.getSafeArea(), uOffset, vOffset, uWidth, vHeight);
//	}
}
