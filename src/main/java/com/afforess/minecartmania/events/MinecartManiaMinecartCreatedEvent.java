package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class MinecartManiaMinecartCreatedEvent extends MinecartManiaEvent {
    private MMMinecart minecart;

    public MinecartManiaMinecartCreatedEvent(MMMinecart cart) {
        super("MinecartManiaMinecartCreatedEvent");
        minecart = cart;
    }

    public MMMinecart getMinecart() {
        return minecart;
    }
}
