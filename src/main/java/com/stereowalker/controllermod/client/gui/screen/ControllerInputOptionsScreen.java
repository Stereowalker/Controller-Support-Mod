package com.stereowalker.controllermod.client.gui.screen;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.controller.ControllerUtil.ListeningMode;
import com.stereowalker.controllermod.client.gui.widget.list.ControllerBindingList;
import com.stereowalker.unionlib.client.gui.screens.DefaultScreen;
import com.stereowalker.unionlib.util.ScreenHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ControllerInputOptionsScreen extends DefaultScreen {
	/** The ID of the button that has been pressed. */
	public ControllerMapping keyToSet;
	private int[] previousInputs;
	private ControllerBindingList keyBindingList;
	private ControllerMod mod;
	private Button buttonReset;

	public ControllerInputOptionsScreen(Screen previousScreen, ControllerMapping keyToSet, int[] previousInputs) {
		super(Component.translatable("options.controller_input.title"), previousScreen);
		this.keyToSet = keyToSet;
		this.previousInputs = previousInputs;
		this.mod = ControllerMod.getInstance();
	}

	@Override
	public void init() {
		boolean isModelEnforced = ControllerMod.getInstance().getActiveController().getActualModel() != null && !ControllerMod.CONFIG.useAnyModel;
		if (!isModelEnforced && !ControllerUtil.isControllerAvailable(mod.controllerOptions.controllerNumber)) {
			this.mod.controllerOptions.controllerModel = ControllerModel.CUSTOM;
			isModelEnforced = true;
		}
		this.keyBindingList = new ControllerBindingList(this, this.minecraft, ControllerMod.getInstance());
		this.addWidget(this.keyBindingList);
		this.buttonReset = this.addRenderableWidget(ScreenHelper.buttonBuilder(Component.translatable("controls.resetAll"), (p_213125_1_) -> {
			for(ControllerMapping keybinding : mod.controllerOptions.controllerBindings) {
				keybinding.setToDefault(ControllerMod.getInstance().getActiveController().getModel());
			}

			ControllerMapping.resetMapping();
		}).bounds(this.width / 2 - 165, this.height - 29, 100, 20).build());
		Button model = this.addRenderableWidget(ScreenHelper.buttonBuilder(Component.empty(), (p_212984_1_) -> {
			this.mod.controllerOptions.controllerModel = ControllerModel.nextModel(this.mod.controllerOptions.controllerModel);
			this.minecraft.setScreen(new ControllerInputOptionsScreen(previousScreen, keyToSet, new int[] {0}));
		}).bounds(this.width / 2 - 155 + 95, this.height - 29, 120, 20).build());
		model.active = !isModelEnforced;
		if (isModelEnforced)
			model.setMessage(Component.translatable("gui.model").append(" : ").append(ControllerMod.getInstance().getActiveController().getModel().getDisplayName(false)));
		else
			model.setMessage(Component.translatable("gui.model").append(" : ").append(this.mod.controllerOptions.controllerModel.getDisplayName(ControllerMod.CONFIG.debug)));
		this.addRenderableWidget(ScreenHelper.buttonBuilder(CommonComponents.GUI_DONE, (p_213124_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}).bounds(this.width / 2 - 145 + 210, this.height - 29, 100, 20).build());
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ESCAPE && keyToSet != null) {
			ControllerMod.getInstance().controllerOptions.setKeyBindingCode(ControllerMod.getInstance().getActiveController().getModel(), keyToSet, Lists.newArrayList(" "));
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
					ControllerMod.getInstance().controllerOptions.setKeyBindingCode(ControllerMod.getInstance().getActiveController().getModel(), keyToSet, keyToSet.isAxis() ? Lists.newArrayList(buttons.get(0)) : buttons);
					keyToSet = null;
					ControllerMapping.resetMapping();
				}
				if (awaitingTicks >= 100) {
					List<String> prev = Lists.newArrayList();
					for (int i = 0; i < previousInputs.length; i++) {
						prev.add(ControllerUtil.getControllerInputId(previousInputs[i]));
					}
					ControllerMod.getInstance().controllerOptions.setKeyBindingCode(ControllerMod.getInstance().getActiveController().getModel(), keyToSet, prev);
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
	public void drawOnScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.keyBindingList.render(guiGraphics, mouseX, mouseY, partialTicks);
		boolean flag = false;

		for(ControllerMapping keybinding : mod.controllerOptions.controllerBindings) {
			if (!keybinding.isDefault(ControllerMod.getInstance().getActiveController().getModel())) {
				flag = true;
				break;
			}
		}

		this.buttonReset.active = flag;
	}
}