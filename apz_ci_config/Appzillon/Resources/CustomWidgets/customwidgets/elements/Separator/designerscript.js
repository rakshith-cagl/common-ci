/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
 var customwidgets = {};
}
customwidgets.Separator = {};
customwidgets.Separator.currobj = null;

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
customwidgets.Separator.init = function(pobject) {
customwidgets.Separator.currobj = pobject;
 $("#nodename").change(function() {
 widgetutils.loadElementNames("elementname",document.getElementById('nodename').value);
 });
 $("#elementname").change(function() {
 widgetutils.loadName(document.getElementById('elementname').value,document.getElementById('nodename').value);
 });
 $("#mainPropCollapse").click(function() {
	$("#mainPropAccordian").toggle();
});
$("#eventscollapse").click(function() {
	$("#eventsaccordian").toggle();
});
$("#appearance").change(function () {
	Separator_appearance();
});
$("#withtext").change(function() {
		var lval = $("#withtext").prop("checked") ? "Y" : "N";
		$("#withtext")[0].value = lval;
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				widgetutils.setProperty("withtext", ldatapointer);
				try {
					//widgetutils.setWidgetVisibility(ldatapointer);
					Separator_toggleTextProp(ldatapointer);
					Separator_withtext(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		    //widgetutils.setWidgetVisibility();
		    Separator_toggleTextProp();
		    Separator_withtext();
		}
});
$("#text").blur(function() {
		if(designer.multipleselect){
			for (var i = 0; i < currobjary.length; i++) {
				$("#id").val(currobjary[i].id);
				var ldatapointer = currobjspntr[i];
				try {
					Separator_textChange(ldatapointer);
				} catch (e) {}
			}
		}
		else{
		    Separator_textChange();
		}
});
$("#labelwidth").change(function() {
	if(designer.multipleselect){
		for (var i = 0; i < currobjary.length; i++) {
			$("#id").val(currobjary[i].id);
			var ldatapointer = currobjspntr[i];
			widgetutils.setProperty("labelwidth", ldatapointer);
			try {
				Separator_labelWidth(ldatapointer);
			} catch (e) {}
		}
	}
	else{
		Separator_labelWidth();
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


/***************************************************************************************
  Attaching on blur event Handler to the property title.
  This way event Handlers can be attached to others properties as well

*****************************************************************************************/
/*$("#title").blur(function() {
	Separator_title();
});*/


};

/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.Separator.getTemplate = function(pobject, pparentobject,receviedForChild,uiParentObj) {
 //var widgethtml = "<div id ='appzillonid'  widgetcategory='ELEMENT' widgettype='Separator' widgetid='Separator' custom='Y' class='apzlocontainer'><hr class='ett-sept pri'></hr></div>";
 var widgethtml = "";
 var parentContainer = utils.getContainer(pparentobject).getAttribute("widgettype");
 var parentContId = utils.getContainer(pparentobject).id;
 if(parentContainer == "LIST" || parentContainer == "NAVBAR") {
 	widgethtml = '<div id ="appzillonid" widgetcategory="ELEMENT" widgettype="Separator" widgetid="Separator" custom="Y" class="apzlocontainer"><hr class="ett-sept pri"></hr></div>';
 } else if(parentContainer == "FORM") {
	 	if(pparentobject.hasAttribute("role")) {
	 		var receivedTemplate = '<hr class="ett-sept pri"></hr>';
			widgethtml = '<li id ="appzillonid" class="apzlocontainer elpad">';
			widgethtml = widgethtml + receivedTemplate;
			widgethtml = widgethtml + '</li>';
	 	} else {
		 	 widgethtml = '<ul id ="appzillonid" widgetcategory="ELEMENT" widgettype="Separator" widgetid="Separator" custom="Y" class="apzlocontainer srb eoc"><li id="label" class="etw-0"></li><li id="content" class="eio etw-100"><hr class="ett-sept pri"></hr></li></ul>'; 		
	 	}
 	}
 return widgethtml;
};

customwidgets.Separator.updateDefaultValues = function(pobject, pparentobject) {
	//document.getElementById("title").value = "Separator";
}

/************************************************************************
 Below function is called from layout.populateProperties() to populate the widget properties provided by the developer
 layout.populateProperties() will be called when the widget has been selected in the designer.
 Thats when the widget properties page will be populated.
	
 Send the widgets property id to the widgetutils.getPropety() function with 'pproperties' object
 So that the value stored in 'pproperties' object will be assigned to the element in the screen with the id which has been passed.
 Example: In below function id and pid are passed to the widgetutils.getProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.Separator.populateProperties = function(pproperties) {
	debugger;
 widgetutils.getProperty("id", pproperties);
 widgetutils.getProperty("pid", pproperties);
 //widgetutils.getProperty("title", pproperties);
 widgetutils.getProperty("cssclasses", pproperties);
 widgetutils.getProperty("withtext", pproperties);
 widgetutils.getProperty("text", pproperties);
 widgetutils.getProperty("appearance", pproperties);
 Separator_displayElementsProperties(pproperties);
 widgetutils.getProperty("labelwidth", pproperties);
 widgetutils.getEvent("eventstable", pproperties);
 widgetutils.loadNodeNames("nodename");
 widgetutils.getProperty("nodename",pproperties);
 widgetutils.loadElementNames("elementname",document.getElementById('nodename').value);
 widgetutils.getProperty("elementname",pproperties);
 Separator_toggleTextProp();
};

/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.Separator.saveProperties = function(pproperties) {
 widgetutils.setProperty("id", pproperties);
 widgetutils.setProperty("pid", pproperties);
 //widgetutils.setProperty("title", pproperties);
 widgetutils.setProperty("cssclasses", pproperties);
 widgetutils.setProperty("withtext", pproperties);
 widgetutils.setProperty("text", pproperties);
 widgetutils.setProperty("appearance", pproperties);
 widgetutils.setProperty("labelwidth", pproperties);
 widgetutils.setEvent("eventstable", pproperties);
 widgetutils.setProperty("nodename",pproperties);
 widgetutils.setProperty("elementname",pproperties);
};

/************************************************************************
 Below function is called while generating the widget html from IDE
 Provide the html as you want to display the widget in the generated page.
	
 In below function lcontent contains the widget related data. Access them with .propertyId
 Ex : If there is a property called title you can access it as lcontent.title
 ************************************************************************/
function getHTML(pproperties) {
 var lcontent = JSON.parse(pproperties);
 var isWithText = lcontent.withtext;
 var appearance = lcontent.appearance;
 var cssclass;
 if(lcontent.cssclass == undefined || lcontent.cssclass == null){
 	cssclass = "";
 } else {
 	cssclass = " "+lcontent.cssclass;
 }
 var html;
 if(isWithText == "Y") {
     html = "<div id ='"+lcontent.id+"' class='ett-sett with-text "+appearance+cssclass+"'><span>"+lcontent.text+"</span></div>";
 } else {
     html = "<hr id ='"+lcontent.id+"' class='ett-sept "+appearance+cssclass+"'></hr>";   
 }
 return html;
}

/////////// Title Change function called by onBlur Event Handler of title Property.
// Similar functions can be written for all the properties calling from thier respective Event Handlers

function Separator_title(pobject) {
	var se, res;
	if(pobject) {
		se = pobject.id;
		res = pobject.title;
	} else {
		se = document.getElementById("id").value;
		res = document.getElementById("title").value;
	}
	$("#"+se).html(res);
}

function Separator_labelWidth(pobject) {
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

function Separator_withtext(pobject) {
    var id,isWithText,template,text,classes;
    if(pobject) {
        id = pobject.id;
        isWithText = pobject.withtext;
        text = pobject.text;
        appearance = pobject.appearance;
    } else {
        id= document.getElementById("id").value;
        isWithText = $("#withtext").is(":checked") ? "Y" : "N";
        text = document.getElementById("text").value;
        appearance = document.getElementById("appearance").value;
    }
    if(utils.isNull(text)) {
        text = "";
    }
    //classes = $("#"+id).attr("class");
    if(isWithText == "Y") {
        //template = "<div id ='"+id+"'  custom = 'Y' widgetcategory='ELEMENT' widgettype='Separator' widgetid='Separator' class='apzlocontainer ett-sett with-text "+appearance+"'><span>"+text+"</span></div>";
    	template = "<div class='ett-sett "+appearance+"'><span>"+text+"</span></div>";
    } else {
        //template = "<hr id ='"+id+"'  custom = 'Y' widgetcategory='ELEMENT' widgettype='Separator' widgetid='Separator' class='apzlocontainer ett-sept "+appearance+"'></hr>";
    	template = "<hr class='ett-sept "+appearance+"'></hr>";
    }
    var parContnr = utils.getContainer($("#"+id)[0]);
	var parContnrType = parContnr.getAttribute("widgettype");
	if(parContnrType == "FORM") {
		$("#"+id).find('#content').html(template);	
	} else if(parContnrType == "LIST" || parContnrType == "NAVBAR") {
		$("#"+id).html(template);
	}
    //$("#"+id).html(template);
    //layout.initNewWidget($("#"+id)[0]);
}

function Separator_displayElementsProperties(pproperties) {
	var id = document.getElementById("id").value;
	var parentContainer = utils.getContainer($("#"+id)[0]).getAttribute("widgettype");
	alert("parentContainer is "+parentContainer);
	if (parentContainer == "FORM") {
		$(".forForm").removeClass("sno");
	} else {
		$(".forForm").addClass("sno");
	}
}

function Separator_toggleTextProp(pobject) {
	var withtext = $("#withtext").is(":checked") ? "Y" : "N";
	if(withtext == "Y") {
		$("#text").parents('ul.control-group:first').removeClass("sno");
	} else {
		$("#text").parents('ul.control-group:first').addClass("sno");
	}
}

function Separator_textChange(pobject) {
	var id,text;
	if(pobject) {
		id = pobject.id;
		text = pobject.text;
	} else {
		id = document.getElementById("id").value;
		text = document.getElementById("text").value;
	}
	$("#"+id+" span").html(text);
}

function Separator_appearance(pobject) {
	if(pobject) {
		var res = pobject.id;
		var se = pobject.appearance;
	} else {
		var res = document.getElementById("id").value;
		var se = document.getElementById("appearance").value;
	}
	$("#"+res).find('hr').removeClass('pri sec ter').addClass(se);
	$("#"+res).find('div').removeClass('pri sec ter').addClass(se);
}

/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 For the Saved Properties to be Effective in UI after close and reopen, Functions which manipulate the look and feel in Ui Should be called Here.
 ************************************************************************/
customwidgets.Separator.applyProperties = function(pobject) {
	//Separator_title(pobject);
	var parentContainer = utils.getContainer($("#"+pobject.id)[0]).getAttribute("widgettype");
	Separator_withtext(pobject);
	Separator_appearance(pobject);
	if(parentContainer == "FORM") {
		Separator_labelWidth(pobject);
	}
};
