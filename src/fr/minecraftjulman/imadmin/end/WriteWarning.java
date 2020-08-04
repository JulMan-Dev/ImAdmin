package fr.minecraftjulman.imadmin.end;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.minecraftjulman.imadmin.config.ConfigFileField;
import fr.minecraftjulman.imadmin.config.Warning;

public class WriteWarning {
	public static void save(List<ConfigFileField> args1, File writeFileJson) throws IOException {
		FileWriter fw = new FileWriter(writeFileJson);
		JSONArray ja = new JSONArray();
		List<JSONObject> joL = new ArrayList<JSONObject>();
		
		args1.forEach(new Consumer<ConfigFileField>() {
			UUID uuid;
			List<Warning> list;
			JSONObject playerInfo = new JSONObject();
			
			@SuppressWarnings("unchecked")
			@Override
			public void accept(ConfigFileField arg0) {
				uuid = arg0.getPlayer().getUniqueId();
				list = arg0.getWarnings();
				
				JSONArray jsonA = new JSONArray();
				list.forEach(new Consumer<Warning>() {
					@Override
					public void accept(Warning arg0) {
						JSONObject jsonO = new JSONObject();
						jsonO.put("reason", arg0.getReason());
						jsonO.put("from", arg0.getWhoWarned().toString());
						jsonO.put("date", arg0.getDate().toInstant().toString());
						jsonA.add(jsonO);
					}
				});			
				playerInfo.put("uuid", uuid.toString());
				playerInfo.put("warnings", jsonA);
				joL.add(playerInfo);
			}
		});
		joL.forEach(new Consumer<JSONObject>() {
			@SuppressWarnings("unchecked")
			@Override
			public void accept(JSONObject arg0) {
				ja.add(arg0);
			}
		});
		fw.write(ja.toJSONString());
		fw.close();
	}
}
