package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The primary key class for the TB_ASMI_APP_ID_VERSION database table.
 */
@Embeddable
public class TbAsmiAppIdVersionPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "OS")
    private String os;

    @Column(name = "APP_ID_VERSION")
    private String appIdVersion;

    public TbAsmiAppIdVersionPK() {
    }

    public TbAsmiAppIdVersionPK(String appId, String appIdVersion) {
        this.appId = appId;
        this.appIdVersion = appIdVersion;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppIdVersion() {
        return this.appIdVersion;
    }

    public void setAppIdVersion(String appIdVersion) {
        this.appIdVersion = appIdVersion;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TbAsmiAppIdVersionPK)) {
            return false;
        }
        TbAsmiAppIdVersionPK castOther = (TbAsmiAppIdVersionPK) other;
        return
                this.appId.equals(castOther.appId)
                        && this.appIdVersion.equals(castOther.appIdVersion);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.appId.hashCode();
        hash = hash * prime + this.appIdVersion.hashCode();

        return hash;
    }
}