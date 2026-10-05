package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrGridColumnData extends DataObject {
    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////// // Data Variables //
    // ////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////
    @JsonProperty("id")
    public String id = "";
    @JsonProperty("controlid")
    public String controlid = "";
    @JsonProperty("pid")
    public String pid = "";
    @JsonProperty("widgetcategory")
    public String widgetcategory = "";
    @JsonProperty("widgettype")
    public String widgettype = "";
    // //////////////Should be in alphabetical
    // order///////////////////////////////
    @JsonProperty("boxshadow")
    public String boxshadow = "";
    @JsonProperty("boxstyle")
    public String boxstyle = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("collapsible")
    public String collapsible = "";
    @JsonProperty("columntype")
    public String columntype = "";
    @JsonProperty("contentcolor")
    public String contentcolor = "";
    @JsonProperty("contenttabcolor")
    public String contenttabcolor = "";
    @JsonProperty("customwidth")
    public String customwidth = "";
    @JsonProperty("customwidthtype")
    public String customwidthtype = "";
    @JsonProperty("desktophide")
    public String desktophide = "";
    @JsonProperty("desktopwidth")
    public String desktopwidth = "";
    @JsonProperty("draggable")
    public String draggable = "";
    @JsonProperty("draggableid")
    public String draggableid = "";
    @JsonProperty("draggableids")
    public String draggableids = "";
    @JsonProperty("droppable")
    public String droppable = "";
    @JsonProperty("icon")
    public String icon = "";
    @JsonProperty("iconposition")
    public String iconposition = "";
    @JsonProperty("iconsize")
    public String iconsize = "";
    @JsonProperty("labelalignment")
    public String labelalignment = "";
    @JsonProperty("maxwidth")
    public String maxwidth = "";
    @JsonProperty("minwidth")
    public String minwidth = "";
    @JsonProperty("options")
    public String options = "N";
    @JsonProperty("phonehide")
    public String phonehide = "";
    @JsonProperty("phonewidth")
    public String phonewidth = "";
    @JsonProperty("title")
    public String title = "";
    @JsonProperty("titlestyle")
    public String titlestyle = "";
    @JsonProperty("tablethide")
    public String tablethide = "";
    @JsonProperty("tabletwidth")
    public String tabletwidth = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonProperty("width")
    public String width = "";
    @JsonProperty("wshide")
    public String wshide = "";
    @JsonProperty("wswidth")
    public String wswidth = "";
    @JsonIgnore
    public boolean skip = false;
    @JsonIgnore
    public ArrayList<ScrGridRowData> gridrows = new ArrayList<ScrGridRowData>();
    @JsonIgnore
    public HashMap<String, ScrGridRowData> gridrows_map = new HashMap<String, ScrGridRowData>();
    @JsonIgnore
    public ArrayList<ScrPanelData> gridpane = new ArrayList<ScrPanelData>();
    @JsonIgnore
    public HashMap<String, ScrPanelData> gridpane_map = new HashMap<String, ScrPanelData>();
    @JsonIgnore
    public ArrayList<ScrContainerData> containers = new ArrayList<ScrContainerData>();
    @JsonIgnore
    public HashMap<String, ScrContainerData> containers_map = new HashMap<String, ScrContainerData>();

    public ScrGridColumnData() {
        // Default constructor
    }
	/*@JsonIgnore
	public ArrayList<ScrChildScreenData> childscreens = new ArrayList<ScrChildScreenData>();
	@JsonIgnore
	public HashMap<String, ScrChildScreenData> childscreens_map = new HashMap<String, ScrChildScreenData>();*/

    // // Implemented Methods From Data Object Abstract Class
    @Override
    public boolean add() {
        boolean lres = true;
        ScrGridRowData lparent = (ScrGridRowData) parent;
        lparent.gridcols.add(this);
        lparent.gridcols_map.put(name, this);
        return lres;
    }


    public boolean rename(String poldname, String pnewname) {
        boolean lres = true;
        return lres;
    }


    public boolean refactorRename(String poldname, String pnewname) {
        boolean lres = true;
        return lres;
    }


    public boolean del() {
        boolean lres = true;
        return lres;
    }


    public boolean copy(DataObject pdataobject) {
        boolean lres = true;
        return lres;
    }


    public boolean load() {
        boolean lres = true;
        return lres;
    }


    public boolean save() {
        boolean lres = true;
        return lres;
    }


    public boolean validate() {
        boolean lres = true;
        return lres;
    }


    public boolean compare(DataObject pwithobj) {
        boolean lres = true;
        return lres;
    }
}
