/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
 var customwidgets = {};
}
customwidgets.SortCodeAccount = {};
customwidgets.SortCodeAccount.currobj = null;

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
customwidgets.SortCodeAccount.init = function(pobject) {
	customwidgets.SortCodeAccount.currobj = pobject;
	var currobjary = designer.selectedobjs;
	var currobjspntr = new Array();
	for (var i = 0; i < currobjary.length; i++) {
		currobjspntr[i] = utils.getDataPointer(currobjary[i]);
	}
	//Apz312 changes start
	$("#mainPropCollapse").click(function(){
		$("#mainPropAccordian").toggle();
	});
	$("#additionalpropcollapse").click(function(){
		$("#additionalprop").toggle();
	});
	$("#eventscollapse").click(function(){
		$("#eventsaccordian").toggle();
	});
	$("#title").blur(function() {
		SortCodeAccount_title();
	});
	$("#width").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("width", ldatapointer);
				try {
					SortCodeAccount_colwidth(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		SortCodeAccount_colwidth();
		}
	});
	$("#customwidth").blur(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("customwidth", ldatapointer);
				try {
					SortCodeAccount_customwidth(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		SortCodeAccount_customwidth();
		}
	});
	$("#customwidthtype").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("customwidthtype", ldatapointer);
				try {
					SortCodeAccount_customwidth(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		SortCodeAccount_customwidth();
		}
	});
	$("#labelwidth").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("labelwidth", ldatapointer);
				try {
					SortCodeAccount_labelwidth(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		SortCodeAccount_labelwidth();
		}
	});
	$("#cssclasses").blur(function () {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("cssclasses", ldatapointer);
			}
		}
	});
	$("#contentalignment").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("contentalignment", ldatapointer);
				try {
					SortCodeAccount_conallign(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		SortCodeAccount_conallign();
		}
	});
	$("#labelalignment").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("labelalignment", ldatapointer);
				try {
					SortCodeAccount_labelallign(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		SortCodeAccount_labelallign();
		}
	});
	$("#state").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("state", ldatapointer);
				try {
					SortCodeAccount_state(ldatapointer);
					
				} catch (e) {}
			}
		}
		else{
		SortCodeAccount_state();
		}
	});
	$("#defaultvalue").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("defaultvalue", ldatapointer);
				try {
					SortCodeAccount_defaultvalue(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		SortCodeAccount_defaultvalue();
		}
	});
	$("#options").change(function() {
		var lval = $("#options").prop("checked") ? "Y" : "N";
		$("#options")[0].value = lval;
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("options", ldatapointer);
				try {
					widgetutils.setWidgetVisibility(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		widgetutils.setWidgetVisibility();
		}
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
                    SortCodeAccount_headerAlign(ldatapointer);
                } catch (e) {}
            }
        } else {
            SortCodeAccount_headerAlign();
        }
    });
	//Apz312 changes end
	$("#selectall").click(function() {
		widgetutils.setMultipleCheckboxes(this, "selectevent");
	});
	$("#placeholder").blur(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("placeholder", ldatapointer);
				try {
					SortCodeAccount_placeholder(ldatapointer);
				} catch (e) {}
			}
		} else{
		    SortCodeAccount_placeholder();
		}
	});
	$("#appearance").change(function () {
		SortCodeAccount_appearance();
	});
};

/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.SortCodeAccount.getTemplate = function(pobject, pparentobject,receviedForChild,uiParentObj) {
    var widgethtml = "";
    var parentContainer = utils.getContainer(pparentobject).getAttribute("widgettype");
    var parentContId = utils.getContainer(pparentobject).id;
    if(parentContainer == "TABLE") {
        var header = $("#"+parentContId).find("#"+parentContId+"_header");
		var newHeader = "<th onclick='widgetutils.selectTableElement(this, event);'>Sort Code Account</th>";
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
		var elmContent = '<span class="ett-srta pri elmWidget"><ul><li><input type="text" class="etw-100 ett-inpt pri" placeholder=""></li></ul></span></div>';
		widgethtml = widgethtml + elmContent;
		widgethtml = widgethtml + "</td>";
    } else if(parentContainer == "LIST" || parentContainer == "NAVBAR") {
        widgethtml = '<span id ="appzillonid"  widgetcategory="elements" widgetid="SortCodeAccount" tabindex="0" class="ett-srta pri ebox apzlocontainer elpad elmWidget"><ul><li><input type="text" class="etw-100 ett-inpt pri" placeholder=""></li></ul></span>';
    } else {
        if(pparentobject.hasAttribute("role")) {
            var receivedTemplate = '<span class="ett-srta pri elmWidget"><ul><li><input type="text" class="etw-100 ett-inpt pri" placeholder=""></li></ul></span>';
			widgethtml = '<li id ="appzillonid" class="apzlocontainer elpad">';
			widgethtml = widgethtml + receivedTemplate;
			widgethtml = widgethtml + '</li>';
        } else {
            widgethtml = '<ul id ="appzillonid" class="apzlocontainer srb eoc"><li id="label" class="etw-40">Sort Code Account</li><li id="content" class="eio etw-60"><span class="ett-srta pri elmWidget"><ul><li><input type="text" class="etw-100 ett-inpt pri" placeholder=""></li></ul></span></li></ul>';
        }
    }
	return widgethtml;
};

