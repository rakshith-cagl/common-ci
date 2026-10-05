package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrElementData extends DataObject {
    private static final String PRESENTATIONELEMENT = "PRESENTATIONELEMENT";
    // ///////////////////////////// Mandatory Properties
    @JsonProperty("id")
    public String id = "";
    @JsonProperty("pid")
    public String pid = "";

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////// // Data Variables
    // ////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////
    @JsonProperty("widgetcategory")
    public String widgetcategory = "";
    @JsonProperty("widgettype")
    public String widgettype = "";
    // ///////////////////////////// Should be alphabetical order
    @JsonProperty("appearance")
    public String appearance = "";
    @JsonProperty("attributeRejectedValue")
    public ObjectNode attributeRejectedValue;
    @JsonProperty("autocomplete")
    public String autocomplete = "";
    @JsonProperty("autolov")
    public String autolov = "";
    @JsonProperty("biphier")
    public String biphier = "";
    public String boxshadow = "";
    @JsonProperty("breadcrumblabelposition")
    public String breadcrumblabelposition = "";
    @JsonProperty("breadcrumbtype")
    public String breadcrumbtype = "";
    @JsonProperty("browsefiletype")
    public String browsefiletype = "";
    @JsonProperty("buttonalignment")
    public String buttonalignment = "";
    @JsonProperty("buttonasicon")
    public String buttonasicon = "";
    @JsonProperty("buttonclickevent")
    public String buttonclickevent = "";
    @JsonProperty("buttonposition")
    public String buttonposition = "";
    @JsonProperty("buttontitle")
    public String buttontitle = "";
    @JsonProperty("buttonwidth")
    public String buttonwidth = "";
    @JsonProperty("chartbuilderfunction")
    public String chartbuilderfunction = "";
    @JsonProperty("checkboxitemstate")
    public String checkboxitemstate = "";
    @JsonProperty("checkboxitemtitle")
    public String checkboxitemtitle = "";
    @JsonProperty("checkboxitemvalue")
    public String checkboxitemvalue = "";
    @JsonProperty("checkboxtype")
    public String checkboxtype = "";
    @JsonProperty("closeonselect")
    public String closeonselect = "";
    @JsonProperty("color")
    public String color = "";
    @JsonProperty("columnalignment")
    public String columnalignment = "";
    @JsonProperty("combinedproductid")
    public String combinedproductid = "";
    @JsonProperty("contentalignment")
    public String contentalignment = "";
    @JsonProperty("controlid")
    public String controlid = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("custom")
    public String custom = "N";
    @JsonProperty("customheight")
    public String customheight = "";
    @JsonProperty("customheighttype")
    public String customheighttype = "";
    @JsonProperty("customwidth")
    public String customwidth = "";
    @JsonProperty("customwidthtype")
    public String customwidthtype = "";
    @JsonProperty("datamodeltype")
    public String datamodeltype = "";
    @JsonProperty("datatype")
    public String datatype = "";
    @JsonProperty("datetype")
    public String datetype = "";
    @JsonProperty("decorations")
    public ArrayList<Decoration> decorations = new ArrayList<Decoration>();
    @JsonProperty("defaultimage")
    public String defaultimage = "";
    @JsonProperty("defaultvalue")
    public String defaultvalue = "";
    @JsonProperty("desktophide")
    public String desktophide = "";
    @JsonProperty("desktopwidth")
    public String desktopwidth = "";
    @JsonProperty("display")
    public String display = "";
    @JsonProperty("displayasliteral")
    public String displayasliteral = "";
    @JsonProperty("displayname")
    public String displayname = "";
    @JsonProperty("displaystyle")
    public String displaystyle = "";
    @JsonProperty("donotformat")
    public String donotformat = "";
    @JsonProperty("dropdowntype")
    public String dropdowntype = "";
    @JsonProperty("effects")
    public String effects;
    @JsonProperty("elementname")
    public String elementname = "";
    @JsonProperty("externalid")
    public String externalid = "";
    @JsonProperty("elementtype")
    public String elementtype = "";
    @JsonProperty("email")
    public String email = "";
    @JsonProperty("endyear")
    public String endyear = "";
    @JsonProperty("events")
    public ArrayList<EventData> events = new ArrayList<EventData>();
    @JsonProperty("externalWidgetType")
    public String externalwidgettype = "";
    @JsonProperty("floatinglabel")
    public String floatinglabel = "";
    @JsonProperty("fontsize")
    public String fontsize = "";
    @JsonProperty("formula")
    public String formula = "";
    @JsonProperty("gaugeranges")
    public ArrayList<GaugeRange> gaugeranges = new ArrayList<GaugeRange>();
    @JsonProperty("gaugetype")
    public String gaugetype = "";
    @JsonProperty("headeralignment")
    public String headeralignment = "";
    @JsonProperty("hint")
    public String hint = "";
    @JsonProperty("icon")
    public String icon = "";
    @JsonProperty("iconappearance")
    public String iconappearance = "";
    @JsonProperty("iconevent")
    public String iconevent = "";
    @JsonProperty("iconposition")
    public String iconposition = "";
    @JsonProperty("iconsemantics")
    public String iconsemantics = "";
    @JsonProperty("iconsize")
    public String iconsize = "";
    @JsonProperty("inputicon")
    public String inputicon = "";
    @JsonProperty("inputtype")
    public String inputtype = "";
    @JsonProperty("interfacename")
    public String interfacename = "";
    @JsonProperty("labelalignment")
    public String labelalignment = "";
    @JsonProperty("labelicon")
    public String labelicon = "";
    @JsonProperty("labeliconappearance")
    public String labeliconappearance = "";
    @JsonProperty("labeliconclickable")
    public String labeliconclickable = "";
    @JsonProperty("labeliconfunction")
    public String labeliconfunction = "";
    @JsonProperty("labeliconposition")
    public String labeliconposition = "";
    @JsonProperty("labeliconsemantics")
    public String labeliconsemantics = "";
    @JsonProperty("labeliconsize")
    public String labeliconsize = "";
    @JsonProperty("labelmandatory")
    public String labelmandatory = "";
    @JsonProperty("labelposition")
    public String labelposition = "";
    @JsonProperty("labelrequired")
    public String labelrequired = "";
    @JsonProperty("labelwidth")
    public String labelwidth = "";
    @JsonProperty("lefticon")
    public String lefticon = "";
    @JsonProperty("listofvalues")
    public String listofvalues = "";
    @JsonProperty("lovappearance")
    public String lovappearance = "";
    @JsonProperty("lovminwidth")
    public String lovminwidth = "";
    @JsonProperty("lovminwidthtype")
    public String lovminwidthtype = "";
    @JsonProperty("lovname")
    public String lovname = "";
    @JsonProperty("lovs")
    public ArrayList<LOVSetData> lovs = new ArrayList<LOVSetData>();
    @JsonProperty("lovsemantics")
    public String lovsemantics = "";
    @JsonProperty("lovwidthclass")
    public String lovwidthclass = "";
    @JsonProperty("lowerlimit")
    public String lowerlimit = "";
    @JsonProperty("mandatory")
    public String mandatory = "";
    @JsonProperty("maskformat")
    public String maskformat = "";
    @JsonProperty("maskrequired")
    public String maskrequired = "";
    @JsonProperty("max")
    public String max = "";
    @JsonProperty("maxdate")
    public String maxdate = "";
    @JsonProperty("maxlength")
    public String maxlength = "";
    @JsonProperty("maxstringlength")
    public String maxstringlength = "";
    @JsonProperty("maxvalue")
    public String maxvalue = "";
    @JsonProperty("maxwidth")
    public String maxwidth = "";
    @JsonProperty("menu")
    public String menu = "";
    @JsonProperty("min")
    public String min = "";
    @JsonProperty("mindate")
    public String mindate = "";
    @JsonProperty("minstringlength")
    public String minstringlength = "";
    @JsonProperty("minvalue")
    public String minvalue = "";
    @JsonProperty("minwidth")
    public String minwidth = "";
    @JsonProperty("modifygauge")
    public String modifygauge = "";
    @JsonProperty("multipleinput")
    public String multipleinput = "";
    @JsonProperty("multiselect")
    public String multiselect = "";
    @JsonProperty("navscreen")
    public String navscreen = "";
    @JsonProperty("nodename")
    public String nodename = "";
    @JsonProperty("numberprefix")
    public String numberprefix = "";
    @JsonProperty("numbersuffix")
    public String numbersuffix = "";
    @JsonProperty("options")
    public String options = "N";
    @JsonProperty("orientation")
    public String orientation = "";
    @JsonProperty("pattern")
    public String pattern = "";
    @JsonProperty("password")
    public String password = "";
    @JsonProperty("phonehide")
    public String phonehide = "";
    @JsonProperty("phonewidth")
    public String phonewidth = "";
    @JsonProperty("placeholder")
    public String placeholder = "";
    @JsonProperty("popoverid")
    public String popoverid = "";
    @JsonProperty("popoverposition")
    public String popoverposition = "";
    @JsonProperty("preset")
    public String preset = "";
    @JsonProperty("progresstype")
    public String progresstype = "";
    @JsonProperty("properties")
    public String properties = "";
    @JsonProperty("reapproval")
    public String reapproval = "";
    @JsonProperty("radiotype")
    public String radiotype = "";
    @JsonProperty("rangepicker")
    public String rangepicker = "";
    @JsonProperty("rangetype")
    public String rangetype = "";
    @JsonProperty("relatedelms")
    public ArrayList<String> relatedelms = new ArrayList<String>();
    @JsonProperty("relatedcontainer")
    public ArrayList<String> relatedcontainer = new ArrayList<String>();
    @JsonProperty("righticon")
    public String righticon = "";
    @JsonProperty("basicrulesetpresent")
    public String basicrulesetpresent = "";
    @JsonProperty("runtimeamountformat")
    public String runtimeamountformat = "";
    @JsonProperty("secondinputid")
    public String secondinputid = "";
    @JsonProperty("semantics")
    public String semantics = "";
    @JsonProperty("singleselect")
    public String singleselect = "";
    @JsonProperty("size")
    public String size = "";
    @JsonProperty("slidertype")
    public String slidertype = "";
    @JsonProperty("source")
    public String source = "";
    @JsonProperty("spinner")
    public String spinner = "";
    @JsonProperty("spinnertext")
    public String spinnertext = "";
    @JsonProperty("spinnertime")
    public String spinnertime = "";
    @JsonProperty("startyear")
    public String startyear = "";
    @JsonProperty("state")
    public String state = "";
    @JsonProperty("staticoptions")
    public ArrayList<ElementStaticOptions> staticoptions = new ArrayList<ElementStaticOptions>();
    @JsonProperty("step")
    public String step = "";
    @JsonProperty("stepcounter")
    public String stepcounter = "";
    @JsonProperty("style")
    public String style = "";
    @JsonProperty("switchoption1")
    public String switchoption1 = "";
    @JsonProperty("switchoption2")
    public String switchoption2 = "";
    @JsonProperty("symbol")
    public String symbol = "";
    @JsonProperty("symbolvalue")
    public String symbolvalue = "";
    @JsonProperty("tablecellwidth")
    public String tablecellwidth = "";
    @JsonProperty("tablecellwidthtype")
    public String tablecellwidthtype = "";
    @JsonProperty("tablethide")
    public String tablethide = "";
    @JsonProperty("tabletwidth")
    public String tabletwidth = "";
    @JsonProperty("targetelement")
    public String targetelement = "";
    @JsonProperty("targethtmlid")
    public String targethtmlid = "";
    @JsonProperty("targetnode")
    public String targetnode = "";
    @JsonProperty("textdecoration")
    public String textdecoration = "";
    @JsonProperty("title")
    public String title = "";
    @JsonProperty("toggleswitchtype")
    public String toggleswitchtype = "";
    @JsonProperty("tooltip")
    public String tooltip = "";
    @JsonProperty("translatedefaultvalue")
    public String translatedefaultvalue = "";
    @JsonProperty("uidescription")
    public String uidescription = "";
    @JsonProperty("uploadbuttonrequired")
    public String uploadbuttonrequired = "";
    @JsonProperty("upperlimit")
    public String upperlimit = "";
    @JsonProperty("url")
    public String url = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonProperty("widgetsource")
    public String widgetsource = "";
    @JsonProperty("width")
    public String width = "";
    @JsonProperty("wshide")
    public String wshide = "";
    @JsonProperty("wswidth")
    public String wswidth = "";
    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////// // Derived Variables
    // ////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////
    @JsonIgnore
    public boolean referId = false;
    @JsonIgnore
    public String mapdata = "";
    //	@JsonProperty("rootId")
//	public String rootId = "";
//	@JsonProperty("headerId")
//	public String headerId = "";
//	@JsonProperty("parentId")
//	public String parentId = "";
//	@JsonProperty("portItemId")
//	public String portItemId = "";
//	@JsonProperty("prodItemId")
//	public String prodItemId = "";
    @JsonIgnore
    public ArrayList<ScrElementData> elements = new ArrayList<ScrElementData>();
    @JsonIgnore
    public HashMap<String, ScrElementData> elements_map = new HashMap<String, ScrElementData>();

    public ScrElementData() {
        // Default constructor
    }

    public ScrElementData(String defaultName) {
        name = defaultName;
        type = PRESENTATIONELEMENT;
        typeClass = PRESENTATIONELEMENT;
    }

    @Override
    public boolean add() {
        boolean lres = true;

        if (parent.type.equals(PRESENTATIONELEMENT)) {
            ScrElementData lparent = (ScrElementData) parent;
            lparent.elements.add(this);
            lparent.elements_map.put(name, this);
        } else if (parent.type.equals("SECTIONCOLUMN")) {
            ScrSectionColumnData lparent = (ScrSectionColumnData) parent;
            lparent.elements.add(this);
            lparent.elements_map.put(name, this);
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

    public void addStaticOptions(ElementStaticOptions pstaticoptions) {
        staticoptions.add(pstaticoptions);
    }

    public void addEvent(EventData pevent) {
        events.add(pevent);
    }

    public void addLOVSet(LOVSetData plov) {
        lovs.add(plov);
    }

    public void addRange(GaugeRange prangedata) {
        gaugeranges.add(prangedata);
    }

}
