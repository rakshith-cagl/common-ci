package com.iexceed.screenDef.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class containers {

    private String[] name;
    private String[] type;
    private String[] custom;
    private String[] multiRec;
    private String[] ro;
    private String[] pgStyle;
    private Integer[] pgSize;
    private String[] childs;
    private Integer[] noOfIfaces;
    private String[] ifaces;
    private Integer[] noOfNodes;
    private String[] nodes;
    private int[] noOfElms;
    private String[] ui;
    private String[] elms;
    private String[] eUi;
    private String[] eCustom;
    private String[] elmType;
    private String[] eRo;
    private String[] eEmail;
    private String[] eMask;
    private String[] eRtFmt;
    private String[] eSkipFmt;
    private String[] eLiteral;
    private String[] eLovId;
    private String[] eLovWidth;
    private String[] eLovMinWidth;
    private String[] eLovRf;
    private Integer[] noOfeLovRf;
    private String[] eLovBv;
    private Integer[] noOfeLovBv;


    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String[] getCustom() {
        return custom;
    }

    public void setCustom(String[] custom) {
        this.custom = custom;
    }

    public String[] getMultiRec() {
        return multiRec;
    }

    public void setMultiRec(String[] multiRec) {
        this.multiRec = multiRec;
    }

    public String[] getRo() {
        return ro;
    }

    public void setRo(String[] ro) {
        this.ro = ro;
    }

    public String[] getPgStyle() {
        return pgStyle;
    }

    public void setPgStyle(String[] pgStyle) {
        this.pgStyle = pgStyle;
    }

    public Integer[] getPgSize() {
        return pgSize;
    }

    public void setPgSize(Integer[] pgSize) {
        this.pgSize = pgSize;
    }

    public String[] getChilds() {
        return childs;
    }

    public void setChilds(String[] childs) {
        this.childs = childs;
    }

    public Integer[] getNoOfIfaces() {
        return noOfIfaces;
    }

    public void setNoOfIfaces(Integer[] noOfIfaces) {
        this.noOfIfaces = noOfIfaces;
    }

    public String[] getIfaces() {
        return ifaces;
    }

    public void setIfaces(String[] ifaces) {
        this.ifaces = ifaces;
    }

    public Integer[] getNoOfNodes() {
        return noOfNodes;
    }

    public void setNoOfNodes(Integer[] noOfNodes) {
        this.noOfNodes = noOfNodes;
    }

    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }

    public int[] getNoOfElms() {
        return noOfElms;
    }

    public void setNoOfElms(int[] noOfElms) {
        this.noOfElms = noOfElms;
    }

    public String[] getUi() {
        return ui;
    }

    public void setUi(String[] ui) {
        this.ui = ui;
    }

    public String[] getElms() {
        return elms;
    }

    public void setElms(String[] elms) {
        this.elms = elms;
    }

    public String[] geteUi() {
        return eUi;
    }

    public void seteUi(String[] eUi) {
        this.eUi = eUi;
    }

    public String[] geteCustom() {
        return eCustom;
    }

    public void seteCustom(String[] eCustom) {
        this.eCustom = eCustom;
    }

    public String[] getElmType() {
        return elmType;
    }

    public void setElmType(String[] elmType) {
        this.elmType = elmType;
    }

    public String[] geteRo() {
        return eRo;
    }

    public void seteRo(String[] eRo) {
        this.eRo = eRo;
    }

    public String[] geteMask() {
        return eMask;
    }

    public void seteMask(String[] eMask) {
        this.eMask = eMask;
    }

    public String[] geteRtFmt() {
        return eRtFmt;
    }

    public void seteRtFmt(String[] eRtFmt) {
        this.eRtFmt = eRtFmt;
    }

    public String[] geteSkipFmt() {
        return eSkipFmt;
    }

    public void seteSkipFmt(String[] eSkipFmt) {
        this.eSkipFmt = eSkipFmt;
    }

    public String[] geteLiteral() {
        return eLiteral;
    }

    public void seteLiteral(String[] eLiteral) {
        this.eLiteral = eLiteral;
    }

    public String[] geteLovId() {
        return eLovId;
    }

    public void seteLovId(String[] eLovId) {
        this.eLovId = eLovId;
    }

    public String[] geteLovWidth() {
        return eLovWidth;
    }

    public void seteLovWidth(String[] eLovWidth) {
        this.eLovWidth = eLovWidth;
    }

    public String[] geteLovMinWidth() {
        return eLovMinWidth;
    }

    public void seteLovMinWidth(String[] eLovMinWidth) {
        this.eLovMinWidth = eLovMinWidth;
    }

    public String[] geteLovRf() {
        return eLovRf;
    }

    public void seteLovRf(String[] eLovRf) {
        this.eLovRf = eLovRf;
    }

    public String[] geteLovBv() {
        return eLovBv;
    }

    public void seteLovBv(String[] eLovBv) {
        this.eLovBv = eLovBv;
    }

    public Integer[] getNoOfeLovRf() {
        return noOfeLovRf;
    }

    public void setNoOfeLovRf(Integer[] noOfeLovRf) {
        this.noOfeLovRf = noOfeLovRf;
    }

    public Integer[] getNoOfeLovBv() {
        return noOfeLovBv;
    }

    public void setNoOfeLovBv(Integer[] noOfeLovBv) {
        this.noOfeLovBv = noOfeLovBv;
    }

    public String[] geteEmail() {
        return eEmail;
    }

    public void seteEmail(String[] eEmail) {
        this.eEmail = eEmail;
    }


}
