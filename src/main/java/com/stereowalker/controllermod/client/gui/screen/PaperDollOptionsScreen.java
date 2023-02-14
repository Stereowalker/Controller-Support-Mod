package com.stereowalker.controllermod.client.gui.screen;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.PaperDollOptions.DollType;
import com.stereowalker.unionlib.util.ScreenHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@Environment(EnvType.CLIENT)
public class PaperDollOptionsScreen extends Screen {
	private final Screen previousScreen;
	private ControllerOptions settings;

	public PaperDollOptionsScreen(Screen p_i51123_1_) {
		super(Component.translatable("options.paper_doll.title"));
		this.previousScreen = p_i51123_1_;
		this.settings = ControllerMod.getInstance().controllerOptions;
	}

	@Override
	public void init() {
		int i = 0;
		for (DollType dollType : DollType.values()) {
			if (dollType.showInMenu()) {
				addOption(i, dollType);
				i++;
			}
		}
		this.addRenderableWidget(ScreenHelper.buttonBuilder(Component.translatable("gui.done"), (p_212984_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}).bounds(this.width / 2 - 100, this.height  / 6 + 168, 200, 20).build());
	}
	
	public void addOption(int index, DollType getter) {
		boolean left = index%2 == 0;
		int position = index / 2;
		this.addRenderableWidget(ScreenHelper.buttonBuilder(getter.getDisplayText().append(" : ").append(CommonComponents.optionStatus(settings.paperDoll.show.get(getter))), (p_212984_1_) -> {
			settings.paperDoll.show.put(getter, !settings.paperDoll.show.get(getter));
			this.minecraft.setScreen(new PaperDollOptionsScreen(previousScreen));
		}).bounds(this.width / 2 +(left?-155:5), this.height  / 6 + (24*position), 150, 20).build());
	}
	
	public void addOption(int index, boolean getter, Consumer<Boolean> setter, MutableComponent text) {
		boolean left = index%2 == 0;
		int position = index / 2;
		this.addRenderableWidget(ScreenHelper.buttonBuilder(text.append(" : ").append(CommonComponents.optionStatus(getter)), (p_212984_1_) -> {
			setter.accept(!getter);
			this.minecraft.setScreen(new PaperDollOptionsScreen(previousScreen));
		}).bounds(this.width / 2 +(left?-155:5), this.height  / 6 + (24*position), 150, 20).build());
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		GuiComponent.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void removed() {
		settings.saveOptions();
	}
}