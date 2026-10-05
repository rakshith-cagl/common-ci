package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsnfTxnGroup;
import com.iexceed.appzillon.domain.entity.TbAsnfTxnGroupPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAsnfTxnGroupsRepository extends
        JpaRepository<TbAsnfTxnGroup, TbAsnfTxnGroupPK> {

}
