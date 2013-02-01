package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Cow;

import com.afforess.minecartmania.MMMinecart;

public class SensorCow extends GenericSensor{
	
	public SensorCow(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MMMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Cow);
		}
		else {
			setState(false);
		}
	}
}