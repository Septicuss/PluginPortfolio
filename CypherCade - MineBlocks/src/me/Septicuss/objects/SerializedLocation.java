package me.Septicuss.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SerializedLocation {

	private String world;
	private int x;
	private int y;
	private int z;

	public SerializedLocation(Location location) {
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
	}

	public SerializedLocation(String serializedString) {
		final String[] args = serializedString.split("/");

		this.world = args[0];
		this.x = Integer.valueOf(args[1]);
		this.y = Integer.valueOf(args[2]);
		this.z = Integer.valueOf(args[3]);
	}

	public SerializedLocation(String world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String serialize() {
		final String serializedString = world + "/" + x + "/" + y + "/" + z;
		return serializedString;
	}

	public Location deserialize() {
		final World deserializedWorld = Bukkit.getWorld(world);
		if (deserializedWorld == null)
			return null;
		final Location deserializedLocation = new Location(deserializedWorld, x, y, z);
		return deserializedLocation;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

}
