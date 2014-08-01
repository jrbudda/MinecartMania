package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class MinecartIntersectionEvent extends MinecartManiaEvent implements MinecartEvent {
    private boolean action = false;
    private MMMinecart minecart;

    public MinecartIntersectionEvent(MMMinecart cart) {
        super("MinecartIntersectionEvent");
        minecart = cart;
    }


    public MMMinecart getMinecart() {
        return minecart;
    }

    public boolean isActionTaken() {
        return action;
    }

    public void setActionTaken(boolean b) {
        action = b;
    }

}
