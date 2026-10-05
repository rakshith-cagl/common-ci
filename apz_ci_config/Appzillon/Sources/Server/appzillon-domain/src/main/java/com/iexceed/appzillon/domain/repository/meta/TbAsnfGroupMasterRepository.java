package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsnfGroupMaster;
import com.iexceed.appzillon.domain.entity.TbAsnfGroupMasterPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TbAsnfGroupMasterRepository extends
        JpaRepository<TbAsnfGroupMaster, TbAsnfGroupMasterPK>,
        JpaSpecificationExecutor<TbAsnfGroupMaster> {

    @Query("SELECT tb FROM TbAsnfGroupMaster tb where tb.id.appId=:appId")
    List<TbAsnfGroupMaster> findByAppId(@Param("appId") String appId);

}
