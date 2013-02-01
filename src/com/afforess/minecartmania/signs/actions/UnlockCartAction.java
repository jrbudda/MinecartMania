package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;

public class UnlockCartAction extends DataValuecAction{
	public static final String name = "Unlock Cart";
	
	public UnlockCartAction() {
		super(name, LockCartAction.name, null);
	}
	
	@Override
	public boolean execute(MMMinecart minecart) {
		if (minecart.hasPlayerPassenger()) {
			if (minecart.getDataValue(this.key) != null) {
				minecart.setDataValue(this.key, null);
				minecart.getPlayerPassenger().sendMessage(Settings.getLocal("SignCommandsMinecartUnlocked"));
			}
		}
		return super.execute(minecart);
	}
}
