package fr.minecraftjulman.imadmin.config;

import java.util.Date;
import java.util.UUID;

public class Warning {
	private String reason;
	private UUID player;
	private UUID whoWarned;
	private Date date;
	
	public Warning(String reason, UUID warningedPlayer, UUID whoWarned, Date date) {
		this.reason = reason;
		this.player = warningedPlayer;
		this.whoWarned = whoWarned;
		this.date = date;
	}
	
	public UUID getPlayer() {
		return player;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String newReason) {
		this.reason = newReason;
	}
	
	public UUID getWhoWarned() {
		return whoWarned;
	}

	public Date getDate() {
		return date;
	}
}
