package com.stereowalker.controllermod.client;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;

public class ButtonHints {


	@SuppressWarnings("resource")
	public static void render(GuiGraphics guiGraphics) {
		ControllerModel model = ControllerMod.getInstance().getActiveController().getModel();
		ControllerMapping map1 = ControllerMod.getInstance().controllerOptions.controllerKeyBindInventory;
		ControllerMapping map2 = ControllerMod.getInstance().controllerOptions.controllerKeyBindAttack;
		String desc1 = displayDescription(map1, model);
		String desc2 = displayDescription(map2, model);
		//Left 1
		if (!desc1.isEmpty()) {
			int x1 = ControllerMod.getSafeArea() - 2;
			int y1 = guiGraphics.guiHeight() - 12;
			ResourceLocation icon = model.getOrCreate(map1.getButtonOnController(model))[0].getIcon();
			guiGraphics.blit(icon, x1, y1 - 10, 0, 0, 20, 20, 20, 20);
			guiGraphics.drawString(Minecraft.getInstance().font, desc1, x1 + 20, y1 - 3, 0xffffff, true);

		}
		//Left 2
		if (!desc2.isEmpty()) {
			int x1 = guiGraphics.guiWidth() - 62;
			int y1 = guiGraphics.guiHeight() - 12;
			ResourceLocation icon = model.getOrCreate(map2.getButtonOnController(model))[0].getIcon();
			guiGraphics.blit(icon, x1, y1 - 10, 0, 0, 20, 20, 20, 20);
			guiGraphics.drawString(Minecraft.getInstance().font, desc2, x1 + 20, y1 - 3, 0xffffff, true);

		}
	}
	
	public static String displayDescription(ControllerMapping mapping, ControllerModel model) {
		if (mapping == null || !mapping.isBoundToButton(model))
			return "";
		if (mapping == ControllerMod.getInstance().controllerOptions.controllerKeyBindAttack) {
			if (Minecraft.getInstance().crosshairPickEntity != null) {
				return I18n.get(mapping.getDescripti()).split("/")[0];
			} else {
			      Entity entity = Minecraft.getInstance().getCameraEntity();
			      HitResult block = entity.pick(20.0D, 0.0F, false);
			      if (block.getType() == HitResult.Type.BLOCK) {
						return I18n.get(mapping.getDescripti()).split("/")[1];
			      } else {
			    	  return "";
			      }
			}
		}
		return I18n.get(mapping.getDescripti());
	}
}
