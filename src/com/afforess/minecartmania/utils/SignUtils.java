package com.afforess.minecartmania.utils;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import com.afforess.minecartmania.signs.SignManager;
import com.afforess.minecartmania.utils.DirectionUtils.CompassDirection;

public class SignUtils {


	public static boolean signMatches(Sign s1, Sign s2) {
		return s1.getBlock().getLocation().equals(s2.getBlock().getLocation());
	}

	/**
	 * Returns the sign at the given world, x, y, z, coordinate, or null if none exits
	 * @param w World
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static Sign getSignAt(World w, int x, int y, int z) {
		switch(w.getBlockTypeIdAt(x, y, z)) {
		case 63:
		case 68:
			return (Sign)w.getBlockAt(x, y, z).getState();
		default:
			return null;
		}
	}


	public static ArrayList<Sign> getAdjacentSignList(Location location, int range) {
		return getAdjacentSignList(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), range);
	}


	private  static ArrayList<Sign> getAdjacentSignList(World w, int x, int y, int z, int range) {
		ArrayList<Sign> signList = new ArrayList<Sign>();

		//		if (!force && Settings.isLimitedSignRange()) {
		//			signList.addAll(getParallelSignList(w, x, y, z));
		//			signList.addAll(getSignBeneathList(w, x, y, z, 2));
		//			return signList;
		//		}

		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					Sign sign = getSignAt(w, x+dx, y+dy, z+dz);
					if (sign != null) {
						signList.add(sign);
					}
				}
			}
		}
		return signList;
	}

	private  static ArrayList<Sign> getAdjacentSignListforDirection(World w, int x, int y, int z, int range, CompassDirection dir) {
		ArrayList<Sign> signList = new ArrayList<Sign>();

		switch (dir){
		case NORTH: case SOUTH:
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){	
					Sign sign = getSignAt(w, x+dx, y+dy, z);
					if (sign != null) {
						signList.add(sign);
					}
				}
			}
			return signList;
		case EAST: case WEST:
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					Sign sign = getSignAt(w, x, y+dy, z+dz);
					if (sign != null) {
						signList.add(sign);
					}
				}
			}
			return signList;
		default:
			return getAdjacentSignList(w, x, y, z, range);
		}



	}



	public static ArrayList<com.afforess.minecartmania.MMSign> getAdjacentMMSignList(Location location, int range) {
		ArrayList<Sign> list = getAdjacentSignList(location, range);
		ArrayList<com.afforess.minecartmania.MMSign> signList = new ArrayList<com.afforess.minecartmania.MMSign>(list.size());
		for (Sign s : list) {
			signList.add(SignManager.getOrCreateMMSign(s.getBlock()));
		}
		return signList;
	}

	public static ArrayList<com.afforess.minecartmania.MMSign> getAdjacentMMSignListforDirection(Location location, int range, CompassDirection dir) {
		ArrayList<Sign> list = getAdjacentSignListforDirection(location.getWorld(), location.getBlockX(), location.getBlockY(),location.getBlockZ(), range, dir);
		ArrayList<com.afforess.minecartmania.MMSign> signList = new ArrayList<com.afforess.minecartmania.MMSign>(list.size());
		for (Sign s : list) {
			signList.add(SignManager.getOrCreateMMSign(s.getBlock()));
		}
		return signList;
	}


}
