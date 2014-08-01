package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class MinecartActionEvent extends MinecartManiaEvent implements MinecartEvent {
    private boolean action = false;
    private MMMinecart minecart;

    public MinecartActionEvent(MMMinecart cart) {
        super("MinecartActionEvent");
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
