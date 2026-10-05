package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAstpARMaster;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.meta.TbAstpARMasterRepository;
import com.iexceed.appzillon.domain.spec.AugumentedRealitySpecification;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static com.iexceed.appzillon.domain.utils.Constants.*;

/**
 * @author arthanarisamy
 */
@Named(ServerConstants.SERVICE_AUGUMENTED_REALITY)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class AugumentedRealityService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getDomainLogger(ServerConstants.LOGGER_DOMAIN,
                    AugumentedRealityService.class.getName());
    @Inject
    TbAstpARMasterRepository ctbASTPARMasterRepo;

    private static boolean distance(double lat1, double lon1, double lat2, double lon2, String unit, String radius) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        boolean status = true;
        if (unit.equals("K")) {
            dist = dist * 1.609344;
        } else if (unit.equals("ME")) {
            dist = dist * 1.609344 * 1000;
        } else if (unit.equals("N")) {
            dist = dist * 0.8684;
        }
        double lRadius = Long.parseLong(radius);
        LOG.trace("{} dist > lRadius {}  > {} : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, dist, lRadius, (dist > lRadius));
        if (dist > lRadius) {
            status = false;
        }

        return status;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    /**
     * @param pMessage
     */
    public void getAugementDetails(Message pMessage) {
        List<TbAstpARMaster> tbAstpARMasterList = null;
        JSONObject lARReqJSON = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} inside getAugementDetails", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lARDetails = lARReqJSON.getJSONObject("fetchARDetails");
        String appId = "%";
        String regionCode = "%";
        String category = "%";
        String latitude = "%";
        String longitude = "%";
        String title = "%";
        String additionalInfo = "%";
        appId = getValue(lARDetails, APP_ID, appId);
        regionCode = getValue(lARDetails, REGION_CODE, regionCode);
        category = getValue(lARDetails, CATEGORY, category);

        if (lARDetails.has(RADIUS) && Utils.isNotNullOrEmpty(lARDetails.getString(RADIUS))) {
            latitude = getValue(lARDetails, LATITUDE, latitude);
            longitude = getValue(lARDetails, LONGITUDE, longitude);
        }

        title = getValue(lARDetails, TITLE, title);
        additionalInfo = getValue(lARDetails, ADDITIONAL_INFO, additionalInfo);

        tbAstpARMasterList = ctbASTPARMasterRepo.findAll(Specification.where(AugumentedRealitySpecification.likeAppId(appId))
                .and(AugumentedRealitySpecification.likeRegionCode(regionCode)).and(AugumentedRealitySpecification.likeCategory(category))
                .and(AugumentedRealitySpecification.likeLatitude(latitude)).and(AugumentedRealitySpecification.likeLongitude(longitude))
                .and(AugumentedRealitySpecification.likeTitle(title)).and(AugumentedRealitySpecification.likeAdditionalInfo(additionalInfo)));
        LOG.debug("{} No of records fetched : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, tbAstpARMasterList.size());
        if (lARDetails.has(RADIUS)) {
            tbAstpARMasterList = calculateRadiusnBuildResponse(tbAstpARMasterList, pMessage);
        }
        LOG.debug("{} No of records fetched After calculating radius : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, tbAstpARMasterList.size());
        buildResponse(tbAstpARMasterList, pMessage);

    }

    private String getValue(JSONObject lARDetails, String key, String value) {
        if (lARDetails.has(key) && Utils.isNotNullOrEmpty(lARDetails.getString(key))) {
            value = lARDetails.getString(key);
        }
        return value;
    }

    /**
     * @param pTbAstpARMAsterDtlsList
     * @param pMessage
     */
    private void buildResponse(List<TbAstpARMaster> pTbAstpARMAsterDtlsList, Message pMessage) {
        JSONArray lARMasterDetails = new JSONArray();
        JSONObject lARMasterDetail = null;
        if (pTbAstpARMAsterDtlsList != null && !pTbAstpARMAsterDtlsList.isEmpty()) {
            for (int i = 0; i < pTbAstpARMAsterDtlsList.size(); i++) {
                TbAstpARMaster tbAstpMaster = pTbAstpARMAsterDtlsList.get(i);
                lARMasterDetail = new JSONObject();
                lARMasterDetail.put(APP_ID, tbAstpMaster.getAppId());
                lARMasterDetail.put(CATEGORY, tbAstpMaster.getCategory());
                lARMasterDetail.put("Id", tbAstpMaster.getId());
                lARMasterDetail.put(LATITUDE, tbAstpMaster.getLatitude());
                lARMasterDetail.put(LONGITUDE, tbAstpMaster.getLongitude());
                lARMasterDetail.put(REGION_CODE, tbAstpMaster.getRegionCode());
                lARMasterDetail.put(TITLE, tbAstpMaster.getTitle());
                lARMasterDetail.put(ADDITIONAL_INFO, tbAstpMaster.getAdditionalInfo());
                lARMasterDetail.put("description", tbAstpMaster.getDescription());
                lARMasterDetail.put("image", tbAstpMaster.getImage());

                lARMasterDetails.put(lARMasterDetail);
            }
        } else {
            LOG.error("{} No record found", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_008);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_008.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }

        pMessage.getResponseObject().setResponseJson(lARMasterDetails);
    }

    private List<TbAstpARMaster> calculateRadiusnBuildResponse(List<TbAstpARMaster> pTbAstpARMAsterDtlsList, Message pMessage) {
        List<TbAstpARMaster> lTbAstpARMasterList = new ArrayList<>();
        JSONObject lARReqJSON = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} calculateRadiusnBuildResponse Request JSON Augumented Request : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lARReqJSON);
        JSONObject lARDetails = lARReqJSON.getJSONObject("fetchARDetails");
        String lRadius = lARDetails.getString(RADIUS);
        String latitude = lARDetails.getString(LATITUDE);
        String longitude = lARDetails.getString(LONGITUDE);

        if (pTbAstpARMAsterDtlsList != null && !pTbAstpARMAsterDtlsList.isEmpty()) {
            for (int i = 0; i < pTbAstpARMAsterDtlsList.size(); i++) {
                TbAstpARMaster tbAstpMaster = pTbAstpARMAsterDtlsList.get(i);
                LOG.trace("{} Entity AR Master Details of Index : {} and tbAstpMaster : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, i, tbAstpMaster);
                String lLatitude = tbAstpMaster.getLatitude();
                String lLongitude = tbAstpMaster.getLongitude();
                boolean status = distance(Double.parseDouble(latitude), Double.parseDouble(longitude), Double.parseDouble(lLatitude), Double.parseDouble(lLongitude), "ME", lRadius);
                LOG.trace("{} Calculated Radius status : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, status);
                if (status) {
                    LOG.trace("{} Removing the record since it is outside configured range {}", ServerConstants.LOGGER_PREFIX_DOMAIN, i);
                    lTbAstpARMasterList.add(tbAstpMaster);
                }
            }
        }

        return lTbAstpARMasterList;

    }
}
