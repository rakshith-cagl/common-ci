package com.iexceed.appzillon.iface;

import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.message.Message;
import org.apache.camel.spring.SpringCamelContext;

/**
 * @author arthanarisamy
 */
public interface IAddlServiceProcessorBean {

    /**
     * @param pMessage
     * @param pRequestPayLoad
     * @param pContext
     * @throws AppzillonException
     */
    void preProcessor(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) throws AppzillonException;


    /**
     * @param pMessage
     * @param pRequestPayLoad
     * @param pContext
     * @throws AppzillonException
     */
    void postProcessor(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) throws AppzillonException;
}
