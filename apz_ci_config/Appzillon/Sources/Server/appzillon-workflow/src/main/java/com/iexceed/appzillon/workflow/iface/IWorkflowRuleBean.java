package com.iexceed.appzillon.workflow.iface;

import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.message.Message;

public interface IWorkflowRuleBean {

    public JSONObject processRuleBean(Message pMessage, JSONObject jsonObject);
}
