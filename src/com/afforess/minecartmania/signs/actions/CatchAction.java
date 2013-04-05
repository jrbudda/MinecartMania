package com.afforess.minecartmania.signs.actions;

import com.afforess.minecartmania.MinecartMania;
import com.afforess.minecartmania.minecarts.MMMinecart;
import com.afforess.minecartmania.signs.SignAction;
import com.afforess.minecartmania.utils.StringUtils;

public class CatchAction extends SignAction {
	private double holdforseconds =-1;

	@Override
	public boolean execute(final MMMinecart minecart) {
		minecart.setFrozen(true);

		if (holdforseconds > 0){
			MinecartMania.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(MinecartMania.getInstance(), new Runnable(){
				@Override
				public void run() {
					try {
						minecart.setFrozen(false);	
					} catch (Exception e) {
						// TODO: handle exception
					}
				}},(long) (holdforseconds * 20));
		}

		return true;
	}

	@Override
	public boolean async() {
		return false;
	}

	@Override
	public boolean process(String[] lines) {
		boolean ok = false;
		
		for (String line : lines) {
			if (line.toLowerCase().contains("[catch")) {
				ok = true;
			}
			if (line.toLowerCase().contains("hold for")) {
				this.holdforseconds = Double.valueOf(StringUtils.getNumber(line));
			}
		}
		
		return ok;
	}

	@Override
	public String getPermissionName() {
		return "catchsign";
	}

	@Override
	public String getFriendlyName() {
		return "Catch";
	}

}
