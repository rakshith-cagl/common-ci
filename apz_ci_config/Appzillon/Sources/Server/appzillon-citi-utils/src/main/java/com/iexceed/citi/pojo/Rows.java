package com.iexceed.citi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Rows {

    @JsonProperty("Name")
    private String Name;
    @JsonProperty("Type")
    private String Type;
    @JsonProperty("noofcolumns")
    private String noofcolumns;
    @JsonProperty("Panels")
    private List<Panel> panel;
    @JsonProperty("DisplayName")
    private String DisplayName;
    @JsonProperty("columns")
    private List<Columns> columns;
    @JsonProperty("visible")
    private String visible;
    @JsonProperty("Rows")
    private List<Rows> rows;
    @JsonProperty("Appearance")
    private String Appearance;
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

    public String getNoofcolumns() {
        return noofcolumns;
    }

    public void setNoofcolumns(String noofcolumns) {
        this.noofcolumns = noofcolumns;
    }

    public List<Panel> getPanel() {
        return panel;
    }

    public void setPanel(List<Panel> panel) {
        this.panel = panel;
    }

    public List<Columns> getColumns() {
        return columns;
    }

    public void setColumns(List<Columns> columns) {
        this.columns = columns;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public String getAppearance() {
        return Appearance;
    }

    public void setAppearance(String appearance) {
        Appearance = appearance;
    }

    public String getCssclasses() {
        return cssclasses;
    }

    public void setCssclasses(String cssclasses) {
        this.cssclasses = cssclasses;
    }

}
