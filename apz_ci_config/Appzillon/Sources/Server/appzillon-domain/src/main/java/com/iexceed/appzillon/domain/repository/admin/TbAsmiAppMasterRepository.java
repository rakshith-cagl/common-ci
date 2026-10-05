package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAsmiAppMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiAppMasterPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAsmiAppMasterRepository extends JpaRepository<TbAsmiAppMaster, TbAsmiAppMasterPK> {

    @Query("select tb from TbAsmiAppMaster tb")
    List<TbAsmiAppMaster> findAllOTAFile();

    @Query("select tb from TbAsmiAppMaster tb where  tb.id.appId =:appId")
    List<TbAsmiAppMaster> findAppMasterByAppIdinList(@Param("appId") String appid);

    @Query("select tb from TbAsmiAppMaster tb where  tb.id.appId =:appId")
    TbAsmiAppMaster findAppMasterByAppId(@Param("appId") String appid);

    @Query("select tb from TbAsmiAppMaster tb where  tb.id.appId =:appId and tb.androidSignature =:androidSignature")
    TbAsmiAppMaster findAppMasterByAppIdAndSignature(@Param("appId") String appid, @Param("androidSignature") String androidSignature);
}
