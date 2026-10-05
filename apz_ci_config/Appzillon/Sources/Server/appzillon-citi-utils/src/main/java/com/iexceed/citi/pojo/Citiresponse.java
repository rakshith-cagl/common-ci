package com.iexceed.citi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Citiresponse {

    @JsonProperty("Name")
    private String name;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Version")
    private int version;
    @JsonProperty("Rows")
    private List<Rows> rows;
    @JsonProperty("cssclasses")
    private String cssclasses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCssclasses() {
        return cssclasses;
    }

    public void setCssclasses(String cssclasses) {
        this.cssclasses = cssclasses;
    }


}
