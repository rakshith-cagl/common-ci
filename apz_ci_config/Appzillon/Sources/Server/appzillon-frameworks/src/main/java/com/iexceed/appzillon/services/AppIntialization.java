package com.iexceed.appzillon.services;


import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.web.context.WebApplicationContext;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AppIntialization {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, AppIntialization.class.getName());

    private static Map<String, String> appsIntializationStatus = new HashMap<>();
    private static boolean isBundledAppCamelContext = false;

    public static Map<String, String> getAppsIntializationStatus() {
        return appsIntializationStatus;
    }

    public static void setAppsIntializationStatus(Map<String, String> appsIntializationStatus) {
        AppIntialization.appsIntializationStatus = appsIntializationStatus;
    }

    public static void intializationOfAppAtRuntime(Message pMessage) {
        WebApplicationContext context = FrameworksStartup.getInstance().getWebAppContext();
        String appId = pMessage.getHeader().getAppId();
        String pathToXmlFiles = "";
        if (Utils.isNotNullOrEmpty(Logger.propertiesPath)) {
            pathToXmlFiles = String.format("%s/%s/%s", Logger.propertiesPath, appId, ServerConstants.META_INF_SPRING);
        } else {
            pathToXmlFiles = ServerConstants.META_INF_SPRING;
        }
        LOG.debug("{} Adding new app by loading xml beans from path , {}", ServerConstants.LOGGER_FRAMEWORKS, pathToXmlFiles);
        String[] files = new String[]{ServerConstants.CAMEL_CONTEXT_XML, ServerConstants.SMS_SPRING_XML,
                ServerConstants.NOTIFICATION_XML};
        for (String s : files) {
            if (s.equalsIgnoreCase(ServerConstants.CAMEL_CONTEXT_XML) && isBundledAppCamelContext) {
                LOG.debug("{} app-camel-context.xml is already loaded once,skipping it now.", ServerConstants.LOGGER_FRAMEWORKS);
            } else {
                AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
                XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(registry);
                xmlReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
                String filePath = pathToXmlFiles + s;
                LOG.debug("{} Loading beans from xml file: {}", ServerConstants.LOGGER_FRAMEWORKS, filePath);
                try (InputStream is = AppIntialization.class.getClassLoader().getResourceAsStream(filePath)) {
                    xmlReader.loadBeanDefinitions(new InputSource(is));
                } catch (BeanDefinitionStoreException | IOException e) {
                    LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
                }
            }
        }

        // Flag for non bundled case to load app-camel-context only once.
        if (!Utils.isNotNullOrEmpty(Logger.propertiesPath)) {
            isBundledAppCamelContext = true;
        }

        // Updating interace defs from properties file.
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_INTERFACE_MASTER_UPDATE);
        DomainStartup.getInstance().processRequest(pMessage);

        // Loading interfaces from database for new app.
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_INTERFACE_MASTER);
        DomainStartup.getInstance().processRequest(pMessage);

        // app is intialized.
        appsIntializationStatus.put(appId, ServerConstants.YES);
    }

}
