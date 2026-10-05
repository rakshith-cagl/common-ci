package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the VW_LOGIN_REPORT database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "VW_LOGIN_REPORT")
@NamedQuery(name = "VwLoginReport.findAll", query = "SELECT v FROM VwLoginReport v")
public class VwLoginReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Temporal(TemporalType.DATE)
    @Column(name = "AccessDate")
    private Date accessDate;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "DEVICE_ID")
    private String deviceId;

    @Column(name = "DistinctLogins")
    private Integer distinctLogins;

    @Column(name = "Logins")
    private Integer logins;

    @Column(name = "TotalTxns")
    private Integer totalTxns;

    public Date getAccessDate() {
        return this.accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this.accessDate = accessDate;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getDistinctLogins() {
        return this.distinctLogins;
    }

    public void setDistinctLogins(Integer distinctLogins) {
        this.distinctLogins = distinctLogins;
    }

    public Integer getLogins() {
        return this.logins;
    }

    public void setLogins(Integer logins) {
        this.logins = logins;
    }

    public Integer getTotalTxns() {
        return this.totalTxns;
    }

    public void setTotalTxns(Integer totalTxns) {
        this.totalTxns = totalTxns;
    }
}