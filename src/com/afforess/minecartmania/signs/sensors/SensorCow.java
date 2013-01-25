package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Cow;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorCow extends GenericSensor{
	
	public SensorCow(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Cow);
		}
		else {
			setState(false);
		}
	}
}