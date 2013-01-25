package com.afforess.minecartmania.signs.actions;

import org.bukkit.Bukkit;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.events.MinecartMeetsConditionEvent;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;

public class EjectionConditionAction implements SignAction {
	private Sign sign;
	public EjectionConditionAction(Sign sign) {
		this.sign = sign;
	}

	
	public boolean execute(MinecartManiaMinecart minecart) {
		MinecartMeetsConditionEvent mmce = new MinecartMeetsConditionEvent(minecart, this.sign);
		Bukkit.getServer().getPluginManager().callEvent(mmce);
		return mmce.isMeetCondition();
	}

	
	public boolean async() {
		return false;
	}

	
	public boolean valid(Sign sign) {
		if (sign.getLine(0).toLowerCase().contains("ejection")) {
			sign.addBrackets();
			return true;
		}
		return false;
	}

	
	public String getName() {
		return "ejectionconditionsign";
	}

	
	public String getFriendlyName() {
		return "Ejection Condition Sign";
	}
}
