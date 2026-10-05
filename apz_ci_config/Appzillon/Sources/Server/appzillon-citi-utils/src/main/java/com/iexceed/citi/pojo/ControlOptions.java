package com.iexceed.citi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlOptions {
    @JsonProperty("value")
    private String value;
    @JsonProperty("Desc")
    private String desc;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


}
