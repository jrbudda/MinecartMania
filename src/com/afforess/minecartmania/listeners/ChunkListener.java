package com.afforess.minecartmania.listeners;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaWorld;

public class ChunkListener implements Listener{
	public static final int CHUNK_RANGE = 4;

	@EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
    	if (!event.isCancelled()) {
    		if (Settings.isKeepMinecartsLoaded()) {
    			ArrayList<MMMinecart> minecarts = MinecartManiaWorld.getMinecartManiaMinecartList();
    			for (MMMinecart minecart : minecarts) {
    				if (Math.abs(event.getChunk().getX() - minecart.getLocation().getBlock().getChunk().getX()) > CHUNK_RANGE) {
    					continue;
    				}
    				if (Math.abs(event.getChunk().getZ() - minecart.getLocation().getBlock().getChunk().getZ()) > CHUNK_RANGE) {
    					continue;
    				}
    				event.setCancelled(true);
    				return;
    			}
    		}
    	}
    }
}
