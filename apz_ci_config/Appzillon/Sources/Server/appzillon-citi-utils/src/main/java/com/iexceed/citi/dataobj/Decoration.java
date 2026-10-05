package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Decoration {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("name")
    public String name;
    @JsonProperty("value")
    public String value;

    public Decoration() {
    }

    public Decoration(String pevent, String pvalue) {
        super();
        name = pevent;
        value = pvalue;
    }
}
