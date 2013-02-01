package com.afforess.minecartmania.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class EntityUtils {

	public static Location getValidLocation(Block base, int Yrange){
		if(base ==null ) return null;
		//TODO: ugh.

		Location out = null;
		Block b = base;

		if (canStand(base)) out = base.getLocation();
		else {

			for (int i = 0 ; i < 4 ; i++){
				out = searchvert(base,3);
				if (out !=null) return out.add(.5, 0, .5);
				else base = b;
			}
		}

		if(out !=null)	return out.add(.5, 0, .5);
		else return null;
	}

	private static Location searchvert(Block start, int count){	
		start = start.getRelative(org.bukkit.block.BlockFace.DOWN);
		for (int i = 0 ; i < count ; i++){
			start = start.getRelative(org.bukkit.block.BlockFace.UP);
			if (canStand(start)) return start.getLocation();
		}
		return null;
	}

	static boolean canStand(org.bukkit.block.Block base){
		org.bukkit.block.Block below = base.getRelative(0, -1, 0);
		if(!below.isEmpty() && net.minecraft.server.v1_4_R1.Block.byId[below.getTypeId()].material.isSolid()){
			if(base.isEmpty() || net.minecraft.server.v1_4_R1.Block.byId[base.getTypeId()].material.isSolid()==false){
				return true;
			}
		}
		return false;
	}



}
