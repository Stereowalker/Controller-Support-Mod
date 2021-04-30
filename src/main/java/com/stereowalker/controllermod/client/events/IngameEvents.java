package com.stereowalker.controllermod.client.events;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.PaperDollSettings;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class IngameEvents {
	public static int paperDollShownTicks = 0;
	public static float lastHeadYaw = 50.0F;
	public static float renderHeadYaw = 50.0F;
	public static final float maxYaw = 210.0F;
	public static final float minYaw = 120.0F;

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		PaperDollSettings paperDoll = ControllerMod.getInstance().controllerSettings.paperDoll;
		float diff = lastHeadYaw - player.rotationYawHead;
		if (lastHeadYaw != Minecraft.getInstance().player.rotationYawHead) {
			lastHeadYaw = lastHeadYaw - diff;
			float newRenderHeadYaw = renderHeadYaw;
			if (newRenderHeadYaw-diff >= minYaw && newRenderHeadYaw-diff <= maxYaw) {
				newRenderHeadYaw-=diff;
			}
			else if (newRenderHeadYaw-diff < minYaw) {
				newRenderHeadYaw = minYaw;
			}
			else if (newRenderHeadYaw-diff > maxYaw) {
				newRenderHeadYaw = maxYaw;
			}
			renderHeadYaw = newRenderHeadYaw;
		}
		boolean isFlying = Config.isFlying.get() && (player.abilities.isFlying);
		boolean isElytraFlying = Config.isElytraFlying.get() && (player.isElytraFlying());
		boolean isRiding = Config.isRiding.get() && (player.isPassenger());
		boolean isSpinning = Config.isSpinning.get() && (player.isSpinAttacking());
		boolean isMoving = Config.isMoving.get() && (!player.movementInput.getMoveVector().equals(Vector2f.ZERO));
		boolean isJumping = Config.isJumping.get() && (player.movementInput.jump);
		boolean isAttacking = Config.isAttacking.get() && (player.isSwingInProgress);
		boolean isUsing = Config.isUsing.get() && (player.isHandActive());
		boolean isHurt = Config.isHurt.get() && (player.hurtTime > 0);
		boolean isBurning = Config.isBurning.get() && (player.isBurning());
		boolean isAlwaysOn = false;
		if (!Minecraft.getInstance().gameSettings.showDebugInfo) {
			if (!Config.hidePaperDoll.get()) {
				if ((isAttacking || isBurning || 
					paperDoll.renderCrawling(player) || 
					paperDoll.renderCrouching(player) || isElytraFlying || isFlying || isHurt || isJumping || isMoving || isRiding || isSpinning || 
					paperDoll.renderSprinting(player) || 
					paperDoll.renderSwimming(player) || isUsing || isAlwaysOn)) {
					paperDollShownTicks = 0;
					drawEntityOnScreen(20, 40, 17, -30, 0, Minecraft.getInstance().player);
				} else if (paperDollShownTicks < 200) {
					paperDollShownTicks++;
					drawEntityOnScreen(20, 40, 17, -30, 0, Minecraft.getInstance().player);
				}
			}
			
			if (!Config.hideCoordinates.get() && Minecraft.getInstance().player.world.getGameRules().getBoolean(GameRules.REDUCED_DEBUG_INFO)) {
				renderPosition(event.getMatrixStack());
			}
			
			if ((!Minecraft.getInstance().isSingleplayer() || Minecraft.getInstance().getIntegratedServer().getPublic()) && Config.ingamePlayerNames.get()) {
				renderNames(event.getMatrixStack());
			}
			Minecraft.getInstance().getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			RenderSystem.disableAlphaTest();
		}
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public static void renderPosition(MatrixStack matrixStack) {
		Minecraft.getInstance().getProfiler().startSection("coordinates223");
		RenderSystem.disableDepthTest();
		RenderSystem.disableAlphaTest();

		BlockPos coordinates = Minecraft.getInstance().player.getPosition();
		String coordinatesText = "Position: "+coordinates.getX()+", "+coordinates.getY()+", "+coordinates.getZ();

		int j = 9;
		int k = Minecraft.getInstance().fontRenderer.getStringWidth(coordinatesText);
		int y = 50;

		RenderSystem.pushMatrix();

		//ARGB
		IngameGui.fill(matrixStack, 0, y - 2, 3 + k + 1, y + j, 0x11010101);
		RenderSystem.enableAlphaTest();
		RenderSystem.enableDepthTest();

		Minecraft.getInstance().fontRenderer.drawString(matrixStack, coordinatesText, 3.0F, y+1, 0x555555);
		Minecraft.getInstance().fontRenderer.drawString(matrixStack, coordinatesText, 2.0F, y, 0xffffff);
		RenderSystem.popMatrix();

		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
		Minecraft.getInstance().getProfiler().endSection();
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderNames(MatrixStack matrixStack) {
		Minecraft.getInstance().getProfiler().startSection("playerName");
		String playerName = Minecraft.getInstance().player.getName().getUnformattedComponentText();
		Minecraft.getInstance().fontRenderer.drawString(matrixStack, playerName, Minecraft.getInstance().getMainWindow().getScaledWidth()-Minecraft.getInstance().fontRenderer.getStringWidth(playerName)-10, 10, TextFormatting.WHITE.getColor());
		Minecraft.getInstance().getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
		Minecraft.getInstance().getProfiler().endSection();
	}
	
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity entity) {
	      float f = (float)Math.atan((double)(mouseX / 40.0F));
	      float f1 = (float)Math.atan((double)(mouseY / 40.0F));
	      RenderSystem.pushMatrix();
	      RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
	      RenderSystem.scalef(1.0F, 1.0F, -1.0F);
	      MatrixStack matrixstack = new MatrixStack();
	      matrixstack.translate(0.0D, 0.0D, 1000.0D);
	      matrixstack.scale((float)scale, (float)scale, (float)scale);
	      Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
	      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
	      quaternion.multiply(quaternion1);
	      matrixstack.rotate(quaternion);
	      float f2 = entity.renderYawOffset;
	      float f3 = entity.rotationYaw;
	      float f4 = entity.rotationPitch;
	      float f5 = entity.prevRotationYawHead;
	      float f6 = entity.rotationYawHead;
	      entity.renderYawOffset = 180.0F + f * 20.0F;
//	      entity.rotationYaw = 180.0F + f * 40.0F;
//	      entity.rotationPitch = -f1 * 20.0F;
	      entity.rotationYawHead = renderHeadYaw;
	      entity.prevRotationYawHead = renderHeadYaw;
	      
	      EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
	      quaternion1.conjugate();
	      entityrenderermanager.setCameraOrientation(quaternion1);
	      entityrenderermanager.setRenderShadow(false);
	      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
	      RenderSystem.runAsFancy(() -> {
	         entityrenderermanager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
	      });
	      irendertypebuffer$impl.finish();
	      entityrenderermanager.setRenderShadow(true);
	      entity.renderYawOffset = f2;
	      entity.rotationYaw = f3;
	      entity.rotationPitch = f4;
	      entity.prevRotationYawHead = f5;
	      entity.rotationYawHead = f6;
	      RenderSystem.popMatrix();
	   }
}
