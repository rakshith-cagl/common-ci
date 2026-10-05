package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class InterfaceMasterSpecification {
    private InterfaceMasterSpecification() {

    }

    public static Specification<TbAsmiIntfMaster> likeAppId(final String appId) {
        return new Specification<TbAsmiIntfMaster>() {


            public Predicate toPredicate(Root<TbAsmiIntfMaster> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_APP_ID), appId);
            }
        };
    }

    public static Specification<TbAsmiIntfMaster> likeInterfaceId(final String intfId) {
        return new Specification<TbAsmiIntfMaster>() {


            public Predicate toPredicate(Root<TbAsmiIntfMaster> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get(ServerConstants.MESSAGE_HEADER_INTERFACE_ID), intfId);
            }
        };
    }

}
