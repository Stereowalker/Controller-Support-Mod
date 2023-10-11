package com.stereowalker.controllermod;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.stereowalker.controllermod.client.ControllerHandler;
import com.stereowalker.controllermod.client.OnScreenKeyboard;
import com.stereowalker.controllermod.client.PaperDollOptions;
import com.stereowalker.controllermod.client.controller.Controller;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.controller.ControllerUtil.ListeningMode;
import com.stereowalker.unionlib.api.collectors.InsertCollector;
import com.stereowalker.unionlib.api.collectors.OverlayCollector;
import com.stereowalker.unionlib.api.collectors.OverlayCollector.Order;
import com.stereowalker.unionlib.client.gui.screens.config.ConfigScreen;
import com.stereowalker.unionlib.insert.ClientInserts;
import com.stereowalker.unionlib.mod.ClientSegment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class ControllerSupportClientSegment extends ClientSegment {

	@Override
	public Screen getConfigScreen(Minecraft mc, Screen previousScreen) {
		return new ConfigScreen(previousScreen, ControllerMod.CONFIG);
	}
	
	@Override
	public ResourceLocation getModIcon() {
		return new ResourceLocation(ControllerMod.MOD_ID, "textures/gui/controller_icon2.png");
	}

	@Override
	public void initClientAfterMinecraft(Minecraft mc) {
		ControllerMod.LOGGER.info("Setting up all connected controlllers");
		ControllerMod.instance.controllerHandler = new ControllerHandler(ControllerMod.instance, mc);
		ControllerMod.instance.controllerHandler.setup(mc.getWindow().getWindow());
		ControllerMod.instance.onScreenKeyboard = new OnScreenKeyboard(mc);
	}
	
	@Override
	public void setupGuiOverlays(OverlayCollector collector) {
		collector.register("paperdoll", Order.END, (gui,renderer,width,height)->{
			PaperDollOptions.renderPlayerDoll(renderer, width, height);
		});
	}
	
	@Override
	public void registerInserts(InsertCollector collector) {
		collector.addInsert(ClientInserts.SCREEN_RENDER_FINISH, (screen,renderer,mousePos)->{
			if (!(screen instanceof LevelLoadingScreen)) {
				int x = (int)(ControllerUtil.virtualmouse.xpos() * (double)Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getWidth());
				int y = (int)(ControllerUtil.virtualmouse.ypos() * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getHeight());
				if(ControllerUtil.isControllerAvailable(ControllerMod.getInstance().controllerOptions.controllerNumber) && ControllerMod.getInstance().controllerOptions.enableController) {
					if (ControllerUtil.listeningMode == ListeningMode.KEYBOARD) {
						ControllerMod.getInstance().onScreenKeyboard.drawKeyboard(renderer, Minecraft.getInstance().font, x, y);
					} else {
						renderPonter(x, y, 8.0D);
						if (ControllerMod.CONFIG.debugButtons) {
							Controller controller = ControllerMod.getInstance().getActiveController();
							List<String> downs = controller.getButtonsDown();
							if (!downs.isEmpty()) {
								for (int i = 0; i < Math.min(2, downs.size()); i++) {
									ResourceLocation icon = controller.getModel().getOrCreate(downs)[i].getIcon();
									if (icon != null)
										renderer.blit(icon, x + 2 + (i * 20), y + 2, 0, 0, 20, 20, 20, 20);
								}
							}
						}
					}
				} 
			}
		});
	}

	private static void renderPonter(int x, int y, double size) {
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.defaultBlendFunc();

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, ControllerMod.Locations.CURSOR);
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(-size+x, size+y, -90.0F).uv(0.0F, 1.0F).endVertex();
		bufferbuilder.vertex(size+x, size+y, -90.0F).uv(1.0F, 1.0F).endVertex();
		bufferbuilder.vertex(size+x, -size+y, -90.0F).uv(1.0F, 0.0F).endVertex();
		bufferbuilder.vertex(-size+x, -size+y, -90.0F).uv(0.0F, 0.0F).endVertex();
		tessellator.end();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.defaultBlendFunc();
	}
}
