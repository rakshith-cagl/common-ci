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
        "externalname",
        "allrecords",
        "pkgen_strategy",
        "portion"
})
public class Node {

    private String typeclass = "NODE";
    private String name;
    private String type = "NODE";
    private List<Element> element;
    private String relationtype;
    private String externalname;
    private String allrecords;
    private String pkgen_strategy;
    private String portion;

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

    public List<Element> getElement() {
        return element;
    }

    @JsonProperty("childs")
    public void setElement(List<Element> element) {
        this.element = element;
    }

    public String getRelationtype() {
        return relationtype;
    }

    public void setRelationtype(String relationtype) {
        this.relationtype = relationtype;
    }

    public String getExternalname() {
        return externalname;
    }

    public void setExternalname(String externalname) {
        this.externalname = externalname;
    }

    public String getAllrecords() {
        return allrecords;
    }

    public void setAllrecords(String allrecords) {
        this.allrecords = allrecords;
    }

    public String getPkgen_strategy() {
        return pkgen_strategy;
    }

    public void setPkgen_strategy(String pkgen_strategy) {
        this.pkgen_strategy = pkgen_strategy;
    }

    public String getPortion() {
        return portion;
    }

    public void setPortion(String portion) {
        this.portion = portion;
    }

}
