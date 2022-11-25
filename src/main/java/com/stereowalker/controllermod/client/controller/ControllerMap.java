package com.stereowalker.controllermod.client.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

public class ControllerMap {
	public static String map(String input, ControllerModel model) {
		String lAr = "\u2190";
		String uAr = "\u2191";
		String rAr = "\u2192";
		String dAr = "\u2193";
		if (input.equals("empty") || input.equals(" ")) return " ";
		switch (model) {
		case XBOX_360:
			if (input.equals("button7")) return "START";
			else if (input.equals("button8")) return "LS";
			else if (input.equals("button9")) return "RS";
			else if (input.equals("button10")) return "UP";
			else if (input.equals("button11")) return "RIGHT";
			else if (input.equals("button12")) return "DOWN";
			else if (input.equals("button13")) return "LEFT";
			else if (input.equals("axis_pos0")) return "LS "+rAr;
			else if (input.equals("axis_neg0")) return "LS "+lAr;
			else if (input.equals("axis_pos1")) return "LS "+dAr;
			else if (input.equals("axis_neg1")) return "LS "+uAr;
			else if (input.equals("axis_pos2")) return "RS "+rAr;
			else if (input.equals("axis_neg2")) return "RS "+lAr;
			else if (input.equals("axis_pos3")) return "RS "+dAr;
			else if (input.equals("axis_neg3")) return "RS "+uAr;
			else if (input.equals("axis_pos4")) return "LT";
			else if (input.equals("axis_pos5")) return "RT";
			else if (input.equals("axis0")) return "LS "+lAr+rAr;
			else if (input.equals("axis1")) return "LS "+uAr+dAr;
			else if (input.equals("axis2")) return "RS "+lAr+rAr;
			else if (input.equals("axis3")) return "RS "+uAr+dAr;
		case PS4_WINDOWS:
			if (input.equals("button12")) return "PS BUTTON";
			else if (input.equals("axis_pos0")) return "LS "+rAr;
			else if (input.equals("axis_neg0")) return "LS "+lAr;
			else if (input.equals("axis_pos1")) return "LS "+dAr;
			else if (input.equals("axis_neg1")) return "LS "+uAr;
			else if (input.equals("axis_pos2")) return "RS "+rAr;
			else if (input.equals("axis_neg2")) return "RS "+lAr;
			else if (input.equals("axis_pos5")) return "RS "+dAr;
			else if (input.equals("axis_neg5")) return "RS "+uAr;
			else if (input.equals("axis0")) return "LS "+lAr+rAr;
			else if (input.equals("axis1")) return "LS "+uAr+dAr;
			else if (input.equals("axis2")) return "RS "+lAr+rAr;
			else if (input.equals("axis5")) return "RS "+uAr+dAr;
		case PS4_LINUX:
			if (input.equals("button10")) return "PS BUTTON";
			else if (input.equals("axis_pos0")) return "LS "+rAr;
			else if (input.equals("axis_neg0")) return "LS "+lAr;
			else if (input.equals("axis_pos1")) return "LS "+dAr;
			else if (input.equals("axis_neg1")) return "LS "+uAr;
			else if (input.equals("axis_pos3")) return "RS "+rAr;
			else if (input.equals("axis_neg3")) return "RS "+lAr;
			else if (input.equals("axis_pos4")) return "RS "+dAr;
			else if (input.equals("axis_neg4")) return "RS "+uAr;
			else if (input.equals("axis0")) return "LS "+lAr+rAr;
			else if (input.equals("axis1")) return "LS "+uAr+dAr;
			else if (input.equals("axis3")) return "RS "+lAr+rAr;
			else if (input.equals("axis4")) return "RS "+uAr+dAr;
		default:
			break;
		}
		if (input.equals("dpadup")) return "UP";
		else if (input.equals("dpadri")) return "RIGHT";
		else if (input.equals("dpaddo")) return "DOWN";
		else if (input.equals("dpadle")) return "LEFT";
		return I18n.get(model.getOrCreate(Lists.newArrayList(input))[0].getName());
	}
	

