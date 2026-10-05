package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASMI_APP_ID_VERSION database table.
 */
@Entity
@Table(name = "TB_ASMI_APP_ID_VERSION")
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@NamedQuery(name = "TbAsmiAppIdVersion.findAll", query = "SELECT t FROM TbAsmiAppIdVersion t")
public class TbAsmiAppIdVersion implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiAppIdVersionPK id;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    @Column(name = "CREATE_USER_ID")
    private String createUserId;

    @Column(name = "VERSION_NO")
    private int versionNo;

    public TbAsmiAppIdVersionPK getId() {
        return this.id;
    }

    public void setId(TbAsmiAppIdVersionPK id) {
        this.id = id;
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