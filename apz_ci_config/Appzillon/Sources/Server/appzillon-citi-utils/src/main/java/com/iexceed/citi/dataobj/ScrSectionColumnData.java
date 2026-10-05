package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrSectionColumnData extends DataObject {
    @JsonProperty("id")
    public String id = "";
    @JsonProperty("pid")
    public String pid = "";

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////// // Data Variables //
    // ////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////
    @JsonProperty("widgetcategory")
    public String widgetcategory = "";
    @JsonProperty("widgettype")
    public String widgettype = "";
    // ///////////Should be in alphabetical order/////////////////////////
    @JsonProperty("appearance")
    public String appearance = "";
    @JsonProperty("controlid")
    public String controlid = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("customwidth")
    public String customwidth = "";
    @JsonProperty("customwidthtype")
    public String customwidthtype = "px";
    @JsonProperty("desktophide")
    public String desktophide = "";
    @JsonProperty("desktopwidth")
    public String desktopwidth = "";
    @JsonProperty("draggable")
    public String draggable = "";
    @JsonProperty("draggableid")
    public String draggableid = "";
    @JsonProperty("droppable")
    public String droppable = "";
    @JsonProperty("draggableids")
    public String draggableids = "";
    @JsonProperty("fieldset")
    public String fieldset = "";
    @JsonProperty("fieldsettitle")
    public String fieldsettitle = "";
    @JsonProperty("horizontalalignment")
    public String horizontalalignment = "";
    @JsonProperty("horizontalgrouping")
    public String horizontalgrouping = "";
    @JsonProperty("labelalignment")
    public String labelalignment = "";
    @JsonProperty("listcolumnwidth")
    public String listcolumnwidth = "";
    @JsonProperty("maxwidth")
    public String maxwidth = "";
    @JsonProperty("minwidth")
    public String minwidth = "";
    @JsonProperty("options")
    public String options = "Y";
    @JsonProperty("phonehide")
    public String phonehide = "";
    @JsonProperty("phonewidth")
    public String phonewidth = "";
    @JsonProperty("semantics")
    public String semantics = "";
    @JsonProperty("tablethide")
    public String tablethide = "";
    @JsonProperty("tabletwidth")
    public String tabletwidth = "";
    @JsonProperty("verticalalignment")
    public String verticalalignment = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonProperty("width")
    public String width = "";
    @JsonProperty("wshide")
    public String wshide = "";
    @JsonProperty("wswidth")
    public String wswidth = "";
    @JsonIgnore
    public ArrayList<ScrSectionRowData> sectionrows = new ArrayList<ScrSectionRowData>();
    @JsonIgnore
    public HashMap<String, ScrSectionRowData> sectionrows_map = new HashMap<String, ScrSectionRowData>();
    @JsonIgnore
    public ArrayList<ScrElementData> elements = new ArrayList<ScrElementData>();
    @JsonIgnore
    public HashMap<String, ScrElementData> elements_map = new HashMap<String, ScrElementData>();

    public ScrSectionColumnData() {
    }

    public ScrSectionColumnData(String defaultName) {
        name = defaultName;
        type = "SECTIONCOLUMN";
        typeClass = "SECTIONCOLUMN";
    }

    @Override
    public boolean add() {
        boolean lres = true;
        ScrSectionRowData lparent = (ScrSectionRowData) parent;
        lparent.sectioncols.add(this);
        lparent.sectioncols_map.put(name, this);
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
        ScrSectionColumnData lnewloseccoldataobject = (ScrSectionColumnData) pdataobject;
        lnewloseccoldataobject.id = id;
        lnewloseccoldataobject.pid = pid;
        lnewloseccoldataobject.widgetcategory = widgetcategory;
        lnewloseccoldataobject.widgettype = widgettype;
        lnewloseccoldataobject.name = name;
        lnewloseccoldataobject.verticalalignment = verticalalignment;
        lnewloseccoldataobject.labelalignment = labelalignment;
        lnewloseccoldataobject.horizontalalignment = horizontalalignment;
        lnewloseccoldataobject.width = width;
        lnewloseccoldataobject.maxwidth = maxwidth;
        lnewloseccoldataobject.minwidth = minwidth;
        lnewloseccoldataobject.options = options;
        lnewloseccoldataobject.cssclasses = cssclasses;
        lnewloseccoldataobject.customwidth = customwidth;
        lnewloseccoldataobject.customwidthtype = customwidthtype;
        lnewloseccoldataobject.horizontalgrouping = horizontalgrouping;
        lnewloseccoldataobject.listcolumnwidth = listcolumnwidth;
        lnewloseccoldataobject.draggable = draggable;
        lnewloseccoldataobject.draggableid = draggableid;
        lnewloseccoldataobject.droppable = droppable;
        lnewloseccoldataobject.draggableids = draggableids;
        lnewloseccoldataobject.fieldset = fieldset;
        lnewloseccoldataobject.fieldsettitle = fieldsettitle;
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
