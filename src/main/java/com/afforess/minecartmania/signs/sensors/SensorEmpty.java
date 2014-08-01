package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class SensorEmpty extends GenericSensor{

	public SensorEmpty(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MMMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() == null);
		}
		else {
			setState(false);
		}
	}
}
