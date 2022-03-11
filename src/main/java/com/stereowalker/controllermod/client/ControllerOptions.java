package com.stereowalker.controllermod.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;
import com.stereowalker.controllermod.client.controller.UseCase;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControllerOptions {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Splitter KEY_VALUE_SPLITTER = Splitter.on(':').limit(2);
	protected Minecraft mc;

	public boolean enableController = false;
	public int controllerNumber = 0;
	public ControllerModel controllerModel = ControllerModel.XBOX_360;

	public PaperDollOptions paperDoll = new PaperDollOptions();

	public List<Integer> negativeTriggerAxes = new ArrayList<Integer>();
	public List<Integer> positiveTriggerAxes = new ArrayList<Integer>();

	public String lastGUID = " "; 

	public static final String NEW = "key.categories.controller";
	public final ControllerMapping controllerBindBack = new ControllerMapping(NEW, "key.controller.back", Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_ESCAPE), (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button1");
		builder.put(ControllerModel.PS4, "button2");
	}, InputType.PRESS, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerBindPause = new ControllerMapping(NEW, "key.controller.pause", Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_ESCAPE), (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button7");
		builder.put(ControllerModel.PS4, "button9");
	}, InputType.PRESS, UseCase.INGAME);

	public final ControllerMapping controllerBindHotbarLeft = new ControllerMapping(NEW, "key.controller.hotbar_left", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button4");
		builder.put(ControllerModel.PS4, "button4");
	}, InputType.PRESS, UseCase.INGAME);

	public final ControllerMapping controllerBindHotbarRight = new ControllerMapping(NEW, "key.controller.hotbar_right",  (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button5");
		builder.put(ControllerModel.PS4, "button5");
	}, InputType.PRESS, UseCase.INGAME);

	public final ControllerMapping controllerBindKeyboard = new ControllerMapping(NEW, "key.controller.keyboard",  (builder) -> {
		builder.put(ControllerModel.PS4, "button13");
	}, InputType.PRESS, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerBindCameraHorizontal = new ControllerMapping(NEW, "key.controller.camera_horizontal", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis2");
		builder.put(ControllerModel.PS4, "axis2");
	}, false, UseCase.INGAME);

	public final ControllerMapping controllerBindCameraVertical = new ControllerMapping(NEW, "key.controller.camera_vertical", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis3");
		builder.put(ControllerModel.PS4, "axis5");
	}, false, UseCase.INGAME);

	public final ControllerMapping controllerBindMouseHorizontal = new ControllerMapping(NEW, "key.controller.mouse_horizontal", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis0");
		builder.put(ControllerModel.PS4, "axis0");
	}, false, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerBindMouseVertical = new ControllerMapping(NEW, "key.controller.mouse_vertical", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis1");
		builder.put(ControllerModel.PS4, "axis1");
	}, false, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerBindMoveHorizontal = new ControllerMapping(NEW, "key.controller.move_horizontal", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis0");
		builder.put(ControllerModel.PS4, "axis0");
	}, false, UseCase.INGAME);

	public final ControllerMapping controllerBindMoveVertical = new ControllerMapping(NEW, "key.controller.move_vertical", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis1");
		builder.put(ControllerModel.PS4, "axis1");
	}, false, UseCase.INGAME);

	public final ControllerMapping controllerBindScroll = new ControllerMapping(NEW, "key.controller.scroll", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis3");
		builder.put(ControllerModel.PS4, "axis5");
	}, false, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerKeyBindInventory = new ControllerMapping(Minecraft.getInstance().options.keyInventory, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button3");
		builder.put(ControllerModel.PS4, "button3");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindJump = new ControllerMapping(Minecraft.getInstance().options.keyJump, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button0");
		builder.put(ControllerModel.PS4, "button1");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindAttack = new ControllerMapping(Minecraft.getInstance().options.keyAttack, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_pos5");
		builder.put(ControllerModel.PS4, "button7");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindUseItem = new ControllerMapping(Minecraft.getInstance().options.keyUse, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_pos4");
		builder.put(ControllerModel.PS4, "button6");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindSneak = new ControllerMapping(Minecraft.getInstance().options.keyShift, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button9");
		builder.put(ControllerModel.PS4, "button11");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindTogglePerspective = new ControllerMapping(Minecraft.getInstance().options.keyTogglePerspective, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button8");
		builder.put(ControllerModel.PS4, "button10");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindDrop = new ControllerMapping(Minecraft.getInstance().options.keyDrop, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button1");
		builder.put(ControllerModel.PS4, "button2");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindForward = new ControllerMapping(Minecraft.getInstance().options.keyUp, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_neg1");
		builder.put(ControllerModel.PS4, "axis_neg1");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindBack = new ControllerMapping(Minecraft.getInstance().options.keyDown, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_pos1");
		builder.put(ControllerModel.PS4, "axis_pos1");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindLeft = new ControllerMapping(Minecraft.getInstance().options.keyLeft, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_neg0");
		builder.put(ControllerModel.PS4, "axis_neg0");
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindRight = new ControllerMapping(Minecraft.getInstance().options.keyRight, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_pos0");
		builder.put(ControllerModel.PS4, "axis_pos0");
	}, UseCase.INGAME);

	public ControllerMapping[] controllerBindings = ArrayUtils.addAll(new ControllerMapping[] {this.controllerBindBack, this.controllerBindPause, this.controllerBindHotbarLeft, this.controllerBindHotbarRight,
			this.controllerBindCameraHorizontal, this.controllerBindCameraVertical, this.controllerBindMouseHorizontal, this.controllerBindMouseVertical, this.controllerBindMoveHorizontal, this.controllerBindMoveVertical, this.controllerBindScroll},
			new ControllerMapping[] {this.controllerKeyBindInventory, this.controllerKeyBindJump, this.controllerKeyBindAttack, this.controllerKeyBindUseItem, this.controllerKeyBindSneak, this.controllerKeyBindTogglePerspective, this.controllerKeyBindDrop, this.controllerKeyBindForward, this.controllerKeyBindBack, this.controllerKeyBindLeft, this.controllerKeyBindRight});
	private final File optionsFile;

	public ControllerOptions(Minecraft mcIn, File mcDataDir) {
		this.mc = mcIn;
		this.optionsFile = new File(mcDataDir, "controller-options.txt");
	}

	public void setKeyBindingCode(ControllerModel model, ControllerMapping keyBindingIn, String inputIn) {
		keyBindingIn.bind(model, inputIn);
		this.saveOptions();
	}

	public void setKeyBindingInputType(ControllerModel model, ControllerMapping keyBindingIn, InputType inputIn) {
		keyBindingIn.setInputType(model, inputIn);
		this.saveOptions();
	}

	public void setKeyBindingInverted(ControllerModel model, ControllerMapping keyBindingIn, boolean inverted) {
		keyBindingIn.setAxisInverted(model, inverted);
		this.saveOptions();
	}

	/**
	 * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
	 */
	public void loadOptions() {
		try {
			if (!this.optionsFile.exists()) {
				return;
			}

			CompoundTag compoundnbt = new CompoundTag();

			try (BufferedReader bufferedreader = Files.newReader(this.optionsFile, Charsets.UTF_8)) {
				bufferedreader.lines().forEach((optionString) -> {
					try {
						Iterator<String> iterator = KEY_VALUE_SPLITTER.split(optionString).iterator();
						compoundnbt.putString(iterator.next(), iterator.next());
					} catch (Exception exception2) {
						LOGGER.warn("Skipping terrible option: {}", (Object)optionString);
					}

				});
			}

			CompoundTag compoundnbt1 = this.dataFix(compoundnbt);

			for(String s : compoundnbt1.getAllKeys()) {
				String s1 = compoundnbt1.getString(s);

				try {

					if ("lastGUID".equals(s)) {
						this.lastGUID = s1;
					}

					if ("enableController".equals(s)) {
						this.enableController = "true".equals(s1);
					}

					if ("controllerNumber".equals(s)) {
						this.controllerNumber = Integer.parseInt(s1);
					}

					if ("controllerModel".equals(s)) {
						this.controllerModel = "ps4".equals(s1) ? ControllerModel.PS4 : "xbox_360".equals(s1) ? ControllerModel.XBOX_360 : ControllerModel.CUSTOM;
					}

					this.paperDoll.readOptions(s, s1);

					if ("customControls_positiveTriggerAxes".equals(s)) {
						String[] pts = s1.split(",");
						List<Integer> positiveTriggerAxes = new ArrayList<Integer>();
						for (String pt: pts) {
							positiveTriggerAxes.add(Integer.parseInt(pt));
						}
						this.positiveTriggerAxes = positiveTriggerAxes;
					}

					if ("customControls_negativeTriggerAxes".equals(s)) {
						String[] pts = s1.split(",");
						List<Integer> negativeTriggerAxes = new ArrayList<Integer>();
						for (String pt: pts) {
							negativeTriggerAxes.add(Integer.parseInt(pt));
						}
						this.negativeTriggerAxes = negativeTriggerAxes;
					}

					for(ControllerModel model : ControllerModel.values()) {
						for(ControllerMapping keybinding : this.controllerBindings) {
							if (s.equals(model.getModelName() + "_binding_" + keybinding.getDescripti())) {
								String[] pts = s1.split(":");
								keybinding.bind(model, pts[0]);
								if (pts.length > 1) keybinding.setInputType(model, pts[1].equals("null") ? null : InputType.valueOf(pts[1]));
								if (pts.length > 2) keybinding.setAxisInverted(model, Boolean.parseBoolean(pts[2]));
							}
						}
					}
				} catch (Exception exception) {
					LOGGER.warn("Skipping horrid option: {}:{} with an {}", s, s1, exception);
				}
			}

			ControllerMapping.resetMapping();
		} catch (Exception exception1) {
			LOGGER.error("Failed to load options", (Throwable)exception1);
		}

	}

	private CompoundTag dataFix(CompoundTag nbt) {
		int i = 0;

		try {
			i = Integer.parseInt(nbt.getString("version"));
		} catch (RuntimeException runtimeexception) {
		}

		return NbtUtils.update(this.mc.getFixerUpper(), DataFixTypes.OPTIONS, nbt, i);
	}

	/**
	 * Saves the options to the options file.
	 */
	public void saveOptions() {
		LOGGER.info("Saving Controls");
		//      if (net.minecraftforge.fml.client.ClientModLoader.isLoading()) return; //Don't save settings before mods add keybindigns and the like to prevent them from being deleted.
		try (PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8))) {
			if (ControllerMod.CONFIG.debug) printwriter.println("lastGUID:"+this.lastGUID);
			printwriter.println("enableController:"+this.enableController);
			printwriter.println("controllerNumber:"+this.controllerNumber);
			printwriter.println("controllerModel:"+this.controllerModel.getModelName());
			String pos = "";
			if (this.positiveTriggerAxes.size() > 0) {
				pos = this.positiveTriggerAxes.get(0)+"";
				if (this.positiveTriggerAxes.size() > 1) {
					for (int i = 1; i < this.positiveTriggerAxes.size(); i++) {
						pos += ","+this.positiveTriggerAxes.get(i);
					}
				}
			}
			printwriter.println("customControls_positiveTriggerAxes:"+pos);
			String neg = "";
			if (this.negativeTriggerAxes.size() > 0) {
				neg = this.negativeTriggerAxes.get(0)+"";
				if (this.negativeTriggerAxes.size() > 1) {
					for (int i = 1; i < this.negativeTriggerAxes.size(); i++) {
						neg += ","+this.negativeTriggerAxes.get(i);
					}
				}
			}
			printwriter.println("customControls_negativeTriggerAxes:"+neg);
			this.paperDoll.writeOptions(printwriter);
			for(ControllerModel model : ControllerModel.values()) {
				for(ControllerMapping keybinding : this.controllerBindings) {
					printwriter.println(model.getModelName() + "_binding_" + keybinding.getDescripti() + ":" + keybinding.getButtonOnController(model) + ":" + keybinding.getInputType(model)+ ":" + keybinding.isAxisInverted(model));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Failed to save options", (Throwable)exception);
		}

	}
}
