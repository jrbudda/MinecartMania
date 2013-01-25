package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorEntity extends GenericSensor{
	
	public SensorEntity(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() != null);
		}
		else {
			setState(false);
		}
	}
}
