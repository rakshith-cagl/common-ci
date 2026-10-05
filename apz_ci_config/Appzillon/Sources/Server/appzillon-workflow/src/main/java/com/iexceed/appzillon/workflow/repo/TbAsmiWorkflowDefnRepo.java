package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowDefn;
import com.iexceed.appzillon.workflow.entity.TbAsmiWorkflowDefnPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAsmiWorkflowDefnRepo extends JpaRepository<TbAsmiWorkflowDefn, TbAsmiWorkflowDefnPK> {

    @Query("select tb from TbAsmiWorkflowDefn tb where tb.id.wFlowDefnAppId =:wFlowDefnAppId and tb.id.wFlowDefnWorkflowId =:wFlowDefnWorkflowId and tb.active =:active")
    public TbAsmiWorkflowDefn findByAppIdWorkflowIdActive(@Param("wFlowDefnAppId") String appId,
                                                          @Param("wFlowDefnWorkflowId") String workflowId, @Param("active") String active);

}
