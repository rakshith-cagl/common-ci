
//import {Renderer2} from '@angular/core'; 
//import {OnInit,ViewChild,ViewContainerRef} from '@angular/core';
/*import {Support} from './support.ts';*/

/*import {dummycomponent} from './appzillon/scripts/angular/dummycomponent/dummycomponent.ts'*/

//import {AdServiceWrapper} from './ad.service.wrapper';
//debugger;
//var serviceComp = new AdServiceWrapper().adService;
/*console.log(serviceComp);
window.serviceComponent = serviceComp.getAds();*/

/*var support = new Support();
console.log("support:",support);*/
/*
console.log("dummycomponent:",dummycomponent);
window.dummy = dummycomponent;*/

/*var dc = new dummycomponent();
console.log("dc--",dc);*/

Apz.prototype.statesMap = {};
Apz.prototype.cntrStatesMap = {};
Apz.react = true;
//Apz.compFactory = {};

/*if(window.apzJquery == undefined){
  window.apzJquery = $; 
}*/

(function () {
  if ( typeof window.CustomEvent === "function" ) return false; //If not IE

  function CustomEvent ( event, params ) {
    params = params || { bubbles: false, cancelable: false, detail: undefined };
    var evt = document.createEvent( 'CustomEvent' );
    evt.initCustomEvent( event, params.bubbles, params.cancelable, params.detail );
    return evt;
   }

  CustomEvent.prototype = window.Event.prototype;

  window.Event = CustomEvent;
})();

/*
apz.app.launchScreen = function(screenName){
    var param = {type:"PG", div:"page_1"};
    param.callBack = apz.app.loadScreen;
    param.paths = ["apps/Appzil/scripts/"+screenName+".js"]
    apz.importModules(param);
}
apz.app.launchSubScreen = function(screenName, parentId){
    if(!document.getElementById(parentId)){
        let parentDiv = document.createElement("div");
        parentDiv.setAttribute("id",parentId)
        document.getElementById("page-body").appendChild(parentDiv);
    }
    var param = {type:"SS", div:parentId};
    param.callBack = apz.app.loadScreen;
    param.paths = ["apps/Appzil/scripts/"+screenName+".js"]
    apz.importModules(param);
}
apz.app.loadScreen = function(param){
    if(param.content){
        apz.setContent(param.content[0].default, param.div);
    } else {
        console.error("Failed in getting module content ");
    }
}*/

Apz.prototype.setContent = function(params){
    var proc = params.proc;
    var renderTo = document.getElementById(proc.target);
    debugger;
  /*  this.unmountComponentAtNode(proc)
    let inst = render(
        proc.content,
        renderTo);
    apz.statesMap[proc.scr] = inst;*/

    //apz.app.instances.push(inst);
   // Renderer2.setValue(renderTo,proc.content);
  // render.prototype.appendChild(renderTo,proc.content)
 // $("#page_1").append('<div><ng-container #container></ng-container></div>');
  //dummy.prototype.displayScreen(proc.content);
  //window.serviceComponent[0].component.prototype.displayScreen(window.serviceComponent[1].component);
 // ɵrenderComponent(proc.content,renderTo);
  window.service[0].component.prototype.displayScreen(window,proc.content);
  window.service[3].component.prototype.renderComponent(window,proc.content);
//    console.log(apz.statesMap);
};

Apz.prototype.unmountComponentAtNode = function(proc){
    var obj = document.getElementById(proc.oldDiv);
    if(!apz.isNull(obj)){
      unmountComponentAtNode(obj);
      if(proc.type == "CF"){
        //// TBD - Find the previous subscreen launched and remove it from statesmap
      }
    }
}
/*
apz.app.saveData = function(props, event){
    props.data[props.name] = event.target.value;
    apz.app.updateStateContext();
}

apz.app.updateStateContext = function(){
    if(apz.app.instances.length > 0){
        for (var i = 0; i < apz.app.instances.length; i++) {
            apz.app.instances[i].setState({});
        }
        //apz.app.inst.setState({data:apz.data.scrdata});
    }
}*/

Apz.prototype.importModules = function(params){
  let modules = [];
  let reqJsList = [];
  let reqList = [];
    let postCallBack = function(receivedModules){
      modules = modules.concat(receivedModules);
        if(params.callBack && params.paths.length == modules.length){
            params.content = modules;
            apz.procThreadCompleted(params);
        }
    }
    if(params.paths){
    params.paths.map(modulePath => 
      {
        if(modulePath.endsWith(".js")){
          reqJsList.push(modulePath);
        } else {
          if(modulePath.lastIndexOf(".jsx") > -1){
            modulePath = modulePath.substr(0,modulePath.lastIndexOf(".ts"))
          }
          reqList.push(modulePath);
        }
      })
    if(reqList.length > 0){
      if(window.apz.deviceOs != "SIMULATOR"){
    	__webpack_public_path__=window.apz.mca.appVersionNo;
      }
      Promise.all(reqList.map(modulePath => 
        {
      	  //// dynamic path correction to match compile time path
          if(window.apz.deviceOs != "SIMULATOR"){
        		let versionIndex = modulePath.indexOf("/container/");
        		modulePath = modulePath.substr(versionIndex+11);
          }
         // return import(/* webpackMode: "lazy-once" */`./${modulePath}.ts`)
        }))
      .then(modules => {
          params.status = true;
          postCallBack(modules);
      }).catch(err => {
          params.status = false;
          console.error("Failed importing modules : "+err);
          postCallBack(null);
      })
    }
    if(reqJsList.length > 0){
      requirejs(reqJsList, function(){
        postCallBack(Array.prototype.slice.call(arguments));
      })
    }
    } else {
    apz.procThreadCompleted(params);
    }
}

window.reRenderReq = function(props){
    if(apz.scrMetaData.containersMap[props.id].modified == false){
        return false;
    }
    return true;
}


///////////////////////////////////////////////// Overridden Methods of Appzillon

Apz.prototype.getHTMLFileName = function(scr, lo,tmpl) {
      return scr + "_" + lo + "_" + tmpl + "_" + this.language;
   }

Apz.prototype.scrHtmlLoaded = function(params) {
      ///Store
      var module = null;
      if(params.content){
        //Pradeep Changes
        module = params.content[0].AppComponent;
         // module = params.content[0].default;
        //module = React.cloneElement(params.content[0].default, {proc:params.proc});
      }
      this.storeScrHtml(params.proc.appId, params.proc.scr, params.proc.lo, params.proc.template,module);
      params.proc.scrHtml = module;
   }

Apz.prototype.layoutDefLoaded = function(params){
       var proc = {};//params.proc;
      proc.scr = params.scr;
      proc.lo = params.lo;
      proc.appId = params.appId;
      proc.type = params.type;
      proc.animation = params.animation;
      proc.newDiv = params.newDiv;
      proc.oldDiv = params.oldDiv;
      proc.userObj = params.userObj;
      proc.scroll = params.scroll;
      ////Create Divs
      proc.origDiv = proc.newDiv;
      var key = proc.scr + this.idSep + proc.lo;
      if(this.isNull(params.template)){
         proc.template = this.loDefsMap[proc.appId][key].defaultTemplate;
      } else {
         proc.template = params.template;
      }
      proc.callBack = this.scrFilesLoaded;
      proc.callBackObj = this;
      proc.threads = [];
      ////Screen Definition
      key = proc.scr + this.idSep + proc.lo + this.idSep + proc.template;
      if(!this.containsKey(this.scrDefsMap[proc.appId], key)){
         params = {};
         params.id = "SCRDEF";
         params.runnerObj = this;
         params.runner = this.getFile;
         params.callBack = this.scrDefLoaded;
         params.callBackObj = this;
         params.path = this.getScrDefPath(proc.appId) + "/" + proc.scr + "_" + proc.lo + "_" + proc.template + ".json";
         params.async = true;
         proc.threads[proc.threads.length] = params;
      }
      ////Screen HTML
      key = this.getHTMLFileName(proc.scr, proc.lo, proc.template);
      /* if (!this.containsKey(this.scrHtmls[proc.appId], key)) {
         var scriptsPath = this.getScriptsPath(proc.appId) + key + ".js";
         params = {};
         params.id = "SCRHTML";
         params.runnerObj = this;
        // params.runner = this.importModules;
        params.runner = this.importAngularModules;
         params.callBack = this.scrHtmlLoaded;
         params.callBackObj = this;
         params.paths = [this.getScrPath(proc.appId) + key];
         params.async = true;
         proc.threads[proc.threads.length] = params;
      } */
      this.startProc(proc);
   }

Apz.prototype.setHtml = function(proc) {
}

Apz.prototype.importAngularModules = function(proc){
  //apz.proc = proc.proc;
  var appId = proc.proc.appId;
  if(apz.appFactory[appId]!=true){
    window.componentLoader.loadProjectModule(appId);
  }
  
  if(proc.paths){
    if(proc.paths.length>0){
      var reqJsList=[];
      proc.paths.map(modulePath => 
        {
          if(modulePath.endsWith(".js")){
            reqJsList.push(modulePath);
          }
        })
        if(reqJsList.length > 0){
          requirejs(reqJsList, function(){
            //postCallBack(Array.prototype.slice.call(arguments));
          })
        }
    }
  }
  window.componentLoader.load(proc.proc.scr+'_'+appId,proc.proc.newDiv,proc.proc);
  apz.procThreadCompleted(proc);
}

