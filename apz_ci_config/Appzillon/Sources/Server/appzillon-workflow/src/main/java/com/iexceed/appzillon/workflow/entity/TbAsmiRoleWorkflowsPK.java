package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAsmiRoleWorkflowsPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "ROLE_ID")
    private String roleId;

    @Column(name = "WORKFLOW_ID")
    private String workflowId;

    @Column(name = "STAGE_ID")
    private String stageId;

    public TbAsmiRoleWorkflowsPK() {
    }

    public TbAsmiRoleWorkflowsPK(String appId, String roleId, String workflowId, String stageId) {
        this.appId = appId;
        this.roleId = roleId;
        this.workflowId = workflowId;
        this.stageId = stageId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiRoleWorkflowsPK that = (TbAsmiRoleWorkflowsPK) o;
        return Objects.equals(appId, that.appId) &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(workflowId, that.workflowId) &&
                Objects.equals(stageId, that.stageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, roleId, workflowId, stageId);
    }
}
