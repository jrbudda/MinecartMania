package com.afforess.minecartmania.signs.sensors;

import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.block.Sign;

public class SensorStation extends GenericSensor {

    private Sign sign;

    public SensorStation(SensorType type, Sign sign, String name) {
        super(type, sign, name);
        this.sign = sign;
    }

    public void input(MMMinecart minecart) {

        if (minecart != null) {
            if (minecart.hasPlayerPassenger()) {
                setState(sign.getLine(2).equals(minecart.getDestination()));
            }
        } else {
            setState(false);
        }

    }

}
