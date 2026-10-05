package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnMaster;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnMasterPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAstpWorkflowTxnMasterRepo extends JpaRepository<TbAstpWorkflowTxnMaster, TbAstpWorkflowTxnMasterPK> {


    @Query("select tb from TbAstpWorkflowTxnMaster tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.status = :status")
    public TbAstpWorkflowTxnMaster findByAppIdWorkflowRefNoStatus(@Param("appId") String appId, @Param("workflowRefNo") String workflowRefNo, @Param("status") String status);


    @Query("select tb from TbAstpWorkflowTxnMaster tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.status !=:status")
    public TbAstpWorkflowTxnMaster findByAppIdWorkflowRefNoNotStatus(@Param("appId") String appId, @Param("workflowRefNo") String workflowRefNo, @Param("status") String status);


}
