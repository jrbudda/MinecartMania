package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class MinecartMotionStopEvent extends MinecartManiaEvent {
    private MMMinecart minecart;

    public MinecartMotionStopEvent(MMMinecart cart) {
        super("MinecartMotionStopEvent");
        minecart = cart;
    }

    public MMMinecart getMinecart() {
        return minecart;
    }
}
