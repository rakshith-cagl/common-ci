/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
 var customwidgets = {};
}
customwidgets.ProgressStep = {};
customwidgets.ProgressStep.currobj = null;
stepArray = new Array();
stepArray[1] = "Step";
stepStatusArray = new Array();
stepStatusArray[1] = "INCOMPLETE";
stepPercentArray = new Array();
stepPercentArray[1] = "0";

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
customwidgets.ProgressStep.init = function(pobject) {
    customwidgets.ProgressStep.currobj = pobject;
    var currobjary = designer.selectedobjs;
    var currobjspntr = new Array();
    for (var i = 0; i < currobjary.length; i++) {
        currobjspntr[i] = utils.getDataPointer(currobjary[i]);
    }
    $("#title").blur(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("title", ldatapointer);
                try {
                    ProgressStep_title(ldatapointer);
                } catch (e) {}
            }
        }
        else{
            ProgressStep_title();
        }
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
    $("#stagecollapse").click(function(){
        $("#stageaccordian").toggle();
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
                    ProgressStep_menu(ldatapointer);
                } catch (e) {}
            }
        }
        else{
        ProgressStep_menu();
        }
    });
    $("#headeralignment").change(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("headeralignment", ldatapointer);
                try {
                    ProgressStep_headeralignment(ldatapointer);
                } catch (e) {}
            }
        }
        else{
        ProgressStep_headeralignment();
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
    $("#labelalignment").change(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("labelalignment", ldatapointer);
                try {
                    ProgressStep_labelallign(ldatapointer);
                } catch (e) {}
            }
        } else{
            ProgressStep_labelallign();
        }
    });
    $("#labelwidth").change(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("labelwidth", ldatapointer);
                try {
                    //form_filebrowser_tablecellminwidth(ldatapointer);
                    ProgressStep_labelwidth(ldatapointer);
                } catch (e) {}
            }
        } else{
            ProgressStep_labelwidth();
        }
    });
    $("#defaultvalue").blur(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("defaultvalue", ldatapointer);
                try {
                    ProgressStep_defaultvalue(ldatapointer);
                } catch (e) {}
            }
        }
        else{
        ProgressStep_defaultvalue();
        }
    });
    $("#url").blur(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("url", ldatapointer);
                try {
                    ProgressStep_url(ldatapointer);
                } catch (e) {}
            }
        } else{
            ProgressStep_url();
        }
    });
    $("#translatedefaultvalue").change(function () {
        $("#translatedefaultvalue")[0].value = $("#translatedefaultvalue").prop("checked") ? "Y" : "N";
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("translatedefaultvalue", ldatapointer);
                try {
                    ProgressStep_defaultvalue(ldatapointer);
                } catch (e) {}
            }
        } else{
            ProgressStep_defaultvalue();
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
    $("#appearance").change(function(){
        progressStep_appearance();
    });
    $("#orientation").change(function(){
        progressStep_orientation();
    });
    $("#selectall").click(function() {
        widgetutils.setMultipleCheckboxes(this,"selectevent");
    });
    $("#selectallmenu").click(function () {
        //$('.selectmenu').attr('checked', this.checked);
        widgetutils.setMultipleCheckboxes(this,"selectmenu");
    });

    //Apz312 changes end
};

