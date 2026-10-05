package com.iexceed.appzillon.workflow;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.services.*;
import org.springframework.web.context.WebApplicationContext;

import static com.iexceed.appzillon.utils.ServerConstants.*;

public class WorkflowStartup {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(LOGGER_DOMAIN,
            WorkflowStartup.class.toString());
    private static WorkflowStartup workflowStartup;
    private WebApplicationContext springContext;

    private WorkflowStartup() {
    }

    public static WorkflowStartup getInstance() {
        if (workflowStartup == null) {
            workflowStartup = new WorkflowStartup();
        }
        return workflowStartup;
    }

    public void init(WebApplicationContext wac) {
        springContext = wac;
        getInstance();
    }

    public void processRequest(Message pMessage) {
        LOG.info(LOGGER_PREFIX_WORKFLOW, "***************************** WorkflowStartup.processRequest * Start ******************************************");
        LOG.debug("{} Header Map: {}, Request Json: {},and ServiceType : {}", LOGGER_PREFIX_WORKFLOW, pMessage.getHeader(), pMessage.getRequestObject().getRequestJson(), pMessage.getHeader().getServiceType());
        if (INTERFACE_ID_WORKFLOW_START.equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
            ((WorkflowStart) this.getService(SERVICE_WORKFLOW_START)).startWorkflow(pMessage);
        } else if ("WorkflowNext".equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
            ((WorkflowNext) this.getService("WorkflowNext")).fetchNextWorkflowDetail(pMessage);
        } else if ("WorkflowPrev".equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
            ((WorkflowPrev) this.getService("WorkflowPrev")).fetchPrevWorkflowDetail(pMessage);
        } else if ("WorkflowSave".equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
            ((WorkflowSave) this.getService("WorkflowSave")).saveWorkflowDetails(pMessage);
        } else if ("WorkflowTerminate".equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
            ((WorkflowTerminate) this.getService("WorkflowTerminate")).terminateWorkflow(pMessage);
        } else if ("WorkflowQuery".equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
            ((WorkflowQuery) this.getService("WorkflowQuery")).fetchStageDetails(pMessage);
        } else if ("WorkflowAssign".equalsIgnoreCase(pMessage.getHeader().getServiceType())) {
            ((WorkflowAssign) this.getService("WorkflowAssign")).assignStageDetailsToUser(pMessage);
        }
        LOG.debug("{} Response Json in WorkflowStartup.processRequest -: {}", LOGGER_PREFIX_WORKFLOW, pMessage.getResponseObject().getResponseJson());
    }

    public WebApplicationContext getSpringContext() {

        return springContext;
    }

    private Object getService(String serviceName) {
        return getInstance().getSpringContext().getAutowireCapableBeanFactory().getBean(serviceName);
    }

}
