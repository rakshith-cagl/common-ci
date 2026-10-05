package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.SQLDetails;
import com.iexceed.appzillon.dbutils.DBUtils;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.sql.NamedParameterStatement;
import org.apache.camel.spring.SpringCamelContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author arthanarisamy
 */
public class SQLServices implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS, SQLServices.class.getName());
    protected SQLDetails sqlDetails;

    @Override
    public Object buildRequest(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) {

        return null;
    }

    @Override
    public Object callService(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) {

        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        getSQLDetails(interfaceId, appId, pContext);
        JSONArray lResponseJson = new JSONArray();
        NamedParameterStatement lNamedParamStmt = null;
        ResultSet lResultSet = null;
        Connection lConnection = null;
        try {
            buildRequest(pMessage, pRequestPayLoad, pContext);
            LOG.debug("{} Creating a new Connection from the DataSource....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            lConnection = DBUtils.getConnectionFromDataSource(sqlDetails.getDSName());
            LOG.debug("{} Connection obtained", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

            JSONObject lQueryParams = pMessage.getRequestObject().getRequestJson();
            LOG.debug("{} Query Parameters from request payload -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    lQueryParams);
            Iterator<String> lJsonKeys = lQueryParams.keys();
            lNamedParamStmt = new NamedParameterStatement(lConnection, sqlDetails.getQuery(), sqlDetails.getTimeOut(),
                    lQueryParams);
            LOG.debug("{} Named Parameter Statement created....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            while (lJsonKeys.hasNext()) {
                String lKey = lJsonKeys.next();
                LOG.debug("{} Looping through JSON to set named parameters -: {}",
                        ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lKey);
                lNamedParamStmt.setObject(lKey, lQueryParams.get(lKey));
            }
            Utils.setExtTime(pMessage, "S");
            lResultSet = lNamedParamStmt.executeQuery();
            Utils.setExtTime(pMessage, "E");
            LOG.debug("{} QueryExecuted and ResultSet Obtained....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            ResultSetMetaData lResultSetMetaData = lResultSet.getMetaData();
            LOG.debug("{} Finding out the resultant columns....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            int lColumnCount = lResultSetMetaData.getColumnCount();
            LOG.debug("{} Resultant Column Length -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lColumnCount);
            List<String> lColumnNames = new ArrayList<>();
            for (int i = 1; i <= lColumnCount; i++) {
                String lColumnName = lResultSetMetaData.getColumnLabel(i);
                lColumnNames.add(lColumnName);
            }
            LOG.debug("{} FinalResultant column names -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lColumnNames);
            LOG.debug("{} Fetching query result from result set {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                    lResultSet.getFetchSize());
            lResponseJson = buildRespJson(lResultSet, lColumnNames);
            lResponseJson = (JSONArray) processResponse(pMessage, lResponseJson, pContext);
            if (lResponseJson.length() <= 0) {
                LOG.error("{} Exception from external system hence will be throwing an exception....",
                        ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
                ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                        .getExternalServicesRouterExceptionInstance();
                exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_038.toString());
                exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_038));
                exsrvcallexp.setPriority("1");
                throw exsrvcallexp;
            }
            LOG.info("{} Query Response -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lResponseJson);
        } catch (AppzillonException ae) {
            LOG.error("{} Exception from external system hence will be throwing an exception.... -: {}",
                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ae);
            throw ae;
        } catch (SQLException e) {
            LOG.error("{} Exception from external system hence will be throwing an exception.... -: {}",
                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                    .getExternalServicesRouterExceptionInstance();
            exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_017.toString());
            exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_017));
            exsrvcallexp.setPriority("1");
            throw exsrvcallexp;
        } finally {
            try {
                if (lNamedParamStmt != null) {
                    lNamedParamStmt.close();
                }
                if (lResultSet != null) {
                    lResultSet.close();
                }
                if (lConnection != null) {
                    lConnection.close();
                }
            } catch (SQLException e) {
                LOG.error("{} SQLException {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
                if (lConnection != null) {
                    try {
                        lConnection.close();
                    } catch (SQLException e1) {
                        LOG.error("{} SQLException -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e1);
                    }
                }
            }
        }
        return lResponseJson;
    }

    private JSONArray buildRespJson(ResultSet lResultSet, List<String> lColumnNames) throws SQLException {
        JSONArray lResponseJson = new JSONArray();
        while (lResultSet.next()) {
            JSONObject lRecord = new JSONObject();
            for (int i = 0; i < lColumnNames.size(); i++) {
                lRecord.put(lColumnNames.get(i), lResultSet.getObject(lColumnNames.get(i)));
            }
            LOG.debug("{} Record -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lRecord);
            lResponseJson.put(lRecord);
        }
        return lResponseJson;
    }

    @Override
    public Object processResponse(Message pMessage, Object pResponse, SpringCamelContext pContext) {
        LOG.debug("{} Returning response from processResponse {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pResponse);
        return pResponse;
    }

    public void getSQLDetails(String pInterfaceID, String pAppId, SpringCamelContext pContext) {
        LOG.debug("{} getSQLDetails ifID: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pInterfaceID);
        sqlDetails = (SQLDetails) ExternalServicesRouter.injectBeanFromSpringContext(pAppId + "_" + pInterfaceID,
                pContext);
        LOG.debug("{} DataSource Name -: {}, Query String: {}, timeOut : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS,
                sqlDetails.getDSName(), sqlDetails.getQuery(), sqlDetails.getTimeOut());
        int timeOut = sqlDetails.getTimeOut();
        if (timeOut == 0) {
            LOG.warn("{} Timeout value not configured will use default timeOut",
                    ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
            timeOut = Integer.parseInt(PropertyUtils.getPropValue(pAppId, ServerConstants.DEFAULT_TIMEOUT).trim());
        }
        sqlDetails.setTimeOut(timeOut);
    }
}
