package com.iexceed.appzillon.domain.exception;

import com.iexceed.appzillon.exception.AppzillonException;

/**
 * @author arthanarisamy A new Exception is created which is thrown while
 * processing log requests from external source
 */
public class LogException extends AppzillonException {

    private static final long serialVersionUID = -6963684896670817609L;
    String code;
    String message;

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

    public enum expcode {

        APZ_LOGEX001;

        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }
}