Apz.prototype.loadScrContent = function(proc) {
  var key = proc.scr + this.idSep + proc.lo + this.idSep + proc.template;
  var scrMeta = apz.copyObjByVal({}, this.scrDefsMap[proc.appId][key]);      
  key = this.getHTMLFileName(proc.scr, proc.lo,proc.template);
  //proc.content = this.scrHtmls[proc.appId][key];
   //proc.content  = React.cloneElement(this.scrHtmls[proc.appId][key], {proc:proc}); 
   //pradeep changes
   proc.content  = this.scrHtmls[proc.appId][key]; 
  this.childScr = proc.scr;
  proc.uiInits = scrMeta.uiInits;
  ////Update Current Screen
  if(proc.type != "CF"){
     this.currScr = proc.scr;
     this.scrMetaData = this.copyJSONObjectWithFilter(scrMeta, ["childScreens"]);
     this.scrMetaData.childScreens = this.copyJSONObject(scrMeta.childScreens);
     this.statesMap = {};
     //this.scrMetaData = this.copyJSONObject(scrMeta);
     ////Inject HTML
     //this.setHtml(proc.newDiv, proc.scrHtml);
  } else {
     this.appendScrDef(this.scrMetaData, scrMeta);
     ////Inject HTML
     /*var newHtml = apzJquery('<div id="scr__'+proc.appId+'__'+proc.scr+'__main"></div>');
     var links = "", fileHtml = apzJquery(proc.scrHtml);
     if(fileHtml.length > 0){
        links = fileHtml.filter("link");
     }
     newHtml = newHtml.append(fileHtml.find('.pagebody').children()).append(links);
     newHtml = newHtml[0].outerHTML;
     this.setHtml(proc.newDiv, newHtml);*/
  }
  
  /////Update Ids
  /*var ids = $('#' + proc.oldDiv + ' [id]').map(function() {
     if (this.id != proc.oldDiv) {
        this.id = this.id + "_Dummy";
        if (this.id == "sidebar_Dummy") {
           apzJquery(this).css("display", "none");
        }
     }
  });
  ////Update Classes for subscreen
  if (proc.type != "PG") {
     apzJquery("div#scr__"+ proc.appId + '__' + proc.scr + "__main .pagebody").each(function(elm, val) {//RADS32ANIM
        val.id = "page-body_" + proc.scr;
        apzJquery("div#scr__" + proc.appId + '__' + proc.scr + "__main .pagebody").removeClass("pagebody");//RADS32ANIM
        apzJquery("div#scr__" + proc.appId + '__' + proc.scr + "__main .header").removeClass("header");
        apzJquery("div#scr__" + proc.appId + '__' + proc.scr + "__main .footer").removeClass("footer");
        apzJquery("div#scr__" + proc.appId + '__' + proc.scr + "__main").removeClass("rolepage");
     });
  }
  ////Update Image paths
  if(this.defaultTheme !== this.theme){
      var imgArr = apzJquery('img');
      for(var j=0;j<imgArr.length;j++){
          var src = apzJquery(imgArr[j]).attr("src");
          src = src.split("/");
          src[3] = this.theme;
          src = src.join("/");
          apzJquery(imgArr[j]).attr("src",src);
      }
      var portions = apzJquery('nav');
      for(var k=0;k<portions.length;k++){
          var bgimg = apzJquery(portions[k]).css("background-image");
          bgimg = bgimg.split("/");
          bgimg.splice(-3,1,this.theme);
          bgimg = bgimg.join("/");
          apzJquery(portions[k]).css("background-image",bgimg);
      }
  }*/
  ////Load Scripts
  var myObj = this;
  var scripts = [];
  scripts = this.scrMetaData.scripts;
  this.undefScripts(scripts);
  proc.target = proc.newDiv;
  //apz.setContent(proc.scrHtml, proc.newDiv, proc)
  var params = {};
         params.id = "SCRHTML";
       //  params.callBack = this.setContent;
         params.callBackObj = this;
         params.paths = scripts;
         params.async = true;
         params.proc = proc;
         proc.threads = [];
         delete proc.noOfThreads;
        // this.importModules(params);
        this.importAngularModules(params);
  /*var params = {};
  params.proc = proc;
         params.id = "SCRHTML";
         params.runnerObj = this;
         params.runner = this.importModules;
         params.callBack = this.scrHtmlLoaded;
         params.callBackObj = this;
         params.paths = [this.getScriptsPath(proc.appId) + "/" + key + ".js"];
  this.importModules(["apps/Appzil/scripts/FirstPage.js"], myObj.scrScriptsLoaded, myObj, proc);*/
  //requirejs(scripts, function() {
     //myObj.scrScriptsLoaded(proc);
  //});
}
Apz.prototype.scrShowScreen = function(proc) {
  //RADS32ANIM
  /*
  var onLoad = "onLoad_" + proc.scr;
  if (this.app[onLoad]) {
     this.app[onLoad]();
  }
  window.scrollTo(0, 0);
  */
  if (proc.type == "PG") {
     this.clearHtml(proc.oldDiv)
     this.flipPages();
  } else {
     $("#"+proc.oldDiv).remove();
    // $("#scr__"+proc.appId+'__'+proc.scr+"__main").unwrap();
    $("#"+proc.newDiv).find(proc.scr).unwrap();
  }
  /// Scroll to view port
  if(proc.scroll){
     var offsetInf = $("#"+proc.origDiv).offset();
     window.scrollTo(offsetInf.left,offsetInf.top);
  }
  ///Init Screen
  this.scrInit(proc);
  ////Execute PostShow
  var onShown = "onShown_" + proc.scr;
  if (this.app[onShown]) {
      this.app[onShown](proc.userObj);
   }
}

/* Apz.prototype.setObjValue = function(obj, value) {
  if (obj && obj.props) {
    let elmMetaData = apz.scrMetaData.elmsMap[obj.props.id]
    if(elmMetaData && elmMetaData.custom=="Y"){
      let custObj = apz[elmMetaData.type];
       if(custObj && apz.isFunction(custObj.setObjValue)){
          custObj.setObjValue(obj, value);
       }
    } else {
      this.data.setPropsData(obj.props, value);
    }
  } else {
      console.log("Error in setting value for " + (obj ? obj.id : obj));
  }
} */

/* Apz.prototype.getObjValue = function(obj) {
  var value = "";
  if (obj && obj.props) {
    let elmMetaData = apz.scrMetaData.elmsMap[obj.props.id]
    if(elmMetaData && elmMetaData.custom=="Y"){
      var custObj = apz[elmMetaData.type];
      if(custObj && apz.isFunction(custObj.getObjValue)){
        value = custObj.getObjValue(obj);
      }
    } else {
      value = obj.props.data[obj.props.name];
    }
    if(apz.isNull(value)){
        value = "";
    }
    return value;
  } else {
      console.log("Error in getting value for " + (obj ? obj.id : obj));
  }
} */

Apz.prototype.updatePaginationRecords = function(containerId) {
    var proceed = true;
    if(this.isFunction(this.app.preUpdatePageSize)){
       proceed = this.app.preUpdatePageSize(containerId);
       if (this.isNull(proceed)) {
          proceed = true;
       }
    }
    if (proceed) {
       var selectedObj = document.getElementById(containerId+"_dps");
       //if(!this.isNull(selectedObj)){
          var containerData = this.scrMetaData.containersMap[containerId];
          var selectedPageSize = selectedObj.value;
          var oldPagSize = containerData.pageSize;
          /// update container pagesize
          if(selectedPageSize == "ALL"){
              containerData.pageSize = containerData.totalRecs;
          } else {
              containerData.pageSize = parseInt(selectedPageSize);
          }
          var newPageSize = containerData.pageSize;
          var currPage = containerData.currPage;
          var firstRec = (currPage - 1) * oldPagSize;
          var newPage = Math.round((firstRec+1) / newPageSize);
          firstRec = (newPage - 1) * newPageSize;
          if (newPage==0) {
             newPage = 1;
             firstRec = 0;
          }
          containerData.currRec = firstRec;
          ///containerData.currPage = newPage;
          if (newPage>0) {
             var dataRecNo = firstRec;
             var params = {};
             params.container = containerId;
             params.recNo = dataRecNo;
             this.data.goToRecord(params);
             this.data.reRenderComponent(containerId);
             //this.data.updateStateContext();
          }
       //}
    }
    if(this.isFunction(this.app.postUpdatePageSize)){
       this.app.postUpdatePageSize(containerId);
    }
 }

