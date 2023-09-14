package com.stereowalker.controllermod.client.gui.screen;

import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.gui.widget.list.TriggerAxesList;
import com.stereowalker.unionlib.api.gui.GuiRenderer;
import com.stereowalker.unionlib.client.gui.screens.DefaultScreen;
import com.stereowalker.unionlib.util.ScreenHelper;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class TriggerSetupScreen extends DefaultScreen {
	private TriggerAxesList triggerAxesList;
	private ControllerMod mod;

	public TriggerSetupScreen(Screen previousScreen) {
		super(Component.translatable("trigger_setup.title"), previousScreen);
		this.mod = ControllerMod.getInstance();
	}

	@Override
	public void init() {
		this.triggerAxesList = new TriggerAxesList(this, this.minecraft, ControllerMod.getInstance());
		this.addWidget(this.triggerAxesList);
		this.addRenderableWidget(ScreenHelper.buttonBuilder(CommonComponents.GUI_DONE, (p_213124_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}).bounds(this.width / 2 - 155 + 210, this.height - 29, 100, 20).build());
	}

	@Override
	public void removed() {
		mod.controllerOptions.saveOptions();
	}

	@Override
	public void drawOnScreen(GuiRenderer guiRenderer, int mouseX, int mouseY, float partialTicks) {
		guiRenderer.renderSelectionList(triggerAxesList, mouseX, mouseY, partialTicks);
	}
}