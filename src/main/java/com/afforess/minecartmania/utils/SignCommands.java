package com.afforess.minecartmania.utils;

import java.util.HashSet;

import org.bukkit.block.Block;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.sensors.Sensor;
import com.afforess.minecartmania.signs.sensors.SensorManager;

public class SignCommands {

	public static void updateSensors(MMMinecart minecart, MMMinecart input) {
		
		HashSet<Block> blockList = minecart.getAdjacentBlocks(1);
		blockList.addAll(minecart.getBlocksBeneath(3));
		HashSet<Block> oldBlockList = minecart.getPreviousLocationAdjacentBlocks(1);
		oldBlockList.addAll(minecart.getPreviousLocationBlocksBeneath(3));
		oldBlockList.removeAll(blockList);

		//Activate new sensors
		for (Block block : blockList) {
			Sensor s = SensorManager.getSensor(block);
			if (s != null) {
				try {
					s.input(input);
				}
				catch (Exception e) {
					SensorManager.delSensor(s.getLocation());
				}
			}
		}
		
		//deactivate old sensors
		for (Block block : oldBlockList) {
			Sensor s = SensorManager.getSensor(block);
			if (s != null){
				try {
					s.input(null);
				}
				catch (Exception e) {
					SensorManager.delSensor(s.getLocation());
				}
			}
		}
	}
	
	public static void updateSensors(MMMinecart minecart) {
		updateSensors(minecart, minecart);
	}


}