Apz.prototype.initDataTable = function(obj, destroy) {
  var jqueryObj = $(obj);
  var tableDiv = jqueryObj.parents(".crb-tabl:first");
  if(tableDiv.find('object').length > 0) {
      var backupObj = tableDiv.parent().find('object');
      tableDiv.find('object').remove();
    }
    var apzObj = this;
    if (jqueryObj.length > 0) {
          if(jqueryObj.is(":visible") && jqueryObj.hasClass('responsive')) {
            var proceed = true;
              if (this.isNull(destroy)) {
                  destroy = false;
              }
              if ($.fn.DataTable.isDataTable(jqueryObj[0])) {
                  if (destroy) {
                      jqueryObj.DataTable().destroy();
                  } else {
                      proceed = false;
                  }
              } else if(jqueryObj.find('tbody tr').length == 0) {
                proceed = false;
              }
              if (proceed) {
                  var table = jqueryObj.DataTable({
                      paging: false,
                      ordering: false,
                      info: false,
                      searching: false,
                      searchable: false
                  });
                  setTimeout(function(){
                    tableDiv.attr("oldWidth",tableDiv.find(".responsive").width());
                  },100);
                  jqueryObj.find('tbody').off("click.apzevent").on('click.apzevent', 'td.dticon', function() {
                      var tablDiv = $(this).closest('.crb-tabl');
                      var tr = $(this).closest('tr');
                      var table = $(this).closest('table').DataTable();
                      var row = table.row(tr);
                      var iconObj = $(this).children('svg');
                      if (iconObj.length > 0) {
                          iconObj.removeClass("px24");
                          var newTargetCls = iconObj.attr('class').replace("icon", "").trim();
                          var oldTargetClass = $(this).attr("targetclass");
                          if (row.child.isShown() && oldTargetClass !== "") {
                              var previousElement = $(this).parent().siblings().find("[targetclass]");
                              previousElement.find("use").attr("xlink:href", "#" + newTargetCls);
                              previousElement.find("svg").removeClass(oldTargetClass).addClass(newTargetCls);
                              previousElement.attr('targetclass', oldTargetClass);
                          }
                          $(this).attr("targetclass", newTargetCls); 
                          $(this).html('').append('<svg class="icon ' + oldTargetClass + ' px24" aria-hidden="true"><use xlink:href="#' + oldTargetClass + '"></use></svg>');
                      }
                      if (apzObj.isFunction(apzObj.app.postExpandAction)) {
                          apzObj.app.postExpandAction(this)
                      }
                  });
              } else if($.fn.DataTable.isDataTable(jqueryObj[0])) {
                jqueryObj.DataTable().columns.adjust().draw();
              }
          }
    }
    tableDiv.append(backupObj);
}

Apz.prototype.searchRecords = function (pcontainer, psearchcontent) {
  var lsearchcontent = psearchcontent.toUpperCase();
  if (this.isObjectEmpty(this.data.scrDataBackup)) {
   this.data.scrDataBackup = {};
   var ifacesArr = this.scrMetaData.containersMap[pcontainer].ifaces;
   for(i=0;i < ifacesArr.length; i++) {
   if(!apz.isNull(this.data.scrdata[ifacesArr[i]])) {
     this.data.scrDataBackup[ifacesArr[i]] = this.copyJSONObject(this.data.scrdata[ifacesArr[i]]);
    }
    if(!apz.isNull(this.data.scrdata[ifacesArr[i]+"_Req"])) {
     this.data.scrDataBackup[ifacesArr[i]+"_Req"] = this.copyJSONObject(this.data.scrdata[ifacesArr[i]+"_Req"]);
    }
    if(!apz.isNull(this.data.scrdata[ifacesArr[i]+"_Res"])) {
     this.data.scrDataBackup[ifacesArr[i]+"_Res"] = this.copyJSONObject(this.data.scrdata[ifacesArr[i]+"_Res"]);
    }
   }
  } else {
   this.data.appendData(this.copyJSONObject(this.data.scrDataBackup));
  }
   var lcontainerobj = this.scrMetaData.containersMap[pcontainer];
 var lcontainertype = lcontainerobj.type; 
 var lnodes = lcontainerobj.nodes;
  if (!this.isNull(psearchcontent)) {
    var lnode;
    var lnoofnodes = lnodes.length;
    var actNodes=new Array();
    for(var m = 0 ; m < lnoofnodes ; m ++){
         var allParents = this.scrMetaData.nodesMap[lnodes[m]].parents;
         if (allParents.length>0) {
            for (var x = 0; x < allParents.length; x++) {
               if ($.inArray(apz.getNodeName(allParents[x]),actNodes) == -1) {
                  actNodes.push(apz.getNodeName(allParents[x]));
               }
            }
         }
     actNodes.push(apz.getNodeName(lnodes[m]));
    }
   var lsearchednodes = new Array();
      var mrParentNodes = new Array();
      var indexArr = new Array();
   if (lcontainertype == "TABLE" || lcontainertype == "LIST" && !this.isNull(psearchcontent)) {
     var lfinaloutput = this.getAllRecords(lcontainerobj);
     for (var x = 0 ; x < lnoofnodes ; x ++) {
       lnode = lnodes[x];
            if ($.inArray(this.scrMetaData.nodesMap[lnode].mrParent,mrParentNodes) == -1) {
               mrParentNodes.push(this.scrMetaData.nodesMap[lnode].mrParent);
            }
         }
         for (var r = 0; r<mrParentNodes.length; r++) {
            indexArr = [];
            var mrParNode = mrParentNodes[r];
            if(!this.containsKey(lsearchednodes,mrParNode) && lfinaloutput[mrParNode]){
               lfinaloutput[mrParNode] = $.grep(lfinaloutput[mrParNode], function(n, i) {
                        lsearchednodes.push(mrParNode);
                        var params = {};
                        params.parentObj = n;
                        params.obj = n;
                        params.searchContent = lsearchcontent;
                        params.actNodes = actNodes;
                        params.searchedNodes = lsearchednodes;
                        params.indexArr = indexArr;
                        params.index = i;
                        params.containerId = pcontainer;
                        params.mrParentNode = mrParNode;
                        params.currentNode = mrParNode;
                        return apz.searchNodeRecords(params);
                     }, false);
               if (!this.scrMetaData.containersMap[pcontainer].searchIndex) {
                  this.scrMetaData.containersMap[pcontainer].searchIndex = {};
               }
               this.scrMetaData.containersMap[pcontainer].searchIndex[mrParNode] = indexArr;
               //var mrParent = this.scrMetaData.nodesMap[lnode].mrParent;
               var parPointer = this.getParentPointer(mrParNode);
               if(!this.isNull(parPointer)) {
                  parPointer[apz.getNodeName(mrParNode)] = lfinaloutput[mrParNode];
               }  
            }
         }
   }
  } else {
      this.scrMetaData.containersMap[pcontainer].searchIndex = null;
   }
  var params = {};
   params.tracker = null;
   params.recNo = 0;
   params.container = pcontainer;
   this.data.goToRecord(params);
   //this.data.updateStateContext();
   apz.data.reRenderComponent(pcontainer);
   var ifacesArr = this.scrMetaData.containersMap[pcontainer].ifaces;
   
}

