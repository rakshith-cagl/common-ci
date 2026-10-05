/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
    var customwidgets = {};
}
customwidgets.DropDownasButton = {};
customwidgets.DropDownasButton.currobj = null;
/************************************************************************
 Below function is called from layout.loadPropPanel() to initialize the widget related functions.
 Functions initializations are done here.
 Functions here are called on certain events like onblur or onchange of the widget properties.
 Consider the widget have a property as Title with id as title
 Then the initialization looks like this :
 $("#title").blur(function() {
 changeTitle();
 });
 Provided the function changeTitle() in the js which changes the title of the widget
 ************************************************************************/
customwidgets.DropDownasButton.init = function(pobject) {
    customwidgets.DropDownasButton.currobj = pobject;
    var currobjary = designer.selectedobjs;
    var currobjspntr = new Array();
    for (var i = 0; i < currobjary.length; i++) {
        currobjspntr[i] = utils.getDataPointer(currobjary[i]);
    }
    //Apz312 changes start
    $("#mainPropCollapse").click(function() {
        $("#mainPropAccordian").toggle();
    });
    $("#additionalpropcollapse").click(function() {
        $("#additionalprop").toggle();
    });
    $("#eventscollapse").click(function() {
        $("#eventsaccordian").toggle();
    });
    $("#title").blur(function() {
        DropDownasButton_title();
    });
    $("#width").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("width", ldatapointer);
                try {
                    DropDownasButton_colwidth(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_colwidth();
        }
    });
    $("#customwidth").blur(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("customwidth", ldatapointer);
                try {
                    DropDownasButton_customwidth(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_customwidth();
        }
    });
    $("#customwidthtype").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("customwidthtype", ldatapointer);
                try {
                    DropDownasButton_customwidth(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_customwidth();
        }
    });
    $("#labelwidth").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("labelwidth", ldatapointer);
                try {
                    DropDownasButton_labelwidth(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_labelwidth();
        }
    });
    $("#cssclasses").blur(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("cssclasses", ldatapointer);
            }
        }
    });
    $("#contentalignment").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("contentalignment", ldatapointer);
                try {
                    DropDownasButton_conallign(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_conallign();
        }
    });
    $("#state").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("state", ldatapointer);
                try {
                    DropDownasButton_state(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_state();
        }
    });
    /*$("#semantics").change(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("semantics", ldatapointer);
                try {
                    DropDownasButton_semantics(ldatapointer);
                } catch (e) {}
            }
        }
        else{
            DropDownasButton_semantics();
        }
    }); */
    $("#defaultvalue").blur(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("defaultvalue", ldatapointer);
                try {
                    DropDownasButton_defaultvalue(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_defaultvalue();
        }
    });
    $("#options").change(function() {
        var lval = $("#options").prop("checked") ? "Y" : "N";
        $("#options")[0].value = lval;
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("options", ldatapointer);
                try {
                    widgetutils.setWidgetVisibility(ldatapointer);
                } catch (e) {}
            }
        } else {
            widgetutils.setWidgetVisibility();
        }
    });
 
    //Apz312 changes end
    $("#selectall").click(function() {
        widgetutils.setMultipleCheckboxes(this, "selectevent");
    });
    $("#headeralignment").change(function() {
        var lval = $("#headeralignment").prop("checked") ? "Y" : "N";
        $("#headeralignment")[0].value = lval;
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("headeralignment", ldatapointer);
                try {
                    DropDownasButton_headerAlign(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_headerAlign();
        }
    });
    $("#appearance").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("appearance", ldatapointer);
                try {
                    DropDownasButton_appearance(ldatapointer);
                } catch (e) {}
            }
        } else {
            DropDownasButton_appearance();
        }
    });
    $("#elementname").change(function() {
		var linterfaceName = document.getElementById('interfacename').value;
		var ldatamodeltype = document.getElementById('datamodeltype').value;
		var lnodename = document.getElementById('nodename').value;
		var lelmvalue = document.getElementById('elementname').value;
		widgetutils.resetDataModalProps(linterfaceName, ldatamodeltype, lnodename, lelmvalue);
		if(widgetutils.isElementUsed(linterfaceName, ldatamodeltype, lnodename, lelmvalue)){
			return false;
		}
		widgetutils.loadName(linterfaceName, ldatamodeltype, lnodename, lelmvalue);
		var previouselename = designer.elementname;
		DropDownasButton_setStaticValuesDropdown(linterfaceName, ldatamodeltype, lnodename, lelmvalue, previouselename);
		widgetutils.setElementProperties(linterfaceName, ldatamodeltype, lnodename, lelmvalue);
		designer.interfacename = linterfaceName;
		designer.datamodeltype = ldatamodeltype;
		designer.nodename = lnodename;
		designer.elementname = lelmvalue;
		$("#datatype").trigger("change");
	});
};
/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.DropDownasButton.getTemplate = function(pobject, pparentobject,receviedForChild,uiParentObj) {
    var widgethtml = "";
    var parentContainer = utils.getContainer(pparentobject).getAttribute("widgettype");
    var parentContId = utils.getContainer(pparentobject).id;
    if(parentContainer == "TABLE") {
		var header = $("#"+parentContId).find("#"+parentContId+"_header");
		var newHeader = "<th onclick='widgetutils.selectTableElement(this, event);'>Dropdown As Button</th>";
		var parentContainer = utils.getContainer(pparentobject);
		if(receviedForChild) {
			var $uiParentObj = $(uiParentObj);
			var parentIndex = $uiParentObj.parent().children().index(uiParentObj);
			var uiParentHeader = header.children()[parentIndex];
			$(uiParentHeader).before(newHeader);
		} else{
			header.append(newHeader);
		}
		widgethtml = "<td id ='appzillonid' class='apzlocontainer'>";
		var elmContent = '<div class="ett-drdn etb-drdn pri elmWidget"><button class="drdn">Select</button><span><svg class="ett-icon icon-down px34"><use  xlink:href="#icon-down"></use></svg></span><div class="drdn-list"><ul></ul></div></div>';
		widgethtml = widgethtml + elmContent;
		widgethtml = widgethtml + "</td>";
    } else if(parentContainer == "LIST" || parentContainer == "NAVBAR") {
        widgethtml = '<div id ="appzillonid" class="ett-drdn etb-drdn pri etw-50 elpad ebox apzlocontainer elmWidget" widgetcategory="elements" widgetid="DropdownAsButton" tabindex="0"><button class="drdn">Select</button><span><svg class="ett-icon icon-down px34"><use xlink:href="#icon-down"></use></svg></span><div class="drdn-list"><ul></ul></div></div>';
    } else {
        if(pparentobject.hasAttribute("role")) {
            var receivedTemplate = '<div class="ett-drdn etb-drdn pri etw-50 elmWidget"><button class="drdn">Select</button><span><svg class="ett-icon icon-down px34"><use xlink:href="#icon-down"></use></svg></span><div class="drdn-list"><ul></ul></div></div>';
			widgethtml = '<li id ="appzillonid" class="apzlocontainer elpad">';
			widgethtml = widgethtml + receivedTemplate;
			widgethtml = widgethtml + '</li>';
        } else {
            widgethtml = '<ul id ="appzillonid" class="apzlocontainer eoc srb"><li id="label" class="etw-40 "></li><li id="content" class="etw-60 eic"><div class="ett-drdn etb-drdn pri etw-50 elmWidget"><button class="drdn">Select</button><span><svg class="ett-icon icon-down px34"><use xlink:href="#icon-down"></use></svg></span><div class="drdn-list"><ul></ul></div></div></li></ul>';
        }
    }
	return widgethtml;
};

