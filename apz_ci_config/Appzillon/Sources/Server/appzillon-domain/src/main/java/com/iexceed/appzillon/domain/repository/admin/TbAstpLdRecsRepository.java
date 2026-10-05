package com.iexceed.appzillon.domain.repository.admin;

import com.iexceed.appzillon.domain.entity.TbAstpLdRecs;
import com.iexceed.appzillon.domain.entity.TbAstpLdRecsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TbAstpLdRecsRepository extends JpaRepository<TbAstpLdRecs, TbAstpLdRecsPK>, JpaSpecificationExecutor<TbAstpLdRecs> {

    @Query("select tb.data1,tb.data2,tb.data3,tb.data4,tb.data5 from TbAstpLdRecs tb where tb.id.refNo =:refNo ORDER BY tb.id.seqNo")
    List<Object[]> findLdRecsByRefNo(@Param("refNo") String refNo);

    @Modifying
    @Query("delete from TbAstpLdRecs tb where tb.id.refNo =:refNo")
    void deleteLDRecs(@Param("refNo") String refNo);

}
