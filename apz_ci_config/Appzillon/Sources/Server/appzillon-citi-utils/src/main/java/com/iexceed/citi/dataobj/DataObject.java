package com.iexceed.citi.dataobj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "typeclass")
@JsonSubTypes({@JsonSubTypes.Type(value = DesignData.class, name = "DESIGN"), @JsonSubTypes.Type(value = PortionData.class, name = "LAYOUTPORTION"),
        @JsonSubTypes.Type(value = ScrPopupData.class, name = "POPUP"), @JsonSubTypes.Type(value = ScrGridRowData.class, name = "GRIDROW"),
        @JsonSubTypes.Type(value = ScrGridColumnData.class, name = "GRIDCOLUMN"), @JsonSubTypes.Type(value = ScrPanelData.class, name = "PANEL"),
        @JsonSubTypes.Type(value = ScrPanelSectionData.class, name = "PANELSECTION"), @JsonSubTypes.Type(value = ScrContainerData.class, name = "CONTAINER"),
        @JsonSubTypes.Type(value = ScrSectionRowData.class, name = "SECTIONROW"), @JsonSubTypes.Type(value = ScrSectionColumnData.class, name = "SECTIONCOLUMN"),
        @JsonSubTypes.Type(value = ScrElementData.class, name = "PRESENTATIONELEMENT")})

public abstract class DataObject {
    @JsonIgnore
    public static final Map<String, DataObject> childsMap = new HashMap<>();
    @JsonIgnore
    public static final List<ScrElementData> elms = new ArrayList<>();
    @JsonIgnore
    public static final Map<String, ScrElementData> elms_map = new HashMap<>();
    private static final List<DataObject> childs = new ArrayList<>();
    @JsonIgnore
    public String typeClass = null;


    /////////////////////////Basic Object Data////////////////////////////////////////////////////////
    public String name = null;
    public String type = null;
    @JsonIgnore
    public DataObject parent = null;

    public static List<DataObject> getChilds() {
        return childs;
    }

    public boolean add() {
        return true;
    }
}
