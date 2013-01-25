package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Creeper;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorCreeper extends GenericSensor{
	
	public SensorCreeper(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Creeper);
		}
		else {
			setState(false);
		}
	}
}