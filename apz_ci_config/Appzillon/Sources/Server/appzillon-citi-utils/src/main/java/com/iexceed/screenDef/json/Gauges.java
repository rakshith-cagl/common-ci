package com.iexceed.screenDef.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gauges {

    private String name;
    private String gaugeType;
    private String widgetCategory;
    private String lowerLimit;
    private String upperLimit;
    private String numberPrefix;
    private String numberSuffix;
    private String caption;
    private String[] gaugeMinRange;
    private String[] gaugeMaxRange;
    private String[] gaugeRangeColor;
    private String[] gaugeValueNode;
    private String[] gaugeValueElement;
    private String[] gaugeValueType;
    private String[] nodes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGaugeType() {
        return gaugeType;
    }

    public void setGaugeType(String gaugeType) {
        this.gaugeType = gaugeType;
    }

    public String getWidgetCategory() {
        return widgetCategory;
    }

    public void setWidgetCategory(String widgetCategory) {
        this.widgetCategory = widgetCategory;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getNumberPrefix() {
        return numberPrefix;
    }

    public void setNumberPrefix(String numberPrefix) {
        this.numberPrefix = numberPrefix;
    }

    public String getNumberSuffix() {
        return numberSuffix;
    }

    public void setNumberSuffix(String numberSuffix) {
        this.numberSuffix = numberSuffix;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String[] getGaugeMinRange() {
        return gaugeMinRange;
    }

    public void setGaugeMinRange(String[] gaugeMinRange) {
        this.gaugeMinRange = gaugeMinRange;
    }

    public String[] getGaugeMaxRange() {
        return gaugeMaxRange;
    }

    public void setGaugeMaxRange(String[] gaugeMaxRange) {
        this.gaugeMaxRange = gaugeMaxRange;
    }

    public String[] getGaugeRangeColor() {
        return gaugeRangeColor;
    }

    public void setGaugeRangeColor(String[] gaugeRangeColor) {
        this.gaugeRangeColor = gaugeRangeColor;
    }

    public String[] getGaugeValueNode() {
        return gaugeValueNode;
    }

    public void setGaugeValueNode(String[] gaugeValueNode) {
        this.gaugeValueNode = gaugeValueNode;
    }

    public String[] getGaugeValueElement() {
        return gaugeValueElement;
    }

    public void setGaugeValueElement(String[] gaugeValueElement) {
        this.gaugeValueElement = gaugeValueElement;
    }

    public String[] getGaugeValueType() {
        return gaugeValueType;
    }

    public void setGaugeValueType(String[] gaugeValueType) {
        this.gaugeValueType = gaugeValueType;
    }

    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }


}
