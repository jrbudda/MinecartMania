package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Sheep;

import com.afforess.minecartmania.MMMinecart;

public class SensorSheep extends GenericSensor{
	
	public SensorSheep(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MMMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Sheep);
		}
		else {
			setState(false);
		}
	}
}