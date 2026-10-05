package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReturnFields {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("queryfield")
    public String queryfield;
    @JsonProperty("screlement")
    public String screlement;
    @JsonProperty("title")
    public String title;
    @JsonProperty("datatype")
    public String datatype;
    @JsonProperty("filterreqd")
    public String filterreqd;
    @JsonProperty("resultreqd")
    public String resultreqd;

    public ReturnFields() {
        // Default constructor
    }
}
