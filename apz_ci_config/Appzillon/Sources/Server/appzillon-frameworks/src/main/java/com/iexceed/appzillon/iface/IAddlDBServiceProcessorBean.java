package com.iexceed.appzillon.iface;

import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.message.Message;
import org.apache.camel.spring.SpringCamelContext;

import javax.persistence.EntityManager;

/**
 * @author arthanarisamy
 */
public interface IAddlDBServiceProcessorBean {

    /**
     * @param pMessage
     * @param pRequestPayLoad
     * @param pContext
     * @throws AppzillonException
     */
    void preProcessor(Message pMessage, Object pRequestPayLoad, EntityManager pEntityManager, SpringCamelContext pContext) throws AppzillonException;


    /**
     * @param pMessage
     * @param pRequestPayLoad
     * @param pContext
     * @throws AppzillonException
     */
    void postProcessor(Message pMessage, Object pRequestPayLoad, EntityManager pEntityManager, SpringCamelContext pContext) throws AppzillonException;
}
