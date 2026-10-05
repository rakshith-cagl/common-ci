package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.repository.admin.TbAstpLdRecsRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

import static com.iexceed.appzillon.utils.ServerConstants.LOGGER_DOMAIN;
import static com.iexceed.appzillon.utils.ServerConstants.TRANSACTION_APPZILLON_ADMIN;

@Named(ServerConstants.SERVICE_LD_RECS)
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class LdRecsService {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(LOGGER_DOMAIN,
            LdRecsService.class.getName());
    @Inject
    private TbAstpLdRecsRepository tbAstpLdRecsRepo;

    public void deleteLdrecs(Message pMessage) {
        LOG.debug("{} Deleting payload details from LdRecs.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("deleteLdRecsRequest");
        String reqLdRefNo = jsonRequest.getString("ReqLdRefNo");
        tbAstpLdRecsRepo.deleteLDRecs(reqLdRefNo);
        pMessage.getResponseObject().setResponseJson(new JSONObject().put("deleteLdRecsResponse", new JSONObject().put(ServerConstants.STATUS, ServerConstants.RESP_BODY_STATUS_SUCCESS)));

    }
}
