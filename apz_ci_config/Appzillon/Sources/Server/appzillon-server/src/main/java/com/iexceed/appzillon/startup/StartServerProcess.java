package com.iexceed.appzillon.startup;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.notification.NotificationStartup;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AuthTokenUtil;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.mysema.commons.lang.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public final class StartServerProcess {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES,
                    StartServerProcess.class.toString());
    private static StartServerProcess process;
    private static boolean isInstrumentKeyLoaded = false;
    private boolean initialized = false;

    private StartServerProcess() {

    }

    public static StartServerProcess getInstance() {
        if (process == null) {
            process = new StartServerProcess();
        }
        return process;
    }

    private static void loadInstrumentationKey() {
        LOG.debug("Loading instrumentation key");
        String instrumentationKeyValue = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.INSTRUMENTATION_KEY);

        if (Utils.isNotNullOrEmpty(instrumentationKeyValue)) {
            LOG.debug("Fetch instrumentation key from key vault");
            String authToken = AuthTokenUtil.getAuthTokenKeyVault(ServerConstants.SERVER_PROP_FILE_CONSTANT);
            String secret = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(instrumentationKeyValue), authToken);
            Assert.isTrue(secret != null, "Instrumentation key cannot be null or empty");
            System.setProperty(ServerConstants.INSTRUMENTATION_KEY, secret);
            isInstrumentKeyLoaded = true;
        } else {
            LOG.error("Instrumentation key value not found");
        }

    }

    public void initializeCore(ServletContext servletContxt) {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(servletContxt);
        String ctxParamName = servletContxt.getInitParameter(ServerConstants.CONTEXT_PARAM_NAME);
        LOG.info("{} ctxParamName : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, ctxParamName);
        PropertyUtils.setCtxParam(ctxParamName);

        //change - Externalizing the prop file when cloud provider is not none.
        String cloudProvider = servletContxt.getInitParameter(ServerConstants.CONTEXT_PARAM_CLOUD_PROVIDER);
        PropertyUtils.setCloudProvider(cloudProvider);

        LOG.info("{} Cloud Provider : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, cloudProvider);
        LOG.debug("{} WebApplicationContext {}", ServerConstants.LOGGER_PREFIX_RESTFULL, wac);

        if (!initialized) {

            LOG.info("{} Initialising server properties", ServerConstants.LOGGER_PREFIX_RESTFULL);
            PropertyUtils.loadServerProperties();

            String cloudName = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.CLOUD_LOGGING);
            if (!isInstrumentKeyLoaded && (Utils.isNotNullOrEmpty(cloudName) && ServerConstants.CLOUD_AZURE.equalsIgnoreCase(cloudName))) {
                loadInstrumentationKey();
            }

            LOG.info("{} Initialising Domain....", ServerConstants.LOGGER_PREFIX_RESTFULL);
            DomainStartup.getInstance().init(wac);
            LOG.info("{} Initialising Workflow....", ServerConstants.LOGGER_PREFIX_RESTFULL);
            WorkflowStartup.getInstance().init(wac);
            LOG.info("{} Initialising SMS....", ServerConstants.LOGGER_PREFIX_RESTFULL);
            SmsStartup.init(wac);
            LOG.info("{} Initialising Notification....", ServerConstants.LOGGER_PREFIX_RESTFULL);
            NotificationStartup.getInstance().init(wac);
            LOG.info("{} Initialising Frameworks....", ServerConstants.LOGGER_PREFIX_RESTFULL);
            FrameworksStartup.init(wac);
            initialized = true;
        }
        LOG.info("{} Core Initialized", ServerConstants.LOGGER_PREFIX_RESTFULL);
    }
}
