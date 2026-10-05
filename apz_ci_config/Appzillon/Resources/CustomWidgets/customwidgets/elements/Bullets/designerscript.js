/************************************************************************
 Below are the widget related objects initializations needs to be provided at the time of loding the js.
 ************************************************************************/
if (customwidgets == null || customwidgets == "undefined") {
 var customwidgets = {};
}
customwidgets.Bullets = {};
customwidgets.Bullets.currobj = null;
bulletArray = new Array();
bulletArray[1] = "bullet";

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
customwidgets.Bullets.init = function(pobject) {
    customwidgets.Bullets.currobj = pobject;
    var currobjary = designer.selectedobjs;
    var currobjspntr = new Array();
    for (var i = 0; i < currobjary.length; i++) {
        currobjspntr[i] = utils.getDataPointer(currobjary[i]);
    }/*
    $("#title").blur(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("title", ldatapointer);
                try {
                    Bullets_title(ldatapointer);
                } catch (e) {}
            }
        }
        else{
            Bullets_title();
        }
    });*/
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
    /*$("#eventscollapse").click(function(){
        $("#eventsaccordian").toggle();
    });*/
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
                    Bullets_menu(ldatapointer);
                } catch (e) {}
            }
        }
        else{
        Bullets_menu();
        }
    });
    $("#headeralignment").change(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("headeralignment", ldatapointer);
                try {
                    Bullets_headeralignment(ldatapointer);
                } catch (e) {}
            }
        }
        else{
        Bullets_headeralignment();
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
    $("#labelwidth").change(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("labelwidth", ldatapointer);
                try {
                    //form_filebrowser_tablecellminwidth(ldatapointer);
                    Bullets_labelwidth(ldatapointer);
                } catch (e) {}
            }
        } else{
            Bullets_labelwidth();
        }
    });/*
    $("#defaultvalue").blur(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("defaultvalue", ldatapointer);
                try {
                    Bullets_defaultvalue(ldatapointer);
                } catch (e) {}
            }
        }
        else{
        Bullets_defaultvalue();
        }
    });*/
    $("#url").blur(function() {
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                $("#id").val(currobjary[i].id);
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("url", ldatapointer);
                try {
                    Bullets_url(ldatapointer);
                } catch (e) {}
            }
        } else{
            Bullets_url();
        }
    });/*
    $("#translatedefaultvalue").change(function () {
        $("#translatedefaultvalue")[0].value = $("#translatedefaultvalue").prop("checked") ? "Y" : "N";
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("translatedefaultvalue", ldatapointer);
                try {
                    Bullets_defaultvalue(ldatapointer);
                } catch (e) {}
            }
        } else{
            Bullets_defaultvalue();
        }
    });*/
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
        Bullets_appearance();
    });
    $("#selectallmenu").click(function () {
        //$('.selectmenu').attr('checked', this.checked);
        widgetutils.setMultipleCheckboxes(this,"selectmenu");
    });
    $("#orderedlist").change(function () {
        $("#orderedlist")[0].value = $("#orderedlist").prop("checked") ? "Y" : "N";
        if(designer.multipleselect){
            for (var i = 0; i < currobjary.length; i++) {
                var ldatapointer = currobjspntr[i];
                widgetutils.setProperty("orderedlist", ldatapointer);
                try {
                    Bullets_orderedList(ldatapointer);
                } catch (e) {}
            }
        } else{
            Bullets_orderedList();
        }
    });

    //Apz312 changes end
};

/************************************************************************
 Below function is called from layout.addWidget() to get the widget template provided by the developer.
 Provided widgethtml content will be displayed inside the Designer screen.
 Keep the provided outer div (with id appzillonid) as it contains widget related initializations, and keep your content inside that div.
 ************************************************************************/