	@Environment(value=EnvType.CLIENT)
    public static final class Button {
		public ControllerModel model;
		public String value;
		public String name;
		public ResourceLocation icon;
		
		Button(String name, ControllerModel model, ResourceLocation icon, String value){
			this.model = model;
			this.value = value;
			this.icon = icon;
			this.name = name.isEmpty() ? this.model.modelName+"."+value : name;
		}
		
		public ControllerModel getModel(){
			return this.model;
		}
		
		public ResourceLocation getIcon() {
			return icon;
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
		CUSTOM("custom", "", new Integer[] {}, new Integer[] {}, Lists.newArrayList()),
		XBOX_360("xbox_360", "78696e70757401000000000000000000", new Integer[] {4,5}, new Integer[] {}, Lists.newArrayList()),
		PS4_WINDOWS("ps4_windows", "030000004c050000cc09000000000000", new Integer[] {3,4}, new Integer[] {}, Lists.newArrayList()),
		PS4_LINUX("ps4_linux", "050000004c050000cc09000000810000", new Integer[] {2,5}, new Integer[] {}, Lists.newArrayList("button13", "button14", "button15", "button16"));
		
		String modelName;
		String GUID;
		List<Integer> controllerNegativeTriggers;
		List<Integer> controllerPositiveTriggers;
		List<String> dupeButtons;
		Map<String, Button> map = new HashMap<String, Button>();
		Map<String, String> aliases = new HashMap<String, String>();

		@Deprecated
        private static void addOButton(ControllerModel model, String name, String icon, String buttonId) {
        	Button button = new Button(name, model, new ResourceLocation("controllermod:textures/gui/"+icon+""), buttonId);
        	model.map.put(buttonId, button);
        }

        private static void addButton(ControllerModel model, String name, String alias, String icon, String buttonId) {
        	Button button = new Button(name+"."+alias, model, new ResourceLocation("controllermod:textures/gui/"+icon+""), buttonId);
        	model.map.put(buttonId, button);
        	model.aliases.put(alias, buttonId);
        }
        
        @Deprecated
        private static void addButton(ControllerModel model, String name, String buttonId) {
        	Button button = new Button(name, model, null, buttonId);
        	model.map.put(buttonId, button);
        }
        
        private static void addButton(ControllerModel model, String name, String alias, String buttonId) {
        	Button button = new Button(name, model, null, buttonId);
        	model.map.put(buttonId, button);
        	model.aliases.put(alias, buttonId);
        }
        
		private ControllerModel(String modelNameIn, String GUIDIn, Integer[] controllerNegativeTriggersIn, Integer[] controllerPositiveTriggersIn, List<String> dupeButtonsIn) {
			modelName = modelNameIn;
			GUID = GUIDIn;
			controllerNegativeTriggers = Lists.newArrayList(controllerNegativeTriggersIn);
			controllerPositiveTriggers = Lists.newArrayList(controllerPositiveTriggersIn);
			dupeButtons = dupeButtonsIn;
		}
		
		public String getIdFromAlias(String alias) {
			return this.aliases.getOrDefault(alias, "NULL");
		}

        public Button[] getOrCreate(List<String> buttonId) {
        	Button[] buts = new Button[buttonId.size()];
        	for (int x = 0; x < buttonId.size(); x++) {
        		buts[x] = this.map.computeIfAbsent(buttonId.get(x), i -> {
                    String j = i;
//                  if (this == MOUSE) {
//                      ++j;
//                  }
                  String string = this.modelName + ".unknown." + j;
                  return new Button(string, this, null, i);
              });
        	}
            return buts; 
        }
        
        static {
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "face_button_down", "controllers/xbox_face_button_down.png", "button0");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "face_button_right", "controllers/xbox_face_button_right.png", "button1");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "face_button_left", "controllers/xbox_face_button_left.png", "button2");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "face_button_up", "controllers/xbox_face_button_up.png", "button3");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "bumper_left", "controllers/xbox_bumper_left.png", "button4");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "bumper_right", "controllers/xbox_bumper_right.png", "button5");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "select_button", "controllers/xbox_select_button.png", "button6");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "start_button", "controllers/xbox_start_button.png", "button7");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "stick_left", "controllers/xbox_stick_left.png", "button8");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "stick_right", "controllers/xbox_stick_right.png", "button9");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "dpad_up", "controllers/xbox_dpad_up.png", "button10");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "dpad_right", "controllers/xbox_dpad_right.png", "button11");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "dpad_down", "controllers/xbox_dpad_down.png", "button12");
        	ControllerModel.addButton(XBOX_360, "button.xbox_360", "dpad_left", "controllers/xbox_dpad_left.png", "button13");
        	ControllerModel.addButton(XBOX_360, "posit_axis.xbox_360.0", "left_stick_right", "axis_pos0");
        	ControllerModel.addButton(XBOX_360, "posit_axis.xbox_360.1", "left_stick_down", "axis_pos1");
        	ControllerModel.addButton(XBOX_360, "posit_axis.xbox_360.2", "right_stick_right", "axis_pos2");
        	ControllerModel.addButton(XBOX_360, "posit_axis.xbox_360.3", "right_stick_down", "axis_pos3");
        	ControllerModel.addButton(XBOX_360, "posit_axis.xbox_360", "left_trigger", "controllers/xbox_left_trigger.png", "axis_pos4");
        	ControllerModel.addButton(XBOX_360, "posit_axis.xbox_360", "right_trigger", "controllers/xbox_right_trigger.png", "axis_pos5");
        	ControllerModel.addButton(XBOX_360, "negat_axis.xbox_360.0", "left_stick_left", "axis_neg0");
        	ControllerModel.addButton(XBOX_360, "negat_axis.xbox_360.1", "left_stick_up", "axis_neg1");
        	ControllerModel.addButton(XBOX_360, "negat_axis.xbox_360.2", "right_stick_left", "axis_neg2");
        	ControllerModel.addButton(XBOX_360, "negat_axis.xbox_360.3", "right_stick_up", "axis_neg3");
        	ControllerModel.addButton(XBOX_360, "axis.xbox_360.0", "left_stick_horizontal", "axis0");
        	ControllerModel.addButton(XBOX_360, "axis.xbox_360.1", "left_stick_vertical", "axis1");
        	ControllerModel.addButton(XBOX_360, "axis.xbox_360.2", "right_stick_horizontal", "axis2");
        	ControllerModel.addButton(XBOX_360, "axis.xbox_360.3", "right_stick_vertical", "axis3");

        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "face_button_left", "controllers/ps4_face_button_left.png", "button0");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "face_button_down", "controllers/ps4_face_button_down.png", "button1");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "face_button_right", "controllers/ps4_face_button_right.png", "button2");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "face_button_up", "controllers/ps4_face_button_up.png", "button3");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "bumper_left", "controllers/ps4_bumper_left.png", "button4");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "bumper_right", "controllers/ps4_bumper_right.png", "button5");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "left_trigger", "controllers/ps4_left_trigger.png", "button6");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "right_trigger", "controllers/ps4_right_trigger.png", "button7");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "select_button", "controllers/ps4_select_button.png", "button8");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "start_button", "controllers/ps4_start_button.png", "button9");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "stick_left", "controllers/ps4_stick_left.png", "button10");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "stick_right", "controllers/ps4_stick_right.png", "button11");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4.12", "button12");
        	ControllerModel.addOButton(PS4_WINDOWS, "button.ps4.touchpad", "controllers/ps4_touchpad.png", "button13");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "dpad_up", "controllers/ps4_dpad_up.png", "button14");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "dpad_right", "controllers/ps4_dpad_right.png", "button15");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "dpad_down", "controllers/ps4_dpad_down.png", "button16");
        	ControllerModel.addButton(PS4_WINDOWS, "button.ps4", "dpad_left", "controllers/ps4_dpad_left.png", "button17");
        	ControllerModel.addButton(PS4_WINDOWS, "posit_axis.ps4.0", "left_stick_right", "axis_pos0");
        	ControllerModel.addButton(PS4_WINDOWS, "posit_axis.ps4.1", "left_stick_down", "axis_pos1");
        	ControllerModel.addButton(PS4_WINDOWS, "posit_axis.ps4.2", "right_stick_right", "axis_pos2");
        	ControllerModel.addButton(PS4_WINDOWS, "posit_axis.ps4.5", "right_stick_down", "axis_pos5");
        	ControllerModel.addButton(PS4_WINDOWS, "negat_axis.ps4.0", "left_stick_left", "axis_neg0");
        	ControllerModel.addButton(PS4_WINDOWS, "negat_axis.ps4.1", "left_stick_up", "axis_neg1");
        	ControllerModel.addButton(PS4_WINDOWS, "negat_axis.ps4.2", "right_stick_left", "axis_neg2");
        	ControllerModel.addButton(PS4_WINDOWS, "negat_axis.ps4.5", "right_stick_up", "axis_neg5");
        	ControllerModel.addButton(PS4_WINDOWS, "axis.ps4.0", "left_stick_horizontal", "axis0");
        	ControllerModel.addButton(PS4_WINDOWS, "axis.ps4.1", "left_stick_vertical", "axis1");
        	ControllerModel.addButton(PS4_WINDOWS, "axis.ps4.2", "right_stick_horizontal", "axis2");
        	ControllerModel.addButton(PS4_WINDOWS, "axis.ps4.5", "right_stick_vertical", "axis5");

        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "face_button_down", "controllers/ps4_face_button_down.png", "button0");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "face_button_right", "controllers/ps4_face_button_right.png", "button1");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "face_button_up", "controllers/ps4_face_button_up.png", "button2");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "face_button_left", "controllers/ps4_face_button_left.png", "button3");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "bumper_left", "controllers/ps4_bumper_left.png", "button4");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "bumper_right", "controllers/ps4_bumper_right.png", "button5");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "left_trigger", "controllers/ps4_left_trigger.png", "button6");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "right_trigger", "controllers/ps4_right_trigger.png", "button7");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "select_button", "controllers/ps4_select_button.png", "button8");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "start_button", "controllers/ps4_start_button.png", "button9");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4.12", "button10");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "stick_left", "controllers/ps4_stick_left.png", "button11");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "stick_right", "controllers/ps4_stick_right.png", "button12");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "dpad_up", "controllers/ps4_dpad_up.png", "dpadup");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "dpad_right", "controllers/ps4_dpad_right.png", "dpadri");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "dpad_down", "controllers/ps4_dpad_down.png", "dpaddo");
        	ControllerModel.addButton(PS4_LINUX, "button.ps4", "dpad_left", "controllers/ps4_dpad_left.png", "dpadle");
        	ControllerModel.addButton(PS4_LINUX, "posit_axis.ps4.0", "left_stick_right", "axis_pos0");
        	ControllerModel.addButton(PS4_LINUX, "posit_axis.ps4.1", "left_stick_down", "axis_pos1");
        	ControllerModel.addButton(PS4_LINUX, "posit_axis.ps4.3", "right_stick_right", "axis_pos3");
        	ControllerModel.addButton(PS4_LINUX, "posit_axis.ps4.4", "right_stick_down", "axis_pos4");
        	ControllerModel.addButton(PS4_LINUX, "negat_axis.ps4.0", "left_stick_left", "axis_neg0");
        	ControllerModel.addButton(PS4_LINUX, "negat_axis.ps4.1", "left_stick_up", "axis_neg1");
        	ControllerModel.addButton(PS4_LINUX, "negat_axis.ps4.3", "right_stick_left", "axis_neg3");
        	ControllerModel.addButton(PS4_LINUX, "negat_axis.ps4.4", "right_stick_up", "axis_neg4");
        	ControllerModel.addButton(PS4_LINUX, "axis.ps4.0", "left_stick_horizontal", "axis0");
        	ControllerModel.addButton(PS4_LINUX, "axis.ps4.1", "left_stick_vertical", "axis1");
        	ControllerModel.addButton(PS4_LINUX, "axis.ps4.3", "right_stick_horizontal", "axis3");
        	ControllerModel.addButton(PS4_LINUX, "axis.ps4.4", "right_stick_vertical", "axis4");
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
		
		public List<String> getDupeButtons() {
			return dupeButtons;
		}
	}
}
