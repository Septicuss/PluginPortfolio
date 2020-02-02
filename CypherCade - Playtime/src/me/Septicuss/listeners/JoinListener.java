package me.Septicuss.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.Septicuss.managers.PlaytimeManager;

public class JoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		final Player player = event.getPlayer();

		PlaytimeManager.addToCache(player);

	}

}
