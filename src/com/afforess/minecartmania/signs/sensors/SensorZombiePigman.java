package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.PigZombie;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorZombiePigman extends GenericSensor {

	public SensorZombiePigman(SensorType type, Sign sign, String name){
		super(type, sign, name);
	}

	
	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof PigZombie);
		}
		else {
			setState(false);
		}
		
	}
	
	
}
