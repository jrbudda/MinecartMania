package com.afforess.minecartmania.commands;

import java.util.ArrayList;

import net.minecraft.server.v1_4_R1.Packet;
import net.minecraft.server.v1_4_R1.Packet23VehicleSpawn;
import net.minecraft.server.v1_4_R1.Packet29DestroyEntity;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public class RedrawMinecartCommand extends MinecartManiaCommand{

	public boolean isPlayerOnly() {
		return false;
	}

	public CommandType getCommand() {
		return CommandType.Hide;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player[] online = Bukkit.getServer().getOnlinePlayers();
		ArrayList<MinecartManiaMinecart> minecarts = MinecartManiaWorld.getMinecartManiaMinecartList();
		for (Player p : online) {
			CraftPlayer player = (CraftPlayer)p;
			for (final MinecartManiaMinecart minecart : minecarts) {
				final Entity passenger = minecart.getPassenger();
				minecart.eject();
				Packet packet = new Packet29DestroyEntity(minecart.getEntityId());
				player.getHandle().playerConnection.sendPacket(packet);
				packet = null;
				final Vector motion = minecart.getMotion();
				minecart.stopCart();
				if (minecart.isStandardMinecart()) {
					packet = new Packet23VehicleSpawn(minecart.getHandle(), 10);
				}
				else if (minecart.isPoweredMinecart()) {
					packet = new Packet23VehicleSpawn(minecart.getHandle(), 12);
				}
				else if (minecart.isStorageMinecart()) {
					packet = new Packet23VehicleSpawn(minecart.getHandle(), 11);
				}
				player.getHandle().playerConnection.sendPacket(packet);
				if (passenger != null) {
					Runnable update = new Runnable() {
						public void run() {
							minecart.setMotion(motion);
							minecart.setPassenger(passenger);
						}
					};
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), update);
					
				}
			}
		}
		sender.sendMessage(LocaleParser.getTextKey("AdminControlsRedrawMinecarts"));
		return true;
	}

}
