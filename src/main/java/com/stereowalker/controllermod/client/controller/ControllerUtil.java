package com.stereowalker.controllermod.client.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallbackI;

import com.mojang.blaze3d.platform.InputConstants;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.VirtualMouseHelper;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(EnvType.CLIENT)
public class ControllerUtil {
	public static VirtualMouseHelper virtualmouse = new VirtualMouseHelper(Minecraft.getInstance());

	static long handle() {
		return Minecraft.getInstance().getWindow().getWindow();
	}

	@SuppressWarnings("resource")
	static MouseHandler mouse() {
		return Minecraft.getInstance().mouseHandler;
	}

	static KeyboardHandler keyboard = (Minecraft.getInstance()).keyboardHandler;

	static Options settings = (Minecraft.getInstance()).options;

	public static boolean isListening = false;

	public static boolean isControllerAvailable(int controller) {
		return /*GLFW.glfwJoystickIsGamepad(controller) &&*/ GLFW.glfwJoystickPresent(controller);
	}

	public static int getKeybindCode(KeyMapping keybinding) {
		return keybinding.key.getValue();
	}

	public static String getControllerInputId(int input) {
		if (input == 0) return " "; 
		for (int i = 1; i < 51; i++) {
			if (input == i) return "button"+(i-1);
		}
		for (int i = 51; i < 76; i++) {
			if (input == i) return "axis_pos"+(i-51);
		}
		for (int i = 76; i < 101; i++) {
			if (input == i) return "axis_neg"+(i-76);
		}
		if (input == 101) return "UP"; 
		if (input == 102) return "DOWN"; 
		if (input == 103) return "LEFT"; 
		if (input == 104) return "RIGHT"; 
		return "???";
	}

	public static int getControllerInputCode(String key) {
		if (key != null) {
			for (int i = 0; i < 105; i++) {
				if (key.equals(getControllerInputId(i))) {
					return i;
				}
			}
		}
		return 0;
	}

	public static void unpressAllKeys() {
		for (int i = 32; i <= 348; i++) {
			int state = GLFW.glfwGetKey(handle(), i);
			if (state == 1)
				keyboard.keyPress(handle(), i, 0, 0, 0); 
		} 
		for (int j = 0; j <= 7; j++) {
			virtualmouse.onPress(handle(), j, 0, 0);
			mouse().onPress(handle(), j, 0, 0);
		} 
		keyMap.forEach((key, value)-> {
			value = 0;
		});
		mouseMap.forEach((key, value)-> {
			value = 0;
		});
		keyToggleMap.forEach((key, value)-> {
			value = 0;
		});
		mouseToggleMap.forEach((key, value)-> {
			value = 0;
		});
	}

	static Map<String, Integer> keyMap = new HashMap<>();

	static Map<String, Integer> mouseMap = new HashMap<>();

	static Map<String, Integer> keyToggleMap = new HashMap<>();

	static Map<String, Integer> mouseToggleMap = new HashMap<>();

	static double prevX;

	static double prevY;

	public static void putKeysInMap(Map<String, Integer> map) {
		for (int i = 0; i < 51; i++) {
			map.put("button"+i, Integer.valueOf(0));
		}
		for (int i = 0; i < 26; i++) {
			map.put("axis_pos"+i, Integer.valueOf(0));
			map.put("axis_neg"+i, Integer.valueOf(0));
		}
		map.put("UP", Integer.valueOf(0));
		map.put("DOWN", Integer.valueOf(0));
		map.put("LEFT", Integer.valueOf(0));
		map.put("RIGHT", Integer.valueOf(0));
	}
	
