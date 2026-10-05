package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsmiSecurityParams;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SecurityParameterSpecification {
    private SecurityParameterSpecification() {

    }

    public static Specification<TbAsmiSecurityParams> likeAppId(
            final String appId) {
        return new Specification<TbAsmiSecurityParams>() {


            public Predicate toPredicate(Root<TbAsmiSecurityParams> root,
                                         CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.like(root.<String>get("appId"), appId);
            }
        };
    }

}
