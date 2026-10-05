/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
 var customwidgets = {};
}
customwidgets.appzilloncontainer = {};
customwidgets.appzilloncontainer.currobj = null;

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
customwidgets.appzilloncontainer.init = function(pobject) {
	 customwidgets.appzilloncontainer.currobj = pobject;	
};

/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.appzilloncontainer.getTemplate = function(pobject, pparentobject) {
 var widgethtml = "<div id ='appzillonid'  widgetcategory='container'></div>";
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
customwidgets.appzilloncontainer.populateProperties = function(pproperties) {
 widgetutils.getProperty("id", pproperties);
 widgetutils.getProperty("pid", pproperties);
 widgetutils.getProperty("attachcustom",pproperties);
};

/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.appzilloncontainer.saveProperties = function(pproperties) {
 widgetutils.setProperty("id", pproperties);
 widgetutils.setProperty("pid", pproperties);
 widgetutils.setProperty("attachcustom",pproperties);
};

/************************************************************************
 Below function is called while generating the widget html from IDE
 Provide the html as you want to display the widget in the generated page.
 Container HTML is devided into 2 parts based on the html id used in below function,
 html id should be the name property provided in the Designer and whenever the property name"_receiver" is encountered,
 from there upto the first ">" character will be considered as first part of the container html.
 Then childs of the container will be generated after which rest of the container html provided in below function will be appended.
 Ex : "<div id='"+lcontent.name+"'><ul id='"+lcontent.name+"_receiver' > ~~~~~Container Childs comes here~~~~~ </ul></div>"
	
 In below function lcontent contains the widget related data. Access them with .propertyId
 Ex : If there is a property called title you can access it as lcontent.title
 ************************************************************************/
function getHTML(pproperties) {
 var lcontent = JSON.parse(pproperties);
 var html = "<div id ='appzillonid'  widgetcategory='container'></div>";
 return html;
}

/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 ************************************************************************/
customwidgets.appzilloncontainer.applyProperties = function(pobject) {
	
};