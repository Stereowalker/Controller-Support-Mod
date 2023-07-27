package com.stereowalker.controllermod.client.gui.widget.list;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;
import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;
import com.stereowalker.unionlib.client.gui.components.OverlayImageButton;
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
import net.minecraft.network.chat.MutableComponent;

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
				this.addEntry(new ControllerBindingList.CategoryEntry(Component.translatable(s1)));
			}

			Component itextcomponent = Component.translatable(keybinding.getDescripti());
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

	public class CategoryEntry extends ControllerBindingList.Entry {
		private final Component labelText;
		private final int labelWidth;

		public CategoryEntry(Component p_i232280_2_) {
			this.labelText = p_i232280_2_;
			this.labelWidth = ControllerBindingList.this.minecraft.font.width(this.labelText);
		}

		@Override
		public void render(GuiGraphics guiGraphics, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			guiGraphics.drawString(ControllerBindingList.this.minecraft.font, this.labelText, ControllerBindingList.this.minecraft.screen.width / 2 - this.labelWidth / 2, p_230432_3_ + p_230432_6_ - 9 - 1, 16777215);
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

	public abstract static class Entry extends ContainerObjectSelectionList.Entry<ControllerBindingList.Entry> {
	}

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
			ControllerModel model = ControllerMod.getInstance().getActiveController().getModel();
			this.btnChangeKeyBinding = new OverlayImageButton(0, 0, 65 /*Forge: add space*/, 20, 
					//Overlay1
					0, 0, 20, 20, null, 20, 20, 
					//Overlay2
					0, 0, 20, 20, null, 20, 20, 
					(p_214386_2_) -> {
						ControllerBindingList.this.controlsScreen.keyToSet = controllerBinding;
					}, keyDesc) {
				@Override
				protected MutableComponent createNarrationMessage() {
					return controllerBinding.isBoundToButton(model) ? Component.translatable("narrator.controls.unbound", keyDesc) : Component.translatable("narrator.controls.bound", keyDesc, super.createNarrationMessage());
				}
			};
			this.btnReset = ScreenHelper.buttonBuilder(Component.translatable("controls.reset"), (p_214387_2_) -> {
				controllerBinding.setToDefault(ControllerMod.getInstance().getActiveController().getModel());
				ControllerBindingList.this.mod.controllerOptions.setKeyBindingCode(ControllerMod.getInstance().getActiveController().getModel(), controllerBinding, controllerBinding.getDefault(ControllerMod.getInstance().getActiveController().getModel()));
				//            ControllerBinding.resetKeyBindingArrayAndHash();
			}).bounds(0, 0, 50, 20).createNarration((narr)-> Component.translatable("narrator.controls.reset", keyDesc)).build();
			this.btnInputType = ScreenHelper.buttonBuilder(controllerBinding.getInputType(ControllerMod.getInstance().getActiveController().getModel()) != null ? controllerBinding.getInputType(ControllerMod.getInstance().getActiveController().getModel()).getDisplayName() : Component.literal(""), (p_214387_2_) -> {
				if (controllerBinding.isAxis()) {
					ControllerBindingList.this.mod.controllerOptions.setKeyBindingInverted(ControllerMod.getInstance().getActiveController().getModel(), controllerBinding, !controllerBinding.isAxisInverted(ControllerMod.getInstance().getActiveController().getModel()));
				} else {
					if (controllerBinding.getInputType(ControllerMod.getInstance().getActiveController().getModel()) == InputType.PRESS) ControllerBindingList.this.mod.controllerOptions.setKeyBindingInputType(ControllerMod.getInstance().getActiveController().getModel(), controllerBinding, InputType.TOGGLE);
					else if (controllerBinding.getInputType(ControllerMod.getInstance().getActiveController().getModel()) == InputType.TOGGLE) ControllerBindingList.this.mod.controllerOptions.setKeyBindingInputType(ControllerMod.getInstance().getActiveController().getModel(), controllerBinding, InputType.HOLD);
					else ControllerBindingList.this.mod.controllerOptions.setKeyBindingInputType(ControllerMod.getInstance().getActiveController().getModel(), controllerBinding, InputType.PRESS);
				}
			}).bounds(0, 10, 70, 20).createNarration((narr)-> Component.translatable("narrator.controls.reset", keyDesc)).build();
		}

		@Override
		public void render(GuiGraphics guiGraphics, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
			boolean flag = ControllerBindingList.this.controlsScreen.keyToSet == this.controllerBinding;
			ControllerModel model = ControllerMod.getInstance().getActiveController().getModel();
			ControllerMap.Button[] button = model.getOrCreate(Lists.newArrayList(controllerBinding.getButtonOnController(model)));
			guiGraphics.drawString(ControllerBindingList.this.minecraft.font, this.keyDesc, p_230432_4_ + 65 - ControllerBindingList.this.maxListLabelWidth, p_230432_3_ + p_230432_6_ / 2 - 9 / 2, 16777215);
			ScreenHelper.setWidgetPosition(this.btnInputType, p_230432_4_ + 166, p_230432_3_);
			if (controllerBinding.isAxis()) {
				this.btnInputType.setMessage(controllerBinding.isAxisInverted(model) ? Component.translatable("gui.inverted") : Component.translatable("Not Inverted"));
			} else {
				this.btnInputType.setMessage(controllerBinding.getInputType(model).getDisplayName());
			}
			this.btnInputType.render(guiGraphics, p_230432_7_, p_230432_8_, p_230432_10_);
			ScreenHelper.setWidgetPosition(this.btnReset, p_230432_4_ + 190 + 50, p_230432_3_);
			this.btnReset.active = !this.controllerBinding.isDefault(model);
			this.btnReset.render(guiGraphics, p_230432_7_, p_230432_8_, p_230432_10_);
			ScreenHelper.setWidgetPosition(this.btnChangeKeyBinding, p_230432_4_ + 98, p_230432_3_);
			this.btnChangeKeyBinding.setFirstOverlay(button[0].getIcon());
			this.btnChangeKeyBinding.adjustFirstOverlay(0, 0);
			this.btnChangeKeyBinding.adjustSecondOverlay(0, 0);
			if (button.length > 1) {
				this.btnChangeKeyBinding.setSecondOverlay(button[1].getIcon());
				this.btnChangeKeyBinding.adjustFirstOverlay(-15, 0);
				this.btnChangeKeyBinding.adjustSecondOverlay(15, 0);
				this.btnChangeKeyBinding.showMessage();
				this.btnChangeKeyBinding.setMessage(Component.literal("+"));
			} else {
				if (button[0].getIcon() != null)
					this.btnChangeKeyBinding.hideMessage();
				this.btnChangeKeyBinding.setSecondOverlay(null);
				this.btnChangeKeyBinding.setMessage(Component.literal(ControllerMap.map(controllerBinding.getButtonOnController(model).get(0), model)));
			}
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
				this.btnChangeKeyBinding.setFirstOverlay(null);
				this.btnChangeKeyBinding.setSecondOverlay(null);
				this.btnChangeKeyBinding.showMessage();
				if (button[0].getIcon() != null)
					this.btnChangeKeyBinding.setMessage((Component.literal("> ")).append(" <").withStyle(ChatFormatting.YELLOW));
				else
					this.btnChangeKeyBinding.setMessage((Component.literal("> ")).append(this.btnChangeKeyBinding.getMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
			} else if (flag1) {
				this.btnChangeKeyBinding.showMessage();
				if (button[0].getIcon() != null)
					this.btnChangeKeyBinding.setMessage(Component.literal("CONFLICT").withStyle(keyCodeModifierConflict ? ChatFormatting.GOLD : ChatFormatting.RED));
				else
					this.btnChangeKeyBinding.setMessage(this.btnChangeKeyBinding.getMessage().copy().withStyle(keyCodeModifierConflict ? ChatFormatting.GOLD : ChatFormatting.RED));
			}
			
			this.btnChangeKeyBinding.render(guiGraphics, p_230432_7_, p_230432_8_, p_230432_10_);
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
