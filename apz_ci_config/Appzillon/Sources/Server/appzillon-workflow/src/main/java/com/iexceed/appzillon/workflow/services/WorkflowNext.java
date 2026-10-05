package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.utils.hauwei.util.CollectionUtils;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.entity.*;
import com.iexceed.appzillon.workflow.exception.WorkflowException;
import com.iexceed.appzillon.workflow.exception.WorkflowParallelException;
import com.iexceed.appzillon.workflow.iface.IWorkflowRuleBean;
import com.iexceed.appzillon.workflow.repo.*;
import com.iexceed.appzillon.workflow.util.WorkFlowUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Named("WorkflowNext")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class WorkflowNext {

    public static final String P_MESSAGE = "pMessage";
    public static final String WORKFLOW_STAGES_REQ = "workflowStagesReq";
    public static final String TB_ASTP_WORKFLOW_TXN_DETAIL = "tbAstpWorkflowTxnDetail";
    public static final String TB_ASTP_WORKFLOW_TXN_MASTER = "tbAstpWorkflowTxnMaster";
    public static final String WORKFLOW_ID1 = "workflowId";
    public static final String TB_ASMI_WORKFLOW_LINKED_STAGES = "TbAsmiWorkflowLinkedStages";
    public static final String INPUTS = "inputs";
    public static final String LINKED_STAGE_RESPONSE_LIST = "linkedStageResponseList";
    public static final String WORKFLOW_SEQ_INIT = "workflowSeqInit";
    public static final String NEXT_LINKED_STAGES_LIST = "nextLinkedStagesList";
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_WORKFLOW,
            WorkflowNext.class.getName());
    @Inject
    private TbAsmiWorkflowLinkedStagesRepo tbAsmiWorkflowLinkedStagesRepo;
    @Inject
    private TbAsmiWorkflowStagesRepo tbAsmiWorkflowStagesRepo;
    @Inject
    private TbAstpWorkflowTxnDetailRepo tbAstpWorkflowTxnDetailRepo;
    @Inject
    private TbAstpWorkflowTxnMasterRepo tbAstpWorkflowTxnMasterRepo;
    @Inject
    private TbAsmiWorkflowRulesRepo tbAsmiWorkflowRulesRepo;
    @Inject
    private WorkflowTxnDetails workflowTxnDetails;
    @Inject
    private WorkflowEvents workflowEvents;
    @Inject
    private WorkflowEntitlements workflowEntitlements;

    @Inject
    private WorkflowMasterDetail workflowMasterDetail;

    @Transactional(noRollbackFor = {WorkflowParallelException.class})
    public void fetchNextWorkflowDetail(Message pMessage) {
        LOG.info("{} WorkflowNext", LOGGER_PREFIX_WORKFLOW);
        JSONObject workflowStagesReq = null;

        boolean arePreviousParallelStagesCompleted = false;

        workflowStagesReq = pMessage.getRequestObject().getRequestJson().getJSONObject(INTERFACE_ID_WORKFLOW_NEXT);
        TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = null;
        TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster = tbAstpWorkflowTxnMasterRepo.findByAppIdWorkflowRefNoStatus(
                workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO), WORKFLOW_STATUS_WIP);

        if (tbAstpWorkflowTxnMaster != null) {
            if (workflowEntitlements.isUserEntitledForWorkflowStage(pMessage.getHeader().getAppId(),
                    pMessage.getHeader().getUserId(), tbAstpWorkflowTxnMaster.getWorkflowId(),
                    workflowStagesReq.getString(WORKFLOW_STAGE_ID))) {
                LOG.debug("{} User Entitled for the workflow stageId", LOGGER_PREFIX_WORKFLOW);
                tbAstpWorkflowTxnDetail = tbAstpWorkflowTxnDetailRepo.findByAppIdWorkflowRefNoSeqNoStageIdStatus(
                        workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                        workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO),
                        workflowStagesReq.getInt(WORKFLOW_SEQ_NO), workflowStagesReq.getString(WORKFLOW_STAGE_ID),
                        WORKFLOW_STATUS_WIP);

                if (tbAstpWorkflowTxnDetail != null) {
                    String workflowId = tbAstpWorkflowTxnMaster.getWorkflowId();
                    List<TbAsmiWorkflowLinkedStages> nextLinkedStagesList;
                    LOG.debug("{} finding next stages", LOGGER_PREFIX_WORKFLOW);
                    nextLinkedStagesList = tbAsmiWorkflowLinkedStagesRepo.findByAppIdWorkflowIdStageId(
                            workflowStagesReq.getString(MESSAGE_HEADER_APP_ID), workflowId,
                            workflowStagesReq.getString(WORKFLOW_STAGE_ID), WORKFLOW_LINKED_STAGE_TYPE_NEXT);

                    LOG.debug("{} checking if previous/parallel stages completed for next", LOGGER_PREFIX_WORKFLOW);
                    arePreviousParallelStagesCompleted = findPreviousParallelStagesStatus(workflowStagesReq, workflowId, nextLinkedStagesList);

                    LOG.debug("{} arePreviousParallelStagesCompleted : {}", LOGGER_PREFIX_WORKFLOW, arePreviousParallelStagesCompleted);
                    JSONObject inputs = new JSONObject();
                    inputs.put(WF_LAST_EVENT_NO, tbAstpWorkflowTxnDetail.getLastEventNo() + 1);
                    inputs.put(WF_UPDATED_NEXT_STAGE, "");
                    inputs.put(STATUS, WORKFLOW_STATUS_COMPLETED);
                    inputs.put(USERID, tbAstpWorkflowTxnDetail.getUserId());
                    inputs.put(APPZILLON_WORKFLOW_SEQ_NO, workflowStagesReq.getInt(APPZILLON_WORKFLOW_SEQ_NO));
                    inputs.put(STAGE_ID, workflowStagesReq.getString(WORKFLOW_STAGE_ID));

                    persistTransaction(nextLinkedStagesList.isEmpty(), "{} no next stages found for current stageId", workflowStagesReq, tbAstpWorkflowTxnMaster, tbAstpWorkflowTxnDetail, inputs, WORKFLOW_STATUS_COMPLETED);

                    LOG.debug("{} No. of next stages available for current stage {}", LOGGER_PREFIX_WORKFLOW, nextLinkedStagesList.size());
                    LOG.debug("{} input for linked stage {} {} {}", LOGGER_PREFIX_WORKFLOW, workflowStagesReq.getString(MESSAGE_HEADER_APP_ID), workflowId, workflowStagesReq.getString(WORKFLOW_STAGE_ID));

                    List<JSONObject> linkedStageResponseList = new ArrayList<>();
                    int workflowSeqInit = tbAstpWorkflowTxnDetailRepo.findByAppIdAndWorkflowIdAndOrderByWorkflowSeqNo(
                            workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO));

                    if (arePreviousParallelStagesCompleted) {

                        Map<String, Object> inputParam = new HashMap<>();
                        inputParam.put(P_MESSAGE, pMessage);
                        inputParam.put(WORKFLOW_STAGES_REQ, workflowStagesReq);
                        inputParam.put(TB_ASTP_WORKFLOW_TXN_DETAIL, tbAstpWorkflowTxnDetail);
                        inputParam.put(TB_ASTP_WORKFLOW_TXN_MASTER, tbAstpWorkflowTxnMaster);
                        inputParam.put(WORKFLOW_ID1, workflowId);
                        inputParam.put(TB_ASMI_WORKFLOW_LINKED_STAGES, nextLinkedStagesList);
                        inputParam.put(INPUTS, inputs);
                        inputParam.put(LINKED_STAGE_RESPONSE_LIST, linkedStageResponseList);
                        inputParam.put(WORKFLOW_SEQ_INIT, workflowSeqInit);
                        buildResponseForNextStage(inputParam);
                    } else {
                        persistTransaction(!nextLinkedStagesList.isEmpty(), "{} Updating the current stage's status to completed as the previous stages are completed", workflowStagesReq, tbAstpWorkflowTxnMaster, tbAstpWorkflowTxnDetail, inputs, WORKFLOW_STATUS_WIP);
                        WorkflowParallelException wfexp = WorkFlowUtil.getWorkFlowParallelExceptionObj(WorkflowParallelException.Code.APZ_WFL_007);
                        LOG.error("{} parallel stage(s) are not completed {}", LOGGER_PREFIX_WORKFLOW, wfexp);
                        throw wfexp;
                    }
                } else {
                    WorkflowException wfexp = WorkFlowUtil.getWorkFlowExceptionObj(WorkflowException.Code.APZ_WFL_006);
                    LOG.error("{} Invalid stageId {}", LOGGER_PREFIX_WORKFLOW, wfexp);
                    throw wfexp;
                }
            }
        } else {
            WorkflowException wfexp = WorkFlowUtil.getWorkFlowExceptionObj(WorkflowException.Code.APZ_WFL_004);
            LOG.error("{} Invalid workflowrefno {}", LOGGER_PREFIX_WORKFLOW, wfexp);
            throw wfexp;
        }

    }

    private void buildResponseForNextStage(Map<String, Object> inputParam) {

        Message pMessage = (Message) inputParam.get(P_MESSAGE);
        JSONObject workflowStagesReq = (JSONObject) inputParam.get(WORKFLOW_STAGES_REQ);
        TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = (TbAstpWorkflowTxnDetail) inputParam.get(TB_ASTP_WORKFLOW_TXN_DETAIL);
        TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster = (TbAstpWorkflowTxnMaster) inputParam.get(TB_ASTP_WORKFLOW_TXN_MASTER);
        String workflowId = (String) inputParam.get(WORKFLOW_ID1);
        List<TbAsmiWorkflowLinkedStages> nextLinkedStagesList = (List<TbAsmiWorkflowLinkedStages>) inputParam.get(NEXT_LINKED_STAGES_LIST);
        JSONObject inputs = (JSONObject) inputParam.get(INPUTS);
        List<JSONObject> linkedStageResponseList = (List<JSONObject>) inputParam.get(LINKED_STAGE_RESPONSE_LIST);
        int workflowSeqInit = (int) inputParam.get(WORKFLOW_SEQ_INIT);

        JSONObject response;
        JSONObject workflowStagesResponse;
        // building respponse for next stages and saving
        // response
        //buildNextStagesResponse(workflowStagesReq, tbAstpWorkflowTxnMaster, nextLinkedStagesList,
        //linkedStageResponseList, workflowSeqInit)
        workflowStagesResponse = new JSONObject();
        workflowStagesResponse.put(MESSAGE_HEADER_APP_ID, workflowStagesReq.getString(MESSAGE_HEADER_APP_ID));
        workflowStagesResponse.put(WORKFLOW_ID, tbAstpWorkflowTxnMaster.getWorkflowId());
        workflowStagesResponse.put(WORKFLOW_REF_NO,
                workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO));

        workflowStagesResponse.put(TASK_DATA, workflowStagesReq.getString(WORKFLOW_TASK_DATA));
        response = new JSONObject();

        /* Checking if there are any rule beans */
        List<String> nextLinkedStages = new ArrayList<>();
        for (TbAsmiWorkflowLinkedStages linkedStages : nextLinkedStagesList) {
            nextLinkedStages.add(linkedStages.getId().getLinkedStageId());
        }
        List<String> skipStages = null;
        if (processUserRuleBean(pMessage, workflowStagesReq, workflowStagesResponse, workflowId)) {
            validNextStages(workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                    tbAstpWorkflowTxnMaster.getWorkflowId(),
                    workflowStagesReq.getString(WORKFLOW_STAGE_ID),
                    workflowStagesResponse.getJSONArray(LINKED_STAE_LIST));
            skipStages = ((WorkflowUtility) WorkflowStartup.getInstance().getSpringContext()
                    .getAutowireCapableBeanFactory().getBean("WorkflowUtility"))
                    .compareResponseListFromRuleBean(
                            workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                            tbAstpWorkflowTxnMaster.getWorkflowId(),
                            workflowStagesResponse.getJSONArray(LINKED_STAE_LIST),
                            nextLinkedStages);
            // validateStagesFromRuleBean(workflowStagesResponse.getJSONArray(LINKED_STAE_LIST))
        }

        Map<String, Object> mInputParam = new HashMap<>();
        mInputParam.put(P_MESSAGE, pMessage);
        mInputParam.put(WORKFLOW_STAGES_REQ, workflowStagesReq);
        mInputParam.put("workflowStagesResponse", workflowStagesResponse);
        mInputParam.put(TB_ASTP_WORKFLOW_TXN_MASTER, tbAstpWorkflowTxnMaster);
        mInputParam.put(NEXT_LINKED_STAGES_LIST, nextLinkedStagesList);
        mInputParam.put(LINKED_STAGE_RESPONSE_LIST, linkedStageResponseList);
        mInputParam.put(WORKFLOW_SEQ_INIT, workflowSeqInit);
        mInputParam.put("skipStages", skipStages);
        processNextStageActivities(mInputParam);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linkedStageResponseList.size(); i++) {
            sb.append(linkedStageResponseList.get(i).getString(STAGE_ID));
            sb.append(",");
        }
        workflowStagesResponse.put(LINKED_STAE_LIST, linkedStageResponseList);

        if (!nextLinkedStagesList.isEmpty()) {
            LOG.debug("{} Updating the current stage's status to completed as the previous stages are completed", LOGGER_PREFIX_WORKFLOW);
            // Persisting Workflow transaction details

            inputs.put(WF_UPDATED_NEXT_STAGE, sb.toString());

            workflowTxnDetails.saveTxnDetail(workflowStagesReq, tbAstpWorkflowTxnMaster,
                    tbAstpWorkflowTxnDetail.getStartTS(),
                    inputs);
            // Persisting Workflow transaction events
            workflowEvents.saveEvent(workflowStagesReq, tbAstpWorkflowTxnDetail,
                    tbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_NEXT,
                    tbAstpWorkflowTxnDetail.getUserId());
            workflowMasterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster,
                    WORKFLOW_STATUS_WIP, workflowStagesReq);
        }


        response.put("workflowNextResponse", workflowStagesResponse);
        pMessage.getResponseObject().setResponseJson(response);
    }

    private void persistTransaction(boolean nextLinkedStagesList, String message, JSONObject workflowStagesReq, TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster, TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail, JSONObject inputs, String workflowStatusWip) {
        if (nextLinkedStagesList) {
            LOG.debug(message, LOGGER_PREFIX_WORKFLOW);
            // Persisting Workflow transaction details
            workflowTxnDetails.saveTxnDetail(workflowStagesReq, tbAstpWorkflowTxnMaster,
                    tbAstpWorkflowTxnDetail.getStartTS(),
                    inputs);
            // Persisting Workflow transaction events
            workflowEvents.saveEvent(workflowStagesReq, tbAstpWorkflowTxnDetail,
                    tbAstpWorkflowTxnDetail.getLastEventNo(), WORKFLOW_STATUS_NEXT,
                    tbAstpWorkflowTxnDetail.getUserId());
            workflowMasterDetail.updateTxnMasterDetails(tbAstpWorkflowTxnMaster,
                    workflowStatusWip, workflowStagesReq);
        }
    }

    private void processNextStageActivities(Map<String, Object> inputParam) {
        Message pMessage = (Message) inputParam.get(P_MESSAGE);
        JSONObject workflowStagesReq = (JSONObject) inputParam.get(WORKFLOW_STAGES_REQ);
        JSONObject workflowStagesResponse = (JSONObject) inputParam.get("workflowStagesResponse");
        TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster = (TbAstpWorkflowTxnMaster) inputParam.get(TB_ASTP_WORKFLOW_TXN_MASTER);
        List<TbAsmiWorkflowLinkedStages> nextLinkedStagesList = (List<TbAsmiWorkflowLinkedStages>) inputParam.get(NEXT_LINKED_STAGES_LIST);
        List<JSONObject> linkedStageResponseList = (List<JSONObject>) inputParam.get(LINKED_STAGE_RESPONSE_LIST);
        int workflowSeqInit = (int) inputParam.get(WORKFLOW_SEQ_INIT);
        List<String> skipStages = (List<String>) inputParam.get("skipStages");

        if (CollectionUtils.isEmpty(skipStages)) {
            LOG.debug("stages to skip are null");
            buildNextStagesResponse(workflowStagesReq, tbAstpWorkflowTxnMaster,
                    nextLinkedStagesList, linkedStageResponseList, workflowSeqInit);

        } else {
            LOG.debug("skipping stages");
            workflowSeqInit = tbAstpWorkflowTxnDetailRepo
                    .findByAppIdAndWorkflowIdAndOrderByWorkflowSeqNo(
                            workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO));
            workflowSeqInit = skipStages(pMessage, workflowStagesReq, tbAstpWorkflowTxnMaster, skipStages, workflowSeqInit);

            JSONArray llistFromRuleBean = workflowStagesResponse.getJSONArray(LINKED_STAE_LIST);
            for (int i = 0; i < llistFromRuleBean.length(); i++) {
                linkedStageResponseList.add(llistFromRuleBean.getJSONObject(i));
            }
            saveNextStagesTxnDetail(workflowStagesReq, tbAstpWorkflowTxnMaster,
                    linkedStageResponseList, workflowSeqInit);
        }
    }

    private boolean findPreviousParallelStagesStatus(JSONObject workflowStagesReq, String workflowId, List<TbAsmiWorkflowLinkedStages> nextLinkedStagesList) {
        boolean arePreviousParallelStagesCompleted;
        if (!nextLinkedStagesList.isEmpty()) {
            arePreviousParallelStagesCompleted = arePreviousParallelStagesCompleted(nextLinkedStagesList,
                    workflowStagesReq, workflowId);
        } else {
            LOG.debug("{} no next stages found for current stageId so making arePreviousParallelStagesCompleted :true", LOGGER_PREFIX_WORKFLOW);
            arePreviousParallelStagesCompleted = true;
        }
        return arePreviousParallelStagesCompleted;
    }

    private void validNextStages(String appId, String workflowId, String currentStage, JSONArray stagesFromRuleBean) {
        List<TbAsmiWorkflowLinkedStages> nextLinkedStagesList = null;
        LOG.debug("{} finding next stages to validate", LOGGER_PREFIX_WORKFLOW);
        nextLinkedStagesList = tbAsmiWorkflowLinkedStagesRepo.findByAppIdWorkflowIdStageId(appId, workflowId,
                currentStage, WORKFLOW_LINKED_STAGE_TYPE_NEXT);
        List<String> allStages = findNextStages(appId, workflowId, nextLinkedStagesList, new ArrayList<>());
        for (int i = 0; i < stagesFromRuleBean.length(); i++) {
            if (!allStages.contains(stagesFromRuleBean.getJSONObject(i).getString(WORKFLOW_STAGE_ID))) {
                WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
                wfexp.setCode(WorkflowException.Code.APZ_WFL_013.toString());
                wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_013));
                wfexp.setPriority("1");
                LOG.error("{} is not a valid next stage of stage : {} {}", stagesFromRuleBean.getJSONObject(i).getString(WORKFLOW_STAGE_ID), currentStage, wfexp);
                throw wfexp;

            }
        }

    }

    private List<String> findNextStages(String appId, String workflowId, List<TbAsmiWorkflowLinkedStages> nextLinkedStagesList, List<String> allStages) {
        for (TbAsmiWorkflowLinkedStages tbAsmiWorkflowLinkedStages : nextLinkedStagesList) {
            if (!allStages.contains(tbAsmiWorkflowLinkedStages.getId().getLinkedStageId()))
                allStages.add(tbAsmiWorkflowLinkedStages.getId().getLinkedStageId());
            List<TbAsmiWorkflowLinkedStages> linkedStagesList = null;
            linkedStagesList = tbAsmiWorkflowLinkedStagesRepo.findByAppIdWorkflowIdStageId(appId, workflowId,
                    tbAsmiWorkflowLinkedStages.getId().getLinkedStageId(), WORKFLOW_LINKED_STAGE_TYPE_NEXT);
            if (linkedStagesList != null && !linkedStagesList.isEmpty()) {
                findNextStages(appId, workflowId, linkedStagesList, allStages);
            }
        }
        return allStages;
    }

    private int skipStages(Message pMessage, JSONObject workflowStagesReq, TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster,
                           List<String> skipStagesList, int workflowSeqInit) {
        for (String stage : skipStagesList) {
            LOG.debug("skipping stage : {}", stage);
            int workflowSeqNo = workflowSeqInit + 1;
            int lastEventNo = 1;
            JSONObject inputs = new JSONObject();
            inputs.put(STAGE_ID, stage);
            inputs.put(APPZILLON_WORKFLOW_SEQ_NO, workflowSeqNo);
            inputs.put(WF_LAST_EVENT_NO, lastEventNo);
            inputs.put(WF_UPDATED_NEXT_STAGE, "");
            inputs.put(STATUS, WORKFLOW_STATUS_SKIPPED);
            inputs.put(USERID, pMessage.getHeader().getUserId());

            TbAstpWorkflowTxnDetail txnDetail = workflowTxnDetails.saveTxnDetail(workflowStagesReq,
                    tbAstpWorkflowTxnMaster, new Date(), inputs);

            workflowEvents.saveEvent(workflowStagesReq, txnDetail, lastEventNo, WORKFLOW_STATUS_SKIPPED, "");
            workflowSeqInit = workflowSeqNo;
            LOG.debug(LOGGER_PREFIX_WORKFLOW, "workflowSeqInit : {}", workflowSeqInit);
        }
        return workflowSeqInit;
    }

    private boolean processUserRuleBean(Message pMessage, JSONObject workflowStagesReq,
                                        JSONObject workflowStagesResponse, String workflowId) {
        TbAsmiWorkflowRules tbAsmiWorkflowRules = null;
        IWorkflowRuleBean workflowRuleBean = null;
        boolean ruleBeanNotNull = false;
        String ruleId = tbAsmiWorkflowStagesRepo.findRuleIdByAppIdWorkflowIdStageId(
                workflowStagesReq.getString(MESSAGE_HEADER_APP_ID), workflowId,
                workflowStagesReq.getString(WORKFLOW_STAGE_ID));
        LOG.debug("{} ruleId is : {}", LOGGER_PREFIX_WORKFLOW, ruleId);

        if (ruleId != null && !ruleId.isEmpty()) {
            ruleBeanNotNull = true;
            LOG.debug("{} rule id is : {} checking for active status", LOGGER_PREFIX_WORKFLOW, ruleId);
            tbAsmiWorkflowRules = tbAsmiWorkflowRulesRepo.findByAppIdWorkflowIdStageId(
                    workflowStagesReq.getString(MESSAGE_HEADER_APP_ID), workflowId, ruleId);
            if (tbAsmiWorkflowRules != null) {
                LOG.debug("{} rule is active", LOGGER_PREFIX_WORKFLOW);
                workflowRuleBean = (IWorkflowRuleBean) WorkflowStartup.getInstance().getSpringContext()
                        .getAutowireCapableBeanFactory().getBean(tbAsmiWorkflowRules.getRuleBean());
                try {
                    workflowStagesResponse = workflowRuleBean.processRuleBean(pMessage, workflowStagesResponse);
                    if (!workflowStagesResponse.has(LINKED_STAE_LIST)) {
                        ruleBeanNotNull = false;
                    }
                } catch (Exception ex) {
                    WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
                    wfexp.setCode(WorkflowException.Code.APZ_WFL_011.toString());
                    wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_011));
                    wfexp.setPriority("1");
                    LOG.error("{} Error in rule bean for the given stageId {}", LOGGER_PREFIX_WORKFLOW, wfexp);
                    throw wfexp;
                }

            }

        }
        return ruleBeanNotNull;
    }

    private void buildNextStagesResponse(JSONObject workflowStagesReq, TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster,
                                         List<TbAsmiWorkflowLinkedStages> nextLinkedStagesList, List<JSONObject> linkedStageResponseList,
                                         int workflowSeqInit) {
        Optional<TbAsmiWorkflowStages> tbAsmiWorkflowStages;
        for (TbAsmiWorkflowLinkedStages tbAsmiWorkflowLinkedStages : nextLinkedStagesList) {
            int workflowSeqNo = workflowSeqInit + 1;
            tbAsmiWorkflowStages = tbAsmiWorkflowStagesRepo.findById(new TbAsmiWorkflowStagesPK(
                    workflowStagesReq.getString(MESSAGE_HEADER_APP_ID), tbAstpWorkflowTxnMaster.getWorkflowId(),
                    tbAsmiWorkflowLinkedStages.getId().getLinkedStageId()));

            JSONObject linkedStageResponse = new JSONObject();
            linkedStageResponse.put(STAGE_ID, tbAsmiWorkflowStages.get().getId().getStageId());
            linkedStageResponse.put(WORKFLOW_STAGE_TYPE, tbAsmiWorkflowStages.get().getStageType());
            linkedStageResponse.put(WORKFLOW_SCREEN_ID, tbAsmiWorkflowStages.get().getScreenId());
            linkedStageResponse.put(WORKFLOW_SEQ_NO, workflowSeqNo);

            int lastEventNo = 1;

            TbAstpWorkflowTxnDetail txnDetail = workflowTxnDetails.saveTxnDetail(workflowStagesReq,
                    tbAstpWorkflowTxnMaster, new Date(), getInputObj(tbAsmiWorkflowStages.get().getId().getStageId(), workflowSeqNo, lastEventNo));
            workflowEvents.saveEvent(workflowStagesReq, txnDetail, lastEventNo, WORKFLOW_STATUS_STARTED, "");
            workflowSeqInit = workflowSeqNo;
            LOG.debug("{} workflowSeqInit: {}", LOGGER_PREFIX_WORKFLOW, workflowSeqInit);
            linkedStageResponseList.add(linkedStageResponse);
        }
    }

    private void saveNextStagesTxnDetail(JSONObject workflowStagesReq, TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster,
                                         List<JSONObject> linkedStageResponseList,
                                         int workflowSeqInit) {
        Optional<TbAsmiWorkflowStages> tbAsmiWorkflowStages;
        for (int i = 0; i < linkedStageResponseList.size(); i++) {

            int workflowSeqNo = workflowSeqInit + 1;
            tbAsmiWorkflowStages = tbAsmiWorkflowStagesRepo.findById(new TbAsmiWorkflowStagesPK(
                    workflowStagesReq.getString(MESSAGE_HEADER_APP_ID), tbAstpWorkflowTxnMaster.getWorkflowId(),
                    linkedStageResponseList.get(i).getString(WORKFLOW_STAGE_ID)));

            JSONObject linkedStageResponse = new JSONObject();
            linkedStageResponse.put(STAGE_ID, tbAsmiWorkflowStages.get().getId().getStageId());
            linkedStageResponse.put(WORKFLOW_STAGE_TYPE, tbAsmiWorkflowStages.get().getStageType());
            linkedStageResponse.put(WORKFLOW_SCREEN_ID, tbAsmiWorkflowStages.get().getScreenId());
            linkedStageResponse.put(WORKFLOW_SEQ_NO, workflowSeqNo);
            linkedStageResponseList.remove(i);
            linkedStageResponseList.add(i, linkedStageResponse);
            int lastEventNo = 1;


            TbAstpWorkflowTxnDetail txnDetail = workflowTxnDetails.saveTxnDetail(workflowStagesReq,
                    tbAstpWorkflowTxnMaster, new Date(), getInputObj(tbAsmiWorkflowStages.get().getId().getStageId(), workflowSeqNo, lastEventNo));
            workflowEvents.saveEvent(workflowStagesReq, txnDetail, lastEventNo, WORKFLOW_STATUS_STARTED, "");
            workflowSeqInit = workflowSeqNo;

        }

    }

    private JSONObject getInputObj(String stageId, int workflowSeqNo, int lastEventNo) {
        JSONObject inputs = new JSONObject();
        inputs.put(STAGE_ID, stageId);
        inputs.put(APPZILLON_WORKFLOW_SEQ_NO, workflowSeqNo);
        inputs.put(WF_LAST_EVENT_NO, lastEventNo);
        inputs.put(WF_UPDATED_NEXT_STAGE, "");
        inputs.put(STATUS, WORKFLOW_STATUS_PENDING);
        inputs.put(USERID, "");
        return inputs;
    }

    private boolean arePreviousParallelStagesCompleted(List<TbAsmiWorkflowLinkedStages> nextLinkedStagesList,
                                                       JSONObject workflowStagesReq, String workflowId) {
        LOG.debug("{} inside check parallel stages", LOGGER_PREFIX_WORKFLOW);
        List<TbAsmiWorkflowLinkedStages> prevLinkedStagesList = null;
        for (TbAsmiWorkflowLinkedStages nextLinkedStages : nextLinkedStagesList) {
            LOG.debug("{} inside check parallel stages, nextStageId {}", LOGGER_PREFIX_WORKFLOW, nextLinkedStages.getId().getLinkedStageId());
            prevLinkedStagesList = tbAsmiWorkflowLinkedStagesRepo.findByAppIdWorkflowIdStageId(
                    workflowStagesReq.getString(MESSAGE_HEADER_APP_ID), workflowId,
                    nextLinkedStages.getId().getLinkedStageId(), "P");

            if (prevLinkedStagesList == null || prevLinkedStagesList.size() == 1)
                return true;

            for (TbAsmiWorkflowLinkedStages tbAsmiWorkflowLinkedStages : prevLinkedStagesList) {
                LOG.debug("{} tbAsmiWorkflowLinkedStages linkedStagesList size {}", LOGGER_PREFIX_WORKFLOW, prevLinkedStagesList.size());
                if (!tbAsmiWorkflowLinkedStages.getId().getLinkedStageId()
                        .equals(workflowStagesReq.getString(WORKFLOW_STAGE_ID))) {
                    List<String> lStatus = new ArrayList<>();
                    lStatus.add(WORKFLOW_STATUS_COMPLETED);
                    lStatus.add(WORKFLOW_STATUS_SKIPPED);
                    TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = tbAstpWorkflowTxnDetailRepo
                            .findByAppIdWorkflowRefNoMaxSeqNoStageIdStatus(
                                    workflowStagesReq.getString(MESSAGE_HEADER_APP_ID),
                                    workflowStagesReq.getString(APPZILLON_WORKFLOW_REF_NO),
                                    tbAsmiWorkflowLinkedStages.getId().getLinkedStageId(), lStatus);

                    if (tbAstpWorkflowTxnDetail == null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
