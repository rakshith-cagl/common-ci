package com.iexceed.appzillon.services.custom;

import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.iface.IAddlDBServiceProcessorBean;
import com.iexceed.appzillon.message.Message;
import org.apache.camel.spring.SpringCamelContext;

import javax.persistence.EntityManager;

import static com.iexceed.appzillon.utils.Constants.TB_ASMI_ROLE_MASTER;

public class RoleDetailQuery implements IAddlDBServiceProcessorBean {

    public void preProcessor(Message pMessage, Object pRequestPayLoad, EntityManager pEntityManager,
                             SpringCamelContext pContext) throws AppzillonException {
        if (pMessage.getHeader().getInterfaceId().contains("RoleDetailQuery_New")) {
            pMessage.getRequestObject().getRequestJson().getJSONObject(TB_ASMI_ROLE_MASTER).put("makerId", pMessage.getHeader().getUserId());
            pMessage.getRequestObject().getRequestJson().getJSONObject(TB_ASMI_ROLE_MASTER).put("createUserId", pMessage.getHeader().getUserId());
            pMessage.getRequestObject().getRequestJson().getJSONObject(TB_ASMI_ROLE_MASTER).put("authStatus", "U");
            pMessage.getRequestObject().getRequestJson().getJSONObject(TB_ASMI_ROLE_MASTER).remove("checkerId");
        }

    }

    public void postProcessor(Message pMessage, Object pRequestPayLoad, EntityManager pEntityManager,
                              SpringCamelContext pContext) throws AppzillonException {
        // postProcessor  method
    }

}