	public static float updateAxisState(String buttonId, Controller controller) {
		if (buttonId != null && buttonId != " " && buttonId != "???") {
			float controlleraxis = 0.0F; 
			for (int i = 0; i < controller.getAxes().capacity(); i++) {
				List<Integer> triggersPos = ControllerMod.getInstance().controllerSettings.controllerModel == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerSettings.positiveTriggerAxes : controller.getModel().getControllerPositiveTriggers();
				List<Integer> triggersNeg = ControllerMod.getInstance().controllerSettings.controllerModel == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerSettings.negativeTriggerAxes : controller.getModel().getControllerNegativeTriggers();
				if (!triggersPos.contains(i) && !triggersNeg.contains(i))
					if (buttonId.equals("axis"+i)) controlleraxis = controller.getAxes().get(i);
			}
			if (controlleraxis > ControllerMod.CONFIG.deadzone && controlleraxis <= 1.0D) {
				return controlleraxis;
			} else if (controlleraxis < -ControllerMod.CONFIG.deadzone && controlleraxis >= -1.0D) {
				return controlleraxis;
			}
		}
		return 0.0F;
	}
	
	public static void updateButtonState(String buttonId, Controller controller, InputConstants.Type keyType, int buttonOnComputer, InputType inputType) {
		if (buttonId != null && buttonId != " " && buttonId != "???") {
			float controllerButton =2.0F; 
			for (int i = 0; i < controller.getButtons().capacity(); i++) {
				if (buttonId.equals("button"+i)) {
					controllerButton = controller.getButtons().get(i);

				}
			}
			for (int i = 0; i < controller.getAxes().capacity(); i++) {
				List<Integer> triggers0 = ControllerMod.getInstance().controllerSettings.controllerModel == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerSettings.positiveTriggerAxes : controller.getModel().getControllerPositiveTriggers();
				if (!triggers0.contains(i))
					if (buttonId.equals("axis_pos"+i)) controllerButton = controller.getAxes().get(i);
				List<Integer> triggers1 = ControllerMod.getInstance().controllerSettings.controllerModel == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerSettings.negativeTriggerAxes : controller.getModel().getControllerNegativeTriggers();
				if (!triggers1.contains(i))
					if (buttonId.equals("axis_neg"+i)) controllerButton = -controller.getAxes().get(i);
			}
			byte UP = controller.getDpadUp();
			byte DOWN = controller.getDpadDown();
			byte LEFT = controller.getDpadLeft();
			byte RIGHT = controller.getDpadRight();
			if (buttonId == "UP") controllerButton = UP;
			if (buttonId == "DOWN") controllerButton = DOWN;
			if (buttonId == "LEFT") controllerButton = LEFT;
			if (buttonId == "RIGHT") controllerButton = RIGHT;
			if (keyType == InputConstants.Type.KEYSYM) {
				if (keyMap.isEmpty()) putKeysInMap(keyMap);
				if (keyToggleMap.isEmpty()) putKeysInMap(keyToggleMap);
				if (inputType == InputType.PRESS) {
					if ((controllerButton > ControllerMod.CONFIG.deadzone && controllerButton <= 1.0D) && ((Integer)keyMap.get(buttonId)).intValue() == 0) {
						keyMap.put(buttonId, Integer.valueOf(1));
						keyboard.keyPress(handle(), buttonOnComputer, 0, 1, 0);
					} 
					if (controllerButton <= ControllerMod.CONFIG.deadzone && ((Integer)keyMap.get(buttonId)).intValue() == 1) {
						keyMap.put(buttonId, Integer.valueOf(0));
						keyboard.keyPress(handle(), buttonOnComputer, 0, 0, 0);
					} 
				} 
				if (inputType == InputType.TOGGLE) {
					if ((controllerButton > ControllerMod.CONFIG.deadzone && controllerButton <= 1.0D) && ((Integer)keyMap.get(buttonId)).intValue() == 0 && ((Integer)keyToggleMap.get(buttonId)).intValue() == 0) {
						keyMap.put(buttonId, Integer.valueOf(1));
						keyToggleMap.put(buttonId, Integer.valueOf(1));
						keyboard.keyPress(handle(), buttonOnComputer, 0, 1, 0);
					} 
					if (controllerButton <= ControllerMod.CONFIG.deadzone && ((Integer)keyMap.get(buttonId)).intValue() == 1)
						keyMap.put(buttonId, Integer.valueOf(0)); 
					if ((controllerButton > ControllerMod.CONFIG.deadzone && controllerButton <= 1.0D) && ((Integer)keyMap.get(buttonId)).intValue() == 0 && ((Integer)keyToggleMap.get(buttonId)).intValue() == 1) {
						keyMap.put(buttonId, Integer.valueOf(1));
						keyToggleMap.put(buttonId, Integer.valueOf(0));
						keyboard.keyPress(handle(), buttonOnComputer, 0, 0, 0);
					} 
				} 
				if (inputType == InputType.HOLD) {
					if ((controllerButton > ControllerMod.CONFIG.deadzone && controllerButton <= 1.0D))
						keyboard.keyPress(handle(), buttonOnComputer, 0, 1, 0); 
					if (controllerButton <= ControllerMod.CONFIG.deadzone)
						keyboard.keyPress(handle(), buttonOnComputer, 0, 0, 0); 
				} 
			} 
			if (keyType == InputConstants.Type.MOUSE) {
				if (mouseMap.isEmpty()) putKeysInMap(mouseMap);
				if (mouseToggleMap.isEmpty()) putKeysInMap(mouseToggleMap);
				if (inputType == InputType.PRESS) {
					if ((controllerButton > ControllerMod.CONFIG.deadzone && controllerButton <= 1.0D) && ((Integer)mouseMap.get(buttonId)).intValue() == 0) {
						mouseMap.put(buttonId, Integer.valueOf(1));
						virtualmouse.onPress(handle(), buttonOnComputer, 1, 0);
					} 
					if (controllerButton <= ControllerMod.CONFIG.deadzone && ((Integer)mouseMap.get(buttonId)).intValue() == 1) {
						mouseMap.put(buttonId, Integer.valueOf(0));
						virtualmouse.onPress(handle(), buttonOnComputer, 0, 0);
					} 
				}
				if (inputType == InputType.TOGGLE) {
					if ((controllerButton > ControllerMod.CONFIG.deadzone && controllerButton <= 1.0D) && ((Integer)mouseMap.get(buttonId)).intValue() == 0 && ((Integer)mouseToggleMap.get(buttonId)).intValue() == 0) {
						mouseMap.put(buttonId, Integer.valueOf(1));
						mouseToggleMap.put(buttonId, Integer.valueOf(1));
						virtualmouse.onPress(handle(), buttonOnComputer, 1, 0);
					} 
					if (controllerButton <= ControllerMod.CONFIG.deadzone && ((Integer)mouseMap.get(buttonId)).intValue() == 1)
						mouseMap.put(buttonId, Integer.valueOf(0)); 
					if ((controllerButton > ControllerMod.CONFIG.deadzone && controllerButton <= 1.0D) && ((Integer)mouseMap.get(buttonId)).intValue() == 0 && ((Integer)mouseToggleMap.get(buttonId)).intValue() == 1) {
						mouseMap.put(buttonId, Integer.valueOf(1));
						mouseToggleMap.put(buttonId, Integer.valueOf(0));
						virtualmouse.onPress(handle(), buttonOnComputer, 0, 0);
					} 
				}
				if (inputType == InputType.HOLD) {
					if ((controllerButton > ControllerMod.CONFIG.deadzone && controllerButton <= 1.0D))
						virtualmouse.onPress(handle(), buttonOnComputer, 2, 0); 
					if (controllerButton <= ControllerMod.CONFIG.deadzone)
						virtualmouse.onPress(handle(), buttonOnComputer, 0, 0); 
				}
			} 
		} 
	}

