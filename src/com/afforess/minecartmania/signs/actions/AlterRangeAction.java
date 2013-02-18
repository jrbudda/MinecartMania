package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.entity.MinecartManiaStorageCart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.MathUtils;
import com.afforess.minecartmania.utils.StringUtils;

public class AlterRangeAction extends SignAction{
	protected int range = -1;
	protected boolean itemRange = false;
	protected boolean itemRangeY = false;
	protected boolean rangeY = false;

	
	
	public boolean execute(MMMinecart minecart) {
		if (itemRange) {
			if (minecart.isStorageMinecart()) {
				((MinecartManiaStorageCart)minecart).setItemRange(this.range);
				return true;
			}
		}
		else if (itemRangeY) {
			if (minecart.isStorageMinecart()) {
				((MinecartManiaStorageCart)minecart).setItemCollectionRangeY(range);
				return true;
			}
		}
		else if (rangeY) {
			minecart.setRangeY(this.range);
		}
		else {
			minecart.setRange(this.range);
			return true;
		}
		return false;
	}
	
	
	public boolean async() {
		return true;
	}
	
	
	public boolean process(String[] lines) {	
		for (String line : lines) {
			if (line.toLowerCase().contains("range:")) {
				String[] split = line.split(":");
				if (split.length != 2) continue;
				try {
					this.range = Integer.parseInt(StringUtils.getNumber(split[1]));
					this.range = MathUtils.range(this.range, Settings.MaxAllowedRange, 0);
				}
				catch (Exception e) {
					this.range = -1;
				}
				this.itemRangeY = line.toLowerCase().contains("[item rangey");
				this.itemRange = line.toLowerCase().contains("[item range");
				this.rangeY = line.toLowerCase().contains("[rangey");
				break;
			}
		}
		return this.range != -1;
	}

	
	public String getPermissionName() {
		return "alterrangesign";
	}

	
	public String getFriendlyName() {
		return "Alter Range";
	}

}
