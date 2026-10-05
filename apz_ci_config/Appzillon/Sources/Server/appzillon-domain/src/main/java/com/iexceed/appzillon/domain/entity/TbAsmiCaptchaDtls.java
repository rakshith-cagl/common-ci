package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "TB_ASMI_CAPTCHA_DTLS")
//@TableGenerator(name = "CAPTCHASEQUENCE", table = "TB_ASTP_SEQ_GEN", pkColumnName = "SEQUENCE_NAME", valueColumnName = "SEQUENCE_VALUE", pkColumnValue = "CAPTCHA_REF_NO ", allocationSize = 1, initialValue = 1)
public class TbAsmiCaptchaDtls implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    //@GeneratedValue(strategy = GenerationType.TABLE, generator = "CAPTCHASEQUENCE")
    @Column(name = "CAPTCHA_REF")
    private long captchaRef;

    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "INTERFACE_ID")
    private String interfaceId;
    @Column(name = "SESSION_ID")
    private String sessionId;
    @Column(name = "CAPTCHA_STRING")
    private String captchaString;
    @Column(name = "AUDIO_CAPTCHA")
    private String audioCaptcha;
    @Column(name = "CREATE_TS")
    private Date createTs;
    @Column(name = "VALIDATE_TS")
    private Date validateTs;
    @Column(name = "CAPTCHA_STATUS")
    private String captchaStatus;
    @Version
    @Column(name = "VERSION_NO")
    private long versionNo;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public long getCaptchaRef() {
        return captchaRef;
    }

    public void setCaptchaRef(long captchaRef) {
        this.captchaRef = captchaRef;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public String getCaptchaString() {
        return captchaString;
    }

    public void setCaptchaString(String captchaString) {
        this.captchaString = captchaString;
    }

    public String getAudioCaptcha() {
        return audioCaptcha;
    }

    public void setAudioCaptcha(String audioCaptcha) {
        this.audioCaptcha = audioCaptcha;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public Date getValidateTs() {
        return validateTs;
    }

    public void setValidateTs(Date validateTs) {
        this.validateTs = validateTs;
    }

    public String getCaptchaStatus() {
        return captchaStatus;
    }

    public void setCaptchaStatus(String captchaStatus) {
        this.captchaStatus = captchaStatus;
    }

    public long getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(long versionNo) {
        this.versionNo = versionNo;
    }

    @Override
    public String toString() {
        return "TbAsmiCaptchaDtls [captchaRef=" + captchaRef + ", appId=" + appId + ", interfaceId=" + interfaceId
                + ", sessionId=" + sessionId + ", captchaString=" + captchaString + ", audioCaptcha=" + audioCaptcha
                + ", createTs=" + createTs + ", validateTs=" + validateTs + ", captchaStatus=" + captchaStatus
                + ", versionNo=" + versionNo + "]";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash;// + this.captchaRef;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TbAsmiCaptchaDtls other = (TbAsmiCaptchaDtls) obj;
        if (this.captchaRef != other.captchaRef) {
            return false;
        }
        return true;
    }

}
