package com.stereowalker.controllermod.client.events;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
public class ControllerBindingEvents {
	static Minecraft mc = Minecraft.getInstance();
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void keybindTick(ClientTickEvent playerEvent) {
		if(mc.screen==null) {
			if (ControllerMod.getInstance().controllerSettings.controllerBindHotbarLeft.isDown(Config.controllerModel.get())) {
				ControllerUtil.virtualmouse.scrollCallback(Minecraft.getInstance().getWindow().getWindow(), 0.0D, 1.0D);
			}
			if (ControllerMod.getInstance().controllerSettings.controllerBindHotbarRight.isDown(Config.controllerModel.get())) {
				ControllerUtil.virtualmouse.scrollCallback(Minecraft.getInstance().getWindow().getWindow(), 0.0D, -1.0D);
			}
			
		}
	}
}
