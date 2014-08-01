package com.afforess.minecartmania.debug;

public class Logger {
    protected static java.util.logging.Logger log = com.afforess.minecartmania.MinecartMania.getInstance().getLogger();
    protected static DebugMode mode = DebugMode.NORMAL;
    protected static Logger instance = null;

    public static void time(String s, Object... args) {
        timeCore(s, true, args);
    }

    public static void timeCore(String s, boolean toConsole, Object... args) {
        if (mode == DebugMode.TIMER) {
            s = String.format(s, args);
            if (toConsole)
                log.info(s);
            else
                queue(s);
        }
    }

    public static void debug(String s) {
        debugCore(s, true, new Object[0]);
    }


    public static void debug(String s, Object... args) {
        debugCore(s, true, args);
    }

    public static void debugCore(String s, boolean toConsole, Object... args) {
        if (mode == DebugMode.DEBUG || mode == DebugMode.TIMER || mode == DebugMode.MOTION) {
            s = String.format(s, args);
            if (toConsole)
                log.info(s);
            else
                queue(s);
        }
        queue(s);
    }

    public static void motion(String s) {
        if (mode == DebugMode.MOTION) {
            log.info(s);
        }
        queue(s);
    }

    public static void logCore(String s, boolean toConsole, Object... args) {
        if (mode == DebugMode.DEBUG || mode == DebugMode.NORMAL || mode == DebugMode.TIMER || mode == DebugMode.MOTION) {
            if (toConsole)
                log.info(s);
            else
                queue(s);
        }
        queue(s);
    }

    public static void info(String s, Object... args) {
        logCore(s, true, args);
    }

    public static void severe(String s, Object... args) {
        severeCore(s, true, args);
    }

    public static void severeCore(String s, boolean toConsole, Object... args) {
        if (mode == DebugMode.DEBUG || mode == DebugMode.NORMAL || mode == DebugMode.SEVERE || mode == DebugMode.TIMER || mode == DebugMode.MOTION) {
            s = String.format(s, args);
            if (toConsole)
                log.severe(s);
            else
                queue(s);
        }
        queue(s);
    }

    public static void switchDebugMode(DebugMode mode) {
        Logger.mode = mode;
        info("Debug mode switched to " + mode.name());
    }

    private static final void queue(String log) {
//		queuedLog.add(log);
//		if (queuedLog.size() > 100) {
//			LogWriter writer = new LogWriter(queuedLog);
//			queuedLog = new LinkedList<String>();
//			writer.start();
//		}
    }
}
