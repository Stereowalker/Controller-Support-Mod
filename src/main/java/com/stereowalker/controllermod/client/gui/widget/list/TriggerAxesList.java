package com.stereowalker.controllermod.client.gui.widget.list;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.gui.screen.TriggerSetupScreen;
import com.stereowalker.unionlib.util.ScreenHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;

public class TriggerAxesList extends ContainerObjectSelectionList<TriggerAxesList.Entry> {
	private ControllerMod mod;

	public TriggerAxesList(TriggerSetupScreen controls, Minecraft mcIn, ControllerMod modIn) {
		super(mcIn, controls.width + 45, controls.height, 43, controls.height - 32, 20);
		this.mod = modIn;
		this.addEntry(new TriggerAxesList.CategoryEntry(Component.translatable("gui.positive_triggers")));
		for (int i = 0; i < mod.getActiveController().getAxes().capacity(); i++) {
			this.addEntry(new TriggerAxesList.TriggerEntry(Component.translatable("Positive Axis "+ i), true, i));
		}

		this.addEntry(new TriggerAxesList.CategoryEntry(Component.translatable("gui.negative_triggers")));
		for (int i = 0; i < mod.getActiveController().getAxes().capacity(); i++) {
			this.addEntry(new TriggerAxesList.TriggerEntry(Component.translatable("Negative Axis "+ i), false, i));
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

	public class CategoryEntry extends TriggerAxesList.Entry {
		private final Component labelText;
		private final int labelWidth;

		public CategoryEntry(Component p_i232280_2_) {
			this.labelText = p_i232280_2_;
			this.labelWidth = TriggerAxesList.this.minecraft.font.width(this.labelText);
		}

		@Override
		public void render(GuiGraphics guiGraphics, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			guiGraphics.drawString(TriggerAxesList.this.minecraft.font, this.labelText, TriggerAxesList.this.minecraft.screen.width / 2 - this.labelWidth / 2, p_230432_3_ + p_230432_6_ - 9 - 1, 16777215);
		}

        @Override
        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent event) {
            return null;
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

	public class TriggerEntry extends TriggerAxesList.Entry {
		private final Button btnTrigger;
		private final Component labelText;
		private final boolean isPostitve;
		private final int axis;

		public TriggerEntry(Component p_i232280_2_, boolean isPostitve, int axis) {
			this.labelText = p_i232280_2_;
			this.isPostitve = isPostitve;
			this.axis = axis;
			this.btnTrigger = ScreenHelper.buttonBuilder(Component.translatable("gui.trigger.mark"), (p_214387_2_) -> {
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
			}).bounds(0, 0, 100, 20).createNarration((s)-> Component.translatable("narrator.controls.reset")).build();
		}

		@Override
		public void render(GuiGraphics guiGraphics, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			guiGraphics.drawString(TriggerAxesList.this.minecraft.font, this.labelText, p_230432_4_, p_230432_3_ + p_230432_6_ / 2 - 9 / 2, 16777215);
			ScreenHelper.setWidgetPosition(this.btnTrigger, p_230432_4_ + 190, p_230432_3_);
			this.btnTrigger.render(guiGraphics, p_230432_7_, p_230432_8_, p_230432_10_);
			
			if (isPostitve)
				if (mod.controllerOptions.positiveTriggerAxes.contains(axis))
					this.btnTrigger.setMessage(Component.translatable("gui.trigger.unmark").withStyle(ChatFormatting.RED));
				else
					this.btnTrigger.setMessage(Component.translatable("gui.trigger.mark").withStyle(ChatFormatting.GREEN));
			else
				if (mod.controllerOptions.negativeTriggerAxes.contains(axis))
					this.btnTrigger.setMessage(Component.translatable("gui.trigger.unmark").withStyle(ChatFormatting.RED));
				else
					this.btnTrigger.setMessage(Component.translatable("gui.trigger.mark").withStyle(ChatFormatting.GREEN));
		}

        @Override
        @Nullable
        public ComponentPath nextFocusPath(FocusNavigationEvent event) {
            return null;
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

	public abstract static class Entry extends ContainerObjectSelectionList.Entry<TriggerAxesList.Entry> {
	}
}
