package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrPanelSectionData extends DataObject {
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

    // ///////////////////////Should be in alphabetical// order////////////////////////
    @JsonProperty("bottompaddingvalue")
    public String bottompaddingvalue = "";
    @JsonProperty("bottompaddingcustom")
    public String bottompaddingcustom = "";
    @JsonProperty("bottompaddingcustomtype")
    public String bottompaddingcustomtype = "px";
    @JsonProperty("boxshadow")
    public String boxshadow = "";
    @JsonProperty("boxstyle")
    public String boxstyle = "";
    @JsonProperty("collapsible")
    public String collapsible = "";
    @JsonProperty("contentcolor")
    public String contentcolor = "";
    @JsonProperty("contenttabcolor")
    public String contenttabcolor = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("customwidth")
    public String customwidth = "";
    @JsonProperty("customwidthtype")
    public String customwidthtype = "";
    @JsonProperty("events")
    public ArrayList<EventData> events = new ArrayList<EventData>();
    @JsonProperty("icon")
    public String icon = "";
    @JsonProperty("iconposition")
    public String iconposition = "";
    @JsonProperty("iconsize")
    public String iconsize = "";
    @JsonProperty("labelalignment")
    public String labelalignment = "";
    @JsonProperty("leftpadding")
    public String leftpadding = "ALL";
    @JsonProperty("leftpaddingvalue")
    public String leftpaddingvalue = "";
    @JsonProperty("leftpaddingcustom")
    public String leftpaddingcustom = "";
    @JsonProperty("leftpaddingcustomtype")
    public String leftpaddingcustomtype = "px";
    @JsonProperty("maxwidth")
    public String maxwidth = "";
    @JsonProperty("minwidth")
    public String minwidth = "";
    @JsonProperty("options")
    public String options = "Y";
    @JsonProperty("panetype")
    public String panetype = "";
    @JsonProperty("rightpaddingvalue")
    public String rightpaddingvalue = "";
    @JsonProperty("rightpaddingcustom")
    public String rightpaddingcustom = "";
    @JsonProperty("rightpaddingcustomtype")
    public String rightpaddingcustomtype = "PX";
    @JsonProperty("spinner")
    public String spinner = "";
    @JsonProperty("spinnersize")
    public String spinnersize = "";
    @JsonProperty("spinnertext")
    public String spinnertext = "";
    @JsonProperty("state")
    public String state = "";
    @JsonProperty("theme")
    public String theme = "";
    @JsonProperty("title")
    public String title = "";
    @JsonProperty("titlestyle")
    public String titlestyle = "";
    @JsonProperty("toppaddingvalue")
    public String toppaddingvalue = "";
    @JsonProperty("toppaddingcustom")
    public String toppaddingcustom = "";
    @JsonProperty("toppaddingcustomtype")
    public String toppaddingcustomtype = "PX";
    @JsonProperty("transpspinner")
    public String transpspinner = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonProperty("width")
    public String width = "";
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

    public ScrPanelSectionData() {
        // Default constructor
    }

    // // Implemented Methods From Data Object Abstract Class
    @Override
    public boolean add() {
        boolean lres = true;
        ScrPanelData lparent = (ScrPanelData) parent;
        lparent.panelsections.add(this);
        lparent.panelsections_map.put(name, this);
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
        ScrPanelSectionData lnewlopanedataobject = (ScrPanelSectionData) pdataobject;
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
