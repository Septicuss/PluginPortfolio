package me.Septicuss.mineblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.Septicuss.files.Files;
import me.Septicuss.files.Files.FileType;
import me.Septicuss.objects.CustomId;
import me.Septicuss.objects.SerializedLocation;
import me.Septicuss.utils.SerializationUtils;

public class MineBlockManager {

	private static List<MineBlock> activeBlocks = new ArrayList<>();
	private static HashMap<String, String> blockLocations = new HashMap<>();

	public static void initialize() {

		loadMineBlocks();
		loadLocations();

	}

	
	// ----------- MineBlock -----------

	public static void addMineBlock(MineBlock mineBlock) {
		// - Removing from list if already exists -
		if (!activeBlocks.isEmpty()) {
			for (MineBlock activeBlock : activeBlocks) {
				if (activeBlock.getName().equalsIgnoreCase(mineBlock.getName())) {
					activeBlocks.remove(activeBlock);
				}
			}
		}

		// - Adding the new mineBlock -
		activeBlocks.add(mineBlock);
		saveMineBlocks();
		loadMineBlocks();
	}
	
	public static void removeMineBlock(String name) {

		final MineBlock mineBlock = getMineBlockByName(name);

		if (!activeBlocks.isEmpty()) {
			activeBlocks.remove(mineBlock);
		}

		List<String> toRemove = new ArrayList<>();

		for (String loc : blockLocations.keySet()) {
			if (!blockLocations.get(loc).equalsIgnoreCase(mineBlock.getName())) {
				continue;
			}
			toRemove.add(loc);
		}

		for (String loc : toRemove) {
			removeLocation(new SerializedLocation(loc).deserialize());
		}

		final FileConfiguration mineBlocks = Files.getConfig(FileType.MINEBLOCKS);
		final FileConfiguration drops = Files.getConfig(FileType.DROPS);

		mineBlocks.set("mineblocks." + mineBlock.getName(), null);
		drops.set("drops." + mineBlock.getName(), null);

		Files.saveFile(FileType.MINEBLOCKS, mineBlocks);
		Files.saveFile(FileType.DROPS, drops);
	}

	public static void loadMineBlocks() {

		final FileConfiguration mineBlocks = Files.getConfig(FileType.MINEBLOCKS);

		activeBlocks.clear();

		if (!mineBlocks.isSet("mineblocks")) {
			return;
		}

		for (String name : mineBlocks.getConfigurationSection("mineblocks").getKeys(false)) {
			final String path = "mineblocks." + name + ".";

			final CustomId baseState = new CustomId(mineBlocks.getString(path + "baseState"));
			final CustomId brokenState = new CustomId(mineBlocks.getString(path + "brokenState"));
			final int resetTime = mineBlocks.getInt(path + "resetTime");
			final ItemStack[] drops = loadDrops(name);

			MineBlock mineBlock = new MineBlock(name, baseState, brokenState, drops, resetTime);
			activeBlocks.add(mineBlock);
		}

	}


	public static void saveMineBlocks() {
		final FileConfiguration mineBlocks = Files.getConfig(FileType.MINEBLOCKS);

		if (activeBlocks.isEmpty()) {
			return;
		}

		for (MineBlock activeBlock : activeBlocks) {

			final String path = "mineblocks." + activeBlock.getName() + ".";

			mineBlocks.set(path + "name", activeBlock.getName());
			mineBlocks.set(path + "baseState", activeBlock.getBaseState().serialize());
			mineBlocks.set(path + "brokenState", activeBlock.getBrokenState().serialize());
			mineBlocks.set(path + "resetTime", activeBlock.getResetTime());

			saveDrops(activeBlock);
		}

		Files.saveFile(FileType.MINEBLOCKS, mineBlocks);
	}

	public static boolean existsByName(String name) {
		for (MineBlock activeBlock : activeBlocks) {
			if (!activeBlock.getName().equalsIgnoreCase(name)) {
				continue;
			}
			return true;
		}
		return false;
	}

	public static MineBlock getMineBlockByName(String name) {
		for (MineBlock activeBlock : activeBlocks) {
			if (!activeBlock.getName().equalsIgnoreCase(name)) {
				continue;
			}

			return activeBlock;
		}

		return null;
	}
	
	// ----------- Locations -----------
	
	public static String getMineBlockNameByLocation(Location location) {
		final String serializedLocation = new SerializedLocation(location).serialize();
		return blockLocations.get(serializedLocation);
	}

	public static void removeLocation(Location location) {
		final FileConfiguration locations = Files.getConfig(FileType.LOCATIONS);

		final String serializedLocation = new SerializedLocation(location).serialize();

		blockLocations.remove(serializedLocation);

		locations.set("locations." + serializedLocation, null);
		Files.saveFile(FileType.LOCATIONS, locations);
	}

	public static void addLocation(Location location, MineBlock mineBlock) {
		final FileConfiguration locations = Files.getConfig(FileType.LOCATIONS);

		final String serializedLocation = new SerializedLocation(location).serialize();
		blockLocations.put(serializedLocation, mineBlock.getName());

		locations.set("locations." + serializedLocation, mineBlock.getName());
		Files.saveFile(FileType.LOCATIONS, locations);
	}

	public static void loadLocations() {

		final FileConfiguration locations = Files.getConfig(FileType.LOCATIONS);

		if (!locations.isSet("locations")) {
			return;
		}

		for (String location : locations.getConfigurationSection("locations").getKeys(false)) {
			blockLocations.put(location, locations.getString("locations." + location));
		}
	}

	// ----------- Drops -----------

	private static void saveDrops(MineBlock mineBlock) {
		final ItemStack[] items = mineBlock.getDrops();

		if (items.length == 0) {
			return;
		}

		final String serializedItems = SerializationUtils.itemStackArrayToBase64(items);
		final String path = "drops." + mineBlock.getName();

		final FileConfiguration drops = Files.getConfig(FileType.DROPS);
		drops.set(path, serializedItems);

		Files.saveFile(FileType.DROPS, drops);
	}

	private static ItemStack[] loadDrops(String mineBlock) {
		try {
			final FileConfiguration drops = Files.getConfig(FileType.DROPS);

			if (!drops.isSet("drops." + mineBlock)) {
				return null;
			}

			final String base64String = drops.getString("drops." + mineBlock);
			final ItemStack[] deserialized = SerializationUtils.itemStackArrayFromBase64(base64String);

			return deserialized;
		} catch (Exception exc) {
			return null;
		}
	}

}
