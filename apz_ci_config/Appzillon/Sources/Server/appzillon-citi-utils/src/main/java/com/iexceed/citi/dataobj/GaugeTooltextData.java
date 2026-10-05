package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GaugeTooltextData {
    @JsonProperty("node")
    public String node;
    @JsonProperty("element")
    public String element;
//////////////////////////////////////////////////////////////////////////////
//////////////////////////// // Data Variables // ////////////////////////////
//////////////////////////////////////////////////////////////////////////////

    public GaugeTooltextData() {
    }

    public GaugeTooltextData(String pnode, String pelement) {
        super();
        node = pnode;
        element = pelement;
    }
}
