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
	public void initialize() {
		this.triggerAxesList = new TriggerAxesList(this, this.minecraft, ControllerMod.getInstance());
		this.addWidget(this.triggerAxesList);
	}

	@Override
	public void removed() {
		mod.controllerOptions.saveOptions();
	}

	@Override
	public void drawOnScreen(GuiRenderer guiRenderer, int mouseX, int mouseY) {
		guiRenderer.renderSelectionList(triggerAxesList, mouseX, mouseY);
	}
}