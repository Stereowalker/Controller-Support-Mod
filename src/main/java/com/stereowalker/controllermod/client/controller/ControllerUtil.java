package com.stereowalker.controllermod.client.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallbackI;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.VirtualMouseHelper;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.GameSettings;
import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;

@OnlyIn(Dist.CLIENT)
public class ControllerUtil {
	public static VirtualMouseHelper virtualmouse = new VirtualMouseHelper(Minecraft.getInstance());

	static long handle = Minecraft.getInstance().getMainWindow().getHandle();

	static MouseHelper mouse() {
		return Minecraft.getInstance().mouseHelper;
	}

	static KeyboardListener keyboard = (Minecraft.getInstance()).keyboardListener;

	static GameSettings settings = (Minecraft.getInstance()).gameSettings;

	public static double dead_zone = ((Double)Config.deadzone.get()).doubleValue();

	public static boolean enableController = ((Boolean)Config.enableControllers.get()).booleanValue();

	public static int controller = ((Integer)Config.controllerNumber.get()).intValue() - 1;

	public static double ingameSensitivity = ((Double)Config.ingameSensitivity.get()).doubleValue();

//	public static boolean useAxisToMove = ((Boolean)Config.useAxisToMove.get()).booleanValue();

	public static double menuSensitivity = ((Double)Config.menuSensitivity.get()).doubleValue();

	public static boolean isListening = true;

	public static boolean isControllerAvailable(int controller) {
		return /*GLFW.glfwJoystickIsGamepad(controller) &&*/ GLFW.glfwJoystickPresent(controller);
	}

	public static int getKeybindCode(KeyBinding keybinding) {
		return keybinding.getKey().getKeyCode();
	}

