package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MMMinecart;

public class SensorPowered extends GenericSensor{
	public SensorPowered(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MMMinecart minecart) {
		if (minecart != null) {
			setState(minecart.isPoweredMinecart());
		}
		else {
			setState(false);
		}
	}
}
