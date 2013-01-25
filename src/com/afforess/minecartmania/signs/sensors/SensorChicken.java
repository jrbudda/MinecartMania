package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Chicken;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorChicken extends GenericSensor{
	
	public SensorChicken(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Chicken);
		}
		else {
			setState(false);
		}
	}
}