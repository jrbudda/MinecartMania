package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.entity.MinecartManiaWorld;

public class SensorStation extends GenericSensor {
	
	private Sign sign;

	public SensorStation(SensorType type, Sign sign, String name){
		super(type, sign, name);
		this.sign = sign;
	}
	
	public void input(MinecartManiaMinecart minecart) {
		
		if (minecart != null) {
			if (minecart.hasPlayerPassenger()){
				setState(sign.getLine(2).equals(MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger()).getLastStation()));
			}
		}
		else {
			setState(false);
		}
		
	}

}
