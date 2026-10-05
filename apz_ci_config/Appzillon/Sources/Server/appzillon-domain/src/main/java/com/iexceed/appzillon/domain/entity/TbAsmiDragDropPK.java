package com.iexceed.appzillon.domain.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Ripu
 */
@Embeddable
public class TbAsmiDragDropPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "APP_ID")
    private String appId;
    @Basic(optional = false)
    @Column(name = "USER_ID")
    private String userId;
    @Basic(optional = false)
    @Column(name = "SCREEN_ID")
    private String screenId;
    @Basic(optional = false)
    @Column(name = "LAYOUT")
    private String layout;
    @Basic(optional = false)
    @Column(name = "PARENT_ID")
    private String parentId;
    @Basic(optional = false)
    @Column(name = "HTML_ID")
    private String htmlId;

    public TbAsmiDragDropPK() {
    }

    public TbAsmiDragDropPK(String appId, String userId) {
        this.appId = appId;
        this.userId = userId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getHtmlId() {
        return htmlId;
    }

    public void setHtmlId(String htmlId) {
        this.htmlId = htmlId;
    }

}