customwidgets.DropDownasButton.updateDefaultValues = function(pproperties) {
    document.getElementById('width').value = "50";
    document.getElementById('defaultvalue').value = "Select";
}
/************************************************************************
 Below function is called from layout.populateProperties() to populate the widget properties provided by the developer
 layout.populateProperties() will be called when the widget has been selected in the designer.
 Thats when the widget properties page will be populated.
	
 Send the widgets property id to the widgetutils.getPropety() function with 'pproperties' object
 So that the value stored in 'pproperties' object will be assigned to the element in the screen with the id which has been passed.
 Example: In below function id and pid are passed to the widgetutils.getProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.DropDownasButton.populateProperties = function(pproperties) {
    widgetutils.getProperty("id", pproperties);
    widgetutils.getProperty("pid", pproperties);
    widgetutils.getProperty("title", pproperties);
    DropDownasButton_displayElementsProperties(pproperties);
    widgetutils.getProperty("tooltip", pproperties);
    widgetutils.getProperty("width", pproperties);
    DropDownasButton_colwidth();
    widgetutils.getProperty("customwidth", pproperties);
    widgetutils.getProperty("customwidthtype", pproperties);
    widgetutils.getProperty("labelwidth", pproperties);
    widgetutils.getProperty("contentalignment", pproperties);
    widgetutils.getProperty("translatedefaultvalue", pproperties);
    widgetutils.getProperty("state", pproperties);
    //widgetutils.getProperty("semantics", pproperties);
    widgetutils.getProperty("options", pproperties);
    widgetutils.getProperty("hint", pproperties);
    widgetutils.getProperty("cssclasses", pproperties);
    widgetutils.getProperty("labelrequired", pproperties);
    widgetutils.getEventElements("eventstable", pproperties);
    DropDownasButton_displayElmRelatedProps();
    DropDownasButton_getDropdowns("staticoptions", pproperties);
    widgetutils.getProperty("appearance", pproperties);
    //DropDownasButton_defaultvalue(pproperties);
};
/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.DropDownasButton.saveProperties = function(pproperties) {
    widgetutils.setProperty("id", pproperties);
    widgetutils.setProperty("pid", pproperties);
    widgetutils.setProperty("title", pproperties);
    widgetutils.setProperty("width", pproperties);
    widgetutils.setProperty("customwidth", pproperties);
    widgetutils.setProperty("customwidthtype", pproperties);
    widgetutils.setProperty("labelwidth", pproperties);
    widgetutils.setProperty("contentalignment", pproperties);
    widgetutils.setProperty("translatedefaultvalue", pproperties);
    widgetutils.setProperty("state", pproperties);
    //widgetutils.setProperty("semantics", pproperties);
    widgetutils.setProperty("options", pproperties);
    widgetutils.setProperty("tooltip", pproperties);
    widgetutils.setProperty("hint", pproperties);
    widgetutils.setProperty("cssclasses", pproperties);
    widgetutils.setProperty("labelrequired", pproperties);
    widgetutils.setEvent("eventstable", pproperties);
    DropDownasButton_displayElmRelatedProps();
    DropDownasButton_setDropdowns("staticoptions",pproperties);
    widgetutils.setProperty("appearance", pproperties);
};
/************************************************************************
 Below function is called while generating the widget html from IDE
 Provide the html as you want to display the widget in the generated page.
	
 In below function lcontent contains the widget related data. Access them with .propertyId
 Ex : If there is a property called title you can access it as lcontent.title
 ************************************************************************/
