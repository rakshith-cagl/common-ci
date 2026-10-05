package com.iexceed.appzillon.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillon.dao.*;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import org.apache.camel.spring.xml.CamelEndpointFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.io.IOException;
import java.util.Iterator;

public class ProperyPlaceHolderBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
            ServerConstants.LOGGER_FRAMEWORKS, ProperyPlaceHolderBeanPostProcessor.class.getName());

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof HttpDetails) {
            bean = dataTranslation(beanName, bean, HttpDetails.class);
        } else if (bean instanceof SOAPDetails) {
            bean = dataTranslation(beanName, bean, SOAPDetails.class);
        } else if (bean instanceof EJBDetails) {
            bean = dataTranslation(beanName, bean, EJBDetails.class);
        } else if (bean instanceof ISO8583Details) {
            bean = dataTranslation(beanName, bean, ISO8583Details.class);
        } else if (bean instanceof JMSDetails) {
            bean = dataTranslation(beanName, bean, JMSDetails.class);
        } else if (bean instanceof LDAPDetails) {
            bean = dataTranslation(beanName, bean, LDAPDetails.class);
        } else if (bean instanceof MailDetails) {
            bean = dataTranslation(beanName, bean, MailDetails.class);
        } else if (bean instanceof ReportDetails) {
            bean = dataTranslation(beanName, bean, ReportDetails.class);
        } else if (bean instanceof SocketDetails) {
            bean = dataTranslation(beanName, bean, SocketDetails.class);
        } else if (bean instanceof SQLDetails) {
            bean = dataTranslation(beanName, bean, SQLDetails.class);
        } else if (bean instanceof CamelEndpointFactoryBean endPoint
                && Utils.isNotNullOrEmpty(endPoint.getUri())) {
            JSONObject json = new JSONObject();
            json.put("url", endPoint.getUri());
            LOG.debug("{} Endpoint url before enrichment: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, json.getString("url"));
            json = replacePlaceHolders(json, getAppId(beanName));
            endPoint.setUri(json.getString("url"));
            LOG.debug("{} Endpoint url after enrichment with runtime envs: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, json.getString("url"));
        }
        return bean;
    }

    public Object dataTranslation(String beanName, Object bean, Class<?> type) {
        String appId = getAppId(beanName);
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(bean);
            jsonInString = replacePlaceHolders(new JSONObject(jsonInString), appId).toString();
            bean = mapper.readValue(jsonInString, type);
        } catch (IOException e) {
            LOG.error(ServerConstants.LOGGER_FRAMEWORKS, e);
        }
        return bean;
    }

    public JSONObject replacePlaceHolders(JSONObject json, String appId) {
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (json.get(key) instanceof JSONObject) {
                JSONObject innerJson = json.getJSONObject(key);
                Iterator<String> innerKeys = innerJson.keys();
                while (innerKeys.hasNext()) {
                    String innerkey = innerKeys.next();
                    String value = innerJson.getString(innerkey);
                    if (isEnvironmentVariable(value)) {
                        value = ServicesUtil.enrichWithProperties(appId, value);
                        innerJson.put(innerkey, value);
                    }
                }
                json.put(key, innerJson);
            }
            if (json.get(key) instanceof String) {
                String value = json.getString(key);
                if (isEnvironmentVariable(value)) {
                    value = ServicesUtil.enrichWithProperties(appId, value);
                    json.put(key, value);
                }
            }
        }
        return json;
    }

    public String getAppId(String beanName) {
        String appId = "";
        appId = beanName.substring(0, beanName.indexOf("_"));
        return appId;
    }

    public boolean isEnvironmentVariable(String env) {
        return env.contains(ServerConstants.APZ_VARIABLE) || env.contains(ServerConstants.ENV_VARIABLE)
                || env.contains(ServerConstants.SYSTEM_VARIABLE);
    }
}
