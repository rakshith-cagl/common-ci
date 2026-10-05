package com.iexceed.appzillon.impl;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IReportServiceBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;


/**
 * @author Ripu
 */
public class BirtServicesImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
            ServerConstants.LOGGER_FRAMEWORKS,
            BirtServicesImpl.class.getName());

    public void serviceRequestDispatcher(Message pMessage, SpringCamelContext context)
            throws ExternalServicesRouterException, InvalidPayloadException,
            ClassNotFoundException, JSONException {

        LOG.debug("{} inside serviceRequestDispatcher()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lAppId = pMessage.getHeader().getAppId();
        String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
        LOG.debug("{} BIRTReportService Injecting Report Service bean with beanId -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lCamelID);
        IReportServiceBean reportService = (IReportServiceBean) context
                .getApplicationContext().getBean(lCamelID);
        LOG.debug("{} BIRTReportService Bean is injected -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, reportService);

        JSONObject finalResponseJson = (JSONObject) reportService.callService(pMessage, context);
        pMessage.getResponseObject().setResponseJson(finalResponseJson);
    }


}
