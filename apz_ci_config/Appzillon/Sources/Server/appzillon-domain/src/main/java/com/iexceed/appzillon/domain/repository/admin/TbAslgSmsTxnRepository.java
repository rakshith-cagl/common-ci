package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAslgSmsTxn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Ripu
 */
@Repository
public interface TbAslgSmsTxnRepository extends JpaRepository<TbAslgSmsTxn, String>, JpaSpecificationExecutor<TbAslgSmsTxn> {

}
