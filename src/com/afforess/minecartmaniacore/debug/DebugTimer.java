package com.afforess.minecartmaniacore.debug;

import com.afforess.minecartmania.MinecartMania;

public class DebugTimer {
	public final long start = System.currentTimeMillis();
	private String name = null;
	
	public DebugTimer() {
	}
	
	public DebugTimer(String name) {
		this.name = name;
	}
	
	public void logProcessTime() {
		MinecartMania.log.time("Process Time " + (name != null ? "(for " + name + ") " : "") + "took " + (System.currentTimeMillis() - start) + " ms");
	}

}
