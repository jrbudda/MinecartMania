package com.afforess.minecartmania.debug;

public enum DebugMode {
    TIMER,
    DEBUG,
    NORMAL,
    MOTION,
    SEVERE;


    public static DebugMode debugModeFromString(String s) {
        for (DebugMode m : DebugMode.values()) {
            if (m.name().equalsIgnoreCase(s)) {
                return m;
            }
        }
        return null;
    }
}
