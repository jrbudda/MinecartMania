package com.afforess.minecartmania.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.entity.Item;

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

	public static boolean isCorrectState(boolean power, RedstoneState state) {
		switch(state) {
		case Default: return true;
		case Enables: return power;
		case Disables: return !power;
		default:
			return false;
		}
	}

	public static boolean isCorrectState(Block block, RedstoneState state) {
		boolean power = block.isBlockIndirectlyPowered() || block.getRelative(0, -1, 0).isBlockIndirectlyPowered();
		if (block.getTypeId() == Item.POWERED_RAIL.getId()) {
			power = (block.getData() & 0x8) != 0;
		}
		switch(state) {
		case Default: return true;
		case Enables: return power;
		case Disables: return !power;
		default:
			return false;
		}
	}

	public static boolean hasSignAction(Block block, Class<? extends SignAction> action){
		if (block ==null) return false;
		Item i = Item.getItem(block);
		if (i == null) return false;
		if (!isControlBlock(i)) return false;
		return getControlBlock(i).hasSignAction(action);

	}

}
