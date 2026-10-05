package com.iexceed.appzillon.iface;

import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;

/**
 * @author arthanarisamy
 */
public interface IExternalServiceRouter {

    /**
     * @param pMessage
     * @param context
     * @throws ExternalServicesRouterException
     * @throws InvalidPayloadException
     * @throws ClassNotFoundException
     * @throws JSONException
     */
    void serviceRequestDispatcher(Message pMessage,
                                  SpringCamelContext context) throws ExternalServicesRouterException,
            InvalidPayloadException, ClassNotFoundException;

}
