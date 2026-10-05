package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class LOVSetData {
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////// // Data Variables // ////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    @JsonProperty("name")
    public String name;
    @JsonProperty("query")
    public String query;
    @JsonProperty("dbconnectionname")
    public String dbconnectionname;
    @JsonProperty("defaultlov")
    public String defaultlov;
    @JsonProperty("lovminwidth")
    public String lovminwidth;
    @JsonProperty("lovminwidthtype")
    public String lovminwidthtype;
    @JsonProperty("lovwidthclass")
    public String lovwidthclass;
    @JsonProperty("returnfields")
    public ArrayList<ReturnFields> returnfields = new ArrayList<ReturnFields>();
    @JsonProperty("bindingvariables")
    public ArrayList<BindVariables> bindingvariables = new ArrayList<BindVariables>();

    public LOVSetData() {
    }

    public LOVSetData(String plovname, String plovquery, String pdefaultlov, String plovminwidth, String plovminwidthtype, String plovwidthclass, String plovdbconnname,
                      ArrayList<ReturnFields> plovreturnfields, ArrayList<BindVariables> pbindingvariables) {
        super();
        name = plovname;
        query = plovquery;
        defaultlov = pdefaultlov;
        lovminwidth = plovminwidth;
        lovminwidthtype = plovminwidthtype;
        lovwidthclass = plovwidthclass;
        dbconnectionname = plovdbconnname;
        returnfields = plovreturnfields;
        bindingvariables = pbindingvariables;
    }

    public void addReturnSet(ReturnFields preturnfld) {
        returnfields.add(preturnfld);
    }

    public void addBindingVariables(BindVariables bindingvars) {
        bindingvariables.add(bindingvars);
    }
}
