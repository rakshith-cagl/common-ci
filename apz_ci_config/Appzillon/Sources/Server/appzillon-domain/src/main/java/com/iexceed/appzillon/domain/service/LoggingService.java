/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.*;
import com.iexceed.appzillon.domain.utils.Constants;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Error;
import com.iexceed.appzillon.message.IntfMasterDtls;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.utils.LargeData;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.appzillon.domain.utils.Constants.ADD_INFO;
import static com.iexceed.appzillon.domain.utils.Constants.L_REQUEST_STATUS_TXN_REF_NO;
import static com.iexceed.appzillon.utils.ServerConstants.*;

/**
 * @author arthanarisamy
 */
@Named("LoggingService")
@Transactional(TRANSACTION_APPZILLON_ADMIN)
public class LoggingService {

    public static final String NO_RECORDS_LAST_LOGIN_FOUND = "{} No records last login found.";
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(LOGGER_DOMAIN,
            LoggingService.class.getName());
    @Inject
    private TbAslgTxnDetailRepository cTbAslgTxnDetailRepo;

    @Inject
    private TbAslgSmsTxnRepository smsTxnRepository;

    @Inject
    private TbAslgUssdTxnRepository ussdTxnRepository;

    @Inject
    private TbAslgTxtMslgLogRepository txtMslgLogRepository;

    @Inject
    private TbAslgCnvUITxnLogRepository cnvUITxnLogRepository;

    @Inject
    private TbAslgFmwTxnDetailRepository cTbAslgFmwTxnDetailRepo;

    @Inject
    private TbAstpLdRecsRepository tbAstpLdRecsRepo;

