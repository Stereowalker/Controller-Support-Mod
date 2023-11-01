package com.stereowalker.controllermod.client;

import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Function;

import org.joml.Quaternionf;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.unionlib.api.gui.GuiRenderer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;

public class PaperDollOptions {

	public Map<DollType, Boolean> show = Maps.newHashMap();
	public DollType lastDisplayedShow = DollType.NONE;
	public DollType currentDoll = DollType.NONE;

	public PaperDollOptions() {
		for (DollType dollShow : DollType.values()) {
			show.put(dollShow, dollShow.defaultV);
		}
	}

	private boolean shouldRender(DollType dollShow, LocalPlayer player) {
		if (dollShow == DollType.NONE) {
			return false;
		} else {
			return show.get(dollShow) && dollShow.renderCheck.apply(player);
		}
	}

	public void writeOptions(PrintWriter printwriter) {
		for (DollType dollShow : DollType.values()) {
			if (dollShow != DollType.NONE) printwriter.println(dollShow.getOptionsText()+":"+show.get(dollShow));
		}
	}

	public void readOptions(String s, String s1) {
		for (DollType dollShow : DollType.values()) {
			if (dollShow != DollType.NONE && dollShow.getOptionsText().equals(s)) show.put(dollShow, "true".equals(s1));
		}
	}


	public static int paperDollShownTicks = 0;
	public static float lastHeadYaw = 50.0F;
	public static float renderHeadYaw = 50.0F;
	public static final float maxYaw = 210.0F;
	public static final float minYaw = 120.0F;
	@SuppressWarnings("resource")
	public static void renderPlayerDoll(GuiRenderer renderer, int width, int height) {
		LocalPlayer player = Minecraft.getInstance().player;
		PaperDollOptions paperDoll = ControllerMod.getInstance().controllerOptions.paperDoll;
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
		if (!Minecraft.getInstance().options.renderDebug) {
			if (ControllerMod.CONFIG.show_paper_doll) {
				boolean renderDoll = false;
				for (DollType dollShow : DollType.values()) {
					if (dollShow != DollType.NONE && paperDoll.shouldRender(dollShow, player)) {
						if (paperDoll.currentDoll != DollType.NONE && paperDoll.currentDoll != dollShow) {
							paperDoll.lastDisplayedShow = paperDoll.currentDoll;
						}
						paperDoll.currentDoll = dollShow;
						renderDoll = true;
						break;
					}
				}
				if (!renderDoll && paperDoll.currentDoll != DollType.NONE) {
					paperDoll.lastDisplayedShow = paperDoll.currentDoll;
					paperDoll.currentDoll = DollType.NONE;
				}
				if (paperDoll.currentDoll != DollType.NONE) {
					paperDollShownTicks = 0;
					drawEntityOnScreen(renderer.poseStack(), 20, 40, 17, -30, 0, Minecraft.getInstance().player);
				} else if (paperDollShownTicks < 200) {
					paperDollShownTicks++;
					drawEntityOnScreen(renderer.poseStack(), 20, 40, 17, -30, 0, Minecraft.getInstance().player);
				}
			}

			if (ControllerMod.CONFIG.show_coordinates && !Minecraft.getInstance().player.isReducedDebugInfo()) {
				renderPosition(renderer);
			}

			if ((!Minecraft.getInstance().hasSingleplayerServer() || Minecraft.getInstance().getSingleplayerServer().isPublished()) && ControllerMod.CONFIG.ingame_player_names) {
				renderNames(renderer, width);
			}
			if (ControllerMod.CONFIG.show_button_hints) {
				ButtonHints.render(renderer, width, height);
			}
		}
	}

