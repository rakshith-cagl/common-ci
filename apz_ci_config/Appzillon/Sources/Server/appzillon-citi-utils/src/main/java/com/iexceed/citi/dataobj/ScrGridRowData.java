package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrGridRowData extends DataObject {
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
    // ////////////////////Should be in alphabetical
    // order////////////////////////
    @JsonProperty("alignment")
    public String alignment = "";
    @JsonProperty("centercolumns")
    public String centercolumns = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("dialogtype")
    public String dialogtype = "";
    @JsonProperty("draggable")
    public String draggable = "";
    @JsonProperty("draggableid")
    public String draggableid = "";
    @JsonProperty("draggableids")
    public String draggableids = "";
    @JsonProperty("draggablemodal")
    public String draggablemodal = "";
    @JsonProperty("droppable")
    public String droppable = "";
    @JsonProperty("equalize")
    public String equalize = "";
    @JsonProperty("options")
    public String options = "Y";
    @JsonProperty("titlestyle")
    public String titlestyle = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonIgnore
    public boolean skip = false;
    @JsonIgnore
    public ArrayList<ScrGridColumnData> gridcols = new ArrayList<ScrGridColumnData>();
    @JsonIgnore
    public HashMap<String, ScrGridColumnData> gridcols_map = new HashMap<String, ScrGridColumnData>();
    @JsonIgnore
    public ArrayList<ScrPanelSectionData> pane = new ArrayList<ScrPanelSectionData>();
    @JsonIgnore
    public HashMap<String, ScrPanelSectionData> pane_map = new HashMap<String, ScrPanelSectionData>();

    public ScrGridRowData() {
        // Default constructor
    }

    @Override
    public boolean add() {
        boolean lres = true;
        if (parent.type.equals("LAYOUTPORTION")) {
            PortionData lparent = (PortionData) parent;
            lparent.gridrows.add(this);
            lparent.gridrows_map.put(name, this);
        } else if (parent.type.equals("GRIDCOLUMN")) {
            ScrGridColumnData lparent = (ScrGridColumnData) parent;
            lparent.gridrows.add(this);
            lparent.gridrows_map.put(name, this);
        } else if (parent.type.equals("PANELSECTION")) {
            ScrPanelSectionData lparent = (ScrPanelSectionData) parent;
            lparent.gridrows.add(this);
            lparent.gridrows_map.put(name, this);
        } else if (parent.type.equals("POPUP")) {
            ScrPopupData lparent = (ScrPopupData) parent;
            lparent.gridrows.add(this);
            lparent.gridrows_map.put(name, this);
        }
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
