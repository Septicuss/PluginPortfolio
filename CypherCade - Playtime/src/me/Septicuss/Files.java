package me.Septicuss;

import java.io.File;
import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Files {

	public enum FileType {
		DATA, CONFIG
	}

	private static File DATA;
	private static FileConfiguration DATA_CONFIG;

	private static File CONFIG;
	private static FileConfiguration CONFIG_CONFIG;

	public void sf(final Playtime plugin) {
		setupFiles(plugin);
	}

	private void setupFiles(final Playtime plugin) {

		DATA = new File(plugin.getDataFolder(), "data.yml");
		DATA_CONFIG = YamlConfiguration.loadConfiguration(DATA);
		DATA_CONFIG.options().copyDefaults(true);

		CONFIG = new File(plugin.getDataFolder(), "config.yml");
		CONFIG_CONFIG = YamlConfiguration.loadConfiguration(CONFIG);
		CONFIG_CONFIG.options().copyDefaults(true);

		setDefaults();

		saveFile(FileType.DATA);
		saveFile(FileType.CONFIG);
	}

	private void setDefaults() {
		CONFIG_CONFIG.addDefault("setting.uuid_based", true);
		CONFIG_CONFIG.addDefault("setting.time_format", "%d%%h%%m%%s%");

		CONFIG_CONFIG.addDefault("messages.unknown_player", Arrays.asList("§cSpecified player could not be found."));
		CONFIG_CONFIG.addDefault("messages.playtime_command", Arrays.asList("%player% has played for :", "%playtime%"));
		CONFIG_CONFIG.addDefault("messages.no_permission", "§cYou do not have the permission to execute this command.");

		CONFIG_CONFIG.addDefault("mysql.host", "localhost");
		CONFIG_CONFIG.addDefault("mysql.port", 3306);
		CONFIG_CONFIG.addDefault("mysql.database", "main");
		CONFIG_CONFIG.addDefault("mysql.password", "");
		CONFIG_CONFIG.addDefault("mysql.username", "root");

	}

	public FileConfiguration getFile(FileType fileType) {
		switch (fileType) {
		case DATA:
			return DATA_CONFIG;
		case CONFIG:
			return CONFIG_CONFIG;
		}
		return null;
	}

	public void loadFile(FileType fileType) {
		try {
			switch (fileType) {
			case DATA:
				DATA_CONFIG = YamlConfiguration.loadConfiguration(DATA);
			case CONFIG:
				CONFIG_CONFIG = YamlConfiguration.loadConfiguration(CONFIG);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

	public void saveFile(FileType fileType) {
		try {
			switch (fileType) {
			case DATA:
				DATA_CONFIG.save(DATA);
			case CONFIG:
				CONFIG_CONFIG.save(CONFIG);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}