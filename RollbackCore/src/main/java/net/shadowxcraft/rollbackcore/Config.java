/**
* Copyright (C) 2016 ShadowXCraft Server - All rights reserved.
*/

package net.shadowxcraft.rollbackcore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteStreams;

/**
 * @author lizardfreak321
 */
public class Config {
	private static JavaPlugin plugin = null;

	// Used to store the amount of time all running tasks added up should target
	// in one tick.
	public static int targetTime = 25;

	private Config() {
	}

	public final boolean Loaded() {
		return plugin != null;
	}

	public static final void loadConfigs(JavaPlugin newPlugin) {
		plugin = newPlugin;
		// Loads config
		loadResource(plugin, "config.yml");
		// Checks the config for any needed changes.
		checkConfig();
		// Loads the target time.
		Config.targetTime = getTargetTime();
		// Alerts user though console.
		plugin.getLogger().info("Configs loaded!");
	}

	// Loads the resource if it isn't there.
	public final static File loadResource(Plugin plugin, String resource) {
		File folder = plugin.getDataFolder();
		// Creates the plugin folder if it doesn't exist.
		if (!folder.exists())
			folder.mkdir();
		File resourceFile = new File(folder, resource);
		// Copes the files that are saved in the jar to the plugin folder if
		// they do not exist in the plugin folder.
		try {
			if (!resourceFile.exists()) {
				resourceFile.createNewFile();
				try (InputStream in = plugin.getResource(resource);
						OutputStream out = new FileOutputStream(resourceFile)) {
					ByteStreams.copy(in, out);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resourceFile;
	}

	private static final void checkConfig() {
		File file = new File(plugin.getDataFolder() + "/config.yml");
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

		if (yaml.contains("Config.rollback.rate"))
			yaml.set("Config.rollback.rate", null);

		if (!yaml.contains("Config.rollback.targettime")) {
			yaml.set("Config.rollback.targettime", 25);
		}

		if (!yaml.contains("configversion")) {
			yaml.set("configversion", 1.0);
		}
		try {
			yaml.save(file);
		} catch (IOException e) {
			Main.plugin.getLogger()
					.info("Unable to save config! This may cause an issue if there is an incompatible config value.");
			e.printStackTrace();
		}
	}

	// Gets Block Rate from the config.
	private static final int getTargetTime() {
		File file = new File(plugin.getDataFolder() + "/config.yml");
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

		// Validates input.
		int finalizedTargetTime = yaml.getInt("Config.rollback.targettime", 25);
		if (finalizedTargetTime > 50) {
			finalizedTargetTime = 50;
			Main.plugin.getLogger().info(
					"Your set value for targettime was too high! Setting to 50ms (I highly recommend that you lower this, an operation that takes an entire tick WILL lag the server)");
		} else if (finalizedTargetTime < 1) {
			finalizedTargetTime = 1;
			Main.plugin.getLogger().info(
					"Your set value for targettime was too low! Setting to 1ms (Pastes copy, paste, and other rollback operations will be slow)");
		}

		return finalizedTargetTime;
	}

	// WARNING: Case sensitive!
	public static final int[] getArenaXYZ(String arena) {
		// For debug reasons.
		if (plugin == null) {
			System.out.println("Plugin is null!");
		}

		// Stores it an array so that they can all be returned at the same time.
		int[] xyz = { 0, 0, 0 };

		// Loads the config.yml
		File file = new File(plugin.getDataFolder() + "/config.yml");
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

		// Gets the values.
		xyz[0] = yaml.getInt("Config.arenas." + arena + ".x");
		xyz[1] = yaml.getInt("Config.arenas." + arena + ".y");
		xyz[2] = yaml.getInt("Config.arenas." + arena + ".z");
		return xyz;
	}

	// Returns false if it fails.
	public static final boolean setArenaXYZ(String arena, int x, int y, int z) {
		// For debug reasons.
		if (plugin == null) {
			System.out.println("Plugin is null!");
		}

		// Loads the config.yml
		File file = new File(plugin.getDataFolder() + "/config.yml");
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

		// Sets the values in the YAML file.
		yaml.set("Config.arenas." + arena + ".x", x);
		yaml.set("Config.arenas." + arena + ".y", y);
		yaml.set("Config.arenas." + arena + ".z", z);

		// Tries to save the file.
		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
