/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
    var customwidgets = {};
}
customwidgets.CardNumber = {};
customwidgets.CardNumber.currobj = null;
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
customwidgets.CardNumber.init = function(pobject) {
    customwidgets.CardNumber.currobj = pobject;
    $("#nodename").change(function() {
        widgetutils.loadElementNames("elementname", document.getElementById('nodename').value);
    });
    $("#elementname").change(function() {
        widgetutils.loadName(document.getElementById('elementname').value, document.getElementById('nodename').value);
    });
    $("#mainPropCollapse").click(function() {
        $("#mainPropAccordian").toggle();
    });
    $("#eventscollapse").click(function() {
        $("#eventsaccordian").toggle();
        $(this).toggleClass('open');
        
    });
    $("#additionalpropcollapse").click(function() {
        $("#additionalprop").toggle();
        $(this).toggleClass('open');
    });
    /***************************************************************************************
  Attaching on blur event Handler to the property title.
  This way event Handlers can be attached to others properties as well

*****************************************************************************************/
    $("#title").blur(function() {
        CardNumber_title();
    });
    $("#labelwidth").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("labelwidth", ldatapointer);
                try {
                    //calling the element.js element_labelwidth fuction
                    element_labelwidth(ldatapointer);
                } catch (e) {}
            }
        } else {
            //calling the element.js element_labelwidth fuction
            element_labelwidth();
        }
    });
    $("#width").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("width", ldatapointer);
                try {
                    form_cardNumber_colwidth(ldatapointer);
                } catch (e) {}
            }
        } else {
            form_cardNumber_colwidth();
        }
    });
    $("#state").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("state", ldatapointer);
                try {
                    form_cardNumber_state(ldatapointer);
                } catch (e) {}
            }
        } else {
            form_cardNumber_state();
        }
    });
    $("#cssclasses").blur(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("cssclasses", ldatapointer);
            }
        }else{
            
        }
    });
    $("#placeholder").blur(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("placeholder", ldatapointer);
                try {
                    form_cardNumber_placeholder(ldatapointer);
                } catch (e) {}
            }
        } else {
            form_cardNumber_placeholder();
        }
    });
    $("#appearance").change(function() {
        form_cardNumber_appearance();
    });
    $("#contentalignment").change(function() {
        element_conallign();
    });
    $("#contentalignment").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("contentalignment", ldatapointer);
                try {
                    cardNumber_conallign(ldatapointer);
                } catch (e) {}
            }
        } else {
            cardNumber_conallign();
        }
    });
    $("#labelalignment").change(function() {
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("labelalignment", ldatapointer);
                try {
                    cardNumber_labelallign(ldatapointer);
                } catch (e) {}
            }
        } else {
            cardNumber_labelallign();
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
    $("#labelrequired").change(function() {
        var lval = $("#labelrequired").prop("checked") ? "Y" : "N";
        $("#labelrequired")[0].value = lval;
        if (designer.multipleselect) {
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("labelrequired'", ldatapointer);
                try {} catch (e) {}
            }
        }
    });
    
    $("#defaultvalue").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("defaultvalue", ldatapointer);
				try {
					form_cardNumber_defaultvalue(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		    form_cardNumber_defaultvalue();
		}
	});
	$("#maxstringlength").blur(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("maxstringlength", ldatapointer);
				try {
					form_cardNumber_maxlength(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		form_cardNumber_maxlength();
		}
	});
};
/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.CardNumber.getTemplate = function(pobject, pparentobject, receviedForChild, uiParentObj) {
    var widgethtml = "";
    var parentContainer = utils.getContainer(pparentobject);
    var parentContId = utils.getContainer(pparentobject).id;
    var containerType = parentContainer.getAttribute("widgettype");
    
    if (containerType == "TABLE") {
        //var header = $("#" + parentContId).find("#" + parentContId + "_header");
        //var newHeader = '<th onclick="widgetutils.selectTableElement(this, event);">Card Number</th>';
        //var parentContainer = utils.getContainer(pparentobject);
        /*if (receviedForChild) {
            var $uiParentObj = $(uiParentObj);
            var parentIndex = $uiParentObj.parent().children().index(uiParentObj);
            var uiParentHeader = header.children()[parentIndex];
            $(uiParentHeader).before(newHeader);
        } else {
            header.append(newHeader);
        }*/
        widgethtml = '<td id ="appzillonid" class="apzlocontainer">';
        var elmContent ='<div class="elmWidget"><span class="ecn"><input class="ett-inpt etw-100 pri card-number card-js" id="input1" placeholder="Card Number" type="tel"><span id="htx-04" class="htx" ></span></span></div>';
        widgethtml = widgethtml + elmContent;
        widgethtml = widgethtml + "</td>";
    } else if (containerType == "LIST" || containerType == "NAVBAR") {
        widgethtml =
            '<div id ="appzillonid" class="apzlocontainer srb eoc"><span class="ecn elmWidget"><input class="ett-inpt etw-100 pri card-number card-js" id="input1" placeholder="Card Number" type="tel"><span id="htx-04" class="htx" ></span></span></div>';
    } else if (containerType == "FORM") {
        if (pparentobject.hasAttribute("role")) {
            var receivedTemplate = '<span class="ecn elmWidget">' +
                '<input class="ett-inpt etw-100 pri card-number card-js" id="input1" placeholder="Card Number" type="tel">' +
                '<span id="htx-04" class="htx" ></span></span>';
            widgethtml = '<li id ="appzillonid" class="apzlocontainer elpad">';
            widgethtml = widgethtml + receivedTemplate;
            widgethtml = widgethtml + '</li>';
        } else {
            widgethtml = '<ul id ="appzillonid" class="apzlocontainer srb eoc"><li id="label" class="etw-40">Card Number</li>' +
                '<li id="content" class="eio etw-60"><span class="ecn elmWidget">' +
                '<input class="ett-inpt etw-100 pri card-number card-js" id="input1" placeholder="Card Number" type="tel">' +
                
                '<span id="htx-04" class="htx" ></span></span></li></ul>';
        }
    }
    return widgethtml;
};
customwidgets.CardNumber.updateDefaultValues = function(pobject, pparentobject) {
    document.getElementById("title").value = "Card Number";
}
/************************************************************************
 Below function is called from layout.populateProperties() to populate the widget properties provided by the developer
 layout.populateProperties() will be called when the widget has been selected in the designer.
 Thats when the widget properties page will be populated.
	
 Send the widgets property id to the widgetutils.getPropety() function with 'pproperties' object
 So that the value stored in 'pproperties' object will be assigned to the element in the screen with the id which has been passed.
 Example: In below function id and pid are passed to the widgetutils.getProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.CardNumber.populateProperties = function(pproperties) {
    //alert("populateProperties");
    widgetutils.getProperty("id", pproperties);
    widgetutils.getProperty("pid", pproperties);
    widgetutils.getProperty("title", pproperties);
    
    widgetutils.displayElementsProperties(pproperties);
    widgetutils.getProperty("labelwidth", pproperties);
    widgetutils.getProperty("width", pproperties);
    form_cardNumber_colwidth();
    widgetutils.getProperty("state", pproperties);
    widgetutils.getProperty("cssclasses", pproperties);
    widgetutils.getProperty("placeholder", pproperties);
    widgetutils.getProperty("appearance", pproperties);
    widgetutils.getProperty("contentalignment", pproperties);
    widgetutils.getProperty("labelalignment", pproperties);
    widgetutils.getProperty("hint", pproperties);
    widgetutils.getProperty("options", pproperties);
    widgetutils.getProperty("labelrequired", pproperties);
    widgetutils.getProperty("defaultvalue", pproperties);
    widgetutils.getEventElements("eventstable", pproperties);
    form_cardNumber_displayElementProperties(pproperties);
    
};
/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.CardNumber.saveProperties = function(pproperties) {
    widgetutils.setProperty("id", pproperties);
    widgetutils.setProperty("pid", pproperties);
    widgetutils.setProperty("title", pproperties);
    widgetutils.setProperty("labelwidth", pproperties);
    widgetutils.setProperty("width", pproperties);
    widgetutils.setProperty("state", pproperties);
    widgetutils.setProperty("cssclasses", pproperties);
    widgetutils.setProperty("placeholder", pproperties);
    widgetutils.setProperty("appearance", pproperties);
    widgetutils.setProperty("contentalignment", pproperties);
    widgetutils.setProperty("labelalignment", pproperties);
    widgetutils.setProperty("hint", pproperties);
    widgetutils.setProperty("options", pproperties);
    widgetutils.setProperty("labelrequired", pproperties);
    widgetutils.setProperty("defaultvalue", pproperties);
    widgetutils.setEvent("eventstable", pproperties);
    
};
/************************************************************************
 Below function is called while generating the widget html from IDE
 Provide the html as you want to display the widget in the generated page.
	
 In below function lcontent contains the widget related data. Access them with .propertyId
 Ex : If there is a property called title you can access it as lcontent.title
 ************************************************************************/
