package com.iexceed.webcontainer.authentication.azuread;

import java.io.Serializable;
import java.util.Date;

class AzureAdStateData implements Serializable {
    private static final long serialVersionUID = 8635537021248345776L;
    private String nonce;
    private Date expirationDate;

    AzureAdStateData(String nonce, Date expirationDate) {
        this.nonce = nonce;
        this.expirationDate = expirationDate;
    }

    String getNonce() {
        return nonce;
    }

    Date getExpirationDate() {
        return expirationDate;
    }
}