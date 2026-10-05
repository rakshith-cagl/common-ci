package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "TB_ASTP_OTP_ENGINE")
//@TableGenerator(name = "OTPENGINESEQUENCE", table = "TB_ASTP_SEQ_GEN", pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_VALUE", pkColumnValue = "OTP_REF_NO ", allocationSize = 1, initialValue = 1)
public class TbAstpOtpEngine implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    // @GeneratedValue(strategy = GenerationType.TABLE, generator =
    // "OTPENGINESEQUENCE")
    @Column(name = "REF_NO")
    private long serialNo;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "INTERFACE_ID")
    private String interfaceId;
    @Column(name = "SESSION_ID")
    private String sessionId;
    @Column(name = "OTP")
    private String otp;
    @Column(name = "OTP_GEN_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otpGenTime;
    @Column(name = "OTP_EXP_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otpExpTime;
    @Column(name = "OTP_VAL_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date otpValTime;
    @Column(name = "OTP_STATUS")
    private String status;
    @Column(name = "REQ_PAYLOAD_STATUS")
    private String payloadStatus;
    @Column(name = "REQ_PAYLOAD_PROCESS_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date payloadProcessTime;

    // otp resend changes
    @Column(name = "OTP_RESENT_COUNT")
    private int otpResentCount;
    @Column(name = "OTP_RESEND_LOCK")
    private String otpResendLock;
    @Column(name = "OTP_RESEND_LOCK_TS")
    private Date otpResendLockTime;

    @Column(name = "OTP_VALIDATION_COUNT")
    private int otpValidationCount;
    @Version
    @Column(name = "VERSION_NO")
    private int versionNo;

    @Column(name = "REQ_LD_REFNO")
    private String reqLdRefNo;

    @Column(name = "REQ_NO_RECS")
    private int reqNoRecs;
    //otp regenarate Changes
    @Column(name = "OTP_REGEN_COUNT")
    private int otpRegenCount;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getOtpValidationCount() {
        return otpValidationCount;
    }

    public void setOtpValidationCount(int otpValidationCount) {
        this.otpValidationCount = otpValidationCount;
    }

    // otp resend changes
    public int getOtpResentCount() {
        return otpResentCount;
    }

    public void setOtpResentCount(int otpResentCount) {
        this.otpResentCount = otpResentCount;
    }

    public String getOtpResendLock() {
        return otpResendLock;
    }

    public void setOtpResendLock(String otpResendLock) {
        this.otpResendLock = otpResendLock;
    }

    public Date getOtpResendLockTime() {
        return otpResendLockTime;
    }

    public void setOtpResendLockTime(Date otpResendLockTime) {
        this.otpResendLockTime = otpResendLockTime;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Date getOtpGenTime() {
        return otpGenTime;
    }

    public void setOtpGenTime(Date otpGenTime) {
        this.otpGenTime = otpGenTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayloadStatus() {
        return payloadStatus;
    }

    public void setPayloadStatus(String payloadStatus) {
        this.payloadStatus = payloadStatus;
    }

    public Date getPayloadProcessTime() {
        return payloadProcessTime;
    }

    public void setPayloadProcessTime(Date payloadProcessTime) {
        this.payloadProcessTime = payloadProcessTime;
    }

    public long getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(long serialNo) {
        this.serialNo = serialNo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getOtpExpTime() {
        return otpExpTime;
    }

    public void setOtpExpTime(Date otpExpTime) {
        this.otpExpTime = otpExpTime;
    }

    public Date getOtpValTime() {
        return otpValTime;
    }

    public void setOtpValTime(Date otpValTime) {
        this.otpValTime = otpValTime;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public String getReqLdRefNo() {
        return reqLdRefNo;
    }

    public void setReqLdRefNo(String reqLdRefNo) {
        this.reqLdRefNo = reqLdRefNo;
    }

    public int getReqNoRecs() {
        return reqNoRecs;
    }

    public void setReqNoRecs(int reqNoRecs) {
        this.reqNoRecs = reqNoRecs;
    }

    public int getOtpRegenCount() {
        return otpRegenCount;
    }

    public void setOtpRegenCount(int otpRegenCount) {
        this.otpRegenCount = otpRegenCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TbAstpOtpEngine that = (TbAstpOtpEngine) o;
        return serialNo == that.serialNo && otpResentCount == that.otpResentCount
                && otpValidationCount == that.otpValidationCount && versionNo == that.versionNo
                && reqNoRecs == that.reqNoRecs && otpRegenCount == that.otpRegenCount
                && Objects.equals(appId, that.appId) && Objects.equals(userId, that.userId)
                && Objects.equals(interfaceId, that.interfaceId) && Objects.equals(sessionId, that.sessionId)
                && Objects.equals(otp, that.otp) && Objects.equals(otpGenTime, that.otpGenTime)
                && Objects.equals(otpExpTime, that.otpExpTime) && Objects.equals(otpValTime, that.otpValTime)
                && Objects.equals(status, that.status) && Objects.equals(payloadStatus, that.payloadStatus)
                && Objects.equals(payloadProcessTime, that.payloadProcessTime)
                && Objects.equals(otpResendLock, that.otpResendLock)
                && Objects.equals(otpResendLockTime, that.otpResendLockTime)
                && Objects.equals(reqLdRefNo, that.reqLdRefNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNo, appId, userId, interfaceId, sessionId, otp, otpGenTime, otpExpTime, otpValTime,
                status, payloadStatus, payloadProcessTime, otpResentCount, otpResendLock, otpResendLockTime,
                otpValidationCount, versionNo, reqLdRefNo, reqNoRecs, otpRegenCount);
    }

}
