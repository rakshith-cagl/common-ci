package com.iexceed.screenDef.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class groups {
    private String[] name;
    private Integer[] noOfNodes;
    private String[] nodes;
    private Integer[] noOfContainers;
    private String[] containers;

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
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

    public String[] getContainers() {
        return containers;
    }

    public void setContainers(String[] containers) {
        this.containers = containers;
    }

    public Integer[] getNoOfContainers() {
        return noOfContainers;
    }

    public void setNoOfContainers(Integer[] noOfContainers) {
        this.noOfContainers = noOfContainers;
    }


}
