package com.iexceed.citi.products;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iexceed.citi.dataobj.*;

public class GenDesignDef {
    //////
    String productId = "", appId = "", scrId = "", scrType = "";
    // /For Containers
    private ObjectNode gcontainersobj = null;
    private ObjectNode ginitializationobj = null;
    private ArrayNode gmapsobj = null;
    private ArrayNode gcontainernames = null;
    private ArrayNode gcontainertypes = null;
    private ArrayNode gcontainercustom = null;
    private ArrayNode gcontainerchilds = null;
    private ArrayNode gcontainermrs = null;
    private ArrayNode gcontainerreadonly = null;
    private ArrayNode gcontainerpgstyles = null;
    private ArrayNode gcontainerpgsizes = null;
    private ArrayNode gcontainernoofifaces = null;
    private ArrayNode gcontainerifaces = null;
    private ArrayNode gcontainernoofnodes = null;
    private ArrayNode gcontainernodes = null;
    private ArrayNode gcontainernoofelms = null;
    private ArrayNode gcontainerisui = null;
    private ArrayNode gcontainerelms = null;
    private ArrayNode gcontainerelmtypes = null;
    private ArrayNode gcontainerelmreadonly = null;
    private ArrayNode gcontainerelmemail = null;
    private ArrayNode gcontainerelmtextdecoration = null;
    private ArrayNode gcontainerelmmaskformat = null;
    private ArrayNode gcontainerelmrtfmt = null;
    private ArrayNode gcontainerelmskipfmt = null;
    private ArrayNode gcontainerelmliteral = null;
    private ArrayNode gcontainerelmui = null;
    private ArrayNode gcontainerelmcustom = null;
    private ArrayNode gcontainerelmlov = null;
    private ArrayNode gcontainerelmlovwidth = null;
    private ArrayNode gcontainerelmlovminwidth = null;
    private ArrayNode gcontainerelmlovretfields = null;
    private ArrayNode gcontainerelmlovretfieldscount = null;
    private ArrayNode gcontainerelmlovbindvars = null;
    private ArrayNode gcontainerelmlovbindvarscount = null;
    private ArrayNode gcheckboxelmsarr = null;
    private ArrayNode gcontextmenuelmarr = null;
    private ArrayNode gpopoverelmarr = null;
    private ArrayNode gdateelmsarr = null;
    private ArrayNode gdropdownelmsarr = null;
    private ArrayNode gdropdownwithinputelmsarr = null;
    private ArrayNode gimgtxtelmsarr = null;
    private ArrayNode gtagelmsarr = null;
    private ArrayNode glistcntrsarr = null;
    private ArrayNode gtreelistcntrsarr = null;
    private ArrayNode gtablscntrsarr = null;
    // //For Charts
    private ArrayNode gchartsarr = null;
    // //For Gauges
    private ArrayNode ggaugesarr = null;
    //// For Groups
    private ObjectNode ggroupsobj = null;
    private ArrayNode ggroupnamesarr = null;
    private ArrayNode ggroupnoofnodesarr = null;
    private ArrayNode ggroupnodesarr = null;
    private ArrayNode ggroupnoofcontainersarr = null;
    private ArrayNode ggroupcontainersarr = null;
    //// For Screen body
    private ArrayNode gscreenprops = null;

