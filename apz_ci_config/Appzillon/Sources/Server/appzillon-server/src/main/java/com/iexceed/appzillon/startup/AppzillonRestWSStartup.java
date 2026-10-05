package com.iexceed.appzillon.startup;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import org.slf4j.MDC;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.iexceed.appzillon.utils.ServerConstants.*;

public class AppzillonRestWSStartup implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, AppzillonRestWSStartup.class.getName());

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        MDC.put(LOG_ROUTER, "AppStartUp/Shutdown");
        //log pattern changes
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE);
        LOG.info("{} Stopping Appzillon Cache....", ServerConstants.LOGGER_PREFIX_RESTFULL);
        destory();
        LOG.info("{} Appzillon Cache manager is stopped....", ServerConstants.LOGGER_PREFIX_RESTFULL);
        MDC.clear();
    }

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        MDC.put(LOG_ROUTER, "AppStartUp/StartUp");
        //log pattern changes
        MDC.put(APPID, APPID_VALUE);
        MDC.put(OSTYPE, OSTYPE_VALUE);
        MDC.put(TXNREF, TXNREF_VALUE);
        MDC.put(USERID, USERID_VALUE);

        init(contextEvent.getServletContext());
        MDC.remove(MESSAGE_HEADER_USER_ID);
        MDC.remove(LOG_ROUTER);
        //log pattern changes
        MDC.remove(APPID);
        MDC.remove(OSTYPE);
        MDC.remove(TXNREF);
        MDC.remove(USERID);
    }

    public void init(ServletContext servletContxt) {
        LOG.info("{} Starting Server Processing", ServerConstants.LOGGER_PREFIX_RESTFULL);
        StartServerProcess.getInstance().initializeCore(servletContxt);
        LOG.info("{} Completed Server Processing", ServerConstants.LOGGER_PREFIX_RESTFULL);
    }

    public void destory() {
        LOG.debug("Destroying context");
    }


}
