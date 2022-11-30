package com.stereowalker.controllermod.client.gui.screen;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.controller.ControllerUtil.ListeningMode;
import com.stereowalker.controllermod.client.gui.widget.list.ControllerBindingList;
import com.stereowalker.controllermod.resources.ControllerModelManager;

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
	public ControllerMapping keyToSet;
	private int[] previousInputs;
	private ControllerBindingList keyBindingList;
	private ControllerMod mod;
	private Button buttonReset;

	public ControllerInputOptionsScreen(Screen previousScreen, ControllerMapping keyToSet, int[] previousInputs) {
		super(new TranslatableComponent("options.controller_input.title"));
		this.previousScreen = previousScreen;
		this.keyToSet = keyToSet;
		this.previousInputs = previousInputs;
		this.mod = ControllerMod.getInstance();
	}

	@Override
	public void init() {
		boolean isModelEnforced = false;
		for (ControllerModel model : ControllerModelManager.ALL_MODELS.values()) {
			if (model.getGUID().equals(
					ControllerMod.getInstance().getActiveController().getGUID())) {
				this.mod.controllerOptions.controllerModel = model;
				isModelEnforced = true;
			}
		}
		if (!isModelEnforced && !ControllerUtil.isControllerAvailable(mod.controllerOptions.controllerNumber)) {
			this.mod.controllerOptions.controllerModel = ControllerModel.CUSTOM;
			isModelEnforced = true;
		}
		this.keyBindingList = new ControllerBindingList(this, this.minecraft, ControllerMod.getInstance());
		this.addWidget(this.keyBindingList);
		this.buttonReset = this.addRenderableWidget(new Button(this.width / 2 - 165, this.height - 29, 100, 20, new TranslatableComponent("controls.resetAll"), (p_213125_1_) -> {
			for(ControllerMapping keybinding : mod.controllerOptions.controllerBindings) {
				keybinding.setToDefault(this.mod.controllerOptions.controllerModel);
			}

			ControllerMapping.resetMapping();
		}));
		Button model = this.addRenderableWidget(new Button(this.width / 2 - 155 + 95, this.height - 29, 120, 20, new TranslatableComponent("gui.model").append(" : ").append(this.mod.controllerOptions.controllerModel.getDisplayName(ControllerMod.CONFIG.debug)), (p_212984_1_) -> {
			this.mod.controllerOptions.controllerModel = ControllerModel.nextModel(this.mod.controllerOptions.controllerModel);
			this.minecraft.setScreen(new ControllerInputOptionsScreen(previousScreen, keyToSet, new int[] {0}));
		}));
		model.active = !isModelEnforced;
		this.addRenderableWidget(new Button(this.width / 2 - 145 + 210, this.height - 29, 100, 20, CommonComponents.GUI_DONE, (p_213124_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && keyToSet != null) {
			ControllerMod.getInstance().controllerOptions.setKeyBindingCode(this.mod.controllerOptions.controllerModel, keyToSet, Lists.newArrayList(" "));
			ControllerMapping.resetMapping();
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void removed() {
		mod.controllerOptions.saveOptions();
	}

	private int awaitingTicks = 0;
	@Override
	public void tick() {
		if (ControllerMod.getInstance().controllerOptions.enableController && ControllerMod.getInstance().getActiveController() != null) {
			if (keyToSet != null) {
				List<String> buttons;
				if (keyToSet.isAxis()) buttons = ControllerMod.getInstance().getActiveController().getAxesMoved();
				else buttons = ControllerMod.getInstance().getActiveController().getButtonsDown();
				ControllerUtil.listeningMode = ListeningMode.CHANGE_MAPPINGS;
				awaitingTicks++;
				if (awaitingTicks > 5 && awaitingTicks < 100 && buttons.size() > 0 && buttons.size() <= 2) {
					ControllerMod.debug(buttons.toString());
					ControllerMod.getInstance().controllerOptions.setKeyBindingCode(this.mod.controllerOptions.controllerModel, keyToSet, keyToSet.isAxis() ? Lists.newArrayList(buttons.get(0)) : buttons);
					keyToSet = null;
					ControllerMapping.resetMapping();
				}
				if (awaitingTicks >= 100) {
					List<String> prev = Lists.newArrayList();
					for (int i = 0; i < previousInputs.length; i++) {
						prev.add(ControllerUtil.getControllerInputId(previousInputs[i]));
					}
					ControllerMod.getInstance().controllerOptions.setKeyBindingCode(this.mod.controllerOptions.controllerModel, keyToSet, prev);
					keyToSet = null;
					ControllerMapping.resetMapping();
				}
			}
			else {
				awaitingTicks = 0;
				ControllerUtil.listeningMode = ListeningMode.LISTEN_TO_MAPPINGS;
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

		for(ControllerMapping keybinding : mod.controllerOptions.controllerBindings) {
			if (!keybinding.isDefault(this.mod.controllerOptions.controllerModel)) {
				flag = true;
				break;
			}
		}

		this.buttonReset.active = flag;
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}