package com.stereowalker.controllermod.client.gui.toasts;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.controllermod.ControllerMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ControllerStatusToast implements IToast {
	private long firstDrawTime;
	private boolean newDisplay;
	List<IReorderingProcessor> subtitles;
	private final int width;
	private ControllerStatusToast.Type type;
	public static final ResourceLocation ICON = ControllerMod.location("textures/gui/controller_icon.png");
	public static final ResourceLocation TEXTURE_TOASTS = ControllerMod.location("textures/gui/toasts.png");

	public ControllerStatusToast(ControllerStatusToast.Type type, @Nullable ITextComponent subtitleComponent) {
		this(type, func_238537_a_(subtitleComponent), 160);
	}

	public static ControllerStatusToast func_238534_a_(ControllerStatusToast.Type type, Minecraft p_238534_0_, ITextComponent p_238534_3_) {
		FontRenderer fontrenderer = p_238534_0_.fontRenderer;
		List<IReorderingProcessor> list = fontrenderer.trimStringToWidth(p_238534_3_, 200);
		int i = Math.max(200, list.stream().mapToInt(fontrenderer::func_243245_a).max().orElse(200));
		return new ControllerStatusToast(type, list, i + 30);
	}

	private ControllerStatusToast(ControllerStatusToast.Type type, List<IReorderingProcessor> p_i232264_3_, int p_i232264_4_) {
		this.subtitles = p_i232264_3_;
		this.width = p_i232264_4_;
		this.type = type;
	}

	private static ImmutableList<IReorderingProcessor> func_238537_a_(@Nullable ITextComponent p_238537_0_) {
		return p_238537_0_ == null ? ImmutableList.of() : ImmutableList.of(p_238537_0_.func_241878_f());
	}

	@Override
	public int func_230445_a_() {
		return this.width;
	}

	@SuppressWarnings("deprecation")
	@Override
	public IToast.Visibility func_230444_a_(MatrixStack matrixStack, ToastGui toastGui, long drawTime) {
		if (this.newDisplay) {
			this.firstDrawTime = drawTime;
			this.newDisplay = false;
		}

		toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
		RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		int i = this.func_230445_a_();
		toastGui.blit(matrixStack, 0, 0, 0, 32, i, this.func_238540_d_());
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		toastGui.getMinecraft().getTextureManager().bindTexture(ICON);
		toastGui.blit(matrixStack, 6, 6, 0 * 20, 0 * 20, 20, 20);
		RenderSystem.enableBlend();
		RenderSystem.popMatrix();
		if (this.subtitles == null) {
			toastGui.getMinecraft().fontRenderer.drawText(matrixStack, type.getText(), 33.0F, 12.0F, -11534256);
		} else {
			toastGui.getMinecraft().fontRenderer.drawText(matrixStack, type.getText(), 33.0F, 7.0F, -11534256);

			for(int k1 = 0; k1 < this.subtitles.size(); ++k1) {
				toastGui.getMinecraft().fontRenderer.func_238422_b_(matrixStack, this.subtitles.get(k1), 33.0F, (float)(18 + k1 * 12), -16777216);
			}
		}

		return drawTime - this.firstDrawTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
	}

	public void setType(ControllerStatusToast.Type type, @Nullable ITextComponent subtitleComponent) {
		this.type = type;
		this.subtitles = func_238537_a_(subtitleComponent);
		this.newDisplay = true;
	}

	public static void func_238536_a_(ToastGui p_238536_0_, ControllerStatusToast.Type type, @Nullable ITextComponent p_238536_3_) {
		p_238536_0_.add(new ControllerStatusToast(type, p_238536_3_));
	}

	public static void addOrUpdate(ToastGui p_193657_0_, ControllerStatusToast.Type type, ITextComponent p_193657_2_) {
		ControllerStatusToast systemtoast = p_193657_0_.getToast(ControllerStatusToast.class, type);
		if (systemtoast == null) {
			func_238536_a_(p_193657_0_, type, p_193657_2_);
		} else {
			systemtoast.setType(type, p_193657_2_);
		}

	}

	public enum Type {
		CONNECT(new TranslationTextComponent("controller.connected")),
		DISCONNECT(new TranslationTextComponent("controller.disconnected"));

		ITextComponent text;
		private Type(ITextComponent text) {
			this.text = text;
		}

		public ITextComponent getText() {
			return text;
		}
	}

}
