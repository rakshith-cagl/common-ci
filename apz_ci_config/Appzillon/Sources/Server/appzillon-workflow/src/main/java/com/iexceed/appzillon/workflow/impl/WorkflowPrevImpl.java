package com.iexceed.appzillon.workflow.impl;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.iface.IWorkflowPrev;

public class WorkflowPrevImpl implements IWorkflowPrev {
    @Override
    public void validateWorkflowRefno(Message pMessage) {
        pMessage.getHeader().setServiceType("WorkflowPrev");
        WorkflowStartup.getInstance().processRequest(pMessage);
    }
}
