package com.stereowalker.controllermod.client.controller;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;

public class Controller {
	private int id;
	private String name;
	private String GUID;
	private long userPointer;

	public Controller(int id) {
		this(id, GLFW.glfwGetJoystickName(id), GLFW.glfwGetJoystickGUID(id), GLFW.glfwGetJoystickUserPointer(id));
	}

	public Controller(int id, String name, String guid, long userPointer) {
		this.id = id;
		this.name = name;
		this.GUID = guid;
		this.userPointer = userPointer;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getGUID() {
		return GUID;
	}

	public long getUserPointer() {
		return userPointer;
	}

	public FloatBuffer getAxes() {
		return GLFW.glfwGetJoystickAxes(id);
	}

	public float getAxis(int index) {
		if (getAxes() != null) {
			try {
				return getAxes().get(index);
			} 
			catch (IndexOutOfBoundsException e) {
				return 0;
			}
		}
		return 0;
	}

	public ByteBuffer getHats() {
		return GLFW.glfwGetJoystickHats(id);
	}

	public byte getHat(int index) {
		if (getHats() != null) {
			try {
				return getHats().get(index);
			} 
			catch (IndexOutOfBoundsException e) {
				return 0;
			}
		}
		return 0;
	}

	public ByteBuffer getButtons() {
		return GLFW.glfwGetJoystickButtons(id);
	}

	public byte getButton(int index) {
		if (getButtons() != null) {
			try {
				return getButtons().get(index);
			} 
			catch (IndexOutOfBoundsException e) {
				return 0;
			}
		}
		return 0;
	}

	public byte getDpadUp() {
		return (byte) ((getHat(0) == GLFW.GLFW_HAT_UP || getHat(0) == GLFW.GLFW_HAT_RIGHT_UP || getHat(0) == GLFW.GLFW_HAT_LEFT_UP) ? 1 : 0);
	}

	public byte getDpadLeft() {
		return (byte) ((getHat(0) == GLFW.GLFW_HAT_LEFT || getHat(0) == GLFW.GLFW_HAT_LEFT_UP || getHat(0) == GLFW.GLFW_HAT_LEFT_DOWN) ? 1 : 0);
	}

	public byte getDpadDown() {
		return (byte) ((getHat(0) == GLFW.GLFW_HAT_DOWN || getHat(0) == GLFW.GLFW_HAT_RIGHT_DOWN || getHat(0) == GLFW.GLFW_HAT_LEFT_DOWN) ? 1 : 0);
	}

	public byte getDpadRight() {
		return (byte) ((getHat(0) == GLFW.GLFW_HAT_RIGHT || getHat(0) == GLFW.GLFW_HAT_RIGHT_DOWN || getHat(0) == GLFW.GLFW_HAT_RIGHT_UP) ? 1 : 0);
	}

	public List<String> getButtonsDown() {
		List<String> buttons = new ArrayList<String>();
		if (this != null) {
			if (this.getButtons() != null) {
				for (int i = 0; i < this.getButtons().capacity(); i++) {
					if (this.getButton(i) > ControllerMod.CONFIG.deadzone && this.getButton(i) <= 1.0F) buttons.add("button"+i);
				}
				for (int i = 0; i < this.getAxes().capacity(); i++) {
					List<Integer> triggers0 = getModel() == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerOptions.positiveTriggerAxes : getModel().getControllerPositiveTriggers();
					if (!triggers0.contains(i)) {
						if (this.getAxis(i) > ControllerMod.CONFIG.deadzone && this.getAxis(i) <= 1.0) buttons.add("axis_pos"+i);
					}

					List<Integer> triggers1 = getModel() == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerOptions.negativeTriggerAxes : getModel().getControllerNegativeTriggers();
					if (!triggers1.contains(i)) {
						if (-this.getAxis(i) > ControllerMod.CONFIG.deadzone && -this.getAxis(i) <= 1.0) buttons.add("axis_neg"+i);
					}
				}
				byte UP = this.getDpadUp();
				byte DOWN = this.getDpadDown();
				byte LEFT = this.getDpadLeft();
				byte RIGHT = this.getDpadRight();
				if (UP > 0.1F) buttons.add("UP"); 
				if (DOWN > 0.1F) buttons.add("DOWN"); 
				if (LEFT > 0.1F) buttons.add("LEFT"); 
				if (RIGHT > 0.1F) buttons.add("RIGHT"); 
			}
			else {
				System.out.println("There are no buttons on this controller");
			}
		}
		return buttons;
	}

	public List<String> getAxesMoved() {
		List<String> axes = new ArrayList<String>();
		if (this != null) {
			for (int i = 0; i < this.getAxes().capacity(); i++) {
				List<Integer> triggersPos = ControllerMod.getInstance().controllerOptions.controllerModel == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerOptions.positiveTriggerAxes : getModel().getControllerPositiveTriggers();
				List<Integer> triggersNeg = ControllerMod.getInstance().controllerOptions.controllerModel == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerOptions.negativeTriggerAxes : getModel().getControllerNegativeTriggers();
				if (!triggersPos.contains(i) && !triggersNeg.contains(i))
					if (this.getAxis(i) > ControllerMod.CONFIG.deadzone && this.getAxis(i) <= 1.0F) axes.add("axis"+i);
					else if (this.getAxis(i) < -ControllerMod.CONFIG.deadzone && this.getAxis(i) >= -1.0F) axes.add("axis"+i);
			}
		}
		return axes;
	}

	public boolean isButtonDown(String button) {
		if (getButtonsDown() != null && button != null) {
			return getButtonsDown().contains(button);
		} else {
			return false;
		}
	}

	public boolean isAxisMoved(String axis) {
		if (getAxesMoved() != null && axis != null) {
			return getAxesMoved().contains(axis);
		} else {
			return false;
		}
	}

	public ControllerModel getModel() {
		for (ControllerModel model : ControllerModel.modelList()) {
			if (model.getGUID().equals(getGUID())) {
				return model;
			}
		}
		return ControllerMod.getInstance().controllerOptions.controllerModel;
	}
}
