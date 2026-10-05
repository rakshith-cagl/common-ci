package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAslgJmsReqResp;
import com.iexceed.appzillon.domain.entity.TbAsmiLovQueries;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAslgJMSReqRespRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsmiLovQueriesRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

import static com.iexceed.appzillon.domain.utils.Constants.DOUBLE_BRACES;

@Named("frameworksService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class FrameWorkService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getDomainLogger(ServerConstants.LOGGER_DOMAIN,
                    FrameWorkService.class.getName());
    @Inject
    TbAslgJMSReqRespRepository cJMSLogRepository;
    @Inject
    TbAsmiIntfMasterRepository cAsmiIntfMasterRepository;
    @Inject
    TbAsmiLovQueriesRepository cAsmiFrmiLovQueriesRepository;

    public void insertJMSRequest(Message pMessage) {
        String ljmstranactionId = null;
        try {
            LOG.debug("{} In insertJMSRequest", ServerConstants.LOGGER_PREFIX_DOMAIN);
            JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
            TbAslgJmsReqResp frmiJmsReqRespLog = new TbAslgJmsReqResp();
            frmiJmsReqRespLog.setAppId(pMessage.getHeader().getAppId());
            frmiJmsReqRespLog.setInterfaceId(pMessage.getHeader().getInterfaceId());
            String msgId = mRequest.getString(ServerConstants.JMS_MSG_ID);
            String defaultResponse = "{\"status\":\"Under Process\",\"correlation ID\":\"" + msgId + "\"}";
            if (Utils.isNullOrEmpty(msgId)) {
                frmiJmsReqRespLog
                        .setMsgId("JMS");
            } else {
                frmiJmsReqRespLog
                        .setMsgId(msgId);
            }
            frmiJmsReqRespLog
                    .setMsgReceived(defaultResponse);
            frmiJmsReqRespLog
                    .setMsgSent(mRequest
                            .getString(ServerConstants.JMS_REQ_PAYLOAD));
            frmiJmsReqRespLog
                    .setMsgStatus(ServerConstants.JMS_STATUS_PENDING);
            frmiJmsReqRespLog.setReceivedDate(null);
            frmiJmsReqRespLog
                    .setReqQ(mRequest
                            .getString(ServerConstants.JMS_REQ_QUEUE));
            frmiJmsReqRespLog
                    .setReqType(mRequest
                            .getString(ServerConstants.JMS_REQ_TYPE));
            frmiJmsReqRespLog
                    .setResType(mRequest
                            .getString(ServerConstants.JMS_RESP_TYPE));
            frmiJmsReqRespLog.setRespDesc(null);
            frmiJmsReqRespLog.setRespQ(null);
            frmiJmsReqRespLog.setSentDate(new Date());
            cJMSLogRepository.save(frmiJmsReqRespLog);
            if (Utils.isNullOrEmpty(msgId)) {
                frmiJmsReqRespLog.setMsgId("JMS_"
                        + frmiJmsReqRespLog.getTransId());
                defaultResponse = "{\"status\":\"Under Process\",\"correlation ID\":\"" + frmiJmsReqRespLog.getMsgId() + "\"}";
                frmiJmsReqRespLog
                        .setMsgReceived(defaultResponse);
                cJMSLogRepository.save(frmiJmsReqRespLog);
            }

            ljmstranactionId = String.valueOf(frmiJmsReqRespLog.getTransId());

            LOG.debug("{} Message JMS Trans IDs : {} ", ServerConstants.LOGGER_PREFIX_DOMAIN, ljmstranactionId);
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");
            throw ldomainException;
        }
        pMessage.getResponseObject().setResponseJson(
                new JSONObject().put("jmsTransactionId", ljmstranactionId));
    }

    public void updateJMSResponse(Message pMessage) {
        String lJMSresp = "MSG ID not Found in Database";
        try {
            JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
            TbAslgJmsReqResp frmiJmsReqResp = findBymsgId(mRequest
                    .getString(ServerConstants.JMS_MSG_CORRELATION_ID));
            if (frmiJmsReqResp != null) {
                frmiJmsReqResp
                        .setMsgReceived(mRequest
                                .getString(ServerConstants.JMS_RESP_MSG));
                frmiJmsReqResp
                        .setMsgStatus(ServerConstants.JMS_STATUS_SUCCESS);
                frmiJmsReqResp.setReceivedDate(new Date());

                frmiJmsReqResp.setRespDesc("success");
                frmiJmsReqResp
                        .setRespQ(mRequest
                                .getString(ServerConstants.JMS_RESP_QUEUE));
                frmiJmsReqResp.setSentDate(new Date());

                cJMSLogRepository.save(frmiJmsReqResp);

                lJMSresp = "successfully updated";
            }

        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");
            throw ldomainException;
        }
        pMessage.getResponseObject().setResponseJson(
                new JSONObject().put("jmsStatus", lJMSresp));
    }

    /**
     * @param pCorrelationId
     * @return
     */

    public TbAslgJmsReqResp findBymsgId(String pCorrelationId) {

        return cJMSLogRepository.findBymsgId(pCorrelationId);
    }

    public void fetchJMSMsgByCorelationId(Message pMessage) {
        String ljmsMsg = "";
        try {
            JSONObject mRequest = pMessage.getRequestObject().getRequestJson();
            TbAslgJmsReqResp frmiJmsReqRespLog = cJMSLogRepository
                    .findBymsgId(mRequest
                            .getString(ServerConstants.JMS_MSG_CORRELATION_ID));
            ljmsMsg = frmiJmsReqRespLog.getMsgReceived();
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");
            throw ldomainException;
        }
        pMessage.getResponseObject().setResponseJson(
                new JSONObject().put("JmsResponseJSON", ljmsMsg));
    }

    public void fetchQueryString(Message pMessage) throws DomainException {
        LOG.debug("{} inside fetchQueryString()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {

            TbAsmiLovQueries asmiLovQueries = cAsmiFrmiLovQueriesRepository.fetchQueryString(
                    pMessage.getHeader().getAppId(), pMessage.getRequestObject().getRequestJson().getString(ServerConstants.LOV_QUERY_ID));

            JSONObject jsonLovDetails = new JSONObject();
            jsonLovDetails.put(ServerConstants.LOV_QUERY_ID,
                    asmiLovQueries.getTbAsmiLovQueriesPK().getQueryId());
            jsonLovDetails.put(ServerConstants.LOV_QUERY_STRING,
                    asmiLovQueries.getQueryString());
            jsonLovDetails.put(ServerConstants.LOV_BIND_VAR_COLS,
                    asmiLovQueries.getBindvarCols());
            jsonLovDetails.put(ServerConstants.LOV_BIND_VAR_DATA_TYPE,
                    asmiLovQueries.getBindvarDataTypes());
            jsonLovDetails.put(ServerConstants.LOV_DATA_SOURCE,
                    asmiLovQueries.getDbJndiName());
            jsonLovDetails.put(ServerConstants.LOV_FILTER_VAR_COLS,
                    asmiLovQueries.getFilterCols());
            jsonLovDetails.put(ServerConstants.LOV_ORDER_BY_COLS,
                    asmiLovQueries.getOrderbyCol());
            jsonLovDetails.put(ServerConstants.LOV_QUERY_TYPE,
                    asmiLovQueries.getQueryType());
            jsonLovDetails.put(ServerConstants.LOV_SESSION_REQD,
                    asmiLovQueries.getSessionReq());
            pMessage.getResponseObject().setResponseJson(jsonLovDetails);
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException ldomainException = DomainException.getDomainExceptionInstance();
            ldomainException.setMessage(ldomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_000));
            ldomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            ldomainException.setPriority("1");
            throw ldomainException;
        }
    }
}
