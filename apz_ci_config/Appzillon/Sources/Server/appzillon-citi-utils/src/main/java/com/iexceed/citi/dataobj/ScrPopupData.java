package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrPopupData extends DataObject {
    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////// // Data Variables //
    // ////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////
    @JsonProperty("id")
    public String id = "";
    @JsonProperty("pid")
    public String pid = "";
    @JsonProperty("widgetcategory")
    public String widgetcategory = "";
    @JsonProperty("widgettype")
    public String widgettype = "";
    @JsonProperty("canceltitle")
    public String canceltitle = "";

    // /////////////////Should be in alphabetical
    // order///////////////////////////
    @JsonProperty("customwidth")
    public String customwidth = "";
    @JsonProperty("customwidthtype")
    public String customwidthtype = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("desktopwidth")
    public String desktopwidth = "";
    @JsonProperty("dialogfooter")
    public String dialogfooter = "";
    @JsonProperty("draggablemodal")
    public String draggablemodal = "";
    @JsonProperty("events")
    public ArrayList<EventData> events = new ArrayList<EventData>();
    @JsonProperty("footertype")
    public String footertype = "";
    @JsonProperty("modalfooter")
    public String modalfooter = "";
    @JsonProperty("oktitle")
    public String oktitle = "";
    @JsonProperty("options")
    public String options = "";
    @JsonProperty("phonewidth")
    public String phonewidth = "";
    @JsonProperty("state")
    public String state = "";
    @JsonProperty("tabletwidth")
    public String tabletwidth = "";
    @JsonProperty("title")
    public String title = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonProperty("width")
    public String width = "";
    @JsonProperty("wswidth")
    public String wswidth = "";
    @JsonIgnore
    public ArrayList<ScrGridRowData> gridrows = new ArrayList<ScrGridRowData>();
    @JsonIgnore
    public HashMap<String, ScrGridRowData> gridrows_map = new HashMap<String, ScrGridRowData>();

    public ScrPopupData() {
        // Default constructor
    }

    // // Implemented Methods From Data Object Abstract Class
    @Override
    public boolean add() {
        boolean lres = true;
        PortionData lparent = (PortionData) parent;
        lparent.popup.add(this);
        lparent.popup_map.put(name, this);
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
        ScrPopupData lnewlopopupdataobject = (ScrPopupData) pdataobject;
        lnewlopopupdataobject.id = id;
        lnewlopopupdataobject.pid = pid;
        lnewlopopupdataobject.widgetcategory = widgetcategory;
        lnewlopopupdataobject.widgettype = widgettype;
        lnewlopopupdataobject.name = name;
        lnewlopopupdataobject.cssclasses = cssclasses;
        lnewlopopupdataobject.width = width;
        lnewlopopupdataobject.options = options;
        lnewlopopupdataobject.state = state;
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
