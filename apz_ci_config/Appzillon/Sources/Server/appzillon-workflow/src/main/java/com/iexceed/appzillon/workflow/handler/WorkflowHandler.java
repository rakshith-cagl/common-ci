package com.iexceed.appzillon.workflow.handler;

import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.workflow.iface.*;
import com.iexceed.appzillon.workflow.impl.*;

public class WorkflowHandler implements IWorkflowHandler {
    @Override
    public void handleRequest(Message pMessage) {
        String ifaceId = pMessage.getHeader().getInterfaceId();
        if (ServerConstants.INTERFACE_ID_WORKFLOW_START.equalsIgnoreCase(ifaceId)) {
            IWorkflowStart workflowStart = new WorkflowStartImpl();
            workflowStart.startWorkflow(pMessage);
        } else if (ServerConstants.INTERFACE_ID_WORKFLOW_NEXT.equalsIgnoreCase(ifaceId)) {
            IWorkflowNext workflowNextImpl = new WorkflowNextImpl();
            workflowNextImpl.validateWorkflowRefno(pMessage);
        } else if (ServerConstants.INTERFACE_ID_WORKFLOW_PREV.equalsIgnoreCase(ifaceId)) {
            IWorkflowPrev workflowPrevImpl = new WorkflowPrevImpl();
            workflowPrevImpl.validateWorkflowRefno(pMessage);
        } else if (ServerConstants.INTERFACE_ID_WORKFLOW_SAVE.equalsIgnoreCase(ifaceId)) {
            IWorkflowSave workflowSave = new WorkflowSaveImpl();
            workflowSave.saveWorkflow(pMessage);
        } else if (ServerConstants.INTERFACE_ID_WORKFLOW_TERMINATE.equalsIgnoreCase(ifaceId)) {
            IWorkflowTerminate workflowStop = new WorkflowTerminateImpl();
            workflowStop.terminateWorkflow(pMessage);
        } else if (ServerConstants.INTERFACE_ID_WORKFLOW_QUERY.equalsIgnoreCase(ifaceId)) {
            IWorkflowQuery workflowQuery = new WorkflowQueryImpl();
            workflowQuery.fetchStageDetails(pMessage);
        } else if (ServerConstants.INTERFACE_ID_WORKFLOW_ASSIGN.equalsIgnoreCase(ifaceId)
                || ServerConstants.INTERFACE_ID_WORKFLOW_REASSIGN.equalsIgnoreCase(ifaceId)
                || ServerConstants.INTERFACE_ID_WORKFLOW_ACQUIRE.equalsIgnoreCase(ifaceId)
                || ServerConstants.INTERFACE_ID_WORKFLOW_UNASSIGN.equalsIgnoreCase(ifaceId)) {
            IWorkflowAssign workflowAssign = new WorkflowAssignImpl();
            workflowAssign.assignStageDetailsToUserId(pMessage);
        }

    }

}
