package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.signs.Sign;

public class UnlockCartAction extends GenericAction{
	public static final String name = "Unlock Cart";
	public UnlockCartAction(Sign sign) {
		super(name, LockCartAction.name, null);
	}
	
	@Override
	public boolean execute(MinecartManiaMinecart minecart) {
		if (minecart.hasPlayerPassenger()) {
			if (minecart.getDataValue(this.key) != null) {
				minecart.setDataValue(this.key, null);
				minecart.getPlayerPassenger().sendMessage(LocaleParser.getTextKey("SignCommandsMinecartUnlocked"));
			}
		}
		return super.execute(minecart);
	}
}
