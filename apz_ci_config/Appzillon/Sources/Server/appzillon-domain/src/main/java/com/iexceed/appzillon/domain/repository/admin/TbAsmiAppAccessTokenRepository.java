package com.iexceed.appzillon.domain.repository.admin;


import com.iexceed.appzillon.domain.entity.TbAsmiAppAccessToken;
import com.iexceed.appzillon.domain.entity.TbAsmiAppAccessTokenPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TbAsmiAppAccessTokenRepository extends JpaRepository<TbAsmiAppAccessToken, TbAsmiAppAccessTokenPK> {

    @Query("select tb from TbAsmiAppAccessToken tb where tb.id.appId =:appId and tb.id.userId =:userId")
    List<TbAsmiAppAccessToken> findByAppIdUserIduserAppAuthentication(@Param("appId") String appId, @Param("userId") String userId);
}