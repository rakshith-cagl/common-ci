package com.iexceed.appzillon.workflow.impl;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.iface.IWorkflowStart;

public class WorkflowStartImpl implements IWorkflowStart {


    @Override
    public void startWorkflow(Message pMessage) {
        pMessage.getHeader().setServiceType("WorkflowStart");
        WorkflowStartup.getInstance().processRequest(pMessage);

    }
}
