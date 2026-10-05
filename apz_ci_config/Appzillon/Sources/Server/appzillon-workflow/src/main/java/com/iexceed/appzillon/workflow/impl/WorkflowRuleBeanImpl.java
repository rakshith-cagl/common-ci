package com.iexceed.appzillon.workflow.impl;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.workflow.iface.IWorkflowRuleBean;
import com.iexceed.appzillon.workflow.services.WorkflowStart;
import org.springframework.stereotype.Service;

@Service("WorkflowRuleBeanImpl")
public class WorkflowRuleBeanImpl implements IWorkflowRuleBean {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWorkflowLogger(ServerConstants.LOGGER_WORKFLOW,
            WorkflowStart.class.getName());

    @Override
    public JSONObject processRuleBean(Message pMessage, JSONObject jsonObject) {
        LOG.info("the rule bean has called");
        return jsonObject;
    }
}
