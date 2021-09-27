package com.stereowalker.controllermod.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaperDollSettings {
	public boolean showSwimming = true;
	public boolean showCrawling = true;
	public boolean showSprinting = true;
	public boolean showCrouching = true;
	public boolean showFlying = true;
	
	public PaperDollSettings() {
	}
	
	public boolean renderCrawling(LocalPlayer player) {
		return this.showCrawling && (player.getPose() == Pose.SWIMMING && !player.isSwimming());
	}
	
	public boolean renderSwimming(LocalPlayer player) {
		return this.showSwimming && player.isSwimming();
	}
	
	public boolean renderSprinting(LocalPlayer player) {
		return this.showSprinting && (player.isSprinting() && !player.isSwimming());
	}
	
	public boolean renderCrouching(LocalPlayer player) {
		return this.showCrouching && (player.isCrouching());
	}
	
	public boolean renderFlying(LocalPlayer player) {
		return this.showFlying && (player.getAbilities().flying);
	}
}
