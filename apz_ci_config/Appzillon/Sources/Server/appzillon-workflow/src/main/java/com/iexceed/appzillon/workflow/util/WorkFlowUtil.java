package com.iexceed.appzillon.workflow.util;

import com.iexceed.appzillon.workflow.exception.WorkflowException;
import com.iexceed.appzillon.workflow.exception.WorkflowParallelException;

public class WorkFlowUtil {

    private WorkFlowUtil() {

    }

    public static WorkflowException getWorkFlowExceptionObj(WorkflowException.Code code) {
        WorkflowException wfexp = WorkflowException.getWorkflowExceptionInstance();
        wfexp.setCode(code.toString());
        wfexp.setMessage(WorkflowException.getWorkflowExceptionMessage(code));
        wfexp.setPriority("1");
        return wfexp;
    }

    public static WorkflowParallelException getWorkFlowParallelExceptionObj(WorkflowParallelException.Code code) {
        WorkflowParallelException wfexp = WorkflowParallelException
                .getWorkflowParallelExceptionInstance();
        wfexp.setCode(code.toString());
        wfexp.setMessage(WorkflowParallelException
                .getWorkflowParallelExceptionMessage(code));
        wfexp.setPriority("1");
        return wfexp;
    }
}
