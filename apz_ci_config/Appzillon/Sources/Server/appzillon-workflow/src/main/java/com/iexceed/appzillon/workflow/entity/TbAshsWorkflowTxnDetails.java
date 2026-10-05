package com.iexceed.appzillon.workflow.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "TB_ASHS_WORKFLOW_TXN_DETAILS")
public class TbAshsWorkflowTxnDetails implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private TbAshsWorkflowTxnDetailsPK tbAshsWorkflowTxnDetailsPK;
    @Column(name = "WORKFLOW_ID")
    private String workflowId;
    @Column(name = "STAGE_ID")
    private String stageId;
    @Lob
    @Column(name = "TASK_DATA")
    private String ashsWflowTxnDetailTaskData;
    @Column(name = "USER_ID")
    private String wFlowTxnDetailUserId;
    @Column(name = "STATUS")
    private String wFlowTxnDetailStatus;
    @Column(name = "UPDATED_NEXT_STAGES")
    private String wFlowTxnDetailUpdatedNextStages;
    @Column(name = "START_TS")
    private Date wFlowStartTS;
    @Column(name = "END_TS")
    private Date wFlowEndTS;
    @Column(name = "CUSTOM_FIELD_1")
    private String wFlowTxnCustomField1;
    @Column(name = "CUSTOM_FIELD_2")
    private String wFlowTxnCustomField2;
    @Column(name = "CUSTOM_FIELD_3")
    private String wFlowTxnCustomField3;
    @Column(name = "CUSTOM_FIELD_4")
    private String wFlowTxnCustomField4;
    @Column(name = "CUSTOM_FIELD_5")
    private String wFlowTxnCustomField5;

    public TbAshsWorkflowTxnDetailsPK getTbAshsWorkflowTxnDetailsPK() {
        return tbAshsWorkflowTxnDetailsPK;
    }

    public void setTbAshsWorkflowTxnDetailsPK(TbAshsWorkflowTxnDetailsPK tbAshsWorkflowTxnDetailsPK) {
        this.tbAshsWorkflowTxnDetailsPK = tbAshsWorkflowTxnDetailsPK;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }


    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getwFlowTxnDetailUserId() {
        return wFlowTxnDetailUserId;
    }

    public void setwFlowTxnDetailUserId(String wFlowTxnDetailUserId) {
        this.wFlowTxnDetailUserId = wFlowTxnDetailUserId;
    }

    public String getwFlowTxnDetailStatus() {
        return wFlowTxnDetailStatus;
    }

    public void setwFlowTxnDetailStatus(String wFlowTxnDetailStatus) {
        this.wFlowTxnDetailStatus = wFlowTxnDetailStatus;
    }

    public String getwFlowTxnDetailUpdatedNextStages() {
        return wFlowTxnDetailUpdatedNextStages;
    }

    public void setwFlowTxnDetailUpdatedNextStages(String wFlowTxnDetailUpdatedNextStages) {
        this.wFlowTxnDetailUpdatedNextStages = wFlowTxnDetailUpdatedNextStages;
    }

    public Date getwFlowStartTS() {
        return wFlowStartTS;
    }

    public void setwFlowStartTS(Date wFlowStartTS) {
        this.wFlowStartTS = wFlowStartTS;
    }

    public Date getwFlowEndTS() {
        return wFlowEndTS;
    }

    public void setwFlowEndTS(Date wFlowEndTS) {
        this.wFlowEndTS = wFlowEndTS;
    }

    public String getwFlowTxnCustomField1() {
        return wFlowTxnCustomField1;
    }

    public void setwFlowTxnCustomField1(String wFlowTxnCustomField1) {
        this.wFlowTxnCustomField1 = wFlowTxnCustomField1;
    }

    public String getwFlowTxnCustomField2() {
        return wFlowTxnCustomField2;
    }

    public void setwFlowTxnCustomField2(String wFlowTxnCustomField2) {
        this.wFlowTxnCustomField2 = wFlowTxnCustomField2;
    }

    public String getwFlowTxnCustomField3() {
        return wFlowTxnCustomField3;
    }

    public void setwFlowTxnCustomField3(String wFlowTxnCustomField3) {
        this.wFlowTxnCustomField3 = wFlowTxnCustomField3;
    }

    public String getwFlowTxnCustomField4() {
        return wFlowTxnCustomField4;
    }

    public void setwFlowTxnCustomField4(String wFlowTxnCustomField4) {
        this.wFlowTxnCustomField4 = wFlowTxnCustomField4;
    }

    public String getwFlowTxnCustomField5() {
        return wFlowTxnCustomField5;
    }

    public void setwFlowTxnCustomField5(String wFlowTxnCustomField5) {
        this.wFlowTxnCustomField5 = wFlowTxnCustomField5;
    }

    public String getAshsWflowTxnDetailTaskData() {
        return ashsWflowTxnDetailTaskData;
    }

    public void setAshsWflowTxnDetailTaskData(String ashsWflowTxnDetailTaskData) {
        this.ashsWflowTxnDetailTaskData = ashsWflowTxnDetailTaskData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAshsWorkflowTxnDetails that = (TbAshsWorkflowTxnDetails) o;
        return Objects.equals(tbAshsWorkflowTxnDetailsPK, that.tbAshsWorkflowTxnDetailsPK) &&
                Objects.equals(workflowId, that.workflowId) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(ashsWflowTxnDetailTaskData, that.ashsWflowTxnDetailTaskData) &&
                Objects.equals(wFlowTxnDetailUserId, that.wFlowTxnDetailUserId) &&
                Objects.equals(wFlowTxnDetailStatus, that.wFlowTxnDetailStatus) &&
                Objects.equals(wFlowStartTS, that.wFlowStartTS) &&
                Objects.equals(wFlowEndTS, that.wFlowEndTS) &&
                Objects.equals(wFlowTxnCustomField1, that.wFlowTxnCustomField1) &&
                Objects.equals(wFlowTxnCustomField2, that.wFlowTxnCustomField2) &&
                Objects.equals(wFlowTxnCustomField3, that.wFlowTxnCustomField3) &&
                Objects.equals(wFlowTxnCustomField4, that.wFlowTxnCustomField4) &&
                Objects.equals(wFlowTxnCustomField5, that.wFlowTxnCustomField5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tbAshsWorkflowTxnDetailsPK, workflowId, stageId, ashsWflowTxnDetailTaskData, wFlowTxnDetailUserId, wFlowTxnDetailStatus, wFlowStartTS, wFlowEndTS, wFlowTxnCustomField1, wFlowTxnCustomField2, wFlowTxnCustomField3, wFlowTxnCustomField4, wFlowTxnCustomField5);
    }
}
