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

@Named("WorkflowSave")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class WorkflowSave {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getWorkflowLogger(LOGGER_WORKFLOW, WorkflowSave.class.getName());

    @Inject
    private TbAstpWorkflowTxnDetailRepo workflowTxnDetailRepo;

    @Inject
    private TbAstpWorkflowTxnMasterRepo workflowTxnMasterRepo;

    @Inject
    private WorkflowEvents events;

    @Inject
    private WorkflowEntitlements entitlements;

    @Inject
    private WorkflowMasterDetail masterDetail;

    public void saveWorkflowDetails(Message pMessage) {
        JSONObject response = null;
        JSONObject workflowSaveResponse = null;
        TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = null;
        JSONObject workflowStagesReq = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(INTERFACE_ID_WORKFLOW_SAVE);
        TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster = workflowTxnMasterRepo
                .findByAppIdWorkflowRefNoNotStatus(workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                        workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO), WORKFLOW_STATUS_TERMINATED);
        if (tbAstpWorkflowTxnMaster != null) {

            tbAstpWorkflowTxnDetail = workflowTxnDetailRepo.findByAppIdWorkflowRefNoWorkflowSeqNoStatus(
                    workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                    workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO),
                    workflowStagesReq.getInt(APPZILLON_WORKFLOW_SEQ_NO), WORKFLOW_STATUS_WIP);
            if (tbAstpWorkflowTxnDetail != null) {


                entitlements.isUserEntitledForWorkflowStage(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(), tbAstpWorkflowTxnMaster.getWorkflowId(), tbAstpWorkflowTxnDetail.getStageId());

                response = new JSONObject();
                workflowSaveResponse = new JSONObject();

                saveTxnDetails(
                        workflowStagesReq, tbAstpWorkflowTxnDetail);

                events.saveEvent(workflowStagesReq, tbAstpWorkflowTxnDetail,
                        tbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_SAVED, tbAstpWorkflowTxnDetail.getUserId());
                masterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster, WORKFLOW_STATUS_WIP, workflowStagesReq);

                workflowSaveResponse.put(MESSAGE_HEADER_APP_ID, workflowStagesReq.getString(MESSAGE_HEADER_APP_ID));
                workflowSaveResponse.put(WORKFLOW_ID, tbAstpWorkflowTxnMaster.getWorkflowId());
                workflowSaveResponse.put(APPZILLON_WORKFLOW_REF_NO,
                        workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO));
                workflowSaveResponse.put(WORKFLOW_SEQ_NO, workflowStagesReq.getInt(WORKFLOW_SEQ_NO));
                workflowSaveResponse.put(MESSAGE_HEADER_USER_ID, workflowStagesReq.getString(MESSAGE_HEADER_USER_ID));
                workflowSaveResponse.put(WORKFLOW_TASK_DATA, workflowStagesReq.getString(WORKFLOW_TASK_DATA));
                workflowSaveResponse.put(MESSAGE_HEADER_STATUS, WORKFLOW_STATUS_SAVED);
                response.put("workflowSaveResponse", workflowSaveResponse);
                pMessage.getResponseObject().setResponseJson(response);
            } else {
                WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
                wfexp.setCode(WorkflowException.Code.APZ_WFL_006.toString());
                wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_006));
                wfexp.setPriority("1");
                LOG.error(LOGGER_PREFIX_WORKFLOW, "Invalid stageId {}", wfexp);
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


    private void saveTxnDetails(JSONObject workflowStagesReq, TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail) {
        tbAstpWorkflowTxnDetail.setLastEventNo(tbAstpWorkflowTxnDetail.getLastEventNo() + 1);
        if (workflowStagesReq.has(WORKFLOW_TASK_DATA)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailTaskData(workflowStagesReq.getString(WORKFLOW_TASK_DATA));
        }
        workflowTxnDetailRepo.save(tbAstpWorkflowTxnDetail);
    }
}
