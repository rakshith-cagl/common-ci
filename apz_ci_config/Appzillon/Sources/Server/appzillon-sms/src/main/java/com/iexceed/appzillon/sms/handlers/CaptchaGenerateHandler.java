package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.ICaptchaGenerate;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

public class CaptchaGenerateHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            CaptchaGenerateHandler.class.getName());
    protected ICaptchaGenerate cCaptchaGenerate;

    @Override
    public void handleRequest(Message pMessage) {
        String mRequesttype = pMessage.getHeader().getInterfaceId();
        LOG.info("{} Routing to CaptchaGenerateImpl", ServerConstants.LOGGER_PREFIX_SMS);
        if (ServerConstants.INTERFACE_ID_GENERATE_CAPTCHA.equals(mRequesttype)) {
            cCaptchaGenerate.handleCaptchaGenerate(pMessage);
        } else if (pMessage.getHeader().getServiceType().equalsIgnoreCase(ServerConstants.VALIDATE_CAPTCHA)) {
            cCaptchaGenerate.handleCaptchaValidation(pMessage);
        }
    }

    public ICaptchaGenerate getcCaptchaRefresh() {
        return cCaptchaGenerate;
    }

    public void setcCaptchaGenerate(ICaptchaGenerate cCaptchaGenerate) {
        this.cCaptchaGenerate = cCaptchaGenerate;
    }

}
