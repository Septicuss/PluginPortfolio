package me.Septicuss.files;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.Septicuss.MineBlocks;

public class Files {

	private static MineBlocks plugin;

	public enum FileType {
		CONFIG, MINEBLOCKS, DROPS, LOCATIONS
	}

	public static void initialize(final MineBlocks plugin) {

		Files.plugin = plugin;

		// -- Creating new files if they dont exist
		for (FileType fileType : FileType.values()) {
			final String fileName = getFileName(fileType);
			final File newFile = new File(plugin.getDataFolder(), fileName);

			FileConfiguration config = getConfig(fileType);
			config.options().copyDefaults(true);
			saveFile(newFile, config);

		}

		// Setting defaults in a config
		final FileConfiguration config = getConfig(FileType.CONFIG);
		config.addDefault("messages.help", Arrays.asList("&e&lMine&f&lBlocks", "&f/mineblocks &esetup [Name]",
				"§f/mineblocks §eset [Name]", "&f/mineblocks &edelete [Name]", "&f/mineblocks &elist"));

		config.options().copyDefaults(true);
		saveFile(FileType.CONFIG, config);
	}

	/**
	 * Save a config to a file using FileType
	 * 
	 * @param fileType File to save the config to
	 * @param config   Config to save into the file
	 */
	public static void saveFile(FileType fileType, FileConfiguration config) {
		final File file = getFile(fileType);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save a config to a file
	 * 
	 * @param file   File to save the config to
	 * @param config Config to save into the file
	 */
	public static void saveFile(File file, FileConfiguration config) {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a FileConfiguration
	 * 
	 * @param fileType File to get
	 * @return Config
	 */
	public static FileConfiguration getConfig(FileType fileType) {
		final File file = getFile(fileType);
		return YamlConfiguration.loadConfiguration(file);
	}

	/**
	 * Get a file for a certain FileType
	 * 
	 * @param fileType File to find
	 * @return File
	 */
	public static File getFile(FileType fileType) {
		final String fileName = getFileName(fileType);
		return new File(plugin.getDataFolder(), fileName);
	}

	/**
	 * Get a .yml file name for chosen enum
	 * 
	 * @param fileType File name to get
	 * @return Lowercase file name
	 */
	public static String getFileName(FileType fileType) {
		return (fileType.toString().toLowerCase() + ".yml");
	}

	// ---------------------

}