package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the VW_CUSTOMER_DETAILS database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "VW_CUSTOMER_DETAILS")
@NamedQuery(name = "VwCustomerDetail.findAll", query = "SELECT v FROM VwCustomerDetail v")
public class VwCustomerDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Temporal(TemporalType.DATE)
    @Column(name = "AccessDate")
    private Date accessDate;

    @Id
    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "DistinctTxns")
    private Integer distinctTxns;

    @Column(name = "END_TM")
    private Timestamp endTm;

    @Column(name = "LATITUDE")
    private String latitude;

    @Column(name = "LONGITUDE")
    private String longitude;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "ST_TM")
    private Timestamp stTm;

    @Column(name = "TotalTxns")
    private Integer totalTxns;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "FORMATTED_ADDRESS")
    private String formattedAddress;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this.accessDate = accessDate;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getDistinctTxns() {
        return distinctTxns;
    }

    public void setDistinctTxns(Integer distinctTxns) {
        this.distinctTxns = distinctTxns;
    }

    public Timestamp getEndTm() {
        return endTm;
    }

    public void setEndTm(Timestamp endTm) {
        this.endTm = endTm;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Timestamp getStTm() {
        return stTm;
    }

    public void setStTm(Timestamp stTm) {
        this.stTm = stTm;
    }

    public Integer getTotalTxns() {
        return totalTxns;
    }

    public void setTotalTxns(Integer totalTxns) {
        this.totalTxns = totalTxns;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}