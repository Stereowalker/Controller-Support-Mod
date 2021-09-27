package com.stereowalker.controllermod.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VirtualMouseHelper extends MouseHandler {
	private final Minecraft minecraft;
	private boolean leftDown;
	private boolean middleDown;
	private boolean rightDown;
	private double mouseX;
	private double mouseY;
	private int simulatedRightClicks;
	private int activeButton = -1;
	private boolean ignoreFirstMove = true;
	private int touchScreenCounter;
	private double eventTime;
	private final SmoothDouble xSmoother = new SmoothDouble();
	private final SmoothDouble ySmoother = new SmoothDouble();
	private double xVelocity;
	private double yVelocity;
	private double accumulatedScrollDelta;
	private double lastLookTime = Double.MIN_VALUE;
	private boolean mouseGrabbed;

	public VirtualMouseHelper(Minecraft minecraftIn) {
		super(minecraftIn);
		this.minecraft = minecraftIn;
	}

	/**
	 * Will be called when a mouse button is pressed or released.
	 *  
	 * @see GLFWMouseButtonCallbackI
	 */
	public void mouseButtonCallback(long handle, int button, int action, int mods) {
		if (handle == this.minecraft.getWindow().getWindow()) {
			boolean flag = action == 1;
			if (Minecraft.ON_OSX && button == 0) {
				if (flag) {
					if ((mods & 2) == 2) {
						button = 1;
						++this.simulatedRightClicks;
					}
				} else if (this.simulatedRightClicks > 0) {
					button = 1;
					--this.simulatedRightClicks;
				}
			}

			if (flag) {
				if (this.minecraft.options.touchscreen && this.touchScreenCounter++ > 0) {
					return;
				}

				this.activeButton = button;
				this.eventTime = Blaze3D.getTime();
			} else if (this.activeButton != -1) {
				if (this.minecraft.options.touchscreen && --this.touchScreenCounter > 0) {
					return;
				}

				this.activeButton = -1;
			}

			boolean[] aboolean = new boolean[]{false};
			if (this.minecraft.getOverlay() == null) {
				if (this.minecraft.screen == null) {
					if (!this.mouseGrabbed && flag) {
						this.grabMouse();
					}
					if (net.minecraftforge.client.ForgeHooksClient.onRawMouseClicked(button, action, mods)) return;
				} else {
					double d0 = this.mouseX * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getWidth();
					double d1 = this.mouseY * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getHeight();
					int p_198023_3_f = button;
					if (flag) {
						Screen.wrapScreenError(() -> {
							aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseClickedPre(this.minecraft.screen, d0, d1, p_198023_3_f);
							if (!aboolean[0]) aboolean[0] = this.minecraft.screen.mouseClicked(d0, d1, p_198023_3_f);
							if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseClickedPost(this.minecraft.screen, d0, d1, p_198023_3_f);
						}, "mouseClicked event handler", this.minecraft.screen.getClass().getCanonicalName());
					} else {
						Screen.wrapScreenError(() -> {
							aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseReleasedPre(this.minecraft.screen, d0, d1, p_198023_3_f);
							if (!aboolean[0]) aboolean[0] = this.minecraft.screen.mouseReleased(d0, d1, p_198023_3_f);
							if (!aboolean[0]) aboolean[0] = net.minecraftforge.client.ForgeHooksClient.onGuiMouseReleasedPost(this.minecraft.screen, d0, d1, p_198023_3_f);
						}, "mouseReleased event handler", this.minecraft.screen.getClass().getCanonicalName());
					}
				}
			}

			if (!aboolean[0] && (this.minecraft.screen == null || this.minecraft.screen.passEvents) && this.minecraft.getOverlay() == null) {
				if (button == 0) {
					this.leftDown = flag;
				} else if (button == 2) {
					this.middleDown = flag;
				} else if (button == 1) {
					this.rightDown = flag;
				}

				KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(button), flag);
				if (flag) {
					if (this.minecraft.player.isSpectator() && button == 2) {
						this.minecraft.gui.getSpectatorGui().onMouseMiddleClick();
					} else {
						KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(button));
					}
				}
			}
			net.minecraftforge.client.ForgeHooksClient.fireMouseInput(button, action, mods);
		}
	}

	/**
	 * Will be called when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.
	 *  
	 * @see GLFWScrollCallbackI
	 */
	public void scrollCallback(long handle, double xoffset, double yoffset) {
		if (handle == Minecraft.getInstance().getWindow().getWindow()) {
			double d0 = (this.minecraft.options.discreteMouseScroll ? Math.signum(yoffset) : yoffset) * this.minecraft.options.mouseWheelSensitivity;
			if (this.minecraft.getOverlay() == null) {
				if (this.minecraft.screen != null) {
					double d1 = this.mouseX * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getWidth();
					double d2 = this.mouseY * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getHeight();
					if (net.minecraftforge.client.ForgeHooksClient.onGuiMouseScrollPre(this, this.minecraft.screen, d0)) return;
					if (this.minecraft.screen.mouseScrolled(d1, d2, d0)) return;
					net.minecraftforge.client.ForgeHooksClient.onGuiMouseScrollPost(this, this.minecraft.screen, d0);
				} else if (this.minecraft.player != null) {
					if (this.accumulatedScrollDelta != 0.0D && Math.signum(d0) != Math.signum(this.accumulatedScrollDelta)) {
						this.accumulatedScrollDelta = 0.0D;
					}

					this.accumulatedScrollDelta += d0;
					float f1 = (float)((int)this.accumulatedScrollDelta);
					if (f1 == 0.0F) {
						return;
					}

					this.accumulatedScrollDelta -= (double)f1;
					//               if (net.minecraftforge.client.ForgeHooksClient.onMouseScrolled(this, d0)) return;
					if (this.minecraft.player.isSpectator()) {
						if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
							this.minecraft.gui.getSpectatorGui().onMouseScrolled((double)(-f1));
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

	@Override
	public void setup(long handle) {
		InputConstants.setupMouseCallbacks(handle, (p_228032_1_, p_228032_3_, p_228032_5_) -> {
			this.minecraft.execute(() -> {
				this.onMove(p_228032_1_, p_228032_3_, p_228032_5_);
			});
		}, (p_228028_1_, p_228028_3_, p_228028_4_, p_228028_5_) -> {
			this.minecraft.execute(() -> {
				this.mouseButtonCallback(p_228028_1_, p_228028_3_, p_228028_4_, p_228028_5_);
			});
		}, (p_228029_1_, p_228029_3_, p_228029_5_) -> {
			this.minecraft.execute(() -> {
				this.scrollCallback(p_228029_1_, p_228029_3_, p_228029_5_);
			});
		}, (p_238227_1_, p_238227_3_, p_238227_4_) -> {
			Path[] apath = new Path[p_238227_3_];

			for(int i = 0; i < p_238227_3_; ++i) {
				apath[i] = Paths.get(GLFWDropCallback.getName(p_238227_4_, i));
			}

			this.minecraft.execute(() -> {
				this.onDrop(p_238227_1_, Arrays.asList(apath));
			});
		});
	}

	/**
	 * Will be called when the cursor is moved.
	 *  
	 * <p>The callback function receives the cursor position, measured in screen coordinates but relative to the top-left
	 * corner of the window client area. On platforms that provide it, the full sub-pixel cursor position is passed
	 * on.</p>
	 *  
	 * @see GLFWCursorPosCallbackI
	 */
	@Override
	public void onMove(long handle, double xpos, double ypos) {
		if (handle == Minecraft.getInstance().getWindow().getWindow()) {
			if (this.ignoreFirstMove) {
				this.mouseX = xpos;
				this.mouseY = ypos;
				this.ignoreFirstMove = false;
			}

			GuiEventListener iguieventlistener = this.minecraft.screen;
			if (iguieventlistener != null && this.minecraft.getOverlay() == null) {
				double d0 = xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getWidth();
				double d1 = ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getHeight();
				Screen.wrapScreenError(() -> {
					iguieventlistener.mouseMoved(d0, d1);
				}, "mouseMoved event handler", iguieventlistener.getClass().getCanonicalName());
				if (this.activeButton != -1 && this.eventTime > 0.0D) {
					double d2 = (xpos - this.mouseX) * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getWidth();
					double d3 = (ypos - this.mouseY) * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getHeight();
					Screen.wrapScreenError(() -> {
						if (net.minecraftforge.client.ForgeHooksClient.onGuiMouseDragPre(this.minecraft.screen, d0, d1, this.activeButton, d2, d3)) return;
						if (iguieventlistener.mouseDragged(d0, d1, this.activeButton, d2, d3)) return;
						net.minecraftforge.client.ForgeHooksClient.onGuiMouseDragPost(this.minecraft.screen, d0, d1, this.activeButton, d2, d3);
					}, "mouseDragged event handler", iguieventlistener.getClass().getCanonicalName());
				}
			}

			this.minecraft.getProfiler().push("mouse");
			if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
				this.xVelocity += xpos - this.mouseX;
				this.yVelocity += ypos - this.mouseY;
			}

			this.turnPlayer();
			this.mouseX = xpos;
			this.mouseY = ypos;
			this.minecraft.getProfiler().pop();
		}
	}

	@Override
	public void turnPlayer() {
		double d0 = Blaze3D.getTime();
		double d1 = d0 - this.lastLookTime;
		this.lastLookTime = d0;
		if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
			double d4 = this.minecraft.options.sensitivity * (double)0.6F + (double)0.2F;
			double d5 = d4 * d4 * d4 * 8.0D;
			double d2;
			double d3;
			if (this.minecraft.options.smoothCamera) {
				double d6 = this.xSmoother.getNewDeltaValue(this.xVelocity * d5, d1 * d5);
				double d7 = this.ySmoother.getNewDeltaValue(this.yVelocity * d5, d1 * d5);
				d2 = d6;
				d3 = d7;
			} else {
				this.xSmoother.reset();
				this.ySmoother.reset();
				d2 = this.xVelocity * d5;
				d3 = this.yVelocity * d5;
			}

			this.xVelocity = 0.0D;
			this.yVelocity = 0.0D;
			int i = 1;
			if (this.minecraft.options.invertYMouse) {
				i = -1;
			}

			this.minecraft.getTutorial().onMouse(d2, d3);
			if (this.minecraft.player != null) {
				this.minecraft.player.turn(d2, d3 * (double)i);
			}

		} else {
			this.xVelocity = 0.0D;
			this.yVelocity = 0.0D;
		}
	}

	/**
	 * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
	 * currently displayed
	 */
	@Override
	public void grabMouse() {
		if (this.minecraft.isWindowActive()) {
			if (!this.mouseGrabbed) {
				if (!Minecraft.ON_OSX) {
					KeyMapping.setAll();
				}

				this.mouseGrabbed = true;
				this.mouseX = (double)(this.minecraft.getWindow().getWidth() / 2);
				this.mouseY = (double)(this.minecraft.getWindow().getHeight() / 2);
				InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.mouseX, this.mouseY);
				this.minecraft.setScreen((Screen)null);
				//            this.minecraft.leftClickCounter = 10000;
				this.ignoreFirstMove = true;
			}
		}
	}

	/**
	 * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
	 */
	public void ungrabMouse() {
		if (this.mouseGrabbed) {
			this.mouseGrabbed = false;
			this.mouseX = (double)(this.minecraft.getWindow().getWidth() / 2);
			this.mouseY = (double)(this.minecraft.getWindow().getHeight() / 2);
			InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.mouseX, this.mouseY);
		}
	}
}