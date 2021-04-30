package com.stereowalker.controllermod.client.gui.screen;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerSettings;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

@OnlyIn(Dist.CLIENT)
public class PaperDollOptionsScreen extends Screen {
	private final Screen previousScreen;
	private ControllerSettings settings;

	public PaperDollOptionsScreen(Screen p_i51123_1_) {
		super(new TranslationTextComponent("options.paper_doll.title"));
		this.previousScreen = p_i51123_1_;
		this.settings = ControllerMod.getInstance().controllerSettings;
	}

	@Override
	public void init() {
		addOption(true, 0, Config.isAttacking);
		addOption(false, 0, Config.isBurning);
		addOption(true, 1, settings.paperDoll.showCrawling, (get) -> settings.paperDoll.showCrawling = get, new TranslationTextComponent("gui.paper_doll.crawling"));
		addOption(false, 1, settings.paperDoll.showCrouching, (get) -> settings.paperDoll.showCrouching = get, new TranslationTextComponent("gui.paper_doll.crouching"));
		addOption(true, 2, Config.isElytraFlying);
		addOption(false, 2, Config.isFlying);
		addOption(true, 3, Config.isHurt);
		addOption(false, 3, Config.isJumping);
		addOption(true, 4, Config.isMoving);
		addOption(false, 4, Config.isRiding);
		addOption(true, 5, Config.isSpinning);
		addOption(false, 5, settings.paperDoll.showSwimming, (get) -> settings.paperDoll.showSwimming = get, new TranslationTextComponent("gui.paper_doll.sprinting"));
		addOption(true, 6, settings.paperDoll.showSwimming, (get) -> settings.paperDoll.showSwimming = get, new TranslationTextComponent("gui.paper_doll.swimming"));
		addOption(false, 6, Config.isUsing);
		this.addButton(new Button(this.width / 2 - 100, this.height  / 6 + 168, 200, 20, new TranslationTextComponent("gui.done"), (p_212984_1_) -> {
			this.minecraft.displayGuiScreen(this.previousScreen);
		}));
	}
	
	public void addOption(boolean left, int index, BooleanValue value) {
		this.addButton(new Button(this.width / 2 +(left?-155:5), this.height  / 6 + (24*index), 150, 20, new TranslationTextComponent(value.getPath().get(1)).appendString(" : ").appendSibling(DialogTexts.optionsEnabled(value.get())), (p_212984_1_) -> {
			value.set(!value.get());
			this.minecraft.displayGuiScreen(new PaperDollOptionsScreen(previousScreen));
		}));
	}
	
	public void addOption(boolean left, int index, boolean getter, Consumer<Boolean> setter, IFormattableTextComponent text) {
		this.addButton(new Button(this.width / 2 +(left?-155:5), this.height  / 6 + (24*index), 150, 20, text.appendString(" : ").appendSibling(DialogTexts.optionsEnabled(getter)), (p_212984_1_) -> {
			setter.accept(!getter);
			this.minecraft.displayGuiScreen(new PaperDollOptionsScreen(previousScreen));
		}));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		AbstractGui.drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void onClose() {
		settings.saveOptions();
	}
}