Apz.prototype.initControls = function(proc) {
  var noOfElms, elms;
  if (this.isNull(this.scrMetaData.uiInitsMap)) {
    this.scrMetaData.uiInitsMap = [];
  }
  var inits = proc.uiInits;
  var scrName = this.childScr;
  //var allElms = this.scrMetaData.elms;

  if (inits != undefined) {
    // // Tags init
    elms = inits.tag;
    if (elms != undefined) {
      noOfElms = elms.length;
      for (var e = 0; e < noOfElms; e++) {
        var tagObj = elms[e];
        this.scrMetaData.uiInitsMap[tagObj[0]] = tagObj;
        var uiObj = document.getElementById(tagObj[0]);
        if (uiObj) {
          var id = this.getObjIdWORowNumber(uiObj);
          this.initTags(tagObj[0], tagObj[1]);
        }
      }
    }
    // // Dates init
    elms = inits.date;
    if (elms != undefined) {
      noOfElms = elms.length;
      for (var e = 0; e < noOfElms; e++) {
        var dateObj = elms[e];
        this.scrMetaData.uiInitsMap[dateObj[0]] = dateObj;
        var uiObj = document.getElementById(dateObj[0]);
        if (uiObj) {
          var params = {};
          params.id = dateObj[0];
          params.dataType = dateObj[1];
          params.lookAndFeel = dateObj[2];
          params.parentDisplay = dateObj[3];
          params.style = dateObj[4];
          params.parentPreset = dateObj[5];
          params.parentMinDate = dateObj[6];
          params.parentMaxDate = dateObj[7];
          params.closeOnSel = dateObj[8];
          params.multiSel = dateObj[9];
          params.parentStartYear = dateObj[10];
          params.parentEndYear = dateObj[11];
          params.parentRangePick = dateObj[12];
          params.secInputId = dateObj[13];
          params.parentMultiInput = dateObj[14];
          params.dateType = dateObj[15];
          this.initDates(params);
          var id = this.getObjIdWORowNumber(uiObj);
        }
      }
    }
    // // Dropdowns init
    elms = inits.dropDown;
    if (elms != undefined) {
      noOfElms = elms.length;
      for (var e = 0; e < noOfElms; e++) {
        var dropdownObj = elms[e];
        this.scrMetaData.uiInitsMap[dropdownObj[0]] = dropdownObj;
        var uiObj = document.getElementById(dropdownObj[0]);
        if (uiObj) {
          this.initDropdowns(dropdownObj);
          var id = this.getObjIdWORowNumber(uiObj);
        }
      }
    }
    // // Checkbox init
    elms = inits.checkBox;
    if (elms != undefined) {
      noOfElms = elms.length;
      for (var e = 0; e < noOfElms; e++) {
        var checkboxObj = elms[e];
        var uiObj = document.getElementById(checkboxObj[0]);
        if (uiObj) {
          this.initCheckboxs(checkboxObj[0], checkboxObj[1]);
          var id = this.getObjIdWORowNumber(uiObj);
          this.scrMetaData.uiInitsMap[id] = checkboxObj;
        }
      }
    }

    // context menu init
    elms = inits.contextMenu;
    if (elms != undefined) {
      noOfElms = elms.length;
      for (var e = 0; e < noOfElms; e++) {
        var contextmenuObj = elms[e];
        this.scrMetaData.uiInitsMap[contextmenuObj[0]] = contextmenuObj;
        var uiObj = document.getElementById(contextmenuObj[0]);
        if (uiObj) {
          this.initContextMenu(contextmenuObj[0], contextmenuObj[1]);
          var id = this.getObjIdWORowNumber(uiObj);
        }
      }
    }

    /// dropdown with input init
    elms = inits.dropdownWithInput;
    if (elms != undefined) {
      noOfElms = elms.length;
      for (var e = 0; e < noOfElms; e++) {
        var dropdownObj = elms[e];
        this.scrMetaData.uiInitsMap[dropdownObj[0]] = dropdownObj;
        var uiObj = document.getElementById(dropdownObj[0]);
        if (uiObj && $("#" + dropdownObj[0] + "_ext").find('.dropdown-list').length != 0) {
          var dropdownListId = $("#" + dropdownObj[0] + "_ext").find('.dropdown-list')[0].id;
          this.initDropdownWithInput(dropdownObj[0], dropdownListId);
          var id = this.getObjIdWORowNumber($("#" + dropdownObj[0])[0]);
        } else {
          this.initNativeDropdownWithInput(dropdownObj[0]);
          var id = this.getObjIdWORowNumber($("#" + dropdownObj[0])[0]);
        }
      }
    }

    ////popover init
    elms = inits.popover;
    if (elms != undefined) {
      noOfElms = elms.length;
      for (var e = 0; e < noOfElms; e++) {
        var popoverObj = elms[e];
        if (this.isNull(this.scrMetaData.uiInitsMap["popover"])) {
          this.scrMetaData.uiInitsMap["popover"] = [];
        }
        this.scrMetaData.uiInitsMap["popover"][popoverObj[0]] = popoverObj;
        if (this.isNull(this.scrMetaData.elmsMap[popoverObj[0]].popoverid)) {
          this.scrMetaData.elmsMap[popoverObj[0]].popoverid = popoverObj[1];
        }
        var uiObj = document.getElementById(popoverObj[0]);
        if (uiObj) {
          var params = {};
          params.elmId = popoverObj[0];
          params.targetId = popoverObj[1];
          params.position = popoverObj[3];
          this.initPopover(params);
          var id = this.getObjIdWORowNumber(uiObj);
        }
      }
    }
  }
}
Apz.prototype.initRow = function(tableId, pobj, rowNo) {    // // On Row Click initilize Mobiscroll Controls		
  var containerData = this.scrMetaData.containersMap[tableId];
  var noOfElms = containerData.elms.length;
  var noOfRows = rowNo ? rowNo : containerData.pageRows;
  for (var c = 0; c < noOfElms; c++) {
    var currElm = containerData.elms[c];
    var currRecHtmlId = currElm.id + "_" + (noOfRows - 1);
   // var mainRowObj = this.scrMetaData.uiInitsMap[currElm.id];
   //Angular related changes
    var initsMapId = currElm.id + "_0";
   var mainRowObj = this.scrMetaData.uiInitsMap && this.scrMetaData.uiInitsMap[initsMapId];
   if(mainRowObj){
    currElm.dataType = currElm.dataType || mainRowObj[1];
   }
    var args = {};
    if(mainRowObj && mainRowObj[2]=="inlineeditable"){
      args.elmId = currRecHtmlId;
      args.fieldType = mainRowObj[1];
      if(currElm.type == "DROPDOWN")
      args.source = mainRowObj[3];
      this.initInlineEditable(args);
    }
    var popoverId = this.scrMetaData.elmsMap[currElm.id].popoverid;
    if (currElm.type == "INPUTBOX" || currElm.type == "INPUTWITHBUTTON") {
      if ((currElm.dataType == "DATE" || currElm.dataType == "DATETIME" || currElm.dataType == "TIME") && !(mainRowObj && mainRowObj[2]=="inlineeditable")) {
        var params = {};
        params.id = currRecHtmlId;
        params.dataType = mainRowObj[1]
        params.lookAndFeel = mainRowObj[2]
        params.parentDisplay = mainRowObj[3]
        params.style = mainRowObj[4]
        params.parentPreset = mainRowObj[5]
        params.parentMinDate = mainRowObj[6]
        params.parentMaxDate = mainRowObj[7]
        params.closeOnSel = mainRowObj[8]
        params.multiSel = mainRowObj[9]
        params.parentStartYear = mainRowObj[10]
        params.parentEndYear = mainRowObj[11]
        params.parentRangePick = mainRowObj[12]
        params.secInputId = mainRowObj[13];
        params.parentMultiInput = mainRowObj[14];
        params.dateType = mainRowObj[15];
        this.initDates(params);
      }
    } else if (currElm.type == "DROPDOWN") {
      mainRowObj[0] = currRecHtmlId;
          if(mainRowObj[2]!=="inlineeditable"){
            if (mainRowObj[6] == "AUTOCOMPLETE" || mainRowObj[6] == "MULTISELECTAUTOCOMPLETE" || mainRowObj[6]=="MULTISELECTTAGS") {
              $("#"+currRecHtmlId).siblings("span").remove();
            }
            this.initDropdowns(mainRowObj);
          }
    } else if (currElm.type == "DROPDOWNWITHINPUT") {
      mainRowObj[0] = currRecHtmlId;
      if ($("#"+mainRowObj[0]+"_ext").find('.dropdown-list').length != 0) {
        var dropdownListId = $("#"+mainRowObj[0]+"_ext").find('.dropdown-list')[0].id;
        this.initDropdownWithInput(mainRowObj[0], dropdownListId);
      } else {
        this.initNativeDropdownWithInput(mainRowObj[0]);
      }
      this.initDropdowns(mainRowObj);
    } else if (currElm.type == "TAGS") {
      this.initTags(currRecHtmlId, mainRowObj[1]);
    }
    if (!this.isNull(popoverId)) {
      var params = {};
      var popoverObj = this.scrMetaData.uiInitsMap["popover"][currElm.id];
      params.elmId = currRecHtmlId;
      params.targetId = popoverObj[1];
      params.position = popoverObj[3];
      this.initPopover(params);
    }
  }
}

Apz.prototype.dropdownAutocomplete = function(obj) {
  $(obj).select2({
    width: '100%'
  });
  $(obj).on("select2:open", function() {
    $(".select2-search--dropdown .select2-search__field").attr("placeholder", "");
  });
  $(obj).on("select2:close", function() {
    $(obj).focus();
  });
  $(obj).on("select2:select", function(event) {
   // apz.data.setPropsData(this.props, this.value);
   //apz.data.setDropDownData(event);
   apz.data.setElmData(event.currentTarget.id,event.currentTarget.value)

  });
}
Apz.prototype.dropdownTags = function(obj){
  $(obj).select2({
    width: '100%'
  });
  $(obj).on("select2:select", function(event) {
  //  apz.data.setPropsData(this.props, apzJquery(this).val().toString());
  //apz.data.setDropDownData(event);
  //apz.data.setElmData(event.currentTarget.id,event.currentTarget.value,true)
	var dropdownValue = apz.getElmValue(this.id);
	apz.data.setElmData(event.currentTarget.id,dropdownValue,true) //Updating the way selected value is read

  });
  $(obj).on("select2:unselect", function(event) {
    //apz.data.setPropsData(this.props, apzJquery(this).val().toString());
   //apz.data.setDropDownData(event);
   apz.data.setElmData(event.currentTarget.id,event.currentTarget.value,true)


  });
}
Apz.prototype.initTags = function(id, parentPlaceholder) {
  var tagName = document.getElementById(id).tagName;
  if (id != "" && tagName != "DD") {
    var existingdiv = $("#" + id).next();
    var objPlaceholder = "add a tag";
    if (!this.isNull(parentPlaceholder)) {
      objPlaceholder = parentPlaceholder;
    }
    var interact = true;
    $("#" + id).attr('apztype', 'tags');
    if(!this.isNull($('#'+id).attr('readonly')) || !this.isNull($('#'+id).attr('disabled'))){
      interact = false;
    }
    let apzObj = this;
    var obj = {
      'defaultText' : objPlaceholder,
      'interactive' : interact,
      'onAddTag' : function(tag){
        //apz.data.setTagsInputData(id);
        apz.data.setElmData(id);
        //apzObj.data.setPropsData(this.props, this.value);
      },
      'onRemoveTag' : function(tag){
        apz.data.setElmData(id);
        //apz.data.setTagsInputData(id);
       // apzObj.data.setPropsData(this.props, this.value);
      }
    };
    if (!existingdiv.hasClass("tagsinput")) {
      $("#" + id).tagsInput(obj);
    } else {
      existingdiv.remove();
      $("#" + id).tagsInput(obj);
    }
    let tagClass = $("#" + id).attr('class');
    $("#" + id + "_tag").addClass("tagsinput "+tagClass);
    let tagTitle = $("#" + id).attr('title');
    $("#" + id + "_tag").attr('title', tagTitle);
  }
}

