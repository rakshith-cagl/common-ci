package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignData extends DataObject {

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
    // ////////////////////////Should be in alphabetical
    // order///////////////////
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
    @JsonProperty("content")
    public StringBuilder content = new StringBuilder();
    @JsonProperty("cssclasses")
    public String cssclasses = "";
    @JsonProperty("cssfiles_list")
    public List<String> cssfilesList = new ArrayList<>();
    @JsonProperty("defaultProducts")
    public ArrayNode defaultProducts = JsonNodeFactory.instance.arrayNode();
    @JsonProperty("description")
    public String description = "";
    @JsonProperty("displayname")
    public String displayname = "";
    @JsonProperty("iconpreference")
    public String iconpreference = "";
    @JsonProperty("jsfiles_list")
    public List<String> jsfilesList = new ArrayList<>();
    @JsonProperty("layoutvariation")
    public String layoutvariation = "";
    @JsonProperty("pagevariation")
    public String pagevariation = "";
    @JsonProperty("source")
    public String source = "APPZILLON";
    @JsonProperty("title")
    public String title = "";

    ////// Citi Details
    @JsonProperty("parentId")
    public String parentId = "";
    @JsonProperty("rootId")
    public String rootId = "";
    @JsonProperty("headerId")
    public String headerId = "";
    @JsonProperty("prodName")
    public String prodName = "";
    @JsonProperty("prodId")
    public String prodId = "";
    @JsonProperty("prodItemId")
    public String prodItemId = "";
    @JsonProperty("portItemId")
    public String portItemId = "";
    @JsonProperty("subProducts")
    public ArrayNode subProducts = JsonNodeFactory.instance.arrayNode();
    @JsonProperty("combinedParentId")
    public String combinedParentId = "";
    @JsonProperty("decorations")
    public ArrayNode decorations;
    @JsonProperty("scripts")
    public ArrayNode scripts;

    @JsonIgnore
    public String appId = "";
    @JsonIgnore
    public String screenId = "";
    @JsonIgnore
    public PortionData body;
    @JsonIgnore
    public PortionData footer;
    @JsonIgnore
    public PortionData header;
    @JsonIgnore
    public PortionData sidebar;
    @JsonIgnore
    public List<ScrContainerData> containers = new ArrayList<>();
    @JsonIgnore
    public Map<String, ScrContainerData> containersMap = new HashMap<>();
    @JsonIgnore
    public List<ScrPanelSectionData> panelSections = new ArrayList<>();
    @JsonIgnore
    public Map<String, ScrPanelSectionData> panelSectionsMap = new HashMap<>();
    @JsonIgnore
    public List<LayoutGroup> groups = new ArrayList<>();
    @JsonIgnore
    public Map<String, LayoutGroup> groupsmap = new HashMap<>();
    @JsonIgnore
    public List<String> customcontainers = new ArrayList<>();
    @JsonIgnore
    public List<String> customelemtns = new ArrayList<>();
    @JsonIgnore
    public List<String> overlays = new ArrayList<>();


    // // Implemented Methods From Data Object Abstract Class
    @Override
    public boolean add() {
        return true;
    }

    public boolean rename() {
        return true;
    }

    public boolean refactorRename() {
        return true;
    }

    public boolean del() {
        return true;
    }

    public boolean load() {
        return true;
    }

    public boolean save() {
        return true;
    }

    public boolean validate() {
        return true;
    }

    // //Add JS files
    public void addJSFiles(String pname) {
        jsfilesList.add(pname);
    }

    // //Add CSS Files
    public void addCSSFiles(String pname) {
        cssfilesList.add(pname);
    }

}
