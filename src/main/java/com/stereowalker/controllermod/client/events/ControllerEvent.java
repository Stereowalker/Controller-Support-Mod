package com.stereowalker.controllermod.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerSettings;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.gui.screen.ControllerInputOptionsScreen;
import com.stereowalker.controllermod.client.gui.screen.ControllerOptionsScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
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
	//	private static MouseHandler mouse = Minecraft.getInstance().mouseHandler;
	//	private static Screen getCurrentScreen() = Minecraft.getInstance().getCurrentScreen();

	public static Screen getCurrentScreen() {
		return Minecraft.getInstance().screen;
	}

	public static MouseHandler getMouse() {
		return Minecraft.getInstance().mouseHandler;
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void drawButtons(GuiScreenEvent.InitGuiEvent event) {
		if(event.getGui() instanceof TitleScreen) {
			event.addWidget(new ImageButton(event.getGui().width / 2 + 104, event.getGui().height / 4 + 24 + 24 * 2, 20, 20, 0, 0, 20, CONTROLLER_BUTTON_TEXTURES, 20, 40, (p_213088_1_) -> {
				event.getGui().getMinecraft().setScreen(new ControllerOptionsScreen(event.getGui()));
			}, new TranslatableComponent("menu.button.controllers")));
		}
//		MouseSettingsScreen
		if(event.getGui() instanceof PauseScreen) {
			event.addWidget(new ImageButton(event.getGui().width / 2 + 104, event.getGui().height / 4 + 96 + -16, 20, 20, 0, 0, 20, CONTROLLER_BUTTON_TEXTURES, 20, 40, (p_213088_1_) -> {
				event.getGui().getMinecraft().setScreen(new ControllerOptionsScreen(event.getGui()));
			}, new TranslatableComponent("menu.button.controllers")));
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void drawPointer(DrawScreenEvent event) {
		if (event.getGui() != null) {
			if (!(event.getGui() instanceof LevelLoadingScreen)) {
				int x = (int)(ControllerUtil.virtualmouse.xpos() * (double)Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getWidth());
				int y = (int)(ControllerUtil.virtualmouse.ypos() * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getHeight());
				if(ControllerUtil.isControllerAvailable(ControllerUtil.controller) && ControllerUtil.enableController) {
					RenderSystem.setShader(GameRenderer::getPositionTexShader);
					RenderSystem.setShaderTexture(0, CURSOR);
					renderCursor(event.getMatrixStack(), x,y, 5.0D);
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

			float newPitch = (cameraYAxis >= -1.0F && cameraYAxis < -0.1D) || (cameraYAxis <= 1.0F && cameraYAxis > 0.1D) ? (float) ((cameraYAxis * ControllerUtil.ingameSensitivity * moveModifier) + Minecraft.getInstance().player.getViewXRot((float) event.getRenderPartialTicks())) : Minecraft.getInstance().player.getViewXRot((float) event.getRenderPartialTicks());
			float newYaw = (cameraXAxis >= -1.0F && cameraXAxis < -0.1D) || (cameraXAxis <= 1.0F && cameraXAxis > 0.1D) ? (float) ((cameraXAxis * ControllerUtil.ingameSensitivity * moveModifier) + Minecraft.getInstance().player.getViewYRot((float) event.getRenderPartialTicks())) : Minecraft.getInstance().player.getViewYRot((float) event.getRenderPartialTicks());

			Minecraft.getInstance().player.xRot = newPitch;
			Minecraft.getInstance().player.yRot = newYaw;
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
			if(!Minecraft.getInstance().isWindowActive()) Minecraft.getInstance().setWindowActive(true);
			Controller controller = ControllerMod.getInstance().getActiveController();
			if (controller != null) {
				float scrollAxis = settings.controllerBindScroll.getAxis();
				float cameraXAxis = settings.controllerBindCameraHorizontal.getAxis();
				float cameraYAxis = settings.controllerBindCameraVertical.getAxis();
				float mouseXAxis = settings.controllerBindMouseHorizontal.getAxis();
				float mouseYAxis = settings.controllerBindMouseVertical.getAxis();
				//Inventory Keybinds
				if(getCurrentScreen() instanceof AbstractContainerScreen) {
					//					if(fromGame) {
					//						ControllerUtil.unpressAllKeys();
					//						fromGame = false;
					//					}
					ControllerUtil.handleContainerInput(controller, mouseXAxis, mouseYAxis, scrollAxis);
				}

				//Ingame Menu Keybinds
				if(getCurrentScreen() instanceof PauseScreen) {
					if(fromGame) {
						ControllerUtil.unpressAllKeys();
						fromGame = false;
					}
					ControllerUtil.handleScreenInput(controller, mouseXAxis, mouseYAxis, scrollAxis, true);
				}

				//Any other menu
				if(getCurrentScreen()!=null && !(getCurrentScreen() instanceof AbstractContainerScreen) && !(getCurrentScreen() instanceof PauseScreen)) {
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
					float moveXAxis = Minecraft.getInstance().player.isMovingSlowly() ? settings.controllerBindMoveHorizontal.getAxis() * 0.3F : settings.controllerBindMoveHorizontal.getAxis();
					float moveYAxis = Minecraft.getInstance().player.isMovingSlowly() ? settings.controllerBindMoveVertical.getAxis() * 0.3F : settings.controllerBindMoveVertical.getAxis();
					if (moveXAxis >= -1.0F && moveXAxis < -ControllerUtil.dead_zone) {
						event.getMovementInput().leftImpulse = -moveXAxis;
						event.getMovementInput().left = true;
					}
					if (moveXAxis <= 1.0F && moveXAxis > ControllerUtil.dead_zone) {
						event.getMovementInput().leftImpulse = -moveXAxis;
						event.getMovementInput().right = true;
					}
					if (moveYAxis >= -1.0F && moveYAxis < -ControllerUtil.dead_zone) {
						event.getMovementInput().forwardImpulse = -moveYAxis;
						event.getMovementInput().up = true;
					}
					if (moveYAxis <= 1.0F && moveYAxis > ControllerUtil.dead_zone) {
						event.getMovementInput().forwardImpulse = -moveYAxis;
						event.getMovementInput().down = true;
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	protected static void renderCursor(PoseStack poseStack, int x, int y, double size) {
		poseStack.pushPose();
//		TODO: RenderSystem.enableAlphaTest();
		RenderSystem.disableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, CURSOR);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(-size+x, size+y, -90.0F).uv(0.0F, 1.0F).endVertex();
		bufferbuilder.vertex(size+x, size+y, -90.0F).uv(1.0F, 1.0F).endVertex();
		bufferbuilder.vertex(size+x, -size+y, -90.0F).uv(1.0F, 0.0F).endVertex();
		bufferbuilder.vertex(-size+x, -size+y, -90.0F).uv(0.0F, 0.0F).endVertex();
		tessellator.end();
//		TODO: RenderSystem.disableAlphaTest();
		poseStack.popPose();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
	}
}
