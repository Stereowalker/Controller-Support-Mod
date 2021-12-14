package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.controller.ControllerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements ResourceManagerReloadListener, AutoCloseable {
	@Shadow @Final private Minecraft minecraft;
	@Inject(method = "renderLevel", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
	public void renderLevel_inject(float pPartialTicks, long pFinishTimeNano, PoseStack pMatrixStack, CallbackInfo ci) {
		if(ControllerUtil.isControllerAvailable(ControllerMod.getInstance().controllerSettings.controllerNumber) && ControllerMod.getInstance().controllerSettings.enableController && minecraft.screen == null) {
			ControllerOptions settings = ControllerMod.getInstance().controllerSettings;
			float cameraXAxis = settings.controllerBindCameraHorizontal.getAxis();
			float cameraYAxis = settings.controllerBindCameraVertical.getAxis();

			double moveModifier = 10.0D;

			float newPitch = (cameraYAxis >= -1.0F && cameraYAxis < -0.1D) || (cameraYAxis <= 1.0F && cameraYAxis > 0.1D) ? (float) ((cameraYAxis * ControllerMod.CONFIG.ingame_sensitivity * moveModifier) + minecraft.player.getViewXRot((float) pPartialTicks)) : minecraft.player.getViewXRot((float) pPartialTicks);
			float newYaw = (cameraXAxis >= -1.0F && cameraXAxis < -0.1D) || (cameraXAxis <= 1.0F && cameraXAxis > 0.1D) ? (float) ((cameraXAxis * ControllerMod.CONFIG.ingame_sensitivity * moveModifier) + minecraft.player.getViewYRot((float) pPartialTicks)) : minecraft.player.getViewYRot((float) pPartialTicks);

			minecraft.player.xRot = newPitch;
			minecraft.player.yRot = newYaw;
			//			event.setRoll(cameraXAxis + event.getRoll());
		}
	}
}
