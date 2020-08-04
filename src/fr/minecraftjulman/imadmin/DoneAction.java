package fr.minecraftjulman.imadmin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sun.istack.internal.NotNull;

public class DoneAction {
	private Action action;
	private Type type;
	private Player player;
	private UUID p;
	
	public static enum Action {
		ADD, SET, REMOVE	
	}
	
	public static enum Type {
		WARNING, OTHER
	}
	
	public DoneAction(@NotNull Action action, @NotNull Type type, @NotNull Player player, @Nullable UUID uuidForWarningTarget) {
		this.action = action;
		this.player = player;
		this.type = type;
		this.p = uuidForWarningTarget;
	}
	
	@NotNull
	public static String toString(DoneAction doneAction) {
		return doneAction.toString();
	}
	
	@Nullable
	@SuppressWarnings("deprecation")
	public static DoneAction fromString(@NotNull String str) {
		List<String> list = new ArrayList<>();
		for (String str1 : str.split(" ")) list.add(str1);
		
		Player player;
		UUID player1 = null;
		Action action = null;
		Type type;
		DoneAction da = null;
		
		switch (list.get(0)) {
		case "Add":
			action = Action.ADD;
			break;
			
		case "Set":
			action = Action.SET;
			break;
			
		case "Remove":
			action = Action.REMOVE;

		default:
			break;
		}
		
		switch (list.get(1)) {
		case "warning":
			type = Type.WARNING;
			break;
			
		default:
			type = Type.OTHER;
			break;
		}
		
		player = Bukkit.getPlayer(list.get(2));
		if (list.size() > 3) {
			list.remove("to");
			UUID str1 = UUID.fromString(list.get(3));
			player1 = str1;			
		}
		
		da = new DoneAction(action, type, player, player1);
		return da;
	}
	
	@NotNull
	@Override
	public String toString() {
		String str = "";
		
		switch (action) {
		case ADD:
			str += "Add ";
			break;

		case SET:
			str += "Set ";
			break;
			
		case REMOVE:
			str += "Remove ";
			break;
			
		default:
			break;
		}
		switch (type) {
		case WARNING:
			str += "warning ";
			break;

		default:
			break;
		}
		String str1 = "";
		if (p != null) {
			str1 = " to " + p.toString();
		}
		str += player.getDisplayName() + str1;
		
		return str;
	}
	
	@NotNull
	public Action getAction() {
		return action;
	}
	
	@NotNull
	public Player getPlayer() {
		return player;
	}
	
	@NotNull
	public UUID getUUID() {
		return p;
	}
	
	public Type getType() {
		return type;
	}
}
