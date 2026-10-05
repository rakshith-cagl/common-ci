package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;

public class UserSpecification {
    private UserSpecification() {

    }

    public static Specification<TbAsmiUser> likeAppId(final String appId) {
        return (root, query, builder) -> {
            Path<?> d = (root).get("id");
            return builder.like(d.<String>get("appId"), appId);
        };
    }

    public static Specification<TbAsmiUser> likeUserId(final String userId) {
        return (root, query, builder) -> {
            Path<?> d = (root).get("id");
            return builder.like(d.<String>get("userId"), userId);
        };
    }

    // added on 30-6-2014
    public static Specification<TbAsmiUser> likeUserName(
            final String userName) {
        return (root, query, builder) -> builder.like(root.<String>get("userName"), userName);
    }

}