function getHTML(pproperties) {
    var lcontent = JSON.parse(pproperties);
    var alignment = "", toolTip = "", tooltipCls = "", cssCls = "", snoCls = "",widthCls="", elmId = lcontent.name;
    var stateAttr = "",widthStyleAttr ="", disabledCls = "", defVal = "&nbsp;";
    var statOpts = lcontent.staticoptions;
    if (lcontent.contentalignment == "LEFT") {
        alignment = " lft";
    } else if (lcontent.contentalignment == "CENTER") {
        alignment = " cen";
    } else if (lcontent.contentalignment == "RIGHT") {
        alignment = " rht";
    }
    var state = lcontent.state;
    if (state=='DISABLED') {
        stateAttr = ' disabled="disabled"';
        disabledCls= " disabled";
    }
    if (lcontent.tooltip) {
        toolTip = " original-title="+lcontent.tooltip+"";
        tooltipCls = " tooltipcls";
    }
    /*var semantics = "";
    if(lcontent.semantics){
     semantics = semantics.toLowerCase();
    } */
    if (lcontent.defaultvalue) {
        defVal = lcontent.defaultvalue;
    }
    if (lcontent.options == "N") {
         snoCls = " sno";
     }
     if (lcontent.cssclasses != undefined) {
         cssCls = " " + lcontent.cssclasses;
     }
     if (lcontent.appearance != undefined && lcontent.appearance != null && lcontent.appearance != "") {
         apprCls = " " + lcontent.appearance;
     }
     if (lcontent.width!="CUSTOM") {
         widthCls = " etw-"+lcontent.width;
     } else {
         widthStyleAttr = " style='width:"+lcontent.customwidth+lcontent.customwidthtype+"'";
     }
    var html = '<div id = "'+elmId+'_ext" '+toolTip+' class="'+apprCls+alignment+tooltipCls+cssCls+snoCls+widthCls+disabledCls+' ett-drdn etb-drdn " '+widthStyleAttr+' '+stateAttr+'><button id="'+elmId+'" class="drdn '+alignment+disabledCls+'" '+stateAttr+'>'+defVal+'</button><span><svg class=\"ett-icon icon-down px34\"><use xlink:href=\"#icon-down\"></use></svg></span><div id ="'+elmId+'_div" class="drdn-list"><ul role="menu">';
        var clickEvent = "";
        /*if(lcontent.events && lcontent.events[0] && lcontent.events[0]["function"]) {
            clickEvent = lcontent.events[0]["function"].replace(/"|'/g,"&quot;");   
        }*/
        var eventName = "";
        var functStr = "";
        for(var n = 0; n < lcontent.events.length; n++) {
            if(lcontent.events[n]["function"]) {
                eventName = lcontent.events[n]['name'];
                functStr = lcontent.events[n]["function"].replace(/"|'/g,"&quot;");
                clickEvent += eventName + " = \"" + functStr + " \"";   
            }
        }
        if(statOpts && statOpts.length > 0) {
        	for(i=0;i< statOpts.length;i++) {
        		html += '<li '+ clickEvent +'><a href = "'+lcontent.staticoptions[i].value+'" target= "_blank">'+lcontent.staticoptions[i].description+'</a></li>';
        	}
        }
        html += '</ul></div></div>';
    return html;
}

function DropDownasButton_displayElementsProperties(pproperties) {
    var id = document.getElementById("id").value;
	var parentContainer = utils.getContainer($("#"+id)[0]).getAttribute("widgettype");
	if (parentContainer == "TABLE") {
		$(".forTable").removeClass("sno");//css("display", "block");
	}
	if (parentContainer == "NAVBAR") {
		$(".forNavbar").removeClass("sno");
	}
	if (parentContainer == "FORM") {
		$(".forForm").removeClass("sno");
	}
	//// fix 1648
	if (parentContainer == "LIST") {
		$(".forList").removeClass("sno");
	}
	if(pproperties.draggable == "Y"){
		$(".draggableid").removeClass("sno");
	} else {
		$(".draggableid").addClass("sno");
	}
	if(pproperties) {
		var interfacename = pproperties.interfacename;
		var datamodeltyp = pproperties.datamodeltype;
		var nodename = pproperties.nodename;
		var elementname = pproperties.elementname;
	} else {
		var interfacename = document.getElementById("interfacename").value;
		var datamodeltyp = document.getElementById("datamodeltype").value;
		var nodename = document.getElementById("nodename").value;
		var elementname = document.getElementById("elementname").value;
	}
	if(utils.isDmlObj(interfacename,datamodeltyp, nodename, elementname)) {
		$(".handleso").addClass("disabled");
	} else {
		$(".handleso").removeClass("disabled");
	}
}

function DropDownasButton_title(pobject) {
    if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.title;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("title").value;
	}
	se = widgetutils.getLITValue(se);
	var parentContainer = utils.getContainer($("#"+res)[0]);
	var parentContainerType = parentContainer.getAttribute("widgettype");
	var parentId = $("#"+res)[0].getAttribute("parentid");
	if(parentContainerType == "TABLE") {
		if(utils.isNull(se)) {
       		$("#"+res+"_heading").html("&nbsp;");
		} else {
			$("#"+res+"_heading").html(se);	
		}
	}
}

