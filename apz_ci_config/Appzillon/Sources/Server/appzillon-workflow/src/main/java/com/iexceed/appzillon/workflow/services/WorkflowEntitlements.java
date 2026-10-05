package com.iexceed.appzillon.workflow.services;

import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRoleRepository;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.workflow.entity.TbAsmiRoleWorkflowsPK;
import com.iexceed.appzillon.workflow.exception.WorkflowException;
import com.iexceed.appzillon.workflow.repo.TbAsmiRoleWorkflowsRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class WorkflowEntitlements {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getWorkflowLogger(ServerConstants.LOGGER_WORKFLOW, WorkflowEntitlements.class.getName());
    @Inject
    private TbAsmiUserRoleRepository tbAsmiUserRoleRepository;
    @Inject
    private TbAsmiRoleWorkflowsRepo tbAsmiRoleWorkflowsRepo;

    /**
     * Validate if given User is entitled for workflow stage
     *
     * @param appId
     * @param userId
     * @param workflowId
     * @param stageId
     */
    public boolean isUserEntitledForWorkflowStage(String appId, String userId, String workflowId, String stageId) {


        boolean isUserEntitled = false;
        List<String> userRoles = tbAsmiUserRoleRepository.findRoleListByAppIdUserId(appId, userId);
        for (String roleId : userRoles) {
            if (tbAsmiRoleWorkflowsRepo.existsById(new TbAsmiRoleWorkflowsPK(appId, roleId, workflowId, stageId))) {
                isUserEntitled = true;
                break;
            }
        }
        if (!isUserEntitled) {
            WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
            wfexp.setCode(WorkflowException.Code.APZ_WFL_012.toString());
            wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(WorkflowException.Code.APZ_WFL_012));
            wfexp.setPriority("1");
            LOG.error("{} Invalid Workflow request, check userId/AppId/WorkflowId {}", ServerConstants.LOGGER_PREFIX_WORKFLOW, wfexp);
            throw wfexp;
        }
        return isUserEntitled;
    }

}
