package com.afforess.minecartmania.events;

import com.afforess.minecartmania.minecarts.MMMinecart;
import org.bukkit.event.Cancellable;

public class MinecartKillEvent extends MinecartManiaEvent implements Cancellable {
    private boolean cancelled = false;
    private MMMinecart minecart;

    protected MinecartKillEvent(MMMinecart minecart) {
        super("MinecartKillEvent");
        this.minecart = minecart;
    }

    public MMMinecart getMinecart() {
        return this.minecart;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

}
