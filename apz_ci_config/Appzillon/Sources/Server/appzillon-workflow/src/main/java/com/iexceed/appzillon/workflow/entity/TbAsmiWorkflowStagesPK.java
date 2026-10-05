package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAsmiWorkflowStagesPK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String wFlowStagesAppId;
    @Column(name = "WORKFLOW_ID")
    private String wFlowStagesWorkflowId;
    @Column(name = "STAGE_ID")
    private String stageId;

    public TbAsmiWorkflowStagesPK() {
    }

    public TbAsmiWorkflowStagesPK(String appId, String workflowId, String stageId) {
        this.wFlowStagesAppId = appId;
        this.wFlowStagesWorkflowId = workflowId;
        this.stageId = stageId;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getwFlowStagesAppId() {
        return wFlowStagesAppId;
    }

    public void setwFlowStagesAppId(String wFlowStagesAppId) {
        this.wFlowStagesAppId = wFlowStagesAppId;
    }

    public String getwFlowStagesWorkflowId() {
        return wFlowStagesWorkflowId;
    }

    public void setwFlowStagesWorkflowId(String wFlowStagesWorkflowId) {
        this.wFlowStagesWorkflowId = wFlowStagesWorkflowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiWorkflowStagesPK that = (TbAsmiWorkflowStagesPK) o;
        return Objects.equals(wFlowStagesAppId, that.wFlowStagesAppId) &&
                Objects.equals(wFlowStagesWorkflowId, that.wFlowStagesWorkflowId) &&
                Objects.equals(stageId, that.stageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wFlowStagesAppId, wFlowStagesWorkflowId, stageId);
    }
}
