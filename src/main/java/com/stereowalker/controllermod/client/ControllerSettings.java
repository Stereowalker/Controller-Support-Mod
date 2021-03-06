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
import com.stereowalker.controllermod.client.controller.ControllerBinding;
import com.stereowalker.controllermod.client.controller.ControllerConflictContext;
import com.stereowalker.controllermod.client.controller.ControllerMap.ControllerModel;
import com.stereowalker.controllermod.client.controller.ControllerUtil.InputType;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;

@OnlyIn(Dist.CLIENT)
public class ControllerSettings {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Splitter KEY_VALUE_SPLITTER = Splitter.on(':').limit(2);
	protected Minecraft mc;

	public boolean useAxisToMove = true;

	public PaperDollSettings paperDoll = new PaperDollSettings();

	public List<Integer> negativeTriggerAxes = new ArrayList<Integer>();
	public List<Integer> positiveTriggerAxes = new ArrayList<Integer>();
	
	public String lastGUID = " "; 

	public static final String NEW = "key.categories.controller";
	public final ControllerBinding controllerBindBack = new ControllerBinding(NEW, "key.controller.back", Type.KEYSYM, GLFW.GLFW_KEY_ESCAPE, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button1");
		builder.put(ControllerModel.PS4, "button2");
	}, InputType.PRESS, ControllerConflictContext.GUI);

	public final ControllerBinding controllerBindPause = new ControllerBinding(NEW, "key.controller.pause", Type.KEYSYM, GLFW.GLFW_KEY_ESCAPE, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button7");
		builder.put(ControllerModel.PS4, "button9");
	}, InputType.PRESS, ControllerConflictContext.IN_GAME);

	public final ControllerBinding controllerBindHotbarLeft = new ControllerBinding(NEW, "key.controller.hotbar_left", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button4");
		builder.put(ControllerModel.PS4, "button4");
	}, InputType.PRESS, ControllerConflictContext.IN_GAME);

	public final ControllerBinding controllerBindHotbarRight = new ControllerBinding(NEW, "key.controller.hotbar_right",  (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button5");
		builder.put(ControllerModel.PS4, "button5");
	}, InputType.PRESS, ControllerConflictContext.IN_GAME);

	public final ControllerBinding controllerBindCameraHorizontal = new ControllerBinding(NEW, "key.controller.camera_horizontal", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis2");
		builder.put(ControllerModel.PS4, "axis2");
	}, false, KeyConflictContext.IN_GAME);

	public final ControllerBinding controllerBindCameraVertical = new ControllerBinding(NEW, "key.controller.camera_vertical", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis3");
		builder.put(ControllerModel.PS4, "axis5");
	}, false, KeyConflictContext.IN_GAME);

	public final ControllerBinding controllerBindMouseHorizontal = new ControllerBinding(NEW, "key.controller.mouse_horizontal", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis0");
		builder.put(ControllerModel.PS4, "axis0");
	}, false, KeyConflictContext.GUI);

	public final ControllerBinding controllerBindMouseVertical = new ControllerBinding(NEW, "key.controller.mouse_vertical", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis1");
		builder.put(ControllerModel.PS4, "axis1");
	}, false, KeyConflictContext.GUI);

	public final ControllerBinding controllerBindMoveHorizontal = new ControllerBinding(NEW, "key.controller.move_horizontal", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis0");
		builder.put(ControllerModel.PS4, "axis0");
	}, false, KeyConflictContext.IN_GAME);

	public final ControllerBinding controllerBindMoveVertical = new ControllerBinding(NEW, "key.controller.move_vertical", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis1");
		builder.put(ControllerModel.PS4, "axis1");
	}, false, KeyConflictContext.IN_GAME);

	public final ControllerBinding controllerBindScroll = new ControllerBinding(NEW, "key.controller.scroll", (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis3");
		builder.put(ControllerModel.PS4, "axis5");
	}, false, KeyConflictContext.GUI);

	public final ControllerBinding controllerKeyBindInventory = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindInventory, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button3");
		builder.put(ControllerModel.PS4, "button3");
	});

	public final ControllerBinding controllerKeyBindJump = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindJump, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button0");
		builder.put(ControllerModel.PS4, "button1");
	});

	public final ControllerBinding controllerKeyBindAttack = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindAttack, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_pos5");
		builder.put(ControllerModel.PS4, "button7");
	});

	public final ControllerBinding controllerKeyBindUseItem = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindUseItem, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_pos4");
		builder.put(ControllerModel.PS4, "button6");
	});

	public final ControllerBinding controllerKeyBindSneak = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindSneak, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button9");
		builder.put(ControllerModel.PS4, "button11");
	});

	public final ControllerBinding controllerKeyBindTogglePerspective = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindTogglePerspective, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button8");
		builder.put(ControllerModel.PS4, "button10");
	});

	public final ControllerBinding controllerKeyBindDrop = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindDrop, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "button1");
		builder.put(ControllerModel.PS4, "button2");
	});

	public final ControllerBinding controllerKeyBindForward = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindForward, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_neg1");
		builder.put(ControllerModel.PS4, "axis_neg1");
	});

	public final ControllerBinding controllerKeyBindBack = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindBack, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_pos1");
		builder.put(ControllerModel.PS4, "axis_pos1");
	});

	public final ControllerBinding controllerKeyBindLeft = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindLeft, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_neg0");
		builder.put(ControllerModel.PS4, "axis_neg0");
	});

	public final ControllerBinding controllerKeyBindRight = new ControllerBinding(Minecraft.getInstance().gameSettings.keyBindRight, (builder) -> {
		builder.put(ControllerModel.XBOX_360, "axis_pos0");
		builder.put(ControllerModel.PS4, "axis_pos0");
	});

	public ControllerBinding[] controllerBindings = ArrayUtils.addAll(new ControllerBinding[] {this.controllerBindBack, this.controllerBindPause, this.controllerBindHotbarLeft, this.controllerBindHotbarRight,
			this.controllerBindCameraHorizontal, this.controllerBindCameraVertical, this.controllerBindMouseHorizontal, this.controllerBindMouseVertical, this.controllerBindMoveHorizontal, this.controllerBindMoveVertical, this.controllerBindScroll},
			new ControllerBinding[] {this.controllerKeyBindInventory, this.controllerKeyBindJump, this.controllerKeyBindAttack, this.controllerKeyBindUseItem, this.controllerKeyBindSneak, this.controllerKeyBindTogglePerspective, this.controllerKeyBindDrop, this.controllerKeyBindForward, this.controllerKeyBindBack, this.controllerKeyBindLeft, this.controllerKeyBindRight});
	private final File optionsFile;

	public ControllerSettings(Minecraft mcIn, File mcDataDir) {
		this.mc = mcIn;
		this.optionsFile = new File(mcDataDir, "controller-options.txt");

		this.loadOptions();
	}

	public void setKeyBindingCode(ControllerModel model, ControllerBinding keyBindingIn, String inputIn) {
		keyBindingIn.bind(model, inputIn);
		this.saveOptions();
	}

	public void setKeyBindingInputType(ControllerModel model, ControllerBinding keyBindingIn, InputType inputIn) {
		keyBindingIn.setInputType(model, inputIn);
		this.saveOptions();
	}

	public void setKeyBindingInverted(ControllerModel model, ControllerBinding keyBindingIn, boolean inverted) {
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

			CompoundNBT compoundnbt = new CompoundNBT();

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

			CompoundNBT compoundnbt1 = this.dataFix(compoundnbt);

			for(String s : compoundnbt1.keySet()) {
				String s1 = compoundnbt1.getString(s);

				try {
					
					if ("lastGUID".equals(s)) {
						this.lastGUID = s1;
					}

					if ("useAxisToMove".equals(s)) {
						this.useAxisToMove = "true".equals(s1);
					}

					if ("paperDoll_showSwimming".equals(s)) {
						this.paperDoll.showSwimming = "true".equals(s1);
					}

					if ("paperDoll_showCrawling".equals(s)) {
						this.paperDoll.showCrawling = "true".equals(s1);
					}
					
					if ("paperDoll_showSprinting".equals(s)) {
						this.paperDoll.showSprinting = "true".equals(s1);
					}
					
					if ("paperDoll_showCrouching".equals(s)) {
						this.paperDoll.showCrouching = "true".equals(s1);
					}

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
						for(ControllerBinding keybinding : this.controllerBindings) {
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

			//         ControllerBinding.resetKeyBindingArrayAndHash();
		} catch (Exception exception1) {
			LOGGER.error("Failed to load options", (Throwable)exception1);
		}

	}

	private CompoundNBT dataFix(CompoundNBT nbt) {
		int i = 0;

		try {
			i = Integer.parseInt(nbt.getString("version"));
		} catch (RuntimeException runtimeexception) {
		}

		return NBTUtil.update(this.mc.getDataFixer(), DefaultTypeReferences.OPTIONS, nbt, i);
	}

	/**
	 * Saves the options to the options file.
	 */
	public void saveOptions() {
		LOGGER.info("Saving Controls");
		//      if (net.minecraftforge.fml.client.ClientModLoader.isLoading()) return; //Don't save settings before mods add keybindigns and the like to prevent them from being deleted.
		try (PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8))) {
			if (Config.debug_mode.get()) printwriter.println("lastGUID:"+this.lastGUID);
			printwriter.println("useAxisToMove:"+this.useAxisToMove);
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
			printwriter.println("paperDoll_showSwimming:"+this.paperDoll.showSwimming);
			printwriter.println("paperDoll_showCrawling:"+this.paperDoll.showCrawling);
			printwriter.println("paperDoll_showSprinting:"+this.paperDoll.showSprinting);
			printwriter.println("paperDoll_showCrouching:"+this.paperDoll.showCrouching);
			for(ControllerModel model : ControllerModel.values()) {
				for(ControllerBinding keybinding : this.controllerBindings) {
					printwriter.println(model.getModelName() + "_binding_" + keybinding.getDescripti() + ":" + keybinding.getButtonOnController(model) + ":" + keybinding.getInputType(model)+ ":" + keybinding.isAxisInverted(model));
				}
			}
		} catch (Exception exception) {
			LOGGER.error("Failed to save options", (Throwable)exception);
		}

	}
}
