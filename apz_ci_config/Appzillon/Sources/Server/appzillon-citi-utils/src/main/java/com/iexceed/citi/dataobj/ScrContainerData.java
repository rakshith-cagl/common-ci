package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScrContainerData extends DataObject {
    // // Properties
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
    // /////////////////////////Should be in aplhabetical
    // order////////////////////
    @JsonProperty("animation")
    public String animation = "";
    @JsonProperty("appearance")
    public String appearance = "";
    @JsonProperty("bgattachment")
    public String bgattachment = "";
    @JsonProperty("bgcolor")
    public String bgcolor = "";
    @JsonProperty("bgimage")
    public String bgimage = "";
    @JsonProperty("bgposition")
    public String bgposition = "";
    @JsonProperty("bgrepeat")
    public String bgrepeat = "";
    @JsonProperty("bgsize")
    public String bgsize = "";
    @JsonProperty("biphier")
    public String biphier = "";
    @JsonProperty("chartbuilderfunction")
    public String chartbuilderfunction = "";
    @JsonProperty("chartstylesheet")
    public String chartstylesheet = "";
    @JsonProperty("charttype")
    public String charttype = "";
    @JsonProperty("chartxtitle")
    public String chartxtitle = "";
    @JsonProperty("chartyelements")
    public ArrayList<ChartElementData> chartyelements = new ArrayList<ChartElementData>();
    @JsonProperty("chartytitle")
    public String chartytitle = "";
    @JsonProperty("chartzelements")
    public ArrayList<ChartElementData> chartzelements = new ArrayList<ChartElementData>();
    @JsonProperty("chartztitle")
    public String chartztitle = "";
    @JsonProperty("collapsibleicon")
    public String collapsibleicon = "";
    @JsonProperty("containerstate")
    public String containerstate = "";
    @JsonProperty("contentalignment")
    public String contentalignment = "";
    @JsonProperty("contextmenu")
    public String contextmenu = "";
    @JsonProperty("createremoveupdate")
    public String createremoveupdate = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("custom")
    public String custom = "N";
    @JsonProperty("datajson")
    public String datajson = "";
    @JsonProperty("datamodeltype")
    public String datamodeltype;
    @JsonProperty("defaultProducts")
    public ArrayList<String> defaultProducts = new ArrayList<String>();
    @JsonProperty("deletewithundo")
    public String deletewithundo = "";
    @JsonProperty("decorations")
    public ArrayList<Decoration> decorations = new ArrayList<Decoration>();
    @JsonProperty("display")
    public String display = "";
    @JsonProperty("displayname")
    public String displayname = "";
    @JsonProperty("draggable")
    public String draggable = "";
    @JsonProperty("draggableid")
    public String draggableid = "";
    @JsonProperty("draggableids")
    public String draggableids = "";
    @JsonProperty("droppable")
    public String droppable = "";
    @JsonProperty("dynamicpagesize")
    public String dynamicpagesize = "";
    @JsonProperty("dynamicpagesizevalue")
    public String dynamicpagesizevalue = "";
    @JsonProperty("editmode")
    public String editmode = "";
    @JsonProperty("events")
    public ArrayList<EventData> events = new ArrayList<EventData>();
    @JsonProperty("expandableicon")
    public String expandableicon = "";
    @JsonProperty("expandableposition")
    public String expandableposition = "";
    @JsonProperty("externalname")
    public String externalname = "";
    @JsonProperty("filtercolumns")
    public String filtercolumns = "";
    @JsonProperty("filteronline")
    public String filteronline = "";
    @JsonProperty("gaugeranges")
    public ArrayList<GaugeRange> gaugeranges = new ArrayList<GaugeRange>();
    @JsonProperty("gaugetype")
    public String gaugetype = "";
    @JsonProperty("gaugevalues")
    public ArrayList<GuageElementData> gaugevalues = new ArrayList<GuageElementData>();
    @JsonProperty("height")
    public String height = "";
    @JsonProperty("heighttype")
    public String heighttype = "";
    @JsonProperty("hovereffect")
    public String hovereffect = "";
    @JsonProperty("icon")
    public String icon = "";
    @JsonProperty("iconposition")
    public String iconposition = "";
    @JsonProperty("iconsize")
    public String iconsize = "";
    @JsonProperty("interfacename")
    public String interfacename;
    @JsonProperty("jshook")
    public String jshook = "";
    @JsonProperty("labelalignment")
    public String labelalignment = "";
    @JsonProperty("layout")
    public String layout = "";
    @JsonProperty("lowerlimit")
    public String lowerlimit = "";
    @JsonProperty("maxwidth")
    public String maxwidth = "";
    @JsonProperty("menutype")
    public String menutype = "";
    @JsonProperty("menuitems")
    public ArrayList<SideBarMenuData> menuitems = new ArrayList<SideBarMenuData>();
    @JsonProperty("minwidth")
    public String minwidth = "";
    @JsonProperty("modifygauge")
    public String modifygauge = "";
    @JsonProperty("nativetable")
    public String nativetable = "";
    @JsonProperty("numberprefix")
    public String numberprefix = "";
    @JsonProperty("numbersuffix")
    public String numbersuffix = "";
    @JsonProperty("options")
    public String options = "";
    @JsonProperty("orientation")
    public String orientation = "";
    @JsonProperty("pagesize")
    public String pagesize = "999";
    @JsonProperty("paginationrequired")
    public String paginationrequired = "";
    @JsonProperty("paginationstyle")
    public String paginationstyle = "";
    @JsonProperty("parentlist")
    public String parentlist = "";
    @JsonProperty("parentnode")
    public String parentnode = "";
    @JsonProperty("properties")
    public String properties = "";
    @JsonProperty("responsive")
    public String responsive = "";
    @JsonIgnore
    public boolean referId = false;
    @JsonProperty("reapproval")
    public String reapproval = "";
    @JsonProperty("relatedcntr")
    public ArrayList<String> relatedcntr = new ArrayList<String>();
    @JsonProperty("rowsclickable")
    public String rowsclickable = "";
    @JsonProperty("rowselectorrequired")
    public String rowselectorrequired = "";
    @JsonProperty("rowselectortype")
    public String rowselectortype = "";
    @JsonProperty("searchable")
    public String searchable = "";
    @JsonProperty("sortable")
    public String sortable = "";
    @JsonProperty("sorthandle")
    public String sorthandle = "";
    @JsonProperty("staticoptions")
    public ArrayList<ElementStaticOptions> staticoptions = new ArrayList<ElementStaticOptions>();
    @JsonProperty("stepcounter")
    public String stepcounter = "";
    @JsonProperty("style")
    public String style = "";
    @JsonProperty("subcaption")
    public String subcaption = "";
    @JsonProperty("subtitle")
    public String subtitle = "";
    @JsonProperty("swipeactions")
    public String swipeactions = "";
    @JsonProperty("tableheight")
    public String tableheight = "";
    @JsonProperty("tablewidth")
    public String tablewidth = "";
    @JsonProperty("targetoverlayid")
    public String targetoverlayid = "";
    @JsonProperty("title")
    public String title = "";
    @JsonProperty("titlestyle")
    public String titlestyle = "";
    @JsonProperty("transparent")
    public String transparent = "";
    @JsonProperty("uidescription")
    public String uidescription = "";
    @JsonProperty("upperlimit")
    public String upperlimit = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonProperty("verticalalignment")
    public String verticalalignment = "";
    @JsonProperty("xelement")
    public String xelement = "";
    @JsonProperty("xfunction")
    public String xfunction = "";
    @JsonProperty("xnode")
    public String xnode = "";
    @JsonProperty("yelement")
    public String yelement = "";
    @JsonProperty("yfunction")
    public String yfunction = "";
    @JsonProperty("ynode")
    public String ynode = "";
    @JsonProperty("zelement")
    public String zelement = "";
    @JsonProperty("zfunction")
    public String zfunction = "";
    @JsonProperty("znode")
    public String znode = "";
    @JsonIgnore
    public boolean skip = false;
    @JsonIgnore
    public String[] containerNodeName;
    @JsonIgnore
    public String[] containerNodeType;
    @JsonIgnore
    public String nearMRec[];
    @JsonIgnore
    public ArrayList<ScrSectionRowData> sectionrows = new ArrayList<ScrSectionRowData>();
    @JsonIgnore
    public HashMap<String, ScrSectionRowData> sectionrows_map = new HashMap<String, ScrSectionRowData>();
    @JsonIgnore
    public ArrayList<ObjectNode> nodes = new ArrayList<ObjectNode>();
    @JsonIgnore
    public HashMap<String, ObjectNode> nodesmap = new HashMap<String, ObjectNode>();
    @JsonIgnore
    public List<ScrElementData> elmsData = new ArrayList<>();
    @JsonIgnore
    public HashMap<String, ScrElementData> elmsmap = new HashMap<String, ScrElementData>();
    @JsonIgnore
    public String multirec = "N"; // Temp.. To be Used Only in
    // generation and Should not be
    // Persisted in XML..Dynamic
    // Variables
    @JsonIgnore
    public String group = "";
    @JsonIgnore
    public int containerlevel = 0;

    public ScrContainerData() {
    }

    public ScrContainerData(String defaultName) {
        name = defaultName;
        type = "CONTAINER";
        typeClass = "CONTAINER";
    }

    // /////////////////////////////////////////////////////////////////
    // // Implemented Methods From Data Object Abstract Class
    @Override
    public boolean add() {
        boolean lres = true;
        ScrPanelSectionData lparent = (ScrPanelSectionData) parent;
        lparent.containers.add(this);
        lparent.containers_map.put(name, this);
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

    public void addEvent(EventData pevent) {
        events.add(pevent);
    }

    public void addMenu(SideBarMenuData pmenu) {
        menuitems.add(pmenu);
    }

    public void addYElement(ChartElementData pyelement) {
        chartyelements.add(pyelement);
    }

    public void addZElement(ChartElementData pzelement) {
        chartzelements.add(pzelement);
    }

    public void addRange(GaugeRange prangedata) {
        gaugeranges.add(prangedata);
    }

    public void addValue(GuageElementData pelmdata) {
        gaugevalues.add(pelmdata);
    }
}
