package com.iexceed.appzillon.domain.exception;

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.EnumMap;
import java.util.Map;

public class OtpStatusUpdationException extends AppzillonException {
    private static final long serialVersionUID = 1L;

    private static final Map<Code, String> otpException = new EnumMap<Code, String>(
            OtpStatusUpdationException.Code.class);

    static {
        otpException.put(OtpStatusUpdationException.Code.APZ_DM_046,
                "Invalid user credentials / User is Session Expired/Incorrect OTP");
        otpException.put(OtpStatusUpdationException.Code.APZ_DM_047, "OTP has expired.");
        otpException.put(OtpStatusUpdationException.Code.APZ_DM_084, "OTP is already validated.");
    }

    String code;
    String message;

    private OtpStatusUpdationException() {
    }

    public static OtpStatusUpdationException getOtpStatusUpdationExceptionInstance() {
        return new OtpStatusUpdationException();
    }

    public String getOtpExceptionMessage(Object key) {
        return otpException.get(key);

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

    public enum Code {

        APZ_DM_046, APZ_DM_047, APZ_DM_084;

        public String toString() {
            return this.name().replace('_', '-');
        }
    }
}
