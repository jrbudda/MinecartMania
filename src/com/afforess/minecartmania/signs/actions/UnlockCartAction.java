package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.config.LocaleParser;
import com.afforess.minecartmania.signs.MMSign;

public class UnlockCartAction extends DataValuecAction{
	public static final String name = "Unlock Cart";
	
	public UnlockCartAction() {
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
