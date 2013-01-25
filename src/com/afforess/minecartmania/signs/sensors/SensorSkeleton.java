package com.afforess.minecartmania.signs.sensors;

import org.bukkit.block.Sign;
import org.bukkit.entity.Skeleton;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SensorSkeleton extends GenericSensor{
	
	public SensorSkeleton(SensorType type, Sign sign, String name) {
		super(type, sign, name);
	}

	public void input(MinecartManiaMinecart minecart) {
		if (minecart != null) {
			setState(minecart.getPassenger() instanceof Skeleton);
		}
		else {
			setState(false);
		}
	}
}
