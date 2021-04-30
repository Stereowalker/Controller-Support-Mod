package com.stereowalker.controllermod.client.controller;

import org.lwjgl.glfw.GLFW;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.gui.toasts.ControllerStatusToast;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

public class ControllerHelper {
	private final Minecraft minecraft;

	public ControllerHelper(Minecraft minecraftIn) {
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
			ControllerStatusToast.addOrUpdate(minecraft.getToastGui(), ControllerStatusToast.Type.CONNECT, new StringTextComponent(controller.getName()));
		}
		else if (disconnected) {
			controller = ControllerMod.getInstance().getController(jid);
			if (controller != null) {
				ControllerStatusToast.addOrUpdate(minecraft.getToastGui(), ControllerStatusToast.Type.DISCONNECT, new StringTextComponent(controller.getName()));
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

	public void registerCallbacks(long handle) {
		ControllerUtil.setGamepadCallbacks((a, b) -> {
			this.minecraft.execute(() -> {
				this.controllerConnectedCallback(a, b);
			});
		});
	}
}
