package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAsmiCsNonceDetail;
import com.iexceed.appzillon.domain.entity.TbAsmiCsNonceDetailPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TbAsmiValidateNonceDetailRepository extends JpaRepository<TbAsmiCsNonceDetail, TbAsmiCsNonceDetailPK> {

    @Modifying
    @Query("delete from TbAsmiCsNonceDetail tb where tb.id.serverNonce=:serverNonce")
    void deleteRecWithsNonce(@Param("serverNonce") String serverNonce);

    @Modifying
    @Query("delete from TbAsmiCsNonceDetail tb where tb.id.appId=:appId AND tb.id.deviceId=:deviceId AND tb.id.serverNonce!=:serverNonce")
    void deleteRecsWithDeviceId(@Param("appId") String appId, @Param("deviceId") String deviceId, @Param("serverNonce") String serverNonce);

    @Query(value = "Select * from TB_ASMI_CS_NONCEDETAILS tb where  tb.CREATED_ON=:createdOn AND tb.DEVICE_ID =:deviceId AND tb.APP_ID =:appId AND tb.REQUEST_ID=:requestId AND tb.CLIENT_NONCE=:clientNonce AND tb.SERVER_NONCE=:serverNonce", nativeQuery = true)
    TbAsmiCsNonceDetail findWithCreatedOnAndPK(@Param("createdOn") LocalDate createdOn, @Param("deviceId") String deviceId, @Param("appId") String appId, @Param("requestId") String requestId, @Param("clientNonce") String clientNonce, @Param("serverNonce") String serverNonce);

    @Query(value = "Select  * from TB_ASMI_CS_NONCEDETAILS tb where tb.DEVICE_ID =:deviceId AND tb.APP_ID =:appId AND tb.REQUEST_ID=:requestId AND tb.CLIENT_NONCE=:clientNonce AND tb.SERVER_NONCE=:serverNonce", nativeQuery = true)
    TbAsmiCsNonceDetail findWithPK(@Param("deviceId") String deviceId, @Param("appId") String appId, @Param("requestId") String requestId, @Param("clientNonce") String clientNonce, @Param("serverNonce") String serverNonce);


}