package com.iexceed.appzillon.domain.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScr;
import com.iexceed.appzillon.domain.entity.TbAsmiCnvUIScrPK;
import com.iexceed.appzillon.domain.entity.TbAsmiIntfMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiIntfMasterPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiCnvUIScrRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiIntfMasterRepository;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.Response;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.citi.dataobj.*;
import com.iexceed.citi.pojo.*;
import com.iexceed.citi.utils.DesignUtils;
import com.iexceed.intf.json.IntfDefinition;
import com.iexceed.screenDef.json.*;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.utils.Constants.*;

@Named("RenderAppzillonDefinitionJson")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class RenderAppzillonDefinitionJson {

    public static final String TAG = "Tag";
    public static final String DATE = "Date";
    public static final String CHECKBOX1 = "Checkbox";
    public static final String DROP_DOWN = "DropDown";
    public static final String CONTEXT = "Context";
    public static final String POPOVER = "Popover";
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_PREFIX_CITI, RenderAppzillonDefinitionJson.class.toString());
    public static String appId;
    public DesignData objLayoutDesign = new DesignData();
    @Inject
    TbAsmiCnvUIScrRepository cAsmiCnvUIScrRepository;
    @Inject
    TbAsmiIntfMasterRepository iAsmiIntfMasterRepository;
    private Set<String> uniqueIDSet = new HashSet<>();
    private DesignUtils objDesignUtils = new DesignUtils();

    public static void main(String[] args) {
        try {
            Logger.propertiesPath = "";
            RenderAppzillonDefinitionJson.appId = "Admin";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] createScreenProps() {
        String[] props = new String[9];
        for (int i = 0; i <= 7; i++) {
            props[i] = "";
        }
        props[8] = "pri";
        return props;
    }

    private DesignData appCitiResponseJson(Citiresponse objcitiresponse) {
        LOG.debug("inside appCitiResponseJson");
        objLayoutDesign.type = "DESIGN";
        objLayoutDesign.name = objcitiresponse.getName();
        objLayoutDesign.typeClass = "DESIGN";
        prepareLayoutPortion(objcitiresponse);
        return objLayoutDesign;
    }

    private void prepareLayoutPortion(Citiresponse objcitiresponse) {
        LOG.debug("inside prepareLayoutPortion");
        PortionData objPortiondata = new PortionData();
        objPortiondata.name = "body";
        objPortiondata.id = "body";
        objPortiondata.pid = getRandomStringForId();
        objPortiondata.widgetcategory = "PORTION";
        objPortiondata.widgettype = "BODY";
        objPortiondata.bgattachment = "";
        objPortiondata.bgcolor = "";
        objPortiondata.bgimage = "";
        objPortiondata.bgposition = "PX";
        objPortiondata.bgrepeat = "PX";
        objPortiondata.bgsize = "PX";
        objPortiondata.typeClass = LAYOUTPORTION;
        objPortiondata.type = LAYOUTPORTION;
        objPortiondata.pid = "pagecontainer";
        objPortiondata.alignment = "";
        objPortiondata.appearance = "pri";
        objPortiondata.attatchment = "";
        objPortiondata.behaviour = "";
        objPortiondata.canceltitle = "";
        objPortiondata.cssclasses = objcitiresponse.getCssclasses() != null ? objcitiresponse.getCssclasses() : "";
        objPortiondata.customwidth = "";
        objPortiondata.customwidthtype = "PX";
        objPortiondata.dialogfooter = "";
        objPortiondata.draggablemodal = "";
        objPortiondata.footertype = "";
        objPortiondata.location = "";
        objPortiondata.margin = "";
        objPortiondata.modalfooter = "";
        objPortiondata.oktitle = "";
        objPortiondata.options = "";
        objPortiondata.pagevariation = "";
        objPortiondata.state = "";
        objPortiondata.variation = "";
        objPortiondata.width = "100";
        PortionData objProtion = generateRows(objPortiondata, objcitiresponse.getRows());
        List<PortionData> defalutLayoutList = addDefaultLayOut();
        objLayoutDesign.getChilds().addAll(defalutLayoutList);
        objLayoutDesign.getChilds().add(1, objProtion);
    }

    private PortionData generateRows(PortionData layoutPortion, List<Rows> citiRows) {
        LOG.debug("inside generateRows");
        if (citiRows != null) {
            for (int nCount = 0; nCount < citiRows.size(); nCount++) {
                int panelsCount = Integer.parseInt(citiRows.get(nCount).getNoofcolumns());
                for (int pCount = 0; pCount < panelsCount; pCount++) {
                    if (citiRows.get(nCount).getPanel() != null) {
                        ScrGridRowData gridRow = prepareRow(citiRows.get(nCount));
                        gridRow.pid = layoutPortion.id;
                        ScrGridColumnData gridColumn = buildGridColumn(pCount + 1);
                        gridColumn.pid = gridRow.id;
                        com.iexceed.citi.pojo.Panel citiPanel = citiRows.get(nCount).getPanel().get(pCount);
                        ScrPanelData panel = buildPanel(citiPanel);
                        panel.pid = gridColumn.id;
                        PanelSections citipanelsection = citiPanel.getPanelSections().get(pCount);
                        ScrPanelSectionData panelSection = buildPanelSection(citipanelsection);
                        panelSection.pid = panel.id;
                        panel.getChilds().add(panelSection);
                        if (citipanelsection.getContainers() != null) {
                            ArrayList<ScrContainerData> listcontainer = new ArrayList<>();
                            for (int cIndex = 0; cIndex < citipanelsection.getContainers().size(); cIndex++) {
                                Containers citiContainer = citipanelsection.getContainers().get(cIndex);
                                listcontainer = generateContainers(citiContainer, listcontainer, panelSection.id, "", "");
                            }
                            panelSection.getChilds().addAll(listcontainer);
                        }
                        gridColumn.getChilds().add(panel);
                        gridRow.getChilds().add(gridColumn);
                        layoutPortion.getChilds().add(gridRow);
                    } else if (citiRows.get(nCount).getRows() != null) {
                        List<Rows> innerRow = new ArrayList<>();
                        for (int index = 0; index < citiRows.get(nCount).getRows().size(); index++) {
                            if (citiRows.get(nCount).getType().equals(POPUP)) {
                                innerRow.add(citiRows.get(nCount).getRows().get(index));
                                ScrPopupData popupRow = generatePopup(layoutPortion, citiRows.get(nCount).getRows().get(index));
                                layoutPortion.getChilds().add(popupRow);
                            }
                        }

                    }
                }
            }
        }
        return layoutPortion;
    }

    private String prepareIntfDefinition(Containers citiContainer, String containerNodeName) {
        String multiRec = null;
        if (citiContainer.getNodeName() != null && citiContainer.getNodeName().length > 0) {

            for (int index = 0; index < citiContainer.getNodeName().length; index++) {
                if (objDesignUtils.nodeNameMap.get(citiContainer.getNodeName()[index]) == null) {
                    objDesignUtils.citiIntfNodeName = citiContainer.getNodeName()[index];
                    HashMap<String, String> nodeValues = new HashMap<>();
                    nodeValues.put(NODE_TYPE, citiContainer.getNodeType()[index]);
                    nodeValues.put(NODE_REL, citiContainer.getRelType()[index]);
                    nodeValues.put("nodeparent", citiContainer.getNodeParent()[index]);
                    nodeValues.put(NO_OF_NODE_ELEMS, "0");
                    nodeValues.put(N_EXT_NAME, citiContainer.getNodeName()[index]);

                    if (citiContainer.getRelType()[index].equals("1:1")) {
                        nodeValues.put(N_MULTI_REC, "N");

                    } else if (citiContainer.getRelType()[index].equals("1:N")) {
                        nodeValues.put(N_MULTI_REC, "Y");
                        multiRec = citiContainer.getNodeName()[index];
                        objDesignUtils.multiRec.append(citiContainer.getRelType()[index] + "" + citiContainer.getNodeName()[index]);
                    }
                    if (containerNodeName.equals("")) {
                        if (citiContainer.getNodeType()[index].equals(REQUEST)) {
                            nodeValues.put(N_PARENT, appId + "__" + objLayoutDesign.name + INT_F + "_Req");
                            nodeValues.put(N_PARENTS, appId + "__" + objLayoutDesign.name + INT_F + "_Req");
                            nodeValues.put(INT_F_NODE, appId + "__" + objLayoutDesign.name + INT_F + STRING_I + citiContainer.getNodeName()[index]);

                        }
                        if (citiContainer.getNodeType()[index].equals(RESPONSE)) {
                            nodeValues.put(N_PARENT, appId + "__" + objLayoutDesign.name + INT_F + "_Res");
                            nodeValues.put(N_PARENTS, appId + "__" + objLayoutDesign.name + INT_F + "_Res");
                            nodeValues.put(INT_F_NODE, appId + "__" + objLayoutDesign.name + INT_F + STRING_O + citiContainer.getNodeName()[index]);
                        }
                    } else {
                        if (citiContainer.getNodeType()[index].equals(REQUEST)) {
                            nodeValues.put(N_PARENT, containerNodeName);
                            if (!containerNodeName.isEmpty()) {
                                objDesignUtils.parentsReqContainerNodeName.append("~").append(containerNodeName);
                                nodeValues.put(N_PARENTS, appId + "__" + objLayoutDesign.name + INT_F + "_Req" + objDesignUtils.parentsReqContainerNodeName.toString());
                            }
                            nodeValues.put(INT_F_NODE, appId + "__" + objLayoutDesign.name + INT_F + STRING_I + citiContainer.getNodeName()[index]);
                        }
                        if (citiContainer.getNodeType()[index].equals(RESPONSE)) {
                            nodeValues.put(N_PARENT, containerNodeName);
                            if (!containerNodeName.isEmpty()) {
                                objDesignUtils.parentsResContainerNodeName.append("~").append(containerNodeName);
                                nodeValues.put(N_PARENTS, appId + "__" + objLayoutDesign.name + INT_F + "_Res" + objDesignUtils.parentsResContainerNodeName.toString());
                            }
                            nodeValues.put(INT_F_NODE, appId + "__" + objLayoutDesign.name + INT_F + STRING_O + citiContainer.getNodeName()[index]);
                        }
                    }
                    if (containerNodeName.equals("")) {
                        if (citiContainer.getRelType()[index].equals("1:N") && citiContainer.getRelType()[index].equals("1:1")) {
                            nodeValues.put(N_MR_PARENT, citiContainer.getNodeName()[index]);
                        }
                    } else {
                        if (citiContainer.getRelType()[index].equals("1:1")) {
                            HashMap<String, String> parentMap = objDesignUtils.nodeNameMap.get(containerNodeName);
                            nodeValues.put(N_MR_PARENT, parentMap.get(N_MR_PARENT));
                        } else if (citiContainer.getRelType()[index].equals("1:N")) {
                            nodeValues.put(N_MR_PARENT, citiContainer.getNodeName()[index]);
                        }
                    }
                    objDesignUtils.nodeNameMap.put(citiContainer.getNodeName()[index], nodeValues);
                }
            }

        }
        return multiRec;
    }

    private ScrPopupData generatePopup(PortionData layoutPortion, Rows citiPopuprows) {
        ScrPopupData popupRow = preparePopup(citiPopuprows);
        popupRow.pid = layoutPortion.id;
        int panelsCount = Integer.parseInt(citiPopuprows.getNoofcolumns());
        for (int pCount = 0; pCount < panelsCount; pCount++) {
            ScrGridRowData gridRow = prepareRow(citiPopuprows);
            gridRow.pid = popupRow.id;
            ScrGridColumnData gridColumn = buildGridColumn(pCount);
            gridColumn.pid = gridRow.id;
            if (citiPopuprows.getPanel() != null) {
                com.iexceed.citi.pojo.Panel citiPanel = citiPopuprows.getPanel().get(pCount);
                ScrPanelData panel = buildPanel(citiPanel);
                panel.pid = gridColumn.id;
                gridRow.getChilds().add(gridColumn);
                PanelSections citipanelsection = citiPanel.getPanelSections().get(pCount);
                ScrPanelSectionData panelSection = buildPanelSection(citipanelsection);
                panelSection.pid = panel.id;
                panel.getChilds().add(panelSection);
                if (citipanelsection.getContainers() != null) {
                    ArrayList<ScrContainerData> listcontainer = new ArrayList<>();
                    for (int cIndex = 0; cIndex < citipanelsection.getContainers().size(); cIndex++) {
                        Containers citiContainer = citipanelsection.getContainers().get(cIndex);
                        listcontainer = generateContainers(citiContainer, listcontainer, panelSection.id, "", "");
                    }
                    panelSection.getChilds().addAll(listcontainer);
                }
                gridColumn.getChilds().add(panel);
            }
            popupRow.getChilds().add(gridRow);
        }
        return popupRow;
    }

    private ScrGridRowData prepareRow(Rows rows) {
        ScrGridRowData gridRow = new ScrGridRowData();
        gridRow.name = rows.getName();
        gridRow.id = getRandomStringForId();
        gridRow.options = "Y";
        gridRow.cssclasses = rows.getCssclasses() != null ? rows.getCssclasses() : "";
        gridRow.centercolumns = "N";
        gridRow.equalize = "N";
        gridRow.widgetcategory = "GRID";
        gridRow.widgettype = "ROW";
        gridRow.typeClass = "GRIDROW";
        gridRow.type = "GRIDROW";
        gridRow.controlid = "";
        gridRow.alignment = "";
        gridRow.dialogtype = "";
        gridRow.draggable = "";
        gridRow.draggableid = "";
        gridRow.draggableids = "";
        gridRow.draggablemodal = "";
        gridRow.droppable = "";
        gridRow.titlestyle = "";
        gridRow.variation = "";
        return gridRow;
    }

    private ScrPopupData preparePopup(Rows rows) {
        ScrPopupData popupRow = new ScrPopupData();
        popupRow.name = rows.getName();
        popupRow.id = getRandomStringForId();
        popupRow.options = rows.getVisible();
        popupRow.widgetcategory = POPUP;
        popupRow.widgettype = "MODAL";
        EventData events = buildEvents(ONLOAD, null);
        ArrayList<EventData> listEvents = new ArrayList<>();
        listEvents.add(events);
        popupRow.events = listEvents;
        popupRow.typeClass = POPUP;
        popupRow.name = "modal";
        popupRow.type = POPUP;
        popupRow.canceltitle = "";
        popupRow.customwidth = "";
        popupRow.customwidthtype = "PX";
        popupRow.cssclasses = rows.getCssclasses() != null ? rows.getCssclasses() : "";
        popupRow.desktopwidth = "";
        popupRow.dialogfooter = "";
        popupRow.draggablemodal = "N";
        popupRow.footertype = "";
        popupRow.modalfooter = "";
        popupRow.oktitle = "";
        popupRow.phonewidth = "";
        popupRow.state = "";
        popupRow.tabletwidth = "";
        popupRow.title = "Modal";
        popupRow.variation = "";
        popupRow.width = "12";
        popupRow.wswidth = "";
        return popupRow;
    }

    private ArrayList<ScrContainerData> generateContainers(Containers citiContainer, ArrayList<ScrContainerData> listcontainer, String panelsectionId, String containerName, String continerNodeName) {
        ScrContainerData container = buildContainer(citiContainer, containerName, continerNodeName);
        container.pid = panelsectionId;
        int rowOfContainer = citiContainer.getRows().size();
        for (int rCount = 0; rCount < rowOfContainer; rCount++) {
            Rows rows = citiContainer.getRows().get(rCount);
            ScrSectionRowData sectionRow = buildSectionRow(rows, rCount + 1);
            sectionRow.pid = container.id;
            int rowColumns = rows.getColumns().size();
            for (int rcCount = 0; rcCount < rowColumns; rcCount++) {
                ScrSectionColumnData sectionColumn = buildSectionColumn(rows.getColumns().get(rcCount), rcCount + 1);
                if (rows.getColumns().get(rcCount).getWidth() != null) {
                    sectionColumn.width = rows.getColumns().get(rcCount).getWidth();
                }
                ScrElementData presentationElement = null;
                sectionColumn.pid = sectionRow.id;
                if (rows.getColumns().get(rcCount).getElements() != null) {
                    List<Elements> citiElements = rows.getColumns().get(rcCount).getElements();
                    int columnElements = citiElements.size();
                    for (int elementCount = 0; elementCount < columnElements; elementCount++) {
                        presentationElement = buildPresentationElement(citiElements.get(elementCount), container);
                        presentationElement.pid = sectionColumn.id;
                        if ((presentationElement.nodename != null && !presentationElement.nodename.isEmpty()) && (objDesignUtils.nChildsList.indexOf(presentationElement.nodename) < 0 && !presentationElement.nodename.isEmpty())) {
                            objDesignUtils.nChildsList.add(presentationElement.nodename);
                            objDesignUtils.ifaceCount.add(1);
                            objDesignUtils.noOfNodesList.add(1);
                            objDesignUtils.ifaceNodenames.add(appId + "__" + objLayoutDesign.name + STRING_SCR + container.name);
                        }
                        sectionColumn.getChilds().add(presentationElement);
                    }
                }
                sectionRow.getChilds().add(sectionColumn);
                if (rows.getColumns().get(rcCount).getContainers() != null) {
                    int columnsContainerSize = rows.getColumns().get(rcCount).getContainers().size();
                    for (int ccCount = 0; ccCount < columnsContainerSize; ccCount++) {
                        Containers innerCitiConatiner = rows.getColumns().get(rcCount).getContainers().get(ccCount);
                        objDesignUtils.ContainerChilds.add(appId + "__" + objLayoutDesign.name + STRING_SCR + innerCitiConatiner.getName());
                        generateContainers(innerCitiConatiner, listcontainer, panelsectionId, container.name, objDesignUtils.citiIntfNodeName);
                    }
                    if (containerName.equals("")) {
                        objDesignUtils.ContainerChilds.add("");
                    }
                }
                if (rows.getColumns().get(rcCount).getRows() != null) {
                    generateSectionRows(sectionColumn, container, rows.getColumns().get(rcCount).getRows(), citiContainer, containerName, continerNodeName, listcontainer);
                }

            }
            container.getChilds().add(sectionRow);
        }
        if (citiContainer.getNodeName() != null) {
            String[] childNode = citiContainer.getNodeName();
            StringBuilder buidlernodes = new StringBuilder();
            for (String nodes : childNode) {
                if (!objDesignUtils.nodeNameMap.get(nodes).containsKey(N_CHILDS)) {
                    objDesignUtils.nodeNameMap.get(nodes).put(N_CHILDS, "");
                }
                buidlernodes.append(nodes);
            }
            if (!continerNodeName.isEmpty()) {
                objDesignUtils.nodeNameMap.get(continerNodeName).put(N_CHILDS, buidlernodes.toString());
            }
        }

        listcontainer.add(container);

        return listcontainer;
    }

    private void generateSectionRows(ScrSectionColumnData sectionRowsColumn, ScrContainerData container, List<Rows> citirows, Containers citiContainer, String containerName, String continerNodeName, ArrayList<ScrContainerData> listcontainer) {
        int rowOfContainer = citirows.size();
        for (int rCount = 0; rCount < rowOfContainer; rCount++) {
            Rows rows = citirows.get(rCount);
            ScrSectionRowData sectionRow = buildSectionRow(rows, rCount + 1);
            int rowColumns = rows.getColumns().size();
            for (int rcCount = 0; rcCount < rowColumns; rcCount++) {
                ScrSectionColumnData sectionColumn = buildSectionColumn(rows.getColumns().get(rcCount), rcCount + 1);
                if (rows.getColumns().get(rcCount).getWidth() != null) {
                    sectionColumn.width = rows.getColumns().get(rcCount).getWidth();
                }
                ScrElementData presentationElement = null;
                sectionColumn.pid = sectionRow.id;
                if (rows.getColumns().get(rcCount).getElements() != null) {
                    List<Elements> citiElements = rows.getColumns().get(rcCount).getElements();
                    int columnElements = citiElements.size();
                    for (int elementCount = 0; elementCount < columnElements; elementCount++) {
                        presentationElement = buildPresentationElement(citiElements.get(elementCount), container);
                        presentationElement.pid = sectionColumn.id;
                        if ((presentationElement.nodename != null && !presentationElement.nodename.isEmpty()) && (objDesignUtils.nChildsList.indexOf(presentationElement.nodename) < 0 && !presentationElement.nodename.isEmpty())) {
                            objDesignUtils.nChildsList.add(presentationElement.nodename);
                            objDesignUtils.ifaceCount.add(1);
                            objDesignUtils.noOfNodesList.add(1);
                            objDesignUtils.ifaceNodenames.add(appId + "__" + objLayoutDesign.name + STRING_SCR + container.name);
                        }
                        sectionColumn.getChilds().add(presentationElement);
                    }
                }
                sectionRow.getChilds().add(sectionColumn);
                if (rows.getColumns().get(rcCount).getContainers() != null) {
                    int columnsContainerSize = rows.getColumns().get(rcCount).getContainers().size();
                    for (int ccCount = 0; ccCount < columnsContainerSize; ccCount++) {
                        Containers innerCitiConatiner = rows.getColumns().get(rcCount).getContainers().get(ccCount);
                        objDesignUtils.ContainerChilds.add(appId + "__" + objLayoutDesign.name + STRING_SCR + innerCitiConatiner.getName());
                        generateContainers(innerCitiConatiner, listcontainer, container.pid, container.name, objDesignUtils.citiIntfNodeName);
                    }
                    if (containerName.equals("")) {
                        objDesignUtils.ContainerChilds.add("");
                    }
                }
                if (rows.getColumns().get(rcCount).getRows() != null) {

                    generateSectionRows(sectionRowsColumn, container, rows.getColumns().get(rcCount).getRows(), citiContainer, containerName, continerNodeName, listcontainer);
                }

            }
            sectionRowsColumn.getChilds().add(sectionRow);
        }
    }

    public String getRandomStringForId() {
        int length = 24;
        StringBuilder result = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        String alphanumeric = DesignUtils.ALPHA_NUM;
        int size = alphanumeric.length();
        for (int count = 0; count < length; count++) {
            result = result.append(alphanumeric.charAt(secureRandom.nextInt(size)));
        }
        if (uniqueIDSet.contains(result.toString())) {
            result = new StringBuilder(getRandomStringForId());
        } else {
            uniqueIDSet.add(result.toString());
        }
        return result.toString();
    }

    private List<PortionData> addDefaultLayOut() {
        List<PortionData> layoutsList = new ArrayList<>();
        String[] layoutIds = {"header", "sidebar", "footer"};
        for (int count = 0; count < layoutIds.length; count++) {
            PortionData objPortiondata = new PortionData();
            objPortiondata.name = layoutIds[count];
            objPortiondata.id = layoutIds[count];
            objPortiondata.pid = getRandomStringForId();
            objPortiondata.widgetcategory = "PORTION";
            objPortiondata.widgettype = layoutIds[count].toUpperCase();
            objPortiondata.bgattachment = "";
            objPortiondata.bgcolor = "";
            objPortiondata.bgimage = "";
            objPortiondata.bgposition = "PX";
            objPortiondata.bgrepeat = "PX";
            objPortiondata.bgsize = "PX";
            objPortiondata.typeClass = LAYOUTPORTION;
            objPortiondata.type = LAYOUTPORTION;
            objPortiondata.pid = "pagecontainer";
            objPortiondata.alignment = "";
            objPortiondata.appearance = "pri";
            objPortiondata.attatchment = "";
            objPortiondata.behaviour = "";
            objPortiondata.canceltitle = "";
            objPortiondata.cssclasses = "";
            objPortiondata.customwidth = "";
            objPortiondata.customwidthtype = "PX";
            objPortiondata.dialogfooter = "";
            objPortiondata.draggablemodal = "";
            objPortiondata.footertype = "";
            objPortiondata.location = "";
            objPortiondata.margin = "";
            objPortiondata.modalfooter = "";
            objPortiondata.oktitle = "";
            objPortiondata.options = "";
            objPortiondata.pagevariation = "";
            objPortiondata.state = "";
            objPortiondata.variation = "";
            objPortiondata.width = "100";
            layoutsList.add(objPortiondata);
        }
        return layoutsList;
    }

    private ScrSectionColumnData buildSectionColumn(Columns citiColumns, int rcCount) {
        ScrSectionColumnData sectionColumn = new ScrSectionColumnData();
        sectionColumn.name = "sc_col" + rcCount;
        sectionColumn.id = getRandomStringForId();
        sectionColumn.widgetcategory = "SECTION";
        sectionColumn.widgettype = "COLUMN";
        sectionColumn.options = "Y";
        sectionColumn.appearance = "pri";
        sectionColumn.fieldset = "N";
        sectionColumn.horizontalalignment = "LEFT";
        sectionColumn.horizontalgrouping = "N";
        sectionColumn.customwidthtype = "PX";
        sectionColumn.typeClass = "SECTIONCOLUMN";
        sectionColumn.type = "SECTIONCOLUMN";
        sectionColumn.controlid = "";
        sectionColumn.cssclasses = citiColumns.getCssclasses() != null ? citiColumns.getCssclasses() : "";
        sectionColumn.customwidth = "";
        sectionColumn.desktopwidth = "";
        sectionColumn.draggable = "";
        sectionColumn.draggableid = "";
        sectionColumn.droppable = "";
        sectionColumn.draggableids = "";
        sectionColumn.fieldsettitle = "";
        sectionColumn.labelalignment = "";
        sectionColumn.listcolumnwidth = "";
        sectionColumn.maxwidth = "";
        sectionColumn.minwidth = "";
        sectionColumn.phonewidth = "";
        sectionColumn.semantics = "";
        sectionColumn.tabletwidth = "";
        sectionColumn.verticalalignment = "";
        sectionColumn.variation = "";
        sectionColumn.width = citiColumns.getWidth() != null ? citiColumns.getWidth() : "";
        sectionColumn.wswidth = "";
        return sectionColumn;
    }

    private ScrElementData buildPresentationElement(Elements citielements, ScrContainerData container) {
        LOG.debug("inside buildPresentationElement");
        ScrElementData presentationElement = new ScrElementData();
        LOG.debug("cotainer name ::{} ", container.name);
        presentationElement.custom = "N";
        presentationElement.customheighttype = "PX";
        presentationElement.customwidthtype = "PX";
        presentationElement.closeonselect = "Y";
        presentationElement.width = citielements.getControlWidth();
        if (citielements.getControlWidth() != null && citielements.getControlWidth().equals("CUSTOM")) {
            presentationElement.customwidth = citielements.getControlCustomWidth();
            presentationElement.customheight = citielements.getControlCustomHeight();
        }
        presentationElement.type = "PRESENTATIONELEMENT";
        presentationElement.iconposition = citielements.getControlIconPosition() != null ? citielements.getControlIconPosition() : "";
        presentationElement.password = citielements.getControlPassword() != null ? citielements.getControlPassword() : "";
        presentationElement.tooltip = citielements.getControlTooltip() != null ? citielements.getControlTooltip() : "";
        presentationElement.displayasliteral = citielements.getDisplayAsLiteral() != null ? citielements.getDisplayAsLiteral() : "N";
        presentationElement.datetype = !Utils.isNull(citielements.getControlDateType()) ? citielements.getControlDateType() : "JQUERY";
        if (citielements.getControlGauges() != null) {
            ArrayList<GaugeRange> gaugeranges = new ArrayList<>();
            for (int index = 0; index < citielements.getControlGauges().size(); index++) {
                ControlGauges citiGauge = citielements.getControlGauges().get(index);
                GaugeRange objGaugeranges = new GaugeRange();
                objGaugeranges.min = citiGauge.getGaugeMin();
                objGaugeranges.max = citiGauge.getGaugeMax();
                objGaugeranges.color = citiGauge.getGaugeColor();
                gaugeranges.add(objGaugeranges);
            }
            presentationElement.gaugeranges = gaugeranges;
        }
        prepareUiLints(citielements);
        prepareControlType(citielements, presentationElement);
        presentationElement.size = citielements.getControlSize() != null ? citielements.getControlSize() : "";
        presentationElement.appearance = citielements.getControlAppearance() != null ? citielements.getControlAppearance() : "";
        presentationElement.fontsize = citielements.getControlFontSize() != null ? citielements.getControlFontSize() : "";
        presentationElement.donotformat = citielements.getDoNotFormat() != null ? citielements.getDoNotFormat() : "Y";
        presentationElement.datatype = citielements.getControlDataType();
        if (presentationElement.datatype.equals("DATE") || presentationElement.datatype.equals("DATETIME")) {
            presentationElement.display = "BUBBLE";
        }
        presentationElement.defaultimage = citielements.getImageName();
        presentationElement.defaultvalue = citielements.getControlDefaultValue();

        presentationElement.maxstringlength = citielements.getControlMaxLength() != null ? citielements.getControlMaxLength() : "";

        String[] controlevents = citielements.getControlAction().split(",");
        String[] methods = citielements.getControlMethod().split(",");
        ArrayList<EventData> listEvents = new ArrayList<>();
        for (int count = 0; count < controlevents.length; count++) {
            EventData events = buildEvents(controlevents[count], methods[count]);
            listEvents.add(events);
        }
        presentationElement.events = listEvents;
        presentationElement.icon = citielements.getiCON();
        presentationElement.iconposition = "LEFT";
        presentationElement.iconsize = "PX24";
        presentationElement.id = getRandomStringForId();
        presentationElement.labelwidth = citielements.getControlLabelWidth();
        presentationElement.mandatory = "N";
        presentationElement.cssclasses = citielements.getCssclasses() != null ? citielements.getCssclasses() : "";
        presentationElement.placeholder = citielements.getControlPlaceHolder() != null ? citielements.getControlPlaceHolder() : "";
        presentationElement.menu = citielements.getControlMenu() != null ? citielements.getControlMenu() : "";
        presentationElement.popoverid = citielements.getControlPopoverId();
        if (citielements.getControlTextType() != null) {
            String value = "PARAGRAPHTEXT";
            if (citielements.getControlTextType().equalsIgnoreCase("H1")) {
                value = "HEADING1";
            } else if (citielements.getControlTextType().equalsIgnoreCase("H2")) {
                value = "HEADING2";
            } else if (citielements.getControlTextType().equalsIgnoreCase("H3")) {
                value = "HEADING3";
            } else if (citielements.getControlTextType().equalsIgnoreCase("H4")) {
                value = "HEADING4";
            } else if (citielements.getControlTextType().equalsIgnoreCase("H5")) {
                value = "HEADING5";
            } else if (citielements.getControlTextType().equalsIgnoreCase("H6")) {
                value = "HEADING6";
            } else if (citielements.getControlTextType().equalsIgnoreCase("H7")) {
                value = "HEADING7";
            }
            presentationElement.elementtype = value;
        }
        presentationElement.name = citielements.getControlName();
        if (citielements.getControlVisible().equals("NO")) {
            presentationElement.options = "N";
        } else {
            presentationElement.options = "Y";
        }
        presentationElement.popoverposition = "AUTO";
        presentationElement.state = citielements.getControlState();


        presentationElement.tablecellwidthtype = "PX";
        presentationElement.translatedefaultvalue = "N";
        presentationElement.widgetcategory = "ELEMENT";
        presentationElement.widgettype = citielements.getControlType();
        if (citielements.getControlType() != null && citielements.getControlType().equals(DesignUtils.DROP_DOWN)) {
            List<ControlOptions> citiControlOptions = citielements.getControloptions();
            ArrayList<ElementStaticOptions> staticOptionsList = new ArrayList<>();
            for (ControlOptions options : citiControlOptions) {
                ElementStaticOptions staticOpitons = new ElementStaticOptions();
                staticOpitons.description = options.getDesc();
                staticOpitons.value = options.getValue();
                staticOptionsList.add(staticOpitons);
            }
            presentationElement.staticoptions = staticOptionsList;
        }
        if (citielements.getControlNode() != null && !citielements.getControlNode().isEmpty()) {
            objDesignUtils.nodeElementList.add(citielements);
            objDesignUtils.presentationEui.add("N");
            HashMap<String, String> nodeTypeMap = objDesignUtils.nodeNameMap.get(citielements.getControlNode());
            presentationElement.elementname = citielements.getControlName();
            presentationElement.nodename = citielements.getControlNode();
            presentationElement.interfacename = objLayoutDesign.name + INT_F;
            if (nodeTypeMap.get(NODE_TYPE).equals(REQUEST)) {
                objDesignUtils.nodeNamesList.add(appId + "__" + objLayoutDesign.name + INT_F + STRING_I + citielements.getControlNode() + "__" + presentationElement.elementname);
                if (objDesignUtils.mappedList.containsKey(container.name)) {
                    objDesignUtils.mappedList.get(container.name).add(appId + "__" + objLayoutDesign.name + INT_F + STRING_I + citielements.getControlNode() + "__" + presentationElement.elementname);
                    objDesignUtils.mappedList.get(container.name + E_LM_TYPE).add(citielements.getControlType());

                    if (citielements.getControlState().equals(ENABLED)) {
                        objDesignUtils.mappedList.get(container.name + "eRo").add("Y");
                    } else {
                        objDesignUtils.mappedList.get(container.name + "eRo").add("N");
                    }
                    if (citielements.getControlEmail() != null && citielements.getControlEmail().equals("Y")) {
                        objDesignUtils.mappedList.get(container.name + E_EMAIL).add("Y");
                        presentationElement.maxstringlength = "100";
                        presentationElement.email = "Y";
                    } else {
                        objDesignUtils.mappedList.get(container.name + E_EMAIL).add("N");
                    }

                } else {
                    ArrayList<String> node = new ArrayList<>();
                    node.add(appId + "__" + objLayoutDesign.name + INT_F + STRING_I + citielements.getControlNode() + "__" + presentationElement.elementname);
                    ArrayList<String> elmTypenode = new ArrayList<>();
                    elmTypenode.add(citielements.getControlType());
                    objDesignUtils.mappedList.put(container.name, node);
                    objDesignUtils.mappedList.put(container.name + E_LM_TYPE, elmTypenode);
                    ArrayList<String> eRo = new ArrayList<>();

                    if (citielements.getControlState().equals(ENABLED)) {
                        eRo.add("Y");
                    } else {
                        eRo.add("N");
                    }
                    ArrayList<String> elmEmail = new ArrayList<>();
                    if (citielements.getControlEmail() != null && citielements.getControlEmail().equals("Y")) {
                        elmEmail.add("Y");
                        presentationElement.maxstringlength = "100";
                        presentationElement.email = "Y";
                    } else {
                        elmEmail.add("N");
                    }
                    objDesignUtils.mappedList.put(container.name + E_EMAIL, elmEmail);
                    objDesignUtils.mappedList.put(container.name + "eRo", eRo);
                }
            }
            if (nodeTypeMap.get(NODE_TYPE).equals(RESPONSE)) {
                objDesignUtils.nodeNamesList.add(appId + "__" + objLayoutDesign.name + INT_F + STRING_O + citielements.getControlNode() + "__" + presentationElement.elementname);
                if (objDesignUtils.mappedList.containsKey(container.name)) {
                    objDesignUtils.mappedList.get(container.name).add(appId + "__" + objLayoutDesign.name + INT_F + STRING_O + citielements.getControlNode() + "__" + presentationElement.elementname);
                    objDesignUtils.mappedList.get(container.name + E_LM_TYPE).add(citielements.getControlType());

                    if (citielements.getControlState().equals(ENABLED)) {
                        objDesignUtils.mappedList.get(container.name + "eRo").add("Y");
                    } else {
                        objDesignUtils.mappedList.get(container.name + "eRo").add("N");
                    }
                    if (citielements.getControlEmail() != null && citielements.getControlEmail().equals("Y")) {
                        objDesignUtils.mappedList.get(container.name + E_EMAIL).add("Y");
                        presentationElement.maxstringlength = "100";
                        presentationElement.email = "Y";
                    } else {
                        objDesignUtils.mappedList.get(container.name + E_EMAIL).add("N");
                    }
                } else {
                    ArrayList<String> node = new ArrayList<>();
                    node.add(appId + "__" + objLayoutDesign.name + INT_F + STRING_O + citielements.getControlNode() + "__" + presentationElement.elementname);
                    objDesignUtils.mappedList.put(container.name, node);
                    ArrayList<String> elmTypenode = new ArrayList<>();
                    elmTypenode.add(citielements.getControlType());
                    objDesignUtils.mappedList.put(container.name + E_LM_TYPE, elmTypenode);
                    ArrayList<String> eRo = new ArrayList<>();
                    if (citielements.getControlState().equals(ENABLED)) {
                        eRo.add("Y");
                    } else {
                        eRo.add("N");
                    }
                    ArrayList<String> elmEmail = new ArrayList<>();
                    if (citielements.getControlEmail() != null && citielements.getControlEmail().equals("Y")) {
                        elmEmail.add("Y");
                        presentationElement.maxstringlength = "100";
                        presentationElement.email = "Y";
                    } else {
                        elmEmail.add("N");
                    }
                    objDesignUtils.mappedList.put(container.name + E_EMAIL, elmEmail);
                    objDesignUtils.mappedList.put(container.name + "eRo", eRo);
                }
            }
            presentationElement.datamodeltype = nodeTypeMap.get(NODE_TYPE).toUpperCase() + "DATAMODEL";

            int elmCount = Integer.parseInt(objDesignUtils.nodeNameMap.get(citielements.getControlNode()).get(NO_OF_NODE_ELEMS));
            elmCount++;
            objDesignUtils.nodeNameMap.get(citielements.getControlNode()).put(NO_OF_NODE_ELEMS, String.valueOf(elmCount));
        } else {
            objDesignUtils.presentationEui.add("Y");
            if (objDesignUtils.mappedList.containsKey(container.name)) {
                objDesignUtils.mappedList.get(container.name).add(appId + "__" + objLayoutDesign.name + STRING_SCR + citielements.getControlName());
                objDesignUtils.mappedList.get(container.name + E_LM_TYPE).add(citielements.getControlType());
                if (citielements.getControlState().equals(ENABLED)) {
                    objDesignUtils.mappedList.get(container.name + "eRo").add("Y");
                } else {
                    objDesignUtils.mappedList.get(container.name + "eRo").add("N");
                }
                if (citielements.getControlEmail() != null && citielements.getControlEmail().equals("Y")) {
                    objDesignUtils.mappedList.get(container.name + E_EMAIL).add("Y");
                    presentationElement.maxstringlength = "100";
                    presentationElement.email = "Y";
                } else {
                    objDesignUtils.mappedList.get(container.name + E_EMAIL).add("N");
                }
            } else {
                ArrayList<String> node = new ArrayList<>();
                node.add(appId + "__" + objLayoutDesign.name + STRING_SCR + citielements.getControlName());
                objDesignUtils.mappedList.put(container.name, node);
                ArrayList<String> elmTypenode = new ArrayList<>();
                elmTypenode.add(citielements.getControlType());
                objDesignUtils.mappedList.put(container.name + E_LM_TYPE, elmTypenode);
                ArrayList<String> eRo = new ArrayList<>();
                if (citielements.getControlState().equals(ENABLED)) {
                    eRo.add("Y");
                } else {
                    eRo.add("N");
                }
                objDesignUtils.mappedList.put(container.name + "eRo", eRo);
                ArrayList<String> elmEmail = new ArrayList<>();
                if (citielements.getControlEmail() != null && citielements.getControlEmail().equals("Y")) {
                    elmEmail.add("Y");
                    presentationElement.maxstringlength = "100";
                } else {
                    elmEmail.add("N");
                }
                objDesignUtils.mappedList.put(container.name + E_EMAIL, elmEmail);
            }
        }
        objDesignUtils.ContainerElements.add(container.name);
        objDesignUtils.nPresentationElement.add(citielements);

        presentationElement.elementname = citielements.getControlName();
        if (citielements.getControlLabelWidth() != null) {
            presentationElement.width = citielements.getControlLabelWidth();
        }
        objDesignUtils.presentationElementName.add(presentationElement.name);
        objDesignUtils.presentationCustom.add(presentationElement.custom);
        objDesignUtils.presentaitonElementEMask.add("");
        objDesignUtils.presentaitonElementERtformat.add("lcol");
        objDesignUtils.presentaitonElementSkipFormat.add(presentationElement.donotformat);
        objDesignUtils.presentaitonElementeLiteral.add(presentationElement.displayasliteral);
        objDesignUtils.presentaitonElementESkipformat.add(appId + "__");
        objDesignUtils.noOfeLovRfCount.add(0);
        return presentationElement;
    }

    private void prepareControlType(Elements citielements, ScrElementData presentationElement) {
        if (citielements.getControlType().equals("CHECKBOX")) {
            List<ControlOptions> objControlOptionsList = citielements.getControloptions();
            if (objControlOptionsList != null) {
                for (ControlOptions options : objControlOptionsList) {
                    ElementStaticOptions elmStatOpts = new ElementStaticOptions();
                    elmStatOpts.description = options.getDesc();
                    elmStatOpts.value = options.getValue();
                    presentationElement.staticoptions.add(elmStatOpts);
                }
            }

        } else if (citielements.getControlType().equals("RADIO")) {
            List<ControlOptions> objControlOptionsList = citielements.getControloptions();
            if (objControlOptionsList != null) {
                for (ControlOptions options : objControlOptionsList) {
                    ElementStaticOptions option = new ElementStaticOptions(options.getValue(), options.getDesc(), ENABLED, "", "");
                    presentationElement.staticoptions.add(option);
                }
            } else {
                ElementStaticOptions option1 = new ElementStaticOptions("One", "Option1", ENABLED, "", "");
                presentationElement.staticoptions.add(option1);
                ElementStaticOptions option2 = new ElementStaticOptions("Two", "Option2", ENABLED, "", "");
                presentationElement.staticoptions.add(option2);
            }

        } else if (citielements.getControlType().equals("TOGGLESWITCH")) {

            List<ControlOptions> objControlOptionsList = citielements.getControloptions();
            if (objControlOptionsList != null) {
                for (ControlOptions options : objControlOptionsList) {
                    ElementStaticOptions option = new ElementStaticOptions(options.getValue(), "", "", "", "");
                    presentationElement.staticoptions.add(option);
                }
                if (objControlOptionsList.size() == 2) {
                    presentationElement.switchoption1 = objControlOptionsList.get(0).getDesc();
                    presentationElement.switchoption2 = objControlOptionsList.get(1).getDesc();
                }
            } else {
                ElementStaticOptions toggleOption1 = new ElementStaticOptions("on", "", "", "", "");
                presentationElement.staticoptions.add(toggleOption1);
                ElementStaticOptions toggleOption2 = new ElementStaticOptions("off", "", "", "", "");
                presentationElement.staticoptions.add(toggleOption2);
                presentationElement.switchoption1 = "Label1";
                presentationElement.switchoption2 = "Label2";
            }
        } else if (citielements.getControlType().equals("TEXTAREA")) {
            presentationElement.maxstringlength = "250";
            if (citielements.getControlWidth() != null && !citielements.getControlWidth().isEmpty()) {
                presentationElement.width = citielements.getControlWidth();
            } else {
                presentationElement.width = "100";
            }
            //			presentationElement.customheight=
        }


    }

    private void prepareUiLints(Elements citicontrol) {
        if (((citicontrol.getControlType().equals("INPUTWITHBUTTON")) || (citicontrol.getControlType().equals("INPUTBOX"))) && ((citicontrol.getControlDataType().equals("DATE")) || (citicontrol.getControlDataType().equals("DATETIME")))) {
            ArrayList<String> presentaionEleDate = new ArrayList<>();
            presentaionEleDate.add(appId + "__" + objLayoutDesign.name + "__" + citicontrol.getControlName());
            presentaionEleDate.add(citicontrol.getControlDataType());
            presentaionEleDate.add("GENERIC");
            presentaionEleDate.add("CENTER");
            presentaionEleDate.add("ANDROID-HOLO");
            presentaionEleDate.add("CALENDER");
            presentaionEleDate.add("");
            presentaionEleDate.add("");
            presentaionEleDate.add("N");
            presentaionEleDate.add("N");
            presentaionEleDate.add("");
            presentaionEleDate.add("");
            presentaionEleDate.add("N");
            presentaionEleDate.add("");
            presentaionEleDate.add("N");
            presentaionEleDate.add(!Utils.isNull(citicontrol.getControlDateType()) ? citicontrol.getControlDateType() : "JQUERY");
            objDesignUtils.uiLintsDate.add(presentaionEleDate);
        } else if ((citicontrol.getControlType().equals("CHECKBOX")) && citicontrol.getControlIndeterminate() != null) {
            ArrayList<String> presentaionEleCheckBox = new ArrayList<>();
            presentaionEleCheckBox.add(appId + "__" + objLayoutDesign.name + "__" + citicontrol.getControlName());
            presentaionEleCheckBox.add(DesignUtils.INDETERMINATE);
            objDesignUtils.uiLintsCheckBox.add(presentaionEleCheckBox);
        } else if (citicontrol.getControlType().equals("DROPDOWN")) {
            ArrayList<String> presentaionDropDown = new ArrayList<>();
            presentaionDropDown.add(appId + "__" + objLayoutDesign.name + "__" + citicontrol.getControlName());
            presentaionDropDown.add("");
            presentaionDropDown.add("");
            presentaionDropDown.add("");
            presentaionDropDown.add("");
            presentaionDropDown.add("");
            presentaionDropDown.add("SIMPLE");
            presentaionDropDown.add("N");
            objDesignUtils.uiLintsDropDown.add(presentaionDropDown);
        } else if (citicontrol.getControlType().equals("TAGS")) {
            ArrayList<String> presentaionTags = new ArrayList<>();
            presentaionTags.add(appId + "__" + objLayoutDesign.name + "__" + citicontrol.getControlName());
            presentaionTags.add("");
            objDesignUtils.uiLintsTags.add(presentaionTags);
        } else if ((citicontrol.getControlType().equals("CONTEXTMENU")) && (!citicontrol.getControlMenu().isEmpty())) {
            ArrayList<String> presentaionContext = new ArrayList<>();
            presentaionContext.add(appId + "__" + objLayoutDesign.name + "__" + citicontrol.getControlName());
            presentaionContext.add(appId + "__" + objLayoutDesign.name + "__" + citicontrol.getControlMenu());
            presentaionContext.add("N");
            objDesignUtils.uiLintsContext.add(presentaionContext);
        } else if ((citicontrol.getControlType().equals("POPOVER")) && (!citicontrol.getControlPopoverId().isEmpty())) {
            ArrayList<String> presentaionPopover = new ArrayList<>();
            presentaionPopover.add(appId + "__" + objLayoutDesign.name + "__" + citicontrol.getControlName());
            presentaionPopover.add(objLayoutDesign.name + "__" + citicontrol.getControlPopoverId());
            presentaionPopover.add("N");
            presentaionPopover.add(citicontrol.getControlPopoverElement());
            objDesignUtils.uiLintsPopover.add(presentaionPopover);
        } else if (citicontrol.getControlType().equals("GAUGE")) {
            for (int index = 0; index < citicontrol.getControlGauges().size(); index++) {
                ControlGauges gauge = citicontrol.getControlGauges().get(index);
                Gauges screendefGauge = new Gauges();
                screendefGauge.setGaugeMinRange(new String[]{(gauge.getGaugeMin())});
                screendefGauge.setGaugeMaxRange(new String[]{(gauge.getGaugeMax())});
                screendefGauge.setName(appId + "__" + objLayoutDesign.name + "__" + citicontrol.getControlName());
                screendefGauge.setGaugeRangeColor(new String[]{(gauge.getGaugeColor())});
                screendefGauge.setGaugeType("CYLINDER");
                screendefGauge.setCaption("Element Gauge Cylinder");
                screendefGauge.setLowerLimit("0");
                screendefGauge.setUpperLimit("100");
                screendefGauge.setNumberPrefix("speed");
                screendefGauge.setNumberSuffix("km/hr");
                objDesignUtils.PresentationGauge.add(screendefGauge);
            }
        }
    }

    private ScrSectionRowData buildSectionRow(Rows citirows, int counter) {
        ScrSectionRowData sectionRow = new ScrSectionRowData();
        sectionRow.name = "sc_row_" + counter;
        sectionRow.id = getRandomStringForId();
        sectionRow.widgetcategory = "SECTION";
        sectionRow.widgettype = "ROW";
        sectionRow.options = "Y";
        sectionRow.appearance = "pri";
        sectionRow.typeClass = "SECTIONROW";
        sectionRow.type = "SECTIONROW";
        sectionRow.cssclasses = citirows.getCssclasses() != null ? citirows.getCssclasses() : "";
        sectionRow.draggable = "";
        sectionRow.draggableid = "";
        sectionRow.draggableids = "";
        sectionRow.droppable = "";
        sectionRow.listclickable = "";
        sectionRow.variation = "";
        return sectionRow;
    }

    private ScrContainerData buildContainer(Containers citiContainer, String containerName, String containerNodeName) {

        ScrContainerData container = new ScrContainerData();
        container.name = citiContainer.getName();
        container.id = getRandomStringForId();
        container.custom = "N";
        container.containerstate = citiContainer.getState() == null ? "EDITABLE" : citiContainer.getState();
        container.widgetcategory = CONTAINER;
        container.widgettype = citiContainer.getType();
        container.options = citiContainer.getVisible();
        container.paginationstyle = citiContainer.getPaginationstyle() != null ? citiContainer.getPaginationstyle() : "";
        container.typeClass = CONTAINER;
        container.type = CONTAINER;
        container.animation = "";
        container.appearance = citiContainer.getAppearance() != null ? citiContainer.getAppearance() : "";
        container.variation = citiContainer.getVariation() != null ? citiContainer.getVariation() : "";
        container.bgattachment = "";
        container.bgcolor = "";
        container.bgimage = "";
        container.bgposition = "";
        container.bgrepeat = "";
        container.bgsize = "";
        container.chartbuilderfunction = "";
        container.chartstylesheet = "";
        container.charttype = "";
        container.chartxtitle = "";
        //		container.chartyelements
        container.chartytitle = "";
        //		container.chartzelements
        container.chartztitle = "";
        container.collapsibleicon = "";
        container.contentalignment = "";
        container.contextmenu = "";
        container.createremoveupdate = "";
        container.cssclasses = citiContainer.getCssclasses() != null ? citiContainer.getCssclasses() : "";
        container.datajson = "";
        container.datamodeltype = null;
        container.deletewithundo = "";
        container.display = "";
        container.draggable = "";
        container.draggableid = "";
        container.draggableids = "";
        container.droppable = "";
        container.dynamicpagesize = citiContainer.getDynamicpagesize() != null ? citiContainer.getDynamicpagesize() : "N";
        container.pagesize = citiContainer.getPagesize() != null ? citiContainer.getPagesize() : "999";
        container.dynamicpagesizevalue = "";
        container.editmode = "";
        container.expandableicon = "";
        container.expandableposition = "";
        //		container.gaugeranges
        container.gaugetype = "";
        //		container.gaugevalues
        container.height = "";
        container.heighttype = "";
        container.hovereffect = "";
        container.icon = "";
        container.iconposition = "";
        container.iconsize = "";
        container.interfacename = null;
        container.jshook = "";
        container.labelalignment = "";
        container.layout = "";
        container.lowerlimit = "";
        container.maxwidth = "";
        container.menutype = "";
        container.minwidth = "";
        container.modifygauge = "";
        container.nativetable = "";
        container.numberprefix = "";
        container.numbersuffix = "";
        container.orientation = citiContainer.getOrientation() != null ? citiContainer.getOrientation() : "";
        container.paginationrequired = "";
        container.parentlist = containerName;
        container.parentnode = "";
        container.properties = "";
        container.responsive = "";
        container.rowsclickable = "";
        container.rowselectorrequired = "";
        container.rowselectortype = "";
        container.searchable = "";
        container.sortable = "";
        container.sorthandle = "";
        if (citiContainer.getContaineroptions() != null && citiContainer.getContaineroptions().size() > 0) {
            List<ControlOptions> citiControlOptions = citiContainer.getContaineroptions();
            ArrayList<ElementStaticOptions> staticOptionsList = new ArrayList<>();
            for (ControlOptions options : citiControlOptions) {
                ElementStaticOptions staticOpitons = new ElementStaticOptions();
                staticOpitons.description = options.getDesc();
                staticOpitons.value = options.getValue();
                staticOptionsList.add(staticOpitons);
            }
            container.staticoptions = staticOptionsList;
        }
        container.stepcounter = "";
        container.style = "";
        container.subcaption = "";
        container.subtitle = "";
        container.swipeactions = "";
        container.tableheight = "";
        container.tablewidth = "";
        container.targetoverlayid = "";
        container.title = citiContainer.getDisplayName();
        container.icon = citiContainer.getIcon();
        container.titlestyle = "";
        container.transparent = "";
        container.upperlimit = "";
        container.verticalalignment = "";
        container.xelement = "";
        container.xfunction = "";
        container.xnode = "";
        container.yelement = "";
        container.yfunction = "";
        container.ynode = "";
        container.zelement = "";
        container.zfunction = "";
        container.znode = "";
        objDesignUtils.ContainerNames.add(appId + "__" + objLayoutDesign.name + STRING_SCR + citiContainer.getName());
        objDesignUtils.ContainerTypes.add(citiContainer.getType());
        objDesignUtils.ContainerCustom.add(container.custom);
        DesignUtils.citiDateFormat = citiContainer.getDateFormat();
        DesignUtils.citiDateTimeFormat = citiContainer.getDateTimeFormat();

        if (citiContainer.getNodeName() != null) {
            String mrParent = prepareIntfDefinition(citiContainer, containerNodeName);
            objDesignUtils.mrParentsList.add(mrParent);
            container.containerNodeType = objDesignUtils.intfNodeType;
            objDesignUtils.noOfIfaces.add(1);
            objDesignUtils.ContainerUI.add("N");

            objDesignUtils.ContainerMultiRec.add(citiContainer.getType().equals("FORM") ? "N" : "Y");

        } else {
            objDesignUtils.noOfIfaces.add(0);
            objDesignUtils.ContainerChilds.add("");
            objDesignUtils.ContainerUI.add("Y");
            objDesignUtils.ContainerMultiRec.add("N");

        }
        if (container.containerstate.equals("EDITABLE")) {
            objDesignUtils.ContainerState.add("N");
        } else if (container.containerstate.equals("READONLY")) {
            objDesignUtils.ContainerState.add("Y");
        }

        objDesignUtils.ContainerPagesize.add(Integer.parseInt(container.pagesize));
        objDesignUtils.ContainerPageStyle.add(container.paginationstyle);


        return container;
    }

    private ScrPanelData buildPanel(com.iexceed.citi.pojo.Panel citipanel) {
        ScrPanelData panel = new ScrPanelData();
        panel.name = citipanel.getName();
        panel.id = getRandomStringForId();
        panel.leftmargin = "ALL";
        panel.leftmargincustomtype = "PX";
        panel.leftmarginvalue = "NULL";
        panel.options = "Y";
        panel.rightmargincustomtype = "PX";
        panel.topmargincustomtype = "PX";
        panel.widgetcategory = PANEL;
        panel.widgettype = citipanel.getType();
        panel.bottommargincustomtype = "PX";
        EventData events = buildEvents(ONLOAD, null);
        ArrayList<EventData> listEvents = new ArrayList<>();
        listEvents.add(events);
        panel.events = listEvents;
        panel.typeClass = PANEL;
        panel.type = PANEL;
        panel.id = getRandomStringForId();
        panel.controlid = "";
        panel.appearance = "pri";
        panel.bottommarginvalue = "";
        panel.bottommargincustom = "";
        panel.cssclasses = citipanel.getCssclasses() != null ? citipanel.getCssclasses() : "";
        panel.icon = "";
        panel.leftmargincustom = "";
        panel.loop = "";
        panel.orientation = "HORIZONTAL";
        panel.panelvariation = "";
        panel.responsive = "Y";
        panel.rightmarginvalue = "";
        panel.rightmargincustom = "";
        panel.theme = "";
        panel.title = "";
        panel.topmarginvalue = "";
        panel.topmargincustom = "";
        panel.variation = "";
        return panel;
    }

    private ScrPanelSectionData buildPanelSection(PanelSections citipanelsection) {
        ScrPanelSectionData panelSection = new ScrPanelSectionData();
        panelSection.id = getRandomStringForId();
        panelSection.appearance = "pri";
        panelSection.bottompaddingcustomtype = "PX";
        EventData events = buildEvents(ONLOAD, null);
        ArrayList<EventData> listEvents = new ArrayList<>();
        listEvents.add(events);
        panelSection.events = listEvents;
        panelSection.leftpadding = "ALL";
        panelSection.leftpaddingcustomtype = "PX";
        panelSection.name = citipanelsection.getName();
        panelSection.options = "Y";
        panelSection.spinner = "N";
        panelSection.spinnersize = "REGULAR";
        panelSection.state = "OPEN";
        panelSection.rightpaddingcustomtype = "PX";
        panelSection.toppaddingcustomtype = "PX";
        panelSection.widgetcategory = PANELSECTION;
        panelSection.widgettype = PANELSECTION;
        panelSection.typeClass = PANELSECTION;
        panelSection.type = PANELSECTION;
        panelSection.controlid = "";
        panelSection.bottompaddingvalue = "";
        panelSection.bottompaddingcustom = "";
        panelSection.boxshadow = "";
        panelSection.boxstyle = "";
        panelSection.collapsible = "";
        panelSection.contentcolor = "";
        panelSection.contenttabcolor = "";
        panelSection.cssclasses = citipanelsection.getCssclasses() != null ? citipanelsection.getCssclasses() : "";
        panelSection.customwidth = "";
        panelSection.customwidthtype = "";
        panelSection.icon = "";
        panelSection.iconposition = "";
        panelSection.iconsize = "";
        panelSection.labelalignment = "";
        panelSection.leftpaddingvalue = "";
        panelSection.leftpaddingcustom = "";
        panelSection.maxwidth = "";
        panelSection.minwidth = "";
        panelSection.panetype = "";
        panelSection.rightpaddingvalue = "";
        panelSection.rightpaddingcustom = "";
        panelSection.spinnertext = "";
        panelSection.theme = "";
        panelSection.title = citipanelsection.getDisplayName() != null ? citipanelsection.getDisplayName() : "";
        panelSection.titlestyle = "";
        panelSection.toppaddingvalue = "";
        panelSection.toppaddingcustom = "";
        panelSection.transpspinner = "N";
        panelSection.variation = "";
        panelSection.width = "";
        return panelSection;
    }

    private EventData buildEvents(String event, String method) {
        EventData events = new EventData();
        events.name = event;
        events.function = method;
        return events;
    }

    private ScrGridColumnData buildGridColumn(int pCount) {
        ScrGridColumnData gridColumn = new ScrGridColumnData();
        gridColumn.id = getRandomStringForId();
        gridColumn.name = "gr_col_" + pCount;
        gridColumn.options = "Y";
        gridColumn.widgetcategory = "GRID";
        gridColumn.widgettype = "COLUMN";
        gridColumn.width = "12";
        gridColumn.typeClass = "GRIDCOLUMN";
        gridColumn.type = "GRIDCOLUMN";
        gridColumn.controlid = "";
        gridColumn.boxshadow = "";
        gridColumn.boxstyle = "";
        gridColumn.cssclasses = "";
        gridColumn.collapsible = "";
        gridColumn.columntype = "";
        gridColumn.contentcolor = "";
        gridColumn.contenttabcolor = "";
        gridColumn.customwidth = "";
        gridColumn.customwidthtype = "";
        gridColumn.desktopwidth = "";
        gridColumn.draggable = "";
        gridColumn.draggableid = "";
        gridColumn.draggableids = "";
        gridColumn.droppable = "";
        gridColumn.icon = "";
        gridColumn.iconposition = "";
        gridColumn.iconsize = "";
        gridColumn.labelalignment = "";
        gridColumn.maxwidth = "";
        gridColumn.minwidth = "";
        gridColumn.options = "Y";
        gridColumn.phonewidth = "";
        gridColumn.title = "";
        gridColumn.titlestyle = "";
        gridColumn.tabletwidth = "";
        gridColumn.variation = "";
        gridColumn.wswidth = "";
        return gridColumn;
    }

    public void buildAppResponseJson(Message pMessage) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside buildAppResponseJson()");
        Header header = pMessage.getHeader();
        RenderAppzillonDefinitionJson.appId = header.getAppId();
        JSONObject objRequestJson = pMessage.getRequestObject().getRequestJson();
        long st = System.currentTimeMillis();
        try {
            TbAsmiCnvUIScr objScreenJson = cAsmiCnvUIScrRepository.getScreenDetailsOnVersion(appId, objRequestJson.getString("scrId"));
            String interfaceDef = iAsmiIntfMasterRepository.getIntfDefbyAppIdAndInterfaceId(appId, appId + "__" + objRequestJson.getString("interfaceId") + INT_F);
            if (objScreenJson != null && interfaceDef != null) {
                Response objAppzillonResponse = Response.getInstance();
                JSONObject jsonObject = new JSONObject();
                JSONObject responseObject = new JSONObject();
                jsonObject.put(DesignUtils.SCREEN_DEF, objScreenJson.getScreenDef());//screenDesignDef
                jsonObject.put(DesignUtils.SCREEN_DESIGN, objScreenJson.getScreenDesign());//ScreenDesign
                jsonObject.put(DesignUtils.LAYOUT_DEF, objScreenJson.getScreenLayout());//Layoutname
                jsonObject.put(DesignUtils.INTERFACE_DEF, interfaceDef);
                responseObject.put(objRequestJson.getString("scrId"), jsonObject);
                LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "************CITI RESPONSE******************");
                objAppzillonResponse.setResponseJson(responseObject);
                pMessage.setResponseObject(objAppzillonResponse);
                LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "Product JSON Reponse From DB " + jsonObject.toString());
            } else {
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                lDomainException.setCode(DomainException.Code.APZ_DM_008.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }
            LOG.info(ServerConstants.LOGGER_PREFIX_CITI + "########## Response sent CITI buildAppResponseJson and Processing time in ms " + (System.currentTimeMillis() - st) + "##################\n\n");
        } catch (Exception jme) {
            LOG.error(EXCEPTION, jme);
        }
    }

    public void parseCitiWidgetJson(Message pMessage) {
        Header header = pMessage.getHeader();
        try {
            JSONObject objRequestJson = pMessage.getRequestObject().getRequestJson();
            LOG.debug("REQUEST JSON" + objRequestJson.toString());
            byte[] jsonData = objRequestJson.toString().getBytes();
            ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Citiresponse objcitiresponse = objectMapper.readValue(jsonData, Citiresponse.class);
            objLayoutDesign.name = objcitiresponse.getName();
            RenderAppzillonDefinitionJson.appId = header.getAppId();
            RenderAppzillonDefinitionJson appzillonScreenJson = new RenderAppzillonDefinitionJson();
            DesignData responsecitiJson = appzillonScreenJson.appCitiResponseJson(objcitiresponse);
            IntfDefinition intfDef = appzillonScreenJson.generateIntfDefinition();
            ScreenDef screenDef = appzillonScreenJson.generateScreenDesign(responsecitiJson);
            TbAsmiCnvUIScr objTbAsmiCnvUIScr = cAsmiCnvUIScrRepository.getScreenDetailsOnVersion(appId, objLayoutDesign.name + "_Scr");
            if (objTbAsmiCnvUIScr == null) {
                objTbAsmiCnvUIScr = new TbAsmiCnvUIScr();
                objTbAsmiCnvUIScr.setCreateUserId(appId);
                objTbAsmiCnvUIScr.setVersionNo(1);
                objTbAsmiCnvUIScr.setScreenDesc("New Screen");
                objTbAsmiCnvUIScr.setScreenLayout(Utils.getDefaultLayoutDef(objLayoutDesign.name));
            }
            TbAsmiCnvUIScrPK objTbAsmiCnvUIScrPK = new TbAsmiCnvUIScrPK();
            objTbAsmiCnvUIScrPK.setAppId(appId);
            objTbAsmiCnvUIScrPK.setScreenId(objLayoutDesign.name + "_Scr");
            objTbAsmiCnvUIScr.setId(objTbAsmiCnvUIScrPK);
            objTbAsmiCnvUIScr.setScreenDef(objectMapper.writeValueAsString(screenDef));
            objTbAsmiCnvUIScr.setScreenDesign(objectMapper.writeValueAsString(responsecitiJson));
            objTbAsmiCnvUIScr.setScreenHtml("");
            objTbAsmiCnvUIScr.setScreenLayout(objTbAsmiCnvUIScr.getScreenLayout());
            objTbAsmiCnvUIScr.setCreateTs(new Timestamp(System.currentTimeMillis()));
            cAsmiCnvUIScrRepository.save(objTbAsmiCnvUIScr);
            LOG.debug("persisted in CnvUIScr");
            TbAsmiIntfMaster objTbAsmiIntfMaster = new TbAsmiIntfMaster();
            objTbAsmiIntfMaster.setInterfaceDef(objectMapper.writeValueAsString(intfDef));
            objTbAsmiIntfMaster.setCaptchaReq(ServerConstants.NO);
            objTbAsmiIntfMaster.setCategory(ServerConstants.INTERFACE_CATEGORY_INTERNAL);
            objTbAsmiIntfMaster.setType(ServerConstants.INTERFACE_CATEGORY_INTERNAL);
            objTbAsmiIntfMaster.setDgTxnLogReq(ServerConstants.YES);
            objTbAsmiIntfMaster.setCreateUserId(pMessage.getHeader().getUserId());
            objTbAsmiIntfMaster.setCreateTs(new Timestamp(System.currentTimeMillis()));
            objTbAsmiIntfMaster.setDescription(appId + "__" + objLayoutDesign.name + INT_F);
            TbAsmiIntfMasterPK objTbAsmiIntfMasterPK = new TbAsmiIntfMasterPK();
            objTbAsmiIntfMasterPK.setInterfaceId(appId + "__" + objLayoutDesign.name + INT_F);
            objTbAsmiIntfMasterPK.setAppId(appId);
            objTbAsmiIntfMaster.setTbAsmiIntfMasterPK(objTbAsmiIntfMasterPK);
            iAsmiIntfMasterRepository.save(objTbAsmiIntfMaster);
            objDesignUtils = new DesignUtils();
            LOG.debug("persisted in IntfMaster");

            Response objAppzillonResponse = Response.getInstance();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(DesignUtils.SCREEN_DEF, objectMapper.writeValueAsString(screenDef));//screenDesignDef
            jsonObject.put(DesignUtils.SCREEN_DESIGN, objectMapper.writeValueAsString(responsecitiJson));//ScreenDesign
            jsonObject.put(DesignUtils.INTERFACE_DEF, objectMapper.writeValueAsString(intfDef));//json intf
            objAppzillonResponse.setResponseJson(jsonObject);
            pMessage.setResponseObject(objAppzillonResponse);
        } catch (JsonMappingException jme) {
            LOG.error(EXCEPTION, jme);
        } catch (JsonParseException jpe) {
            LOG.error(EXCEPTION, jpe);
        } catch (IOException ioe) {
            LOG.error(EXCEPTION, ioe);
        }
    }

    public void persistAppScreenJson(Message pMessage) {
        LOG.debug("Inside persistAppScreenJson");
        JSONObject jsonObject = pMessage.getRequestObject().getRequestJson();
        String screenDef = jsonObject.getString(DesignUtils.SCR_DEF);
        String screenHtml = jsonObject.getString(DesignUtils.SCR_HTML);
        String scrId = jsonObject.getString(DesignUtils.SCR_ID);
        String scrDesc = jsonObject.getString(DesignUtils.SCR_DESC);
        String intfdef = jsonObject.getString(DesignUtils.INTERFACE_DEF);
        String intfId = jsonObject.getString(DesignUtils.INTERFACE_ID);
        String userId = pMessage.getHeader().getUserId();

        TbAsmiCnvUIScr objTbAsmiCnvUIScr = cAsmiCnvUIScrRepository.getScreenDetailsOnVersion(pMessage.getHeader().getAppId(), scrId);
        objTbAsmiCnvUIScr.setScreenDef(screenDef);
        objTbAsmiCnvUIScr.setScreenHtml(screenHtml);
        objTbAsmiCnvUIScr.setCreateTs(new Timestamp(System.currentTimeMillis()));
        objTbAsmiCnvUIScr.setScreenDesc(scrDesc);
        objTbAsmiCnvUIScr.setCreateUserId(pMessage.getHeader().getUserId());
        TbAsmiCnvUIScrPK objTbAsmiCnvUIScrPK = new TbAsmiCnvUIScrPK();
        objTbAsmiCnvUIScrPK.setScreenId(scrId);
        objTbAsmiCnvUIScrPK.setAppId(pMessage.getHeader().getAppId());
        objTbAsmiCnvUIScr.setId(objTbAsmiCnvUIScrPK);
        cAsmiCnvUIScrRepository.save(objTbAsmiCnvUIScr);

        LOG.debug("Persisted in CnvUIScr");

        TbAsmiIntfMaster objTbAsmiIntfMaster = new TbAsmiIntfMaster();
        objTbAsmiIntfMaster.setInterfaceDef(intfdef);
        objTbAsmiIntfMaster.setCaptchaReq(ServerConstants.NO);
        objTbAsmiIntfMaster.setCategory(ServerConstants.INTERFACE_CATEGORY_INTERNAL);
        objTbAsmiIntfMaster.setType(ServerConstants.INTERFACE_CATEGORY_INTERNAL);
        objTbAsmiIntfMaster.setDgTxnLogReq(ServerConstants.YES);
        objTbAsmiIntfMaster.setCreateUserId(userId);
        objTbAsmiIntfMaster.setCreateTs(new Timestamp(System.currentTimeMillis()));
        objTbAsmiIntfMaster.setDescription(intfId);
        TbAsmiIntfMasterPK objTbAsmiIntfMasterPK = new TbAsmiIntfMasterPK();
        objTbAsmiIntfMasterPK.setInterfaceId(intfId);
        objTbAsmiIntfMasterPK.setAppId(pMessage.getHeader().getAppId());
        objTbAsmiIntfMaster.setTbAsmiIntfMasterPK(objTbAsmiIntfMasterPK);
        iAsmiIntfMasterRepository.save(objTbAsmiIntfMaster);
        LOG.debug("Product Json Persisted in interface and screen ");

        Response objAppzillonResponse = Response.getInstance();
        JSONObject responseJsonObject = new JSONObject();

        responseJsonObject.put(DesignUtils.SCR_ID, scrId);
        responseJsonObject.put(DesignUtils.SCREEN_DEF, screenDef);//screenDesignDef
        responseJsonObject.put(DesignUtils.SCREEN_HTML, screenHtml);//ScreenDesign
        responseJsonObject.put(DesignUtils.INTERFACE_DEF, intfdef);//json intf
        responseJsonObject.put(DesignUtils.INTERFACE_ID, intfId);
        objAppzillonResponse.setResponseJson(responseJsonObject);
        pMessage.setResponseObject(objAppzillonResponse);

    }

    private IntfDefinition generateIntfDefinition() {

        ArrayList<Elements> nodeElement = new ArrayList<>();
        IntfDefinition objIntfDefinition = new IntfDefinition();
        ArrayList<String> reqnodeName = new ArrayList<>();
        ArrayList<String> nodeName = new ArrayList<>();
        ArrayList<String> reqrelType = new ArrayList<>();
        ArrayList<String> resrelType = new ArrayList<>();
        ArrayList<String> reqExtName = new ArrayList<>();
        ArrayList<String> resExtName = new ArrayList<>();
        ArrayList<String> reqnMultiRec = new ArrayList<>();
        ArrayList<String> resnMultiRec = new ArrayList<>();
        ArrayList<String> reqParent = new ArrayList<>();
        ArrayList<String> resParent = new ArrayList<>();
        ArrayList<String> reqParents = new ArrayList<>();
        ArrayList<String> resParents = new ArrayList<>();
        ArrayList<String> reqnMrParent = new ArrayList<>();
        ArrayList<String> resnMrParent = new ArrayList<>();
        ArrayList<Integer> resnoOfNode = new ArrayList<>();
        ArrayList<Integer> reqnoOfNode = new ArrayList<>();
        ArrayList<String> scriNodes = new ArrayList<>();
        ArrayList<String> resChild = new ArrayList<>();
        ArrayList<String> reqChild = new ArrayList<>();
        reqnodeName.add(appId + "__" + objLayoutDesign.name + INT_F + "_Req");
        nodeName.add(appId + "__" + objLayoutDesign.name + INT_F + "_Res");
        reqrelType.add("1:1");
        resrelType.add("1:1");
        reqnMultiRec.add("N");
        resnMultiRec.add("N");
        reqParent.add("");
        resParent.add("");
        reqParents.add("");
        resParents.add("");
        resnoOfNode.add(0);
        reqnoOfNode.add(0);
        reqnMrParent.add(appId + "__" + objLayoutDesign.name + INT_F + "_Req");
        resnMrParent.add(appId + "__" + objLayoutDesign.name + INT_F + "_Res");
        reqExtName.add(appId + "__" + objLayoutDesign.name + INT_F + "_Req");
        resExtName.add(appId + "__" + objLayoutDesign.name + INT_F + "_Res");
        if (objDesignUtils.nodeNameMap.size() > 0) {
            Map.Entry<String, HashMap<String, String>> key = objDesignUtils.nodeNameMap.entrySet().iterator().next();
            String firstkey = key.getKey();
            HashMap<String, String> values = key.getValue();
            if (values.get(NODE_TYPE).equals(REQUEST)) {
                reqChild.add(firstkey);
                resChild.add("");
            }
            if (values.get(NODE_TYPE).equals(RESPONSE)) {
                reqChild.add("");
                resChild.add(firstkey);
            }

            for (Map.Entry<String, HashMap<String, String>> entry : objDesignUtils.nodeNameMap.entrySet()) {
                LOG.debug("node {}", entry.getKey());

                HashMap<String, String> nodeValueMap = entry.getValue();

                scriNodes.add(nodeValueMap.get(INT_F_NODE));
                if (nodeValueMap.get(NODE_TYPE).equals(REQUEST)) {
                    reqnodeName.add(entry.getKey());
                    reqrelType.add(nodeValueMap.get(NODE_REL));
                    reqExtName.add(nodeValueMap.get(N_EXT_NAME));
                    reqnMultiRec.add(nodeValueMap.get(N_MULTI_REC));
                    reqParent.add(nodeValueMap.get(N_PARENT));
                    reqParents.add(nodeValueMap.get(N_PARENTS));
                    reqChild.add(nodeValueMap.get(N_CHILDS));
                    reqnMrParent.add(nodeValueMap.get(N_MR_PARENT));
                    reqnoOfNode.add(Integer.parseInt(nodeValueMap.get(NO_OF_NODE_ELEMS)));
                }
                if (nodeValueMap.get(NODE_TYPE).equals(RESPONSE)) {
                    nodeName.add(entry.getKey());
                    resrelType.add(nodeValueMap.get(NODE_REL));
                    resExtName.add(nodeValueMap.get(N_EXT_NAME));
                    resnMultiRec.add(nodeValueMap.get(N_MULTI_REC));
                    resParent.add(nodeValueMap.get(N_PARENT));
                    resChild.add(nodeValueMap.get(N_CHILDS));
                    resParents.add(nodeValueMap.get(N_PARENTS));
                    resnMrParent.add(nodeValueMap.get(N_MR_PARENT));
                    resnoOfNode.add(Integer.parseInt(nodeValueMap.get(NO_OF_NODE_ELEMS)));
                }
            }
        }
        nodeName.addAll(0, reqnodeName);
        nodeName.add(appId + "__" + objLayoutDesign.name + INT_F + "_Flt");
        resrelType.addAll(0, reqrelType);
        resrelType.add("1:1");
        int requestNodeCount = reqExtName.size();
        int responseNodeCount = resExtName.size();
        objIntfDefinition.setNoOfReqNodes(requestNodeCount);
        objIntfDefinition.setNoOfResNodes(responseNodeCount);
        resExtName.addAll(0, reqExtName);
        resExtName.add(appId + "__" + objLayoutDesign.name + INT_F + "_Flt");
        resnMultiRec.addAll(0, reqnMultiRec);
        resnMultiRec.add("N");
        resParent.addAll(0, reqParent);
        resParent.add("");
        resParents.addAll(0, reqParents);
        resParents.add("");
        resnMrParent.addAll(0, reqnMrParent);
        resnMrParent.add(appId + "__" + objLayoutDesign.name + INT_F + "_Flt");
        resnoOfNode.addAll(0, reqnoOfNode);
        resnoOfNode.add(0);
        resChild.addAll(0, reqChild);
        resChild.add("");
        objDesignUtils.ioNodes = DesignUtils.getContainerVal(scriNodes);

        objIntfDefinition.setnChilds(DesignUtils.getContainerVal(resChild));
        objIntfDefinition.setNodes(DesignUtils.getContainerVal(nodeName));
        objIntfDefinition.setnExtName(DesignUtils.getContainerVal(resExtName));
        objIntfDefinition.setnMrParent(DesignUtils.getContainerVal(resnMrParent));
        objIntfDefinition.setnParent(DesignUtils.getContainerVal(resParent));
        objIntfDefinition.setnParents(DesignUtils.getContainerVal(resParents));
        objIntfDefinition.setnRelType(DesignUtils.getContainerVal(resrelType));
        objIntfDefinition.setNoOFaultNodes(1);
        int[] noOfnodeElms = new int[nodeName.toArray().length];
        String[] nmultiRec = new String[noOfnodeElms.length];
        for (int index = 0; index < noOfnodeElms.length; index++) {
            int elementCount = 0;
            for (Elements elements : objDesignUtils.nodeElementList) {
                if (elements.getControlNode().equals(nodeName.toArray()[index])) {
                    nodeElement.add(elements);
                }
            }
            noOfnodeElms[index] = elementCount;
            nmultiRec[index] = "N";
        }
        objIntfDefinition.setNoOfNodeElms(DesignUtils.getContainerNumberVal(resnoOfNode));
        objIntfDefinition.setnMultiRec(DesignUtils.getContainerVal(resnMultiRec));

        objIntfDefinition.setName(appId + "__" + objLayoutDesign.name + INT_F);
        objIntfDefinition.setType("CUSTOM");
        objIntfDefinition.setDateFormat(DesignUtils.citiDateFormat);
        if (DesignUtils.citiDateTimeFormat != null && !DesignUtils.citiDateTimeFormat.isEmpty()) {
            objIntfDefinition.setDateTimeFormat(DesignUtils.citiDateTimeFormat);
            String timeFormat = DesignUtils.citiDateTimeFormat.split(" ")[1];
            objIntfDefinition.setTimeFormat(timeFormat);
        }
        objIntfDefinition.setOffline("N");
        objIntfDefinition.setAmountMask("");
        objIntfDefinition.setSession("N");
        objIntfDefinition.setCorrectReq("N");

        objIntfDefinition.setCorrectReq("Y");
        objIntfDefinition.setCorrectRes("Y");
        String[] elms = new String[nodeElement.size()];
        String[] nExtName = new String[nodeElement.size()];
        String[] eDataType = new String[nodeElement.size()];
        String[] eMinVal = new String[nodeElement.size()];
        String[] eMaxVal = new String[nodeElement.size()];
        String[] eMinLen = new String[nodeElement.size()];
        String[] eMaxLen = new String[nodeElement.size()];
        String[] eMaxDec = new String[nodeElement.size()];
        String[] eLenType = new String[nodeElement.size()];
        String[] eArr = new String[nodeElement.size()];
        String[] ePattern = new String[nodeElement.size()];
        String[] eMand = new String[nodeElement.size()];
        String[] eRelNode = new String[nodeElement.size()];
        String[] eRelElm = new String[nodeElement.size()];
        int[] nodesElementCount = new int[nodeName.toArray().length];
        for (int index = 0; index < nodesElementCount.length; index++) {
            for (int count = 0; count < nodeElement.size(); count++) {
                if (nodeElement.get(count).getControlNode().equals(nodeName.toArray()[index])) {
                    elms[count] = nodeElement.get(count).getControlName();
                    nExtName[count] = "NewElement";
                    if (nodeElement.get(count).getControlDataType().isEmpty()) {
                        eDataType[count] = "STRING";
                    } else {
                        eDataType[count] = nodeElement.get(count).getControlDataType();
                    }
                    eMinVal[count] = nodeElement.get(count).getControlMinVal();
                    eMaxVal[count] = nodeElement.get(count).getControlMaxVal();
                    eMinLen[count] = "";
                    eMaxLen[count] = nodeElement.get(count).getControlMaxLength();
                    eMaxDec[count] = "";
                    eLenType[count] = "F";
                    eArr[count] = "N";
                    if (nodeElement.get(count).getControlTextPattern() == null) {
                        ePattern[count] = "";
                    } else {
                        ePattern[count] = nodeElement.get(count).getControlTextPattern();
                    }
                    eMand[count] = nodeElement.get(count).getControlRequired();
                    eRelNode[count] = "";
                    eRelElm[count] = "";
                }
            }
        }
        objIntfDefinition.setElms(elms);
        objIntfDefinition.seteExtName(nExtName);
        objIntfDefinition.seteDataType(eDataType);
        objIntfDefinition.seteMinVal(eMinVal);
        objIntfDefinition.seteMaxVal(eMaxVal);
        objIntfDefinition.seteMinLen(eMinLen);
        objIntfDefinition.seteMaxLen(eMaxLen);
        objIntfDefinition.seteMaxDec(eMaxDec);
        objIntfDefinition.seteLenType(eLenType);
        objIntfDefinition.seteArr(eArr);
        objIntfDefinition.setePattern(ePattern);
        objIntfDefinition.seteMand(eMand);
        objIntfDefinition.seteRelNode(eRelNode);
        objIntfDefinition.seteRelElm(eRelElm);
        return objIntfDefinition;
    }

    private ScreenDef generateScreenDesign(DesignData responsecitiJson) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside generateScreenDesign()");
        String[] ifaces = new String[1];
        ScreenDef screenDef = new ScreenDef();
        screenDef.setScr(responsecitiJson.name + "_Scr");
        screenDef.setScreenType(responsecitiJson.type);
        screenDef.setIcon("");
        screenDef.setId("D0");
        screenDef.setDisplayname("D0");
        screenDef.setLayout(DesignUtils.NEW_LAYOUT);
        ifaces[0] = appId + "__" + objLayoutDesign.name + INT_F;
        containers objContainers = createContainerScreenDef();
        groups objGroup = createGroupScreenDef();
        uiInits listUilints = createUILintsScreeenDef();
        String[] props = createScreenProps();
        screenDef.setContainers(objContainers);
        screenDef.setGroups(objGroup);
        screenDef.setUiInits(listUilints);
        screenDef.setIfaces(ifaces);
        String[] script = new String[]{objLayoutDesign.name + "_Scr.js"};
        screenDef.setScripts(script);
        if (objDesignUtils.PresentationGauge != null) {
            screenDef.setGauges(objDesignUtils.PresentationGauge);
        }
        screenDef.setScrProps(props);
        return screenDef;
    }

    private containers createContainerScreenDef() {

        containers objContainers = new containers();
        objContainers.setName(DesignUtils.getContainerVal(objDesignUtils.ContainerNames));
        objContainers.setType(DesignUtils.getContainerVal(objDesignUtils.ContainerTypes));
        objContainers.setCustom(DesignUtils.getContainerVal(objDesignUtils.ContainerCustom));
        objContainers.setRo(DesignUtils.getContainerVal(objDesignUtils.ContainerState));
        objContainers.setPgSize(DesignUtils.getContainerNumberVal(objDesignUtils.ContainerPagesize));
        objContainers.setPgStyle(DesignUtils.getContainerVal(objDesignUtils.ContainerPageStyle));
        objContainers.setUi(DesignUtils.getContainerVal(objDesignUtils.ContainerUI));
        Set<String> elmsName = new LinkedHashSet<>(objDesignUtils.ContainerElements);
        int[] count = new int[elmsName.size()];
        for (int index = 0; index < elmsName.toArray().length; index++) {
            count[index] = Collections.frequency(objDesignUtils.ContainerElements, elmsName.toArray()[index]);
        }

        for (String name : elmsName) {
            if (objDesignUtils.mappedList.containsKey(name)) {
                objDesignUtils.PresentaionElms.addAll(objDesignUtils.mappedList.get(name));
            }
            if (objDesignUtils.mappedList.containsKey(name + E_LM_TYPE)) {
                objDesignUtils.presentationElementType.addAll(objDesignUtils.mappedList.get(name + E_LM_TYPE));
            }
            if (objDesignUtils.mappedList.containsKey(name + "eRo")) {
                objDesignUtils.presentationState.addAll(objDesignUtils.mappedList.get(name + "eRo"));
            }
            if (objDesignUtils.mappedList.containsKey(name + E_EMAIL)) {
                objDesignUtils.presentationEmail.addAll(objDesignUtils.mappedList.get(name + E_EMAIL));
            }
        }

        objContainers.setElms(DesignUtils.getContainerVal(objDesignUtils.PresentaionElms));
        objContainers.seteUi(DesignUtils.getContainerVal(objDesignUtils.presentationEui));
        objContainers.seteCustom(DesignUtils.getContainerVal(objDesignUtils.presentationCustom));
        objContainers.setElmType(DesignUtils.getContainerVal(objDesignUtils.presentationElementType));
        objContainers.seteRo(DesignUtils.getContainerVal(objDesignUtils.presentationState));
        objContainers.seteEmail(DesignUtils.getContainerVal(objDesignUtils.presentationEmail));
        objContainers.setChilds(DesignUtils.getContainerVal(objDesignUtils.ContainerChilds));
        objContainers.setMultiRec(DesignUtils.getContainerVal(objDesignUtils.ContainerMultiRec));
        objContainers.setNoOfElms(count);
        objContainers.seteMask(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementEMask));
        objContainers.seteRtFmt(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementEMask));
        objContainers.seteSkipFmt(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementSkipFormat));
        objContainers.seteLiteral(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementeLiteral));
        objContainers.seteLovId(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementESkipformat));
        objContainers.seteLovWidth(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementERtformat));
        objContainers.seteLovMinWidth(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementEMask));
        objContainers.seteLovRf(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementEMask));
        objContainers.setNoOfeLovRf(DesignUtils.getContainerNumberVal(objDesignUtils.noOfeLovRfCount));
        objContainers.seteLovBv(DesignUtils.getContainerVal(objDesignUtils.presentaitonElementEMask));
        objContainers.setNoOfeLovBv(DesignUtils.getContainerNumberVal(objDesignUtils.noOfeLovRfCount));
        objContainers.setNoOfIfaces(DesignUtils.getContainerNumberVal(objDesignUtils.noOfIfaces));
        objContainers.setNoOfNodes(objContainers.getNoOfIfaces());
        objContainers.setNodes(objDesignUtils.ioNodes);

        if (objDesignUtils.ioNodes != null) {
            String[] iface = new String[objDesignUtils.ioNodes.length];
            for (int index = 0; index < objDesignUtils.ioNodes.length; index++)
                iface[index] = appId + "__" + objLayoutDesign.name + INT_F;
            objContainers.setIfaces(iface);
        }
        return objContainers;
    }

    private groups createGroupScreenDef() {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside createGroupScreenDef()");
        groups objgroups = new groups();
        objgroups.setContainers(DesignUtils.getContainerVal(objDesignUtils.ifaceNodenames));
        objgroups.setNoOfContainers(DesignUtils.getContainerNumberVal(objDesignUtils.ifaceCount));
        objgroups.setNodes(objDesignUtils.ioNodes);
        objgroups.setNoOfNodes(DesignUtils.getContainerNumberVal(objDesignUtils.noOfNodesList));
        int count = 0;
        if (objDesignUtils.ioNodes != null) {
            String[] names = new String[objDesignUtils.ioNodes.length];
            for (int index = 0; index < objDesignUtils.ioNodes.length; index++) {
                count++;
                names[index] = "GRP_FirstPage_" + count;
            }
            objgroups.setName(names);
        }
        return objgroups;
    }

    public String[] getContainerVal(List<String> containerName) {
        return containerName.toArray(new String[containerName.size()]);
    }

    private uiInits createUILintsScreeenDef() {
        LOG.debug(ServerConstants.LOGGER_PREFIX_CITI + "inside createUILintsScreeenDef()");
        uiInits uiInt = new uiInits();
        setUiComponent(objDesignUtils.uiLintsTags, uiInt, TAG);
        setUiComponent(objDesignUtils.uiLintsDate, uiInt, DATE);
        setUiComponent(objDesignUtils.uiLintsCheckBox, uiInt, CHECKBOX1);
        setUiComponent(objDesignUtils.uiLintsDropDown, uiInt, DROP_DOWN);
        setUiComponent(objDesignUtils.uiLintsContext, uiInt, CONTEXT);
        setUiComponent(objDesignUtils.uiLintsPopover, uiInt, POPOVER);
        return uiInt;
    }

    private void setUiComponent(List<ArrayList<String>> designObject, uiInits uilint, String componentType) {

        String[][] popover = new String[0][];
        if (!designObject.isEmpty()) {
            popover = new String[designObject.size()][];
            for (int index = 0; index < designObject.size(); index++) {
                ArrayList<String> popoverRow = designObject.get(index);
                popover[index] = DesignUtils.getContainerVal(popoverRow);
            }
        }
        switch (componentType) {
            case TAG:
                uilint.setTags(popover);
                break;
            case DATE:
                uilint.setDate(popover);
                break;
            case CHECKBOX1:
                uilint.setCheckbox(popover);
                break;
            case DROP_DOWN:
                uilint.setDropDown(popover);
                break;
            case CONTEXT, POPOVER:
                uilint.setContextMenu(popover);
                break;
            default:
                LOG.info("in default switch case");
        }
    }
}
