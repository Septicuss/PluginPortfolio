package me.Septicuss.managers;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Septicuss.Files;
import me.Septicuss.Files.FileType;
import me.Septicuss.Playtime;
import me.Septicuss.objects.PlaytimeData;

public class PlaytimeManager {

	private static HashMap<String, PlaytimeData> PLAYTIME_CACHE;
	private static boolean UUID_BASED;

	public static void initialize() {
		loadPlaytimeData();

		final FileConfiguration config = new Files().getFile(FileType.CONFIG);
		UUID_BASED = config.getBoolean("settings.uuid_based");

		for (Player player : Bukkit.getOnlinePlayers()) {
			addToCache(player);
		}

		runTimer();
	}

	/**
	 * 
	 * If not already added, adds the given player to the playtime cache list.
	 * 
	 * @param player Player to add
	 */
	public static void addToCache(Player player) {
		String playerName;

		if (UUID_BASED) {
			playerName = player.getUniqueId().toString();
		} else {
			playerName = player.getName();
		}

		if (!PLAYTIME_CACHE.containsKey(playerName)) {
			PlaytimeData newData = new PlaytimeData(playerName, 0);
			PLAYTIME_CACHE.put(playerName, newData);
		}

	}

	/**
	 * Updates the playtime value in specified players PlaytimeData
	 * 
	 * @param playerName Player whose time to update
	 * @param newValue
	 */
	public static void updateTime(String playerName, long newValue) {

		PlaytimeData newData;

		if (PLAYTIME_CACHE.containsKey(playerName)) {
			newData = PLAYTIME_CACHE.get(playerName);
			newData.setSeconds(newValue);
		} else {
			newData = new PlaytimeData(playerName, newValue);
		}

		PLAYTIME_CACHE.put(playerName, newData);

	}

	/**
	 * 
	 * Gets a PlaytimeData object from cache for specified player
	 * 
	 * @param playerName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static PlaytimeData getPlaytime(String playerName) {

		String searchFor;

		final OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(playerName);

		if (oPlayer == null) {
			return null;
		}

		if (UUID_BASED) {
			searchFor = oPlayer.getUniqueId().toString();
		} else {
			searchFor = oPlayer.getName();
		}

		if (!PLAYTIME_CACHE.containsKey(searchFor)) {
			return null;
		}

		final PlaytimeData data = PLAYTIME_CACHE.get(searchFor);
		return data;

	}

	/**
	 * Saving the cache into database or a local file if database isn't available.
	 * This method is only used on server shutdown.
	 */
	public static void savePlaytimeData() {

		if (PLAYTIME_CACHE == null || PLAYTIME_CACHE.isEmpty()) {
			return;
		}

		if (new DatabaseManager().isEnabled()) {

			final DatabaseManager databaseManager = new DatabaseManager();

			for (String username : PLAYTIME_CACHE.keySet()) {

				@SuppressWarnings("deprecation")
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

				if (offlinePlayer == null) {
					continue;
				}

				final PlaytimeData data = PLAYTIME_CACHE.get(username);

				databaseManager.createPlayer(offlinePlayer);

				databaseManager.updateVariable(offlinePlayer, data.getSeconds());

			}

		} else {
			System.out.println("[Playtime] Database seems to be disabled, saving player data to data.yml");

			final FileConfiguration data = new Files().getFile(FileType.DATA);

			for (String username : PLAYTIME_CACHE.keySet()) {

				@SuppressWarnings("deprecation")
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

				if (offlinePlayer == null) {
					continue;
				}

				final PlaytimeData playtimeData = PLAYTIME_CACHE.get(username);

				data.set("playtime." + username, playtimeData.getSeconds());

			}

			new Files().saveFile(FileType.DATA);
		}

	}

	/**
	 * Loading all of the PlaytimeData into cache from either a database or a local
	 * file
	 */
	private static void loadPlaytimeData() {

		PLAYTIME_CACHE = new HashMap<>();

		if (new DatabaseManager().isEnabled()) {

			final DatabaseManager databaseManager = new DatabaseManager();

			List<PlaytimeData> cacheList = databaseManager.getPlaytimeCache();

			for (PlaytimeData cache : cacheList) {
				PLAYTIME_CACHE.put(cache.getPlayer(), cache);
			}

		} else {

			final FileConfiguration data = new Files().getFile(FileType.DATA);

			if (!data.isSet("playtime")) {
				return;
			}

			for (String username : data.getConfigurationSection("playtime").getKeys(false)) {

				final String path = "playtime." + username;
				final long timePlayed = data.getLong(path);

				final PlaytimeData cache = new PlaytimeData(username, timePlayed);

				PLAYTIME_CACHE.put(username, cache);
			}

		}

	}

	/**
	 * Runs the actual playtime timer. Every 5 seconds all online players playtime
	 * in cache gets updated asynchronously
	 */
	private static void runTimer() {
		new BukkitRunnable() {
			public void run() {

				for (String username : PLAYTIME_CACHE.keySet()) {

					Player player;

					if (UUID_BASED) {
						player = Bukkit.getPlayer(UUID.fromString(username));
					} else {
						player = Bukkit.getPlayer(username);
					}

					if (player == null || !player.isOnline()) {
						continue;
					}

					PLAYTIME_CACHE.get(username).addSeconds(5);
				}

			}
		}.runTaskTimerAsynchronously(Playtime.getInstance(), 0, (5 * 20));
	}

}
