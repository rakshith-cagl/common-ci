package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowLinkedStages;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnDetail;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnMaster;
import com.iexceed.appzillon.workflow.repo.TbAsmiWorkflowLinkedStagesRepo;
import com.iexceed.appzillon.workflow.repo.TbAstpWorkflowTxnDetailRepo;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.iexceed.appzillon.utils.ServerConstants.*;

@Named("WorkflowUtility")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class WorkflowUtility {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_WORKFLOW,
            WorkflowUtility.class.getName());


    @Inject
    private TbAstpWorkflowTxnDetailRepo tbAstpWorkflowTxnDetailRepo;

    @Inject
    private TbAsmiWorkflowLinkedStagesRepo tbAsmiWorkflowLinkedStagesRepo;


    public List<TbAsmiWorkflowLinkedStages> ignorePreviouslySkippedStages(JSONObject workflowPrevReq,
                                                                          TbAstpWorkflowTxnMaster tbAstpWorkflowTxnMaster, List<TbAsmiWorkflowLinkedStages> prevLinkedStages, List<TbAsmiWorkflowLinkedStages> updatedPrevLinkedStages) {
        for (TbAsmiWorkflowLinkedStages prevStage : prevLinkedStages) {
            // fetch stage status from txn details table if completed
            List<String> status = new ArrayList<>();
            status.add(WORKFLOW_STATUS_COMPLETED);
            TbAstpWorkflowTxnDetail tbAstpWorkflowTxnDetail = tbAstpWorkflowTxnDetailRepo
                    .findByAppIdWorkflowRefNoMaxSeqNoStageIdStatus(workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                            workflowPrevReq.getString(APPZILLON_WORKFLOW_REF_NO), prevStage.getId().getLinkedStageId(),
                            status);
            if (tbAstpWorkflowTxnDetail != null && validateNextStages(tbAstpWorkflowTxnDetail.getUpdatedNextStages(),
                    workflowPrevReq.getString(WORKFLOW_STAGE_ID)) && !updatedPrevLinkedStages.contains(prevStage)) {
                updatedPrevLinkedStages.add(prevStage);
            } else {
                List<TbAsmiWorkflowLinkedStages> linkedStages = tbAsmiWorkflowLinkedStagesRepo
                        .findByAppIdWorkflowIdStageId(workflowPrevReq.getString(MESSAGE_HEADER_APP_ID),
                                tbAstpWorkflowTxnMaster.getWorkflowId(), prevStage.getId().getLinkedStageId(),
                                WORKFLOW_LINKED_STAGE_TYPE_PREV);
                if (linkedStages != null) {
                    ignorePreviouslySkippedStages(workflowPrevReq, tbAstpWorkflowTxnMaster, linkedStages, updatedPrevLinkedStages);
                }
            }
        }
        return updatedPrevLinkedStages;
    }

    private boolean validateNextStages(String getNextStages, String string) {
        String[] arrOfStr = getNextStages.split(",");
        List<String> str = Arrays.asList(arrOfStr);
        return str.contains(string);
    }


    public List<String> compareResponseListFromRuleBean(String appId, String workflowId,
                                                        JSONArray plistFromRuleBean, List<String> linkedStageResponseList) {
        List<String> skipStages = new ArrayList<>();
        LOG.debug("comparing if rule bean generated");
        List<String> listFromRuleBean = new ArrayList<>();
        for (int i = 0; i < plistFromRuleBean.length(); i++) {
            listFromRuleBean.add(plistFromRuleBean.getJSONObject(i).getString(WORKFLOW_STAGE_ID));
        }
        LOG.debug("size of stage list from rule bean : {} and actual linked stages {}", listFromRuleBean.size(), linkedStageResponseList.size());
        List<String> stagesToSkip = new ArrayList<>();
        for (int i = 0; i < linkedStageResponseList.size(); i++) {
            String stage = linkedStageResponseList.get(i);
            if (!listFromRuleBean.contains(stage)) {
                skipStages.add(stage);
                stagesToSkip.add(stage);
            }
        }

        if (!skipStages.isEmpty()) {
            LOG.info("stages to skip are not empty");
            skipStagesForward(appId, workflowId, listFromRuleBean, stagesToSkip, skipStages);
            skipStagesBackward(appId, workflowId, listFromRuleBean, listFromRuleBean, skipStages);
            LOG.debug("no of stages to skip : {}", skipStages.size());
            return skipStages;
        }

        return skipStages;

    }

    private void skipStagesForward(String appId, String workflowId, List<String> stagesFromRuleBean,
                                   List<String> skippedStages, List<String> skipStages) {
        // check for next stages to skip or not
        LOG.debug("inside skipStagesForward");
        for (int i = 0; i < skippedStages.size(); i++) {

            String stage = skippedStages.get(i);
            // get nextStages of the skipped stage
            List<String> nextStages = tbAsmiWorkflowLinkedStagesRepo.findLinkedStageIdByAppIdWorkflowIdStageId(appId,
                    workflowId, stage, WORKFLOW_LINKED_STAGE_TYPE_NEXT);
            for (String s : nextStages) {

                boolean recursiveCall = true;

                // check if #previous stages of s is 1
                recursiveCall = isRecursiveCall(appId, workflowId, stagesFromRuleBean, skipStages, s, recursiveCall);

                if (recursiveCall)
                    skipStagesForward(appId, workflowId, stagesFromRuleBean, nextStages, skipStages);
            }
        }

    }

    private boolean isRecursiveCall(String appId, String workflowId, List<String> stagesFromRuleBean, List<String> skipStages, String s, boolean recursiveCall) {
        if (tbAsmiWorkflowLinkedStagesRepo.findLinkedStageIdByAppIdWorkflowIdStageId(appId, workflowId, s,
                WORKFLOW_LINKED_STAGE_TYPE_PREV).size() == 1) {
            // if it is not in skipStages list and has only one previous
            // stage and not in the response from rule bean skip the
            // stage
            if (!skipStages.contains(s) && !stagesFromRuleBean.contains(s)) {
                LOG.debug("adding stage to skiplist : {}", s);
                skipStages.add(s);
            } else {
                recursiveCall = false;
            }

        } else {
            recursiveCall = false;
        }
        return recursiveCall;
    }

    private void skipStagesBackward(String appId, String workflowId, List<String> stagesFromRuleBean, List<String> currentStages,
                                    List<String> skipStages) {
        // get previous of stagesFromRuleBean
        for (String stage : currentStages) {
            List<String> prevStages = tbAsmiWorkflowLinkedStagesRepo.findLinkedStageIdByAppIdWorkflowIdStageId(appId,
                    workflowId, stage, WORKFLOW_LINKED_STAGE_TYPE_PREV);
            LOG.debug("inside skipStagesBackward");
            for (String s : prevStages) {
                if (!skipStages.contains(s) && !stagesFromRuleBean.contains(s)) {
                    LOG.debug("adding stage to skiplist : {}", s);
                    skipStages.add(s);
                    // check if have more than 1 next stage
                    if (tbAsmiWorkflowLinkedStagesRepo.findLinkedStageIdByAppIdWorkflowIdStageId(appId, workflowId, s,
                            WORKFLOW_LINKED_STAGE_TYPE_NEXT).size() > 1) {
                        skipStagesForward(appId, workflowId, stagesFromRuleBean, Collections.singletonList(s), skipStages);
                    }
                    skipStagesBackward(appId, workflowId, stagesFromRuleBean, Collections.singletonList(s), skipStages);
                } else {
                    break;
                }
            }
        }

    }


}