function getHTML(pproperties) {
   
    var lcontent = JSON.parse(pproperties);
   
    var html = "",
        rowNo = "",
        elmId = lcontent.name;
    var alignment = "",
        cssCls = "",
        snoCls = "",
        toolTip = "",
        tooltipCls = "",
        placeholder = "",
        stateAttr = "",
        apprCls = " pri",
        width = "",
        accCls = "", defaultvalue ="";
    if (lcontent.contentalignment == "LEFT") {
        alignment = "lft";
    } else if (lcontent.contentalignment == "CENTER") {
        alignment = "cen";
    } else if (lcontent.contentalignment == "RIGHT") {
        alignment = "rht";
    }
    if (lcontent.state == "DISABLED") {
        stateAttr = " disabled='disabled'";
    } else if (lcontent.state == "READONLY") {
        stateAttr = " readonly='readonly'";
    }
    if (lcontent.placeholder) {
        placeholder = " placeholder=" + lcontent.placeholder + "";
    }
    if (lcontent.options == "N") {
        snoCls = " sno";
    }
    if (lcontent.cssclasses != undefined) {
        cssCls = " " + lcontent.cssclasses;
    }
    if (lcontent.rowno != undefined && lcontent.rowno != -1) {
        rowNo = "_" + lcontent.rowno;
    }
    if (lcontent.appearance != null && lcontent.appearance != undefined && lcontent.appearance != "") {
        apprCls = lcontent.appearance + " ";
    }
    if (lcontent.defaultvalue != null || lcontent.defaultvalue != undefined) {
        
        defaultvalue = lcontent.defaultvalue;
        //defaultvalue = "+apz.CardNumber.formatInputNumber("+lcontent.defaultvalue+");"
        
    }
    if (lcontent.width != null || lcontent.width != undefined) {
        width = lcontent.width;
    }
    var eventobj = "";
    var events = "",
        keyupReq = false;
    for (var i = 0; i < lcontent.events.length; i++) {
        eventobj = lcontent.events[i];
        if (eventobj.function != null && eventobj.function != undefined && eventobj.function != "" && eventobj.name != null && eventobj.name !=
            undefined && eventobj.name != "") {
            var updatedFunction = eventobj.
                function;
            if (eventobj.name == "ONKEYUP") {
                events += eventobj.name.toLowerCase() + "= 'apz.CardNumber.formatInput(this); " + updatedFunction + "' ";
                keyupReq = true;
            } else {
                events += eventobj.name.toLowerCase() + "= '" + updatedFunction + "' ";
            }
        }
    }
    if (!keyupReq) {
        events = ' onkeyup ="apz.CardNumber.formatInput(this);" ' + events;
    }
    
    var html = '<span class="ecn etw-'+width+'" style=""><span class="etw-100 icr  pri"><input id="' + elmId + rowNo +
        '" class="etw-100 ett-inpt card-number card-js ' + apprCls + alignment + cssCls+ snoCls+'" ' + placeholder + stateAttr + ' ' + events +
        '"  aria-labelledby="input_label_01" aria-describedby="htx_01" enabled="enabled" value="'+defaultvalue+'" type="tel"></span></span>';
    return html;
}
/////////// Title Change function called by onBlur Event Handler of title Property.
// Similar functions can be written for all the properties calling from thier respective Event Handlers
function CardNumber_title(pobject) {
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.title;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("title").value;
    }
    se = widgetutils.getLITValue(se);
    var parentContainer = utils.getContainer($("#" + res)[0]);
    var parentContainerType = parentContainer.getAttribute("widgettype");
    var parentId = $("#" + res)[0].getAttribute("parentid");
    if (parentContainerType != "TABLE") {
        var parentobject = $("#" + parentId);
        if (parentContainerType == "FORM") {
            if (parentobject[0].hasAttribute("role")) {
                var firstChild = parentobject.find("#" + parentId + "_receiver").children()[0].id;
                if (firstChild == res) parentobject.find("#group1").html("");
            } else {
                $("#" + res).find("#label").html(se);
            }
        }
    } else {
        if (utils.isNull(se)) {
            $("#" + res + "_heading").html("&nbsp;");
        } else {
            $("#" + res + "_heading").html(se);
        }
    }
}

