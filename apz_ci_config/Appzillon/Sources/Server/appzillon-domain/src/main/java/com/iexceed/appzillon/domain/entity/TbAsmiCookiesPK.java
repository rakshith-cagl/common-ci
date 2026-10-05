package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * The primary key class for the TB_ASMI_COOKIES database table.
 */
@Embeddable
public class TbAsmiCookiesPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "USR_SELECTOR")
    private String usrSelector;

    public TbAsmiCookiesPK() {
    }

    public TbAsmiCookiesPK(String appId, String usrSelector) {
        this.appId = appId;
        this.usrSelector = usrSelector;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSelector() {
        return this.usrSelector;
    }

    public void setSelector(String usrSelector) {
        this.usrSelector = usrSelector;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TbAsmiCookiesPK)) {
            return false;
        }
        TbAsmiCookiesPK castOther = (TbAsmiCookiesPK) other;
        return
                this.appId.equals(castOther.appId)
                        && this.usrSelector.equals(castOther.usrSelector);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.appId.hashCode();
        hash = hash * prime + this.usrSelector.hashCode();

        return hash;
    }

    @Override
    public String toString() {
        return "TbAsmiCookiesPK [appId=" + appId + ", usrSelector=" + usrSelector + "]";
    }

}