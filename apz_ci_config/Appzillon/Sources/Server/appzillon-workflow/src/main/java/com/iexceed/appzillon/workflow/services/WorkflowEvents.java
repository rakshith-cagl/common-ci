package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowEvent;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowEventPK;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnDetail;
import com.iexceed.appzillon.workflow.exception.WorkflowParallelException;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowEventRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Service
@Transactional(value = TRANSACTION_APPZILLON_ADMIN, noRollbackFor = {WorkflowParallelException.class})
public class WorkflowEvents {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_WORKFLOW,
            WorkflowEvents.class.getName());

    @Inject
    private TbAstpWorkflowEventRepo tbAstpWorkflowEventRepo;

    public void saveEvent(JSONObject workflowReq, TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail, int eventSeqNo,
                          String eventId, String userId) {
        TbAstpWorkflowEventPK id = new TbAstpWorkflowEventPK(
                workflowReq.getString(MESSAGE_HEADER_APP_ID),
                tbAstpWorkflowTxnDetail.getId().getWorkflowRefNo(),
                tbAstpWorkflowTxnDetail.getId().getWorkflowSeqNo(),
                eventSeqNo);

        TbAstpWorkflowEvent tbAstpWorkflowEvent = new TbAstpWorkflowEvent();
        tbAstpWorkflowEvent.setId(id);
        tbAstpWorkflowEvent.setUserId(userId);
        tbAstpWorkflowEvent.setEventTS(new Date());
        tbAstpWorkflowEvent.setEventId(eventId);
        tbAstpWorkflowEvent.setStageId(tbAstpWorkflowTxnDetail.getStageId());
        LOG.debug("{} EventId : {} and  StageId : {}", LOGGER_PREFIX_WORKFLOW, eventId, tbAstpWorkflowTxnDetail.getStageId());
        if (eventId.equalsIgnoreCase(WORKFLOW_STATUS_STARTED)) {
            tbAstpWorkflowEvent.setwFlowEventTaskData("");
            tbAstpWorkflowEvent.setwFlowEventRemarks("");
        } else {
            setWFlowEvent(workflowReq, tbAstpWorkflowEvent);
        }

        tbAstpWorkflowEvent.setWorkflowId(tbAstpWorkflowTxnDetail.getWorkflowId());
        tbAstpWorkflowEventRepo.save(tbAstpWorkflowEvent);
    }

    private void setWFlowEvent(JSONObject workflowReq, TbAstpWorkflowEvent tbAstpWorkflowEvent) {
        if (workflowReq.has(WORKFLOW_REMARKS)) {
            tbAstpWorkflowEvent.setwFlowEventRemarks(workflowReq.getString(WORKFLOW_REMARKS));
        }
        if (workflowReq.has(WORKFLOW_TASK_DATA)) {
            tbAstpWorkflowEvent.setwFlowEventTaskData(workflowReq.getString(WORKFLOW_TASK_DATA));
        }

        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_1)) {
            tbAstpWorkflowEvent.setwFlowEventCustomField1(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_1));
        }
        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_2)) {
            tbAstpWorkflowEvent.setwFlowEventCustomField2(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_2));
        }
        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_3)) {
            tbAstpWorkflowEvent.setwFlowEventCustomField3(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_3));
        }
        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_4)) {
            tbAstpWorkflowEvent.setwFlowEventCustomField4(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_4));
        }
        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_5)) {
            tbAstpWorkflowEvent.setwFlowEventCustomField5(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_5));
        }
    }
}
