package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventData {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("name")
    public String name;
    @JsonProperty("function")
    public String function;

    public EventData() {
    }

    public EventData(String pevent, String pfunc) {
        super();
        name = pevent;
        function = pfunc;
    }
}
