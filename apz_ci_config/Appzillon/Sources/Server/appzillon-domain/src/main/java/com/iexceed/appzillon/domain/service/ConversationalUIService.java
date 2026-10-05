package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScr;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScrPK;
import com.iexceed.appzillon.domain.entity.TbAsmiDlgMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiDlgMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiCnvUIScrRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiDlgMasterRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Named(ServerConstants.SERVICE_CONVERSATIONAL_UI)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class ConversationalUIService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            ConversationalUIService.class.getName());

    @Inject
    private TbAsmiDlgMasterRepository cAsmiDlgMasterRepository;

    @Inject
    private TbAsmiCnvUIScrRepository cAsmiCnvUIScrRepository;

    public void fetchFirstCnvUIDlg(Message pMessage) {
        LOG.debug("{} fetching first conversationaUI dialogue", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_REQUEST);

        String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String cnvUIId = requestJson.getString(ServerConstants.APPZILLON_CNVUI_ID);
        TbAsmiDlgMaster asmiDlgMaster = cAsmiDlgMasterRepository.findFirstDlg(appId, cnvUIId);
        String scrId = asmiDlgMaster.getScreenId();
        JSONObject screenDetails = fetchScreenDet(appId, scrId);
        JSONObject responseJson = new JSONObject();
        responseJson.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
        responseJson.put(ServerConstants.APPZILLON_CNVUI_ID, cnvUIId);
        responseJson.put(ServerConstants.DLG_ID, asmiDlgMaster.getId().getDlgId());
        responseJson.put(ServerConstants.RESP_DLG_ID, asmiDlgMaster.getRespDlgId());
        responseJson.put(ServerConstants.DLG_DESC, asmiDlgMaster.getDlgDesc());
        responseJson.put(ServerConstants.SCREEN_DETAILS, screenDetails);
        pMessage.getResponseObject().getResponseJson().put(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_RESPONSE,
                responseJson);
        cnvUITxnLog(pMessage);

    }

    public void fetchNextCnvUIDlg(Message pMessage) {
        LOG.debug("{} fetching next conversationaUI dialogue", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_REQUEST);

        String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String cnvUIId = requestJson.getString(ServerConstants.APPZILLON_CNVUI_ID);
        String dlgId = requestJson.getString(ServerConstants.DLG_ID);
        String txnRef = requestJson.getString(ServerConstants.TXN_REF);
        Optional<TbAsmiDlgMaster> asmiDlgMasterOpt;
        TbAsmiDlgMaster asmiDlgMaster;
        if (requestJson.has(ServerConstants.DLG_ID_FROM_RULE_BEAN)) {
            asmiDlgMasterOpt = cAsmiDlgMasterRepository.findById(new TbAsmiDlgMasterPK(appId, cnvUIId,
                    requestJson.getString(ServerConstants.DLG_ID_FROM_RULE_BEAN)));
            if (asmiDlgMasterOpt.isPresent()) {
                asmiDlgMaster = asmiDlgMasterOpt.get();
                requestJson.remove(ServerConstants.DLG_ID_FROM_RULE_BEAN);
            } else {
                LOG.error("{} No records found.",
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ds = DomainException.getDomainExceptionInstance();
                ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                ds.setCode(DomainException.Code.APZ_DM_008.toString());
                ds.setPriority("1");
                throw ds;
            }
        } else {
            asmiDlgMaster = cAsmiDlgMasterRepository.findNextDlg(appId, cnvUIId, dlgId);
        }
        String scrId = asmiDlgMaster.getScreenId();
        JSONObject screenDetails = fetchScreenDet(appId, scrId);
        JSONObject responseJson = new JSONObject();
        responseJson.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
        responseJson.put(ServerConstants.APPZILLON_CNVUI_ID, cnvUIId);
        responseJson.put(ServerConstants.TXN_REF, txnRef);
        responseJson.put(ServerConstants.DLG_ID, asmiDlgMaster.getId().getDlgId());
        responseJson.put(ServerConstants.RESP_DLG_ID, asmiDlgMaster.getRespDlgId());
        responseJson.put(ServerConstants.DLG_DESC, asmiDlgMaster.getDlgDesc());
        responseJson.put(ServerConstants.SCREEN_DETAILS, screenDetails);
        pMessage.getResponseObject().getResponseJson().put(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_RESPONSE,
                responseJson);
        cnvUITxnLog(pMessage);
    }

    private JSONObject fetchScreenDet(String appId, String scrId) {
        Optional<TbAsmiCnvUIScr> asmiCnvUIScr = cAsmiCnvUIScrRepository.findById(new TbAsmiCnvUIScrPK(appId, scrId));
        JSONObject screenDetails = new JSONObject();
        if (asmiCnvUIScr.isPresent()) {
            screenDetails.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, scrId);
            screenDetails.put(ServerConstants.SCREEN_DEF, asmiCnvUIScr.get().getScreenDef());
            screenDetails.put(ServerConstants.SCREEN_HTML, asmiCnvUIScr.get().getScreenHtml());
        } else {
            LOG.error("{} No records found.",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException ds = DomainException.getDomainExceptionInstance();
            ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            ds.setCode(DomainException.Code.APZ_DM_008.toString());
            ds.setPriority("1");
            throw ds;
        }
        return screenDetails;
    }

    private void cnvUITxnLog(Message pMessage) {
        LOG.debug("{} Logging cnv ui Transaction", ServerConstants.LOGGER_PREFIX_DOMAIN);
        pMessage.getHeader().setServiceType(ServerConstants.CNVUI_LOG_TRANSACTION);
        try {
            DomainStartup.getInstance().processRequest(pMessage);
        } catch (Exception e) {
            LOG.error("{} cnv UI txn log failed", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lException = DomainException.getDomainExceptionInstance();
            lException.setCode(DomainException.Code.APZ_DM_070.toString());
            lException.setMessage(lException.getDomainExceptionMessage(DomainException.Code.APZ_DM_070));
            lException.setPriority("1");
            LOG.error(ServerConstants.EXCEPTION, e);
            throw lException;
        }
        pMessage.getHeader().setServiceType("");
    }

}
