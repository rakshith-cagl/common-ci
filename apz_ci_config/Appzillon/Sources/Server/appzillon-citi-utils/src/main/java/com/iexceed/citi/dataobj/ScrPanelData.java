package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrPanelData extends DataObject {
    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////// // Data Variables //
    // ////////////////////////////
    // ////////////////////f////////////////////////////////////////////////////////
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
    @JsonProperty("appearance")
    public String appearance = "";

    // /////////////Should be in alphabetical
    // order/////////////////////////////////
    @JsonProperty("bottommarginvalue")
    public String bottommarginvalue = "";
    @JsonProperty("bottommargincustom")
    public String bottommargincustom = "";
    @JsonProperty("bottommargincustomtype")
    public String bottommargincustomtype = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("events")
    public ArrayList<EventData> events = new ArrayList<EventData>();
    @JsonProperty("icon")
    public String icon = "";
    @JsonProperty("leftmargin")
    public String leftmargin = "";
    @JsonProperty("leftmarginvalue")
    public String leftmarginvalue = "";
    @JsonProperty("leftmargincustom")
    public String leftmargincustom = "";
    @JsonProperty("leftmargincustomtype")
    public String leftmargincustomtype = "";
    @JsonProperty("loop")
    public String loop = "";
    @JsonProperty("options")
    public String options = "Y";
    @JsonProperty("orientation")
    public String orientation = "";
    @JsonProperty("panelvariation")
    public String panelvariation = "";
    @JsonProperty("responsive")
    public String responsive = "";
    @JsonProperty("rightmarginvalue")
    public String rightmarginvalue = "";
    @JsonProperty("rightmargincustom")
    public String rightmargincustom = "";
    @JsonProperty("rightmargincustomtype")
    public String rightmargincustomtype = "";
    @JsonProperty("theme")
    public String theme = "";
    @JsonProperty("title")
    public String title = "";
    @JsonProperty("topmarginvalue")
    public String topmarginvalue = "";
    @JsonProperty("topmargincustom")
    public String topmargincustom = "";
    @JsonProperty("topmargincustomtype")
    public String topmargincustomtype = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonIgnore
    public ArrayList<ScrGridColumnData> gridcols = new ArrayList<ScrGridColumnData>();
    @JsonIgnore
    public HashMap<String, ScrGridColumnData> gridcols_map = new HashMap<String, ScrGridColumnData>();
    @JsonIgnore
    public ArrayList<ScrPanelSectionData> panelsections = new ArrayList<ScrPanelSectionData>();
    @JsonIgnore
    public HashMap<String, ScrPanelSectionData> panelsections_map = new HashMap<String, ScrPanelSectionData>();

    public ScrPanelData() {
        // Default constructor
    }

    // // Implemented Methods From Data Object Abstract Class
    @Override
    public boolean add() {
        boolean lres = true;
        ScrGridColumnData lparent = (ScrGridColumnData) parent;
        lparent.gridpane.add(this);
        lparent.gridpane_map.put(name, this);
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
        ScrPanelData lnewlopanedataobject = (ScrPanelData) pdataobject;
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