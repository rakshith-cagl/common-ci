package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuageElementData {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("node")
    public String node;
    @JsonProperty("element")
    public String element;
    @JsonProperty("tooltext")
    public String tooltext;

    public GuageElementData() {
    }

    public GuageElementData(String pnode, String pelement, String ptooltext) {
        super();
        node = pnode;
        element = pelement;
        tooltext = ptooltext;
    }
}
