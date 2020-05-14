package me.Septicuss.mineblock;

import org.bukkit.inventory.ItemStack;

import me.Septicuss.objects.CustomId;

public class MineBlock {

	private String name;
	private CustomId baseState;
	private CustomId brokenState;
	private ItemStack[] drops;
	private int resetTime;

	public MineBlock(String name, CustomId baseStage, CustomId brokenState, ItemStack[] drops, int resetTime) {
		this.name = name;
		this.baseState = baseStage;
		this.brokenState = brokenState;
		this.drops = drops;
		this.resetTime = resetTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CustomId getBaseState() {
		return baseState;
	}

	public void setBaseState(CustomId baseState) {
		this.baseState = baseState;
	}

	public CustomId getBrokenState() {
		return brokenState;
	}

	public void setBrokenState(CustomId brokenState) {
		this.brokenState = brokenState;
	}

	public ItemStack[] getDrops() {
		return drops;
	}

	public void setDrops(ItemStack[] drops) {
		this.drops = drops;
	}

	public int getResetTime() {
		return resetTime;
	}

	public void setResetTime(int resetTime) {
		this.resetTime = resetTime;
	}

}