	public static void updateMousePosition(float xLAxis, float yLAxis, Controller controller, boolean isCamera, boolean useHats) {
		byte UP;
		byte DOWN;
		byte LEFT;
		byte RIGHT;
		if (controller.getHats() == null && !useHats) {
			UP = 0;
			DOWN = 0;
			LEFT = 0;
			RIGHT = 0;
		} else {
			UP = controller.getDpadUp();
			DOWN = controller.getDpadDown();
			LEFT = controller.getDpadLeft();
			RIGHT = controller.getDpadRight();
		}
		int width = Minecraft.getInstance().getWindow().getWidth();
		int height = Minecraft.getInstance().getWindow().getHeight();
		float yMod = height / 480.0F;
		float xMod = width / 854.0F;

		if (virtualmouse.xpos() >= 0.0D && virtualmouse.xpos() <= width && virtualmouse.ypos() >= 0.0D && virtualmouse.ypos() <= height && !isCamera) {
			if (xLAxis >= -1.0F && xLAxis < -ControllerMod.CONFIG.deadzone)
				virtualmouse.onMove(handle(), virtualmouse.xpos() + xLAxis * ControllerMod.CONFIG.menu_sensitivity * 25.0D * xMod, virtualmouse.ypos() + 0.0D); 
			if (xLAxis <= 1.0F && xLAxis > ControllerMod.CONFIG.deadzone)
				virtualmouse.onMove(handle(), virtualmouse.xpos() + xLAxis * ControllerMod.CONFIG.menu_sensitivity * 25.0D * xMod, virtualmouse.ypos() + 0.0D); 
			if (yLAxis >= -1.0F && yLAxis < -ControllerMod.CONFIG.deadzone)
				virtualmouse.onMove(handle(), virtualmouse.xpos() + 0.0D, virtualmouse.ypos() + yLAxis * ControllerMod.CONFIG.menu_sensitivity * 25.0D * yMod); 
			if (yLAxis <= 1.0F && yLAxis > ControllerMod.CONFIG.deadzone)
				virtualmouse.onMove(handle(), virtualmouse.xpos() + 0.0D, virtualmouse.ypos() + yLAxis * ControllerMod.CONFIG.menu_sensitivity * 25.0D * yMod); 
			if (RIGHT == 1)
				virtualmouse.onMove(handle(), virtualmouse.xpos() + 0.25D * ControllerMod.CONFIG.menu_sensitivity * 25.0D * xMod, virtualmouse.ypos() + 0.0D); 
			if (LEFT == 1)
				virtualmouse.onMove(handle(), virtualmouse.xpos() - 0.25D * ControllerMod.CONFIG.menu_sensitivity * 25.0D * xMod, virtualmouse.ypos() + 0.0D); 
			if (UP == 1)
				virtualmouse.onMove(handle(), virtualmouse.xpos() + 0.0D, virtualmouse.ypos() - 0.25D * ControllerMod.CONFIG.menu_sensitivity * 25.0D * yMod); 
			if (DOWN == 1)
				virtualmouse.onMove(handle(), virtualmouse.xpos() + 0.0D, virtualmouse.ypos() + 0.25D * ControllerMod.CONFIG.menu_sensitivity * 25.0D * yMod); 
		} else if (mouse().isMouseGrabbed() || isCamera) {
			if (xLAxis >= -1.0F && xLAxis < -ControllerMod.CONFIG.deadzone)
				mouse().onMove(handle(), mouse().xpos() + xLAxis * ControllerMod.CONFIG.ingame_sensitivity * 100.0D, mouse().ypos() + 0.0D); 
			if (xLAxis <= 1.0F && xLAxis > ControllerMod.CONFIG.deadzone)
				mouse().onMove(handle(), mouse().xpos() + xLAxis * ControllerMod.CONFIG.ingame_sensitivity * 100.0D, mouse().ypos() + 0.0D); 
			if (yLAxis >= -1.0F && yLAxis < -ControllerMod.CONFIG.deadzone)
				mouse().onMove(handle(), mouse().xpos() + 0.0D, mouse().ypos() + yLAxis * ControllerMod.CONFIG.ingame_sensitivity * 100.0D); 
			if (yLAxis <= 1.0F && yLAxis > ControllerMod.CONFIG.deadzone)
				mouse().onMove(handle(), mouse().xpos() + 0.0D, mouse().ypos() + yLAxis * ControllerMod.CONFIG.ingame_sensitivity * 100.0D); 
		}
		if (virtualmouse.xpos() < 0.0D) {
			virtualmouse.onMove(handle(), 0.0D, virtualmouse.ypos());
		} else if (virtualmouse.ypos() < 0.0D) {
			virtualmouse.onMove(handle(), virtualmouse.xpos(), 0.0D);
		} else if (virtualmouse.xpos() > width) {
			virtualmouse.onMove(handle(), width, virtualmouse.ypos());
		} else if (virtualmouse.ypos() > height) {
			virtualmouse.onMove(handle(), virtualmouse.xpos(), height);
		} else if (virtualmouse.xpos() == 0.0D && virtualmouse.ypos() == 0.0D) {
			virtualmouse.onMove(handle(), (width / 2), (height / 2));
		} 
		if (virtualmouse.xpos() != prevX || virtualmouse.ypos() != prevY)
			mouse().onMove(handle(), virtualmouse.xpos(), virtualmouse.ypos()); 
		prevX = virtualmouse.xpos();
		prevY = virtualmouse.ypos();
	}

