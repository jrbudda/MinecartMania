package com.afforess.minecartmania.config;

import java.util.Iterator;
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
				com.afforess.minecartmania.MinecartMania.log("executing " + a.getFriendlyName());
				if (a.execute(minecart, loc) ) success = true;;	
			}		
		}

		return success;
	}


	public boolean hasSignAction(Class<? extends SignAction> action) {
		Iterator<SignAction> i = actions.iterator();
		while(i.hasNext()){
			SignAction executor = i.next();
			if (action.isInstance(executor)) {
				return true;
			}
		}
		return false;
	}


}
