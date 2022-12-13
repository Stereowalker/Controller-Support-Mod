package com.stereowalker.controllermod.client;

import java.util.List;

import com.google.common.collect.Sets;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;
import com.stereowalker.controllermod.client.controller.ControllerUtil.ListeningMode;
import com.stereowalker.controllermod.client.controller.UseCase;
import com.stereowalker.controllermod.client.gui.toasts.ControllerStatusToast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;

public class ControllerHandler {
	private final Minecraft minecraft;
	private final ControllerMod controllerMod;

	public ControllerHandler(ControllerMod controllerModIn, Minecraft minecraftIn) {
		this.minecraft = minecraftIn;
		this.controllerMod = controllerModIn;
	}

	public void controllerConnectedCallback(int jid, int status){
		boolean connected = status == GLFW.GLFW_CONNECTED;
		boolean disconnected = status == GLFW.GLFW_DISCONNECTED;
		Controller controller;
		if(connected) {
			GLFW.glfwGetGamepadName(jid);
			controller = new Controller(jid, GLFW.glfwGetJoystickName(jid), GLFW.glfwGetJoystickGUID(jid), GLFW.glfwGetJoystickUserPointer(jid));
			controllerMod.controllers.add(controller);
			ControllerStatusToast.addOrUpdate(minecraft.getToasts(), ControllerStatusToast.Type.CONNECT, new TextComponent(controller.getName()));
		}
		else if (disconnected) {
			controller = controllerMod.getController(jid);
			if (controller != null) {
				ControllerStatusToast.addOrUpdate(minecraft.getToasts(), ControllerStatusToast.Type.DISCONNECT, new TextComponent(controller.getName()));
				controllerMod.controllers.remove(controller);
			}
		}
		else {
			controller = null;
		}
		if (controller != null) {

			System.out.println(jid +" "+controllerMod.controllers.size()+" "+GLFW.glfwGetGamepadName(jid));
		}
	}

	private List<ControllerMapping> previouslyUsed = Lists.newArrayList();
	private boolean forceRelease = false;
	public void addToPrevoiuslyUsed(ControllerMapping ma) {
		previouslyUsed.add(ma);
		forceRelease = true;
	}
	
