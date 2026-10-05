package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAslgDeviceLocation;
import com.iexceed.appzillon.domain.entity.TbAslgDeviceLocationPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAslgDeviceLocationRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;
import java.util.Date;

@Named(ServerConstants.SERVICE_TRACK_LOCATION)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class TrackLocationService {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            TrackLocationService.class.getName());
    @Inject
    TbAslgDeviceLocationRepository tbAslgDeviceLocationRepo;

    public void saveOrUpdateLocationDetails(Message pMessage) {
        LOG.info("{} In saveOrUpdateLocationDetails()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lReqJson = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_TRACK_LOCATION_REQUEST);
        if (Utils.isNotNullOrEmpty(lReqJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID))
                && Utils.isNotNullOrEmpty(lReqJson.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID))) {
            TbAslgDeviceLocation lRecord = new TbAslgDeviceLocation();
            TbAslgDeviceLocationPK pk = new TbAslgDeviceLocationPK();
            pk.setAppId(lReqJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            pk.setDeviceId(lReqJson.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
            Timestamp createTs = new Timestamp(new Date().getTime());
            lRecord.setId(pk);
            lRecord.setLatitude(lReqJson.getString(ServerConstants.LATITUDE));
            lRecord.setLongitude(lReqJson.getString(ServerConstants.LONGITUDE));
            lRecord.setCreateTs(createTs);
            tbAslgDeviceLocationRepo.save(lRecord);
            JSONObject lResponse = new JSONObject();
            lResponse.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
            pMessage.getResponseObject().setResponseJson(lResponse);
        } else {
            LOG.warn("{} appId and deviceId cannot be null or empty", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
            lDomainException.setCode(DomainException.Code.APZ_DM_009.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }
}