	public static void handleIngameInput(Controller controller, float xAxis, float yAxis) {
		if (isListening) {
			keyboard.setSendRepeatsToGui(false);
			for (ControllerBinding binding : ControllerMod.getInstance().controllerSettings.controllerBindings) {
				boolean flag = true;
				if(ControllerMod.CONFIG.usePreciseMovement && (binding.getDescripti() == settings.keyUp.getName() || binding.getDescripti() == settings.keyRight.getName() || binding.getDescripti() == settings.keyLeft.getName() || binding.getDescripti() == settings.keyDown.getName())) flag = false;
				if (flag && binding != null) {
					if (binding.isBoundToButton(controller.getModel()))
						controller.updateButtonState(binding);
				}
			}
		}
	}

	public static void handleScreenInput(Controller controller, float xAxis, float yAxis, float scoll, boolean isIngameMenu) {
		if (isListening) {
			for (ControllerBinding binding : ControllerMod.getInstance().controllerSettings.controllerBindings) {
				boolean flag = true;
				if (flag && binding != null) {
					if (binding.isBoundToButton(controller.getModel()))
						controller.updateButtonState(binding);
				}
			}
			updateMousePosition(xAxis, yAxis, controller, false, true);
			if (scoll >= -1.0F && scoll < -0.1F)
				mouse().onScroll(handle(), 0.0D, -scoll * ControllerMod.CONFIG.menu_sensitivity * 100.0D / 20.0D); 
			if (scoll <= 1.0F && scoll > 0.1F)
				mouse().onScroll(handle(), 0.0D, -scoll * ControllerMod.CONFIG.menu_sensitivity * 100.0D / 20.0D); 
		}
	}

