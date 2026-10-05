package com.iexceed.appzillon.notification.exception;

/**
 * @author Vinod Rawat
 */

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class NotificationException extends AppzillonException {

    private static final Map<Code, String> NOTIFICATION_EXCEPTIONS = new HashMap<>();

    static {
        NOTIFICATION_EXCEPTIONS.put(Code.APZ_NT_001,
                "Notification's interfaceid mismatched");
        NOTIFICATION_EXCEPTIONS.put(Code.APZ_NT_002,
                "Exception while sending web notification");

    }

    String code;
    String message;

    private NotificationException() {

    }

    public static NotificationException getNotificationExceptionInstance() {
        return new NotificationException();
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

    public String getNotificationExceptionMessage(Object key) {
        return NOTIFICATION_EXCEPTIONS.get(key);
    }

    public enum Code {

        APZ_NT_001, APZ_NT_002;

        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }

}
