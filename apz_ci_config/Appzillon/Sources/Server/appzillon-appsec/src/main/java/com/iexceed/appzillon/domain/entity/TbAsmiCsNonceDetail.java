package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;


/**
 * The persistent class for the TB_ASMI_CS_NONCEDETAILS database table.
 */
@Entity
@Table(name = "TB_ASMI_CS_NONCEDETAILS")
@NamedQuery(name = "TbAsmiCsNonceDetail.findAll", query = "SELECT t FROM TbAsmiCsNonceDetail t")
public class TbAsmiCsNonceDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiCsNonceDetailPK id;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    @Column(name = "SERVER_TOKEN")
    private String serverToken;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_ON")
    private LocalDate createdOn;

    public TbAsmiCsNonceDetail() { // default constructor
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public TbAsmiCsNonceDetailPK getId() {
        return id;
    }

    public void setId(TbAsmiCsNonceDetailPK id) {
        this.id = id;
    }


    public Timestamp getCreateTs() {
        return createTs;
    }


    public void setCreateTs(Timestamp createTs) {
        this.createTs = createTs;
    }


    public String getServerToken() {
        return serverToken;
    }


    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }


    @Override
    public String toString() {
        return "TbAsmiCsNonceDetail{" +
                "id=" + id +
                ", createTs=" + createTs +
                ", serverToken='" + serverToken + '\'' +
                ", status='" + status + '\'' +
                ", createdOn=" + createdOn +
                '}';
    }

}