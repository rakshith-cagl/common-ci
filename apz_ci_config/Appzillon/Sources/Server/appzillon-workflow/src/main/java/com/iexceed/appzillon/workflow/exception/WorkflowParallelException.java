package com.iexceed.appzillon.workflow.exception;

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.EnumMap;
import java.util.Map;

public class WorkflowParallelException extends AppzillonException {
    private static final long serialVersionUID = 1L;

    private static final Map<Code, String> workflowException = new EnumMap<>(WorkflowParallelException.Code.class);

    static {
        workflowException.put(WorkflowParallelException.Code.APZ_WFL_007, "Parallel stage(s) are not completed");
    }

    String wFlowParallelExceptionCode;
    String wFlowParallelExceptionMessage;

    private WorkflowParallelException() {

    }

    public static String getWorkflowParallelExceptionMessage(Object key) {
        return workflowException.get(key);

    }

    public static WorkflowParallelException getWorkflowParallelExceptionInstance() {
        return new WorkflowParallelException();
    }

    @Override
    public String getCode() {
        return this.wFlowParallelExceptionCode;
    }

    @Override
    public void setCode(String wFlowExceptionCode) {
        this.wFlowParallelExceptionCode = wFlowExceptionCode;
    }

    @Override
    public String getMessage() {
        return this.wFlowParallelExceptionMessage;
    }

    @Override
    public void setMessage(String wFlowExceptionMessage) {
        this.wFlowParallelExceptionMessage = wFlowExceptionMessage;
    }

    public enum Code {

        APZ_WFL_007;

        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }
}



