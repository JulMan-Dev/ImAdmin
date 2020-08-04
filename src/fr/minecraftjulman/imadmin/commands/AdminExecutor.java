package fr.minecraftjulman.imadmin.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.minecraftjulman.imadmin.InventoryOpener;
import fr.minecraftjulman.imadmin.Main;
import fr.minecraftjulman.imadmin.booleanCheck.Checker;
import fr.minecraftjulman.imadmin.booleanCheck.checkCFFOption;
import fr.minecraftjulman.imadmin.config.ConfigFileField;
import fr.minecraftjulman.imadmin.config.ConfigFileFieldGetter;
import fr.minecraftjulman.imadmin.config.Warning;

public class AdminExecutor implements CommandExecutor {
	private Main main;
	
	public AdminExecutor(Main main) {
		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Boolean returnValue = false;
		
		if (cmd.getName().equalsIgnoreCase("admin")) {
			returnValue = true;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("xray")) {
					if (sender instanceof Player) {
						Inventory inv = Bukkit.createInventory(null, 54, "§4Xray menu");
						
						main.getMinedDiamonds().forEach(new BiConsumer<UUID, Double>() {
							@Override
							public void accept(UUID t, Double u) {
								Player p = Bukkit.getPlayer(t);
								if (p == null) {
									ItemStack it1 = new ItemStack(Material.DEAD_BUSH, 1);
									ItemMeta it1m = it1.getItemMeta();
									it1m.setDisplayName("§eA offline player");
									it1.setItemMeta(it1m);
									inv.addItem(it1);
								} else {
									ItemStack it = new ItemStack(Material.PLAYER_HEAD, 1);
									SkullMeta itM = (SkullMeta) it.getItemMeta();
									itM.setOwner(p.getDisplayName());
									itM.setDisplayName("§c"+ p.getDisplayName());
									itM.addItemFlags(ItemFlag.HIDE_ENCHANTS);
									itM.setLore(Arrays.asList("§aMined diamond(s) : §4" + Integer.toString(u.intValue()), "", "§eClick for more action !"));
									it.setItemMeta(itM);
									inv.addItem(it);
								}
							}
						});
						
						((Player) sender).openInventory(inv);
					} else sender.sendMessage("§cYou can't you that !");
				} else if (args[0].equalsIgnoreCase("top")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("§cYou can't use that !");
					} else {
						for (int i = 255; i > 0; i--) {
							Player p = (Player) sender;
							double X = p.getLocation().getX();
							double Z = p.getLocation().getZ();
							float yaw = p.getLocation().getYaw();
							float pitch = p.getLocation().getPitch();
							Location loc = new Location(p.getWorld(), X, i, Z, yaw, pitch);
							
							if (loc.getBlock().getType() != Material.AIR) {
								loc.setY(loc.getY() + 1);
								p.sendMessage("§eTeleported " + Integer.toString((i - p.getLocation().getBlockY()) + 1) + " blocks heigher !");
								p.teleport(loc);
								break;
							} else {
								continue;
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("warn")) {
					sender.sendMessage("§cArguments : ");
					sender.sendMessage("§e - \"clear\"");
				} else if (args[0].equalsIgnoreCase("tp")) {
					sender.sendMessage("§cArguments : ");
					sender.sendMessage("§e - \"<world>\"");
				} else {
					sender.sendMessage("The arguments ?");
				}
			} else if (args.length == 2) {
				if (sender instanceof Player) {
					if (args[0].equalsIgnoreCase("xray")) {
						Player p1 = Bukkit.getPlayer(args[1]);
						if (p1 == null) {
							sender.sendMessage("§cEuh... I can't find this player !");
						} else {
							if (!main.invOpen.openXrayPlayer(p1, (Player) sender)) 
								sender.sendMessage("§cThis player is not in the list !");							
						}
					} else if (args[0].equalsIgnoreCase("warn")) {
						if (args[1].equalsIgnoreCase("get")) {
							sender.sendMessage("§cArguments :");
							sender.sendMessage("§e - A player");	
						} else if (args[1].equalsIgnoreCase("clear")) {
							sender.sendMessage("§cArguments :");
							sender.sendMessage("§e - \"all\"");
							sender.sendMessage("§e - A player");						
						} else if (args[1].equalsIgnoreCase("add")) {
							sender.sendMessage("§cArguments :");
							sender.sendMessage("§e - A player");
						} else {
							sender.sendMessage("§cThe arguments ?");
						}
					} else if (args[0].equalsIgnoreCase("tp")) {
						if (Bukkit.getWorld(args[1]) == null) {
							sender.sendMessage("§cWhere is it, i can't find !");
						} else {
							((Player) sender).teleport(Bukkit.getWorld(args[1]).getSpawnLocation(), TeleportCause.COMMAND);
						}
					}
				} else {
					sender.sendMessage("§cYou can't use that !");
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("warn")) {
					if (args[1].equalsIgnoreCase("clear")) {
						if (args[2].equalsIgnoreCase("all")) {
							main.clearAllWarnings();
							try {
								Main.WriteWarning.save(main.getConfigFileFieldList(), Main.writeFileJson);
							} catch (IOException e) {
								e.printStackTrace();
							}
							sender.sendMessage("§aAll warnings have been removed !");
						} else {
							Player p1 = Bukkit.getPlayer(args[2]);
							if (p1 == null) {
								sender.sendMessage("§cEuh... This player is offline ?");
							} else {
								if (!main.getConfigFileFieldList().isEmpty() && Checker.checkConfigFileField(checkCFFOption.UUID, p1.getUniqueId(), main.getConfigFileFieldList()) ) {
									main.getConfigFileFieldList().remove(ConfigFileFieldGetter.getFromUUID(p1.getUniqueId(), main.getConfigFileFieldList()));
									sender.sendMessage("§eAll warnings from " + p1.getDisplayName() + " have been removed !");
								} else {
									sender.sendMessage("§cThis player hasn't got any warnings !");
								}
							}
						}
					} else if (args[1].equalsIgnoreCase("get")) {
						Player p1 = Bukkit.getPlayer(args[2]);
						if (p1 == null) {
							sender.sendMessage("§c§cEuh... I can't find this player !");
						} else {
							if (Checker.checkConfigFileField(checkCFFOption.UUID, p1.getUniqueId(), main.getConfigFileFieldList()) && !main.getConfigFileFieldList().isEmpty()) {
								if (sender instanceof Player) {
									new InventoryOpener(main).openWarnMenu(p1, (Player) sender);
								} else {
									sender.sendMessage("§cYou can't use that !");
								}
							} else {
								sender.sendMessage("§cSorry, but this player hasn't got any warnings !");
							}
						}
					} else if (args[1].equalsIgnoreCase("add")) {
						Player p = Bukkit.getPlayer(args[2]);
						if (p == null) {
							sender.sendMessage("§c§cEuh... I can't find this player !");
						} else {
							String str = "No reason specified !";
							
							List<ConfigFileField> cffL = main.getConfigFileFieldList();
							ConfigFileField cffO;
							if (Checker.checkConfigFileField(checkCFFOption.UUID, p.getUniqueId(), cffL) && !main.getConfigFileFieldList().isEmpty()) {
								ConfigFileField cff1 = new ConfigFileField(p.getUniqueId(), ConfigFileFieldGetter.getFromUUID(p.getUniqueId(), cffL).getWarnings());
								cff1.getWarnings().add(new Warning(str, p.getUniqueId(), ((Player) sender).getUniqueId(), new Date()));
								cffL.remove(ConfigFileFieldGetter.getFromUUID(p.getUniqueId(), cffL));
								cffL.add(cff1);
								sender.sendMessage("§e" + p.getDisplayName() + " has been warned for : " + str);
								cffO = cff1;
							} else {
								ConfigFileField cff2 = new ConfigFileField(p.getUniqueId(), new ArrayList<Warning>());
								cff2.getWarnings().add(new Warning(str, p.getUniqueId(), ((Player) sender).getUniqueId(), new Date()));
								cffL.add(cff2);
								sender.sendMessage("§e" + p.getDisplayName() + " has been warned for : " + str);
								cffO = cff2;
							}
							ConsoleCommandSender console = Bukkit.getConsoleSender();
							try {
								console.sendMessage("[§eI'm Admin§r] §aStarting new Task : §3save warnings");
								Main.WriteWarning.save(main.getConfigFileFieldList(), Main.writeFileJson);
								console.sendMessage("[§eI'm Admin§r] §a > Saved !");
								console.sendMessage("[§eI'm Admin§r] §aTask ended without error(s) !");
							} catch (IOException e) {
								console.sendMessage("[§eI'm Admin§r] §aTask ended with error(s) :");
								e.printStackTrace();
							}
							if (cffO.getWarnings().size() > 3) {
								p.kickPlayer("§eYou have been kicked because you have §2"  + Integer.toString(cffO.getWarnings().size()) + "§e warnings !");
							}
						}
					} else {
						sender.sendMessage("§cThe arguments ?");
					}
				} else {
					sender.sendMessage("§cThe arguments ?");
				}
			} else if (args.length > 3) {
				if (args[0].equalsIgnoreCase("warn")) {
					if (args[1].equalsIgnoreCase("add")) {
						Player p = Bukkit.getPlayer(args[2]);
						if (p == null) {
							sender.sendMessage("§c§cEuh... I can't find this player !");
						} else {
							if (sender instanceof Player) {
								List<String> list = new ArrayList<String>();
								for (String str : args) list.add(str);
								list.remove(0); list.remove(0); list.remove(0);
								
								String str = "";
								for (String str1 : list) str += str1 + " ";
								str = str.replace("&", "§");
							
								List<ConfigFileField> cffL = main.getConfigFileFieldList();
								ConfigFileField cffO;
								if (Checker.checkConfigFileField(checkCFFOption.UUID, p.getUniqueId(), cffL) && !main.getConfigFileFieldList().isEmpty()) {
									ConfigFileField cff1 = new ConfigFileField(p.getUniqueId(), ConfigFileFieldGetter.getFromUUID(p.getUniqueId(), cffL).getWarnings());
									cff1.getWarnings().add(new Warning(str, p.getUniqueId(), ((Player) sender).getUniqueId(), new Date()));
									cffL.remove(ConfigFileFieldGetter.getFromUUID(p.getUniqueId(), cffL));
									cffL.add(cff1);
									sender.sendMessage("§e" + p.getDisplayName() + " has been warned for : " + str);
									cffO = cff1;
								} else {
									ConfigFileField cff2 = new ConfigFileField(p.getUniqueId(), new ArrayList<Warning>());
									cff2.getWarnings().add(new Warning(str, p.getUniqueId(), ((Player) sender).getUniqueId(), new Date()));
									cffL.add(cff2);
									sender.sendMessage("§e" + p.getDisplayName() + " has been warned for : " + str);
									cffO = cff2;
								}
								ConsoleCommandSender console = Bukkit.getConsoleSender();
								try {
									console.sendMessage("[§eI'm Admin§r] §aStarting new Task : §3save warnings");
									Main.WriteWarning.save(main.getConfigFileFieldList(), Main.writeFileJson);
									console.sendMessage("[§eI'm Admin§r] §a > Saved !");
									console.sendMessage("[§eI'm Admin§r] §aTask ended without error(s) !");
								} catch (IOException e) {
									console.sendMessage("[§eI'm Admin§r] §aTask ended with error(s) :");
									e.printStackTrace();
								}
								if (cffO.getWarnings().size() > 3) {
									p.kickPlayer("§eYou have been kicked because you have §2"  + Integer.toString(cffO.getWarnings().size()) + "§e warnings !");
								}
							}
						}
					} else {
						sender.sendMessage("§cThe arguments ?");
					}
				} else {
					sender.sendMessage("§cThe arguments ?");
				}
			} else {
				sender.sendMessage("§cThe arguments ?");
			}
		}
		
		return returnValue;
	}
}
