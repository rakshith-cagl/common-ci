package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_DBTP_TXN_MASTER database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "TB_DBTP_TXN_MASTER")
@NamedQuery(name = "TbDbtpTxnMaster.findAll", query = "SELECT t FROM TbDbtpTxnMaster t")
public class TbDbtpTxnMaster implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TXN_REF_NO")
    private String txnRefNo;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "COMPLETION_TS")
    private Timestamp completionTs;

    @Column(name = "CREATION_TS")
    private Timestamp creationTs;

    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "TXN_STATUS")
    private String txnStatus;

    @Column(name = "TXN_TYPE")
    private String txnType;

    @Column(name = "USER_ID")
    private String userId;

    public String getTxnRefNo() {
        return this.txnRefNo;
    }

    public void setTxnRefNo(String txnRefNo) {
        this.txnRefNo = txnRefNo;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Timestamp getCompletionTs() {
        return this.completionTs;
    }

    public void setCompletionTs(Timestamp completionTs) {
        this.completionTs = completionTs;
    }

    public Timestamp getCreationTs() {
        return this.creationTs;
    }

    public void setCreationTs(Timestamp creationTs) {
        this.creationTs = creationTs;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTxnStatus() {
        return this.txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getTxnType() {
        return this.txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}