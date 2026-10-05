package com.iexceed.citi.utils;

import com.iexceed.citi.pojo.CitiIntfNodes;
import com.iexceed.citi.pojo.Elements;
import com.iexceed.intf.json.IntfDefinition;
import com.iexceed.screenDef.json.Gauges;

import java.util.*;

public class DesignUtils {

    public static final String DROP_DOWN = "DROPDOWN";
    public static final String INDETERMINATE = "INDETERMINATE";

    public static final String SCREEN_DEF = "screenDef";
    public static final String SCREEN_DESIGN = "screenDesign";
    public static final String SCREEN_HTML = "screenHtml";
    public static final String SCREEN_LAYOUT = "screenLayout";
    public static final String TEMPLATE = "template";
    public static final String INTERFACE_DEF = "ifaceDef";
    public static final String SCREEN_DESC = "screenDesc";

    public static final String SCR_DEF = "scrDef";
    public static final String SCR_HTML = "scrHtml";
    public static final String SCR_ID = "scrId";
    public static final String SCR_DESC = "scrDesc";
    public static final String LAYOUT_DEF = "layoutDef";
    public static final String PORTION = "PORTION";
    public static final String PIXEL = "PX";

    public static final String INPUTWITHBUTTON = "INPUTWITHBUTTON";
    public static final String INPUTBOX = "INPUTBOX";
    public static final String CHECKBOX = "CHECKBOX";
    public static final String DROPDOWN = DROP_DOWN;
    public static final String TAGS = "TAGS";
    public static final String CONTEXTMENU = "CONTEXTMENU";
    public static final String POPOVER = "POPOVER";
    public static final String GAUGE = "GAUGE";
    public static final String DATE = "DATE";
    public static final String DATE_TIME = "DATETIME";

    public static final String NEW_LAYOUT = "NewLayout";

    public static final String LIST_OF_OBJECT = "ListOfObject";
    public static final String TYPE_OBJECT = "Object";
    public static final String ID = "Id";
    public static final String HEADER = "HEADER";
    public static final String BODY = "BODY";
    public static final String SIDEBAR = "SIDEBAR";
    public static final String FOOTER = "FOOTER";
    public static final String ATTRIBUTE = "Attribute";
    public static final String ATTRIBUTE_DECORATION = "AttributeDecoration";
    public static final String RELATIONSHIP = "Relationship";
    public static final String RELATIONSHIP_DECORATION = "RelationshipDecoration";
    public static final String RELATIONSHIP_DOMAIN = "RelationshipDomain";
    public static final String RELATIONSHIP_DOMAIN_DECORATION = "RelationshipDomainDecoration";
    public static final String INTF_DEF = "ifaceDef";
    public static final String INTERFACE_ID = "interfaceId";

    public static final int SEQUENCEUPPERLIMIT = 999999999;


    public static final List<String> ContainerNames = new ArrayList<>();
    public static final List<String> ContainerTypes = new ArrayList<>();
    public static final List<String> ContainerCustom = new ArrayList<>();
    public static final List<String> ContainerState = new ArrayList<>();
    public static final List<String> ContainerUI = new ArrayList<>();
    public static final List<IntfDefinition> containerIntfs = new ArrayList<>();
    public static final List<String> nodeNamesList = new ArrayList<>();
    public static final List<Integer> ifaceCount = new ArrayList<>();
    public static final List<String> ifaceNodenames = new ArrayList<>();
    public static final List<Integer> noOfNodesList = new ArrayList<>();
    public static final List<Integer> noOfIfaces = new ArrayList<>();
    public static final String[] nameNodes = null;
    public static final String[] nodeType = null;
    public static final String[] ifaces = null;
    public static final List<String> PresentaionElms = new ArrayList<>();
    public static final Map<String, ArrayList<String>> mappedList = new LinkedHashMap<>();
    public static final Map<String, HashMap<String, String>> nodeNameMap = new LinkedHashMap<>();
    public static final List<Elements> nodeElementList = new ArrayList<>();
    public static final List<Elements> nPresentationElement = new ArrayList<>();
    public static final String ALPHA_NUM = "0123456789abcdefghijklmnopqrstuvwxyz";
    public static final List<ArrayList<String>> uiLintsDate = new ArrayList<>();
    public static final List<ArrayList<String>> uiLintsCheckBox = new ArrayList<>();
    public static final List<ArrayList<String>> uiLintsDropDown = new ArrayList<>();
    public static final List<ArrayList<String>> uiLintsTags = new ArrayList<>();
    public static final List<ArrayList<String>> uiLintsContext = new ArrayList<>();
    public static final List<ArrayList<String>> uiLintsPopover = new ArrayList<>();
    public static final List<String> ContainerMultiRec = new ArrayList<>();
    public static final List<String> ContainerElements = new ArrayList<>();
    public static final List<String> ContainerChilds = new ArrayList<>();
    public static final List<Integer> ContainerPagesize = new ArrayList<>();
    public static final List<String> ContainerPageStyle = new ArrayList<>();
    public static final List<String> nChildsList = new ArrayList<>();
    public static final List<String> presentationState = new ArrayList<>();
    public static final List<String> presentationElementName = new ArrayList<>();
    public static final List<String> presentationEui = new ArrayList<>();
    public static final List<String> presentationCustom = new ArrayList<>();
    public static final List<String> presentationElementType = new ArrayList<>();
    public static final List<String> presentationEmail = new ArrayList<>();
    public static final List<String> presentaitonElementEMask = new ArrayList<>();
    public static final List<String> presentaitonElementSkipFormat = new ArrayList<>();
    public static final List<String> presentaitonElementeLiteral = new ArrayList<>();
    public static final List<String> presentaitonElementERtformat = new ArrayList<>();
    public static final List<String> presentaitonElementESkipformat = new ArrayList<>();
    public static final List<Integer> noOfeLovRfCount = new ArrayList<>();
    public static final Map<String, CitiIntfNodes> citiMap = new HashMap<>();
    // public ArrayList<Gauges> presentationElementGauges=new
    public static final List<Gauges> PresentationGauge = new ArrayList<>();
    public static final List<String> mrParentsList = new ArrayList<>();
    public static final String NODES = null;
    public static final StringBuilder parentsReqContainerNodeName = new StringBuilder();
    public static final StringBuilder parentsResContainerNodeName = new StringBuilder();
    public static final StringBuilder multiRec = new StringBuilder();
    public static String citiDateFormat = null;
    public static String citiDateTimeFormat = "";
    public static String[] intfnParent;
    public static String[] intfNodeType;
    public String citiIntfNodeName = null;
    public String[] ioNodes = null;

    public static String[] getContainerVal(List<String> containerName) {
        return containerName.toArray(new String[containerName.size()]);
    }

    public static String[] getContainerValBySet(Set<String> containerName) {
        return containerName.toArray(new String[containerName.size()]);
    }

    public static Integer[] getContainerNumberVal(List<Integer> containerName) {
        return containerName.toArray(new Integer[containerName.size()]);
    }

}
