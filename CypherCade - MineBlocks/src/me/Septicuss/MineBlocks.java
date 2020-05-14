package me.Septicuss;

import org.bukkit.plugin.java.JavaPlugin;

import me.Septicuss.commands.MineBlocksCommand;
import me.Septicuss.files.Files;
import me.Septicuss.listeners.MineBlockListener;
import me.Septicuss.listeners.MineBlockWandListener;
import me.Septicuss.mineblock.MineBlockManager;
import me.Septicuss.mineblock.MineBlockSetup;

public class MineBlocks extends JavaPlugin {

	private static MineBlocks instance;

	public void onEnable() {

		instance = this;

		Files.initialize(this);
		MineBlockManager.initialize();

		registerCommands();
		registerListeners();
	}

	public void onDisable() {
	}

	public static MineBlocks getInstance() {
		return instance;
	}

	private void registerCommands() {
		MineBlocksCommand.registerMineBlocksCommand(this);
	}

	private void registerListeners() {
		MineBlockSetup.registerListener(this);
		MineBlockListener.registerMineBlockWandListener(this);
		MineBlockWandListener.registerMineBlockWandListener(this);
	}

}
