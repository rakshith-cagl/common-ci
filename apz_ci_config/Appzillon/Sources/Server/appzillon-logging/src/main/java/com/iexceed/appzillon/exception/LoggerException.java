/**
 *
 */
package com.iexceed.appzillon.exception;

import java.util.EnumMap;
import java.util.Map;

//import com.iexceed.appzillon.exception.AppzillonException;


/**
 * @author Ripu
 *
 */
public class LoggerException extends AppzillonException {
    private static final long serialVersionUID = 1L;

    private static final Map<Code, String> logException = new EnumMap<>(Code.class);

    static {
        logException.put(LoggerException.Code.APZ_LOG_000, "Property file not found");
    }

    String code;
    String message;

    private LoggerException() {

    }

    public static LoggerException getLoggerInstance() {
        return new LoggerException();
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogExceptionMessage(Object key) {
        return logException.get(key);
    }

    public enum Code {
        APZ_LOG_000;

        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }
}
