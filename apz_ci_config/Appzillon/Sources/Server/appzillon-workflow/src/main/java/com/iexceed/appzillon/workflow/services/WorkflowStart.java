package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.entity.*;
import com.iexceed.appzillon.workflow.exception.WorkflowException;
import com.iexceed.appzillon.workflow.repo.TbAsmiWorkflowDefnRepo;
import com.iexceed.appzillon.workflow.repo.TbAsmiWorkflowStagesRepo;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnDetailRepo;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnMasterRepo;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Named(SERVICE_WORKFLOW_START)
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class WorkflowStart {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_WORKFLOW,
            WorkflowStart.class.getName());

    @Inject
    private TbAsmiWorkflowDefnRepo defnRepo;

    @Inject
    private TbAsmiWorkflowStagesRepo stagesRepo;

    @Inject
    private TbAstpWorkflowTxnDetailRepo txnDetailRepo;

    @Inject
    private TbAstpWorkflowTxnMasterRepo txnMasterRepo;

    @Inject
    private WorkflowEvents events;

    @Inject
    private WorkflowEntitlements workflowEntitlements;

    public void startWorkflow(Message pMessage) {

        TbAsmiWorkflowDefn tbAsmiWorkflowDefn = defnRepo.findByAppIdWorkflowIdActive(
                pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                        .getString(MESSAGE_HEADER_APP_ID),
                pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                        .getString(WORKFLOW_ID),
                YES);
        if (tbAsmiWorkflowDefn != null) {
            String workflowRefNo = System.currentTimeMillis() + Utils.generateRandomofLength(16 - Long.toString(System.currentTimeMillis()).length());
            LOG.debug("{} created workflow ref no : {}", LOGGER_PREFIX_WORKFLOW, workflowRefNo);
            this.fetchFirstStageAndScreen(pMessage, workflowRefNo);
        } else {
            WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
            wfexp.setCode(WorkflowException.Code.APZ_WFL_009.toString());

            wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_009));
            wfexp.setPriority("1");
            LOG.error("{} Workflow is not active {}", LOGGER_PREFIX_WORKFLOW, wfexp);
            throw wfexp;
        }
    }

    private void fetchFirstStageAndScreen(Message pMessage, String workflowRefNo) {

        TbAsmiWorkflowStages tbAsmiWorkflowStage = stagesRepo.findByAppIdWorkflowIdOrderBySeqNo(
                pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                        .getString(MESSAGE_HEADER_APP_ID),
                pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                        .getString(WORKFLOW_ID));
        if (tbAsmiWorkflowStage != null) {
            // Check if user is entitled for workflow stage.
            if (workflowEntitlements.isUserEntitledForWorkflowStage(pMessage.getHeader().getAppId(),
                    pMessage.getHeader().getUserId(), pMessage.getRequestObject().getRequestJson()
                            .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_ID),
                    tbAsmiWorkflowStage.getId().getStageId())) {

                TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetails = saveTxnDetails(pMessage, workflowRefNo,
                        tbAsmiWorkflowStage);
                events.saveEvent(
                        pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START),
                        tbAstpWorkflowTxnDetails, 1, WORKFLOW_STATUS_STARTED, "");

                // Creating workflow master transaction
                saveTxnMaster(pMessage, workflowRefNo);

                // Creating workflow response
                getWorkFlowStageResponse(pMessage, workflowRefNo, tbAsmiWorkflowStage);
            }
        } else {
            WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
            wfexp.setCode(WorkflowException.Code.APZ_WFL_012.toString());
            wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_012));
            wfexp.setPriority("1");
            LOG.error("{} Invalid Workflow request, check userId/AppId/WorkflowId {}", LOGGER_PREFIX_WORKFLOW, wfexp);
            throw wfexp;
        }

    }

    private void getWorkFlowStageResponse(Message pMessage, String workflowRefNo,
                                          TbAsmiWorkflowStages tbAsmiWorkflowStages) {
        LOG.debug("{} inside getWorkFlowStageResponse", LOGGER_PREFIX_WORKFLOW);
        JSONObject stageResp = new JSONObject();
        stageResp.put(MESSAGE_HEADER_APP_ID, pMessage.getRequestObject().getRequestJson()
                .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(MESSAGE_HEADER_APP_ID));
        stageResp.put(WORKFLOW_ID, pMessage.getRequestObject().getRequestJson()
                .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_ID));
        stageResp.put(WORKFLOW_REF_NO, workflowRefNo);
        stageResp.put(WORKFLOW_SEQ_NO, "1");
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START).has(TASK_DATA)) {
            stageResp.put(TASK_DATA, pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(TASK_DATA));
        }

        stageResp.put(MESSAGE_HEADER_SCREEN_ID, tbAsmiWorkflowStages.getScreenId());
        stageResp.put(WORKFLOW_STAGE_ID, tbAsmiWorkflowStages.getId().getStageId());
        pMessage.getResponseObject().setResponseJson(new JSONObject().put("workflowStartResponse", stageResp));
    }

    private void saveTxnMaster(Message pMessage, String workflowRefNo) {
        TbAstpWorkflowTxnMasterPK tbAstpWorkflowTxnMasterPK = new TbAstpWorkflowTxnMasterPK(pMessage.getRequestObject()
                .getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(MESSAGE_HEADER_APP_ID),
                workflowRefNo);
        TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster = new TbAstpWorkflowTxnMaster();
        tbAstpWorkflowTxnMaster.setTbAstpWorkflowTxnMasterPK(tbAstpWorkflowTxnMasterPK);
        tbAstpWorkflowTxnMaster.setStartTS(new Date());
        tbAstpWorkflowTxnMaster.setWorkflowId(pMessage.getRequestObject().getRequestJson()
                .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_ID));
        tbAstpWorkflowTxnMaster.setStatus(WORKFLOW_STATUS_WIP);
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_1)) {
            tbAstpWorkflowTxnMaster.setCustomField1(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_1));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_2)) {
            tbAstpWorkflowTxnMaster.setCustomField2(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_2));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_3)) {
            tbAstpWorkflowTxnMaster.setCustomField3(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_3));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_4)) {
            tbAstpWorkflowTxnMaster.setCustomField4(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_4));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_5)) {
            tbAstpWorkflowTxnMaster.setCustomField5(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_5));
        }

        txnMasterRepo.save(tbAstpWorkflowTxnMaster);
    }

    private TbAstpWorkflowTxnDetail saveTxnDetails(Message pMessage, String workflowRefNo,
                                                   TbAsmiWorkflowStages tbAsmiWorkflowStages) {
        TbAstpWorkflowTxnDetailPK tbAstpWorkflowTxnDetailPK = new TbAstpWorkflowTxnDetailPK(pMessage.getRequestObject()
                .getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(MESSAGE_HEADER_APP_ID),
                workflowRefNo, 1);
        TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = new TbAstpWorkflowTxnDetail();
        tbAstpWorkflowTxnDetail.setId(tbAstpWorkflowTxnDetailPK);
        tbAstpWorkflowTxnDetail.setStartTS(new Date());
        tbAstpWorkflowTxnDetail.setLastEventNo(1);
        tbAstpWorkflowTxnDetail.setStageId(tbAsmiWorkflowStages.getId().getStageId());
        tbAstpWorkflowTxnDetail.setStatus(WORKFLOW_STATUS_PENDING);
        tbAstpWorkflowTxnDetail.setWorkflowId(pMessage.getRequestObject().getRequestJson()
                .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_ID));

        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START).has(TASK_DATA)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailTaskData(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(TASK_DATA));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_1)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField1(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_1));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_2)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField2(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_2));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_3)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField3(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_3));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_4)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField4(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_4));
        }
        if (pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_START)
                .has(WORKFLOW_CUSTOM_FIELD_5)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField5(pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(INTERFACE_ID_WORKFLOW_START).getString(WORKFLOW_CUSTOM_FIELD_5));
        }
        txnDetailRepo.save(tbAstpWorkflowTxnDetail);
        return tbAstpWorkflowTxnDetail;
    }
}
