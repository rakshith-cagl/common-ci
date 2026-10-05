package com.iexceed.appzillon.workflow.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "TB_ASMI_WORKFLOW_DEFN")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiWorkflowDefn implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiWorkflowDefnPK id;
    @Column(name = "WORKFLOW_DESC")
    private String workflowDesc;
    @Column(name = "VERSION_NO")
    private int wFlowDefnVersionNo;
    @Column(name = "MAKER_ID")
    private String wFlowDefnMakerId;
    @Column(name = "MAKER_TS")
    private Date wFlowDefnMakerTs;
    @Column(name = "CHECKER_ID")
    private String wFlowDefnCheckerId;
    @Column(name = "CHECKER_TS")
    private Date wFlowDefnCheckerTs;
    @Column(name = "AUTH_STATUS")
    private String authStatus;
    @Column(name = "ACTIVE")
    private String active;
    @Column(name = "TXN_TYPE")
    private String txnType;
    @Column(name = "ONCE_AUTH")
    private String wFlowDefnOnceAuth;


    public TbAsmiWorkflowDefn() {
        // default constructor
    }

    public TbAsmiWorkflowDefnPK getId() {
        return id;
    }

    public void setId(TbAsmiWorkflowDefnPK id) {
        this.id = id;
    }

    public String getWorkflowDesc() {
        return workflowDesc;
    }

    public void setWorkflowDesc(String workflowDesc) {
        this.workflowDesc = workflowDesc;
    }

    public int getwFlowDefnVersionNo() {
        return wFlowDefnVersionNo;
    }

    public void setwFlowDefnVersionNo(int wFlowDefnVersionNo) {
        this.wFlowDefnVersionNo = wFlowDefnVersionNo;
    }

    public String getwFlowDefnMakerId() {
        return wFlowDefnMakerId;
    }

    public void setwFlowDefnMakerId(String wFlowDefnMakerId) {
        this.wFlowDefnMakerId = wFlowDefnMakerId;
    }

    public Date getwFlowDefnMakerTs() {
        return wFlowDefnMakerTs;
    }

    public void setwFlowDefnMakerTs(Date wFlowDefnMakerTs) {
        this.wFlowDefnMakerTs = wFlowDefnMakerTs;
    }

    public String getwFlowDefnCheckerId() {
        return wFlowDefnCheckerId;
    }

    public void setwFlowDefnCheckerId(String wFlowDefnCheckerId) {
        this.wFlowDefnCheckerId = wFlowDefnCheckerId;
    }

    public Date getwFlowDefnCheckerTs() {
        return wFlowDefnCheckerTs;
    }

    public void setwFlowDefnCheckerTs(Date wFlowDefnCheckerTs) {
        this.wFlowDefnCheckerTs = wFlowDefnCheckerTs;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getwFlowDefnOnceAuth() {
        return wFlowDefnOnceAuth;
    }

    public void setwFlowDefnOnceAuth(String wFlowDefnOnceAuth) {
        this.wFlowDefnOnceAuth = wFlowDefnOnceAuth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiWorkflowDefn that = (TbAsmiWorkflowDefn) o;
        return wFlowDefnVersionNo == that.wFlowDefnVersionNo &&
                Objects.equals(id, that.id) &&
                Objects.equals(workflowDesc, that.workflowDesc) &&
                Objects.equals(wFlowDefnMakerId, that.wFlowDefnMakerId) &&
                Objects.equals(wFlowDefnMakerTs, that.wFlowDefnMakerTs) &&
                Objects.equals(wFlowDefnCheckerId, that.wFlowDefnCheckerId) &&
                Objects.equals(wFlowDefnCheckerTs, that.wFlowDefnCheckerTs) &&
                Objects.equals(authStatus, that.authStatus) &&
                Objects.equals(active, that.active) &&
                Objects.equals(txnType, that.txnType) &&
                Objects.equals(wFlowDefnOnceAuth, that.wFlowDefnOnceAuth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workflowDesc, wFlowDefnVersionNo, wFlowDefnMakerId, wFlowDefnMakerTs, wFlowDefnCheckerId, wFlowDefnCheckerTs, authStatus, active, txnType, wFlowDefnOnceAuth);
    }
}