    public void initForLO() {
        // //////////////For Containers
        gcontainersobj = JsonNodeFactory.instance.objectNode();
        ginitializationobj = JsonNodeFactory.instance.objectNode();
        gmapsobj = JsonNodeFactory.instance.arrayNode();
        gcontainernames = JsonNodeFactory.instance.arrayNode();
        gcontainertypes = JsonNodeFactory.instance.arrayNode();
        gcontainercustom = JsonNodeFactory.instance.arrayNode();
        gcontainerchilds = JsonNodeFactory.instance.arrayNode();
        gcontainermrs = JsonNodeFactory.instance.arrayNode();
        gcontainerreadonly = JsonNodeFactory.instance.arrayNode();
        gcontainerpgstyles = JsonNodeFactory.instance.arrayNode();
        gcontainerpgsizes = JsonNodeFactory.instance.arrayNode();
        gcontainernoofifaces = JsonNodeFactory.instance.arrayNode();
        gcontainerifaces = JsonNodeFactory.instance.arrayNode();
        gcontainernoofnodes = JsonNodeFactory.instance.arrayNode();
        gcontainernodes = JsonNodeFactory.instance.arrayNode();
        gcontainernoofelms = JsonNodeFactory.instance.arrayNode();
        gcontainerisui = JsonNodeFactory.instance.arrayNode();
        gcontainerelms = JsonNodeFactory.instance.arrayNode();
        gcontextmenuelmarr = JsonNodeFactory.instance.arrayNode();
        gpopoverelmarr = JsonNodeFactory.instance.arrayNode();
        gcontainerelmtypes = JsonNodeFactory.instance.arrayNode();
        gcontainerelmreadonly = JsonNodeFactory.instance.arrayNode();
        gcontainerelmemail = JsonNodeFactory.instance.arrayNode();
        gcontainerelmtextdecoration = JsonNodeFactory.instance.arrayNode();
        gcontainerelmmaskformat = JsonNodeFactory.instance.arrayNode();
        gcontainerelmrtfmt = JsonNodeFactory.instance.arrayNode();
        gcontainerelmskipfmt = JsonNodeFactory.instance.arrayNode();
        gcontainerelmliteral = JsonNodeFactory.instance.arrayNode();
        gcontainerelmui = JsonNodeFactory.instance.arrayNode();
        gcontainerelmcustom = JsonNodeFactory.instance.arrayNode();
        gcontainerelmlov = JsonNodeFactory.instance.arrayNode();
        gcontainerelmlovwidth = JsonNodeFactory.instance.arrayNode();
        gcontainerelmlovminwidth = JsonNodeFactory.instance.arrayNode();
        gcontainerelmlovretfields = JsonNodeFactory.instance.arrayNode();
        gcontainerelmlovretfieldscount = JsonNodeFactory.instance.arrayNode();
        gcontainerelmlovbindvars = JsonNodeFactory.instance.arrayNode();
        gcontainerelmlovbindvarscount = JsonNodeFactory.instance.arrayNode();
        gcheckboxelmsarr = JsonNodeFactory.instance.arrayNode();
        gdateelmsarr = JsonNodeFactory.instance.arrayNode();
        gdropdownelmsarr = JsonNodeFactory.instance.arrayNode();
        gdropdownwithinputelmsarr = JsonNodeFactory.instance.arrayNode();
        gimgtxtelmsarr = JsonNodeFactory.instance.arrayNode();
        gtagelmsarr = JsonNodeFactory.instance.arrayNode();
        glistcntrsarr = JsonNodeFactory.instance.arrayNode();
        gtreelistcntrsarr = JsonNodeFactory.instance.arrayNode();
        gtablscntrsarr = JsonNodeFactory.instance.arrayNode();
        // //////////////For Charts
        gchartsarr = JsonNodeFactory.instance.arrayNode();
        // //////////////For Gauges
        ggaugesarr = JsonNodeFactory.instance.arrayNode();
        // //////////////For Groups
        ggroupsobj = JsonNodeFactory.instance.objectNode();
        ggroupnamesarr = JsonNodeFactory.instance.arrayNode();
        ggroupnoofnodesarr = JsonNodeFactory.instance.arrayNode();
        ggroupnodesarr = JsonNodeFactory.instance.arrayNode();
        ggroupnoofcontainersarr = JsonNodeFactory.instance.arrayNode();
        ggroupcontainersarr = JsonNodeFactory.instance.arrayNode();
        // //////////////For Screen Props
        gscreenprops = JsonNodeFactory.instance.arrayNode();
        // //////////////For Child Screens
        //// For Child Screens
        ObjectNode gchildscreensobj = JsonNodeFactory.instance.objectNode();
        ArrayNode gchildsreennames = JsonNodeFactory.instance.arrayNode();
        ArrayNode gchildscreentypes = JsonNodeFactory.instance.arrayNode();
        ArrayNode gchildscreentargetids = JsonNodeFactory.instance.arrayNode();
    }

    public boolean addArrayToObject(ObjectNode pparent, String pname, ArrayNode parray) {
        boolean ladded = false;
        if (parray.size() > 0) {
            ladded = true;
            pparent.set(pname, parray);
        }
        return ladded;
    }

    public boolean addObjectToObject(ObjectNode parent, String name, ObjectNode obj) {
        boolean ladded = false;
        if (obj.size() > 0) {
            ladded = true;
            parent.set(name, obj);
        }
        return ladded;
    }

    public boolean addArrayToArray(ArrayNode pparent, ArrayNode parray) {
        boolean ladded = false;
        if (parray.size() > 0) {
            ladded = true;
            pparent.add(parray);
        }
        return ladded;
    }