function DropDownasButton_labelwidth(pobject) {
    if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.labelwidth;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("labelwidth").value;
	}
	if(!utils.isNull(se) && $.isNumeric(se)){
		var ae = 100 - se;
	}else{
		var ae = 100;
	}
	var parentId = $("#"+res)[0].getAttribute("parentid");
	var parentobject = $("#" + parentId);
	var parentObjType = parentobject.attr("widgettype");
	var containerobj = utils.getContainer($("#"+parentId)[0]);
	var containertype = containerobj.getAttribute("widgettype");
	var classarray = ['etw-0', 'etw-5', 'etw-10', 'etw-15', 'etw-20', 'etw-25', 'etw-30', 'etw-35', 'etw-40', 'etw-45', 'etw-50', 'etw-55', 'etw-60', 'etw-65', 'etw-70', 'etw-75', 'etw-80', 'etw-85', 'etw-90', 'etw-95', 'etw-100'];
	if(containertype == 'FORM'){
		if(!parentobject[0].hasAttribute("role")) {
			var temp = $("#" + res).find("#label");
			var tem = $("#" + res).find("#content");
			if(!utils.isNull(se)) {
				$(temp).removeClass(classarray.join(' ')).addClass('etw-' + se);
				$(tem).removeClass(classarray.join(' ')).addClass('etw-' + ae);
			} else {
				$(temp).removeClass(classarray.join(' ')).addClass('etw-40');
				$(tem).removeClass(classarray.join(' ')).addClass('etw-60');
			}
		}
	}
}

