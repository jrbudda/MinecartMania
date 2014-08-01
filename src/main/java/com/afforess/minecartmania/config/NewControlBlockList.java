package com.afforess.minecartmania.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.signs.SignAction;

public class NewControlBlockList {
	public static HashMap<Item,NewControlBlock> controlBlocks = new HashMap<Item,NewControlBlock>();

	public static Collection<NewControlBlock> getControlBlockList() {
		return controlBlocks.values();
	}

	public static boolean isControlBlock(Item item) {
		return controlBlocks.containsKey(item);
	}

	public static NewControlBlock getControlBlock(Item item) {
		if (item == null) return null;
		if(controlBlocks.containsKey(item)) return controlBlocks.get(item);
		else return null;
	}


	public static boolean hasSignAction(Block block, Class<? extends SignAction> action){
		if (block ==null) return false;
		Item i = Item.getItem(block);
		if (i == null) return false;
		if (!isControlBlock(i)) return false;
		return getControlBlock(i).hasSignAction(action);

	}

	public static Map<Location,NewControlBlock> getControlBlocksNearby(Location loc, int range) {
		Map<Location,NewControlBlock> out = new HashMap<Location,NewControlBlock>();
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					
					Item i = Item.getItem(loc.getWorld().getBlockAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz));
					
					if(i !=null && isControlBlock(i)){
						out.put(new Location(loc.getWorld(),loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz), getControlBlock(i));
					}

				}
			}
		}
		return out;
	}

}
