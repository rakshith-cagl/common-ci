package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsnfGroupMaster;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class GroupSpecification {
    public static Specification<TbAsnfGroupMaster> likeDesc(
            final String description) {
        return new Specification<TbAsnfGroupMaster>() {

            public Predicate toPredicate(Root<TbAsnfGroupMaster> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {

                return builder
                        .like(root.<String>get(ServerConstants.DEVICE_GROUP_DESCRIPTION), description);
            }
        };
    }

    public static Specification<TbAsnfGroupMaster> likeGroupId(
            final String groupId) {
        return new Specification<TbAsnfGroupMaster>() {

            public Predicate toPredicate(Root<TbAsnfGroupMaster> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {

                Path<?> d = (root).get("id");
                return builder.like(d.<String>get(ServerConstants.NOTIFICATION_GROUP_ID), groupId);
            }
        };
    }

    public static Specification<TbAsnfGroupMaster> descisNull() {
        return new Specification<TbAsnfGroupMaster>() {

            public Predicate toPredicate(Root<TbAsnfGroupMaster> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.isNull(root.<String>get(ServerConstants.DEVICE_GROUP_DESCRIPTION));
            }
        };
    }
}
