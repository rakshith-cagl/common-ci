package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the VW_MESSAGE_NOTIFY_STATS database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "VW_MESSAGE_NOTIFY_STATS")
@NamedQuery(name = "VwMessageNotifyStat.findAll", query = "SELECT v FROM VwMessageNotifyStat v")
public class VwMessageNotifyStat implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "appId")
    private String appId;

    @Column(name = "count")
    private Integer count;

    @Column(name = "datesos")
    private String datesos;

    @Temporal(TemporalType.DATE)
    @Column(name = "osdates")
    private Date osdates;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDatesos() {
        return this.datesos;
    }

    public void setDatesos(String datesos) {
        this.datesos = datesos;
    }

    public Date getOsdates() {
        return this.osdates;
    }

    public void setOsdates(Date osdates) {
        this.osdates = osdates;
    }

}