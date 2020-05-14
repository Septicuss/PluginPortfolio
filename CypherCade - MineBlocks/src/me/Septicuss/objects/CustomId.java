package me.Septicuss.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class CustomId {

	private int id;
	private int data;

	public CustomId(ItemStack item) {
		this.id = item.getType().getId();
		this.data = item.getDurability();
	}

	public CustomId(int id, short data) {
		this.id = id;
		this.data = data;
	}

	public CustomId(String string) {
		final String[] args = string.split("!");

		this.id = Integer.valueOf(args[0]);
		this.data = Integer.valueOf(args[1]);
	}

	public String serialize() {
		return id + "!" + data;
	}

	public int getId() {
		return id;
	}

	public byte getData() {
		return (byte) data;
	}

	public Material getMaterial() {
		return Material.getMaterial(id);
	}

}
