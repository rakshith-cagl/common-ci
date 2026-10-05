package com.iexceed.appzillon.logging;

public final class AppzLoggerStartup {

    private static AppzLoggerStartup appzLoggerStartup = null;

    private AppzLoggerStartup() {

    }

    public static AppzLoggerStartup getInstance() {

        if (appzLoggerStartup == null) {
            appzLoggerStartup = new AppzLoggerStartup();
        }
        return appzLoggerStartup;

    }

    public LoggerFactory getLogFactory() {
        return LoggerFactory.getLoggerFactory();
    }

}
