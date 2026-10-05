package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "TB_ASHS_WORKFLOW_TXN_MASTER")
public class TbAshsWorkflowTxnMaster {
    @EmbeddedId
    private TbAshsWorkflowTxnMasterPK tbAshsWorkflowTxnMasterPK;
    @Column(name = "WORKFLOW_ID")
    private String wFlowTxnMasterWorkflowId;
    @Column(name = "STATUS")
    private String wFlowTxnMasterStatus;
    @Column(name = "START_TS")
    private Date wFlowTxnMasterStartTs;
    @Column(name = "MODIFIED_TS")
    private Date wFlowTxnMasterModifiedTs;
    @Column(name = "END_TS")
    private Date wFlowTxnMasterEndTs;
    @Column(name = "CUSTOM_FIELD_1")
    private String wFlowTxnMasterCustomField1;
    @Column(name = "CUSTOM_FIELD_2")
    private String wFlowTxnMasterCustomField2;
    @Column(name = "CUSTOM_FIELD_3")
    private String wFlowTxnMasterCustomField3;
    @Column(name = "CUSTOM_FIELD_4")
    private String wFlowTxnMasterCustomField4;
    @Column(name = "CUSTOM_FIELD_5")
    private String wFlowTxnMasterCustomField5;

    public TbAshsWorkflowTxnMasterPK getTbAshsWorkflowTxnMasterPK() {
        return tbAshsWorkflowTxnMasterPK;
    }

    public void setTbAshsWorkflowTxnMasterPK(TbAshsWorkflowTxnMasterPK tbAshsWorkflowTxnMasterPK) {
        this.tbAshsWorkflowTxnMasterPK = tbAshsWorkflowTxnMasterPK;
    }

    public String getwFlowTxnMasterWorkflowId() {
        return wFlowTxnMasterWorkflowId;
    }

    public void setwFlowTxnMasterWorkflowId(String wFlowTxnMasterWorkflowId) {
        this.wFlowTxnMasterWorkflowId = wFlowTxnMasterWorkflowId;
    }

    public String getwFlowTxnMasterStatus() {
        return wFlowTxnMasterStatus;
    }

    public void setwFlowTxnMasterStatus(String wFlowTxnMasterStatus) {
        this.wFlowTxnMasterStatus = wFlowTxnMasterStatus;
    }

    public Date getwFlowTxnMasterStartTs() {
        return wFlowTxnMasterStartTs;
    }

    public void setwFlowTxnMasterStartTs(Date wFlowTxnMasterStartTs) {
        this.wFlowTxnMasterStartTs = wFlowTxnMasterStartTs;
    }

    public Date getwFlowTxnMasterModifiedTs() {
        return wFlowTxnMasterModifiedTs;
    }

    public void setwFlowTxnMasterModifiedTs(Date wFlowTxnMasterModifiedTs) {
        this.wFlowTxnMasterModifiedTs = wFlowTxnMasterModifiedTs;
    }

    public Date getwFlowTxnMasterEndTs() {
        return wFlowTxnMasterEndTs;
    }

    public void setwFlowTxnMasterEndTs(Date wFlowTxnMasterEndTs) {
        this.wFlowTxnMasterEndTs = wFlowTxnMasterEndTs;
    }

    public String getwFlowTxnMasterCustomField1() {
        return wFlowTxnMasterCustomField1;
    }

    public void setwFlowTxnMasterCustomField1(String wFlowTxnMasterCustomField1) {
        this.wFlowTxnMasterCustomField1 = wFlowTxnMasterCustomField1;
    }

    public String getwFlowTxnMasterCustomField2() {
        return wFlowTxnMasterCustomField2;
    }

    public void setwFlowTxnMasterCustomField2(String wFlowTxnMasterCustomField2) {
        this.wFlowTxnMasterCustomField2 = wFlowTxnMasterCustomField2;
    }

    public String getwFlowTxnMasterCustomField3() {
        return wFlowTxnMasterCustomField3;
    }

    public void setwFlowTxnMasterCustomField3(String wFlowTxnMasterCustomField3) {
        this.wFlowTxnMasterCustomField3 = wFlowTxnMasterCustomField3;
    }

    public String getwFlowTxnMasterCustomField4() {
        return wFlowTxnMasterCustomField4;
    }

    public void setwFlowTxnMasterCustomField4(String wFlowTxnMasterCustomField4) {
        this.wFlowTxnMasterCustomField4 = wFlowTxnMasterCustomField4;
    }

    public String getwFlowTxnMasterCustomField5() {
        return wFlowTxnMasterCustomField5;
    }

    public void setwFlowTxnMasterCustomField5(String wFlowTxnMasterCustomField5) {
        this.wFlowTxnMasterCustomField5 = wFlowTxnMasterCustomField5;
    }

    @Override
    public String toString() {
        return "TbAshsWorkflowTxnMaster{" +
                "tbAshsWorkflowTxnMasterPK=" + tbAshsWorkflowTxnMasterPK +
                ", workflowId='" + wFlowTxnMasterWorkflowId + '\'' +
                ", status='" + wFlowTxnMasterStatus + '\'' +
                ", startTS=" + wFlowTxnMasterStartTs +
                ", modifiedTS=" + wFlowTxnMasterModifiedTs +
                ", endTS=" + wFlowTxnMasterEndTs +
                ", customField1='" + wFlowTxnMasterCustomField1 + '\'' +
                ", customField2='" + wFlowTxnMasterCustomField2 + '\'' +
                ", customField3='" + wFlowTxnMasterCustomField3 + '\'' +
                ", customField4='" + wFlowTxnMasterCustomField4 + '\'' +
                ", customField5='" + wFlowTxnMasterCustomField5 + '\'' +
                '}';
    }
}
