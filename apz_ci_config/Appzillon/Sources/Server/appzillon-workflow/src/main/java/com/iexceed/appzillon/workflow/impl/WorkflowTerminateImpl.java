package com.iexceed.appzillon.workflow.impl;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.iface.IWorkflowTerminate;

public class WorkflowTerminateImpl implements IWorkflowTerminate {
    @Override
    public void terminateWorkflow(Message pMessage) {
        pMessage.getHeader().setServiceType("WorkflowTerminate");
        WorkflowStartup.getInstance().processRequest(pMessage);
    }
}