    public ObjectNode gen(DesignData loTemplate, String productId, String appId, String scrId) {
        ObjectNode lloTempObj = JsonNodeFactory.instance.objectNode();
        try {
            initForLO();
            ArrayNode lifacesarray = JsonNodeFactory.instance.arrayNode();
            int lnoofcontainers = loTemplate.containers.size();
            this.appId = appId;
            this.scrId = scrId;
            scrType = "MAIN";
            // ///////////////////Layout Details//////////////////////
            Utils.addChildJSONElm(lloTempObj, "scr", scrId);
            Utils.addChildJSONElm(lloTempObj, "layout", "NewLayout");
            Utils.addChildJSONElm(lloTempObj, "id", loTemplate.name);
            Utils.addChildJSONElm(lloTempObj, "icon", loTemplate.iconpreference);
            Utils.addChildJSONElm(lloTempObj, "displayname", loTemplate.displayname);
            Utils.addChildJSONElm(lloTempObj, "screenType", scrType);
            addArrayToObject(lloTempObj, "defaultProducts", loTemplate.defaultProducts);
            // ///////////////////Interface Details//////////////////////
            lifacesarray.add(appId + "__" + productId + "_Intf");
            addArrayToObject(lloTempObj, "ifaces", lifacesarray);
            // ///////////////////Container Details//////////////////////
            for (int lc = 0; lc < lnoofcontainers; lc++) {
                ScrContainerData lcontainer = loTemplate.containers.get(lc);
                appendContainer(loTemplate, lcontainer);
            }
            ////////////////// Groups/////////////////////
            genGroups(loTemplate, appId, scrId);
            // //////////////Containers///////////////
            addArrayToObject(gcontainersobj, "name", gcontainernames);
            addArrayToObject(gcontainersobj, "type", gcontainertypes);
            addArrayToObject(gcontainersobj, "custom", gcontainercustom);
            addArrayToObject(gcontainersobj, "multiRec", gcontainermrs);
            addArrayToObject(gcontainersobj, "ro", gcontainerreadonly);
            addArrayToObject(gcontainersobj, "pgStyle", gcontainerpgstyles);
            addArrayToObject(gcontainersobj, "pgSize", gcontainerpgsizes);
            addArrayToObject(gcontainersobj, "childs", gcontainerchilds);
            addArrayToObject(gcontainersobj, "noOfIfaces", gcontainernoofifaces);
            addArrayToObject(gcontainersobj, "ifaces", gcontainerifaces);
            addArrayToObject(gcontainersobj, "noOfNodes", gcontainernoofnodes);
            addArrayToObject(gcontainersobj, "nodes", gcontainernodes);
            addArrayToObject(gcontainersobj, "noOfElms", gcontainernoofelms);
            addArrayToObject(gcontainersobj, "ui", gcontainerisui);
            addArrayToObject(gcontainersobj, "elms", gcontainerelms);
            addArrayToObject(gcontainersobj, "eUi", gcontainerelmui);
            addArrayToObject(gcontainersobj, "eCustom", gcontainerelmcustom);
            addArrayToObject(gcontainersobj, "elmType", gcontainerelmtypes);
            addArrayToObject(gcontainersobj, "eRo", gcontainerelmreadonly);
            addArrayToObject(gcontainersobj, "eEmail", gcontainerelmemail);
            addArrayToObject(gcontainersobj, "eTextdecoration", gcontainerelmtextdecoration);
            addArrayToObject(gcontainersobj, "eMask", gcontainerelmmaskformat);
            addArrayToObject(gcontainersobj, "eRtFmt", gcontainerelmrtfmt);
            addArrayToObject(gcontainersobj, "eSkipFmt", gcontainerelmskipfmt);
            addArrayToObject(gcontainersobj, "eLiteral", gcontainerelmliteral);
            addArrayToObject(gcontainersobj, "eLovId", gcontainerelmlov);
            addArrayToObject(gcontainersobj, "eLovWidth", gcontainerelmlovwidth);
            addArrayToObject(gcontainersobj, "eLovMinWidth", gcontainerelmlovminwidth);
            addArrayToObject(gcontainersobj, "eLovRf", gcontainerelmlovretfields);
            addArrayToObject(gcontainersobj, "noOfeLovRf", gcontainerelmlovretfieldscount);
            addArrayToObject(gcontainersobj, "eLovBv", gcontainerelmlovbindvars);
            addArrayToObject(gcontainersobj, "noOfeLovBv", gcontainerelmlovbindvarscount);
            lloTempObj.set("containers", gcontainersobj);
            // //////////////Charts///////////////////
            addArrayToObject(lloTempObj, "charts", gchartsarr);
            // //////////////Gauges///////////////////
            addArrayToObject(lloTempObj, "gauges", ggaugesarr);
            // //////////////Groups///////////////////
            addArrayToObject(ggroupsobj, "name", ggroupnamesarr);
            addArrayToObject(ggroupsobj, "noOfNodes", ggroupnoofnodesarr);
            addArrayToObject(ggroupsobj, "nodes", ggroupnodesarr);
            addArrayToObject(ggroupsobj, "noOfContainers", ggroupnoofcontainersarr);
            addArrayToObject(ggroupsobj, "containers", ggroupcontainersarr);
            addObjectToObject(lloTempObj, "groups", ggroupsobj);
            //////////////// Initializations///////////////
            addArrayToObject(ginitializationobj, "checkBox", gcheckboxelmsarr);
            addArrayToObject(ginitializationobj, "date", gdateelmsarr);
            addArrayToObject(ginitializationobj, "dropDown", gdropdownelmsarr);
            addArrayToObject(ginitializationobj, "imageText", gimgtxtelmsarr);
            addArrayToObject(ginitializationobj, "tag", gtagelmsarr);
            addArrayToObject(ginitializationobj, "list", glistcntrsarr);
            addArrayToObject(ginitializationobj, "treeList", gtreelistcntrsarr);
            addArrayToObject(ginitializationobj, "table", gtablscntrsarr);
            addArrayToObject(ginitializationobj, "contextMenu", gcontextmenuelmarr);
            addArrayToObject(ginitializationobj, "dropdownWithInput", gdropdownwithinputelmsarr);
            addArrayToObject(ginitializationobj, "popover", gpopoverelmarr);
            addObjectToObject(lloTempObj, "uiInits", ginitializationobj);
            ArrayNode scrIDArrayJs = JsonNodeFactory.instance.arrayNode();
            if (loTemplate.scripts != null) {
                scrIDArrayJs = loTemplate.scripts; // scripts js files now taken from to update to avoid 404 error
            }
            lloTempObj.set("scripts", scrIDArrayJs);
            addArrayToObject(lloTempObj, "maps", gmapsobj);
            if (loTemplate.body != null) {
                PortionData body = loTemplate.body;
                String cssClass = loTemplate.body.cssclasses;
                String bgImg = "";
                gscreenprops.add(cssClass);
                gscreenprops.add(body.bgcolor);
                gscreenprops.add(bgImg);
                gscreenprops.add(body.bgrepeat);
                gscreenprops.add(body.bgattachment);
                gscreenprops.add(body.bgposition);
                gscreenprops.add(body.bgsize);
                gscreenprops.add(body.pagevariation.trim());
                if (scrType.equals("MAIN")) {
                    gscreenprops.add(body.appearance + body.menuappearance);
                } else {
                    gscreenprops.add(body.menuappearance);
                }
                addArrayToObject(lloTempObj, "scrProps", gscreenprops);
            }
            //////////////////////
        } catch (Exception jsone) {
            jsone.printStackTrace();
        }
        return lloTempObj;
    }

