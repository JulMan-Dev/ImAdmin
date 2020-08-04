package fr.minecraftjulman.imadmin.config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.minecraftjulman.imadmin.Main;

public class WarningsValidator implements Consumer<Object> {
	private Main main;
	
	public WarningsValidator(Main plugin) {
		main = plugin;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void accept(Object t) {
		JSONObject jo = (JSONObject) t;
		UUID uuid = UUID.fromString((String) jo.get("uuid"));
		List<Warning> list = new ArrayList<Warning>();
		((JSONArray) jo.get("warnings")).forEach(new Consumer<JSONObject>() {
			@Override
			public void accept(JSONObject t) {
				Warning w = new Warning((String) t.get("reason"), uuid, UUID.fromString((String) t.get("from")), 
						Date.from(Instant.parse((String) t.get("date"))));
				list.add(w);
			}
		});
		ConfigFileField cff = new ConfigFileField(uuid, list);
		main.getConfigFileFieldList().add(cff);
	}
}
