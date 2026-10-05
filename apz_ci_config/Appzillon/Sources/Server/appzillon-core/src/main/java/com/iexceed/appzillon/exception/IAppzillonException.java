package com.iexceed.appzillon.exception;

public interface IAppzillonException {
    /**
     * @return
     */
    String getCode();

    /**
     * @param code
     */
    void setCode(String code);

    /**
     * @return
     */
    String getMessage();

    /**
     * @param message
     */
    void setMessage(String message);

    /**
     * @return
     */
    String getType();

    /**
     * @return
     */
    String getPriority();
}
