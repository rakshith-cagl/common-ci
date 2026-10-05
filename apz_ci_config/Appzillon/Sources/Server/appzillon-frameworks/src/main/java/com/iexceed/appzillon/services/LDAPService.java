package com.iexceed.appzillon.services;

import com.iexceed.appzillon.dao.LDAPDetails;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IServicesBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utils.ServicesUtil;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.springldap.SpringLdapProducer;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.iexceed.appzillon.utils.Constants.*;

public class LDAPService implements IServicesBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    LDAPService.class.getName());

    protected LDAPDetails ldapDtls = null;

    public String update(Message pMessage, SpringCamelContext pContext,
                         ProducerTemplate pProducer) {
        LOG.info("{} inside LDAPService update", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        String dn = null;
        String addAttributes = null;
        String replaceAttributes = null;
        String deleteAttributes = null;
        JSONObject load = pMessage.getRequestObject().getRequestJson();
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        LdapTemplate ldapTemplate = (LdapTemplate) pContext
                .getApplicationContext().getBean(
                        appId + "_" + interfaceId + ServerConstants.LDAP_TEMPLATE);

        try {
            dn = load.get(ServerConstants.LDAP_CONSTANTS_DN).toString();
            LOG.debug("{} dn value {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, dn);
            if (load.has(ServerConstants.LDAP_CONSTANTS_ADD_ATTRIBUTES)) {
                addAttributes = load.get(
                                ServerConstants.LDAP_CONSTANTS_ADD_ATTRIBUTES)
                        .toString();
                LOG.debug("{} add attributes {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, addAttributes);
            }
            if (load.has(ServerConstants.LDAP_CONSTANTS_REPLACE_ATTRIBUTES)) {
                replaceAttributes = load.get(
                                ServerConstants.LDAP_CONSTANTS_REPLACE_ATTRIBUTES)
                        .toString();
                LOG.debug("{} Replace attributes {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, replaceAttributes);
            }
            if (load.has(ServerConstants.LDAP_CONSTANTS_DELETE_ATTRIBUTES)) {
                deleteAttributes = load.get(
                                ServerConstants.LDAP_CONSTANTS_DELETE_ATTRIBUTES)
                        .toString();
                LOG.debug("{} Delete attributes {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, deleteAttributes);
            }

        } catch (JSONException e1) {
            LOG.error(LOGGER_JSON_EXCEPTION, e1);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e1.getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        LOG.info("{} 1 ADD : {} 2 REPLACE: {}3 DELETE: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, addAttributes, replaceAttributes, deleteAttributes);

        String[] addArray = new String[0];
        String[] replaceArray = new String[0];
        String[] deleteArray = new String[0];
        if (Utils.isNotNullOrEmpty(addAttributes)) {
            addArray = addAttributes.split(",");
            LOG.debug("{} total no of items to be added {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, addArray.length);

        }
        if (Utils.isNotNullOrEmpty(replaceAttributes)) {
            replaceArray = replaceAttributes.split(",");
            LOG.debug("{} total no of items to be replaced {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, replaceArray.length);

        }

        if (Utils.isNotNullOrEmpty(deleteAttributes)) {
            deleteArray = deleteAttributes.split(",");
            LOG.debug("{} total no of items to be deleted {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, deleteArray.length);

        }
        int mod = deleteArray.length + replaceArray.length + addArray.length;
        LOG.debug("{} total no of items to be modified {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, mod);
        ModificationItem[] mods = new ModificationItem[mod];
        int itemCounter = 0;
        int i = 0;
        while (i < addArray.length) {
            String[] split = addArray[i].split(":");
            Attribute item = new BasicAttribute(split[0], split[1]);
            ModificationItem addItem = new ModificationItem(
                    DirContext.ADD_ATTRIBUTE, item);
            mods[itemCounter] = addItem;
            itemCounter++;
            i++;
        }

        int j = 0;
        while (j < replaceArray.length) {
            String[] split = replaceArray[j].split(":");
            Attribute item = new BasicAttribute(split[0], split[1]);
            ModificationItem repItem = new ModificationItem(
                    DirContext.REPLACE_ATTRIBUTE, item);
            mods[itemCounter] = repItem;
            itemCounter++;
            j++;
        }

        int k = 0;
        while (k < deleteArray.length) {
            String[] split = deleteArray[k].split(":");
            Attribute item = new BasicAttribute(split[0], split[1]);
            ModificationItem delItem = new ModificationItem(
                    DirContext.REMOVE_ATTRIBUTE, item);
            mods[itemCounter] = delItem;
            itemCounter++;
            k++;
        }

        LOG.debug("{} {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, mods);
        LOG.debug("{} Ldap template used {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ldapTemplate);
        try {
            Utils.setExtTime(pMessage, "S");
            ldapTemplate.modifyAttributes(dn, mods);
            Utils.setExtTime(pMessage, "E");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, "", mods);

        } catch (Exception e) {
            LOG.error("Exception: ", e);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e.getMessage());
            exExp.setPriority("1");
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, e.getMessage(), mods);
            throw exExp;
        }

        return "{\"response\":\"updated successfully\"}";

    }

    public String performSearch(Message pMessage, SpringCamelContext pContext,
                                ProducerTemplate pProducer) {
        LOG.debug("{} inside LDAPService performSearch", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String output = "";
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String endpointURI = ServerConstants.LDAP_SPRING_URI_PREFIX + appId
                + "_" + interfaceId + ServerConstants.LDAP_TEMPLATE;
        String scope = SUB_TREE;
        String dn = null;
        String filter = null;
        Exchange exchange = null;
        JSONObject load = pMessage.getRequestObject().getRequestJson();
        if (load.has(ServerConstants.LDAP_CONSTANTS_SCOPE)) {
            try {
                scope = load.getString(ServerConstants.LDAP_CONSTANTS_SCOPE);
            } catch (JSONException e) {
                LOG.warn(LOGGER_LDAP_SOAP_NOT_FOUND);
            }
        }
        String operation = "?operation=search&scope=" + scope;

        try {
            endpointURI = endpointURI + operation;
            LOG.debug(LOGGER_LDAP_SOAP_ENDPOINT, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpointURI);
            dn = load.get(ServerConstants.LDAP_CONSTANTS_DN).toString();
            LOG.debug("{} dn {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, dn);

            filter = load.get(ServerConstants.LDAP_CONSTANTS_FILTER)
                    .toString();
            LOG.debug("{} filter {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, filter);

        } catch (JSONException e1) {
            LOG.error(LOGGER_JSON_EXCEPTION, e1);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e1.getMessage());
            exExp.setPriority("1");
            throw exExp;
        }
        final String dN = dn;
        final String fILTER = filter;
        Utils.setExtTime(pMessage, "S");
        final Map<String, String> map = new HashMap<String, String>();
        exchange = pProducer.request(endpointURI, new Processor() {
            public void process(Exchange exchng) throws Exception {

                map.put(SpringLdapProducer.DN, dN);
                map.put(SpringLdapProducer.FILTER, fILTER);
                exchng.getIn().setBody(map);
            }
        });
        Utils.setExtTime(pMessage, "E");
        if (exchange.getException() == null) {
            output = exchange.getIn().getBody(String.class);
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, output, map);
        } else {
            LOG.error(EXCHANGE_EXCEPTION, exchange.getException());
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, exchange.getException().getMessage(), map);
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(exchange.getException().getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        return "{\"respone\":\"" + output + "\"}";
    }

    public String create(Message pMessage, SpringCamelContext pContext,
                         ProducerTemplate pProducer) {
        LOG.info("{} Inside LDAPService create", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        String output = "";
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String endpointURI = ServerConstants.LDAP_SPRING_URI_PREFIX + appId
                + "_" + interfaceId + ServerConstants.LDAP_TEMPLATE;
        String scope = SUB_TREE;
        String dn = null;
        JSONObject load = pMessage.getRequestObject().getRequestJson();
        Exchange exchange = null;
        if (load.has(ServerConstants.LDAP_CONSTANTS_SCOPE)) {
            try {
                scope = load.getString(ServerConstants.LDAP_CONSTANTS_SCOPE);
            } catch (JSONException e) {

                LOG.warn(LOGGER_LDAP_SOAP_NOT_FOUND);
            }
        }
        String operation = "?operation=bind&scope=" + scope;
        String attrs = null;
        String classes = null;

        try {
            endpointURI = endpointURI + operation;
            LOG.debug(LOGGER_LDAP_SOAP_ENDPOINT, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpointURI);
            attrs = load.get(ServerConstants.LDAP_CONSTANTS_ATTRIBUTES)
                    .toString();

            classes = load
                    .get(ServerConstants.LDAP_CONSTANTS_OBJECT_CLASSES)
                    .toString();
            dn = load.get(ServerConstants.LDAP_CONSTANTS_DN).toString();
        } catch (JSONException e1) {
            LOG.error(LOGGER_JSON_EXCEPTION, e1);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e1.getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        String[] aTTRS = attrs.split(",");
        String[] objectclasses = classes.split(",");
        int i = 0;
        Attributes attributes = new BasicAttributes();

        while (i < aTTRS.length) {
            String[] spliter = aTTRS[i].split(":");
            attributes.put(spliter[0], spliter[1]);
            i++;
        }
        BasicAttribute oc = new BasicAttribute(
                ServerConstants.LDAP_CONSTANTS_OBJECTCLASS);
        i = 0;
        while (i < objectclasses.length) {
            oc.add(objectclasses[i]);
            i++;
        }
        attributes.put(oc);

        final String dN = dn;
        final Attributes aTTRIBUTES = attributes;
        Utils.setExtTime(pMessage, "S");
        final Map<String, Serializable> map = new HashMap<String, Serializable>();
        exchange = pProducer.request(endpointURI, new Processor() {
            public void process(Exchange exchng) throws Exception {

                map.put(SpringLdapProducer.DN, dN);
                map.put(SpringLdapProducer.ATTRIBUTES, aTTRIBUTES);
                exchng.getIn().setBody(map);
            }
        });
        Utils.setExtTime(pMessage, "E");
        if (exchange.getException() == null) {
            output = exchange.getIn().getBody(String.class);
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, output, map);
            LOG.debug("{} output from Service {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, output);
        } else {
            LOG.error(EXCHANGE_EXCEPTION, exchange.getException());
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, exchange.getException().getMessage(), map);
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(exchange.getException().getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        return "{\"response\":\"createded successfully\"}";

    }

    public String delete(Message pMessage, SpringCamelContext pContext,
                         ProducerTemplate pProducer) {
        LOG.debug("{} inside LDAPService delete", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);

        String dn = null;
        String output = "";
        String appId = pMessage.getHeader().getAppId();
        String interfaceId = pMessage.getHeader().getInterfaceId();
        String endpointURI = ServerConstants.LDAP_SPRING_URI_PREFIX + appId
                + "_" + interfaceId + "_ldapTemplate";
        String scope = SUB_TREE;
        Exchange exchange = null;
        JSONObject load = pMessage.getRequestObject().getRequestJson();
        if (load.has(ServerConstants.LDAP_CONSTANTS_SCOPE)) {
            try {
                scope = load.getString(ServerConstants.LDAP_CONSTANTS_SCOPE);
            } catch (JSONException e) {
                LOG.warn(LOGGER_LDAP_SOAP_NOT_FOUND);
            }
        }
        String operation = "?operation=unbind&scope=" + scope;
        try {
            endpointURI = endpointURI + operation;
            LOG.debug(LOGGER_LDAP_SOAP_ENDPOINT, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, endpointURI);
            dn = load.get(ServerConstants.LDAP_CONSTANTS_DN).toString();
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "dn :" + dn);
        } catch (JSONException e1) {
            LOG.error(LOGGER_JSON_EXCEPTION, e1);
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(e1.getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        final String dN = dn;

        Utils.setExtTime(pMessage, "S");
        final Map<String, String> map = new HashMap<String, String>();
        exchange = pProducer.request(endpointURI, new Processor() {
            public void process(Exchange exchng) throws Exception {

                map.put(SpringLdapProducer.DN, dN);
                exchng.getIn().setBody(map);
            }
        });
        Utils.setExtTime(pMessage, "E");
        if (exchange.getException() == null) {
            output = exchange.getIn().getBody(String.class);
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.SUCCESS, output, map);
            LOG.debug("{} output from Service {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, output);
        } else {
            LOG.error(EXCHANGE_EXCEPTION, exchange.getException());
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            ServicesUtil.processFmwTxnDetails(pMessage, ServerConstants.ERROR, exchange.getException().getMessage(), map);
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage(exchange.getException().getMessage());
            exExp.setPriority("1");
            throw exExp;
        }

        return "{\"response\":\"deleted successfully\"}";

    }

    @Override
    public Object buildRequest(Message pMessage, Object pRequestPayLoad,
                               SpringCamelContext pContext) {
        return pRequestPayLoad;
    }

    @Override
    public Object processResponse(Message pMessage, Object pResponse,
                                  SpringCamelContext pContext) {
        return pResponse;
    }

    @Override
    public Object callService(Message pMessage, Object pRequestPayLoad,
                              SpringCamelContext pContext) {
        String output = null;

        ProducerTemplate lproducer = ExternalServicesRouter.createProducerTemplate();
        String lappId = pMessage.getHeader().getAppId();
        String linterfaceId = pMessage.getHeader().getInterfaceId();
        String ldaoBeanID = lappId + "_" + linterfaceId;

        String[] loperations = null;
        ldapDtls = (LDAPDetails) ExternalServicesRouter
                .injectBeanFromSpringContext(ldaoBeanID, pContext);

        LOG.info("{} ldap server ip configured {}, server port {}, sAuthenticationReq {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, ldapDtls.getHost(), ldapDtls.getPort(), ldapDtls.getAuthenticationReq());

        if (ldapDtls.getOperationAllowed() != null) {
            loperations = ldapDtls.getOperationAllowed().split(",");
        } else {
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage("Parameter operationAllowed not configured");
            exExp.setPriority("1");
            LOG.error("{} Parameter operation Allowed not configured. {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exExp);
            throw exExp;

        }
        pRequestPayLoad = ServicesUtil.getModifiedPayloadWithMaskedValue(pMessage, pRequestPayLoad, ldapDtls.getAutoGenElementMap(), ldapDtls.getTranslationElementMap());
        pMessage.getRequestObject().setRequestJson(new JSONObject(pRequestPayLoad + ""));
        LOG.debug("{} After Appending Request Json With MaskedId : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        String lpayLoad = (String) buildRequest(pMessage, pRequestPayLoad.toString(), pContext);
        LOG.debug("{} Payload after buildRequest {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lpayLoad);
        JSONObject load = new JSONObject(lpayLoad);

        String operation = load
                .get(ServerConstants.LDAP_CONSTANTS_OPERATION).toString();
        int i = 0;
        boolean flag = false;
        while (i < loperations.length) {
            LOG.info((i + 1) + " method configured " + loperations[i]);
            if (loperations[i].equals(operation)) {
                flag = true;
                break;
            }
            i++;
        }
        if (flag) {
            LOG.debug("{} operation found : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, operation);
            if (operation.equals(ServerConstants.LDAP_CONSTANTS_ADD)) {
                output = this.create(pMessage, pContext, lproducer);
            } else if (operation
                    .equals(ServerConstants.LDAP_CONSTANTS_UPDATE)) {
                output = this.update(pMessage, pContext, lproducer);
            } else if (operation
                    .equals(ServerConstants.LDAP_CONSTANTS_DELETE)) {
                output = this.delete(pMessage, pContext, lproducer);
            } else if (operation
                    .equals(ServerConstants.LDAP_CONSTANTS_SEARCH)) {
                output = this.performSearch(pMessage, pContext, lproducer);
            } else {
                ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
                exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
                exExp.setMessage("Method not allowed");
                exExp.setPriority("1");
                LOG.error("{} method not allowed {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exExp);
                throw exExp;
            }
        } else {
            ExternalServicesRouterException exExp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
            exExp.setCode(EXCEPTION_CODE.APZ_FM_EX_020.toString());
            exExp.setMessage("Method not supported by  LDAP Service");
            exExp.setPriority("1");
            LOG.error("{} method not supported by Ldap Service {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, exExp);
            throw exExp;
        }
        output = (String) processResponse(pMessage, output, pContext);
        LOG.debug("{} Response processResponse method {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, output);
        return new JSONObject(output);

    }
}
