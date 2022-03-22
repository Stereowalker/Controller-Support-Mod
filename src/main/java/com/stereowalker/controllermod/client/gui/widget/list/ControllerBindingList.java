package com.stereowalker.controllermod.client.gui.widget.list;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;
import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;
import com.stereowalker.unionlib.client.gui.components.OverlayImageButton;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControllerBindingList extends ContainerObjectSelectionList<ControllerBindingList.Entry> {
	private final ControllerInputOptionsScreen controlsScreen;
	private int maxListLabelWidth;
	private ControllerMod mod;

	public ControllerBindingList(ControllerInputOptionsScreen controls, Minecraft mcIn, ControllerMod modIn) {
		super(mcIn, controls.width + 45, controls.height, 43, controls.height - 32, 20);
		this.controlsScreen = controls;
		this.mod = modIn;
		ControllerMapping[] akeybinding = ArrayUtils.clone(modIn.controllerOptions.controllerBindings);
		Arrays.sort(akeybinding);
		String s = null;

		for(ControllerMapping keybinding : akeybinding) {
			String s1 = keybinding.getCategory();
			if (!s1.equals(s)) {
				s = s1;
				this.addEntry(new ControllerBindingList.CategoryEntry(new TranslatableComponent(s1)));
			}

			Component itextcomponent = new TranslatableComponent(keybinding.getDescripti());
			int i = mcIn.font.width(itextcomponent);
			if (i > this.maxListLabelWidth) {
				this.maxListLabelWidth = i;
			}

			this.addEntry(new ControllerBindingList.KeyEntry(keybinding, itextcomponent));
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
	public class CategoryEntry extends ControllerBindingList.Entry {
		private final Component labelText;
		private final int labelWidth;

		public CategoryEntry(Component p_i232280_2_) {
			this.labelText = p_i232280_2_;
			this.labelWidth = ControllerBindingList.this.minecraft.font.width(this.labelText);
		}

		@Override
		public void render(PoseStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			ControllerBindingList.this.minecraft.font.draw(p_230432_1_, this.labelText, (float)(ControllerBindingList.this.minecraft.screen.width / 2 - this.labelWidth / 2), (float)(p_230432_3_ + p_230432_6_ - 9 - 1), 16777215);
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
	public abstract static class Entry extends ContainerObjectSelectionList.Entry<ControllerBindingList.Entry> {
	}

	@OnlyIn(Dist.CLIENT)
	public class KeyEntry extends ControllerBindingList.Entry {
		/** The controllerBinding specified for this KeyEntry */
		private final ControllerMapping controllerBinding;
		/** The localized key description for this KeyEntry */
		private final Component keyDesc;
		private final OverlayImageButton btnChangeKeyBinding;
		private final Button btnInputType;
		private final Button btnReset;

		private KeyEntry(final ControllerMapping controllerBinding, final Component keyDesc) {
			this.controllerBinding = controllerBinding;
			this.keyDesc = keyDesc;
			ControllerModel model = ControllerMod.getInstance().controllerOptions.controllerModel;
			this.btnChangeKeyBinding = new OverlayImageButton(0, 0, 65 /*Forge: add space*/, 20, 0, 0, 20, 20, model.getOrCreate(controllerBinding.getButtonOnController(model)).getIcon(), 20, 20, (p_214386_2_) -> {
				ControllerBindingList.this.controlsScreen.keyToSet = controllerBinding;
			}, keyDesc) {
				@Override
				protected MutableComponent createNarrationMessage() {
					return controllerBinding.isBoundToButton(model) ? new TranslatableComponent("narrator.controls.unbound", keyDesc) : new TranslatableComponent("narrator.controls.bound", keyDesc, super.createNarrationMessage());
				}
			};
			this.btnReset = new Button(0, 0, 50, 20, new TranslatableComponent("controls.reset"), (p_214387_2_) -> {
				controllerBinding.setToDefault(ControllerMod.getInstance().controllerOptions.controllerModel);
				ControllerBindingList.this.mod.controllerOptions.setKeyBindingCode(ControllerMod.getInstance().controllerOptions.controllerModel, controllerBinding, controllerBinding.getDefault(ControllerMod.getInstance().controllerOptions.controllerModel));
				//            ControllerBinding.resetKeyBindingArrayAndHash();
			}) {
				protected MutableComponent createNarrationMessage() {
					return new TranslatableComponent("narrator.controls.reset", keyDesc);
				}
			};
			this.btnInputType = new Button(0, 10, 70, 20, controllerBinding.getInputType(ControllerMod.getInstance().controllerOptions.controllerModel) != null ? controllerBinding.getInputType(ControllerMod.getInstance().controllerOptions.controllerModel).getDisplayName() : new TextComponent(""), (p_214387_2_) -> {
				if (controllerBinding.isAxis()) {
					ControllerBindingList.this.mod.controllerOptions.setKeyBindingInverted(ControllerMod.getInstance().controllerOptions.controllerModel, controllerBinding, !controllerBinding.isAxisInverted(ControllerMod.getInstance().controllerOptions.controllerModel));
				} else {
					if (controllerBinding.getInputType(ControllerMod.getInstance().controllerOptions.controllerModel) == InputType.PRESS) ControllerBindingList.this.mod.controllerOptions.setKeyBindingInputType(ControllerMod.getInstance().controllerOptions.controllerModel, controllerBinding, InputType.TOGGLE);
					else if (controllerBinding.getInputType(ControllerMod.getInstance().controllerOptions.controllerModel) == InputType.TOGGLE) ControllerBindingList.this.mod.controllerOptions.setKeyBindingInputType(ControllerMod.getInstance().controllerOptions.controllerModel ,controllerBinding, InputType.HOLD);
					else ControllerBindingList.this.mod.controllerOptions.setKeyBindingInputType(ControllerMod.getInstance().controllerOptions.controllerModel, controllerBinding, InputType.PRESS);
				}
			}) {
				@Override
				protected MutableComponent createNarrationMessage() {
					return new TranslatableComponent("narrator.controls.reset", keyDesc);
				}
			};
		}

		@Override
		public void render(PoseStack p_230432_1_, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			boolean flag = ControllerBindingList.this.controlsScreen.keyToSet == this.controllerBinding;
			ControllerModel model = ControllerMod.getInstance().controllerOptions.controllerModel;
			ControllerMap.Button button = model.getOrCreate(controllerBinding.getButtonOnController(model));
			ControllerBindingList.this.minecraft.font.draw(p_230432_1_, this.keyDesc, (float)(p_230432_4_ + 65 - ControllerBindingList.this.maxListLabelWidth), (float)(p_230432_3_ + p_230432_6_ / 2 - 9 / 2), 16777215);
			this.btnInputType.x = p_230432_4_ + 166;
			this.btnInputType.y = p_230432_3_;
			if (controllerBinding.isAxis()) {
				this.btnInputType.setMessage(controllerBinding.isAxisInverted(model) ? new TranslatableComponent("gui.inverted") : new TranslatableComponent("Not Inverted"));
			} else {
				this.btnInputType.setMessage(controllerBinding.getInputType(model).getDisplayName());
			}
			this.btnInputType.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
			this.btnReset.x = p_230432_4_ + 190 + 50;
			this.btnReset.y = p_230432_3_;
			this.btnReset.active = !this.controllerBinding.isDefault(model);
			this.btnReset.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
			this.btnChangeKeyBinding.x = p_230432_4_ + 98;
			this.btnChangeKeyBinding.y = p_230432_3_;
			this.btnChangeKeyBinding.setOverlay(button.getIcon());
			if (button.getIcon() != null)
				this.btnChangeKeyBinding.hideMessage();
			this.btnChangeKeyBinding.setMessage(new TextComponent(ControllerMap.map(controllerBinding.getButtonOnController(model), model)));
			boolean flag1 = false;
			boolean keyCodeModifierConflict = false;//true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
			if (this.controllerBinding.isBoundToButton(model)) {
				for(ControllerMapping keybinding : ControllerMod.getInstance().controllerOptions.controllerBindings) {
					if (keybinding == this.controllerBinding || !this.controllerBinding.same(keybinding, model)) continue;
					flag1 = true;
					break;
					//                  keyCodeModifierConflict &= keybinding.hasKeyCodeModifierConflict(keybinding);
				}
			}

			if (flag) {
				this.btnChangeKeyBinding.setOverlay(null);
				this.btnChangeKeyBinding.showMessage();
				if (button.getIcon() != null)
					this.btnChangeKeyBinding.setMessage((new TextComponent("> ")).append(" <").withStyle(ChatFormatting.YELLOW));
				else
					this.btnChangeKeyBinding.setMessage((new TextComponent("> ")).append(this.btnChangeKeyBinding.getMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
			} else if (flag1) {
				this.btnChangeKeyBinding.showMessage();
				if (button.getIcon() != null)
					this.btnChangeKeyBinding.setMessage(new TextComponent("CONFLICT").withStyle(keyCodeModifierConflict ? ChatFormatting.GOLD : ChatFormatting.RED));
				else
					this.btnChangeKeyBinding.setMessage(this.btnChangeKeyBinding.getMessage().copy().withStyle(keyCodeModifierConflict ? ChatFormatting.GOLD : ChatFormatting.RED));
			}

			this.btnChangeKeyBinding.render(p_230432_1_, p_230432_7_, p_230432_8_, p_230432_10_);
		}

		@Override
		public List<? extends GuiEventListener> children() {
			return ImmutableList.of(this.btnChangeKeyBinding, this.btnReset, this.btnInputType);
		}

		@Override
		public List<? extends NarratableEntry> narratables() {
			return ImmutableList.of(this.btnChangeKeyBinding, this.btnReset, this.btnInputType);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.btnChangeKeyBinding.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else if (this.btnInputType.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else {
				return this.btnReset.mouseClicked(mouseX, mouseY, button);
			}
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return this.btnChangeKeyBinding.mouseReleased(mouseX, mouseY, button) || this.btnReset.mouseReleased(mouseX, mouseY, button) || this.btnInputType.mouseReleased(mouseX, mouseY, button);
		}
	}
}