function form_cardNumber_displayElementProperties(pobject){
    $("#translatedefaultvalue" ).parent().parent().css( "display", "none" );
    $("#maxstringlength" ).parent().parent().css( "display", "block" );
}


function form_cardNumber_colwidth(pobject) {
    debugger;
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.width;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("width").value;
    }
    var custom = document.getElementById("customwidth");
    var temp = $("#" + res).find("input").parents(".ecn:first");
    var classarray = ['etw-0', 'etw-5', 'etw-10', 'etw-15', 'etw-20', 'etw-25', 'etw-30', 'etw-35', 'etw-40', 'etw-45', 'etw-50', 'etw-55', 'etw-60',
        'etw-65', 'etw-70', 'etw-75', 'etw-80', 'etw-85', 'etw-90', 'etw-95', 'etw-100'
    ];
    $(custom).parent().addClass('sno');
    $("#customwidthtype").parent().addClass('sno');
    $("#width").removeAttr("style");
    if ((!utils.isNull(se)) && se != "CUSTOM") {
        $(custom).val("");
        $(temp).removeClass(classarray.join(' '));
        $(temp).css('width', '');
        $(temp).addClass('etw-' + se);
    } else {
        if (se == "CUSTOM") {
            $(custom).parent().removeClass('sno');
            $("#customwidthtype").parent().removeClass('sno');
            $("#width").width("55px");
            $("#width").css("padding-right", "22px");
        }
    }
}
function form_cardNumber_maxlength(pobject){
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.maxstringlength;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("maxstringlength").value;
	}
	var temp = $("#" + res).find(".elmWidget input");
	$(temp).attr('maxlength', se);
}