Apz.prototype.initJqueryDates = function(param) {
  var params = {
    dateFormat: this.dateformat,
    firstDay: 1,
    showOtherMonths: true,
    selectOtherMonths: true,
    minDate: Date.parseExact(param.parentMinDate, this.dateFormat),
    maxDate: Date.parseExact(param.parentMaxDate, this.dateFormat),
    changeMonth: true,
    changeYear: true
  };
  if (param.secInputId !== "" && $("#" + param.id).attr("firstinputid") == undefined) {
    document.getElementById(param.id).setAttribute("secondinputid", param.secInputId);
    document.getElementById(param.secInputId).setAttribute("firstinputid", param.id);
  }
  var apzObj = this;
  params.beforeShow = function(input, inst) {
    apzObj.alignDatePicker(input, inst);
  }
  if (param.parentRangePick == "Y" && param.parentMultiInput == "N") {
    var previousDate = "",
      fromDate = "",
      toDate = "",
      dateSelected = "";
    params.beforeShow = function(input, inst) {
      previousDate = this.value.split(" ");
      fromDate = new Date(previousDate[0]);
      toDate = new Date(previousDate[2]);
      apzObj.alignDatePicker(input, inst);
    }
    params.beforeShowDay = function(date1) {
      if (dateSelected == "") {
        if (date1 >= fromDate && date1 <= toDate) {
          return [true, 'actdate'];
        }
      }
      return [true, ''];
    }
    params.onSelect = function(selectedDate) {
      dateSelected = selectedDate;
      if (!$(this).data().datepicker.first) {
        $(this).data().datepicker.inline = true
        $(this).data().datepicker.first = selectedDate;
      } else {
        if (selectedDate > $(this).data().datepicker.first) {
          $(this).val($(this).data().datepicker.first + " - " + selectedDate);
        } else {
          $(this).val(selectedDate + " - " + $(this).data().datepicker.first);
        }
        $(this).data().datepicker.inline = false;
      }
      $(".activeDates").removeClass("activeDates");
      $(this).focus();
    }
    params.onClose = function() {
      delete $(this).data().datepicker.first;
      $(this).data().datepicker.inline = false;
      dateSelected = "";
    }
  } else if (param.eventCalender == "Y") {
    params.beforeShowDay = enableDays;
  } else if (param.parentRangePick == "Y" && (param.parentMultiInput == "Y" && ($("#" + param.id).attr("firstinputid") !== undefined || $("#" + param.id).attr("secondinputid") !== undefined))) {
    var dateSelected = "";
    params.onSelect = function(selectedDate) {
      dateSelected = selectedDate;
    }
    params.onClose = function() {
      if (document.getElementById(param.id).hasAttribute("secondinputid")) {
        $("#" + param.secInputId).datepicker("option", "minDate", dateSelected);
      } else {
        var firstId = document.getElementById(param.id).getAttribute("firstinputid")
        $("#" + firstId).datepicker("option", "maxDate", dateSelected);
      }
    }
  }
  if (!params.onSelect) {
    let myObj = this;
    params.onSelect = function() {
      let input = this;
      let reqParams = {};
      reqParams.props = input.props;
      reqParams.targetObj =  input;
      reqParams.targetEvent =  "blur";
      reqParams.targetReactEvent = "onBlur"; 
      setTimeout(function() {
        apz.data.setPropsData(input.props, input.value);
        myObj.triggerEvent(reqParams);
      }, 0);
    }
  }
  if ($("#" + param.id).hasClass("hasDatepicker")) {
    $("#" + param.id).removeClass("hasDatepicker");
  }
  $("#" + param.id).datepicker(params);
}

Apz.prototype.populateDropdown = function(obj, options, dropDownType) {
  /* Params contains the below values
     *** obj, options(Array of val,desc object) ***
  */
  let id;
  if (dropDownType) {
	id = obj.id;
	let index = id.lastIndexOf('_');
    id = id.substr(0, index).replace("_span", "");
  } else {
		id = this.getObjIdWORowNumber(obj);
  }
  apz.scrMetaData.elmsMap[id].staticOptions = options;
  var elmsData = apz.scrMetaData.elmsMap[id];
  if(elmsData && elmsData.container)
     apz.data.reRenderComponent(elmsData.container);
}

Apz.prototype.handleHeader = function(event) {
  ///// TBC - What was this function for???
  /*var divObj = document.getElementById("header");
  if (divObj) {
    var nodes = divObj.getElementsByTagName('*');
    for (var k = 0; k < nodes.length; k++) {
      tagType = nodes[k].tagName.toLowerCase();
      if (tagType == "div" || tagType == "ul" || tagType == "li") {
        event.stopPropagation();
      }
    }
  }*/
}

//// Boilerplate function for getting updated props on each dom updates
Apz.prototype.setDomReference = function(element, props, value) {
  if (element) {
    let elmMetaData = apz.scrMetaData.elmsMap[props.id];
    element.props = props;
    apz.updateObjValue(element, value, props, elmMetaData);
  }
}

///// handle for updating any dom related changes after react updates
Apz.prototype.updateObjValue = function(obj, value, props, metaData) {
  let widgetType = metaData.type;
  switch(widgetType){
    case "TAGS" : this.updateTagsValue(obj, value, props, metaData);
      break;
    case "INPUTBOX" : this.updateInputValue(obj, value, props, metaData);
      break;
    case "CHECKBOX" : this.updateCheckboxValue(obj, value, props, metaData);
      break;
    case "DROPDOWN" : this.updateDropdownValue(obj,value,props,metaData);
      break;
  }
}

Apz.prototype.updateTagsValue = function(obj, value, props, metaData) {
  $(obj).importTags(value);
}

Apz.prototype.updateDropdownValue = function(obj, value, props, metaData) {
  let prevVal = obj.prevVal;
  obj.prevVal = value;
  if (props.type == "MULTISELECTTAGS" && !this.isValueEmpty(prevVal)) {
    prevVal = prevVal.toString();
    value = value.toString();
  }
  if (prevVal !== value) {
    if (props.type == "AUTOCOMPLETE" || props.type == "MULTISELECTTAGS") {
      if ($(obj).hasClass("select2-hidden-accessible")) {
        $(obj).trigger("change");
      }
    }
    if (!this.isValueEmpty(prevVal)) {
      this.triggerEvent({
        targetEvent: "change",
        targetReactEvent: "onChange",
        props: props,
        targetObj: obj
      });
    }
  }
}

Apz.prototype.updateInputValue = function(obj, value, props, metaData) {
  
}

Apz.prototype.updateCheckboxValue = function(obj, value, props, metaData) {
  if(obj.getAttribute("indeterminateval") == value){
    obj.indeterminate = true;
  } else {
    obj.indeterminate = false;
  }
}



////// Default event handlers for saving and calling further event chain
Apz.prototype.changeHandler = function(props, event){
  apz.data.saveValue(props, event);
  if(props.apzevents && props.apzevents.onChange){
    props.apzevents.onChange(event)
  }
}
Apz.prototype.blurHandler = function(props, event){
  apz.data.saveValue(props, event);
  if(props.apzevents && props.apzevents.onBlur){
    props.apzevents.onBlur(event)
  }
}

