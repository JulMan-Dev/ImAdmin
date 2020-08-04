package fr.minecraftjulman.imadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.minecraftjulman.imadmin.DoneAction.Action;
import fr.minecraftjulman.imadmin.booleanCheck.Checker;
import fr.minecraftjulman.imadmin.booleanCheck.checkCFFOption;
import fr.minecraftjulman.imadmin.config.ConfigFileField;
import fr.minecraftjulman.imadmin.config.ConfigFileFieldGetter;
import fr.minecraftjulman.imadmin.config.Warning;

public class EventsListener implements Listener {
	private Main main;
	
	public EventsListener(Main main) {
		this.main = main;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (e.getPlayer().toString() == "") e.setCancelled(true);
		if (b.getType() == Material.DIAMOND_ORE) {
			if (main.getMinedDiamonds().containsKey(e.getPlayer().getUniqueId())) {
				main.getMinedDiamonds().replace(e.getPlayer().getUniqueId(), main.getMinedDiamonds().get(e.getPlayer().getUniqueId()) + 1.0);
			} else {
				main.getMinedDiamonds().put(e.getPlayer().getUniqueId(), 1.0);
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (Checker.checkConfigFileField(checkCFFOption.UUID, p.getUniqueId(), main.getConfigFileFieldList())) {
			ConfigFileField cff = ConfigFileFieldGetter.getFromUUID(p.getUniqueId(), main.getConfigFileFieldList());
			p.sendMessage("§eInformation : You have §c" + Integer.toString(cff.getWarnings().size()) + "§e warning(s).");
		} else {
			p.sendMessage("§eInformation : You haven't got any warnings !");
		}
		String str = e.getJoinMessage();
		e.setJoinMessage(null);
		Bukkit.getConsoleSender().sendMessage(str);
		for (Player p1 : Bukkit.getOnlinePlayers()) {
			if (p1.getUniqueId() == p.getUniqueId()) {
				continue;
			} else {
				p1.sendMessage(str);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryCLick(InventoryClickEvent e) throws Exception {
		InventoryView inv = e.getView();
		ItemStack it = e.getCurrentItem();
		
		if (it == null) return;
		
		if (inv.getTitle().equalsIgnoreCase("§4xray menu")) {
			e.setCancelled(true);
			if (it.getType() == Material.PLAYER_HEAD && it.hasItemMeta() && it.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
				Player p = Bukkit.getPlayer(((SkullMeta) it.getItemMeta()).getOwner());
				if (p != null && main.getMinedDiamonds().containsKey(p.getUniqueId())) {
					main.invOpen.openXrayPlayer(p, (Player) e.getWhoClicked());
				} else {
					e.getWhoClicked().sendMessage("§cThis player isn't online or an error occurred");
				}
			}
		} else if (inv.getTitle().toLowerCase().startsWith("§4xray menu §r- §e")) {
			e.setCancelled(true);
			Player p1 = Bukkit.getPlayer(inv.getTitle().split("§4Xray menu §r- §e")[1]);
			if (p1 == null) {
				e.getWhoClicked().sendMessage("§cThis player is offline !");
				return;
			} else {
				if (it.hasItemMeta() && it.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
					switch (it.getType()) {
					case SADDLE:
						e.getWhoClicked().setGameMode(GameMode.SPECTATOR);
						e.getWhoClicked().teleport(p1);
						break;
				
					case FIREWORK_ROCKET:
						p1.playSound(p1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
						List<ConfigFileField> cffL = main.getConfigFileFieldList();
						ConfigFileField cffO;
						if (Checker.checkConfigFileField(checkCFFOption.UUID, p1.getUniqueId(), cffL) && !main.getConfigFileFieldList().isEmpty()) {
							ConfigFileField cff1 = new ConfigFileField(p1.getUniqueId(), ConfigFileFieldGetter.getFromUUID(p1.getUniqueId(), cffL).getWarnings());
							cff1.getWarnings().add(new Warning("Warned by " + ((Player) e.getWhoClicked()).getDisplayName() + " for xray", p1.getUniqueId(), 
									((Player) e.getWhoClicked()).getUniqueId(), new Date()));
							cffL.remove(ConfigFileFieldGetter.getFromUUID(p1.getUniqueId(), cffL));
							cffL.add(cff1);
							cffO = cff1;
						} else {
							ConfigFileField cff2 = new ConfigFileField(p1.getUniqueId(), new ArrayList<Warning>());
							cff2.getWarnings().add(new Warning("Warned by " + ((Player) e.getWhoClicked()).getDisplayName() + " for xray", p1.getUniqueId(), 
									((Player) e.getWhoClicked()).getUniqueId(), new Date()));
							cffL.add(cff2);
							cffO = cff2;
						}
						e.getWhoClicked().sendMessage("§e" + p1.getDisplayName() + " has been warn for §c\"xray\"§e.");
						ConsoleCommandSender console = Bukkit.getConsoleSender();
						try {
							console.sendMessage("[§eI'm Admin§r] §aStarting new Task : §3save warnings");
							Main.WriteWarning.save(main.getConfigFileFieldList(), Main.writeFileJson);
							console.sendMessage("[§eI'm Admin§r] §a > Saved !");
							console.sendMessage("[§eI'm Admin§r] §aTask ended without error(s) !");
						} catch (IOException e1) {
							console.sendMessage("[§eI'm Admin§r] §aTask ended with error(s) :");
							e1.printStackTrace();
						}
						if (cffO.getWarnings().size() > 3) {
							p1.kickPlayer("§eYou have been kicked because you have §2"  + Integer.toString(cffO.getWarnings().size()) + "§e warnings !");
						}
						break;
					
					case CAULDRON:
						p1.getInventory().clear();
						e.getWhoClicked().sendMessage("§cInventory cleared !");
						p1.sendMessage("§n§4Information§r§c : Your inventory have been cleared by §n" + ((Player) e.getWhoClicked()).getDisplayName() + "§r§c.");
						break;
				
					default:
						break;
					}
				}
			}
		} else if (inv.getTitle().toLowerCase().startsWith("§ewarnings - §c")) {
			e.setCancelled(true);
			Player p1 = Bukkit.getPlayer(inv.getTitle().split("§eWarnings - §c")[1]);
			if (it.hasItemMeta() && it.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
				switch (it.getType()) {
				case RED_WOOL:
					Date date = new Date();
					date.setTime(date.getTime() + 1296000000);
					e.getWhoClicked().sendMessage("§e" + p1.getDisplayName() + " is banned for 15 days");
					Bukkit.getBanList(Type.NAME).addBan(p1.getDisplayName(), "§eYou're banned by §3" + ((Player) e.getWhoClicked()).getDisplayName() + "§c", date, ((Player) e.getWhoClicked()).getDisplayName());
					p1.kickPlayer("§eYou're banned by §3" + ((Player) e.getWhoClicked()).getDisplayName() + "§c");
					break;

				case ORANGE_WOOL:
					e.getWhoClicked().sendMessage("§e" + p1.getDisplayName() + " has been kicked form the server");
					p1.kickPlayer("§eYou have been kicked by " + ((Player) e.getWhoClicked()).getDisplayName());
					break;
					
				case YELLOW_WOOL:
					main.invOpen.openKeyboard((Player) e.getWhoClicked(), new DoneAction(Action.ADD, fr.minecraftjulman.imadmin.DoneAction.Type.WARNING, (Player) e.getWhoClicked(), p1.getUniqueId()));
					break;
				
				default:
					break;
				}
			}
		} else if (inv.getTitle().toLowerCase().startsWith("keyboard - ")) {
			e.setCancelled(true);
			ItemStack it1 = inv.getItem(4);
			DoneAction da = DoneAction.fromString(inv.getTitle().split("Keyboard - ")[1]);
			ItemStack it2 = e.getCurrentItem();
			if (it2.hasItemMeta() && it2.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
				switch (it2.getType()) {
				case STONE:
					String str = it2.getItemMeta().getDisplayName();
					str = str.split("§e")[1];
					if (str == "SPACE") {
						str = " ";
					}
					ItemMeta it1m = it1.getItemMeta();
					if (it1m.getDisplayName() == " ") {
						it1m.setDisplayName(str);
					} else {
						it1m.setDisplayName(it1m.getDisplayName() + str);
					}
					it1.setItemMeta(it1m);
					break;

				case GREEN_WOOL:
					if (it1.getItemMeta().getDisplayName() != " ") {
						List<ConfigFileField> cffL = Main.cffList;
						ConfigFileField cffO;
						if (Checker.checkConfigFileField(checkCFFOption.UUID, da.getUUID(), cffL) && !Main.cffList.isEmpty()) {
							ConfigFileField cff1 = new ConfigFileField(da.getUUID(), ConfigFileFieldGetter.getFromUUID(da.getUUID(), cffL).getWarnings());
							cff1.getWarnings().add(new Warning(it1.getItemMeta().getDisplayName(), 
									da.getUUID(), ((Player) e.getWhoClicked()).getUniqueId(), new Date()));
							cffL.remove(ConfigFileFieldGetter.getFromUUID(da.getUUID(), cffL));
							cffL.add(cff1);
							da.getPlayer().sendMessage("§eWarning added successfully !");
							cffO = cff1;
						} else {
							ConfigFileField cff2 = new ConfigFileField(da.getUUID(), new ArrayList<Warning>());
							cff2.getWarnings().add(new Warning(it1.getItemMeta().getDisplayName(), 
									da.getUUID(), ((Player) e.getWhoClicked()).getUniqueId(), new Date()));
							cffL.add(cff2);
							da.getPlayer().sendMessage("§eWarning added successfully !");
							cffO = cff2;
						}
						ConsoleCommandSender console = Bukkit.getConsoleSender();
						try {
							console.sendMessage("[§eI'm Admin§r] §aStarting new Task : §3save warnings");
							Main.WriteWarning.save(main.getConfigFileFieldList(), Main.writeFileJson);
							console.sendMessage("[§eI'm Admin§r] §a > Saved !");
							console.sendMessage("[§eI'm Admin§r] §aTask ended without error(s) !");
						} catch (IOException e1) {
							console.sendMessage("[§eI'm Admin§r] §aTask ended with error(s) :");
							e1.printStackTrace();
						}
						if (cffO.getWarnings().size() > 3) {
							Player p1 = Bukkit.getPlayer(da.getUUID());
							if (p1 != null) {
								p1.kickPlayer("§eYou have been kicked because you have §2"  + Integer.toString(cffO.getWarnings().size()) + "§e warnings !");
							}
						}
					} else {
						e.getWhoClicked().sendMessage("§cPlease enter a reason");
					}
					break;
					
				default:
					break;
				}
			}
		}
	}
}
