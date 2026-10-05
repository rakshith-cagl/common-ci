package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsmiJobMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAsmiJobMasterRepository extends JpaRepository<TbAsmiJobMaster, String> {

    @Query("SELECT t FROM TbAsmiJobMaster t where t.jobStatus != 'C'")
    List<TbAsmiJobMaster> findJobsByStatus();

    @Query("SELECT t FROM TbAsmiJobMaster t where t.jobName =:JobName")
    TbAsmiJobMaster findJobMasterByJobName(@Param("JobName") String jobName);
}
