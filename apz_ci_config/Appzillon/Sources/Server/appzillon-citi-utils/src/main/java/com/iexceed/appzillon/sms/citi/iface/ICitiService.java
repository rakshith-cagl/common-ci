package com.iexceed.appzillon.sms.citi.iface;

import com.iexceed.appzillon.message.Message;

public interface ICitiService {

    void generateAppzillonJson(Message pMessage);

    void persistAppScreenJson(Message pMessage);

    void parseProductJson(Message pMessage);

    void parseWidgetJson(Message pMessage);

}
