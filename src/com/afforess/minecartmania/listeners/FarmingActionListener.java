package com.afforess.minecartmania.listeners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.events.MinecartActionEvent;
import com.afforess.minecartmania.farming.CactusFarming;
import com.afforess.minecartmania.farming.MelonFarming;
import com.afforess.minecartmania.farming.PumpkinFarming;
import com.afforess.minecartmania.farming.SugarFarming;
import com.afforess.minecartmania.farming.WheatFarming;
import com.afforess.minecartmania.farming.WoodFarming;
import com.afforess.minecartmaniacore.entity.MinecartManiaStorageCart;

public class FarmingActionListener implements Listener {
	
	@EventHandler
	public void onMinecartActionEvent(MinecartActionEvent event) {
		if (!event.isActionTaken()) {
			MinecartManiaMinecart minecart = event.getMinecart();
			if (minecart.isStorageMinecart()) {
				//Efficiency. Don't farm overlapping tiles repeatedly, waste of time
				int interval = minecart.getDataValue("Farm Interval") == null ? -1 : (Integer)minecart.getDataValue("Farm Interval");
				if (interval > 0) {
					minecart.setDataValue("Farm Interval", interval - 1);
				}
				else {
					minecart.setDataValue("Farm Interval", minecart.getRange()/2);
					WheatFarming.doAutoFarm((MinecartManiaStorageCart)minecart);
					WoodFarming.doAutoFarm((MinecartManiaStorageCart)minecart);
					CactusFarming.doAutoFarm((MinecartManiaStorageCart)minecart);
					SugarFarming.doAutoFarm((MinecartManiaStorageCart)minecart);
					MelonFarming.doAutoFarm((MinecartManiaStorageCart)minecart);
					PumpkinFarming.doAutoFarm((MinecartManiaStorageCart)minecart);
				}
			}
		}
	}
}
