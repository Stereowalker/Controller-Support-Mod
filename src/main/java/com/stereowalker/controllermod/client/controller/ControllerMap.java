package com.stereowalker.controllermod.client.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	

	@OnlyIn(Dist.CLIENT)
    public static final class Button {
		public ControllerModel model;
		public String value;
		public String name;
		
		Button(String name, ControllerModel model, String value){
			this.model = model;
			this.value = value;
			this.name = name.isEmpty() ? this.model.modelName+"."+value : name;
		}
		
		public ControllerModel getModel(){
			return this.model;
		}
		
		public String getValue(){
			return this.value;
		}
		
		public String getName() {
			return name;
		}

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Button button = (Button)object;
            return this.value == button.value && this.model == button.model;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.model, this.value});
        }

        public String toString() {
            return this.name;
        }
	}
	
	public enum ControllerModel {
		CUSTOM("custom", "", new Integer[] {}, new Integer[] {}),
		XBOX_360("xbox_360", "78696e70757401000000000000000000", new Integer[] {4,5}, new Integer[] {}),
		PS4("ps4", "030000004c050000cc09000000000000", new Integer[] {3,4}, new Integer[] {});
		
		List<Integer> controllerNegativeTriggers;
		List<Integer> controllerPositiveTriggers;
		Map<String, Button> map = new HashMap<String, Button>();
		String modelName;
		String GUID;

        private static void addButton(ControllerModel model, String name, String buttonId) {
        	Button button = new Button(name, model, buttonId);
        	model.map.put(buttonId, button);
        }
        
		private ControllerModel(String modelNameIn, String GUIDIn, Integer[] controllerNegativeTriggersIn, Integer[] controllerPositiveTriggersIn) {
			modelName = modelNameIn;
			GUID = GUIDIn;
			controllerNegativeTriggers = Lists.newArrayList(controllerNegativeTriggersIn);
			controllerPositiveTriggers = Lists.newArrayList(controllerPositiveTriggersIn);
		}

        public Button getOrCreate(String buttonId) {
            return this.map.computeIfAbsent(buttonId, i -> {
                String j = i;
//                if (this == MOUSE) {
//                    ++j;
//                }
                String string = this.modelName + ".unknown." + j;
                return new Button(string, this, i);
            });
        }
        
        static {
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.0", "button0");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.1", "button1");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.2", "button2");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.3", "button3");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.4", "button4");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.5", "button5");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.6", "button6");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.7", "button7");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.8", "button8");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.9", "button9");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.10", "button10");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.11", "button11");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.12", "button12");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360.13", "button13");
        	ControllerModel.addButton(XBOX_360, "posi_axis.xbox_360.0", "axis_pos0");
        	ControllerModel.addButton(XBOX_360, "posi_axis.xbox_360.1", "axis_pos1");
        	ControllerModel.addButton(XBOX_360, "posi_axis.xbox_360.2", "axis_pos2");
        	ControllerModel.addButton(XBOX_360, "posi_axis.xbox_360.3", "axis_pos3");
        	ControllerModel.addButton(XBOX_360, "posi_axis.xbox_360.4", "axis_pos4");
        	ControllerModel.addButton(XBOX_360, "posi_axis.xbox_360.5", "axis_pos5");
        	ControllerModel.addButton(XBOX_360, "nega_axis.xbox_360.0", "axis_neg0");
        	ControllerModel.addButton(XBOX_360, "nega_axis.xbox_360.1", "axis_neg1");
        	ControllerModel.addButton(XBOX_360, "nega_axis.xbox_360.2", "axis_neg2");
        	ControllerModel.addButton(XBOX_360, "nega_axis.xbox_360.3", "axis_neg3");
        	ControllerModel.addButton(XBOX_360, "axis.xbox_360.0", "axis0");
        	ControllerModel.addButton(XBOX_360, "axis.xbox_360.1", "axis1");
        	ControllerModel.addButton(XBOX_360, "axis.xbox_360.2", "axis2");
        	ControllerModel.addButton(XBOX_360, "axis.xbox_360.3", "axis3");
        	
        	ControllerModel.addButton(PS4, "button.dualshock_4.0", "button0");
        	ControllerModel.addButton(PS4, "button.dualshock_4.1", "button1");
        	ControllerModel.addButton(PS4, "button.dualshock_4.2", "button2");
        	ControllerModel.addButton(PS4, "button.dualshock_4.3", "button3");
        	ControllerModel.addButton(PS4, "button.dualshock_4.4", "button4");
        	ControllerModel.addButton(PS4, "button.dualshock_4.5", "button5");
        	ControllerModel.addButton(PS4, "button.dualshock_4.6", "button6");
        	ControllerModel.addButton(PS4, "button.dualshock_4.7", "button7");
        	ControllerModel.addButton(PS4, "button.dualshock_4.8", "button8");
        	ControllerModel.addButton(PS4, "button.dualshock_4.9", "button9");
        	ControllerModel.addButton(PS4, "button.dualshock_4.10", "button10");
        	ControllerModel.addButton(PS4, "button.dualshock_4.11", "button11");
        	ControllerModel.addButton(PS4, "button.dualshock_4.12", "button12");
        	ControllerModel.addButton(PS4, "button.dualshock_4.13", "button13");
        	ControllerModel.addButton(PS4, "button.dualshock_4.14", "button14");
        	ControllerModel.addButton(PS4, "button.dualshock_4.15", "button15");
        	ControllerModel.addButton(PS4, "button.dualshock_4.16", "button16");
        	ControllerModel.addButton(PS4, "button.dualshock_4.17", "button17");
        	ControllerModel.addButton(PS4, "posi_axis.dualshock_4.0", "axis_pos0");
        	ControllerModel.addButton(PS4, "posi_axis.dualshock_4.1", "axis_pos1");
        	ControllerModel.addButton(PS4, "posi_axis.dualshock_4.2", "axis_pos2");
        	ControllerModel.addButton(PS4, "posi_axis.dualshock_4.5", "axis_pos5");
        	ControllerModel.addButton(PS4, "nega_axis.dualshock_4.0", "axis_neg0");
        	ControllerModel.addButton(PS4, "nega_axis.dualshock_4.1", "axis_neg1");
        	ControllerModel.addButton(PS4, "nega_axis.dualshock_4.2", "axis_neg2");
        	ControllerModel.addButton(PS4, "nega_axis.dualshock_4.5", "axis_neg5");
        	ControllerModel.addButton(PS4, "axis.dualshock_4.0", "axis0");
        	ControllerModel.addButton(PS4, "axis.dualshock_4.1", "axis1");
        	ControllerModel.addButton(PS4, "axis.dualshock_4.2", "axis2");
        	ControllerModel.addButton(PS4, "axis.dualshock_4.5", "axis5");
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
