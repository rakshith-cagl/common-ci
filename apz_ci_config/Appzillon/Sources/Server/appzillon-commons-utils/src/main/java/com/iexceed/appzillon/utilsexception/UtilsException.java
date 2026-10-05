package com.iexceed.appzillon.utilsexception;

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.HashMap;
import java.util.Map;


public final class UtilsException extends AppzillonException {
    private static final long serialVersionUID = 1L;
    private static final Map<Code, String> UTILS_EXCEPTIONS = new HashMap<Code, String>();

    static {
        UTILS_EXCEPTIONS.put(Code.APZ_UT_000, "At least one codec must be enabled if owasp Sanitization is Required");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_001, "Intrusion Exception");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_002, "Encoding Exception");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_003, "Header's interface details are not found under internal category....");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_004, "Invalid Base64 encoding");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_005, "Invalid Email Id");
        UTILS_EXCEPTIONS.put(Code.APZ_UT_006, "Invalid Mobile No");

    }

    private String code;
    private String message;

    private UtilsException() {

    }

    public static UtilsException getUtilsExceptionInstance() {
        return new UtilsException();
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

    public String getUtilsExceptionMessage(Object key) {
        return UTILS_EXCEPTIONS.get(key);

    }

    public enum Code {

        APZ_UT_000, APZ_UT_001, APZ_UT_002, APZ_UT_003, APZ_UT_004, APZ_UT_005, APZ_UT_006;

        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }


}
