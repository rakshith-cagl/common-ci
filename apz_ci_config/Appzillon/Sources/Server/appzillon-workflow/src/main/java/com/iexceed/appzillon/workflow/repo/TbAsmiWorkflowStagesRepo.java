package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowStages;
import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowStagesPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAsmiWorkflowStagesRepo extends JpaRepository<TbAsmiWorkflowStages, TbAsmiWorkflowStagesPK> {


    //fetch stage id
    @Query("select tb from TbAsmiWorkflowStages tb where tb.id.wFlowStagesAppId =:wFlowStagesAppId and tb.id.wFlowStagesWorkflowId =:wFlowStagesWorkflowId and tb.stageSeqNo = (select min(tb.stageSeqNo) from TbAsmiWorkflowStages tb where tb.id.wFlowStagesAppId =:wFlowStagesAppId and tb.id.wFlowStagesWorkflowId =:wFlowStagesWorkflowId)")
    public TbAsmiWorkflowStages findByAppIdWorkflowIdOrderBySeqNo(@Param("wFlowStagesAppId") String appId, @Param("wFlowStagesWorkflowId") String workflowId);

    @Query("select tb.stageSeqNo from TbAsmiWorkflowStages tb where tb.id.wFlowStagesAppId =:wFlowStagesAppId and tb.id.wFlowStagesWorkflowId =:wFlowStagesWorkflowId and tb.stageSeqNo = (select min(tb.stageSeqNo) from TbAsmiWorkflowStages tb where tb.id.wFlowStagesAppId =:wFlowStagesAppId and tb.id.wFlowStagesWorkflowId =:wFlowStagesWorkflowId)")
    public int findMinSeqNo(@Param("wFlowStagesAppId") String appId, @Param("wFlowStagesWorkflowId") String workflowId);


    @Query("select tb.ruleId from TbAsmiWorkflowStages tb where tb.id.wFlowStagesAppId =:wFlowStagesAppId and tb.id.wFlowStagesWorkflowId =:wFlowStagesWorkflowId and tb.id.stageId=:stageId")
    public String findRuleIdByAppIdWorkflowIdStageId(@Param("wFlowStagesAppId") String appId, @Param("wFlowStagesWorkflowId") String workflowId, @Param("stageId") String stageId);

    @Query("select tb.stageSeqNo from TbAsmiWorkflowStages tb where tb.id.wFlowStagesAppId =:wFlowStagesAppId and tb.id.wFlowStagesWorkflowId =:wFlowStagesWorkflowId and tb.id.stageId=:stageId")
    public int findSeqNoByAppIdWorkflowIdStageId(@Param("wFlowStagesAppId") String appId, @Param("wFlowStagesWorkflowId") String workflowId, @Param("stageId") String stageId);

    @Query("select tb.id.stageId from TbAsmiWorkflowStages tb where tb.id.wFlowStagesAppId =:wFlowStagesAppId and tb.id.wFlowStagesWorkflowId =:wFlowStagesWorkflowId and tb.stageSeqNo=:stageSeqNo")
    public String findStageIdByAppIdWorkflowIdStageSeqNo(@Param("wFlowStagesAppId") String appId, @Param("wFlowStagesWorkflowId") String workflowId, @Param("stageSeqNo") int stageSeqNo);

}
