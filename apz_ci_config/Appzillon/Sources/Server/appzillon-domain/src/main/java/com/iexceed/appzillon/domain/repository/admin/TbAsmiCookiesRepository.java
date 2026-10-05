package com.iexceed.appzillon.domain.repository.admin;


import com.iexceed.appzillon.domain.entity.TbAsmiCookies;
import com.iexceed.appzillon.domain.entity.TbAsmiCookiesPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAsmiCookiesRepository extends JpaRepository<TbAsmiCookies, TbAsmiCookiesPK>, JpaSpecificationExecutor<TbAsmiCookiesPK> {
    @Query("select tb from TbAsmiCookies tb where tb.id.appId =:appId and tb.id.usrSelector !=:usrSelector and tb.userId =:userId")
    List<TbAsmiCookies> findByAppIdUserIdNotSelector(@Param("appId") String appId, @Param("usrSelector") String usrSelector, @Param("userId") String userId);
}
