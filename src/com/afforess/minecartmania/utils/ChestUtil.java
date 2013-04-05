package com.afforess.minecartmania.utils;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.afforess.minecartmania.entity.Item;
import com.afforess.minecartmania.entity.MinecartManiaChest;
import com.afforess.minecartmania.signs.MMSign;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class ChestUtil {

	public static boolean isNoCollection(MinecartManiaChest chest) {
		ArrayList<MMSign> signList = SignUtils.getAdjacentMMSignList(chest.getLocation(), 2);
		for (MMSign sign : signList) {
			for (int i = 0; i < sign.getNumLines(); i++) {
				if (sign.getLine(i).toLowerCase().contains("[no collection")) {
					sign.setLine(i, "[No Collection]");
					return true;
				}
			}
		}
		return false;
	}



	public static Location getSpawnLocationSignOverride(MinecartManiaChest chest) {
		ArrayList<MMSign> signList = SignUtils.getAdjacentMMSignList(chest.getLocation(), 2);
		Location spawn = chest.getLocation();
		Location result = null;
		Block neighbor = chest.getNeighborChest() != null ? chest.getNeighborChest().getLocation().getBlock() : null;

		for (MMSign sign : signList) {
			for (int i = 0; i < sign.getNumLines(); i++) {

				if (sign.getLine(i).toLowerCase().contains("east")) {
					sign.setLine(i, "[Chest East]");
					result = getAdjacentTrack(spawn.getBlock(), BlockFace.EAST);
					if ((result == null) && (neighbor != null))
						return getAdjacentTrack(neighbor, BlockFace.EAST);
					else
						return result;
				}
				if (sign.getLine(i).toLowerCase().contains("south")) {
					sign.setLine(i, "[Chest South]");
					result = getAdjacentTrack(spawn.getBlock(), BlockFace.SOUTH);
					if ((result == null) && (neighbor != null))
						return getAdjacentTrack(neighbor, BlockFace.SOUTH);
					else
						return result;
				}
				if (sign.getLine(i).toLowerCase().contains("west")) {
					sign.setLine(i, "[Chest West]");
					result = getAdjacentTrack(spawn.getBlock(), BlockFace.WEST);
					if ((result == null) && (neighbor != null))
						return getAdjacentTrack(neighbor, BlockFace.WEST);
					else
						return result;
				}
				if (sign.getLine(i).toLowerCase().contains("north")) {
					sign.setLine(i, "[Chest North]");
					result = getAdjacentTrack(spawn.getBlock(), BlockFace.NORTH);
					if ((result == null) && (neighbor != null))
						return getAdjacentTrack(neighbor, BlockFace.NORTH);
					else
						return result;
				}
			}
		}



		return null;
	}

	private static Location getAdjacentTrack(Block center, BlockFace dir) {
		if (MinecartUtils.isTrack(center.getRelative(dir))) {
			return center.getRelative(dir).getLocation();
		}
		if (center.getRelative(dir).getTypeId() == Item.CHEST.getId() && MinecartUtils.isTrack(center.getRelative(dir).getRelative(dir))) {
			return center.getRelative(dir).getRelative(dir).getLocation();
		}
		return null;
	}

	public static CompassDirection getDirection(Location loc1,	Location loc2) {

		if ((loc1.getBlockX() - loc2.getBlockX()) > 0)
			return CompassDirection.WEST;
		if ((loc1.getBlockX() - loc2.getBlockX()) < 0)
			return CompassDirection.EAST;
		if ((loc1.getBlockZ() - loc2.getBlockZ()) > 0)
			return CompassDirection.NORTH;
		if ((loc1.getBlockZ() - loc2.getBlockZ()) < 0)
			return CompassDirection.SOUTH;


		return CompassDirection.NO_DIRECTION;


	}
}
