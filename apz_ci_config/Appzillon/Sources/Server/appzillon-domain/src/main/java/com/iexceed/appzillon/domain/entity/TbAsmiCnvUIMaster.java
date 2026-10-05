package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASMI_CNVUI_MASTER database table.
 */
@Entity
@Table(name = "TB_ASMI_CNVUI_MASTER")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@NamedQuery(name = "TbAsmiCnvUIMaster.findAll", query = "SELECT t FROM TbAsmiCnvUIMaster t")
public class TbAsmiCnvUIMaster implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiCnvUIMasterPK id;

    @Column(name = "CNVUI_DESC")
    private String cnvUIDesc;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    @Column(name = "CREATE_USER_ID")
    private String createUserId;

    @Column(name = "VERSION_NO")
    private int versionNo;

    public TbAsmiCnvUIMasterPK getId() {
        return this.id;
    }

    public void setId(TbAsmiCnvUIMasterPK id) {
        this.id = id;
    }

    public String getCnvUIDesc() {
        return this.cnvUIDesc;
    }

    public void setCnvUIDesc(String cnvUIDesc) {
        this.cnvUIDesc = cnvUIDesc;
    }

    public Timestamp getCreateTs() {
        return this.createTs;
    }

    public void setCreateTs(Timestamp createTs) {
        this.createTs = createTs;
    }

    public String getCreateUserId() {
        return this.createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public int getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }
}