    //////////////////////////////////////////////////////////
    public void appendContainer(DesignData templateDataObj, ScrContainerData pcontainer) {
        try {
            String lwidgettype = pcontainer.widgettype;
            // //Regular Containers
            if ((lwidgettype.equals("FORM")) || (lwidgettype.equals("TABLE")) || (lwidgettype.equals("LIST")) || (lwidgettype.equals("NAVBAR")) || (lwidgettype.equals("BREADCRUMB"))) {
                appendRegularContainer(templateDataObj, pcontainer);
            }
            // ///Charts
            else if (lwidgettype.equals("CHART")) {
                appendChart(templateDataObj, pcontainer, appId);
            }
            // ///Gauges
            else if (lwidgettype.equals("GAUGE")) {
                appendGauge(templateDataObj, pcontainer, appId);
            }
        } catch (Exception pex) {
            pex.printStackTrace();
        }
    }

    public void appendRegularContainer(DesignData templateDataObj, ScrContainerData pcontainer) {
        try {
            int lnoofdmlelms = 0;
            int lnoofelms = pcontainer.elms.size();
            int lpagesize = 999;
            String pagStyle = pcontainer.paginationstyle;
            String contreadonly = "N";
            String ifaceName = "";
            String cntrType = pcontainer.widgettype;
            if (!Utils.isNull(pcontainer.pagesize)) {
                String pageSize = pcontainer.pagesize;
                if (pageSize.trim().contains(" RECORDS")) {
                    pageSize = pageSize.substring(0, pageSize.indexOf(" "));
                }
                lpagesize = Integer.parseInt(pageSize);
            }
            if (pcontainer.dynamicpagesize.equals("Y")) {
                int noOfOptions = pcontainer.staticoptions.size();
                lpagesize = 999;
                String optValue = "";
                ElementStaticOptions optionObj = null;
                if (noOfOptions > 0) {
                    optionObj = pcontainer.staticoptions.get(0);
                    optValue = optionObj.value;
                    if (!Utils.isNull(optValue) && !"ALL".equals(optValue)) {
                        lpagesize = getPagesize(lpagesize, optValue);
                    }
                }
            }
            if (pcontainer.multirec.equals("N")) {
                lpagesize = 1;
                pagStyle = "PAGE1";
            }
            if (pcontainer.containerstate.equals("READONLY")) {
                contreadonly = "Y";
            }
            if (lnoofelms > 0) {
                String lmultireccontainer = "N";
                if ((cntrType.equals("LIST") || cntrType.equals("TABLE")) && pcontainer.multirec.equals("Y")) {
                    lmultireccontainer = "Y";
                }
                for (int le = 0; le < lnoofelms; le++) {
                    ScrElementData lelement = pcontainer.elms.get(le);
                    boolean ldmlelm = Utils.isDMlElement(lelement);
                    String lelmwidgettype = lelement.widgettype;
                    if (ldmlelm) {
                        lnoofdmlelms = lnoofdmlelms + 1;
                        gcontainerelmui.add("N");
                    } else {
                        gcontainerelmui.add("Y");
                    }
                    if ("Y".equals(lelement.custom)) {
                        lnoofdmlelms = lnoofdmlelms + 1;
                        gcontainerelmcustom.add("Y");
                    } else {
                        gcontainerelmcustom.add("N");
                    }
                    String lreadonly = "N";
                    String lemail = "N";
                    String textDecoration = "";
                    boolean apzNode = false;
                    String lnodeid = "";
                    String lelmid = "";
                    if (ldmlelm) {
                        String partialNodeName = lelement.nodename.replace(lelement.interfacename, "");
                        String name = "";
                        if (partialNodeName.equals("_Req") || partialNodeName.equals("_Res") || partialNodeName.equals("_Flt")) {
                            name = appId + "__" + lelement.nodename;
                            apzNode = true;
                        }

                        if (apzNode) {
                            lelmid = appId + "__" + lelement.interfacename + "__" + Utils.getDMLId(lelement.datamodeltype) + "__" + name + "__" + lelement.elementname;
                        } else {
                            lelmid = Utils.getElementId(lelement, appId, scrId, -1);
                        }

                    } else {
                        lelmid = Utils.getElementId(lelement, appId, scrId, -1);
                    }
                    gcontainerelms.add(lelmid);
                    gcontainerelmtypes.add(lelmwidgettype);
                    if (lelement.state.equals("DISABLED") || lelement.state.equals("READONLY")) {
                        lreadonly = "Y";
                    }
                    if (lelement.widgettype.equals("INPUTBOX")) {
                        if (lelement.email.equals("Y")) {
                            lemail = "Y";
                        }
                        if (!Utils.isNull(lelement.textdecoration)) {
                            textDecoration = lelement.textdecoration;
                        }
                        if (Utils.isNull(lelement.displayasliteral)) {
                            lelement.displayasliteral = "N";
                        }
                    }
                    gcontainerelmemail.add(lemail);
                    gcontainerelmtextdecoration.add(textDecoration);
                    gcontainerelmreadonly.add(lreadonly);
                    gcontainerelmmaskformat.add(lelement.maskformat);
                    gcontainerelmrtfmt.add(lelement.runtimeamountformat);
                    gcontainerelmskipfmt.add(lelement.donotformat);
                    gcontainerelmliteral.add(lelement.displayasliteral);
                    gcontainerelmlov.add(appId + "__" + lelement.lovname);
                    gcontainerelmlovwidth.add("lcol" + lelement.lovwidthclass);
                    gcontainerelmlovminwidth.add(lelement.lovminwidth + lelement.lovminwidthtype.toLowerCase());
                    appendLOVDetails(templateDataObj, lelement);
                    if (lelmwidgettype.equals("GAUGE")) {
                        appendGaugeElement(templateDataObj, lelement, pcontainer);
                    }
                    //// Modified to generate UI elements as well
                    boolean lmultirec = false;
                    if ("Y".equals(lmultireccontainer)) {
                        lmultirec = true;
                    }
                    genElementInitializations(lelement, lmultirec, templateDataObj, pcontainer);
                }
                if (lnoofdmlelms > 0) {
                    //// Add Nodes
                    int lnoofnodes = pcontainer.nodes.size();
                    int lnoofifaces = 0;
                    if (lnoofnodes > 0) {
                        gcontainernoofnodes.add(lnoofnodes);
                        for (int i = 0; i < lnoofnodes; i++, ++lnoofifaces) {
                            ObjectNode lnode = pcontainer.nodes.get(i);
                            boolean apzNode = false;
                            String lnodeid = "";
                            String name = "";
                            ifaceName = lnode.get("interfacename").asText();
                            String dmlType = lnode.get("datamodeltype").asText();
                            String nodename = lnode.get("nodename").asText();
                            String partialNodeName = nodename.replace(ifaceName, "");
                            if (partialNodeName.equals("_Req")) {
                                name = appId + "__" + nodename;
                                apzNode = true;
                            } else if (partialNodeName.equals("_Res")) {
                                name = appId + "__" + nodename;
                                apzNode = true;
                            } else if (partialNodeName.equals("_Flt")) {
                                name = appId + "__" + nodename;
                                apzNode = true;
                            }
                            if (apzNode) {
                                lnodeid = Utils.getNodeId(ifaceName, dmlType, name);
                            } else {
                                lnodeid = Utils.getNodeId(lnode);
                            }

                            gcontainernodes.add(appId + "__" + lnodeid);
                            gcontainerifaces.add(appId + "__" + ifaceName);
                        }
                    }
                    gcontainernoofifaces.add(1);
                    gcontainerisui.add("N");
                } else {
                    gcontainernoofifaces.add(0);
                    gcontainernoofnodes.add(0);
                    gcontainerisui.add("Y");
                }
                // //Add Container Props
                gcontainernoofelms.add(lnoofelms);
                String containerName = appId + "__" + scrId + "__" + pcontainer.name;
                gcontainernames.add(containerName);
                gcontainertypes.add(pcontainer.widgettype);
                gcontainercustom.add(pcontainer.custom);
                String lchilds = "";    /////// TBC - Required?
                gcontainerchilds.add(lchilds);
                gcontainermrs.add(lmultireccontainer);
                gcontainerreadonly.add(contreadonly);
                gcontainerpgstyles.add(pagStyle);
                gcontainerpgsizes.add(lpagesize);
            }
        } catch (Exception aex) {
            aex.printStackTrace();
            ;
        }
    }