function DropDownasButton_colwidth(pobject) {
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.width;
        var custom = pobject.customwidth;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("width").value;
        var custom = document.getElementById("customwidth");
    }
    var parContnr = utils.getContainer($("#"+res)[0]);
	var parContnrType = parContnr.getAttribute("widgettype");
	var temp;
	if(parContnrType == "FORM" || parContnrType == "TABLE") {
		temp = $("#"+res).find('.elmWidget');
	} else {
		temp = $("#"+res);
	}
    var classarray = ['etw-0', 'etw-5', 'etw-10', 'etw-15', 'etw-20', 'etw-25', 'etw-30', 'etw-35', 'etw-40', 'etw-45', 'etw-50', 'etw-55', 'etw-60', 'etw-65', 'etw-70', 'etw-75', 'etw-80', 'etw-85', 'etw-90', 'etw-95', 'etw-100'];
    if ((!utils.isNull(se)) && se != "CUSTOM") {
        $(custom).attr('disabled', 'disabled');
        $("#customwidthtype").attr('disabled', 'disabled');
        $(custom).val("");
        $(temp).removeClass(classarray.join(' '));
        $(temp).css('width', '');
        $(temp).addClass('etw-' + se);
    } else {
        $(custom).removeAttr('disabled');
        $("#customwidthtype").removeAttr('disabled');
    }
}

function DropDownasButton_customwidth(pobject) {
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.customwidth;
        var setype = pobject.customwidthtype;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("customwidth").value;
        var setype = document.getElementById("customwidthtype").value;
    }
    var parContnr = utils.getContainer($("#"+res)[0]);
	var parContnrType = parContnr.getAttribute("widgettype");
	var temp;
	if(parContnrType == "FORM" || parContnrType == "TABLE") {
		temp = $("#"+res).find('.elmWidget');
	} else {
		temp = $("#"+res);
	}
    if (!utils.isNull(se)) {
        //var temp = $("#" + res).find(".with-dropddown #dropdownbtn");
        $(temp).css('width', se + setype);
    }
}

function DropDownasButton_conallign(pobject) {
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.contentalignment;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("contentalignment").value;
    }
    var temp = $("#" + res).find(".drdn-list");
    var tempButton = $("#" + res).find("button");
    if (se == "LEFT") {
        $(temp).removeClass('rht cen').addClass('lft');
        $(tempButton).removeClass('rht cen').addClass('lft');  
    } else if (se == "RIGHT") {
        $(temp).removeClass('lft cen').addClass('rht');
        $(tempButton).removeClass('lft cen').addClass('rht');
    } else if (se == "CENTER") {
        $(temp).removeClass('rht lft').addClass('cen');
        $(tempButton).removeClass('rht lft').addClass('cen');
    }
}

function DropDownasButton_state(pobject) {
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.state;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("state").value;
    }
    var temp = $("#" + res).find('button');
    if (se == "DISABLED") {
        $(temp).addClass('disabled');
    } else {
        $(temp).removeClass('disabled');
    }
}

/*function DropDownasButton_semantics(pobject) {
    if(pobject != undefined) {
        var res = pobject.id;
        var se = pobject.semantics;
    } else {
        var se = document.getElementById("semantics").value;
        var res = document.getElementById("id").value;
    }
    var temp = $("#" + res).find('button');
    var classarray = ['err', 'inf', 'war', 'suc', 'inv'];
    $(temp).removeClass(classarray.join(' '));
    if(!utils.isNull(se))
        $(temp).addClass(se.toLowerCase());
} */

function DropDownasButton_headerAlign(pobject) {
    if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.headeralignment;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("headeralignment").value;
	}
	var index = $("#" + res).parent("tr").children().index($("#"+res));
	var temp = $("#"+($("#"+res).closest("tbody").attr("parentid")+"_header")).children()[index];
	if(se == "LEFT")
		$(temp).removeClass('rht cen').addClass('lft');
	else if(se == "RIGHT")
		$(temp).removeClass('lft cen').addClass('rht');
	else 
		$(temp).removeClass('rht lft').addClass('cen');
}

function DropDownasButton_displayElmRelatedProps () {
    widgetutils.updatePropertyVisibility("translatedefaultvalue","NO");
    widgetutils.updatePropertyVisibility("minstringlength","NO");
    widgetutils.updatePropertyVisibility("maxstringlength","NO");
    widgetutils.updatePropertyVisibility("minvalue","NO");
    widgetutils.updatePropertyVisibility("maxvalue","NO");
    widgetutils.updatePropertyVisibility("pattern","NO");
    widgetutils.updatePropertyVisibility("mandatory","NO");
}