Apz.prototype.triggerEvent = function(params){
  let props = params.props;
  if(props.apzevents && props.apzevents[params.targetReactEvent]){
    let obj = params.targetObj;
    if(!params.targetObj){
      obj = document.getElementById(params.id);
    }
    let event = new Event(params.targetEvent, { bubbles: true });
    obj.dispatchEvent(event);
    props.apzevents[params.targetReactEvent](event);
  }
}
Apz.prototype.isValueEmpty = function(obj) {
  /* Params contains the below value
      *** obj ***
      * Response contains below value
      *** boolean ***
  */
  return (!((obj !== null) &&  (obj !== "undefined") && (obj !== undefined)));
}
Apz.prototype.sortRecords = function(params) {
/* Params contains the below attributes
  *** container, element, sortType ***
  */
    var containerId = params.container;
    var element = params.element;
    var sortType = params.sortType;
    var containerObj = this.scrMetaData.containersMap[containerId];
    var containerType = containerObj.type;
    var obj = {};
    obj.containerId = containerId;
    obj.action = "C";
    obj.tracker = [];
    obj.dataPointers = [];
    if (containerObj.multiRec == 'Y') {
      var node;
      var nodes = containerObj.nodes;
      var noOfNodes = nodes.length;
    var elmObj = this.scrMetaData.elmsMap[element.substring(0, element.length - 2)];
    var parents = apz.scrMetaData.nodesMap[elmObj.nodeId].parents;
    if(apz.scrMetaData.nodesMap[elmObj.nodeId].relType == "1:N"){
          params.node = elmObj.nodeId;
        this.sortRows(params);
    }else{
        for(var k = parents.length-1; k >= 0; k--){
          if(apz.scrMetaData.nodesMap[parents[k]].relType == "1:N"){
            params.node = parents[k];
              this.sortRows(params);
              break;
          }
        }
    }
      for (var x = 0 ; x < noOfNodes ; x ++){
          node = nodes[x];
          if(parents.indexOf(node) < 0 && apz.scrMetaData.nodesMap[node].relType == "1:N"){
            params.node = node;
            this.sortRows(params);
          }
      }
    }

   var params = {};
   params.tracker = null;
   params.recNo = 0;
   params.container = containerId;
   this.data.goToRecord(params);
   apz.data.reRenderComponent(containerId);
   //this.data.updateStateContext();
   //apz.compFactory[proc.scr+'_'+proc.appId].compFactory.changeDetectorRef.detectChanges();
}

Apz.prototype.initFixedHeaderTable = function(obj,destroy) {
  var jqueryObj = $(obj); 
  if(jqueryObj.length > 0 && jqueryObj.is(":visible") && jqueryObj.hasClass('fixedheader')) {
    var tblWrap = jqueryObj.parents(".crb-tabl:first");
    if (destroy) {
      jqueryObj.fixedHeaderTable('destroy');
    }else {
      tblWrap.find('.fht-cell').remove();
    }
    tblWrap.attr("oldWidth",tblWrap.width());			
    jqueryObj.fixedHeaderTable({});
    tblWrap.find('table:first').find('th').not(".cen").off('click.apzevent').on('click.apzevent', function (e) {      
      let elmId = $(this).attr('lovid') + "_0";
      let parId = $(this).parents(".crb-tabl:first").attr('id');
      apz.sortAction(parId, elmId, e.target);
    })
    tblWrap.find('table:first').find('.cen').off('click.apzevent').on('click.apzevent', function (e) { 
      let parId = $(this).parents(".crb-tabl:first").attr('id');
      apz.toggleRowSelection(parId)
    });
  }
}

Apz.prototype.setElmValue = function(id, value,fromAngular) {
  /* Params contains the below values
      *** id(DOM element ID), value ***
  */
   this.setObjValue(document.getElementById(id), value,fromAngular);
}

