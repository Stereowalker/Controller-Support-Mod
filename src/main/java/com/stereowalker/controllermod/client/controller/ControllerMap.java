package com.stereowalker.controllermod.client.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ControllerMap {
	public static String map(String input, ControllerModel model) {
		switch (model) {
		case XBOX_360:
			if (input.equals("empty")) return " ";
			else if (input.equals("button0")) return "A";
			else if (input.equals("button1")) return "B";
			else if (input.equals("button2")) return "X";
			else if (input.equals("button3")) return "Y";
			else if (input.equals("button4")) return "LB";
			else if (input.equals("button5")) return "RB";
			else if (input.equals("button6")) return "BACK";
			else if (input.equals("button7")) return "START";
			else if (input.equals("button8")) return "LS";
			else if (input.equals("button9")) return "RS";
			else if (input.equals("button10")) return "UP";
			else if (input.equals("button11")) return "RIGHT";
			else if (input.equals("button12")) return "DOWN";
			else if (input.equals("button13")) return "LEFT";
			else if (input.equals("axis_pos0")) return "LS RIGHT";
			else if (input.equals("axis_neg0")) return "LS LEFT";
			else if (input.equals("axis_pos1")) return "LS DOWN";
			else if (input.equals("axis_neg1")) return "LS UP";
			else if (input.equals("axis_pos2")) return "RS RIGHT";
			else if (input.equals("axis_neg2")) return "RS LEFT";
			else if (input.equals("axis_pos3")) return "RS DOWN";
			else if (input.equals("axis_neg3")) return "RS UP";
			else if (input.equals("axis_pos4")) return "LT";
			else if (input.equals("axis_pos5")) return "RT";
			else if (input.equals("axis0")) return "LS RIGHT/LEFT";
			else if (input.equals("axis1")) return "LS UP/DOWN";
			else if (input.equals("axis2")) return "RS RIGHT/LEFT";
			else if (input.equals("axis3")) return "RS UP/DOWN";
			else return input;
		case PS4:
			if (input.equals("empty")) return " ";
			else if (input.equals("button0")) return "SQUARE";
			else if (input.equals("button1")) return "CROSS";
			else if (input.equals("button2")) return "CIRCLE";
			else if (input.equals("button3")) return "TRIANGLE";
			else if (input.equals("button4")) return "L1";
			else if (input.equals("button5")) return "R1";
			else if (input.equals("button6")) return "L2";
			else if (input.equals("button7")) return "R2";
			else if (input.equals("button8")) return "SHARE";
			else if (input.equals("button9")) return "OPTIONS";
			else if (input.equals("button10")) return "L3";
			else if (input.equals("button11")) return "R3";
			else if (input.equals("button12")) return "PS BUTTON";
			else if (input.equals("button13")) return "TOUCHPAD";
			else if (input.equals("button14")) return "UP";
			else if (input.equals("button15")) return "RIGHT";
			else if (input.equals("button16")) return "DOWN";
			else if (input.equals("button17")) return "LEFT";
			else if (input.equals("axis_pos0")) return "LS RIGHT";
			else if (input.equals("axis_neg0")) return "LS LEFT";
			else if (input.equals("axis_pos1")) return "LS DOWN";
			else if (input.equals("axis_neg1")) return "LS UP";
			else if (input.equals("axis_pos2")) return "RS RIGHT";
			else if (input.equals("axis_neg2")) return "RS LEFT";
			else if (input.equals("axis_pos5")) return "RS DOWN";
			else if (input.equals("axis_neg5")) return "RS UP";
			else if (input.equals("axis0")) return "LS RIGHT/LEFT";
			else if (input.equals("axis1")) return "LS UP/DOWN";
			else if (input.equals("axis2")) return "RS RIGHT/LEFT";
			else if (input.equals("axis5")) return "RS UP/DOWN";
			else return input;
		default:
			return input;
		}
	}
	
	public enum ControllerModel {
		CUSTOM("custom", "", (triggers) -> {
		}, (triggers) -> {
		}),
		XBOX_360("xbox_360", "78696e70757401000000000000000000", (triggers) -> {
			triggers.add(4);
			triggers.add(5);
		}, (triggers) -> {
		}),
		PS4("ps4", "030000004c050000cc09000000000000", (triggers) -> {
			triggers.add(3);
			triggers.add(4);
		}, (triggers) -> {
		});
		
		List<Integer> controllerNegativeTriggers;
		List<Integer> controllerPositiveTriggers;
		String modelName;
		String GUID;
		private ControllerModel(String modelNameIn, String GUIDIn, Consumer<List<Integer>> controllerNegativeTriggersIn, Consumer<List<Integer>> controllerPositiveTriggersIn) {
			modelName = modelNameIn;
			GUID = GUIDIn;
			List<Integer> negativeTriggers = new ArrayList<Integer>();
			controllerNegativeTriggersIn.accept(negativeTriggers);
			controllerNegativeTriggers = negativeTriggers;
			
			List<Integer> positiveTriggers = new ArrayList<Integer>();
			controllerPositiveTriggersIn.accept(positiveTriggers);
			controllerPositiveTriggers = positiveTriggers;
		}
		
		public List<Integer> getControllerNegativeTriggers() {
			return controllerNegativeTriggers;
		}
		
		public List<Integer> getControllerPositiveTriggers() {
			return controllerPositiveTriggers;
		}
		
		public static List<ControllerModel> modelList(){
			return Arrays.asList(values());
		}
		
		public String getGUID() {
			return GUID;
		}
		
		public String getModelName() {
			return modelName;
		}
	}
}
