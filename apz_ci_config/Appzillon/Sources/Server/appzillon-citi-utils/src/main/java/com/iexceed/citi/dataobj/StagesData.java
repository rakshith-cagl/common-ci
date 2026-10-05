package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StagesData {
    @JsonProperty("action")
    public String action;
    @JsonProperty("confirm")
    public String confirm;

    // ////////////////////////////////////////////////////////////////////////////
    // ////////////////////////// // Data Variables //
    // ////////////////////////////
    // ////////////////////////////////////////////////////////////////////////////
    @JsonProperty("displaytext")
    public String displaytext;
    @JsonProperty("percent")
    public String percent;
    @JsonProperty("undo")
    public String undo;

    public StagesData() {
    }

    public StagesData(String ppercent, String pdisplaytext, String paction,
                      String pundo, String pconfirm) {
        super();
        percent = ppercent;
        displaytext = pdisplaytext;
        action = paction;
        undo = pundo;
        confirm = pconfirm;
    }
}