    public String logTransactionDetails(Message pMessage) {
        JSONObject location = pMessage.getHeader().getLocation();
        String longitude = "0";
        String latitude = "0";
        Map<String, IntfMasterDtls> interfaceDBMap = InterfaceMasterService.getInterfaceMasterMap()
                .get(pMessage.getHeader().getAppId());
        if (location != null) {
//            longitude = location.has(ServerConstants.LONGITUDE) ? location.getString(ServerConstants.LONGITUDE) : "0";
//            latitude = location.has(ServerConstants.LATITUDE) ? location.getString(ServerConstants.LATITUDE) : "0";
            if(location.has(ServerConstants.LATITUDE)) {
                latitude=location.getString(ServerConstants.LATITUDE);
            }else if(location.has("lat")) {
                latitude=location.getString("lat");
            }
            if(location.has(ServerConstants.LONGITUDE)) {
                longitude = location.getString(ServerConstants.LONGITUDE);
            }else if(location.has("lng")) {
                longitude = location.getString("lng");
            }
        }
        if (pMessage.getSecurityParams().getLogTxn().equalsIgnoreCase(YES) && ServerConstants.YES
                .equalsIgnoreCase(interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getTxnLogReq())) {
            LOG.debug("{} Logging of Transaction Started", LOGGER_PREFIX_DOMAIN);
            String lRequsetStatus;
            TbAslgTxnDetail lSmlgTxnDetail = null;

            String requestBody = pMessage.getRequestObject().getRequestJson().toString();
            String lTxnStat = "S";

            // below changes made by ripu as part of 3.1 development on
            // 29-10-2014
            JSONObject addInfo = null;
            JSONObject requestBodyJson = pMessage.getRequestObject().getRequestJson();
            if (requestBodyJson.has(ADD_INFO)) {
                addInfo = (JSONObject) requestBodyJson.get(ADD_INFO);
                requestBodyJson.remove(ADD_INFO);
            }

            // changes end

            Timestamp lsttm = pMessage.getHeader().getStartTime();
            // Changes made to avoid logging download file in txn table

            String lGetEncString = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
                    pMessage.getHeader().getDeviceId(), requestBody);

            /*
             * Below changes are made by Samy on 20/04/2016 To Avoid fetching txn ref no
             * from table generator Combination of Userid+systemcurrentmilliseconds+randomno
             * is used
             */
            TbAslgTxnDetail tbcslgtxndetobj = new TbAslgTxnDetail();
            tbcslgtxndetobj.setTxnRef(pMessage.getHeader().getMasterTxnRef());
            tbcslgtxndetobj.setUserId(pMessage.getHeader().getUserId());
            tbcslgtxndetobj.setAppId(pMessage.getHeader().getAppId());
            tbcslgtxndetobj.setInterfaceId(pMessage.getHeader().getInterfaceId());
            tbcslgtxndetobj.setDeviceId(pMessage.getHeader().getDeviceId());
            tbcslgtxndetobj.setStTm(lsttm);
            tbcslgtxndetobj.setTxnStat(lTxnStat);
            tbcslgtxndetobj.setCreateTs(lsttm);
            lSmlgTxnDetail = tbcslgtxndetobj;

            lSmlgTxnDetail.setCreateBy(pMessage.getHeader().getUserId());
            // adding source to transaction detail
            lSmlgTxnDetail.setSource(pMessage.getHeader().getSource());
            lSmlgTxnDetail.setLongitude(longitude);
            lSmlgTxnDetail.setLatitude(latitude);
            // Origination changes made by Samy to capture the source IP address
            lSmlgTxnDetail.setOrigination(pMessage.getHeader().getOrigination());
            // below changes made by ripu as part of 3.1 development on
            // 29-10-2014
            setInfo(lSmlgTxnDetail, addInfo);
            // Changes end here

            // * Request string is split for 10 different columns and inserted
            // into the data base along with the size
            // * Changes made by Samy on 20/02/2015

            lRequsetStatus = "" + lSmlgTxnDetail.getTxnRef();
            pMessage.getHeader().setTxnRef(lRequsetStatus);
            Timestamp endtm = new Timestamp(new Date().getTime());
            lSmlgTxnDetail.setEndTm(endtm);

            lSmlgTxnDetail.setSessionId(pMessage.getHeader().getSessionId());
            lSmlgTxnDetail.setExtStTm(pMessage.getHeader().getExtStartTime());
            lSmlgTxnDetail.setExtEndTm(pMessage.getHeader().getExtEndTime());

            // adding location details into transaction details table

            addLocationDetails(location, lSmlgTxnDetail);
            List<Error> errors = pMessage.getErrors();
            JSONArray lErrors = new JSONArray();
            JSONObject body = new JSONObject();
            String lGetEncResponse = handleError(pMessage, errors, lErrors, body);
            lSmlgTxnDetail.setStatus(pMessage.getHeader().getStatus() ? "S" : "F");
            lSmlgTxnDetail.setEndTm(endtm);
            lSmlgTxnDetail.setAppUserId(pMessage.getHeader().getAppUserId());
            lSmlgTxnDetail.setUserAppId(pMessage.getHeader().getUserAppId());

            // * Request string is split for 10 different columns and inserted
            // into the database along with the size
            // * Changes made by Samy on 20/02/2015*/

            persistRecordsInDB(pMessage, interfaceDBMap, lSmlgTxnDetail, lGetEncString, lGetEncResponse);

            return lRequsetStatus;
        } else {
            LOG.debug("{} TXN logging bypassed", LOGGER_PREFIX_DOMAIN);
            return RESP_BODY_STATUS_ERROR;
        }
    }

    private void persistRecordsInDB(Message pMessage, Map<String, IntfMasterDtls> interfaceDBMap, TbAslgTxnDetail lSmlgTxnDetail, String lGetEncString, String lGetEncResponse) {
        List<LargeData> reqLdRecList = new ArrayList<>();
        List<LargeData> resLdRecList = new ArrayList<>();
        if (TXN_LOG_PAYLOAD_BOTH.equalsIgnoreCase(pMessage.getSecurityParams().getTransactionLogPayload())
                || TXN_LOG_PAYLOAD_REQUEST.equalsIgnoreCase(pMessage.getSecurityParams().getTransactionLogPayload())
                || TXN_LOG_PAYLOAD_RESPONSE
                .equalsIgnoreCase(pMessage.getSecurityParams().getTransactionLogPayload())) {

            if (TXN_LOG_PAYLOAD_BOTH.equalsIgnoreCase(
                    interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getTxnLogPayLoadReq())) {

                /*
                 * encrypted req and res is split into 5 different columns based on the max tax
                 * len and stored into separate table changes made by Asha on 27/03/2019
                 */

                reqLdRecList = Utils.getPayloadList(lGetEncString, pMessage);
                persistLdRecs(reqLdRecList);

                resLdRecList = Utils.getPayloadList(lGetEncResponse, pMessage);
                persistLdRecs(resLdRecList);
                /* end */
            } else if (TXN_LOG_PAYLOAD_REQUEST.equalsIgnoreCase(
                    interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getTxnLogPayLoadReq())) {
                reqLdRecList = Utils.getPayloadList(lGetEncString, pMessage);
                persistLdRecs(reqLdRecList);
            } else if (TXN_LOG_PAYLOAD_RESPONSE.equalsIgnoreCase(
                    interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getTxnLogPayLoadReq())) {
                resLdRecList = Utils.getPayloadList(lGetEncResponse, pMessage);
                persistLdRecs(resLdRecList);
            }

        }

        lSmlgTxnDetail.setReqLdRefNo(!reqLdRecList.isEmpty() ? reqLdRecList.get(0).getRefNo() : "");
        lSmlgTxnDetail.setReqNoRecs(reqLdRecList.size());

        lSmlgTxnDetail.setResLdRefNo(!resLdRecList.isEmpty() ? resLdRecList.get(0).getRefNo() : "");
        lSmlgTxnDetail.setResNoRecs(resLdRecList.size());
        cTbAslgTxnDetailRepo.save(lSmlgTxnDetail);
    }

    private String handleError(Message pMessage, List<Error> errors, JSONArray lErrors, JSONObject body) {
        String lResponse;
        if (errors.size() > 1) {
            validateForErrors(errors, lErrors);
            lResponse = lErrors.toString();
        } else {
            lResponse = pMessage.getResponseObject().getResponseJson().toString();
            body = pMessage.getResponseObject().getResponseJson();
        }
        String lGetEncResponse = null;
        // Changes made to avoid logging download file in txn table
        if (body != null) {
            if (!body.has(OTAFILEDOWNLOAD_RESPONSE)) {
                lGetEncResponse = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
                        pMessage.getHeader().getDeviceId(), lResponse);
            } else {
                JSONObject lresponse1 = body.getJSONObject(OTAFILEDOWNLOAD_RESPONSE);
                String lres = lresponse1.get("fileName").toString() + lresponse1.get("filePath").toString();
                lGetEncResponse = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
                        pMessage.getHeader().getDeviceId(), lres);
            }
        }
        return lGetEncResponse;
    }

    private void validateForErrors(List<Error> errors, JSONArray lErrors) {
        for (int i = 0; i < errors.size(); i++) {
            Error lError = errors.get(i);
            JSONObject lErrorJson = new JSONObject();
            if (Utils.isNotNullOrEmpty(lError.getErrorCode())) {
                lErrorJson.put(ServerConstants.MESSAGE_HEADER_ERROR_CODE, lError.getErrorCode());
                lErrorJson.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, lError.getErrorDesc());
                lErrors.put(lErrorJson);
            }
        }
    }

    private void addLocationDetails(JSONObject location, TbAslgTxnDetail lSmlgTxnDetail) {
        if (location != null) {
            lSmlgTxnDetail.setSublocality(
                    location.has(ServerConstants.SUBLOCALITY) ? location.getString(ServerConstants.SUBLOCALITY)
                            : "");
            lSmlgTxnDetail.setAdminAreaLvl1(location.has(ServerConstants.ADMIN_AREA_LVL_1)
                    ? location.getString(ServerConstants.ADMIN_AREA_LVL_1)
                    : "");
            lSmlgTxnDetail.setAdminAreaLvl2(location.has(ServerConstants.ADMIN_AREA_LVL_2)
                    ? location.getString(ServerConstants.ADMIN_AREA_LVL_2)
                    : "");
            lSmlgTxnDetail.setCountry(
                    location.has(ServerConstants.COUNTRY) ? location.getString(ServerConstants.COUNTRY) : "");
            lSmlgTxnDetail.setFormattedAddress(location.has(ServerConstants.FORAMATTED_ADDRESS)
                    ? location.getString(ServerConstants.FORAMATTED_ADDRESS)
                    : ServerConstants.DEFAULT_FORMATTED_ADDRESS);
        }
    }

    private void setInfo(TbAslgTxnDetail lSmlgTxnDetail, JSONObject addInfo) {
        String info1 = "";
        String info2 = "";
        String info3 = "";
        String info4 = "";
        String info5 = "";

        if (addInfo != null) {
            info1 = (String) (addInfo.has(ADDITIONAL_INFO1) ? addInfo.get(ADDITIONAL_INFO1) : "");
            info2 = (String) (addInfo.has(ADDITIONAL_INFO2) ? addInfo.get(ADDITIONAL_INFO2) : "");
            info3 = (String) (addInfo.has(ADDITIONAL_INFO3) ? addInfo.get(ADDITIONAL_INFO3) : "");
            info4 = (String) (addInfo.has(ADDITIONAL_INFO4) ? addInfo.get(ADDITIONAL_INFO4) : "");
            info5 = (String) (addInfo.has(ADDITIONAL_INFO5) ? addInfo.get(ADDITIONAL_INFO5) : "");
        }

        lSmlgTxnDetail.setInfo1(info1);
        lSmlgTxnDetail.setInfo2(info2);
        lSmlgTxnDetail.setInfo3(info3);
        lSmlgTxnDetail.setInfo4(info4);
        lSmlgTxnDetail.setInfo5(info5);
    }

    public void persistLdRecs(List<LargeData> ldRecList) {
        LOG.debug("{} Inside persistLdRecs()", LOGGER_PREFIX_DOMAIN);
        TbAstpLdRecs tbAstpLdRecs = null;
        TbAstpLdRecsPK bAstpLdRecsPK = null;
        List<TbAstpLdRecs> ldRecs = new ArrayList<>();
        for (LargeData recs : ldRecList) {
            bAstpLdRecsPK = new TbAstpLdRecsPK(recs.getRefNo(), recs.getSeqNo());
            tbAstpLdRecs = new TbAstpLdRecs(bAstpLdRecsPK, recs.getDataChunk1(), recs.getDataChunk2(), recs.getDataChunk3(),
                    recs.getDataChunk4(), recs.getDataChunk5());
            ldRecs.add(tbAstpLdRecs);
        }
        long sttime = System.currentTimeMillis();
        tbAstpLdRecsRepo.saveAll(ldRecs);
        LOG.info("{} Total time to persist ldRecs : {}", LOGGER_PREFIX_DOMAIN, (System.currentTimeMillis() - sttime));
    }


    private String encryptData(String pAppId, String pUserId, String pDevciceId, String pAppzillonBody) {
        return AppzillonAESUtils.encryptString(pAppId + pUserId + pDevciceId, pAppzillonBody);
    }

    /**
     * Below method written by ripu on 7-Mar-2016 purpose : To Log SMS Txn in
     * TB_ASLG_SMS_TXN,
     *
     * @param pMessage
     */
    public void smsLogTransaction(Message pMessage) {
        LOG.debug("{} inside smsLogTransaction()", LOGGER_PREFIX_DOMAIN);
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        TbAslgSmsTxn smsTxnObj = null;
        String msgId = "";
        if (Utils.isNullOrEmpty(pMessage.getHeader().getTxnRef())) {
            smsTxnObj = new TbAslgSmsTxn();
            /*
             * Below changes are made by Samy on 21/04/2016 To Avoid fetching txn ref no
             * from table generator Combination of Userid+systemcurrentmilliseconds+randomno
             * is used
             */
            SecureRandom secureRandom = new SecureRandom();
            int randomno = secureRandom.nextInt(1000000);
            smsTxnObj.setSmsTxnRef(requestJson.getString(MOBILENUMBER) + System.currentTimeMillis() + "" + randomno);
            smsTxnObj.setMobileNumber(requestJson.getString(MOBILENUMBER));
            // changes made on 11-08-2017 to encrypt request.
            String lGetEncString = encryptData(pMessage.getHeader().getAppId(), requestJson.getString(MOBILENUMBER),
                    requestJson.getString(MOBILENUMBER), requestJson.getString(MESSAGE));
            smsTxnObj.setRequest(lGetEncString);

            if (requestJson.has(JMS_MSG_ID)) {
                msgId = requestJson.getString(JMS_MSG_ID);
            }
            smsTxnObj.setMessageId(msgId);
            smsTxnObj.setStartTime(new Date());
            smsTxnObj.setOrigination(pMessage.getHeader().getOrigination());
            smsTxnObj.setCreatedBy(pMessage.getHeader().getUserId());
            smsTxnObj.setCreateTs(new Date());
            smsTxnObj.setAppId(pMessage.getHeader().getAppId());
            smsTxnRepository.save(smsTxnObj);
            String lRequsetStatus = smsTxnObj.getSmsTxnRef() + "";
            LOG.debug(L_REQUEST_STATUS_TXN_REF_NO, LOGGER_PREFIX_DOMAIN, lRequsetStatus);
            pMessage.getHeader().setTxnRef(lRequsetStatus);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put(STATUS, SUCCESS));
        } else {
            Optional<TbAslgSmsTxn> smsTxnObjOpt = smsTxnRepository.findById(pMessage.getHeader().getTxnRef());
            if (smsTxnObjOpt.isPresent())
                smsTxnObj = smsTxnObjOpt.get();
            else {
                LOG.error(NO_RECORDS_LAST_LOGIN_FOUND,
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ds = DomainException.getDomainExceptionInstance();
                ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                ds.setCode(DomainException.Code.APZ_DM_008.toString());
                ds.setPriority("1");
                throw ds;
            }
            LOG.debug(Constants.TRANSACTION_LOG_DETAILS, LOGGER_PREFIX_DOMAIN,
                    pMessage.getHeader().getTxnRef(), smsTxnObj);
            if (smsTxnObj != null) {
                String lRespEncString = encryptData(pMessage.getHeader().getAppId(),
                        requestJson.getString(MOBILENUMBER), requestJson.getString(MOBILENUMBER),
                        requestJson.getString(RESPONSE));
                smsTxnObj.setResponse(lRespEncString);
                smsTxnObj.setEndTime(new Date());
                smsTxnRepository.save(smsTxnObj);
            }
        }
    }

    /**
     * Below method written by ripu on 8-Mar-2016 purpose : To Log SMS Txn in
     * TB_ASLG_USSD_TXN,
     *
     * @param pMessage
     */
    public void ussdLogTransaction(Message pMessage) {
        LOG.debug("{} inside ussdLogTransaction().", LOGGER_PREFIX_DOMAIN);
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        TbAslgUssdTxn ussdTxnObj = null;
        if (Utils.isNullOrEmpty(pMessage.getHeader().getTxnRef())) {
            ussdTxnObj = new TbAslgUssdTxn();
            /*
             * Below changes are made by Samy on 21/04/2016 To Avoid fetching txn ref no
             * from table generator Combination of Userid+systemcurrentmilliseconds+randomno
             * is used
             */
            SecureRandom secureRandom = new SecureRandom();
            int randomno = secureRandom.nextInt(1000000);
            ussdTxnObj.setUssdTxnRef(requestJson.getString(MOBILENUMBER) + System.currentTimeMillis() + "" + randomno);
            ussdTxnObj.setMobileNumber(requestJson.getString(MOBILENUMBER));
            // changes made on 11-08-2017 to encrypt.
            String lGetEncString = encryptData(pMessage.getHeader().getAppId(), requestJson.getString(MOBILENUMBER),
                    requestJson.getString(MOBILENUMBER), requestJson.getString("data"));
            ussdTxnObj.setRequest(lGetEncString);
            ussdTxnObj.setAction(requestJson.getString("action"));
            ussdTxnObj.setStartTime(new Date());
            ussdTxnObj.setOrigination(pMessage.getHeader().getOrigination());
            ussdTxnObj.setCreatedBy(pMessage.getHeader().getUserId());
            ussdTxnObj.setCreateTs(new Date());
            ussdTxnObj.setAppId(pMessage.getHeader().getAppId());
            ussdTxnRepository.save(ussdTxnObj);

            String lRequsetStatus = ussdTxnObj.getUssdTxnRef() + "";
            LOG.debug(L_REQUEST_STATUS_TXN_REF_NO, LOGGER_PREFIX_DOMAIN, lRequsetStatus);
            pMessage.getHeader().setTxnRef(lRequsetStatus);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put(STATUS, SUCCESS));
        } else {
            Optional<TbAslgUssdTxn> ussdTxnObjOpt = ussdTxnRepository.findById(pMessage.getHeader().getTxnRef());
            if (ussdTxnObjOpt.isPresent())
                ussdTxnObj = ussdTxnObjOpt.get();
            else {
                LOG.error(NO_RECORDS_LAST_LOGIN_FOUND,
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ds = DomainException.getDomainExceptionInstance();
                ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                ds.setCode(DomainException.Code.APZ_DM_008.toString());
                ds.setPriority("1");
                throw ds;
            }
            LOG.debug(Constants.TRANSACTION_LOG_DETAILS, LOGGER_PREFIX_DOMAIN,
                    pMessage.getHeader().getTxnRef(), ussdTxnObj);
            if (ussdTxnObj != null) {
                String lRespEncString = encryptData(pMessage.getHeader().getAppId(),
                        requestJson.getString(MOBILENUMBER), requestJson.getString(MOBILENUMBER),
                        requestJson.getString(RESPONSE));
                ussdTxnObj.setResponse(lRespEncString);
                ussdTxnObj.setEndTime(new Date());
                ussdTxnRepository.save(ussdTxnObj);
            }
        }
    }

    /**
     * Below method written by ripu on 14-Mar-2016 purpose : To Log SMS Txn in
     * TB_ASLG_TXT_MSLG_LOG,
     *
     * @param pMessage
     */
    public void txtMessageLogTransaction(Message pMessage) {
        LOG.debug("{} inside txtMessageLogTransaction().", LOGGER_PREFIX_DOMAIN);
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        TbAslgTxtMslgLog txtMslgObj = null;
        if (Utils.isNullOrEmpty(pMessage.getHeader().getTxnRef())) {
            txtMslgObj = new TbAslgTxtMslgLog();
            SecureRandom secureRandom = new SecureRandom();
            int randomno = secureRandom.nextInt(1000000);
            txtMslgObj.setSmsTxnRef(requestJson.getString(MOBILENUMBER) + System.currentTimeMillis() + "" + randomno);
            txtMslgObj.setMobileNumber(requestJson.getString(MOBILENUMBER));
            // changes made on 11-08-2017 to encrypt.
            String lGetEncString = encryptData(requestJson.getString(MESSAGE_HEADER_APP_ID),
                    requestJson.getString(MOBILENUMBER), requestJson.getString(MOBILENUMBER),
                    requestJson.getString(MESSAGE));
            txtMslgObj.setRequest(lGetEncString);
            txtMslgObj.setAppId(requestJson.getString(MESSAGE_HEADER_APP_ID));
            if (requestJson.has("port")) {
                txtMslgObj.setPort(requestJson.getString("port"));
            }
            txtMslgObj.setStartTime(new Date());
            txtMslgObj.setOrigination(pMessage.getHeader().getOrigination());
            txtMslgObj.setCreatedBy(pMessage.getHeader().getUserId());
            txtMslgObj.setCreateTs(new Date());
            txtMslgLogRepository.save(txtMslgObj);

            String lRequsetStatus = txtMslgObj.getSmsTxnRef() + "";
            LOG.debug(L_REQUEST_STATUS_TXN_REF_NO, LOGGER_PREFIX_DOMAIN, lRequsetStatus);
            pMessage.getHeader().setTxnRef(lRequsetStatus);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put(STATUS, SUCCESS));
        } else {
            Optional<TbAslgTxtMslgLog> txtMslgObjOpt = txtMslgLogRepository.findById(pMessage.getHeader().getTxnRef());
            if (txtMslgObjOpt.isPresent())
                txtMslgObj = txtMslgObjOpt.get();
            else {
                LOG.error(NO_RECORDS_LAST_LOGIN_FOUND,
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ds = DomainException.getDomainExceptionInstance();
                ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                ds.setCode(DomainException.Code.APZ_DM_008.toString());
                ds.setPriority("1");
                throw ds;
            }
            LOG.debug(Constants.TRANSACTION_LOG_DETAILS, LOGGER_PREFIX_DOMAIN,
                    pMessage.getHeader().getTxnRef(), txtMslgObj);
            if (txtMslgObj != null) {
                String lRespEncString = encryptData(requestJson.getString(MESSAGE_HEADER_APP_ID),
                        requestJson.getString(MOBILENUMBER), requestJson.getString(MOBILENUMBER),
                        requestJson.get(RESPONSE).toString());
                txtMslgObj.setResponse(lRespEncString);
                txtMslgObj.setEndTime(new Date());
                txtMslgLogRepository.save(txtMslgObj);
            }
        }
    }

    public void cnvUILogTransaction(Message pMessage) {
        LOG.debug("{} inside cnvUILogTransaction()", LOGGER_PREFIX_DOMAIN);
        JSONObject requestJson;
        JSONObject responseJson;
        TbAslgCnvUITxnLog cnvUITxnLog = null;
        String txnRef = "";
        String appId = "";
        if (pMessage.getRequestObject().getRequestJson()
                .has(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_REQUEST)) {
            cnvUITxnLog = new TbAslgCnvUITxnLog();
            requestJson = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_REQUEST);

            responseJson = pMessage.getResponseObject().getResponseJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_RESPONSE);

            appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            txnRef = appId + System.currentTimeMillis() + Utils.generateSecureRandom(1000000);
            responseJson.put(TXN_REF, txnRef);
            pMessage.getResponseObject().getResponseJson().put(APPZILLON_ROOT_GET_FIRST_CNVUI_DLG_RESPONSE,
                    responseJson);
            cnvUITxnLog.setCreateTs(new Timestamp(new Date().getTime()));
        } else {
            requestJson = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_REQUEST);
            txnRef = requestJson.getString(ServerConstants.TXN_REF);
            Optional<TbAslgCnvUITxnLog> cnvUITxnLogOpt = cnvUITxnLogRepository.findById(txnRef);
            if (cnvUITxnLogOpt.isPresent())
                cnvUITxnLog = cnvUITxnLogOpt.get();
            else {
                LOG.error(NO_RECORDS_LAST_LOGIN_FOUND,
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ds = DomainException.getDomainExceptionInstance();
                ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                ds.setCode(DomainException.Code.APZ_DM_008.toString());
                ds.setPriority("1");
                throw ds;
            }
            responseJson = pMessage.getResponseObject().getResponseJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_GET_CNVUI_DLG_RESPONSE);

            appId = requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String screenDataStr = requestJson.getJSONObject(ServerConstants.CNVUI_SCREEN_DATA).toString();
            String lGetEncString = encryptData(pMessage.getHeader().getAppId(), txnRef,
                    pMessage.getHeader().getDeviceId(), screenDataStr);
            cnvUITxnLog.setScreenData(lGetEncString);
            cnvUITxnLog.setUpdateTs(new Timestamp(new Date().getTime()));
        }

        String cnvUIId = requestJson.getString(ServerConstants.APPZILLON_CNVUI_ID);
        String respDlgId = responseJson.getString(ServerConstants.RESP_DLG_ID);
        responseJson.remove(ServerConstants.RESP_DLG_ID);
        String userId = pMessage.getHeader().getUserId();
        cnvUITxnLog.setTxnRef(txnRef);
        cnvUITxnLog.setAppId(appId);
        cnvUITxnLog.setCnvUIId(cnvUIId);
        cnvUITxnLog.setRespDlgId(respDlgId);
        if (userId != null && !userId.isEmpty()) {
            cnvUITxnLog.setCreateUserId(userId);
        }
        cnvUITxnLogRepository.save(cnvUITxnLog);

    }

    public void logFmwTransactionDetails(Message pMessage) {

        Map<String, IntfMasterDtls> interfaceDBMap = InterfaceMasterService.getInterfaceMasterMap()
                .get(pMessage.getHeader().getAppId());

        LOG.debug("{} inside logFmwTransactionDetails()", LOGGER_PREFIX_DOMAIN);
        if ((ServerConstants.YES
                .equalsIgnoreCase(interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getTxnLogReq())
                && ServerConstants.YES
                .equalsIgnoreCase(interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getFmwTxnReq()))
                || ServerConstants.APPZILLON_ROOT_MAIL_TYPE.equals(pMessage.getIntfDtls().getType())) {
            LOG.info(LOGGER_PREFIX_DOMAIN + " Logging of FMW Transaction Started");
            String requestBody = pMessage.getRequestObject().getRequestJson()
                    .getString(ServerConstants.REQUEST_PAYLOAD);
            String responseBody = pMessage.getResponseObject().getResponseJson().toString();

            String encryptedRequest = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
                    pMessage.getHeader().getDeviceId(), requestBody);
            String encryptedResponse = encryptData(pMessage.getHeader().getAppId(), pMessage.getHeader().getUserId(),
                    pMessage.getHeader().getDeviceId(), responseBody);
            TbAslgFmwTxnDetail lSmlgFmwTxnDetail = new TbAslgFmwTxnDetail();
            lSmlgFmwTxnDetail.setCreateTs(new Timestamp(System.currentTimeMillis()));
            lSmlgFmwTxnDetail.setEndpointType(InterfaceMasterService.getInterfaceMasterMap()
                    .get(pMessage.getHeader().getAppId()).get(pMessage.getHeader().getInterfaceId()).getType());
            lSmlgFmwTxnDetail.setInterfaceId(pMessage.getHeader().getInterfaceId());

            lSmlgFmwTxnDetail.setMasterTxnRef(pMessage.getHeader().getMasterTxnRef());
            lSmlgFmwTxnDetail.setTxnRef(Utils.getTxnRefNum(pMessage.getHeader().getUserId()));
            lSmlgFmwTxnDetail.setStTm(pMessage.getHeader().getExtStartTime());
            lSmlgFmwTxnDetail.setEndTm(pMessage.getHeader().getExtEndTime());

            List<LargeData> reqLdRecList = new ArrayList<>();
            List<LargeData> resLdRecList = new ArrayList<>();

            if ((Utils.isNotNullOrEmpty(pMessage.getSecurityParams().getFmwTxnPayload())
                    && (TXN_LOG_PAYLOAD_BOTH.equalsIgnoreCase(pMessage.getSecurityParams().getFmwTxnPayload())
                    || TXN_LOG_PAYLOAD_REQUEST.equalsIgnoreCase(pMessage.getSecurityParams().getFmwTxnPayload())
                    || TXN_LOG_PAYLOAD_RESPONSE
                    .equalsIgnoreCase(pMessage.getSecurityParams().getFmwTxnPayload())))
                    || ServerConstants.APPZILLON_ROOT_MAIL_TYPE.equals(pMessage.getIntfDtls().getType())) {

                if (TXN_LOG_PAYLOAD_BOTH.equalsIgnoreCase(
                        interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getFmwTxnPayloadReq())
                        || ServerConstants.APPZILLON_ROOT_MAIL_TYPE.equals(pMessage.getIntfDtls().getType())) {

                    // Changes to populate the additional encrypted payload of Request and Response
                    // to separate table with Master TXN reference and sequence number
                    // modified on 08/03/18

                    reqLdRecList = Utils.getPayloadList(encryptedRequest, pMessage);
                    persistLdRecs(reqLdRecList);

                    resLdRecList = Utils.getPayloadList(encryptedResponse, pMessage);
                    persistLdRecs(resLdRecList);
                } else if (TXN_LOG_PAYLOAD_REQUEST.equalsIgnoreCase(
                        interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getFmwTxnPayloadReq())
                        || ServerConstants.APPZILLON_ROOT_MAIL_TYPE.equals(pMessage.getIntfDtls().getType())) {
                    reqLdRecList = Utils.getPayloadList(encryptedRequest, pMessage);
                    persistLdRecs(reqLdRecList);
                } else if (TXN_LOG_PAYLOAD_RESPONSE.equalsIgnoreCase(
                        interfaceDBMap.get(pMessage.getHeader().getInterfaceId()).getFmwTxnPayloadReq())
                        || ServerConstants.APPZILLON_ROOT_MAIL_TYPE.equals(pMessage.getIntfDtls().getType())) {
                    resLdRecList = Utils.getPayloadList(encryptedResponse, pMessage);
                    persistLdRecs(resLdRecList);
                }
            }

            persistInFmwTxnDetail(pMessage, lSmlgFmwTxnDetail, reqLdRecList, resLdRecList);

        } else {
            LOG.debug("{} Frameworks Transaction logging is bypassed", LOGGER_PREFIX_DOMAIN);
        }
    }

    private void persistInFmwTxnDetail(Message pMessage, TbAslgFmwTxnDetail lSmlgFmwTxnDetail, List<LargeData> reqLdRecList, List<LargeData> resLdRecList) {
        lSmlgFmwTxnDetail.setReqLdRefNo(!reqLdRecList.isEmpty() ? reqLdRecList.get(0).getRefNo() : "");
        lSmlgFmwTxnDetail.setReqNoRecs(reqLdRecList.size());

        lSmlgFmwTxnDetail.setResLdRefNo(!resLdRecList.isEmpty() ? resLdRecList.get(0).getRefNo() : "");
        lSmlgFmwTxnDetail.setResNoRecs(resLdRecList.size());
        String status = "";
        status = getStatus(pMessage, status);
        lSmlgFmwTxnDetail.setStatus(status);
        cTbAslgFmwTxnDetailRepo.save(lSmlgFmwTxnDetail);
    }

    private String getStatus(Message pMessage, String status) {
        if (pMessage.getResponseObject().getResponseJson().has(ServerConstants.SUCCESS))
            status = "S";
        else if (pMessage.getResponseObject().getResponseJson().has(ServerConstants.ERROR)) {
            status = "F";
        }
        return status;
    }
}
