package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAslgTxnDetail;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAslgTxnDetailRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAstpLdRecsRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAsnfTxnLogRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.iexceed.appzillon.domain.utils.Constants.*;

@Named("GenerateReportService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class GenerateReport {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            GenerateReport.class.getName());

    @Inject
    private TbAslgTxnDetailRepository txnDetailsRepo;

    @Inject
    private TbAsnfTxnLogRepository txnLogRepo;

    @Inject
    private TbAstpLdRecsRepository tbAstpLdRecsRepo;

    public static String getDurationBreakdown(long obj) {
        long hours = TimeUnit.MILLISECONDS.toHours(obj) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(obj) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(obj) % 60;
        long milliseconds = obj % 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    public void loginReport(Message pMessage) {
        JSONObject lResponse = null;
        try {
            LOG.debug("{} Request to generate login report  recieved", ServerConstants.LOGGER_PREFIX_DOMAIN);
            JSONObject lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLONLOGINREPORTREQUEST);

            String startDateString = lRequest.get(ServerConstants.START_DATE).toString();
            String endDateString = lRequest.get(ServerConstants.END_DATE).toString();
            List<Object[]> result = null;
            DateFormat formatter = new SimpleDateFormat(ServerConstants.APPZILLON_DATE_FORMAT);
            String appId = lRequest.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();

            if (appId == null || appId.isEmpty()) {
                appId = ServerConstants.PERCENT;
            }

            if ((startDateString != null && !startDateString.isEmpty())
                    && (endDateString != null && !endDateString.isEmpty())) {
                Timestamp stDate = null;
                Timestamp enDate = null;
                stDate = getTimestamp(stDate, formatter, startDateString);
                enDate = getTimestamp(enDate, formatter, endDateString);
                result = txnDetailsRepo.findLoginReportDetailsBetweenDate(stDate, enDate, appId);

            } else if ((startDateString == null || startDateString.isEmpty())
                    && (endDateString != null && !endDateString.isEmpty())) {
                Timestamp enDate = null;
                enDate = getTimestamp(enDate, formatter, endDateString);
                result = txnDetailsRepo.findLoginReportDetailsBeforeDate(enDate, appId);
            } else if ((endDateString == null || endDateString.isEmpty())
                    && (startDateString != null && !startDateString.isEmpty())) {
                Timestamp stDate = null;
                stDate = getTimestamp(stDate, formatter, startDateString);
                result = txnDetailsRepo.findLoginReportDetailsAfterDate(stDate, appId);
            } else {
                result = txnDetailsRepo.findLoginReportDetails(appId);
            }

            if (result.isEmpty()) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error("{} No record found ", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }

            JSONArray jsonArray = new JSONArray();
            for (Object[] res : result) {
                JSONObject obj = new JSONObject();
                obj.put(ServerConstants.TOTAL_TXNS, res[0]);
                obj.put(ServerConstants.NOOFLOGINS, res[1]);
                obj.put(ServerConstants.NUM_DISTINCT_LOGINS, res[2]);
                obj.put(ServerConstants.DATE, res[5] + "/" + res[4] + "/" + res[3]);

                jsonArray.put(obj);
            }
            lResponse = new JSONObject();
            lResponse.put(ServerConstants.APPZILLONLOGINREPORTRESPONSE, jsonArray);
        } catch (JSONException jsone) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(lResponse);
    }

    private Timestamp getTimestamp(Timestamp inputDate, DateFormat formatter, String inputDateString) {
        try {
            inputDate = new Timestamp(formatter.parse(inputDateString).getTime());
        } catch (ParseException e) {
            LOG.error(PARSE_EXCEPTION, ServerConstants.LOGGER_PREFIX_DOMAIN, e);
        }
        return inputDate;
    }

    public void appUsageReport(Message pMessage) {
        JSONObject lResponse = null;
        try {
            LOG.debug("{} Request to generate app usage report recieved", ServerConstants.LOGGER_PREFIX_DOMAIN);
            JSONObject lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLONAPPUSAGEREPORTREQUEST);

            String appId = lRequest.get(ServerConstants.MESSAGE_HEADER_APP_ID).toString();
            List<Object[]> result = null;
            JSONArray txnArrDet = new JSONArray();
            JSONObject txnRes = null;
            Timestamp stDate = null;
            Timestamp enDate = null;
            DateFormat formatter = new SimpleDateFormat(ServerConstants.APPZILLON_DATE_FORMAT);

            if (Utils.isNullOrEmpty(appId)) {
                appId = ServerConstants.PERCENT;
                LOG.debug("{} all the app id details will be fetched", ServerConstants.LOGGER_PREFIX_DOMAIN);
            }

            String startDateString = null;
            String endDateString = null;

            if (lRequest.has(ServerConstants.START_DATE)) {
                startDateString = lRequest.get(ServerConstants.START_DATE).toString();
            }
            if (lRequest.has(ServerConstants.END_DATE)) {
                endDateString = lRequest.get(ServerConstants.END_DATE).toString();
            }

            if ((startDateString != null && !startDateString.isEmpty())
                    && (endDateString != null && !endDateString.isEmpty())) {
                stDate = new Timestamp(formatter.parse(startDateString).getTime());
                enDate = new Timestamp(formatter.parse(endDateString).getTime());
                result = txnDetailsRepo.findTxnAppUsageDetailsByDate(appId, stDate, enDate);
            } else if ((startDateString == null || startDateString.isEmpty())
                    && (endDateString != null && !endDateString.isEmpty())) {
                endDateString = lRequest.get(ServerConstants.END_DATE).toString();
                enDate = new Timestamp(formatter.parse(endDateString).getTime());
                result = txnDetailsRepo.findTxnAppUsageDetailsBeforeDate(appId, enDate);
            } else if ((startDateString != null && !startDateString.isEmpty())
                    && (endDateString == null || endDateString.isEmpty())) {
                startDateString = lRequest.get(ServerConstants.START_DATE).toString();
                stDate = new Timestamp(formatter.parse(startDateString).getTime());
                result = txnDetailsRepo.findTxnAppUsageDetailsAfterDate(appId, stDate);
            }

            if (CollectionUtils.isEmpty(result)) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error("{} No record found ", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }

            for (Object[] obj : result) {
                txnRes = new JSONObject();
                txnRes.put(ServerConstants.INTERFACE_DESC, obj[0]);
                txnRes.put(ServerConstants.TXN_STAT, obj[1]);
                txnRes.put(ServerConstants.MIN, getDurationBreakdown(((BigInteger) obj[2]).longValue()));
                txnRes.put(ServerConstants.MAX, getDurationBreakdown(((BigInteger) obj[3]).longValue()));
                txnRes.put(ServerConstants.AVG, getDurationBreakdown(((Double) obj[4]).longValue()));
                txnRes.put(ServerConstants.COUNT, obj[5]);
                txnArrDet.put(txnRes);
            }


            lResponse = new JSONObject();
            lResponse.put(ServerConstants.APPZILLONAPPUSAGEREPORTRESPONSE, txnArrDet);
        } catch (JSONException e) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        } catch (ParseException e) {
            LOG.error(PARSE_EXCEPTION, ServerConstants.LOGGER_PREFIX_DOMAIN, e);
        }
        pMessage.getResponseObject().setResponseJson(lResponse);
    }

    public void searchTxn(Message pMessage) {
        JSONObject lResponse = null;
        try {
            LOG.debug("{} Request to search for transactions recieved", ServerConstants.LOGGER_PREFIX_DOMAIN);
            JSONObject lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLONSEARCHTXNLOGGINGREQUEST);
            final String fappId;
            final String fuserId;
            final String fiId;
            final String fdeviceId;
            final String fTxnStatus;
            SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_TIME_FORMAT);
            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID).isEmpty()
                    && lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID).isEmpty()
                    && lRequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).isEmpty()
                    && lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID).isEmpty()) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_009));
                dexp.setCode(DomainException.Code.APZ_DM_009.toString());
                dexp.setPriority("1");
                LOG.error("{} Search columns values not found", ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }
            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID).isEmpty()) {
                fappId = ServerConstants.PERCENT;
            } else {
                fappId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            }

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID).isEmpty()) {
                fuserId = ServerConstants.PERCENT;
            } else {
                fuserId = lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
            }

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID).isEmpty()) {
                fiId = ServerConstants.PERCENT;
            } else {
                fiId = lRequest.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
            }

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID).isEmpty()) {
                fdeviceId = ServerConstants.PERCENT;
            } else {
                fdeviceId = lRequest.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID);
            }

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_STATUS).isEmpty()) {
                fTxnStatus = ServerConstants.PERCENT;
            } else {
                fTxnStatus = lRequest.getString(ServerConstants.MESSAGE_HEADER_STATUS);
            }

            String startDateString = null;
            String endDateString = null;
            List<Object[]> recordslist = null;
            Timestamp stDate = null;
            Timestamp enDate = null;
            DateFormat formatter = new SimpleDateFormat(ServerConstants.APPZILLON_DATE_FORMAT);

            if (lRequest.has(ServerConstants.START_DATE)) {
                startDateString = lRequest.get(ServerConstants.START_DATE).toString();
            }
            if (lRequest.has(ServerConstants.END_DATE)) {
                endDateString = lRequest.get(ServerConstants.END_DATE).toString();
            }

            if ((startDateString != null && !startDateString.isEmpty())
                    && (endDateString != null && !endDateString.isEmpty())) {
                stDate = new Timestamp(formatter.parse(startDateString).getTime());
                enDate = new Timestamp(formatter.parse(endDateString).getTime());
                enDate = Utils.modifyEndDate(enDate);
                recordslist = txnDetailsRepo.findTransactionDetailsBetweenDateRange(fiId, stDate, enDate, fappId,
                        fuserId, fdeviceId, fTxnStatus, PageRequest.of(0, 1000));
            } else if ((startDateString == null || startDateString.isEmpty())
                    && (endDateString != null && !endDateString.isEmpty())) {
                endDateString = lRequest.get(ServerConstants.END_DATE).toString();
                enDate = new Timestamp(formatter.parse(endDateString).getTime());
                enDate = Utils.modifyEndDate(enDate);
                recordslist = txnDetailsRepo.findTransactionDetailsBeforeDate(fiId, enDate, fappId, fuserId, fdeviceId,
                        fTxnStatus, PageRequest.of(0, 1000));
            } else if ((startDateString != null && !startDateString.isEmpty())
                    && (endDateString == null || endDateString.isEmpty())) {
                startDateString = lRequest.get(ServerConstants.START_DATE).toString();
                stDate = new Timestamp(formatter.parse(startDateString).getTime());
                recordslist = txnDetailsRepo.findTransactionDetailsAfterDate(fiId, stDate, fappId, fuserId, fdeviceId,
                        fTxnStatus, PageRequest.of(0, 1000));
            }
            JSONArray lArr = new JSONArray();
            JSONObject recobj;
            if (recordslist.isEmpty()) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error(NO_RECORD_FOUND, ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }

            for (Object[] recObj : recordslist) {
                recobj = new JSONObject();
                recobj.put(ServerConstants.TRANSACTION_ID, recObj[0]);
                recobj.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, recObj[1]);
                recobj.put(ServerConstants.MESSAGE_HEADER_APP_ID, recObj[2]);
                recobj.put(ServerConstants.MESSAGE_HEADER_USER_ID, recObj[3]);
                recobj.put(ServerConstants.START_TIME, sdf.format(recObj[4]));
                recobj.put(ServerConstants.END_TIME, sdf.format(recObj[5]));

                Timestamp stTime = (Timestamp) recObj[4];
                Timestamp endTime = (Timestamp) recObj[5];
                long stTimeMilliseconds = stTime.getTime();
                long endTimeMilliseconds = endTime.getTime();
                long diff = endTimeMilliseconds - stTimeMilliseconds;

                recobj.put(ServerConstants.PROCESSING_TIME, getDurationBreakdown(diff));
                recobj.put(ServerConstants.STATUS, recObj[6]);
                recobj.put(ServerConstants.INTERFACE_DESC, recObj[7]);

                if (recObj[8] != null) {
                    recobj.put(ServerConstants.EXT_START_TIME, sdf.format(recObj[8]));
                } else {
                    recobj.put(ServerConstants.EXT_START_TIME, recObj[8]);
                }
                if (recObj[9] != null) {
                    recobj.put(ServerConstants.EXT_END_TIME, sdf.format(recObj[9]));
                } else {
                    recobj.put(ServerConstants.EXT_END_TIME, recObj[9]);
                }

                lArr.put(recobj);
            }

            JSONObject response = new JSONObject();
            response.put(ServerConstants.DETAIL_SCREEN, lArr);

            lResponse = new JSONObject();
            lResponse.put(ServerConstants.APPZILLONSEARCHTXNLOGGINGRESPONSE, response);

        } catch (JSONException e) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        } catch (ParseException e) {
            LOG.error("ParseException", e);
        }
        pMessage.getResponseObject().setResponseJson(lResponse);
    }

    public void getReqRes(Message pMessage) {
        JSONObject reqAndResp = null;
        JSONObject lResponse = null;
        try {
            LOG.debug("{} Request to get transaction request and response  recieved",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            JSONObject lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLONGETREQRESPREQUEST);
            LOG.debug("{} transaction Id found {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    lRequest.getString(ServerConstants.TRANSACTION_ID));
            String txnref = lRequest.getString(ServerConstants.TRANSACTION_ID);
            Optional<TbAslgTxnDetail> recordOpt = txnDetailsRepo.findById(txnref);
            if (recordOpt.isPresent()) {
                TbAslgTxnDetail tbAslgTxnDetail = recordOpt.get();
                String request = "";
                String resp = "";
                String key = tbAslgTxnDetail.getAppId() + tbAslgTxnDetail.getUserId() + tbAslgTxnDetail.getDeviceId();
                /*
                 * Request string is split for 10 different columns and inserted into the data
                 * base along with the size Changes made by Samy on 20/02/2015
                 */
                StringBuilder requestPayload = new StringBuilder();
                if (tbAslgTxnDetail.getReqNoRecs() > 0) {
                    requestPayload.append(concatLDRecs(tbAslgTxnDetail.getReqLdRefNo()));
                    request = AppzillonAESUtils.decryptString(key, requestPayload.toString());
                }

                StringBuilder responseString = new StringBuilder();
                if (tbAslgTxnDetail.getResNoRecs() > 0) {
                    responseString.append(concatLDRecs(tbAslgTxnDetail.getResLdRefNo()));
                    resp = AppzillonAESUtils.decryptString(key, responseString.toString());
                }

                reqAndResp = new JSONObject();
                reqAndResp.put("request", request);
                reqAndResp.put("response", resp);
            }

            lResponse = new JSONObject();
            lResponse.put(ServerConstants.APPZILLONGETREQRESPRESPONSE, reqAndResp);
        } catch (JSONException e) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(lResponse);
    }

    private StringBuilder concatLDRecs(String refNo) {
        StringBuilder payload = new StringBuilder();
        List<Object[]> addrecords = tbAstpLdRecsRepo.findLdRecsByRefNo(refNo);
        for (Object[] obj : addrecords) {
            for (Object chunk : obj) {
                payload.append(chunk != null ? chunk : "");
            }
        }
        return payload;
    }

    public void getMsgStatDetails(Message pMessage) {
        LOG.debug("{} Request to get Message stats request recieved", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lResponse = null;
        JSONArray txnArrDet = new JSONArray();
        JSONObject txnRes = null;
        List<Object[]> txnlist = null;
        try {
            JSONObject lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLONMSGSTATSREPORTREQUEST);

            String appId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String startDateString = lRequest.get(ServerConstants.START_DATE).toString();
            String endDateString = lRequest.get(ServerConstants.END_DATE).toString();
            DateFormat formatter = new SimpleDateFormat(ServerConstants.APPZILLON_DATE_FORMAT);
            DateFormat customFormatter = new SimpleDateFormat(ServerConstants.DATE_FORMAT);
            Timestamp stDate = null;
            Timestamp enDate = null;
            stDate = getTimestamp(stDate, formatter, startDateString);
            enDate = getTimestamp(enDate, formatter, endDateString);

            txnlist = txnLogRepo.findNotificationsCount(appId, stDate, enDate);

            LOG.debug("{} Message stat size : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, txnlist.size());

            for (Object[] obj : txnlist) {
                txnRes = new JSONObject();
                String dateString = customFormatter.format(obj[0]);
                txnRes.put(ServerConstants.DATE, dateString);
                txnRes.put(ServerConstants.COUNT, obj[1]);
                txnRes.put(ServerConstants.JSON_OS, obj[2]);
                txnArrDet.put(txnRes);
            }

            if (txnlist.isEmpty()) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error(NO_RECORD_FOUND, ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }
            lResponse = new JSONObject();
            lResponse.put(ServerConstants.APPZILLONMSGSTATSREPORTRSPONSE, txnArrDet);

        } catch (JSONException e) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(lResponse);
    }

    public void getCustomerOverview(Message pMessage) {
        LOG.debug("{} Request to get customer overview service request recieved", ServerConstants.LOGGER_PREFIX_DOMAIN);
        final String appId;
        final String userId;
        JSONObject lResponse = null;
        JSONArray txnArrDet = new JSONArray();
        try {
            JSONObject lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_CUSTOMER_REQ);

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID).isEmpty()) {
                appId = ServerConstants.PERCENT;
            } else {
                appId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            }

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID).isEmpty()) {
                userId = ServerConstants.PERCENT;
            } else {
                userId = lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
            }

            List<Object[]> custlist = txnDetailsRepo.findCustomerOverview(appId, userId);

            List<Object[]> loclist = txnDetailsRepo.findCustomerLocationDetails(appId, userId);

            JSONObject txnRes = new JSONObject();
            for (Object[] obj : custlist) {
                txnRes.put(ServerConstants.MESSAGE_HEADER_USER_ID, obj[0]);
                txnRes.put(ServerConstants.MIN_SESSION_TIME, getDurationBreakdown(((BigInteger) obj[1]).longValue()));
                txnRes.put(ServerConstants.MAX_SESSION_TIME, getDurationBreakdown(((BigInteger) obj[2]).longValue()));
                txnRes.put(ServerConstants.AVG_SESSION_TIME, getDurationBreakdown(((Double) obj[3]).longValue()));
                txnRes.put(ServerConstants.TOTAL_TXNS, obj[4]);
                txnRes.put(ServerConstants.DISTINCT_TXNS, obj[5]);
                txnRes.put(ServerConstants.LOGINS, obj[6]);
                txnRes.put(ServerConstants.DISTINCT_LOGINS, obj[7]);
            }

            for (Object[] obj : loclist) {
                txnRes.put(ServerConstants.LONGITUDE, obj[2]);
                txnRes.put(ServerConstants.LATITUDE, obj[4]);
                txnRes.put(ServerConstants.FORAMATTED_ADDRESS, obj[5]);
            }
            txnArrDet.put(txnRes);

            if (custlist.isEmpty() || loclist.isEmpty()) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error(NO_RECORD_FOUND, ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }
            lResponse = new JSONObject();
            lResponse.put(ServerConstants.APPZILLON_ROOT_CUSTOMER_RES, txnArrDet);

        } catch (JSONException e) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(lResponse);
    }

    public void getCustomerLocationDetail(Message pMessage) {
        LOG.debug("{} Request to getCustomerLocationDetail service request recieved",
                ServerConstants.LOGGER_PREFIX_DOMAIN);
        final String appId;
        final String userId;
        JSONObject lResponse = null;
        JSONObject txnRes = null;
        JSONArray txnArrDet = new JSONArray();
        try {
            JSONObject lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_CUSTOMER_LOC_REQ);

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID).isEmpty()) {
                appId = ServerConstants.PERCENT;
            } else {
                appId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            }

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID).isEmpty()) {
                userId = ServerConstants.PERCENT;
            } else {
                userId = lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
            }

            List<Object[]> custLoclist = txnDetailsRepo.findCustomerAllLocDetails(appId, userId);

            for (Object[] result : custLoclist) {
                txnRes = new JSONObject();
                txnRes.put(ServerConstants.MESSAGE_HEADER_ORIGINATION, result[1]);
                txnRes.put(ServerConstants.LATITUDE, result[2]);
                txnRes.put(ServerConstants.LONGITUDE, result[3]);
                txnRes.put(ServerConstants.SUBLOCALITY, result[4]);
                txnRes.put(ServerConstants.ADMIN_AREA_LVL_1, result[5]);
                txnRes.put(ServerConstants.ADMIN_AREA_LVL_2, result[6]);
                txnRes.put(ServerConstants.COUNTRY, result[7]);
                txnRes.put(ServerConstants.FORAMATTED_ADDRESS, result[8]);
                txnArrDet.put(txnRes);
            }

            if (custLoclist.isEmpty()) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error(NO_RECORD_FOUND, ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }
            lResponse = new JSONObject();
            lResponse.put(ServerConstants.APPZILLON_ROOT_CUSTOMER_LOC_RES, txnArrDet);

        } catch (JSONException e) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        pMessage.getResponseObject().setResponseJson(lResponse);
    }

    public void getCustomerDetailsReport(Message pMessage) {
        LOG.debug("{} Request to get customer Detail Report service request recieved",
                ServerConstants.LOGGER_PREFIX_DOMAIN);
        final String appId;
        final String userId;
        Timestamp stDate = null;
        Timestamp enDate = null;
        JSONObject lResponse = null;
        JSONArray txnArrDet = new JSONArray();
        List<Object[]> loclist = null;
        DateFormat formatter = new SimpleDateFormat(ServerConstants.APPZILLON_DATE_FORMAT);
        DateFormat customFormatter = new SimpleDateFormat(ServerConstants.DATE_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(ServerConstants.SIMPLE_DATE_TIME_FORMAT);

        try {
            JSONObject lRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_CUSTOMER_DETAILS_REQ);

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID).isEmpty()) {
                appId = ServerConstants.PERCENT;
            } else {
                appId = lRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            }

            if (lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID).isEmpty()) {
                userId = ServerConstants.PERCENT;
            } else {
                userId = lRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID);
            }
            String startDateString = null;
            String endDateString = null;

            if (lRequest.has(ServerConstants.START_DATE)) {
                startDateString = lRequest.get(ServerConstants.START_DATE).toString();
            }
            if (lRequest.has(ServerConstants.END_DATE)) {
                endDateString = lRequest.get(ServerConstants.END_DATE).toString();
            }

            if ((startDateString != null && !startDateString.isEmpty())
                    && (endDateString != null && !endDateString.isEmpty())) {
                stDate = new Timestamp(formatter.parse(startDateString).getTime());
                enDate = new Timestamp(formatter.parse(endDateString).getTime());
                loclist = txnDetailsRepo.findCustomerDetailReport(appId, userId, stDate, enDate);

            } else if ((startDateString == null || startDateString.isEmpty())
                    && (endDateString != null && !endDateString.isEmpty())) {
                endDateString = lRequest.get(ServerConstants.END_DATE).toString();
                enDate = new Timestamp(formatter.parse(endDateString).getTime());
                loclist = txnDetailsRepo.findCustomerDetailReportBeforeDate(appId, userId, enDate);

            } else if ((startDateString != null && !startDateString.isEmpty())
                    && (endDateString == null || endDateString.isEmpty())) {
                startDateString = lRequest.get(ServerConstants.START_DATE).toString();
                stDate = new Timestamp(formatter.parse(startDateString).getTime());
                loclist = txnDetailsRepo.findCustomerDetailReportAfterDate(appId, userId, stDate);
            }

            if (CollectionUtils.isEmpty(loclist)) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                LOG.error(NO_RECORD_FOUND, ServerConstants.LOGGER_PREFIX_DOMAIN, dexp);
                throw dexp;
            }

            for (Object[] obj : loclist) {
                JSONObject txnRes = new JSONObject();
                txnRes.put(ServerConstants.ACCESS_DATE, customFormatter.format(obj[0]));
                txnRes.put(ServerConstants.START_TM, sdf.format(obj[1]));
                txnRes.put(ServerConstants.END_TM, sdf.format(obj[2]));
                txnRes.put(ServerConstants.TOTAL_TXNS, obj[3]);
                txnRes.put(ServerConstants.DISTINCT_TXNS, obj[4]);
                txnRes.put(ServerConstants.LATITUDE, obj[5]);
                txnRes.put(ServerConstants.LONGITUDE, obj[6]);
                txnRes.put(ServerConstants.FORAMATTED_ADDRESS, obj[7]);
                long diff = ((Timestamp) obj[2]).getTime() - ((Timestamp) obj[1]).getTime();
                txnRes.put(ServerConstants.TOTAL_TIME, getDurationBreakdown(diff));
                txnArrDet.put(txnRes);
            }


            lResponse = new JSONObject();
            lResponse.put(ServerConstants.APPZILLON_ROOT_CUSTOMER_DETAILS_RES, txnArrDet);
            pMessage.getResponseObject().setResponseJson(lResponse);

        } catch (JSONException e) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, e);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(e.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        } catch (ParseException e) {
            LOG.error(PARSE_EXCEPTION, ServerConstants.LOGGER_PREFIX_DOMAIN, e);
        }
    }
}
