package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.util.Vector;

public class MinecartLaunchedEvent extends MinecartManiaEvent implements MinecartEvent {
    private MMMinecart minecart;
    private boolean action = false;
    private Vector launchSpeed;

    public MinecartLaunchedEvent(MMMinecart cart, Vector speed) {
        super("MinecartLaunchedEvent");
        minecart = cart;
        launchSpeed = speed;
    }

    public MMMinecart getMinecart() {
        return minecart;
    }

    public Vector getLaunchSpeed() {
        return launchSpeed.clone();
    }

    public void setLaunchSpeed(Vector speed) {
        launchSpeed = speed;
    }

    public boolean isActionTaken() {
        return action;
    }

    public void setActionTaken(boolean Action) {
        this.action = Action;
    }
}