customwidgets.SortCodeAccount.updateDefaultValues = function(pobject, pparentobject) {
	document.getElementById("title").value ="Sort Code Account";
}
/************************************************************************
 Below function is called from layout.populateProperties() to populate the widget properties provided by the developer
 layout.populateProperties() will be called when the widget has been selected in the designer.
 Thats when the widget properties page will be populated.
	
 Send the widgets property id to the widgetutils.getPropety() function with 'pproperties' object
 So that the value stored in 'pproperties' object will be assigned to the element in the screen with the id which has been passed.
 Example: In below function id and pid are passed to the widgetutils.getProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.SortCodeAccount.populateProperties = function(pproperties) {
	widgetutils.getProperty("id", pproperties);
	widgetutils.getProperty("pid", pproperties);
	widgetutils.getProperty("title", pproperties);
	SortCodeAccount_displayElementsProperties(pproperties);
	widgetutils.getProperty("tooltip", pproperties);
	widgetutils.getProperty("width", pproperties);
	SortCodeAccount_colwidth();
	widgetutils.getProperty("customwidth", pproperties);
	widgetutils.getProperty("customwidthtype", pproperties);
	widgetutils.getProperty("labelwidth", pproperties);
	widgetutils.getProperty("contentalignment", pproperties);
	widgetutils.getProperty("labelalignment", pproperties);
	widgetutils.getProperty("translatedefaultvalue", pproperties);
	widgetutils.getProperty("placeholder", pproperties);
	widgetutils.getProperty("state", pproperties);
	widgetutils.getProperty("options", pproperties);
	widgetutils.getProperty("headeralignment", pproperties);
	widgetutils.getProperty("hint", pproperties);
	widgetutils.getProperty("cssclasses", pproperties);
	widgetutils.getEventElements("eventstable", pproperties);
	widgetutils.getProperty("appearance", pproperties);
	widgetutils.getProperty("labelrequired", pproperties);
};

/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.SortCodeAccount.saveProperties = function(pproperties) {
	widgetutils.setProperty("id", pproperties);
	widgetutils.setProperty("pid", pproperties);
	widgetutils.setProperty("title", pproperties);
	widgetutils.setProperty("width", pproperties);
	widgetutils.setProperty("customwidth", pproperties);
	widgetutils.setProperty("customwidthtype", pproperties);
	widgetutils.setProperty("labelwidth", pproperties);
	widgetutils.setProperty("contentalignment", pproperties);
	widgetutils.setProperty("labelalignment", pproperties);
	widgetutils.setProperty("translatedefaultvalue", pproperties);
	widgetutils.setProperty("placeholder", pproperties);
	widgetutils.setProperty("state", pproperties);
	widgetutils.setProperty("options", pproperties);
	widgetutils.setProperty("headeralignment", pproperties);
	widgetutils.setProperty("tooltip", pproperties);
	widgetutils.setProperty("hint", pproperties);
	widgetutils.setProperty("cssclasses", pproperties);
	widgetutils.setEvent("eventstable", pproperties);
	widgetutils.setProperty("appearance", pproperties);
	widgetutils.setProperty("labelrequired", pproperties);
};

/************************************************************************
 Below function is called while generating the widget html from IDE
 Provide the html as you want to display the widget in the generated page.
	
 In below function lcontent contains the widget related data. Access them with .propertyId
 Ex : If there is a property called title you can access it as lcontent.title
 ************************************************************************/
