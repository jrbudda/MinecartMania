package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

public class MinecartElevatorEvent extends MinecartManiaEvent implements Cancellable {

    private MMMinecart minecart;
    private boolean cancelled = false;
    private Location location;

    public MinecartElevatorEvent(MMMinecart minecart, Location teleport) {
        super("MinecartElevatorEvent");
        this.minecart = minecart;
        this.location = teleport;
    }

    public MMMinecart getMinecart() {
        return this.minecart;
    }

    public Location getTeleportLocation() {
        return location.clone();
    }

    public void setTeleportLocation(Location location) {
        this.location = location;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}