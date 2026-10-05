package com.iexceed.citi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PanelSections {
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("DisplayName")
    private String DisplayName;
    @JsonProperty("Containers")
    private List<Containers> Containers;
    @JsonProperty("cssclasses")
    private String cssclasses;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public List<Containers> getContainers() {
        return Containers;
    }

    public void setContainers(List<Containers> containers) {
        Containers = containers;
    }

    public String getCssclasses() {
        return cssclasses;
    }

    public void setCssclasses(String cssclasses) {
        this.cssclasses = cssclasses;
    }


}