Apz.prototype.setObjValue = function(obj, value,fromAngular){
     /* Params contains the below values
         *** obj(DOM element),value ***
     */
      var tagName = obj.tagName;
      var elmData = "";
      if (this.isNull(value)) {
         value = "";
      }
      var $obj = $(obj);
      var objRowNo = this.getObjRowNumber(obj);
      if (objRowNo!=-1) {
         var objRowId = this.getObjIdWORowNumber(obj);
         elmData = this.scrMetaData.elmsMap[objRowId];
      } else {
         elmData = this.scrMetaData.elmsMap[obj.id];
      }
      var idType = $(obj).attr('apztype');
      if(!this.isNull(elmData) && elmData.custom == "Y"){
         var custObj = apz[elmData.type];
         if(custObj && apz.isFunction(custObj.setObjValue)){
            custObj.setObjValue(obj, value);
         }
      } else if (tagName == "INPUT" || (tagName == "SPAN" && !this.isNull(elmData) && elmData.type == "DROPDOWN")) {
         var id = obj.id;
         var type = $('#' + id).attr('type');
         if (type == "hidden") {
            if (obj.classList.contains("appzillon_date")) {
               $('#' + id)[0].nextElementSibling.innerHTML = value;
            }
         } else if (type == "CHECKBOX") {
            /*if (idType == 'toggleswitch') {
               var selectedVal = $('#' + id).attr('selectedval');
               var unselectedVal = $('#' + id).attr('unselectedval');
               if (value == selectedVal) {
                  obj.value = selectedVal;
                  $('#' + id).prop('checked', true);
               } else {
                  obj.value = unselectedVal;
                  $('#' + id).prop('checked', false);
               }
            } else {*/
               var checkedVal = $obj.attr('checkedval');
               var uncheckedVal = $obj.attr('uncheckedval');
               var indeterminateVal = $obj.attr('indeterminateval');
               obj.indeterminate = false;
               if (value == checkedVal) {
                  obj.value = checkedVal;
                  $obj.prop('checked', true);
               } else if (value == indeterminateVal) {
                  obj.value = indeterminateVal;
                  obj.indeterminate = true;
               } else {
                  obj.value = uncheckedVal;
                  $obj.prop('checked', false);
               }
            //}
         } else if (!this.isNull(elmData) && elmData.type == "DROPDOWN") {
             var dropDownDiv = document.getElementById(obj.id+"_div");
             var opts = $(dropDownDiv).find('ul li');
             var noOfElem = $(dropDownDiv).find('ul li').length;
             var isWithCheckbox = false;
             var isWithSubOpt = false;
             if(tagName != "SPAN"){
                if($(opts[0]).find('label').length > 0) {
                   isWithCheckbox = true;
                } else if($(opts[0]).find('span').length > 0) {
                   isWithSubOpt = true;
                }
             }
             if($(obj).parent().hasClass("is-multi-tiered")) {
                var indx ;
                var resArr = [];
                var valArr = value.split(",");
                var labelOpts = $(dropDownDiv).find('label');
                var chkOpts = $(dropDownDiv).find('input');
                for(i=0;i<opts.length;i++) {
                   indx = valArr.indexOf(opts[i].getAttribute("data-value"));
                   if(indx >-1) {
                      resArr.push(labelOpts[i].innerHTML);
                      $(chkOpts[i]).prop("checked",true);
                   } else {
                     $(chkOpts[i]).prop("checked",false);
                   }
                }
                resArr = resArr.toString();
                $(obj).val(resArr);
             } else {
                if(isWithSubOpt) {
                   for (var i = 0; i < noOfElem; i++) {
                       if (opts[i].getAttribute("data-value") == value) {
                      obj.value = $(opts[i]).text();
                      $(opts[i]).addClass('is-selected');
                      } else {
                        $(opts[i]).removeClass('is-selected');
                      }
                   }
                } else {
                   for (var i = 0; i < noOfElem; i++) {
                      if (opts[i].getAttribute("data-value") == value) {
                        if(tagName != "SPAN")
                         obj.value = opts[i].innerHTML;
                      else
                        obj.innerHTML = opts[i].innerHTML;
                        $(opts[i]).addClass('is-selected');
                     } else {
                         $(opts[i]).removeClass('is-selected');
                     }
                  }
                  if ($(opts[0]).parent().find(".is-selected").length == 0) {
                     if(tagName != "SPAN")
                        obj.value = "";
                     else
                        obj.innerHTML = "";
                  }
               }
            }
            if (!this.isNull(obj.getAttribute("onChange")) && !this.isNull(value)) {
              $("#" + obj.id).trigger("change")
            }
          } else if (!this.isNull(elmData) && elmData.type == "DROPDOWNWITHINPUT") {
            var opts = $(obj).siblings('div').find('ul li');
            var noOfElem = $(obj).siblings('div').find('ul li').length;
            var inputid = $(obj).parents('li:first').siblings('li').find('input')[0].id;
            for (var i = 0; i < noOfElem; i++) {
               if (opts[i].getAttribute("value") == value) {
                  //obj.value = opts[i].innerHTML;
                  var inputBoxId = $(obj).attr("id");
                  //$("#"+inputBoxId+"_input")[0].value = opts[i].innerHTML;
                  $("#"+inputid).val(opts[i].innerHTML);
                  $(opts[i]).addClass('is-selected');
               } else {
                 $(opts[i]).removeClass('is-selected');
               }
            }
            if ($(opts[0]).parent().find(".is-selected").length == 0) {
               $("#"+inputid).val('');
               //obj.value = "";
            }
         } else if(type == "text" && idType == "tags"){
            $(obj).importTags(value);
              if(!apz.isNull($(obj).attr('readonly')) || !apz.isNull($(obj).attr('disabled'))){
                $(obj).parent().find('.tagsinput').find('a').remove();
              }
         } else if(idType == "stepper") {
            var minVal = parseFloat(obj.getAttribute("min"));
            var maxVal = parseFloat(obj.getAttribute("max"));
            var minusObj = $(obj).parent().siblings('li:first').children('button');
            var plusObj = $(obj).parent().siblings('li:last').children('button');
            obj.value = value;
            if(value <= minVal) {
               minusObj.attr('disabled','disabled');
               plusObj.removeAttr('disabled');
            } else if(value >= maxVal) {
               plusObj.attr('disabled','disabled');
               minusObj.removeAttr('disabled');
            } else if(!isNaN(value)) {
               plusObj.removeAttr('disabled');
               minusObj.removeAttr('disabled');
            }
         } else if (type != "file") {
             this.changeTypeToText(obj);
             obj.value = value;
          }
      } else if (tagName == "LI" || tagName == "DIV") {
         if (idType == "toggleswitch") {
            var id0 = obj.id + '_00';
            var id1 = obj.id + '_11';
            if ($('#' + id0).val() == value) {
               $('#' + id0)[0].checked = true;
               $('#' + id1)[0].checked = false;
            } else {
               $('#' + id1)[0].checked = true;
               $('#' + id0)[0].checked = false;
            }
         } else if (idType == "radiogroup") {
            $obj.attr('value', value);
            var lid = obj.id;
            var id = lid + '_option_' + value;
            $('#' + id).attr('checked', true);
         } else if (idType == "progress") {
            value = +value;
            value = value*100;
            $(obj).css("width",""+value+"%");

         } /*else {
               var lid = obj.id;
               $obj.attr('value', value);
               var id = lid + '_option_' + value;
               $('#' + id).attr('checked', true);
         }*/  // redundant code
      } else if (tagName == "SELECT") {
         var noOfElem = obj.options.length;
         var makeNull = true;
         if($(obj).parent().hasClass("mltt")) {
            var valArr = value.split(",");
            var indx;
            var appender ;
            var tagArr = [];
           $(obj).parent().find('.select2-selection__rendered .select2-selection__choice').remove();
             for (var i = 0; i < noOfElem; i++) {
               indx = valArr.indexOf(obj.options[i].value);
            if (indx >-1) {
               makeNull = false;
               tagArr.push($(obj.options[i]).attr('value'));
            } else {
              $(obj.options[i]).prop('selected',false);
            }
         }
         if (makeNull) {
            $(obj).val("").trigger('change')
         } else {
            $(obj).val(tagArr).trigger('change')
         }
         } else {
            var optVal = "";
             for (var i = 0; i < noOfElem; i++) {
            if (obj.options[i].value == value) {
               makeNull = false;
               obj.selectedIndex = i;
               if($(obj).parent().hasClass("acpt")) {
                  optVal = $(obj.options[i]).attr('value');
                  $(obj).val(optVal).trigger('change');
               }
            }
         }
         if (makeNull) {
            obj.selectedIndex = "";
            if($(obj).parent().hasClass("acpt")) {
               $("#"+obj.id).val("").trigger('change')
            }
         }
         }
         if (!this.isNull(obj.getAttribute("onChange")) && !this.isNull(value)) {
            $("#" + obj.id).trigger("change")
         }
      } else if (tagName == "TEXTAREA") {
         obj.value = value;
      } else if (tagName == "DD") {
         obj.innerHTML = value;
      } else if (tagName == "DL") {
         var ddList = $(obj).find('dd');
         if (ddList.length > 0) {
            ddList.addClass('sno');
             var dropValue = value.split(","); 
            for (var i = 0; i < ddList.length; i++) {
               if (dropValue.indexOf($(ddList[i]).attr('value')) != -1) {
                  $(ddList[i]).removeClass('sno');
                  //break;
               }
            }
         }
      } else if (tagName == "P" || tagName == "H1" || tagName == "H2" || tagName == "H3" || tagName == "H4" || tagName == "H5" || tagName == "H6") {
         var currObj = $(obj).find("#"+obj.id+"_txtcnt");
         if (currObj.length==0) {
            currObj = obj;
         }
         $(currObj).text(value);
      } else if (tagName == "A") {
         var currObj = $(obj).find("#"+obj.id+"_txtcnt");
         if (currObj.length==0) {
            currObj = obj;
         }
         $(currObj).text(value);
         if(!this.isNull(value) && $(obj).hasClass("mbsc-comp editable")){
           var format = "";
         if(elmData.dataType == "DATE")
           format = this.dateformat;
         else if(elmData.dataType == "DATETIME")
           format = this.datetimeformat;
           var functionParam = mobiscroll.util.datetime.parseDate(format, value);
           $(obj).mobiscroll("setDate", functionParam);
         }
         if($(obj).data("editable") && value != null){
            $(obj).editable("setValue",value);
            $(obj).attr("value", value);
         }
      } else if (tagName == "IMG" || (tagName == "SPAN" && obj.getAttribute("type") == "SVG")) {
         var myStatus;
         var isSVG = false;
         if((tagName == "SPAN" && obj.getAttribute("type") == "SVG")){
          isSVG = true;
         }
         if (value != null && value != "" && (value.indexOf('.') == -1)) {
          if(isSVG){
            obj = this.swipeIdsForImage(obj);
          }
            myStatus = value.indexOf("data:image/");
            if (myStatus == -1) {
               myStatus = value.indexOf("data:image/jpg;base64,");
               if (myStatus == -1) {
                  obj.src = "data:image/png;base64," + value;
               } else {
                  obj.src = value;
               }
            } else {
               obj.src = value;
            }
         }
         else if ((value.indexOf('<svg') == 0) || (value.indexOf('<?xml') == 0) || (value.indexOf('<!DOCTYPE') == 0) || (value.substr(value.length-4)) == ".svg") {
       if(!isSVG){
        obj = this.swipeIdsForImage(obj);
       }
       var imageObj=$(obj);
       if((value.substr(value.length-4)) == ".svg"){
            var currTheme = this.theme;
        myStatus = value.indexOf("styles/themes/" + currTheme + "/img");
        if (!this.isNull(myStatus) && myStatus != -1) {
           var val=this.getFile(value);
           imageObj.html(val);
        } else {
           var path = this.getStylesPath() + "/" + currTheme + "/img/" + value;
           var val=this.getFile(path);
           imageObj.html(val);
        }
      } else {
        imageObj.html(value);
      }
         }
         else {
          if(isSVG){
            obj = this.swipeIdsForImage(obj);
          }
            if (value != null && value != "") {
               var currTheme = this.theme;
               myStatus = value.indexOf("styles/themes/" + currTheme + "/img");
               if (!this.isNull(myStatus) && myStatus != -1) {
                  obj.src = value;
               } else {
                  obj.src = this.getStylesPath() + "/" + currTheme + "/img/" + value;
               }
            } else {
               obj.src = value;
            }
         }
      } else if(tagName == "svg") {
         var classes =  obj.getAttribute("class").split("icon-");
         if(classes.length > 1) {
            $(obj).removeClass("icon-"+classes[1].split(' ')[0]).addClass(value);
            $(obj).find('use').attr("xlink:href","#" + value);
         } else {
            $(obj).addClass(value);
            $(obj).find('use').attr("xlink:href","#" + value);
         }
      } else if (tagName == "PROGRESS") {
         obj.value = value;
      } else if (tagName == "SPAN") {
         if (idType=="radiogroup") {
            var lid = obj.id;
            $obj.attr('value', value);
            var id = lid + '_option_' + value;
            $('#' + id).attr('checked', true);
         } else {
            obj.innerHTML = value;
         }
      } else if(tagName == "BUTTON") {
         if ($(obj).hasClass("with-dropddown")) {
            var opts = $(obj).siblings('div').find('ul li');
            var noOfElem = $(obj).siblings('div').find('ul li').length;
            for (var i = 0; i < noOfElem; i++) {
               if (opts[i].getAttribute("value") == value) {
                  value = opts[i].innerHTML;
               }
            }
            $(obj).html(value);
         } else {
            var currObj = $(obj).find("#"+obj.id+"_txtcnt");
            if (currObj.length==0) {
               currObj = obj;
            }
            $(currObj).text(value);
         }
      } else if (!this.isNull(elmData) && elmData.type == "SORTCODEACCOUNT") {
         var codes = value.split("-");
         for (var i = 0; i < 4; i++) {
            if(codes[i]){
               $("#"+obj.id+"_"+(i+1)).val(codes[i]);
            }
         }
      }
      apz.data.setElmData(obj.id,value,fromAngular);
}
Apz.prototype.screenLoaded = function(proc) {
      /// Customizer screen here as DOM corrections, initializations are done and screen ready to show to user.
      // TBC Darshan - DOM manipulated at show screen level should be handled ??
      var key = proc.scr + this.idSep + proc.lo;
      apz.compFactory[proc.scr+'_'+proc.appId].compFactory.changeDetectorRef.detectChanges();
      if(Apz.customizerApp && this.isFunction(this.customizer.refreshDesigner)){
         this.customizer.refreshDesigner(proc);
      }
      if(this.customizer && this.loDefsMap[proc.appId][key].customize == "Y" && this.isFunction(this.customizer.customize)){
         key = proc.scr + this.idSep + proc.lo + this.idSep + proc.template;
         if (!apz.isNull(this.customizer.config[proc.appId][key])) {
            this.customizer.customize(this.customizer.config[proc.appId][key], this.customizer.configMeta[proc.appId][key]);
         }
      }
      ///Show Screen
      if (proc.animation > 0) {
         var animator = new Apz.Anim(this);
         animator.animate(proc);
      } else {
         this.scrShowScreen(proc);
      }
}
Apz.prototype.initSidebarState = function() {
    var $rolepage = $(".rolepage");
    var sideBar = document.getElementById('sidebar');
    $($rolepage).removeClass('apz-nav-push apz-nav-stay-left apz-nav-stay-right');
    if($rolepage.length==1){
      if (!this.isNull(sideBar)){
        var $sideBar = $(sideBar);
        if ($sideBar.hasClass('apz-nav-open')) {
          if (($sideBar.attr('sidebartype')=='STATIC')) {
            if ($sideBar.hasClass('lft')) {
              $($rolepage).addClass('apz-nav-push apz-nav-stay-left');
            } else if ($sideBar.hasClass('rht')) {
              $($rolepage).addClass('apz-nav-push apz-nav-stay-right');
            }
          }
        }
      }
    }
  }

