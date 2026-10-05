/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
 var customwidgets = {};
}
customwidgets.appzillonelement = {};
customwidgets.appzillonelement.currobj = null;

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
customwidgets.appzillonelement.init = function(pobject) {
customwidgets.appzillonelement.currobj = pobject;
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
/***************************************************************************************
  Attaching on blur event Handler to the property title.
  This way event Handlers can be attached to others properties as well

*****************************************************************************************/
$("#title").blur(function() {
	appzillonelement_title();
});


};

/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.appzillonelement.getTemplate = function(pobject, pparentobject) {
 var widgethtml = "<div id ='appzillonid'  widgetcategory='element' widgetid='appzillonelement' >appzillonelement</div>";
 return widgethtml;
};

customwidgets.appzillonelement.updateDefaultValues = function(pobject, pparentobject) {
	document.getElementById("title").value = "appzillonelement";
}

/************************************************************************
 Below function is called from layout.populateProperties() to populate the widget properties provided by the developer
 layout.populateProperties() will be called when the widget has been selected in the designer.
 Thats when the widget properties page will be populated.
	
 Send the widgets property id to the widgetutils.getPropety() function with 'pproperties' object
 So that the value stored in 'pproperties' object will be assigned to the element in the screen with the id which has been passed.
 Example: In below function id and pid are passed to the widgetutils.getProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.appzillonelement.populateProperties = function(pproperties) {
 widgetutils.getProperty("id", pproperties);
 widgetutils.getProperty("pid", pproperties);
 widgetutils.getProperty("title", pproperties);
 widgetutils.getEvent("eventstable", pproperties);
 widgetutils.loadNodeNames("nodename");
 widgetutils.getProperty("nodename",pproperties);
 widgetutils.loadElementNames("elementname",document.getElementById('nodename').value);
 widgetutils.getProperty("elementname",pproperties);
};

/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.appzillonelement.saveProperties = function(pproperties) {
 widgetutils.setProperty("id", pproperties);
 widgetutils.setProperty("pid", pproperties);
 widgetutils.setProperty("title", pproperties);
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
 var html = "<div id ='appzillonid'  widgetcategory='element' widgetid='appzillonelement' ></div>";
 return html;
}

/////////// Title Change function called by onBlur Event Handler of title Property.
// Similar functions can be written for all the properties calling from thier respective Event Handlers

function appzillonelement_title(pobject) {
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

/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 For the Saved Properties to be Effective in UI after close and reopen, Functions which manipulate the look and feel in Ui Should be called Here.
 ************************************************************************/
customwidgets.appzillonelement.applyProperties = function(pobject) {
	appzillonelement_title(pobject);
};