function DropDownasButton_getDropdowns(ptable, pproperties) {
    var interfacename = document.getElementById('interfacename').value;
	var datamodeltyp = document.getElementById('datamodeltype').value;
	var nodename = document.getElementById('nodename').value;
	var elementname = document.getElementById('elementname').value;
	if( typeof pproperties.staticoptions != 'undefined') {
		$("#" + ptable + " tbody").empty();
		for( i = 1; i <= pproperties.staticoptions.length; i++) {
			DropDownasButton_addDropdown(pproperties.staticoptions[i - 1]);
			$("#" + ptable + " tr:eq(" + i + ") #value").val(pproperties.staticoptions[i - 1]["value"]);
			$("#" + ptable + " tr:eq(" + i + ") #description").val(pproperties.staticoptions[i - 1]["description"]);
			if(pproperties.staticoptions[i - 1]["group"] == "Y")
				$("#" + ptable + " tr:eq(" + i + ") #group").prop("checked",true);
		}
		if(!utils.isNull(interfacename) && !utils.isNull(datamodeltyp) && !utils.isNull(nodename) && !utils.isNull(elementname)){
			$("#staticoptions").find('input,a').attr('disabled','disabled');
		} else {
			$("#staticoptions").find('input,a').removeAttr('disabled');
		}
	}
}

function DropDownasButton_addDropdown(pproperties) {
	var newRow = '<tr>' + '<td>' + '<input id="selectdropdown" type="checkbox" class="selectdropdown" />' + '</td>' + '<td>' + '<INPUT id="value" type="text" name="txt" class="value" onblur="DropDownasButton_changeValue();" />' + '</td>' + 
				 '<td>' + '<INPUT id="description" type="text" name="txt" style="width:80%" class="description" onblur="DropDownasButton_changeDescription();" /><a class="ebtn-mini etxt-info px16 ebtn-icon" onclick="widgetutils.getLITCode(this);"><i class="icon-edit"></i></a>' + '</td>' + '<td class="optiongroup">' + '<INPUT id="group" type="checkbox" name="group" />' + '</td>' + '</tr>';
	$("#staticoptions tbody").append(newRow);
	if(designer.multipleselect){
		for (var i = 0; i < designer.selectedobjs.length; i++) {
			$("#id").val(designer.selectedobjs[i].id);
			try {
				DropDownasButton_addDropdownElement(pproperties);
			} catch (e) {}
		}
	}
	else{
	DropDownasButton_addDropdownElement(pproperties);
	}
	if(designer.multipleselect){
		for (var i = 0; i < designer.selectedobjs.length; i++) {
			$("#id").val(designer.selectedobjs[i].id);
			try {
				var ldatapointer = utils.getDataPointer(designer.selectedobjs[i]);
				DropDownasButton_setDropdowns("staticoptions", ldatapointer);
			} catch (e) {}
		}
	}
}

function DropDownasButton_addDropdownElement(pproperties) {
	var eleId = document.getElementById('id').value;
	var selectid = $("#"+eleId).find(".drdn-list ul")[0];
	var objNewOption = document.createElement("li");
	if(pproperties == undefined) {
		$(selectid).append(objNewOption);
	}
}

function DropDownasButton_deleteDropdown(ptablename) {
	var checked = jQuery('input:checkbox:checked').map(function() {
		if(this.classList.contains("selectdropdown"))
		return this;
	}).get();
	//jQuery('input:checkbox:checked').parents("tr").remove();
	var deletable = jQuery('input:checkbox:checked');
	for(var i = 0; i < checked.length; i++) {
		$(checked[i]).parents("tr").remove();
		var optvalue = $(checked[i]).parent().next().children()[0].value;
		if(designer.multipleselect){
			for (var j = 0; j < designer.selectedobjs.length; j++) {
				var res = designer.selectedobjs[j].id
				$("#" + res).find(".elmWidget option[value='" + optvalue + "']").remove();
			}
		}
		else{
		var res = document.getElementById('id').value;
		$("#" + res).find(".elmWidget option[value='" + optvalue + "']").remove();
		}
	}
	if(designer.multipleselect){
		for (var i = 0; i < designer.selectedobjs.length; i++) {
			$("#id").val(designer.selectedobjs[i].id);
			try {
				var ldatapointer = utils.getDataPointer(designer.selectedobjs[i]);
				DropDownasButton_setDropdowns("staticoptions", ldatapointer);
			} catch (e) {}
		}
	}
}

