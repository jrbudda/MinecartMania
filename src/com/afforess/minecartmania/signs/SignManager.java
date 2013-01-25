package com.afforess.minecartmania.signs;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.events.MinecartManiaSignFoundEvent;
import com.afforess.minecartmaniacore.debug.MinecartManiaLogger;

public class SignManager {
	private static ConcurrentHashMap<Block, Sign> signList = new ConcurrentHashMap<Block, Sign>();

	public static Sign getSignAt(Block block) {
		return getSignAt(block, null);
	}

	public static Sign getSignAt(Location location) {
		return getSignAt(location.getBlock(), null);
	}

	public static Sign getSignAt(Location location, Player player) {
		return getSignAt(location.getBlock(), player);
	}

	public static Sign getSignAt(Block block, Player player) {
		switch(block.getTypeId()) {
		case 63:
		case 68:
			break;
		default:
			return null;
		}

		//process the signs only if they are new or have changed.
		Sign temp = signList.get(block);
		boolean reregister = false;

		if (temp == null){
			reregister = true;
			temp = new MinecartManiaSign(block);
			MinecartManiaLogger.getInstance().debug("Found new sign: " + temp);
		}
		else {
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign)block.getState();
			if (!temp.equals(sign)){
				reregister = true;
				MinecartManiaLogger.getInstance().debug("Found updated sign: " + temp);
				temp.update(sign);
			}
		}
		
		if (reregister){
			MinecartManiaSignFoundEvent mmsfe = new MinecartManiaSignFoundEvent(temp, player);
			MinecartMania.callEvent(mmsfe);
			mmsfe.logProcessTime();
			temp = mmsfe.getSign();
			signList.put(block, temp);
		}

		return temp;
	}

	public static void updateSign(Location location, Sign sign) {
		updateSign(location.getBlock(), sign);
	}

	public static void updateSign(Block block, Sign sign) {
		if (sign == null) {
			signList.remove(block);
		}
		else {
			signList.put(sign.getBlock(), sign);
		}
	}

}
