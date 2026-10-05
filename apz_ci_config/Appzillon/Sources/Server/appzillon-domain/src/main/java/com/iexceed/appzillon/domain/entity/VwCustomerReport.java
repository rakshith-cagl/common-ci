package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The persistent class for the VW_CUSTOMER_REPORT database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "VW_CUSTOMER_REPORT")
@NamedQuery(name = "VwCustomerReport.findAll", query = "SELECT v FROM VwCustomerReport v")
public class VwCustomerReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "DistinctLogins")
    private BigInteger distinctLogins;

    @Column(name = "DistinctTxns")
    private BigInteger distinctTxns;

    @Column(name = "Logins")
    private BigInteger logins;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Id
    private BigInteger tm;

    @Column(name = "TotalTxns")
    private BigInteger totalTxns;

    @Column(name = "USER_ID")
    private String userId;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public BigInteger getDistinctLogins() {
        return this.distinctLogins;
    }

    public void setDistinctLogins(BigInteger distinctLogins) {
        this.distinctLogins = distinctLogins;
    }

    public BigInteger getDistinctTxns() {
        return this.distinctTxns;
    }

    public void setDistinctTxns(BigInteger distinctTxns) {
        this.distinctTxns = distinctTxns;
    }

    public BigInteger getLogins() {
        return this.logins;
    }

    public void setLogins(BigInteger logins) {
        this.logins = logins;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public BigInteger getTm() {
        return this.tm;
    }

    public void setTm(BigInteger tm) {
        this.tm = tm;
    }

    public BigInteger getTotalTxns() {
        return this.totalTxns;
    }

    public void setTotalTxns(BigInteger totalTxns) {
        this.totalTxns = totalTxns;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}