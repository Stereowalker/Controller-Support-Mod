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
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;
import com.stereowalker.controllermod.client.controller.UseCase;
import com.stereowalker.controllermod.resources.ControllerModelManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;

@Environment(EnvType.CLIENT)
public class ControllerOptions {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Splitter KEY_VALUE_SPLITTER = Splitter.on(':').limit(2);
	protected Minecraft mc;

	public boolean enableController = false;
	public int controllerNumber = 0;
	public ControllerModel controllerModel = ControllerModel.XBOX_360_WINDOWS;

	public PaperDollOptions paperDoll = new PaperDollOptions();

	public List<Integer> negativeTriggerAxes = new ArrayList<Integer>();
	public List<Integer> positiveTriggerAxes = new ArrayList<Integer>();

	public String lastGUID = " "; 

	public static final String NEW = "key.categories.controller";
	public static final String ON_SCREEN_KEYBOARD = "key.categories.on_screen_keyboard";
	public static final String INVENTORY = "key.categories.inventory";
	
	private void collect(Map<ResourceLocation, List<String>> builder, List<String> alias, ResourceLocation... models) {
		for (ResourceLocation model : models) {
			builder.put(model, alias);
		}
	}
	public final ControllerMapping controllerBindBack = new ControllerMapping(NEW, "key.controller.back", Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_ESCAPE), (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_right"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerBindPause = new ControllerMapping(NEW, "key.controller.pause", Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_ESCAPE), (builder) -> {
		collect(builder, Lists.newArrayList("#start_button"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.INGAME);
	
	public final ControllerMapping controllerBindSplit = new ControllerMapping(INVENTORY, "key.controller.split", Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_RIGHT), (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_up"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.ANY_SCREEN);
	
	public final ControllerMapping controllerBindQuickMove = new ControllerMapping(INVENTORY, "key.controller.quickMove", Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_RIGHT), (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_left"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerBindHotbarLeft = new ControllerMapping(INVENTORY, "key.controller.hotbar_left", (builder) -> {
		collect(builder, Lists.newArrayList("#bumper_left"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.INGAME);

	public final ControllerMapping controllerBindHotbarRight = new ControllerMapping(INVENTORY, "key.controller.hotbar_right",  (builder) -> {
		collect(builder, Lists.newArrayList("#bumper_right"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.INGAME);

	public final ControllerMapping controllerBindKeyboard = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard",  (builder) -> {
		builder.put(ControllerModel.PS4_WINDOWS.defaultName, Lists.newArrayList("button13"));
		collect(builder, Lists.newArrayList("#select_button"), ControllerModel.XBOX_360_WINDOWS.defaultName, ControllerModel.XBOX_360_LINUX.defaultName, new ResourceLocation("controllermod:ps4_linux"));
	}, InputType.PRESS, UseCase.ANY_SCREEN);
	
	public final ControllerMapping controllerBindKeyboardBackspace = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_backspace",  (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_left"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardSelect = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_select",  (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_down"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardCaps = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_caps",  (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_up"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardSpace = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_space",  (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_right"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardUp = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_up",  (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_up"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardDown = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_down",  (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_down"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardLeft = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_left",  (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_left"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardRight = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_right",  (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_right"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardArrowLeft = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_arrow_left",  (builder) -> {
		collect(builder, Lists.newArrayList("#bumper_left"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);
	
	public final ControllerMapping controllerBindKeyboardArrowRight = new ControllerMapping(ON_SCREEN_KEYBOARD, "key.controller.keyboard_arrow_right",  (builder) -> {
		collect(builder, Lists.newArrayList("#bumper_right"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, InputType.PRESS, UseCase.KEYBOARD);

	public final ControllerMapping controllerBindCameraHorizontal = new ControllerMapping(NEW, "key.controller.camera_horizontal", (builder) -> {
		collect(builder, Lists.newArrayList("#right_stick_horizontal"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, false, UseCase.INGAME);

	public final ControllerMapping controllerBindCameraVertical = new ControllerMapping(NEW, "key.controller.camera_vertical", (builder) -> {
		collect(builder, Lists.newArrayList("#right_stick_vertical"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, false, UseCase.INGAME);

	public final ControllerMapping controllerBindMouseHorizontal = new ControllerMapping(NEW, "key.controller.mouse_horizontal", (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_horizontal"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, false, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerBindMouseVertical = new ControllerMapping(NEW, "key.controller.mouse_vertical", (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_vertical"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, false, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerBindMoveHorizontal = new ControllerMapping(NEW, "key.controller.move_horizontal", (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_horizontal"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, false, UseCase.INGAME);

	public final ControllerMapping controllerBindMoveVertical = new ControllerMapping(NEW, "key.controller.move_vertical", (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_vertical"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, false, UseCase.INGAME);

	public final ControllerMapping controllerBindScroll = new ControllerMapping(NEW, "key.controller.scroll", (builder) -> {
		collect(builder, Lists.newArrayList("#right_stick_vertical"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, false, UseCase.ANY_SCREEN);

	public final ControllerMapping controllerKeyBindForward = new ControllerMapping(Minecraft.getInstance().options.keyUp, "key.override.forward", (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_up"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);//TODO: Remove these default keybinds in a later update

	public final ControllerMapping controllerKeyBindBack = new ControllerMapping(Minecraft.getInstance().options.keyDown, "key.override.back", (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_down"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);//TODO: Remove these default keybinds in a later update

	public final ControllerMapping controllerKeyBindLeft = new ControllerMapping(Minecraft.getInstance().options.keyLeft, "key.override.left", (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_left"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);//TODO: Remove these default keybinds in a later update

	public final ControllerMapping controllerKeyBindRight = new ControllerMapping(Minecraft.getInstance().options.keyRight, "key.override.right", (builder) -> {
		collect(builder, Lists.newArrayList("#left_stick_right"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);//TODO: Remove these default keybinds in a later update

	public final ControllerMapping controllerKeyBindJump = new ControllerMapping(Minecraft.getInstance().options.keyJump, "key.override.jump", (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_down"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindSneak = new ControllerMapping(Minecraft.getInstance().options.keyShift, "key.override.sneak", (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_right"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);
	
	public final ControllerMapping controllerKeyBindSprint = new ControllerMapping(Minecraft.getInstance().options.keySprint, "key.override.sprint", (builder) -> {
		collect(builder, Lists.newArrayList("#stick_left"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindInventory = new ControllerMapping(Minecraft.getInstance().options.keyInventory, "key.override.inventory", (builder) -> {
		collect(builder, Lists.newArrayList("#face_button_up"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindDrop = new ControllerMapping(Minecraft.getInstance().options.keyDrop, "key.override.drop", (builder) -> {
		collect(builder, Lists.newArrayList("#dpad_down"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindUseItem = new ControllerMapping(Minecraft.getInstance().options.keyUse, "key.override.use", (builder) -> {
		collect(builder, Lists.newArrayList("#left_trigger"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindAttack = new ControllerMapping(Minecraft.getInstance().options.keyAttack, "key.override.attack", (builder) -> {
		collect(builder, Lists.newArrayList("#right_trigger"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindChat = new ControllerMapping(Minecraft.getInstance().options.keyChat, "key.override.chat", (builder) -> {
		collect(builder, Lists.newArrayList("#dpad_right"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);

	public final ControllerMapping controllerKeyBindTogglePerspective = new ControllerMapping(Minecraft.getInstance().options.keyTogglePerspective, "key.override.togglePerspective", (builder) -> {
		collect(builder, Lists.newArrayList("#dpad_up"), Lists.newArrayList(ControllerModelManager.ALL_MODELS.keySet()).toArray(new ResourceLocation[0]));
	}, UseCase.INGAME);

	public ControllerMapping[] controllerBindings = ArrayUtils.addAll(new ControllerMapping[] {this.controllerBindBack, this.controllerBindPause, this.controllerBindHotbarLeft, this.controllerBindHotbarRight, this.controllerBindSplit, this.controllerBindQuickMove,
			this.controllerBindCameraHorizontal, this.controllerBindCameraVertical, this.controllerBindMouseHorizontal, this.controllerBindMouseVertical, this.controllerBindMoveHorizontal, this.controllerBindMoveVertical, this.controllerBindScroll},
			new ControllerMapping[] {this.controllerKeyBindInventory, this.controllerKeyBindJump, this.controllerKeyBindAttack, this.controllerKeyBindUseItem, this.controllerKeyBindChat, this.controllerKeyBindTogglePerspective, this.controllerKeyBindDrop, 
					this.controllerKeyBindForward, this.controllerKeyBindBack, this.controllerKeyBindLeft, this.controllerKeyBindRight, this.controllerKeyBindSprint, this.controllerKeyBindSneak, //Movement
					this.controllerBindKeyboard, this.controllerBindKeyboardArrowLeft, this.controllerBindKeyboardArrowRight, this.controllerBindKeyboardBackspace,
					this.controllerBindKeyboardCaps, this.controllerBindKeyboardUp, this.controllerBindKeyboardDown, this.controllerBindKeyboardLeft, this.controllerBindKeyboardRight, 
					this.controllerBindKeyboardSelect, this.controllerBindKeyboardSpace});
	private final File optionsFile;

	public ControllerOptions(Minecraft mcIn, File mcDataDir) {
		this.mc = mcIn;
		this.optionsFile = new File(mcDataDir, "controller-options.txt");
	}
	
	public ControllerOptions(ControllerOptions old) {
		this.mc = old.mc;
		this.optionsFile = old.optionsFile;
	}

	public void setKeyBindingCode(ControllerModel model, ControllerMapping keyBindingIn, List<String> inputIn) {
		keyBindingIn.bind(model.getKey(), inputIn);
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
						this.controllerModel = ControllerModelManager.ALL_MODELS.get(new ResourceLocation(s1));
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

					for(ResourceLocation key : ControllerModelManager.ALL_MODELS.keySet()) {
						ControllerModel model = ControllerModelManager.ALL_MODELS.get(key);
						for(ControllerMapping keybinding : this.controllerBindings) {
							if (s.equals(model.getModelName() + "_binding_" + keybinding.getDescripti())) {
								String[] pts = s1.split(":");
								keybinding.bind(key, Lists.newArrayList(pts[0].split(";")));
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
			ControllerModelManager.ALL_MODELS.forEach((key, val) -> {
				if (val == this.controllerModel)
					printwriter.println("controllerModel:"+key);
			});
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
			for(ControllerModel model : ControllerModelManager.ALL_MODELS.values()) {
				for(ControllerMapping keybinding : this.controllerBindings) {
					if (keybinding.getButtonOnController(model) == null) {
						LOGGER.warn("Skipping binding {} because it's button on controller returned null", keybinding.getDescripti());
					}
					String buttons = "";
					for (int i = 0; i < keybinding.getButtonOnController(model).size(); i++) {
						buttons += keybinding.getButtonOnController(model).get(i) + (i==keybinding.getButtonOnController(model).size()-1?"":";");
					}
					printwriter.println(model.getModelName() + "_binding_" + keybinding.getDescripti() + ":" + buttons + ":" + keybinding.getInputType(model)+ ":" + keybinding.isAxisInverted(model));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Failed to save options", (Throwable)exception);
		}
	}
}
