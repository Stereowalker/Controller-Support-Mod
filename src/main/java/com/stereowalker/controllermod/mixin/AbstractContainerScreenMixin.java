package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stereowalker.controllermod.ControllerMod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Mixin(AbstractContainerScreen.class)
@Environment(EnvType.CLIENT)
public abstract class AbstractContainerScreenMixin <T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {

	public AbstractContainerScreenMixin(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pTitle);
	}
	
	@Redirect(method = {"mouseReleased", "mouseClicked"}, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;isKeyDown(JI)Z"))
	public boolean shiftClick(long l, int i) {
		return Screen.hasShiftDown() || ControllerMod.getInstance().controllerOptions.controllerBindQuickMove.isDown(ControllerMod.getInstance().getActiveController().getModel());
	}
	
	@Redirect(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hasShiftDown()Z"))
	public boolean shiftClick() {
		return Screen.hasShiftDown() || ControllerMod.getInstance().controllerOptions.controllerBindQuickMove.isDown(ControllerMod.getInstance().getActiveController().getModel());
	}
}
