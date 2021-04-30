package com.stereowalker.controllermod.client.gui.widget.list;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerBinding;
import com.stereowalker.controllermod.client.controller.ControllerMap;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;
import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControllerBindingList extends AbstractOptionList<ControllerBindingList.Entry> {
	private final ControllerInputOptionsScreen controlsScreen;
	private int maxListLabelWidth;
	private ControllerMod mod;

	public ControllerBindingList(ControllerInputOptionsScreen controls, Minecraft mcIn, ControllerMod modIn) {
		super(mcIn, controls.width + 45, controls.height, 43, controls.height - 32, 20);
		this.controlsScreen = controls;
		this.mod = modIn;
		ControllerBinding[] akeybinding = ArrayUtils.clone(modIn.controllerSettings.controllerBindings);
		Arrays.sort(akeybinding);
		String s = null;

		for(ControllerBinding keybinding : akeybinding) {
			String s1 = keybinding.getCategory();
			if (!s1.equals(s)) {
				s = s1;
				this.addEntry(new ControllerBindingList.CategoryEntry(new TranslationTextComponent(s1)));
			}

			ITextComponent itextcomponent = new TranslationTextComponent(keybinding.getDescripti());
			int i = mcIn.fontRenderer.getStringPropertyWidth(itextcomponent);
			if (i > this.maxListLabelWidth) {
				this.maxListLabelWidth = i;
			}

			this.addEntry(new ControllerBindingList.KeyEntry(keybinding, itextcomponent));
		}

	}

	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 15 + 40;
	}

	public int getRowWidth() {
		return super.getRowWidth() + 72;
	}

	@OnlyIn(Dist.CLIENT)
	public class CategoryEntry extends ControllerBindingList.Entry {
		private final ITextComponent labelText;
		private final int labelWidth;

		public CategoryEntry(ITextComponent p_i232280_2_) {
			this.labelText = p_i232280_2_;
			this.labelWidth = ControllerBindingList.this.minecraft.fontRenderer.getStringPropertyWidth(this.labelText);
		}

		public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			ControllerBindingList.this.minecraft.fontRenderer.drawText(p_230432_1_, this.labelText, (float)(ControllerBindingList.this.minecraft.currentScreen.width / 2 - this.labelWidth / 2), (float)(p_230432_3_ + p_230432_6_ - 9 - 1), 16777215);
		}

		public boolean changeFocus(boolean focus) {
			return false;
		}

		public List<? extends IGuiEventListener> getEventListeners() {
			return Collections.emptyList();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public abstract static class Entry extends AbstractOptionList.Entry<ControllerBindingList.Entry> {
	}

	@OnlyIn(Dist.CLIENT)
	public class KeyEntry extends ControllerBindingList.Entry {
		/** The keybinding specified for this KeyEntry */
		private final ControllerBinding keybinding;
		/** The localized key description for this KeyEntry */
		private final ITextComponent keyDesc;
		private final Button btnChangeKeyBinding;
		private final Button btnInputType;
		private final Button btnReset;

		private KeyEntry(final ControllerBinding controllerBinding, final ITextComponent keyDesc) {
			this.keybinding = controllerBinding;
			this.keyDesc = keyDesc;
			this.btnChangeKeyBinding = new Button(0, 0, 75 + 10 /*Forge: add space*/, 20, keyDesc, (p_214386_2_) -> {
				ControllerBindingList.this.controlsScreen.keyToSet = controllerBinding;
			}) {
				protected IFormattableTextComponent getNarrationMessage() {
					return controllerBinding.isBoundToButton(Config.controllerModel.get()) ? new TranslationTextComponent("narrator.controls.unbound", keyDesc) : new TranslationTextComponent("narrator.controls.bound", keyDesc, super.getNarrationMessage());
				}
			};
			this.btnReset = new Button(0, 0, 50, 20, new TranslationTextComponent("controls.reset"), (p_214387_2_) -> {
				keybinding.setToDefault(Config.controllerModel.get());
				ControllerBindingList.this.mod.controllerSettings.setKeyBindingCode(Config.controllerModel.get(), controllerBinding, controllerBinding.getDefault(Config.controllerModel.get()));
				//            ControllerBinding.resetKeyBindingArrayAndHash();
			}) {
				protected IFormattableTextComponent getNarrationMessage() {
					return new TranslationTextComponent("narrator.controls.reset", keyDesc);
				}
			};
			this.btnInputType = new Button(0, 10, 70, 20, keybinding.getInputType(Config.controllerModel.get()) != null ? keybinding.getInputType(Config.controllerModel.get()).getDisplayName() : new StringTextComponent(""), (p_214387_2_) -> {
				if (keybinding.isAxis()) {
					ControllerBindingList.this.mod.controllerSettings.setKeyBindingInverted(Config.controllerModel.get(), keybinding, !keybinding.isAxisInverted(Config.controllerModel.get()));
				} else {
					if (keybinding.getInputType(Config.controllerModel.get()) == InputType.PRESS) ControllerBindingList.this.mod.controllerSettings.setKeyBindingInputType(Config.controllerModel.get(), keybinding, InputType.TOGGLE);
					else if (keybinding.getInputType(Config.controllerModel.get()) == InputType.TOGGLE) ControllerBindingList.this.mod.controllerSettings.setKeyBindingInputType(Config.controllerModel.get() ,keybinding, InputType.HOLD);
					else ControllerBindingList.this.mod.controllerSettings.setKeyBindingInputType(Config.controllerModel.get(), keybinding, InputType.PRESS);
				}
			}) {
				protected IFormattableTextComponent getNarrationMessage() {
					return new TranslationTextComponent("narrator.controls.reset", keyDesc);
				}
			};
		}

		public void render(MatrixStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			boolean flag = ControllerBindingList.this.controlsScreen.keyToSet == this.keybinding;
			ControllerBindingList.this.minecraft.fontRenderer.drawText(p_230432_1_, this.keyDesc, (float)(p_230432_4_ + 65 - ControllerBindingList.this.maxListLabelWidth), (float)(p_230432_3_ + p_230432_6_ / 2 - 9 / 2), 16777215);
			this.btnInputType.x = p_230432_4_ + 166;
			this.btnInputType.y = p_230432_3_;
			if (keybinding.isAxis()) {
				this.btnInputType.setMessage(keybinding.isAxisInverted(Config.controllerModel.get()) ? new TranslationTextComponent("gui.inverted") : new TranslationTextComponent("Not Inverted"));
			} else {
				this.btnInputType.setMessage(keybinding.getInputType(Config.controllerModel.get()).getDisplayName());
			}
			this.btnInputType.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
			this.btnReset.x = p_230432_4_ + 190 + 50;
			this.btnReset.y = p_230432_3_;
			this.btnReset.active = !this.keybinding.isDefault(Config.controllerModel.get());
			this.btnReset.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
			this.btnChangeKeyBinding.x = p_230432_4_ + 78;
			this.btnChangeKeyBinding.y = p_230432_3_;
			this.btnChangeKeyBinding.setMessage(new StringTextComponent(ControllerMap.map(keybinding.getButtonOnController(Config.controllerModel.get()), Config.controllerModel.get())));
			boolean flag1 = false;
			boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
			//         if (!this.keybinding.isInvalid()) {
			//            for(ControllerBinding keybinding : ControllerBindings.BINDINGS) {
			//               if (keybinding != this.keybinding && this.keybinding.conflicts(keybinding)) {
			//                  flag1 = true;
			//                  keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(keybinding);
			//               }
			//            }
			//         }

			if (flag) {
				this.btnChangeKeyBinding.setMessage((new StringTextComponent("> ")).appendSibling(this.btnChangeKeyBinding.getMessage().deepCopy().mergeStyle(TextFormatting.YELLOW)).appendString(" <").mergeStyle(TextFormatting.YELLOW));
			} else if (flag1) {
				this.btnChangeKeyBinding.setMessage(this.btnChangeKeyBinding.getMessage().deepCopy().mergeStyle(keyCodeModifierConflict ? TextFormatting.GOLD : TextFormatting.RED));
			}

			this.btnChangeKeyBinding.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
		}

		public List<? extends IGuiEventListener> getEventListeners() {
			return ImmutableList.of(this.btnChangeKeyBinding, this.btnReset, this.btnInputType);
		}

		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.btnChangeKeyBinding.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else if (this.btnInputType.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else {
				return this.btnReset.mouseClicked(mouseX, mouseY, button);
			}
		}

		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return this.btnChangeKeyBinding.mouseReleased(mouseX, mouseY, button) || this.btnReset.mouseReleased(mouseX, mouseY, button) || this.btnInputType.mouseReleased(mouseX, mouseY, button);
		}
	}
}
