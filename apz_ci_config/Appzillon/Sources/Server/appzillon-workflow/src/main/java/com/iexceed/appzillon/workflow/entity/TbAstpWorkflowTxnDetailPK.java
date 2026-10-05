package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAstpWorkflowTxnDetailPK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "WORKFLOW_REF_NO")
    private String workflowRefNo;
    @Column(name = "WORKFLOW_SEQ_NO")
    private int workflowSeqNo;

    public TbAstpWorkflowTxnDetailPK() {
    }

    public TbAstpWorkflowTxnDetailPK(String appId, String workflowRefNo, int workflowSeqNo) {
        this.appId = appId;
        this.workflowRefNo = workflowRefNo;

        this.workflowSeqNo = workflowSeqNo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getWorkflowRefNo() {
        return workflowRefNo;
    }

    public void setWorkflowRefNo(String workflowRefNo) {
        this.workflowRefNo = workflowRefNo;
    }

    public int getWorkflowSeqNo() {
        return workflowSeqNo;
    }

    public void setWorkflowSeqNo(int workflowSeqNo) {
        this.workflowSeqNo = workflowSeqNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAstpWorkflowTxnDetailPK that = (TbAstpWorkflowTxnDetailPK) o;
        return workflowSeqNo == that.workflowSeqNo &&
                Objects.equals(appId, that.appId) &&
                Objects.equals(workflowRefNo, that.workflowRefNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, workflowRefNo, workflowSeqNo);
    }
}
