package com.stereowalker.controllermod.client.gui.toasts;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class ControllerStatusToast implements Toast {
	private long firstDrawTime;
	private boolean newDisplay;
	List<FormattedCharSequence> subtitles;
	private final int width;
	private ControllerStatusToast.Type type;
	public static final ResourceLocation ICON = ControllerMod.getInstance().location("textures/gui/controller_icon.png");
	public static final ResourceLocation TEXTURE_TOASTS = ControllerMod.getInstance().location("textures/gui/toasts.png");

	public ControllerStatusToast(ControllerStatusToast.Type type, @Nullable Component subtitleComponent) {
		this(type, func_238537_a_(subtitleComponent), 160);
	}

	public static ControllerStatusToast func_238534_a_(ControllerStatusToast.Type type, Minecraft p_238534_0_, Component p_238534_3_) {
		Font fontrenderer = p_238534_0_.font;
		List<FormattedCharSequence> list = fontrenderer.split(p_238534_3_, 200);
		int i = Math.max(200, list.stream().mapToInt(fontrenderer::width).max().orElse(200));
		return new ControllerStatusToast(type, list, i + 30);
	}

	private ControllerStatusToast(ControllerStatusToast.Type type, List<FormattedCharSequence> p_i232264_3_, int p_i232264_4_) {
		this.subtitles = p_i232264_3_;
		this.width = p_i232264_4_;
		this.type = type;
	}

	private static ImmutableList<FormattedCharSequence> func_238537_a_(@Nullable Component p_238537_0_) {
		return p_238537_0_ == null ? ImmutableList.of() : ImmutableList.of(p_238537_0_.getVisualOrderText());
	}

	@Override
	public int width() {
		return this.width;
	}

	@SuppressWarnings({ "resource" })
	@Override
	public Toast.Visibility render(PoseStack matrixStack, ToastComponent toastGui, long drawTime) {
		if (this.newDisplay) {
			this.firstDrawTime = drawTime;
			this.newDisplay = false;
		}

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE_TOASTS);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.width();
		toastGui.blit(matrixStack, 0, 0, 0, 32, i, this.height());
		matrixStack.pushPose();
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, ICON);
		toastGui.blit(matrixStack, 6, 6, 0 * 20, 0 * 20, 20, 20);
		RenderSystem.enableBlend();
		matrixStack.popPose();
		if (this.subtitles == null) {
			toastGui.getMinecraft().font.draw(matrixStack, type.getText(), 33.0F, 12.0F, -11534256);
		} else {
			toastGui.getMinecraft().font.draw(matrixStack, type.getText(), 33.0F, 7.0F, -11534256);

			for(int k1 = 0; k1 < this.subtitles.size(); ++k1) {
				toastGui.getMinecraft().font.draw(matrixStack, this.subtitles.get(k1), 33.0F, (float)(18 + k1 * 12), -16777216);
			}
		}

		return drawTime - this.firstDrawTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	public void setType(ControllerStatusToast.Type type, @Nullable Component subtitleComponent) {
		this.type = type;
		this.subtitles = func_238537_a_(subtitleComponent);
		this.newDisplay = true;
	}

	public static void func_238536_a_(ToastComponent p_238536_0_, ControllerStatusToast.Type type, @Nullable Component p_238536_3_) {
		p_238536_0_.addToast(new ControllerStatusToast(type, p_238536_3_));
	}

	public static void addOrUpdate(ToastComponent p_193657_0_, ControllerStatusToast.Type type, Component p_193657_2_) {
		ControllerStatusToast systemtoast = p_193657_0_.getToast(ControllerStatusToast.class, type);
		if (systemtoast == null) {
			func_238536_a_(p_193657_0_, type, p_193657_2_);
		} else {
			systemtoast.setType(type, p_193657_2_);
		}

	}

	public enum Type {
		CONNECT(new TranslatableComponent("controller.connected")),
		DISCONNECT(new TranslatableComponent("controller.disconnected"));

		Component text;
		private Type(Component text) {
			this.text = text;
		}

		public Component getText() {
			return text;
		}
	}

}
