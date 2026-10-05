/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.message;

import com.iexceed.appzillon.json.JSONObject;

import java.sql.Timestamp;

/**
 * @author arthanarisamy
 */
public class Header {

    private String userId = "";
    private String appId = "";
    private String requestKey = "";
    private String sessionId = "";
    private boolean status = false;
    private String serviceType = "";
    private String deviceId = "";
    private String screenId = "";
    private String txnRef = null;
    private String asynch = "";
    private String interfaceId = "";
    private String source = "";
    private String os = "";
    private String origination = "";
    private String captchaRef = "";
    private String captchaString = "";
    private String clientNonce = "";
    private String serverNonce = "";
    private String sessionToken = "";
    private String inputString = "";
    private String serverToken = "";
    private String selector = "";
    private String signature = "";
    private String masterTxnRef = "";
    private boolean notifOsFlag = false;
    private boolean notifGroupFlag = false;
    private boolean notifDeviceFlag = false;
    private int notifOffset = -1;
    private int notifSuccessCount = 0;
    private int notifFailureCount = 0;
    private long notifSeqNo = -1;

    //App AccessToken changes 07/06/19
    private String userAppAccessToken = "";

    private boolean keepUserSignedIn = false;
    /**
     * Changes made by Ripu,
     * newly introduced 'pin'(plain password) for accessing the services from external application without session
     * Appzillon 3.1 - 60 -- Start
     */
    private String pin = "";
    /**
     * Appzillon 3.1 - 60 -- END
     */
    private boolean preLogin = false; // this field is added as part of 3.1 OTA changes by Ripu on .


    private String requestId;
    private String otpValStatus = "N";
    private String reqRefId = "";
    private Timestamp startTime;
    private Timestamp extStartTime;
    private Timestamp extEndTime;
    private JSONObject location;
    private String userAppId = "";
    private String appUserId = "";
    private boolean smsType = false;
    private boolean flushSessionMap = false;
    private boolean asyncMail = false;
    private String appVersion = null;
    private String parentTitle = "";

    private String appLaunch = "";

    //external auth provider changes
    private String authToken = "";

    //Encryption Length changes
    private int safeBit = 0;
    private int encyKeyLen = 1;
    private int encMode = 0;

    private Header() {

    }
    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public static Header getInstance() {
        return new Header();
    }

    public int getEncyKeyLen() {
        return encyKeyLen;
    }

    public void setEncyKeyLen(int encyKeyLen) {
        this.encyKeyLen = encyKeyLen;
    }

    public int getEncMode() {
        return encMode;
    }

    public void setEncMode(int encMode) {
        this.encMode = encMode;
    }

    public int getSafeBit() {
        return safeBit;
    }

