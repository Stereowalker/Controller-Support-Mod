package com.stereowalker.controllermod.resources;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stereowalker.controllermod.ControllerMod;
import com.stereowalker.controllermod.client.ControllerOptions;
import com.stereowalker.controllermod.client.controller.ControllerModel;

import net.minecraft.Util.OS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ControllerModelManager extends SimplePreparableReloadListener<Map<ResourceLocation,ControllerModel>> {
	public static final Map<ResourceLocation,ControllerModel> ALL_MODELS = new HashMap<ResourceLocation,ControllerModel>();

	@Override
	protected Map<ResourceLocation,ControllerModel> prepare(ResourceManager manager, ProfilerFiller var2) {
		Map<ResourceLocation,ControllerModel> models = new HashMap<>();
		ControllerModel.DEFAULTS.forEach(def -> models.put(def.defaultName, def));
		for (ResourceLocation id : manager.listResources("controllermodels/", (s) -> s.endsWith(".json"))) {
			System.out.println(id);
			ResourceLocation modelId = new ResourceLocation(
					id.getNamespace(),
					id.getPath().replace("controllermodels/", "").replace(".json", "")
					);
			try {
				Resource resource = manager.getResource(id);
				try (InputStream stream = resource.getInputStream(); 
						InputStreamReader reader = new InputStreamReader(stream)) {

					JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
					List<String> dupe_buttons = Lists.newArrayList();
					if (object.has("dupe_buttons"))
						object.get("dupe_buttons").getAsJsonArray().forEach((a) -> dupe_buttons.add(a.getAsString()));
					List<Integer> positive_triggers = Lists.newArrayList();
					List<Integer> negative_triggers = Lists.newArrayList();
					if (object.has("triggers"))
						object.get("triggers").getAsJsonArray().forEach((a) -> { 
							if (a.getAsInt() > 0) positive_triggers.add(a.getAsInt());
							else if (a.getAsInt() < 0) negative_triggers.add(a.getAsInt()*-1);
						});
					ControllerModel model = new ControllerModel(
							object.get("modelName").getAsString(), 
							object.get("GUID").getAsString(), 
							object.get("os").getAsString().equals("windows") ? OS.WINDOWS :
								object.get("os").getAsString().equals("linux") ? OS.LINUX : OS.UNKNOWN, 
										negative_triggers.toArray(new Integer[] {}), 
										positive_triggers.toArray(new Integer[] {}), 
										dupe_buttons);

					for (Entry<String, JsonElement> el : object.get("buttons").getAsJsonObject().entrySet()) {
						JsonObject ob = el.getValue().getAsJsonObject();
						ControllerModel.addButton(model, ob.get("name"), ob.get("alias"), ob.get("icon"), el.getKey());
					}
					model.setKey(modelId);
					models.put(modelId, model);
				}
			} catch (Throwable e) {
				ControllerMod.debug("Failed to read textures!", e);
			}
		}


		System.out.println("Look 2 me");
		ALL_MODELS.putAll(models);
		return models;
	}

	private static boolean firstTime = true;
	@Override
	protected void apply(Map<ResourceLocation,ControllerModel> var1, ResourceManager var2, ProfilerFiller var3) {
		ALL_MODELS.clear();
		ALL_MODELS.putAll(var1);
		ControllerMod mod = ControllerMod.getInstance();

		if (firstTime) {
			mod.connectControllers();
			firstTime = false;
		} else {
			mod.disconnectControllers();
			mod.controllerOptions = new ControllerOptions(mod.controllerOptions);
			mod.controllerOptions.loadOptions();
			mod.connectControllers();
		}
	}

}
