package com.iexceed.appzillon.rest;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import org.slf4j.MDC;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class USSDServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, USSDServlet.class.getName());


    private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        processRequest();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        processRequest();
    }

    protected void processRequest() {

        MDC.put("logRouter", "USSDStartup");

        //log pattern changes
        MDC.put("APPID", "APPID:");
        MDC.put("OSTYPE", "OSTYPE:");
        MDC.put("TXNREF", "TXNREF:");
        MDC.put("USERID", "USERID:");


        LOG.info("************************************* Start USSD Process ************************************************");

        LOG.info("************************************* End USSD Process **************************************************\n");

    }
}
