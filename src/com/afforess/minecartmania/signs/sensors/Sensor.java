package com.afforess.minecartmania.signs.sensors;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import com.afforess.minecartmania.MinecartManiaMinecart;

public interface Sensor{

	public void input(MinecartManiaMinecart minecart);
	
	public boolean output();
	
	public Sign getSign();
	
	public Location getLocation();
	
	public String getName();
	
	public SensorType getType();
	
	public boolean equals(Location location);
	
	public void kill();
	
	public SensorDataTable getDataTable();
}
