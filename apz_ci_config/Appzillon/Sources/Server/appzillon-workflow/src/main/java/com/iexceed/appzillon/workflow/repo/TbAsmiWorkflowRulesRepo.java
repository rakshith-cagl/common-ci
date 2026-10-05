package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowRules;
import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowRulesPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAsmiWorkflowRulesRepo extends JpaRepository<TbAsmiWorkflowRules, TbAsmiWorkflowRulesPK> {


    @Query("select tb from TbAsmiWorkflowRules tb where tb.id.appId =:appId and tb.id.workflowId =:workflowId and tb.id.ruleId =:ruleId and tb.active='Y'")
    public TbAsmiWorkflowRules findByAppIdWorkflowIdStageId(@Param("appId") String appId, @Param("workflowId") String workflowId, @Param("ruleId") String ruleId);

}
