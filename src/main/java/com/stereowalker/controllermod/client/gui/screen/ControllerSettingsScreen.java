package com.stereowalker.controllermod.client.gui.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(EnvType.CLIENT)
public class ControllerSettingsScreen extends Screen {
	private final Screen previousScreen;
	private Button nextController;
	private Button prevController;
	private Button controller;
	private ControllerMod mod;

	public ControllerSettingsScreen(Screen screenIn) {
		super(new TranslatableComponent("options.controller_settings.title"));
		this.previousScreen = screenIn;
		this.mod = ControllerMod.getInstance();
	}

	@Override
	public void init() {
		ControllerOptions options = this.mod.controllerOptions;
		options.lastGUID = ControllerMod.getInstance().getActiveController().getGUID();
		options.saveOptions();
		for (ControllerModel model : ControllerModel.modelList()) {
			if (model.getGUID().equals(
					ControllerMod.getInstance().getActiveController().getGUID())) {
				this.mod.controllerOptions.controllerModel = model;
			}
		}
		boolean hasName = GLFW.glfwGetJoystickName(options.controllerNumber) != null;
		boolean controllerPresent = ControllerUtil.isControllerAvailable(ControllerMod.getInstance().controllerOptions.controllerNumber);
		prevController = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height  / 6, 20, 20, new TextComponent("<<"), (p_212984_1_) -> {
			if(options.controllerNumber>0)options.controllerNumber = options.controllerNumber-1;
			if (!ControllerUtil.isControllerAvailable(options.controllerNumber)) options.enableController = false;
			this.minecraft.setScreen(new ControllerSettingsScreen(previousScreen));
		}));
		Component name;
		if (hasName) {
			name = new TranslatableComponent("<").append((options.controllerNumber+1)+"> "+GLFW.glfwGetJoystickName(options.controllerNumber)+" [").append(new TranslatableComponent(ControllerUtil.isControllerAvailable(options.controllerNumber) ? "controller.connected" : "controller.disconnected")).append("]"+" : ").append(new TranslatableComponent(options.enableController ? "options.on" : "options.off"));
		} else {
			name = new TranslatableComponent("<").append((options.controllerNumber+1)+"> ");
		}
		if (!controllerPresent) {
			this.mod.controllerOptions.controllerModel = ControllerModel.CUSTOM;
		}
		controller = this.addRenderableWidget(new Button(this.width / 2 - 125, this.height  / 6, 250, 20, name, (p_212984_1_) -> {
			options.enableController = !options.enableController;
			this.minecraft.setScreen(new ControllerSettingsScreen(previousScreen));
		}));
		nextController = this.addRenderableWidget(new Button(this.width / 2 + 135, this.height  / 6, 20, 20, new TextComponent(">>"), (p_212984_1_) -> {
			if(options.controllerNumber<ControllerMod.getInstance().getTotalConnectedControllers())options.controllerNumber = options.controllerNumber+1;
			if (!ControllerUtil.isControllerAvailable(options.controllerNumber)) options.enableController = false;
			this.minecraft.setScreen(new ControllerSettingsScreen(previousScreen));
		}));
		this.addRenderableWidget(new Button(this.width / 2 - 155, this.height  / 6 + 24, 150, 20, new TranslatableComponent("gui.paperDollOptions"), (p_212984_1_) -> {
			this.minecraft.setScreen(new PaperDollOptionsScreen(previousScreen));
		}));
		Button trigger = this.addRenderableWidget(new Button(this.width / 2 + 5, this.height  / 6 + 24, 150, 20, new TranslatableComponent("gui.triggerSetup"), (p_212984_1_) -> {
			this.minecraft.setScreen(new TriggerSetupScreen(this));
		}));
		if (options.enableController && controllerPresent) {
			trigger.active = (this.mod.controllerOptions.controllerModel == ControllerModel.CUSTOM || ControllerMod.CONFIG.debug) && mod.
					getActiveController().
					getAxes() != null && 
					mod.
					getActiveController().
					getAxes().
					capacity() > 0;
		}
		else {
			trigger.active = false;
		}
		this.addRenderableWidget(new Button(this.width / 2 - 100, this.height  / 6 + 168, 200, 20, new TranslatableComponent("gui.done"), (p_212984_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}));
		nextController.active = options.controllerNumber<ControllerMod.getInstance().getTotalConnectedControllers();
		prevController.active = options.controllerNumber>0;
		controller.active = hasName && controllerPresent;
	}

	@Override
	public void removed() {
		ControllerMod.getInstance().controllerOptions.saveOptions();
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		GuiComponent.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}