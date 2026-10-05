package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "TB_ASTP_WORKFLOW_TXN_MASTER")
public class TbAstpWorkflowTxnMaster implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private TbAstpWorkflowTxnMasterPK tbAstpWorkflowTxnMasterPK;
    @Column(name = "WORKFLOW_ID")
    private String workflowId;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "START_TS")
    private Date startTS;
    @Column(name = "MODIFIED_TS")
    private Date modifiedTS;
    @Column(name = "END_TS")
    private Date endTS;
    @Column(name = "CUSTOM_FIELD_1")
    private String customField1;
    @Column(name = "CUSTOM_FIELD_2")
    private String customField2;
    @Column(name = "CUSTOM_FIELD_3")
    private String customField3;
    @Column(name = "CUSTOM_FIELD_4")
    private String customField4;
    @Column(name = "CUSTOM_FIELD_5")
    private String customField5;

    public TbAstpWorkflowTxnMasterPK getTbAstpWorkflowTxnMasterPK() {
        return tbAstpWorkflowTxnMasterPK;
    }

    public void setTbAstpWorkflowTxnMasterPK(TbAstpWorkflowTxnMasterPK tbAstpWorkflowTxnMasterPK) {
        this.tbAstpWorkflowTxnMasterPK = tbAstpWorkflowTxnMasterPK;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTS() {
        return startTS;
    }

    public void setStartTS(Date startTS) {
        this.startTS = startTS;
    }

    public Date getModifiedTS() {
        return modifiedTS;
    }

    public void setModifiedTS(Date modifiedTS) {
        this.modifiedTS = modifiedTS;
    }

    public Date getEndTS() {
        return endTS;
    }

    public void setEndTS(Date endTS) {
        this.endTS = endTS;
    }

    public String getCustomField1() {
        return customField1;
    }

    public void setCustomField1(String customField1) {
        this.customField1 = customField1;
    }

    public String getCustomField2() {
        return customField2;
    }

    public void setCustomField2(String customField2) {
        this.customField2 = customField2;
    }

    public String getCustomField3() {
        return customField3;
    }

    public void setCustomField3(String customField3) {
        this.customField3 = customField3;
    }

    public String getCustomField4() {
        return customField4;
    }

    public void setCustomField4(String customField4) {
        this.customField4 = customField4;
    }

    public String getCustomField5() {
        return customField5;
    }

    public void setCustomField5(String customField5) {
        this.customField5 = customField5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAstpWorkflowTxnMaster that = (TbAstpWorkflowTxnMaster) o;
        return Objects.equals(tbAstpWorkflowTxnMasterPK, that.tbAstpWorkflowTxnMasterPK) &&
                Objects.equals(workflowId, that.workflowId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(startTS, that.startTS) &&
                Objects.equals(modifiedTS, that.modifiedTS) &&
                Objects.equals(endTS, that.endTS) &&
                Objects.equals(customField1, that.customField1) &&
                Objects.equals(customField2, that.customField2) &&
                Objects.equals(customField3, that.customField3) &&
                Objects.equals(customField4, that.customField4) &&
                Objects.equals(customField5, that.customField5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tbAstpWorkflowTxnMasterPK, workflowId, status, startTS, modifiedTS, endTS, customField1, customField2, customField3, customField4, customField5);
    }
}
