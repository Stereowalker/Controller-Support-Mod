package com.stereowalker.controllermod.client.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap.Button;
import com.stereowalker.controllermod.resources.ControllerModelManager;

import net.minecraft.Util;
import net.minecraft.Util.OS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ControllerModel {
	public static final ControllerModel CUSTOM = new ControllerModel(new ResourceLocation("minecraft:custom_unknown"), "custom", "", OS.UNKNOWN, new Integer[] {}, new Integer[] {}, Lists.newArrayList());
	public static final ControllerModel XBOX_360_WINDOWS = new ControllerModel(new ResourceLocation("minecraft:xbox_360_windows"), "xbox_360", "78696e70757401000000000000000000", OS.WINDOWS, new Integer[] {4,5}, new Integer[] {}, Lists.newArrayList());
	public static final ControllerModel XBOX_360_LINUX = new ControllerModel(new ResourceLocation("minecraft:xbox_360_linux"), "xbox_360", "03000000de280000ff11000001000000", OS.LINUX, new Integer[] {2,5}, new Integer[] {}, Lists.newArrayList());
	public static final ControllerModel PS4_WINDOWS = new ControllerModel(new ResourceLocation("minecraft:ps4_windows"), "ps4", "030000004c050000cc09000000000000", OS.WINDOWS, new Integer[] {3,4}, new Integer[] {}, Lists.newArrayList());
//	public static final ControllerModel PS4_LINUX = new ControllerModel(new ResourceLocation("minecraft:ps4_linux"), "ps4", "050000004c05MISC0000cc09000000810000", OS.LINUX, new Integer[] {2,5}, new Integer[] {}, Lists.newArrayList("button13", "button14", "button15", "button16"));
	public static final List<ControllerModel> DEFAULTS = Lists.newArrayList(CUSTOM, XBOX_360_WINDOWS, XBOX_360_LINUX, PS4_WINDOWS);
	String modelName;
	String GUID;
	OS os;
	List<Integer> controllerNegativeTriggers;
	List<Integer> controllerPositiveTriggers;
	List<String> dupeButtons;
	Map<String, Button> map = new HashMap<String, Button>();
	Map<String, String> aliases = new HashMap<String, String>();
	//
	ResourceLocation key = null;
	public ResourceLocation getKey() {
		return key;
	}
	public void setKey(ResourceLocation key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return this.modelName+"_"+this.getOs().telemetryName();
	}
	
	public static ControllerModel nextModel(ControllerModel currentModel) {
		List<ControllerModel> all = Lists.newArrayList(ControllerModelManager.ALL_MODELS.values());
		System.out.println(all.toString());
		System.out.println(ControllerModelManager.ALL_MODELS.toString());
		for (int i = all.indexOf(currentModel)+1; i < all.size(); i++) {
			System.out.println(i);
			ControllerModel nextModel = all.get(i);
			if (Util.getPlatform() == nextModel.getOs() || nextModel == CUSTOM || ControllerMod.CONFIG.debug) {
				return nextModel;
			}
		}
		for (int i = 0; i < all.indexOf(currentModel); i++) {
			ControllerModel nextModel = all.get(i);
			if (Util.getPlatform() == nextModel.getOs() || nextModel == CUSTOM || ControllerMod.CONFIG.debug) {
				return nextModel;
			}
		}
		return currentModel;
	}

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

    public static void addButton(ControllerModel model, JsonElement name, JsonElement alias, JsonElement icon, String buttonId) {
    	addButton(model, name.getAsString(), alias.getAsString(), icon.getAsString(), buttonId);
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
    
	public ControllerModel(String modelNameIn, String GUIDIn, OS osIn, Integer[] controllerNegativeTriggersIn, Integer[] controllerPositiveTriggersIn, List<String> dupeButtonsIn) {
		modelName = modelNameIn;
		os = osIn;
		GUID = GUIDIn;
		controllerNegativeTriggers = Lists.newArrayList(controllerNegativeTriggersIn);
		controllerPositiveTriggers = Lists.newArrayList(controllerPositiveTriggersIn);
		dupeButtons = dupeButtonsIn;
	}
    
	public ResourceLocation defaultName = null;
	public ControllerModel(ResourceLocation defaultName, String modelNameIn, String GUIDIn, OS osIn, Integer[] controllerNegativeTriggersIn, Integer[] controllerPositiveTriggersIn, List<String> dupeButtonsIn) {
		this.defaultName = defaultName;
		modelName = modelNameIn;
		os = osIn;
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
//              if (this == MOUSE) {
//                  ++j;
//              }
              String string = this.modelName + ".unknown." + j;
              return new Button(string, this, null, i);
          });
    	}
        return buts; 
    }
    
    static {
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "face_button_down", "controllers/xbox_face_button_down.png", "button0");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "face_button_right", "controllers/xbox_face_button_right.png", "button1");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "face_button_left", "controllers/xbox_face_button_left.png", "button2");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "face_button_up", "controllers/xbox_face_button_up.png", "button3");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "bumper_left", "controllers/xbox_bumper_left.png", "button4");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "bumper_right", "controllers/xbox_bumper_right.png", "button5");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "select_button", "controllers/xbox_select_button.png", "button6");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "start_button", "controllers/xbox_start_button.png", "button7");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "stick_left", "controllers/xbox_stick_left.png", "button8");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "stick_right", "controllers/xbox_stick_right.png", "button9");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "dpad_up", "controllers/xbox_dpad_up.png", "button10");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "dpad_right", "controllers/xbox_dpad_right.png", "button11");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "dpad_down", "controllers/xbox_dpad_down.png", "button12");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "button.xbox_360", "dpad_left", "controllers/xbox_dpad_left.png", "button13");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "posit_axis.xbox_360.0", "lefPS4_LINUXt_stick_right", "axis_pos0");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "posit_axis.xbox_360.1", "left_stick_down", "axis_pos1");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "posit_axis.xbox_360.2", "right_stick_right", "axis_pos2");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "posit_axis.xbox_360.3", "right_stick_down", "axis_pos3");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "posit_axis.xbox_360", "left_trigger", "controllers/xbox_left_trigger.png", "axis_pos4");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "posit_axis.xbox_360", "right_trigger", "controllers/xbox_right_trigger.png", "axis_pos5");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "negat_axis.xbox_360.0", "left_stick_left", "axis_neg0");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "negat_axis.xbox_360.1", "left_stick_up", "axis_neg1");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "negat_axis.xbox_360.2", "right_stick_left", "axis_neg2");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "negat_axis.xbox_360.3", "right_stick_up", "axis_neg3");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "axis.xbox_360.0", "left_stick_horizontal", "axis0");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "axis.xbox_360.1", "left_stick_vertical", "axis1");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "axis.xbox_360.2", "right_stick_horizontal", "axis2");
    	ControllerModel.addButton(XBOX_360_WINDOWS, "axis.xbox_360.3", "right_stick_vertical", "axis3");
    	

    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "face_button_down", "controllers/xbox_face_button_down.png", "button0");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "face_button_right", "controllers/xbox_face_button_right.png", "button1");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "face_button_left", "controllers/xbox_face_button_left.png", "button2");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "face_button_up", "controllers/xbox_face_button_up.png", "button3");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "bumper_left", "controllers/xbox_bumper_left.png", "button4");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "bumper_right", "controllers/xbox_bumper_right.png", "button5");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "select_button", "controllers/xbox_select_button.png", "button6");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "start_button", "controllers/xbox_start_button.png", "button7");
    	//Missing button?
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "stick_left", "controllers/xbox_stick_left.png", "button9");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "stick_right", "controllers/xbox_stick_right.png", "button10");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "dpad_up", "controllers/xbox_dpad_up.png", "button11");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "dpad_right", "controllers/xbox_dpad_right.png", "button12");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "dpad_down", "controllers/xbox_dpad_down.png", "button13");
    	ControllerModel.addButton(XBOX_360_LINUX, "button.xbox_360", "dpad_left", "controllers/xbox_dpad_left.png", "button14");
    	ControllerModel.addButton(XBOX_360_LINUX, "posit_axis.xbox_360.0", "left_stick_right", "axis_pos0");
    	ControllerModel.addButton(XBOX_360_LINUX, "posit_axis.xbox_360.1", "left_stick_down", "axis_pos1");
    	ControllerModel.addButton(XBOX_360_LINUX, "posit_axis.xbox_360.3", "right_stick_right", "axis_pos3");
    	ControllerModel.addButton(XBOX_360_LINUX, "posit_axis.xbox_360.4", "right_stick_down", "axis_pos4");
    	ControllerModel.addButton(XBOX_360_LINUX, "posit_axis.xbox_360", "left_trigger", "controllers/xbox_left_trigger.png", "axis_pos4");
    	ControllerModel.addButton(XBOX_360_LINUX, "posit_axis.xbox_360", "right_trigger", "controllers/xbox_right_trigger.png", "axis_pos5");
    	ControllerModel.addButton(XBOX_360_LINUX, "negat_axis.xbox_360.0", "left_stick_left", "axis_neg0");
    	ControllerModel.addButton(XBOX_360_LINUX, "negat_axis.xbox_360.1", "left_stick_up", "axis_neg1");
    	ControllerModel.addButton(XBOX_360_LINUX, "negat_axis.xbox_360.3", "right_stick_left", "axis_neg3");
    	ControllerModel.addButton(XBOX_360_LINUX, "negat_axis.xbox_360.4", "right_stick_up", "axis_neg4");
    	ControllerModel.addButton(XBOX_360_LINUX, "axis.xbox_360.0", "left_stick_horizontal", "axis0");
    	ControllerModel.addButton(XBOX_360_LINUX, "axis.xbox_360.1", "left_stick_vertical", "axis1");
    	ControllerModel.addButton(XBOX_360_LINUX, "axis.xbox_360.3", "right_stick_horizontal", "axis3");
    	ControllerModel.addButton(XBOX_360_LINUX, "axis.xbox_360.4", "right_stick_vertical", "axis4");

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

