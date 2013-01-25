package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorAll extends GenericSensor{
	
	public SensorAll(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(true);
		}
		else {
			setState(false);
		}
	}
}
