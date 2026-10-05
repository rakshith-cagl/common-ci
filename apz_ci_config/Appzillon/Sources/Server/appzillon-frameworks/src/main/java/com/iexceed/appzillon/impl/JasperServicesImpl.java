/**
 *
 */
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
 * @author user
 *
 */
public class JasperServicesImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
            ServerConstants.LOGGER_FRAMEWORKS,
            JasperServicesImpl.class.getName());

    public void serviceRequestDispatcher(Message pMessage, SpringCamelContext context)
            throws ExternalServicesRouterException, InvalidPayloadException,
            ClassNotFoundException, JSONException {

        LOG.debug("{} inside serviceRequestDispatcher()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lAppId = pMessage.getHeader().getAppId();
        String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
        LOG.debug("{} ReportService Injecting Report Service bean with beanId -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lCamelID);
        IReportServiceBean reportService = (IReportServiceBean) context
                .getApplicationContext().getBean(lCamelID);
        LOG.debug("{} ReportService Bean is injected -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, reportService);
        JSONObject finalResponseJson = (JSONObject) reportService.callService(pMessage, context);
        pMessage.getResponseObject().setResponseJson(finalResponseJson);

    }
}
