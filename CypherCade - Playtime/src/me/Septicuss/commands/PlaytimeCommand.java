package me.Septicuss.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.Septicuss.Files;
import me.Septicuss.Files.FileType;
import me.Septicuss.managers.DatabaseManager;
import me.Septicuss.managers.PlaytimeManager;
import me.Septicuss.utils.MessageUtils;

public class PlaytimeCommand implements CommandExecutor {

	private static FileConfiguration config;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (config == null) {
			config = new Files().getFile(FileType.CONFIG);
		}

		// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //

		final int argsLength = args.length;

		if (argsLength == 0) {

			if (sender instanceof Player) {

				final Player player = (Player) sender;

				for (String messageString : config.getStringList("messages.playtime_command")) {

					final String formattedTime = PlaytimeManager.getPlaytime(player.getName()).getFormattedTime();
					messageString = messageString.replaceAll("%playtime%", formattedTime);
					messageString = messageString.replaceAll("%player%", player.getName());

					player.sendMessage(MessageUtils.color(messageString));

				}

				return true;
			}

			sender.sendMessage("§c/playtime [Player]");
			sender.sendMessage("§c/playtime reload");

			return true;
		}

		// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //

		final String arg = args[0];

		if (arg.equalsIgnoreCase("reload")) {

			if (!sender.isOp()) {
				final String noPerm = MessageUtils.color(config.getString("messages.no_permission"));
				sender.sendMessage(noPerm);
				return true;
			}

			new Files().loadFile(FileType.DATA);
			new Files().loadFile(FileType.CONFIG);
			new DatabaseManager().initialize();

			sender.sendMessage("§aReloaded");
			return true;
		}

		// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //

		if (PlaytimeManager.getPlaytime(arg) == null) {

			for (String messageString : config.getStringList("messages.unknown_player")) {
				messageString = MessageUtils.color(messageString);
				sender.sendMessage(MessageUtils.color(messageString));
			}

			return true;
		}

		for (String messageString : config.getStringList("messages.playtime_command")) {

			final String formattedTime = PlaytimeManager.getPlaytime(arg).getFormattedTime();
			messageString = messageString.replaceAll("%playtime%", formattedTime);
			messageString = messageString.replaceAll("%player%", arg);

			sender.sendMessage(MessageUtils.color(messageString));

		}

		return true;

		// -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- // -- //

	}

}
