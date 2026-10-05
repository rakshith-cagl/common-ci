package com.iexceed.intf.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Arrays;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntfDefinition {
    private String name;
    private String type;
    private String dateFormat;
    private String dateTimeFormat;
    private String timeFormat;
    private String offline;
    private String amountMask;
    private String session;
    private String correctReq;
    private int noOfReqNodes;
    private String correctRes;
    private int noOfResNodes;
    private int noOFaultNodes;
    private String[] nodes;
    private Integer[] noOfNodeElms;
    private String[] nExtName;
    private String[] nMultiRec;
    private String[] nParent;
    private String[] nParents;
    private String[] nChilds;
    private String[] nRelType;
    private String[] nMrParent;
    private String[] elms;
    private String[] eExtName;
    private String[] eDataType;
    private String[] eMinVal;
    private String[] eMaxVal;
    private String[] eMaxDec;
    private String[] eLenType;
    private String[] eMinLen;
    private String[] eMaxLen;
    private String[] eArr;
    private String[] ePattern;
    private String[] eMand;
    private String[] eRelNode;
    private String[] eRelElm;
    @JsonIgnore
    private String[] nodeType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getOffline() {
        return offline;
    }

    public void setOffline(String offline) {
        this.offline = offline;
    }

    public String getAmountMask() {
        return amountMask;
    }

    public void setAmountMask(String amountMask) {
        this.amountMask = amountMask;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCorrectReq() {
        return correctReq;
    }

    public void setCorrectReq(String correctReq) {
        this.correctReq = correctReq;
    }

    public String getCorrectRes() {
        return correctRes;
    }

    public void setCorrectRes(String correctRes) {
        this.correctRes = correctRes;
    }

    public int getNoOfReqNodes() {
        return noOfReqNodes;
    }

    public void setNoOfReqNodes(int noOfReqNodes) {
        this.noOfReqNodes = noOfReqNodes;
    }

    public int getNoOfResNodes() {
        return noOfResNodes;
    }

    public void setNoOfResNodes(int noOfResNodes) {
        this.noOfResNodes = noOfResNodes;
    }

    public int getNoOFaultNodes() {
        return noOFaultNodes;
    }

    public void setNoOFaultNodes(int noOFaultNodes) {
        this.noOFaultNodes = noOFaultNodes;
    }

    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }

    public Integer[] getNoOfNodeElms() {
        return noOfNodeElms;
    }

    public void setNoOfNodeElms(Integer[] noOfNodeElms) {
        this.noOfNodeElms = noOfNodeElms;
    }

    public String[] getnExtName() {
        return nExtName;
    }

    public void setnExtName(String[] nExtName) {
        this.nExtName = nExtName;
    }

    public String[] getnMultiRec() {
        return nMultiRec;
    }

    public void setnMultiRec(String[] nMultiRec) {
        this.nMultiRec = nMultiRec;
    }

    public String[] getnParent() {
        return nParent;
    }

    public void setnParent(String[] nParent) {
        this.nParent = nParent;
    }

    public String[] getnParents() {
        return nParents;
    }

    public void setnParents(String[] nParents) {
        this.nParents = nParents;
    }

    public String[] getnChilds() {
        return nChilds;
    }

    public void setnChilds(String[] nChilds) {
        this.nChilds = nChilds;
    }

    public String[] getnRelType() {
        return nRelType;
    }

    public void setnRelType(String[] nRelType) {
        this.nRelType = nRelType;
    }

    public String[] getnMrParent() {
        return nMrParent;
    }

    public void setnMrParent(String[] nMrParent) {
        this.nMrParent = nMrParent;
    }

    public String[] getElms() {
        return elms;
    }

    public void setElms(String[] elms) {
        this.elms = elms;
    }

    public String[] geteExtName() {
        return eExtName;
    }

    public void seteExtName(String[] eExtName) {
        this.eExtName = eExtName;
    }

    public String[] geteDataType() {
        return eDataType;
    }

    public void seteDataType(String[] eDataType) {
        this.eDataType = eDataType;
    }

    public String[] geteMinVal() {
        return eMinVal;
    }

    public void seteMinVal(String[] eMinVal) {
        this.eMinVal = eMinVal;
    }

    public String[] geteMaxVal() {
        return eMaxVal;
    }

    public void seteMaxVal(String[] eMaxVal) {
        this.eMaxVal = eMaxVal;
    }

    public String[] geteMaxDec() {
        return eMaxDec;
    }

    public void seteMaxDec(String[] eMaxDec) {
        this.eMaxDec = eMaxDec;
    }

    public String[] geteLenType() {
        return eLenType;
    }

    public void seteLenType(String[] eLenType) {
        this.eLenType = eLenType;
    }

    public String[] geteMinLen() {
        return eMinLen;
    }

    public void seteMinLen(String[] eMinLen) {
        this.eMinLen = eMinLen;
    }

    public String[] geteMaxLen() {
        return eMaxLen;
    }

    public void seteMaxLen(String[] eMaxLen) {
        this.eMaxLen = eMaxLen;
    }

    public String[] geteArr() {
        return eArr;
    }

    public void seteArr(String[] eArr) {
        this.eArr = eArr;
    }

    public String[] getePattern() {
        return ePattern;
    }

    public void setePattern(String[] ePattern) {
        this.ePattern = ePattern;
    }

    public String[] geteMand() {
        return eMand;
    }

    public void seteMand(String[] eMand) {
        this.eMand = eMand;
    }

    public String[] geteRelNode() {
        return eRelNode;
    }

    public void seteRelNode(String[] eRelNode) {
        this.eRelNode = eRelNode;
    }

    public String[] geteRelElm() {
        return eRelElm;
    }

    public void seteRelElm(String[] eRelElm) {
        this.eRelElm = eRelElm;
    }

    public String[] getNodeType() {
        return nodeType;
    }

    public void setNodeType(String[] nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String toString() {
        return "IntfDefinition [name=" + name + ", type=" + type + ", dateFormat=" + dateFormat + ", dateTimeFormat="
                + dateTimeFormat + ", timeFormat=" + timeFormat + ", offline=" + offline + ", amountMask=" + amountMask
                + ", session=" + session + ", correctReq=" + correctReq + ", noOfReqNodes=" + noOfReqNodes
                + ", correctRes=" + correctRes + ", noOfResNodes=" + noOfResNodes + ", noOFaultNodes=" + noOFaultNodes
                + ", nodes=" + Arrays.toString(nodes) + ", noOfNodeElms=" + Arrays.toString(noOfNodeElms)
                + ", nExtName=" + Arrays.toString(nExtName) + ", nMultiRec=" + Arrays.toString(nMultiRec) + ", nParent="
                + Arrays.toString(nParent) + ", nParents=" + Arrays.toString(nParents) + ", nChilds="
                + Arrays.toString(nChilds) + ", nRelType=" + Arrays.toString(nRelType) + ", nMrParent="
                + Arrays.toString(nMrParent) + ", elms=" + Arrays.toString(elms) + ", eExtName="
                + Arrays.toString(eExtName) + ", eDataType=" + Arrays.toString(eDataType) + ", eMinVal="
                + Arrays.toString(eMinVal) + ", eMaxVal=" + Arrays.toString(eMaxVal) + ", eMaxDec="
                + Arrays.toString(eMaxDec) + ", eLenType=" + Arrays.toString(eLenType) + ", eMinLen="
                + Arrays.toString(eMinLen) + ", eMaxLen=" + Arrays.toString(eMaxLen) + ", eArr=" + Arrays.toString(eArr)
                + ", ePattern=" + Arrays.toString(ePattern) + ", eMand=" + Arrays.toString(eMand) + ", eRelNode="
                + Arrays.toString(eRelNode) + ", eRelElm=" + Arrays.toString(eRelElm) + ", nodeType="
                + Arrays.toString(nodeType) + "]";
    }


}