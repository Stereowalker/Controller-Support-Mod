package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements Widget {

	@Inject(method = "render", at = @At("TAIL"))
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
		if (!((Object)this instanceof LevelLoadingScreen)) {
			int x = (int)(ControllerUtil.virtualmouse.xpos() * (double)Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getWidth());
			int y = (int)(ControllerUtil.virtualmouse.ypos() * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getHeight());
			if(ControllerUtil.isControllerAvailable(ControllerMod.getInstance().controllerOptions.controllerNumber) && ControllerMod.getInstance().controllerOptions.enableController) {
				renderCursor(pPoseStack, x,y, 5.0D);
			} 
		}
	}

	private static void renderCursor(PoseStack poseStack, int x, int y, double size) {
		poseStack.pushPose();
		RenderSystem.disableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, ControllerMod.Locations.CURSOR);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(-size+x, size+y, -90.0F).uv(0.0F, 1.0F).endVertex();
		bufferbuilder.vertex(size+x, size+y, -90.0F).uv(1.0F, 1.0F).endVertex();
		bufferbuilder.vertex(size+x, -size+y, -90.0F).uv(1.0F, 0.0F).endVertex();
		bufferbuilder.vertex(-size+x, -size+y, -90.0F).uv(0.0F, 0.0F).endVertex();
		tessellator.end();
		poseStack.popPose();
		RenderSystem.enableDepthTest();
	}
}
