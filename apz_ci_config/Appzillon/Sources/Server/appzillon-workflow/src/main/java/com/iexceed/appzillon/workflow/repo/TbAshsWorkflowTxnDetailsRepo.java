package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAshsWorkflowTxnDetails;
import com.iexceed.appzillon.workflow.entity.TbAshsWorkflowTxnDetailsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAshsWorkflowTxnDetailsRepo extends JpaRepository<TbAshsWorkflowTxnDetails, TbAshsWorkflowTxnDetailsPK> {
}
