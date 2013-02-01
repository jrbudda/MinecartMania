package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;

import com.afforess.minecartmania.MMMinecart;

public class SensorAnimal extends GenericSensor{

	public SensorAnimal(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MMMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Animals);
		}
		else {
			setState(false);
		}
	}

}
