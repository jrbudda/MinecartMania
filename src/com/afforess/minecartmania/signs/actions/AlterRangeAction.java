package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.config.Settings;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.minecarts.MMStorageCart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.MathUtils;
import com.afforess.minecartmania.utils.StringUtils;

public class AlterRangeAction extends SignAction{
	protected int range = -1;
	protected boolean itemRange = false;
	protected boolean itemRangeY = false;
	protected boolean farmRange = false;
	protected boolean farmrangeY = false;

	public boolean execute(MMMinecart minecart) {
		if (minecart.isStorageMinecart()) {
			if (itemRange) {
				((MMStorageCart)minecart).setItemRange(this.range);
				return true;
			}
			else if (itemRangeY) {
				((MMStorageCart)minecart).setItemCollectionRangeY(range);
				return true;
			}
			else if (farmrangeY) {
				((MMStorageCart)minecart).setFarmingRangeY(range);
				return true;
			}
			else {
				((MMStorageCart)minecart).setFarmingRange(this.range);
				return true;
			}
		}

		return false;
	}

	public boolean async() {
		return false;
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
				this.farmrangeY = line.toLowerCase().contains("[farm rangey");
				this.farmRange = line.toLowerCase().contains("[farm range");
				break;
			}
		}
		return this.range != -1;
	}


	public String getPermissionName() {
		return "alterrangesign";
	}


	public String getFriendlyName() {
		return "Alter Range " + (farmRange ? "Farming" : "") + (farmrangeY ? "FarmingY" : "")+ (itemRange ? "Item" : "")+ (itemRangeY ? "ItemY" : "") ;
	}

}
