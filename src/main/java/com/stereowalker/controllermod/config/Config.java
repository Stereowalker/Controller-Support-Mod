package com.stereowalker.controllermod.config;

import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
	public static ForgeConfigSpec.BooleanValue debug_mode;
	//Controller Settings
	public static ForgeConfigSpec.BooleanValue enableControllers;
	public static ForgeConfigSpec.IntValue controllerNumber;
	public static ForgeConfigSpec.DoubleValue ingameSensitivity;
	public static ForgeConfigSpec.DoubleValue menuSensitivity;
	public static ForgeConfigSpec.DoubleValue deadzone;
	public static ForgeConfigSpec.EnumValue<ControllerModel> controllerModel;
	

	public static ForgeConfigSpec.BooleanValue ingamePlayerNames;
	public static ForgeConfigSpec.BooleanValue hideCoordinates;
	public static ForgeConfigSpec.BooleanValue hidePaperDoll;
	//PaperDollOptions
	public static ForgeConfigSpec.BooleanValue isFallFlying;
	public static ForgeConfigSpec.BooleanValue isRiding;
	public static ForgeConfigSpec.BooleanValue isSpinning;
	public static ForgeConfigSpec.BooleanValue isMoving;
	public static ForgeConfigSpec.BooleanValue isJumping;
	public static ForgeConfigSpec.BooleanValue isAttacking;
	public static ForgeConfigSpec.BooleanValue isUsing;
	public static ForgeConfigSpec.BooleanValue isHurt;
	public static ForgeConfigSpec.BooleanValue isOnFire;
	public static ForgeConfigSpec.BooleanValue isAlwaysOn;
	
	//Ingame Input
	
	public static void init(ForgeConfigSpec.Builder client) {
		client.comment("Developer Options");
		debug_mode = client
				.define("developerOptions.debug_mode", false);

		//PaperDoll
		isFallFlying = client.define("Paper Doll.Fall Flying", true);
		isRiding = client.define("Paper Doll.Riding", true);
		isSpinning = client.define("Paper Doll.Spinning", true);
		isMoving = client.define("Paper Doll.Moving", true);
		isJumping = client.define("Paper Doll.Jumping", true);
		isAttacking = client.define("Paper Doll.Attacking", true);
		isUsing = client.define("Paper Doll.Using", true);
		isHurt = client.define("Paper Doll.Hurt", true);
		isOnFire = client.define("Paper Doll.On Fire", true);
		//Controller Settings
		client.comment("Controller");
		enableControllers = client
				.define("controller.enableControllers", false);
		
		ingamePlayerNames = client
				.define("controller.In-Game Player Names", true);
		hideCoordinates = client
				.define("controller.Hide Coordinates", false);
		hidePaperDoll = client
				.define("Paper Doll.Hide Paper Doll", false);
		controllerNumber = client
				.comment("Select Controller \n Whatever controller you choose should be your controller minus 1. So that would mean controller 1 would be 0")
				.defineInRange("controller.controllerNumber", 1, 1, 8);
		ingameSensitivity = client
				.comment("The sensitivity of the controller ingame")
				.defineInRange("controller.ingameSensitivity", 0.5D, 0, 1.0D);
		menuSensitivity = client
				.comment("The sensitivity of the controller in the menu")
				.defineInRange("controller.menuSensitivity", 0.2D, 0, 1.0D);
		deadzone = client
				.comment("The dead zone of the controller")
				.defineInRange("controller.deadzone", 0.2D, 0, 1.0D);
		controllerModel = client.defineEnum("controller.loadMap", ControllerModel.XBOX_360);
	}


}