function form_cardNumber_state(pobject) {
    //alert("form_cardNumber_state");
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.state;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("state").value;
    }
    var temp = $("#" + res).find(".elmWidget input");
    if (se == "DISABLED") {
        $(temp).attr('disabled', 'disabled');
        $(temp)[0].type = 'text';
    } else if (se == "READONLY") {
        $(temp).removeAttr('disabled');
        $(temp).attr('readonly', 'true');
        $(temp)[0].type = 'text';
    } else {
        $(temp).removeAttr('disabled');
        $(temp).removeAttr('readonly');
        $(temp)[0].type = 'text';
    }
}

function form_cardNumber_placeholder(pobject) {
    //alert("form_cardNumber_placeholder");
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.placeholder;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("placeholder").value;
    }
    se = widgetutils.getLITValue(se);
    if (!se) {
        se = "";
    }
    var temp = $("#" + res).find(".elmWidget input");
    $(temp).attr('placeholder', se);
}

function form_cardNumber_appearance(pobject) {
    //alert("form_cardNumber_appearance");
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.appearance;
        var inputtype = pobject.inputtype;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("appearance").value;
    }
    var array = ["pri", "sec", "ter"];
    if (array.indexOf(se) == -1) {
        se = "pri";
    }
    var join = array.join(" ");
    var parContnr = utils.getContainer($("#" + res)[0]);
    var parContnrType = parContnr.getAttribute("widgettype");
    if (parContnrType == "FORM" || parContnrType == "TABLE" || parContnrType == "LIST") {
        var temp = $("#" + res).find(".elmWidget input");
    } else {
        var temp = $("#" + res);
    }
    temp.removeClass(join);
    $(temp).addClass(se);
}

function cardNumber_conallign(pobject) {
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.contentalignment;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("contentalignment").value;
    }
    var temp = $("#" + res).find("input");
    $(temp).removeClass('rht cen lft')
    if (se == "LEFT") $(temp).addClass('lft');
    else if (se == "RIGHT") $(temp).addClass('rht');
    else if (se == "CENTER") $(temp).addClass('cen');
}

function cardNumber_labelallign(pobject) {
    if (pobject != undefined) {
        var res = pobject.id;
        var se = pobject.labelalignment;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("labelalignment").value;
    }
    var parentId = $("#" + res)[0].getAttribute("parentid");
    var parentobject = $("#" + parentId);
    if (!parentobject[0].hasAttribute("role")) {
        var temp = $("#" + res).find("#label");
        if (se == "LEFT") $(temp).removeClass('rht cen').addClass('lft');
        else if (se == "RIGHT") $(temp).removeClass('lft cen').addClass('rht');
        else if (se == "CENTER") $(temp).removeClass('rht lft').addClass('cen');
        else $(temp).removeClass('rht cen lft');
    }
}

function form_cardNumber_defaultvalue(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.defaultvalue;
		//var translatedefval = pobject.translatedefaultvalue;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("defaultvalue").value;
		//var translatedefval = document.getElementById("translatedefaultvalue").value;
	}
	/*if(translatedefval == "Y"){
		se = widgetutils.getLITValue(se);
	}*/
	var temp = $("#" + res).find(".elmWidget input");
	$(temp).attr("value", se);
}
/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 For the Saved Properties to be Effective in UI after close and reopen, Functions which manipulate the look and feel in Ui Should be called Here.
 ************************************************************************/
customwidgets.CardNumber.applyProperties = function(pobject) {
    debugger;
    var parentContainer = utils.getContainer($("#" + pobject.id)[0]).getAttribute("widgettype");
    //alert("applyProperties parentContainer > " + JSON.stringify(pobject));
    CardNumber_title(pobject);
    form_cardNumber_colwidth(pobject);
    if (parentContainer == "FORM") {
        element_labelwidth(pobject);
        cardNumber_labelallign(pobject);
    }
    form_cardNumber_state(pobject);
    form_cardNumber_placeholder(pobject);
    form_cardNumber_appearance(pobject);
    //element_conallign(pobject);
    cardNumber_conallign(pobject);
    form_cardNumber_defaultvalue(pobject);
    widgetutils.setWidgetVisibility(pobject);
    form_cardNumber_maxlength(pobject)
};
