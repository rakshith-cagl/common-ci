package com.iexceed.citi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Elements {
    @JsonProperty("Control_Id")
    private String controlId;
    @JsonProperty("Control_Text")
    private String controlText;
    @JsonProperty("Control_Node")
    private String controlNode;
    @JsonProperty("Control_Name")
    private String controlName;
    @JsonProperty("Control_Width")
    private String controlWidth;
    @JsonProperty("Control_CustomWidth")
    private String controlCustomWidth;
    @JsonProperty("Control_CustomHeight")
    private String controlCustomHeight;
    @JsonProperty("Control_Type")
    private String controlType;
    @JsonProperty("Control_Text_Type")
    private String controlTextType;
    @JsonProperty("Control_DataType")
    private String controlDataType;
    @JsonProperty("Control_Visible")
    private String controlVisible;
    @JsonProperty("Grid_Column_Sorting")
    private String gridColumnSorting;
    @JsonProperty("Image_Name")
    private String imageName;
    @JsonProperty("ICON")
    private String iCON;
    @JsonProperty("Control_Action")
    private String controlAction;
    @JsonProperty("Control_Method")
    private String controlMethod;
    @JsonProperty("Control_DateFormat")
    private String controlDateFormat;
    @JsonProperty("Control_DateType")
    private String controlDateType;
    @JsonProperty("Grid_Column_Filtering")
    private boolean gridColumnFiltering;
    @JsonProperty("Control_DefaultValue")
    private String controlDefaultValue;
    @JsonProperty("Control_Required")
    private String controlRequired;
    @JsonProperty("Control_State")
    private String controlState;
    @JsonProperty("Control_Email")
    private String controlEmail;
    @JsonProperty("Control_MinVal")
    private String controlMinVal;
    @JsonProperty("Control_MaxVal")
    private String controlMaxVal;
    @JsonProperty("Control_MaxLength")
    private String controlMaxLength;
    @JsonProperty("Control_TextPattern")
    private String controlTextPattern;
    @JsonProperty("Control_Tooltip")
    private String controlTooltip;
    @JsonProperty("Control_Options")
    private List<ControlOptions> controloptions;
    @JsonProperty("Control_LabelWidth")
    private String controlLabelWidth;
    @JsonProperty("Control_PlaceHolder")
    private String controlPlaceHolder;
    @JsonProperty("Control_Indeterminate")
    private String controlIndeterminate;
    @JsonProperty("Control_Menu")
    private String controlMenu;
    @JsonProperty("Control_PopoverId")
    private String controlPopoverId;
    @JsonProperty("Control_Gauges")
    private List<ControlGauges> controlGauges;
    @JsonProperty("Control_Appearance")
    private String controlAppearance;
    @JsonProperty("Control_FontSize")
    private String controlFontSize;

    @JsonProperty("Control_Size")
    private String controlSize;

    @JsonProperty("Control_PopoverElement")
    private String controlPopoverElement;

    @JsonProperty("Control_Element")
    private String controlElement;

    @JsonProperty("Control_IconAlignment")
    private String controlIconAlignment;

    @JsonProperty("Control_Title")
    private String controlTitle;
    @JsonProperty("Control_ContentAlignment")
    private String controlContentAlignment;

    @JsonProperty("doNotFormat")
    private String doNotFormat;

    @JsonProperty("displayAsLiteral")
    private String displayAsLiteral;

    @JsonProperty("Control_Password")
    private String controlPassword;
    @JsonProperty("Control_IconPosition")
    private String controlIconPosition;
    @JsonProperty("Control_ButtonSize")
    private String controlButtonSize;
    @JsonProperty("cssclasses")
    private String cssclasses;

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }

    public String getControlText() {
        return controlText;
    }

    public void setControlText(String controlText) {
        this.controlText = controlText;
    }

    public String getControlNode() {
        return controlNode;
    }

    public void setControlNode(String controlNode) {
        this.controlNode = controlNode;
    }

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    public String getControlWidth() {
        return controlWidth;
    }

    public void setControlWidth(String controlWidth) {
        this.controlWidth = controlWidth;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getControlDataType() {
        return controlDataType;
    }

    public void setControlDataType(String controlDataType) {
        this.controlDataType = controlDataType;
    }

    public String getControlVisible() {
        return controlVisible;
    }

    public void setControlVisible(String controlVisible) {
        this.controlVisible = controlVisible;
    }

    public String getGridColumnSorting() {
        return gridColumnSorting;
    }

    public void setGridColumnSorting(String gridColumnSorting) {
        this.gridColumnSorting = gridColumnSorting;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getiCON() {
        return iCON;
    }

    public void setiCON(String iCON) {
        this.iCON = iCON;
    }

    public String getControlAction() {
        return controlAction;
    }

    public void setControlAction(String controlAction) {
        this.controlAction = controlAction;
    }

    public String getControlMethod() {
        return controlMethod;
    }

    public void setControlMethod(String controlMethod) {
        this.controlMethod = controlMethod;
    }

    public String getControlDateFormat() {
        return controlDateFormat;
    }

    public void setControlDateFormat(String controlDateFormat) {
        this.controlDateFormat = controlDateFormat;
    }

    public boolean isGridColumnFiltering() {
        return gridColumnFiltering;
    }

    public void setGridColumnFiltering(boolean gridColumnFiltering) {
        this.gridColumnFiltering = gridColumnFiltering;
    }

    public String getControlDefaultValue() {
        return controlDefaultValue;
    }

    public void setControlDefaultValue(String controlDefaultValue) {
        this.controlDefaultValue = controlDefaultValue;
    }

    public String getControlRequired() {
        return controlRequired;
    }

    public void setControlRequired(String controlRequired) {
        this.controlRequired = controlRequired;
    }

    public String getControlState() {
        return controlState;
    }

    public void setControlState(String controlState) {
        this.controlState = controlState;
    }

    public String getControlMinVal() {
        return controlMinVal;
    }

    public void setControlMinVal(String controlMinVal) {
        this.controlMinVal = controlMinVal;
    }

    public String getControlMaxVal() {
        return controlMaxVal;
    }

    public void setControlMaxVal(String controlMaxVal) {
        this.controlMaxVal = controlMaxVal;
    }

    public String getControlMaxLength() {
        return controlMaxLength;
    }

    public void setControlMaxLength(String controlMaxLength) {
        this.controlMaxLength = controlMaxLength;
    }

    public String getControlTextPattern() {
        return controlTextPattern;
    }

    public void setControlTextPattern(String controlTextPattern) {
        this.controlTextPattern = controlTextPattern;
    }

    public String getControlTooltip() {
        return controlTooltip;
    }

    public void setControlTooltip(String controlTooltip) {
        this.controlTooltip = controlTooltip;
    }

    public List<ControlOptions> getControloptions() {
        return controloptions;
    }

    public void setControloptions(List<ControlOptions> controloptions) {
        this.controloptions = controloptions;
    }

    public String getControlLabelWidth() {
        return controlLabelWidth;
    }

    public void setControlLabelWidth(String controlLabelWidth) {
        this.controlLabelWidth = controlLabelWidth;
    }

    public String getControlPlaceHolder() {
        return controlPlaceHolder;
    }

    public void setControlPlaceHolder(String controlPlaceHolder) {
        this.controlPlaceHolder = controlPlaceHolder;
    }

    public String getControlIndeterminate() {
        return controlIndeterminate;
    }

    public void setControlIndeterminate(String controlIndeterminate) {
        this.controlIndeterminate = controlIndeterminate;
    }

    public String getControlMenu() {
        return controlMenu;
    }

    public void setControlMenu(String controlMenu) {
        this.controlMenu = controlMenu;
    }

    public String getControlPopoverId() {
        return controlPopoverId;
    }

    public void setControlPopoverId(String controlPopoverId) {
        this.controlPopoverId = controlPopoverId;
    }

    public String getControlPopoverElement() {
        return controlPopoverElement;
    }

    public void setControlPopoverElement(String controlPopoverElement) {
        this.controlPopoverElement = controlPopoverElement;
    }

    public List<ControlGauges> getControlGauges() {
        return controlGauges;
    }

    public void setControlGauges(List<ControlGauges> controlGauges) {
        this.controlGauges = controlGauges;
    }

    public String getControlElement() {
        return controlElement;
    }

    public void setControlElement(String controlElement) {
        this.controlElement = controlElement;
    }


    public String getControlAppearance() {
        return controlAppearance;
    }

    public void setControlAppearance(String controlAppearance) {
        this.controlAppearance = controlAppearance;
    }

    public String getControlFontSize() {
        return controlFontSize;
    }

    public void setControlFontSize(String controlFontSize) {
        this.controlFontSize = controlFontSize;
    }

    public String getControlSize() {
        return controlSize;
    }

    public void setControlSize(String controlSize) {
        this.controlSize = controlSize;
    }

    public String getControlCustomWidth() {
        return controlCustomWidth;
    }

    public void setControlCustomWidth(String controlCustomWidth) {
        this.controlCustomWidth = controlCustomWidth;
    }

    public String getControlCustomHeight() {
        return controlCustomHeight;
    }

    public void setControlCustomHeight(String controlCustomHeight) {
        this.controlCustomHeight = controlCustomHeight;
    }

    public String getControlIconAlignment() {
        return controlIconAlignment;
    }

    public void setControlIconAlignment(String controlIconAlignment) {
        this.controlIconAlignment = controlIconAlignment;
    }

    public String getControlTitle() {
        return controlTitle;
    }

    public void setControlTitle(String controlTitle) {
        this.controlTitle = controlTitle;
    }

    public String getControlContentAlignment() {
        return controlContentAlignment;
    }

    public void setControlContentAlignment(String controlContentAlignment) {
        this.controlContentAlignment = controlContentAlignment;
    }

    public String getControlPassword() {
        return controlPassword;
    }

    public void setControlPassword(String controlPassword) {
        this.controlPassword = controlPassword;
    }

    public String getControlIconPosition() {
        return controlIconPosition;
    }

    public void setControlIconPosition(String controlIconPosition) {
        this.controlIconPosition = controlIconPosition;
    }

    public String getControlButtonSize() {
        return controlButtonSize;
    }

    public void setControlButtonSize(String controlButtonSize) {
        this.controlButtonSize = controlButtonSize;
    }

    public String getCssclasses() {
        return cssclasses;
    }

    public void setCssclasses(String cssclasses) {
        this.cssclasses = cssclasses;
    }

    public String getControlEmail() {
        return controlEmail;
    }

    public void setControlEmail(String controlEmail) {
        this.controlEmail = controlEmail;
    }

    public String getControlTextType() {
        return controlTextType;
    }

    public void setControlTextType(String controlTextType) {
        this.controlTextType = controlTextType;
    }

    public String getDoNotFormat() {
        return doNotFormat;
    }

    public void setDoNotFormat(String doNotFormat) {
        this.doNotFormat = doNotFormat;
    }

    public String getDisplayAsLiteral() {
        return displayAsLiteral;
    }

    public void setDisplayAsLiteral(String displayAsLiteral) {
        this.displayAsLiteral = displayAsLiteral;
    }

    public String getControlDateType() {
        return controlDateType;
    }

    public void setControlDateType(String controlDateType) {
        this.controlDateType = controlDateType;
    }


}
