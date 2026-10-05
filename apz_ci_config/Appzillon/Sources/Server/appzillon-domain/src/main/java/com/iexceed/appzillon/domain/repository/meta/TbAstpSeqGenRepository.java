package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAstpSeqGen;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAstpSeqGenRepository extends
        CrudRepository<TbAstpSeqGen, TbAstpSeqGen> {
    @Query("select tb from TbAstpSeqGen tb where tb.sequenceName =:psequenceName")
    TbAstpSeqGen getloggingsequencenumber(
            @Param("psequenceName") String psequenceName);

    // Oracle
    @Query(value = "select NOTIFICATION_NO.nextval from dual", nativeQuery = true)
    int getNotifSequenceFromDBSequence();


}
