package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAshsWorkflowTxnMaster;
import com.iexceed.appzillon.workflow.entity.TbAshsWorkflowTxnMasterPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAshsWorkflowTxnMasterRepo extends JpaRepository<TbAshsWorkflowTxnMaster, TbAshsWorkflowTxnMasterPK> {
}
