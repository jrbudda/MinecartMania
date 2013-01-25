package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.utils.ItemUtils;

public class SensorItemHeld extends GenericSensor {
	
	private Sign sign;
	public SensorItemHeld(SensorType type, Sign sign, String name){
		super(type, sign, name);
		this.sign = sign;
	}
	
	public void input(MinecartManiaMinecart minecart) {
		
		if (minecart != null) {
			if (minecart.hasPlayerPassenger() && minecart.getPlayerPassenger().getItemInHand() != null){
				setState(minecart.getPlayerPassenger().getItemInHand().equals(ItemUtils.getItemStringToMaterial(sign.getLine(2))));
			}
		}
		else {
			setState(false);
		}
		
	}

}
