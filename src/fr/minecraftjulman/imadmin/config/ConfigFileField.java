package fr.minecraftjulman.imadmin.config;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConfigFileField {
	private UUID playerId;
	private List<Warning> wc;
	
	public ConfigFileField(UUID playerId, List<Warning> warningsList) {
		this.playerId = playerId;
		this.wc = warningsList;
	}
	
	@Nullable
	public Player getPlayer() {
		return Bukkit.getPlayer(playerId);
	}
	
	public List<Warning> getWarnings() {
		return wc;
	}
}
