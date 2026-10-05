package com.iexceed.interfaces.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "typeclass",
        "name",
        "type",
        "childs",
        "relationtype",
        "allrecords",
        "pkgenstrategy",
        "portion"
})
public class DataModel {
    private String typeclass;
    private String name;
    private String type;
    private String relationtype;
    private String allrecords;
    private String pkgenstrategy;
    private String portion;
    private List<Node> node;

    public String getTypeclass() {
        return typeclass;
    }

    public void setTypeclass(String typeclass) {
        this.typeclass = typeclass;
    }

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

    public String getRelationtype() {
        return relationtype;
    }

    public void setRelationtype(String relationtype) {
        this.relationtype = relationtype;
    }

    public String getAllrecords() {
        return allrecords;
    }

    public void setAllrecords(String allrecords) {
        this.allrecords = allrecords;
    }

    public String getPkgenstrategy() {
        return pkgenstrategy;
    }

    public void setPkgenstrategy(String pkgenstrategy) {
        this.pkgenstrategy = pkgenstrategy;
    }

    public String getPortion() {
        return portion;
    }

    public void setPortion(String portion) {
        this.portion = portion;
    }

    public List<Node> getNode() {
        return node;
    }

    @JsonProperty("childs")
    public void setNode(List<Node> node) {
        this.node = node;
    }

}
