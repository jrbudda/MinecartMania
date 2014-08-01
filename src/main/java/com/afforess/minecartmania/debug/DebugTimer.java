package com.afforess.minecartmania.debug;


public class DebugTimer {
	public final long start = System.currentTimeMillis();
	private String name = null;
	
	public DebugTimer() {
	}
	
	public DebugTimer(String name) {
		this.name = name;
	}
	
	public void logProcessTime() {
		Logger.time("Process Time " + (name != null ? "(for " + name + ") " : "") + "took " + (System.currentTimeMillis() - start) + " ms");
	}

}
