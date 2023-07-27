package com.stereowalker.controllermod.client;

import org.lwjgl.glfw.GLFWScrollCallbackI;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.Mth;

public class VirtualMouseHelper extends MouseHandler {

	public VirtualMouseHelper(Minecraft minecraftIn) {
		super(minecraftIn);
	}

	/**
	 * Will be called when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.
	 *  
	 * @see GLFWScrollCallbackI
	 */
	public void scrollCallback(long handle, double xoffset, double yoffset) {
		if (handle == Minecraft.getInstance().getWindow().getWindow()) {
			double d0 = (this.minecraft.options.discreteMouseScroll().get() ? Math.signum(yoffset) : yoffset) * this.minecraft.options.mouseWheelSensitivity().get();
			if (this.minecraft.getOverlay() == null) {
				if (this.minecraft.screen != null) {
					double d1 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getWidth();
					double d2 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getHeight();
					if (this.minecraft.screen.mouseScrolled(d1, d2, d0)) return;
					this.minecraft.screen.afterMouseAction();
				} else if (this.minecraft.player != null) {
					if (this.accumulatedScroll != 0.0D && Math.signum(d0) != Math.signum(this.accumulatedScroll)) {
						this.accumulatedScroll = 0.0D;
					}

					this.accumulatedScroll += d0;
					int f1 = (int)this.accumulatedScroll;
					if (f1 == 0.0F) {
						return;
					}

					this.accumulatedScroll -= (double)f1;
					//               if (net.minecraftforge.client.ForgeHooksClient.onMouseScrolled(this, d0)) return;
					if (this.minecraft.player.isSpectator()) {
						if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
							this.minecraft.gui.getSpectatorGui().onMouseScrolled(-f1);
						} else {
							float f = Mth.clamp(this.minecraft.player.getAbilities().getFlyingSpeed() + f1 * 0.005F, 0.0F, 0.2F);
							this.minecraft.player.getAbilities().setFlyingSpeed(f);
						}
					} else {
						this.minecraft.player.getInventory().swapPaint((double)f1);
					}
				}
			}
		}

	}

	/**
	 * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
	 */
	public void ungrabMouse() {
		if (this.mouseGrabbed) {
			this.mouseGrabbed = false;
			this.xpos = (double)(this.minecraft.getWindow().getWidth() / 2);
			this.ypos = (double)(this.minecraft.getWindow().getHeight() / 2);
			InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.xpos, this.ypos);
		}
	}
}