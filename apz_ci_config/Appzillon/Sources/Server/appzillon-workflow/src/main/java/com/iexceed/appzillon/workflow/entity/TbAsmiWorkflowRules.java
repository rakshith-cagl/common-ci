package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "TB_ASMI_WORKFLOW_RULES")
public class TbAsmiWorkflowRules implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiWorkflowRulesPK id;
    @Column(name = "RULE_BEAN")
    private String ruleBean;
    @Column(name = "VERSION_NO")
    private int wFlowRulesVersionNo;
    @Column(name = "MAKER_ID")
    private String wFlowRulesMakerId;
    @Column(name = "MAKER_TS")
    private Date wFlowRulesMakerTs;
    @Column(name = "CHECKER_ID")
    private String wFlowRulesCheckerId;
    @Column(name = "CHECKER_TS")
    private Date wFlowRulesCheckerTs;
    @Column(name = "AUTH_STATUS")
    private char authStatus;
    @Column(name = "RULE_DESC")
    private String ruleDesc;
    @Column(name = "ACTIVE")
    private String active;
    @Column(name = "ONCE_AUTH")
    private String onceAuth;

    public TbAsmiWorkflowRulesPK getId() {
        return id;
    }

    public void setId(TbAsmiWorkflowRulesPK id) {
        this.id = id;
    }

    public String getRuleBean() {
        return ruleBean;
    }

    public void setRuleBean(String ruleBean) {
        this.ruleBean = ruleBean;
    }

    public int getwFlowRulesVersionNo() {
        return wFlowRulesVersionNo;
    }

    public void setwFlowRulesVersionNo(int wFlowRulesVersionNo) {
        this.wFlowRulesVersionNo = wFlowRulesVersionNo;
    }

    public String getwFlowRulesMakerId() {
        return wFlowRulesMakerId;
    }

    public void setwFlowRulesMakerId(String wFlowRulesMakerId) {
        this.wFlowRulesMakerId = wFlowRulesMakerId;
    }

    public Date getwFlowRulesMakerTs() {
        return wFlowRulesMakerTs;
    }

    public void setwFlowRulesMakerTs(Date wFlowRulesMakerTs) {
        this.wFlowRulesMakerTs = wFlowRulesMakerTs;
    }

    public String getwFlowRulesCheckerId() {
        return wFlowRulesCheckerId;
    }

    public void setwFlowRulesCheckerId(String wFlowRulesCheckerId) {
        this.wFlowRulesCheckerId = wFlowRulesCheckerId;
    }

    public Date getwFlowRulesCheckerTs() {
        return wFlowRulesCheckerTs;
    }

    public void setwFlowRulesCheckerTs(Date wFlowRulesCheckerTs) {
        this.wFlowRulesCheckerTs = wFlowRulesCheckerTs;
    }

    public char getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(char authStatus) {
        this.authStatus = authStatus;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;

    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getOnceAuth() {
        return onceAuth;
    }

    public void setOnceAuth(String onceAuth) {
        this.onceAuth = onceAuth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiWorkflowRules that = (TbAsmiWorkflowRules) o;
        return wFlowRulesVersionNo == that.wFlowRulesVersionNo &&
                authStatus == that.authStatus &&
                Objects.equals(id, that.id) &&
                Objects.equals(ruleBean, that.ruleBean) &&
                Objects.equals(wFlowRulesMakerId, that.wFlowRulesMakerId) &&
                Objects.equals(wFlowRulesMakerTs, that.wFlowRulesMakerTs) &&
                Objects.equals(wFlowRulesCheckerId, that.wFlowRulesCheckerId) &&
                Objects.equals(wFlowRulesCheckerTs, that.wFlowRulesCheckerTs) &&
                Objects.equals(ruleDesc, that.ruleDesc) &&
                Objects.equals(active, that.active) &&
                Objects.equals(onceAuth, that.onceAuth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ruleBean, wFlowRulesVersionNo, wFlowRulesMakerId, wFlowRulesMakerTs, wFlowRulesCheckerId, wFlowRulesCheckerTs, authStatus, ruleDesc, active, onceAuth);
    }
}
