package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SideBarMenuData {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("menuname")
    public String menuname;
    @JsonProperty("menuicon")
    public String menuicon;
    @JsonProperty("parentname")
    public String parentname;
    @JsonProperty("selected")
    public String selected;

    public SideBarMenuData() {
    }

    public SideBarMenuData(String pmenuname, String pmenuicon, String pparentname, String pselected) {
        super();
        menuname = pmenuname;
        menuicon = pmenuicon;
        parentname = pparentname;
        selected = pselected;
    }
}
