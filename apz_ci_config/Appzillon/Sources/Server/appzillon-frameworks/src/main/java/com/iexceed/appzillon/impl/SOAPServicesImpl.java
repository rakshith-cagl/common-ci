package com.iexceed.appzillon.impl;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IAddlServiceProcessorBean;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.spring.SpringCamelContext;

public class SOAPServicesImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    SOAPServicesImpl.class.getName());

    public void serviceRequestDispatcher(Message pMessage,
                                         SpringCamelContext pContext) throws ExternalServicesRouterException {

        LOG.info("{} SoapService Implementation Dispatching request to Service Bean to process the request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lAppId = pMessage.getHeader().getAppId();
        String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;

        LOG.debug("{} SoapService Application Id -: {}, InterfaceId -: {}, Service BeanId -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lAppId, lInterfaceId, lCamelID);

        IServicesBean soapService = (IServicesBean) pContext
                .getApplicationContext().getBean(lCamelID);
        LOG.debug("{} SoapService Bean is injected -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, soapService);
        String lAddlServiceProcessBeanID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE + ServerConstants.BEAN_APPEND_SERVICE_PROC;
        IAddlServiceProcessorBean addlServiceProcessorBean = (IAddlServiceProcessorBean) pContext.getApplicationContext().getBean(lAddlServiceProcessBeanID);
        //PreProcessor
        addlServiceProcessorBean.preProcessor(pMessage, pMessage.getRequestObject().getRequestJson(), pContext);

        JSONObject lResponse = (JSONObject) soapService.callService(pMessage,
                pMessage.getRequestObject().getRequestJson(), pContext);
        LOG.debug("{} SoapService Response received from the service is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lResponse.toString());
        pMessage.getResponseObject().setResponseJson(lResponse);
        // PostProcessor
        addlServiceProcessorBean.postProcessor(pMessage, pMessage.getResponseObject().getResponseJson(), pContext);
    }
}
