package com.afforess.minecartmania.commands;

import java.util.ArrayList;

import net.minecraft.server.v1_4_R1.Packet;
import net.minecraft.server.v1_4_R1.Packet29DestroyEntity;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public class HideMinecartCommand extends MinecartManiaCommand{

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
			for (MinecartManiaMinecart minecart : minecarts) {
				Packet packet = new Packet29DestroyEntity(minecart.getEntityId());
				player.getHandle().playerConnection.sendPacket(packet);
			}
		}
		sender.sendMessage(LocaleParser.getTextKey("AdminControlsHideMinecarts"));
		return true;
	}

}
