package com.stereowalker.controllermod.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerSettings;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;
import com.stereowalker.controllermod.client.gui.screen.ControllerOptionsScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldLoadProgressScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ControllerEvent {
	public static final ResourceLocation CURSOR = new ResourceLocation(ControllerMod.MOD_ID, "textures/gui/cursor.png");
	private static final ResourceLocation CONTROLLER_BUTTON_TEXTURES = new ResourceLocation(ControllerMod.MOD_ID, "textures/gui/controller_button.png");
	//	private static MouseHelper mouse = Minecraft.getInstance().mouseHelper;
	//	private static Screen getCurrentScreen() = Minecraft.getInstance().getCurrentScreen();

	public static Screen getCurrentScreen() {
		return Minecraft.getInstance().currentScreen;
	}

	public static MouseHelper getMouse() {
		return Minecraft.getInstance().mouseHelper;
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void drawButtons(GuiScreenEvent.InitGuiEvent event) {
		if(event.getGui() instanceof MainMenuScreen) {
			event.addWidget(new ImageButton(event.getGui().width / 2 + 104, event.getGui().height / 4 + 24 + 24 * 2, 20, 20, 0, 0, 20, CONTROLLER_BUTTON_TEXTURES, 20, 40, (p_213088_1_) -> {
				event.getGui().getMinecraft().displayGuiScreen(new ControllerOptionsScreen(event.getGui()));
			}, new TranslationTextComponent("menu.button.controllers")));
		}
//		MouseSettingsScreen
		if(event.getGui() instanceof IngameMenuScreen) {
			event.addWidget(new ImageButton(event.getGui().width / 2 + 104, event.getGui().height / 4 + 96 + -16, 20, 20, 0, 0, 20, CONTROLLER_BUTTON_TEXTURES, 20, 40, (p_213088_1_) -> {
				event.getGui().getMinecraft().displayGuiScreen(new ControllerOptionsScreen(event.getGui()));
			}, new TranslationTextComponent("menu.button.controllers")));
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void drawPointer(DrawScreenEvent event) {
		if (event.getGui() != null) {
			if (!(event.getGui() instanceof WorldLoadProgressScreen)) {
				int x = (int)(ControllerUtil.virtualmouse.getMouseX() * (double)Minecraft.getInstance().getMainWindow().getScaledWidth() / (double)Minecraft.getInstance().getMainWindow().getWidth());
				int y = (int)(ControllerUtil.virtualmouse.getMouseY() * (double)Minecraft.getInstance().getMainWindow().getScaledHeight() / (double)Minecraft.getInstance().getMainWindow().getHeight());
				if(ControllerUtil.isControllerAvailable(ControllerUtil.controller) && ControllerUtil.enableController) {
					Minecraft.getInstance().getTextureManager().bindTexture(CURSOR);
					renderCursor(x,y, 5.0D);
				} 
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void controllerHandler(CameraSetup event) {
		if(ControllerUtil.isControllerAvailable(ControllerUtil.controller) && ControllerUtil.enableController && getCurrentScreen() == null) {
			ControllerSettings settings = ControllerMod.getInstance().controllerSettings;
			float cameraXAxis = settings.controllerBindCameraHorizontal.getAxis();
			float cameraYAxis = settings.controllerBindCameraVertical.getAxis();

			double moveModifier = 10.0D;

			float newPitch = (cameraYAxis >= -1.0F && cameraYAxis < -0.1D) || (cameraYAxis <= 1.0F && cameraYAxis > 0.1D) ? (float) ((cameraYAxis * ControllerUtil.ingameSensitivity * moveModifier) + Minecraft.getInstance().player.getPitch((float) event.getRenderPartialTicks())) : Minecraft.getInstance().player.getPitch((float) event.getRenderPartialTicks());
			float newYaw = (cameraXAxis >= -1.0F && cameraXAxis < -0.1D) || (cameraXAxis <= 1.0F && cameraXAxis > 0.1D) ? (float) ((cameraXAxis * ControllerUtil.ingameSensitivity * moveModifier) + Minecraft.getInstance().player.getYaw((float) event.getRenderPartialTicks())) : Minecraft.getInstance().player.getYaw((float) event.getRenderPartialTicks());

			Minecraft.getInstance().player.rotationPitch = newPitch;
			Minecraft.getInstance().player.rotationYaw = newYaw;
			//			event.setRoll(cameraXAxis + event.getRoll());
		}
	}

	static boolean fromGame = false;
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void controllerHandler(ClientTickEvent event) {
		if(ControllerUtil.isControllerAvailable(ControllerUtil.controller) && ControllerUtil.enableController) {
			ControllerSettings settings = ControllerMod.getInstance().controllerSettings;
			if (getMouse().isMouseGrabbed()) ControllerUtil.virtualmouse.grabMouse();
			else ControllerUtil.virtualmouse.ungrabMouse();
			if(!Minecraft.getInstance().isGameFocused()) Minecraft.getInstance().setGameFocused(true);
			Controller controller = ControllerMod.getInstance().getActiveController();
			if (controller != null) {
				float scrollAxis = settings.controllerBindScroll.getAxis();
				float cameraXAxis = settings.controllerBindCameraHorizontal.getAxis();
				float cameraYAxis = settings.controllerBindCameraVertical.getAxis();
				float mouseXAxis = settings.controllerBindMouseHorizontal.getAxis();
				float mouseYAxis = settings.controllerBindMouseVertical.getAxis();
				//Inventory Keybinds
				if(getCurrentScreen() instanceof ContainerScreen) {
					//					if(fromGame) {
					//						ControllerUtil.unpressAllKeys();
					//						fromGame = false;
					//					}
					ControllerUtil.handleContainerInput(controller, mouseXAxis, mouseYAxis, scrollAxis);
				}

				//Ingame Menu Keybinds
				if(getCurrentScreen() instanceof IngameMenuScreen) {
					if(fromGame) {
						ControllerUtil.unpressAllKeys();
						fromGame = false;
					}
					ControllerUtil.handleScreenInput(controller, mouseXAxis, mouseYAxis, scrollAxis, true);
				}

				//Any other menu
				if(getCurrentScreen()!=null && !(getCurrentScreen() instanceof ContainerScreen) && !(getCurrentScreen() instanceof IngameMenuScreen)) {
					if(getCurrentScreen() instanceof ControllerInputOptionsScreen) {
						if(!((ControllerInputOptionsScreen)getCurrentScreen()).isAwaitingInput()) {
							if(fromGame) {
								ControllerUtil.unpressAllKeys();
								fromGame = false;
							}
							ControllerUtil.handleScreenInput(controller, mouseXAxis, mouseYAxis, scrollAxis, false);
						}
					} else {
						if(fromGame) {
							ControllerUtil.unpressAllKeys();
							fromGame = false;
						}
						ControllerUtil.handleScreenInput(controller, mouseXAxis, mouseYAxis, scrollAxis, false);
					}
				}

				//Ingame Keybinds
				if(getCurrentScreen()==null) {
					fromGame = true;
					ControllerUtil.handleIngameInput(controller, cameraXAxis, cameraYAxis);
				}
			}
		}

	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void overrideInput(InputUpdateEvent event) {
		if (ControllerMod.getInstance().controllerSettings.useAxisToMove) {
			if(ControllerUtil.isControllerAvailable(ControllerUtil.controller) && ControllerUtil.enableController) {
				if (getCurrentScreen() == null) {
					ControllerSettings settings = ControllerMod.getInstance().controllerSettings;
					float moveXAxis = Minecraft.getInstance().player.isForcedDown() ? settings.controllerBindMoveHorizontal.getAxis() * 0.3F : settings.controllerBindMoveHorizontal.getAxis();
					float moveYAxis = Minecraft.getInstance().player.isForcedDown() ? settings.controllerBindMoveVertical.getAxis() * 0.3F : settings.controllerBindMoveVertical.getAxis();
					if (moveXAxis >= -1.0F && moveXAxis < -ControllerUtil.dead_zone) {
						event.getMovementInput().moveStrafe = -moveXAxis;
						event.getMovementInput().leftKeyDown = true;
					}
					if (moveXAxis <= 1.0F && moveXAxis > ControllerUtil.dead_zone) {
						event.getMovementInput().moveStrafe = -moveXAxis;
						event.getMovementInput().rightKeyDown = true;
					}
					if (moveYAxis >= -1.0F && moveYAxis < -ControllerUtil.dead_zone) {
						event.getMovementInput().moveForward = -moveYAxis;
						event.getMovementInput().forwardKeyDown = true;
					}
					if (moveYAxis <= 1.0F && moveYAxis > ControllerUtil.dead_zone) {
						event.getMovementInput().moveForward = -moveYAxis;
						event.getMovementInput().backKeyDown = true;
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	protected static void renderCursor(int x, int y, double size) {
		RenderSystem.pushMatrix();
		RenderSystem.enableAlphaTest();
		RenderSystem.disableDepthTest();
//		RenderSystem.depthMask(true);
//		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA, GlStateManager.SourceFactor.ONE_MINUS_CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
		Minecraft.getInstance().getTextureManager().bindTexture(CURSOR);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-size+x, size+y, -90.0F).tex(0.0F, 1.0F).endVertex();
		bufferbuilder.pos(size+x, size+y, -90.0F).tex(1.0F, 1.0F).endVertex();
		bufferbuilder.pos(size+x, -size+y, -90.0F).tex(1.0F, 0.0F).endVertex();
		bufferbuilder.pos(-size+x, -size+y, -90.0F).tex(0.0F, 0.0F).endVertex();
		tessellator.draw();
//		RenderSystem.depthMask(false);
//		RenderSystem.enableDepthTest();
		RenderSystem.disableAlphaTest();
		RenderSystem.popMatrix();
		Minecraft.getInstance().getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
	}
}
