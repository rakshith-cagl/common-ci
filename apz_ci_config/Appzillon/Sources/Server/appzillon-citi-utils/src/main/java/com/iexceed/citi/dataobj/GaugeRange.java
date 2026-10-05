package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GaugeRange {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("min")
    public String min;
    @JsonProperty("max")
    public String max;
    @JsonProperty("color")
    public String color;

    public GaugeRange() {
    }

    public GaugeRange(String pminrange, String pmaxrange, String pcolor) {
        super();
        min = pminrange;
        max = pmaxrange;
        color = pcolor;
    }
}
