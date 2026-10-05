package com.iexceed.appzillon.domain.spec;

import com.iexceed.appzillon.domain.entity.TbAstpARMaster;
import org.springframework.data.jpa.domain.Specification;

public class AugumentedRealitySpecification {

    private AugumentedRealitySpecification() {
    }

    public static Specification<TbAstpARMaster> likeAppId(final String appId) {
        return (tbAstpARMaster, query, builder) -> builder
                .like(tbAstpARMaster.<String>get("appId"), appId);
    }

    public static Specification<TbAstpARMaster> likeRegionCode(final String regionCode) {
        return (tbAstpARMaster, query, builder) -> builder
                .like(tbAstpARMaster.<String>get("regionCode"), regionCode);
    }

    public static Specification<TbAstpARMaster> likeCategory(final String category) {
        return (tbAstpARMaster, query, builder) -> builder
                .like(tbAstpARMaster.<String>get("category"), category);
    }

    public static Specification<TbAstpARMaster> likeLatitude(final String latitude) {
        return (tbAstpARMaster, query, builder) -> builder
                .like(tbAstpARMaster.<String>get("latitude"), latitude);
    }

    public static Specification<TbAstpARMaster> likeLongitude(final String longitude) {
        return (tbAstpARMaster, query, builder) -> builder
                .like(tbAstpARMaster.<String>get("longitude"), longitude);
    }

    public static Specification<TbAstpARMaster> likeTitle(final String title) {
        return (tbAstpARMaster, query, builder) -> builder
                .like(tbAstpARMaster.<String>get("title"), title);

    }

    public static Specification<TbAstpARMaster> likeAdditionalInfo(final String additionalInfo) {
        return (tbAstpARMaster, query, builder) -> builder
                .like(tbAstpARMaster.<String>get("additionalInfo"), additionalInfo);
    }

}
