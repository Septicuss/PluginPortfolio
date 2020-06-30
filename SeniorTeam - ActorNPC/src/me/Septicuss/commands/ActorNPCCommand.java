package me.Septicuss.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.Septicuss.files.Files;
import me.Septicuss.files.Files.FileType;
import me.Septicuss.npc.NPCHandler;

public class ActorNPCCommand implements CommandExecutor {

	final static String command = "actornpc";

	public void initialize(JavaPlugin plugin) {
		plugin.getCommand(command).setExecutor(new ActorNPCCommand());
	}

	// /actornpc setlocation
	// /actornpc spawn

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("§cMust be a player.");
			return true;
		}

		final int argsLength = args.length;

		if (argsLength == 0) {
			sender.sendMessage("§e/actornpc setlocation");
			sender.sendMessage("§e/actornpc reload");
			sender.sendMessage("§e/actornpc spawn");
			return true;
		}

		final String firstArg = args[0];
		final Player player = (Player) sender;

		if (firstArg.equalsIgnoreCase("setlocation")) {

			final Location playerLoc = player.getLocation();
			final String world = playerLoc.getWorld().getName();
			final double x = playerLoc.getX();
			final double y = playerLoc.getY();
			final double z = playerLoc.getZ();

			final FileConfiguration CONFIG = Files.getConfig(FileType.CONFIG);
			final String path = "settings.location.";

			CONFIG.set(path + "world", world);
			CONFIG.set(path + "x", x);
			CONFIG.set(path + "y", y);
			CONFIG.set(path + "z", z);

			Files.saveConfig(FileType.CONFIG, CONFIG);
			sender.sendMessage("§aLocation saved.");
			new NPCHandler().initialize();

			return true;
		}

		if (firstArg.equalsIgnoreCase("spawn")) {
			NPCHandler.createNPC();
		}

		if (firstArg.equalsIgnoreCase("reload")) {
			sender.sendMessage("§aReloaded.");
			new NPCHandler().initialize();
		}

		return true;
	}

}
