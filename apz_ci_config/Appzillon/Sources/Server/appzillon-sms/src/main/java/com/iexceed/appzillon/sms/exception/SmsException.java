package com.iexceed.appzillon.sms.exception;

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.EnumMap;
import java.util.Map;

public final class SmsException extends AppzillonException {

    private static final Map<EXCEPTION_CODE, String> SMS_EXCEPTIONS = new EnumMap<>(EXCEPTION_CODE.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    static {
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_001, "First level authorization fails");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_002, "Failed sending generated password to the user");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_003, "You have been logged out of the application due to inactivity and for security reasons. Please log in again to continue with your application.");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_004, "A valid session actually exists, would you like to relogin and create a new session?");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_005, "Failed while relogging the user in");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_006, "Failed to logout the user for relogin request");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_008, "Password is not valid");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_009, "Admin authentication required");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_010, "User not authorized for this interface");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_011, "EmailId/Mobile number does not match");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_012, "Mobile number does not match");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_013, "EmailId does not match");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_014, "User is already loggedin by other device");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_015, "Captcha required is disabled for this interface");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_016, "Invalid captcha. Validation failed");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_017, "Captcha already processed. Validation failed");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_018, "Invalid appid in tenant creation request");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_019, "Auth code is not present in the request");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_020, "Error while getting the token from azure ad. Please re-authenticate and try again");
        SMS_EXCEPTIONS.put(EXCEPTION_CODE.APZ_SMS_EX_022, "The provided refresh token has expired. Please re-authenticate and try again");

    }

    private String code;
    private String message;

    private SmsException() {

    }

    public static SmsException getSMSExceptionInstance() {
        return new SmsException();
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

    public String getSMSExceptionMessage(Object key) {
        return SMS_EXCEPTIONS.get(key);

    }

    /*_
     * Below Exception enumeration changes made by Samy on 24-12-2013
     *
     */
    public enum Code {

        APZ_SM_001, APZ_SM_002, APZ_SM_003;

        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    public enum EXCEPTION_CODE {

        APZ_SMS_EX_001("APZ_SMS_EX_001"), APZ_SMS_EX_002("APZ_SMS_EX_002"), APZ_SMS_EX_003("APZ_SMS_EX_003"),
        APZ_SMS_EX_004("APZ_SMS_EX_004"), APZ_SMS_EX_005("APZ_SMS_EX_005"), APZ_SMS_EX_006("APZ_SMS_EX_006"),
        APZ_SMS_EX_007("APZ_SMS_EX_007"), APZ_SMS_EX_008("APZ_SMS_EX_008"), APZ_SMS_EX_009("APZ_SMS_EX_009"),
        APZ_SMS_EX_010("APZ_SMS_EX_010"), APZ_SMS_EX_011("APZ_SMS_EX_011"), APZ_SMS_EX_012("APZ_SMS_EX_012"),
        APZ_SMS_EX_013("APZ_SMS_EX_013"), APZ_SMS_EX_014("APZ_SMS_EX_014"), APZ_SMS_EX_015("APZ_SMS_EX_015"),
        APZ_SMS_EX_016("APZ_SMS_EX_016"), APZ_SMS_EX_017("APZ_SMS_EX_017"), APZ_SMS_EX_018("APZ_SMS_EX_018"),
        APZ_SMS_EX_019("APZ_SMS_EX_019"), APZ_SMS_EX_020("APZ_SMS_EX_020"), APZ_SMS_EX_021("APZ_SMS_EX_021"),
        APZ_SMS_EX_022("APZ_SMS_EX_022");
        private String exCode;

        private EXCEPTION_CODE(String exCode) {
            this.exCode = exCode;
        }

        @Override
        public String toString() {
            return exCode.replace('_', '-');
        }
    }

}
