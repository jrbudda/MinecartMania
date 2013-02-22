package com.afforess.minecartmania.listeners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.events.MinecartActionEvent;
import com.afforess.minecartmania.farming.CactusFarming;
import com.afforess.minecartmania.farming.MelonFarming;
import com.afforess.minecartmania.farming.PumpkinFarming;
import com.afforess.minecartmania.farming.SugarFarming;
import com.afforess.minecartmania.farming.WheatFarming;
import com.afforess.minecartmania.farming.WoodFarming;

public class FarmingActionListener implements Listener {
	
	@EventHandler
	public void onMinecartActionEvent(MinecartActionEvent event) {
		if (!event.isActionTaken()) {
			MMMinecart minecart = event.getMinecart();
			if (minecart.isStorageMinecart()) {
				//Efficiency. Don't farm overlapping tiles repeatedly, waste of time
				int interval = minecart.getDataValue("Farm Interval") == null ? -1 : (Integer)minecart.getDataValue("Farm Interval");
				if (interval > 0) {
					minecart.setDataValue("Farm Interval", interval - 1);
				}
				else {
					MinecartManiaStorageCart cart = (MinecartManiaStorageCart) minecart;
					minecart.setDataValue("Farm Interval", cart.getFarmingRange()/2);
					WheatFarming.doAutoFarm(cart);
					WoodFarming.doAutoFarm(cart);
					CactusFarming.doAutoFarm(cart);
					SugarFarming.doAutoFarm(cart);
					MelonFarming.doAutoFarm(cart);
					PumpkinFarming.doAutoFarm(cart);
					com.afforess.minecartmania.farming.PotatoFarming.doAutoFarm(cart);
					com.afforess.minecartmania.farming.CarrotFarming.doAutoFarm(cart);
					com.afforess.minecartmania.farming.CocoaFarming.doAutoFarm(cart);
				}
			}
		}
	}
}
