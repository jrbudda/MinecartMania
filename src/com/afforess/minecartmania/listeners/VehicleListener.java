package com.afforess.minecartmania.listeners;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmaniacore.utils.SignCommands;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;
public class VehicleListener implements Listener{

	public static void toggleBlockFromEntering(Player player) {
		if (isBlockedFromEntering(player)) {
			MinecartManiaWorld.getMinecartManiaPlayer(player).setDataValue("Blocked From Entering Minecarts", null);
		}
		else {
			MinecartManiaWorld.getMinecartManiaPlayer(player).setDataValue("Blocked From Entering Minecarts", Boolean.TRUE);
		}
	}

	public static boolean isBlockedFromEntering(Player player) {
		return MinecartManiaWorld.getMinecartManiaPlayer(player).getDataValue("Blocked From Entering Minecarts") != null;
	}

	public static int getMinecartKillTimer() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("EmptyMinecartKillTimer",60);
	}

	public static int getStorageMinecartKillTimer() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("EmptyStorageMinecartKillTimer",60);
	}

	public static int getPoweredMinecartKillTimer() {
		return (Integer)MinecartManiaWorld.getConfigurationValue("EmptyPoweredMinecartKillTimer",60);
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (event.isCancelled()) {
			return;
		}


		if (event.getVehicle() instanceof Minecart) {   		
			if (event.getEntered() instanceof Player) {
				if (isBlockedFromEntering((Player)event.getEntered())) {
					event.setCancelled(true);
					((Player)event.getEntered()).sendMessage(LocaleParser.getTextKey("AdminControlsBlockMinecartEntry"));
					return;
				}
			}

			MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)event.getVehicle());

			if (minecart !=null && minecart.getDataValue("Lock Cart") != null && minecart.isMoving()) {
				if (minecart.hasPlayerPassenger()) {
					minecart.getPlayerPassenger().sendMessage(LocaleParser.getTextKey("SignCommandsMinecartLockedError"));
				}
				event.setCancelled(true);
				return;
			}

			SignCommands.updateSensors(minecart);

		}
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getVehicle() instanceof Minecart) {
			MinecartManiaMinecart minecart = MinecartManiaWorld.getOrCreateMMMinecart((Minecart)event.getVehicle());
			SignCommands.updateSensors(minecart);
		}
	}

	@EventHandler
	public void onVehicleBounce(org.bukkit.event.vehicle.VehicleBlockCollisionEvent event) {
		if (event.getVehicle() instanceof Minecart) {
			com.afforess.minecartmania.MinecartMania.log("Bounce! " + event.getVehicle().getLocation());
		}
	}

}
