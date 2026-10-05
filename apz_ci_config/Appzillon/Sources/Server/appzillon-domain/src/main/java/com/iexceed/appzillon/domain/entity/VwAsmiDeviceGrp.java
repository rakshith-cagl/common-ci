package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the vw_asmi_device_grps database table.
 */
@Entity
@Table(name = "VW_ASMI_DEVICE_GRPS")
@NamedQuery(name = "VwAsmiDeviceGrp.findAll", query = "SELECT v FROM VwAsmiDeviceGrp v")
public class VwAsmiDeviceGrp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "DEVICE_GROUP_DESCRIPTION")
    private String deviceGroupDescription;

    @Column(name = "DEVICE_GROUP_ID")
    private String deviceGroupId;

    @Column(name = "HEIGHT")
    private int height;

    private String orientation;

    @Column(name = "WIDTH")
    private int width;

    @Column(name = "OS")
    private String os;

    public VwAsmiDeviceGrp() {
        // Default constructor
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDeviceGroupDescription() {
        return this.deviceGroupDescription;
    }

    public void setDeviceGroupDescription(String deviceGroupDescription) {
        this.deviceGroupDescription = deviceGroupDescription;
    }

    public String getDeviceGroupId() {
        return this.deviceGroupId;
    }

    public void setDeviceGroupId(String deviceGroupId) {
        this.deviceGroupId = deviceGroupId;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getOrientation() {
        return this.orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}