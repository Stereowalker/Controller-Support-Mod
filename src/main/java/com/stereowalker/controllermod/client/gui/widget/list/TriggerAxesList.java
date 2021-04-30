package com.stereowalker.controllermod.client.gui.widget.list;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.gui.screen.TriggerSetupScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TriggerAxesList extends AbstractOptionList<TriggerAxesList.Entry> {
	private ControllerMod mod;

	public TriggerAxesList(TriggerSetupScreen controls, Minecraft mcIn, ControllerMod modIn) {
		super(mcIn, controls.width + 45, controls.height, 43, controls.height - 32, 20);
		this.mod = modIn;
		this.addEntry(new TriggerAxesList.CategoryEntry(new TranslationTextComponent("gui.positive_triggers")));
		for (int i = 0; i < mod.getActiveController().getAxes().capacity(); i++) {
			this.addEntry(new TriggerAxesList.TriggerEntry(new TranslationTextComponent("Positive Axis "+ i), true, i));
		}

		this.addEntry(new TriggerAxesList.CategoryEntry(new TranslationTextComponent("gui.negative_triggers")));
		for (int i = 0; i < mod.getActiveController().getAxes().capacity(); i++) {
			this.addEntry(new TriggerAxesList.TriggerEntry(new TranslationTextComponent("Negative Axis "+ i), false, i));
		}
	}

	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 15 + 40;
	}

	public int getRowWidth() {
		return super.getRowWidth() + 72;
	}

	@OnlyIn(Dist.CLIENT)
	public class CategoryEntry extends TriggerAxesList.Entry {
		private final ITextComponent labelText;
		private final int labelWidth;

		public CategoryEntry(ITextComponent p_i232280_2_) {
			this.labelText = p_i232280_2_;
			this.labelWidth = TriggerAxesList.this.minecraft.fontRenderer.getStringPropertyWidth(this.labelText);
		}

		public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			TriggerAxesList.this.minecraft.fontRenderer.drawText(p_230432_1_, this.labelText, (float)(TriggerAxesList.this.minecraft.currentScreen.width / 2 - this.labelWidth / 2), (float)(p_230432_3_ + p_230432_6_ - 9 - 1), 16777215);
		}

		public boolean changeFocus(boolean focus) {
			return false;
		}

		public List<? extends IGuiEventListener> getEventListeners() {
			return Collections.emptyList();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public class TriggerEntry extends TriggerAxesList.Entry {
		private final Button btnTrigger;
		private final ITextComponent labelText;
		private final boolean isPostitve;
		private final int axis;

		public TriggerEntry(ITextComponent p_i232280_2_, boolean isPostitve, int axis) {
			this.labelText = p_i232280_2_;
			this.isPostitve = isPostitve;
			this.axis = axis;
			this.btnTrigger = new Button(0, 0, 100, 20, new TranslationTextComponent("gui.trigger.mark"), (p_214387_2_) -> {
				if (isPostitve)
					if (mod.controllerSettings.positiveTriggerAxes.contains(axis))
						mod.controllerSettings.positiveTriggerAxes.remove(new Integer(axis));
					else
						mod.controllerSettings.positiveTriggerAxes.add(new Integer(axis));
				else
					if (mod.controllerSettings.negativeTriggerAxes.contains(axis))
						mod.controllerSettings.negativeTriggerAxes.remove(new Integer(axis));
					else
						mod.controllerSettings.negativeTriggerAxes.add(new Integer(axis));

				mod.controllerSettings.saveOptions();
			}) {
				protected IFormattableTextComponent getNarrationMessage() {
					return new TranslationTextComponent("narrator.controls.reset");
				}
			};
		}

		public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			TriggerAxesList.this.minecraft.fontRenderer.drawText(p_230432_1_, this.labelText, (float)(p_230432_4_), (float)(p_230432_3_ + p_230432_6_ / 2 - 9 / 2), 16777215);

			this.btnTrigger.x = p_230432_4_ + 190;
			this.btnTrigger.y = p_230432_3_;
			this.btnTrigger.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
			
			if (isPostitve)
				if (mod.controllerSettings.positiveTriggerAxes.contains(axis))
					this.btnTrigger.setMessage(new TranslationTextComponent("gui.trigger.unmark").mergeStyle(TextFormatting.RED));
				else
					this.btnTrigger.setMessage(new TranslationTextComponent("gui.trigger.mark").mergeStyle(TextFormatting.GREEN));
			else
				if (mod.controllerSettings.negativeTriggerAxes.contains(axis))
					this.btnTrigger.setMessage(new TranslationTextComponent("gui.trigger.unmark").mergeStyle(TextFormatting.RED));
				else
					this.btnTrigger.setMessage(new TranslationTextComponent("gui.trigger.mark").mergeStyle(TextFormatting.GREEN));
		}

		public boolean changeFocus(boolean focus) {
			return false;
		}

		public List<? extends IGuiEventListener> getEventListeners() {
			return ImmutableList.of(this.btnTrigger);
		}

		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return this.btnTrigger.mouseClicked(mouseX, mouseY, button);
		}

		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return this.btnTrigger.mouseReleased(mouseX, mouseY, button);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public abstract static class Entry extends AbstractOptionList.Entry<TriggerAxesList.Entry> {
	}
}
