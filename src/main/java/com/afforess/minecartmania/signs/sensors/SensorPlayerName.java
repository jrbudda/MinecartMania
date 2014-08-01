package com.afforess.minecartmania.signs.sensors;

import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.block.Sign;

public class SensorPlayerName extends GenericSensor {
    private String player;

    public SensorPlayerName(SensorType type, Sign sign, String name, String player) {
        super(type, sign, name);
        this.player = player;
    }

    public void input(MMMinecart minecart) {
        boolean state = false;
        if (minecart != null) {
            if (minecart.hasPlayerPassenger()) {
                if (minecart.getPlayerPassenger().getName().equals(this.player)) {
                    state = true;
                }
            }
        }
        setState(state);
    }
}
