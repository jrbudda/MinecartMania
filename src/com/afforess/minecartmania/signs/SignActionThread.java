package com.afforess.minecartmania.signs;

import com.afforess.minecartmania.MinecartManiaMinecart;

public class SignActionThread extends Thread{
	protected MinecartManiaMinecart minecart;
	protected SignAction action;
	protected SignActionThread(MinecartManiaMinecart minecart, SignAction action) {
		this.minecart = minecart;
		this.action = action;
	}
	
	@Override
	public void start() {
		action.execute(minecart);
	}

}
