package com.afforess.minecartmania.signs.actions;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaWorld;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.DirectionUtils;

public class PromptAction extends SignAction {

	@Override
	public boolean execute(MMMinecart minecart) {
		if(minecart.isAtIntersection() && minecart.hasPlayerPassenger()){
			//prompt
			minecart.setFrozen(true);
			Player passenger = minecart.getPlayerPassenger();
			//set the track straight
			int data = DirectionUtils.getMinetrackRailDataForDirection(minecart.getDirection(), minecart.getDirection());
			Block oldBlock = MinecartManiaWorld.getBlockAt(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ());
			ArrayList<Integer> blockData = new ArrayList<Integer>();
			blockData.add(new Integer(oldBlock.getX()));
			blockData.add(new Integer(oldBlock.getY()));
			blockData.add(new Integer(oldBlock.getZ()));
			blockData.add(new Integer(oldBlock.getData()));	
			minecart.setDataValue("old rail data", blockData);

			if (data != -1) {
				MinecartManiaWorld.setBlockData(minecart.getWorld(), minecart.getX(), minecart.getY(), minecart.getZ(), data);
			}

			passenger.sendMessage(Settings.getLocal("StationsTapInDirection"));
			return true;
		}

		return false;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[prompt")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "promptsign";
	}

	@Override
	public String getFriendlyName() {
		return "Prompt";
	}

}
