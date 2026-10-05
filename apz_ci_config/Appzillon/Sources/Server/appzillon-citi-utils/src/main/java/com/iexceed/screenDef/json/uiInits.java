package com.iexceed.screenDef.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class uiInits {
    private String[][] date;
    private String[][] dropDown;
    private String[][] checkbox;
    private String[][] tags;
    private String[][] table;
    //	private String [][] dropdownWithInput;
    private String[][] contextMenu;
    private String[][] popover;

    public String[][] getDate() {
        return date;
    }

    public void setDate(String[][] date) {
        this.date = date;
    }

    public String[][] getDropDown() {
        return dropDown;
    }

    public void setDropDown(String[][] dropDown) {
        this.dropDown = dropDown;
    }

    public String[][] getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(String[][] checkbox) {
        this.checkbox = checkbox;
    }

    public String[][] getTags() {
        return tags;
    }

    public void setTags(String[][] tags) {
        this.tags = tags;
    }

    public String[][] getTable() {
        return table;
    }

    public void setTable(String[][] table) {
        this.table = table;
    }

    //	public String[][] getDropdownWithInput() {
//		return dropdownWithInput;
//	}
//	public void setDropdownWithInput(String[][] dropdownWithInput) {
//		this.dropdownWithInput = dropdownWithInput;
//	}
    public String[][] getContextMenu() {
        return contextMenu;
    }

    public void setContextMenu(String[][] contextMenu) {
        this.contextMenu = contextMenu;
    }

    public String[][] getPopover() {
        return popover;
    }

    public void setPopover(String[][] popover) {
        this.popover = popover;
    }


}
