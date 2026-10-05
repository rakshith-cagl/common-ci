package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAshsWorkflowTxnDetailsPK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String wFlowTxnAppId;
    @Column(name = "WORKFLOW_REF_NO")
    private String workflowReferenceNo;
    @Column(name = "WORKFLOW_SEQ_NO")
    private int workflowSequenceNo;

    public TbAshsWorkflowTxnDetailsPK(String appId, String workflowRefNo, int workflowSeqNo) {
        this.wFlowTxnAppId = appId;
        this.workflowReferenceNo = workflowRefNo;
        this.workflowSequenceNo = workflowSeqNo;
    }

    public TbAshsWorkflowTxnDetailsPK() {
    }

    public String getwFlowTxnAppId() {
        return wFlowTxnAppId;
    }

    public void setwFlowTxnAppId(String wFlowTxnAppId) {
        this.wFlowTxnAppId = wFlowTxnAppId;
    }

    public String getWorkflowReferenceNo() {
        return workflowReferenceNo;
    }

    public void setWorkflowReferenceNo(String workflowReferenceNo) {
        this.workflowReferenceNo = workflowReferenceNo;
    }

    public int getWorkflowSequenceNo() {
        return workflowSequenceNo;
    }

    public void setWorkflowSequenceNo(int workflowSequenceNo) {
        this.workflowSequenceNo = workflowSequenceNo;
    }

    @Override
    public String toString() {
        return "TbAshsWorkflowTxnDetailsPK{" +
                "appId='" + wFlowTxnAppId + '\'' +
                ", workflowRefNo='" + workflowReferenceNo + '\'' +
                ", workflowSeqNo=" + workflowSequenceNo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAshsWorkflowTxnDetailsPK that = (TbAshsWorkflowTxnDetailsPK) o;
        return workflowSequenceNo == that.workflowSequenceNo &&
                Objects.equals(wFlowTxnAppId, that.wFlowTxnAppId) &&
                Objects.equals(workflowReferenceNo, that.workflowReferenceNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wFlowTxnAppId, workflowReferenceNo, workflowSequenceNo);
    }
}
