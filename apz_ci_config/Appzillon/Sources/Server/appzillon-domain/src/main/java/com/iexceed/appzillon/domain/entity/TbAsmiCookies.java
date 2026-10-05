package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the TB_ASMI_COOKIES database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "TB_ASMI_COOKIES")
@NamedQuery(name = "TbAsmiCookies.findAll", query = "SELECT t FROM TbAsmiCookies t")
public class TbAsmiCookies implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiCookiesPK id;

    @Column(name = "USR_VALIDATOR")
    private String usrValidator;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;

    @Column(name = "EXPIRY_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryTs;

    @Column(name = "VERSION_NO")
    private int versionNo;

    public TbAsmiCookiesPK getId() {
        return this.id;
    }

    public void setId(TbAsmiCookiesPK id) {
        this.id = id;
    }

    public String getValidator() {
        return this.usrValidator;
    }

    public void setValidator(String usrValidator) {
        this.usrValidator = usrValidator;
    }

    public String getuserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTs() {
        return this.createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public Date getExpiryTs() {
        return this.expiryTs;
    }

    public void setExpiryTs(Date expiryTs) {
        this.expiryTs = expiryTs;
    }

    public int getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((usrValidator == null) ? 0 : usrValidator.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((createTs == null) ? 0 : createTs.hashCode());
        result = prime * result + ((expiryTs == null) ? 0 : expiryTs.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + versionNo;
        return result;
    }

}