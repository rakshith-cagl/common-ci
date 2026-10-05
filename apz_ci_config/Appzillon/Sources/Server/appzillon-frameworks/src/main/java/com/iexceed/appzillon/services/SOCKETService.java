package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.SocketDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import org.apache.camel.spring.SpringCamelContext;
import org.owasp.encoder.Encode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class SOCKETService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    SOCKETService.class.getName());
    protected SocketDetails socketDet = null;

    @Override
    public Object callService(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) {
        OutputStream out = null;
        InputStream in = null;
        String dSource = null;
        int timeOut = 120;
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String payLoad = null;
        try {
            socketDet = getSocketDetails(interfaceId, appId, pContext);
            timeOut = socketDet.getTimeOut();

            LOG.info("{} connecting to {} on port {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, socketDet.getEndPointURL(), socketDet.getPortNo());
            try (Socket socket = new Socket(socketDet.getEndPointURL(), Integer.parseInt(socketDet.getPortNo()))) {
                LOG.debug("{} Setting Time out: {}, to millisecond: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, timeOut, (timeOut * 1000));
                socket.setSoTimeout(timeOut * 1000);
                pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad,
                        socketDet.getAutoGenElementMap(), socketDet.getTranslationElementMap());
                pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
                LOG.debug("{} After Appending Request Json With MaskedId : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());

                payLoad = (String) buildRequest(pMessage, pRequestPayLoad.toString(), pContext);

                out = socket.getOutputStream();

                Utils.setExtTime(pMessage, "S");
                out.write(Encode.forHtmlContent(payLoad).getBytes());
                Utils.setExtTime(pMessage, "E");
                out.flush();

                String str = null;

                in = socket.getInputStream();
                int size = socket.getReceiveBufferSize();
                LOG.info("{} buffer size {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, size);
                byte[] resbytes = new byte[size];
                LOG.info("{} response byte array {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, resbytes);
                int length = in.read(resbytes);
                LOG.info("{} length {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, length);
                str = new String(resbytes, 0, length);
                ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, str, payLoad);
                LOG.info("{} xmlResponse from external server {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, str);
                dSource = str;
            }

        } catch (NumberFormatException e) {
            LOG.error("{} Exception in port {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_054.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_054));
            exsrvcallexp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, e.getMessage(), payLoad);
            throw exsrvcallexp;
        } catch (UnknownHostException e) {
            LOG.error("{} UnknownHostException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, e.getMessage(), payLoad);
            throw exsrvcallexp;
        } catch (ConnectException e) {
            LOG.error("{} ConnectException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, e.getMessage(), payLoad);
            throw exsrvcallexp;
        } catch (SocketTimeoutException e) {
            LOG.error("{} SocketTimeoutException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, e.getMessage(), payLoad);
            throw exsrvcallexp;
        } catch (SocketException e) {
            LOG.error("{} SocketException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, e.getMessage(), payLoad);
            throw exsrvcallexp;
        } catch (IOException e) {
            LOG.error("{} IOException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_055.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_055));
            exsrvcallexp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, e.getMessage(), payLoad);
            throw exsrvcallexp;
        } finally {
            // closing resources
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOG.error("{} IOException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            }
        }
        dSource = (String) processResponse(pMessage, dSource, pContext);
        return new JSONObject(dSource);
    }

    public SocketDetails getSocketDetails(String pInterfaceID, String pAppId,
                                          SpringCamelContext pContext) {
        SocketDetails socketDetails = (SocketDetails) ExternalServicesRouter
                .injectBeanFromSpringContext(pAppId + "_" + pInterfaceID,
                        pContext);
        int timeOut = socketDetails.getTimeOut();
        /**
         * Below changes are made by Vinod as part of 
         * At app level, service time out should be configurable.
         * Appzillon 3.1 - 63 -- Start
         */
        if (timeOut == 0) {
            LOG.warn("{} Timeout value not configured will use default timeOut", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            timeOut = Integer.parseInt(PropertyUtils.getPropValue(pAppId, ServerConstants.DEFAULT_TIMEOUT).trim());
        }
        /** Appzillon 3.1 - 63 -- END */
        socketDetails.setTimeOut(timeOut);
        return socketDetails;

    }

    @Override
    public Object buildRequest(Message pMessage, Object pRequestPayLoad,
                               SpringCamelContext pContext) {
        LOG.debug("{} Inside SuperClass buildRequest with payload {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pRequestPayLoad);

        return pRequestPayLoad;
    }

    @Override
    public Object processResponse(Message pMessage, Object pResponse,
                                  SpringCamelContext pContext) {
        LOG.debug("{} Inside SuperClass processResponse with payload {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pResponse);
        return pResponse;
    }
}
