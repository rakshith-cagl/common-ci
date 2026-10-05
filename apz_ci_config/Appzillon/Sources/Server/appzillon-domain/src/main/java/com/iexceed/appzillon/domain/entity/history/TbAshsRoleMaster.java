/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iexceed.appzillon.domain.entity.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASHS_ROLE_MASTER")

@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAshsRoleMaster implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("TbAsmiRoleScr")
    @Transient
    public List<TbAshsRoleScr> tbAshsRoleScrs = new ArrayList<>();
    @JsonProperty("TbAsmiRoleIntf")
    @Transient
    public List<TbAshsRoleIntf> tbAshsRoleIntfs = new ArrayList<>();
    @JsonProperty("TbAsmiRoleControls")
    @Transient
    public List<TbAshsRoleControls> tbAshsRoleControls = new ArrayList<>();
    @EmbeddedId
    @JsonProperty("id")
    protected TbAshsRoleMasterPK id;
    @Column(name = "ROLE_DESC")
    @JsonProperty("roleDesc")
    private String roleDesc;
    @Column(name = "INTERFACE_ALLOWED")
    @JsonProperty("interfaceAllowed")
    private String interfaceAllowed;
    @Column(name = "SCREEN_ALLOWED")
    @JsonProperty("screenAllowed")
    private String screenAllowed;
    @Column(name = "CONTROL_ALLOWED")
    @JsonProperty("controlAllowed")
    private String controlAllowed;
    @Column(name = "CREATE_USER_ID")
    private String createUserId;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "MAKER_TS")
    @JsonProperty("makerTs")
    private Date makerTs;
    @Column(name = "MAKER_ID")
    @JsonProperty("makerId")
    private String makerId;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CHECKER_TS")
    @JsonProperty("checkerTs")
    private Date checkerTs;
    @Column(name = "CHECKER_ID")
    @JsonProperty("checkerId")
    private String checkerId;
    @Column(name = "AUTH_STATUS")
    @JsonProperty("authStat")
    private String authStat;

    public TbAshsRoleMaster() {
    }

    public TbAshsRoleMaster(TbAshsRoleMasterPK tbAsmiRoleMasterPK) {
        this.id = tbAsmiRoleMasterPK;
    }

    public TbAshsRoleMaster(String roleId, String appId) {
        this.id = new TbAshsRoleMasterPK(roleId, appId);
    }

    public Date getMakerTs() {
        return this.makerTs;
    }

    public void setMakerTs(Date makerTs) {
        this.makerTs = makerTs;
    }

    public String getMakerId() {
        return this.makerId;
    }

    public void setMakerId(String makerId) {
        this.makerId = makerId;
    }

    public Date getCheckerTs() {
        return this.checkerTs;
    }

    public void setCheckerTs(Date checkerTs) {
        this.checkerTs = checkerTs;
    }

    public String getCheckerId() {
        return this.checkerId;
    }

    public void setCheckerId(String checkerId) {
        this.checkerId = checkerId;
    }

    public String getAuthStat() {
        return this.authStat;
    }

    public void setAuthStat(String authStat) {
        this.authStat = authStat;
    }

    public TbAshsRoleMasterPK getTbAsmiRoleMasterPK() {
        return id;
    }

    public void setTbAsmiRoleMasterPK(TbAshsRoleMasterPK tbAsmiRoleMasterPK) {
        this.id = tbAsmiRoleMasterPK;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    public String getInterfaceAllowed() {
        return interfaceAllowed;
    }

    public void setInterfaceAllowed(String interfaceAllowed) {
        this.interfaceAllowed = interfaceAllowed;
    }

    public String getScreenAllowed() {
        return screenAllowed;
    }

    public void setScreenAllowed(String screenAllowed) {
        this.screenAllowed = screenAllowed;
    }

    public String getControlAllowed() {
        return controlAllowed;
    }

    public void setControlAllowed(String controlAllowed) {
        this.controlAllowed = controlAllowed;
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
        if (!(object instanceof TbAshsRoleMaster)) {
            return false;
        }
        TbAshsRoleMaster other = (TbAshsRoleMaster) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "com.iexceed.appzillon.domain.entity.TbAsmiRoleMaster[ tbAsmiRoleMasterPK=" + id + " ]";
    }

}
