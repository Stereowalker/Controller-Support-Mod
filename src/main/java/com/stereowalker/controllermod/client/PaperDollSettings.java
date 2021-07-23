package com.stereowalker.controllermod.client;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Pose;
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
	
	public boolean renderCrawling(ClientPlayerEntity player) {
		return this.showCrawling && (player.getPose() == Pose.SWIMMING && !player.isSwimming());
	}
	
	public boolean renderSwimming(ClientPlayerEntity player) {
		return this.showSwimming && player.isSwimming();
	}
	
	public boolean renderSprinting(ClientPlayerEntity player) {
		return this.showSprinting && (player.isSprinting() && !player.isSwimming());
	}
	
	public boolean renderCrouching(ClientPlayerEntity player) {
		return this.showCrouching && (player.isCrouching());
	}
	
	public boolean renderFlying(ClientPlayerEntity player) {
		return this.showFlying && (player.abilities.isFlying);
	}
}
