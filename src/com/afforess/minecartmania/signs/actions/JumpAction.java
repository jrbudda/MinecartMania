package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartManiaMinecart;
import com.afforess.minecartmania.signs.Sign;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class JumpAction implements SignAction {

	private int blocks =-1;

	public JumpAction(Sign sign) {
		blocks = com.afforess.minecartmania.config.Settings.defaultJumpHeight;
		
		if (sign == null) {
			return;
		}
		
		for (String line : sign.getLines()) {
			if (line.toLowerCase().contains("jump")) {
				String[] split = line.split(":");
				if (split.length != 2) continue;
				blocks = Integer.parseInt(StringUtils.getNumber(split[1]));
				sign.addBrackets();
				break;
			}
		}
	}

	@Override
	public boolean execute(MinecartManiaMinecart minecart) {
		if (blocks <=0) return false;
		double h =blocks;
		double v =Math.sqrt(31.119*h);
		if(v>150) v = 150;
	
	//	com.afforess.minecartmaniacore.debug.MinecartManiaLogger.getInstance().log("jump! v=" + v);
		
		minecart.setMotionY(v/20);
		return true;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean valid(Sign sign) {
			return blocks > 0;
	}

	@Override
	public String getName() {
		return "jumpsign";
	}

	@Override
	public String getFriendlyName() {
		return "Jump Sign";
	}

}
