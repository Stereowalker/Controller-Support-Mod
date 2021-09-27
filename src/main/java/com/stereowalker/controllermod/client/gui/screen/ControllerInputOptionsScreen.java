package com.stereowalker.controllermod.client.gui.screen;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerBinding;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.gui.widget.list.ControllerBindingList;
import com.stereowalker.controllermod.config.Config;
import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
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
		super(new TranslatableComponent("options.controller_input.title"));
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
		this.addWidget(this.keyBindingList);
		this.buttonReset = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 29, 100, 20, new TranslatableComponent("controls.resetAll"), (p_213125_1_) -> {
			for(ControllerBinding keybinding : mod.controllerSettings.controllerBindings) {
				keybinding.setToDefault(Config.controllerModel.get());
			}

			KeyMapping.resetMapping();
		}));
		Button model = this.addRenderableWidget(new Button(this.width / 2 - 155 + 105, this.height - 29, 100, 20, new TranslatableComponent("gui.model").append(" : "+Config.controllerModel.get()), (p_212984_1_) -> {
			Config.controllerModel.set(RegistryHelper.rotateEnumForward(Config.controllerModel.get(), ControllerModel.values()));
			this.minecraft.setScreen(new ControllerInputOptionsScreen(previousScreen, keyToSet, awaitingTicks));
		}));
		model.active = !isModelEnforced;
		this.addRenderableWidget(new Button(this.width / 2 - 155 + 210, this.height - 29, 100, 20, CommonComponents.GUI_DONE, (p_213124_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && keyToSet != null) {
			ControllerMod.getInstance().controllerSettings.setKeyBindingCode(Config.controllerModel.get(), keyToSet, " ");
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void removed() {
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
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		this.keyBindingList.render(matrixStack, mouseX, mouseY, partialTicks);
		GuiComponent.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
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