    public void setSafeBit(int safeBit) {
        this.safeBit = safeBit;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    //End
    public boolean isFlushSessionMap() {
        return flushSessionMap;
    }

    public void setFlushSessionMap(boolean flushSessionMap) {
        this.flushSessionMap = flushSessionMap;
    }

    public String getUserAppAccessToken() {
        return userAppAccessToken;
    }

    public void setUserAppAccessToken(String userAppAccessToken) {
        this.userAppAccessToken = userAppAccessToken;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isSmsType() {
        return smsType;
    }

    public void setSmsType(boolean smsType) {
        this.smsType = smsType;
    }

    public String getReqRefId() {
        return reqRefId;
    }

    public void setReqRefId(String reqRefId) {
        this.reqRefId = reqRefId;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getAsynch() {
        return asynch;
    }

    public void setAsynch(String asynch) {
        this.asynch = asynch;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOtpValStatus() {
        return otpValStatus;
    }

    public void setOtpValStatus(String otpValStatus) {
        this.otpValStatus = otpValStatus;
    }

    public String getOrigination() {
        return origination;
    }

    public void setOrigination(String origination) {
        this.origination = origination;
    }

    public boolean isPreLogin() {
        return preLogin;
    }

    public void setPreLogin(boolean b) {
        this.preLogin = b;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getExtStartTime() {
        return extStartTime;
    }

    public void setExtStartTime(Timestamp extStartTime) {
        this.extStartTime = extStartTime;
    }

    public Timestamp getExtEndTime() {
        return extEndTime;
    }

    public void setExtEndTime(Timestamp extEndTime) {
        this.extEndTime = extEndTime;
    }

    public JSONObject getLocation() {
        return location;
    }

    public void setLocation(JSONObject location) {
        this.location = location;
    }

    public String getUserAppId() {
        return userAppId;
    }

    public void setUserAppId(String userAppId) {
        this.userAppId = userAppId;
    }

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getCaptchaRef() {
        return captchaRef;
    }

    public void setCaptchaRef(String captchaRef) {
        this.captchaRef = captchaRef;
    }

    public String getCaptchaString() {
        return captchaString;
    }

    public void setCaptchaString(String captchaString) {
        this.captchaString = captchaString;
    }


    public String getClientNonce() {
        return clientNonce;
    }

    public void setClientNonce(String clientNonce) {
        this.clientNonce = clientNonce;
    }

    public String getServerNonce() {
        return serverNonce;
    }

    public void setServerNonce(String serverNonce) {
        this.serverNonce = serverNonce;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }


    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }


    public String getServerToken() {
        return serverToken;
    }

    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }


    public String getMasterTxnRef() {
        return masterTxnRef;
    }

    public void setMasterTxnRef(String masterTxnRef) {
        this.masterTxnRef = masterTxnRef;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public boolean getKeepUserSignedIn() {
        return keepUserSignedIn;
    }

    public void setKeepUserSignedIn(boolean keepUserSignedIn) {
        this.keepUserSignedIn = keepUserSignedIn;
    }

    public boolean isAsyncMail() {
        return asyncMail;
    }

    public void setAsyncMail(boolean asyncMail) {
        this.asyncMail = asyncMail;
    }

    public boolean isnotifOsFlag() {
        return notifOsFlag;
    }

    public void setnotifOsFlag(boolean notifOsFlag) {
        this.notifOsFlag = notifOsFlag;
    }

    public boolean isnotifGroupFlag() {
        return notifGroupFlag;
    }

    public void setnotifGroupFlag(boolean notifGroupFlag) {
        this.notifGroupFlag = notifGroupFlag;
    }

    public boolean isnotifDeviceFlag() {
        return notifDeviceFlag;
    }

    public void setnotifDeviceFlag(boolean notifDeviceFlag) {
        this.notifDeviceFlag = notifDeviceFlag;
    }

    public int getNotifOffset() {
        return notifOffset;
    }

    public void setNotifOffset(int notifOffset) {
        this.notifOffset = notifOffset;
    }

    public int getNotifSuccessCount() {
        return notifSuccessCount;
    }

    public void setNotifSuccessCount(int notifSuccessCount) {
        this.notifSuccessCount = notifSuccessCount;
    }

    public int getNotifFailureCount() {
        return notifFailureCount;
    }

    public void setNotifFailureCount(int notifFailureCount) {
        this.notifFailureCount = notifFailureCount;
    }

    public long getNotifSeqNo() {
        return notifSeqNo;
    }

    public void setNotifSeqNo(long notifSeqNo) {
        this.notifSeqNo = notifSeqNo;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public String getAppLaunch() {
        return appLaunch;
    }

    public void setAppLaunch(String appLaunch) {
        this.appLaunch = appLaunch;
    }

    @Override
    public String toString() {
        return "Header{" +
                "userId='" + userId + '\'' +
                ", appId='" + appId + '\'' +
                ", requestKey='" + requestKey + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", status=" + status +
                ", serviceType='" + serviceType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", screenId='" + screenId + '\'' +
                ", txnRef='" + txnRef + '\'' +
                ", asynch='" + asynch + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", source='" + source + '\'' +
                ", os='" + os + '\'' +
                ", origination='" + origination + '\'' +
                ", captchaRef='" + captchaRef + '\'' +
                ", captchaString='" + captchaString + '\'' +
                ", clientNonce='" + clientNonce + '\'' +
                ", serverNonce='" + serverNonce + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                ", inputString='" + inputString + '\'' +
                ", serverToken='" + serverToken + '\'' +
                ", selector='" + selector + '\'' +
                ", signature='" + signature + '\'' +
                ", masterTxnRef='" + masterTxnRef + '\'' +
                ", notifOsFlag=" + notifOsFlag +
                ", notifGroupFlag=" + notifGroupFlag +
                ", notifDeviceFlag=" + notifDeviceFlag +
                ", notifOffset=" + notifOffset +
                ", notifSuccessCount=" + notifSuccessCount +
                ", notifFailureCount=" + notifFailureCount +
                ", notifSeqNo=" + notifSeqNo +
                ", userAppAccessToken='" + userAppAccessToken + '\'' +
                ", keepUserSignedIn=" + keepUserSignedIn +
                ", pin='" + pin + '\'' +
                ", preLogin=" + preLogin +
                ", requestId='" + requestId + '\'' +
                ", otpValStatus='" + otpValStatus + '\'' +
                ", reqRefId='" + reqRefId + '\'' +
                ", startTime=" + startTime +
                ", extStartTime=" + extStartTime +
                ", extEndTime=" + extEndTime +
                ", location=" + location +
                ", userAppId='" + userAppId + '\'' +
                ", appUserId='" + appUserId + '\'' +
                ", smsType=" + smsType +
                ", flushSessionMap=" + flushSessionMap +
                ", asyncMail=" + asyncMail +
                ", parentTitle='" + parentTitle + '\'' +
                ", appLaunch='" + appLaunch + '\'' +
                ", authToken='" + authToken + '\'' +
                ", safeBit=" + safeBit +
                ", encMode=" + encMode +
                ", encyKeyLen=" + encyKeyLen +
                '}';
    }
}
