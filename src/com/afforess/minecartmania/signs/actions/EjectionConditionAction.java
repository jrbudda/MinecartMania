package com.afforess.minecartmania.signs.actions;

import org.bukkit.Bukkit;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.events.MinecartMeetsConditionEvent;
import com.afforess.minecartmania.signs.SignAction;

public class EjectionConditionAction extends SignAction {
	
	public boolean execute(MMMinecart minecart) {
		
		MinecartMeetsConditionEvent mmce = new MinecartMeetsConditionEvent(minecart, com.afforess.minecartmania.signs.SignManager.getOrCreateMMSign(loc).getLines());
		Bukkit.getServer().getPluginManager().callEvent(mmce);
		return mmce.isMeetCondition();
		
	}

	
	public boolean async() {
		return false;
	}

	
	public boolean process(String[] lines) {
		return false;
//		if (lines[0].toLowerCase().contains("[ejection")) {
//			return true;
//		}
//		return false;
	}

	
	public String getPermissionName() {
		return "ejectionconditionsign";
	}

	
	public String getFriendlyName() {
		return "Ejection Condition";
	}
}
