package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAsmiWorkflowRulesPK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "WORKFLOW_ID")
    private String workflowId;
    @Column(name = "RULE_ID")
    private String ruleId;

    public TbAsmiWorkflowRulesPK(String appId, String workflowId, String ruleId) {
        this.appId = appId;
        this.workflowId = workflowId;
        this.ruleId = ruleId;
    }

    public TbAsmiWorkflowRulesPK() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiWorkflowRulesPK that = (TbAsmiWorkflowRulesPK) o;
        return Objects.equals(appId, that.appId) &&
                Objects.equals(workflowId, that.workflowId) &&
                Objects.equals(ruleId, that.ruleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, workflowId, ruleId);
    }
}
