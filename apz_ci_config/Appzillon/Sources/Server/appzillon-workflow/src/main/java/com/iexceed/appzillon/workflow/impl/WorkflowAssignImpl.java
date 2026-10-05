package com.iexceed.appzillon.workflow.impl;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.iface.IWorkflowAssign;

public class WorkflowAssignImpl implements IWorkflowAssign {


    @Override
    public void assignStageDetailsToUserId(Message pMessage) {
        pMessage.getHeader().setServiceType("WorkflowAssign");
        WorkflowStartup.getInstance().processRequest(pMessage);
    }
}
