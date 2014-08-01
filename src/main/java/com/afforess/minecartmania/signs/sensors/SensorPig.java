package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Pig;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class SensorPig extends GenericSensor{
	

	public SensorPig(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MMMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Pig);
		}
		else {
			setState(false);
		}
	}
}