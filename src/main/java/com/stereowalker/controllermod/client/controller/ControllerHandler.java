package com.stereowalker.controllermod.client.controller;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.gui.toasts.ControllerStatusToast;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

public class ControllerHandler {
	private final Minecraft minecraft;

	public ControllerHandler(Minecraft minecraftIn) {
		this.minecraft = minecraftIn;
	}
	
	public void controllerConnectedCallback(int jid, int status){
		boolean connected = status == GLFW.GLFW_CONNECTED;
		boolean disconnected = status == GLFW.GLFW_DISCONNECTED;
		Controller controller;
		if(connected) {
			GLFW.glfwGetGamepadName(jid);
			controller = new Controller(jid, GLFW.glfwGetJoystickName(jid), GLFW.glfwGetJoystickGUID(jid), GLFW.glfwGetJoystickUserPointer(jid));
			ControllerMod.getInstance().controllers.add(controller);
			ControllerStatusToast.addOrUpdate(minecraft.getToasts(), ControllerStatusToast.Type.CONNECT, new TextComponent(controller.getName()));
		}
		else if (disconnected) {
			controller = ControllerMod.getInstance().getController(jid);
			if (controller != null) {
				ControllerStatusToast.addOrUpdate(minecraft.getToasts(), ControllerStatusToast.Type.DISCONNECT, new TextComponent(controller.getName()));
				ControllerMod.getInstance().controllers.remove(controller);
			}
		}
		else {
			controller = null;
		}
		if (controller != null) {
			
			System.out.println(jid +" "+ControllerMod.getInstance().controllers.size()+" "+GLFW.glfwGetGamepadName(jid));
		}
	}

	private List<ControllerMapping> previouslyUsed = Lists.newArrayList();

	public void handleMappings(Controller controller, List<UseCase> useCase) {
		if (ControllerUtil.isListening) {
			if (useCase.contains(UseCase.INGAME)) this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
			int i = 0, j = 0;
			List<ControllerMapping> currentlyUsing = ControllerMapping.retrieveActiveMappings(controller, useCase);
			//This shoudl release buttons we are no longer holding
			for (ControllerMapping binding : previouslyUsed) {
				if (!currentlyUsing.contains(binding) && binding != null) {
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