	public boolean forceRelease() {
		return forceRelease;
	}

	
	boolean left = false, right = false, up = false, down = false;
	public void processControllerInput(Controller controller, List<UseCase> useCase) {
		if (ControllerUtil.listeningMode == ListeningMode.KEYBOARD && !useCase.contains(UseCase.INGAME)) {
			if (controller.isButtonDown(this.controllerMod.controllerOptions.controllerBindKeyboard.getButtonOnController(controller.getModel())) && this.controllerMod.onScreenKeyboard.switchCooldown == 0) {
				this.controllerMod.onScreenKeyboard.switchKeyboard();
			}
			else {
				OnScreenKeyboard keyboard = this.controllerMod.onScreenKeyboard;
				ControllerOptions options = this.controllerMod.controllerOptions;
				int mods = keyboard.isCapsLocked ? GLFW.GLFW_MOD_CAPS_LOCK : 0;
				long handle = minecraft.getWindow().getWindow();
				if (options.controllerBindKeyboardLeft.isBoundToButton(controller.getModel()))
					ControllerUtil.pushDown(options.controllerBindKeyboardLeft.getButtonOnController(controller.getModel()), controller, InputType.PRESS, 0.9f, () -> left = true, () -> left = false);
				if (options.controllerBindKeyboardRight.isBoundToButton(controller.getModel()))
					ControllerUtil.pushDown(options.controllerBindKeyboardRight.getButtonOnController(controller.getModel()), controller, InputType.PRESS, 0.9f, () -> right = true, () -> right = false);
				if (options.controllerBindKeyboardUp.isBoundToButton(controller.getModel()))
					ControllerUtil.pushDown(options.controllerBindKeyboardUp.getButtonOnController(controller.getModel()), controller, InputType.PRESS, 0.9f, () -> up = true, () -> up = false);
				if (options.controllerBindKeyboardDown.isBoundToButton(controller.getModel()))
					ControllerUtil.pushDown(options.controllerBindKeyboardDown.getButtonOnController(controller.getModel()), controller, InputType.PRESS, 0.9f, () -> down = true, () -> down = false);
				
				keyboard.changeKey(up, down, left, right);
				
				if (options.controllerBindKeyboardSelect.isBoundToButton(controller.getModel()))
					ControllerUtil.pushDown(options.controllerBindKeyboardSelect.getButtonOnController(controller.getModel()), controller, InputType.PRESS, ControllerMod.CONFIG.deadzone, () -> {minecraft.keyboardHandler.charTyped(handle, keyboard.getUnicodeKey(), mods); minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));}, () -> {});
				if (options.controllerBindKeyboardBackspace.isBoundToButton(controller.getModel())) {
					int key = GLFW.GLFW_KEY_BACKSPACE;
					ControllerUtil.pushDown(options.controllerBindKeyboardBackspace.getButtonOnController(controller.getModel()), controller, InputType.PRESS, ControllerMod.CONFIG.deadzone, () -> minecraft.keyboardHandler.keyPress(handle, key, 0, 1, 0), () -> minecraft.keyboardHandler.keyPress(handle, key, 0, 0, 0));
				}
				if (options.controllerBindKeyboardArrowLeft.isBoundToButton(controller.getModel())) {
					int key = GLFW.GLFW_KEY_LEFT;
					ControllerUtil.pushDown(options.controllerBindKeyboardArrowLeft.getButtonOnController(controller.getModel()), controller, InputType.PRESS, ControllerMod.CONFIG.deadzone, () -> minecraft.keyboardHandler.keyPress(handle, key, 0, 1, 0), () -> minecraft.keyboardHandler.keyPress(handle, key, 0, 0, 0));
				}
				if (options.controllerBindKeyboardArrowRight.isBoundToButton(controller.getModel())) {
					int key = GLFW.GLFW_KEY_RIGHT;
					ControllerUtil.pushDown(options.controllerBindKeyboardArrowRight.getButtonOnController(controller.getModel()), controller, InputType.PRESS, ControllerMod.CONFIG.deadzone, () -> minecraft.keyboardHandler.keyPress(handle, key, 0, 1, 0), () -> minecraft.keyboardHandler.keyPress(handle, key, 0, 0, 0));
				}
				if (options.controllerBindKeyboardSpace.isBoundToButton(controller.getModel())) {
					int key = GLFW.GLFW_KEY_SPACE;
					ControllerUtil.pushDown(options.controllerBindKeyboardSpace.getButtonOnController(controller.getModel()), controller, InputType.PRESS, ControllerMod.CONFIG.deadzone, () -> minecraft.keyboardHandler.charTyped(handle, key, mods), () -> {});
				}
				if (options.controllerBindKeyboardCaps.isBoundToButton(controller.getModel()))
					ControllerUtil.pushDown(options.controllerBindKeyboardCaps.getButtonOnController(controller.getModel()), controller, InputType.PRESS, ControllerMod.CONFIG.deadzone, () -> keyboard.isCapsLocked = !keyboard.isCapsLocked, () -> {});
			}
		}
		else if (ControllerUtil.listeningMode == ListeningMode.LISTEN_TO_MAPPINGS) {
			if (useCase.contains(UseCase.INGAME)) this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
			int i = 0, j = 0;
			List<ControllerMapping> currentlyUsing = ControllerMapping.retrieveActiveMappings(controller, useCase);
			//This shoudl release buttons we are no longer holding
			for (ControllerMapping binding : Sets.newHashSet(previouslyUsed)) {
				if ((!currentlyUsing.contains(binding) || forceRelease) && binding != null) {
					j++;
					if (!binding.isAxis()) {
						if (binding.isBoundToButton(controller.getModel()) && (useCase.contains(binding.getUseCase()))) {
							//							if (controller.isButtonDown(binding.getButtonOnController(controller.getModel()))) {
							//								binding.tick();
							//							} else {
							binding.release();
							//							}
							ControllerUtil.updateButtonState(binding, binding.getButtonOnController(controller.getModel()), controller, binding.getButtonOnKeyboardOrMouse(), binding.getInputType(controller.getModel()));
						}
					} else {
						binding.axis = 0;
					}
				}
			}
			previouslyUsed.clear();
			forceRelease = false;
			for (ControllerMapping binding : currentlyUsing) {
				i++;
				if (binding.isAxis()) {
					if (binding.isBoundToButton(controller.getModel()) && (useCase.contains(binding.getUseCase())))
						binding.axis = ControllerUtil.updateAxisState(binding.getButtonOnController(controller.getModel()), controller) * (binding.isAxisInverted(controller.getModel()) ? -1 : 1);
					else
						binding.axis = 0;
				} else {
					boolean flag = true;
					if (useCase.contains(UseCase.INGAME) && ControllerMod.CONFIG.usePreciseMovement && (binding.getDescripti() == minecraft.options.keyUp.getName() || binding.getDescripti() == minecraft.options.keyRight.getName() || binding.getDescripti() == minecraft.options.keyLeft.getName() || binding.getDescripti() == minecraft.options.keyDown.getName())) flag = false;
					if (useCase.contains(UseCase.CONTAINER) && /* binding.getDescripti() == minecraft.options.keyBindInventory.getName() || */binding.getDescripti() == minecraft.options.keyUse.getName()) flag = false;
					if (flag && binding != null) {
						if (binding.isBoundToButton(controller.getModel()) && (useCase.contains(binding.getUseCase()))) {
							//							if (controller.isButtonDown(binding.getButtonOnController(controller.getModel()))) {
							binding.tick();
							//							} else {
							//								binding.release();
							//							}
							ControllerUtil.updateButtonState(binding, binding.getButtonOnController(controller.getModel()), controller, binding.getButtonOnKeyboardOrMouse(), binding.getInputType(controller.getModel()));
						}
					}
				}
				previouslyUsed.add(binding);
			}
			if (i != 0 || j != 0) ControllerMod.debug("Pressed "+i+" bindings and released "+j);
		}
	}

	public void setup(long handle) {
		ControllerUtil.setGamepadCallbacks((a, b) -> {
			this.minecraft.execute(() -> {
				this.controllerConnectedCallback(a, b);
			});
		});
	}
}
