package me.Septicuss.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketUtils {

	public static void sendPacket(Object packet) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			sendPacket(packet, player);
		});
	}

	private static void sendPacket(Object packet, Player player) {
		try {
			Method sendPacket = NMSUtils.getNMSClass("PlayerConnection").getMethod("sendPacket",
					NMSUtils.getNMSClass("Packet"));
			sendPacket.invoke(NMSUtils.getConnection(player), packet);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException
				| NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
