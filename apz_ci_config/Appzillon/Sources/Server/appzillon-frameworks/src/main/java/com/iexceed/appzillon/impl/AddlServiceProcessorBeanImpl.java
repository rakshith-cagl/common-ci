package com.iexceed.appzillon.impl;

import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.iface.IAddlDBServiceProcessorBean;
import com.iexceed.appzillon.iface.IAddlServiceProcessorBean;
import com.iexceed.appzillon.message.Message;
import org.apache.camel.spring.SpringCamelContext;

import javax.persistence.EntityManager;

public class AddlServiceProcessorBeanImpl implements IAddlServiceProcessorBean, IAddlDBServiceProcessorBean {
    @Override
    public void preProcessor(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) throws AppzillonException {
        // Override method
    }

    @Override
    public void postProcessor(Message pMessage, Object pRequestPayLoad, SpringCamelContext pContext) throws AppzillonException {
        // Override method
    }

    @Override
    public void preProcessor(Message pMessage, Object pRequestPayLoad, EntityManager pEntityManager,
                             SpringCamelContext pContext) throws AppzillonException {
        // Override method
    }

    @Override
    public void postProcessor(Message pMessage, Object pRequestPayLoad, EntityManager pEntityManager,
                              SpringCamelContext pContext) throws AppzillonException {
        // Override method
    }
}
