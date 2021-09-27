package com.stereowalker.controllermod.client.events;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.PaperDollSettings;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec2;
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
		LocalPlayer player = Minecraft.getInstance().player;
		PaperDollSettings paperDoll = ControllerMod.getInstance().controllerSettings.paperDoll;
		float diff = lastHeadYaw - player.yHeadRot;
		if (lastHeadYaw != Minecraft.getInstance().player.yHeadRot) {
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
		boolean isFallFlying = Config.isFallFlying.get() && (player.isFallFlying());
		boolean isRiding = Config.isRiding.get() && (player.isPassenger());
		boolean isSpinning = Config.isSpinning.get() && (player.isAutoSpinAttack());
		boolean isMoving = Config.isMoving.get() && (!player.input.getMoveVector().equals(Vec2.ZERO));
		boolean isJumping = Config.isJumping.get() && (player.input.jumping);
		boolean isAttacking = Config.isAttacking.get() && (player.swinging);
		boolean isUsing = Config.isUsing.get() && (player.isUsingItem());
		boolean isHurt = Config.isHurt.get() && (player.hurtTime > 0);
		boolean isOnFire = Config.isOnFire.get() && (player.isOnFire());
		boolean isAlwaysOn = false;
		if (!Minecraft.getInstance().options.renderDebug) {
			if (!Config.hidePaperDoll.get()) {
				if ((isAttacking || isOnFire || 
					paperDoll.renderCrawling(player) || 
					paperDoll.renderCrouching(player) || isFallFlying || 
					paperDoll.renderFlying(player) || isHurt || isJumping || isMoving || isRiding || isSpinning || 
					paperDoll.renderSprinting(player) || 
					paperDoll.renderSwimming(player) || isUsing || isAlwaysOn)) {
					paperDollShownTicks = 0;
					drawEntityOnScreen(20, 40, 17, -30, 0, Minecraft.getInstance().player);
				} else if (paperDollShownTicks < 200) {
					paperDollShownTicks++;
					drawEntityOnScreen(20, 40, 17, -30, 0, Minecraft.getInstance().player);
				}
			}
			
			if (!Config.hideCoordinates.get() && Minecraft.getInstance().player.level.getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO)) {
				renderPosition(event.getMatrixStack());
			}
			
			if ((!Minecraft.getInstance().hasSingleplayerServer() || Minecraft.getInstance().getSingleplayerServer().isPublished()) && Config.ingamePlayerNames.get()) {
				renderNames(event.getMatrixStack());
			}
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
//			TODO: RenderSystem.disableAlphaTest();
		}
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public static void renderPosition(PoseStack matrixStack) {
		Minecraft.getInstance().getProfiler().push("coordinates223");
		RenderSystem.disableDepthTest();
//		TODO: RenderSystem.disableAlphaTest();

		BlockPos coordinates = Minecraft.getInstance().player.blockPosition();
		String coordinatesText = "Position: "+coordinates.getX()+", "+coordinates.getY()+", "+coordinates.getZ();

		int j = 9;
		int k = Minecraft.getInstance().font.width(coordinatesText);
		int y = 50;

		matrixStack.pushPose();

		//ARGB
		Gui.fill(matrixStack, 0, y - 2, 3 + k + 1, y + j, 0x11010101);
//		TODO: RenderSystem.enableAlphaTest();
		RenderSystem.enableDepthTest();

		Minecraft.getInstance().font.draw(matrixStack, coordinatesText, 3.0F, y+1, 0x555555);
		Minecraft.getInstance().font.draw(matrixStack, coordinatesText, 2.0F, y, 0xffffff);
		matrixStack.popPose();

//		TODO: RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
		Minecraft.getInstance().getProfiler().pop();
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderNames(PoseStack matrixStack) {
		Minecraft.getInstance().getProfiler().push("playerName");
		String playerName = Minecraft.getInstance().player.getName().getContents();
		Minecraft.getInstance().font.draw(matrixStack, playerName, Minecraft.getInstance().getWindow().getGuiScaledWidth()-Minecraft.getInstance().font.width(playerName)-10, 10, ChatFormatting.WHITE.getColor());
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
		Minecraft.getInstance().getProfiler().pop();
	}
	
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity entity) {
	      float f = (float)Math.atan((double)(mouseX / 40.0F));
	      float f1 = (float)Math.atan((double)(mouseY / 40.0F));
	      PoseStack matrixstack = new PoseStack();
	      matrixstack.pushPose();
	      matrixstack.translate((float)posX, (float)posY, 1050.0F);
	      matrixstack.scale(1.0F, 1.0F, -1.0F);
	      matrixstack.translate(0.0D, 0.0D, 1000.0D);
	      matrixstack.scale((float)scale, (float)scale, (float)scale);
	      Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
	      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
	      quaternion.mul(quaternion1);
	      matrixstack.mulPose(quaternion);
	      float f2 = entity.yBodyRot;
	      float f3 = entity.yRot;
	      float f4 = entity.xRot;
	      float f5 = entity.yHeadRotO;
	      float f6 = entity.yHeadRot;
	      entity.yBodyRot = 180.0F + f * 20.0F;
//	      entity.yRot = 180.0F + f * 40.0F;
//	      entity.xRot = -f1 * 20.0F;
	      entity.yHeadRot = renderHeadYaw;
	      entity.yHeadRotO = renderHeadYaw;
	      
	      EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
	      quaternion1.conj();
	      entityrenderermanager.overrideCameraOrientation(quaternion1);
	      entityrenderermanager.setRenderShadow(false);
	      MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
	      RenderSystem.runAsFancy(() -> {
	         entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
	      });
	      irendertypebuffer$impl.endBatch();
	      entityrenderermanager.setRenderShadow(true);
	      entity.yBodyRot = f2;
	      entity.yRot = f3;
	      entity.xRot = f4;
	      entity.yHeadRotO = f5;
	      entity.yHeadRot = f6;
	      matrixstack.popPose();
	   }
}