function getHTML(pproperties) {
 var lcontent = JSON.parse(pproperties);
 var html = "",rowNo = "",widthCls="",widthAttr = "", elmId = lcontent.name;
 var alignment = "", cssCls = "", snoCls = "", toolTip = "", tooltipCls = "", placeholder = "", stateAttr = "", apprCls = " pri";
 if(lcontent.contentalignment == "LEFT"){
     alignment = "lft";
 } else if(lcontent.contentalignment == "CENTER"){
     alignment = "cen";
 } else if(lcontent.contentalignment == "RIGHT"){
     alignment = "rht";
 }
 if (lcontent.state == "DISABLED") {
     stateAttr = " disabled='disabled'";
 } else if (lcontent.state == "READONLY") {
     stateAttr = " readonly='readonly'";
 }
 if (lcontent.tooltip) {
    toolTip = " original-title="+lcontent.tooltip+"";
    tooltipCls = " tooltipcls";
}
if (lcontent.placeholder) {
    placeholder = " placeholder="+lcontent.placeholder+"";
}
 if (lcontent.options == "N") {
     snoCls = " sno";
 }
 if (lcontent.cssclasses != undefined) {
     cssCls = " " + lcontent.cssclasses;
 }
 if (lcontent.rowno != undefined && lcontent.rowno != -1) {
     rowNo = "_"+lcontent.rowno;
 }
 if (lcontent.appearance != null && lcontent.appearance != undefined && lcontent.appearance != "") {
     apprCls = lcontent.appearance + " ";
 }
 if (lcontent.width!="CUSTOM") {
     widthCls = " etw-"+lcontent.width;
 } else {
     widthAttr = " width:"+lcontent.customwidth+lcontent.customwidthtype;
 }
 if (lcontent.state == "READONLY") {
     html = '<span id ="'+elmId+rowNo+'" class="ett-srta '+apprCls+cssCls+snoCls+tooltipCls+'" rowno="'+lcontent.rowno+'"><ul>'+
    	'<li><p type="text" id="'+elmId+'_00'+rowNo+'" class="'+alignment+widthCls+'" '+placeholder+stateAttr+' style="'+widthAttr+'"></p></li>'+
    '</ul></span>';
 } else {
     html = '<span id ="'+elmId+rowNo+'" class="ett-srta '+apprCls+cssCls+snoCls+tooltipCls+'" rowno="'+lcontent.rowno+'"><ul>'+
    	'<li><input type="text" id="'+elmId+'_00'+rowNo+'" class="ett-inpt '+apprCls+alignment+widthCls+'" style="'+widthAttr+'" '+placeholder+stateAttr+' onkeydown="apz.SortCodeAccount.validateInput(this,event);"></li>'+
    '</ul>';
 }
 
 return html;
}

function SortCodeAccount_displayElementsProperties(pproperties) {
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

function SortCodeAccount_title(pobject) {
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
	if(parentContainerType != "TABLE") {
		var parentobject = $("#" + parentId);
		if (parentContainerType == "FORM") {
		    if(parentobject[0].hasAttribute("role")) {
    			var firstChild = parentobject.find("#"+parentId+"_receiver").children()[0].id;
    			if(firstChild == res)
    				parentobject.find("#group1").html("");
    		} else {
    		    $("#" + res).find("#label").html(se);
    		}
		}
	} else {
		if(utils.isNull(se)) {
       		$("#"+res+"_heading").html("&nbsp;");
		} else {
			$("#"+res+"_heading").html(se);	
		}
	}
}

function SortCodeAccount_placeholder(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.placeholder;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("placeholder").value;
	}
	se = widgetutils.getLITValue(se);
	if(!se){
		se = "";
	}
	var temp = $("#" + res).find("input");
	$(temp).attr('placeholder', se);
}

