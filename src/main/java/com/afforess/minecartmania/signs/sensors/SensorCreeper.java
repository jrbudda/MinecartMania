package com.afforess.minecartmania.signs.sensors;

import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.block.Sign;
import org.bukkit.entity.Creeper;

public class SensorCreeper extends GenericSensor {

    public SensorCreeper(SensorType type, Sign sign, String name) {
        super(type, sign, name);
    }

    public void input(MMMinecart minecart) {
        if (minecart != null) {
            setState(minecart.getPassenger() instanceof Creeper);
        } else {
            setState(false);
        }
    }
}