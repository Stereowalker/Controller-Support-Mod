package com.stereowalker.controllermod.client.gui.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
		super(new TranslationTextComponent("controller_options.title"));
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
		prevController = this.addButton(new Button(this.width / 2 - 155, this.height  / 6, 20, 20, new StringTextComponent("<<"), (p_212984_1_) -> {
			if(ControllerUtil.controller>0)ControllerUtil.controller = ControllerUtil.controller-1;
			if (!ControllerUtil.isControllerAvailable(ControllerUtil.controller)) ControllerUtil.enableController = false;
			this.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}));
		ITextComponent name;
		if (hasName) {
			name = new TranslationTextComponent("<").appendString((ControllerUtil.controller+1)+"> "+GLFW.glfwGetJoystickName(ControllerUtil.controller)+" [").appendSibling(new TranslationTextComponent(ControllerUtil.isControllerAvailable(ControllerUtil.controller) ? "controller.connected" : "controller.disconnected")).appendString("]"+" : ").appendSibling(new TranslationTextComponent(ControllerUtil.enableController ? "options.on" : "options.off"));
		} else {
			name = new TranslationTextComponent("<").appendString((ControllerUtil.controller+1)+"> ");
		}
		if (!controllerPresent) {
			Config.controllerModel.set(ControllerModel.CUSTOM);
		}
		controller = this.addButton(new Button(this.width / 2 - 125, this.height  / 6, 250, 20, name, (p_212984_1_) -> {
			ControllerUtil.enableController = !ControllerUtil.enableController;
			this.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}));
		nextController = this.addButton(new Button(this.width / 2 + 135, this.height  / 6, 20, 20, new StringTextComponent(">>"), (p_212984_1_) -> {
			if(ControllerUtil.controller<ControllerMod.getInstance().getTotalConnectedControllers())ControllerUtil.controller = ControllerUtil.controller+1;
			if (!ControllerUtil.isControllerAvailable(ControllerUtil.controller)) ControllerUtil.enableController = false;
			this.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}));
		this.addButton(new IngameSensitivitySlider(this, this.width / 2 - 155, this.height  / 6 + 24, 150));
		this.addButton(new MenuSensitivitySlider(this, this.width / 2 + 5, this.height  / 6 + 24, 150));
		this.addButton(new Button(this.width / 2 - 155, this.height  / 6 + 48, 150, 20, new TranslationTextComponent("gui.useAxisToMove").appendString(" : ").appendSibling(DialogTexts.optionsEnabled(ControllerMod.getInstance().controllerSettings.useAxisToMove)), (p_212984_1_) -> {
			ControllerMod.getInstance().controllerSettings.useAxisToMove = !ControllerMod.getInstance().controllerSettings.useAxisToMove;
			this.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}));
		this.addButton(new Button(this.width / 2 + 5, this.height  / 6 + 48, 150, 20, new TranslationTextComponent("gui.editControllerInput"), (p_212984_1_) -> {
			this.getMinecraft().displayGuiScreen(new ControllerInputOptionsScreen(this, null, 0));
		}));
		this.addButton(new Button(this.width / 2 - 155, this.height  / 6 + 72, 150, 20, new TranslationTextComponent("gui.paperDollOptions"), (p_212984_1_) -> {
			this.minecraft.displayGuiScreen(new PaperDollOptionsScreen(previousScreen));
		}));
		Button trigger = this.addButton(new Button(this.width / 2 + 5, this.height  / 6 + 72, 150, 20, new TranslationTextComponent("gui.triggerSetup"), (p_212984_1_) -> {
			this.getMinecraft().displayGuiScreen(new TriggerSetupScreen(this));
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
		this.addButton(new DeadzoneSlider(this, this.width / 2 - 155, this.height  / 6 + 96, 150));
		this.addButton(new Button(this.width / 2 + 5, this.height  / 6 + 96, 150, 20, new TranslationTextComponent("gui.ingamePlayerNames").appendString(" : ").appendSibling(DialogTexts.optionsEnabled(Config.ingamePlayerNames.get())), (p_212984_1_) -> {
			Config.ingamePlayerNames.set(!Config.ingamePlayerNames.get());
			this.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}));
		this.addButton(new Button(this.width / 2 - 155, this.height  / 6 + 120, 150, 20, new TranslationTextComponent("gui.hideCoordinates").appendString(" : ").appendSibling(DialogTexts.optionsEnabled(Config.hideCoordinates.get())), (p_212984_1_) -> {
			Config.hideCoordinates.set(!Config.hideCoordinates.get());
			this.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}));
		this.addButton(new Button(this.width / 2 - 100, this.height  / 6 + 168, 200, 20, new TranslationTextComponent("gui.done"), (p_212984_1_) -> {
			this.minecraft.displayGuiScreen(this.previousScreen);
		}));
		nextController.active = ControllerUtil.controller<ControllerMod.getInstance().getTotalConnectedControllers();
		prevController.active = ControllerUtil.controller>0;
		controller.active = hasName && controllerPresent;
	}

	@Override
	public void onClose() {
		Config.enableControllers.set(ControllerUtil.enableController);
		Config.ingameSensitivity.set(ControllerUtil.ingameSensitivity);
		Config.menuSensitivity.set(ControllerUtil.menuSensitivity);
		Config.deadzone.set(ControllerUtil.dead_zone);
		Config.controllerNumber.set(ControllerUtil.controller+1);
		Config.enableControllers.save();
		ControllerMod.getInstance().controllerSettings.saveOptions();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		AbstractGui.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@OnlyIn(Dist.CLIENT)
	public class IngameSensitivitySlider extends AbstractSlider {
		ControllerOptionsScreen screen;

		public IngameSensitivitySlider(ControllerOptionsScreen screen, int x, int y, int width) {
			super(x, y, width, 20, new TranslationTextComponent(""), ControllerUtil.ingameSensitivity);
			this./*updateMessage*/func_230979_b_();
			this.screen = screen;
		}

		@Override
		protected void /*updateMessage*/func_230979_b_() {
			String s = (float)this.sliderValue == (float)this.getYImage(false) ? I18n.format("options.off") : (int)((float)this.sliderValue * 100.0F) + "%";
			this.setMessage(new TranslationTextComponent("gui.ingameSensitivity").appendString(": " + s));
		}

		@Override
		protected void /*applyValue*/func_230972_a_() {
			ControllerUtil.ingameSensitivity = this.sliderValue;
			screen.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public class MenuSensitivitySlider extends AbstractSlider {
		ControllerOptionsScreen screen;

		public MenuSensitivitySlider(ControllerOptionsScreen screen, int x, int y, int width) {
			super(x, y, width, 20, new TranslationTextComponent(""), ControllerUtil.menuSensitivity);
			this./*updateMessage*/func_230979_b_();
			this.screen = screen;
		}

		@Override
		protected void /*updateMessage*/func_230979_b_() {
			String s = (float)this.sliderValue == (float)this.getYImage(false) ? I18n.format("options.off") : (int)((float)this.sliderValue * 100.0F) + "%";
			this.setMessage(new TranslationTextComponent("gui.menuSensitivity").appendString(": " + s));
		}

		@Override
		protected void /*applyValue*/func_230972_a_() {
			ControllerUtil.menuSensitivity = this.sliderValue;
			screen.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}
	}
	
	public static <T extends Enum<?>> T rotateEnumForward(T input, T[] values) {
		if (input.ordinal() == values.length - 1) {
			return values[0];
		}
		else {
			return values[input.ordinal() + 1];
		}
	}
	
	public static <T extends Enum<?>> T rotateEnumBackward(T input, T[] values) {
		if (input.ordinal() == values.length - 1) {
			return values[0];
		}
		else {
			return values[input.ordinal() + 1];
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public class DeadzoneSlider extends AbstractSlider {
		ControllerOptionsScreen screen;

		public DeadzoneSlider(ControllerOptionsScreen screen, int x, int y, int width) {
			super(x, y, width, 20, new TranslationTextComponent(""), ControllerUtil.dead_zone);
			this./*updateMessage*/func_230979_b_();
			this.screen = screen;
		}

		@Override
		protected void /*updateMessage*/func_230979_b_() {
			String s = (float)this.sliderValue == (float)this.getYImage(false) ? I18n.format("options.off") : (int)((float)this.sliderValue * 100.0F) + "%";
			this.setMessage(new TranslationTextComponent("gui.deadzone").appendString(": " + s));
		}

		@Override
		protected void /*applyValue*/func_230972_a_() {
			ControllerUtil.dead_zone = this.sliderValue;
			screen.minecraft.displayGuiScreen(new ControllerOptionsScreen(previousScreen));
		}
	}
}