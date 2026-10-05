/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
 var customwidgets = {};
}
customwidgets.ExternalLink = {};
customwidgets.ExternalLink.currobj = null;

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
customwidgets.ExternalLink.init = function(pobject) {
	customwidgets.ExternalLink.currobj = pobject;
	var currobjary = designer.selectedobjs;
	var currobjspntr = new Array();
	for (var i = 0; i < currobjary.length; i++) {
		currobjspntr[i] = utils.getDataPointer(currobjary[i]);
	}
	$("#title").blur(function() {
		ExternalLink_title();
	});
	$("#controlid").blur(function() {
				for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("controlid", ldatapointer);
	}
	});
	//Apz312 changes start
	$("#mainPropCollapse").click(function(){
		$("#mainPropAccordian").toggle();
	});
	$("#addcollapse").click(function(){
		$("#addaccordian").toggle();
	});
	$("#eventscollapse").click(function(){
		$("#eventsaccordian").toggle();
	});
	//Apz312 changes end
	$("#options").change(function() {
		var lval = $("#options").prop("checked") ? "Y" : "N";
		$("#options")[0].value = lval;
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("options", ldatapointer);
				try {
					//form_filebrowser_tablecellminwidth(ldatapointer);
					widgetutils.setWidgetVisibility(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		widgetutils.setWidgetVisibility();
		}
	});
	$("#menu").blur(function () {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("menu", ldatapointer);
				try {
					ExternalLink_menu(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		ExternalLink_menu();
		}
	});
	$("#headeralignment").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("headeralignment", ldatapointer);
				try {
					//form_filebrowser_tablecellminwidth(ldatapointer);
					ExternalLink_headeralignment(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		ExternalLink_headeralignment();
		}
	});
	$("#columnalignment").change(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("columnalignment", ldatapointer);
			}
		}
	});
	$("#defaultvalue").blur(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("defaultvalue", ldatapointer);
				try {
					ExternalLink_defaultvalue(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		ExternalLink_defaultvalue();
		}
	});
	$("#url").blur(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("url", ldatapointer);
				try {
					ExternalLink_url(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		ExternalLink_url();
		}
	});
	$("#translatedefaultvalue").change(function () {
		$("#translatedefaultvalue")[0].value = $("#translatedefaultvalue").prop("checked") ? "Y" : "N";
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("translatedefaultvalue", ldatapointer);
				try {
					ExternalLink_defaultvalue(ldatapointer);
				} catch (e) {}
			}
		}
		else{
			ExternalLink_defaultvalue();
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
	$("#selectall").click(function() {
		widgetutils.setMultipleCheckboxes(this, "selectevent");
	});
	//Apz312 changes start
	$("#interfacename").change(function() {
		var linterfaceName = document.getElementById('interfacename').value;
		widgetutils.loadNodetypes("datamodeltype",linterfaceName);
		var ldatamodeltype = document.getElementById('datamodeltype').value;
		widgetutils.loadNodeNames("nodename",ldatamodeltype);
		widgetutils.resetDataModalProps(linterfaceName, ldatamodeltype, document.getElementById('nodename').value, document.getElementById('elementname').value);
		designer.interfacename = linterfaceName;
	});
	$("#datamodeltype").change(function() {
		var ldatamodeltype = document.getElementById('datamodeltype').value;
		widgetutils.loadNodeNames("nodename",ldatamodeltype)
		widgetutils.resetDataModalProps(linterfaceName, ldatamodeltype, document.getElementById('nodename').value, document.getElementById('elementname').value);
		designer.interfacename = linterfaceName;
		designer.datamodeltype = ldatamodeltype;

	 });
	$("#nodename").change(function() {
		var linterfaceName = document.getElementById('interfacename').value;
		var ldatamodeltype = document.getElementById('datamodeltype').value;
		var lnodename = document.getElementById('nodename').value;
		widgetutils.loadElementNames("elementname", lnodename);
		widgetutils.resetDataModalProps(linterfaceName, ldatamodeltype, lnodename, document.getElementById('elementname').value);
		designer.interfacename = linterfaceName;
		designer.datamodeltype = ldatamodeltype;
		designer.nodename = lnodename;
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
		widgetutils.setElementProperties(linterfaceName, ldatamodeltype, lnodename, lelmvalue);
		designer.interfacename = linterfaceName;
		designer.datamodeltype = ldatamodeltype;
		designer.nodename = lnodename;
		designer.elementname = lelmvalue;
	});
	$("#appearance").change(function(){
		ExternalLink_appearance();
	});
	
	$("#state").change(function(){
	    if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("state", ldatapointer);
				$("#id").val(currobjary[i].id);
				try {
					ExternalLink_state(ldatapointer);
				} catch (e) {}
			}
		}
		else{
			ExternalLink_state();
		}
	});

	//Apz312 changes end
};

