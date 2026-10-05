package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "TB_ASLG_FMW_TXN_DETAIL")
public class TbAslgFmwTxnDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic
    @Column(name = "TXN_REF")
    private String txnRef;
    @Column(name = "MASTER_TXN_REF")
    private String masterTxnRef;
    @Column(name = "INTERFACE_ID")
    private String interfaceId;
    @Column(name = "ENDPOINT_TYPE")
    private String endpointType;
    @Column(name = "ST_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stTm;
    @Column(name = "END_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTm;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;

    @Column(name = "REQ_LD_REFNO")
    private String reqLdRefNo;

    @Column(name = "RES_LD_REFNO")
    private String resLdRefNo;

    @Column(name = "REQ_NO_RECS")
    private int reqNoRecs;

    @Column(name = "RES_NO_RECS")
    private int resNoRecs;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getMasterTxnRef() {
        return masterTxnRef;
    }

    public void setMasterTxnRef(String masterTxnRef) {
        this.masterTxnRef = masterTxnRef;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(String endpointType) {
        this.endpointType = endpointType;
    }

    public Date getStTm() {
        return stTm;
    }

    public void setStTm(Date stTm) {
        this.stTm = stTm;
    }

    public Date getEndTm() {
        return endTm;
    }

    public void setEndTm(Date endTm) {
        this.endTm = endTm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public String getReqLdRefNo() {
        return reqLdRefNo;
    }

    public void setReqLdRefNo(String reqLdRefNo) {
        this.reqLdRefNo = reqLdRefNo;
    }

    public String getResLdRefNo() {
        return resLdRefNo;
    }

    public void setResLdRefNo(String resLdRefNo) {
        this.resLdRefNo = resLdRefNo;
    }

    public int getReqNoRecs() {
        return reqNoRecs;
    }

    public void setReqNoRecs(int reqNoRecs) {
        this.reqNoRecs = reqNoRecs;
    }

    public int getResNoRecs() {
        return resNoRecs;
    }

    public void setResNoRecs(int resNoRecs) {
        this.resNoRecs = resNoRecs;
    }

}
