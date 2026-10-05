package com.iexceed.appzillon.domain.repository.admin;


import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScr;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScrPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TbAsmiCnvUIScrRepository extends JpaRepository<TbAsmiCnvUIScr, TbAsmiCnvUIScrPK> {
    @Query("select tb from TbAsmiCnvUIScr tb where  tb.id.appId =:appId and tb.id.screenId =:screenId")
    public TbAsmiCnvUIScr getScreenDetailsOnVersion(@Param("appId") String appId, @Param("screenId") String screenId);
}