/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.ExternalLink.getTemplate = function(pobject, pparentobject) {
	var widgethtml = "";
    var parentContainer = utils.getContainer(pparentobject).getAttribute("widgettype");
    var parentContId = utils.getContainer(pparentobject).id;
    if(parentContainer == "TABLE") {
		var header = $("#"+parentContId).find("#"+parentContId+"_header");
		var newHeader = "<th onclick='widgetutils.selectTableElement(this, event);'>External Link</th>";
		var parentContainer = utils.getContainer(pparentobject);
		header.append(newHeader);
		widgethtml = "<td id ='appzillonid' class='apzlocontainer ett-extl pri'>";
		var receivedTemplate = "<div class='eboxh'><a class='txtlink-p elmWidget' href='javascript:;'>External Link</a></div>";
		widgethtml = widgethtml + receivedTemplate;
		widgethtml = widgethtml + "</td>";
    } else if(parentContainer == "LIST" || parentContainer == "NAVBAR") {
        widgethtml = "<div class='eobox' id ='appzillonid'  widgetcategory='elements' widgetid='ExternalLink' tabindex='0' ><a class='txtlink-p elmWidget' href='javascript:;'>External Link</a></div>";
    } else {
        if(pparentobject.hasAttribute("role")) {
            var receivedTemplate = "<a class='txtlink-p elmWidget' href='javascript:;'>External Link</a>";
			widgethtml = '<li id ="appzillonid" class="apzlocontainer elpad">';
			widgethtml = widgethtml + receivedTemplate;
			widgethtml = widgethtml + '</li>';
        } else {
            //widgethtml = '<ul id ="appzillonid" class="apzlocontainer srow eobox wrapped"><li id="label" class="w40"></li><li id="content" class="w60 ebox"><a class="txtlink-p elmWidget" href="javascript:;">External Link</a></li></ul>';
            widgethtml = '<ul id ="appzillonid" class="apzlocontainer srb eoc"><li id="label" class="etw-40"></li><li id="content" class="eio etw-60"><a class="ett-extl pri elmWidget" href="#appzillon">External Link</a></li></ul>';
        }
    }
	return widgethtml;
};

/************************************************************************
 Below function is called from layout.populateProperties() to populate the widget properties provided by the developer
 layout.populateProperties() will be called when the widget has been selected in the designer.
 Thats when the widget properties page will be populated.
	
 Send the widgets property id to the widgetutils.getPropety() function with 'pproperties' object
 So that the value stored in 'pproperties' object will be assigned to the element in the screen with the id which has been passed.
 Example: In below function id and pid are passed to the widgetutils.getProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.ExternalLink.populateProperties = function(pproperties) {

	widgetutils.getProperty("id", pproperties);
	widgetutils.getProperty("pid", pproperties);
	widgetutils.getProperty("title", pproperties);
	widgetutils.getProperty("controlid", pproperties);
	ExternalLink_displayElementsProperties(pproperties);
	widgetutils.getProperty("headeralignment", pproperties);
	widgetutils.getProperty("columnalignment", pproperties);
	widgetutils.getProperty("options", pproperties);
	widgetutils.getProperty("hint", pproperties);
	widgetutils.getProperty("cssclasses", pproperties);
	widgetutils.getProperty("tooltip", pproperties);
	widgetutils.getProperty("translatedefaultvalue", pproperties);
	widgetutils.getProperty("url", pproperties);
	widgetutils.getEventElements("eventstable", pproperties);
	widgetutils.getProperty("appearance", pproperties);
	widgetutils.getProperty("state",pproperties);
	//ExternalLink_options(pproperties);
};

/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.ExternalLink.saveProperties = function(pproperties) {
	
	widgetutils.setProperty("id", pproperties);
	widgetutils.setProperty("pid", pproperties);
	widgetutils.setProperty("title", pproperties);
	widgetutils.setProperty("controlid", pproperties);
	widgetutils.setProperty("headeralignment", pproperties);
	widgetutils.setProperty("columnalignment", pproperties);
	widgetutils.setProperty("options", pproperties);
	widgetutils.setProperty("tooltip", pproperties);
	widgetutils.setProperty("translatedefaultvalue", pproperties);
	widgetutils.setProperty("url", pproperties);
	widgetutils.setProperty("hint", pproperties);
	widgetutils.setProperty("cssclasses", pproperties);
	widgetutils.setEvent("eventstable", pproperties);
	widgetutils.setProperty("appearance", pproperties);
	widgetutils.setProperty("state",pproperties);
};

customwidgets.ExternalLink.updateDefaultValues = function(pobject, pparentobject) {
	var parentContainer = utils.getContainer(pparentobject).getAttribute("widgettype");
	if(parentContainer == "TABLE"){
		document.getElementById("title").value ="External Link";
	}
	document.getElementById("defaultvalue").value ="External Link";
	document.getElementById("appearance").value ="pri";
}

/************************************************************************
 Below function is called while generating the widget html from IDE
 Provide the html as you want to display the widget in the generated page.
	
 In below function lcontent contains the widget related data. Access them with .propertyId
 Ex : If there is a property called title you can access it as lcontent.title
 ************************************************************************/
