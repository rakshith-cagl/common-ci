package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TbAstpWorkflowDetPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "TRANSACTION_REF_NO")
    private String transactionRefNo;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "WORKFLOW_ID")
    private String workflowId;

    public TbAstpWorkflowDetPK() {
    }

    public TbAstpWorkflowDetPK(String transactionRefNo, String appId, String workflowId) {
        this.transactionRefNo = transactionRefNo;
        this.appId = appId;
        this.workflowId = workflowId;
    }

    public String getTransactionRefNo() {
        return this.transactionRefNo;
    }

    public void setTransactionRefNo(String transactionRefNo) {
        this.transactionRefNo = transactionRefNo;
    }

    public String getWorkflowId() {
        return this.workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TbAstpWorkflowDetPK)) {
            return false;
        }
        TbAstpWorkflowDetPK castOther = (TbAstpWorkflowDetPK) other;
        return
                this.transactionRefNo.equals(castOther.transactionRefNo)
                        && this.workflowId.equals(castOther.workflowId)
                        && this.appId.equals(castOther.appId);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.transactionRefNo.hashCode();
        hash = hash * prime + this.workflowId.hashCode();
        hash = hash * prime + this.appId.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "TbAftpWorkflowDetPK [transactionRefNo=" + transactionRefNo
                + ", appId=" + appId + ", workflowId=" + workflowId + "]";
    }
}