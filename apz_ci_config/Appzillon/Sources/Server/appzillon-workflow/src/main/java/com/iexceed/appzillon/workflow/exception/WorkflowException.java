package com.iexceed.appzillon.workflow.exception;

import com.iexceed.appzillon.exception.AppzillonException;

import java.util.EnumMap;
import java.util.Map;

public class WorkflowException extends AppzillonException {

    private static final long serialVersionUID = 1L;

    private static final Map<WorkflowException.Code, String> WORKFLOW_EXCEPTION_CODES = new EnumMap<>(WorkflowException.Code.class);

    static {
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_000, "JSONException");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_001, "Invalid workflow id");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_002, "Failed to fetch deatils from the DB");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_003, "Workflow has been stopped");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_004, "Invalid workflow reference number");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_005, "Previous stage(s) are not completed");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_006, "Invalid stageId /Invalid workflowRefNo/ Workflow already completed");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_007, "Parallel stage(s) are not completed");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_008, "No previous stage found for this stageId");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_009, "Workflow is not active");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_010, "Invalid userid");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_011, "Error in rule bean");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_012, "Not a valid Workflow request, check userId/AppId/WorkflowId");
        WORKFLOW_EXCEPTION_CODES.put(WorkflowException.Code.APZ_WFL_013, "Stage(s) returned from workflow are invalid");
    }

    String wFlowExceptionCode;
    String wFlowExceptionMessage;

    private WorkflowException() {

    }

    public static String getWorkflowExceptionMessage(Object key) {
        return WORKFLOW_EXCEPTION_CODES.get(key);

    }

    public static WorkflowException getWorkflowExceptionInstance() {
        return new WorkflowException();
    }

    @Override
    public String getCode() {
        return this.wFlowExceptionCode;
    }

    @Override
    public void setCode(String wFlowExceptionCode) {
        this.wFlowExceptionCode = wFlowExceptionCode;
    }

    @Override
    public String getMessage() {
        return this.wFlowExceptionMessage;
    }

    @Override
    public void setMessage(String wFlowExceptionMessage) {
        this.wFlowExceptionMessage = wFlowExceptionMessage;
    }

    public enum Code {

        APZ_WFL_000, APZ_WFL_001, APZ_WFL_002, APZ_WFL_003, APZ_WFL_004, APZ_WFL_005, APZ_WFL_006, APZ_WFL_007, APZ_WFL_008, APZ_WFL_009, APZ_WFL_010, APZ_WFL_011, APZ_WFL_012, APZ_WFL_013;

        @Override
        public String toString() {
            return this.name().replace('_', '-');
        }
    }
}
