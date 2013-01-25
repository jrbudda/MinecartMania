package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Zombie;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorZombie extends GenericSensor{
	
	public SensorZombie(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Zombie);
		}
		else {
			setState(false);
		}
	}
}
