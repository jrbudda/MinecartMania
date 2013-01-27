package com.afforess.minecartmania.config;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;

import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.entity.Item;

public class NewControlBlock {
	private List<SignAction> actions = new LinkedList<SignAction>();
	private Item item = null;
	public RedstoneState redstoneEffect = RedstoneState.Default;

	public NewControlBlock(Item item, List<SignAction> actions){
		this.item = item;
		this.actions = actions;
	}

	public boolean execute(com.afforess.minecartmania.MinecartManiaMinecart minecart, Location loc) {
		boolean success = false;

		for (SignAction a:actions){
			
			if(minecart != null || ((minecart == null) && a.getexecuteAcceptsNull())){
				if (a.execute(minecart, loc) ) success = true;;	
			}
			
		}

		return success;
	}


	public boolean isStation(){
		for (SignAction a:actions){
			if (a instanceof com.afforess.minecartmania.signs.actions.StationAction) return true;
		}
		return false;
	}


}
