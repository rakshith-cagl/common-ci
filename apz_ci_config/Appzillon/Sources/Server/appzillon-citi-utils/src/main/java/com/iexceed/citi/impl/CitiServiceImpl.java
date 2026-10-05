package com.iexceed.citi.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.service.Parser;
import com.iexceed.appzillon.domain.service.RenderAppzillonDefinitionJson;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.citi.iface.ICitiService;
import com.iexceed.appzillon.utils.ServerConstants;

import static com.iexceed.utils.Constants.RENDER_APPZILLON_DEFINITION_JSON;

public class CitiServiceImpl implements ICitiService {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_PREFIX_CITI, CitiServiceImpl.class.toString());


    @Override
    public void generateAppzillonJson(Message pMessage) {
        LOG.info(ServerConstants.LOGGER_PREFIX_CITI + "Generating Appzillon JSON");
        ((RenderAppzillonDefinitionJson) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                .getBean(RENDER_APPZILLON_DEFINITION_JSON)).buildAppResponseJson(pMessage);
    }

    @Override
    public void persistAppScreenJson(Message pMessage) {
        LOG.info(ServerConstants.LOGGER_PREFIX_CITI + "Persisting Appzillon Screen JSON");
        ((RenderAppzillonDefinitionJson) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                .getBean(RENDER_APPZILLON_DEFINITION_JSON)).persistAppScreenJson(pMessage);

    }

    @Override
    public void parseProductJson(Message pMessage) {
        LOG.info(ServerConstants.LOGGER_PREFIX_CITI + "Parsing  Citi Product JSON");
        ((Parser) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                .getBean("Parser")).parseCitiProjectJson(pMessage);
    }

    @Override
    public void parseWidgetJson(Message pMessage) {
        LOG.info(ServerConstants.LOGGER_PREFIX_CITI + "Parsing  Citi Widget JSON");
        ((RenderAppzillonDefinitionJson) DomainStartup.getInstance().getSpringContext().getAutowireCapableBeanFactory()
                .getBean(RENDER_APPZILLON_DEFINITION_JSON)).parseCitiWidgetJson(pMessage);
    }


}
