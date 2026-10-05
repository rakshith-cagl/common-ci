package com.iexceed.appzillon.intf;

public class AppzillonInterface {

    private String interfaceId;
    private String appId;
    private String category;
    private String type;
    private String description;
    private String sessionRequired;
    private String txnLogReq;
    private String txnPayLoadLogReq;
    private String authorizationReq;
    private String captchaReq;
    private String dgTxnLogRequired;

    public AppzillonInterface(String[] inputParam) {
        this.interfaceId = inputParam[0];
        this.appId = inputParam[1];
        this.category = inputParam[2];
        this.type = inputParam[3];
        this.description = inputParam[4];
        this.sessionRequired = inputParam[5];
        this.txnLogReq = inputParam[6];
        this.txnPayLoadLogReq = inputParam[7];
        this.authorizationReq = inputParam[8];
        this.captchaReq = inputParam[9];
        this.dgTxnLogRequired = inputParam[10];
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSessionRequired() {
        return sessionRequired;
    }

    public void setSessionRequired(String sessionRequired) {
        this.sessionRequired = sessionRequired;
    }

    public String getTxnLogReq() {
        return txnLogReq;
    }

    public void setTxnLogReq(String txnLogReq) {
        this.txnLogReq = txnLogReq;
    }

    public String getTxnPayLoadLogReq() {
        return txnPayLoadLogReq;
    }

    public void setTxnPayLoadLogReq(String txnPayLoadLogReq) {
        this.txnPayLoadLogReq = txnPayLoadLogReq;
    }

    public String getAuthorizationReq() {
        return authorizationReq;
    }

    public void setAuthorizationReq(String authorizationReq) {
        this.authorizationReq = authorizationReq;
    }

    public String getCaptchaReq() {
        return captchaReq;
    }

    public void setCaptchaReq(String captchaReq) {
        this.captchaReq = captchaReq;
    }

    public String getDgTxnLogRequired() {
        return dgTxnLogRequired;
    }

    public void setDgTxnLogRequired(String dgTxnLogRequired) {
        this.dgTxnLogRequired = dgTxnLogRequired;
    }

    @Override
    public String toString() {
        return "AppzillonInterface{" +
                "interfaceId='" + interfaceId + '\'' +
                ", appId='" + appId + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", sessionRequired='" + sessionRequired + '\'' +
                ", txnLogReq='" + txnLogReq + '\'' +
                ", txnPayLoadLogReq='" + txnPayLoadLogReq + '\'' +
                ", authorizationReq='" + authorizationReq + '\'' +
                ", captchaReq='" + captchaReq + '\'' +
                ", dgTxnLogRequired='" + dgTxnLogRequired + '\'' +
                '}';
    }
}
