package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MMMinecart;
import com.afforess.minecartmania.config.Settings;

public class LockCartAction extends DataValuecAction{
	public static final String name = "Lock Cart";

	public LockCartAction() {
		super(name);
	}
	
	@Override
	public boolean execute(MMMinecart minecart) {
		if (minecart.hasPlayerPassenger()) {
			if (minecart.getDataValue(this.key) == null) {
				minecart.getPlayerPassenger().sendMessage(Settings.getLocal("SignCommandsMinecartLocked"));
			}
		}
		return super.execute(minecart);
	}
	
	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("["+name.toLowerCase()) && !line.toLowerCase().contains(UnlockCartAction.name.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
