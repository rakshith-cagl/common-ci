package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsmiJobExpression;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAsmiJobExpressionRepository extends CrudRepository<TbAsmiJobExpression, String> {

    @Query("SELECT t FROM TbAsmiJobExpression t")
    List<TbAsmiJobExpression> findJobExpressions();

    @Query("SELECT t FROM TbAsmiJobExpression t where t.jobName =:JobName")
    TbAsmiJobExpression findJobExpessionByJobName(@Param("JobName") String jobName);
}
