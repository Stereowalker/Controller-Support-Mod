package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.PaperDollOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Gui.class)
public class GuiMixin extends GuiComponent {

	@Shadow public int screenWidth;
    @Shadow public int screenHeight;
    @Shadow @Final private Minecraft minecraft;
	@Shadow private void renderSlot(int x, int y, float partialTick, Player player, ItemStack stack, int i) {}
	
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	public void render2(PoseStack arg0, float arg1, CallbackInfo ci, Font font, float f) {
		PaperDollOptions.renderPlayerDoll((Gui)(Object)this, arg0);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScaledWidth()I"))
	public int render_reditect_width(Window window) {
		return window.getGuiScaledWidth() - (ControllerMod.getSafeArea()*2);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;getGuiScaledHeight()I"))
	public int render_reditect_height(Window window) {
		return window.getGuiScaledHeight() - (ControllerMod.getSafeArea()*2);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V"))
	public void render_all_blit(PoseStack poseStack, int minX, int minY, int maxX, int maxY, int color) {
		Gui.fill(poseStack, minX, minY, maxX + (ControllerMod.getSafeArea()*2), maxY + (ControllerMod.getSafeArea()*2), color);
	}
	
	@Redirect(method = "lambda$renderEffects$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
	public void render_all_blit(PoseStack poseStack, int x, int y, int blitOffset, int width, int height, TextureAtlasSprite sprite) {
		Gui.blit(poseStack, x + ControllerMod.getSafeArea(), y + ControllerMod.getSafeArea(), blitOffset, width, height, sprite);
	}
	
	@Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSlot(IIFLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"))
	public void renderSlot_rediect(Gui gui, int x, int y, float partialTick, Player player, ItemStack stack, int i) {
		renderSlot(x + ControllerMod.getSafeArea(), y + ControllerMod.getSafeArea(), partialTick, player, stack, i);
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
	
	@Override
	public void blit(PoseStack poseStack, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
		super.blit(poseStack, x + ControllerMod.getSafeArea(), y + ControllerMod.getSafeArea(), uOffset, vOffset, uWidth, vHeight);
	}
}
