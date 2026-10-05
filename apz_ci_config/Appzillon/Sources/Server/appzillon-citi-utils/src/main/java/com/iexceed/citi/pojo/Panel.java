package com.iexceed.citi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Panel {
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Type")
    private String Type;
    @JsonProperty("PanelSections")
    private List<PanelSections> PanelSections;
    @JsonProperty("Location")
    private String Location;
    @JsonProperty("Value")
    private String Value;
    @JsonProperty("Custom")
    private String Custom;
    @JsonProperty("CustomType")
    private String CustomType;
    @JsonProperty("cssclasses")
    private String cssclasses;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public List<PanelSections> getPanelSections() {
        return PanelSections;
    }

    public void setPanelSections(List<PanelSections> panelSections) {
        PanelSections = panelSections;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getCustom() {
        return Custom;
    }

    public void setCustom(String custom) {
        Custom = custom;
    }

    public String getCustomType() {
        return CustomType;
    }

    public void setCustomType(String customType) {
        CustomType = customType;
    }

    public String getCssclasses() {
        return cssclasses;
    }

    public void setCssclasses(String cssclasses) {
        this.cssclasses = cssclasses;
    }


}
