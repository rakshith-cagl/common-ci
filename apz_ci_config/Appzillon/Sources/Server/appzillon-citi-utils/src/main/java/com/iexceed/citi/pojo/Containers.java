package com.iexceed.citi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Containers {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("DisplayName")
    private String displayName;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("State")
    private String state;
    @JsonProperty("DateFormat")
    private String dateFormat;
    @JsonProperty("DateTimeFormat")
    private String dateTimeFormat;
    @JsonProperty("NodeName")
    private String[] nodeName;
    @JsonProperty("NodeType")
    private String[] nodeType;
    @JsonProperty("NodeParent")
    private String[] nodeParent;
    @JsonProperty("RelType")
    private String[] relType;
    @JsonProperty("ROWS")
    private List<Rows> rows;
    @JsonProperty("Alginment")
    private String alginment;
    @JsonProperty("Orientation")
    private String orientation;
    @JsonProperty("Visible")
    private String visible;
    @JsonProperty("ICON")
    private String icon;
    @JsonProperty("paginationstyle")
    private String paginationstyle;
    @JsonProperty("pagesize")
    private String pagesize;
    @JsonProperty("dynamicpagesize")
    private String dynamicpagesize;
    @JsonProperty("ContainerOptions")
    private List<ControlOptions> containeroptions;
    @JsonProperty("Appearance")
    private String appearance;
    @JsonProperty("Variation")
    private String variation;
    @JsonProperty("cssclasses")
    private String cssclasses;

    public String getAlginment() {
        return alginment;
    }

    public void setAlginment(String alginment) {
        this.alginment = alginment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String[] getNodeName() {
        return nodeName;
    }

    public void setNodeName(String[] nodeName) {
        this.nodeName = nodeName;
    }

    public String[] getNodeType() {
        return nodeType;
    }

    public void setNodeType(String[] nodeType) {
        this.nodeType = nodeType;
    }

    public String[] getNodeParent() {
        return nodeParent;
    }

    public void setNodeParent(String[] nodeParent) {
        this.nodeParent = nodeParent;
    }

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getAppearance() {
        return appearance;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }

    public String[] getRelType() {
        return relType;
    }

    public void setRelType(String[] relType) {
        this.relType = relType;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getCssclasses() {
        return cssclasses;
    }

    public void setCssclasses(String cssclasses) {
        this.cssclasses = cssclasses;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String iCON) {
        icon = iCON;
    }

    public String getPaginationstyle() {
        return paginationstyle;
    }

    public void setPaginationstyle(String paginationstyle) {
        this.paginationstyle = paginationstyle;
    }

    public String getPagesize() {
        return pagesize;
    }

    public void setPagesize(String pagesize) {
        this.pagesize = pagesize;
    }

    public String getDynamicpagesize() {
        return dynamicpagesize;
    }

    public void setDynamicpagesize(String dynamicpagesize) {
        this.dynamicpagesize = dynamicpagesize;
    }

    public List<ControlOptions> getContaineroptions() {
        return containeroptions;
    }

    public void setContaineroptions(List<ControlOptions> containeroptions) {
        this.containeroptions = containeroptions;
    }

}
