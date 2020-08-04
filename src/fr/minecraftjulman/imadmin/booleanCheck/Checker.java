package fr.minecraftjulman.imadmin.booleanCheck;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.HashBasedTable;

import fr.minecraftjulman.imadmin.config.ConfigFileField;
import fr.minecraftjulman.imadmin.config.Warning;

public class Checker {
	public static boolean isBetween(double checkingDouble, int int1, int int2) {
		if (int1 < checkingDouble && checkingDouble < int2) {
			return true;
		}
		return false;
	}
	
	public static boolean isBetween(int checkingInt, int int1, int int2) {
		if (int1 < checkingInt && checkingInt < int2) {
			return true;
		}
		return false;
	}
	
	public static int getSize(HashMap<Object, Object> hashMap) {
		return hashMap.size();
	}
	
	public static int getSize(List<Object> list) {
		return list.size();
	}
	
	public static int getSize(HashBasedTable<Object, Object, Object> hashBasedTable) {
		return hashBasedTable.size();
	}
	
	public static boolean isNull(Object obj) {
		return obj == null;
	}
	
	static boolean returnStatment = false;
	public static boolean checkConfigFileField(checkCFFOption option, Object objectToCheck, List<ConfigFileField> list) {
		if (option == checkCFFOption.UUID) {
			list.forEach(new Consumer<ConfigFileField>() {
				@Override
				public void accept(ConfigFileField arg0) {
					if (arg0.getPlayer() != null) {
						if (arg0.getPlayer().getUniqueId() == objectToCheck) {
							returnStatment = true;
						}
					}
				}
			});
		} else if (option == checkCFFOption.WARNING) {
			list.forEach(new Consumer<ConfigFileField>() {
				@Override
				public void accept(ConfigFileField arg0) {
					arg0.getWarnings().forEach(new Consumer<Warning>() {
						@Override
						public void accept(Warning arg0) {
							if (arg0 == objectToCheck) {
								returnStatment = true;
							}
						}
					});
				}
			});
		}
		return returnStatment;
	}
}
