package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAstpWorkflowDet;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class TaskRepairSpecification {
    private TaskRepairSpecification() {

    }

    public static Specification<TbAstpWorkflowDet> likeAppId(final String pAppId) {
        return new Specification<TbAstpWorkflowDet>() {
            public Predicate toPredicate(Root<TbAstpWorkflowDet> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get("appId"), pAppId);
            }
        };
    }

    public static Specification<TbAstpWorkflowDet> likeUserId(final String pUserId) {
        return new Specification<TbAstpWorkflowDet>() {
            public Predicate toPredicate(Root<TbAstpWorkflowDet> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.like(root.<String>get("userId"), pUserId);
            }
        };
    }

    public static Specification<TbAstpWorkflowDet> likeWorkFlowId(final String pWorkFlowID) {
        return new Specification<TbAstpWorkflowDet>() {
            public Predicate toPredicate(Root<TbAstpWorkflowDet> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get("workflowId"), pWorkFlowID);
            }
        };
    }


    public static Specification<TbAstpWorkflowDet> likeStatus(final String pStatus) {
        return new Specification<TbAstpWorkflowDet>() {
            public Predicate toPredicate(Root<TbAstpWorkflowDet> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.like(root.<String>get("status"), pStatus);
            }
        };
    }
}
