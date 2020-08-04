package fr.minecraftjulman.imadmin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.minecraftjulman.imadmin.booleanCheck.Checker;
import fr.minecraftjulman.imadmin.booleanCheck.checkCFFOption;
import fr.minecraftjulman.imadmin.commands.AdminExecutor;
import fr.minecraftjulman.imadmin.config.ConfigFileField;
import fr.minecraftjulman.imadmin.config.ConfigFileFieldGetter;
import fr.minecraftjulman.imadmin.config.Warning;
import fr.minecraftjulman.imadmin.config.WarningsValidator;

public class Main extends JavaPlugin {
	public static final long VERSION_ID = 0;
	private ConsoleCommandSender console = Bukkit.getConsoleSender();
	private HashMap<UUID, Double> hm2 = new HashMap<UUID, Double>();
	public InventoryOpener invOpen = new InventoryOpener(this);
	public static List<ConfigFileField> cffList = new ArrayList<ConfigFileField>();
	public static File writeFileJson;
	
	public void clearAllWarnings() {
		cffList = new ArrayList<ConfigFileField>();
	}
	
	public static void clearAllWarningsStatic() {
		cffList = new ArrayList<ConfigFileField>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onLoad() {
		writeFileJson = new File(getDataFolder(), "warnings.json");
		console.sendMessage("[§eI'm Admin§r] §aLoading plugin...");
		File configurationFile = getDataFolder();
		configurationFile.mkdir();
		File file = new File(configurationFile, "warnings.json");
		console.sendMessage("[§eI'm Admin§r] §a> Loading warnings...");
		if (!file.exists()) {
			JSONArray warnings1 = new JSONArray();
			try {
				FileWriter fw = new FileWriter(new File(configurationFile, "warnings.json"));
				fw.write(warnings1.toJSONString());
				fw.close();
			} catch (IOException e) {
				console.sendMessage("[§eI'm Admin§r] §c  > An error occurred will loading warnings (" + e.getLocalizedMessage() + ")");
			}
		} else {
			JSONParser jp = new JSONParser();
			try {
				FileReader fr = new FileReader(file);
				try {
					JSONArray player = (JSONArray) jp.parse(fr);
					player.forEach(new WarningsValidator(this));
				} catch (IOException | ParseException e) {
					console.sendMessage("[§eI'm Admin§r] §c  > An error occurred will loading warnings (" + e.getLocalizedMessage() + ")");
				}
			} catch (FileNotFoundException e) {
				console.sendMessage("[§eI'm Admin§r] §c  > An error occurred will loading warnings (" + e.getLocalizedMessage() + ")");
			}
		}
		console.sendMessage("[§eI'm Admin§r] §a  > Warnings loaded ! (§e" + Integer.toString(ConfigFileFieldGetter.warningsCount(cffList)) + "§a)");
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Checker.checkConfigFileField(checkCFFOption.UUID, p.getUniqueId(), this.getConfigFileFieldList())) {
				ConfigFileField cff = ConfigFileFieldGetter.getFromUUID(p.getUniqueId(), this.getConfigFileFieldList());
				p.sendMessage("§eInformation : You have §c" + Integer.toString(cff.getWarnings().size()) + "§e warning(s).");
			} else {
				p.sendMessage("§eInformation : You haven't got any warnings !");
			}
		}
		console.sendMessage("[§eI'm Admin§r] §aStarting plugin...");
	}
	
	@Override
	public void onEnable() {
		console.sendMessage("[§eI'm Admin§r] §aPlugin ready !");	
		
		PluginCommand adminC = getCommand("admin");
		adminC.setExecutor(new AdminExecutor(this));
		adminC.setTabCompleter(new TabCompleter() {
			@Override
			public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				List<String> list = new ArrayList<String>();
				
				if (arg3.length == 1) {
					list.add("top");
					list.add("tp");
					list.add("xray");
					list.add("warn");
				} else if (arg3.length == 2) {
					if (arg3[0].equalsIgnoreCase("xray")) {
						for (Player p : Bukkit.getOnlinePlayers())
							list.add(p.getDisplayName());
					} else if (arg3[0].equalsIgnoreCase("warn")) {
						list.add("add");
						list.add("clear");
						list.add("get");
					} else if (arg3[0].equalsIgnoreCase("tp")) {
						for (World world : Bukkit.getWorlds())
							list.add(world.getName());
					}
				} else if (arg3.length == 3) {
					if (arg3[0].equalsIgnoreCase("warn")) {
						if (arg3[1].equalsIgnoreCase("clear")) {
							list.add("all");
							for (Player p : Bukkit.getOnlinePlayers())
								list.add(p.getDisplayName());
						} else if (arg3[1].equalsIgnoreCase("get")) {
							for (Player p : Bukkit.getOnlinePlayers())
								list.add(p.getDisplayName());
						} else if (arg3[1].equalsIgnoreCase("add")) {
							for (Player p : Bukkit.getOnlinePlayers())
								list.add(p.getDisplayName());
						}
					}
				}
				return list;
			}
		});
		
		Bukkit.getPluginManager().registerEvents(new EventsListener(this), this);
		
		checkUpdate();
	}
	
	private void checkUpdate() {
		try {
			URL url = new URL("https://minecraftjulman.github.io/download/versions.json");

			JSONObject jo = (JSONObject) new JSONParser().parse(new InputStreamReader(url.openStream()));
			JSONObject jo1 = (JSONObject) jo.get("imadmin");
			long id = (long) jo1.get("id");
			if (id > VERSION_ID) { 
				console.sendMessage(" -=- [§eI'm Admin§r] [§eUpdater§r] A new update is available (" + ((String) ((JSONObject) jo.get("imadmin")).get("name")) + ") ! Go to \"§3https://minecraftjulman.github.io§r\" for download the update. -=-");
				for (Player player : Bukkit.getOnlinePlayers())
					if (player.isOp())
						player.sendMessage("[§cI'm Admin§r] [§cUpdater§r] §9A new update is available (" + ((String) ((JSONObject) jo.get("imadmin")).get("name")) + ") ! Go to \"§3https://minecraftjulman.github.io§9\" for download the update.");
			}
			
		} catch (Throwable e) {}
	}

	@Override
	public void onDisable() {
		console.sendMessage("[§eI'm Admin§r] §aStopping...");
		console.sendMessage("[§eI'm Admin§r] §a > Saving warnings...");
		try {
			WriteWarning.save(cffList, writeFileJson);
			console.sendMessage("[§eI'm Admin§r] §a  > Saved !");
		} catch (IOException e) {
			console.sendMessage("[§eI'm Admin§r] §c  > An error occurred will saving warnings. (" + e.getLocalizedMessage() + ")");
		}
		console.sendMessage("[§eI'm Admin§r] §aPlugin disabled !");
	}
	
	/**
	 * @return the hm2
	 */
	public HashMap<UUID, Double> getMinedDiamonds() {
		return hm2;
	}
	
	public List<ConfigFileField> getConfigFileFieldList() {
		return cffList;
	}
	
	public static class WriteWarning {
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
}