customwidgets.Bullets.getTemplate = function(pobject, pparentobject,receviedForChild,uiParentObj) {
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
        var elmContent = '<span class="ecn ett-blt pri elmWidget"><ol class="ordl"><li><p>Ordered lists</p></li></ol></span></div>';
        widgethtml = widgethtml + elmContent;
        widgethtml = widgethtml + "</td>";
    } else if(parentContainer == "LIST" || parentContainer == "NAVBAR") {
        widgethtml = '<div id ="appzillonid" class="apzlocontainer elpad elmWidget "><span class="ecn ett-blt pri"><ol class="ordl"><li><p>Ordered lists</p></li></ol></span></div>';
    } else {
        if(pparentobject.hasAttribute("role")) {
            var receivedTemplate = '<span class="ecn ett-blt pri elmWidget"><ol class="ordl"><li><p>Ordered lists</p></li></ol></span>';
            widgethtml = '<li id ="appzillonid" class="apzlocontainer elpad">';
            widgethtml = widgethtml + receivedTemplate;
            widgethtml = widgethtml + '</li>';
        } else {
            widgethtml = '<ul id ="appzillonid" class="apzlocontainer srb eoc"><li id="label" class="etw-40"></li><li id="content" class="eio etw-60"><span class="ecn ett-blt pri elmWidget"><ol class="ordl"><li id="bulletli1"><p id="bullet1">Ordered lists</p></li></ol></span></li></ul>';
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
customwidgets.Bullets.populateProperties = function(pproperties) {

    widgetutils.getProperty("id", pproperties);
    widgetutils.getProperty("pid", pproperties);
    widgetutils.getProperty("title", pproperties);
    widgetutils.getProperty("controlid", pproperties);
    Bullets_displayElementsProperties(pproperties);
    widgetutils.getProperty("headeralignment", pproperties);
    widgetutils.getProperty("columnalignment", pproperties);
    widgetutils.getProperty("options", pproperties);
    widgetutils.getProperty("labelwidth", pproperties);
    widgetutils.getProperty("hint", pproperties);
    widgetutils.getProperty("cssclasses", pproperties);
    widgetutils.getProperty("tooltip", pproperties);
    widgetutils.getProperty("appearance", pproperties);
    //widgetutils.getEventElements("eventstable", pproperties);
    Bullets_getBulletElements("bullettable", pproperties);
    Bullets_displayContainerRelatedProps();
    widgetutils.getProperty("orderedlist", pproperties);
};

/************************************************************************
 Below function is called from layout.saveProperties() to save the widget properties
 layout.populateProperties() will be called when the widget is deselected in the designer.
 Thats when the widget's properties needs to be saved from the properties page.
 
 Send the widget's property id to the widgetutils.setPropety() function with 'pproperties' object
 So that the value will be stored in 'pproperties' object.
 Example: In following lines id and pid are passed to the widgetutils.setProperty() function with 'pproperties'.
 ************************************************************************/
customwidgets.Bullets.saveProperties = function(pproperties) {
    
    widgetutils.setProperty("id", pproperties);
    widgetutils.setProperty("pid", pproperties);
    widgetutils.setProperty("title", pproperties);
    widgetutils.setProperty("controlid", pproperties);
    widgetutils.setProperty("headeralignment", pproperties);
    widgetutils.setProperty("columnalignment", pproperties);
    widgetutils.setProperty("options", pproperties);
    widgetutils.setProperty("tooltip", pproperties);
    widgetutils.setProperty("labelwidth", pproperties);
    widgetutils.setProperty("hint", pproperties);
    widgetutils.setProperty("cssclasses", pproperties);
    widgetutils.setProperty("appearance", pproperties);
    ///widgetutils.setEvent("eventstable", pproperties);
    Bullets_setBullet("bullettable", pproperties);
    widgetutils.setProperty("orderedlist", pproperties);
};

customwidgets.Bullets.updateDefaultValues = function(pobject, pparentobject) {
    //var parentContainer = utils.getContainer(pparentobject).getAttribute("widgettype");
    //document.getElementById("title").value ="Bullet";
}

/************************************************************************
 Below function is called while generating the widget html from IDE
 Provide the html as you want to display the widget in the generated page.
    
 In below function lcontent contains the widget related data. Access them with .propertyId
 Ex : If there is a property called title you can access it as lcontent.title
 ************************************************************************/
function getHTML(pproperties) {
 var lcontent = JSON.parse(pproperties);
 var prgTypeCls = "", toolTip = "", tooltipCls = "", cssCls = "",apprCls=" pri",tagName="ol", ordlCls="ordl ";
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
 if (lcontent.tooltip) {
    toolTip = " original-title="+lcontent.tooltip+"";
    tooltipCls = " tooltipcls";
}
if (lcontent.orderedlist=="N") {
    tagName = 'ul';
    ordlCls = "unol ";
}
var noOfRec = lcontent.bulletitems.length;
 var html = "<span id='"+lcontent.name+"_span_ext' class='ecn ett-blt "+apprCls+" '><"+tagName+" id ='"+lcontent.name+"' class='"+ordlCls+tooltipCls+cssCls+showClass+"'>"
for (var i=0; i<noOfRec; i++) {
    var currBullet = lcontent.bulletitems[i];
    var bulletName = currBullet.bulletname;
    html += '<li id="'+bulletName+'_li"><p>'+bulletName+'</span></li>';
}
 html += '</'+tagName+'></span>';
 return html;
}

function Bullets_displayElementsProperties(pproperties) {
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

function Bullets_title(pobject) {
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

function Bullets_headeralignment(pobject) {
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
function Bullets_labelwidth(pobject) {
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

function Bullets_displayContainerRelatedProps() {
    $("#datatype").parents("ul.control-group:first").addClass("sno");
    $("#translatedefaultvalue").parents("ul.control-group:first").addClass("sno");
    $("#minvalue").parents("ul.control-group:first").addClass("sno");
    $("#maxvalue").parents("ul.control-group:first").addClass("sno");
    $("#pattern").parents("ul.control-group:first").addClass("sno");
    $("#mandatory").parents("ul.control-group:first").addClass("sno");
    $("#minstringlength").parents("ul.control-group:first").addClass("sno");
    $("#defaultvalue").parents("ul.control-group:first").addClass("sno");
}

Bullets_addBulletElements = function (pproperties) {
    var num = bulletArray.length;
    var newRow = '<tr>' + '<td>' + '<input id="selectbullet' + num + '" type="checkbox" class="selectbullet"/>' + '</td>' + '<td>' + '<INPUT id="bulletname' + num + '" onblur="Bullets_bullets(this);" class="one" type="text" style="width:90%" name="bulletname" value="Bullet" /><a class="ebtn-mini etxt-info px16 ebtn-icon" style="width:19px" onclick="widgetutils.getLITCode(this);"><svg class="icon-edit px18"><use xlink:href="#icon-edit"></use></svg></a>' + '</td>';
    newRow = newRow + '</tr>';
    $("#bullettable tbody").append(newRow);
    var res = document.getElementById("id").value;
    var temp = $("#" + res).find('.ecn').children(); // <li><p>Ordered lists</p></li>
    var newbullet = "<li id='bulletli" + num + "'><p id='bullet"+num+"'>Bullet</p></li>";
    $(temp).append(newbullet);
    bulletArray[num] = "Bullet";
}
Bullets_deleteBullet = function (ptablename) {
    var checked = jQuery('input:checkbox:checked').map(function () {
            return this.id;
        }).get();
    var count = 0;
    var deleteArray = new Array();
    var res = document.getElementById("id").value;
    for (var k = 0; k < checked.length; k++) {
        var bulletid = checked[k].match(/(\d+)/g);
        if (bulletid != null) {
            var bulletnam = $("#bulletname" + bulletid)[0].value;
            deleteArray.push(bulletid);
            $("#" + "bullettable").find("#selectbullet" + bulletid).parents("tr").remove();
            if ($("#" + res).find("#bullet" + bulletid + "")[0] != undefined) {
                var val = $("#" + res).find("#bullet" + bulletid + "")[0].innerHTML;
                var li2del = $("#" + res).find("#bulletli" + bulletid + "").parent();
                if (li2del.attr("id") != undefined)
                    $("#" + res).find("#bulletli" + bulletid + "").remove();
                else if (li2del.children().length > 1)
                    $("#" + res).find("#bulletli" + bulletid + "").remove();
                else
                    $("#" + res).find("#bulletli" + bulletid + "").parent().remove();
            }
        }
    }
    deleteArray.sort();
    for (var k = deleteArray.length - 1; k >= 0; k--) {
        var bulletid = deleteArray[k];
        bulletArray.splice(bulletid, 1);
        Bullets_decrementPosition(deleteArray[k]);
    }

}
function Bullets_decrementPosition(bulletid) {
    var res = document.getElementById("id").value;
    for (var j = bulletid; j <= bulletArray.length; j++) {
        var bulletli = $("#" + res).find("#bulletli" + (parseInt(j) + 1));
        var bullet = $("#" + res).find("#bullet" + (parseInt(j) + 1));
        var selectbullet = $("#" + "bullettable").find("#selectbullet" + (parseInt(j) + 1));
        var bulletname = $("#" + "bullettable").find("#bulletname" + (parseInt(j) + 1));
        $(bulletli).attr('id', ("bulletli" + j));
        $(bullet).attr('id', ("bullet" + j));
        $(selectbullet).attr('id', ("selectbullet" + j));
        $(bulletname).attr('id', ("bulletname" + j));
    }
}
Bullets_getBulletElements = function (ptable, pproperties) {
    bulletArray = new Array();
    bulletArray[1] = "Bullet";
    if (typeof pproperties.bulletitems != 'undefined') {
        $("#" + ptable).find("tbody").empty();
        for (i = 1; i <= pproperties.bulletitems.length; i++) {
            Bullets_loadBulletElement(pproperties);
            $("#" + ptable).find("tr:eq(" + i + ")").find("#bulletname" + i).val(pproperties.bulletitems[i - 1].bulletname);
            bulletArray[i] = pproperties.bulletitems[i - 1].bulletname;
        }

    }
}
Bullets_setBullet = function (ptable, pproperties) {
    var rows = $("#" + ptable + " tr:gt(0)");
    var i = 1;
    pproperties.bulletitems = new Array();
    rows.each(function (index) {
        //if ($("#" + ptable).find("tr:eq(" + i + ")").find("#bulletname").val() != 'default') {
            if (!pproperties.bulletitems[i])
                pproperties.bulletitems[i - 1] = {};
            pproperties.bulletitems[i - 1].bulletname = $("#" + ptable).find("tr:eq(" + i + ")").find("#bulletname" + i).val();
            i += 1;
        //}
    });
}
Bullets_loadBulletElement = function (pproperties) {
    if (pproperties != undefined) {
        var sections = pproperties.id;
        var res = pproperties.id;
    } else {
        var sections = document.getElementById("id").value;
        var res = document.getElementById("id").value;
    }
    sections = $("#" + sections).children("div")[0];
    $(sections).css("display", "none");
    var num = pproperties.bulletitems.length;
    var newRow = '<tr>' + '<td>' + '<input id="selectbullet' + i + '" type="checkbox" class="selectbullet"/>' + '</td>' + '<td>' + '<INPUT id="bulletname' + i + '" onblur="Bullets_bullets(this);" class="one" type="text" name="bulletname"  style="width:90%" value="' + pproperties.bulletitems[i - 1].bulletname + '"/><a class="ebtn-mini etxt-info px16 ebtn-icon" style="width:19px" onclick="widgetutils.getLITCode(this);"><svg class="icon-edit px18"><use xlink:href="#icon-edit"></use></svg></a>' + '</td>';
    newRow = newRow + '</tr>';
    $("#bullettable tbody").append(newRow);
    var bulletCls = "";
    if (($("#" + res).find("#bullet" + i))[0] == undefined) {
        var temp = $("#" + res).find('.ecn').children();
        var lbulletname = widgetutils.getLITValue(pproperties.bulletitems[i - 1].bulletname);
        var newbullet = '<li id="bulletli'+i+'"><p id="bullet'+i+'">' + lbulletname + '</p></li>';
        $(temp).append(newbullet);
    } else {
        var temp = $("#" + res).find('.ecn #bulletli'+i+'');
        var lbulletname = widgetutils.getLITValue(pproperties.bulletitems[i - 1].bulletname);
        $(temp).find('p').text(lbulletname);
    }
}
function Bullets_bullets(caller) {
    var pos = caller.id.match(/(\d+)/g);
    var num = bulletArray.length + 1;
    var res = document.getElementById("id").value;
    var se = document.getElementById(caller.id).value;
    var present = $("#"+res).find("#bullet" + pos + "");
    $(present)[0].innerHTML = widgetutils.getLITValue(se);
    bulletArray[pos] = se;
}
function Bullets_appearance(pobject) {
    var res,se;
    if (pobject!=undefined) {
        res = pobject.id;
        se = pobject.appearance;
    } else {
        res = document.getElementById('id').value;
        se = document.getElementById('appearance').value;
    }
    if(!utils.isNull(se)) {
        var temp = $("#"+res).find('.ecn');
        $(temp).removeClass('pri sec ter');
        $(temp).addClass(se);
    }
}
function Bullets_orderedList(pobject) {
    var res,se;
    if (pobject!=undefined) {
        res = pobject.id;
        se = pobject.orderedlist;
    } else {
        res = document.getElementById('id').value;
        se = document.getElementById('orderedlist').value;
    }
    var temp = $("#"+res).find('.ecn');
    var content = temp.children().html();
    if(!utils.isNull(se) && se=="Y") {
        temp.children().replaceWith('<ol class="ordl">' + content +'</ol>');
    } else {
        temp.children().replaceWith('<ul class="unol">' + content +'</ul>');
    }
}
/************************************************************************
 Below function is called from layout.applyProperties() to apply the widget properties after loading the widget in designer.
 Call the functions inside below function to apply the properties (like title) to the widget.
 Pass the 'pobject' as the parameter to the functions as pobject containes the data related to that widget.
 ************************************************************************/
customwidgets.Bullets.applyProperties = function(pobject) {
    var parentContainer = utils.getContainer($("#"+pobject.id)[0]).getAttribute("widgettype");
    //Bullets_title(pobject);
    widgetutils.setWidgetVisibility(pobject);
    Bullets_appearance(pobject);
    Bullets_getBulletElements("bullettable", pobject);
    if(parentContainer == "TABLE") {
        Bullets_headeralignment(pobject);
    }
    if(parentContainer == "FORM") {
        Bullets_labelwidth(pobject);
    }
};

