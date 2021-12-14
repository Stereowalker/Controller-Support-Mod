package com.stereowalker.controllermod.client.gui.screen;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.PaperDollOptions.DollType;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaperDollOptionsScreen extends Screen {
	private final Screen previousScreen;
	private ControllerOptions settings;

	public PaperDollOptionsScreen(Screen p_i51123_1_) {
		super(new TranslatableComponent("options.paper_doll.title"));
		this.previousScreen = p_i51123_1_;
		this.settings = ControllerMod.getInstance().controllerSettings;
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
		this.addRenderableWidget(new Button(this.width / 2 - 100, this.height  / 6 + 168, 200, 20, new TranslatableComponent("gui.done"), (p_212984_1_) -> {
			this.minecraft.setScreen(this.previousScreen);
		}));
	}
	
	public void addOption(int index, DollType getter) {
		boolean left = index%2 == 0;
		int position = index / 2;
		this.addRenderableWidget(new Button(this.width / 2 +(left?-155:5), this.height  / 6 + (24*position), 150, 20, getter.getDisplayText().append(" : ").append(CommonComponents.optionStatus(settings.paperDoll.show.get(getter))), (p_212984_1_) -> {
			settings.paperDoll.show.put(getter, !settings.paperDoll.show.get(getter));
			this.minecraft.setScreen(new PaperDollOptionsScreen(previousScreen));
		}));
	}
	
	public void addOption(int index, boolean getter, Consumer<Boolean> setter, MutableComponent text) {
		boolean left = index%2 == 0;
		int position = index / 2;
		this.addRenderableWidget(new Button(this.width / 2 +(left?-155:5), this.height  / 6 + (24*position), 150, 20, text.append(" : ").append(CommonComponents.optionStatus(getter)), (p_212984_1_) -> {
			setter.accept(!getter);
			this.minecraft.setScreen(new PaperDollOptionsScreen(previousScreen));
		}));
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