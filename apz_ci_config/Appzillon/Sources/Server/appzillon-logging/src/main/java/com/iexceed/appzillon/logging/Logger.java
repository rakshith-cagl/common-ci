package com.iexceed.appzillon.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.LogUtils;
import org.owasp.encoder.Encode;
import org.slf4j.LoggerFactory;

import static com.iexceed.appzillon.utils.Constants.LOGBACK_XML;

public class Logger {

    public static final String VALUE_IS_NULL = "Value is null";
    public static String propertiesPath = "APPZILLONSERVERPROPSCNTX";
    private org.slf4j.Logger logger = null;

    public Logger(String loggername, String classname) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            if (propertiesPath != null && !"".equals(propertiesPath)) {
                configurator.doConfigure(Logger.class.getClassLoader()
                        .getResource(propertiesPath + "/" + "META-INF" + "/" + LOGBACK_XML));
            } else
                configurator.doConfigure(Logger.class.getClassLoader().getResource(LOGBACK_XML));
        } catch (JoranException e) {
            e.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);

        this.logger = LoggerFactory.getLogger(classname);
    }

    public Logger(Message pMessage) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            if (propertiesPath != null && !"".equals(propertiesPath)) {
                configurator.doConfigure(Logger.class.getClassLoader()
                        .getResource(propertiesPath + "/" + "META-INF" + "/" + LOGBACK_XML));

                JSONObject responseObject = new JSONObject();
                responseObject.put("status", "success");

                pMessage.getResponseObject().setResponseJson(responseObject);
            }
        } catch (JoranException e) {
            e.printStackTrace();
        }
    }

    public void trace(String msg) {
        msg = convertString(msg);
        this.logger.trace(msg);
    }

    public void debug(String msg) {
        msg = convertString(msg);
        this.logger.debug(msg);
    }

    public void info(String msg) {
        this.logger.info(convertString(msg));
    }

    public void warn(String msg) {
        msg = convertString(msg);
        this.logger.warn(msg);
    }

    public void error(String msg) {
        msg = convertString(msg);
        this.logger.error(msg);
    }

    public void error(String msg, Exception pException) {
        msg = convertString(msg);
        this.logger.error(msg, LogUtils.getStackTrace(pException));
    }

    public void info(String message, Object p0) {
        message = convertString(message);
        p0 = convertString(p0);
        this.logger.info(message, p0);
    }

    public void info(String message, Object p0, Object p1) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        this.logger.info(message, p0, p1);
    }

    public void info(String message, Object p0, Object p1, Object p2) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        this.logger.info(message, p0, p1, p2);
    }

    public void info(String message, Object p0, Object p1, Object p2, Object p3) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        this.logger.info(message, p0, p1, p2, p3);
    }

    public void info(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        this.logger.info(message, p0, p1, p2, p3, p4);
    }

    public void info(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        this.logger.info(message, p0, p1, p2, p3, p4, p5);
    }

    public void info(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5, Object p6) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        this.logger.info(message, p0, p1, p2, p3, p4, p5, p6);
    }

    public void info(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5, Object p6,
                     Object p7) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        this.logger.info(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    public void info(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5, Object p6,
                     Object p7, Object p8) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        this.logger.info(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public void info(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5, Object p6,
                     Object p7, Object p8, Object p9) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        p9 = convertString(p9);
        this.logger.info(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    public void debug(String message, Object p0) {
        message = convertString(message);
        p0 = convertString(p0);
        this.logger.debug(message, p0);
    }

    public void debug(String message, Object p0, Object p1) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        this.logger.debug(message, p0, p1);
    }

    public void debug(String message, Object p0, Object p1, Object p2) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        this.logger.debug(message, p0, p1, p2);
    }

    public void debug(String message, Object p0, Object p1, Object p2, Object p3) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        this.logger.debug(message, p0, p1, p2, p3);
    }

    public void debug(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        this.logger.debug(message, p0, p1, p2, p3, p4);
    }

    public void debug(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        this.logger.debug(message, p0, p1, p2, p3, p4, p5);
    }

    public void debug(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        this.logger.debug(message, p0, p1, p2, p3, p4, p5, p6);
    }

    public void debug(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        this.logger.debug(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    public void debug(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7, Object p8) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        this.logger.debug(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public void debug(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7, Object p8, Object p9) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        p9 = convertString(p9);
        this.logger.debug(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }


    public void warn(String message, Object p0) {
        message = convertString(message);
        p0 = convertString(p0);
        this.logger.warn(message, p0);
    }

    public void warn(String message, Object p0, Object p1) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        this.logger.warn(message, p0, p1);
    }

    public void warn(String message, Object p0, Object p1, Object p2) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        this.logger.warn(message, p0, p1, p2);
    }

    public void warn(String message, Object p0, Object p1, Object p2, Object p3) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        this.logger.warn(message, p0, p1, p2, p3);
    }

    public void warn(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        this.logger.warn(message, p0, p1, p2, p3, p4);
    }

    public void warn(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        this.logger.warn(message, p0, p1, p2, p3, p4, p5);
    }

    public void warn(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5, Object p6) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        this.logger.warn(message, p0, p1, p2, p3, p4, p5, p6);
    }

    public void warn(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5, Object p6,
                     Object p7) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        this.logger.warn(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    public void warn(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5, Object p6,
                     Object p7, Object p8) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        this.logger.warn(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public void warn(String message, Object p0, Object p1, Object p2, Object p3,
                     Object p4, Object p5, Object p6,
                     Object p7, Object p8, Object p9) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        p9 = convertString(p9);
        this.logger.warn(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }


    public void error(String message, Object p0, Exception pException) {
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        message = convertString(message);
        p0 = convertString(p0);
        this.logger.error(message, p0, exceptionString);
    }

    public void error(String message, Object p0, Object p1, Exception pException) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        this.logger.error(message, p0, p1, exceptionString);
    }

    public void error(String message, Object p0, Object p1, Object p2, Exception pException) {

        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        this.logger.error(message, p0, p1, p2, exceptionString);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Exception pException) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        this.logger.error(message, p0, p1, p2, p3, exceptionString);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Exception pException) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        this.logger.error(message, p0, p1, p2, p3, p4, exceptionString);
    }


    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Exception pException) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        this.logger.error(message, p0, p1, p2, p3, p4, p5, exceptionString);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6, Exception pException) {

        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        this.logger.error(message, p0, p1, p2, p3, p4, p5, p6, exceptionString);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6, Object p7, Exception pException) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        this.logger.error(message, p0, p1, p2, p3, p4, p5, p6, p7, exceptionString);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6, Object p7, Object p8, Exception pException) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        String exceptionString = convertString(LogUtils.getStackTrace(pException));
        this.logger.error(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, exceptionString);
    }

    public void error(String message, Object p0) {
        message = convertString(message);
        p0 = convertString(p0);
        this.logger.error(message, p0);
    }

    public void error(String message, Object p0, Object p1) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        this.logger.error(message, p0, p1);
    }

    public void error(String message, Object p0, Object p1, Object p2) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        this.logger.error(message, p0, p1, p2);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        this.logger.error(message, p0, p1, p2, p3);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        this.logger.error(message, p0, p1, p2, p3, p4);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        this.logger.error(message, p0, p1, p2, p3, p4, p5);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        this.logger.error(message, p0, p1, p2, p3, p4, p5, p6);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        this.logger.error(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7, Object p8) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        this.logger.error(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public void error(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7, Object p8, Object p9) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        p9 = convertString(p9);
        this.logger.error(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }


    public void trace(String message, Object p0) {
        message = convertString(message);
        p0 = convertString(p0);
        this.logger.trace(message, p0);
    }

    public void trace(String message, Object p0, Object p1) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        this.logger.trace(message, p0, p1);
    }

    public void trace(String message, Object p0, Object p1, Object p2) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        this.logger.trace(message, p0, p1, p2);
    }

    public void trace(String message, Object p0, Object p1, Object p2, Object p3) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        this.logger.trace(message, p0, p1, p2, p3);
    }

    public void trace(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        this.logger.trace(message, p0, p1, p2, p3, p4);
    }

    public void trace(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        this.logger.trace(message, p0, p1, p2, p3, p4, p5);
    }

    public void trace(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        this.logger.trace(message, p0, p1, p2, p3, p4, p5, p6);
    }

    public void trace(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        this.logger.trace(message, p0, p1, p2, p3, p4, p5, p6, p7);
    }

    public void trace(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7, Object p8) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        this.logger.trace(message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
    }

    public void trace(String message, Object p0, Object p1, Object p2, Object p3,
                      Object p4, Object p5, Object p6,
                      Object p7, Object p8, Object p9) {
        message = convertString(message);
        p0 = convertString(p0);
        p1 = convertString(p1);
        p2 = convertString(p2);
        p3 = convertString(p3);
        p4 = convertString(p4);
        p5 = convertString(p5);
        p6 = convertString(p6);
        p7 = convertString(p7);
        p8 = convertString(p8);
        p9 = convertString(p9);
        this.logger.trace(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }

    private String convertString(Object message) {
        return (message == null ? Encode.forJava(VALUE_IS_NULL) : Encode.forJava(message.toString()));
    }
}
