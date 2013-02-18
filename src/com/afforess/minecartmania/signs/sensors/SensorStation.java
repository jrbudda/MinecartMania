package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.entity.MinecartManiaWorld;

public class SensorStation extends GenericSensor {
	
	private Sign sign;

	public SensorStation(SensorType type, Sign sign, String name){
		super(type, sign, name);
		this.sign = sign;
	}
	
	public void input(MMMinecart minecart) {
		
		if (minecart != null) {
			if (minecart.hasPlayerPassenger()){
				setState(sign.getLine(2).equals(minecart.getDestination()));
			}
		}
		else {
			setState(false);
		}
		
	}

}
