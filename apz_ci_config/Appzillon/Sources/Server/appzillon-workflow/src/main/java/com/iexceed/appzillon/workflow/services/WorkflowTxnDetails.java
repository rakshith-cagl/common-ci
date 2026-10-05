package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnDetail;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnDetailPK;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnMaster;
import com.iexceed.appzillon.workflow.exception.WorkflowParallelException;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnDetailRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Service
@Transactional(value = TRANSACTION_APPZILLON_ADMIN, noRollbackFor = {WorkflowParallelException.class})
public class WorkflowTxnDetails {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_WORKFLOW,
            WorkflowTxnDetails.class.getName());

    @Inject
    private TbAstpWorkflowTxnDetailRepo tbAstpWorkflowTxnDetailRepo;

    public TbAstpWorkflowTxnDetail saveTxnDetail(JSONObject workflowReq,
                                                 TbAstpWorkflowTxnMaster txnMaster, Date date, JSONObject inputs) {
        TbAstpWorkflowTxnDetailPK tbAstpWorkflowTxnDetailPK = new TbAstpWorkflowTxnDetailPK();
        tbAstpWorkflowTxnDetailPK.setAppId(workflowReq.getString(MESSAGE_HEADER_APP_ID));
        tbAstpWorkflowTxnDetailPK.setWorkflowRefNo(workflowReq.getString(APPZILLON_WORKFLOW_REF_NO));
        tbAstpWorkflowTxnDetailPK.setWorkflowSeqNo(inputs.getInt(APPZILLON_WORKFLOW_SEQ_NO));
        TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = new TbAstpWorkflowTxnDetail();
        tbAstpWorkflowTxnDetail.setStageId(inputs.getString(STAGE_ID));
        tbAstpWorkflowTxnDetail.setId(tbAstpWorkflowTxnDetailPK);
        tbAstpWorkflowTxnDetail.setStartTS(date);
        tbAstpWorkflowTxnDetail.setStatus(inputs.getString(STATUS));
        tbAstpWorkflowTxnDetail.setNextStages(inputs.getString(WF_UPDATED_NEXT_STAGE));
        tbAstpWorkflowTxnDetail.setWorkflowId(txnMaster.getWorkflowId());
        tbAstpWorkflowTxnDetail.setLastEventNo(inputs.getInt(WF_LAST_EVENT_NO));
        tbAstpWorkflowTxnDetail.setUserId(inputs.getString(USERID));

        if (inputs.getString(STATUS).equalsIgnoreCase(WORKFLOW_STATUS_PENDING) || inputs.getString(STATUS).equalsIgnoreCase(WORKFLOW_STATUS_SKIPPED)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailRemarks("");
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailTaskData("");
        } else {
            setAstpWflowTxnDetail(workflowReq, tbAstpWorkflowTxnDetail);
        }

        if (inputs.getString(STATUS).equalsIgnoreCase(WORKFLOW_STATUS_COMPLETED)) {
            tbAstpWorkflowTxnDetail.setEndTS(new Date());
        }

        LOG.debug("{} Saving transaction details", LOGGER_PREFIX_WORKFLOW);
        tbAstpWorkflowTxnDetailRepo.save(tbAstpWorkflowTxnDetail);
        return tbAstpWorkflowTxnDetail;
    }

    private void setAstpWflowTxnDetail(JSONObject workflowReq, TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail) {
        if (workflowReq.has(WORKFLOW_REMARKS)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailRemarks(workflowReq.getString(WORKFLOW_REMARKS));
        }
        if (workflowReq.has(WORKFLOW_TASK_DATA)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailTaskData(workflowReq.getString(WORKFLOW_TASK_DATA));
        }

        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_1)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField1(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_1));
        }
        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_2)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField2(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_2));
        }
        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_3)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField3(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_3));
        }
        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_4)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField4(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_4));
        }
        if (workflowReq.has(WORKFLOW_CUSTOM_FIELD_5)) {
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField5(workflowReq.getString(WORKFLOW_CUSTOM_FIELD_5));
        }
    }


}
