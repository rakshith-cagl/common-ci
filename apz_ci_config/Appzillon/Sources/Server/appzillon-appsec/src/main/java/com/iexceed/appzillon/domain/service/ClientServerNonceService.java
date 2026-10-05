package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.dbutils.DBUtils;
import com.iexceed.appzillon.domain.entity.TbAsmiCsNonceDetail;
import com.iexceed.appzillon.domain.entity.TbAsmiCsNonceDetailPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiAppMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiValidateNonceDetailRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Named("ClientServerNonceServcie")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class ClientServerNonceService {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_PREFIX_NONCE, ClientServerNonceService.class.getName());

    @Inject
    TbAsmiValidateNonceDetailRepository cAsmiValidateNonceRepo;

    @Inject
    private TbAsmiAppMasterRepository tbAsmiAppMasterRepository;

    public static LocalDate getYesterdayLocalDate(LocalDate presentDate) {
        return presentDate.minusDays(1);
    }

    private static LocalDate getLocalDate() {

        return LocalDate.now();

    }

    public void generateNonce(Message pMessage) {
        LOG.debug("{} inside generateNonce()", ServerConstants.LOGGER_PREFIX_NONCE);
        JSONObject lRequest = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_GET_APP_SEC_TOKENS_REQUEST);
        if (lRequest.has(ServerConstants.MESSAGE_HEADER_DEVICE_ID) && Utils.isNotNullOrEmpty(lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID))) {
            String appId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            // Validating Signature - ADNROID
            isSignatureValid(pMessage);
            String deviceId = "";
            deviceId = lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
            //clearExpiredRecords(pMessage,deviceId);//deleting expired records
            TbAsmiCsNonceDetail lRecord = new TbAsmiCsNonceDetail();

            String lReqId = deviceId + "~" + appId;
            byte[] lencoded = Base64.encodeBase64(lReqId.getBytes());
            String encodedReqId = new String(lencoded);

            String reqNonce = Utils.generateRandomOfLength(24, ServerConstants.OTP_ALPHA_NUMERIC);
            String appendedNonce = reqNonce + System.currentTimeMillis();
            TbAsmiCsNonceDetailPK lPk = new TbAsmiCsNonceDetailPK(appId, deviceId, lReqId, "N", appendedNonce);
            lRecord.setId(lPk);
            lRecord.setStatus("N");
            String serverToken = Utils.generateRandomOfLength(24, ServerConstants.OTP_ALPHA_NUMERIC) + System.currentTimeMillis();
            lRecord.setServerToken(serverToken);
            Timestamp validateTs = new Timestamp(System.currentTimeMillis());
            lRecord.setCreateTs(validateTs);
            lRecord.setCreatedOn(getLocalDate());
            insertWitNonceDetail(lRecord);
            JSONObject lresponse = new JSONObject();
            lresponse.put(ServerConstants.SESSION_TOKEN, encodedReqId);
            lresponse.put(ServerConstants.SERVER_NONCE, appendedNonce);
            lresponse.put(ServerConstants.SAFE_TOKEN, serverToken);
            lresponse.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_GET_APP_SEC_TOKENS_RESPONSE, lresponse));
        } else {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
            lDomainException.setCode(DomainException.Code.APZ_DM_009.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    private void isSignatureValid(Message pMessage) {
        String enforceSignValidation = (PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.ENFORCE_SIGN_VALIDATION));
        LOG.debug(ServerConstants.LOGGER_PREFIX_NONCE + "is SignValidation Enforced -:" + enforceSignValidation);

        if (Utils.isNotNullOrEmpty(enforceSignValidation) && enforceSignValidation.equals("Y")) {
            String appId = pMessage.getHeader().getAppId();
            String signature = pMessage.getHeader().getSignature();

            if (pMessage.getHeader().getOs().equals(ServerConstants.ANDROID)) {
                LOG.debug("{} checking  Signature validation for android", ServerConstants.LOGGER_PREFIX_NONCE);
                if (tbAsmiAppMasterRepository.findAppMasterByAppIdAndSignature(appId, signature) == null) {
                    DomainException lDomainException = DomainException.getDomainExceptionInstance();
                    lDomainException.setCode(DomainException.Code.APZ_APP_SIGN_FAULT.toString());
                    lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_APP_SIGN_FAULT));
                    lDomainException.setPriority("1");
                    throw lDomainException;
                }
            }
        }
    }

    public void clearExpiredRecords(Message pMessage) {
        LOG.debug("{} Inside clearExpiredRecords()", ServerConstants.LOGGER_PREFIX_NONCE);
        String deviceId = pMessage.getHeader().getDeviceId();
        String serverNonce = pMessage.getHeader().getServerNonce();
        String appId = pMessage.getHeader().getAppId();
        if (!pMessage.getHeader().getDeviceId().equals(ServerConstants.WEB)) {
            cAsmiValidateNonceRepo.deleteRecsWithDeviceId(appId, deviceId, serverNonce);
        }
    }

    public Date getYesterdayDate(Date presentDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(presentDate);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public void validateClientServerNonce(Message pMessage) {
        LOG.debug("{} inside validateNonce()", ServerConstants.LOGGER_PREFIX_NONCE);
        String cNonce = pMessage.getHeader().getClientNonce();
        String sNonce = pMessage.getHeader().getServerNonce();
        String sessionToken = pMessage.getHeader().getSessionToken();
        String decodedSessionToken = new String(Base64.decodeBase64(sessionToken));
        String deviceId = decodedSessionToken.substring(0, decodedSessionToken.indexOf("~"));
        LocalDate simpleDateFormat = getLocalDate();

        String disableDbPartition = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.DISABLE_DB_PARTITION);
        String dataBaseType = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.DATABASE);

        LOG.debug("Database : {}, Disable db partition flag : {}", dataBaseType, disableDbPartition);


        TbAsmiCsNonceDetail lcsNonceRecord = null;
        if (disableDbPartition.equalsIgnoreCase(ServerConstants.NO) && (dataBaseType.equalsIgnoreCase(ServerConstants.ORACLE) || dataBaseType.equalsIgnoreCase(ServerConstants.POSTGRESQL))) {
            lcsNonceRecord = cAsmiValidateNonceRepo.findWithCreatedOnAndPK(simpleDateFormat, deviceId, pMessage.getHeader().getAppId(), decodedSessionToken, "N", sNonce);
        } else {
            lcsNonceRecord = cAsmiValidateNonceRepo.findWithPK(deviceId, pMessage.getHeader().getAppId(), decodedSessionToken, "N", sNonce);
        }
        if (disableDbPartition.equalsIgnoreCase(ServerConstants.NO) && ((ServerConstants.ORACLE.equalsIgnoreCase(dataBaseType) || ServerConstants.POSTGRESQL.equalsIgnoreCase(dataBaseType)) && lcsNonceRecord == null)) {
            simpleDateFormat = getYesterdayLocalDate(simpleDateFormat);
            lcsNonceRecord = cAsmiValidateNonceRepo.findWithCreatedOnAndPK(simpleDateFormat, deviceId, pMessage.getHeader().getAppId(), decodedSessionToken, "N", sNonce);
        }
        // sNonce Check
        if (lcsNonceRecord != null && lcsNonceRecord.getId().getServerNonce().equals(sNonce)) {
            pMessage.getHeader().setServerToken(lcsNonceRecord.getServerToken());
            TbAsmiCsNonceDetail tbAsmiCsNonceDetail = null;
            tbAsmiCsNonceDetail = getTbAsmiCsNonceDetail(pMessage, cNonce, sNonce, decodedSessionToken, deviceId, simpleDateFormat, disableDbPartition, dataBaseType);
            Map<String, String> inputParam = new HashMap<>();
            inputParam.put("cNonce", cNonce);
            inputParam.put("sNonce", sNonce);
            inputParam.put("deviceId", deviceId);
            checkNonce(pMessage, inputParam, decodedSessionToken, simpleDateFormat, lcsNonceRecord, tbAsmiCsNonceDetail);
        } else {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_072));
            lDomainException.setCode(DomainException.Code.APZ_DM_072.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    private TbAsmiCsNonceDetail getTbAsmiCsNonceDetail(Message pMessage, String cNonce, String sNonce, String decodedSessionToken, String deviceId, LocalDate simpleDateFormat, String disableDbPartition, String dataBaseType) {
        TbAsmiCsNonceDetail tbAsmiCsNonceDetail;
        if (disableDbPartition.equalsIgnoreCase(ServerConstants.NO) && (dataBaseType.equalsIgnoreCase(ServerConstants.ORACLE) || dataBaseType.equalsIgnoreCase(ServerConstants.POSTGRESQL))) {
            tbAsmiCsNonceDetail = cAsmiValidateNonceRepo.findWithCreatedOnAndPK(simpleDateFormat, pMessage.getHeader().getDeviceId(), pMessage.getHeader().getAppId(), decodedSessionToken, cNonce, sNonce);
        } else {
            tbAsmiCsNonceDetail = cAsmiValidateNonceRepo.findWithPK(deviceId, pMessage.getHeader().getAppId(), decodedSessionToken, cNonce, sNonce);
        }
        return tbAsmiCsNonceDetail;
    }

    private void checkNonce(Message pMessage, Map<String, String> inputParam, String decodedSessionToken, LocalDate simpleDateFormat, TbAsmiCsNonceDetail lcsNonceRecord, TbAsmiCsNonceDetail tbAsmiCsNonceDetail) {
        String cNonce = inputParam.get("cNonce");
        String sNonce = inputParam.get("sNonce");
        String deviceId = inputParam.get("deviceId");
        // cNonce check
        if (tbAsmiCsNonceDetail == null) {
            LOG.debug("{} client Nonce is valid", ServerConstants.LOGGER_PREFIX_NONCE);
            TbAsmiCsNonceDetail lnewRecord = new TbAsmiCsNonceDetail();
            TbAsmiCsNonceDetailPK lPk = new TbAsmiCsNonceDetailPK(pMessage.getHeader().getAppId(), deviceId, decodedSessionToken, cNonce, sNonce);

            lnewRecord.setId(lPk);
            lnewRecord.setStatus("Y");
            lnewRecord.setServerToken(lcsNonceRecord.getServerToken());
            Timestamp validateTs = new Timestamp(System.currentTimeMillis());
            lnewRecord.setCreateTs(validateTs);
            lnewRecord.setCreatedOn(simpleDateFormat);
            insertWitNonceDetail(lnewRecord);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put("clientServerNonceValidation", new JSONObject().put("status", true)));
        } else {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_071.toString()));
            lDomainException.setCode(DomainException.Code.APZ_DM_071.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    public void purgeNonce(Message pMessage) {
        String sNonce = pMessage.getHeader().getServerNonce();
        cAsmiValidateNonceRepo.deleteRecWithsNonce(sNonce);
    }

    private void insertWitNonceDetail(TbAsmiCsNonceDetail nonceDetail) {
        EntityManager entityManager = null;
        entityManager = DBUtils.getEntityManager("appzillon-domain");
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.createNativeQuery("INSERT INTO TB_ASMI_CS_NONCEDETAILS (DEVICE_ID,APP_ID,REQUEST_ID,CLIENT_NONCE,SERVER_NONCE,STATUS,SERVER_TOKEN,CREATE_TS,CREATED_ON) values (?,?,?,?,?,?,?,?,?)").setParameter(1, nonceDetail.getId().getDeviceId()).setParameter(2, nonceDetail.getId().getAppId()).setParameter(3, nonceDetail.getId().getRequestId()).setParameter(4, nonceDetail.getId().getClientNonce()).setParameter(5, nonceDetail.getId().getServerNonce()).setParameter(6, nonceDetail.getStatus()).setParameter(7, nonceDetail.getServerToken()).setParameter(8, nonceDetail.getCreateTs()).setParameter(9, nonceDetail.getCreatedOn()).executeUpdate();
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
            entityManager.close();
        }
    }

}
