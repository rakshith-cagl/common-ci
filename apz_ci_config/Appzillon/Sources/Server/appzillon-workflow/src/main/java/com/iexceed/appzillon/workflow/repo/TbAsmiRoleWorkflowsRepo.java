package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAsmiRoleWorkflows;
import com.iexceed.appzillon.workflow.entity.TbAsmiRoleWorkflowsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TbAsmiRoleWorkflowsRepo extends JpaRepository<TbAsmiRoleWorkflows, TbAsmiRoleWorkflowsPK> {

    @Query("select tb.id.roleId from TbAsmiRoleWorkflows tb where tb.id.appId =:appId and tb.id.workflowId =:workflowId and tb.id.stageId =:stageId")
    public String findRoleIdByAppIdWorkflowIdStageId(@Param("appId") String appId,
                                                     @Param("workflowId") String workflowId, @Param("stageId") String stageId);

}
