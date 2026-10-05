package com.iexceed.appzillon.impl;

import com.iexceed.appzillon.iface.ITranslationBean;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class TranslationBeanImpl implements ITranslationBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    TranslationBeanImpl.class.getName());

    @Override
    public String getParamValue(String paramKey) {
        LOG.info("{} Translation Bean Implementation has to be provided by the user", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        return paramKey;
    }

}
