package com.iexceed.appzillon.workflow.repo;

import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnDetail;
import com.iexceed.appzillon.workflow.entity.TbAstpWorkflowTxnDetailPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbAstpWorkflowTxnDetailRepo extends JpaRepository<TbAstpWorkflowTxnDetail, TbAstpWorkflowTxnDetailPK> {
    @Query("select tb from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.id.workflowSeqNo =:workflowSeqNo and tb.stageId=:stageId and tb.status =:status ")
    public TbAstpWorkflowTxnDetail findByAppIdWorkflowRefNoSeqNoStageIdStatus(@Param("appId") String appId,
                                                                              @Param("workflowRefNo") String workflowRefNo, @Param("workflowSeqNo") int workflowSeqNo,
                                                                              @Param("stageId") String stageId, @Param("status") String status);

    @Query("select tb from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.id.workflowSeqNo =:workflowSeqNo and tb.status =:status ")
    public TbAstpWorkflowTxnDetail findByAppIdWorkflowRefNoSeqNoStatus(@Param("appId") String appId,
                                                                       @Param("workflowRefNo") String workflowRefNo, @Param("workflowSeqNo") int workflowSeqNo, @Param("status") String status);

    @Query("select tb from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.stageId=:stageId and tb.status =:status ")
    public TbAstpWorkflowTxnDetail findByAppIdWorkflowRefNoStageIdStatus(@Param("appId") String appId,
                                                                         @Param("workflowRefNo") String workflowRefNo, @Param("stageId") String stageId,
                                                                         @Param("status") String status);

    @Query("select tb from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.id.workflowSeqNo=:workflowSeqNo and tb.status =:status ")
    public TbAstpWorkflowTxnDetail findByAppIdWorkflowRefNoWorkflowSeqNoStatus(@Param("appId") String appId,
                                                                               @Param("workflowRefNo") String workflowRefNo, @Param("workflowSeqNo") int workflowSeqNo,
                                                                               @Param("status") String status);

    @Query("select tb from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.status in (:status) and tb.id.workflowSeqNo = (select max(tb.id.workflowSeqNo) from tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.stageId =:stageId )")
    public TbAstpWorkflowTxnDetail findByAppIdWorkflowRefNoMaxSeqNoStageIdStatus(@Param("appId") String appId,
                                                                                 @Param("workflowRefNo") String workflowRefNo, @Param("stageId") String stageId,
                                                                                 @Param("status") List<String> status);

    @Query("select tb from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.id.workflowSeqNo = (select max(tb.id.workflowSeqNo) from tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.stageId =:stageId and tb.status not in (:status))")
    public TbAstpWorkflowTxnDetail findByAppIdWorkflowRefNoMaxSeqNoStageIdNotStatus(@Param("appId") String appId,
                                                                                    @Param("workflowRefNo") String workflowRefNo, @Param("stageId") String stageId,
                                                                                    @Param("status") List<String> status);

    @Query("select tb from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.status =:status")
    public List<TbAstpWorkflowTxnDetail> findByAppIdAndWorkflowRefNoAndStatus(@Param("appId") String appId,
                                                                              @Param("workflowRefNo") String workflowRefNo, @Param("status") String status);

    @Query("select tb from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo and tb.stageId=:stageId and tb.status !=:status")
    public TbAstpWorkflowTxnDetail findAppIdAndWorkflowRefNoAndStageIdAndNotStatus(@Param("appId") String appId,
                                                                                   @Param("workflowRefNo") String workflowRefNo, @Param("stageId") String stageId,
                                                                                   @Param("status") String status);

    @Query("select max(tb.id.workflowSeqNo) from TbAstpWorkflowTxnDetail tb where tb.id.appId =:appId and tb.id.workflowRefNo =:workflowRefNo")
    public int findByAppIdAndWorkflowIdAndOrderByWorkflowSeqNo(@Param("appId") String appId,
                                                               @Param("workflowRefNo") String workflowRefNo);

}
