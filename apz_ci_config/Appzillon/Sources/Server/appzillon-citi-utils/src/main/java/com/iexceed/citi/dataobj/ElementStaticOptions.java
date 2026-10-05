package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElementStaticOptions {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("description")
    public String description = "";
    @JsonProperty("group")
    public String group; // Added for Mobiscroll Dropdown - Darshan
    @JsonProperty("state")
    public String state = "";
    @JsonProperty("value")
    public String value = "";
    @JsonProperty("hint")
    public String hint = "";

    public ElementStaticOptions() {
    }

    public ElementStaticOptions(String pval, String pdesc, String pstate, String pgroup, String phint) {
        super();
        description = pdesc;
        state = pstate;
        group = pgroup;
        value = pval;
        hint = phint;
    }

}
