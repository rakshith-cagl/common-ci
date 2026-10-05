package com.iexceed.appzillon.workflow.impl;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.workflow.WorkflowStartup;
import com.iexceed.appzillon.workflow.iface.IWorkflowQuery;

public class WorkflowQueryImpl implements IWorkflowQuery {


    @Override
    public void fetchStageDetails(Message pMessage) {
        pMessage.getHeader().setServiceType("WorkflowQuery");
        WorkflowStartup.getInstance().processRequest(pMessage);

    }


}
