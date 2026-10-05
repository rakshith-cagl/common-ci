package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAppzillonReport;
import com.iexceed.appzillon.utils.ServerConstants;

public class ReportImpl implements IAppzillonReport {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS, ScreenMaintainerImpl.class.getName());

    public void getLoginReoport(Message pMessage) {
        LOG.debug("{} getLoginReoport", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_GENERATE_REPORTS);
        LOG.info("{} Routing to Domain getLoginReoport GENERATE REPORT SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getAppUsageReport(Message pMessage) {
        LOG.debug("{} getAppUsageReport", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_GENERATE_REPORTS);
        LOG.info("{} Routing to Domain getAppUsageReport GENERATE REPORT SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void searchTxnLogging(Message pMessage) {
        LOG.debug("{} searchTxnLogging", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_GENERATE_REPORTS);
        LOG.info("{} Routing to Domain searchTxnLogging GENERATE REPORT SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getReqResp(Message pMessage) {
        LOG.debug("{} getReqResp", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_GENERATE_REPORTS);
        LOG.info("{} Routing to Domain getReqResp GENERATE REPORT SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getMsgStatDetails(Message pMessage) {
        LOG.debug("{} get Message Statistics detail", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GENERATE_REPORTS);
        LOG.info("{} Routing to Domain getMsgStatDetails GENERATE REPORT SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getCustomerOverview(Message pMessage) {
        LOG.debug("{} get Customer Overview Service details", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GENERATE_REPORTS);
        LOG.info("{} Routing to Domain getCustomerOverview GENERATE REPORT SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getCustomerLocationDetail(Message pMessage) {
        LOG.debug("{} get Customer Location Service details", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GENERATE_REPORTS);
        LOG.info("{} Routing to Domain getCustomerLocationDetails GENERATE REPORT SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getCustomerDetailsReport(Message pMessage) {
        LOG.debug("{} get Customer Service detail", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GENERATE_REPORTS);
        LOG.info("{} Routing to Domain getCustomerDetailsReport GENERATE REPORT SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

}
