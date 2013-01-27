package com.afforess.minecartmania.listeners;

import java.util.ArrayList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.events.MinecartActionEvent;
import com.afforess.minecartmania.events.MinecartCaughtEvent;
import com.afforess.minecartmania.events.MinecartLaunchedEvent;
import com.afforess.minecartmania.events.MinecartManiaMinecartCreatedEvent;
import com.afforess.minecartmania.events.MinecartManiaMinecartDestroyedEvent;
import com.afforess.minecartmania.events.MinecartMotionStartEvent;
import com.afforess.minecartmania.events.MinecartMotionStopEvent;
import com.afforess.minecartmania.events.MinecartPassengerEjectEvent;
import com.afforess.minecartmania.events.MinecartTimeEvent;
import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmania.signs.actions.EjectionAction;
import com.afforess.minecartmania.signs.actions.EjectionConditionAction;
import com.afforess.minecartmania.signs.actions.HoldSignData;
import com.afforess.minecartmaniacore.utils.SignCommands;
import com.afforess.minecartmaniacore.utils.SignUtils;

public class SignsActionListener implements Listener{

	//Test 1
	@EventHandler
	public void onMinecartActionEvent(MinecartActionEvent event) {
		final MinecartManiaMinecart minecart = event.getMinecart();
		if (minecart.isOnRails()){
			ArrayList<com.afforess.minecartmania.signs.MMSign> list = SignUtils.getAdjacentMinecartManiaSignList(minecart.getLocation(), 2);
	//		com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info("signs: " + list.size());
			for (com.afforess.minecartmania.signs.MMSign sign : list) {
				com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().info("Executing sign " + sign.getLine(0));
				sign.executeActions(minecart);
			}
			SignCommands.updateSensors(minecart);
		}	
	}

	@EventHandler
	public void onMinecartPassengerEjectEvent(MinecartPassengerEjectEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		ArrayList<com.afforess.minecartmania.signs.MMSign> list = SignUtils.getAdjacentMinecartManiaSignList(minecart.getLocation(), 2);
		boolean success = false;
		boolean found = false;
		for (com.afforess.minecartmania.signs.MMSign sign : list) {
			if (sign.hasSignAction(EjectionConditionAction.class)) {
				found = true;
				if (sign.executeAction(minecart, EjectionConditionAction.class)) {
					success = true;
					break;
				}
			}
		}
		if (found && !success) {
			event.setCancelled(true);
		}
		if (minecart.getDataValue("Eject At Sign") == null) {
			list = SignUtils.getAdjacentMinecartManiaSignList(minecart.getLocation(), 8, true);
			SignUtils.sortByDistance(minecart.getLocation().getBlock(), list);
			for (com.afforess.minecartmania.signs.MMSign sign : list) {
				if (sign.executeAction(minecart, EjectionAction.class)) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onMinecartLaunchedEvent(MinecartLaunchedEvent event) {
		if (event.isActionTaken()) {
			return;
		}
		if (event.getMinecart().getDataValue("hold sign data") != null) {
			event.setActionTaken(true);
			return;
		}
	}

	@EventHandler
	public void onMinecartCaughtEvent(MinecartCaughtEvent event) {
		if (event.isActionTaken()) {
			return;
		}
		if (event.getMinecart().hasPlayerPassenger() && SignCommands.doPassPlayer(event.getMinecart())) {
			event.setActionTaken(true);
			return;
		}
	}

	@EventHandler
	public void onMinecartManiaMinecartCreatedEvent(MinecartManiaMinecartCreatedEvent event) {
		SignCommands.updateSensors(event.getMinecart());
	}

	@EventHandler
	public void onMinecartTimeEvent(MinecartTimeEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		HoldSignData data = (HoldSignData)minecart.getDataValue("hold sign data");
		/*if (data == null) {
			try {
				data = MinecartManiaSignCommands.instance.getDatabase().find(HoldSignData.class).where().idEq(minecart.minecart.getEntityId()).findUnique();
			}
			catch (Exception e) {
				List<HoldSignData> list = MinecartManiaSignCommands.instance.getDatabase().find(HoldSignData.class).where().idEq(minecart.minecart.getEntityId()).findList();
				MinecartManiaSignCommands.instance.getDatabase().delete(list);
			}
		}*/
		if (data != null) {
			data.setTime(data.getTime() - 1);
			com.afforess.minecartmania.signs.MMSign sign = SignManager.getOrCreateMMSign(data.getSignLocation());
			if (sign == null) {
				minecart.setMotion(data.getMotion());
				minecart.setDataValue("hold sign data", null);
				minecart.setDataValue("HoldForDelay", null);
				return;
			}
			//update sign counter
			if (data.getLine() < sign.getNumLines() && data.getLine() > -1) {
				if (data.getTime() > 0) {
					sign.setLine(data.getLine(), "[Holding For " + data.getTime() + "]");

				}
				else {
					sign.setLine(data.getLine(), "");
				}
			}

			if (data.getTime() == 0) {
				minecart.setMotion(data.getMotion());
				minecart.setDataValue("hold sign data", null);
				minecart.setDataValue("HoldForDelay", null);
				//MinecartManiaSignCommands.instance.getDatabase().delete(data);
			}
			else {
				minecart.setDataValue("hold sign data", data);
				//MinecartManiaSignCommands.instance.getDatabase().update(data);
			}
		}
	}

	@EventHandler
	public void onMinecartManiaMinecartDestroyedEvent(MinecartManiaMinecartDestroyedEvent event) {
		SignCommands.updateSensors(event.getMinecart(), null);
	}

	@EventHandler
	public void onMinecartMotionStopEvent(MinecartMotionStopEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		if (minecart.getDataValue("Lock Cart") != null) {
			minecart.setDataValue("Lock Cart", null);
			if (minecart.hasPlayerPassenger()) {
				minecart.getPlayerPassenger().sendMessage(LocaleParser.getTextKey("SignCommandsMinecartUnlocked"));
			}
		}
	}

	@EventHandler
	public void onMinecartMotionStartEvent(MinecartMotionStartEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		if (minecart.getDataValue("HoldForDelay") != null) {
			minecart.stopCart();
			HoldSignData data = (HoldSignData)minecart.getDataValue("hold sign data");
			minecart.teleport(data.getMinecartLocation());
		}
	}




}
