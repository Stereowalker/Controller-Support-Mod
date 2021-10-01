package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.controllermod.client.controller.ControllerBindings;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(AbstractContainerScreen.class)
@OnlyIn(Dist.CLIENT)
public abstract class AbstractContainerScreenMixin <T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

	public AbstractContainerScreenMixin(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pTitle);
	}
	
	/**
	 * @owner Stereowalker
	 */
	@Redirect(method = {"mouseReleased", "mouseClicked"}, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;isKeyDown(JI)Z"))
	public boolean shiftClick(long l, int i) {
		return Screen.hasShiftDown() || ControllerBindings.SHIFT_MOVE_INPUT.isDown(Config.controllerModel.get());
	}
	
	/**
	 * @owner Stereowalker
	 */
	@Redirect(method = "mouseReleased", at = @At(value = "INVOKE", target = "hasShiftDown()Z"))
	public boolean shiftClick() {
		return Screen.hasShiftDown() || ControllerBindings.SHIFT_MOVE_INPUT.isDown(Config.controllerModel.get());
	}

}
