package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbAsmiWorkflowDefnPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String wFlowDefnAppId;
    @Column(name = "WORKFLOW_ID")
    private String wFlowDefnWorkflowId;

    public TbAsmiWorkflowDefnPK(String appId, String workflowId) {
        this.wFlowDefnAppId = appId;
        this.wFlowDefnWorkflowId = workflowId;
    }

    public TbAsmiWorkflowDefnPK() {
    }

    public String getwFlowDefnAppId() {
        return wFlowDefnAppId;
    }

    public void setwFlowDefnAppId(String wFlowDefnAppId) {
        this.wFlowDefnAppId = wFlowDefnAppId;
    }

    public String getwFlowDefnWorkflowId() {
        return wFlowDefnWorkflowId;
    }

    public void setwFlowDefnWorkflowId(String wFlowDefnWorkflowId) {
        this.wFlowDefnWorkflowId = wFlowDefnWorkflowId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiWorkflowDefnPK that = (TbAsmiWorkflowDefnPK) o;
        return Objects.equals(wFlowDefnAppId, that.wFlowDefnAppId) &&
                Objects.equals(wFlowDefnWorkflowId, that.wFlowDefnWorkflowId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wFlowDefnAppId, wFlowDefnWorkflowId);
    }
}
