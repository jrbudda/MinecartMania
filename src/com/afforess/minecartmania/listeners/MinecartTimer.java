package com.afforess.minecartmania.listeners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.events.MinecartTimeEvent;
import com.afforess.minecartmaniacore.entity.MinecartManiaStorageCart;

public class MinecartTimer implements Listener{

	@EventHandler
	public void onMinecartTimeEvent(MinecartTimeEvent event) {
		MinecartManiaMinecart minecart = event.getMinecart();
		
		int timer = -1;
		if (minecart.isStandardMinecart()) {
			timer = VehicleListener.getMinecartKillTimer();
		}
		else if (minecart.isStorageMinecart()) {
			timer = VehicleListener.getStorageMinecartKillTimer();
		}
		else {
			timer = VehicleListener.getPoweredMinecartKillTimer();
		}
		boolean kill = minecart.getPassenger() == null && (!minecart.isStorageMinecart() || ((MinecartManiaStorageCart)minecart).isEmpty());
		
		if (timer > 0) {
			if (kill) {
				//No timer, start counting
				if (minecart.getDataValue("Empty Timer") == null) {
					minecart.setDataValue("Empty Timer", new Integer(timer));
				}
				else {
					//Decrement timer
					Integer timeLeft = (Integer)minecart.getDataValue("Empty Timer");
					if (timeLeft > 1) {
						minecart.setDataValue("Empty Timer", new Integer(timeLeft.intValue()-1));
					}
					else {
						minecart.kill();
					}
				}
			}
			//has passenger, resent counter if already set
			else {
				if (minecart.getDataValue("Empty Timer") != null) {
					minecart.setDataValue("Empty Timer", null);
				}
			}
		}
		else if (timer == 0) {
			if (kill) {
				minecart.kill();
			}
		}
	}
}
