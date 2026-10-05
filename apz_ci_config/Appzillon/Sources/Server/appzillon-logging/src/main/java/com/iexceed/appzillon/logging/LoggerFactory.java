package com.iexceed.appzillon.logging;

import com.iexceed.appzillon.message.Message;

public class LoggerFactory {
    private static LoggerFactory logFactory = null;

    public LoggerFactory() {
        // default meyhod
    }

    public static LoggerFactory getLoggerFactory() {
        if (logFactory == null) {
            logFactory = new LoggerFactory();
        }
        return logFactory;
    }

    public com.iexceed.appzillon.logging.Logger getLogger(String logger) {
        return new com.iexceed.appzillon.logging.Logger(logger, "");

    }

    public com.iexceed.appzillon.logging.Logger getUssdLogger(String logger,
                                                              String classname) {
        return new com.iexceed.appzillon.logging.Logger(logger,
                classname);
    }

    public com.iexceed.appzillon.logging.Logger getWorkflowLogger(String logger, String classname) {
        return new com.iexceed.appzillon.logging.Logger(logger, classname);
    }

    public com.iexceed.appzillon.logging.Logger getDomainLogger(String logger,
                                                                String classname) {
        return new com.iexceed.appzillon.logging.Logger(logger,
                classname);
    }

    public com.iexceed.appzillon.logging.Logger getSmsLogger(String logger,
                                                             String classname) {
        return new com.iexceed.appzillon.logging.Logger(logger,
                classname);
    }

    public com.iexceed.appzillon.logging.Logger getRestServicesLogger(
            String logger, String classname) {
        return new com.iexceed.appzillon.logging.Logger(logger,
                classname);
    }

    public void reloadLogger(Message pMessage) {
        new com.iexceed.appzillon.logging.Logger(pMessage);
    }

    public com.iexceed.appzillon.logging.Logger getFrameWorksLogger(
            String logger, String classname) {
        return new com.iexceed.appzillon.logging.Logger(logger,
                classname);
    }

    public com.iexceed.appzillon.logging.Logger getNotificationsLogger(
            String logger, String classname) {
        return new com.iexceed.appzillon.logging.Logger(logger,
                classname);
    }

    public com.iexceed.appzillon.logging.Logger getSchedulerLogger(
            String logger, String classname) {
        return new com.iexceed.appzillon.logging.Logger(logger,
                classname);
    }

    /*
     * Created by Samy on 12-07-2013 As a part of Logging utility
     */
    // Server Appzillon Changes (Server Appzillon 2.1 ) - Start
    public com.iexceed.appzillon.logging.Logger getLogsLogger(String logger,
                                                              String classname) {

        //	com.iexceed.appzillon.logging.Logger appzlogger = new com.iexceed.appzillon.logging.Logger(
        //			logger, classname);

        return new com.iexceed.appzillon.logging.Logger(logger, classname);
    }

    // Server Appzillon Changes (Server Appzillon 2.1 ) - END

    // Created by Ripu on 21-08-2013 for logging AdminDomain
    // Server Appzillon Changes (Server Appzillon 2.1 ) - Start
    public com.iexceed.appzillon.logging.Logger getAdminDomainLogger(
            String logger, String classname) {
        //com.iexceed.appzillon.logging.Logger appzlogger = new com.iexceed.appzillon.logging.Logger(logger, classname);
        return new com.iexceed.appzillon.logging.Logger(logger, classname);
    }

    public com.iexceed.appzillon.logging.Logger getAdminFileUploaderLogger(String logger, String classname) {

        //	com.iexceed.appzillon.logging.Logger appzlogger = new com.iexceed.appzillon.logging.Logger(logger, classname);

        return new com.iexceed.appzillon.logging.Logger(logger, classname);
    }

    // Server Appzillon Changes (Server Appzillon 2.1 ) - END

    // ripu changes start
    public com.iexceed.appzillon.logging.Logger getErrorLoggingLogger(
            String logger, String classname) {
        return new com.iexceed.appzillon.logging.Logger(
                logger, classname);
    }
    // ripu changes end
}