    private int getPagesize(int lpagesize, String optValue) {
        try {
            lpagesize = Integer.parseInt(optValue);
        } catch (Exception e) {
        }
        return lpagesize;
    }

    public void appendChart(DesignData templateDataObj, ScrContainerData pcontainer, String appId) throws Exception {
        ///// To be added
    }

    public void appendGauge(DesignData templateDataObj, ScrContainerData pcontainer, String appId) throws Exception {
        ///// To be added
    }

    public void appendGaugeElement(DesignData templateDataObj, ScrElementData pelement, ScrContainerData pcontainer) throws Exception {
        ///// To be added
    }

    public void appendLOVDetails(DesignData templateDataObj, ScrElementData pelement) {
        try {
            int llovcount = pelement.lovs.size();
            if (llovcount > 0) {
                for (int m = 0; m < llovcount; m++) {
                    LOVSetData ldata = pelement.lovs.get(m);
                    for (int bv = 0; bv < ldata.bindingvariables.size(); bv++) {
                        BindVariables lbindingvariable = ldata.bindingvariables.get(bv);
                        if (!Utils.isNull(lbindingvariable.screenelement)) {
                            boolean dataObj = false;
                            ScrElementData screlmElementData = templateDataObj.elms_map.get(lbindingvariable.screenelement);
                            if (screlmElementData != null) {
                                dataObj = Utils.isDMlElement(screlmElementData);
                            }
                            if (dataObj) {
                                gcontainerelmlovbindvars.add(appId + "__" + lbindingvariable.screenelement);
                            } else {
                                gcontainerelmlovbindvars.add(appId + "__" + scrId + "__" + lbindingvariable.screenelement);
                            }
                        } else {
                            gcontainerelmlovbindvars.add("");
                        }
                    }
                    gcontainerelmlovbindvarscount.add(ldata.bindingvariables.size());
                    for (int rf = 0; rf < ldata.returnfields.size(); rf++) {
                        ReturnFields lreturnfields = ldata.returnfields.get(rf);
                        if (!Utils.isNull(lreturnfields.screlement)) {
                            boolean dmlObj = true;
                            ScrElementData screlmElementDataObj = templateDataObj.elms_map.get(lreturnfields.screlement);
                            if (screlmElementDataObj != null) {
                                dmlObj = Utils.isDMlElement(screlmElementDataObj);
                            }
                            if (dmlObj) {
                                gcontainerelmlovretfields.add(appId + "__" + lreturnfields.screlement);
                            } else {
                                gcontainerelmlovretfields.add(appId + "__" + scrId + "__" + lreturnfields.screlement);
                            }
                        } else {
                            gcontainerelmlovretfields.add("");
                        }
                    }
                    gcontainerelmlovretfieldscount.add(ldata.returnfields.size());
                }
            } else {
                gcontainerelmlovbindvarscount.add(0);
                gcontainerelmlovretfieldscount.add(0);
            }
        } catch (Exception pex) {
            pex.printStackTrace();
        }
    }

