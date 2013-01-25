package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorPlayer extends GenericSensor{


	public SensorPlayer(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.hasPlayerPassenger());
		}
		else {
			setState(false);
		}
	}
}
