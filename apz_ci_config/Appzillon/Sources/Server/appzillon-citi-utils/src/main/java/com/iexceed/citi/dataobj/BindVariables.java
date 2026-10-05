package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BindVariables {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("screenelement")
    public String screenelement;
    @JsonProperty("datatype")
    public String datatype;

    public BindVariables() {
        // Default constructor
    }
}
