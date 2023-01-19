package com.stereowalker.controllermod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.controller.ControllerUtil;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick_inject(boolean bl, float f, CallbackInfo ci) {
		if (ControllerMod.CONFIG.usePreciseMovement && ControllerUtil.isControllerAvailable(ControllerMod.getInstance().controllerOptions.controllerNumber) && ControllerMod.getInstance().controllerOptions.enableController) {
			ControllerOptions settings = ControllerMod.getInstance().controllerOptions;
			float moveXAxis = bl ? settings.controllerBindMoveHorizontal.getAxis() * f : settings.controllerBindMoveHorizontal.getAxis();
			float moveYAxis = bl ? settings.controllerBindMoveVertical.getAxis() * f : settings.controllerBindMoveVertical.getAxis();
			if (moveXAxis >= -1.0F && moveXAxis < -ControllerMod.CONFIG.deadzone) {
				this.leftImpulse = -moveXAxis;
				this.left = true;
			}
			if (moveXAxis <= 1.0F && moveXAxis > ControllerMod.CONFIG.deadzone) {
				this.leftImpulse = -moveXAxis;
				this.right = true;
			}
			if (moveYAxis >= -1.0F && moveYAxis < -ControllerMod.CONFIG.deadzone) {
				this.forwardImpulse = -moveYAxis;
				this.up = true;
			}
			if (moveYAxis <= 1.0F && moveYAxis > ControllerMod.CONFIG.deadzone) {
				this.forwardImpulse = -moveYAxis;
				this.down = true;
			}
		}
	}

}
