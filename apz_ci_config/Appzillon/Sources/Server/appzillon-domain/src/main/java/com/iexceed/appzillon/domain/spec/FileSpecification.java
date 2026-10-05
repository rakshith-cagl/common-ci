package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAsfsFileDetails;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;


public class FileSpecification {
    public static Specification<TbAsfsFileDetails> likeAppId(final String appId) {
        return (root, query, builder) -> {
            Path<?> d = (root).get("id");
            return builder.like(d.<String>get("appId"), appId);
        };
    }
}
