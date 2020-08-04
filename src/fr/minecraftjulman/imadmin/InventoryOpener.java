package fr.minecraftjulman.imadmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.sun.istack.internal.NotNull;

import fr.minecraftjulman.imadmin.Alphabet.Letter;
import fr.minecraftjulman.imadmin.booleanCheck.Checker;
import fr.minecraftjulman.imadmin.config.ConfigFileField;
import fr.minecraftjulman.imadmin.config.ConfigFileFieldGetter;
import fr.minecraftjulman.imadmin.config.Warning;

public class InventoryOpener {
	private Main main;
	
	public InventoryOpener(Main main) {	
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
	public boolean openXrayPlayer(Player p, Player toOpenInv) {
		Inventory inv1 = Bukkit.createInventory(null, 36, "§4Xray menu §r- §e" + p.getDisplayName());
		
		if (!(main.getMinedDiamonds().containsKey(p.getUniqueId()))) {
			return false;
		}
		
		ItemStack it1 = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta it1m = (SkullMeta) it1.getItemMeta();
		it1m.setOwner(p.getDisplayName());
		it1m.setDisplayName("§c" + p.getDisplayName());
		it1m.setLore(Arrays.asList("§aMined diamond(s) : §4" + Integer.toString(main.getMinedDiamonds().get(p.getUniqueId()).intValue())));
		it1.setItemMeta(it1m);
		
		ItemStack it2 = new ItemStack(Material.SADDLE, 1);
		ItemMeta it2m = it2.getItemMeta();
		it2m.setDisplayName("§cTeleport to " + p.getDisplayName());
		it2m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it2.setItemMeta(it2m);
		
		ItemStack it3 = new ItemStack(Material.FIREWORK_ROCKET, 1);
		ItemMeta it3m = it3.getItemMeta();
		it3m.setDisplayName("§cWarning " + p.getDisplayName());
		it3m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it3.setItemMeta(it3m);
		
		ItemStack it4 = new ItemStack(Material.CAULDRON, 1);
		ItemMeta it4m = it4.getItemMeta();
		it4m.setDisplayName("§cClear the " + p.getDisplayName() + "'s inventory");
		it4m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it4.setItemMeta(it4m);
		
		inv1.setItem(10, it1);
		inv1.setItem(12, it2);
		inv1.setItem(13, it3);
		inv1.setItem(14, it4);
		
		toOpenInv.openInventory(inv1);
		return true;
	}
	
	public void openMainXrayManu(Player toOpenInv) {
		Inventory inv = Bukkit.createInventory(null, 54, "§4Xray menu");

		if (Checker.isBetween(main.getMinedDiamonds().size(), 1, 9)) {
			inv.setMaxStackSize(9);
		} else if (Checker.isBetween(main.getMinedDiamonds().size(), 10, 18)) {
			inv.setMaxStackSize(18);
		} else if (Checker.isBetween(main.getMinedDiamonds().size(), 19, 27)) {
			inv.setMaxStackSize(27);
		} else if (Checker.isBetween(main.getMinedDiamonds().size(), 28, 36)) {
			inv.setMaxStackSize(36);
		} else if (Checker.isBetween(main.getMinedDiamonds().size(), 37, 45)) {
			inv.setMaxStackSize(45);
		} else if (Checker.isBetween(main.getMinedDiamonds().size(), 46, 54)) {
			inv.setMaxStackSize(54);
		} 
		
		toOpenInv.openInventory(inv);
	}
	
	private ItemStack Key(char text) {
		String str = Character.toString(text);
		if (text == Letter.SPACE) {
			str = "SPACE";
		}
		str = "§e" + str;
		
		ItemStack it = new ItemStack(Material.STONE, 1);
		ItemMeta itm = it.getItemMeta();
		itm.setDisplayName(str);
		itm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it.setItemMeta(itm);
		
		return it;
	}
	
	public void openKeyboard(@NotNull Player toOpenInv, @NotNull DoneAction action) {
		Inventory inv = Bukkit.createInventory(null, 54, "Keyboard - " + action.toString());
		String str = " ";
		
		ItemStack it = new ItemStack(Material.PAPER, 1);
		ItemMeta itm = it.getItemMeta();
		itm.setDisplayName(str);
		it.setItemMeta(itm);
		inv.setItem(4, it);
		
		inv.setItem(9, Key(Letter.A));
		inv.setItem(10, Key(Letter.B));
		inv.setItem(11, Key(Letter.C));
		inv.setItem(12, Key(Letter.D));
		inv.setItem(13, Key(Letter.E));
		inv.setItem(14, Key(Letter.F));
		inv.setItem(15, Key(Letter.G));
		inv.setItem(16, Key(Letter.H));
		inv.setItem(17, Key(Letter.I));
		inv.setItem(18, Key(Letter.J));
		inv.setItem(19, Key(Letter.K));
		inv.setItem(20, Key(Letter.L));
		inv.setItem(21, Key(Letter.M));
		inv.setItem(22, Key(Letter.N));
		inv.setItem(23, Key(Letter.O));
		inv.setItem(24, Key(Letter.P));
		inv.setItem(25, Key(Letter.Q));
		inv.setItem(26, Key(Letter.R));
		inv.setItem(27, Key(Letter.S));
		inv.setItem(28, Key(Letter.T));
		inv.setItem(29, Key(Letter.U));
		inv.setItem(30, Key(Letter.V));
		inv.setItem(31, Key(Letter.W));
		inv.setItem(32, Key(Letter.X));
		inv.setItem(33, Key(Letter.Y));
		inv.setItem(34, Key(Letter.Z));
		inv.setItem(35, Key(Letter.SPACE));
		
		ItemStack it1 = new ItemStack(Material.GREEN_WOOL, 1);
		ItemMeta it1m = it1.getItemMeta();
		it1m.setDisplayName("§cDone §a(A ERROR CAN BE THROW)");
		it1m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it1.setItemMeta(it1m);
		inv.setItem(53, it1);
		
		toOpenInv.openInventory(inv);
	}
	
	@SuppressWarnings("deprecation")
	public void openWarnMenu(Player warned, Player toOpenInv) {
		ConfigFileField cff = ConfigFileFieldGetter.getFromUUID(warned.getUniqueId(), main.getConfigFileFieldList());
		Inventory inv = Bukkit.createInventory(null, 27, "§eWarnings - §c" + warned.getDisplayName());
		
		ItemStack it1 = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta it1m = (SkullMeta) it1.getItemMeta();
		it1m.setDisplayName("§2" + warned.getDisplayName());
		List<String> list = new ArrayList<String>();
		list.add("§eWarning count : §c" + Integer.toString(cff.getWarnings().size()));
		int count = 1;
		for (Warning warn : cff.getWarnings()) {
			list.add("");
			list.add("§cWarning n°" + Integer.toString(count) + " :");
			list.add(" §aReason : §c" + warn.getReason());
			Player p1 = Bukkit.getPlayer(warn.getWhoWarned());
			String str = "";
			if (p1 == null) str = "Player offline";
			else str = p1.getDisplayName();
			list.add(" §aFrom : §c" + str);
			list.add(" §aWhen : " + warn.getDate().toLocaleString());
			count++;
		}
		it1m.setLore(list);
		it1.setItemMeta(it1m);
		inv.setItem(10, it1);
		
		ItemStack it2 = new ItemStack(Material.RED_WOOL, 1);
		ItemMeta it2m = it2.getItemMeta();
		it2m.setDisplayName("§4Ban from the server");
		it2m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it2.setItemMeta(it2m);
		inv.setItem(12, it2);
		
		ItemStack it3 = new ItemStack(Material.ORANGE_WOOL, 1);
		ItemMeta it3M = it3.getItemMeta();
		it3M.setDisplayName("§cKick from the server");
		it3M.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it3.setItemMeta(it3M);
		inv.setItem(13, it3);
		
		ItemStack it4 = new ItemStack(Material.YELLOW_WOOL, 1);
		ItemMeta it4m = it4.getItemMeta();
		it4m.setDisplayName("§eAdd a warning §e(WIP)");
		it4m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		it4.setItemMeta(it4m);
		inv.setItem(14, it4);
		
		toOpenInv.openInventory(inv);
	}
}
