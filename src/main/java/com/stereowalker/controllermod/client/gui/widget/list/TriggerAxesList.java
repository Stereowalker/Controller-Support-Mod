package com.stereowalker.controllermod.client.gui.widget.list;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.gui.screen.TriggerSetupScreen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TriggerAxesList extends ContainerObjectSelectionList<TriggerAxesList.Entry> {
	private ControllerMod mod;

	public TriggerAxesList(TriggerSetupScreen controls, Minecraft mcIn, ControllerMod modIn) {
		super(mcIn, controls.width + 45, controls.height, 43, controls.height - 32, 20);
		this.mod = modIn;
		this.addEntry(new TriggerAxesList.CategoryEntry(new TranslatableComponent("gui.positive_triggers")));
		for (int i = 0; i < mod.getActiveController().getAxes().capacity(); i++) {
			this.addEntry(new TriggerAxesList.TriggerEntry(new TranslatableComponent("Positive Axis "+ i), true, i));
		}

		this.addEntry(new TriggerAxesList.CategoryEntry(new TranslatableComponent("gui.negative_triggers")));
		for (int i = 0; i < mod.getActiveController().getAxes().capacity(); i++) {
			this.addEntry(new TriggerAxesList.TriggerEntry(new TranslatableComponent("Negative Axis "+ i), false, i));
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 15 + 40;
	}

	@Override
	public int getRowWidth() {
		return super.getRowWidth() + 72;
	}

	@OnlyIn(Dist.CLIENT)
	public class CategoryEntry extends TriggerAxesList.Entry {
		private final Component labelText;
		private final int labelWidth;

		public CategoryEntry(Component p_i232280_2_) {
			this.labelText = p_i232280_2_;
			this.labelWidth = TriggerAxesList.this.minecraft.font.width(this.labelText);
		}

		@Override
		public void render(PoseStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			TriggerAxesList.this.minecraft.font.draw(p_230432_1_, this.labelText, (float)(TriggerAxesList.this.minecraft.screen.width / 2 - this.labelWidth / 2), (float)(p_230432_3_ + p_230432_6_ - 9 - 1), 16777215);
		}

		@Override
		public boolean changeFocus(boolean focus) {
			return false;
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return Collections.emptyList();
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return Collections.emptyList();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public class TriggerEntry extends TriggerAxesList.Entry {
		private final Button btnTrigger;
		private final Component labelText;
		private final boolean isPostitve;
		private final int axis;

		public TriggerEntry(Component p_i232280_2_, boolean isPostitve, int axis) {
			this.labelText = p_i232280_2_;
			this.isPostitve = isPostitve;
			this.axis = axis;
			this.btnTrigger = new Button(0, 0, 100, 20, new TranslatableComponent("gui.trigger.mark"), (p_214387_2_) -> {
				if (isPostitve)
					if (mod.controllerOptions.positiveTriggerAxes.contains(axis))
						mod.controllerOptions.positiveTriggerAxes.remove(Integer.valueOf(axis));
					else
						mod.controllerOptions.positiveTriggerAxes.add(Integer.valueOf(axis));
				else
					if (mod.controllerOptions.negativeTriggerAxes.contains(axis))
						mod.controllerOptions.negativeTriggerAxes.remove(Integer.valueOf(axis));
					else
						mod.controllerOptions.negativeTriggerAxes.add(Integer.valueOf(axis));

				mod.controllerOptions.saveOptions();
			}) {
				@Override
				protected MutableComponent createNarrationMessage() {
					return new TranslatableComponent("narrator.controls.reset");
				}
			};
		}

		@Override
		public void render(PoseStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			TriggerAxesList.this.minecraft.font.draw(p_230432_1_, this.labelText, (float)(p_230432_4_), (float)(p_230432_3_ + p_230432_6_ / 2 - 9 / 2), 16777215);

			this.btnTrigger.x = p_230432_4_ + 190;
			this.btnTrigger.y = p_230432_3_;
			this.btnTrigger.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
			
			if (isPostitve)
				if (mod.controllerOptions.positiveTriggerAxes.contains(axis))
					this.btnTrigger.setMessage(new TranslatableComponent("gui.trigger.unmark").withStyle(ChatFormatting.RED));
				else
					this.btnTrigger.setMessage(new TranslatableComponent("gui.trigger.mark").withStyle(ChatFormatting.GREEN));
			else
				if (mod.controllerOptions.negativeTriggerAxes.contains(axis))
					this.btnTrigger.setMessage(new TranslatableComponent("gui.trigger.unmark").withStyle(ChatFormatting.RED));
				else
					this.btnTrigger.setMessage(new TranslatableComponent("gui.trigger.mark").withStyle(ChatFormatting.GREEN));
		}

		@Override
		public boolean changeFocus(boolean focus) {
			return false;
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return ImmutableList.of(this.btnTrigger);
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return ImmutableList.of(this.btnTrigger);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return this.btnTrigger.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return this.btnTrigger.mouseReleased(mouseX, mouseY, button);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public abstract static class Entry extends ContainerObjectSelectionList.Entry<TriggerAxesList.Entry> {
	}
}
