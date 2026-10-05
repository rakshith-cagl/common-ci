package com.iexceed.appzillon.domain.entity.history;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Ripu
 */
@Embeddable
public class TbAshsWorkflowDetPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "TRANSACTION_REF_NO")
    private String transactionRefNo;

    @Column(name = "VERSION_NO")
    private long versionNo;

    @Column(name = "WORKFLOW_ID")
    private String workflowId;

    @Column(name = "APP_ID")
    private String appId;

    public TbAshsWorkflowDetPK() {
        // Default constructor
    }

    public String getTransactionRefNo() {
        return this.transactionRefNo;
    }

    public void setTransactionRefNo(String transactionRefNo) {
        this.transactionRefNo = transactionRefNo;
    }

    public long getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(long versionNo) {
        this.versionNo = versionNo;
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
        if (!(other instanceof TbAshsWorkflowDetPK)) {
            return false;
        }
        TbAshsWorkflowDetPK castOther = (TbAshsWorkflowDetPK) other;
        return
                this.transactionRefNo.equals(castOther.transactionRefNo)
                        && (this.versionNo == castOther.versionNo)
                        && this.workflowId.equals(castOther.workflowId)
                        && this.appId.equals(castOther.appId);
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.transactionRefNo.hashCode();
        hash = hash * prime + ((int) (this.versionNo ^ (this.versionNo >>> 32)));
        hash = hash * prime + this.workflowId.hashCode();
        hash = hash * prime + this.appId.hashCode();

        return hash;
    }

    @Override
    public String toString() {
        return "TbAfhsWorkflowDetPK [transactionRefNo=" + transactionRefNo
                + ", versionNo=" + versionNo + ", workflowId=" + workflowId
                + ", appId=" + appId + "]";
    }


}