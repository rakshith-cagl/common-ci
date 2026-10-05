package com.iexceed.screenDef.json;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreenDef {
    private String scr;
    private String layout;
    private String id;
    private String displayname;
    private String screenType;
    private String icon;
    private String[] ifaces;
    private containers containers;
    private groups groups;
    private uiInits uiInits;
    private String[] scripts;
    private String[] scrProps;
    private String[] maps;
    private List<Gauges> gauges;
    private String[] charts;

    public String getScr() {
        return scr;
    }

    public void setScr(String scr) {
        this.scr = scr;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public String[] getIfaces() {
        return ifaces;
    }

    public void setIfaces(String[] ifaces) {
        this.ifaces = ifaces;
    }


    public containers getContainers() {
        return containers;
    }

    public void setContainers(containers containers) {
        this.containers = containers;
    }

    public groups getGroups() {
        return groups;
    }

    public void setGroups(groups groups) {
        this.groups = groups;
    }

    public uiInits getUiInits() {
        return uiInits;
    }

    public void setUiInits(uiInits uiInits) {
        this.uiInits = uiInits;
    }

    public String[] getScripts() {
        return scripts;
    }

    public void setScripts(String[] scripts) {
        this.scripts = scripts;
    }

    public String[] getScrProps() {
        return scrProps;
    }

    public void setScrProps(String[] scrProps) {
        this.scrProps = scrProps;
    }

    public String[] getMaps() {
        return maps;
    }

    public void setMaps(String[] maps) {
        this.maps = maps;
    }

    public List<Gauges> getGauges() {
        return gauges;
    }

    public void setGauges(List<Gauges> gauges) {
        this.gauges = gauges;
    }

    public String[] getCharts() {
        return charts;
    }

    public void setCharts(String[] charts) {
        this.charts = charts;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
