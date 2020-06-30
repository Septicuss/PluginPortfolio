package me.Septicuss.controller;

import org.bukkit.Location;

import me.Septicuss.npc.classes.NPCAction;

public interface NPCController {

	public void initialize(String name, Location location);

	public int getEntityId();

	public void setEntityId(int id);

	public void spawnNPC();

	public void playAction(NPCAction action);

	public void destroy();
	
}