function SortCodeAccount_labelwidth(pobject) {
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
	var classarray = ['etw-0', 'etw-5', 'etw-10', 'etw-15', 'etw-20', 'etw-25', 'etw-30', 'etw-35', 'etw-40', 'etw-45', 'etw-50', 'etw-55', 'etw-60', 'etw-65', 'etw-70', 'etw-75', 'etw-80', 'etw-85', 'etw-90', 'etw-95', 'etw-100'];
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

function SortCodeAccount_colwidth(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.width;
		var custom = pobject.customwidth;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("width").value;
		var custom = document.getElementById("customwidth");
	}
	var temp = $("#" + res).find("input");
	var classarray = ['etw-0', 'etw-5', 'etw-10', 'etw-15', 'etw-20', 'etw-25', 'etw-30', 'etw-35', 'etw-40', 'etw-45', 'etw-50', 'etw-55', 'etw-60', 'etw-65', 'etw-70', 'etw-75', 'etw-80', 'etw-85', 'etw-90', 'etw-95', 'etw-100'];
	if((!utils.isNull(se)) && se != "CUSTOM") {
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

function SortCodeAccount_customwidth(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.customwidth;
		var setype = pobject.customwidthtype;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("customwidth").value;
		var setype = document.getElementById("customwidthtype").value;
	}
	if(!utils.isNull(se)) {
		var temp = $("#" + res).find("input");
		$(temp).css('width', se+setype);
	}
}

function SortCodeAccount_conallign(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.contentalignment;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("contentalignment").value;
	}
	var parContnr = utils.getContainer($("#"+res)[0]);
	var parContnrType = parContnr.getAttribute("widgettype");
	if(parContnrType == "FORM" || parContnrType == "TABLE") {
		var temp = $("#" + res).find(".elmWidget input");
	} else {
		var temp = $("#"+res).find('input');
	}
	$(temp).removeClass('rht cen lft');
	if(se == "LEFT")
		$(temp).addClass('lft');
	else if(se == "RIGHT")
		$(temp).addClass('rht');
	else if(se == "CENTER")
		$(temp).addClass('cen');
}

function SortCodeAccount_labelallign(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.labelalignment;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("labelalignment").value;
	}
	var parentId = $("#"+res)[0].getAttribute("parentid");
	var parentobject = $("#" + parentId);
	if(!parentobject[0].hasAttribute("role")) {
		var temp = $("#" + res).find("#label");
    	if(se == "LEFT")
    		$(temp).removeClass('rht cen').addClass('lft');
    	else if(se == "RIGHT")
    		$(temp).removeClass('lft cen').addClass('rht');
    	else if(se == "CENTER")
    		$(temp).removeClass('rht lft').addClass('cen');
    	else
    		$(temp).removeClass('rht cen lft');
	}
}

function SortCodeAccount_appearance(pobject) {
    if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.appearance;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("appearance").value;
	}
	var parContnr = utils.getContainer($("#"+res)[0]);
	var parContnrType = parContnr.getAttribute("widgettype");
	if(parContnrType == "FORM" || parContnrType == "TABLE") {
		var temp = $("#" + res).find(".elmWidget");
	} else {
		var temp = $("#"+res);
	}
	var temp1 = $(temp).find("input");
	temp.removeClass('pri sec ter');
	temp1.removeClass('pri sec ter');
	if (!utils.isNull(se)) {
		$(temp).addClass(se);
		$(temp1).addClass(se);
	}else {
		$(temp).addClass('pri');
		$(temp1).addClass('pri');
	}
}

function SortCodeAccount_state(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.state;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("state").value;
	}
	var temp = $("#" + res).find("input");
	if(se == "DISABLED") {
		$(temp).attr('disabled', 'disabled');
		$(temp)[0].type = 'text';
	} else if(se == "READONLY") {
		$(temp).removeAttr('disabled');
		$(temp).attr('readonly', 'true');
		$(temp)[0].type = 'text';
	} else {
		$(temp).removeAttr('disabled');
		$(temp).removeAttr('readonly');
		$(temp)[0].type = 'text';
	}
}

function SortCodeAccount_headerAlign(pobject) {
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

/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 ************************************************************************/
customwidgets.SortCodeAccount.applyProperties = function(pobject) {
    var parentContainer = utils.getContainer($("#"+pobject.id)[0]).getAttribute("widgettype");
	SortCodeAccount_title(pobject);
	SortCodeAccount_colwidth(pobject);
	SortCodeAccount_customwidth(pobject);
	SortCodeAccount_conallign(pobject);
	SortCodeAccount_state(pobject);
	SortCodeAccount_placeholder(pobject);
	SortCodeAccount_appearance(pobject);
	widgetutils.setWidgetVisibility(pobject);
	if(parentContainer == "FORM") {
		SortCodeAccount_labelwidth(pobject);
	    SortCodeAccount_labelallign(pobject);
	}
	if(parentContainer == "TABLE") {
	    SortCodeAccount_headerAlign(pobject);
	}
};
