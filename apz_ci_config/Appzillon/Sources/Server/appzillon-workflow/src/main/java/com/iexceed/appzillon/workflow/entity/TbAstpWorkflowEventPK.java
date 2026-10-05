package com.iexceed.appzillon.workflow.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class TbAstpWorkflowEventPK implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "APP_ID")
    private String wFlowEventAppId;
    @Column(name = "WORKFLOW_REF_NO")
    private String wFlowEventRefNo;
    @Column(name = "WORKFLOW_SEQ_NO")
    private int wFlowEventSeqNo;
    @Column(name = "EVENT_SEQ_NO")
    private int eventSeqNo;


    public TbAstpWorkflowEventPK() {
    }

    public TbAstpWorkflowEventPK(String appId, String workflowRefNo, int workflowSeqNo, int eventSeqNo) {
        this.wFlowEventAppId = appId;
        this.wFlowEventRefNo = workflowRefNo;
        this.wFlowEventSeqNo = workflowSeqNo;
        this.eventSeqNo = eventSeqNo;
    }

    public String getwFlowEventAppId() {
        return wFlowEventAppId;
    }

    public void setwFlowEventAppId(String wFlowEventAppId) {
        this.wFlowEventAppId = wFlowEventAppId;
    }

    public String getwFlowEventRefNo() {
        return wFlowEventRefNo;
    }

    public void setwFlowEventRefNo(String wFlowEventRefNo) {
        this.wFlowEventRefNo = wFlowEventRefNo;
    }

    public int getEventSeqNo() {
        return eventSeqNo;
    }

    public void setEventSeqNo(int eventSeqNo) {
        this.eventSeqNo = eventSeqNo;
    }

    public int getwFlowEventSeqNo() {
        return wFlowEventSeqNo;
    }

    public void setwFlowEventSeqNo(int wFlowEventSeqNo) {
        this.wFlowEventSeqNo = wFlowEventSeqNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAstpWorkflowEventPK that = (TbAstpWorkflowEventPK) o;
        return wFlowEventSeqNo == that.wFlowEventSeqNo &&
                eventSeqNo == that.eventSeqNo &&
                Objects.equals(wFlowEventAppId, that.wFlowEventAppId) &&
                Objects.equals(wFlowEventRefNo, that.wFlowEventRefNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wFlowEventAppId, wFlowEventRefNo, wFlowEventSeqNo, eventSeqNo);
    }
}
