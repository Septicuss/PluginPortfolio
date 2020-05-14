package me.Septicuss.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Septicuss.MineBlocks;
import me.Septicuss.files.Files;
import me.Septicuss.files.Files.FileType;
import me.Septicuss.mineblock.MineBlockManager;
import me.Septicuss.mineblock.MineBlockSetup;
import me.Septicuss.utils.MessageUtil;

public class MineBlocksCommand implements CommandExecutor {

	// /mineblocks setup [Name]

	public static void registerMineBlocksCommand(final MineBlocks plugin) {
		plugin.getCommand("mineblocks").setExecutor(new MineBlocksCommand());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		final String cmdName = cmd.getName();
		final int argsLength = args.length;

		if (cmdName.equalsIgnoreCase("mineblocks")) {

			if (!sender.isOp()) {
				return true;
			}

			if (argsLength == 0) {
				for (String line : MessageUtil.getMessage("help")) {
					sender.sendMessage(line);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("setup")) {

				if (!(sender instanceof Player)) {
					sender.sendMessage("§cYou must be a player to set up a block.");
				}

				if (argsLength == 1) {
					sender.sendMessage("§f/mineblocks §csetup [Name]");
					return true;
				}

				final Player player = (Player) sender;

				final String name = args[1];

				MineBlockSetup.beginSetup(name, player);

			}

			if (args[0].equalsIgnoreCase("set")) {

				if (!(sender instanceof Player)) {
					sender.sendMessage("§cYou must be a player to get a wand.");
				}

				if (argsLength == 1) {
					sender.sendMessage("§f/mineblocks §cset [Name]");
					return true;
				}

				final Player player = (Player) sender;
				final String name = args[1];

				if (!MineBlockManager.existsByName(name)) {
					player.sendMessage("§cMineBlock not found.");
					return true;
				}

				ItemStack wand = new ItemStack(Material.STICK);
				ItemMeta wandMeta = wand.getItemMeta();
				wandMeta.setDisplayName("§fMineBlock Wand");

				List<String> lore = new ArrayList<>();
				lore.add("§8" + name.toUpperCase());

				wandMeta.setLore(lore);
				wand.setItemMeta(wandMeta);

				player.getInventory().addItem(wand);
				player.sendMessage("§eWand given.");
			}

			if (args[0].equalsIgnoreCase("delete")) {

				if (argsLength == 1) {
					sender.sendMessage("§f/mineblocks §cdelete [Name]");
					return true;
				}

				final String name = args[1];

				if (!MineBlockManager.existsByName(name)) {
					sender.sendMessage("§cMineBlock not found.");
					return true;
				}

				MineBlockManager.removeMineBlock(name);
				sender.sendMessage("§cMineBlock was deleted.");

			}

			if (args[0].equalsIgnoreCase("list")) {

				final FileConfiguration mineBlocks = Files.getConfig(FileType.MINEBLOCKS);

				sender.sendMessage("§eCurrent active §fMineBlocks : ");

				if (!mineBlocks.isSet("mineblocks")) {
					sender.sendMessage("§fNone");
					return true;
				}

				for (String name : mineBlocks.getConfigurationSection("mineblocks").getKeys(false)) {
					sender.sendMessage("§f- " + name.toUpperCase());
				}
			}

		}

		return true;
	}

}