//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "face_button_down", "controllers/ps4_face_button_down.png", "button0");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "face_button_right", "controllers/ps4_face_button_right.png", "button1");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "face_button_up", "controllers/ps4_face_button_up.png", "button2");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "face_button_left", "controllers/ps4_face_button_left.png", "button3");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "bumper_left", "controllers/ps4_bumper_left.png", "button4");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "bumper_right", "controllers/ps4_bumper_right.png", "button5");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "left_trigger", "controllers/ps4_left_trigger.png", "button6");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "right_trigger", "controllers/ps4_right_trigger.png", "button7");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "select_button", "controllers/ps4_select_button.png", "button8");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "start_button", "controllers/ps4_start_button.png", "button9");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4.12", "button10");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "stick_left", "controllers/ps4_stick_left.png", "button11");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "stick_right", "controllers/ps4_stick_right.png", "button12");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "dpad_up", "controllers/ps4_dpad_up.png", "dpadup");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "dpad_right", "controllers/ps4_dpad_right.png", "dpadri");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "dpad_down", "controllers/ps4_dpad_down.png", "dpaddo");
//    	ControllerModel.addButton(PS4_LINUX, "button.ps4", "dpad_left", "controllers/ps4_dpad_left.png", "dpadle");
//    	ControllerModel.addButton(PS4_LINUX, "posit_axis.ps4.0", "left_stick_right", "axis_pos0");
//    	ControllerModel.addButton(PS4_LINUX, "posit_axis.ps4.1", "left_stick_down", "axis_pos1");
//    	ControllerModel.addButton(PS4_LINUX, "posit_axis.ps4.3", "right_stick_right", "axis_pos3");
//    	ControllerModel.addButton(PS4_LINUX, "posit_axis.ps4.4", "right_stick_down", "axis_pos4");
//    	ControllerModel.addButton(PS4_LINUX, "negat_axis.ps4.0", "left_stick_left", "axis_neg0");
//    	ControllerModel.addButton(PS4_LINUX, "negat_axis.ps4.1", "left_stick_up", "axis_neg1");
//    	ControllerModel.addButton(PS4_LINUX, "negat_axis.ps4.3", "right_stick_left", "axis_neg3");
//    	ControllerModel.addButton(PS4_LINUX, "negat_axis.ps4.4", "right_stick_up", "axis_neg4");
//    	ControllerModel.addButton(PS4_LINUX, "axis.ps4.0", "left_stick_horizontal", "axis0");
//    	ControllerModel.addButton(PS4_LINUX, "axis.ps4.1", "left_stick_vertical", "axis1");
//    	ControllerModel.addButton(PS4_LINUX, "axis.ps4.3", "right_stick_horizontal", "axis3");
//    	ControllerModel.addButton(PS4_LINUX, "axis.ps4.4", "right_stick_vertical", "axis4");
    }
	
	public List<Integer> getControllerNegativeTriggers() {
		return controllerNegativeTriggers;
	}
	
	public List<Integer> getControllerPositiveTriggers() {
		return controllerPositiveTriggers;
	}
	
	public String getGUID() {
		return GUID;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public Component getDisplayName(boolean showOS) {
		return Component.translatable("model."+modelName).append((showOS?" ("+getOs().telemetryName().substring(0, 1).toUpperCase()+getOs().telemetryName().substring(1)+")":""));
	}
	
	public List<String> getDupeButtons() {
		return dupeButtons;
	}

	public OS getOs() {
		return os;
	}
}
