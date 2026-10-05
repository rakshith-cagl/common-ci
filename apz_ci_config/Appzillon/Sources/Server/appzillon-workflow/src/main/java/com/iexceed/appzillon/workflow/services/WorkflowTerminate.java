package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnDetail;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnMaster;
import com.iexceed.appzillon.workflow.exception.WorkflowException;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnDetailRepo;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnMasterRepo;
import com.iexceed.appzillon.workflow.util.WorkFlowUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Named("WorkflowTerminate")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class WorkflowTerminate {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getWorkflowLogger(LOGGER_WORKFLOW, WorkflowTerminate.class.getName());
    @Inject
    private TbAstpWorkflowTxnDetailRepo astpWflowTxnDetailRepo;

    @Inject
    private TbAstpWorkflowTxnMasterRepo astpWflowTxnMasterRepo;

    @Inject
    private WorkflowEvents wrkFlowEvents;

    @Inject
    private WorkflowEntitlements wrkFlowEntitlements;

    @Inject
    private WorkflowMasterDetail wrkFlowMasterDetail;

    @Inject
    private WorkflowTxnDetails wrkFlowTxnDetails;

    public void terminateWorkflow(Message pMessage) {

        JSONObject workflowTerminateReq = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(INTERFACE_ID_WORKFLOW_TERMINATE);
        String userId = null;
        if (workflowTerminateReq.has(MESSAGE_HEADER_USER_ID)) {
            userId = workflowTerminateReq.getString(MESSAGE_HEADER_USER_ID);
        }

        JSONObject response = null;
        JSONObject workflowStopResponse = null;
        TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster = astpWflowTxnMasterRepo
                .findByAppIdWorkflowRefNoNotStatus(workflowTerminateReq.getString(MESSAGE_HEADER_APP_ID), workflowTerminateReq.getString(APPZILLON_WORKFLOW_REF_NO), WORKFLOW_STATUS_TERMINATED);

        if (tbAstpWorkflowTxnMaster != null) {

            TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = astpWflowTxnDetailRepo
                    .findByAppIdWorkflowRefNoSeqNoStatus(workflowTerminateReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowTerminateReq.getString(APPZILLON_WORKFLOW_REF_NO),
                            workflowTerminateReq.getInt(APPZILLON_WORKFLOW_SEQ_NO), WORKFLOW_STATUS_WIP);

            if (tbAstpWorkflowTxnDetail != null) {
                wrkFlowEntitlements.isUserEntitledForWorkflowStage(pMessage.getHeader().getAppId(),
                        pMessage.getHeader().getUserId(), tbAstpWorkflowTxnMaster.getWorkflowId(),
                        tbAstpWorkflowTxnDetail.getStageId());
                response = new JSONObject();
                workflowStopResponse = new JSONObject();
                JSONObject inputs = new JSONObject();
                inputs.put(STAGE_ID, tbAstpWorkflowTxnDetail.getStageId());
                inputs.put(APPZILLON_WORKFLOW_SEQ_NO, tbAstpWorkflowTxnDetail.getId().getWorkflowSeqNo());
                inputs.put(WF_LAST_EVENT_NO, tbAstpWorkflowTxnDetail.getLastEventNo() + 1);
                inputs.put(WF_UPDATED_NEXT_STAGE, "");
                inputs.put(STATUS, WORKFLOW_STATUS_TERMINATED);
                inputs.put(USERID, tbAstpWorkflowTxnDetail.getUserId());

                tbAstpWorkflowTxnDetail = wrkFlowTxnDetails.saveTxnDetail(workflowTerminateReq,
                        tbAstpWorkflowTxnMaster,
                        tbAstpWorkflowTxnDetail.getStartTS(), inputs);
                wrkFlowEvents.saveEvent(workflowTerminateReq, tbAstpWorkflowTxnDetail,
                        tbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_TERMINATED,
                        tbAstpWorkflowTxnDetail.getUserId());
                // Updating master details
                wrkFlowMasterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster, WORKFLOW_STATUS_TERMINATED,
                        workflowTerminateReq);

                workflowStopResponse.put(MESSAGE_HEADER_APP_ID, workflowTerminateReq.getString(MESSAGE_HEADER_APP_ID));
                workflowStopResponse.put(WORKFLOW_ID, tbAstpWorkflowTxnMaster.getWorkflowId());
                workflowStopResponse.put(WORKFLOW_REF_NO, workflowTerminateReq.getString(APPZILLON_WORKFLOW_REF_NO));
                workflowStopResponse.put(WORKFLOW_SEQ_NO, workflowTerminateReq.getInt(APPZILLON_WORKFLOW_SEQ_NO));
                workflowStopResponse.put(MESSAGE_HEADER_USER_ID, userId);
                workflowStopResponse.put(STATUS, WORKFLOW_STATUS_TERMINATED);
                response.put("workflowTerminateResponse", workflowStopResponse);
                pMessage.getResponseObject().setResponseJson(response);

            } else {
                WorkflowException wfexp = WorkFlowUtil.getWorkFlowExceptionObj(WorkflowException.Code.APZ_WFL_006);
                LOG.error("{} invalid stageId {}", LOGGER_PREFIX_WORKFLOW, wfexp);
                throw wfexp;

            }
        } else {
            WorkflowException wfexp = WorkFlowUtil.getWorkFlowExceptionObj(WorkflowException.Code.APZ_WFL_004);
            LOG.error("{} Invalid workflow reference number {}", LOGGER_PREFIX_WORKFLOW, wfexp);
            throw wfexp;
        }
    }


}
