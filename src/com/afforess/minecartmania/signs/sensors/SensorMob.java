package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Monster;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorMob extends GenericSensor{

	public SensorMob(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Monster);
		}
		else {
			setState(false);
		}
	}
}