    public void genGroups(DesignData templateDataObj, String appId, String scrId) {
        // // Write Groups
        try {
            int lnoofgroups = templateDataObj.groups.size();
            for (int g = 0; g < lnoofgroups; g++) {
                LayoutGroup llayoutgroup = templateDataObj.groups.get(g);
                ggroupnamesarr.add(llayoutgroup.name);
                int lnoofgrpnodes = llayoutgroup.nodes.size();
                ggroupnoofnodesarr.add(lnoofgrpnodes);
                for (int gn = 0; gn < lnoofgrpnodes; gn++) {
                    ObjectNode lgroupnode = llayoutgroup.nodes.get(gn);
                    boolean apzNode = false;
                    String lnodeid = "";
                    String name = "";
                    String ifaceName = lgroupnode.get("interfacename").asText();
                    String dmlType = lgroupnode.get("datamodeltype").asText();
                    String nodename = lgroupnode.get("nodename").asText();
                    String partialNodeName = nodename.replace(ifaceName, "");
                    if (partialNodeName.equals("_Req")) {
                        name = appId + "__" + nodename;
                        apzNode = true;
                    } else if (partialNodeName.equals("_Res")) {
                        name = appId + "__" + nodename;
                        apzNode = true;
                    } else if (partialNodeName.equals("_Flt")) {
                        name = appId + "__" + nodename;
                        apzNode = true;
                    }
                    if (apzNode) {
                        lnodeid = Utils.getNodeId(ifaceName, dmlType, name);
                    } else {
                        lnodeid = Utils.getNodeId(lgroupnode);
                    }

                    lnodeid = appId + "__" + lnodeid;
                    ggroupnodesarr.add(lnodeid);
                }
                int lnoofgrpctnrs = llayoutgroup.containers.size();
                ggroupnoofcontainersarr.add(lnoofgrpctnrs);
                for (int gc = 0; gc < lnoofgrpctnrs; gc++) {
                    ScrContainerData lgroupctnr = llayoutgroup.containers.get(gc);
                    ggroupcontainersarr.add(appId + "__" + scrId + "__" + lgroupctnr.name);
                }
            }
        } catch (Exception pex) {
            pex.printStackTrace();
        }
    }

