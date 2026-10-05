package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class PortionData extends DataObject {

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////// // Data Variables //
    // ////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////
    @JsonProperty("id")
    public String id = "body";
    @JsonProperty("pid")
    public String pid = "pagecontainer";
    @JsonProperty("widgetcategory")
    public String widgetcategory = "PORTION";
    @JsonProperty("widgettype")
    public String widgettype = "body";

    // /////////////////Should be in alphabetical
    // order///////////////////////////
    @JsonProperty("alignment")
    public String alignment = "";
    @JsonProperty("appearance")
    public String appearance = "";
    @JsonProperty("attatchment")
    public String attatchment = "";
    @JsonProperty("behaviour")
    public String behaviour = "";
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
    @JsonProperty("canceltitle")
    public String canceltitle = "";
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("customwidth")
    public String customwidth = "";
    @JsonProperty("customwidthtype")
    public String customwidthtype = "";
    @JsonProperty("dialogfooter")
    public String dialogfooter = "";
    @JsonProperty("draggablemodal")
    public String draggablemodal = "";

    @JsonProperty("events")
    public ArrayList<EventData> events = new ArrayList<EventData>();
    @JsonProperty("footertype")
    public String footertype = "";
    @JsonProperty("location")
    public String location = "";

    @JsonProperty("margin")
    public String margin = "";
    @JsonProperty("modalfooter")
    public String modalfooter = "";

    @JsonProperty("oktitle")
    public String oktitle = "";
    @JsonProperty("options")
    public String options = "";
    @JsonProperty("pagevariation")
    public String pagevariation = "";
    @JsonProperty("state")
    public String state = "";
    @JsonProperty("variation")
    public String variation = "";
    @JsonProperty("width")
    public String width = "";
    @JsonIgnore
    public String menuappearance = "";
    @JsonIgnore
    public String menuiconposition = "";
    @JsonIgnore
    public ArrayList<ScrGridRowData> gridrows = new ArrayList<ScrGridRowData>();
    @JsonIgnore
    public HashMap<String, ScrGridRowData> gridrows_map = new HashMap<String, ScrGridRowData>();
    @JsonIgnore
    public ArrayList<ScrPopupData> popup = new ArrayList<ScrPopupData>();
    @JsonIgnore
    public HashMap<String, ScrPopupData> popup_map = new HashMap<String, ScrPopupData>();

    @JsonIgnore
    public PortionData modal;
    @JsonIgnore
    public PortionData dialog;
    @JsonIgnore
    public PortionData contextmenu;

    // // Implemented Methods From Data Object Abstract Class
    @Override
    public boolean add() {
        boolean lresult = true;
        return lresult;
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

    public boolean save() {
        boolean lres = true;
        return lres;
    }

    public boolean validate() {
        boolean lres = true;
        return lres;
    }


}
