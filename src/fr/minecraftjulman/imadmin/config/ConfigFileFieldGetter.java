package fr.minecraftjulman.imadmin.config;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

public class ConfigFileFieldGetter {
	private static ConfigFileField cff = null;
	@Nullable
	/**
	 * Get a CnfigFileField in a list form a UUID.
	 * @param uuid
	 * @param list
	 * @return
	 */
	public static ConfigFileField getFromUUID(UUID uuid, List<ConfigFileField> list) {
		cff = null;
		list.forEach(new Consumer<ConfigFileField>() {
			@Override
			public void accept(ConfigFileField arg0) {
				if (arg0.getPlayer().getUniqueId() == uuid) {
					cff = arg0;
				}
			}
		});
		return cff;
	}
	
	public static ConfigFileField getFromWarning(Warning warning, List<ConfigFileField> list) {
		cff = null;
		list.forEach(new Consumer<ConfigFileField>() {
			@Override
			public void accept(ConfigFileField arg0) {
				arg0.getWarnings().forEach(new Consumer<Warning>() {
					@Override
					public void accept(Warning arg1) {
						if (arg1.equals(warning)) {
							cff = arg0;
						}
					}
				});
			}
		});
		return cff;
	}
	
	public static int warningsCount(ConfigFileField cff) {
		int count = 0;
		for (@SuppressWarnings("unused") Warning warn : cff.getWarnings()) {
			count++;
		}
		return count;
	}
	
	public static int warningsCount(List<ConfigFileField> cffList) {
		int count = 0;
		for (ConfigFileField cff : cffList) {
			for (@SuppressWarnings("unused") Warning warn : cff.getWarnings()) {
				count++;
			}
		}
		return count;
	}
}
