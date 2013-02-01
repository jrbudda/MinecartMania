package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Creeper;

import com.afforess.minecartmania.MMMinecart;

public class SensorCreeper extends GenericSensor{
	
	public SensorCreeper(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MMMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Creeper);
		}
		else {
			setState(false);
		}
	}
}