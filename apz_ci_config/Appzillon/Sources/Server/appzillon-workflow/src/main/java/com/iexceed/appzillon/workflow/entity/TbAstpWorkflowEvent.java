package com.iexceed.appzillon.workflow.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "TB_ASTP_WORKFLOW_EVENT")
public class TbAstpWorkflowEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private TbAstpWorkflowEventPK id;
    @Column(name = "WORKFLOW_ID")
    private String workflowId;
    @Column(name = "STAGE_ID")
    private String stageId;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "EVENT_ID")
    private String eventId;
    @Column(name = "EVENT_TS")
    private Date eventTS;
    @Column(name = "REMARKS")
    private String wFlowEventRemarks;
    @Lob
    @Column(name = "TASK_DATA")
    private String wFlowEventTaskData;
    @Column(name = "CUSTOM_FIELD_1")
    private String wFlowEventCustomField1;
    @Column(name = "CUSTOM_FIELD_2")
    private String wFlowEventCustomField2;
    @Column(name = "CUSTOM_FIELD_3")
    private String wFlowEventCustomField3;
    @Column(name = "CUSTOM_FIELD_4")
    private String wFlowEventCustomField4;
    @Column(name = "CUSTOM_FIELD_5")
    private String wFlowEventCustomField5;

    public TbAstpWorkflowEventPK getId() {
        return id;
    }

    public void setId(TbAstpWorkflowEventPK id) {
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

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public Date getEventTS() {
        return eventTS;
    }

    public void setEventTS(Date eventTS) {
        this.eventTS = eventTS;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getwFlowEventRemarks() {
        return wFlowEventRemarks;
    }

    public void setwFlowEventRemarks(String wFlowEventRemarks) {
        this.wFlowEventRemarks = wFlowEventRemarks;
    }

    public String getwFlowEventCustomField1() {
        return wFlowEventCustomField1;
    }

    public void setwFlowEventCustomField1(String wFlowEventCustomField1) {
        this.wFlowEventCustomField1 = wFlowEventCustomField1;
    }

    public String getwFlowEventCustomField2() {
        return wFlowEventCustomField2;
    }

    public void setwFlowEventCustomField2(String wFlowEventCustomField2) {
        this.wFlowEventCustomField2 = wFlowEventCustomField2;
    }

    public String getwFlowEventCustomField3() {
        return wFlowEventCustomField3;
    }

    public void setwFlowEventCustomField3(String wFlowEventCustomField3) {
        this.wFlowEventCustomField3 = wFlowEventCustomField3;
    }

    public String getwFlowEventCustomField4() {
        return wFlowEventCustomField4;
    }

    public void setwFlowEventCustomField4(String wFlowEventCustomField4) {
        this.wFlowEventCustomField4 = wFlowEventCustomField4;
    }

    public String getwFlowEventCustomField5() {
        return wFlowEventCustomField5;
    }

    public void setwFlowEventCustomField5(String wFlowEventCustomField5) {
        this.wFlowEventCustomField5 = wFlowEventCustomField5;
    }

    public String getwFlowEventTaskData() {
        return wFlowEventTaskData;
    }

    public void setwFlowEventTaskData(String wFlowEventTaskData) {
        this.wFlowEventTaskData = wFlowEventTaskData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAstpWorkflowEvent that = (TbAstpWorkflowEvent) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(workflowId, that.workflowId) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventTS, that.eventTS) &&
                Objects.equals(wFlowEventRemarks, that.wFlowEventRemarks) &&
                Objects.equals(wFlowEventTaskData, that.wFlowEventTaskData) &&
                Objects.equals(wFlowEventCustomField1, that.wFlowEventCustomField1) &&
                Objects.equals(wFlowEventCustomField2, that.wFlowEventCustomField2) &&
                Objects.equals(wFlowEventCustomField3, that.wFlowEventCustomField3) &&
                Objects.equals(wFlowEventCustomField4, that.wFlowEventCustomField4) &&
                Objects.equals(wFlowEventCustomField5, that.wFlowEventCustomField5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, workflowId, stageId, userId, eventId, eventTS, wFlowEventRemarks, wFlowEventTaskData, wFlowEventCustomField1, wFlowEventCustomField2, wFlowEventCustomField3, wFlowEventCustomField4, wFlowEventCustomField5);
    }
}