//Pradeep Added to make tabAction compatible with angular

Apz.prototype.tabAction = function(obj, componentContext){
  var proceed = true;
    var activeTabCont = [];
    var tabobj, ulObj;
    if (this.isFunction(componentContext.beforeTabAction)) {
        proceed = componentContext.beforeTabAction(obj);
    }
    if (proceed != false) {
        var jqueryObj = $(obj);
        if (jqueryObj.parent().hasClass('acco')) {
            if (jqueryObj.parent().hasClass('acco') && !jqueryObj.next('div').hasClass('active')) {
                jqueryObj.attr({'aria-selected':'true','aria-expanded':'true'});
                jqueryObj.next('div').addClass('active');
                var indx = jqueryObj.parent().parent().find('.acco').index(jqueryObj.parent());
                var liObj = jqueryObj.parents('.tabs-ctr:first').siblings('.tabs').find('li')[indx];
                $(liObj).siblings('li').removeClass('current').attr({'aria-selected':'false','aria-expanded':'false'});
                $(liObj).addClass('current').attr({'aria-selected':'true','aria-expanded':'true'});
                jqueryObj.addClass('current');
                jqueryObj.parent().siblings('div').children('ul').removeClass('current').attr({'aria-selected':'false','aria-expanded':'false'});
                jqueryObj.parent().siblings('div').children('.active').removeClass('active');
                activeTabCont = jqueryObj.next('div');

            }
        }
        //for desktop
        else {
            if (!(jqueryObj).hasClass("current")) {
                jqueryObj.addClass("current");
                jqueryObj.siblings('li').removeClass("current").attr({'aria-selected':'false','aria-expanded':'false'});
                jqueryObj.attr({'aria-selected':'true','aria-expanded':'true',})
            }
            var parentDiv = jqueryObj.parents('div.pst-tabs:first');
            if (parentDiv.hasClass('nrsp')) {
                tabobj = jqueryObj.closest('div.pst-tabs').children('div.tabcontent');
                tabobj.removeClass('active').attr('aria-hidden','true');
                activeTabCont = tabobj.eq(jqueryObj.index());
                activeTabCont.addClass('active').attr('aria-hidden','false');
            } else {
                tabobj = jqueryObj.closest('div.pst-tabs').children('div.tabs-ctr').find('div.acco').children('div.tabcontent');
                ulObj = jqueryObj.parent('ul:first').siblings('div.tabs-ctr').find('div.acco').children('ul');
                tabobj.removeClass('active').attr('aria-hidden','true');
                tabobj.parent('.acco').attr('aria-hidden','true');
                ulObj.removeClass('current').attr({'aria-selected':'false','aria-expanded':'false'});
                activeTabCont = tabobj.eq(jqueryObj.index());
                activeTabCont.attr('aria-hidden','false').parent('.acco').attr('aria-hidden','false');
                activeTabCont.addClass('active').attr('aria-hidden','false').siblings("ul").addClass('current').attr({'aria-selected':'true','aria-expanded':'true'});
            }
        }
    }
    if (activeTabCont.length > 0) {
        this.initFixedHeaderTables(activeTabCont.find('table[id].fixedheader:visible'));
        this.initDataTables(activeTabCont.find('table[id].responsive:visible'));
    }
    if (this.isFunction(componentContext.afterTabAction)) {
        componentContext.afterTabAction(obj);
    }
}


Apz.prototype.hideColumn = function(id) {
	 var tdid,indx,recs;
      var elementData = apz.scrMetaData.elmsMap[id];
         if(!apz.isNull(elementData)) {
            tdid = "td_"+id+"_0";
         } else {
            var id = apz.getElmObjIdWORowNumber(document.getElementById(id));
            elementData = apz.scrMetaData.elmsMap[id];
            if(!apz.isNull(elementData)) {
               tdid = "td_"+id+"_0";
            } else {
               id = id.slice(3);
               elementData = apz.scrMetaData.elmsMap[id];
               tdid = "td_"+id+"_0";
            }
         }   
      var containerId = elementData.container;
      var containerData = apz.scrMetaData.containersMap[containerId];
      var containerType = containerData.type;
      if(containerType == "LIST" || containerType == "NAVBAR") {
         var $id = $("[id^='"+id+"_0']");
         recs = $id.closest('li').children();
         indx = recs.index($id.closest('seccolcomponent'));
         $("#"+containerId).find('ul.pri').find('li').children(":nth-child("+(indx+1)+")").addClass("sno");
      } else if(containerType == "TABLE") {
         var $tdid = $("#"+tdid);
         recs = $tdid.closest('tr').children();
         indx = recs.index($tdid.parent());
         $("#"+containerId+"_table").find('tr').children(":nth-child("+(indx+1)+")").addClass("sno");
      }
}


Apz.prototype.showColumn = function(id) {
	  var tdid,indx,recs;
      var elementData = apz.scrMetaData.elmsMap[id];
         if(!apz.isNull(elementData)) {
            tdid = "td_"+id+"_0";
         } else {
            var id = apz.getElmObjIdWORowNumber(document.getElementById(id));
            elementData = apz.scrMetaData.elmsMap[id];
            if(!apz.isNull(elementData)) {
               tdid = "td_"+id+"_0";
            } else {
               id = id.slice(3);
               elementData = apz.scrMetaData.elmsMap[id];
               tdid = "td_"+id+"_0";
            }
         }   
      var containerId = elementData.container;
      var containerData = apz.scrMetaData.containersMap[containerId];
      var containerType = containerData.type;
      if(containerType == "LIST" || containerType == "NAVBAR") {
         var $id = $("[id^='"+id+"_0']");
         recs = $id.closest('li').children();
         indx = recs.index($id.closest('seccolcomponent'));
         $("#"+containerId).find('ul.pri').find('li').children(":nth-child("+(indx+1)+")").removeClass("sno");
      } else if(containerType == "TABLE") {
         var $tdid = $("#"+tdid);
         recs = $tdid.closest('tr').children();
         indx = recs.index($tdid.parent());
         $("#"+containerId+"_table").find('tr').children(":nth-child("+(indx+1)+")").removeClass("sno");
      }
}


/* Apz.prototype.updatePaginationRecords = function(containerId) {
      var proceed = true;
      if(this.isFunction(this.app.preUpdatePageSize)){
         proceed = this.app.preUpdatePageSize(containerId);
         if (this.isNull(proceed)) {
            proceed = true;
         }
      }
      if (proceed) {
         var selectedObj = document.getElementById(containerId+"_dps");
         //if(!this.isNull(selectedObj)){
            var containerData = this.scrMetaData.containersMap[containerId];
            var selectedPageSize = selectedObj.value;
            var params = {};
            params.containerId = containerId;
            params.action = "C";
            params.dataPointers = [];
            params.tracker = [];
            this.data.setData(params);
            var oldPagSize = containerData.pageSize;
            if (containerData.type == "TABLE") {
               if($("#"+containerId+"_table").hasClass('dataTable')){
                  $("#"+containerId).find('tbody tr').not(":first").each(function(){
                     $("#"+containerId+"_table").DataTable().row(this).remove().draw(false);
                  });
               } else {
                  $("#"+containerId).find('tbody tr').not(":first").remove();
               }
            } else if (containerData.type=="LIST") {
               $($("#"+containerId).find('[rowNo="0"]:first')[0]).siblings('li').remove();
            }
            /// update container pagesize
            if(selectedPageSize == "ALL"){
                containerData.pageSize = containerData.totalRecs;
            } else {
                containerData.pageSize = parseInt(selectedPageSize);
            }
            var newPageSize = containerData.pageSize;
            var currPage = containerData.currPage;
            var firstRec = (currPage - 1) * oldPagSize;
            var newPage = Math.round((firstRec+1) / newPageSize);
            firstRec = (newPage - 1) * newPageSize;
            if (newPage==0) {
               newPage = 1;
               firstRec = 0;
            }
            containerData.currRec = firstRec;
            ///containerData.currPage = newPage;
            if (newPage>0) {
               var dataRecNo = firstRec;
               var params = {};
               params.container = containerId;
               params.dataRecNo = dataRecNo;
               params.action = "D";
               this.data.goToRecord(params);
               this.data.reRenderComponent(containerId);
            }
         //}
      }
      if(this.isFunction(this.app.postUpdatePageSize)){
         this.app.postUpdatePageSize(containerId);
      }
} */
