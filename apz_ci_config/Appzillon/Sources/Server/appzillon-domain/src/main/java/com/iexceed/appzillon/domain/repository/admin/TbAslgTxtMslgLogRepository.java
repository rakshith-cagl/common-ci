package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAslgTxtMslgLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Ripu
 */
@Repository
public interface TbAslgTxtMslgLogRepository extends JpaRepository<TbAslgTxtMslgLog, String>, JpaSpecificationExecutor<TbAslgTxtMslgLog> {

}
