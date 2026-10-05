package com.iexceed.appzillon.services;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IConversationalUI;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.spring.SpringCamelContext;

public class ConversationalUIService {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS, ConversationalUIService.class.getName());
    SpringCamelContext context = ExternalServicesRouter.getCamelContext();

    public void getFirstCnvUIDlg(Message pMessage) {
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CONVERSATIONAL_UI);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getNextCnvUIDlg(Message pMessage) {
        LOG.debug("Inside getNextCnvUIDlg");
        JSONObject jsonReq = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_REQUEST);
        JSONObject screenData = jsonReq.getJSONObject(ServerConstants.CNVUI_SCREEN_DATA);
        String dlgId = jsonReq.getString(ServerConstants.DLG_ID);
        String cnvUIId = jsonReq.getString(ServerConstants.APPZILLON_CNVUI_ID);
        String appId = pMessage.getHeader().getAppId();
        JSONObject jsonReqToRuleBean = new JSONObject();
        jsonReqToRuleBean.put(ServerConstants.CNVUI_SCREEN_DATA, screenData);
        jsonReqToRuleBean.put(ServerConstants.DLG_ID, dlgId);
        LOG.debug("fetching updated dlgId from cnv UI rule bean for cnvUIId : {}  and dlgId : {}", cnvUIId, dlgId);
        IConversationalUI conversationalUI = (IConversationalUI) context.getApplicationContext()
                .getBean(appId + "_" + cnvUIId + "_" + ServerConstants.CNVUI_RULES_BEAN);
        JSONObject jsonRespFromRuleBean = conversationalUI.processDlgId(pMessage, jsonReqToRuleBean);
        String ruleBeanDlgId = jsonRespFromRuleBean.getString(ServerConstants.DLG_ID);
        LOG.debug("dlgId from the rule bean is : {}", ruleBeanDlgId);
        if (!dlgId.equals(ruleBeanDlgId)) {
            jsonReq.put(ServerConstants.DLG_ID_FROM_RULE_BEAN, ruleBeanDlgId);
        }
        jsonReq.put(ServerConstants.CNVUI_SCREEN_DATA,
                jsonRespFromRuleBean.getJSONObject(ServerConstants.CNVUI_SCREEN_DATA));
        pMessage.getRequestObject().getRequestJson().put(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_REQUEST, jsonReq);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CONVERSATIONAL_UI);
        DomainStartup.getInstance().processRequest(pMessage);
        pMessage.getRequestObject().getRequestJson().put(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_REQUEST, jsonReq);

    }

}