function DropDownasButton_loadDropdowns(ptable, pproperties) {
	if( typeof pproperties.staticoptions != 'undefined') {
		$("#" + pproperties.id).find(".elmWidget").find("option").remove();
		for( i = 1; i <= pproperties.staticoptions.length; i++) {
			var eleId = pproperties.id;
			var parentContnrType = utils.getContainer(document.getElementById(eleId)).getAttribute("widgettype");
			var selectid;
			/*if(parentContnrType == "FORM" || parentContnrType == "TABLE") {
				selectid = $("#"+eleId).find(".elmWidget")[0];
			} else {
				selectid = $("#"+eleId)[0];
			}*/
			selectid = $("#"+eleId).find(".elmWidget")[0];
			var objNewOption = document.createElement("OPTION");
			selectid.options.add(objNewOption);
			if(pproperties.staticoptions != undefined) {
				objNewOption.innerHTML = widgetutils.getLITValue(pproperties.staticoptions[i - 1].description);
				objNewOption.value = pproperties.staticoptions[i - 1].value;
				if(pproperties.defaultvalue == pproperties.staticoptions[i - 1].value)
					objNewOption.selected = true;
			}
		}
	}
}

function DropDownasButton_changeValue() {
	if(designer.multipleselect){
		for (var i = 0; i < designer.selectedobjs.length; i++) {
			$("#id").val(designer.selectedobjs[i].id);
			try {
				DropDownasButton_changeValueFunc();
			} catch (e) {}
		}
	}
	else{
	DropDownasButton_changeValueFunc();
	}
	if(designer.multipleselect){
		for (var i = 0; i < designer.selectedobjs.length; i++) {
			$("#id").val(designer.selectedobjs[i].id);
			try {
				var ldatapointer = utils.getDataPointer(designer.selectedobjs[i]);
				DropDownasButton_setDropdowns("staticoptions", ldatapointer);
			} catch (e) {}
		}
	}
}

function DropDownasButton_changeValueFunc() {
	var eleId = document.getElementById('id').value;
	var defaultvalue = document.getElementById('defaultvalue').value;
	var selectid = $("#"+eleId).find(".drdn-list ul")[0];
	var values = document.getElementsByClassName('value');
	for(var i = 0; i < values.length; i++) {
		$($(selectid).children[i]).attr('value', values[i].value);
	};
}

function DropDownasButton_changeDescription(ptable, pproperties) {
	if(designer.multipleselect){
		for (var i = 0; i < designer.selectedobjs.length; i++) {
			$("#id").val(designer.selectedobjs[i].id);
			try {
				DropDownasButton_changeDescriptionFunc(ptable, pproperties);
			} catch (e) {}
		}
	}
	else{
	DropDownasButton_changeDescriptionFunc(ptable, pproperties);
	}
	if(designer.multipleselect){
		for (var i = 0; i < designer.selectedobjs.length; i++) {
			$("#id").val(designer.selectedobjs[i].id);
			try {
				var ldatapointer = utils.getDataPointer(designer.selectedobjs[i]);
				DropDownasButton_setDropdowns("staticoptions", ldatapointer);
			} catch (e) {}
		}
	}
}
function DropDownasButton_changeDescriptionFunc(ptable, pproperties) {
	var eleId = document.getElementById('id').value;
	var selectid = $("#"+eleId).find(".drdn-list ul")[0];
	var description = document.getElementsByClassName('description');
	for(var i = 0; i < description.length; i++) {
		$(selectid).children[i].innerHTML = widgetutils.getLITValue(description[i].value);
	};
}

function DropDownasButton_setDropdowns(ptable, pproperties) {
	var rows = $("#" + ptable + " tr:gt(0)");
	var i = 1;
	pproperties.staticoptions = [];
	rows.each(function(index) {
		if(!pproperties.staticoptions[i])
			pproperties.staticoptions[i - 1] = {
			};
		pproperties.staticoptions[i - 1]["description"] = $("#" + ptable + " tr:eq(" + i + ") #description").val();
		pproperties.staticoptions[i - 1]["value"] = $("#" + ptable + " tr:eq(" + i + ") #value").val();
		pproperties.staticoptions[i - 1]["group"] = "N";
		if($("#" + ptable + " tr:eq(" + i + ") #group").prop("checked"))
			pproperties.staticoptions[i - 1]["group"] = "Y";
		i += 1;
	});
}

DropDownasButton_defaultvalue = function (pobject) {
    var res, defaultvalue;
    if(pobject) {
        res = pobject.id;
        defaultvalue = pobject.defaultvalue;
    } else {
        res = document.getElementById("id").value;
        defaultvalue = document.getElementById("defaultvalue").value;
    }
    //var dropIcon = $("#"+res).find('button svg');
    $($("#"+res).find('button')[0]).text("");
    $($("#"+res).find('button')[0]).append(defaultvalue);
    //$("#"+res).find('button').append(dropIcon);
}

