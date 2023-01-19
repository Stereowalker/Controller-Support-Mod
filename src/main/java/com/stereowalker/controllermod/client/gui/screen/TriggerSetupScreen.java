package com.stereowalker.controllermod.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.gui.widget.list.TriggerAxesList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class TriggerSetupScreen extends Screen {
	private final Screen previousScreen;
	private TriggerAxesList triggerAxesList;
	private ControllerMod mod;

	public TriggerSetupScreen(Screen previousScreen) {
		super(Component.translatable("trigger_setup.title"));
		this.previousScreen = previousScreen;
		this.mod = ControllerMod.getInstance();
	}

	@Override
	public void init() {
		this.triggerAxesList = new TriggerAxesList(this, this.minecraft, ControllerMod.getInstance());
		this.addWidget(this.triggerAxesList);
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_213124_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}).bounds(this.width / 2 - 155 + 210, this.height - 29, 100, 20).build());
	}

	@Override
	public void removed() {
		mod.controllerOptions.saveOptions();
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		this.triggerAxesList.render(matrixStack, mouseX, mouseY, partialTicks);
		GuiComponent.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}