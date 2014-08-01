package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;

public class MinecartCaughtEvent extends MinecartManiaEvent implements MinecartEvent {
    private MMMinecart minecart;
    private boolean action = false;

    public MinecartCaughtEvent(MMMinecart cart) {
        super("MinecartLaunchedEvent");
        minecart = cart;
    }

    public MMMinecart getMinecart() {
        return minecart;
    }

    public boolean isActionTaken() {
        return action;
    }

    public void setActionTaken(boolean Action) {
        this.action = Action;
    }
}
