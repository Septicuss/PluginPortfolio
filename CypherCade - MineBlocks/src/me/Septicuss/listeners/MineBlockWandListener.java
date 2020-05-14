package me.Septicuss.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.Septicuss.MineBlocks;
import me.Septicuss.mineblock.MineBlock;
import me.Septicuss.mineblock.MineBlockManager;

@SuppressWarnings("deprecation")
public class MineBlockWandListener implements Listener {

	public static void registerMineBlockWandListener(final MineBlocks plugin) {
		plugin.getServer().getPluginManager().registerEvents(new MineBlockWandListener(), plugin);
	}

	@EventHandler
	public void wandInteract(PlayerInteractEvent event) {

		final Action action = event.getAction();
		final Player player = event.getPlayer();

		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
			return;
		}

		final ItemStack inHand = player.getInventory().getItemInMainHand();

		if (inHand == null || !inHand.hasItemMeta() || !inHand.getItemMeta().hasDisplayName()) {
			return;
		}

		if (!inHand.getItemMeta().getDisplayName().contains("MineBlock Wand")) {
			return;
		}

		if (!player.isOp()) {
			return;
		}

		final Block clicked = event.getClickedBlock();

		if (action == Action.RIGHT_CLICK_BLOCK) {
			final String mineBlockName = ChatColor.stripColor(inHand.getItemMeta().getLore().get(0));
			final MineBlock mineBlock = MineBlockManager.getMineBlockByName(mineBlockName);

			clicked.setType(mineBlock.getBaseState().getMaterial());
			clicked.setData(mineBlock.getBaseState().getData());

			MineBlockManager.addLocation(clicked.getLocation(), mineBlock);
			player.sendMessage("§eYou have set a new MineBlock called §f'" + mineBlock.getName() + "§e'.");
		}

		if (action == Action.LEFT_CLICK_BLOCK) {

			final Location loc = clicked.getLocation();

			if (MineBlockManager.getMineBlockNameByLocation(loc) == null) {
				player.sendMessage("§cThis block is not a MineBlock.");
				return;
			}

			final String mineBlockName = MineBlockManager.getMineBlockNameByLocation(loc);
			final MineBlock mineBlock = MineBlockManager.getMineBlockByName(mineBlockName);

			if (player.isSneaking()) {
				MineBlockManager.removeLocation(loc);
				player.sendMessage("§cMineBlock removed.");
			} else {
				player.sendMessage("§eThis block is a MineBlock called '§f" + mineBlock.getName() + "§e'");
			}

		}

	}

	@EventHandler
	public void wandDrop(PlayerDropItemEvent event) {

		final ItemStack item = event.getItemDrop().getItemStack();

		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
			return;
		}

		if (item.getItemMeta().getDisplayName().contains("MineBlock Wand")) {
			event.getItemDrop().remove();
			return;
		}
	}

}
