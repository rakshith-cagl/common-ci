package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsmiAppIdVersion;
import com.iexceed.appzillon.domain.entity.TbAsmiAppIdVersionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TbAsmiAppIdVersionRepository extends JpaRepository<TbAsmiAppIdVersion, TbAsmiAppIdVersionPK> {

    @Query("select MAX(tb.id.appIdVersion) from TbAsmiAppIdVersion tb where  tb.id.appId =:appId")
    String findMaxAppIdVersionByAppId(@Param("appId") String appid);

    @Query("select MAX(tb.id.appIdVersion) from TbAsmiAppIdVersion tb where  tb.id.appId =:appId AND tb.id.os =:os")
    String findMaxAppIdVersionByAppIdAndOS(@Param("appId") String appid, @Param("os") String lOS);

}
