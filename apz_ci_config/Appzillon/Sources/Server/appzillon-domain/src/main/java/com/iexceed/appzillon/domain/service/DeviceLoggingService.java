package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.slf4j.MDC;

import javax.inject.Named;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Appzillon-3.1-14
 *
 * @author Ripu
 * This class is written to deal with Remote Enabling of Log.
 */
@Named(ServerConstants.DEVICE_LOGGING_SERVICE)
public class DeviceLoggingService {

    private Logger log = LoggerFactory.getLoggerFactory().getErrorLoggingLogger(ServerConstants.LOGGER_ERROR, DeviceLoggingService.class.getName());

    /**
     * Below method written by Ripu for writing error logs by creating time stamp folder under user folder
     *
     * @param pMessage
     * @return
     */
    public void writeLogs(Message pMessage) {
        JSONObject response = null;
        Date date = new java.util.Date();
        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dft.format(date);
        dft = new SimpleDateFormat("HH-mm-ss-sss");
        String timeStamp = dft.format(date);
        String deviceId = pMessage.getHeader().getDeviceId();
        if (deviceId.contains("/") || deviceId.contains("\\")) {
            deviceId = deviceId.replace("/", "");
            deviceId = deviceId.replace("\\", "");
        }
        String appId = pMessage.getHeader().getAppId();
        String userID = pMessage.getHeader().getUserId();
        String logRouter = appId + "/DeviceLogs/" + userID + "/" + deviceId + "/" + dateStr + "/" + timeStamp;
        MDC.put("errorLogRouter", logRouter.trim());
        try {
            JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
            JSONObject errorLogginRequest = (JSONObject) requestJson.get(ServerConstants.APPZILLON_ERROR_LOGGING_REQUEST);
            String error = (String) errorLogginRequest.get(ServerConstants.ERROR);
            log.trace(error);
            response = new JSONObject();
            response.put(ServerConstants.APPZILLON_ERROR_LOGGING_RESPONSE, new JSONObject().put(ServerConstants.RESPONSE, ServerConstants.SUCCESS));
        } catch (JSONException jsonExp) {
            log.error("{} Json Exception", ServerConstants.LOGGER_PREFIX_DOMAIN, jsonExp);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(jsonExp.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
        pMessage.getResponseObject().setResponseJson(response);
    }
}
