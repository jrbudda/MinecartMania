package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.event.Cancellable;

public class MinecartSpeedMultiplierEvent extends MinecartManiaEvent implements Cancellable {

    private final double origMultiplier;
    private MMMinecart minecart;
    private double multiplier;

    public MinecartSpeedMultiplierEvent(MMMinecart minecart, double multiplier) {
        super("MinecartSpeedAlterEvent");
        this.minecart = minecart;
        this.multiplier = multiplier;
        this.origMultiplier = multiplier;
    }

    public MMMinecart getMinecart() {
        return this.minecart;
    }

    public double getSpeedMultiplier() {
        return this.multiplier;
    }

    public void setSpeedMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public boolean isCancelled() {
        return this.multiplier == 1.0D;
    }

    public void setCancelled(boolean cancelled) {
        if (cancelled)
            this.multiplier = 1.0D;
        else
            this.multiplier = this.origMultiplier;
    }

}
