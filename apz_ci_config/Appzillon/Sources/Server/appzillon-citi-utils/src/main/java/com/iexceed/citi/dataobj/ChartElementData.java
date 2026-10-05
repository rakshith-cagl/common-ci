package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChartElementData {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("title")
    public String title;
    @JsonProperty("node")
    public String node;
    @JsonProperty("element")
    public String element;

    public ChartElementData() {
    }

    public ChartElementData(String ptitle, String pnode, String pelement) {
        super();
        title = ptitle;
        node = pnode;
        element = pelement;
    }
}
