package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.service.InterfaceMasterService;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.ICaptchaGenerate;
import com.iexceed.appzillon.sms.iface.ICaptchaProperties;
import com.iexceed.appzillon.sms.iface.ICaptchaProperties.CaptchaType;
import com.iexceed.appzillon.utils.ServerConstants;
import nl.captcha.Captcha;
import nl.captcha.audio.AudioCaptcha;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CaptchaGenerateImpl implements ICaptchaGenerate {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            CaptchaGenerateImpl.class.getName());

    @Override
    public void handleCaptchaGenerate(Message pMessage) {
        LOG.info("{} Handling Captcha Generate", ServerConstants.LOGGER_PREFIX_SMS);
        this.generateCaptcha(pMessage);
        LOG.debug("{} Captcha Generated and persisted successfully", ServerConstants.LOGGER_PREFIX_SMS);
    }

    public void generateCaptcha(Message pMessage) {
        JSONObject lrequestJson = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_REQUEST);
        String lresultCaptcha = null;
        String lresultAudioCaptcha = null;

        String interfaceCaptchaType = InterfaceMasterService.getInterfaceMasterMap()
                .get(lrequestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID))
                .get(lrequestJson.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)).getCaptchaType();

        ICaptchaProperties objICaptchaProperties = (ICaptchaProperties) SmsStartup.getInstance().getSpringContext()
                .getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_CAPTCHA_PROPERTIES);
        LOG.debug("{} Captcha Request : {}", ServerConstants.LOGGER_PREFIX_SMS, interfaceCaptchaType);
        if (interfaceCaptchaType.equals(ServerConstants.BOTH)) {
            lresultAudioCaptcha = generateAudioCaptcha(objICaptchaProperties, lrequestJson);
            lresultCaptcha = generateTextCaptcha(objICaptchaProperties, lrequestJson);
        } else if (interfaceCaptchaType.equals(ServerConstants.AUDIO)) {
            lresultAudioCaptcha = generateAudioCaptcha(objICaptchaProperties, lrequestJson);
        } else if (interfaceCaptchaType.equals(ServerConstants.TEXT)
                || interfaceCaptchaType.equals(ServerConstants.YES)) {
            lresultCaptcha = generateTextCaptcha(objICaptchaProperties, lrequestJson);
        }
        pMessage.getHeader().setServiceType(ServerConstants.PERSIST_CAPTCHA);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject lresponseJson = pMessage.getResponseObject().getResponseJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_RESPONSE);
        if (Utils.isNotNullOrEmpty(lresultAudioCaptcha)) {
            lresponseJson.put(ServerConstants.AUDIO_CAPTCHA, lresultAudioCaptcha);
        }
        lresponseJson.put(ServerConstants.CAPTCHA_STRING, lresultCaptcha);

    }

    private String generateTextCaptcha(ICaptchaProperties iCaptchaProperties, JSONObject lrequestJson) {
        BufferedImage challengeImage = null;
        Captcha textCaptcha = null;
        String lresultCaptcha = null;
        String lanswer = null;
        try {
            ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
            Captcha.Builder captchaObj = new Captcha.Builder(iCaptchaProperties.width(), iCaptchaProperties.height());

            if (iCaptchaProperties.textProducer(CaptchaType.ALPHANUMERIC) != null) {
                if (iCaptchaProperties.wordrenderer() != null) {
                    if (iCaptchaProperties.textColors() != null && iCaptchaProperties.fonts() != null) {
                        captchaObj.addText(iCaptchaProperties.textProducer(CaptchaType.ALPHANUMERIC),
                                iCaptchaProperties.wordrenderer(iCaptchaProperties.textColors(),
                                        iCaptchaProperties.fonts()));
                    } else {
                        captchaObj.addText(iCaptchaProperties.textProducer(CaptchaType.ALPHANUMERIC),
                                iCaptchaProperties.wordrenderer());
                    }
                } else {
                    captchaObj.addText(iCaptchaProperties.textProducer(CaptchaType.ALPHANUMERIC));
                }
                textCaptcha = captchaObj.addBackground(iCaptchaProperties.backgroundProducer(Color.DARK_GRAY, Color.WHITE)).addNoise().addNoise().gimp().addBorder().build();
                challengeImage = textCaptcha.getImage();
                lanswer = textCaptcha.getAnswer();
            } else if (CaptchaType.DUMMY.equals(iCaptchaProperties.captchaType())) {
                lanswer = Utils.generateRandomOfLength(iCaptchaProperties.length(), CaptchaType.DUMMY.toString());
            }
            lrequestJson.put(ServerConstants.CAPTCHA_STRING, lanswer);
            ImageIO.write(challengeImage, iCaptchaProperties.imageType().name(), imgOutputStream);
            lresultCaptcha = Base64.encodeBase64String(imgOutputStream.toByteArray());
        } catch (IOException e) {
            LOG.error("{} Exception -: {}", ServerConstants.LOGGER_PREFIX_SMS, e);
        }
        return lresultCaptcha;
    }

    private String generateAudioCaptcha(ICaptchaProperties iCaptchaProperties, JSONObject lrequestJson) {
        String lresultAudioCaptcha = null;
        try {
            AudioCaptcha.Builder captcha = new AudioCaptcha.Builder();
            if (iCaptchaProperties.textProducer(CaptchaType.NUMERIC) != null) {
                captcha.addAnswer(iCaptchaProperties.textProducer(CaptchaType.NUMERIC));
            }
            if (iCaptchaProperties.voiceProducer() != null) {
                captcha.addVoice(iCaptchaProperties.voiceProducer());
            }
            if (iCaptchaProperties.audioNoiseProducer() != null) {
                captcha.addNoise(iCaptchaProperties.audioNoiseProducer());
            }
            AudioCaptcha audioCaptcha = captcha.build();
            AudioInputStream stream = audioCaptcha.getChallenge().getAudioInputStream();
            String audioAnswer = audioCaptcha.getAnswer();
            lrequestJson.put(ServerConstants.AUDIO_CAPTCHA, audioAnswer);
            ByteArrayOutputStream byteAudioOutputStream = new ByteArrayOutputStream();
            AudioSystem.write(stream, iCaptchaProperties.audioFileFormat(), byteAudioOutputStream);
            lresultAudioCaptcha = Base64.encodeBase64String(byteAudioOutputStream.toByteArray());
        } catch (IOException e) {
            LOG.error("{} Exception -: {}", ServerConstants.LOGGER_PREFIX_SMS, e);
        }
        return lresultAudioCaptcha;

    }

    @Override
    public void handleCaptchaValidation(Message pMessage) {
        LOG.info("{} Handling Captcha Validation", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject lresponseJson = pMessage.getResponseObject().getResponseJson()
                .getJSONObject(ServerConstants.VALIDATE_CAPTCHA_RESPONSE);
        if (lresponseJson.getString(ServerConstants.CAPTCHA_STATUS).equalsIgnoreCase(ServerConstants.SUCCESS)) {
            LOG.debug("{} Captcha Validation Success", ServerConstants.LOGGER_PREFIX_SERVICES_ROUTER);
        } else if (lresponseJson.getString(ServerConstants.CAPTCHA_STATUS).equalsIgnoreCase(ServerConstants.OTP_PROCESSED)) {
            LOG.error("{} Captcha Already Processed - Validation Failed !! ", ServerConstants.LOGGER_PREFIX_SMS);
            SmsException lSmsException = SmsException.getSMSExceptionInstance();
            lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_017));
            lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_017.toString());
            lSmsException.setPriority("1");
            throw lSmsException;
        } else {
            String appId = pMessage.getHeader().getAppId();
            String interfaceId = pMessage.getHeader().getInterfaceId();
            String sessionId = pMessage.getHeader().getSessionId();
            JSONObject genCaptcha = new JSONObject();
            genCaptcha.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, interfaceId);
            genCaptcha.put(ServerConstants.MESSAGE_HEADER_APP_ID, appId);
            genCaptcha.put(ServerConstants.MESSAGE_HEADER_SESSION_ID, sessionId);
            JSONObject lrequestJson = new JSONObject();
            lrequestJson.put(ServerConstants.APPZILLON_ROOT_GENERATE_CAPTCHA_REQUEST, genCaptcha);
            pMessage.getRequestObject().setRequestJson(lrequestJson);
            this.generateCaptcha(pMessage);
            LOG.error("{} Invalid Captcha - Validation Failed !! ", ServerConstants.LOGGER_PREFIX_SMS);
            SmsException lSmsException = SmsException.getSMSExceptionInstance();
            lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_016));
            lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_016.toString());
            lSmsException.setPriority("1");
            throw lSmsException;
        }
    }
}
