package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TbAsnfTxnLogPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "NOTIF_ID")
    private Long notifId;

    @Column(name = "NOTIF_REG_ID")
    private String notifRegId;

    @Column(name = "DEVICE_ID")
    private String deviceId;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getNotifId() {
        return this.notifId;
    }

    public void setNotifId(Long notifIdAll) {
        this.notifId = notifIdAll;
    }

    public String getNotifRegId() {
        return this.notifRegId;
    }

    public void setNotifRegId(String notifRegId) {
        this.notifRegId = notifRegId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TbAsnfTxnLogPK)) {
            return false;
        }
        TbAsnfTxnLogPK castOther = (TbAsnfTxnLogPK) other;
        return
                this.appId.equals(castOther.appId)
                        && this.notifId.equals(castOther.notifId)
                        && this.notifRegId.equals(castOther.notifRegId)
                        && this.deviceId.equals(castOther.deviceId);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.appId.hashCode();
        hash = hash * prime + this.notifId.hashCode();
        hash = hash * prime + this.notifRegId.hashCode();
        hash = hash * prime + this.deviceId.hashCode();

        return hash;
    }
}
