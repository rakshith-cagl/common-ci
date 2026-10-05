package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowLinkedStages;
import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowLinkedStagesPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAsmiWorkflowLinkedStagesRepo extends JpaRepository<TbAsmiWorkflowLinkedStages, TbAsmiWorkflowLinkedStagesPK> {
    @Query("select  tb from  TbAsmiWorkflowLinkedStages  tb where tb.id.appId =:appId and tb.id.workflowId =:workflowId and tb.id.stageId =:stageId and tb.id.linkageType =:linkageType")
    public List<TbAsmiWorkflowLinkedStages> findByAppIdWorkflowIdStageId(@Param("appId") String appId, @Param("workflowId") String workflowId, @Param("stageId") String stageId, @Param("linkageType") String linkageType);

    @Query("select  tb.id.linkedStageId from  TbAsmiWorkflowLinkedStages  tb where tb.id.appId =:appId and tb.id.workflowId =:workflowId and tb.id.stageId =:stageId and tb.id.linkageType =:linkageType")
    public List<String> findLinkedStageIdByAppIdWorkflowIdStageId(@Param("appId") String appId, @Param("workflowId") String workflowId, @Param("stageId") String stageId, @Param("linkageType") String linkageType);


}
