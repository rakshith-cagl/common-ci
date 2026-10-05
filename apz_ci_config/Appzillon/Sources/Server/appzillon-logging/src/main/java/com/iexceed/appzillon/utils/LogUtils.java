package com.iexceed.appzillon.utils;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author arthanarisamy
 * Created on 15-07-2013
 */
public class LogUtils {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger("com.iexceed.appzillon.rest",
                    LogUtils.class.toString());

    private LogUtils() {
        //default constructor
    }

    /*
     * getStackTrace() method takes Exception as its parameter
     * and returns the stack trace of the exception as a string
     * which helps in logging and better debugging
     * Created on 10-07-2013
     */
    public static String getStackTrace(Exception pEx) {
        StringWriter lSw = null;
        PrintWriter lPw = null;
        try {
            // Creating String writer Object
            lSw = new StringWriter();
            // Creating print writer object
            lPw = new PrintWriter(lSw);
            //Getting stack trace and storing it in print writer obj
            pEx.printStackTrace(lPw);
            //Storing the stack trace string to the string object
        } catch (Exception ex) {
            LOG.error("Exception", ex);
            return null;
        }
        // returning stack trace string
        return lSw.toString();
    }
}
