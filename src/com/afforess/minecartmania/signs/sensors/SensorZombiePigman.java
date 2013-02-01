package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.PigZombie;

import com.afforess.minecartmania.MMMinecart;

public class SensorZombiePigman extends GenericSensor {

	public SensorZombiePigman(SensorType type, Sign sign, String name){
		super(type, sign, name);
	}

	
	public void input(MMMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof PigZombie);
		}
		else {
			setState(false);
		}
		
	}
	
	
}
