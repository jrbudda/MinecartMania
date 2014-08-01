package com.afforess.minecartmania.signs.sensors;

import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.block.Sign;

public class SensorPowered extends GenericSensor {
    public SensorPowered(SensorType type, Sign sign, String name) {
        super(type, sign, name);
    }

    public void input(MMMinecart minecart) {
        if (minecart != null) {
            setState(minecart.isPoweredMinecart());
        } else {
            setState(false);
        }
    }
}