DropDownasButton_setStaticValuesDropdown = function(interfacename, datamodeltyp, nodename, elementname, previouselename) {
	var res = document.getElementById("id").value;
	var defaultvalue = document.getElementById("defaultvalue").value;
	var elenameexist = !(previouselename == "" || previouselename == null);
	if(!utils.isNull(interfacename) && !utils.isNull(datamodeltyp) && !utils.isNull(nodename) && !utils.isNull(elementname)){
	 	$(".handleso").addClass("disabled");
	if ( typeof store.metadata != 'undefined' && typeof store.interfacesmap[interfacename].dmlsmap[datamodeltyp].nodesmap[nodename].elmsmap[elementname]!= 'undefined') {
			var loptionsmap = store.interfacesmap[interfacename].dmlsmap[datamodeltyp].nodesmap[nodename].elmsmap[elementname].staticoptions;
		if (elenameexist || (!elenameexist && (loptionsmap.length > 0))) {
			$("#staticoptions tbody").empty();
			$("#" + res).find(".elmWidget").find(".drdn-list ul li").remove();
				for (var i = 0; i < loptionsmap.length; i++) {
					var newRow = '<tr>' + '<td>' + '<input id="selectdropdown" type="checkbox" class="selectdropdown" />' + '</td>' + '<td style="min-width:100px">' + '<INPUT id="value" type="text" name="txt" class="value" onblur="table_dropdown_changeValue();"  value="' + store.interfacesmap[interfacename].dmlsmap[datamodeltyp].nodesmap[nodename].elmsmap[elementname].staticoptions[i].value + '" />' + '</td>' + '<td style="min-width:100px">' + '<INPUT id="description" type="text" name="txt" class="description" onblur="table_dropdown_changeDescription();"  value="' + store.interfacesmap[interfacename].dmlsmap[datamodeltyp].nodesmap[nodename].elmsmap[elementname].staticoptions[i].description + '" />' + '</td>' +'<td class="optiongroup"><input id="group" type="checkbox" name="group"></td>'+'</tr>';
					$("#staticoptions tbody").append(newRow);
					var selectid = $("#" + res).find(".elmWidget .drdn-list ul")[0];
					var objNewOption = document.createElement("li");
					$(selectid).append(objNewOption);
					objNewOption.innerHTML = store.interfacesmap[interfacename].dmlsmap[datamodeltyp].nodesmap[nodename].elmsmap[elementname].staticoptions[i].description;
					objNewOption.value = store.interfacesmap[interfacename].dmlsmap[datamodeltyp].nodesmap[nodename].elmsmap[elementname].staticoptions[i].value;
					$(objNewOption).attr('value',store.interfacesmap[interfacename].dmlsmap[datamodeltyp].nodesmap[nodename].elmsmap[elementname].staticoptions[i].value);
					
				}
				$("#staticoptions").find("input,a").attr("disabled", "disabled");
		}
	}else{
		$("#staticoptions tbody").empty();

	}
 }else{
		$(".handleso").removeClass("disabled");
		$("#staticoptions").find("input,a").removeAttr("disabled");
	}

	$("#elementname").trigger("blur");
}

DropDownasButton_appearance = function(pobject) {
    var res, appr;
    if(pobject) {
        res = pobject.id;
        appr = pobject.appearance;
    } else {
        res = document.getElementById("id").value;
        appr = document.getElementById("appearance").value;
    }
    var parContnr = utils.getContainer($("#"+res)[0]);
	var parContnrType = parContnr.getAttribute("widgettype");
	var temp;
	if(parContnrType == "FORM" || parContnrType == "TABLE") {
		temp = $("#"+res).find('.elmWidget');
	} else {
		temp = $("#"+res);
	}
	$(temp).removeClass('pri sec ter');
	if (!utils.isNull(appr)) {
	    $(temp).addClass(appr);
	}
}


/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 ************************************************************************/
customwidgets.DropDownasButton.applyProperties = function(pobject) {
    var parentContainer = utils.getContainer($("#"+pobject.id)[0]).getAttribute("widgettype");
    DropDownasButton_colwidth(pobject);
    DropDownasButton_customwidth(pobject);
    DropDownasButton_conallign(pobject);
    DropDownasButton_state(pobject);
    widgetutils.setWidgetVisibility(pobject);
    DropDownasButton_defaultvalue(pobject);
    DropDownasButton_appearance(pobject);
    //DropDownasButton_semantics(pobject);
    if (parentContainer == "TABLE") {
        DropDownasButton_title(pobject);
        DropDownasButton_headerAlign(pobject);
    } else if (parentContainer == "FORM") {
        DropDownasButton_labelwidth(pobject);
    }
};
