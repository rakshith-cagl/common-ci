package com.iexceed.appzillon.domain.repository.impl;

import com.iexceed.appzillon.dbutils.DBUtils;
import com.iexceed.appzillon.domain.entity.TbAstpSessionStorage;
import com.iexceed.appzillon.domain.entity.TbAstpSessionStoragePK;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TbAsTpSessionStorageRepositoryImpl {

    public static final String ERROR_MSG = "Not able to close DB connection / preparedStatement";
    public static final String DATASOURCE = "DATASOURCE";
    public static final String APP_ID = "APP_ID";
    public static final String USER_ID = "USER_ID";
    public static final String SESSION_ID = "SESSION_ID";
    public static final String SESSION_KEY = "SESSION_KEY";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String SESSION_VALUE = "SESSION_VALUE";
    public static final String CREATED_BY = "CREATED_BY";
    public static final String CREATE_TS = "CREATE_TS";
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    TbAsTpSessionStorageRepositoryImpl.class.getName());


    public List<TbAstpSessionStorage> findAllById(TbAstpSessionStoragePK records, String appId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection con = null;
        String dateSource;
        TbAstpSessionStoragePK sessionStoragePk;
        TbAstpSessionStorage sessionStorage;

        String selectQuery = "SELECT APP_ID, USER_ID, SESSION_ID, SESSION_KEY, DEVICE_ID, " +
                "SESSION_VALUE, CREATED_BY, CREATE_TS FROM TB_ASTP_SESSION_STORAGE WHERE APP_ID=? " +
                "AND USER_ID=? AND SESSION_ID=? AND SESSION_KEY=?";
        List<TbAstpSessionStorage> lRecord = new ArrayList<>();
        LOG.debug("Select query is :{}", selectQuery);
        try {
            dateSource = PropertyUtils.getPropValue(appId, DATASOURCE);
            con = DBUtils.getConnectionFromDataSource(dateSource);
            ps = con.prepareStatement(selectQuery);
            LOG.debug("Select query param : appID= {}, userID = {}, sessionID={}, sessionKey={} ", selectQuery,
                    records.getAppId(), records.getUserId(), records.getSessionId(), records.getSessionKey());

            ps.setString(1, records.getAppId());
            ps.setString(2, records.getUserId());
            ps.setString(3, records.getSessionId());
            ps.setString(4, records.getSessionKey());
            rs = ps.executeQuery();
            while (rs.next()) {
                sessionStoragePk = new TbAstpSessionStoragePK();
                sessionStorage = new TbAstpSessionStorage();
                sessionStoragePk.setAppId(rs.getString(APP_ID));
                sessionStoragePk.setUserId(rs.getString(USER_ID));
                sessionStoragePk.setSessionId(rs.getString(SESSION_ID));
                sessionStoragePk.setSessionKey(rs.getString(SESSION_KEY));
                sessionStorage.setDeviceId(rs.getString(DEVICE_ID));
                sessionStorage.setSessionValue(rs.getString(SESSION_VALUE));
                sessionStorage.setCreatedBy(rs.getString(CREATED_BY));
                sessionStorage.setCreateTs(rs.getTimestamp(CREATE_TS));
                sessionStorage.setId(sessionStoragePk);
                lRecord.add(sessionStorage);
                LOG.debug(" Got this record for appID:{}, userID:{}, sessionID:{}, sessionKey{}, deviceId:{}",
                        sessionStoragePk.getAppId(), sessionStoragePk.getUserId(),
                        sessionStoragePk.getSessionId(), sessionStoragePk.getSessionKey(), sessionStorage.getDeviceId());
            }
        } catch (Exception exception) {
            LOG.error("Error in findAllByIdNoCon while getting data");
            LOG.error("Exception is findAllById :{}", exception.getMessage());
            LOG.error("Exception is findAllById :{}", exception.toString());

        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (SQLException e) {
                LOG.error(ERROR_MSG);
                LOG.error("Exception is findAllById :{}", e.getMessage());
                LOG.error("Exception is findAllById :{}", e.toString());
            }
        }
        return lRecord;
    }


    public void saveAll(List<TbAstpSessionStorage> lRecordList, String appId) {

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;
        ResultSet rs;
        boolean insertFlag = true;

        String selectQuery = "SELECT APP_ID, USER_ID, SESSION_ID, SESSION_KEY, DEVICE_ID, SESSION_VALUE, CREATED_BY, CREATE_TS " +
                "FROM TB_ASTP_SESSION_STORAGE WHERE APP_ID=? AND USER_ID=? AND SESSION_ID=? AND SESSION_KEY=? FOR UPDATE NOWAIT";

        String updateQuery = "UPDATE TB_ASTP_SESSION_STORAGE SET APP_ID=?, USER_ID=?, SESSION_ID=?, SESSION_KEY=?, DEVICE_ID=?," +
                "SESSION_VALUE=?, CREATED_BY=?, CREATE_TS=?" + "WHERE APP_ID=? AND USER_ID=? AND SESSION_ID=? AND SESSION_KEY=?";

        String insertQuery = "INSERT INTO TB_ASTP_SESSION_STORAGE (APP_ID, USER_ID, SESSION_ID, SESSION_KEY, DEVICE_ID," +
                "SESSION_VALUE, CREATED_BY, CREATE_TS) VALUES(?,?,?,?,?,?,?,?)";
        LOG.debug("Select query is :{}", selectQuery);
        LOG.debug("Update query is :{}", updateQuery);
        LOG.debug("insert query is :{}", insertQuery);

        try {
            String dateSource = PropertyUtils.getPropValue(appId, DATASOURCE);
            con = DBUtils.getConnectionFromDataSource(dateSource);
            con.setAutoCommit(false);

            ps = con.prepareStatement(selectQuery);
            psUpdate = con.prepareStatement(updateQuery);
            psInsert = con.prepareStatement(insertQuery);

            for (TbAstpSessionStorage sessionStorage : lRecordList) {
                LOG.debug("input param : appID= {}, userID = {}, sessionID={}, sessionKey={} ",
                        sessionStorage.getId().getAppId(), sessionStorage.getId().getUserId(),
                        sessionStorage.getId().getSessionId(), sessionStorage.getId().getSessionKey());

                LOG.debug("Select query param : appID= {}, userID = {}, sessionID={}, sessionKey={} ", selectQuery,
                        sessionStorage.getId().getAppId(), sessionStorage.getId().getUserId(),
                        sessionStorage.getId().getSessionId(), sessionStorage.getId().getSessionKey());

                ps.setString(1, sessionStorage.getId().getAppId());
                ps.setString(2, sessionStorage.getId().getUserId());
                ps.setString(3, sessionStorage.getId().getSessionId());
                ps.setString(4, sessionStorage.getId().getSessionKey());
                rs = ps.executeQuery();
                LOG.debug("saveAll select query executed");
                insertFlag = setParamForSaveOrUpdate(psInsert, psUpdate, rs, sessionStorage);
            }
            if (insertFlag)
                psInsert.executeBatch();
            else
                psUpdate.executeBatch();
            con.commit();
        } catch (Exception exception) {
            LOG.error("Exception is saveAll :{}", exception.getMessage());
            LOG.error("Exception is saveAll :{}", exception.toString());
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.error("Exception is saveAll :{}", e.getMessage());
                LOG.error("Exception is saveAll :{}", e.toString());
                LOG.error("Error in saveAll while rollback data");
            }
            LOG.error("Error in saveAll data");
        } finally {
            try {
                if (psUpdate != null)
                    psUpdate.close();
                if (psInsert != null)
                    psInsert.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
                if (psInsert != null)
                    psInsert.close();
            } catch (SQLException e) {
                LOG.error("Exception is saveAll :{}", e.getMessage());
                LOG.error("Exception is saveAll :{}", e.toString());
                LOG.error(ERROR_MSG);
            }
        }
    }

    private boolean setParamForSaveOrUpdate(PreparedStatement psInsert, PreparedStatement psUpdate, ResultSet rs, TbAstpSessionStorage sessionStorage) throws SQLException {
        boolean insertFlag = true;
        while (rs.next()) {
            insertFlag = false;
            psUpdate.setString(1, rs.getString(APP_ID));
            psUpdate.setString(2, rs.getString(USER_ID));
            psUpdate.setString(3, rs.getString(SESSION_ID));
            psUpdate.setString(4, rs.getString(SESSION_KEY));
            psUpdate.setString(5, rs.getString(DEVICE_ID));
            psUpdate.setString(6, rs.getString(SESSION_VALUE));
            psUpdate.setString(7, rs.getString(CREATED_BY));
            psUpdate.setTimestamp(8, rs.getTimestamp(CREATE_TS));
            psUpdate.setString(9, rs.getString(APP_ID));
            psUpdate.setString(10, rs.getString(USER_ID));
            psUpdate.setString(11, rs.getString(SESSION_ID));
            psUpdate.setString(12, rs.getString(SESSION_KEY));
            psUpdate.addBatch();
            LOG.debug("setParamForSaveOrUpdate update query executed");
            LOG.debug("Record updated for appID:{}, userID:{}, sessionID:{}, sessionKey{}, deviceId:{}",
                    sessionStorage.getId().getAppId(), sessionStorage.getId().getUserId(),
                    sessionStorage.getId().getSessionId(), sessionStorage.getId().getSessionKey(), sessionStorage.getDeviceId());
        }
        if (insertFlag) {
            psInsert.setString(1, sessionStorage.getId().getAppId());
            psInsert.setString(2, sessionStorage.getId().getUserId());
            psInsert.setString(3, sessionStorage.getId().getSessionId());
            psInsert.setString(4, sessionStorage.getId().getSessionKey());
            psInsert.setString(5, sessionStorage.getDeviceId());
            psInsert.setString(6, sessionStorage.getSessionValue());
            psInsert.setString(7, sessionStorage.getCreatedBy());
            psInsert.setTimestamp(8, sessionStorage.getCreateTs());
            psInsert.addBatch();
            LOG.debug("setParamForSaveOrUpdate insert query executed");
            LOG.debug("New record inserted for appID:{}, userID:{}, sessionID:{}, sessionKey{}, deviceId:{}",
                    sessionStorage.getId().getAppId(), sessionStorage.getId().getUserId(),
                    sessionStorage.getId().getSessionId(), sessionStorage.getId().getSessionKey(), sessionStorage.getDeviceId());
        }
        return insertFlag;
    }


    public void deleteAll(String appId, String userId, String deviceId) {
        ResultSet rs;
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement psDelete = null;
        LOG.debug("input param for deleteAll appId:{}, userId:{}, deviceId{}", appId, userId, deviceId);

        String selectQuery = "SELECT APP_ID, USER_ID, SESSION_ID, SESSION_KEY, DEVICE_ID, SESSION_VALUE, CREATED_BY, CREATE_TS " +
                "FROM TB_ASTP_SESSION_STORAGE WHERE APP_ID=? AND USER_ID=? AND DEVICE_ID=? FOR UPDATE NOWAIT";

        String deleteQuery = "DELETE FROM TB_ASTP_SESSION_STORAGE WHERE APP_ID=? AND USER_ID=? AND DEVICE_ID=?";

        String dateSource = PropertyUtils.getPropValue(appId, DATASOURCE);
        LOG.debug("Select query is :{}", selectQuery);
        LOG.debug("delete query is :{}", deleteQuery);

        try {
            con = DBUtils.getConnectionFromDataSource(dateSource);
            con.setAutoCommit(false);
            ps = con.prepareStatement(selectQuery);
            ps.setString(1, appId);
            ps.setString(2, userId);
            ps.setString(3, deviceId);
            rs = ps.executeQuery();
            LOG.debug("deleteAll select query executed");

            psDelete = con.prepareStatement(deleteQuery);
            while (rs.next()) {
                psDelete.setString(1, rs.getString(APP_ID));
                psDelete.setString(2, rs.getString(USER_ID));
                psDelete.setString(3, rs.getString(DEVICE_ID));
                psDelete.addBatch();
                LOG.debug("Record got deleted for appID:{}, userID:{}, sessionID:{}, sessionKey{}, deviceId:{}",
                        rs.getString(APP_ID), rs.getString(USER_ID),
                        rs.getString(SESSION_ID), rs.getString(SESSION_KEY), rs.getString(DEVICE_ID));
            }
            psDelete.executeBatch();
            con.commit();
        } catch (Exception exception) {
            LOG.error("Exception is deleteAll :{}", exception.getMessage());
            LOG.error("Exception is deleteAll :{}", exception.toString());

            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e) {
                LOG.error("Exception is deleteAll :{}", e.getMessage());
                LOG.error("Exception is deleteAll :{}", e.toString());
                LOG.error(ERROR_MSG);
            }
            LOG.error(" Error in saveAllSessionStorage while performing deleteSessionStorage");
        } finally {
            try {
                if (psDelete != null)
                    psDelete.close();
                if (ps != null)
                    ps.close();
                if (con != null)
                    con.close();
            } catch (Exception e) {
                LOG.error("Exception is deleteAll :{}", e.getMessage());
                LOG.error("Exception is deleteAll :{}", e.toString());
                LOG.error(ERROR_MSG);
            }
        }
    }
}
