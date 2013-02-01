package com.afforess.minecartmania.signs;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.MMSign;
import com.afforess.minecartmania.debug.Logger;

public class SignManager {
	private static ConcurrentHashMap<Location, MMSign> signList = new ConcurrentHashMap<Location, MMSign>();

	public static MMSign getOrCreateMMSign(Block block) {
		return getOrCreateMMSign(block, null);
	}

	public static MMSign getOrCreateMMSign(Block block, Player player) {
		switch(block.getTypeId()) {
		case 63:
		case 68:
			break;
		default:
			return null;
		}

		return getOrCreateMMSign((Sign)block.getState(), player);
	}

	public static MMSign getOrCreateMMSign(Location location) {
		return getOrCreateMMSign(location.getBlock(), null);
	}

	public static MMSign getOrCreateMMSign(Location location, Player player) {
		return getOrCreateMMSign(location.getBlock(), player);
	}


	private static MMSign getOrCreateMMSign(Sign sign, Player player) {

		MMSign existing = signList.get(sign.getLocation());
		boolean reregister = false;

		//process the signs only if they are new or have changed.

		if (existing == null) 		{
			//new
			reregister = true;
			existing = new MMSign(sign);
			Logger.debug("Found new sign: " + existing.getLine(0));
		}
		else{
			if(!existing.textMatches(sign)) {
				//changed
				reregister = true;
				existing = new MMSign(sign);
				Logger.debug("Found updated sign: " + existing.getLine(0));
			}
		}
		if (reregister){
			List<SignAction> actions = ActionList.getSignActionsforLines(sign.getLines());
			if (actions.size() > 0) existing.addBrackets();
				for (SignAction action : actions)	{
				Logger.debug("adding action: " + action.getFriendlyName());
				action.loc = existing.getLocation();
				existing.addSignAction(action);
			}
			signList.put(sign.getLocation(), existing);
		}

		return existing;
	}

	public static void remove(Block block){
		signList.remove(block.getLocation());
	}

}
