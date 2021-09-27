package com.stereowalker.controllermod.client.gui.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControllerOptionsScreen extends Screen {
	private final Screen previousScreen;
	private Button nextController;
	private Button prevController;
	private Button controller;
	private ControllerMod mod;

	public ControllerOptionsScreen(Screen screenIn) {
		super(new TranslatableComponent("controller_options.title"));
		this.previousScreen = screenIn;
		this.mod = ControllerMod.getInstance();
	}

	@Override
	public void init() {
		this.mod.controllerSettings.lastGUID = ControllerMod.getInstance().getActiveController().getGUID();
		this.mod.controllerSettings.saveOptions();
		for (ControllerModel model : ControllerModel.modelList()) {
			if (model.getGUID().equals(
					ControllerMod.getInstance().getActiveController().getGUID())) {
				Config.controllerModel.set(model);
			}
		}
		boolean hasName = GLFW.glfwGetJoystickName(ControllerUtil.controller) != null;
		boolean controllerPresent = ControllerUtil.isControllerAvailable(ControllerUtil.controller);
		prevController = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height  / 6, 20, 20, new TextComponent("<<"), (p_212984_1_) -> {
			if(ControllerUtil.controller>0)ControllerUtil.controller = ControllerUtil.controller-1;
			if (!ControllerUtil.isControllerAvailable(ControllerUtil.controller)) ControllerUtil.enableController = false;
			this.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}));
		Component name;
		if (hasName) {
			name = new TranslatableComponent("<").append((ControllerUtil.controller+1)+"> "+GLFW.glfwGetJoystickName(ControllerUtil.controller)+" [").append(new TranslatableComponent(ControllerUtil.isControllerAvailable(ControllerUtil.controller) ? "controller.connected" : "controller.disconnected")).append("]"+" : ").append(new TranslatableComponent(ControllerUtil.enableController ? "options.on" : "options.off"));
		} else {
			name = new TranslatableComponent("<").append((ControllerUtil.controller+1)+"> ");
		}
		if (!controllerPresent) {
			Config.controllerModel.set(ControllerModel.CUSTOM);
		}
		controller = this.addRenderableWidget(new Button(this.width / 2 - 125, this.height  / 6, 250, 20, name, (p_212984_1_) -> {
			ControllerUtil.enableController = !ControllerUtil.enableController;
			this.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}));
		nextController = this.addRenderableWidget(new Button(this.width / 2 + 135, this.height  / 6, 20, 20, new TextComponent(">>"), (p_212984_1_) -> {
			if(ControllerUtil.controller<ControllerMod.getInstance().getTotalConnectedControllers())ControllerUtil.controller = ControllerUtil.controller+1;
			if (!ControllerUtil.isControllerAvailable(ControllerUtil.controller)) ControllerUtil.enableController = false;
			this.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}));
		this.addRenderableWidget(new IngameSensitivitySlider(this, this.width / 2 - 155, this.height  / 6 + 24, 150));
		this.addRenderableWidget(new MenuSensitivitySlider(this, this.width / 2 + 5, this.height  / 6 + 24, 150));
		this.addRenderableWidget(new Button(this.width / 2 - 155, this.height  / 6 + 48, 150, 20, new TranslatableComponent("gui.useAxisToMove").append(" : ").append(CommonComponents.optionStatus(ControllerMod.getInstance().controllerSettings.useAxisToMove)), (p_212984_1_) -> {
			ControllerMod.getInstance().controllerSettings.useAxisToMove = !ControllerMod.getInstance().controllerSettings.useAxisToMove;
			this.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}));
		this.addRenderableWidget(new Button(this.width / 2 + 5, this.height  / 6 + 48, 150, 20, new TranslatableComponent("gui.editControllerInput"), (p_212984_1_) -> {
			this.getMinecraft().setScreen(new ControllerInputOptionsScreen(this, null, 0));
		}));
		this.addRenderableWidget(new Button(this.width / 2 - 155, this.height  / 6 + 72, 150, 20, new TranslatableComponent("gui.paperDollOptions"), (p_212984_1_) -> {
			this.minecraft.setScreen(new PaperDollOptionsScreen(previousScreen));
		}));
		Button trigger = this.addRenderableWidget(new Button(this.width / 2 + 5, this.height  / 6 + 72, 150, 20, new TranslatableComponent("gui.triggerSetup"), (p_212984_1_) -> {
			this.getMinecraft().setScreen(new TriggerSetupScreen(this));
		}));
		if (ControllerUtil.enableController && controllerPresent) {
			trigger.active = (Config.controllerModel.get() == ControllerModel.CUSTOM || Config.debug_mode.get()) &&  mod.
					getActiveController().
					getAxes().
					capacity() > 0;
		}
		else {
			trigger.active = false;
		}
		this.addRenderableWidget(new DeadzoneSlider(this, this.width / 2 - 155, this.height  / 6 + 96, 150));
		this.addRenderableWidget(new Button(this.width / 2 + 5, this.height  / 6 + 96, 150, 20, new TranslatableComponent("gui.ingamePlayerNames").append(" : ").append(CommonComponents.optionStatus(Config.ingamePlayerNames.get())), (p_212984_1_) -> {
			Config.ingamePlayerNames.set(!Config.ingamePlayerNames.get());
			this.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}));
		this.addRenderableWidget(new Button(this.width / 2 - 155, this.height  / 6 + 120, 150, 20, new TranslatableComponent("gui.hideCoordinates").append(" : ").append(CommonComponents.optionStatus(Config.hideCoordinates.get())), (p_212984_1_) -> {
			Config.hideCoordinates.set(!Config.hideCoordinates.get());
			this.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}));
		this.addRenderableWidget(new Button(this.width / 2 - 100, this.height  / 6 + 168, 200, 20, new TranslatableComponent("gui.done"), (p_212984_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}));
		nextController.active = ControllerUtil.controller<ControllerMod.getInstance().getTotalConnectedControllers();
		prevController.active = ControllerUtil.controller>0;
		controller.active = hasName && controllerPresent;
	}

	@Override
	public void removed() {
		Config.enableControllers.set(ControllerUtil.enableController);
		Config.ingameSensitivity.set(ControllerUtil.ingameSensitivity);
		Config.menuSensitivity.set(ControllerUtil.menuSensitivity);
		Config.deadzone.set(ControllerUtil.dead_zone);
		Config.controllerNumber.set(ControllerUtil.controller+1);
		Config.enableControllers.save();
		ControllerMod.getInstance().controllerSettings.saveOptions();
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		GuiComponent.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@OnlyIn(Dist.CLIENT)
	public class IngameSensitivitySlider extends AbstractSliderButton {
		ControllerOptionsScreen screen;

		public IngameSensitivitySlider(ControllerOptionsScreen screen, int x, int y, int width) {
			super(x, y, width, 20, new TranslatableComponent(""), ControllerUtil.ingameSensitivity);
			this.updateMessage();
			this.screen = screen;
		}

		@Override
		protected void updateMessage() {
			String s = (float)this.value == (float)this.getYImage(false) ? I18n.get("options.off") : (int)((float)this.value * 100.0F) + "%";
			this.setMessage(new TranslatableComponent("gui.ingameSensitivity").append(": " + s));
		}

		@Override
		protected void applyValue() {
			ControllerUtil.ingameSensitivity = this.value;
			screen.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public class MenuSensitivitySlider extends AbstractSliderButton {
		ControllerOptionsScreen screen;

		public MenuSensitivitySlider(ControllerOptionsScreen screen, int x, int y, int width) {
			super(x, y, width, 20, new TranslatableComponent(""), ControllerUtil.menuSensitivity);
			this.updateMessage();
			this.screen = screen;
		}

		@Override
		protected void updateMessage() {
			String s = (float)this.value == (float)this.getYImage(false) ? I18n.get("options.off") : (int)((float)this.value * 100.0F) + "%";
			this.setMessage(new TranslatableComponent("gui.menuSensitivity").append(": " + s));
		}

		@Override
		protected void applyValue() {
			ControllerUtil.menuSensitivity = this.value;
			screen.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public class DeadzoneSlider extends AbstractSliderButton {
		ControllerOptionsScreen screen;

		public DeadzoneSlider(ControllerOptionsScreen screen, int x, int y, int width) {
			super(x, y, width, 20, new TranslatableComponent(""), ControllerUtil.dead_zone);
			this.updateMessage();
			this.screen = screen;
		}

		@Override
		protected void updateMessage() {
			String s = (float)this.value == (float)this.getYImage(false) ? I18n.get("options.off") : (int)((float)this.value * 100.0F) + "%";
			this.setMessage(new TranslatableComponent("gui.deadzone").append(": " + s));
		}

		@Override
		protected void applyValue() {
			ControllerUtil.dead_zone = this.value;
			screen.minecraft.setScreen(new ControllerOptionsScreen(previousScreen));
		}
	}
}