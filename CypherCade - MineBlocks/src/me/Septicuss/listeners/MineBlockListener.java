package me.Septicuss.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.Septicuss.MineBlocks;
import me.Septicuss.mineblock.MineBlock;
import me.Septicuss.mineblock.MineBlockManager;

@SuppressWarnings("deprecation")
public class MineBlockListener implements Listener {

	public static void registerMineBlockWandListener(final MineBlocks plugin) {
		plugin.getServer().getPluginManager().registerEvents(new MineBlockListener(), plugin);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {

		final Block brokenBlock = event.getBlock();
		final Location loc = brokenBlock.getLocation();
		final Player player = event.getPlayer();

		if (MineBlockManager.getMineBlockNameByLocation(loc) == null) {
			return;
		}

		event.setCancelled(true);

		final String mineBlockName = MineBlockManager.getMineBlockNameByLocation(loc);
		final MineBlock mineBlock = MineBlockManager.getMineBlockByName(mineBlockName);
		final ItemStack inHand = player.getInventory().getItemInMainHand();

		final String mineBlockType = mineBlock.getBaseState().getMaterial().toString();
		final String brokenBlockType = brokenBlock.getType().toString().replaceAll("GLOWING_", "");

		if (!mineBlockType.equalsIgnoreCase(brokenBlockType)
				|| brokenBlock.getData() != mineBlock.getBaseState().getData()) {
			return;
		}

		if (!isUsableTool(inHand, brokenBlock.getType())) {
			return;
		}

		int dropMultiplier = 1;

		if (inHand != null && inHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
			final int level = inHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
			dropMultiplier = level + 1;
		}

		for (ItemStack drop : mineBlock.getDrops()) {
			ItemStack toDrop = drop.clone();
			toDrop.setAmount(drop.getAmount() * dropMultiplier);
			loc.getWorld().dropItem(loc.clone().add(0.5, 1, 0.5), toDrop);
		}

		brokenBlock.setType(mineBlock.getBrokenState().getMaterial());

		Bukkit.getScheduler().runTaskLater(MineBlocks.getInstance(), () -> {
			brokenBlock.setType(mineBlock.getBaseState().getMaterial());
			brokenBlock.setData(mineBlock.getBaseState().getData());
		}, mineBlock.getResetTime());
	}

	public static boolean isUsableTool(ItemStack tool, Material block) {
		net.minecraft.server.v1_12_R1.Block nmsBlock = org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers
				.getBlock(block);
		if (nmsBlock == null) {
			return false;
		}
		net.minecraft.server.v1_12_R1.IBlockData data = nmsBlock.getBlockData();
		return data.getMaterial().isAlwaysDestroyable() || tool != null && tool.getType() != Material.AIR
				&& org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers.getItem(tool.getType())
						.canDestroySpecialBlock(data);
	}

}
