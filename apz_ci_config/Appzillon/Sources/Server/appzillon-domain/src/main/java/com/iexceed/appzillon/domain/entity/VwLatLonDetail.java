package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the VW_LAT_LON_DETAILS database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "VW_LAT_LON_DETAILS")
@NamedQuery(name = "VwLatLonDetail.findAll", query = "SELECT v FROM VwLatLonDetail v")
public class VwLatLonDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "COUNT_LAT")
    private Integer countLat;

    @Column(name = "COUNT_LON")
    private Integer countLon;

    @Column(name = "FORMATTED_ADDRESS")
    private String formattedAddress;

    @Column(name = "LATITUDE")
    private String latitude;

    @Column(name = "LONGITUDE")
    private String longitude;

    @Column(name = "USER_ID")
    private String userId;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getCountLat() {
        return this.countLat;
    }

    public void setCountLat(Integer countLat) {
        this.countLat = countLat;
    }

    public Integer getCountLon() {
        return this.countLon;
    }

    public void setCountLon(Integer countLon) {
        this.countLon = countLon;
    }

    public String getFormattedAddress() {
        return this.formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
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

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}