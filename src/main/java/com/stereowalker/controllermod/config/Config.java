package com.stereowalker.controllermod.config;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;

@UnionConfig(name = "controllermod", autoReload = true, translatableName = "gui.controller_mod")
public class Config implements ConfigObject {
	
	@UnionConfig.Entry(group = "General", name = "Debug Mode")
	public boolean debug = false;
	
	@UnionConfig.Entry(group = "Controller", name = "Ingame Sensitivity")
	@UnionConfig.Comment(comment = "The sensitivity of the controller ingame")
	@UnionConfig.Range(max = 1.0, min = 0.0)
	@UnionConfig.Slider
	public float ingame_sensitivity = 0.5f;
	
	@UnionConfig.Entry(group = "Controller", name = "Menu Sensitivity")
	@UnionConfig.Comment(comment = "The sensitivity of the controller in the menu")
	@UnionConfig.Range(max = 1.0, min = 0.0)
	@UnionConfig.Slider
	public float menu_sensitivity = 0.2f;
	
	@UnionConfig.Entry(group = "Controller", name = "Deadzone")
	@UnionConfig.Comment(comment = "The deadzone of the controller")
	@UnionConfig.Range(max = 1.0, min = 0.0)
	@UnionConfig.Slider
	public float deadzone = 0.2f;
	
	@UnionConfig.Entry(group = "Controller", name = "Use Precise Movement")
	@UnionConfig.Comment(comment = {"Allows you to use mose fine movements using your thumbstick"})
	public boolean usePreciseMovement = true;
	
	@UnionConfig.Entry(group = "Gameplay", name = "Show Ingame Player Names")
	@UnionConfig.Comment(comment = {"Shows your name on the to right of the screen when in multiplayer","Useful in split-screen to identify whose instance is whose"})
	public boolean ingame_player_names = true;
	
	@UnionConfig.Entry(group = "Gameplay", name = "Show Coordinates")
	@UnionConfig.Comment(comment = {"Shows your current coordinates on the screen if you are allowed to see them","I hope you weren't planning to use this feature to bypass the reduced debug info rule"})
	public boolean show_coordinates = false;
	
	@UnionConfig.Entry(group = "Gameplay", name = "Show Paper Doll")
	@UnionConfig.Comment(comment = {"Shows the mini you at the top left corner of the screen","Adorable right?"})
	public boolean show_paper_doll = true;
}
