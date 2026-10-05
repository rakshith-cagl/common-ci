package com.iexceed.appzillon.domain.repository.meta;

import com.iexceed.appzillon.domain.entity.TbAsfsFileDetails;
import com.iexceed.appzillon.domain.entity.TbAsfsFileDetailsId;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TbAsfsFileDetailsRepository extends
        CrudRepository<TbAsfsFileDetails, TbAsfsFileDetailsId>,
        JpaSpecificationExecutor<TbAsfsFileDetails> {

}
