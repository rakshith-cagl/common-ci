package com.iexceed.citi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Columns {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("width")
    private String width;
    @JsonProperty("Alginment")
    private String alginment;
    @JsonProperty("Elements")
    private List<Elements> elements;
    @JsonProperty("Containers")
    private List<Containers> containers;
    @JsonProperty("ROWS")
    private List<Rows> rows;
    @JsonProperty("HorizontalAlignment")
    private String horizontalAlignment;
    @JsonProperty("VerticalAlignment")
    private String verticalAlignment;
    @JsonProperty("cssclasses")
    private String cssclasses;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getAlginment() {
        return alginment;
    }

    public void setAlginment(String alginment) {
        this.alginment = alginment;
    }

    public List<Elements> getElements() {
        return elements;
    }

    public void setElements(List<Elements> elements) {
        this.elements = elements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Containers> getContainers() {
        return containers;
    }

    public void setContainers(List<Containers> containers) {
        this.containers = containers;
    }

    public String getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(String horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public String getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(String verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public String getCssclasses() {
        return cssclasses;
    }

    public void setCssclasses(String cssclasses) {
        this.cssclasses = cssclasses;
    }

}
