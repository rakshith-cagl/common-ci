package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.entity.*;
import com.iexceed.appzillon.workflow.exception.WorkflowException;
import com.iexceed.appzillon.workflow.repo.TbAsmiWorkflowStagesRepo;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnDetailRepo;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnMasterRepo;
import com.iexceed.appzillon.workflow.util.WorkFlowUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Named("WorkflowQuery")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class WorkflowQuery {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_WORKFLOW,
            WorkflowQuery.class.getName());

    @Inject
    private TbAsmiWorkflowStagesRepo tbAsmiWorkflowStagesRepo;

    @Inject
    private TbAstpWorkflowTxnDetailRepo tbAstpWorkflowTxnDetailRepo;

    @Inject
    private TbAstpWorkflowTxnMasterRepo tbAstpWorkflowTxnMasterRepo;

    @Inject
    private WorkflowEntitlements workflowEntitlements;

    public void fetchStageDetails(Message pMessage) {
        JSONObject response = null;
        JSONObject workflowQueryResponse = null;
        TbAstpWorkflowTxnMasterPK tbAstpWorkflowTxnMasterPK = new TbAstpWorkflowTxnMasterPK(
                pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_QUERY)
                        .getString(MESSAGE_HEADER_APP_ID),
                pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_QUERY)
                        .getString(APPZILLON_WORKFLOW_REF_NO));

        Optional<TbAstpWorkflowTxnMaster> tbAstpWorkflowTxnMaster = tbAstpWorkflowTxnMasterRepo
                .findById(tbAstpWorkflowTxnMasterPK);
        if (tbAstpWorkflowTxnMaster.isPresent()) {
            TbAstpWorkflowTxnDetailPK tbAstpWorkflowTxnDetailPK = new TbAstpWorkflowTxnDetailPK(
                    pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_QUERY)
                            .getString(MESSAGE_HEADER_APP_ID),
                    pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_QUERY)
                            .getString(APPZILLON_WORKFLOW_REF_NO),
                    pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_QUERY)
                            .getInt(APPZILLON_WORKFLOW_SEQ_NO));
            Optional<TbAstpWorkflowTxnDetail> tbAstpWorkflowTxnDetail = tbAstpWorkflowTxnDetailRepo
                    .findById(tbAstpWorkflowTxnDetailPK);
            if (tbAstpWorkflowTxnDetail.isPresent()) {
                TbAsmiWorkflowStagesPK tbAsmiWorkflowStagesPK = new TbAsmiWorkflowStagesPK(
                        pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_QUERY)
                                .getString(MESSAGE_HEADER_APP_ID),
                        tbAstpWorkflowTxnMaster.get().getWorkflowId(), tbAstpWorkflowTxnDetail.get().getStageId());
                response = new JSONObject();
                workflowQueryResponse = new JSONObject();
                Optional<TbAsmiWorkflowStages> tbAsmiWorkflowStages = tbAsmiWorkflowStagesRepo
                        .findById(tbAsmiWorkflowStagesPK);
                workflowQueryResponse.put(MESSAGE_HEADER_APP_ID, pMessage.getRequestObject().getRequestJson()
                        .getJSONObject(INTERFACE_ID_WORKFLOW_QUERY).getString(MESSAGE_HEADER_APP_ID));
                workflowQueryResponse.put(WORKFLOW_ID, tbAstpWorkflowTxnMaster.get().getWorkflowId());
                workflowQueryResponse.put(WORKFLOW_REF_NO, pMessage.getRequestObject().getRequestJson()
                        .getJSONObject(INTERFACE_ID_WORKFLOW_QUERY).getString(APPZILLON_WORKFLOW_REF_NO));
                workflowQueryResponse.put(MESSAGE_HEADER_USER_ID, tbAstpWorkflowTxnDetail.get().getUserId());
                workflowQueryResponse.put(TASK_DATA, tbAstpWorkflowTxnDetail.get().getAstpWflowTxnDetailTaskData());
                workflowQueryResponse.put("screenId", tbAsmiWorkflowStages.get().getScreenId());
                workflowQueryResponse.put(WORKFLOW_STAGE_ID, tbAstpWorkflowTxnDetail.get().getStageId());
                response.put("workflowQueryResponse", workflowQueryResponse);
                workflowEntitlements.isUserEntitledForWorkflowStage(pMessage.getHeader().getAppId(),
                        pMessage.getHeader().getUserId(), tbAstpWorkflowTxnMaster.get().getWorkflowId(),
                        tbAstpWorkflowTxnDetail.get().getStageId());
                pMessage.getResponseObject().setResponseJson(response);
            } else {
                WorkflowException wfexp = WorkFlowUtil.getWorkFlowExceptionObj(WorkflowException.Code.APZ_WFL_006);
                LOG.error("{} Invalid stageId /Invalid workflowRefNo/ Workflow already completed {}",
                        LOGGER_PREFIX_WORKFLOW, wfexp);
                throw wfexp;
            }
        } else {
            WorkflowException wfexp = WorkFlowUtil.getWorkFlowExceptionObj(WorkflowException.Code.APZ_WFL_004);
            LOG.error("{} Invalid workflow reference number {}", LOGGER_PREFIX_WORKFLOW, wfexp);
            throw wfexp;
        }
    }

}
