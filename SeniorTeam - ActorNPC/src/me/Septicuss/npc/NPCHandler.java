package me.Septicuss.npc;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import me.Septicuss.ActorNPC;
import me.Septicuss.controller.NPCController;
import me.Septicuss.controller.NPCController_1_12_R1;
import me.Septicuss.controller.NPCController_1_13_R2;
import me.Septicuss.controller.NPCController_1_14_R1;
import me.Septicuss.controller.NPCController_1_15_R1;
import me.Septicuss.files.Files;
import me.Septicuss.files.Files.FileType;
import me.Septicuss.npc.classes.NPCAction;

public class NPCHandler {

	private static NPCController controller;

	private static int delay;
	private static String name;
	private static Location location;
	private static List<String> actionList;

	public void initialize() {

		final FileConfiguration CONFIG = Files.getConfig(FileType.CONFIG);
		final String locationPath = "settings.location.";

		final World world = Bukkit.getWorld(CONFIG.getString(locationPath + "world"));
		final double x = CONFIG.getDouble(locationPath + "x");
		final double y = CONFIG.getDouble(locationPath + "y");
		final double z = CONFIG.getDouble(locationPath + "z");

		delay = CONFIG.getInt("settings.action_delay_ticks");
		actionList = CONFIG.getStringList("actions");
		name = CONFIG.getString("settings.name");
		location = new Location(world, x, y, z);
		name = ChatColor.translateAlternateColorCodes('&', name);

		if (location == null) {
			throw new NullPointerException("Invalid NPC location");
		}

		destroyExistingNPC();

		if (setupController()) {
			controller.initialize(name, location);
		} else {
			System.out.println("[ACTORNPC] Unable to set up an NPC Controller for current server version!");
			return;
		}

	}

	public static void createNPC() {

		destroyExistingNPC();

		controller.spawnNPC();

		final List<String> actionList = NPCHandler.getActionList();
		final int delay = NPCHandler.getDelay();
		int taskTime = delay;

		for (String action : actionList) {
			taskTime += delay;

			Bukkit.getScheduler().runTaskLater(ActorNPC.getInstance(), () -> {
				NPCAction npcAction = NPCAction.valueOf(action.toUpperCase());
				controller.playAction(npcAction);
			}, taskTime);

		}

	}

	private boolean setupController() {
		String version;

		try {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			return false;
		}

		if (version.contains("v1_15")) {

			controller = new NPCController_1_15_R1();

		} else if (version.contains("v1_14")) {

			controller = new NPCController_1_14_R1();

		} else if (version.contains("v1_13")) {

			controller = new NPCController_1_13_R2();

		} else if (version.contains("v1_12")) {

			controller = new NPCController_1_12_R1();

		} else {

			controller = new NPCController_1_12_R1();
		}

		return controller != null;
	}

	public static List<String> getActionList() {
		return actionList;
	}

	public static int getDelay() {
		return delay;
	}

	public static void destroyExistingNPC() {
		if (controller != null)
			controller.destroy();
	}

	public static NPCController getController() {
		return controller;
	}
}
