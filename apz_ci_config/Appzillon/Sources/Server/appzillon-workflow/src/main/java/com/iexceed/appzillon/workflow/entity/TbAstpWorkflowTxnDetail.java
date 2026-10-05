package com.iexceed.appzillon.workflow.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "TB_ASTP_WORKFLOW_TXN_DETAIL")
public class TbAstpWorkflowTxnDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private TbAstpWorkflowTxnDetailPK id;
    @Column(name = "WORKFLOW_ID")
    private String workflowId;

    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "UPDATED_NEXT_STAGES")
    private String updatedNextStages;
    @Column(name = "LAST_EVENT_NO")
    private int lastEventNo;
    @Column(name = "START_TS")
    private Date startTS;
    @Column(name = "STAGE_ID")
    private String stageId;
    @Column(name = "END_TS")
    private Date endTS;
    @Column(name = "REMARKS")
    private String astpWflowTxnDetailRemarks;
    @Lob
    @Column(name = "TASK_DATA")
    private String astpWflowTxnDetailTaskData;
    @Column(name = "CUSTOM_FIELD_1")
    private String astpWflowTxnDetailCustomField1;
    @Column(name = "CUSTOM_FIELD_2")
    private String astpWflowTxnDetailCustomField2;
    @Column(name = "CUSTOM_FIELD_3")
    private String astpWflowTxnDetailCustomField3;
    @Column(name = "CUSTOM_FIELD_4")
    private String astpWflowTxnDetailCustomField4;
    @Column(name = "CUSTOM_FIELD_5")
    private String astpWflowTxnDetailCustomField5;

    public TbAstpWorkflowTxnDetailPK getId() {
        return id;
    }

    public void setId(TbAstpWorkflowTxnDetailPK id) {
        this.id = id;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedNextStages() {
        return updatedNextStages;
    }

    public void setNextStages(String updatedNextStages) {
        this.updatedNextStages = updatedNextStages;
    }

    public int getLastEventNo() {
        return lastEventNo;
    }

    public void setLastEventNo(int lastEventNo) {
        this.lastEventNo = lastEventNo;
    }

    public Date getStartTS() {
        return startTS;
    }

    public void setStartTS(Date startTS) {
        this.startTS = startTS;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public Date getEndTS() {
        return endTS;
    }

    public void setEndTS(Date endTS) {
        this.endTS = endTS;
    }

    public String getAstpWflowTxnDetailRemarks() {
        return astpWflowTxnDetailRemarks;
    }

    public void setAstpWflowTxnDetailRemarks(String astpWflowTxnDetailRemarks) {
        this.astpWflowTxnDetailRemarks = astpWflowTxnDetailRemarks;
    }

    public String getAstpWflowTxnDetailCustomField1() {
        return astpWflowTxnDetailCustomField1;
    }

    public void setAstpWflowTxnDetailCustomField1(String astpWflowTxnDetailCustomField1) {
        this.astpWflowTxnDetailCustomField1 = astpWflowTxnDetailCustomField1;
    }

    public String getAstpWflowTxnDetailCustomField2() {
        return astpWflowTxnDetailCustomField2;
    }

    public void setAstpWflowTxnDetailCustomField2(String astpWflowTxnDetailCustomField2) {
        this.astpWflowTxnDetailCustomField2 = astpWflowTxnDetailCustomField2;
    }

    public String getAstpWflowTxnDetailCustomField3() {
        return astpWflowTxnDetailCustomField3;
    }

    public void setAstpWflowTxnDetailCustomField3(String astpWflowTxnDetailCustomField3) {
        this.astpWflowTxnDetailCustomField3 = astpWflowTxnDetailCustomField3;
    }

    public String getAstpWflowTxnDetailCustomField4() {
        return astpWflowTxnDetailCustomField4;
    }

    public void setAstpWflowTxnDetailCustomField4(String astpWflowTxnDetailCustomField4) {
        this.astpWflowTxnDetailCustomField4 = astpWflowTxnDetailCustomField4;
    }

    public String getAstpWflowTxnDetailCustomField5() {
        return astpWflowTxnDetailCustomField5;
    }

    public void setAstpWflowTxnDetailCustomField5(String astpWflowTxnDetailCustomField5) {
        this.astpWflowTxnDetailCustomField5 = astpWflowTxnDetailCustomField5;
    }

    public String getAstpWflowTxnDetailTaskData() {
        return astpWflowTxnDetailTaskData;
    }

    public void setAstpWflowTxnDetailTaskData(String astpWflowTxnDetailTaskData) {
        this.astpWflowTxnDetailTaskData = astpWflowTxnDetailTaskData;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAstpWorkflowTxnDetail that = (TbAstpWorkflowTxnDetail) o;
        return lastEventNo == that.lastEventNo &&
                Objects.equals(id, that.id) &&
                Objects.equals(workflowId, that.workflowId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(status, that.status) &&
                Objects.equals(updatedNextStages, that.updatedNextStages) &&
                Objects.equals(startTS, that.startTS) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(endTS, that.endTS) &&
                Objects.equals(astpWflowTxnDetailRemarks, that.astpWflowTxnDetailRemarks) &&
                Objects.equals(astpWflowTxnDetailTaskData, that.astpWflowTxnDetailTaskData) &&
                Objects.equals(astpWflowTxnDetailCustomField1, that.astpWflowTxnDetailCustomField1) &&
                Objects.equals(astpWflowTxnDetailCustomField2, that.astpWflowTxnDetailCustomField2) &&
                Objects.equals(astpWflowTxnDetailCustomField3, that.astpWflowTxnDetailCustomField3) &&
                Objects.equals(astpWflowTxnDetailCustomField4, that.astpWflowTxnDetailCustomField4) &&
                Objects.equals(astpWflowTxnDetailCustomField5, that.astpWflowTxnDetailCustomField5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workflowId, userId, status, updatedNextStages, lastEventNo, startTS, stageId, endTS, astpWflowTxnDetailRemarks, astpWflowTxnDetailTaskData, astpWflowTxnDetailCustomField1, astpWflowTxnDetailCustomField2, astpWflowTxnDetailCustomField3, astpWflowTxnDetailCustomField4, astpWflowTxnDetailCustomField5);
    }
}
