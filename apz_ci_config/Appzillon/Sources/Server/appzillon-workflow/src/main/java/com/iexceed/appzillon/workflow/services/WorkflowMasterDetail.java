package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnMaster;
import com.iexceed.appzillon.workflow.exception.WorkflowParallelException;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnMasterRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Service
@Transactional(value = TRANSACTION_APPZILLON_ADMIN, noRollbackFor = {WorkflowParallelException.class})
public class WorkflowMasterDetail {


    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_WORKFLOW,
            WorkflowMasterDetail.class.getName());

    @Inject
    private TbAstpWorkflowTxnMasterRepo txnMasterRepo;

    /**
     * @param txnMaster
     * @param status
     * @param reqJSONObject
     */
    public void updateTxnMasterDetails(TbAstpWorkflowTxnMaster txnMaster, String status, JSONObject reqJSONObject) {

        LOG.debug("{} saving master details", LOGGER_PREFIX_WORKFLOW);
        if (status.equalsIgnoreCase(WORKFLOW_STATUS_COMPLETED)) {
            txnMaster.setModifiedTS(new Date());
            txnMaster.setEndTS(new Date());
            txnMaster.setStatus(WORKFLOW_STATUS_COMPLETED);
        } else if (status.equalsIgnoreCase(WORKFLOW_STATUS_TERMINATED)) {
            txnMaster.setModifiedTS(new Date());
            txnMaster.setEndTS(new Date());
            txnMaster.setStatus(WORKFLOW_STATUS_TERMINATED);
        } else {
            txnMaster.setModifiedTS(new Date());
        }
        if (reqJSONObject.has(WORKFLOW_CUSTOM_FIELD_1)) {
            txnMaster.setCustomField1(reqJSONObject.getString(WORKFLOW_CUSTOM_FIELD_1));
        }
        if (reqJSONObject.has(WORKFLOW_CUSTOM_FIELD_2)) {
            txnMaster.setCustomField2(reqJSONObject.getString(WORKFLOW_CUSTOM_FIELD_2));
        }
        if (reqJSONObject.has(WORKFLOW_CUSTOM_FIELD_3)) {
            txnMaster.setCustomField3(reqJSONObject.getString(WORKFLOW_CUSTOM_FIELD_3));
        }
        if (reqJSONObject.has(WORKFLOW_CUSTOM_FIELD_4)) {
            txnMaster.setCustomField4(reqJSONObject.getString(WORKFLOW_CUSTOM_FIELD_4));
        }
        if (reqJSONObject.has(WORKFLOW_CUSTOM_FIELD_5)) {
            txnMaster.setCustomField5(reqJSONObject.getString(WORKFLOW_CUSTOM_FIELD_5));
        }

        txnMasterRepo.save(txnMaster);
    }
}
