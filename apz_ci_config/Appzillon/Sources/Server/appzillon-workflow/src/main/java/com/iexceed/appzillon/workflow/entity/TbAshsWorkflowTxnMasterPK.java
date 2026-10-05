package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAshsWorkflowTxnMasterPK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String wFlowTxnMasterAppId;
    @Column(name = "WORKFLOW_REF_NO")
    private String wFlowtxnMasterRefNo;

    public TbAshsWorkflowTxnMasterPK() {
    }

    public TbAshsWorkflowTxnMasterPK(String appId, String workflowRefNo) {
        this.wFlowTxnMasterAppId = appId;
        this.wFlowtxnMasterRefNo = workflowRefNo;
    }

    public String getwFlowTxnMasterAppId() {
        return wFlowTxnMasterAppId;
    }

    public void setwFlowTxnMasterAppId(String wFlowTxnMasterAppId) {
        this.wFlowTxnMasterAppId = wFlowTxnMasterAppId;
    }

    public String getwFlowtxnMasterRefNo() {
        return wFlowtxnMasterRefNo;
    }

    public void setwFlowtxnMasterRefNo(String wFlowtxnMasterRefNo) {
        this.wFlowtxnMasterRefNo = wFlowtxnMasterRefNo;
    }

    @Override
    public String toString() {
        return "TbAshsWorkflowTxnMasterPK{" +
                "appId='" + wFlowTxnMasterAppId + '\'' +
                ", workflowRefNo='" + wFlowtxnMasterRefNo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAshsWorkflowTxnMasterPK that = (TbAshsWorkflowTxnMasterPK) o;
        return Objects.equals(wFlowTxnMasterAppId, that.wFlowTxnMasterAppId) &&
                Objects.equals(wFlowtxnMasterRefNo, that.wFlowtxnMasterRefNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wFlowTxnMasterAppId, wFlowtxnMasterRefNo);
    }
}
