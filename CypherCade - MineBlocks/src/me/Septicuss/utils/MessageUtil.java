package me.Septicuss.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import me.Septicuss.files.Files;
import me.Septicuss.files.Files.FileType;

public class MessageUtil {

	public static List<String> getMessage(String name) {
		final FileConfiguration config = Files.getConfig(FileType.CONFIG);
		final String path = "messages" + "." + name;

		final List<String> message = config.getStringList(path);

		if (message == null || message.isEmpty()) {
			return null;
		}

		List<String> translatedMessage = new ArrayList<>();

		for (String line : message) {
			translatedMessage.add(ChatColor.translateAlternateColorCodes('&', line));
		}

		return translatedMessage;
	}

}
