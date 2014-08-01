package com.afforess.minecartmania.utils;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockUtils {
	
	public static HashSet<Block> getAdjacentBlocks(Location location, int range) {
		//default constructor size is purely for efficiency reasons - and to show off my math skills
		HashSet<Block> blockList = new HashSet<Block>();
		Block center = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					blockList.add(center.getRelative(dx, dy, dz));
				}
			}
		}
		return blockList;
	}
	
	
	public static HashSet<Block> getBlocksBeneath(Location location, int range) {
		HashSet<Block> blockList = new HashSet<Block>();
		for (int dy = -range; dy <= 0; dy++) {
			blockList.add(location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY()+dy, location.getBlockZ()));
		}
		return blockList;
	}
}