package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.history.TbAshsSmsUser;
import com.iexceed.appzillon.domain.entity.history.TbAshsSmsUserPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Ripu
 * This Repository is written for doing all required operation with DB for sending SMS to user
 */
@Repository
public interface TbAshsSmsUserRepository extends JpaRepository<TbAshsSmsUser, TbAshsSmsUserPK> {

    @Query("select max(tb.id.versionNo) from TbAshsSmsUser tb where tb.id.appId =:appId AND tb.id.userId =:userId")
    Integer findMaxVersionNoByAppIdAndUserId(@Param("appId") String lAppId, @Param("userId") String lUserId);


}
