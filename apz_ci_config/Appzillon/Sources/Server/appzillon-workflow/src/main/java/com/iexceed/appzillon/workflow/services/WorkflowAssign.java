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
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Named("WorkflowAssign")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class WorkflowAssign {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getWorkflowLogger(LOGGER_WORKFLOW, WorkflowAssign.class.getName());

    @Inject
    private TbAstpWorkflowTxnDetailRepo astpWorkflowTxnDetailRepo;

    @Inject
    private TbAstpWorkflowTxnMasterRepo astpWorkflowTxnMasterRepo;

    @Inject
    private WorkflowEvents wFlowEvents;

    @Inject
    private WorkflowEntitlements wFlowEntitlements;

    @Inject
    private WorkflowMasterDetail wFlowMasterDetail;

    @Inject
    private WorkflowTxnDetails wFlowTxnDetails;

    public void assignStageDetailsToUser(Message pMessage) {
        LOG.info("{} inside the workflow assign service", LOGGER_PREFIX_WORKFLOW);
        JSONObject workflowAssignReq = null;
        TbAstpWorkflowTxnDetail pendingTbAstpWorkflowTxnDetail = null;
        workflowAssignReq = getJsonObjectFromMessage(pMessage, workflowAssignReq);

        TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster = null;
        if (workflowAssignReq != null) {
            tbAstpWorkflowTxnMaster = astpWorkflowTxnMasterRepo.findByAppIdWorkflowRefNoNotStatus(
                    workflowAssignReq.getString(MESSAGE_HEADER_APP_ID),
                    workflowAssignReq.getString(APPZILLON_WORKFLOW_REF_NO), WORKFLOW_STATUS_TERMINATED);
        }


        if (tbAstpWorkflowTxnMaster != null) {

            LOG.info("{} after checking the workflowref number", LOGGER_PREFIX_WORKFLOW);
            pendingTbAstpWorkflowTxnDetail = astpWorkflowTxnDetailRepo.findByAppIdWorkflowRefNoWorkflowSeqNoStatus(
                    workflowAssignReq.getString(MESSAGE_HEADER_APP_ID),
                    workflowAssignReq.getString(APPZILLON_WORKFLOW_REF_NO),
                    workflowAssignReq.getInt(APPZILLON_WORKFLOW_SEQ_NO), WORKFLOW_STATUS_PENDING);

            if (INTERFACE_ID_WORKFLOW_REASSIGN.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())
                    || INTERFACE_ID_WORKFLOW_UNASSIGN.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
                LOG.info("{} interfaceId is workflowReassign or workflowUnassign", LOGGER_PREFIX_WORKFLOW);
                pendingTbAstpWorkflowTxnDetail = astpWorkflowTxnDetailRepo
                        .findByAppIdWorkflowRefNoWorkflowSeqNoStatus(workflowAssignReq.getString(MESSAGE_HEADER_APP_ID),
                                workflowAssignReq.getString(APPZILLON_WORKFLOW_REF_NO),
                                workflowAssignReq.getInt(APPZILLON_WORKFLOW_SEQ_NO), WORKFLOW_STATUS_WIP);
            }
            if (pendingTbAstpWorkflowTxnDetail != null) {
                LOG.info("{} inside the workflow assign service", LOGGER_PREFIX_WORKFLOW);
                JSONObject response = new JSONObject();
                JSONObject assign = new JSONObject();
                assign.put(MESSAGE_HEADER_APP_ID, workflowAssignReq.getString(MESSAGE_HEADER_APP_ID));
                assign.put(WORKFLOW_ID, tbAstpWorkflowTxnMaster.getWorkflowId());
                assign.put(APPZILLON_WORKFLOW_REF_NO, workflowAssignReq.getString(APPZILLON_WORKFLOW_REF_NO));
                LOG.debug("{} json : {}", LOGGER_PREFIX_WORKFLOW, assign);
                JSONObject inputs = new JSONObject();
                inputs.put(STAGE_ID, pendingTbAstpWorkflowTxnDetail.getStageId());
                inputs.put(APPZILLON_WORKFLOW_SEQ_NO, pendingTbAstpWorkflowTxnDetail.getId().getWorkflowSeqNo());
                inputs.put("lastEventNo", pendingTbAstpWorkflowTxnDetail.getLastEventNo() + 1);
                inputs.put("updatedNextStages", "");
                inputs.put(STATUS, WORKFLOW_STATUS_WIP);
                inputs.put(USERID, workflowAssignReq.getString(MESSAGE_HEADER_USER_ID));

                if (INTERFACE_ID_WORKFLOW_ASSIGN.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
                    wFlowEntitlements.isUserEntitledForWorkflowStage(
                            workflowAssignReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowAssignReq.getString(MESSAGE_HEADER_USER_ID),
                            tbAstpWorkflowTxnMaster.getWorkflowId(), pendingTbAstpWorkflowTxnDetail.getStageId());
                    pendingTbAstpWorkflowTxnDetail = wFlowTxnDetails.saveTxnDetail(workflowAssignReq,
                            tbAstpWorkflowTxnMaster, pendingTbAstpWorkflowTxnDetail.getStartTS(), inputs);

                    wFlowEvents.saveEvent(workflowAssignReq, pendingTbAstpWorkflowTxnDetail,
                            pendingTbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_ASSIGNED,
                            workflowAssignReq.getString(MESSAGE_HEADER_USER_ID));
                    wFlowMasterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster, WORKFLOW_STATUS_WIP,
                            workflowAssignReq);
                    assign.put(MESSAGE_HEADER_USER_ID, workflowAssignReq.getString(MESSAGE_HEADER_USER_ID));
                    response.put(WORKFLOW_ASSIGN_RESPONSE, assign);
                    LOG.debug("{} json : {}", LOGGER_PREFIX_WORKFLOW, assign);
                } else if (INTERFACE_ID_WORKFLOW_REASSIGN.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
                    wFlowEntitlements.isUserEntitledForWorkflowStage(
                            workflowAssignReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowAssignReq.getString(MESSAGE_HEADER_USER_ID),
                            tbAstpWorkflowTxnMaster.getWorkflowId(), pendingTbAstpWorkflowTxnDetail.getStageId());
                    pendingTbAstpWorkflowTxnDetail = wFlowTxnDetails.saveTxnDetail(workflowAssignReq,
                            tbAstpWorkflowTxnMaster, pendingTbAstpWorkflowTxnDetail.getStartTS(),
                            inputs);

                    wFlowEvents.saveEvent(workflowAssignReq, pendingTbAstpWorkflowTxnDetail,
                            pendingTbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_REASSIGNED,
                            workflowAssignReq.getString(MESSAGE_HEADER_USER_ID));
                    wFlowMasterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster, WORKFLOW_STATUS_WIP,
                            workflowAssignReq);
                    assign.put(MESSAGE_HEADER_USER_ID, workflowAssignReq.getString(MESSAGE_HEADER_USER_ID));
                    LOG.debug("{} Reassign json : {}", LOGGER_PREFIX_WORKFLOW, assign);
                    response.put(WORKFLOW_RE_ASSIGN_RESPONSE, assign);
                } else if (INTERFACE_ID_WORKFLOW_ACQUIRE.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
                    wFlowEntitlements.isUserEntitledForWorkflowStage(pMessage.getHeader().getAppId(),
                            pMessage.getHeader().getUserId(), tbAstpWorkflowTxnMaster.getWorkflowId(),
                            pendingTbAstpWorkflowTxnDetail.getStageId());

                    inputs.put(USERID, pMessage.getHeader().getUserId());

                    pendingTbAstpWorkflowTxnDetail = wFlowTxnDetails.saveTxnDetail(workflowAssignReq,
                            tbAstpWorkflowTxnMaster,
                            pendingTbAstpWorkflowTxnDetail.getStartTS(),
                            inputs);
                    wFlowEvents.saveEvent(workflowAssignReq, pendingTbAstpWorkflowTxnDetail,
                            pendingTbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_ACQUIRED,
                            pMessage.getHeader().getUserId());
                    wFlowMasterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster, WORKFLOW_STATUS_WIP,
                            workflowAssignReq);
                    LOG.debug("{} Acquire json : {}", LOGGER_PREFIX_WORKFLOW, assign);
                    assign.put(MESSAGE_HEADER_USER_ID, pMessage.getHeader().getUserId());
                    response.put(WORKFLOW_ACQUIRE_RESPONSE, assign);
                } else if (INTERFACE_ID_WORKFLOW_UNASSIGN.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
                    wFlowEntitlements.isUserEntitledForWorkflowStage(
                            workflowAssignReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowAssignReq.getString(MESSAGE_HEADER_USER_ID),
                            tbAstpWorkflowTxnMaster.getWorkflowId(), pendingTbAstpWorkflowTxnDetail.getStageId());

                    inputs.put(USERID, "");
                    inputs.put(STATUS, WORKFLOW_STATUS_PENDING);

                    pendingTbAstpWorkflowTxnDetail = wFlowTxnDetails.saveTxnDetail(workflowAssignReq,
                            tbAstpWorkflowTxnMaster,
                            pendingTbAstpWorkflowTxnDetail.getStartTS(),
                            inputs);
                    wFlowEvents.saveEvent(workflowAssignReq, pendingTbAstpWorkflowTxnDetail,
                            pendingTbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_UNASSIGNED, pMessage.getHeader().getUserId());
                    wFlowMasterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster, WORKFLOW_STATUS_WIP,
                            workflowAssignReq);
                    LOG.debug("{} Unassign json : {}", LOGGER_PREFIX_WORKFLOW, assign);
                    response.put(WORKFLOW_UNASSIGN_RESPONSE, assign);
                }
                pMessage.getResponseObject().setResponseJson(response);

            } else {
                WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
                wfexp.setCode(WorkflowException.Code.APZ_WFL_006.toString());
                wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_006));
                wfexp.setPriority("1");
                LOG.error("{} Invalid stageId /invalid workflowRefNo/ stageId (or) Workflow already completed {}",
                        LOGGER_PREFIX_WORKFLOW, wfexp);
                throw wfexp;
            }
        } else {
            WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
            wfexp.setCode(WorkflowException.Code.APZ_WFL_004.toString());
            wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_004));
            wfexp.setPriority("1");
            LOG.error("{} Invalid workflow reference number {}", LOGGER_PREFIX_WORKFLOW, wfexp);
            throw wfexp;
        }

    }

    private JSONObject getJsonObjectFromMessage(Message pMessage, JSONObject workflowAssignReq) {
        if (INTERFACE_ID_WORKFLOW_ASSIGN.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
            workflowAssignReq = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_ASSIGN);
        } else if (INTERFACE_ID_WORKFLOW_REASSIGN
                .equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
            workflowAssignReq = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_REASSIGN);
        } else if (INTERFACE_ID_WORKFLOW_ACQUIRE
                .equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
            workflowAssignReq = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_ACQUIRE);
        } else if ((INTERFACE_ID_WORKFLOW_UNASSIGN
                .equalsIgnoreCase(pMessage.getHeader().getInterfaceId()))) {
            workflowAssignReq = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_UNASSIGN);
        }
        return workflowAssignReq;
    }

}
