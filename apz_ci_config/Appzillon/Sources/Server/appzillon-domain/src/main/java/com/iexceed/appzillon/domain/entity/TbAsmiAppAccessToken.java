package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASMI_APP_ACCESS_TOKEN database table.
 */
@Entity
@Table(name = "TB_ASMI_APP_ACCESS_TOKEN")
@NamedQuery(name = "TbAsmiAppAccessToken.findAll", query = "SELECT t FROM TbAsmiAppAccessToken t")
public class TbAsmiAppAccessToken implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiAppAccessTokenPK id;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    public TbAsmiAppAccessTokenPK getId() {
        return this.id;
    }

    public void setId(TbAsmiAppAccessTokenPK id) {
        this.id = id;
    }

    public Timestamp getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Timestamp createTs) {
        this.createTs = createTs;
    }
}