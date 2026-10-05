package com.iexceed.citi.pojo;

public class CitiIntfNodes {
    private String nodeType;
    private String nodeParent;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeParent() {
        return nodeParent;
    }

    public void setNodeParent(String nodeParent) {
        this.nodeParent = nodeParent;
    }

    @Override
    public String toString() {
        return "CitiIntfNodes [nodeType=" + nodeType + ", nodeParent=" + nodeParent + "]";
    }


}
