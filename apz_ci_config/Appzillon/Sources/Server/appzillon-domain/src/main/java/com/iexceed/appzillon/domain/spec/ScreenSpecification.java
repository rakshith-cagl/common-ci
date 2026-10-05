package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsmiScrMaster;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class ScreenSpecification {
    private ScreenSpecification() {

    }

    public static Specification<TbAsmiScrMaster> likeScreenId(final String screenId) {
        return new Specification<TbAsmiScrMaster>() {


            public Predicate toPredicate(Root<TbAsmiScrMaster> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get("screenId"), screenId);
            }
        };
    }

    public static Specification<TbAsmiScrMaster> likeDesc(final String description) {
        return new Specification<TbAsmiScrMaster>() {


            public Predicate toPredicate(Root<TbAsmiScrMaster> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {

                return builder.like(root.<String>get("screenDesc"), description);
            }
        };
    }

    public static Specification<TbAsmiScrMaster> likeAppId(final String appId) {
        return new Specification<TbAsmiScrMaster>() {


            public Predicate toPredicate(Root<TbAsmiScrMaster> root, CriteriaQuery<?> query,
                                         CriteriaBuilder builder) {
                Path<?> d = (root).get("id");
                return builder.like(d.<String>get("appId"), appId);
            }
        };
    }
}