	public static void handleContainerInput(Controller controller, float xAxis, float yAxis, float scoll) {
		if (isListening) {
			for (ControllerBinding binding : ControllerMod.getInstance().controllerSettings.controllerBindings) {
				boolean flag = true;
				if (/* binding.getDescripti() == settings.keyBindInventory.getName() || */binding.getDescripti() == settings.keyUse.getName()) flag = false;
				if (flag && binding != null) {
					if (binding.isBoundToButton(controller.getModel()))
						controller.updateButtonState(binding);
				}
			}
		}

		updateMousePosition(xAxis, yAxis, controller, false, false);
		if (scoll >= -1.0F && scoll < -0.1D)
			mouse().onScroll(handle(), 0.0D, -scoll * ControllerMod.CONFIG.menu_sensitivity * 100.0D / 20.0D); 
		if (scoll <= 1.0F && scoll > 0.1D)
			mouse().onScroll(handle(), 0.0D, -scoll * ControllerMod.CONFIG.menu_sensitivity * 100.0D / 20.0D); 
	}

	public static void setGamepadCallbacks(GLFWJoystickCallbackI p_216503_2_) {
		GLFW.glfwSetJoystickCallback(p_216503_2_);
	}

	public enum InputType {
		PRESS("press"), TOGGLE("toggle"), HOLD("hold");
		
		String name;
		InputType(String name) {
			this.name = name;
		}

		public static InputType getInput(int code) {
			switch (code) {
			case 0: return PRESS;
			case 1: return TOGGLE;
			case 2: return HOLD;
			default: return PRESS;
			}
		}

		public int getCode() {
			switch (this) {
			case PRESS: return 0;
			case TOGGLE: return 1;
			case HOLD: return 2;
			default: return 0;
			}
		}

		public Component getDisplayName() {
			return new TranslatableComponent("input_type." + this.name);
		}
	}
}