	@SuppressWarnings("resource")
	public static void renderPosition(GuiRenderer renderer) {
		Minecraft.getInstance().getProfiler().push("display-position");
		RenderSystem.disableDepthTest();

		BlockPos coordinates = Minecraft.getInstance().player.blockPosition();
		String coordinatesText = "Position: "+coordinates.getX()+", "+coordinates.getY()+", "+coordinates.getZ();

		int j = 9;
		int k = Minecraft.getInstance().font.width(coordinatesText);
		int y = 50;


		//ARGB
		renderer.fillOverlay(ControllerMod.getSafeArea(), y - 2, 3 + k + 1, y + j, 0x22020202);

		renderer.drawString(coordinatesText, ControllerMod.getSafeArea() + 3, y+1, 0x555555, false);
		renderer.drawString(coordinatesText, ControllerMod.getSafeArea() + 2, y, 0xffffff, false);
		Minecraft.getInstance().getProfiler().pop();
	}

	@SuppressWarnings("resource")
	public static void renderNames(GuiRenderer renderer, int width) {
		Minecraft.getInstance().getProfiler().push("playerName");
		Component playerName = Minecraft.getInstance().player.getName();
		renderer.drawString(playerName, width - Minecraft.getInstance().font.width(playerName), ControllerMod.getSafeArea(), ChatFormatting.WHITE.getColor(), false);
		Minecraft.getInstance().getProfiler().pop();
	}

	@SuppressWarnings("deprecation")
	public static void drawEntityOnScreen(PoseStack matrixstack, int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity entity) {
		float f = (float)Math.atan((double)(mouseX / 40.0F));
		float f1 = (float)Math.atan((double)(mouseY / 40.0F));
		matrixstack.pushPose();
		matrixstack.translate((float)posX, (float)posY, 1050.0F);
		matrixstack.scale(1.0F, 1.0F, -1.0F);
		matrixstack.translate(0.0D, 0.0D, 1000.0D);
		matrixstack.scale((float)scale, (float)scale, (float)scale);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(f1 * 20.0f * ((float)Math.PI / 180));
		quaternionf.mul(quaternionf2);
		matrixstack.mulPose(quaternionf);
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

		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		quaternionf2.conjugate();
		entityrenderermanager.overrideCameraOrientation(quaternionf2);
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

	public enum DollType {
		ALWAYS_ON("AlwaysOn", "always_on", false, (player) -> true),
		CRAWLING("Crawling", "crawling", true, (player) -> player.isVisuallyCrawling()),
		CROUCHING("Crouching", "crouching", true, (player) -> player.isCrouching()),
		HURT("Hurt", "hurt", true, (player) -> player.hurtTime > 0),
		ON_FIRE("OnFire", "on_fire", true, (player) -> player.isOnFire()),
		FLYING("Flying", "flying", true, (player) -> player.getAbilities().flying),
		GLIDING("Gliding", "gliding", true, (player) -> player.isFallFlying()),
		MOVING("Moving", "moving", true, (player) -> !player.input.getMoveVector().equals(Vec2.ZERO)),
		JUMPING("Jumping", "jumping", true, (player) -> player.input.jumping),
		NONE("", "", false, null),
		RIDING("Riding", "riding", true, (player) -> player.isPassenger()),
		SPINNING("Spinning", "spinning", true, (player) -> player.isAutoSpinAttack()),
		SPRINTING("Sprinting", "sprinting", true, (player) -> player.isSprinting() && !player.isSwimming()), 
		SWIMMING("Swimming", "swimming", true, (player) -> player.isVisuallySwimming() && player.isInWater()),
		SWINGING_ARM("SwingingArm", "swinging_arm", true, (player) -> player.swinging),
		USING_ITEM("UsingItem", "using_item", true, (player) -> player.isUsingItem());

		String optionsText;
		String displayText;
		Function<LocalPlayer, Boolean> renderCheck;
		boolean defaultV;
		private DollType(String options, String display, boolean defaultV, Function<LocalPlayer, Boolean> renderCheck) {
			this.optionsText = options;
			this.displayText = display;
			this.renderCheck = renderCheck;
			this.defaultV = defaultV;
		}

		public String getOptionsText() {
			return "paperDoll_show"+optionsText;
		}

		public MutableComponent getDisplayText() {
			return Component.translatable("gui.paper_doll."+displayText+"");
		}

		public boolean showInMenu() {
			return this != NONE && this != ALWAYS_ON;
		}
	}
}
