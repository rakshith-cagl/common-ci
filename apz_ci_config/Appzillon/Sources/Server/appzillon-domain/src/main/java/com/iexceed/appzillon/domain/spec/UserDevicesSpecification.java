package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsmiUserDevices;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class UserDevicesSpecification {
    private UserDevicesSpecification() {

    }

    public static Specification<TbAsmiUserDevices> likeUserId(
            final String userId) {
        return new Specification<TbAsmiUserDevices>() {

            public Predicate toPredicate(Root<TbAsmiUserDevices> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get("userId"), userId);
            }
        };
    }

    public static Specification<TbAsmiUserDevices> likeDeviceId(
            final String deviceId) {
        return new Specification<TbAsmiUserDevices>() {

            public Predicate toPredicate(Root<TbAsmiUserDevices> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get("deviceId"), deviceId);
            }
        };
    }

}
