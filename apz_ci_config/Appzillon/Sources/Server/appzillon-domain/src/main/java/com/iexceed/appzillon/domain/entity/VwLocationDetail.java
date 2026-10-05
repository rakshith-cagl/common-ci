package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the VW_LOCATION_DETAILS database table.
 */
@Entity
@Table(name = "VW_LOCATION_DETAILS")
@NamedQuery(name = "VwLocationDetail.findAll", query = "SELECT v FROM VwLocationDetail v")
public class VwLocationDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "ADMIN_AREA_LVL_1")
    private String adminAreaLvl1;

    @Column(name = "ADMIN_AREA_LVL_2")
    private String adminAreaLvl2;

    @Id
    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    @Column(name = "DEVICE_ID")
    private String deviceId;

    @Column(name = "FORMATTED_ADDRESS")
    private String formattedAddress;

    @Column(name = "LAST_REQ_TIME")
    private Timestamp lastReqTime;

    @Column(name = "LATITUDE")
    private String latitude;

    @Column(name = "LOGIN_TIME")
    private Timestamp loginTime;

    @Column(name = "LONGITUDE")
    private String longitude;

    @Column(name = "ORIGINATION")
    private String origination;

    @Column(name = "OTP")
    private String otp;

    @Column(name = "OTP_GENERATION_TIME")
    private Timestamp otpGenerationTime;

    @Column(name = "OTP_STATUS")
    private String otpStatus;

    @Column(name = "REQUEST_KEY")
    private String requestKey;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "SUBLOCALITY")
    private String sublocality;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "VERSION_NO")
    private int versionNo;

    public VwLocationDetail() {
        // Default constructor
    }

    public String getAdminAreaLvl1() {
        return this.adminAreaLvl1;
    }

    public void setAdminAreaLvl1(String adminAreaLvl1) {
        this.adminAreaLvl1 = adminAreaLvl1;
    }

    public String getAdminAreaLvl2() {
        return this.adminAreaLvl2;
    }

    public void setAdminAreaLvl2(String adminAreaLvl2) {
        this.adminAreaLvl2 = adminAreaLvl2;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Timestamp getCreateTs() {
        return this.createTs;
    }

    public void setCreateTs(Timestamp createTs) {
        this.createTs = createTs;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Timestamp getLastReqTime() {
        return this.lastReqTime;
    }

    public void setLastReqTime(Timestamp lastReqTime) {
        this.lastReqTime = lastReqTime;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Timestamp getLoginTime() {
        return this.loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOrigination() {
        return this.origination;
    }

    public void setOrigination(String origination) {
        this.origination = origination;
    }

    public String getOtp() {
        return this.otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Timestamp getOtpGenerationTime() {
        return this.otpGenerationTime;
    }

    public void setOtpGenerationTime(Timestamp otpGenerationTime) {
        this.otpGenerationTime = otpGenerationTime;
    }

    public String getOtpStatus() {
        return this.otpStatus;
    }

    public void setOtpStatus(String otpStatus) {
        this.otpStatus = otpStatus;
    }

    public String getRequestKey() {
        return this.requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSublocality() {
        return this.sublocality;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }
}