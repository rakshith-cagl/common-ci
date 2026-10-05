package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAsmiSecurityParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author arthanarisamy
 */
@Repository
public interface TbAsmiSecurityParamsRepository extends JpaRepository<TbAsmiSecurityParams, String>, JpaSpecificationExecutor<TbAsmiSecurityParams> {
    @Query("SELECT c FROM TbAsmiSecurityParams as c WHERE c.appId =:appId")
    TbAsmiSecurityParams findSecurityParamsbyAppId(@Param("appId") String appId);

    @Modifying
    @Query("Delete from TbAsmiSecurityParams as c where c.appId =:appId")
    void purgeSecurityParamsByAppId(@Param("appId") String appId);
}
