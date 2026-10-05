package com.iexceed.appzillon.utils.mail;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import org.apache.camel.Exchange;
import org.apache.camel.support.SynchronizationAdapter;
import org.slf4j.MDC;

import static com.iexceed.appzillon.utils.Constants.USER_ID;

public class AsyncMailListener extends SynchronizationAdapter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS, AsyncMailListener.class.getName());

    @Override
    public void onComplete(Exchange exchange) {
        setThreadContext(exchange);
        LOG.debug("{} Mail has been delivered successfully.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        Message m = (Message) exchange.getProperty("pMessage");
        Utils.setExtTime(m, "E");
        ServicesUtil.processMailTxn(m, ServerConstants.SUCCESS, "Mail has been delivered successfully.",
                exchange.getProperty("payload"));
    }

    @Override
    public void onFailure(Exchange exchange) {
        setThreadContext(exchange);
        LOG.error("{} Exception occurred while sending mail asynchronously. {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchange.getException());
        if (exchange.getException() instanceof javax.mail.AuthenticationFailedException) {
            LOG.error("{} MailService failed due to authentication failure. {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchange.getException());
        } else if (exchange.getException() instanceof javax.mail.internet.AddressException) {
            LOG.error("{} MailService failed due to missing domain name in mail address. {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchange.getException());
        } else if (exchange.getException() instanceof javax.mail.SendFailedException) {
            LOG.error("{} MailService failed due to incorrect mail address entered by users. {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exchange.getException());
        }
        LOG.error("{} Exception obscured while sending mail... {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                exchange.getException());
        Message m = (Message) exchange.getProperty("pMessage");
        Utils.setExtTime(m, "E");
        ServicesUtil.processMailTxn(m, ServerConstants.ERROR, exchange.getException().getMessage(),
                exchange.getProperty("payload"));

    }

    public void setThreadContext(Exchange exchange) {
        MDC.put("logRouter", exchange.getProperty("appId") + "/" + exchange.getProperty(USER_ID));

        //log pattern changes
        MDC.put("APPID", "APPID:" + (String) exchange.getProperty("appId"));
        MDC.put("OSTYPE", "OSTYPE:" + (String) exchange.getProperty("osType"));
        MDC.put("USERID", "USERID:" + (String) exchange.getProperty(USER_ID));
        MDC.put("TXNREF", "TXNREF:" + (String) exchange.getProperty("txnRef"));
    }

}
