package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAsmiWorkflowLinkedStagesPK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "WORKFLOW_ID")
    private String workflowId;
    @Column(name = "STAGE_ID")
    private String stageId;
    @Column(name = "LINKAGE_TYPE")
    private String linkageType;
    @Column(name = "LINKED_STAGE_ID")
    private String linkedStageId;


    public TbAsmiWorkflowLinkedStagesPK(String appId, String workflowId, String stageId, String linkageType, String linkedStageId) {
        this.appId = appId;
        this.workflowId = workflowId;
        this.stageId = stageId;
        this.linkageType = linkageType;
        this.linkedStageId = linkedStageId;
    }

    public TbAsmiWorkflowLinkedStagesPK() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public String getLinkageType() {
        return linkageType;
    }

    public void setLinkageType(String linkageType) {
        this.linkageType = linkageType;
    }

    public String getLinkedStageId() {
        return linkedStageId;
    }

    public void setLinkedStageId(String linkedStageId) {
        this.linkedStageId = linkedStageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiWorkflowLinkedStagesPK that = (TbAsmiWorkflowLinkedStagesPK) o;
        return linkageType.equals(that.linkageType) &&
                Objects.equals(appId, that.appId) &&
                Objects.equals(workflowId, that.workflowId) &&
                Objects.equals(stageId, that.stageId) &&
                Objects.equals(linkedStageId, that.linkedStageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, workflowId, stageId, linkageType, linkedStageId);
    }
}
