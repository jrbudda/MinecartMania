package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Pig;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorPig extends GenericSensor{
	

	public SensorPig(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Pig);
		}
		else {
			setState(false);
		}
	}
}