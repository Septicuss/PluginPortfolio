package me.Septicuss;

import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.Septicuss.commands.ActorNPCCommand;
import me.Septicuss.files.Files;
import me.Septicuss.files.Files.FileType;
import me.Septicuss.npc.NPCHandler;

public class ActorNPC extends JavaPlugin {

	private static final int jumpHeight = 3800;
	private static ActorNPC instance;

	public void onEnable() {

		instance = this;

		Files.initialize(this);
		setConfigDefaults();

		new ActorNPCCommand().initialize(this);
		new NPCHandler().initialize();
	}

	public void onDisable() {
		NPCHandler.destroyExistingNPC();

	}

	public static ActorNPC getInstance() {
		return instance;
	}

	public static int getJumpHeight() {
		return jumpHeight;
	}

	private void setConfigDefaults() {
		final FileConfiguration CONFIG = Files.getConfig(FileType.CONFIG);
		CONFIG.addDefault("settings.action_delay_ticks", 20);
		CONFIG.addDefault("settings.name", "NPC Name");
		CONFIG.addDefault("settings.location.world", "world");
		CONFIG.addDefault("settings.location.x", 0);
		CONFIG.addDefault("settings.location.y", 0);
		CONFIG.addDefault("settings.location.z", 0);
		CONFIG.addDefault("actions",
				Arrays.asList("JUMP", "JUMP", "CROUCH", "UNCROUCH", "CROUCH", "HIT", "UNCROUCH", "DESTROY"));

		CONFIG.options().copyDefaults(true);
		Files.saveConfig(FileType.CONFIG, CONFIG);
	}

}
