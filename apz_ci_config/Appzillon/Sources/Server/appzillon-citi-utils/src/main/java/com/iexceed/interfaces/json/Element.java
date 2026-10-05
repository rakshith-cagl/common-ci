package com.iexceed.interfaces.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "typeclass",
        "name",
        "type",
        "externalname",
        "validationtype",
        "lengthtype",
        "paddingtype",
        "cents",
        "datatype",
        "mandatory",
        "keycolumn",
        "allowmodification",
        "arrays",
        "autogenerate",
        "translaterqd"
})
public class Element {

    private String typeclass = "ELEMENT";
    private String name;
    private String type = "ELEMENT";
    private String externalname;
    private String validationtype;
    private String lengthtype;
    private String paddingtype;
    private String cents;
    private String datatype;
    private String mandatory;
    private String keycolumn;
    private String allowmodification;
    private String arrays;
    private String autogenerate;
    private String translaterqd;

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

    public String getExternalname() {
        return externalname;
    }

    public void setExternalname(String externalname) {
        this.externalname = externalname;
    }

    public String getValidationtype() {
        return validationtype;
    }

    public void setValidationtype(String validationtype) {
        this.validationtype = validationtype;
    }

    public String getLengthtype() {
        return lengthtype;
    }

    public void setLengthtype(String lengthtype) {
        this.lengthtype = lengthtype;
    }

    public String getPaddingtype() {
        return paddingtype;
    }

    public void setPaddingtype(String paddingtype) {
        this.paddingtype = paddingtype;
    }

    public String getCents() {
        return cents;
    }

    public void setCents(String cents) {
        this.cents = cents;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getKeycolumn() {
        return keycolumn;
    }

    public void setKeycolumn(String keycolumn) {
        this.keycolumn = keycolumn;
    }

    public String getAllowmodification() {
        return allowmodification;
    }

    public void setAllowmodification(String allowmodification) {
        this.allowmodification = allowmodification;
    }

    public String getArrays() {
        return arrays;
    }

    public void setArrays(String arrays) {
        this.arrays = arrays;
    }

    public String getAutogenerate() {
        return autogenerate;
    }

    public void setAutogenerate(String autogenerate) {
        this.autogenerate = autogenerate;
    }

    public String getTranslaterqd() {
        return translaterqd;
    }

    public void setTranslaterqd(String translaterqd) {
        this.translaterqd = translaterqd;
    }
}
