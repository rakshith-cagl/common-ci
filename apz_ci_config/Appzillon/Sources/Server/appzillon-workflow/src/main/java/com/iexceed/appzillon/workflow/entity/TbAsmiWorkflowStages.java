package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "TB_ASMI_WORKFLOW_STAGES")
public class TbAsmiWorkflowStages {

    @EmbeddedId
    private TbAsmiWorkflowStagesPK id;
    @Column(name = "STAGE_SEQ_NO")
    private int stageSeqNo;
    @Column(name = "STAGE_TYPE")
    private String stageType;
    @Column(name = "STAGE_DESC")
    private String stageDesc;
    @Column(name = "RULE_ID")
    private String ruleId;
    @Column(name = "SCREEN_ID")
    private String screenId;
    @Column(name = "IS_AUTH_MATRIX_REQD")
    private String isAuthMatrixReqd;

    public TbAsmiWorkflowStages() {
    }

    public TbAsmiWorkflowStages(TbAsmiWorkflowStagesPK tbAsmiWorkflowStagesPK, int stageSeqNo, String stageType, String stageDesc, String ruleId, String screenId) {
        this.id = tbAsmiWorkflowStagesPK;
        this.stageSeqNo = stageSeqNo;
        this.stageType = stageType;
        this.stageDesc = stageDesc;
        this.ruleId = ruleId;
        this.screenId = screenId;
    }

    public TbAsmiWorkflowStagesPK getId() {
        return id;
    }

    public void setId(TbAsmiWorkflowStagesPK id) {
        this.id = id;
    }

    public int getStageSeqNo() {
        return stageSeqNo;
    }

    public void setStageSeqNo(int stageSeqNo) {
        this.stageSeqNo = stageSeqNo;
    }

    public String getStageType() {
        return stageType;
    }

    public void setStageType(String stageType) {
        this.stageType = stageType;
    }

    public String getStageDesc() {
        return stageDesc;
    }

    public void setStageDesc(String stageDesc) {
        this.stageDesc = stageDesc;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getIsAuthMatrixReqd() {
        return isAuthMatrixReqd;
    }

    public void setIsAuthMatrixReqd(String isAuthMatrixReqd) {
        this.isAuthMatrixReqd = isAuthMatrixReqd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiWorkflowStages that = (TbAsmiWorkflowStages) o;
        return stageSeqNo == that.stageSeqNo &&
                Objects.equals(id, that.id) &&
                Objects.equals(stageType, that.stageType) &&
                Objects.equals(stageDesc, that.stageDesc) &&
                Objects.equals(ruleId, that.ruleId) &&
                Objects.equals(screenId, that.screenId) &&
                Objects.equals(isAuthMatrixReqd, that.isAuthMatrixReqd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stageSeqNo, stageType, stageDesc, ruleId, screenId, isAuthMatrixReqd);
    }
}
