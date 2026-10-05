/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASHS_ROLE_SCR")

@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsRoleScr implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    @JsonProperty("id")
    protected TbAshsRoleScrPK id;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;

    public TbAshsRoleScr() {
    }

    public TbAshsRoleScr(TbAshsRoleScrPK id) {
        this.id = id;
    }

    public TbAshsRoleScr(String appId, String screenId, String roleId) {
        this.id = new TbAshsRoleScrPK(appId, screenId, roleId);
    }

    public TbAshsRoleScrPK getTbAsmiRoleScrPK() {
        return id;
    }

    public void setTbAsmiRoleScrPK(TbAshsRoleScrPK id) {
        this.id = id;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TbAshsRoleScr)) {
            return false;
        }
        TbAshsRoleScr other = (TbAshsRoleScr) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiRoleScr[ id=" + id + " ]";
    }

}
