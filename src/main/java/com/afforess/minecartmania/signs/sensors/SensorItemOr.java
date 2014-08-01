package com.afforess.minecartmania.signs.sensors;

import com.afforess.minecartmania.entity.AbstractItem;
import com.afforess.minecartmania.entity.MinecartManiaPlayer;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.List;

public class SensorItemOr extends GenericSensor {
    private List<AbstractItem> detect = new ArrayList<AbstractItem>();

    public SensorItemOr(SensorType type, Sign sign, String name, List<AbstractItem> item) {
        super(type, sign, name);
        this.detect = item;
    }

    public void input(MMMinecart minecart) {
        boolean state = false;
        if (minecart != null) {
            for (AbstractItem item : detect) {
                if (minecart.isStorageMinecart()) {
                    if (((MMStorageCart) minecart).amount(item.type()) > (item.isInfinite() ? 0 : item.getAmount())) {
                        state = true;
                        break;
                    }
                } else if (minecart.hasPlayerPassenger()) {
                    MinecartManiaPlayer player = MinecartManiaWorld.getMinecartManiaPlayer(minecart.getPlayerPassenger());
                    if (player.amount(item.type()) > (item.isInfinite() ? 0 : item.getAmount())) {
                        state = true;
                        break;
                    }
                }
            }
        }
        setState(state);
    }
}
