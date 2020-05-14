package me.Septicuss.mineblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Septicuss.MineBlocks;
import me.Septicuss.objects.CustomId;

public class MineBlockSetup implements Listener {

	private static List<Player> settingUp = new ArrayList<>();

	private static HashMap<Player, Integer> setupStage = new HashMap<>();

	private static HashMap<Player, String> name = new HashMap<>();
	private static HashMap<Player, CustomId> baseState = new HashMap<>();
	private static HashMap<Player, CustomId> brokenState = new HashMap<>();
	private static HashMap<Player, ItemStack[]> drops = new HashMap<>();
	private static HashMap<Player, Integer> resetTime = new HashMap<>();

	public static void registerListener(final MineBlocks plugin) {
		plugin.getServer().getPluginManager().registerEvents(new MineBlockSetup(), plugin);
	}

	public static void beginSetup(final String blockName, Player player) {

		player.sendMessage("§eYou've entered §f§lMineBlock §esetup mode.");
		player.sendMessage("§eYou can type §c'cancel' §ein chat at any moment to exit setup mode.");
		player.sendMessage(" ");
		player.sendMessage("§f§l1. §eDrop a block that you want to be used as a base state of your MineBlock");

		settingUp.add(player);
		name.put(player, blockName);

		setSetupStage(player, 1);
	}

	private static void completeSetup(Player player) {

		final String blockName = name.get(player);
		final CustomId base = baseState.get(player);
		final CustomId broken = brokenState.get(player);
		final ItemStack[] items = drops.get(player);
		final int time = resetTime.get(player);

		final MineBlock newBlock = new MineBlock(blockName, base, broken, items, time);
		MineBlockManager.addMineBlock(newBlock);

		stopSetup(player);
	}

	private static void stopSetup(Player player) {

		settingUp.remove(player);
		setupStage.remove(player);
		name.remove(player);
		baseState.remove(player);
		brokenState.remove(player);
		drops.remove(player);
		resetTime.remove(player);

	}

	private static int getSetupStage(Player player) {
		return setupStage.get(player);
	}

	private static void setSetupStage(Player player, int stage) {
		setupStage.put(player, stage);
	}

	// -- Block setting / removing listeners --

	// -- Setup Listeners --

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();

		if (!settingUp.contains(player)) {
			return;
		}

		final String message = event.getMessage();

		// - Stopping the setup -
		if (message.equalsIgnoreCase("cancel")) {
			event.setCancelled(true);
			stopSetup(player);

			player.sendMessage("§cSetup canceled.");
			return;
		}

		// - 3rd stage, setting up drops -
		if (message.equalsIgnoreCase("start")) {

			if (getSetupStage(player) != 3) {
				return;
			}

			final Inventory inventory = Bukkit.createInventory(null, 54, "Drops");
			player.openInventory(inventory);
			player.sendMessage("§cInventory opened...");

			event.setCancelled(true);
			return;
		}

		// - 4th stage, setting up reset time -
		if (getSetupStage(player) == 4) {

			event.setCancelled(true);

			try {
				int testInt = Integer.valueOf(message);
				testInt = testInt + 1;
			} catch (Exception exc) {
				player.sendMessage("§cMust be a number.");
				return;
			}

			final int number = Integer.valueOf(message);
			resetTime.put(player, number);

			player.sendMessage(
					"§f§lDone. §eSetup has been completed. To set/remove the blocks, use §f/mineblock set [Name].");

			completeSetup(player);
		}

	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {

		final Player player = event.getPlayer();

		if (!settingUp.contains(player)) {
			return;
		}

		if (getSetupStage(player) > 2) {
			return;
		}

		final ItemStack dropped = event.getItemDrop().getItemStack();
		final int stage = getSetupStage(player);

		if (!dropped.getType().isBlock()) {
			player.sendMessage("§cDropped item must be a block!");
			event.setCancelled(true);
		}

		if (stage == 1) {

			final CustomId baseMaterial = new CustomId(dropped);

			baseState.put(player, baseMaterial);

			player.sendMessage("§eMaterial set!");
			player.sendMessage("§f§l2. §eDrop a block that you want to be used as a broken state of your MineBlock");

			setSetupStage(player, 2);

			event.setCancelled(true);
			return;
		}

		if (stage == 2) {

			final CustomId brokenMaterial = new CustomId(dropped);

			brokenState.put(player, brokenMaterial);

			player.sendMessage("§eMaterial set!");
			player.sendMessage(
					"§f§l3. §eNext you will set up the drops of the block, type 'start' in chat whenever you are ready. After you close the inventory, it will be saved.");

			setSetupStage(player, 3);
			event.setCancelled(true);
			return;
		}

	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {

		final Player player = (Player) event.getPlayer();

		if (!settingUp.contains(player)) {
			return;
		}

		final Inventory closedInventory = event.getInventory();

		if (closedInventory == null || closedInventory.getName() == null) {
			return;
		}

		final String inventoryName = closedInventory.getName();

		if (inventoryName.equalsIgnoreCase("Drops")) {

			List<ItemStack> savedDrops = new ArrayList<>();

			for (ItemStack item : closedInventory.getContents()) {
				if (item == null) {
					continue;
				}

				savedDrops.add(item);
			}

			drops.put(player, savedDrops.toArray(new ItemStack[0]));

			player.sendMessage("§eDrops set!");
			player.sendMessage(
					"§f§l4. §eNext you will set up the reset time of a block (How fast it will return to Its base state after broken). Type a §f§lnumber §ein chat. 0 if instant.");

			setSetupStage(player, 4);
			return;
		}

	}

}
