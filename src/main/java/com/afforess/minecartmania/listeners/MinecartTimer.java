package com.afforess.minecartmania.listeners;

import com.afforess.minecartmania.events.MinecartTimeEvent;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MinecartTimer implements Listener {

    @EventHandler
    public void onMinecartTimeEvent(MinecartTimeEvent event) {
        MMMinecart minecart = event.getMinecart();

        int timer = -1;
        if (minecart.isStandardMinecart()) {
            timer = com.afforess.minecartmania.config.Settings.EmptyMinecartKillTimer;
        } else if (minecart.isStorageMinecart()) {
            timer = com.afforess.minecartmania.config.Settings.EmptyStorageMinecartKillTimer;
        } else {
            timer = com.afforess.minecartmania.config.Settings.EmptyPoweredMinecartKillTimer;
        }


        boolean kill = minecart.getPassenger() == null && (!minecart.isStorageMinecart() || ((MMStorageCart) minecart).isEmpty());

        if (timer > 0) {
            if (kill) {
                //No timer, start counting
                if (minecart.getDataValue("Empty Timer") == null) {
                    minecart.setDataValue("Empty Timer", new Integer(timer));
                } else {
                    //Decrement timer
                    Integer timeLeft = (Integer) minecart.getDataValue("Empty Timer");
                    if (timeLeft > 1) {
                        minecart.setDataValue("Empty Timer", new Integer(timeLeft.intValue() - 1));
                    } else {
                        minecart.killOptionalReturn();
                    }
                }
            }
            //has passenger, resent counter if already set
            else {
                if (minecart.getDataValue("Empty Timer") != null) {
                    minecart.setDataValue("Empty Timer", null);
                }
            }
        } else if (timer == 0) {
            if (kill) {
                minecart.killOptionalReturn();
            }
        }
    }
}
