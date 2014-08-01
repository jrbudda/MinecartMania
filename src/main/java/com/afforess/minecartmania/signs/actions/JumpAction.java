package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class JumpAction extends SignAction {

	private double blocks =-1;

	@Override
	public boolean execute(MMMinecart minecart) {
		if (blocks < 0) blocks =com.afforess.minecartmania.config.Settings.defaultJumpHeight;
		if (blocks <=0) return false;
		double h =blocks;
		double v =Math.sqrt(31.119*h);
		if(v>150) v = 150;

		com.afforess.minecartmania.debug.Logger.debug("jump! v=" + v);

		minecart.setMotionY(v/20);
		
		return true;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		for (String line : lines) {
			if (line.toLowerCase().contains("[jump")) {
				String[] split = line.split(":");
				if (split.length != 2) {
					blocks =com.afforess.minecartmania.config.Settings.defaultJumpHeight;
					return true;
				}
				blocks = Double.parseDouble(StringUtils.getNumber(split[1]));
				return true;
			}
		}
		return false;
	}

	@Override
	public String getPermissionName() {
		return "jumpsign";
	}

	@Override
	public String getFriendlyName() {
		return "Jump";
	}

}