    public void genElementInitializations(ScrElementData pelement, boolean pmultirec, DesignData templateDataObj, ScrContainerData pcontainer) {
        String lwidgettype = pelement.widgettype;
        String lname = "";
        int lnoofstaticopts = pelement.staticoptions.size();
        ArrayNode lparamarray = JsonNodeFactory.instance.arrayNode();
        String ldatatype = pelement.datatype;
        lname = Utils.getElementId(pelement, appId, scrId, -1);
        String ldefaultval = pelement.defaultvalue;
        if (pmultirec) {
            lname = lname + "_0";
        }
        if (lwidgettype.equals("TAGS")) {
            lparamarray.add(lname);
            lparamarray.add(pelement.placeholder);
            addArrayToArray(gtagelmsarr, lparamarray);
        } else if ((lwidgettype.equals("INPUTBOX") || lwidgettype.equals("INPUTWITHBTN"))) {
            String lpreset = Utils.isNull(pelement.preset) ? "CALENDAR" : pelement.preset;
            String inputType = Utils.isNull(pelement.inputtype) ? "GENERIC" : pelement.inputtype;
            String display = Utils.isNull(pelement.display) ? "CENTER" : pelement.display;
            String style = Utils.isNull(pelement.style) ? "ANDROID-HOLO" : pelement.style;
            String secInpId = "";
            //String closeonselect = Utils.isNull(pelement.closeonselect) ? "N : pelement.closeonselect;
            //// Checking for DML element for Screen Element
            if ((ldatatype.equals("DATETIME") || ldatatype.equals("DATE"))) {
                lparamarray.add(lname);
                lparamarray.add(ldatatype);
                lparamarray.add(pelement.inputtype);
                lparamarray.add(display);
                lparamarray.add(style);
                lparamarray.add(lpreset);
                lparamarray.add(pelement.mindate);
                lparamarray.add(pelement.maxdate);
                lparamarray.add(pelement.closeonselect);
                lparamarray.add(pelement.multiselect);
                lparamarray.add(pelement.startyear);
                lparamarray.add(pelement.endyear);
                lparamarray.add(pelement.rangepicker);
                if (!Utils.isNull(pelement.secondinputid)) {
                    ScrElementData elmData = pcontainer.elmsmap.get(pelement.secondinputid);
                    if (elmData != null) {
                        secInpId = Utils.getElementId(elmData, appId, scrId, -1);
                    }
                }
                lparamarray.add(secInpId);
                lparamarray.add(pelement.multipleinput);
                lparamarray.add(pelement.datetype);
                addArrayToArray(gdateelmsarr, lparamarray);
            }
        } else if (lwidgettype.equals("DROPDOWN")) {
            String lgroup = "N";
            String drdnType = "SIMPLE";
            if (pelement.autocomplete.equals("N") && lnoofstaticopts > 0) {
                for (int s = 0; s < lnoofstaticopts; s++) {
                    ElementStaticOptions lstatopt = pelement.staticoptions.get(s);
                    if (!Utils.isNull(lstatopt.group)) {
                        if (lstatopt.group.equals("Y")) {
                            lgroup = "Y";
                            break;
                        }
                    }
                }
            }
            lparamarray.add(lname);
            lparamarray.add(pelement.autocomplete);
            lparamarray.add(pelement.style);
            lparamarray.add(pelement.display);
            lparamarray.add(pelement.closeonselect);
            lparamarray.add(pelement.multiselect);
            if (!Utils.isNull(pelement.dropdowntype)) {
                drdnType = pelement.dropdowntype;
            }
            lparamarray.add(drdnType);
            lparamarray.add(lgroup);
            lparamarray.add(pelement.singleselect);
            addArrayToArray(gdropdownelmsarr, lparamarray);
        }/* else if (lwidgettype.equals("DROPDOWNWITHINPUT)) {
			lparamarray.add(lname);
			addArrayToArray(gdropdownwithinputelmsarr, lparamarray);
		}*/ else if (lwidgettype.equals("IMAGEANDTEXT")) {
            lparamarray.add(lname);
            lparamarray.add(pelement.style);
            lparamarray.add(pelement.display);
            lparamarray.add(pelement.closeonselect);
            lparamarray.add(pelement.preset);
            addArrayToArray(gimgtxtelmsarr, lparamarray);
        } else if (lwidgettype.equals("CHECKBOX")) {
            if (lnoofstaticopts > 2) {
                boolean indeterminate = false;
                String lindeterminval = "";
                ElementStaticOptions optionobj = pelement.staticoptions.get(2);
                lindeterminval = optionobj.value;
                if (lindeterminval.equals(ldefaultval)) {
                    indeterminate = true;
                }
                if (indeterminate) {
                    lparamarray.add(lname);
                    lparamarray.add("INDETERMINATE");
                    addArrayToArray(gcheckboxelmsarr, lparamarray);
                }
            }
        }
        if (!pelement.menu.equals("")) {
            lparamarray = JsonNodeFactory.instance.arrayNode();
            lparamarray.add(lname);
            lparamarray.add(appId + "__" + scrId + "__" + pelement.menu);
            lparamarray.add(pmultirec);
            addArrayToArray(gcontextmenuelmarr, lparamarray);
        }
        if (!Utils.isNull(pelement.popoverid) && !pcontainer.widgettype.equals("BREADCRUMB")) {
            lparamarray = JsonNodeFactory.instance.arrayNode();
            lparamarray.add(lname);
            lparamarray.add(appId + "__" + scrId + "__" + pelement.popoverid);
            lparamarray.add(pmultirec);
            lparamarray.add(pelement.popoverposition);
            addArrayToArray(gpopoverelmarr, lparamarray);
        }
    }

}
