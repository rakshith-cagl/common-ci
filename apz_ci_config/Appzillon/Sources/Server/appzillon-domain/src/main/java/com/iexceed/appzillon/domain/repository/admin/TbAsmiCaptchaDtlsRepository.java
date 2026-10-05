package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAsmiCaptchaDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAsmiCaptchaDtlsRepository
        extends JpaRepository<TbAsmiCaptchaDtls, Integer>, JpaSpecificationExecutor<TbAsmiCaptchaDtls> {

    @Query("select tb from TbAsmiCaptchaDtls tb where tb.captchaStatus = 'P'  ")
    List<TbAsmiCaptchaDtls> findAsmiCaptchaDtlsByCaptchaStatus();

    @Query("select tb from TbAsmiCaptchaDtls tb where tb.id.captchaRef =:captchaRef")
    TbAsmiCaptchaDtls findAsmiCaptchaByRefNo(@Param("captchaRef") long captchaRef);
}
