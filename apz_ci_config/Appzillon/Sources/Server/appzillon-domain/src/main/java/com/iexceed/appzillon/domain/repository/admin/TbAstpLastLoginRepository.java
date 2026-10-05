/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAstpLastLogin;
import com.iexceed.appzillon.domain.entity.TbAstpLastLoginPK;
import com.iexceed.appzillon.domain.entity.VwActiveSession;
import com.iexceed.appzillon.domain.entity.VwLocationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author arthanarisamy
 */
@Repository
public interface TbAstpLastLoginRepository extends JpaRepository<TbAstpLastLogin, TbAstpLastLoginPK>, JpaSpecificationExecutor<TbAstpLastLogin> {
    @Query("select tb from TbAstpLastLogin tb where tb.id.userId =:userId and tb.id.appId =:appId order by tb.loginTime desc")
    List<TbAstpLastLogin> findByUserIdAndAppIdOrderByLoginTime(@Param("userId") String string, @Param("appId") String appId);

    @Query("select tb from TbAstpLastLogin tb where tb.id.userId =:userId and tb.id.appId =:appId and tb.id.deviceId !=:deviceId")
    List<TbAstpLastLogin> findByUserIdAndAppIdAndNotByDeviceId(@Param("userId") String pUserId, @Param("appId") String pAppId, @Param("deviceId") String pDeviceId);

    @Query("select count(*) from TbAstpLastLogin tb where tb.id.appId =:appId AND tb.id.deviceId = 'WEB' AND tb.sessionId !=null OR tb.sessionId = ''")
    int getCurrentSessionCountByOsWeb(@Param("appId") String pAppId);

    @Query("select count(*) from TbAstpLastLogin tb where tb.id.appId =:appId AND tb.id.deviceId = 'WEB' AND (tb.sessionId !=null OR tb.sessionId = '') AND tb.createTs>=:fromDate AND tb.createTs <=:toDate")
    int getCurrentSessionCountByOsWebAndDate(@Param("appId") String pAppId, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query("select tb from VwLocationDetail tb where tb.id.appId =:appId")
    List<VwLocationDetail> getLocationDetailsByAppId(@Param("appId") String pAppId);

    @Query("select tb.origination, tb.latitude, tb.longitude, tb.sublocality, tb.adminAreaLvl1, tb.adminAreaLvl2, tb.country,tb.formattedAddress,tb.userId,tb.appId,tb.loginTime from VwLocationDetail tb where tb.appId =:appId")
    List<Object[]> getLocationDetailsByAppIdAsObj(@Param("appId") String appId);

    @Query("select vs from VwActiveSession vs where vs.appId=:appId")
    List<VwActiveSession> getCurrentSessionCountByOs(@Param("appId") String pAppId);

    @Modifying
    @Query("update TbAstpLastLogin tb SET tb.lastReqTime =:reqTime WHERE tb.id.userId =:userId and tb.id.appId =:appId and tb.id.deviceId =:deviceId and tb.sessionId =:sessionId")
    void updateLastReqTime(@Param("userId") String userId, @Param("appId") String appId, @Param("deviceId") String deviceId, @Param("sessionId") String sessionId, @Param("reqTime") Date reqTime);

    @Query("select tb from TbAstpLastLogin tb where tb.id.userId =:userId and tb.id.appId =:appId and tb.id.deviceId !=:deviceId and sessionId is null")
    List<TbAstpLastLogin> findByUserIdAndAppIdAndNotByDeviceIdAndNullSessionId(@Param("userId") String pUserId, @Param("appId") String pAppId, @Param("deviceId") String pDeviceId);

    @Modifying
    @Query("update TbAstpLastLogin u set u.otpValidationCount =:otpValidationCount where u.id.appId =:appId and u.id.userId =:userId and u.id.deviceId =:deviceId ")
    int updateValidationCount(@Param("appId") String appId, @Param("userId") String userId, @Param("deviceId") String deviceId, @Param("otpValidationCount") int otpValidationCount);


    @Query("select tb from TbAstpLastLogin tb where tb.id.appId =:appId and tb.id.deviceId IN :deviceIdList")
    List<TbAstpLastLogin> findUsersByAppIdAndDeviceID(@Param("appId") String appId, @Param("deviceIdList") List<String> deviceIdList);

}
