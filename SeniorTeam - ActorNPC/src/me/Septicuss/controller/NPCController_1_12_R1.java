package me.Septicuss.controller;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import me.Septicuss.ActorNPC;
import me.Septicuss.npc.classes.NPCAction;
import me.Septicuss.utils.NMSUtils;
import me.Septicuss.utils.PacketUtils;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

public class NPCController_1_12_R1 implements NPCController {

	private String name;
	private int entityId;
	private EntityPlayer npc;
	private Location location;

	@Override
	public void initialize(String name, Location location) {
		this.name = name;
		this.location = location;
	}

	@Override
	public void spawnNPC() {

		GameProfile gameprofile = new GameProfile(UUID.randomUUID(), name);

		MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

		EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameprofile, new PlayerInteractManager(nmsWorld));
		Player npcPlayer = npc.getBukkitEntity().getPlayer();

		this.npc = npc;
		this.entityId = npc.getBukkitEntity().getEntityId();

		npcPlayer.setPlayerListName(name);
		npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc);
		PacketPlayOutNamedEntitySpawn entitySpawn = new PacketPlayOutNamedEntitySpawn(npc);

		PacketUtils.sendPacket(playerInfo);
		PacketUtils.sendPacket(entitySpawn);

	}

	@Override
	public void playAction(NPCAction action) {
		DataWatcher dataWatcher = npc.getDataWatcher();
		boolean metadata = false;

		switch (action) {

		case CROUCH:
			metadata = true;
			npc.setSneaking(true);
			break;

		case HIT:
			setAnimation((byte) 0);
			break;

		case JUMP:

			final long jumpHeight = ActorNPC.getJumpHeight();

			PacketPlayOutRelEntityMove move = new PacketPlayOutRelEntityMove(entityId, (long) 0, (long) jumpHeight,
					(long) 0, true);
			PacketUtils.sendPacket(move);

			Bukkit.getScheduler().runTaskLater(ActorNPC.getInstance(), () -> {
				PacketPlayOutRelEntityMove moveDown = new PacketPlayOutRelEntityMove(entityId, (long) 0,
						(long) -jumpHeight, (long) 0, true);
				PacketUtils.sendPacket(moveDown);
			}, 5);

			break;

		case UNCROUCH:
			metadata = true;
			npc.setSneaking(false);
			break;

		case DESTROY:
			destroy();
			break;

		default:
			break;
		}

		if (metadata) {
			PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityId, dataWatcher, false);
			PacketUtils.sendPacket(packet);
		}
	}

	@Override
	public void destroy() {
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityId);
		PacketUtils.sendPacket(packet);
	}

	public void setAnimation(byte animation) {
		try {
			PacketPlayOutAnimation playAnimation = new PacketPlayOutAnimation();
			NMSUtils.setField(playAnimation, "a", entityId);
			NMSUtils.setField(playAnimation, "b", (int) animation);
			PacketUtils.sendPacket(playAnimation);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	@Override
	public int getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(int id) {
		entityId = id;
	}

}
