package com.iexceed.appzillon.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillon.dao.DBDetails;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import org.apache.camel.spring.SpringCamelContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

public class DBServiceUtils {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS, DBServiceUtils.class.getName());
    private static Map<String, EntityManagerFactory> entityManagerFactoryMap = new HashMap<>();
    EntityManagerFactory entityManagerFactory = null;

    public static java.util.Date getUtilDate(java.util.Date pdate) {
        java.util.Date ldate = pdate;
        if (pdate != null) {
            ldate = new java.util.Date(pdate.getTime());
        }
        return ldate;
    }

    private EntityManagerFactory getEntityManagerFactory(String pPersistenceunit) {
        if (!entityManagerFactoryMap.containsKey(pPersistenceunit)) {
            entityManagerFactory = Persistence.createEntityManagerFactory(pPersistenceunit);
            entityManagerFactoryMap.put(pPersistenceunit, entityManagerFactory);
            LOG.debug("{} creating EntityManagerFactory...", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        } else {
            entityManagerFactory = entityManagerFactoryMap.get(pPersistenceunit);
        }
        return entityManagerFactory;
    }

    public DBDetails getDBDetails(com.iexceed.appzillon.message.Message pMessage, SpringCamelContext pContext) {
        String lifaceid = pMessage.getHeader().getInterfaceId();
        int lidx = lifaceid.lastIndexOf("_");
        if (lidx >= 0) {
            lifaceid = lifaceid.substring(0, lidx);
        }
        return (DBDetails) ExternalServicesRouter.injectBeanFromSpringContext(pMessage.getHeader().getAppId() + "_" + lifaceid, pContext);
    }

    public EntityManager getEntityManager(com.iexceed.appzillon.message.Message pMessage, SpringCamelContext pContext) {
        EntityManager entityManager = null;
        DBDetails dbDtls = getDBDetails(pMessage, pContext);
        String lpersistenceunit = dbDtls.getPersistenceUnitName();
        entityManagerFactory = getEntityManagerFactory(lpersistenceunit);
        entityManager = entityManagerFactory.createEntityManager();
        return entityManager;
    }

    /**
     * @param persistenceUnit
     * @return
     */
    public EntityManager getEntityManagerFromPU(String persistenceUnit) {
        EntityManager entityManager;
        entityManagerFactory = getEntityManagerFactory(persistenceUnit);
        entityManager = entityManagerFactory.createEntityManager();
        return entityManager;
    }

    public void closeEntityManager(EntityManager pEntityManager) {
        if (pEntityManager != null) {
            pEntityManager.close();
        }
    }

    public ExternalServicesRouterException getException(String pcode, String ppriority) {
        EXCEPTION_CODE lcode = EXCEPTION_CODE.valueOf(pcode);
        ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
        exsrvcallexp.setCode(pcode);
        exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(lcode));
        exsrvcallexp.setPriority("1");
        return exsrvcallexp;
    }

    public Object getRequest(Object pRequestPayLoad, Class pclass, String pdbaction, DateFormat pdefdateformat) {
        JSONObject lreqjsonobj = (JSONObject) pRequestPayLoad;
        String lreqjsonstr = lreqjsonobj.toString(3);
        Object lreqpayload = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(pdefdateformat);
        mapper.setTimeZone(pdefdateformat.getTimeZone());
        LOG.debug("{} setting request timezone from Date format....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        try {
            lreqpayload = mapper.readValue(lreqjsonstr, pclass);
        } catch (Exception e) {
            e.printStackTrace();
            throw getException("APZ_FM_EX_040", "1");
        }
        return lreqpayload;
    }

    public JSONObject getResponse(Object prespobj, String pdbaction, DateFormat pdefdateformat) {
        JSONObject lresp = null;
        //Build JSON Response..
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(pdefdateformat);
        mapper.setTimeZone(pdefdateformat.getTimeZone());
        LOG.debug("{} setting timezone from Date format....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lrespjson = null;
        try {
            mapper.setSerializationInclusion(Include.NON_NULL);
            lrespjson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prespobj);
            LOG.debug("Response..: {}", lrespjson);
            lresp = new JSONObject(lrespjson);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw getException("APZ_FM_EX_040", "1");
        }
        return lresp;
    }

    public String getAutoAuth(Message pMessage, String pAuthStat) {
        return pAuthStat;
    }
}
