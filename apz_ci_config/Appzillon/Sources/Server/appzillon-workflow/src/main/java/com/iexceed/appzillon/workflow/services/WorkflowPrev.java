package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.entity.*;
import com.iexceed.appzillon.workflow.exception.WorkflowException;
import com.iexceed.appzillon.workflow.repo.TbAsmiWorkflowLinkedStagesRepo;
import com.iexceed.appzillon.workflow.repo.TbAsmiWorkflowStagesRepo;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnDetailRepo;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnMasterRepo;
import com.iexceed.appzillon.workflow.util.WorkFlowUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Named("WorkflowPrev")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class WorkflowPrev {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_DOMAIN,
            WorkflowPrev.class.getName());

    @Inject
    private TbAsmiWorkflowLinkedStagesRepo tbAsmiWorkflowLinkedStagesRepo;

    @Inject
    private TbAsmiWorkflowStagesRepo tbAsmiWorkflowStagesRepo;

    @Inject
    private TbAstpWorkflowTxnDetailRepo wfTxnDetailRepo;

    @Inject
    private TbAstpWorkflowTxnMasterRepo wfTxnMasterRepo;

    @Inject
    private WorkflowEvents wfEvents;

    @Inject
    private WorkflowEntitlements wfEntitlements;

    @Inject
    private WorkflowMasterDetail wfMasterDetail;

    private List<TbAstpWorkflowTxnDetail> tbAstpWorkflowTxnDetailRejectList = null;

    public void fetchPrevWorkflowDetail(Message pMessage) {

        LOG.debug("{} inside workflowPrev", LOGGER_PREFIX_WORKFLOW);

        JSONObject workflowPrevReq = null;

        workflowPrevReq = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(INTERFACE_ID_WORKFLOW_PREV);

        TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster = wfTxnMasterRepo.findByAppIdWorkflowRefNoStatus(
                workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                workflowPrevReq.getString(APPZILLON_WORKFLOW_REF_NO),
                WORKFLOW_STATUS_WIP);

        if (tbAstpWorkflowTxnMaster != null) {
            wfEntitlements.isUserEntitledForWorkflowStage(pMessage.getHeader().getAppId(),
                    pMessage.getHeader().getUserId(), tbAstpWorkflowTxnMaster.getWorkflowId(),
                    workflowPrevReq.getString(WORKFLOW_STAGE_ID));
            JSONObject response = null;
            TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = wfTxnDetailRepo.findByAppIdWorkflowRefNoSeqNoStageIdStatus(
                    workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                    workflowPrevReq.getString(APPZILLON_WORKFLOW_REF_NO),
                    workflowPrevReq.getInt(WORKFLOW_SEQ_NO),
                    workflowPrevReq.getString(WORKFLOW_STAGE_ID), WORKFLOW_STATUS_WIP);

            if (tbAstpWorkflowTxnDetail != null) {
                List<TbAsmiWorkflowLinkedStages> linkedStagesList = tbAsmiWorkflowLinkedStagesRepo
                        .findByAppIdWorkflowIdStageId(workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                                tbAstpWorkflowTxnMaster.getWorkflowId(), workflowPrevReq.getString(WORKFLOW_STAGE_ID),
                                WORKFLOW_LINKED_STAGE_TYPE_PREV);

                if (!linkedStagesList.isEmpty()) {
                    List<TbAsmiWorkflowLinkedStages> pLinkedStagesList = new ArrayList<>();
                    // remove skipped stages from the list
                    linkedStagesList = ((WorkflowUtility) WorkflowStartup.getInstance().getSpringContext()
                            .getAutowireCapableBeanFactory().getBean("WorkflowUtility")).ignorePreviouslySkippedStages(
                            workflowPrevReq, tbAstpWorkflowTxnMaster, linkedStagesList, pLinkedStagesList);
                    workflowPrevReq = pMessage.getRequestObject().getRequestJson()
                            .getJSONObject(INTERFACE_ID_WORKFLOW_PREV);

                    response = new JSONObject();

                    JSONObject workflowPrevResponse = new JSONObject();

                    List<JSONObject> linkedStageResponseList = new ArrayList<>();

                    rejectAllParallelStages(pMessage, linkedStagesList, workflowPrevReq, tbAstpWorkflowTxnMaster);

                    int workflowSeqInit = wfTxnDetailRepo.findByAppIdAndWorkflowIdAndOrderByWorkflowSeqNo(
                            workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowPrevReq.getString(WORKFLOW_REF_NO));
                    LOG.debug("{} WorkflowSeqMax : {}", LOGGER_PREFIX_WORKFLOW, workflowSeqInit);
                    // change status of all parallel stages to rejected

                    List<String> responseStageId = new ArrayList<>();
                    linkStageResponse(workflowPrevReq, tbAstpWorkflowTxnMaster, linkedStagesList,
                            linkedStageResponseList, workflowSeqInit, responseStageId);

                    wfMasterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster, WORKFLOW_STATUS_WIP,
                            workflowPrevReq);

                    workflowPrevResponse.put(MESSAGE_HEADER_APP_ID, workflowPrevReq.getString(MESSAGE_HEADER_APP_ID));
                    workflowPrevResponse.put(WORKFLOW_ID, tbAstpWorkflowTxnMaster.getWorkflowId());
                    workflowPrevResponse.put(WORKFLOW_REF_NO, workflowPrevReq.getString(WORKFLOW_REF_NO));
                    workflowPrevResponse.put(WORKFLOW_TASK_DATA, workflowPrevReq.getString(WORKFLOW_TASK_DATA));
                    workflowPrevResponse.put("linkedStageList", linkedStageResponseList);

                    response.put("workflowPrevResponse", workflowPrevResponse);
                    pMessage.getResponseObject().setResponseJson(response);

                } else {
                    WorkflowException wfexp = WorkFlowUtil.getWorkFlowExceptionObj(WorkflowException.Code.APZ_WFL_008);
                    LOG.error("{} no previous stage found for this stageId {}", LOGGER_PREFIX_WORKFLOW, wfexp);
                    throw wfexp;
                }
            } else {
                WorkflowException wfexp = WorkFlowUtil.getWorkFlowExceptionObj(WorkflowException.Code.APZ_WFL_006);
                LOG.error("{} invalid stageId {}", LOGGER_PREFIX_WORKFLOW, wfexp);
                throw wfexp;
            }

        } else {
            WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
            wfexp.setCode(WorkflowException.Code.APZ_WFL_004.toString());
            wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_004));
            wfexp.setPriority("1");
            LOG.error("{} invalid workflowrefno {}", LOGGER_PREFIX_WORKFLOW, wfexp);
            throw wfexp;
        }
    }

    private void linkStageResponse(JSONObject workflowPrevReq, TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster, List<TbAsmiWorkflowLinkedStages> linkedStagesList, List<JSONObject> linkedStageResponseList, int workflowSeqInit, List<String> responseStageId) {
        Optional<TbAsmiWorkflowStages> tbAsmiWorkflowStages;
        for (TbAsmiWorkflowLinkedStages tbAsmiWorkflowLinkedStages : linkedStagesList) {
            int workflowSeqNo = workflowSeqInit + 1;
            tbAsmiWorkflowStages = tbAsmiWorkflowStagesRepo
                    .findById(new TbAsmiWorkflowStagesPK(workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                            tbAstpWorkflowTxnMaster.getWorkflowId(),
                            tbAsmiWorkflowLinkedStages.getId().getLinkedStageId()));
            if (!responseStageId.contains(tbAsmiWorkflowLinkedStages.getId().getLinkedStageId())) {
                responseStageId.add(tbAsmiWorkflowLinkedStages.getId().getLinkedStageId());
                JSONObject linkedStageResponse = new JSONObject();
                linkedStageResponse.put(WORKFLOW_STAGE_ID, tbAsmiWorkflowStages.get().getId().getStageId());
                linkedStageResponse.put(WORKFLOW_STAGE_TYPE, tbAsmiWorkflowStages.get().getStageType());
                linkedStageResponse.put(WORKFLOW_SCREEN_ID, tbAsmiWorkflowStages.get().getScreenId());
                linkedStageResponse.put(WORKFLOW_SEQ_NO, workflowSeqNo);

                TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail1ForEvent = saveResponseTxnDetail(
                        workflowPrevReq, tbAstpWorkflowTxnMaster, tbAsmiWorkflowStages.get().getId().getStageId(),
                        workflowSeqNo);

                wfEvents.saveEvent(workflowPrevReq, tbAstpWorkflowTxnDetail1ForEvent, 1,
                        WORKFLOW_STATUS_STARTED, "");
                workflowSeqInit = workflowSeqNo;
                linkedStageResponseList.add(linkedStageResponse);
            }
        }
    }

    private void rejectAllParallelStages(Message pMessage, List<TbAsmiWorkflowLinkedStages> linkedStagesList,
                                         JSONObject workflowPrevReq, TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster) {
        tbAstpWorkflowTxnDetailRejectList = new ArrayList<>();
        // fetch for workflowRefNo and appId and status not rejected
        for (TbAsmiWorkflowLinkedStages linkedStage : linkedStagesList) {
            List<String> status = new ArrayList<>();
            status.add(WORKFLOW_STATUS_COMPLETED);
            TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = wfTxnDetailRepo
                    .findByAppIdWorkflowRefNoMaxSeqNoStageIdStatus(workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowPrevReq.getString(APPZILLON_WORKFLOW_REF_NO), linkedStage.getId().getLinkedStageId(),
                            status);
            String updatedNextStages = tbAstpWorkflowTxnDetail.getUpdatedNextStages();
            String[] arrOfStr = updatedNextStages.split(",");
            List<String> nextLinkedStagesIdList = Arrays.asList(arrOfStr);
            getStages(nextLinkedStagesIdList, workflowPrevReq, tbAstpWorkflowTxnMaster);

        }


        // update status to rejected
        for (TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail : tbAstpWorkflowTxnDetailRejectList) {
            tbAstpWorkflowTxnDetail.setStatus(WORKFLOW_STATUS_REJECTED);
            tbAstpWorkflowTxnDetail.setLastEventNo(tbAstpWorkflowTxnDetail.getLastEventNo() + 1);
            tbAstpWorkflowTxnDetail.setUserId(pMessage.getHeader().getUserId());
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailTaskData(workflowPrevReq.getString(WORKFLOW_TASK_DATA));
            tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailRemarks(workflowPrevReq.getString(WORKFLOW_REMARKS));
            if (workflowPrevReq.has(WORKFLOW_CUSTOM_FIELD_1)) {
                tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField1(workflowPrevReq.getString(WORKFLOW_CUSTOM_FIELD_1));
            }
            if (workflowPrevReq.has(WORKFLOW_CUSTOM_FIELD_2)) {
                tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField2(workflowPrevReq.getString(WORKFLOW_CUSTOM_FIELD_2));
            }
            if (workflowPrevReq.has(WORKFLOW_CUSTOM_FIELD_3)) {
                tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField3(workflowPrevReq.getString(WORKFLOW_CUSTOM_FIELD_3));
            }
            if (workflowPrevReq.has(WORKFLOW_CUSTOM_FIELD_4)) {
                tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField4(workflowPrevReq.getString(WORKFLOW_CUSTOM_FIELD_4));
            }
            if (workflowPrevReq.has(WORKFLOW_CUSTOM_FIELD_5)) {
                tbAstpWorkflowTxnDetail.setAstpWflowTxnDetailCustomField5(workflowPrevReq.getString(WORKFLOW_CUSTOM_FIELD_5));
            }
            tbAstpWorkflowTxnDetail.setEndTS(new Date());
        }
        wfTxnDetailRepo.saveAll(tbAstpWorkflowTxnDetailRejectList);
        for (TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail : tbAstpWorkflowTxnDetailRejectList) {
            LOG.debug("{} eventId changing to prev", LOGGER_PREFIX_WORKFLOW);
            wfEvents.saveEvent(workflowPrevReq, tbAstpWorkflowTxnDetail,
                    tbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_PREV, tbAstpWorkflowTxnDetail.getUserId());
        }

    }

    private void getStages(List<String> stageIdList, JSONObject workflowPrevReq,
                           TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster) {
        for (String stageId : stageIdList) {
            TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = null;
            List<String> status = new ArrayList<>();
            status.add(WORKFLOW_STATUS_REJECTED);
            status.add(WORKFLOW_STATUS_SKIPPED);
            tbAstpWorkflowTxnDetail = wfTxnDetailRepo.findByAppIdWorkflowRefNoMaxSeqNoStageIdNotStatus(
                    workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                    workflowPrevReq.getString(APPZILLON_WORKFLOW_REF_NO),
                    stageId, status);
            if (tbAstpWorkflowTxnDetail != null) {
                if (!tbAstpWorkflowTxnDetailRejectList.contains(tbAstpWorkflowTxnDetail))
                    tbAstpWorkflowTxnDetailRejectList.add(tbAstpWorkflowTxnDetail);

                List<String> nextLinkedStagesIdList = tbAsmiWorkflowLinkedStagesRepo
                        .findLinkedStageIdByAppIdWorkflowIdStageId(
                                workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                                tbAstpWorkflowTxnMaster.getWorkflowId(), tbAstpWorkflowTxnDetail.getStageId(), WORKFLOW_LINKED_STAGE_TYPE_NEXT);
                getStages(nextLinkedStagesIdList, workflowPrevReq, tbAstpWorkflowTxnMaster);
            } else {
                return;
            }
        }
    }

    private TbAstpWorkflowTxnDetail saveResponseTxnDetail(JSONObject workflowStagesReq, TbAstpWorkflowTxnMaster txnMaster, String stageId,
                                                          int workflowSeqNo) {
        TbAstpWorkflowTxnDetailPK tbAstpWorkflowTxnDetailPK = new TbAstpWorkflowTxnDetailPK(
                workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO), workflowSeqNo);
        TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = new TbAstpWorkflowTxnDetail();
        tbAstpWorkflowTxnDetail.setId(tbAstpWorkflowTxnDetailPK);
        tbAstpWorkflowTxnDetail.setStageId(stageId);
        tbAstpWorkflowTxnDetail.setStartTS(new Date());
        tbAstpWorkflowTxnDetail.setLastEventNo(1);
        tbAstpWorkflowTxnDetail.setStatus(WORKFLOW_STATUS_PENDING);
        tbAstpWorkflowTxnDetail.setWorkflowId(txnMaster.getWorkflowId());

        wfTxnDetailRepo.save(tbAstpWorkflowTxnDetail);
        return tbAstpWorkflowTxnDetail;
    }

}
