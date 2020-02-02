package me.Septicuss;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.Septicuss.commands.PlaytimeCommand;
import me.Septicuss.listeners.JoinListener;
import me.Septicuss.managers.DatabaseManager;
import me.Septicuss.managers.PlaytimeManager;
import me.Septicuss.placeholder.PlaytimePlaceholder;

public class Playtime extends JavaPlugin {

	private static Playtime INSTANCE;

	public void onEnable() {

		INSTANCE = this;

		registerFiles();
		registerCommands();
		registerListeners();

		new DatabaseManager().initialize();
		PlaytimeManager.initialize();
		
		 // Small check to make sure that PlaceholderAPI is installed
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
              new PlaytimePlaceholder(this).register();
        }
	}

	public void onDisable() {
		PlaytimeManager.savePlaytimeData();
	}

	private void registerFiles() {
		new Files().sf(this);
	}

	private void registerCommands() {
		getCommand("playtime").setExecutor(new PlaytimeCommand());
	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new JoinListener(), this);
	}

	public static Playtime getInstance() {
		return INSTANCE;
	}

}
