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
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;

public class JMSServicesImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    JMSServicesImpl.class.getName());

    public void serviceRequestDispatcher(Message pMessage,
                                         SpringCamelContext context) throws ExternalServicesRouterException,
            InvalidPayloadException, ClassNotFoundException {

        LOG.info("{} JMSServices Implementation Dispatching request to Service Bean to process the request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lAppId = pMessage.getHeader().getAppId();
        String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
        LOG.debug("{} JMSServices Application Id -: {}, InterfaceId -: {}, Service BeanId -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lAppId, lInterfaceId, lCamelID);

        IServicesBean jmsService = (IServicesBean) context
                .getApplicationContext().getBean(lCamelID);
        LOG.debug("{} JMSServices Bean is injected -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jmsService);
        String lAddlServiceProcessBeanID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE + ServerConstants.BEAN_APPEND_SERVICE_PROC;
        IAddlServiceProcessorBean addlServiceProcessorBean = (IAddlServiceProcessorBean) context.getApplicationContext().getBean(lAddlServiceProcessBeanID);
        //PreProcessor
        addlServiceProcessorBean.preProcessor(pMessage, pMessage.getRequestObject().getRequestJson(), context);

        JSONObject lResponse = (JSONObject) jmsService.callService(pMessage,
                pMessage.getRequestObject().getRequestJson(), context);
        LOG.debug("{} JMSServices Response received from the service is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lResponse.toString());
        pMessage.getResponseObject().setResponseJson(lResponse);
        // PostProcessor
        addlServiceProcessorBean.postProcessor(pMessage, pMessage.getResponseObject().getResponseJson(), context);

    }

}
