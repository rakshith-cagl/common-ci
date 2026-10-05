package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;

public class LayoutGroup {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("name")
    public String name = "";
    @JsonProperty("containers")
    public ArrayList<ScrContainerData> containers = new ArrayList<ScrContainerData>();
    @JsonProperty("nodes")
    public ArrayList<ObjectNode> nodes = new ArrayList<ObjectNode>();
    @JsonProperty("nodesmap")
    public HashMap<String, ObjectNode> nodesmap = new HashMap<String, ObjectNode>();

    public LayoutGroup() {
        // Default constructor
    }
}
