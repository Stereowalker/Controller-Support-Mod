package com.stereowalker.controllermod.client.gui.screen;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerBinding;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.gui.widget.list.ControllerBindingList;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControllerInputOptionsScreen extends Screen {
	private final Screen previousScreen;
	/** The ID of the button that has been pressed. */
	public ControllerBinding keyToSet;
	private int previousInput;
	private ControllerBindingList keyBindingList;
	private ControllerMod mod;
	private Button buttonReset;

	public ControllerInputOptionsScreen(Screen previousScreen, ControllerBinding keyToSet, int previousInput) {
		super(new TranslationTextComponent("options.controller_input.title"));
		this.previousScreen = previousScreen;
		this.keyToSet = keyToSet;
		this.previousInput = previousInput;
		this.mod = ControllerMod.getInstance();
	}

	@Override
	public void init() {
		boolean isModelEnforced = false;
		for (ControllerModel model : ControllerModel.modelList()) {
			if (model.getGUID().equals(
					ControllerMod.getInstance().getActiveController().getGUID())) {
				Config.controllerModel.set(model);
				isModelEnforced = true;
			}
		}
		if (!isModelEnforced && !ControllerUtil.isControllerAvailable(ControllerUtil.controller)) {
			Config.controllerModel.set(ControllerModel.CUSTOM);
			isModelEnforced = true;
		}
		this.keyBindingList = new ControllerBindingList(this, this.minecraft, ControllerMod.getInstance());
		this.children.add(this.keyBindingList);
		this.buttonReset = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 100, 20, new TranslationTextComponent("controls.resetAll"), (p_213125_1_) -> {
			for(ControllerBinding keybinding : mod.controllerSettings.controllerBindings) {
				keybinding.setToDefault(Config.controllerModel.get());
			}

			KeyBinding.resetKeyBindingArrayAndHash();
		}));
		Button model = this.addButton(new Button(this.width / 2 - 155 + 105, this.height - 29, 100, 20, new TranslationTextComponent("gui.model").appendString(" : "+Config.controllerModel.get()), (p_212984_1_) -> {
			Config.controllerModel.set(rotateEnumForward(Config.controllerModel.get(), ControllerModel.values()));
			this.minecraft.displayGuiScreen(new ControllerInputOptionsScreen(previousScreen, keyToSet, awaitingTicks));
		}));
		model.active = !isModelEnforced;
		this.addButton(new Button(this.width / 2 - 155 + 210, this.height - 29, 100, 20, DialogTexts.GUI_DONE, (p_213124_1_) -> {
			this.minecraft.displayGuiScreen(this.previousScreen);
		}));
	}
	
	public static <T extends Enum<?>> T rotateEnumForward(T input, T[] values) {
		if (input.ordinal() == values.length - 1) {
			return values[0];
		}
		else {
			return values[input.ordinal() + 1];
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && keyToSet != null) {
			ControllerMod.getInstance().controllerSettings.setKeyBindingCode(Config.controllerModel.get(), keyToSet, " ");
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void onClose() {
		mod.controllerSettings.saveOptions();
	}

	private int awaitingTicks = 0;
	@Override
	public void tick() {
		if (ControllerUtil.enableController && ControllerMod.getInstance().getActiveController() != null) {
			if (keyToSet != null) {
				List<String> buttons;
				if (keyToSet.isAxis()) buttons = ControllerMod.getInstance().getActiveController().getAxesMoved();
				else buttons = ControllerMod.getInstance().getActiveController().getButtonsDown();
				ControllerUtil.isListening = false;
				awaitingTicks++;
				if (awaitingTicks > 5 && awaitingTicks < 100 && buttons.size() > 0) {
					ControllerMod.getInstance().controllerSettings.setKeyBindingCode(Config.controllerModel.get(), keyToSet, buttons.get(0));
					keyToSet = null;
				}
				if (awaitingTicks >= 100) {
					ControllerMod.getInstance().controllerSettings.setKeyBindingCode(Config.controllerModel.get(), keyToSet, ControllerUtil.getControllerInputId(previousInput));
					keyToSet = null;
				}
			}
			else {
				awaitingTicks = 0;
				ControllerUtil.isListening = true;
			}
		} else {
			keyToSet = null;
		}
	}

	public boolean isAwaitingInput() {
		return awaitingTicks > 0;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		this.keyBindingList.render(matrixStack, mouseX, mouseY, partialTicks);
		AbstractGui.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
		boolean flag = false;

		for(ControllerBinding keybinding : mod.controllerSettings.controllerBindings) {
			if (!keybinding.isDefault(Config.controllerModel.get())) {
				flag = true;
				break;
			}
		}

		this.buttonReset.active = flag;
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}