package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.signs.MMSign;

public class LockCartAction extends DataValuecAction{
	public static final String name = "Lock Cart";

	public LockCartAction() {
		super(name);
	}
	
	@Override
	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.hasPlayerPassenger()) {
			if (minecart.getDataValue(this.key) == null) {
				minecart.getPlayerPassenger().sendMessage(LocaleParser.getTextKey("SignCommandsMinecartLocked"));
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
