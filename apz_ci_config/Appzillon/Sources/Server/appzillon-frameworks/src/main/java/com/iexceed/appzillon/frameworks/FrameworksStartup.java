package com.iexceed.appzillon.frameworks;

import com.iexceed.appzillon.handlers.FrameworksRoutingHandler;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.web.context.WebApplicationContext;

public class FrameworksStartup {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
            ServerConstants.LOGGER_FRAMEWORKS,
            FrameworksStartup.class.getName());
    private static FrameworksStartup frameworksStartup;
    private static SpringCamelContext springCamelContext;
    private static ProducerTemplate producerTemplate;
    private static WebApplicationContext webAppContext;

    private FrameworksStartup() {

    }

    public static void init(WebApplicationContext wac) {
        webAppContext = wac;
        LOG.info("{} Initializing Frameworks", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        getInstance();
        try {
            getCamelContext().start();
        } catch (Exception ex) {
            LOG.error("Exception", ex);
        }

        LOG.info("{} Frameworks Initialized", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
    }

    public static WebApplicationContext getWebAppContext() {
        return webAppContext;
    }

    private static WebApplicationContext createApplicationContext() {
        return webAppContext;
    }

    public static FrameworksStartup getInstance() {

        if (frameworksStartup == null) {
            frameworksStartup = new FrameworksStartup();
        }

        return frameworksStartup;

    }

    public static SpringCamelContext getCamelContext() {

        if (springCamelContext == null) {
            springCamelContext = (SpringCamelContext) createApplicationContext()
                    .getBean("appzillonframeworks");
        }

        return springCamelContext;

    }

    public static ProducerTemplate getProducerTemplate() {

        if (producerTemplate == null) {
            producerTemplate = (ProducerTemplate) springCamelContext
                    .getApplicationContext().getBean("producerTemplate");
        }
        return producerTemplate;

    }

    public void processRequest(Message pMessage)
            throws ExternalServicesRouterException, InvalidPayloadException,
            ClassNotFoundException, JSONException {
        LOG.info("{} ***************************** FrameworksStartup.processRequest * Start ******************************************", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        LOG.debug("{} FrameworksStartup.processRequest - p_headerMap: {} and p_inputjsonstr: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader(), pMessage.getRequestObject().getRequestJson());

        FrameworksRoutingHandler servicesRouter = new FrameworksRoutingHandler();
        servicesRouter.serviceRequestHandler(pMessage);

        LOG.info("{} ***************************** FrameworksStartup.processRequest * END ******************************************\n\n", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

    }

    public void stopCamelContext() {
        try {
            getCamelContext().stop();
        } catch (Exception ex) {
            LOG.error("Exception", ex);
        }
    }


}
