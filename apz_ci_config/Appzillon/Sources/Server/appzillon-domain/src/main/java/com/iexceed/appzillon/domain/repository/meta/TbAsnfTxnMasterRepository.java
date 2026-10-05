package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsnfTxnMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnMasterPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAsnfTxnMasterRepository extends
        JpaRepository<TbAsnfTxnMaster, TbAsnfTxnMasterPK> {

}
