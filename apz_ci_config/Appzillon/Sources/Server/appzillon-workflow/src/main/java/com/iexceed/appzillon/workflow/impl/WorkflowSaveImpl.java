package com.iexceed.appzillon.workflow.impl;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.iface.IWorkflowSave;

public class WorkflowSaveImpl implements IWorkflowSave {


    @Override
    public void saveWorkflow(Message pMessage) {
        pMessage.getHeader().setServiceType("WorkflowSave");
        WorkflowStartup.getInstance().processRequest(pMessage);
    }
}