/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.ProgressStep.getTemplate = function(pobject, pparentobject,receviedForChild,uiParentObj) {
    var widgethtml = "";
    var parentContainer = utils.getContainer(pparentobject).getAttribute("widgettype");
    var parentContId = utils.getContainer(pparentobject).id;
    if(parentContainer == "TABLE") {
        var header = $("#"+parentContId).find("#"+parentContId+"_header");
        var newHeader = "<th onclick='widgetutils.selectTableElement(this, event);'>Sort Code</th>";
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
        var elmContent = '<span class="ett-srtc pri elmWidget"><ul><li><input type="text" class="ett-inpt pri" placeholder=""></li><li> - </li><li><input type="text" class="ett-inpt pri" placeholder=""></li><li> - </li><li><input type="text" class="ett-inpt pri" placeholder=""></li></ul></span></div>';
        widgethtml = widgethtml + elmContent;
        widgethtml = widgethtml + "</td>";
    } else if(parentContainer == "LIST" || parentContainer == "NAVBAR") {
        widgethtml = '<div id ="appzillonid" class="apzlocontainer elpad elmWidget "><ul class="ett-pgst pri hor"><li id="stepli1" class="pgst-step pgs-0"><span></span><span id="step1">Step</span></li></ul></div>';
    } else {
        if(pparentobject.hasAttribute("role")) {
            var receivedTemplate = '<ul class="ett-pgst pri hor"><li class="pgst-step pgs-100"><span></span><span></span></li></ul>';
            widgethtml = '<li id ="appzillonid" class="apzlocontainer elpad">';
            widgethtml = widgethtml + receivedTemplate;
            widgethtml = widgethtml + '</li>';
        } else {
            widgethtml = '<ul id ="appzillonid" class="apzlocontainer srb eoc"><li id="label" class="etw-40">ProgressStep</li><li id="content" class="eio etw-60"><ul class="ett-pgst pri hor"><li id="stepli1" class="pgst-step pgs-0"><span></span><span id="step1">Step</span></li></ul></li></ul>';
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
customwidgets.ProgressStep.populateProperties = function(pproperties) {

    widgetutils.getProperty("id", pproperties);
    widgetutils.getProperty("pid", pproperties);
    widgetutils.getProperty("title", pproperties);
    widgetutils.getProperty("controlid", pproperties);
    ProgressStep_displayElementsProperties(pproperties);
    widgetutils.getProperty("headeralignment", pproperties);
    widgetutils.getProperty("columnalignment", pproperties);
    widgetutils.getProperty("options", pproperties);
    widgetutils.getProperty("labelwidth", pproperties);
    widgetutils.getProperty("labelalignment", pproperties);
    widgetutils.getProperty("hint", pproperties);
    widgetutils.getProperty("cssclasses", pproperties);
    widgetutils.getProperty("tooltip", pproperties);
    widgetutils.getProperty("orientation", pproperties);
    widgetutils.getProperty("appearance", pproperties);
    widgetutils.getEventElements("eventstable", pproperties);
    progressstep_getStepElements("steptable", pproperties);
    ProgressStep_displayContainerRelatedProps();
};

/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.ProgressStep.saveProperties = function(pproperties) {
    
    widgetutils.setProperty("id", pproperties);
    widgetutils.setProperty("pid", pproperties);
    widgetutils.setProperty("title", pproperties);
    widgetutils.setProperty("controlid", pproperties);
    widgetutils.setProperty("headeralignment", pproperties);
    widgetutils.setProperty("columnalignment", pproperties);
    widgetutils.setProperty("options", pproperties);
    widgetutils.setProperty("tooltip", pproperties);
    widgetutils.setProperty("labelwidth", pproperties);
    widgetutils.setProperty("labelalignment", pproperties);
    widgetutils.setProperty("hint", pproperties);
    widgetutils.setProperty("cssclasses", pproperties);
    widgetutils.setProperty("orientation", pproperties);
    widgetutils.setProperty("appearance", pproperties);
    widgetutils.setEvent("eventstable", pproperties);
    progressstep_setStep("steptable", pproperties);
};

customwidgets.ProgressStep.updateDefaultValues = function(pobject, pparentobject) {
    var parentContainer = utils.getContainer(pparentobject).getAttribute("widgettype");
    document.getElementById("title").value ="Progress Step";
}

/************************************************************************
 Below function is called while generating the widget html from IDE
 Provide the html as you want to display the widget in the generated page.
    
 In below function lcontent contains the widget related data. Access them with .propertyId
 Ex : If there is a property called title you can access it as lcontent.title
 ************************************************************************/
function getHTML(pproperties) {
 var lcontent = JSON.parse(pproperties);
 var prgTypeCls = "", toolTip = "", tooltipCls = "", cssCls = "",apprCls=" pri",orientCls = " hor";
 var showClass = "";
 if(lcontent.options == "N") {
     showClass = " sno";
 }
 if (lcontent.cssclasses != undefined) {
     cssCls = " " + lcontent.cssclasses;
 }
 if (lcontent.appearance != undefined) {
     apprCls = " " + lcontent.appearance;
 }
 if (lcontent.orientation != undefined) {
     orientCls = " " + lcontent.orientation;
 }
 if (lcontent.tooltip) {
    toolTip = " original-title="+lcontent.tooltip+"";
    tooltipCls = " tooltipcls";
}
var noOfRec = lcontent.stepitems.length;
 var html = "<ul id ='"+lcontent.name+"_ext' class='ett-pgst"+tooltipCls+cssCls+showClass+apprCls+orientCls+"'>"
for (var i=0; i<noOfRec; i++) {
    var currStep = lcontent.stepitems[i];
    var stepName = currStep.stepname;
    var stepStatus = currStep.stepstatus;
    var stepPercent = currStep.steppercent;
    var actCls = "";
    if (stepStatus=="ACTIVE") {
        actCls = " active";
    }
    html += '<li class="pgst-step pgs-'+stepPercent+actCls+'"><span></span><span>'+stepName+'</span></li>';
}
 html += '</ul>';
 return html;
}

function ProgressStep_displayElementsProperties(pproperties) {
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

function ProgressStep_title(pobject) {
    if(pobject != undefined) {
        var res = pobject.id;
        var se = pobject.title ? pobject.title : "";
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("title").value;
    }
    se = widgetutils.getLITValue(se);
    var parentContainer = utils.getContainer($("#"+res)[0]);
    var parentContainerType = parentContainer.getAttribute("widgettype");
    if(parentContainerType != "TABLE") {
        var parentId = $("#"+res)[0].getAttribute("parentid");
        var parentobject = $("#" + parentId);
        if(parentobject[0].hasAttribute("role")) {
        } else
            $("#" + res).find("#label").html(se);
    } else {
        if(utils.isNull(se))
            $("#" + res+"_heading").html("&nbsp;");
        else
            $("#" + res+"_heading").html(se);
    }
}

function ProgressStep_headeralignment(pobject) {
    if(pobject != undefined) {
        var res = pobject.id;
        var se = pobject.headeralignment;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("headeralignment").value;
    }
    var index = $("#" + res).parent("tr").children().index($("#"+res));
    var temp = $("#"+($("#"+res).closest("tbody").attr("parentid")+"_header")).children()[index];
    if(se == "RIGHT")
        $(temp).addClass('rht');
    else if(se == "CENTER")
        $(temp).addClass('cen');
    else
        $(temp).addClass('lft');
}
function ProgressStep_labelwidth(pobject) {
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

function ProgressStep_displayContainerRelatedProps() {
    $("#datatype").parents("ul.control-group:first").addClass("sno");
    $("#translatedefaultvalue").parents("ul.control-group:first").addClass("sno");
    $("#minvalue").parents("ul.control-group:first").addClass("sno");
    $("#maxvalue").parents("ul.control-group:first").addClass("sno");
    $("#pattern").parents("ul.control-group:first").addClass("sno");
    $("#mandatory").parents("ul.control-group:first").addClass("sno");
    $("#minstringlength").parents("ul.control-group:first").addClass("sno");
    $("#interfacename").parents("ul.control-group:first").addClass("sno");
    $("#nodename").parents("ul.control-group:first").addClass("sno");
    $("#elementname").parents("ul.control-group:first").addClass("sno");
    $("#defaultvalue").parents("ul.control-group:first").addClass("sno");
    $("#datamodeltype").parents("ul.control-group:first").addClass("sno");
    var elmId = document.getElementById("id").value;
    var parentContainerType = utils.getContainer($("#"+elmId)[0]).getAttribute("widgettype");
    if(parentContainerType == "LIST" || parentContainerType == "NAVBAR") {
        $("#title").parents("ul.control-group:first").addClass("sno");
    }
    var parentobject = utils.getParentObject($("#"+elmId)[0]);
    var parentId = parentobject.id;
     if(parentobject.hasAttribute("role")) {
         //var firstChild = $(parentobject).find("#"+parentId+"_receiver").children()[0].id;
        // if(firstChild != elmId) {
            //alert("no error before here"+$("#"+firstChild).parents("#"+parentId+"_receiver").parent().siblings().find('label').html());
             //$(firstChild).parents("label.elabel:first").html(se);
            //alert($("#"+firstChild).parents("#"+parentId+"_receiver:first")[0]);
            //$("#"+firstChild).parents("#"+parentId+"_receiver").parent().siblings().find('label').html(se);
             $("#title").parents("ul.control-group:first").addClass("sno");
        // }
     }
}

function ProgressStep_labelallign(pobject) {
    if(pobject != undefined) {
        var res = pobject.id;
        var se = pobject.labelalignment;
    } else {
        var res = document.getElementById("id").value;
        var se = document.getElementById("labelalignment").value;
    }
    var parentId = $("#"+res)[0].getAttribute("parentid");
    var parentobject = $("#" + parentId);
    var parentObjType = parentobject.attr("widgettype");
    var containerobj = utils.getContainer($("#"+parentId)[0]);
    var containertype = containerobj.getAttribute("widgettype");
    var temp = $("#" + res).find("#label");
    $(temp).removeClass('rht cen lft');
    if(containertype == 'FORM'){
        if(!parentobject[0].hasAttribute("role")) {
            if(se == "RIGHT")
                $(temp).addClass('rht');
            else if(se == "CENTER")
                $(temp).addClass('cen');
            else
                $(temp).addClass('lft');
        }
    }
}

progressstep_addStepElements = function (pproperties) {
    var num = stepArray.length;
    var newRow = '<tr>' + '<td>' + '<input id="selectstep' + num + '" type="checkbox" class="selectstep"/>' + '</td>' + '<td>' + '<INPUT id="stepname' + num + '" onblur="progressstep_steps(this);" class="one" type="text" name="stepname" value="Step" style="width:68px"/><a class="ebtn-mini etxt-info px16 ebtn-icon" onclick="widgetutils.getLITCode(this);"><svg class="icon-edit px18"><use xlink:href="#icon-edit"></use></svg></a>' + '</td>';
    newRow = newRow + '<td><select id="stepstatus' + num + '" class="small m-wrap" onchange="progressstep_stepStatus(this);" tabindex="1"><option value="COMPLETED">Complete</option><option value="ACTIVE">Active</option><option value="INCOMPLETE" selected="selected">Incomplete</option></select></td>';
    newRow = newRow + '<td><select id="steppercent' + num + '" class="small m-wrap" onchange="progressstep_stepPercent(this);" tabindex="1" disabled="disabled"><option value="0">0%</option><option value="25">25%</option><option value="50">50%</option><option value="75">75%</option><option value="100">100%</option></select></td></tr>';
    $("#steptable tbody").append(newRow);
    var res = document.getElementById("id").value;
    var temp = $("#" + res).find('.ett-pgst');
    var newstep = "<li id='stepli" + num + "' class='pgst-step pgs-0'><span></span><span id='step" + num + "'>Step</span></li>";
    $(temp).append(newstep);
    stepArray[num] = "Step";
    stepStatusArray[num] = "INCOMPLETE";
    stepPercentArray[num] = "0";
}
progressstep_deleteStep = function (ptablename) {
    var checked = jQuery('input:checkbox:checked').map(function () {
            return this.id;
        }).get();
    var count = 0;
    var deleteArray = new Array();
    var res = document.getElementById("id").value;
    for (var k = 0; k < checked.length; k++) {
        var stepid = checked[k].match(/(\d+)/g);
        if (stepid != null) {
            var stepnam = $("#stepname" + stepid)[0].value;
            deleteArray.push(stepid);
            $("#" + "steptable").find("#selectstep" + stepid).parents("tr").remove();
            if ($("#" + res).find("#step" + stepid + "")[0] != undefined) {
                var val = $("#" + res).find("#step" + stepid + "")[0].innerHTML;
                var li2del = $("#" + res).find("#stepli" + stepid + "").parent();
                if (li2del.attr("id") != undefined)
                    $("#" + res).find("#stepli" + stepid + "").remove();
                else if (li2del.children().length > 1)
                    $("#" + res).find("#stepli" + stepid + "").remove();
                else
                    $("#" + res).find("#stepli" + stepid + "").parent().remove();
            }
        }
    }
    deleteArray.sort();
    for (var k = deleteArray.length - 1; k >= 0; k--) {
        var stepid = deleteArray[k];
        stepArray.splice(stepid, 1);
        progressstep_decrementPosition(deleteArray[k]);
    }

}
function progressstep_decrementPosition(stepid) {
    var res = document.getElementById("id").value;
    for (var j = stepid; j <= stepArray.length; j++) {
        var stepli = $("#" + res).find("#stepli" + (parseInt(j) + 1));
        var step = $("#" + res).find("#step" + (parseInt(j) + 1));
        var selectstep = $("#" + "steptable").find("#selectstep" + (parseInt(j) + 1));
        var stepname = $("#" + "steptable").find("#stepname" + (parseInt(j) + 1));
        var stepStatus = $("#" + "steptable").find("#stepstatus" + (parseInt(j) + 1));
        var stepPercent = $("#" + "steptable").find("#steppercent" + (parseInt(j) + 1));
        $(stepli).attr('id', ("stepli" + j));
        $(stepStatus).attr('id', ("stepstatus" + j));
        $(step).attr('id', ("step" + j));
        $(selectstep).attr('id', ("selectstep" + j));
        $(stepname).attr('id', ("stepname" + j));
        $(stepPercent).attr('id', ("stepbutton" + j));
    }
}
function progressstep_moveup() {
    var checked = jQuery('input:checkbox:checked').map(function () {
            return this.id;
        }).get();   // Initial selected checkbox sequence
    var target, swap; 
    var j=1;
    var lcheckedlen = checked.length - 1; // selected checkbox count
    while(lcheckedlen>0){
        var checked = jQuery('input:checkbox:checked').map(function () {
            return this.id;
        }).get();   // On multiselect checkbox move up re-arranged sequence
        var stepid = checked[j].replace ( /[^\d.]/g, '' );
     if ((parseInt(stepid) - 1) > 0) {
        target = i - 1;
        if (target != undefined) {
            var rowMoveUp = $("#" + "steptable").find("#selectstep" + stepid).parents("tr").remove();
            $("#" + "steptable").find("#selectstep" + target).parents("tr").before(rowMoveUp);
            swap = i + 1;
            var res = document.getElementById("id").value;
            var stepMoveUp = $("#" + res).find("#stepli" + stepid).remove();
            $("#" + res).find("#stepli" + target).before(stepMoveUp);
            progressstep_exchangePosition(target, stepid);
        }
    }
        lcheckedlen--;
        j++;
        swap = "";
    }
}

function progressstep_movedown() {
    var checked = jQuery('input:checkbox:checked').map(function () {
            return this.id;
        }).get(); // Initial selected checkbox sequence
    var stepid = checked[0].match(/(\d+)/g);
    var target, swap;
    var lcheckedlen = checked.length - 1; // selected checkbox count
    while(lcheckedlen>0){
        var checked = jQuery('input:checkbox:checked').map(function () {
            return this.id;
        }).get(); // On multiselect checkbox move up re-arranged sequence
        var stepid = checked[lcheckedlen].replace ( /[^\d.]/g, '' );
    if ((parseInt(stepid) + 1) < stepArray.length) {
        var i = parseInt(stepid);
        target = i + 1;
        if (target != undefined) {
            var rowMoveDown = $("#" + "steptable").find("#selectstep" + stepid).parents("tr").remove();
            $("#" + "steptable").find("#selectstep" + target).parents("tr").after(rowMoveDown);
            var i = parseInt(stepid);
            swap = i - 1;
            var res = document.getElementById("id").value;
            var stepMoveDown = $("#" + res).find("#stepli" + stepid).remove();
            $("#" + res).find("#stepli" + target).after(stepMoveDown);
            progressstep_exchangePosition(stepid, target);
        }
    }
        lcheckedlen--;
        swap="";
 }
}
function progressstep_exchangePosition(stepid1, stepid2) {
    var res = document.getElementById("id").value;
    var stepli = $("#" + res).find("#stepli" + stepid1);
    var step = $("#" + res).find("#step" + stepid1);
    var selectstep = $("#" + "steptable").find("#selectstep" + stepid1);
    var stepname = $("#" + "steptable").find("#stepname" + stepid1);
    var stepStatus = $("#" + "steptable").find("#stepstatus" + stepid1);
    var stepPercent = $("#" + "steptable").find("#steppercent" + stepid1);
    $(stepli).attr('id', ("stepli" + stepid2));
    $(step).attr('id', ("step" + stepid2));
    $(selectstep).attr('id', ("selectstep" + stepid2));
    $(stepname).attr('id', ("stepname" + stepid2));
    $(stepStatus).attr('id', ("stepstatus" + stepid2));
    $(stepPercent).attr('id', ("steppercent" + stepid2));

    stepli = $("#" + res).find("#stepli" + stepid2);
    step = $("#" + res).find("#step" + stepid2);
    selectstep = $("#" + "steptable").find("#selectstep" + stepid2);
    stepname = $("#" + "steptable").find("#stepname" + stepid2);
    stepStatus = $("#" + "steptable").find("#stepstatus" + stepid2);
    stepPercent = $("#" + "steptable").find("#steppercent" + stepid2);
    $(stepli).attr('id', ("stepli" + stepid1));
    $(step).attr('id', ("step" + stepid1));
    $(selectstep).attr('id', ("selectstep" + stepid1));
    $(stepname).attr('id', ("stepname" + stepid1));
    $(stepStatus).attr('id', ("stepstatus" + stepid1));
    $(stepPercent).attr('id', ("steppercent" + stepid1));
}
progressstep_getStepElements = function (ptable, pproperties) {
    stepArray = new Array();
    stepArray[1] = "Step";
    stepStatusArray = new Array();
    stepStatusArray[1] = "INCOMPLETE";
    stepPercentArray = new Array();
    stepPercentArray[1] = "0";
    if (typeof pproperties.stepitems != 'undefined') {
        $("#" + ptable).find("tbody").empty();
        for (i = 1; i <= pproperties.stepitems.length; i++) {
            progressstep_loadStepElement(pproperties);
            $("#" + ptable).find("tr:eq(" + i + ")").find("#stepname" + i).val(pproperties.stepitems[i - 1].stepname);
            $("#" + ptable).find("tr:eq(" + i + ")").find("#stepstatus" + i).val(pproperties.stepitems[i - 1].stepstatus);
            $("#" + ptable).find("tr:eq(" + i + ")").find("#steppercent" + i).val(pproperties.stepitems[i - 1].steppercent);
            stepArray[i] = pproperties.stepitems[i - 1].stepname;
            stepStatusArray[i] = pproperties.stepitems[i - 1].stepstatus;
            stepPercentArray[i] = pproperties.stepitems[i - 1].steppercent;
        }

    }
}
progressstep_setStep = function (ptable, pproperties) {
    var rows = $("#" + ptable + " tr:gt(0)");
    var i = 1;
    pproperties.stepitems = new Array();
    rows.each(function (index) {
        //if ($("#" + ptable).find("tr:eq(" + i + ")").find("#stepname").val() != 'default') {
            if (!pproperties.stepitems[i])
                pproperties.stepitems[i - 1] = {};
            pproperties.stepitems[i - 1].stepname = $("#" + ptable).find("tr:eq(" + i + ")").find("#stepname" + i).val();
            pproperties.stepitems[i - 1].stepstatus = $("#" + ptable).find("tr:eq(" + i + ")").find("#stepstatus" + i).val();
            pproperties.stepitems[i - 1].steppercent = $("#" + ptable).find("tr:eq(" + i + ")").find("#steppercent" + i).val();
            i += 1;
        //}
    });
}
progressstep_loadStepElement = function (pproperties) {
    if (pproperties != undefined) {
        var sections = pproperties.id;
        var res = pproperties.id;
    } else {
        var sections = document.getElementById("id").value;
        var res = document.getElementById("id").value;
    }
    sections = $("#" + sections).children("div")[0];
    $(sections).css("display", "none");
    var num = pproperties.stepitems.length;
    var newRow = '<tr>' + '<td>' + '<input id="selectstep' + i + '" type="checkbox" class="selectstep"/>' + '</td>' + '<td>' + '<INPUT id="stepname' + i + '" onblur="progressstep_steps(this);" class="one" type="text" name="stepname"  style="width:68px" value="' + pproperties.stepitems[i - 1].stepname + '"/><a class="ebtn-mini etxt-info px16 ebtn-icon" onclick="widgetutils.getLITCode(this);"><svg class="icon-edit px18"><use xlink:href="#icon-edit"></use></svg></a>' + '</td>';
    newRow = newRow + '<td><select id="stepstatus' + i + '" class="small m-wrap" onchange="progressstep_stepStatus(this);" tabindex="1"><option value="COMPLETED">Complete</option><option value="ACTIVE">Active</option><option value="INCOMPLETE" selected="selected">Incomplete</option></select></td>';
    newRow = newRow + '<td><select id="steppercent' + i + '" class="small m-wrap" onchange="progressstep_stepPercent(this);" tabindex="1" disabled="disabled"><option value="0">0%</option><option value="25">25%</option><option value="50">50%</option><option value="75">75%</option><option value="100">100%</option></select></td></tr>';
    $("#steptable tbody").append(newRow);
    var stepStatus = pproperties.stepitems[i - 1].stepstatus;
    var stepPercent = pproperties.stepitems[i - 1].steppercent;
    var stepCls = "";
    $("#steptable tbody").find('#stepstatus'+i).val(stepStatus);
    $("#steptable tbody").find('#steppercent'+i).val(stepPercent);
    if (stepStatus=="ACTIVE") {
        $("#steptable tbody").find('#steppercent'+i).removeAttr('disabled');
        stepCls = "active";
    }
    if (($("#" + res).find("#step" + i))[0] == undefined) {
        var temp = $("#" + res).find('.ett-pgst');
        var lstepname = widgetutils.getLITValue(pproperties.stepitems[i - 1].stepname);
        var newstep = '<li id="stepli'+i+'" class="pgst-step pgs-'+stepPercent+' '+stepCls+'"><span></span><span id="step'+i+'">' + lstepname + '</span></li>';
        $(temp).append(newstep);
    } else {
        var temp = $("#" + res).find('.ett-pgst #stepli'+i+'');
        var lstepname = widgetutils.getLITValue(pproperties.stepitems[i - 1].stepname);
        $($(temp).find('span')[1]).text(lstepname);
        var actCls = "";
        if (pproperties.stepitems[i - 1].stepstatus == "ACTIVE") {
            actCls = " active";
        }
        $(temp).attr('class',"pgst-step pgs-"+pproperties.stepitems[i - 1].steppercent+actCls);
    }
}
function progressstep_steps(caller) {
    var pos = caller.id.match(/(\d+)/g);
    var num = stepArray.length + 1;
    var res = document.getElementById("id").value;
    var se = document.getElementById(caller.id).value;
    var present = $("#"+res).find("#step" + pos + "");
    $(present)[0].innerHTML = widgetutils.getLITValue(se);
    stepArray[pos] = se;
}
function progressstep_stepStatus(caller) {
    var pos = caller.id.match(/(\d+)/g);
    var num = stepArray.length + 1;
    var res = document.getElementById("id").value;
    var se = document.getElementById(caller.id).value;
    var present = $("#"+res).find("#stepli" + pos + "");
    var temp = $("#" + res).find('.ett-pgst');
    var classarray = ['pgs-0', 'pgs-25', 'pgs-50', 'pgs-75', 'pgs-100', 'active'];
    var appender = '<option value="0">0%</option><option value="25">25%</option><option value="50">50%</option><option value="75">75%</option><option value="100">100%</option>';
    $("#steppercent"+pos)[0].innerHTML = appender;
    $(present).removeClass(classarray.join(' '));
    if (se=="COMPLETED") {
        $("#steppercent"+pos).attr('disabled','disabled');
        $("#steppercent"+pos).val("100");
        stepPercentArray[pos] = "100";
    } else if (se=="INCOMPLETE") {
        $("#steppercent"+pos).attr('disabled','disabled');
        $("#steppercent"+pos).val("0");
        stepPercentArray[pos] = "0";
    } else {
        $("#steppercent"+pos).removeAttr('disabled');
        $("#steppercent"+pos).val("25");
        stepPercentArray[pos] = "25";
        $("#steppercent"+pos +" option[value='0']").remove();
        $("#steppercent"+pos +" option[value='100']").remove();
        $(present).addClass('active');
    }
    stepStatusArray[pos] = se;
    $(present).addClass('pgs-'+stepPercentArray[pos]);
}
function progressstep_stepPercent(caller) {
    var pos = caller.id.match(/(\d+)/g);
    var num = stepArray.length + 1;
    var res = document.getElementById("id").value;
    var se = document.getElementById(caller.id).value;
    var present = $("#"+res).find("#stepli" + pos + "");
    var temp = $("#" + res).find('.ett-pgst');
    var classarray = ['pgs-0', 'pgs-25', 'pgs-50', 'pgs-75', 'pgs-100'];
    $(present).removeClass(classarray.join(' '));
    if (!utils.isNull(se)) {
        $(present).addClass('pgs-'+se);
        stepPercentArray[pos] = se;
    }
}
function progressStep_appearance(pobject) {
    var res,se;
    if (pobject!=undefined) {
        res = pobject.id;
        se = pobject.appearance;
    } else {
        res = document.getElementById('id').value;
        se = document.getElementById('appearance').value;
    }
    if(!utils.isNull(se)) {
        var temp = $("#"+res).find('.ett-pgst');
        $(temp).removeClass('pri sec ter');
        $(temp).addClass(se);
    }
}
function progressStep_orientation(pobject) {
    var res,se;
    if (pobject!=undefined) {
        res = pobject.id;
        se = pobject.orientation;
    } else {
        res = document.getElementById('id').value;
        se = document.getElementById('orientation').value;
    }
    var temp = $("#"+res).find('.ett-pgst');
    $(temp).removeClass('hor ver');
    if (!utils.isNull(se)) {
        $(temp).addClass(se);
    }
}
/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 ************************************************************************/
customwidgets.ProgressStep.applyProperties = function(pobject) {
    var parentContainer = utils.getContainer($("#"+pobject.id)[0]).getAttribute("widgettype");
    ProgressStep_title(pobject);
    widgetutils.setWidgetVisibility(pobject);
    progressStep_appearance(pobject);
    progressStep_orientation(pobject);
    progressstep_getStepElements("steptable", pobject);
    if(parentContainer == "TABLE") {
        ProgressStep_headeralignment(pobject);
    }
    if(parentContainer == "FORM") {
        ProgressStep_labelwidth(pobject);
        ProgressStep_labelallign(pobject);
    }
};

