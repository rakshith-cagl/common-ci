package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * The primary key class for the TB_ASMI_APP_ACCESS_TOKEN database table.
 */
@Embeddable
public class TbAsmiAppAccessTokenPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "ACCESS_TOKEN")
    private String accessToken;

    @Column(name = "CLIENT_NONCE")
    private String clientNonce;

    public TbAsmiAppAccessTokenPK() {

    }

    public TbAsmiAppAccessTokenPK(String appId, String userId, String accessToken, String clientNonce) {
        super();
        this.appId = appId;
        this.userId = userId;
        this.accessToken = accessToken;
        this.clientNonce = clientNonce;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClientNonce() {
        return clientNonce;
    }

    public void setClientNonce(String clientNonce) {
        this.clientNonce = clientNonce;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
        result = prime * result + ((appId == null) ? 0 : appId.hashCode());
        result = prime * result + ((clientNonce == null) ? 0 : clientNonce.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        TbAsmiAppAccessTokenPK other = (TbAsmiAppAccessTokenPK) obj;
        return Objects.equals(other.accessToken, this.accessToken)
                && Objects.equals(other.appId, this.appId)
                && Objects.equals(other.clientNonce, this.clientNonce)
                && Objects.equals(other.userId, this.userId);
    }


}