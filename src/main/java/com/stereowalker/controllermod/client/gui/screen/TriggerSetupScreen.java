package com.stereowalker.controllermod.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.gui.widget.list.TriggerAxesList;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TriggerSetupScreen extends Screen {
	private final Screen previousScreen;
	private TriggerAxesList triggerAxesList;
	private ControllerMod mod;

	public TriggerSetupScreen(Screen previousScreen) {
		super(new TranslationTextComponent("trigger_setup.title"));
		this.previousScreen = previousScreen;
		this.mod = ControllerMod.getInstance();
	}

	@Override
	public void init() {
		this.triggerAxesList = new TriggerAxesList(this, this.minecraft, ControllerMod.getInstance());
		this.children.add(this.triggerAxesList);
		this.addButton(new Button(this.width / 2 - 155 + 210, this.height - 29, 100, 20, DialogTexts.GUI_DONE, (p_213124_1_) -> {
			this.minecraft.displayGuiScreen(this.previousScreen);
		}));
	}

	@Override
	public void onClose() {
		mod.controllerSettings.saveOptions();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		this.triggerAxesList.render(matrixStack, mouseX, mouseY, partialTicks);
		AbstractGui.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}