package com.afforess.minecartmania.events;

import com.afforess.minecartmania.debug.DebugTimer;
import org.bukkit.event.HandlerList;

public abstract class MinecartManiaEvent extends org.bukkit.event.Event {
    private static final HandlerList handlers = new HandlerList();
    private final DebugTimer timer;

    protected MinecartManiaEvent(String name) {
        super();
        timer = new DebugTimer(name);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void logProcessTime() {
        timer.logProcessTime();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
