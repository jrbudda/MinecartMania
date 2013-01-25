package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.signs.Sign;

public class LockCartAction extends GenericAction{
	public static final String name = "Lock Cart";

	public LockCartAction(Sign sign) {
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
	public boolean valid(Sign sign) {
		for (String line : sign.getLines()) {
			if (line.toLowerCase().contains(name.toLowerCase()) && !line.toLowerCase().contains(UnlockCartAction.name.toLowerCase())) {
				sign.addBrackets();
				return true;
			}
		}
		return false;
	}

}
