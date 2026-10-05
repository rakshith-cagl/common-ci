package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * The primary key class for the TB_ASMI_LAYOUT_DESIGN database table.
 */
@Embeddable
public class TbAsmiLayoutDesignPK implements Serializable {
    //default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "SCREEN_ID")
    private String screenId;

    @Column(name = "LAYOUT_ID")
    private String layoutId;

    @Column(name = "DESIGN_ID")
    private String designId;

    public TbAsmiLayoutDesignPK() {
        // Default constructor
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getScreenId() {
        return this.screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getLayoutId() {
        return this.layoutId;
    }

    public void setLayoutId(String layoutId) {
        this.layoutId = layoutId;
    }

    public String getDesignId() {
        return designId;
    }

    public void setDesignId(String designId) {
        this.designId = designId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appId == null) ? 0 : appId.hashCode());
        result = prime * result + ((designId == null) ? 0 : designId.hashCode());
        result = prime * result + ((layoutId == null) ? 0 : layoutId.hashCode());
        result = prime * result + ((screenId == null) ? 0 : screenId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        TbAsmiLayoutDesignPK other = (TbAsmiLayoutDesignPK) obj;
        return Objects.equals(other.appId, this.appId)
                && Objects.equals(other.designId, this.designId)
                && Objects.equals(other.layoutId, this.layoutId)
                && Objects.equals(other.screenId, this.screenId);
    }
}