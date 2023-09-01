package com.stereowalker.controllermod.client.controller;

import java.util.Objects;

import com.google.common.collect.Lists;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

public class ControllerMap {
	public static String map(String input, ControllerModel model) {
		String lAr = "\u2190";
		String uAr = "\u2191";
		String rAr = "\u2192";
		String dAr = "\u2193";
		if (input.equals("empty") || input.equals(" ")) return " ";
		if (model == ControllerModel.XBOX_360_WINDOWS)
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
		if (model == ControllerModel.PS4_WINDOWS)
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
		if (input.equals("dpadup")) return "UP";
		else if (input.equals("dpadri")) return "RIGHT";
		else if (input.equals("dpaddo")) return "DOWN";
		else if (input.equals("dpadle")) return "LEFT";
		return I18n.get(model.getOrCreate(Lists.newArrayList(input))[0].getName());
	}


    public static final class Button {
		public ControllerModel model;
		public String value;
		public String name;
		public ResourceLocation icon;

		protected Button(String name, ControllerModel model, ResourceLocation icon, String value){
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
}
