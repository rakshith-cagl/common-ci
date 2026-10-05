package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsmiRoleMaster;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class RoleMasterSpecification {
    private RoleMasterSpecification() {

    }

    public static Specification<TbAsmiRoleMaster> likeAppId(final String appId) {
        return new Specification<TbAsmiRoleMaster>() {

            public Predicate toPredicate(Root<TbAsmiRoleMaster> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_APP_ID), appId);
            }
        };
    }

    public static Specification<TbAsmiRoleMaster> likeRoleDesc(
            final String roleDesc) {
        return new Specification<TbAsmiRoleMaster>() {

            public Predicate toPredicate(Root<TbAsmiRoleMaster> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.like(root.<String>get(ServerConstants.ROLEDESCRIPTION), roleDesc);
            }
        };
    }

    public static Specification<TbAsmiRoleMaster> likeRoleId(final String roleId) {
        return new Specification<TbAsmiRoleMaster>() {

            public Predicate toPredicate(Root<TbAsmiRoleMaster> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get(ServerConstants.ROLEID), roleId);
            }
        };
    }
}
