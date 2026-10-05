package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrSectionRowData extends DataObject {
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
    // ///////////Should be in alphabetical
    // order///////////////////////////////////
    @JsonProperty("appearance")
    public String appearance = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("draggable")
    public String draggable = "";
    @JsonProperty("draggableid")
    public String draggableid = "";
    @JsonProperty("draggableids")
    public String draggableids = "";
    @JsonProperty("droppable")
    public String droppable = "";
    @JsonIgnore
    public String hasRespSecCol = "";
    @JsonProperty("listclickable")
    public String listclickable = "";
    @JsonProperty("options")
    public String options = "Y";
    @JsonProperty("variation")
    public String variation = "";
    @JsonIgnore
    public boolean skip = false;
    @JsonIgnore
    public ArrayList<ScrSectionColumnData> sectioncols = new ArrayList<ScrSectionColumnData>();
    @JsonIgnore
    public HashMap<String, ScrSectionColumnData> sectioncols_map = new HashMap<String, ScrSectionColumnData>();

    public ScrSectionRowData() {
    }

    public ScrSectionRowData(String defaultName) {
        name = defaultName;
        type = "SECTIONROW";
        typeClass = "SECTIONROW";
    }

    @Override
    public boolean add() {
        boolean lres = true;
        if (parent.type.equals("CONTAINER")) {
            ScrContainerData lparent = (ScrContainerData) parent;
            lparent.sectionrows.add(this);
            lparent.sectionrows_map.put(name, this);
        } else if (parent.type.equals("SECTIONCOLUMN")) {
            ScrSectionColumnData lparent = (ScrSectionColumnData) parent;
            lparent.sectionrows.add(this);
            lparent.sectionrows_map.put(name, this);
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
        ScrSectionRowData lnewlosecrowdataobject = (ScrSectionRowData) pdataobject;
        lnewlosecrowdataobject.id = id;
        lnewlosecrowdataobject.pid = pid;
        lnewlosecrowdataobject.widgetcategory = widgetcategory;
        lnewlosecrowdataobject.widgettype = widgettype;
        lnewlosecrowdataobject.options = options;
        lnewlosecrowdataobject.cssclasses = cssclasses;
        lnewlosecrowdataobject.listclickable = listclickable;
        lnewlosecrowdataobject.draggable = draggable;
        lnewlosecrowdataobject.draggableid = draggableid;
        lnewlosecrowdataobject.droppable = droppable;
        lnewlosecrowdataobject.draggableids = draggableids;
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
