package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAstpBeacon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TbAstpBeaconRepository extends JpaRepository<TbAstpBeacon, Integer>, JpaSpecificationExecutor<TbAstpBeacon> {

    @Query("SELECT t FROM TbAstpBeacon t where t.status ='P'")
    List<TbAstpBeacon> getBeaconDetails();

    @Query("select tb from TbAstpBeacon tb where tb.id.id =:id")
    TbAstpBeacon findAstpBeaconByRefNo(@Param("id") long id);


}
