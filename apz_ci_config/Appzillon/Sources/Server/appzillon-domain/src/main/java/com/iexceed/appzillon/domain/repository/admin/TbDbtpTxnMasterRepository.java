package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbDbtpTxnMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TbDbtpTxnMasterRepository extends JpaRepository<TbDbtpTxnMaster, String>, JpaSpecificationExecutor<TbDbtpTxnMaster> {

}
