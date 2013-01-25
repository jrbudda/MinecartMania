package com.afforess.minecartmania.stations;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public interface Direction {
	CompassDirection direction(MinecartManiaMinecart input, String str);
}
