package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Animals;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorAnimal extends GenericSensor{

	public SensorAnimal(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Animals);
		}
		else {
			setState(false);
		}
	}

}
