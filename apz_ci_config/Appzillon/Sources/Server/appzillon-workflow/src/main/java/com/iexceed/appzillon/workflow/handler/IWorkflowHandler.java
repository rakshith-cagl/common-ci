package com.iexceed.appzillon.workflow.handler;

import com.iexceed.appzillon.message.Message;

public interface IWorkflowHandler {

    public void handleRequest(Message pMessage);
}
