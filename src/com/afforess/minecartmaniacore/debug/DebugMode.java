package com.afforess.minecartmaniacore.debug;

public enum DebugMode {
	TIMER,
	DEBUG,
	NORMAL,
	MOTION,
	SEVERE,
	NONE;
	
	public static DebugMode debugModeFromString(String s) {
		for (DebugMode m: DebugMode.values()) {
			if (m.name().equalsIgnoreCase(s)) {
				return m;
			}
		}
		return null;
	}
}