function getHTML(pproperties) {
 var lcontent = JSON.parse(pproperties);
 var appearance = lcontent.appearance;
 var appearanceCls = "", shownoneCls = "", cssCls = "", toolTip = "", tooltipCls = "",stateCls="";
 appearanceCls = appearance ? " "+appearance : " pri";
 if (lcontent.options == "N") {
     shownoneCls = " sno ";
 }
 if (lcontent.state == "DISABLED") {
     stateCls = " disabled ";
 }
 if(lcontent.cssclasses){
    cssCls = " "+lcontent.cssclasses;
  }
  if (lcontent.tooltip) {
    toolTip = " original-title="+lcontent.tooltip+"";
    tooltipCls = " tooltipcls";
 }
 var html = "<a id ='"+lcontent.name+"' class='ett-extl elmWidget" + appearanceCls + shownoneCls + stateCls + cssCls+tooltipCls + "' " + toolTip + " target='_blank' href='"+lcontent.url+"'>" + lcontent.defaultvalue + "</a>";
 return html;
}

function ExternalLink_displayElementsProperties(pproperties) {
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
    
function ExternalLink_title(pobject) {
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
		if(parentobject[0].hasAttribute("role")) {
			var firstChild = parentobject.find("#"+parentId+"_receiver").children()[0].id;
			if(firstChild == res)
				parentobject.find("#group1").html("");
		}
	} else {
	    if(utils.isNull(se)) {
       		$("#"+res+"_heading").html("&nbsp;");
		} else {
			$("#"+res+"_heading").html(se);	
		}
	}
}

function ExternalLink_headeralignment(pobject) {
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

function ExternalLink_defaultvalue(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.defaultvalue;
		var translatedefval = pobject.translatedefaultvalue;
	} else {
		var se = document.getElementById("defaultvalue").value;
		var res = document.getElementById("id").value;
		var translatedefval = document.getElementById("translatedefaultvalue").value;
	}
	if(translatedefval == "Y"){
		se = widgetutils.getLITValue(se);
	}
	$("#" + res).find(".elmWidget").html(se);
}

function ExternalLink_url(pobject) {
	if(pobject != undefined) {
		var res = pobject.id;
		var se = pobject.url;
	} else {
		var se = document.getElementById("url").value;
		var res = document.getElementById("id").value;
	}
	var temp = $("#" + res).find(".elmWidget");
	temp.attr("href", "javascript:" + se + ";");
}

function ExternalLink_appearance(pobject) {
	if (pobject != undefined) {
		var res = pobject.id;
		var se = pobject.appearance;
	} else {
		var se = document.getElementById("appearance").value;
		var res = document.getElementById("id").value;
	}
	var linkElm = $("#"+res).find('.elmWidget');
	$(linkElm).removeClass("pri sec ter");
	var appClass = !utils.isNull(se) ? " "+se: " pri";
	$(linkElm).addClass(appClass);
}
function ExternalLink_state(pobject){
    if (pobject != undefined) {
		var res = pobject.id;
		var se = pobject.state;
	} else {
		var se = document.getElementById("state").value;
		var res = document.getElementById("id").value;
	}
	var linkElm = $("#"+res).find('.elmWidget');
	if(se == "DISABLED") {
		$(linkElm).addClass("disabled");
	} else{
		$(linkElm).removeClass("disabled");
	}
}
/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 ************************************************************************/
customwidgets.ExternalLink.applyProperties = function(pobject) {
	var parentContainer = utils.getContainer($("#"+pobject.id)[0]).getAttribute("widgettype");
	ExternalLink_title(pobject);
	widgetutils.setWidgetVisibility(pobject);
	ExternalLink_defaultvalue(pobject);
	ExternalLink_appearance(pobject);
	ExternalLink_url(pobject);
	if(parentContainer == "TABLE") {
		ExternalLink_headeralignment(pobject);
	}
	ExternalLink_state(pobject);
};
