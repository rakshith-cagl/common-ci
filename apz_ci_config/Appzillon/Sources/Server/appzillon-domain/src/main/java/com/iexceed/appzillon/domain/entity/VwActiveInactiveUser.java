package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the VW_ACTIVE_INACTIVE_USERS database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "VW_ACTIVE_INACTIVE_USERS")
@NamedQuery(name = "VwActiveInactiveUser.findAll", query = "SELECT v FROM VwActiveInactiveUser v")
public class VwActiveInactiveUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "ACTIVE")
    private Integer active;

    @Column(name = "APP_ID")
    private String appId;

    @Id
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATE_TS")
    private Date createTs;

    @Column(name = "INACTIVE")
    private Integer inactive;

    public Integer getActive() {
        return this.active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Date getCreateTs() {
        return this.createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public Integer getInactive() {
        return this.inactive;
    }

    public void setInactive(Integer inactive) {
        this.inactive = inactive;
    }

}