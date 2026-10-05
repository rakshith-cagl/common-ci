package com.iexceed.appzillon.workflow.impl;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.iface.IWorkflowNext;

public class WorkflowNextImpl implements IWorkflowNext {

    @Override
    public void validateWorkflowRefno(Message pMessage) {
        pMessage.getHeader().setServiceType("WorkflowNext");
        WorkflowStartup.getInstance().processRequest(pMessage);
    }
}
