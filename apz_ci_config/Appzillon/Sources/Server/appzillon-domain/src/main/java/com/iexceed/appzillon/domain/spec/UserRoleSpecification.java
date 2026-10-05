package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsmiUserRole;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;

public class UserRoleSpecification {
    private UserRoleSpecification() {

    }

    public static Specification<TbAsmiUserRole> likeAppId(final String appId) {
        return (root, query, builder) -> {
            Path<?> d = (root).get("id");
            return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_APP_ID), appId);
        };
    }

    public static Specification<TbAsmiUserRole> likeUserId(final String userId) {
        return (root, query, builder) -> {
            Path<?> d = (root).get("id");
            return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_USER_ID), userId);
        };
    }

    public static Specification<TbAsmiUserRole> likeRoleId(final String roleId) {
        return (root, query, builder) -> {
            Path<?> d = (root).get("id");
            return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_USER_ID), roleId);
        };
    }

}
