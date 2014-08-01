package com.afforess.minecartmania.signs.actions;

import org.bukkit.Location;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;

public class HoldingForAction extends SignAction{
	

	protected int time = -1;
	protected Location sign;

	
	public boolean execute(MMMinecart minecart) {
	
		//TODO: fix this mess. Making holding an option of Catch
		return true;
		
//		if (minecart.getDataValue("HoldForDelay") != null) {
//			return false;
//		}
//		if (ControlBlockList.isCatcherBlock(minecart.getItemBeneath())) {
//			
//			HoldSignData data = null;
//
//			if (data == null) {
//				data = new HoldSignData(minecart.getEntityId(), time, minecart.getLocation(), sign, minecart.getMotion());
//			}
//			
//			minecart.stopCart();
//			minecart.setDataValue("hold sign data", data);
//			minecart.setDataValue("HoldForDelay", true);
//			
//			//MinecartManiaSignCommands.instance.getDatabase().save(data);
//
//			return true;
//		}
//		return false;
	}

	
	public boolean async() {
		return true;
	}

	
	public boolean process(String[] lines) {
		return false;
//		for (int i = 0; i < lines.length; i++) {
//			if (lines[i].toLowerCase().contains("hold for")) {
//				try {
//					this.time = Double.valueOf(StringUtils.getNumber(lines[i])).intValue();
//				}
//				catch (Exception e) {
//				}
//			}
//	
//		}
//
//		return time != -1;
	}

	
	public String getPermissionName() {
		return "holdingsign";
	}

	
	public String getFriendlyName() {
		return "Holding";
	}

}
