package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iexceed.appzillon.domain.service.Parser;
import com.iexceed.appzillon.domain.service.RenderAppzillonDefinitionJson;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.citi.utils.DesignUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.iexceed.utils.Constants.*;

public class Utils {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_PREFIX_CITI, Utils.class.toString());

    public static boolean isNull(String pstr) {
        boolean lres = false;
        if ((pstr == null) || (pstr.equals(""))) {
            lres = true;
        }
        return lres;
    }

    public static String getNodeId(ObjectNode nodeObj) {
        return Utils.getNodeId(nodeObj.get("interfacename").asText(), nodeObj.get("datamodeltype").asText(), nodeObj.get("nodename").asText());
    }

    public static String getNodeId(String pifacename, String pdmltype, String pnode) {
        String lid = null;
        if ((!isNull(pifacename)) && (!isNull(pdmltype)) && (!isNull(pnode))) {
            lid = pifacename + "__" + getDMLId(pdmltype) + "__" + getIfaceNodeName(pifacename, pnode);
        }
        return lid;
    }

    public static String getDMLId(String pdmltype) {
        String lid = null;
        if (pdmltype.equals("REQUESTDATAMODEL")) {
            lid = "i";
        } else if (pdmltype.equals("RESPONSEDATAMODEL")) {
            lid = "o";
        } else if (pdmltype.equals("FAULTDATAMODEL")) {
            lid = "f";
        }
        return lid;
    }

    public static String getIfaceNodeName(String pifacename, String pname) {
        String lname = pname;
        if (pname.equals("AppzillonRequest")) {
            lname = pifacename + "_Req";
        } else if (pname.equals("AppzillonResponse")) {
            lname = pifacename + "_Res";
        } else if (pname.equals("AppzillonFault")) {
            lname = pifacename + "_Flt";
        }
        return lname;
    }

    public static String getElmId(ScrElementData elmObj) {
        String lid = getElmId(elmObj.interfacename, elmObj.datamodeltype, elmObj.nodename, elmObj.elementname);
        if (lid == null) {
            lid = elmObj.name;
        }
        return lid;
    }

    public static String getElmId(String pifacename, String pdmltype, String pnode, String pelm) {
        String lid = null;
        if ((!Utils.isNull(pifacename)) && (!Utils.isNull(pdmltype)) && (!Utils.isNull(pnode)) && (!Utils.isNull(pelm))) {
            lid = pifacename + "__" + Utils.getDMLId(pdmltype) + "__" + Utils.getIfaceNodeName(pifacename, pnode) + "__" + pelm;
        }
        return lid;
    }

    public static void sortNodes(ArrayList<ObjectNode> pnodes) {
        int lnoofnodes = pnodes.size();
        ArrayList<ObjectNode> lbackup = new ArrayList<ObjectNode>();
        int lmaxlevel = 0;
        if (lnoofnodes > 0) {
            ////Populate Level Maps
            for (int i = 0; i < lnoofnodes; i++) {
                ObjectNode lnodedata = pnodes.get(i);
                int llevel = lnodedata.get("nodelevel").asInt();
                if (llevel > lmaxlevel) {
                    lmaxlevel = llevel;
                }
                lbackup.add(lnodedata);
            }
            ////ln("Max Level :" + lmaxlevel);
            ////Consolidate them back..
            pnodes.clear();
            for (int l = 0; l <= lmaxlevel; l++) {
                for (int i = 0; i < lnoofnodes; i++) {
                    ObjectNode lnodedata = lbackup.get(i);
                    if (lnodedata.get("nodelevel").asInt() == l) {
                        pnodes.add(lnodedata);
                    }
                }
            }
        }
    }

    // /// Sort Containers
    public static void sortContainers(ArrayList<ScrContainerData> pcontainers) {
        ArrayList<ScrContainerData> lbackup = new ArrayList<ScrContainerData>();
        int lmaxlevel = 0;
        int lnoofcontainrs = pcontainers.size();
        for (int c = 0; c < lnoofcontainrs; c++) {
            ScrContainerData lctnrdataobj = pcontainers.get(c);
            lbackup.add(lctnrdataobj);
            int llevel = lctnrdataobj.containerlevel;
            if (llevel > lmaxlevel) {
                lmaxlevel = llevel;
            }
        }
        pcontainers.clear();
        for (int l = 0; l <= lmaxlevel; l++) {
            for (int i = 0; i < lnoofcontainrs; i++) {
                ScrContainerData lctnrdataobj = lbackup.get(i);
                if (lctnrdataobj.containerlevel == l) {
                    pcontainers.add(lctnrdataobj);
                }
            }
        }
    }

    // // populate Groups
    public static void populateGroups(DesignData templateDataobj, String scrId) {
        HashMap<String, ObjectNode> ltrackpeersmap = new HashMap<String, ObjectNode>();
        ArrayList<ObjectNode> llonodes = getLONodes(templateDataobj);
        int lgroupcounter = 0;
        templateDataobj.groups.clear();
        // // Add Group
        int lnoofnodes = llonodes.size();
        LayoutGroup lgroupobj = null;
        if (lnoofnodes > 0) {
            for (int nd = 0; nd < lnoofnodes; nd++) {
                ObjectNode lnodedataobj = llonodes.get(nd);
                String lnodeid = Utils.getNodeId(lnodedataobj);
                if (!ltrackpeersmap.containsKey(lnodeid)) {
                    lgroupcounter = lgroupcounter + 1;
                    lgroupobj = new LayoutGroup();
                    lgroupobj.name = "GRP_" + scrId + "_" + lgroupcounter;
                    populateGroupNodesandContainers(templateDataobj, lgroupobj, lnodedataobj, ltrackpeersmap);
                    if (lgroupobj != null) {
                        templateDataobj.groups.add(lgroupobj);
                    }
                }
            }
        }
        // //Sort Group Nodes and Containers
        Utils.sortGroupNodesAndContainers(templateDataobj);
    }

    public static void populateGroupNodesandContainers(DesignData templateData, LayoutGroup pgroup, ObjectNode pnode, HashMap<String, ObjectNode> ptrackpeersmap) {
        int lnooflocontainers = templateData.containers.size();
        String lnodeid = Utils.getNodeId(pnode);
        for (int c = 0; c < lnooflocontainers; c++) {
            ScrContainerData lctnrdata = templateData.containers.get(c);
            int lnoofcontainernodes = lctnrdata.nodes.size();
            for (int n = 0; n < lnoofcontainernodes; n++) {
                if (lctnrdata.nodesmap.containsKey(lnodeid)) {
                    // // Add Containers to Group
                    if (!pgroup.containers.contains(lctnrdata)) {
                        lctnrdata.group = pgroup.name;
                        pgroup.containers.add(lctnrdata);
                        int lnoofctnrnodes = lctnrdata.nodes.size();
                        for (int cn = 0; cn < lnoofctnrnodes; cn++) {
                            ObjectNode lnodedataobj = lctnrdata.nodes.get(cn);
                            String lcntrnodeid = Utils.getNodeId(lnodedataobj);
                            if (!pgroup.nodesmap.containsKey(lcntrnodeid)) {
                                // // Add Nodes to Group
                                pgroup.nodes.add(lnodedataobj);
                                pgroup.nodesmap.put(lcntrnodeid, lnodedataobj);
                                ptrackpeersmap.put(lcntrnodeid, lnodedataobj);
                                populateGroupNodesandContainers(templateData, pgroup, lnodedataobj, ptrackpeersmap);
                            }
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<ObjectNode> getLONodes(DesignData tempData) {
        HashMap<String, String> lnodeset = new HashMap<String, String>();
        ArrayList<ObjectNode> llonodes = new ArrayList<ObjectNode>();
        int lnoofcontainers = tempData.containers.size();
        if (lnoofcontainers > 0) {
            for (int c = 0; c < lnoofcontainers; c++) {
                ScrContainerData lcntrdata = tempData.containers.get(c);
                int lnoofcntrnodes = lcntrdata.nodes.size();
                for (int n = 0; n < lnoofcntrnodes; n++) {
                    ObjectNode lnodedataobj = lcntrdata.nodes.get(n);
                    String lnodeid = Utils.getNodeId(lnodedataobj.get("interfacename").asText(), lnodedataobj.get("datamodeltype").asText(), lnodedataobj.get("nodename").asText());
                    if (!lnodeset.containsKey(lnodeid)) {
                        lnodeset.put(lnodeid, null);
                        llonodes.add(lnodedataobj);
                    }
                }
            }
        }
        return llonodes;
    }

    public static void sortGroupNodesAndContainers(DesignData templateDataObj) {
        int lnoofgroups = templateDataObj.groups.size();
        if (lnoofgroups > 0) {
            for (int g = 0; g < lnoofgroups; g++) {
                LayoutGroup lgroupobj = templateDataObj.groups.get(g);
                // //Sort Group Nodes
                Utils.sortNodes(lgroupobj.nodes);
                // //Sort Group Containers
                sortContainers(lgroupobj.containers);
            }
        }
    }

    public static String getElementType(String str) {
        String type = "";
        if (str.equals("BDG")) {
            type = "BADGE";
        } else if (str.equals("BTN") || str.equals("Btn")) {
            type = "BUTTON";
        } else if (str.equals("CBX")) {
            type = "CHECKBOX";
        } else if (str.equals("CXG")) {
            type = "CHECKBOXGROUP";
        } else if (str.equals("DPD") || str.equals("DDN")) {
            type = "DROPDOWN";
        } else if (str.equals("FIL")) {
            type = "FILEBROWSER";
        } else if (str.equals("GAG")) {
            type = "GAUGE";
        } else if (str.equals("HPL")) {
            type = "HYPERLINK";
        } else if (str.equals("ICN")) {
            type = "ICON";
        } else if (str.equals("IMG")) {
            type = "IMAGE";
        } else if (str.equals("INP") || str.equals("Num") || str.equals("FTxt") || str.equals("FTxtL")
                || str.equals("MailId") || str.equals("URL") || str.equals("FTxtC") || str.equals("FTxtU")
                || str.equals("Phone") || str.equals("RdO") || str.equals("Date") || str.equals("DateTime") || str.equals("IPS")) {
            type = "INPUTBOX";
        } else if (str.equals("IPB") || str.equals("DPF")) {
            type = "INPUTWITHBUTTON";
        } else if (str.equals("LABEL")) {
            type = "LABEL";
        } else if (str.equals("Label")) {
            type = "TEXT";
        } else if (str.equals("PGB")) {
            type = "PROGRESSBAR";
        } else if (str.equals("RadB")) {
            type = "RADIO";
        } else if (str.equals("SLD")) {
            type = "SLIDER";
        } else if (str.equals("STP")) {
            type = "STEPPER";
        } else if (str.equals("TAG")) {
            type = "TAGS";
        } else if (str.equals("TXT")) {
            type = "TEXT";
        } else if (str.equals("TXA") || str.equals("TxtArea")) {
            type = "TEXTAREA";
        } else if (str.equals("TGL")) {
            type = "TOGGLESWITCH";
        } else {
            type = "INPUTBOX";    ///// TBC - Default will be Text?
        }
        return type;
    }

    // // Container
    public static ScrContainerData createContainer(DataObject designObj, ObjectNode nodeData, ScrPanelSectionData paneData, String pname) {
        ScrContainerData lscrcntnrdata_obj = new ScrContainerData(pname);
        lscrcntnrdata_obj.parent = paneData;
        lscrcntnrdata_obj.pid = paneData.id;
        lscrcntnrdata_obj.titlestyle = "NONE";
        //lscrcntnrdata_obj.title = nodeData.title;
        lscrcntnrdata_obj.widgetcategory = "CONTAINER";
        lscrcntnrdata_obj.id = pname;
        updateObjLinks(lscrcntnrdata_obj, paneData, designObj);
        return lscrcntnrdata_obj;
    }

    // // Section Row
    public static ScrSectionRowData createSectionRow(DataObject designObj, ObjectNode nodeData, ScrContainerData pcontainerdata, String pname) {
        ScrSectionRowData sectionrowdataobj = new ScrSectionRowData(pname);
        sectionrowdataobj.parent = pcontainerdata;
        sectionrowdataobj.name = pname;
        sectionrowdataobj.id = pname;
        sectionrowdataobj.widgetcategory = "SECTION";
        sectionrowdataobj.widgettype = "ROW";
        sectionrowdataobj.pid = pcontainerdata.id;
        updateObjLinks(sectionrowdataobj, pcontainerdata, designObj);
        return sectionrowdataobj;
    }

    // // Section Column
    public static ScrSectionColumnData createSectionColumn(DataObject designObj, ObjectNode nodeData, ScrSectionRowData psecrowdata, String pname) {
        ScrSectionColumnData sectioncoldataobj = new ScrSectionColumnData(pname);
        sectioncoldataobj.parent = psecrowdata;
        sectioncoldataobj.name = pname;
        sectioncoldataobj.id = pname;
        sectioncoldataobj.widgetcategory = "SECTION";
        sectioncoldataobj.widgettype = "COLUMN";
        sectioncoldataobj.width = "12";
        sectioncoldataobj.verticalalignment = "MIDDLE";
        sectioncoldataobj.horizontalalignment = "";
        sectioncoldataobj.labelalignment = "";
        if (pname.equals("AppzillonWSButtons_SectionColumn")) {
            sectioncoldataobj.horizontalalignment = "RIGHT";
        }
        sectioncoldataobj.pid = psecrowdata.id;
        updateObjLinks(sectioncoldataobj, psecrowdata, designObj);
        return sectioncoldataobj;
    }

    // // Element
    public static ScrElementData createElement(String pname, ScrSectionColumnData secColData, ScrContainerData pcontainer, Parser parser) {
        DesignData designObj = parser.configObj;
        //String origId = Utils.removeSpecialChars(attr.get("OrigId").asText());
        ScrElementData scrElmData = new ScrElementData(pname);
        scrElmData.widgetcategory = "ELEMENT";
        //scrElmData.widgettype = "TEXT";
        scrElmData.appearance = "pri";
        //Default Value of the element.
        scrElmData.labelwidth = "40";
        scrElmData.width = "80";
        if (scrElmData.datatype.equals("DATE")) {
            scrElmData.icon = "icon-calendar";
            scrElmData.labelwidth = "40";
            scrElmData.width = "80";
            scrElmData.display = "CENTER";
            scrElmData.preset = "CALENDAR";
            scrElmData.state = "ENABLED";
            scrElmData.labelalignment = "";
        } else if (scrElmData.datatype.equals("DATETIME")) {
            scrElmData.icon = "icon-calendar";
            scrElmData.labelwidth = "40";
            scrElmData.width = "80";
            scrElmData.display = "CENTER";
            scrElmData.preset = "CALENDAR_DATETIME";
            scrElmData.style = "ANDROID-HOLO";
            scrElmData.state = "ENABLED";
            scrElmData.labelalignment = "";
        }
        scrElmData.pid = secColData.id;
        scrElmData.id = pname;
        updateObjLinks(scrElmData, secColData, designObj);
        populatePresentationElments(designObj, pcontainer, scrElmData);
        return scrElmData;
    }

    public static ScrElementData getNewElement(ObjectNode attr, String relationId, Parser parser, String subKey, String group) {
        DesignData designObj = parser.configObj;
        LOG.info(ServerConstants.LOGGER_PREFIX_CITI + "creating a new element for " + attr + " with Id " + relationId);
        String origId = Utils.removeSpecialChars(attr.get("OrigId").asText());
        LOG.info(ServerConstants.LOGGER_PREFIX_CITI + " origId : " + origId);
        ScrElementData scrElmData = null;
        ScrSectionColumnData secColData = null;
        if (relationId != null) {
            ScrContainerData cntr = designObj.containersMap.get(relationId);
            ScrContainerData headCntr = designObj.containersMap.get(relationId + "_header");
            if (headCntr != null) {
                int colIndex = 0;
                ScrSectionRowData secRowObj = headCntr.sectionrows.get(0);
                secColData = secRowObj.sectioncols.get(colIndex);
                if (attr.has("DXP_" + Parser.prodShortName + UI_SEQ)) {
                    ObjectNode sequence = (ObjectNode) attr.get("DXP_" + Parser.prodShortName + UI_SEQ);
                    colIndex = Integer.parseInt(sequence.get(VALUE).asText()) - 1;
                } else if (attr.has("DX_" + Parser.prodShortName + UI_SEQ)) {
                    ObjectNode sequence = (ObjectNode) attr.get("DX_" + Parser.prodShortName + UI_SEQ);
                    colIndex = Integer.parseInt(sequence.get(VALUE).asText()) - 1;
                } else if (attr.has("DX_" + Parser.prodShortName + SEQUENCE)) {
                    ObjectNode sequence = (ObjectNode) attr.get("DX_" + Parser.prodShortName + SEQUENCE);
                    colIndex = Integer.parseInt(sequence.get(VALUE).asText()) - 1;
                }
                boolean sequenceFlag = secRowObj.sectioncols.size() > colIndex ? true : false;
                if (sequenceFlag) {
                    secColData = secRowObj.sectioncols.get(colIndex);
                }
                scrElmData = designObj.elms_map.get(origId + "_title");
                if (scrElmData == null) {
                    if (headCntr.widgettype.equals("LIST") && !sequenceFlag) {
                        secColData = createSectionColumn(designObj, attr, secRowObj, relationId + "_secCol_header" + secRowObj.sectioncols.size());
                    }
                    scrElmData = createElement(origId + "_title", secColData, headCntr, parser);
                }
                scrElmData.widgettype = "TEXT";
                scrElmData.defaultvalue = attr.get(DISPLAY_NAME).asText();
                scrElmData.options = "Y";
            }
            if (cntr != null) {
                int colIndex = 0;
                ScrSectionRowData secRowObj = cntr.sectionrows.get(0);
                secColData = secRowObj.sectioncols.get(colIndex);
                if (attr.has("DXP_" + Parser.prodShortName + UI_SEQ)) {
                    ObjectNode sequence = (ObjectNode) attr.get("DXP_" + Parser.prodShortName + UI_SEQ);
                    colIndex = Integer.parseInt(sequence.get(VALUE).asText()) - 1;
                } else if (attr.has("DX_" + Parser.prodShortName + UI_SEQ)) {
                    ObjectNode sequence = (ObjectNode) attr.get("DX_" + Parser.prodShortName + UI_SEQ);
                    colIndex = Integer.parseInt(sequence.get(VALUE).asText()) - 1;
                } else if (attr.has("DX_" + Parser.prodShortName + SEQUENCE)) {
                    ObjectNode sequence = (ObjectNode) attr.get("DX_" + Parser.prodShortName + SEQUENCE);
                    colIndex = Integer.parseInt(sequence.get(VALUE).asText()) - 1;
                }

                boolean sequenceFlag = secRowObj.sectioncols.size() > colIndex ? true : false;
                if (sequenceFlag) {
                    secColData = secRowObj.sectioncols.get(colIndex);
                }
                if (cntr.widgettype.equals("LIST") && !sequenceFlag) {
                    secColData = createSectionColumn(designObj, attr, secRowObj, relationId + "_secCol" + secRowObj.sectioncols.size());
                }
                scrElmData = Utils.createElement(origId, secColData, cntr, parser);
                parser.updateElmObj(scrElmData, attr, subKey, group);
            } else {
                ScrPanelSectionData panelSecObj = designObj.panelSections.get(designObj.panelSections.size() - 1);
                // // Create Container
                cntr = createContainer(designObj, attr, panelSecObj, relationId);
                // // Section Row
                ScrSectionRowData lsecrowdataobj = createSectionRow(designObj, attr, cntr, relationId + "_secRow");
                // // Section Column
                secColData = createSectionColumn(designObj, attr, lsecrowdataobj, relationId + "_secCol");
                scrElmData = Utils.createElement(origId, secColData, cntr, parser);
                populateContainerDefaults((DesignData) designObj, cntr);
                parser.updateElmObj(scrElmData, attr, subKey, group);
            }
        } else {
            System.out.println("Cannont create element with out container id relation in template");
        }
        return scrElmData;
    }

    // // Portions
    public static void populatePortions(DesignData templateData, PortionData portionData) {
        if (portionData.getChilds() != null) {
            for (int c = 0; c < portionData.getChilds().size(); c++) {
                DataObject portionChild = portionData.getChilds().get(c);
                if (portionChild.type.equals("GRIDROW")) {
                    ScrGridRowData lgridrow = (ScrGridRowData) portionChild;
                    populateLayoutGridRow(templateData, lgridrow);
                } else if (portionChild.type.equals("POPUP")) {
                    ScrPopupData popupObj = (ScrPopupData) portionChild;
                    populatePopUp(templateData, popupObj);
                }
            }
        }
    }

    // // PopUp
    public static void populatePopUp(DesignData templateData, ScrPopupData popupData) {
        int lnoofgr = popupData.gridrows.size();
        if (lnoofgr > 0) {
            for (int lgc = 0; lgc < lnoofgr; lgc++) {
                ScrGridRowData lgridrow = popupData.gridrows.get(lgc);
                populateLayoutGridRow(templateData, lgridrow);
            }
        }
    }

    // // Polpulate GridRow
    public static void populateLayoutGridRow(DesignData templateDataObj, ScrGridRowData pgridrow) {
        int lnoofgc = pgridrow.gridcols.size();
        if (lnoofgc > 0) {
            for (int lgc = 0; lgc < lnoofgc; lgc++) {
                ScrGridColumnData lgridcol = pgridrow.gridcols.get(lgc);
                populateLayoutGridCol(templateDataObj, lgridcol);
            }
        }
    }

    // // Populate Grid Column
    public static void populateLayoutGridCol(DesignData tmplateDataObj, ScrGridColumnData gridColData) {
        int lnoofgridrows = gridColData.gridrows.size();
        int noOfGridPanes = gridColData.gridpane.size();
        // // Grid Column Grid Rows
        for (int gr = 0; gr < lnoofgridrows; gr++) {
            ScrGridRowData gridRowData = gridColData.gridrows.get(gr);
            populateLayoutGridRow(tmplateDataObj, gridRowData);
        }
        if (noOfGridPanes > 0) {
            for (int lgc = 0; lgc < noOfGridPanes; lgc++) {
                ScrPanelData gridPaneData = gridColData.gridpane.get(lgc);
                populateLayoutPanel(tmplateDataObj, gridPaneData);
            }
        }
    }

    ///// Populate grid Pane
    public static void populateLayoutPanel(DesignData templateDataObj, ScrPanelData panelData) {
        int noOfPanes = panelData.panelsections.size();
        if (noOfPanes > 0) {
            for (int lgc = 0; lgc < noOfPanes; lgc++) {
                ScrPanelSectionData paneData = panelData.panelsections.get(lgc);
                populateLayoutPanelSection(templateDataObj, paneData);
            }
        }
    }

    /////
    public static void populateLayoutPanelSection(DesignData templateDataObj, ScrPanelSectionData panelSecData) {
        templateDataObj.panelSections.add(panelSecData);
        templateDataObj.panelSectionsMap.put(panelSecData.name, panelSecData);
        int noOfContainers = panelSecData.containers.size();
        int lnoofgridrows = panelSecData.gridrows.size();
        // // Add Containers
        for (int c = 0; c < noOfContainers; c++) {
            ScrContainerData containerData = panelSecData.containers.get(c);
            containerData.nodes.clear();
            containerData.nodesmap.clear();
            containerData.elms.clear();
            containerData.elmsmap.clear();
            containerData.multirec = "N";
            // // getContainer Nodes
            populateContainerDefaults(templateDataObj, containerData);
        }
        // // Grid Column Grid Rows
        for (int gr = 0; gr < lnoofgridrows; gr++) {
            ScrGridRowData gridRowData = panelSecData.gridrows.get(gr);
            populateLayoutGridRow(templateDataObj, gridRowData);
        }
    }

    // // Container Nodes
    public static void populateContainerDefaults(DesignData templateDataObj, ScrContainerData pcontainer) {
        // HashSet<String> lnodesset = new HashSet();
        if (pcontainer != null) {
            if (pcontainer.widgettype.equals("CHART")) {
                pcontainer.multirec = "Y";
            }
            PortionData lportiondata = (PortionData) templateDataObj.body;
            if (pcontainer.widgettype.equals("MENU")) {
                if (!Utils.isNull(pcontainer.appearance)) {
                    if (pcontainer.appearance.equals("pri")) {
                        lportiondata.menuappearance = " mnp";
                    } else if (pcontainer.appearance.equals("sec")) {
                        lportiondata.menuappearance = " mns";
                    } else {
                        lportiondata.menuappearance = " mnt";
                    }
                }
                if (!Utils.isNull(pcontainer.iconposition)
                        && pcontainer.iconposition.equals("TOP")) {
                    lportiondata.menuiconposition = " icp";
                }
            }
            /// Adding overlay containers to layout
            if (!Utils.isNull(pcontainer.targetoverlayid)) {
                templateDataObj.overlays.add(templateDataObj.appId + "__" + templateDataObj.screenId + "__" + pcontainer.targetoverlayid);
            }
            // // Add Container to Layout
            templateDataObj.containers.add(pcontainer);
            templateDataObj.containersMap.put(pcontainer.name, pcontainer);
            // // Add CustomWidgets Containers to map
            if (pcontainer.custom.equals("Y")) {
                if (!templateDataObj.customcontainers.contains(pcontainer.widgettype)) {
                    templateDataObj.customcontainers.add(pcontainer.widgettype);
                }
            }
            int lnoofrows = pcontainer.sectionrows.size();
            for (int s = 0; s < lnoofrows; s++) {
                ScrSectionRowData lsecrow = pcontainer.sectionrows.get(s);
                populateSectionRows(templateDataObj, pcontainer, lsecrow);
            }
        }
    }

    // // Populate SectionRow
    public static void populateSectionRows(DesignData templateData, ScrContainerData pcontainer, ScrSectionRowData psecrow) {
        int lnoofsecs = psecrow.sectioncols.size();
        for (int c = 0; c < lnoofsecs; c++) {
            ScrSectionColumnData seccoldataobj = psecrow.sectioncols.get(c);
            populateSectionColumns(templateData, pcontainer, psecrow, seccoldataobj);
        }
    }

    // // Populate Section Cols
    public static void populateSectionColumns(DesignData templateData, ScrContainerData pcontainer, ScrSectionRowData psecrow, ScrSectionColumnData seccoldataobj) {
        // // Selection Column Elements
        int lnoofelms = seccoldataobj.elements.size();
        for (int e = 0; e < lnoofelms; e++) {
            ScrElementData lelmddata = seccoldataobj.elements.get(e);
            populatePresentationElments(templateData, pcontainer, lelmddata);
        }
        // // Section Column Section Rows
        int lnoofsecrows = seccoldataobj.sectionrows.size();
        for (int s = 0; s < lnoofsecrows; s++) {
            ScrSectionRowData secrowdataobj = seccoldataobj.sectionrows.get(s);
            populateSectionRows(templateData, pcontainer, secrowdataobj);
        }
		/*	TBC - Do we need to update here as its used only in generation part?
		 * if((!Utils.isNull(seccoldataobj.phonewidth) || !Utils.isNull(seccoldataobj.tabletwidth) || 
				!Utils.isNull(seccoldataobj.desktopwidth) || !Utils.isNull(seccoldataobj.wswidth)) && 
				!seccoldataobj.width.equals("CUSTOM") && !Utils.isNull(seccoldataobj.width)) {
			psecrow.hasRespSecCol = "Y";
		}*/
    }

    // // Populate Section Cols
    public static void populatePresentationElments(DesignData templateData, ScrContainerData pcontainer, ScrElementData lelmddata) {
        // // Add Screen Element Data to Containers Array
        if (!pcontainer.elmsmap.containsKey(lelmddata.name)) {
            pcontainer.elms.add(lelmddata);
            pcontainer.elmsmap.put(lelmddata.name, lelmddata);
        }
        if (!templateData.elms_map.containsKey(lelmddata.name)) {
            templateData.elms_map.put(lelmddata.name, lelmddata);
            templateData.elms.add(lelmddata);
        }
        //populateElementNodes(templateData, pcontainer, lelmddata);
        int noOfElmsinElm = lelmddata.elements.size();
        for (int i = 0; i < noOfElmsinElm; i++) {
            ScrElementData llelmddata = lelmddata.elements.get(i);
            if (!pcontainer.elmsmap.containsKey(llelmddata.name)) {
                pcontainer.elms.add(llelmddata);
                pcontainer.elmsmap.put(llelmddata.name, llelmddata);
            }
            if (!templateData.elms_map.containsKey(llelmddata.name)) {
                templateData.elms_map.put(llelmddata.name, llelmddata);
                templateData.elms.add(lelmddata);
            }
        }
    }

    public static void updateObjLinks(DataObject newObj, DataObject parentObj, DataObject designObj) {
        newObj.parent = parentObj;
        parentObj.getChilds().add(newObj);
        parentObj.childsMap.put(newObj.name, newObj);
        newObj.add();
    }

    public static void writeFile(String pcontent, String ppath) {
        try {
            String previous = getFileContent(ppath);
            URL filePath = RenderAppzillonDefinitionJson.class.getClassLoader().getResource(ppath);

            try (Writer lout = new BufferedWriter(new FileWriter(filePath.getFile()))) {
                lout.write(previous + pcontent.toString() + "\n");
            }
        } catch (Exception pex) {
            pex.printStackTrace();
        }
    }

    public static String getFileContent(String fileName) throws Exception {
        byte[] resultContent = null;
        try {
            resultContent = readFileContents(fileName);
        } catch (Exception e) {
            resultContent = readFileContents("Default_Template.json");
        }
        String lstr = new String(resultContent);
        return lstr;
    }

    public static byte[] readFileContents(String file) throws IOException {

        DataInputStream dis;
        byte[] privKeyBytes;

        try (InputStream is = RenderAppzillonDefinitionJson.class.getClassLoader().
                getResourceAsStream(file)) {
            dis = new DataInputStream(is);
        }
        privKeyBytes = new byte[dis.available()];
        dis.readFully(privKeyBytes);
        dis.close();
        return privKeyBytes;
    }

    public static String getGlobalVariables(Object obj, String str, Parser p, boolean ignoreRltdElms) {
        boolean internalVariable = false, processFromRoot = false;
        String trimedStr = str.trim();
        if (str.startsWith(".:")) {
            processFromRoot = true;
            trimedStr = trimedStr.substring(2).trim();
        } else if (str.startsWith(":")) {
            trimedStr = trimedStr.substring(1).trim();
        } else if (str.startsWith(".")) {
            processFromRoot = true;
            trimedStr = trimedStr.substring(1).trim();
        } else if (str.startsWith("&")) {
            trimedStr = trimedStr.substring(1).trim();
        }
        if (trimedStr.endsWith(IS_NULL) || trimedStr.endsWith(IS_NOT_NULL)) {
            if (trimedStr.contains(IS_NULL)) {
                trimedStr = trimedStr.substring(0, trimedStr.indexOf(IS_NULL));
            }
            if (trimedStr.contains(IS_NOT_NULL)) {
                trimedStr = trimedStr.substring(0, trimedStr.indexOf(IS_NOT_NULL));
            }
            trimedStr = trimedStr.trim();
        }
        if (trimedStr.equals("Region") || trimedStr.equals(":Region") || trimedStr.equals(".:Region")) {
            internalVariable = true;
            str = str.replace(trimedStr, "apz.products.Region");
        } else if (trimedStr.equals("Pname") || trimedStr.equals(":Pname") || trimedStr.equals(".:Pname")) {
            internalVariable = true;
            str = str.replace(trimedStr, "apz.products.Pname");
        } else if (trimedStr.equals("RPID") || trimedStr.equals(":RPID") || trimedStr.equals(".:RPID")) {
            internalVariable = true;
            str = str.replace(trimedStr, "apz.products.RPID");
        } else if (str.equals("PPID") || str.equals(":PPID") || str.equals(".:PPID")) {
            internalVariable = true;
            str = str.replace(trimedStr, "apz.products.PPID");
        }
        ScrElementData elmObj = null;
        ScrContainerData cntObj = null;
        boolean referId = false;
        String type = SCR_ELEMENT_DATA;
        if (obj instanceof ScrElementData) {
            elmObj = (ScrElementData) obj;
            referId = elmObj.referId;
        } else if (obj instanceof ScrContainerData) {
            type = SRC_CONTAINER_DATA;
            cntObj = (ScrContainerData) obj;
            referId = cntObj.referId;
        }

        if (internalVariable) {
            if (Parser.decorationMappings.containsKey(trimedStr)) {
                String mappedId = Parser.decorationMappings.get(trimedStr);
                ScrElementData refElmObj = null;
                for (int i = p.relatedProducts.size() - 1; i >= 0; i--) {
                    String key = p.relatedProducts.get(i) + "__" + mappedId;
                    if (Parser.nameMapper.containsKey(key)) {
                        refElmObj = Parser.nameMapper.get(key);
                        break;
                    }
                }
                if (refElmObj != null) {
                    if (SCR_ELEMENT_DATA.equals(type) && elmObj != null) {
                        String relatedElm = Utils.getElementId(elmObj, p.appId, p.scrId, -1);
                        if (!refElmObj.relatedelms.contains(relatedElm)) {
                            refElmObj.relatedelms.add(relatedElm);
                        }
                    } else if (SRC_CONTAINER_DATA.equals(type) && cntObj != null) {
                        String relatedCntrID = Utils.getContainerId(cntObj, p.appId, p.scrId);
                        if (!refElmObj.relatedcontainer.contains(relatedCntrID)) {
                            refElmObj.relatedcontainer.add(relatedCntrID);
                        }
                    }
                }
            }
        } else {
            ScrElementData refElmObj = null;
            ArrayList<String> prodList = new ArrayList<String>(p.relatedProducts);
            if (!processFromRoot) {
                Collections.reverse(prodList);
            }
            for (int i = 0; i < prodList.size(); i++) {
                String key = prodList.get(i) + "__" + trimedStr;
                if (Parser.nameMapper.containsKey(key)) {
                    refElmObj = Parser.nameMapper.get(key);
                    break;
                } else if (referId && Parser.origIdMapper.containsKey(key)) {
                    refElmObj = Parser.origIdMapper.get(key);
                    break;
                }
            }
            if (refElmObj == null && trimedStr.contains(".") && trimedStr.contains(":")) {
                String[] chunks = trimedStr.split("\\.");
                int chunksLen = chunks.length;
                ObjectNode relObj = null;
                for (int c = 0; c < chunksLen; c++) {
                    String chunk = chunks[c].trim();
                    if (c == chunksLen - 1) {
                        String[] lists = chunk.split(":");
                        chunk = lists[0].trim();
                        if (relObj != null) {
                            relObj = (ObjectNode) relObj.get(chunk);
                        } else {
                            relObj = (ObjectNode) Parser.sharedRelatedProductsMap.get(chunk);
                        }
                        if (relObj != null) {
                            String key = relObj.get("Name").asText() + "__" + lists[1].trim();
                            if (Parser.nameMapper.containsKey(key)) {
                                refElmObj = Parser.nameMapper.get(key);
                            } else if (elmObj.referId && Parser.origIdMapper.containsKey(key)) {
                                refElmObj = Parser.origIdMapper.get(key);
                            }
                        }
                        if (refElmObj != null) {
                            str = str.replace(trimedStr, Utils.getElementId(refElmObj, p.appId, p.scrId, -1));
                            if (!ignoreRltdElms) {
                                if (SCR_ELEMENT_DATA.equals(type)) {
                                    String relatedElm = Utils.getElementId(elmObj, p.appId, p.scrId, -1);
                                    if (!refElmObj.relatedelms.contains(relatedElm)) {
                                        refElmObj.relatedelms.add(relatedElm);
                                    }
                                } else if (SRC_CONTAINER_DATA.equals(type)) {
                                    String relatedCntrElm = Utils.getContainerId(cntObj, p.appId, p.scrId);
                                    if (!refElmObj.relatedcontainer.contains(relatedCntrElm)) {
                                        refElmObj.relatedcontainer.add(relatedCntrElm);
                                    }
                                }
                            }
                            refElmObj = null;
                        }
                    } else {
                        if (relObj != null) {
                            relObj = (ObjectNode) relObj.get(chunk);
                        } else {
                            relObj = (ObjectNode) Parser.sharedRelatedProductsMap.get(chunk);
                        }
                    }
                }
                refElmObj = null;
            }
            if (refElmObj != null) {
                str = str.replace(trimedStr, Utils.getElementId(refElmObj, p.appId, p.scrId, -1));
                if (!ignoreRltdElms) {
                    if (SCR_ELEMENT_DATA.equals(type)) {
                        String relatedElm = Utils.getElementId(elmObj, p.appId, p.scrId, -1);
                        if (!refElmObj.relatedelms.contains(relatedElm)) {
                            refElmObj.relatedelms.add(relatedElm);
                        }
                    } else if (SRC_CONTAINER_DATA.equals(type)) {
                        String relatedCntrElm = Utils.getContainerId(cntObj, p.appId, p.scrId);
                        if (!refElmObj.relatedcontainer.contains(relatedCntrElm)) {
                            refElmObj.relatedcontainer.add(relatedCntrElm);
                        }
                    }
                }
            }
        }
        return str;
    }

    private static String getContainerId(ScrContainerData cntObj, String appId, String scrId) {
        String cntrId = "";
        String cntrName = cntObj.name;
        if (Utils.isNull(cntrId)) {
            cntrId = appId + "__" + scrId + "__" + cntrName;
        }
        return cntrId;
    }

    public static String getObjectGlobalVariables(String str, Parser p) {
        boolean internalVariable = false, processFromRoot = false;
        String trimedStr = str.trim();
        if (str.startsWith(".:")) {
            processFromRoot = true;
            trimedStr = trimedStr.substring(2).trim();
        } else if (str.startsWith(":")) {
            trimedStr = trimedStr.substring(1).trim();
        } else if (str.startsWith(".")) {
            processFromRoot = true;
            trimedStr = trimedStr.substring(1).trim();
        } else if (str.startsWith("&")) {
            trimedStr = trimedStr.substring(1).trim();
        }
        if (trimedStr.endsWith(IS_NULL) || trimedStr.endsWith(IS_NOT_NULL)) {
            if (trimedStr.contains(IS_NULL)) {
                trimedStr = trimedStr.substring(0, trimedStr.indexOf(IS_NULL));
            }
            if (trimedStr.contains(IS_NOT_NULL)) {
                trimedStr = trimedStr.substring(0, trimedStr.indexOf(IS_NOT_NULL));
            }
            trimedStr = trimedStr.trim();
        }
        if (trimedStr.equals("Region") || trimedStr.equals(":Region") || trimedStr.equals(".:Region")) {
            internalVariable = true;
            str = str.replace(trimedStr, "apz.products.Region");
        } else if (trimedStr.equals("Pname") || trimedStr.equals(":Pname") || trimedStr.equals(".:Pname")) {
            internalVariable = true;
            str = str.replace(trimedStr, "apz.products.Pname");
        } else if (trimedStr.equals("RPID") || trimedStr.equals(":RPID") || trimedStr.equals(".:RPID")) {
            internalVariable = true;
            str = str.replace(trimedStr, "apz.products.RPID");
        } else if (str.equals("PPID") || str.equals(":PPID") || str.equals(".:PPID")) {
            internalVariable = true;
            str = str.replace(trimedStr, "apz.products.PPID");
        }
        ScrElementData refElmObj = null;
        ArrayList<String> prodList = new ArrayList<String>(p.relatedProducts);
        if (!processFromRoot) {
            Collections.reverse(prodList);
        }
        for (int i = 0; i < prodList.size(); i++) {
            String key = prodList.get(i) + "__" + trimedStr;
            if (Parser.nameMapper.containsKey(key)) {
                refElmObj = Parser.nameMapper.get(key);
                break;
            }
        }
        if (refElmObj == null && trimedStr.contains(".")) {
            String[] chunks = trimedStr.split("\\.");
            for (int c = 0; c < chunks.length; c++) {
                String chunk = chunks[c];
                if (prodList.size() > c) {
                    String key = p.relatedProducts.get(c) + "__" + chunk;
                    if (Parser.nameMapper.containsKey(key)) {
                        refElmObj = Parser.nameMapper.get(key);
                    }
                    if (refElmObj != null) {
                        chunks[c] = chunk.replace(chunk, Utils.getObjectElementId(refElmObj, p.appId, p.scrId));
                        refElmObj = null;
                    }
                }
            }
            str = str.replace(trimedStr, StringUtils.join(chunks, "."));
            refElmObj = null;
        }
        if (refElmObj != null) {
            str = str.replace(trimedStr, Utils.getObjectElementId(refElmObj, p.appId, p.scrId));
        }

        return str;

    }


    public static String getObjectElementId(ScrElementData elementData, String appId, String scrId) {
        String elementId = "";
        String elementIfaceName = elementData.interfacename;
        String elemntDmlType = elementData.datamodeltype;
        String elementNodeName = elementData.nodename;
        String elementName = elementData.elementname;
        if (Utils.isDMlElement(elementData)) {
            String partialNodeName = elementData.nodename.replace(elementData.interfacename, "");
            String name = "";
            boolean apzNode = false;
            if (partialNodeName.equals("_Req")) {
                name = appId + "__" + elementData.nodename;
                apzNode = true;
            } else if (partialNodeName.equals("_Res")) {
                name = appId + "__" + elementData.nodename;
                apzNode = true;
            } else if (partialNodeName.equals("_Flt")) {
                name = appId + "__" + elementData.nodename;
                apzNode = true;
            }
            if (apzNode) {
                elementId = elementData.interfacename + "__" + Utils.getDMLId(elementData.datamodeltype)
                        + "__" + name + "__" + elementData.elementname;
            } else {
                elementId = Utils.getElmId(elementIfaceName, elemntDmlType, elementNodeName, elementName);
            }
        } else {
            elementId = scrId + "__" + elementData.name;
        }
        if (!Utils.isNull(elementId)) {
            elementId = appId + "__" + elementId;
        }
        return elementId;

    }

    public static String updateStrWithIds(Object Obj, String decoreName, String decoreVal, Parser p, boolean ignoreRltdElms) {
        String[] splitted = getOperatorsSplitStr(decoreVal, decoreName);
        int length = splitted.length;
        String trimedDecorName = decoreName.split(" ")[0];
        String[] result = new String[length];
        for (int a = 0; a < length; a++) {
            String fragment = splitted[a];
            String trimedFragmnt = fragment.trim();
            if (trimedFragmnt.length() > 2) {
                if (trimedDecorName.endsWith("_API")) {
                    if (a > 0 && splitted[a - 1].trim().endsWith("&")) {
                        result[a] = fragment.replace(trimedFragmnt, Utils.getGlobalVariables(Obj, trimedFragmnt, p, ignoreRltdElms));
                    } else {
                        result[a] = splitted[a];
                    }
                } else {
                    result[a] = fragment.replace(trimedFragmnt, Utils.getGlobalVariables(Obj, trimedFragmnt, p, ignoreRltdElms));
                }

                if (trimedDecorName.contains("_APIOutputMap")) {
                    if (a > 0 && splitted[a - 1].equals(",")) {
                        result[a] = splitted[a];
                    } else {
                        result[a] = fragment.replace(trimedFragmnt, Utils.getGlobalVariables(Obj, trimedFragmnt, p, ignoreRltdElms));
                    }
                }

                if (trimedDecorName.contains("_Rvalue")) {
                    if ((a > 0 && a == length - 1) && splitted[a - 1].equals("|")) {
                        result[a] = splitted[a];
                    }
                }

            } else if (trimedFragmnt.equals("&") && result.length > a) {
                String previousStr = splitted[a - 1];
                String nextStr = splitted[a + 1];
                if (splitted[a - 1].endsWith(" ") && splitted[a + 1].startsWith(" ")) {
                    String temp = previousStr + splitted[a] + nextStr;
                    result[a - 1] = fragment.replace(trimedFragmnt, Utils.getGlobalVariables(Obj, temp.trim(), p, ignoreRltdElms));
                    result[a] = result[a + 1] = "";
                    a++;
                } else {
                    result[a] = splitted[a];
                }
            } else {
                result[a] = splitted[a];
            }
        }
        decoreVal = StringUtils.join(result, "");
        return decoreVal;
    }

    public static String[] getOperatorsSplitStr(String decoreVal, String decoreName) {
        String allowedOps = "&|()><=!";
        if (decoreName.contains("_APIOutputMap") || decoreName.contains("_SetValue") || decoreName.contains("_GetValue")) {
            allowedOps = "&|()><=!,";
        } else if (decoreName.contains("_LinkedItem")) {
            allowedOps = "&|()><=!,\\[\\]";
        } else if (decoreName.contains("_API")) {
            allowedOps = "&|()><=!,\\[\\]'";
        }
        String[] result = decoreVal.split("(?<![" + allowedOps + "])(?=[" + allowedOps + "])|(?<=[" + allowedOps + "])(?![" + allowedOps + "])");
        return result;
    }

    public static String removeSpecialChars(String str) {
        if (str.contains("-")) {
            str = str.substring(str.indexOf("-") + 1);
        }
        str = str.replace("@", "");
        str = str.replace("#", "");
    	/*Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(str);
        while(match.find())
        {
            String s= match.group();
            str=str.replaceAll("\\"+s, "");
        }*/
        return str;
    }

    public static void addChildJSONElm(ObjectNode pobj, String pname, String pval) {
        if (pval != null) {
            pobj.put(pname, pval);
        }
    }

    public static void addChildJSONElm(ObjectNode pobj, String pname, boolean pval) {
        pobj.put(pname, pval);
    }

    public static boolean isDMlElement(ScrElementData pelementdata) {
        boolean lresult = false;
        if (!Utils.isNull(pelementdata.interfacename) && (!Utils.isNull(pelementdata.datamodeltype)) && (!Utils.isNull(pelementdata.nodename)) && (!Utils.isNull(pelementdata.elementname))) {
            lresult = true;
        }
        return lresult;
    }

    public static String getElementId(ScrElementData elementData, String appId, String scrId, int rowNo) {
        String elementId = "";
        String elementIfaceName = elementData.interfacename;
        String elemntDmlType = elementData.datamodeltype;
        String elementNodeName = elementData.nodename;
        String elementName = elementData.elementname;
        String rowIndex = getRowIndex(elementData, rowNo);

        if (Utils.isDMlElement(elementData)) {
            String partialNodeName = elementData.nodename.replace(elementData.interfacename, "");
            String name = "";
            boolean apzNode = false;
            if (partialNodeName.equals("_Req")) {
                name = appId + "__" + elementData.nodename;
                apzNode = true;
            } else if (partialNodeName.equals("_Res")) {
                name = appId + "__" + elementData.nodename;
                apzNode = true;
            } else if (partialNodeName.equals("_Flt")) {
                name = appId + "__" + elementData.nodename;
                apzNode = true;
            }
            if (apzNode) {
                elementId = elementData.interfacename + "__" + Utils.getDMLId(elementData.datamodeltype)
                        + "__" + name + "__" + elementData.elementname;
            } else {
                elementId = Utils.getElmId(elementIfaceName, elemntDmlType, elementNodeName, elementName);
            }
        } else {
            elementId = scrId + "__" + elementData.name;
        }
        if (rowNo != -1) {
            elementId = elementId + "_" + rowIndex;
        }
        if (!Utils.isNull(elementId)) {
            elementId = appId + "__" + elementId;
        }
        return elementId;
    }

    public static String getRowIndex(ScrElementData elementData, int rowNo) {
        String index = "";
        if (rowNo != -1) {
            index = Integer.toString(rowNo);
        }
        return index;
    }


    public static String getTitle(ObjectNode relationship) {
        String title = null;
        if (relationship.has("DXP_" + Parser.prodShortName + UI_NAME)) {
            title = relationship.get("DXP_" + Parser.prodShortName + UI_NAME).get(VALUE).asText();
        } else if (relationship.has("DX_" + Parser.prodShortName + UI_NAME)) {
            title = relationship.get("DX_" + Parser.prodShortName + UI_NAME).get(VALUE).asText();
        } else if (relationship.has("DXP_" + Parser.prodShortName + DISP_NAME)) {
            title = relationship.get("DXP_" + Parser.prodShortName + DISP_NAME).get(VALUE).asText();
        } else if (relationship.has("DX_" + Parser.prodShortName + DISP_NAME)) {
            title = relationship.get("DX_" + Parser.prodShortName + DISP_NAME).get(VALUE).asText();
        } else if (relationship.has(DISPLAY_NAME)) {
            title = relationship.get(DISPLAY_NAME).asText();
        } else {
            title = relationship.get("Name").asText();
        }
        return title;
    }

    public static void createDecorationsMap(ObjectNode obj, String decoration) {
        LOG.info(ServerConstants.LOGGER_PREFIX_CITI + "Creating Decoraation Map ");
        JsonNode attrDecor = obj.get(decoration);
        if (attrDecor != null) {
            String dispName = obj.get(DISPLAY_NAME).asText();
            if (attrDecor.isArray()) {
                ArrayNode attrDecors = (ArrayNode) attrDecor;
                LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "decoration Mapping Details : ");
                for (int d = 0; d < attrDecors.size(); d++) {
                    ObjectNode decor = (ObjectNode) attrDecors.get(d);
                    String decorationName = decor.get("Name").asText();
                    obj.put(decorationName, decor);
                    if (decorationName.equals("DXP_" + Parser.prodShortName + MAPPING) || decorationName.equals("DX_" + Parser.prodShortName + MAPPING)) {
                        String decorationVal = decor.get(VALUE).asText();
                        String secRule = decorationVal.split(",")[1];
                        String lhs = secRule.trim();
                        if (secRule.contains("!=")) {
                            lhs = secRule.split("!=")[0].trim();
                        } else if (secRule.contains("=")) {
                            lhs = secRule.split("=")[0].trim();
                        } else if (secRule.contains("<>")) {
                            lhs = secRule.split("<>")[0].trim();
                        }
                        Parser.decorationMappings.put(lhs, dispName);
                        LOG.trace(ServerConstants.LOGGER_PREFIX_CITI + " lhs - " + lhs + " dispName - " + dispName);
                    }
                }
            } else {
                ObjectNode decor = (ObjectNode) attrDecor;
                String decorationName = decor.get("Name").asText();
                obj.put(decorationName, decor);
                LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "decoration Mapping Details : ");
                if (decorationName.equals("DXP_" + Parser.prodShortName + MAPPING) || decorationName.equals("DX_" + Parser.prodShortName + MAPPING)) {
                    String decorationVal = decor.get(VALUE).asText();
                    String secRule = decorationVal.split(",")[1];
                    String lhs = secRule.trim();
                    if (secRule.contains("!=")) {
                        lhs = secRule.split("!=")[0].trim();
                    } else if (secRule.contains("=")) {
                        lhs = secRule.split("=")[0].trim();
                    } else if (secRule.contains("<>")) {
                        lhs = secRule.split("<>")[0].trim();
                    }
                    Parser.decorationMappings.put(lhs, dispName);
                    LOG.trace(ServerConstants.LOGGER_PREFIX_CITI + " lhs - " + lhs + " dispName - " + dispName);
                }
            }
            LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "Decoration node" + attrDecor.toString());
        }
    }

    public static void sortObjects(List<ObjectNode> list, final String decoration, final ObjectNode object) {
        Collections.sort(list, new Comparator<ObjectNode>() {
            @Override
            public int compare(ObjectNode o1, ObjectNode o2) {
                int res = 0;
                int curSeq = 0, newSeq = 0, min = 0, max = 0;
                //System.out.print(o1.get("OrigId").asText()+" : "+o2.get("OrigId").asText());
                JsonNode attrDecor = o2.get(decoration);
                if (attrDecor != null) {
                    if (attrDecor.isArray()) {
                        ArrayNode attrDecors = (ArrayNode) attrDecor;
                        for (int d = 0; d < attrDecors.size(); d++) {
                            ObjectNode decor = (ObjectNode) attrDecors.get(d);
                            String decorationName = decor.get("Name").asText();
                            if (decorationName.equals("DXP_" + Parser.prodShortName + UI_SEQ)) {
                                curSeq = Integer.parseInt(decor.get(VALUE).asText());
                            } else if (decorationName.equals("DX_" + Parser.prodShortName + UI_SEQ)) {
                                curSeq = Integer.parseInt(decor.get(VALUE).asText());
                            } else if (decorationName.equals("DX_" + Parser.prodShortName + "_Sequance")) {
                                curSeq = Integer.parseInt(decor.get(VALUE).asText());
                            }
                        }
                    }
                }
                attrDecor = o1.get(decoration);
                if (attrDecor != null) {
                    if (attrDecor.isArray()) {
                        ArrayNode attrDecors = (ArrayNode) attrDecor;
                        for (int d = 0; d < attrDecors.size(); d++) {
                            ObjectNode decor = (ObjectNode) attrDecors.get(d);
                            String decorationName = decor.get("Name").asText();
                            if (decorationName.equals("DXP_" + Parser.prodShortName + UI_SEQ)) {
                                newSeq = Integer.parseInt(decor.get(VALUE).asText());
                            } else if (decorationName.equals("DX_" + Parser.prodShortName + UI_SEQ)) {
                                newSeq = Integer.parseInt(decor.get(VALUE).asText());
                            } else if (decorationName.equals("DX_" + Parser.prodShortName + "_Sequance")) {
                                newSeq = Integer.parseInt(decor.get(VALUE).asText());
                            }
                        }
                    }
                }
                if (curSeq < newSeq) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + " curSeq " + curSeq + "< newSeq " + newSeq);
                    res = 1;
                    min = curSeq == 0 ? newSeq : curSeq;
                    max = newSeq;
                } else if (curSeq > newSeq) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + " curSeq " + curSeq + "> newSeq " + newSeq);
                    res = -1;
                    min = newSeq;
                    max = curSeq;
                }
                if (!DesignUtils.RELATIONSHIP_DOMAIN_DECORATION.equals(decoration)) {
                    int availMin = object.get("min" + decoration).asInt();
                    int availMax = object.get("max" + decoration).asInt();
                    if (min > 0 && min < availMin) {
                        object.put("min" + decoration, min);
                        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + " min" + decoration);
                    }
                    if (max > availMax) {
                        object.put("max" + decoration, max);
                        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + " max" + decoration);
                    }
                }
                return res;
            }
        });
    }

    public static String getDefaultTemplate() {
        String templateContent = "{\"typeclass\":\"DESIGN\",\"name\":\"D0\",\"type\":\"DESIGN\",\"childs\":[{\"typeclass\":\"LAYOUTPORTION\","
                + "\"name\":\"header\",\"type\":\"LAYOUTPORTION\",\"id\":\"header\",\"pid\":\"pagecontainer\",\"widgetcategory\":\"PORTION\","
                + "\"widgettype\":\"HEADER\"},{\"typeclass\":\"LAYOUTPORTION\",\"name\":\"body\",\"type\":\"LAYOUTPORTION\",\"childs\":[{"
                + "\"typeclass\":\"GRIDROW\",\"name\":\"gr_row_1\",\"type\":\"GRIDROW\",\"childs\":[{\"typeclass\":\"GRIDCOLUMN\","
                + "\"name\":\"gr_col_1\",\"type\":\"GRIDCOLUMN\",\"childs\":[{\"typeclass\":\"PANEL\",\"name\":\"pl_pnl_1\","
                + "\"type\":\"PANEL\",\"childs\":[{\"typeclass\":\"PANELSECTION\",\"name\":\"ps_pls_1\",\"type\":\"PANELSECTION\","
                + "\"id\":\"d3ed27ab40198222c77efe7e\",\"pid\":\"b87ae46a4e0692b287a39db8\",\"widgetcategory\":\"PANELSECTION\","
                + "\"widgettype\":\"PANELSECTION\",\"appearance\":\"pri\",\"bottompaddingcustomtype\":\"PX\",\"events\":[{\"name\":\"ONLOAD\"}],"
                + "\"leftpadding\":\"ALL\",\"leftpaddingcustomtype\":\"PX\",\"options\":\"Y\",\"rightpaddingcustomtype\":\"PX\","
                + "\"spinner\":\"N\",\"spinnersize\":\"REGULAR\",\"state\":\"OPEN\",\"toppaddingcustomtype\":\"PX\",\"transpspinner\":\"N\"}],"
                + "\"id\":\"b87ae46a4e0692b287a39db8\",\"pid\":\"963c22d8453cb7d6375fe92d\",\"widgetcategory\":\"PANEL\","
                + "\"widgettype\":\"SIMPLE\",\"bottommargincustomtype\":\"PX\",\"events\":[{\"name\":\"ONLOAD\"}],\"leftmargin\":\"ALL\","
                + "\"leftmargincustomtype\":\"PX\",\"options\":\"Y\",\"rightmargincustomtype\":\"PX\",\"topmargincustomtype\":\"PX\"}],"
                + "\"id\":\"963c22d8453cb7d6375fe92d\",\"pid\":\"a1301d4745328772fef2f791\",\"widgetcategory\":\"GRID\","
                + "\"widgettype\":\"COLUMN\",\"desktophide\":\"N\",\"options\":\"Y\",\"phonehide\":\"N\",\"tablethide\":\"N\","
                + "\"width\":\"12\",\"wshide\":\"N\"}],\"id\":\"a1301d4745328772fef2f791\",\"pid\":\"body\",\"widgetcategory\":\"GRID\","
                + "\"widgettype\":\"ROW\",\"centercolumns\":\"N\",\"equalize\":\"N\",\"options\":\"Y\"}],\"id\":\"body\","
                + "\"pid\":\"pagecontainer\",\"widgetcategory\":\"PORTION\",\"widgettype\":\"BODY\",\"appearance\":\"pri\","
                + "\"customwidthtype\":\"PX\",\"width\":\"100\"},{\"typeclass\":\"LAYOUTPORTION\",\"name\":\"sidebar\","
                + "\"type\":\"LAYOUTPORTION\",\"id\":\"sidebar\",\"pid\":\"pagecontainer\",\"widgetcategory\":\"PORTION\","
                + "\"widgettype\":\"SIDEBAR\"},{\"typeclass\":\"LAYOUTPORTION\",\"name\":\"footer\",\"type\":\"LAYOUTPORTION\","
                + "\"id\":\"footer\",\"pid\":\"pagecontainer\",\"widgetcategory\":\"PORTION\",\"widgettype\":\"FOOTER\"}],\"displayname\":\"D0\",\"source\":\"APPZILLON\"}";
        return templateContent;
    }

    public static String getDefaultLayoutDef(String scrId) {
        String scr = scrId + "_Scr";
        String loDef = "{\"scr\":\"" + scr + "\",\"customize\":\"N\",\"scrdisplayname\":\"" + scr + "\","
                + "\"id\":\"NewLayout\",\"lodisplayname\":\"NewLayout\",\"defaultTemplate\":\"D0\",\"designs\":[\"D0\"],"
                + "\"icons\":[\"\"],\"designDisplayNames\":[\"D0\"],\"scripts\":[]}";
        return loDef;

    }


    public static void getProductShortName(ObjectNode mainObj) {
        ObjectNode object = null;
        if (mainObj.has(DesignUtils.LIST_OF_OBJECT)) {
            ObjectNode listOfObject = (ObjectNode) mainObj.get(DesignUtils.LIST_OF_OBJECT);

            if (listOfObject.has(DesignUtils.TYPE_OBJECT)) {
                object = (ObjectNode) listOfObject.get(DesignUtils.TYPE_OBJECT);
            }
        }
        if (object != null) {
            JsonNode obj = object.get(OBJECT_DECORATION);
            if (obj != null) {
                updateProductsShortName(object, OBJECT_DECORATION);
            }
            if (Utils.isNull(Parser.prodShortName)) {
                obj = object.get(DesignUtils.ATTRIBUTE);
                if (obj != null) {
                    if (obj.isArray()) {
                        for (int a = 0; a < obj.size(); a++) {
                            if (!Utils.isNull(Parser.prodShortName)) {
                                break;
                            }
                            updateProductsShortName((ObjectNode) obj.get(a), DesignUtils.ATTRIBUTE_DECORATION);
                        }
                    }
                    if (obj.isObject()) {
                        updateProductsShortName((ObjectNode) obj, DesignUtils.ATTRIBUTE_DECORATION);
                    }
                }
            }
            if (Utils.isNull(Parser.prodShortName)) {
                obj = object.get(DesignUtils.RELATIONSHIP);
                if (obj != null) {
                    updateProductsShortName(object, DesignUtils.RELATIONSHIP_DECORATION);
                    if (Utils.isNull(Parser.prodShortName)) {
                        JsonNode relationsDom = obj.get(DesignUtils.RELATIONSHIP_DOMAIN);
                        if (relationsDom.isArray()) {
                            for (int a = 0; a < relationsDom.size(); a++) {
                                if (!Utils.isNull(Parser.prodShortName)) {
                                    break;
                                }
                                updateProductsShortName((ObjectNode) relationsDom.get(a), DesignUtils.RELATIONSHIP_DOMAIN_DECORATION);
                            }
                        } else if (relationsDom.isObject()) {
                            updateProductsShortName((ObjectNode) relationsDom, DesignUtils.RELATIONSHIP_DOMAIN_DECORATION);
                        }
                    }
                }
            }
        }
    }

    public static void updateProductsShortName(ObjectNode obj, String decoration) {
        JsonNode attrDecor = obj.get(decoration);
        if (attrDecor != null) {
            if (attrDecor.isArray()) {
                ArrayNode attrDecors = (ArrayNode) attrDecor;
                for (int d = 0; d < attrDecors.size(); d++) {
                    ObjectNode decor = (ObjectNode) attrDecors.get(d);
                    String decorationName = decor.get("Name").asText();
                    String lastName = decorationName.substring(decorationName.indexOf("_") + 1);
                    Parser.prodShortName = lastName.replace("_", " ").split(" ")[0];
                    LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + PRODUCT_SHORT_NAME + Parser.prodShortName);
                }
            } else {
                ObjectNode decor = (ObjectNode) attrDecor;
                String decorationName = decor.get("Name").asText();
                String lastName = decorationName.substring(decorationName.indexOf("_") + 1);
                Parser.prodShortName = lastName.replace("_", " ").split(" ")[0];
                LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + PRODUCT_SHORT_NAME + Parser.prodShortName);
            }
        }
    }

    public static void getPName(ObjectNode mainObj) {
        ObjectNode object = null;
        if (mainObj.has(DesignUtils.LIST_OF_OBJECT)) {
            ObjectNode listOfObject = (ObjectNode) mainObj.get(DesignUtils.LIST_OF_OBJECT);

            if (listOfObject.has(DesignUtils.TYPE_OBJECT)) {
                object = (ObjectNode) listOfObject.get(DesignUtils.TYPE_OBJECT);
            }
        }
        if (object != null) {
            JsonNode attrDecor = object.get(OBJECT_DECORATION);
            if (attrDecor != null) {
                if (attrDecor.isArray()) {
                    ArrayNode attrDecors = (ArrayNode) attrDecor;
                    for (int d = 0; d < attrDecors.size(); d++) {
                        if (!Utils.isNull(Parser.prodShortName)) {
                            break;
                        }
                        ObjectNode decor = (ObjectNode) attrDecors.get(d);
                        String decorationName = decor.get("Name").asText();
                        if (decorationName.endsWith("_Pname")) {
                            String decorValue = decor.get(VALUE).asText();
                            if (decorValue.contains("=")) {
                                decorValue = decorValue.split("=")[1].trim();
                            }
                            Parser.prodShortName = decorValue;
                            LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + PRODUCT_SHORT_NAME + Parser.prodShortName);
                        }
                    }
                } else {
                    ObjectNode decor = (ObjectNode) attrDecor;
                    String decorationName = decor.get("Name").asText();
                    if (decorationName.endsWith("_Pname")) {
                        String decorValue = decor.get(VALUE).asText();
                        if (decorValue.contains("=")) {
                            decorValue = decorValue.split("=")[1].trim();
                        }
                        Parser.prodShortName = decorValue;
                        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + PRODUCT_SHORT_NAME + Parser.prodShortName);
                    }
                }
            }
        }

    }
}
