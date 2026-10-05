package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAppzillonReport;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Vinod Rawat
 */
public class ReportHandler implements IHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            ReportHandler.class.getName());
    private IAppzillonReport cReporter;

    @Override
    public void handleRequest(Message pMessage) {

        String pRequestIntfID = pMessage.getHeader().getInterfaceId();
        LOG.info("{} Routing to Appzillon Report Impl", ServerConstants.LOGGER_PREFIX_SMS);
        if (ServerConstants.INTERFACE_ID_SEARCH_TXN_LOG.equalsIgnoreCase(pRequestIntfID)) {
            cReporter.searchTxnLogging(pMessage);

        } else if (ServerConstants.INTERFACE_ID_LOGIN_REPORT.equalsIgnoreCase(pRequestIntfID)) {
            cReporter.getLoginReoport(pMessage);

        } else if (ServerConstants.INTERFACE_ID_APP_USAGE_REPORT.equalsIgnoreCase(pRequestIntfID)) {
            cReporter.getAppUsageReport(pMessage);

        } else if (ServerConstants.INTERFACE_ID_GET_REQ_RESP.equalsIgnoreCase(pRequestIntfID)) {
            cReporter.getReqResp(pMessage);

        } else if (ServerConstants.INTERFACE_ID_MSG_STATS.equalsIgnoreCase(pRequestIntfID)) {
            cReporter.getMsgStatDetails(pMessage);
        } else if (ServerConstants.INTERFACE_ID_CUSTOMER.equalsIgnoreCase(pRequestIntfID)) {
            cReporter.getCustomerOverview(pMessage);
        } else if (ServerConstants.INTERFACE_ID_CUSTOMER_LOCATION.equalsIgnoreCase(pRequestIntfID)) {
            cReporter.getCustomerLocationDetail(pMessage);
        } else if (ServerConstants.INTERFACE_ID_CUSTOMER_DETAILS.equalsIgnoreCase(pRequestIntfID)) {
            cReporter.getCustomerDetailsReport(pMessage);
        }

    }

    public IAppzillonReport getCReporter() {
        return cReporter;
    }

    public void setCReporter(IAppzillonReport cReporter) {
        this.cReporter = cReporter;
    }

}