	public static String getControllerInputId(int input) {
//		if (input == -1) return "> <"; 
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
			int state = GLFW.glfwGetKey(handle, i);
			if (state == 1)
				keyboard.onKeyEvent(handle, i, 0, 0, 0); 
		} 
		for (int j = 0; j <= 7; j++) {
			virtualmouse.mouseButtonCallback(handle, j, 0, 0);
			mouse().mouseButtonCallback(handle, j, 0, 0);
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
				List<Integer> triggersPos = Config.controllerModel.get() == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerSettings.positiveTriggerAxes : controller.getModel().getControllerPositiveTriggers();
				List<Integer> triggersNeg = Config.controllerModel.get() == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerSettings.negativeTriggerAxes : controller.getModel().getControllerNegativeTriggers();
				if (!triggersPos.contains(i) && !triggersNeg.contains(i))
					if (buttonId.equals("axis"+i)) controlleraxis = controller.getAxes().get(i);
			}
			if (controlleraxis > dead_zone && controlleraxis <= 1.0D) {
				return controlleraxis;
			} else if (controlleraxis < -dead_zone && controlleraxis >= -1.0D) {
				return controlleraxis;
			}
		}
		return 0.0F;
	}
	
	public static void updateButtonState(String buttonId, Controller controller, InputMappings.Type keyType, int buttonOnComputer, InputType inputType) {
		if (buttonId != null && buttonId != " " && buttonId != "???") {
			float controllerButton =2.0F; 
			for (int i = 0; i < controller.getButtons().capacity(); i++) {
				if (buttonId.equals("button"+i)) {
					controllerButton = controller.getButtons().get(i);

				}
			}
			for (int i = 0; i < controller.getAxes().capacity(); i++) {
				List<Integer> triggers0 = Config.controllerModel.get() == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerSettings.positiveTriggerAxes : controller.getModel().getControllerPositiveTriggers();
				if (!triggers0.contains(i))
					if (buttonId.equals("axis_pos"+i)) controllerButton = controller.getAxes().get(i);
				List<Integer> triggers1 = Config.controllerModel.get() == ControllerModel.CUSTOM ? ControllerMod.getInstance().controllerSettings.negativeTriggerAxes : controller.getModel().getControllerNegativeTriggers();
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
			if (keyType == InputMappings.Type.KEYSYM) {
				if (keyMap.isEmpty()) putKeysInMap(keyMap);
				if (keyToggleMap.isEmpty()) putKeysInMap(keyToggleMap);
				if (inputType == InputType.PRESS) {
					if ((controllerButton > dead_zone && controllerButton <= 1.0D) && ((Integer)keyMap.get(buttonId)).intValue() == 0) {
						keyMap.put(buttonId, Integer.valueOf(1));
						keyboard.onKeyEvent(handle, buttonOnComputer, 0, 1, 0);
					} 
					if (controllerButton <= dead_zone && ((Integer)keyMap.get(buttonId)).intValue() == 1) {
						keyMap.put(buttonId, Integer.valueOf(0));
						keyboard.onKeyEvent(handle, buttonOnComputer, 0, 0, 0);
					} 
				} 
				if (inputType == InputType.TOGGLE) {
					if ((controllerButton > dead_zone && controllerButton <= 1.0D) && ((Integer)keyMap.get(buttonId)).intValue() == 0 && ((Integer)keyToggleMap.get(buttonId)).intValue() == 0) {
						keyMap.put(buttonId, Integer.valueOf(1));
						keyToggleMap.put(buttonId, Integer.valueOf(1));
						keyboard.onKeyEvent(handle, buttonOnComputer, 0, 1, 0);
					} 
					if (controllerButton <= dead_zone && ((Integer)keyMap.get(buttonId)).intValue() == 1)
						keyMap.put(buttonId, Integer.valueOf(0)); 
					if ((controllerButton > dead_zone && controllerButton <= 1.0D) && ((Integer)keyMap.get(buttonId)).intValue() == 0 && ((Integer)keyToggleMap.get(buttonId)).intValue() == 1) {
						keyMap.put(buttonId, Integer.valueOf(1));
						keyToggleMap.put(buttonId, Integer.valueOf(0));
						keyboard.onKeyEvent(handle, buttonOnComputer, 0, 0, 0);
					} 
				} 
				if (inputType == InputType.HOLD) {
					if ((controllerButton > dead_zone && controllerButton <= 1.0D))
						keyboard.onKeyEvent(handle, buttonOnComputer, 0, 1, 0); 
					if (controllerButton <= dead_zone)
						keyboard.onKeyEvent(handle, buttonOnComputer, 0, 0, 0); 
				} 
			} 
			if (keyType == InputMappings.Type.MOUSE) {
				if (mouseMap.isEmpty()) putKeysInMap(mouseMap);
				if (mouseToggleMap.isEmpty()) putKeysInMap(mouseToggleMap);
				if (inputType == InputType.PRESS) {
					if ((controllerButton > dead_zone && controllerButton <= 1.0D) && ((Integer)mouseMap.get(buttonId)).intValue() == 0) {
						mouseMap.put(buttonId, Integer.valueOf(1));
						virtualmouse.mouseButtonCallback(handle, buttonOnComputer, 1, 0);
					} 
					if (controllerButton <= dead_zone && ((Integer)mouseMap.get(buttonId)).intValue() == 1) {
						mouseMap.put(buttonId, Integer.valueOf(0));
						virtualmouse.mouseButtonCallback(handle, buttonOnComputer, 0, 0);
					} 
				}
				if (inputType == InputType.TOGGLE) {
					if ((controllerButton > dead_zone && controllerButton <= 1.0D) && ((Integer)mouseMap.get(buttonId)).intValue() == 0 && ((Integer)mouseToggleMap.get(buttonId)).intValue() == 0) {
						mouseMap.put(buttonId, Integer.valueOf(1));
						mouseToggleMap.put(buttonId, Integer.valueOf(1));
						virtualmouse.mouseButtonCallback(handle, buttonOnComputer, 1, 0);
					} 
					if (controllerButton <= dead_zone && ((Integer)mouseMap.get(buttonId)).intValue() == 1)
						mouseMap.put(buttonId, Integer.valueOf(0)); 
					if ((controllerButton > dead_zone && controllerButton <= 1.0D) && ((Integer)mouseMap.get(buttonId)).intValue() == 0 && ((Integer)mouseToggleMap.get(buttonId)).intValue() == 1) {
						mouseMap.put(buttonId, Integer.valueOf(1));
						mouseToggleMap.put(buttonId, Integer.valueOf(0));
						virtualmouse.mouseButtonCallback(handle, buttonOnComputer, 0, 0);
					} 
				}
				if (inputType == InputType.HOLD) {
					if ((controllerButton > dead_zone && controllerButton <= 1.0D))
						virtualmouse.mouseButtonCallback(handle, buttonOnComputer, 2, 0); 
					if (controllerButton <= dead_zone)
						virtualmouse.mouseButtonCallback(handle, buttonOnComputer, 0, 0); 
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
		int width = Minecraft.getInstance().getMainWindow().getWidth();
		int height = Minecraft.getInstance().getMainWindow().getHeight();
		float yMod = height / 480.0F;
		float xMod = width / 854.0F;
		if (virtualmouse.getMouseX() >= 0.0D && virtualmouse.getMouseX() <= width && virtualmouse.getMouseY() >= 0.0D && virtualmouse.getMouseY() <= height && !isCamera) {
			if (xLAxis >= -1.0F && xLAxis < -dead_zone)
				virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX() + xLAxis * menuSensitivity * 25.0D * xMod, virtualmouse.getMouseY() + 0.0D); 
			if (xLAxis <= 1.0F && xLAxis > dead_zone)
				virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX() + xLAxis * menuSensitivity * 25.0D * xMod, virtualmouse.getMouseY() + 0.0D); 
			if (yLAxis >= -1.0F && yLAxis < -dead_zone)
				virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX() + 0.0D, virtualmouse.getMouseY() + yLAxis * menuSensitivity * 25.0D * yMod); 
			if (yLAxis <= 1.0F && yLAxis > dead_zone)
				virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX() + 0.0D, virtualmouse.getMouseY() + yLAxis * menuSensitivity * 25.0D * yMod); 
			if (RIGHT == 1)
				virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX() + 0.25D * menuSensitivity * 25.0D * xMod, virtualmouse.getMouseY() + 0.0D); 
			if (LEFT == 1)
				virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX() - 0.25D * menuSensitivity * 25.0D * xMod, virtualmouse.getMouseY() + 0.0D); 
			if (UP == 1)
				virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX() + 0.0D, virtualmouse.getMouseY() - 0.25D * menuSensitivity * 25.0D * yMod); 
			if (DOWN == 1)
				virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX() + 0.0D, virtualmouse.getMouseY() + 0.25D * menuSensitivity * 25.0D * yMod); 
		} else if (mouse().isMouseGrabbed() || isCamera) {
			if (xLAxis >= -1.0F && xLAxis < -dead_zone)
				mouse().cursorPosCallback(handle, mouse().getMouseX() + xLAxis * ingameSensitivity * 100.0D, mouse().getMouseY() + 0.0D); 
			if (xLAxis <= 1.0F && xLAxis > dead_zone)
				mouse().cursorPosCallback(handle, mouse().getMouseX() + xLAxis * ingameSensitivity * 100.0D, mouse().getMouseY() + 0.0D); 
			if (yLAxis >= -1.0F && yLAxis < -dead_zone)
				mouse().cursorPosCallback(handle, mouse().getMouseX() + 0.0D, mouse().getMouseY() + yLAxis * ingameSensitivity * 100.0D); 
			if (yLAxis <= 1.0F && yLAxis > dead_zone)
				mouse().cursorPosCallback(handle, mouse().getMouseX() + 0.0D, mouse().getMouseY() + yLAxis * ingameSensitivity * 100.0D); 
		}
		if (virtualmouse.getMouseX() < 0.0D) {
			virtualmouse.cursorPosCallback(handle, 0.0D, virtualmouse.getMouseY());
		} else if (virtualmouse.getMouseY() < 0.0D) {
			virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX(), 0.0D);
		} else if (virtualmouse.getMouseX() > width) {
			virtualmouse.cursorPosCallback(handle, width, virtualmouse.getMouseY());
		} else if (virtualmouse.getMouseY() > height) {
			virtualmouse.cursorPosCallback(handle, virtualmouse.getMouseX(), height);
		} else if (virtualmouse.getMouseX() == 0.0D && virtualmouse.getMouseY() == 0.0D) {
			virtualmouse.cursorPosCallback(handle, (width / 2), (height / 2));
		} 
		if (virtualmouse.getMouseX() != prevX || virtualmouse.getMouseY() != prevY)
			mouse().cursorPosCallback(handle, virtualmouse.getMouseX(), virtualmouse.getMouseY()); 
		prevX = virtualmouse.getMouseX();
		prevY = virtualmouse.getMouseY();
	}

	public static void handleIngameInput(Controller controller, float xAxis, float yAxis) {
		if (isListening) {
			keyboard.enableRepeatEvents(false);
			for (ControllerBinding binding : ControllerMod.getInstance().controllerSettings.controllerBindings) {
				boolean flag = true;
				if(ControllerMod.getInstance().controllerSettings.useAxisToMove && (binding.getDescripti() == settings.keyBindForward.getKeyDescription() || binding.getDescripti() == settings.keyBindRight.getKeyDescription() || binding.getDescripti() == settings.keyBindLeft.getKeyDescription() || binding.getDescripti() == settings.keyBindBack.getKeyDescription())) flag = false;
				if (flag && binding != null) {
					if (binding.isBoundToButton(controller.getModel()) && (binding.getConflict() == KeyConflictContext.IN_GAME || binding.getConflict() == ControllerConflictContext.IN_GAME || binding.getConflict() == KeyConflictContext.UNIVERSAL))
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
					if (binding.isBoundToButton(controller.getModel()) && (binding.getConflict() == KeyConflictContext.GUI || binding.getConflict() == ControllerConflictContext.GUI || binding.getConflict() == KeyConflictContext.UNIVERSAL))
						controller.updateButtonState(binding);
				}
			}
			updateMousePosition(xAxis, yAxis, controller, false, true);
			if (scoll >= -1.0F && scoll < -0.1F)
				mouse().scrollCallback(handle, 0.0D, -scoll * menuSensitivity * 100.0D / 20.0D); 
			if (scoll <= 1.0F && scoll > 0.1F)
				mouse().scrollCallback(handle, 0.0D, -scoll * menuSensitivity * 100.0D / 20.0D); 
		}
	}

	public static void handleContainerInput(Controller controller, float xAxis, float yAxis, float scoll) {
		if (isListening) {
			for (ControllerBinding binding : ControllerMod.getInstance().controllerSettings.controllerBindings) {
				boolean flag = true;
				if (/* binding.getDescripti() == settings.keyBindInventory.getKeyDescription() || */binding.getDescripti() == settings.keyBindUseItem.getKeyDescription()) flag = false;
				if (flag && binding != null) {
					if (binding.isBoundToButton(controller.getModel()) && (binding.getConflict() == KeyConflictContext.GUI || binding.getConflict() == ControllerConflictContext.CONTAINER || binding.getConflict() == KeyConflictContext.UNIVERSAL))
						controller.updateButtonState(binding);
				}
			}
		}

		updateMousePosition(xAxis, yAxis, controller, false, false);
		if (scoll >= -1.0F && scoll < -0.1D)
			mouse().scrollCallback(handle, 0.0D, -scoll * menuSensitivity * 100.0D / 20.0D); 
		if (scoll <= 1.0F && scoll > 0.1D)
			mouse().scrollCallback(handle, 0.0D, -scoll * menuSensitivity * 100.0D / 20.0D); 
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

		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("input_type." + this.name);
		}
	}
}
