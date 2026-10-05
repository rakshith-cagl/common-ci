package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASLG_DEVICE_LOCATION database table.
 */
@Entity
@Table(name = "TB_ASLG_DEVICE_LOCATION")
@NamedQuery(name = "TbAslgDeviceLocation.findAll", query = "SELECT t FROM TbAslgDeviceLocation t")
public class TbAslgDeviceLocation implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAslgDeviceLocationPK id;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    @Column(name = "LATITUDE")
    private String latitude;

    @Column(name = "LONGITUDE")
    private String longitude;

    public TbAslgDeviceLocation() {
        // Default constructor
    }

    public TbAslgDeviceLocationPK getId() {
        return this.id;
    }

    public void setId(TbAslgDeviceLocationPK id) {
        this.id = id;
    }

    public Timestamp getCreateTs() {
        return this.createTs;
    }

    public void setCreateTs(Timestamp createTs) {
        this.createTs = createTs;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}