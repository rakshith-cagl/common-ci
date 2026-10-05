package com.iexceed.appzillon.services;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.StringHelper;
import com.iexceed.appzillon.utils.sql.DebugLevel;
import com.iexceed.appzillon.utils.sql.StatementFactory;
import org.apache.camel.spring.SpringCamelContext;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.iexceed.appzillon.utils.Constants.*;

public class LOVService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    LOVService.class.getName());

    int lTotalnoOfPages = 0;
    int lTotalnoofrecords = 0;
    String lResultColsNames = null;
    String lResultColsDataTypes = null;
    String lCurrentPage = null;
    String lFilterColmnValues = null;
    String lBinVarValues = null;
    ResultSet lRs = null;
    JSONObject lResJson = null;
    JSONObject lRespJSON = null;
    JSONArray lRespJsonArray = null;
    String lRecordsPerPage = null;
    String lElementID = null;
    String lOrderByCol = null;
    String lOrderByType = null;
    Connection lConnection = null;
    PreparedStatement lPreparedStatement = null;
    DataSource dataSource = null;
    DebugLevel debug = DebugLevel.ON;

    @SuppressWarnings("resource")
    public String getVariablesList(Message pMessage) throws ExternalServicesRouterException {
        LOG.debug("{} inside getVariablesList()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        try {
            LOG.debug("{} ****************************** getVariablesList *****************************", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            lRespJsonArray = new JSONArray();
            String lAppId = pMessage.getHeader().getAppId();
            String lInterfaceId = pMessage.getHeader().getInterfaceId();
            LOG.debug("{} LOVService- pHeaderMap: {} and pPayload: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader(), pMessage.getRequestObject().getRequestJson());

            JSONObject lLOVPayLoad = pMessage.getRequestObject().getRequestJson();

            String lLOVReq = lLOVPayLoad.get(lInterfaceId + REQUEST).toString();

            String lQueryId = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ID);

            lElementID = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ELEMENT_ID);

            lResultColsNames = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_RESULT_SET_COLUMN_NAMES);

            lResultColsDataTypes = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_RESULT_SET_COLUMN_DATA_TYPES);

            lRecordsPerPage = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_RECORDS_PER_PAGE);

            lCurrentPage = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_CURRENT_PAGE_NO);

            lFilterColmnValues = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_FILTER_COLUMNS_VALUES);

            lBinVarValues = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_BIND_VARIABLES_VALUES);

            LOG.debug("lQueryId: {}, lElementID: {}, lResultColsNames: {}, lResultColsDataTypes: {}", lQueryId, lElementID, lResultColsNames, lResultColsDataTypes);
            LOG.debug("lRecordsPerPage: {}, lCurrentPage: {}, lFilterColmnValues: {}, lBinVarValues: {}", lRecordsPerPage, lCurrentPage, lFilterColmnValues, lBinVarValues);


            if (new JSONObject(lLOVReq).has(ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME)) {
                lOrderByCol = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME);
            } else {
                LOG.debug(NOT_FOUND, ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME);
            }
            LOG.debug("{} lOrderBy_col: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lOrderByCol);

            if (new JSONObject(lLOVReq).has(ServerConstants.LOV_QUERY_ORDER_BY_TYPE)) {
                lOrderByType = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ORDER_BY_TYPE);
            } else {
                LOG.debug(NOT_FOUND, ServerConstants.LOV_QUERY_ORDER_BY_TYPE);
            }
            LOG.debug("{} lOrderBy_type: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lOrderByType);

            JSONObject lCachedLOV = null;
            String lKey = lAppId + lInterfaceId + lQueryId;

            pMessage.getHeader().setServiceType(ServerConstants.APPZDBFETCHLOVREQUEST);
            JSONObject pResqObject = new JSONObject();
            LOG.debug("{} Before putting query id - lQueryId: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lQueryId);
            pResqObject.put(ServerConstants.LOV_QUERY_ID, lQueryId);
            LOG.debug("{} After building request JSON - pResqObject: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pResqObject);
            pMessage.getRequestObject().setRequestJson(pResqObject);
            DomainStartup.getInstance().processRequest(pMessage);

            JSONObject lLovDetails = pMessage.getResponseObject().getResponseJson();

            if (!lLovDetails.has(ServerConstants.LOV_BIND_VAR_DATA_TYPE)) {
                lLovDetails.put(ServerConstants.LOV_BIND_VAR_DATA_TYPE, "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_BIND_VAR_COLS)) {
                lLovDetails.put(ServerConstants.LOV_BIND_VAR_COLS, "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_FILTER_VAR_COLS)) {
                lLovDetails.put(ServerConstants.LOV_FILTER_VAR_COLS,
                        "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_ORDER_BY_COLS)) {
                lLovDetails.put(ServerConstants.LOV_ORDER_BY_COLS, "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_ORDER_BY_TYPE)) {
                lLovDetails.put(ServerConstants.LOV_ORDER_BY_TYPE, "");
            }

            LOG.debug("{} getVariablesList - lLovDetails: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lLovDetails);
            if (!compareBindVariables(lCachedLOV, lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE), lBinVarValues, lInterfaceId, lAppId, lQueryId)
                    || !compareFilterVariables(lCachedLOV, lLovDetails.getString(ServerConstants.LOV_FILTER_VAR_COLS),
                    lFilterColmnValues, lInterfaceId, lAppId, lQueryId)) {
                InitialContext lCtx = new InitialContext();
                dataSource = (javax.sql.DataSource) lCtx.lookup(lLovDetails.getString(ServerConstants.LOV_DATA_SOURCE));

                lConnection = dataSource.getConnection();
                LOG.debug("{} Connection obtained from data Source....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

                String lBuiltQuery = buildFilterCriteriaQuery(
                        lLovDetails.getString(ServerConstants.LOV_FILTER_VAR_COLS),
                        lFilterColmnValues,
                        lLovDetails.getString(ServerConstants.LOV_QUERY_STRING),
                        lLovDetails.getString(ServerConstants.LOV_ORDER_BY_COLS),
                        lLovDetails.getString(ServerConstants.LOV_ORDER_BY_TYPE),
                        lLovDetails.getString(ServerConstants.LOV_QUERY_TYPE));

                if (lLovDetails.getString(ServerConstants.LOV_QUERY_TYPE).equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_SQL)) {

                    lPreparedStatement = appendBindVariables(lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE),
                            lBinVarValues, lBuiltQuery, lConnection, lAppId);

                    if (debug == DebugLevel.ON) {
                        lPreparedStatement = lConnection.prepareStatement(lPreparedStatement.toString());
                    }
                    Utils.setExtTime(pMessage, "S");
                    lRs = lPreparedStatement.executeQuery();

                    Utils.setExtTime(pMessage, "E");

                    String[] lRespCols = Utils.split(lResultColsNames, ServerConstants.SEPARATOR_PIPE);
                    String[] lRespColsDataTypes = Utils.split(lResultColsDataTypes, ServerConstants.SEPARATOR_PIPE);
                    while (lRs.next()) {
                        lResJson = new JSONObject();
                        if (lRespColsDataTypes.length == lRespCols.length) {
                            for (int i = 0; i < lRespCols.length; i++) {
                                String fColName = lRespCols[i];
                                lResJson = JSONUtils.putJSonObj(lResJson, fColName, lRs.getObject(fColName));
                            }
                            lTotalnoofrecords++;
                            lRespJsonArray.put(lResJson);
                        } else {
                            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_013.toString());
                            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_013));
                            exsrvcallexp.setPriority("1");
                            lPreparedStatement.close();
                            throw exsrvcallexp;
                        }
                    }
                    lPreparedStatement.close();
                    LOG.debug("{} Total number of records after: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTotalnoofrecords);

                }
                if (lTotalnoofrecords < 1) {
                    LOG.error("{} Total no of records fetched are {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTotalnoofrecords);
                    ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                    exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_038.toString());
                    exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_038));
                    exsrvcallexp.setPriority("1");
                    throw exsrvcallexp;
                }

                JSONObject lCachingLOV = new JSONObject();
                lCachingLOV.put(ServerConstants.LOV_BIND_VARIABLES_DATA_TYPE, lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE));
                lCachingLOV.put(ServerConstants.LOV_BIND_VARIABLES_VALUES, lBinVarValues);
                lCachingLOV.put(ServerConstants.LOV_FILTER_VARIABLES_COLS, lLovDetails.getString(ServerConstants.LOV_FILTER_VAR_COLS));
                lCachingLOV.put(ServerConstants.LOV_FILTER_VARIABLES_VALUES, lFilterColmnValues);
                lCachingLOV.put(ServerConstants.LOV_QUERY_RESULT, lRespJsonArray);
                JSONObject lLOVObject = new JSONObject();
                lLOVObject.put(lKey, lCachingLOV);

                JSONObject lRespJson = getPagedResponse(lRespJsonArray, lTotalnoofrecords, Integer.parseInt(lCurrentPage), Integer.parseInt(lRecordsPerPage));

                lResJson = new JSONObject();
                lResJson.put(ServerConstants.LOV_QUERY_RESULT_NODE_NAME, lRespJson);

                lTotalnoOfPages = calculateTotalNoOfRows(lTotalnoofrecords, Integer.parseInt(lRecordsPerPage));
                LOG.debug("{} result set - lTotalnoOfPages: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTotalnoOfPages);
                lResJson.put(ServerConstants.LOV_QUERY_TOTAL_NO_OF_PAGES, lTotalnoOfPages);
            }
            lRespJSON = new JSONObject();
            lRespJSON.put(lInterfaceId + "Response", lResJson);

        } catch (ExternalServicesRouterException servicesex) {
            LOG.error(CAUGHT_EXTERNAL_SERVICE_ROUTER_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            throw servicesex;
        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_001.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_001));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } finally {
            try {
                if (lConnection != null) {
                    lConnection.close();
                }
                if (lPreparedStatement != null) {
                    lPreparedStatement.close();
                }
            } catch (Exception ex) {
                LOG.error(LOG_EXCEPTION, ex);
            }
        }

        return lRespJSON.toString();

    }

    /*
     * Below method is added by Abhishek on 31-10-2014 as part of Appzillon 3.1 development
     * API to Fetch Static Data - 3.1 - 48
     * Start
     */
    @SuppressWarnings("resource")
    public JSONObject getVariablesListStatic(Message pMessage) throws ExternalServicesRouterException {
        LOG.debug("{} inside getVariablesListStatic()..", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        try {
            LOG.debug("{} ****************************** getVariablesListStatic *****************************", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            lRespJsonArray = new JSONArray();
            String lAppId = pMessage.getHeader().getAppId();
            String lInterfaceId = pMessage.getHeader().getInterfaceId();
            LOG.debug("{} LOVService- pHeaderMap: {} and pPayload: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader(), pMessage.getRequestObject().getRequestJson());

            JSONObject lLOVPayLoad = pMessage.getRequestObject().getRequestJson();
            String lLOVReq = lLOVPayLoad.get(lInterfaceId + REQUEST).toString();

            JSONObject lLOVReqobj = ((JSONObject) lLOVPayLoad.get(lInterfaceId + REQUEST)).getJSONObject(ServerConstants.LOV_REQOBJ);
            String lquery = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_QUERY);
            LOG.debug("{} lquery: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lquery);

            String ljndi = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_JNDI);
            String lbindcolumns = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_BINDCOLUMNS);
            String lbindvardatatype = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_BIND_VAR_DATA_TYPE);

            lResultColsNames = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_QUERY_RESULT_SET_COLUMN_NAMES);
            lResultColsDataTypes = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_QUERY_RESULT_SET_COLUMN_DATA_TYPES);
            lBinVarValues = JSONUtils.getJsonValueFromKey(lLOVReqobj.toString(), ServerConstants.LOV_QUERY_BIND_VARIABLES_VALUES);

            LOG.debug("{} ljndi: {}, lbindcolumns: {}, lbindvardatatype: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ljndi, lbindcolumns, lbindvardatatype);
            LOG.debug("{} lResultColsDataTypes: {}, lResultColsNames: {}, lBinVarValues: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lResultColsDataTypes, lResultColsNames, lBinVarValues);
            if (new JSONObject(lLOVReq).has(ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME)) {
                lOrderByCol = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME);
            } else {
                LOG.debug(NOT_FOUND, ServerConstants.LOV_QUERY_ORDER_BY_COLUMN_NAME);
            }

            if (new JSONObject(lLOVReq).has(ServerConstants.LOV_QUERY_ORDER_BY_TYPE)) {
                lOrderByType = JSONUtils.getJsonValueFromKey(lLOVReq, ServerConstants.LOV_QUERY_ORDER_BY_TYPE);
            } else {
                LOG.debug(NOT_FOUND, ServerConstants.LOV_QUERY_ORDER_BY_TYPE);
            }
            LOG.debug("{} lOrderBy_col: {}, lOrderBy_type: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lOrderByCol, lOrderByType);

            JSONObject lCachedLOV = null;
            pMessage.getHeader().setServiceType(ServerConstants.APPZDBFETCHLOVREQUEST);
            JSONObject pResqObject = new JSONObject();
            pMessage.getRequestObject().setRequestJson(pResqObject);
            JSONObject lLovDetails = pMessage.getResponseObject().getResponseJson();

            if (!lLovDetails.has(ServerConstants.LOV_BIND_VAR_DATA_TYPE)) {
                lLovDetails.put(ServerConstants.LOV_BIND_VAR_DATA_TYPE, lbindvardatatype);
            }

            if (!lLovDetails.has(ServerConstants.LOV_BIND_VAR_COLS)) {
                lLovDetails.put(ServerConstants.LOV_BIND_VAR_COLS, lbindcolumns);
            }

            if (!lLovDetails.has(ServerConstants.LOV_FILTER_VAR_COLS)) {
                lLovDetails.put(ServerConstants.LOV_FILTER_VAR_COLS,
                        "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_ORDER_BY_COLS)) {
                lLovDetails.put(ServerConstants.LOV_ORDER_BY_COLS, "");
            }

            if (!lLovDetails.has(ServerConstants.LOV_ORDER_BY_TYPE)) {
                lLovDetails.put(ServerConstants.LOV_ORDER_BY_TYPE, "");
            }

            LOG.debug("{} getVariablesList - lLovDetails: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lLovDetails);
            if (!compareBindVariables(lCachedLOV, lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE), lBinVarValues, lInterfaceId, lAppId, null)
                    || !compareFilterVariables(lCachedLOV, lLovDetails.getString(ServerConstants.LOV_FILTER_VAR_COLS),
                    lFilterColmnValues, lInterfaceId, lAppId, null)) {
                InitialContext lCtx = new InitialContext();
                dataSource = (javax.sql.DataSource) lCtx.lookup(ljndi);

                lConnection = dataSource.getConnection();
                LOG.debug("{} Connection obtained from data Source....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

                lPreparedStatement = appendBindVariables(lLovDetails.getString(ServerConstants.LOV_BIND_VAR_DATA_TYPE),
                        lBinVarValues, lquery, lConnection, lAppId);

                if (debug == DebugLevel.ON) {
                    lPreparedStatement = lConnection.prepareStatement(lPreparedStatement.toString());
                }
                Utils.setExtTime(pMessage, "S");
                lRs = lPreparedStatement.executeQuery();
                Utils.setExtTime(pMessage, "E");

                String[] lRespCols = Utils.split(lResultColsNames, ServerConstants.SEPARATOR_PIPE);
                String[] lRespColsDataTypes = Utils.split(lResultColsDataTypes, ServerConstants.SEPARATOR_PIPE);
                while (lRs.next()) {
                    lResJson = new JSONObject();
                    if (lRespColsDataTypes.length == lRespCols.length) {
                        for (int i = 0; i < lRespCols.length; i++) {
                            String fColName = lRespCols[i];
                            lResJson = JSONUtils.putJSonObj(lResJson, fColName, lRs.getObject(fColName));
                        }
                        lTotalnoofrecords++;
                        lRespJsonArray.put(lResJson);
                    } else {
                        ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                        exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_013.toString());
                        exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_013));
                        exsrvcallexp.setPriority("1");
                        lPreparedStatement.close();
                        throw exsrvcallexp;
                    }
                }
                lPreparedStatement.close();
                LOG.debug("{} Total number of records after: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTotalnoofrecords);


                JSONObject lRespJsonobj = getPagedResponse(lRespJsonArray, lTotalnoofrecords, 1, lTotalnoofrecords);

                lRespJSON = new JSONObject();
                lRespJSON.put(lInterfaceId + "Response", lRespJsonobj);
            }
        } catch (ExternalServicesRouterException servicesex) {
            LOG.error(CAUGHT_EXTERNAL_SERVICE_ROUTER_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            throw servicesex;
        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_001.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_001));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } finally {
            try {
                if (lConnection != null) {
                    lConnection.close();
                }
                if (lPreparedStatement != null) {
                    lPreparedStatement.close();
                }
            } catch (Exception ex) {
                LOG.error(LOG_EXCEPTION, ex);
            }
        }

        return lRespJSON;
    }

    /*
     * End API to Fetch Static Data - 3.1 - 48
     */
    public JSONObject getPagedResponse(JSONArray pCompleteresponseJsonArr,
                                       int pNoOfRows, int pPageno, int pRecordsPerPage) {
        JSONObject lRespJsonObject = null;
        JSONArray lRespJsonArray = null;
        try {
            LOG.debug("{} getPagedResponse - pNoOfRows: {}, pPageno:{}, pRecordsPerPage:{}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pNoOfRows, pPageno, pRecordsPerPage);

            lRespJsonArray = new JSONArray();

            int lLoopStartIndex = 0;
            int lLoopEndIndex = 0;
            if ((pRecordsPerPage != 0 && pPageno != 0)) {
                lLoopStartIndex = pRecordsPerPage * (pPageno - 1);
                lLoopEndIndex = pRecordsPerPage * pPageno;
                LOG.debug("{} getPagedResponse - lLoopStartIndex: {}, lLoopEndIndex: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lLoopStartIndex, lLoopEndIndex);
            } else {

                pRecordsPerPage = pCompleteresponseJsonArr
                        .length();
                LOG.debug("{} current page no is sent empty for Dynamic LOV, hence setting the end index to the max response array length....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                lLoopEndIndex = pCompleteresponseJsonArr.length();
                LOG.debug("{} getPagedResponse - lLoopEndIndex for Dynamic LOV: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lLoopEndIndex);
            }

            int lTotalnoOfPages = calculateTotalNoOfRows(pNoOfRows,
                    pRecordsPerPage);
            LOG.debug("{} Total no of pages - lTotalnoOfPages: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lTotalnoOfPages);

            if (pPageno > lTotalnoOfPages) {
                LOG.error("{} Total number of page is less than the requested page no.", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_005.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_005));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;

            }
            if (pCompleteresponseJsonArr.length() < lLoopEndIndex) {
                lLoopEndIndex = pCompleteresponseJsonArr.length();
                LOG.debug("{} lLoopEndIndex: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lLoopEndIndex);
            }

            for (int i = lLoopStartIndex; i < lLoopEndIndex; i++) {
                lRespJsonObject = new JSONObject();
                lRespJsonObject = (JSONObject) pCompleteresponseJsonArr.get(i);
                lRespJsonArray.put(lRespJsonObject);
            }
            lRespJsonObject = new JSONObject();
            lRespJsonObject.put(
                    ServerConstants.LOV_QUERY_RESULT_NODE_ARRAY_NAME,
                    lRespJsonArray);

        } catch (JSONException jsonex) {
            LOG.warn(LOGGER_JSON_EXCEPTION, jsonex);

            lRespJsonObject = new JSONObject();
            try {
                lRespJsonObject.put(
                        ServerConstants.LOV_QUERY_RESULT_NODE_ARRAY_NAME,
                        lRespJsonArray);
            } catch (JSONException e) {
                LOG.warn("{} Error while adding json array in exception block: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS
                        , e);
            }
        } catch (ExternalServicesRouterException servicesex) {
            LOG.error(CAUGHT_EXTERNAL_SERVICE_ROUTER_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            throw servicesex;
        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, Utils.getStackTrace(ex));
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_006.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_006));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;

        }
        return lRespJsonObject;
    }

    public String buildFilterCriteriaQuery(String pFilterColmns,
                                           String pFilterColmnsValue, String pActualQuery, String pOrderByCol,
                                           String pOrderByType, String pQueryType) {
        StringBuilder lFinalQuery = null;
        String[] lColmnsname = null;
        String[] lColmnsValue = null;
        try {
            lFinalQuery = new StringBuilder();
            lColmnsname = Utils.split(pFilterColmns,
                    ServerConstants.SEPARATOR_PIPE);
            lColmnsValue = Utils.split(pFilterColmnsValue,
                    ServerConstants.SEPARATOR_PIPE);

            LOG.debug("{} buildFilterCriteriaQuery.pFilterColmnsValue : {}, No of columns: {}, No of values: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pFilterColmnsValue, lColmnsname.length, lColmnsValue.length);

            if (lColmnsname.length == lColmnsValue.length
                    && (lColmnsname.length != 0 && lColmnsValue.length != 0)) {
                if (!pQueryType
                        .equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_HQL)) {
                    lFinalQuery
                            .append(ServerConstants.LOV_QUERY_OUTER_QUERY);
                }
                LOG.debug("{} Query after appending Outer query: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lFinalQuery.toString());
                lFinalQuery.append(pActualQuery);
                LOG.debug("{} Query after appending the actual query: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lFinalQuery.toString());
                boolean isWhere = false;
                boolean allEmpty = true;
                for (int i = 0; i < lColmnsname.length; i++) {

                    LOG.debug("{} buildFilterCriteriaQuery - i: {}, lColmnsname[i]: {}, lColmnsValue[i]: {}, lColmnsValue[i].length: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, i
                            , lColmnsname[i]
                            , lColmnsValue[i]
                            , lColmnsValue[i].length());
                    if (lColmnsValue[i] != null && lColmnsValue[i].length() > 0) {
                        allEmpty = false;
                        if (!isWhere) {
                            if (!pQueryType
                                    .equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_HQL)) {
                                lFinalQuery
                                        .append(ServerConstants.LOV_QUERY_OUTER_QUERY_WHERE_CLAUSE);

                            } else {
                                if (lFinalQuery.toString().toLowerCase()
                                        .contains(ServerConstants.WHERE)) {
                                    lFinalQuery.append(ServerConstants.AND);
                                } else {
                                    lFinalQuery.append(ServerConstants.WHERE);
                                }
                            }

                            isWhere = true;
                        }
                        /**
                         * Below changes are made by Vinod as part of 
                         * Querying case sensitive and case insensitive values.
                         * Appzillon 3.1 - 62 -- Start
                         */
                        boolean caseSenstivityReq = false;
                        if (lColmnsValue[i].endsWith("\"") && lColmnsValue[i].startsWith("\"")) {
                            LOG.debug("{} caseSensitivityReq for Column {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lColmnsname[i]);
                            caseSenstivityReq = true;

                        }
                        if (caseSenstivityReq)
                            lFinalQuery.append(lColmnsname[i]);
                        else {
                            lFinalQuery.append("upper(" + lColmnsname[i] + ")");
                        }
                        if (lColmnsValue[i].contains("%")) {
                            LOG.info("{} appending IS LIKE as % found", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                            lFinalQuery.append(" LIKE '");
                        } else {
                            LOG.info("{} appending =  as % not found", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                            lFinalQuery.append(" = '");
                        }
                        if (caseSenstivityReq) {
                            String valueInCot = lColmnsValue[i].substring(1, lColmnsValue[i].length() - 1);
                            LOG.debug("{} value in cots : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, valueInCot);
                            lFinalQuery.append(valueInCot);
                        } else {
                            lFinalQuery.append(lColmnsValue[i].toUpperCase());
                        }
                        /** Appzillon 3.1 - 62 -- END */
                        lFinalQuery.append("'");
                    }
                    LOG.debug("{} Checking based on the column values instead of column names....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                    if (lColmnsValue.length > 1 && lColmnsValue.length - 1 != i
                            && (lColmnsValue[i + 1] != null && lColmnsValue[i + 1].length() > 0) && isWhere) {
                        lFinalQuery.append(" and ");
                    }

                }
                if (allEmpty) {
                    LOG.info("{} allEmpty found true all string are empty String", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                    if (!pQueryType
                            .equalsIgnoreCase(ServerConstants.LOV_QUERY_TYPE_HQL)) {
                        lFinalQuery
                                .append(ServerConstants.LOV_QUERY_OUTER_QUERY_END);
                    }
                }

            } else {
                lFinalQuery.append(pActualQuery);
            }
            if (pOrderByCol != null && pOrderByCol.length() > 0) {
                lFinalQuery
                        .append(ServerConstants.LOV_QUERY_OUTER_QUERY_ORDER_BY_CLAUSE);
                lFinalQuery.append(pOrderByCol);
                lFinalQuery.append(" ");
                lFinalQuery.append(pOrderByType);
            }

        } catch (Exception ex) {
            LOG.error(Utils.getStackTrace(ex));

            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_007.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_007));
            exsrvcallexp.setPriority("1");

            throw exsrvcallexp;

        }
        return lFinalQuery.toString();
    }

    public PreparedStatement appendBindVariables(String pBindVariableDataType,
                                                 String pBindVariableValues, PreparedStatement pPreStatement) {
        PreparedStatement lPreparedStatement = null;
        String[] lBindVarDataTypesArr = null;
        String[] lBindVarValuesArr = null;
        try {
            lPreparedStatement = pPreStatement;
            lBindVarDataTypesArr = Utils.split(pBindVariableDataType,
                    ServerConstants.SEPARATOR_PIPE);

            lBindVarValuesArr = Utils.split(pBindVariableValues,
                    ServerConstants.SEPARATOR_PIPE);

            LOG.debug("{} lBindVarDataTypesArr length : {}, lBindVarValuesArr length : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lBindVarDataTypesArr.length, lBindVarValuesArr.length);

            if (lBindVarDataTypesArr.length == lBindVarValuesArr.length) {
                for (int i = 1; i <= lBindVarDataTypesArr.length; i++) {
                    LOG.debug("{} i: {} , *** lBindVarValuesArr[i-1] : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, i, lBindVarValuesArr[i - 1]);
                    String lBindValue = StringHelper.escapeSQL(lBindVarValuesArr[i - 1]);
                    LOG.debug("{} *** lBindValue : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lBindValue);
                    lPreparedStatement.setObject(i, lBindValue);
                }
            } else {
                lPreparedStatement = pPreStatement;
            }
        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_008.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_008));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return lPreparedStatement;

    }

    public PreparedStatement appendBindVariables(String pBindVariableDataType,
                                                 String pBindVariableValues, String pQuery, Connection pConnection, String pAppId) {
        PreparedStatement lPreparedStatement = null;
        String[] lBindVarDataTypesArr = null;
        String[] lBindVarValuesArr = null;
        DebugLevel debug = DebugLevel.ON;
        try {

            lPreparedStatement = StatementFactory.getStatement(pConnection,
                    pQuery, debug);

            lBindVarDataTypesArr = Utils.split(pBindVariableDataType,
                    ServerConstants.SEPARATOR_PIPE);

            lBindVarValuesArr = Utils.split(pBindVariableValues,
                    ServerConstants.SEPARATOR_PIPE);
            LOG.debug("{} lBindVarDataTypesArr.length: {}, lBindVarValuesArr.length: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lBindVarDataTypesArr.length, lBindVarValuesArr.length);

            if (lBindVarDataTypesArr.length == lBindVarValuesArr.length) {
                for (int i = 1; i <= lBindVarDataTypesArr.length; i++) {
                    String lBindDataType = lBindVarDataTypesArr[i - 1];
                    String lBindValue = StringHelper.escapeSQL(lBindVarValuesArr[i - 1]);
                    LOG.debug("{} i: {}, lBindVarDataTypesArr[i-1]: {}, lBindValue: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, i, lBindVarDataTypesArr[i - 1], lBindValue);
                    if ("string".equalsIgnoreCase(lBindDataType)) {
                        lPreparedStatement.setString(i, lBindValue);
                    } else if ("int".equalsIgnoreCase(lBindDataType)) {
                        lPreparedStatement.setInt(i,
                                Integer.parseInt(lBindValue));
                    } else if ("date".equalsIgnoreCase(lBindDataType)) {
                        String dateFormat = PropertyUtils.getPropValue(pAppId, "datePatternForLogFiles").toString()
                                .trim();
                        SimpleDateFormat formatter = new SimpleDateFormat(
                                dateFormat);
                        Date lBindDate = formatter.parse(lBindValue);
                        lPreparedStatement
                                .setDate(i, (java.sql.Date) lBindDate);
                    }

                }
            }
        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, ex);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_008.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_008));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        }
        return lPreparedStatement;

    }

    public JSONArray getJSonFromHQLList(List<Object[]> pResult,
                                        String pResColsName, String pRespColsDataTypes) {
        JSONObject lResJson = null;
        JSONArray lRespJsonArray = new JSONArray();
        String[] lRespColNames = Utils.split(pResColsName,
                ServerConstants.SEPARATOR_PIPE);
        String[] lRespColDataTypes = Utils.split(pRespColsDataTypes,
                ServerConstants.SEPARATOR_PIPE);

        for (Object[] fObject : pResult) {
            lResJson = new JSONObject();
            if (fObject.length == lRespColNames.length
                    && fObject.length == lRespColDataTypes.length) {
                for (int i = 0; i < fObject.length; i++) {
                    String fColName = lRespColNames[i];
                    String fColValue = fObject[i].toString();
                    lResJson = JSONUtils.putJSonObj(lResJson, fColName,
                            fColValue);
                }
                lRespJsonArray.put(lResJson);
            } else {
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_013.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_013));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }

        }
        return lRespJsonArray;

    }

    public int calculateTotalNoOfRows(int pNoOfRows, int pRecordsPerPage) {
        int lTotalnoOfPages = 1;
        try {
            LOG.debug("{} calculateTotalNoOfRows - pNoOfRows: {}, pRecordsPerPage: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pNoOfRows, pRecordsPerPage);

            if (0 != pNoOfRows && 0 != pRecordsPerPage) {
                int lModValue = pNoOfRows % pRecordsPerPage;

                if (lModValue > 0) {
                    lTotalnoOfPages = (pNoOfRows / pRecordsPerPage) + 1;
                } else if (lModValue < 0) {
                    lTotalnoOfPages = 1;
                } else {
                    lTotalnoOfPages = pNoOfRows / pRecordsPerPage;
                }
            }

        } catch (Exception ex) {
            LOG.error(LOG_EXCEPTION, ex);
        }
        return lTotalnoOfPages;
    }

    public boolean compareBindVariables(JSONObject pCachedJson,
                                        String pBindVarDataType, String pBindVarVal, String pInterfaceId,
                                        String pAppId, String pQueryId) {
        boolean lExists = false;
        try {
            if (pCachedJson != null) {
                String lKey = pAppId + pInterfaceId + pQueryId;
                LOG.debug("{} JSON Node names: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lKey);
                JSONObject lLOVRequest = (JSONObject) pCachedJson.get(lKey);
                if (lLOVRequest != null) {
                    String lBindVars = lLOVRequest
                            .getString("bindvariablesDataTypes");
                    String lBindValues = lLOVRequest
                            .getString("bindvariablesValues");
                    LOG.debug("{} LOVService.lBindVars: {}, lBindValues: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lBindVars, lBindValues);
                    if (lBindVars != null && lBindVars.length() > 1 &&
                            pBindVarDataType.equalsIgnoreCase(lBindVars)
                            && lBindValues.equals(pBindVarVal)) {
                        lExists = true;
                    }

                }

            }

            LOG.debug("{} LOVService.compareBindVariables - lExists: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lExists);
        } catch (JSONException ex) {
            LOG.error(LOGGER_JSON_EXCEPTION, ex);
        }
        return lExists;
    }

    public boolean compareFilterVariables(JSONObject pCachedJson,
                                          String pFilterVarCols, String pFilterVarVal, String pInterfaceId,
                                          String pAppId, String pQueryId) {
        boolean lExists = false;
        try {
            if (pCachedJson != null) {
                String lKey = pAppId + pInterfaceId + pQueryId;
                LOG.debug("{} JSON Node name : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lKey);
                JSONObject lLOVRequest = (JSONObject) pCachedJson.get(lKey);
                if (lLOVRequest != null) {
                    String lFilterVars = lLOVRequest
                            .getString("filtervariableCols");
                    String lFilterValues = lLOVRequest
                            .getString("filtervariablesValues");
                    LOG.debug("{} LOVService.lFilterVars: {}, lFilterValues: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lFilterVars, lFilterValues);
                    if (pFilterVarCols.equalsIgnoreCase(lFilterVars) && lFilterValues.equals(pFilterVarVal)) {
                        lExists = true;
                    }
                }
            }
            LOG.debug("{} LOVService.compareFilterVariables - lExists: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lExists);
        } catch (JSONException ex) {
            LOG.error(LOGGER_JSON_EXCEPTION, ex);
        }
        return lExists;
    }

    @SuppressWarnings("deprecation")
    public String clearHibernateCache() {
        JSONObject lResponse = null;

        try {
            lResponse = new JSONObject();
            Configuration cfg = new Configuration()
                    .configure(ServerConstants.HIBERNATE_CFG_XML);
            SessionFactory sessionFactory = cfg.buildSessionFactory();
            Map<String, ClassMetadata> classesMetadata = sessionFactory
                    .getAllClassMetadata();
            for (String entityName : classesMetadata.keySet()) {
                LOG.debug("{} Evicting Entity from 2nd level cache : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, entityName);
                sessionFactory.getCache().evictEntityRegion(entityName);
                lResponse.put(entityName, "Cleared from cache memory....");
            }

            sessionFactory.close();
            return lResponse.toString();
        } catch (Exception pEx) {
            LOG.warn("{} Exception - clearHibernateCache: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS
                    , pEx);
            return null;
        }

    }

    @Override
    public Object buildRequest(Message pMessage, Object pRequestPayLoad,
                               SpringCamelContext pContext) {
        LOG.debug("{} Inside LOV buildRequest", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        return pRequestPayLoad;
    }

    @Override
    public Object processResponse(Message pMessage, Object pResponse,
                                  SpringCamelContext pContext) {
        LOG.debug("{} Inside LOV processResponse", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        return pResponse;
    }

    @Override
    public Object callService(Message pMessage, Object pRequestPayLoad,
                              SpringCamelContext pContext) {
        JSONObject requestJson = (JSONObject) buildRequest(pMessage, pRequestPayLoad, pContext);
        pMessage.getRequestObject().setRequestJson(requestJson);
        String output = getVariablesList(pMessage);
        output = (String) processResponse(pMessage, output, pContext);
        return new JSONObject(output);
    }
}
