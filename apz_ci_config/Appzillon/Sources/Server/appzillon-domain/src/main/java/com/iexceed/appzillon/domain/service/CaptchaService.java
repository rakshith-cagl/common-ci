package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsmiCaptchaDtls;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiCaptchaDtlsRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.SequenceGenerator;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;

@Named("CaptchaService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class CaptchaService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            CaptchaService.class.toString());

    @Inject
    TbAsmiCaptchaDtlsRepository cAsmiCaptchaDtlsRepository;
    @Inject
    TbAsmiIntfMasterRepository cAsmiIntfMasterRepo;

    public void persistCaptcha(Message pMessage) {
        LOG.debug("{} persisting generated Captcha String", ServerConstants.LOGGER_PREFIX_DOMAIN);
        TbAsmiCaptchaDtls asmiCaptchaDtlsOpt = null;
        TbAsmiCaptchaDtls asmiCaptchaDtls = null;
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_REQUEST);
        String appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
        String interfaceId = requestJson.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
        String sessionId = requestJson.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID);

        if (requestJson.has(ServerConstants.CAPTCHA_REF)) {
            asmiCaptchaDtlsOpt = cAsmiCaptchaDtlsRepository
                    .findAsmiCaptchaByRefNo(Long.parseLong(requestJson.getString(ServerConstants.CAPTCHA_REF)));
            if (asmiCaptchaDtlsOpt != null)
                asmiCaptchaDtls = asmiCaptchaDtlsOpt;
            if (asmiCaptchaDtls != null) {
                Timestamp createTs = new Timestamp(System.currentTimeMillis());
                asmiCaptchaDtls.setCaptchaRef(Long.parseLong(requestJson.getString(ServerConstants.CAPTCHA_REF)));
                asmiCaptchaDtls.setCreateTs(createTs);
            }
        } else {
            Timestamp createTs = new Timestamp(System.currentTimeMillis());
            asmiCaptchaDtls = new TbAsmiCaptchaDtls();
            asmiCaptchaDtls.setCreateTs(createTs);
            asmiCaptchaDtls.setVersionNo(1L);
        }
        String captchaString = null;
        if (asmiCaptchaDtls == null) {
            asmiCaptchaDtls = new TbAsmiCaptchaDtls();
        }
        if (requestJson.has(ServerConstants.CAPTCHA_STRING)) {
            captchaString = requestJson.getString(ServerConstants.CAPTCHA_STRING);
            captchaString = HashUtils.hashSHA256(captchaString, appId + interfaceId);
            asmiCaptchaDtls.setCaptchaString(captchaString);
        }
        String audioCaptcha = null;
        if (requestJson.has(ServerConstants.AUDIO_CAPTCHA)) {
            audioCaptcha = requestJson.getString(ServerConstants.AUDIO_CAPTCHA);
            audioCaptcha = HashUtils.hashSHA256(audioCaptcha, appId + interfaceId);
            asmiCaptchaDtls.setAudioCaptcha(audioCaptcha);
        }
        asmiCaptchaDtls.setAppId(appId);
        asmiCaptchaDtls.setCaptchaRef(SequenceGenerator.getInstance().nextId());
        asmiCaptchaDtls.setInterfaceId(interfaceId);
        asmiCaptchaDtls.setSessionId(sessionId);
        asmiCaptchaDtls.setCaptchaStatus("NP");

        cAsmiCaptchaDtlsRepository.save(asmiCaptchaDtls);
        LOG.debug("{} Captcha Details persisted successfully", ServerConstants.LOGGER_PREFIX_DOMAIN);
        pMessage.getResponseObject()
                .setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_RESPONSE,
                        new JSONObject().put("captchaRef", asmiCaptchaDtls.getCaptchaRef())));

    }

    public void validateCaptcha(Message pMessage) {
        String refNo = pMessage.getHeader().getCaptchaRef();
        TbAsmiCaptchaDtls asmiCaptchaDtlsOpt = cAsmiCaptchaDtlsRepository.findAsmiCaptchaByRefNo(Long.parseLong(refNo));
        TbAsmiCaptchaDtls asmiCaptchaDtls = null;
        if (asmiCaptchaDtlsOpt != null)
            asmiCaptchaDtls = asmiCaptchaDtlsOpt;
        String dbCaptchaAnswer = null;
        String dbAudioCaptchaAnswer = "";
        if (asmiCaptchaDtls != null) {
            if (!asmiCaptchaDtls.getCaptchaStatus().equals("P")) {
                asmiCaptchaDtls.setCaptchaStatus("P");
                String captchaAnswer = pMessage.getHeader().getCaptchaString();
                // should comment captchaAnswer Hashing for testing in jmeter
                captchaAnswer = HashUtils.hashSHA256(captchaAnswer,
                        pMessage.getHeader().getAppId() + pMessage.getHeader().getInterfaceId());
                if (pMessage.getIntfDtls().getCaptchaType().equals(ServerConstants.BOTH)) {
                    dbAudioCaptchaAnswer = asmiCaptchaDtls.getAudioCaptcha();
                    dbCaptchaAnswer = asmiCaptchaDtls.getCaptchaString();
                } else if (pMessage.getIntfDtls().getCaptchaType().equals(ServerConstants.AUDIO)) {
                    dbCaptchaAnswer = asmiCaptchaDtls.getAudioCaptcha();
                } else {
                    dbCaptchaAnswer = asmiCaptchaDtls.getCaptchaString();
                }
                LOG.info("{} inside validateCaptcha() Captcha details found", ServerConstants.LOGGER_PREFIX_DOMAIN);
                validateCaptcha(pMessage, asmiCaptchaDtls, dbCaptchaAnswer, dbAudioCaptchaAnswer, captchaAnswer);
                cAsmiCaptchaDtlsRepository.save(asmiCaptchaDtls);
            } else {
                String captchaStatus = ServerConstants.OTP_PROCESSED;
                JSONObject responseJson = new JSONObject();
                responseJson.put(ServerConstants.CAPTCHA_STATUS, captchaStatus);
                pMessage.getResponseObject()
                        .setResponseJson(new JSONObject().put(ServerConstants.VALIDATE_CAPTCHA_RESPONSE, responseJson));
            }
        } else {
            String captchaStatus = ServerConstants.FAILURE;
            JSONObject responseJson = new JSONObject();
            responseJson.put(ServerConstants.CAPTCHA_STATUS, captchaStatus);
            pMessage.getResponseObject()
                    .setResponseJson(new JSONObject().put(ServerConstants.VALIDATE_CAPTCHA_RESPONSE, responseJson));
        }
    }

    private void validateCaptcha(Message pMessage, TbAsmiCaptchaDtls asmiCaptchaDtls, String dbCaptchaAnswer, String dbAudioCaptchaAnswer, String captchaAnswer) {
        if (dbCaptchaAnswer.equals(captchaAnswer) || dbAudioCaptchaAnswer.equals(captchaAnswer)) {
            Timestamp validateTs = new Timestamp(System.currentTimeMillis());
            asmiCaptchaDtls.setValidateTs(validateTs);
            String captchaStatus = ServerConstants.SUCCESS;
            JSONObject responseJson = new JSONObject();
            responseJson.put(ServerConstants.CAPTCHA_STATUS, captchaStatus);
            pMessage.getResponseObject().setResponseJson(
                    new JSONObject().put(ServerConstants.VALIDATE_CAPTCHA_RESPONSE, responseJson));
            LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Captcha asmiCaptchaDtls :" + asmiCaptchaDtls);
        } else {
            String captchaStatus = ServerConstants.FAILURE;
            JSONObject responseJson = new JSONObject();
            responseJson.put(ServerConstants.CAPTCHA_STATUS, captchaStatus);
            pMessage.getResponseObject().setResponseJson(
                    new JSONObject().put(ServerConstants.VALIDATE_CAPTCHA_RESPONSE, responseJson));
            LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Captcha asmiCaptchaDtls :" + asmiCaptchaDtls);
        }
    }

}
