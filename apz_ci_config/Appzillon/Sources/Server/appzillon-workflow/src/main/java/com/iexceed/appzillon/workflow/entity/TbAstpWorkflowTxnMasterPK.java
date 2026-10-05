package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAstpWorkflowTxnMasterPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "WORKFLOW_REF_NO")
    private String workflowRefNo;

    public TbAstpWorkflowTxnMasterPK() {
    }

    public TbAstpWorkflowTxnMasterPK(String appId, String workflowRefNo) {
        this.appId = appId;
        this.workflowRefNo = workflowRefNo;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAstpWorkflowTxnMasterPK that = (TbAstpWorkflowTxnMasterPK) o;
        return Objects.equals(appId, that.appId) &&
                Objects.equals(workflowRefNo, that.workflowRefNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, workflowRefNo);
    }
}
