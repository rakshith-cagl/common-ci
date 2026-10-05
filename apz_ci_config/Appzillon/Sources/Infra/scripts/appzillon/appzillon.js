/**
 * Apz 
 * @namespace  
 */
var Apz = function() {
   ///////////////App Hooks////////////////////
   this.app = {};
   this.config = {};
   this.config.shouldRemoveSpaces = false;
   //////////////For Range picker//////////////   
   this.ranges = [];       /// TBC
   this.rangesmap = {}; 
   this.appFactory={};  //// TBC 
   this.compFactory={};
   ///////////////Properties////////////////////
   this.version = "1.0.0.0";
   this.expiryDate = null;
   this.serverToken = null;
   this.otpReqd = null;
   this.otaReqd = null;
   this.serverUrl = null;
   this.trackLocation = false;
   this.serverToken = null;
   this.firstPage = null;
   this.firstPageLayout = null;
   this.defaultAuthorization = null;
   this.dataIntegrity = "N";
   this.encryption = "N";
   ///////////////Settings//////////////////////
   this.dateFormat = "";
   this.timeFormat = "";
   this.dateTimeFormat = "";
   this.dfltDecimals = 2;
   this.decimalSep = ".";
   this.thousandSep = ",";
   this.numberMask = "MILLION";
   this.theme = "";
   this.baseThemesMap = "";
   this.language = "en";
   this.enableAnimations = true;
   this.encryptionKeyFileName = "";
   //////////////Constants//////////////////
   this.bindingEngine = "APPZILLON";
   this.genTheme = null;
   this.idSep = "__";
   this.recSep = "_"
   this.webPath = "";
   this.deviceId = "SIMULATOR";
   this.deviceGroup = null;
   this.deviceGroupDet = null;
   this.deviceType = "SIMULATOR";
   this.screenSize = null;
   this.screenPpi = null;
   this.stdPpi = 160;
   this.nativeServiceCBName = "Apz.nativeServiceCB";
   /////////////Angular Specific////////////////
   this.angularscope = null;
   /////////////App Data////////////////////////
   this.latitude = "0";
   this.longitude = "0";
   this.orientation = "PORTRAIT";
   this.lockRotation = false;
   this.sessionId = null;
   this.userId = null;
   this.userFirstName = null;
   this.userLastName = null;
   this.userExtId = null;
   this.userProfilePic = null;
   this.lastLogin = null;
   //this.store = [];	TBC-Darshan what is it for? commented for creating store function
   this.ifaces = [];
   this.ifacesMap = [];
   this.scrs = [];
   this.scrDefs = [];
   this.scrDefsMap = [];
   this.firstPagesMap = [];
   this.loDefsMap = [];
   this.loMap = [];
   this.scrHtmls = [];
   this.loadDiv = "page_1";
   this.currDiv = "page_2";
   this.currScr = "";
   this.childScr = "";
   this.msgs = [];
   this.lovs = [];
   this.lits = [];
   this.ccys = [];  
   this.convMap = [];
   this.workflows = [];
   this.scrMetaData = null;
   this.scrReq = null;
   this.scrResp = null;
   this.privs = {};
   this.privs.screens = {};
   this.privs.ifaces = {};
   this.privs.controls = {};
   ////////////////////////Micro Apps variables /////////
   this.appsMap = {};
   ////////////Map to store and retrieve info////////////
   this.keyValuePair = {};
   /////////////Flags/Temp Stores////////////////////////
   this.procIdCntr = 0;
   ////////////////Initialize Mandatory Modules/////////
   this.ns = new Apz.Ns(this);
   this.log = new Apz.Log(this);
   ////////////////Initialize Optional Modules//////////
   if (Apz.Data) {
      this.data = new Apz.Data(this);
   }
   if (Apz.Audit) {
      this.audit = new Apz.Audit(this);
   }
   if (Apz.Val) {
      this.val = new Apz.Val(this);
   }
   if (Apz.Mca) {
      this.mca = new Apz.Mca(this);
   }
   if (Apz.Server) {
      this.server = new Apz.Server(this);
   }
   if (Apz.Offline) {
      this.offline = new Apz.Offline(this);
   }
   if (Apz.Wf) {
      this.workflow = new Apz.Wf(this);
   }
   if (Apz.Lov) {
      this.lov = new Apz.Lov(this);
   }
   if (Apz.Qunit) {
      this.qunit = new Apz.Qunit(this);
   }
   if (Apz.Customizer) {
      this.customizer = new Apz.Customizer(this);
   }
   if (Apz.ConvUI) {
	      this.convui = new Apz.ConvUI(this);
	   }
   if (Apz.OWASP) {
      this.owasp = new Apz.OWASP(this);
   }
   if (Apz.Products) {
      this.products = new Apz.Products(this);
   }
   define('jquery', [], function() {
      return jQuery;
   });
   // require.config({
	// 	waitSeconds: 200
   // });
};
Apz.rict = APPZILLONRICTAPP //Dont Delete
Apz.mcaApp = APPZILLONMCAAPP //Dont Delete
Apz.customizerApp = APPZILLONCUSTOMIZER //Dont Delete
////Plugin Call Details
Apz.apzNativeServiceDet = [];
////Display Msg Details
Apz.dispMsgCBParams = null;
///Disp MSg Call Back
// With the latest alertify changes_AB // updated again_29Apr2022_AB
Apz.dispMsgCB = function(value,evnt) {
   apz.dispMsgCleanup();
   var params = Apz.dispMsgCBParams;
   if (params) {
      params.choice = true;
      // added code for alertify changes_AB
      if (apz.isNull(evnt) && value != undefined && value.button!= undefined && value.button.className == 'ajs-cancel'){
         params.choice = false;
         value = "";
      } else if (!apz.isNull(evnt) && value.button.className != 'ajs-cancel') {
         params.evnt = evnt;
      }
	  params.val = value;
	  ////Nullify Params
	  Apz.dispMsgCBParams = null;
      //Call User Call Back
      if (typeof  params.callBack == "function") {
         if (params.callBackObj) {
              params.callBack.call(params.callBackObj, params);
         } else {
              params.callBack(params);
         }
      }
   }
}
/////Init Native Service
///This gets called from Window Context as the call comes from native layer
Apz.nativeServiceCB = function(res) {
   if (typeof res == "string") {
      res = JSON.parse(res);
   }
   var id = res.id;
   if (res.id) {
      var cbDet = Apz.apzNativeServiceDet[id];
      res.proc = cbDet.proc;
      res.fwdData = cbDet.fwdData;
      res.callBack = cbDet.callBack;
      res.callBackObj = cbDet.callBackObj;
      res.userCallBack = cbDet.userCallBack;
      res.userCallBackObj = cbDet.userCallBackObj;
      ////Call Thread  CallBack
      cbDet.owner.procThreadCompleted.call(cbDet.owner, res);
      ///Remove From Map
      if (!res.keepAlive) {
         delete Apz.apzNativeServiceDet[id];
      }
   }
};
Apz.appendLog = function(type, msg){
	apz.log.log(type, msg);
}
//////////////////////////////////////////////////////////////
///////////////////Prototype Definition///////////////////////
//////////////////////////////////////////////////////////////
Apz.prototype = {
   initNativeService : function(req) {
	   /* Params contains the below attributes
	       *** id, callBackObj, callBack, proc, fwdData, runner, runnerObj ***
	   */
      if (req) {
         if (this.isNull(req.id)) {
            req.id = this.getProcId();
         }
         var cbDet = {};
         cbDet.id = req.id;
         //Reqiured to call thread Completed of the current instance
         cbDet.owner = this;
         cbDet.callBack = req.callBack;
         cbDet.callBackObj = req.callBackObj;
         cbDet.userCallBack = req.userCallBack;
         cbDet.userCallBackObj = req.userCallBackObj;
         cbDet.proc = req.proc;
         cbDet.fwdData = req.fwdData;
         Apz.apzNativeServiceDet[req.id] = cbDet;
         ///Remove circular dependency
         if (req.proc) {
             req.proc = null;
         }
         if (req.fwdData) {
             req.fwdData = null;
         }
         if (req.runnerObj) {
             req.runnerObj = null;
         }
         if (req.runner) {
             req.runner = null;
         }
         if (req.callBack) {
             req.callBack = null;
         }
         if (req.callBackObj) {
             req.callBackObj = null;
         }
         if (req.userCallBack) {
             req.userCallBack = null;
         }
         if (req.userCallBackObj) {
             req.userCallBackObj = null;
         }
      }
   }, getProcId : function() {
	   /* Response contains below attributes
	       *** procIdCntr ***
	   */
      this.procIdCntr = this.procIdCntr + 1;
      return "PROC_" + this.procIdCntr;
   },
   //////////////////////////////////////////////////////////////
   ///////////////////Process Definition///////////////////////
   //////////////////////////////////////////////////////////////
   startProc : function(proc) {
      var noOfThreads = proc.threads.length;
      proc.noOfThreads = noOfThreads;
      proc.successCount = 0;
      proc.failureCount = 0;
      if (noOfThreads > 0) {
         for (var i = 0; i < noOfThreads; i++) {
            var params = proc.threads[i];
            params.proc = proc;
            ///Execute
            params.runner.call(params.runnerObj, params);
         }
      } else {
         ////Directly call Process Completed
         if (this.isFunction(proc.callBack)) {
            if (proc.callBackObj) {
               proc.callBack.call(proc.callBackObj, proc);
            } else {
               proc.callBack(proc);
            }
         }
      }
   }, procThreadCompleted : function(params) {
      if (params) {
         try {
            ////Call Thread Callback Function
            if (this.isFunction(params.callBack)) {
               if (params.callBackObj) {
                  params.callBack.call(params.callBackObj, params);
               } else {
                  params.callBack(params);
               }
            }
            ////Call Process Call Back..
            if (params.proc) {
               if (!params.status) {
                  params.proc.failureCount = params.proc.failureCount + 1;
               } else {
                  params.proc.successCount = params.proc.successCount + 1;
               }
               if (params.proc.noOfThreads) {
                  params.proc.noOfThreads = params.proc.noOfThreads - 1;
                  if (params.proc.noOfThreads === 0) {
               //      console.log("Process Completed..");
                     if (this.isFunction(params.proc.callBack)) {
                        if (params.proc.callBackObj) {
                           params.proc.callBack.call(params.proc.callBackObj, params.proc);
                        } else {
                           params.proc.callBack(params.proc);
                        }
                     }
                  }
               }
            }
         } catch (e) {
            console.log("Error :" + e.message + "\n" + e.stack);
            if ((params.content) && (params.content).includes("Oops!!! Something went wrong here...")) {
                var params = {
                    "code": "APZ-CSRF-EX"
                };
                params.callBack = apz.app.sessionTimeoutCallBack;
                this.dispMsg(params);
            }
         }
      }
   },
   //////////////////////////////////////////////////////////////
   ///////////////////Utils//////////////////////////////////////
   //////////////////////////////////////////////////////////////
   /**
    * The API lets you read the contents of a data file within the app.
    * @param {object} params params is a JSON object with the following structure
    * {
    * path: This parameter should contain the name of the file name with relative path.
    * async: Ajax call to be asynchronous or synchronous.
    * }
    * @example
    * var fileData;
    * var params = {};
    * params.path = apz.getDataFilesPath() + "/balenq.json";
    * fileData=apz.getFile(params);
    */
   getFile : function(params) {
	   /* Params contains the below attributes
	       *** path, async(boolean) ***
	       * Response contains below attributes
	       *** content ***
	 */
      var content = null;
      if (this.getDataType(params) == "STRING") {
         var path = params;
         params = {};
         params.path = path;
      }
      var myobj = $(this)[0];
      if (this.isNull(params.async)) {
         params.async = false;
      }
      $.ajax({
         type : "GET", url : params.path, dataType : "text", async : params.async, success : function(data) {
            params.status = true;
            params.content = data;
            ///Callback
            myobj.procThreadCompleted(params);
            content = data;
         }, error : function(data) {
            params.status = false;
            params.content = null;
            ///Callback
            myobj.procThreadCompleted(params);
         }
      });
      return content;
   }, 
   /**
    * This API starts loader on the screen.
    * No params in Appzillon binding engine. 
    * @param angularComponentContext - Optional param in Angular binding engine. Component's "this" object 
    *								    need to passed as a param. 
    * @example
    * Appzillon: 
    * apz.startLoader();
    *  
    * Pre call back hook: 
    * apz.app.preStartLoader() {
	*	//Method definition
	*	return true;
	* }
    * 
    * @example
    * Angular: 
    * this._apz.startLoader(this);
    * 
    * Pre call back hook:
    * preStartLoader() {
	*	//Method defintion
	*	return true;
    *
    * } 
    */
   startLoader : function(angularComponentContext) {
        var status = true;
        if (this.isFunction(this.app.preStartLoader)) {
            status = this.app.preStartLoader();
        }
        
        if (angularComponentContext && this.isFunction(angularComponentContext.preStartLoader)) {
			status = angularComponentContext.preStartLoader();
		}
		
        if (status) {
            $("#apzloader").removeClass("sno ssp");
            $("body").addClass("loader-start");
        }
   }, 
   /**
    * This API stops loader on the screen
    * @example
    * Appzillon: 
    * apz.stopLoader();
    *  
    * Pre call back hook: 
    * apz.app.preStartLoader() {
	*	//Method definition
	*	return true;
	* }
    * 
    * @example
    * Angular: 
    * this._apz.preStopLoader(this);
    * 
    * Pre call back hook:
    * preStopLoader() {
	*	//Method defintion
	*	return true;
    *
    */
   stopLoader : function(angularComponentContext) {
      var status = true;
      if(this.isFunction(this.app.preStopLoader)) {
         status = this.app.preStopLoader();
      }
      
      if (angularComponentContext && this.isFunction(angularComponentContext.preStopLoader)) {
			status = angularComponentContext.preStopLoader();
	  }
      if(status){
         $("#apzloader").addClass("sno");
         $("body").removeClass("loader-start");
      }
   }, 
   /**
    * This API starts spinner on the specified div id.
    * @param {string} elmId  This is the ID of the element to which the spinner is associated with.
    * @example
    * apz. startSpinner (elmId);
    */
   startSpinner : function(elmId) {
	   /* Params contains the below value
	       *** elementId ***
	   */
      $("#"+elmId+"_spinner").removeClass("sno");
      $("#"+elmId).attr("aria-busy","true");
   }, 
   /**
    * This API stops the spinner for the specified div id.
    * @param {string} elmId  This is the ID of the element to which the spinner is associated with.
    * @example
    * apz.stopSpinner (elmId);
    */
   stopSpinner : function(elmId) {
	   /* Params contains the below value
	       *** elementId ***
	   */
      var status = true;
      if(this.isFunction(this.app.preStopSpinner)) {
         status = this.app.preStopSpinner();
      }
      if(status){
         $("#"+elmId+"_spinner").addClass("sno");
         $("#"+elmId).removeAttr("aria-busy");
      }
   },
   /**
    *
    * This API is used to check whether the variable is null or not. It returns true if it’s null, else returns false.
    * @param {object} obj  - Object to be checked. 
    * @return {boolean}
    * @example
    * var num = 10;
    * apz.isNull(num);
    */ 
   isNull : function(obj) {
	   /* Params contains the below value
	       *** obj ***
	       * Response contains below value
	       *** boolean ***
	   */
      return (!((obj !== null) && (obj !== "") && (obj !== "undefined") && (obj !== undefined)));
   }, getBoolean : function(flag) {
      return (flag == "Y");
   }, 
   /**
    *This API checks whether passed key exists in the given object or not. It returns true if the key exists. 
    * @param {object} obj  The JSON object for which the key is present needs to be checked.
    * @param {string} key  The key which needs to be checked.
    * @example
    * var lobj = {};
    * lobj.name = "Appzillon";
    * apz.containsKey (lobj,"name");
    */
   containsKey : function(obj, key) {
      return key in obj;
   }, 
   /**
    * This API copies the JSON object as an independent object
    * @param {object} obj The JSON Object which needs to be cloned.
    * @example
    * var lobj = {};
    * lobj.name = "Appzillon";
    * var copiedObj = apz.copyJSONObject (lobj);
    */
   copyJSONObject : function(obj) {
	   /* Params contains the below value
	       *** obj ***
	       * Response contains below value
	       *** newObj ***
	   */
      var newObj = null;
      try {
         newObj = JSON.parse(JSON.stringify(obj));
      } catch (err) {
      }
      return newObj;
   }, copyJSONObjectWithFilter : function(fromObj, excludeKeys, targetObj) {
	   /* Params contains the below values
	       *** fromObj, excludeKeys(Array of keys), targetObj ***
	       * Response contains below value
	       *** newObj ***
	   */
	  var newObj = targetObj || {};
      try {
      	 for (var k in fromObj) {
	        if (fromObj.hasOwnProperty(k) && excludeKeys.indexOf(k) == -1) {
	           newObj[k] = fromObj[k];
	        }
	     }
      } catch (err) {
      }
      return newObj;
   }, copyObjByVal : function(){
      for(var i=1; i<arguments.length; i++){
         for(var key in arguments[i]){
            if(arguments[i].hasOwnProperty(key)){
               var val = arguments[i][key];
                  if(Array.isArray(val)){
                     arguments[0][key] = this.copyObjByVal(arguments[0][key] || [], val);
                  } else if(typeof val === "object" && val != null){
                     arguments[0][key] = this.copyObjByVal(arguments[0][key] || {}, val);
                  } else {
                     arguments[0][key] = val;
                  }
            }
         }
      }
      return arguments[0];
   },clearJSONObject : function(obj) {
	   /* Params contains the below value
	       *** obj ***
	   */
      for (var m in obj)
      delete obj[m];
   }, sleep : function(millis, callBack) {
	   /* Params contains the below values
	       *** milliseconds(Number), callBack ***
	   */
      setTimeout(function() {
         if (callBack !== null) {
            callBack();
         }
      }, millis);
   }, getFloat : function(str) {
	   /* Params contains the below value
	       *** string ***
	       * Response contains below value
	       *** float ***
	   */
      var flt = null;
      try {
         flt = parseFloat(str);
      } catch (err) {
         flt = null;
      }
      if (flt.toString() == "NaN") {
         flt = null;
      }
      return flt;
   }, getInt : function(str) {
	   /* Params contains the below value
	       *** string ***
	       * Response contains below value
	       *** IntegerValue ***
	   */
      var intVal = null;
      try {
         intVal = parseInt(str);
      } catch (err) {
         intVal = null;
      }
      if (intVal.toString() == "NaN") {
         intVal = null;
      }
      return intVal;
   }, 
   /**
    * The API is used to get data type of the object.
    * @param {object} object  The object whose data type needs to be obtained.
    * @example
    * apz.getDataType(object);
    * apz.getDataType(document.getElementById("ELEMENT_ID"));
    */
   getDataType : function(object) {
	   /* Params contains the below value
	       *** Object ***
	       * Response contains below value
	       *** type ***
	   */
      if (object === null) {
         return "null";
      } else if (object === undefined) {
         return "undefined";
      } else if (object.constructor === String) {
         return "STRING";
      } else if (object.constructor === Array) {
         return "Array";
      } else if (object.constructor === Object) {
         return "Object";
      } else if (object == "") {
         return "STRING";
      } else {
         return "none";
      }
   }, 
   /**
    * This API returns true if object type is a function else returns false.
    * @param {object} obj - This is the function name that needs to be checked.
    * @example
    * newFunc = function(){
	 *     var num = 10;
	 *     alert(num);
    *  }

    *  Calledfunc = function(){
    *     if(apz.isFunction(newFunc)){
    *        alert("It is a Function");
    *     }
    *  }
    */   
   isFunction : function(obj) {
	   /* Params contains the below value
	       *** Object ***
	       * Response contains below value
	       *** Boolean ***
	   */
      return ( typeof obj == "function");
   }, renameNode : function(params) {
	   //Expects parentNode, node, oldName, newName
      try {
         if (params.oldName != params.newName) {
            var lstr = JSON.stringify(params.node);
            params.parentNode[params.newName] = JSON.parse(lstr);
            params.node = params.parentNode[params.newName];
            delete params.parentNode[params.oldName];
         }
      } catch (err) {
      }
      return params.node;
   }, getObjRowNumber : function(obj) {
      var rowno = obj.getAttribute('rowno');
      if (this.isNull(rowno)) {
         rowno = -1;
      }
      rowno = this.getInt(rowno);
      return rowno;
   }, getObjIdWORowNumber : function(obj) {
      var id = obj.id;
      var lrecno = this.getObjRowNumber(obj);
      var idx = -1;
      if (lrecno != -1) {
         idx = id.lastIndexOf('_');
         id = id.substr(0, idx);
      }
      return id;
   }, getElmObjIdWORowNumber : function(obj) {
   	  var id = "";
   	  if(!this.isNull(obj)){
	      id = obj.id;
	      var lrecno = this.getElmObjRowNumber(obj);
	      var idx = -1;
	      if (lrecno != -1) {
	         idx = id.lastIndexOf('_');
	         id = id.substr(0, idx);
	      }
      }
      return id;
   }, getElmObjRowNumber : function(obj) {
      var rowno = "";
      var trObj = $(obj).closest("[rowno]");
      if (trObj.length > 0) {
         var rowno = trObj.attr('rowno');
      }
      if (this.isNull(rowno)) {
         rowno = -1;
      }
      rowno = this.getInt(rowno);
      return rowno;
   }, getIfaceObj : function(ifaceId, appId) {
	  var ifaceName = this.getIfaceName(ifaceId);
      if(this.isNull(appId)){
         appId = this.currAppId;
      }
      return this.ifacesMap[appId][ifaceName];
   }, getIfaceType : function(iface, appId) {
      var type = "APPZILLON";
      var ifaceName = this.getIfaceName(iface);
      try {
         type = this.getIfaceObj(ifaceName, appId).type;
      } catch (err) {
         type = "APPZILLON";
      }
      return type;
   }, getReqRoot : function(ifaceName) {
      return ifaceName + "_Req";
   }, getResRoot : function(ifaceName) {
      var type = this.getIfaceType(ifaceName);
      var root = ifaceName + "_Res";
      if (type == "DATABASE") {
         root = ifaceName + "_Req";
      }
      return root;
   }, getIfaceIdFromDml : function(iface, dml) {
      var dmlInd = "";
      if (dml == "RES") {
         dmlInd = "_Res";
      } else if (dml == "REQ") {
         dmlInd = "_Req"
      }
      return iface + dmlInd;
   }, getNodeId : function(params) {
	   //Expects iface, dml, node
      var id = "";
      if (!this.isNull(params.node)) {
         var dmlInd = "i";
         if ((params.dml == "RES") || (params.dml == "o")) {
            dmlInd = "o";
         }
         id = params.iface + this.idSep + dmlInd + this.idSep + params.node;
      }
      return id;
   }, getNodeName : function(nodeId) {
      var nodeName = nodeId;
      if (this.containsKey(this.scrMetaData.nodesMap, nodeId)) {
         nodeName = this.scrMetaData.nodesMap[nodeId].name;
      }
      return nodeName;
   }, getElmId : function(nodeId, elm) {
      return nodeId + this.idSep + elm;
   }, getElmName : function(elmId) {
      var elmName = elmId;
      if (this.containsKey(this.scrMetaData.elmsMap, elmId)) {
         elmName = this.scrMetaData.elmsMap[elmId].name;
      }
      return elmName;
   }, getElmIface : function(elmId) {
      var iface = "";
      var ind = elmId.indexOf(this.idSep);
      if (ind >= 0) {
         iface = elmId.substring(0, ind);
      }
      return iface;
   },
   //// Method to check the given element is Datamodel Element
   isDMLElm : function(elmId) {
      var dmlElm = false;
      var elmDet = elmId.split(this.idSep);
      if (elmDet.length == 5) {
         if ((!this.isNull(elmDet[0])) && (!this.isNull(elmDet[0])) && (!this.isNull(elmDet[0])) && (!this.isNull(elmDet[0]))) {
            dmlElm = true;
            ///Rads .. Need to check other conditions as well..
         }
      }
      return dmlElm;
   }, 
   /**
    * This API is used to adjust screen size based on data painted dynamically.
    * @example
    * apz.readjustHeight();
    */
   readjustHeight : function() {
      try {
         var x, y;
         if (self.innerHeight)// all except Explorer
         {
            x = self.innerWidth;
            y = self.innerHeight;
         } else if (document.documentElement && document.documentElement.clientHeight) {
            x = document.documentElement.clientWidth;
            y = document.documentElement.clientHeight;
         } else if (document.body) {
            x = document.body.clientWidth;
            y = document.body.clientHeight;
         }
         var hdrHeight = jQuery('#header').height();
         if (this.isNull(hdrHeight)) {
            hdrHeight = 0;
         }
         var ftrHeight = jQuery('#footer').height();
         if (this.isNull(ftrHeight)) {
            ftrHeight = 0;
         }
         var pageHeight = y - (hdrHeight + ftrHeight);
         var pageWidth = x;
         var sidebarHeight = 0;
         var body = document.getElementById("page-body");
         var sidebar = document.getElementById("sidebar");
         if (pageHeight < 75) {
            return false;
         } else {
            body.style.paddingTop = hdrHeight + "px";
            body.style.paddingBottom = ftrHeight + "px";
            if (!this.isNull(sidebar)) {
               sidebar.style.top = hdrHeight + "px";
               sidebar.style.bottom = ftrHeight + "px";
               sidebarHeight = sidebar.clientHeight;
            }
         }
         if (body.clientHeight <= sidebarHeight) {
            if (pageHeight >= sidebarHeight) {
              
               sidebar.style.minHeight = pageHeight + "px";
            }
         } 
      } catch (e) {
      }
   }, getEncryptionKey : function() {
	   /* Response contains below value
	       *** key ***
	   */
      var key = this.encryptionKey;
      if (!this.isNull(key)) {
         key = key.replace("$APPID", this.appId).replace("$DEVICEID", this.deviceId).replace("$USERID", this.userId);
      }
      return key;
   }, 
   /**
    * This API returns the deviceGroup details.
    * @example
    * apz.getDeviceGroup();
    * @returns  name, os, width, height, orientation.
    * 
    */
   getDeviceGroup : function() {
	   /* Response contains below value
	       *** deviceGroup ***
	   */
      var path = this.getConfigPath() + "/" + "devicegroups.json";
      var params = {};
      params.path = path;
      params.async = false;
      params.id = "DEVICEGROUP";
      params.callBack = null;
      params.content = this.getFile(params);
      var deviceGroups = JSON.parse(params.content);
      deviceGroups = deviceGroups.deviceGroups;
      var deviceGroup = null;
      var sw = this.screenSize.split("X")[0];
      var sh = this.screenSize.split("X")[1];
      sw = Math.round(sw / (this.screenPpi / this.stdPpi));
      sh = Math.round(sh / (this.screenPpi / this.stdPpi));
      var dw = 0;
      var dh = 0;
      var dt = 0;
      var minDiff = 999999;
      for (var i = 0; i < deviceGroups.length; i++) {
         if ((deviceGroups[i].os == "ALL") || (deviceGroups[i].os == this.deviceOs)) {
            dw = Math.abs(sw - deviceGroups[i].width);
            dh = Math.abs(sh - deviceGroups[i].height);
            if (dw == 0 && dh == 0) {
               deviceGroup = deviceGroups[i].name;
               this.deviceGroupDet = deviceGroups[i];
               break;
            } else {
               dt = dw + dh;
               if (dt < minDiff) {
                  deviceGroup = deviceGroups[i].name;
                  minDiff = dt;
                  this.deviceGroupDet = deviceGroups[i];
               }
            }
         }
      }
      return deviceGroup;
   }, 
   // added code_AB
   // This method is used to ckeck whether the user is using mobile or tablet browser
   checkMobileOrTabletBrowser: function () {
   	var isMobTabBrowser = false;
	  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino|android|ipad|playbook|silk/i.test(a)) isMobTabBrowser = true;})(navigator.userAgent||navigator.vendor||window.opera);
	  return isMobTabBrowser;
   },
   /**
    * 
    * @param {object} params
    * @description
    * <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | apz.app.preOrientationChange      | This API (as a hook function) will be used as part of onOrientationChange function. |
    * | apz.app.postOrientationChange     | This API (as a hook function) will be used as part of onOrientationChange function.    | 
    * @example
    * apz.app.preOrientationChange = function(orientation){
	 *	/// write the function that needs to be executed before orientation change
	 * };
    * @example
    * apz.app.postOrientationChange = function(orientation){
	 *	/// content
    * };
    */
   onOrientationChange : function(params) {
	   /* Params contains the below attributes
	       *** orientation ***
	   */
   	  if(this.isFunction(this.app.preOrientationChange)){
		  this.app.preOrientationChange(params.orientation);
	  }
   	  if(params.event == "orientation_change"){
	      var currScr = this.currScr;
	      var currLayout = this.getLayout({"scr":currScr});
	      this.orientation = params.orientation;
	      var recLayout = this.getLayout({"scr":currScr});
	      if(currLayout != recLayout) {
	         this.data.buildData();
	         var args = {};
	         args.scr = currScr;
	         this.launchScreen(args);
	      }
      }
      if(this.isFunction(this.app.postOrientationChange)){
         this.app.postOrientationChange(params.orientation);
      }
   }, getServerDateFormat : function(node) {
	  var format = "";
	  var ifaceName = this.scrMetaData.nodesMap[node].ifaceName;
	  var appId = ifaceName.split("__")[0];
	  var appInfo = this.ifacesMap[appId]; 
	  if(appInfo && appInfo[ifaceName]){
		  format = appInfo[ifaceName].dateFormat;
	  }
     //added condition to fetch the format from the application preferences
     if (this.isNull(format)) {
      if(this.isNull(this[this.appId].dateFormat))
         format = "yyyy-MM-dd";
      else
         format = this[this.appId].dateFormat;
     }
      return format;
   }, getServerTimeFormat : function(node) {
	  var format = "";
	  var ifaceName = this.scrMetaData.nodesMap[node].ifaceName;
	  var appId = ifaceName.split("__")[0];
	  var appInfo = this.ifacesMap[appId]; 
	  if(appInfo && appInfo[ifaceName]){
		  format = appInfo[ifaceName].timeFormat;
	  }
     //added condition to fetch the format from the application preferences
     if (this.isNull(format)) {
      if(this.isNull(this[this.appId].timeFormat))
         format = "hh:mm:ss";
      else
         format = this[this.appId].timeFormat;
     }
      return format;
   }, getServerDateTimeFormat : function(node) {
	  var format = "";
	  var ifaceName = this.scrMetaData.nodesMap[node].ifaceName;
	  var appId = ifaceName.split("__")[0];
	  var appInfo = this.ifacesMap[appId]; 
	  if(appInfo && appInfo[ifaceName]){
		  format = appInfo[ifaceName].dateTimeFormat;
	  }
     //added condition to fetch the format from the application preferences
     if (this.isNull(format)) {
      if(this.isNull(this[this.appId].dateTimeFormat))
         format = "yyyy-MM-dd";
      else
         format = this[this.appId].dateTimeFormat;
     }
      return format;
   }, 
   /**
    * This API is used to apply date format on a value and returns the formatted date value.
    * @param {object} params params includes
    * <br>val: Date which should be formatted.
    * <br>fromFormat: Current date format of the value.
    * <br>toFormat:  Target date format of the value.
    * @example
    * var dt = "21-04-2012";
    * var params = {};
    * params.val = dt;
    * params.fromFormat = "dd-MM-yyyy"
    * params.toFormat = "dd/MM/yyyy"
    * var result = apz.formatDate (params);
    */
   formatDate : function(params) {
	   /* Params contains the below attributes
	       *** val, fromFormat, toFormat ***
	       * Response contains below value
	       *** Date ***
	   */
      var value = "";
      if (!this.isNull(params.val)) {
         try {
            if (params.fromFormat != params.toFormat) {
               var ldate = null;
               ////Convert To Date Object
               if (params.fromFormat == "sssssssssssss") {
                  ldate = new Date();
                  ldate.setTime(params.val);
               } else {
                  ldate = Date.parseExact(encodeURIComponent(params.val), encodeURIComponent(params.fromFormat));
                  // added code_AB
                  // Bug 53913 - DateTime data type sorting/formatting issue in Appzillon 3.7.5 version
                  if (!ldate) {
                     ldate = new Date(params.val);
                  }
               }
               ////Make it a String.
               if (params.toFormat == "sssssssssssss") {
                  value = ldate.getTime();
               } else {
                  value = ldate.toString(params.toFormat);
               }
               ///Check For Null..
               if (this.isNull(value)) {
                  value = params.val;
               }
            } else {
               value = params.val;
            }
         } catch (err) {
            value = params.val;
         }
      }
      return value;
   },formatJqueryDate : function(params) {
      /* Params contains the below attributes
          *** val, fromFormat, toFormat ***
          * Response contains below value
          *** Date ***
      */
      var value = "";
      if (!this.isNull(params.val)) {
         try {
               var ldate = null;
               ldate = Date.parseExact(encodeURIComponent(params.val), encodeURIComponent(params.fromFormat));
               value = ldate;
         } catch (err) {
            value = null;
         }
      }
      return value;
   }, getDecimalPoints : function(elmData, recNo) {
      var decPoints = this.dfltDecimals;
      if (!this.isNull(elmData)) {
         if (elmData.dataType == "INTEGER") {
            decPoints = 0;
         } else {
            var relId = elmData.relElm;
            if (!this.isNull(relId)) {
               if (recNo !== -1) {
                  relId = relId + "_" + recNo;
               }
               if (relId != "__") {
                  var relObj = document.getElementById(relId);
                  if (!this.isNull(relObj)) {
                     var ccy = this.getObjValue(relObj);
                     if (!this.isNull(ccy)) {
                        try {
                           decPoints = this.ccys[ccy];
                           if (this.isNull(decPoints)) {
                              decPoints = this.dfltDecimals;
                           }
                        } catch (e) {
                           decPoints = this.dfltDecimals;
                        }
                     }
                  }
               }
            } else {// // Added by Chandu to take max decimals if currencies not
               // maintained
               decPoints = elmData.maxDec;
               if (this.isNull(decPoints)) {
                  decPoints = this.dfltDecimals;
               }
            }
         }
         if ((this.isNull(decPoints)) || (elmData.dataType != "INTEGER" && decPoints == 0)) {
            decPoints = 2;
         }
      }
      return decPoints;
   }, million : function(params) {
	   //Expects value, decSep, decPoint
      if (params.decSep == ".") {
         thouSep = ",";
      } else {
         thouSep = ".";
      }
      x = params.value.split('.');
      x1 = x[0];
     //Fix for 4. blur giving 4.00. -krish-28.06//
     x2 = x.length > 1 && !this.isNull(x[1]) ? params.decSep + x[1] : '';
      var rgx = /(\d+)(\d{3})/;
      while (rgx.test(x1)) {
         x1 = x1.replace(rgx, '$1' + thouSep + '$2');
      }
      if (x2.length > 1 && params.decPoint > 0) {
         x2 = params.decSep + x2.substring(1, params.decPoint+1);
         if ((x[1].length) <= params.decPoint) {
            for ( i = 0; i < (params.decPoint - x[1].length); i++) {
               x2 = x2 + 0;
            }
         }
      } else {
         for ( i = 0; i < params.decPoint; i++) {
            if (i == 0)
               x1 = x1 + params.decSep + "0";
            else
               x1 = x1 + "0";
         }
      }
      return (x1 + x2);
   },/**
    * Converts to String
    * @param {string} params 
    */ 
   lakh : function(params) {
	   //Expects value, decSep, decPoint
      if (params.decSep == ".") {
         thouSep = ",";
      } else {
         thouSep = ".";
      }
      var runTime = "N";
      var x = params.value.split('.');
      var value = x[0];
      var len = value.length;
      var len1 = len - 3;
      var i = value.substring(len - 3, len);
      var j = value.substring(0, len - 3);
      var result = '';
      if (len > 3) {
         var a = 2;
         if (len1 % 2 == 0) {
            for (var count = 0; count < len1; count++) {
               result += j.substring(count, a) + thouSep;
               count = count + 1;
               a = a + 2;
            }
         } else {
            var k = value.substring(0, 1) + thouSep;
            var l = j.substring(1, j.length);
            for (var count = 0; count < l.length; count++) {
               result += l.substring(count, a) + thouSep;
               count = count + 1;
               a = a + 2;
            }
            result = k + result;
         }
      }
      result = result + i;
      if (x.length > 1 && params.decPoint > 0) {
         result = result + params.decSep + x[1].substring(0, params.decPoint);
         if (x[1].length < params.decPoint) {
            for ( i = 0; i < (params.decPoint - x[1].length); i++) {
               result = result + 0;
            }
         }
      } else {
         for ( i = 0; i < params.decPoint; i++) {
            if (i == 0)
               result = result + params.decSep + "0";
            else
               result = result + "0";
         }
      }
      return result;
   }, 
   /**
    * The API is used to apply number format on a value. It returns the formatted value as the output.
    * @param {object} params params includes
    * <br>value: Value on which number format needs to be applied.
    * <br>decimalPoints: Number of decimals that should be retained in the value. If there is no need to truncate the decimals, then this parameter can be sent as null.
    * <br>mask: This can be any of the following i)MILLION ii)LAKH
    * <br>decimalSep: This can be any value dot (.) , comma (,) , space( ) etc.
    * <br>displayAsLiteral: Used to format numbers to display in terms of number or literal (Ex. 10,00,000 or 1L). Takes Value “Y” or “N”.
    * @example
    * var obj = {};
    * obj.value = amt;
    * obj.mask = “MILLION”;
    * obj.decimalPoints = 2;
    * obj.decimalSep = “.”
    * var result = apz.formatNumber (obj);
    */
   formatNumber : function(params) {
	   /* Params contains the below attributes
	       *** value, decimalSep, decimalPoints, mask, displayAsLiteral(Y/N) ***
	       * Response contains below value
	       *** Value ***
	   */
      var lvalue = "";
      if (params.decimalSep == ".") {
         thousandSep = ",";
      } else {
         thousandSep = ".";
      }
      // // Check if number has to be displayed as Literal
      if (!this.isNull(params.value)) {
         var decSep = params.decimalSep;
         var maskSep = thousandSep;
         var value = parseFloat(params.value);
         value = value.toString();
         /* Handling Big Numbers */
         if(value !== params.value.toString()) {
            value = params.value.toString();
         }
         /* Handling Big Numbers ends*/
         var lvalue = value.trim();
         var minus = lvalue.split(/-/g).length - 1;
         if (minus > 0) {
            lvalue = lvalue.substring(1, lvalue.length);
         }
         if (params.displayAsLiteral == "Y") {
            if (params.mask == "MILLION") {
               if (lvalue >= 10000000)
                  lvalue = (lvalue / 1000000000).toFixed(1) + 'B';
               else if (lvalue >= 100000)
                  lvalue = (lvalue / 1000000).toFixed(1) + 'M';
               else if (lvalue >= 1000)
                  lvalue = (lvalue / 1000).toFixed(1) + 'K';
            } else if (params.mask == "LAKH") {
               if (lvalue >= 10000000)
                  lvalue = (lvalue / 10000000).toFixed(1) + 'C';
               else if (lvalue >= 100000)
                  lvalue = (lvalue / 100000).toFixed(1) + 'L';
               else if (lvalue >= 1000)
                  lvalue = (lvalue / 1000).toFixed(1) + 'K';
            }
         } else {
            var args = {};
            args.value = lvalue;
            args.decSep = decSep;
            args.decPoint = params.decimalPoints;
            if (params.mask == "MILLION") {
               lvalue = this.million(args);
            } else if (params.mask == "LAKH") {
               lvalue = this.lakh(args);
            }
         }
         if (minus > 0) {
            lvalue = "-" + lvalue;
         }
      }
      return lvalue;
   }, formatNumberControl : function(obj) {
      var id = obj.id;
      var elmData = null;
      var recNo = -1;
      recNo = this.getObjRowNumber(obj);
      id = this.getObjIdWORowNumber(obj);
      try {
         elmData = this.scrMetaData.elmsMap[id];
      } catch (e) {
         elmData = null;
      }
      var value = this.getObjValue(obj);
      if (!this.isNull(value)) {
         //RADS - Unformat and Format Number..Check Should be based on whether
         // the value is Changed or Not..
         //If its not Changed, we should not format because its already formatted
         /// TBC - onfocus set obj.oldval = obj.value to find previous value?
         var errorMsg = apz.val.validateNumber(obj, value,elmData.displayAsLiteral);
         if (this.isNull(errorMsg)) {
            this.val.removeClass(obj);
            var params = {};
            params.value = value;
            params.decimalSep = this.decimalSep;
            params.mask = this.numberMask;
            params.displayAsLiteral = elmData.displayAsLiteral;
			var decimalPoints = this.getDecimalPoints(elmData,recNo);
			params.decimalPoints = decimalPoints;
			   value = this.unFormatNumber(params);
               params.value = value;
		      value = this.formatNumber(params);
			   this.setObjValue(obj,value);
		 } else {
            if ($(obj).hasClass("iosnumber")) {
               obj.value = '';
            }
            var params = {"code":errorMsg}
            this.val.addClass(obj);
            this.dispMsg(params);
		 }
      }
   }, formatNumberUIControl : function(obj) {
      var id = obj.id;
      var elmData = null;
      var recNo = -1;
      recNo = this.getObjRowNumber(obj);
      id = this.getObjIdWORowNumber(obj);
      try {
         elmData = this.scrMetaData.elmsMap[id];
      } catch (e) {
         elmData = null;
      }
      var value = this.getObjValue(obj);
      if (!this.isNull(value)) {
         //RADS - Unformat and Format Number..Chekc Should be based on whether
         // the value is Changed or Not..
         //If its not Changed, we shouldnot format because its alreay formatted
        /// TBC - onfocus set obj.oldval = obj.value to find previous value?
    	 value = this.unFormatNumber({"decimalSep":this.decimalSep,"displayAsLiteral":elmData.displayAsLiteral,"value":value});
         if (apz.val.isNumber(value)) {
           var decimalPoints = this.getDecimalPoints(elmData,recNo);
            var params = {};
            params.value = value;
            params.decimalSep = this.decimalSep;
            params.decimalPoints = decimalPoints;
            params.mask = this.numberMask;
            params.displayAsLiteral = elmData.displayAsLiteral;
           value = this.unFormatNumber(params);
            value = this.formatNumber(params);
           this.setObjValue(obj,value);
         } else {
            var params = {"code":"APZ-CNT-078"}
            this.dispMsg(params);
         }
      }
   },
   /**
    * The API is used to remove number formatting on a value. It returns the unformatted value as the output.
    * @param {object} params params includes
    * <br>value: Value on which number format needs to be applied.
    * <br>decimalSep: This can be any value dot (.) , comma (,) , space( ) etc.
    * <br>displayAsLiteral: Used to format numbers to display in terms of number or literal (Ex. 10,00,000 or 1L). Takes Value “Y” or “N”.
    * @example
    * var obj = {};
    * obj.value = amt;
    * obj.decimalSep = “,”;
    * var result = apz.unFormatNumber(obj);
    */
   unFormatNumber : function(params) {
	   /* Params contains the below attributes
	       *** value, decimalSep, displayAsLiteral ***
	       * Response contains below value
	       *** Value ***
	   */
      if (params.decimalSep == ".") {
         thousandSep = ",";
      } else {
         thousandSep = ".";
      }
      var lvalue = params.value;
      if (!this.isNull(params.value)) {
         if(!this.isNull(params.displayAsLiteral)){
         if (params.displayAsLiteral == "Y") {
            var lastChar = lvalue.slice(-1);
            var val = lvalue.split(lastChar)[0];
            if (!$.isNumeric(lastChar)) {
               lastChar = lastChar.toUpperCase();
               val = Number(val);
               if (lastChar == "C") {
                  lvalue = val * 10000000;
               } else if (lastChar == "L") {
                  lvalue = val * 100000;
               } else if (lastChar == "K") {
                  lvalue = val * 1000;
                  lvalue = numeral().unformat(lvalue);
               } else if (lastChar == "B") {
                  lvalue = val * 1000000000;
               } else if (lastChar == "M") {
                  lvalue = val * 1000000;
               } else {
                  lvalue = params.value;
               }
            }
         } else {
            lvalue = params.value.toString();
            var regex = null;
            if (thousandSep == ".") {
               regex = new RegExp(thousandSep, "g");
               lvalue = lvalue.replace(/\./g, '');
               regex = new RegExp(",", "g");
               lvalue = lvalue.replace(regex, '.');
            } else {
               regex = new RegExp(thousandSep, "g");
               lvalue = lvalue.replace(regex, '');
            }
         }
      }
   }
      return lvalue;
   }, 
   /**
    * This API helps show the configured message.
    * @param {object} params params include
    * <br>appId: The application ID from which the call to the API is made.
    * <br>code: The error code that is maintained at the project level. Based on the user language, Appzillon will automatically pick up the description maintained for the error code in that language and display to the user.
    * <br>args: The <#> separated message.
    * <br>callBack: The callback function that is invoked after the alert window is closed.
    * <br>callBackObj: The object that contains the callback function.
    * <br>message: The string containing user defined message.
    * <br>msgtype: The display type for message. Can be "LOG" or "INLINEMESSAGE"
    * <br>type: The type of message (such as Error (“E”), Success(“S”))
    * <br>closeonclick: Option to close log messages on click.
    * <br>blogposition: Position where log messages will appear.
    * <br>title: The message title.
    * <br>label: The message label.
    * @example
    * var val = apz.getElmValue(“element_inputbox_1”);
	 * var objlabel =apz.getElmValue (“element_inputbox_1_lbl”);
	 * var params = {};
	 * params.code = "CUST-VAL-010";
	 * params.args = val+"<#>"+objlabel;
    * params.callBackObj = this;
    * params.callBack = this.displayMessageCallBack;
    * apz.dispMsg(params);
    */
   // Bug 57266 - Corporate Servicing : Alert messages are not working
   // With the latest alertify changes_AB // updated again_29Apr2022_AB
   dispMsg: function (params) {
      ////Register Call Back
      /* Params contains the below attributes
      *** appId, code, callBack, callBackObj, args, message, msgtype, type, closeonclick, logposition, title, labels ***
      * Response contains below attributes
      *** choice, val ***
      */
      Apz.dispMsgCBParams = params;
      var labelArr = [];
      var desc = "";
      var placeHolder = ""
      var type = "E";
      var typeCls = '';
      var cssClasses = null;
      var image = null;
      var lables = {};
      var closableByDimmer = true;
      var msgType = params.msgtype;
      var widgetId = params.widgetId;
      if (alertify.okBtn || alertify.cancelBtn) {
         preMigration = true;
      }
      if (params.ignoreDispMsg) {
         //Apz.dispMsgCB();
         if (typeof params.callBack == "function") {
            if (params.callBackObj) {
               params.callBack.call(params.callBackObj, params);
            } else {
               params.callBack(params);
            }
         }
         return;
      }
      try {
         apz.dispMsgCleanup();
         if (!this.isNull(params.message) || msgType == "INLINEMESSAGE") {
            desc = params.message;
            if (params.type) {
               type = params.type;
            }
         } else {
            var appId = params.appId;
            if (this.isNull(appId)) {
               appId = this.currAppId;
            }
            desc = this.msgs[appId] ? this.msgs[appId][params.code] : "";
            if (this.isNull(desc)) {
               desc = this.msgs[this.appId][params.code];
            }
            type = desc.substring(0, 1);
            desc = desc.substring(1);
            if (!this.isNull(params.args)) {
               var argsLen = 0;
               var args = params.args.split("<#>");
               argsLen = args.length;
               for (var a = 0; a < argsLen; a++) {
                  arg = args[a];
                  if (arg.indexOf("<#>") >= 0) {
                     arg = "Invalid Argument";
                  }
                  ind = desc.indexOf("<#>");
                  if (ind >= 0) {
                     desc = desc.substring(0, ind) + arg + desc.substring(ind + 3);
                  }
               }
            }
         }
      } catch (err) {
         desc = "Error Code " + params.code + " is Missing. Please Contact Helpdesk.";
         type = "E";
      }


      if (msgType == "LOG") {
         var closeOnClick = params.closeonclick;
         var logMsgPosition = params.logposition;
         var msgdelay = params.msgdelay;
         if (apz.isNull(msgdelay)) {
             msgdelay = 5;
         }
         if (apz.isNull(logMsgPosition)) {
             logMsgPosition = "bottom-right";
         }
         alertify.set('notifier', 'position', logMsgPosition);
         if (type == "S") {
             var msgsucc = alertify.success(desc, msgdelay, Apz.dispMsgCB);
             if (!closeOnClick) {
                 msgsucc.ondismiss = function () { return closeOnClick; };
                 setTimeout(function () { closeOnClick = true; msgsucc.dismiss() }, msgdelay * 1000);
             }
     
         }
         else if (type == "E") {
             var msgerr = alertify.error(desc, msgdelay, Apz.dispMsgCB);
             if (!closeOnClick) {
                 msgerr.ondismiss = function () { return closeOnClick; };
                 setTimeout(function () { closeOnClick = true; msgerr.dismiss() }, msgdelay * 1000);
             }
         }
         else if (type == "W") {
             var msgwar = alertify.warning(desc, msgdelay, Apz.dispMsgCB);
             if (!closeOnClick) {
                 msgwar.ondismiss = function () { return closeOnClick; };
                 setTimeout(function () { closeOnClick = true; msgwar.dismiss() }, msgdelay * 1000);
             }
         }
         // .ajs-message.ajs-custom { color: #31708f;  background-color: #d9edf7;  border-color: #31708f; }
         else {
             var msgcus = alertify.notify(desc, 'custom', msgdelay, Apz.dispMsgCB);
             if (!closeOnClick) {
                 msgcus.ondismiss = function () { return closeOnClick; };
                 setTimeout(function () { closeOnClick = true; msgcus.dismiss() }, msgdelay * 1000);
             }
         }
     } else if (msgType == "INLINEMESSAGE") {
         var parentElm;
         var $msgObj;
         var msgClass;
         if (type == "S") {
            $msgObj = $("#apz-message-success");
            msgClass = "suc";
         } else if (type == "W") {
            $msgObj = $("#apz-message-warning");
            msgClass = "war";
         } else if (type == "E") {
            $msgObj = $("#apz-message-error");
            msgClass = "err";
         } else {
            $msgObj = $("#apz-message-info");
            msgClass = "inf";
         }
         if (params.animClass) {
            msgClass = msgClass + " animated " + params.animClass;
         }
         if (params.parent != undefined) {
            parentElm = $(document.getElementById(params.parent));
            var cln = $msgObj[0].cloneNode(true);
            if (widgetId != undefined) {
               cln = document.getElementById(widgetId);
            }
            parentElm[0].appendChild(cln);
            $msgObj = $(cln);
         }
         if (!this.isNull(params.title) && type == "I") {
            $msgObj.find("p").html(desc);
            desc = params.title;
         }
         $msgObj.find("h4").html(desc);
         $msgObj.removeClass("sno");
         if (widgetId != undefined) {
            $msgObj.addClass('message pnt-ialt ' + msgClass);
         }
         var delay = params.delay ? params.delay : 5000;
         setTimeout(function () {
            $msgObj.addClass("sno");
            if (parentElm && parentElm.length != 0) {
               if (widgetId != undefined) {
                  parentElm.find($msgObj).addClass('sno');
                  $msgObj.removeClass('message pnt-ialt ' + msgClass);
               } else {
                  parentElm.find($msgObj).remove();
               }
            }
         }, delay);
      } else {
         // //Update Button Lables...
         if (!this.isNull(params.labels) && (params.labels.length >= 1)) {
            lables = params.labels;
            if (lables.length == 1) {
               labelArr.push(
                  {
                     text: this.getLabel(lables[0])
                  }
               );
               labelArr.forEach(function (obj) {
                  if (obj.text === apz.getLabel(lables[0])) {
                     obj.className = alertify.defaults.theme.ok;
                     obj.scope = 'primary';
                  }
               });
            } else if (lables.length == 2) {
               labelArr.push(
                  {
                     text: this.getLabel(lables[0])
                  },
                  {
                     text: this.getLabel(lables[1])
                  }
               );
               labelArr.forEach(function (obj) {
                  if (obj.text === apz.getLabel(lables[0])) {
                     obj.className = alertify.defaults.theme.ok;
                     obj.scope = 'primary';
                  } else if (obj.text === apz.getLabel(lables[1])) {
                     obj.className = alertify.defaults.theme.cancel;
                  }
               });
            }
         } else {
            if (type === "E" || type === "S" || type === "I" || type === "W") {
               labelArr.push(
                  {
                     text: this.getLabel('LIT_OK')
                  }
               );
               labelArr.forEach(function (obj) {
                  if (obj.text === apz.getLabel('LIT_OK')) {
                     obj.className = alertify.defaults.theme.ok;
                     obj.scope = 'primary';
                  }
               });
            } else {
               labelArr.push(
                  {
                     text: this.getLabel('LIT_OK')
                  },
                  {
                     text: this.getLabel('LIT_CANCEL')
                  }
               );
               labelArr.forEach(function (obj) {
                  if (obj.text === apz.getLabel('LIT_OK')) {
                     obj.className = alertify.defaults.theme.ok;
                     obj.scope = 'primary';
                  } else if (obj.text === apz.getLabel('LIT_CANCEL')) {
                     obj.className = alertify.defaults.theme.cancel;
                  }
               });
            }
         }
         // backdrop onclick handling
         if (params.closableByDimmer == false) {
            closableByDimmer = false;
         }

         // Generic alert
         var title = '<p>&nbsp;</p>';
         if (type === 'S' && (this.getLabel('LIT_SUCCESS') !== 'LIT_SUCCESS')) {
            title = this.getLabel('LIT_SUCCESS');
         } else if (type === 'C' && (this.getLabel('LIT_CONFIRM') !== 'LIT_CONFIRM')) {
            title = this.getLabel('LIT_CONFIRM');
         } else if (type === 'E' && (this.getLabel('LIT_ERROR') !== 'LIT_ERROR')) {
            title = this.getLabel('LIT_ERROR');
         } else if (type === 'I' && (this.getLabel('LIT_INFO') !== 'LIT_INFO')) {
            title = this.getLabel('LIT_INFO');
         } else if (type === 'W' && (this.getLabel('LIT_WARNING') !== 'LIT_WARNING')) {
            title = this.getLabel('LIT_WARNING');
         } else if (type === 'P' && (this.getLabel('LIT_PROMPT') !== 'LIT_PROMPT')) {
            title = this.getLabel('LIT_PROMPT');
         }
         if (!this.isNull(params.title)) {
            title = params.title;
         }
         if (alertify.genAlert) {
            delete alertify.genAlert;
         }
         //define a new Generic Alert
         if (type != 'P' && !alertify.genAlert) {
            alertify.dialog('genAlert', function factory() {
               return {
                  build: function () {
                     this.setHeader(title);
                  },
                  callback: Apz.dispMsgCB,
                  setup: function () {
                     return {
                        buttons: labelArr,
                        //  fix for Popup message issue
                        focus: {
                           element: 0
                        },
                        options: {
                           closableByDimmer
                        }
                     }
                  },
                  hooks: {
                     onshow: function () {
                        document.querySelector('.ajs-commands').parentElement.removeChild(document.querySelector('.ajs-commands'));
                     },
                     // fix for the backdrop onclick issue_AB
                     onclose: apz.dispMsgCleanup
                  }
               };
            }, true, 'alert');
         }

         if (type != 'P') {
            alertify.genAlert('<p>' + desc + '</p>').set('autoReset', true);;
         } else if (type == 'P') {
            if (document.querySelector('.ajs-commands')) {
               document.querySelector('.ajs-commands').parentElement.removeChild(document.querySelector('.ajs-commands'));
            }
            alertify.prompt(title, desc, '', Apz.dispMsgCB, Apz.dispMsgCB);
         }
         if (type === "C") {
            document.querySelector('.ajs-content').classList.add('confirm');
         } else if (type === "P") {
            document.querySelector('.ajs-content').classList.add('prompt');
         } else if (type === "S") {
            document.querySelector('.ajs-content').classList.add('success');
         } else if (type === 'W') {
            document.querySelector('.ajs-content').classList.add('warning');
         } else if (type === 'I') {
            document.querySelector('.ajs-content').classList.add('info');
         } else if (type === 'E') {
            document.querySelector('.ajs-content').classList.add('error');
         }
      }
      if( document.querySelector('.ajs-content')) {
         document.querySelector('.ajs-content').style.top = '20%';
         $('.ajs-content').children().css('margin', '38px');
      }
   }, 
   dispMsgCleanup: function () {
      while ($('.alertify').length > 0) {
         document.querySelector('.alertify').parentElement.removeChild(document.querySelector('.alertify'));
      }
   },
   /**
    * This API is used to hide the control.
    * @param {string} id  The HTML ID of the element whose control needs to be hidden.
    * @example
    * apz.hide(id);
    * apz.hide("element_id");
    */
   hide : function(id) {
	   /* Params contains the below value
	       *** id(DOM element ID) ***
	   */
      var obj = document.getElementById(id);
      if (!this.isNull(obj)) {
         $(obj).addClass("sno");
      }
   }, 
   /**
    * This API will be used for hiding the given  container like form, list, chart, navbar,table etc.
    * @param {string} id  The HTML ID of the container that needs to be hidden.
    * @example
    * apz.hideContainer(id);
    * apz.hideContainer("container_form_1");
    */
   hideContainer : function(id) {
	   /* Params contains the below value
	       *** id ***
	   */
      var obj;
      if(!this.isNull(this.scrMetaData.containersMap[id])) {
         obj = document.getElementById(id);
      } else if(!this.isNull(this.scrMetaData.chartsMap[id])) {
         obj = document.getElementById(id+"_chart");
      } else if(!this.isNull(this.scrMetaData.gaugesMap[id])) {
         obj = document.getElementById(id+"_gauge");
      }
        if(!this.isNull(obj)) {
            $(obj).addClass("sno");
        }  
   }, 
   /**
    * This API is used to hide the element and the label associated with it as well.
    * @param {string} pid  The HTML ID of the element that needs to be hidden.
    * @example
    * apz.hideElement(id);
    * apz.hideElement("element_id");
    */
   hideElement : function(pid) {
        /* Params contains the below value
         *** id ***
         */
        var id = pid;
        var tdid;
        var elementData = this.scrMetaData.elmsMap[id];
        if (!this.isNull(elementData)) {
            tdid = "td_" + id;
        } else {
            var id = this.getElmObjIdWORowNumber(document.getElementById(id));
            elementData = this.scrMetaData.elmsMap[id];
            if (!this.isNull(elementData)) {
                tdid = "td_" + pid;
            } else {
                id = id.slice(3);
                elementData = this.scrMetaData.elmsMap[id];
                tdid = pid;
            }
        }
        if (!this.isNull(elementData)) {
            var containerId = elementData.container;
            var containerData = this.scrMetaData.containersMap[containerId];
            var containerType = containerData.type;
            if (containerType == "FORM") {
                obj = document.getElementById(id + "_ul");
            } else if (containerType == "LIST" || containerType == "NAVBAR") {
                var elmType = elementData.type;
                /*if (containerData.multiRec == "N") {
                    obj = document.getElementById(id);
                } else {
                    obj = $("#" + pid).parent('span');
                }*/
                //added specific conditions for each widget
                var spanElms = ["DROPDOWN", "INPUT", "INPUTWITHBUTTON", "SLIDER", "TAGS", "TEXTAREA"];
                if (spanElms.indexOf(elmType) != -1) {
                    obj = $("#" + pid).parents('span.ecn:first')[0];
                } else if (elmType == "FILEBROWSER" || elmType == "STEPPER") {
                    obj = $("#" + pid).parents('ul:first')[0];
                } else if (elmType == "PROGRESSBAR") {
                    obj = $("#" + pid).parents('div.ecn:first')[0];
                } else if (elmType == "CHECKBOX") {
                    obj = $("#" + pid).parents('span.etb-chek:first')[0];
                } else {
                    obj = document.getElementById(pid);
                }
            } else {
                obj = document.getElementById(tdid);
            }
            if (!this.isNull(obj)) {
                $(obj).addClass("sno");
            }
        } else {
            $("#" + pid).addClass("sno");
        }
        //for citi each element label is represented by label_title. Hide that as well..
        var unmapelm = apz.isDMLElm(id);
        if (!unmapelm) {
            var obj2 = document.getElementById(id + "_title");
            if (!this.isNull(obj2)) {
                $(obj2).addClass("sno");
            }
        } else {
            var elmId = this.getElmName(pid);
            var titleId = this.currAppId + "__" + this.currScr + "_Scr" + "__" + elmId + "_title";
            var obj3 = document.getElementById(titleId);
            if (!this.isNull(obj3)) {
                $(obj3).addClass("sno");
            }
        }
    }, 
    /**
     * This API is used to hide the entire column (across multiple rows)  corresponding to a particular element inside a table.
     * @param {string} id  The HTML ID of the column that needs to be hidden.
     * @example
     * apz.hideColumn(id);
     * apz.hideColumn("dbservice__i__tbAccountMast__acno");
     */
    hideColumn : function(id) {
	   /* Params contains the below value
	       *** id ***
	   */
      var tdid,indx,recs;
      var elementData = this.scrMetaData.elmsMap[id];
         if(!this.isNull(elementData)) {
            tdid = "td_"+id+"_0";
         } else {
            var id = this.getElmObjIdWORowNumber(document.getElementById(id));
            elementData = this.scrMetaData.elmsMap[id];
            if(!this.isNull(elementData)) {
               tdid = "td_"+id+"_0";
            } else {
               id = id.slice(3);
               elementData = this.scrMetaData.elmsMap[id];
               tdid = "td_"+id+"_0";
            }
         }   
      var containerId = elementData.container;
      var containerData = this.scrMetaData.containersMap[containerId];
      var containerType = containerData.type;
      if(containerType == "LIST" || containerType == "NAVBAR") {
         var $id = $("#"+id+"_0");
         recs = $id.closest('li').children('span');
         indx = recs.index($id.parent('span').parent());
         $("#"+containerId).find('ul.pri').find('li').children(":nth-child("+(indx+1)+")").addClass("sno");
      } else if(containerType == "TABLE") {
         var $tdid = $("#"+tdid);
         recs = $tdid.parent('tr').find('td');
         indx = recs.index($tdid);
         $("#"+containerId+"_table").find('tr').children(":nth-child("+(indx+1)+")").addClass("sno");
      }
   }, 
   /**
    * This API is used to Show the hidden control.
    * @param {string} id This API is used to Show the hidden control.
    * @example
    * apz.show (id);
    * apz.show("element_id");
    */
   show : function(id) {
	   /* Params contains the below value
	       *** id(DOM element ID) ***
	   */
      var obj = document.getElementById(id);
      if (!this.isNull(obj)) {
         $(obj).removeClass("ssp sno");
      }
   }, 
   /**
    * This API is used to show hidden containers.
    * @param {string} id  The HTML ID of the container that needs to be shown.
    * @example
    * apz.showContainer(id);
    * apz.showContainer("container_form_1");
    */
   showContainer : function(id) {
	   /* Params contains the below value
	       *** id ***
	   */
      var obj;
      if(!this.isNull(this.scrMetaData.containersMap[id])) {
         obj = document.getElementById(id);
      } else if(!this.isNull(this.scrMetaData.chartsMap[id])) {
         obj = document.getElementById(id+"_chart");
      } else if(!this.isNull(this.scrMetaData.gaugesMap[id])) {
         obj = document.getElementById(id+"_gauge");
      }
        if(!this.isNull(obj)) {
            $(obj).removeClass("sno");
        } 
   }, 
   /**
    * This API is used to show the hidden element and the label associated with it as well.
    * @param {string} pid The HTML ID of the element that needs to be shown.
    * @example
    * apz.showElement(id);
    * apz.showElement("id_of_element");
    */
   showElement: function(pid) {
        var id = pid;
        var tdid;
        var elementData = this.scrMetaData.elmsMap[id];
        if (!this.isNull(elementData)) {
            tdid = "td_" + id;
        } else {
            var id = this.getElmObjIdWORowNumber(document.getElementById(id));
            elementData = this.scrMetaData.elmsMap[id];
            if (!this.isNull(elementData)) {
                tdid = "td_" + pid;
            } else {
                id = id.slice(3);
                elementData = this.scrMetaData.elmsMap[id];
                tdid = pid;
            }
        }
        if (!this.isNull(elementData)) {
            var containerId = elementData.container;
            var containerData = this.scrMetaData.containersMap[containerId];
            var containerType = containerData.type;
            if (containerType == "FORM") {
                obj = document.getElementById(id + "_ul");
            } else if (containerType == "LIST" || containerType == "NAVBAR") {
                var elmType = elementData.type;
                /*if (containerData.multiRec == "N") {
                    obj = document.getElementById(id);
                } else {
                    obj = $("#" + pid).parent('span');
                }*/
                //added specific conditions for each widget
                var spanElms = ["DROPDOWN", "INPUT", "INPUTWITHBUTTON", "SLIDER", "TAGS", "TEXTAREA"];
                if (spanElms.indexOf(elmType) != -1) {
                    obj = $("#" + pid).parents('span.ecn:first')[0];
                } else if (elmType == "FILEBROWSER" || elmType == "STEPPER") {
                    obj = $("#" + pid).parents('ul:first')[0];
                } else if (elmType == "PROGRESSBAR") {
                    obj = $("#" + pid).parents('div.ecn:first')[0];
                } else if (elmType == "CHECKBOX") {
                    obj = $("#" + pid).parents('span.etb-chek:first')[0];
                } else {
                    obj = document.getElementById(pid);
                }

            } else {
                obj = document.getElementById(tdid);
            }
            if (!this.isNull(obj)) {
                $(obj).removeClass("sno");
            }
        } else {
            $("#" + pid).removeClass("sno");
        }
    }, 
    /**
     * This API is used to show  the entire hidden column (across multiple rows)  corresponding to a particular element inside a table.
     * @param {string} id The HTML ID of the column that needs to be shown.
     * @example
     * apz.showColumn(id);
     * apz.showColumn("dbservice__i__tbAccountMast__acno");
     */
    showColumn : function(id) {
	  /* Params contains the below value
       *** id ***
      */
      var tdid,indx,recs;
      var elementData = this.scrMetaData.elmsMap[id];
         if(!this.isNull(elementData)) {
            tdid = "td_"+id+"_0";
         } else {
            var id = this.getElmObjIdWORowNumber(document.getElementById(id));
            elementData = this.scrMetaData.elmsMap[id];
            if(!this.isNull(elementData)) {
               tdid = "td_"+id+"_0";
            } else {
               id = id.slice(3);
               elementData = this.scrMetaData.elmsMap[id];
               tdid = "td_"+id+"_0";
            }
         }   
      var containerId = elementData.container;
      var containerData = this.scrMetaData.containersMap[containerId];
      var containerType = containerData.type;
      if(containerType == "LIST" || containerType == "NAVBAR") {
         var $id = $("#"+id+"_0");
         recs = $id.closest('li').children('span');
         indx = recs.index($id.parent('span').parent());
         $("#"+containerId).find('ul.pri').find('li').children(":nth-child("+(indx+1)+")").removeClass("sno");
      } else if(containerType == "TABLE") {
         var $tdid = $("#"+tdid);
         recs = $tdid.parent('tr').find('td');
         indx = recs.index($tdid);
         $("#"+containerId+"_table").find('tr').children(":nth-child("+(indx+1)+")").removeClass("sno");
      }
   }, getLabel : function(title,appId) {
	   /* Params contains the below value
	       *** title, appId ***
	       * Response contains below value
	       *** label ***
	   */
      var label = title;
      if (!this.isNull(title)) {
         try {
             if(this.isNull(appId)){
                appId = this.currAppId;
             }
             label = this.lits[appId] ? this.lits[appId][title] : "";
             //Added multi-language support for getting labels
             if (this.isNull(label)) {
                 label = this.lits[appId][this.language][title];
             }
             if (this.isNull(label)) {
                 label = this.lits[this.appId][title];
             }
             if (this.isNull(label)) {
                 label = this.lits[this.appId][this.language][title];
             }
             if (this.isNull(label)) {
                 label = title;
             }
         } catch (e) {
            label = title;
         }
      }
      return label;
   }, 
   /**
    * This API is used to replace the first occurrence of the string with given data based on key.
    * @param {object} params params include
    *  <br>string: This contains the content that needs to modified.
    *  <br>key: The key that will be replaced.
    *  <br>replaceData: This is the data to be replaced.
    
    * @example
    * var params = {};
    *  params.string = "Accept the change and be the change";
    *  params.key = “change”;
    *  params.replaceData = “human”
    *  apz.replace(params);
    */
   replace : function(params) {
	   /* Params contains the below attributes
	       *** string, key, replaceData ***
	       * Response contains below value
	       *** replacedString ***
	   */
      var lstring = params.string;
      lstring = lstring.replace(new RegExp('' + params.key + ''), params.replaceData);
      return lstring;
   }, 
   /**
    * This API is used to replace all occurrences of the string in the content with given data based on key throughout the string.
    * @param {object} params params include
    *<br>string: This contains the content that needs to modified.
    *<br>key: The key that will be replaced.
    *<br>replaceData: This is the data to be replaced.
    * @example
    * var params = {};
    * params.string = "Accept the change and be the change";
    * params.key = “change”
    * params.replaceData = “human”
    * apz.replaceAll(params);
*/
   replaceAll : function(params) {
	   /* Params contains the below attributes
	       *** string, key, replaceData ***
	       * Response contains below value
	       *** replacedString ***
	   */
      var lstring = params.string;
      lstring = lstring.replace(new RegExp('' + params.key + '', 'g'), params.replaceData);
      return lstring;
   }, 
   /**
    * This API is used to apply mask on values based on a format.
    * @param {string} maskFormat The maskformat which needs to be applied on the value .The maskformat length should be same as value length.
    * 
    * @param {string} value Value that needs to be masked.
    * @example
    * var result = apz.getMaskedValue('999XXX','123456');
    */
   getMaskedValue : function(maskFormat, value) {
	   /* Params contains the below values
	       *** maskFormat, value ***
	       * Response contains below value
	       *** maskedValue ***
	   */
      var elemMaskFormatLength = maskFormat.length;
      var elemMaskFormat = maskFormat;
      var lmaskFormat = elemMaskFormat.replace(/[^\w\s]/gi, '');
      var lvalue = value;
      var j = 0, i = 0;
      var pattern = new RegExp(/[~`!#$%\^&*+=\-\[\]\\';,/{}|\\":<>\?]/);
      if (value.length <= lmaskFormat.length) {
         while (i < elemMaskFormatLength) {
            if (isNaN(elemMaskFormat[j]) && !pattern.test(elemMaskFormat[j])) {
               j++;
               i++;
            } else if (pattern.test(elemMaskFormat[j])) {
               j++;
            } else {
               elemMaskFormat = elemMaskFormat.split('');
               elemMaskFormat[j] = lvalue[i];
               elemMaskFormat = elemMaskFormat.join('');
               j++;
               i++;
            }
         }
      } else {
         elemMaskFormat = lvalue;
      }
      return elemMaskFormat;
   }, 
   /**
    * This API is used to check whether the given object is empty or not. If empty returns true else returns false.
    * @param {object} obj - This is the object that needs to be checked whether its empty or not.
    * @example
    * var obj = {}
    * obj.temp = "Sample";
    * if(!apz.isObjectEmpty(obj)){
    *    alerts("Object is not Null");
    *  }
    */
   isObjectEmpty : function(obj) {
	   /* Params contains the below Value
	       *** obj ***
	       * Response contains below value
	       *** boolean ***
	   */
      return jQuery.isEmptyObject(obj);
   }, 
   /**
    * This API is used to change the application theme at runtime.
    * @param {string} newTheme The theme of the application set at the project level.
    * @example
    * apz.changeStyleTheme (theme);
    * apz.changeStyleTheme ("AppzillonTheme");
    */
   changeStyleTheme : function(newTheme) {
	   /* Params contains the below value
	       *** newTheme ***
	   */
      if (this.theme != newTheme){
         var linkArr = $('link');            
         //Avoiding unnecessary file download if base themes are same--krish-09.07//
         if(this.baseThemesMap[this.theme] !== this.baseThemesMap[newTheme]){
             var lhref = linkArr[0].href;
             lhref = lhref.replace("styles/themes/" + this.baseThemesMap[this.theme] + "/css/" + this.baseThemesMap[this.theme] + ".css", "styles/themes/" + this.baseThemesMap[newTheme] + "/css/" + this.baseThemesMap[newTheme] + ".css");
             $(linkArr[0]).attr('href', lhref);
         }
         for (var i = 1; i < linkArr.length; i++){
            var lhref = linkArr[i].href;
            lhref = lhref.replace("styles/themes/" + this.theme, "styles/themes/" + newTheme);
            $(linkArr[i]).attr('href', lhref);
         }
         this.theme = newTheme;
      }
   }, changeLanguage : function(langCode, appId) {
	   /* Params contains the below values
	       *** langCode, appId ***
	   */
      if (this.language != langCode) {
         this.language = langCode;
         if(this.isNull(appId)){
            appId = this.currAppId;
         }
         var def = this.getFile(this.getConfigPath(this.appId) + "/prjdef.json");
         def = JSON.parse(def);
         this.msgs[appId] = def.msgs[this.language];
         this.lits[appId] = def.lits[this.language];
      }
   }, 
   /**
    * This API is used to populate given options to the dropdown object. It clears all pre-existing values before populating.
    * @param {Object} obj -This is the DOM object where options needs to be populated.
    * @param {Array} options -This is an array of objects containing 'val' and 'desc' for value and description of the options respectively.
    * @example
    * var options = [{"val":"Android","desc":"Android"},
    * 	            {"val":"iOS","desc":"iOS"}];
	 * apz.populateDropdown(document.getElementById("drop"),options);
    * 
    */
   populateDropdown : function(obj, options) {
	   /* Params contains the below values
	       *** obj, options(Array of val,desc object) ***
	   */
      var opt = "";
      var variation = "";
      this.clearHtml(obj.id);
      var id = this.getObjIdWORowNumber(obj);
      var rowNo = this.getObjRowNumber(obj);
      var dropdownObj = this.copyJSONObject(this.scrMetaData.uiInitsMap[id]);
      if (dropdownObj) {
         dropdownObj[0] = obj.id;
         if (rowNo!=-1 && rowNo!=0) {
            var objId = id+"_"+rowNo;
            dropdownObj[0] = objId;
         }
         variation = dropdownObj[6];
      }else{
         variation = "filterTable";
      }
      var params = {};
      params.obj = obj;
      params.options = options;
      params.dropdownObj = dropdownObj;
      if ((variation == "" || variation == "SIMPLE") && obj.tagName=="INPUT") {
         var childUl = document.createElement('ul');
         for (var i = 0; i < options.length; i++) {
            //For li's Id was missing so adding new line for generating ID's
            // var opt = $("<li tabindex='0' data-value='"+options[i].val+"'>"+options[i].desc+"</li>");
             var opt = $("<li id='"+obj.id+"_option_"+options[i].desc+"' tabindex='0' data-value='"+options[i].val+"'>"+options[i].desc+"</li>");
             if (i==0) {
                $(opt).addClass("is-selected");
                $(obj).val(options[i].desc);
             }
             childUl.appendChild(opt[0]);
          }
         var appender = $(obj).siblings("div");
         $(appender[0]).children().remove();
         $(appender[0]).append(childUl);
         this.initDropDownObj(dropdownObj,obj);
      } else if (variation == "WITHSUBOPTIONS") {
         this.populateDropdownSublevels(params);
      } else if (variation == "MULTISELECTCHECKBOX" || variation == "MULTISELECTCHECKBOXAUTOCOMPLETE") {
         this.populateDropdownMultiselect(params);
      } else {
            if(obj.tagName=="INPUT"){
                //// CITI Enhancement - In fallback if input tag, dropdown for table column filter to be populated
                var selectTag = $("#"+obj.id+"_select")[0];
                //// CITI Enhancement - _select for backup of  options to consider after search clears visible options.
                this.clearHtml(obj.id+"_select");
                this.clearHtml(obj.id+"_so");
                var ulTag = $("#"+obj.id+"_so")[0];
                $(ulTag).data("options","");
                var optLi="",optInput="",optLabel="";
                if(!apz.isNull(selectTag) && !apz.isNull(ulTag)){
                    for (var i = 0; i < options.length; i++) {
                        opt = document.createElement('li');
                        opt.setAttribute("data-value",options[i].val);
                        opt.innerHTML = options[i].desc;
                        selectTag.appendChild(opt);
                        optLi = $("<li id=\"" +obj.id + "_option_" +options[i].desc + "\" class='form-group--checkbox' data-value=\"" +options[i].val + "\"></li>");
                        optInput = $("<input id=\"" + obj.id + "_option_" + options[i].desc + "_input" + "\" type='checkbox'  />");
                        optLabel = $("<label id=\"" + obj.id + "_option_" + options[i].desc + "_label" + "\" for=\"" + obj.id + "_option_" + options[i].desc + "_input" + "\">" + options[i].desc + "</label>");
                        ulTag.appendChild(optLi[0]);
                        $(optLi).append(optInput[0]).append(optLabel[0]);
                    }
                }
                apz.initFilterElements(obj);
                
            }else{
                for (var i = 0; i < options.length; i++) {
                    opt = document.createElement('option');
                    opt.value = options[i].val;
                    opt.innerHTML = options[i].desc;
                    obj.appendChild(opt);
                }
                if (variation == "AUTOCOMPLETE" || variation == "MULTISELECTTAGS") {
                    $(obj).siblings("span.select2").remove();
                    this.initDropDownObj(dropdownObj, obj);
                }
            }
        }
   }, populateDropdownSublevels: function(params) {
        //Expects obj, options, dropdownObj
        var optLi = "",
            optSpan = "";
        this.clearHtml(params.obj.id);
        if (params.obj.tagName == "INPUT") {
            var fragment = document.createDocumentFragment();
            var ul = '<ul id="ul0">';
            var args = {};
            args.ElmObj = params.obj;
            args.Arr = params.options;
            args.fragment = fragment;
            args.helper = { "currentUl": 0, "currentLi": '', "incrementer": 0 };
            args.html = {html:ul};
            this.createRecursiveDropdownLevels(args);
            var appender = $(params.obj).siblings("div");
            $(appender[0]).children().remove();
            $(fragment).append(args.html["html"]);
            $(appender[0]).append(fragment);
            this.initDropDownObj(params.dropdownObj, params.obj);
        }
   }, populateDropdownMultiselect : function(params) {
	   //Expects obj, options, dropdownObj
      var optLi = "", optInput= "", optLabel= "";
      this.clearHtml(params.obj.id);
      if (params.obj.tagName=="INPUT") {
         var childUl = document.createElement('ul');
         childUl.setAttribute("id",params.obj.id+"_so");        
         for (var i = 0; i < params.options.length; i++) {
            optLi = $("<li id='"+params.obj.id+"_option_"+params.options[i].desc+"' tabindex='0' class='form-group--checkbox' data-value='"+params.options[i].val+"'></li>");
            optInput = $("<input id='"+params.obj.id+"_option_"+params.options[i].desc+"_input"+"' type='checkbox'  />");
            optLabel = $("<label id='"+params.obj.id+"_option_"+params.options[i].desc+"_label"+"' for='"+params.obj.id+"_option_"+params.options[i].desc+"_input"+"'>"+params.options[i].desc+"</label>");
            
            // adde code_AB
            if (i == 0 && params.options[i].val == '') {
               optLi = $("<li id='"+params.obj.id+"_option_"+params.options[i].desc+"' tabindex='0' class='form-group--checkbox default-title' data-value='"+params.options[i].val+"'></li>");
               optInput = '';
            }
            if (i == 0) {
               if (params.options[i].val == '') {
                  $(params.obj).val(params.options[i].desc);
               } else {
                  $(params.obj).val(params.options[i].desc);
                  $(optInput).prop("checked",true);
               }
             }

             $(optLi).append(optInput[0]).append(optLabel[0]);
             childUl.appendChild(optLi[0]);
            //support for only one child level is provided
            if(!this.isNull(params.options[i].childs)&& params.options[i].childs.length > 0){
            	var childArr = params.options[i].childs;
            	var $childUl1 = document.createElement('ul');
            	for(var i=0,len=childArr.length;i<len;i++){
            		var $optLi1 = $("<li id='"+params.obj.id+"_option_child"+childArr[i].desc+"' tabindex='0' class='form-group--checkbox' data-value='"+childArr[i].val+"'></li>");
                    var $optInput1 = $("<input id='"+params.obj.id+"_option_child"+childArr[i].desc+"_input"+"' type='checkbox'  />");
                    var $optLabel1 = $("<label id='"+params.obj.id+"_option_child"+childArr[i].desc+"_label"+"' for='"+params.obj.id+"_option_child"+childArr[i].desc+"_input"+"'>"+childArr[i].desc+"</label>");
                    $($optLi1).append($optInput1[0]).append($optLabel1[0]);
                    $childUl1.appendChild($optLi1[0]);
            	}
            	childUl.appendChild($childUl1);
            }
         }
         this.selectionOfMultiSelectDropdown(params, childUl);
         this.initDropDownObj(params.dropdownObj,params.obj);
      }
   },
   selectionOfMultiSelectDropdown : function(params, childUl){
      //added by_AS
      //Multi-select search field enhancement_AS
     let appender;
       if(params.dropdownObj[6] == "MULTISELECTCHECKBOXAUTOCOMPLETE"){
         //const inputElement = document.createElement('input');
         //inputElement.setAttribute("id", "input_search");
         //inputElement.setAttribute("class","ett-inpt pri etw-100");

         appender = $(params.obj).siblings("div");
         /////////////bug gix for additional ul in dropdown//////////
         appender[0].children[1].remove();
         $(appender[0]).append(childUl);
         $(childUl).addClass('pos-rel');
         //$('.sub-ctr').prepend(inputElement);
      } else {
         appender = $(params.obj).siblings("div");
         $(appender[0]).children().remove();
         $(appender[0]).append(childUl);
      }
  }, 
  populateROSelect : function(obj, options) {
      var dd = "";
      var lclass = $($(obj).find("dd"))[0].classList;
      var classStr = lclass.toString();
      for (var i = 0; i < options.length; i++) {
         dd = document.createElement('dd');
         $(dd).attr('id', obj.id + "_" + i);
         $(dd).attr('value', options[i].val);
         $(dd).addClass(classStr);
         dd.innerHTML = options[i].desc;
         obj.appendChild(dd);
      }
   },createRecursiveDropdownLevels: function(params) {
        var drdnObj = params.ElmObj;
        var myObj = this;
        for (var k = 0; k < params.Arr.length; k++) {
            var obj = params.Arr[k];
            for (var property in obj) {
                /** creates problem when space and special characters are present**/
                //var objDesc = obj["desc"].replace(/[^a-zA-Z0-9]/g,'');
                var objDesc = obj["desc"];
                if (obj.hasOwnProperty(property)) {
                    if (obj[property].constructor == Array) {
                        var ind = params.helper.incrementer + 1;
                        var  ul = '<ul id="ul' + ind + '">';
                        params.html["html"] = params.html["html"]+ul;
                        var newObj = this.copyJSONObject(params);
                        newObj.fragment = params.fragment;
                        newObj.ElmObj = drdnObj;
                        newObj.helper.incrementer = ind;
                        newObj.helper.currentUl = ind;
                        newObj.Arr = obj[property];
                        newObj.html = params.html;
                        myObj.createRecursiveDropdownLevels(newObj);
                    } else {
                        if (property == 'val') {
                            var $li = "<li data-value='" + obj[property] + "' id='" + params.ElmObj.id + "_option_" + objDesc + "' tabindex='0'>";
                            var hasChild = false;
                            for (var a in obj) {
                                if (obj.hasOwnProperty("property") && obj["property"].constructor == Array) {
                                    hasChild = true;
                                }
                            }
                            var $optSpan = "<span id='" + params.ElmObj.id + "_option_child" + objDesc + "' class='menu-expand'></span>";
                            var tempHtml = params.html["html"];
                            if (!hasChild) {
                                params.html["html"] = tempHtml+$li+obj.desc +'</li>';
                            } else {
                                params.html["html"] = tempHtml +$li+$optSpan+obj.desc+'</li>';
                            }
                            params.helper.currentLi = params.ElmObj.id + '_option_' + objDesc;
                        }
                    }
                }
            }
            if (k == params.Arr.length - 1) {
                params.html["html"] = params.html["html"] +'</ul>';
                params.helper.currentUl = params.helper.currentUl - 1;
            }
        }
   },
   //////Sort/Filter/Serach Table Functions to go Here..
   getRecordNumber : function(container, recNo) {
      var currPage = this.scrMetaData.containersMap[container].currPage;
      var pageSize = this.scrMetaData.containersMap[container].pageSize;
      var recNo = (pageSize * (currPage - 1)) + recNo;
      return recNo;
   }, toggleRowSelection : function(tableId) {
      var checkboxId = $($($("#"+tableId).find('thead th')[0]).find('input')).attr('id');
      if ($("#"+checkboxId).prop('checked')) {
         apz.selectAllRows(tableId);
      } else {
         apz.unSelectAllRows(tableId);
      }
   }, 
   /**
    * This API is used to to select all rows of the table.
    * @param {string} tableId -The HTML ID of the table.
    * @example
    * apz.selectAllRows("id_of_Table");
    */
   selectAllRows : function(tableId) {
	   /* Params contains the below value
	       *** tableId ***
	   */
      var select = true;
      if(this.isFunction(this.app.preSelectAll)){
         select = this.app.preSelectAll(tableId);
         if(this.isNull(select)) {
            select = true;
         }
      }
      if (select) {
         var ltableId = '#' + tableId + ' .group-checkable';
         $(ltableId).each(function(){//Jquery :visible didn't work 
			 if($(this).css("visibility") !== "hidden"){
				 $(this).prop('checked', true);
			 }
		 });
      }
      if(this.isFunction(this.app.postSelectAll)){
         this.app.postSelectAll(tableId);
      } 
   }, 
   /**
    * This API is used to unselect all rows of a table.
    * @param {string} tableId -The HTML ID of the table
    * @example
    * apz.unSelectAllRows("id_of_Table")
    */
   unSelectAllRows : function(tableId) {
	   /* Params contains the below value
	       *** tableId ***
	   */
      var unSelect = true;
      if(this.isFunction(this.app.preUnSelectAll)){
         unSelect = this.app.preUnSelectAll(tableId);
         if (this.isNull(unSelect)) {
            unSelect = true;
         }
      }
      if (unSelect) {
         var ltableId = '#' + tableId + ' .group-checkable';
         $(ltableId).prop('checked', false);
      }
      if(this.isFunction(this.app.postUnSelectAll)){
         this.app.postUnSelectAll(tableId);
      }
   }, changeTypeToText : function(obj) {
      if ($(obj).hasClass("iosnumber")) {
         $(obj).attr('type', 'text');
      }
   }, 
//   addScriptsPath : function(appId, scripts) {
//      if(!this.isNull(scripts)){
//         var count = scripts.length;
//         for (var i = 0; i < count; i++) {
//            if(scripts[i].substring(0,4) == "cwd/"){			 
//   				scripts[i] = this.getInfraPath() + "/" + scripts[i];
//   			}else{
//   				scripts[i] = this.getScriptsPath(appId) + "/" + scripts[i];
//   			}
//         }
//      }
//   }
   addScriptsPath : function(appId, scripts) { 
	   if(!this.isNull(scripts)){
		   var count = scripts.length;
		   for (var i = 0; i < count; i++) {
			   if(scripts[i].substring(0,4) == "cwd/"){
				   scripts[i] = this.getInfraPath() + "/" + scripts[i];
			   }else{
				   // If running in code coverage mode the use the instrumented scripts for
					// app scripts //and the regular scripts for test scripts
				   if(typeof(isCodeCoverageRun) != "undefined" && isCodeCoverageRun && scripts[i].indexOf("testscripts") == -1){
                  if(typeof codecoverageconfig!="undefined")
                  {
                    var present=false;
                    var config_file=JSON.parse(codecoverageconfig);
                    config_file.exclude.forEach(function(file){
                      if(file.indexOf(scripts[i])!=-1)
                      {
                        present=true;
                      }
                    })
                    scripts[i]=present?this.getScriptsPath(appId) + "/" + scripts[i]:this.getInstrumentedScriptsPath(appId) + "/" + scripts[i];
                  }
                  else{
                    scripts[i] = this.getInstrumentedScriptsPath(appId) + "/" + scripts[i];
                  }
				   }else{
					   scripts[i] = this.getScriptsPath(appId) + "/" + scripts[i]; 
				   }
			   }
			}
	   }
   },
   getInstrumentedScriptsPath: function(appId) { 
	   if(!appId){
		   appId = this.currAppId; 
	   }
	   path = "apps-instrumented/" + appId + "/scripts"; 
	   if(this.deviceOs == "MCA"){
		   path = this.mca.mcaSourcepath + "controllers"; 
	   }
	   return path; 
   }
   , setSelectedFileName : function(obj){
	  var id = obj.id;
	  var spanLi = $("#" + id).parent().parent().siblings().find('#selectedfiles');
	  $(spanLi).children().remove();
      var result = $(obj)[0].files;
      for(var x = 0;x< result.length;x++){
         var file = result[x];
         // here are the files
         $(spanLi).append("<p>" + file.name +','+' '+"</p>");  
      }
   }, changeTypeToNumber : function(obj) {
       var val = this.getObjValue(obj);
       if (!this.isNull(val)) {
           var params = {};
           params.value = val;
           params.decimalSep = this.decimalSep;
           val = this.unFormatNumber(params);
           obj.value = val;
       }
       $(obj).attr('type', 'number');
   }, onRowElmChange : function(obj, event) {
       if ($(obj).closest('tr').length > 0) {
           var obj = $(obj).closest('tr')[0];
           this.data.rowClicked(obj, event);
       }
   },
   ///////////////////////////////////////////////////////
   /////////////////////////Dom///////////////////////////
   ///////////////////////////////////////////////////////
   getID : function(elmData, recNo) {
      var id = elmData.id;
      if (!this.isNull(recNo)) {
         if (recNo != -1) {
            id = id + this.recSep + recNo;
         }
      }
      return id;
   }, 
   /**
    * 
    * @param {string} id This field indicates the DOM element.
    * @example
    * apz.app.getHtml = function(id){
	 *	var html = apz.getHtml(id);
	 *	alert(html);		
	 *  }
    */
   getHtml : function(id) {
	   /* Params contains the below value
	       *** id(DOM element ID) ***
	       * Response contains below value
	       *** html ***
	   */
      return $("#" + id).html();
   }, 
   /**
    * 
    * @param {string} id This field indicates the DOM element.
    * @param {string} html This field will contain the HTML that should be replaced with the current HTML.
    * @example
    * var html = ‘<p>Hello World!</p>’;
    * apz.setHtml(id,html);
    */
   setHtml : function(id, html) {
	   /* Params contains the below value
	       *** id(DOM element ID), html ***
	   */
	  if(this.bindingEngine == "ANGULAR"){
		 angular.element(document).injector().invoke(function($compile) {
			 var loadDiv = $("#" + id);
			 loadDiv.html(html);
			 $compile(loadDiv.contents())(apz.data.angularAppScope);
			 apz.data.angularAppScope.data = apz.data.scrdata;
		 });
	  } else {
         if (apz.deviceType == "WIN8SURFACE" || apz.deviceType == "WIN8.1SURFACE" || apz.deviceType == "WIN8.1PHONE") {
            MSApp.execUnsafeLocalFunction(function() {
               $("#" + id).html(html);
            });
         } else {
	        $("#" + id).html(html);
         }
	  }
   }, clearHtml : function(id) {
	   /* Params contains the below value
	       *** id(DOM element ID) ***
	   */
      $("#" + id).empty();
   }, 
   /**
    * This API returns the value of the field
    * @param {string} id The HTML ID of the element.
    * @example
    * apz.getElmValue("input_field_id");
    */
   getElmValue : function(id) {
	   /* Params contains the below value
	       *** id(DOM element ID) ***
	       * Response contains below value
	       *** value ***
	   */
      return this.getObjValue(document.getElementById(id));
   },
   /** 
      * This API is used to set the title of the page.
      * @param {String} title The title of the page to be set.
      * @example
      * apz.setTitle("Login")
   */ 
   setTitle:function(title){
      document.title=title;
   },
    getValue : function(elmData, recNo) {
      var id = this.getID(elmData, recNo);
      var value = "";
      var obj = document.getElementById(id);
      if (!this.isNull(obj)) {
         if (elmData.type == "GAUGE") {
            ///Get Value From Gauge
         }
         else if (!this.isNull(elmData.mask)) {
            value = $('#' + id).attr('apzvalue');
         } else {
            value = this.getObjValue(obj);
            ////Un Formatting to be Handled Here
            var dataType = elmData.dataType;
            var presetDataType = elmData.preset;
            var nodeId = elmData.nodeId;
            if (dataType == "NUMBER" || dataType == "INTEGER") {
               if (elmData.skipFormat == "N") {
                  var params = {};
                  params.value = value;
                  params.decimalSep = this.decimalSep;
				  params.displayAsLiteral = elmData.displayAsLiteral;
                  value = this.unFormatNumber(params);
               }
            } else if (dataType == "TIME") {
               var serverTimeFormat = this.getServerTimeFormat(nodeId);
               value = this.formatTime(value, this.timeformat, serverTimeFormat);
            } else if (dataType == "DATETIME") {
               var serverDateTimeFormat = this.getServerDateTimeFormat(nodeId);
               var params = {};
               params.val = value;
               params.fromFormat = this.dateTimeFormat;
               params.toFormat = serverDateTimeFormat;
               value = this.formatDate(params);
            } else if (dataType == "DATE") {
               var serverdateFormat = this.getServerDateFormat(nodeId);
               var params = {};
               params.val = value;
               params.fromFormat = this.dateFormat;
               params.toFormat = serverdateFormat;
               value = this.formatDate(params);
            }
         }
         if (elmData.isArray == "Y") {
            if (value.indexOf(",") > 0) {
               value = value.split(",");
            } else {
               value = new Array(value);
            }
         }
      }
      return value;
   }, 
   /**
    * This API returns value of the object
    * @param {Object} obj It is DOM object whose value needs to be fetched.
    * @example
    * apz.getObjValue(obj);
    * apz.getObjValue(document.getElementById("ELEMENT_ID"));
    */
   getObjValue : function(obj) {
	   /* Params contains the below value
	       *** obj(DOM element) ***
	       * Response contains below value
	       *** value ***
	   */
      var value = "";
      var tagName = obj.tagName;
      var elmData = "";
      var idType = $(obj).attr('apztype');
      var objRowNo = this.getObjRowNumber(obj);
      if (objRowNo!=-1) {
         var objRowId = this.getObjIdWORowNumber(obj);
         elmData = this.scrMetaData.elmsMap[objRowId];
      } else {
         elmData = this.scrMetaData.elmsMap[obj.id];
      }
      if(!this.isNull(elmData) && elmData.custom == "Y"){
         var custObj = apz[elmData.type];
         if(custObj && apz.isFunction(custObj.getObjValue)){
            value = custObj.getObjValue(obj);
         }
      } else if (tagName == "INPUT" || (tagName == "SPAN" && !this.isNull(elmData) && elmData.type == "DROPDOWN")) {
         var id = obj.id;
         var type = $('#' + id).attr('type');
         if (type == "hidden") {
            if (obj.classList.contains("appzillon_date")) {
               try {
                  value = $('#' + id)[0].nextElementSibling.firstChild.data;
               } catch (e) {
                  value = "";
               }
            }
         } else if (type == "CHECKBOX") {
            if($(obj).prop("indeterminate")){
               value = $(obj).attr('indeterminateval');
            }else{
               var checkedAttr = $(obj).attr('checked');
               if (checkedAttr != null && checkedAttr != undefined) {
                  value = $(obj).attr('checkedval');
               } else {
                  value = $(obj).attr('uncheckedval');
               }
         }
         } else if(!this.isNull(elmData) && elmData.type == "DROPDOWN") {
        	   var dropDownDiv = document.getElementById(obj.id+"_div");
            var opts = $(dropDownDiv).find('ul li');
            var noOfElem = $(dropDownDiv).find('ul li').length;
            var allVals = [];
        	   if($(obj).parent().hasClass("is-multi-tiered")) {
                 var indx ;
                 var labelOpts = $(dropDownDiv).find('label');
                 var chkOpts = $(dropDownDiv).find('input');
                 for(i=0;i<opts.length;i++) {
                    if($(opts[i]).find('input').prop("checked")) {
                    	allVals.push(opts[i].getAttribute("data-value"));
                    }
                 }
            } else {
            	 var valArr = $(dropDownDiv).find('ul .is-selected');
                 if(valArr[0]){
                    var valArrlength = valArr.length;
                    for(i=0;i<valArrlength;i++) {
                       allVals.push(valArr[i].getAttribute("data-value"));
                    }
                 }
            }
        	   if (allVals.length>0) {
        		  value = allVals.join(',');
        	   } else {
        		  value = "";
        	   }
         } else if(!this.isNull(elmData) && elmData.type == "DROPDOWNWITHINPUT") {
            var valArr = $(obj).siblings('div').find('ul li.is-selected');
            if (valArr.length>0) {
               value = valArr[0].getAttribute("value");
            } else {
               value = "";
            }
         } else {
            value = obj.value;
         }
      } else if ((tagName == "LI") || (tagName == "DIV")) {
         if (idType == "toggleswitch") {
             var id0 = obj.id + '_00';
             var id1 = obj.id + '_11';
             var id0Obj = document.getElementById(id0);
             var id1Obj = document.getElementById(id1);
             if (id0Obj.checked) {
                 value = $(id0Obj).val();
             } else {
                 value = $(id1Obj).val();
             }
         } else if (idType == "radiogroup") {
             $(obj).find('input').each(function() {
                 if ($(this).attr("type") == "radio") {
                     var checked = $(this).is(":checked");
                     if (checked) {
                         value = $(this).val();
                     }
                 }
             });
         } else if (idType == "progress") {
             value = obj.style.width.split("%")[0];
             value = +value / 100;
         }
         /*else {
                    var id = obj.id;
                    var span = $('span', $('#' + id));
                    for (var i = 0; i < span.length; i++) {
                       var testId = span[i].id;
                       if (!this.isNull(testId)) {
                          var checked = $('#' + testId)[0].checked;
                          if (checked) {
                             value = $('#' + testId)[0].value;
                          }
                       }
                    }
                 }*/ // redundant code
     } else if (tagName == "SELECT") {
         if (!this.isNull(elmData) && elmData.isArray == "Y") {
             value = $(obj).val();
         } else {
             var selOpts = $("#" + obj.id + " " + "option:selected");
             var allVal = [];
             for (var i = 0; i < selOpts.length; i++) {
                 allVal.push($(selOpts[i]).attr('value'));
             }
             value = allVal.toString();
         }
      } else if (tagName == "TEXTAREA") {
         value = obj.value;
      } else if (tagName == "DD") {
         //value = obj.innerHTML;
         value = obj.textContent;
      } else if (tagName == "DL") {
         var ddList = $(obj).find('dd');
         if (ddList.length > 0) {
            for (var i = 0; i < ddList.length; i++) {
               if (!$(ddList[i]).hasClass('sno')) {
                  value = $(ddList[i]).attr('value');
                  break;
               }
            }
         }
      } else if (tagName == "P" || tagName == "H1" || tagName == "H2" || tagName == "H3" || tagName == "H4" || tagName == "H5" || tagName == "H6") {
         var currObj = $(obj).find("#"+obj.id+"_txtcnt");
         if (currObj.length==0) {
            currObj = obj;
         }
         value = $(currObj).text();
      } else if (tagName == "A") {
         if(obj.classList.contains("editable") && elmData.type == "DROPDOWN"){
            value = $(obj).attr("value");
         }else{
            var currObj = $(obj).find("#"+obj.id+"_txtcnt");
            if (currObj.length==0) {
               currObj = obj;
            }
            value = $(currObj).text();
         }
      } else if (tagName == "IMG" || (tagName == "SPAN" && obj.getAttribute("type") == "SVG")) {
      	 if(tagName == "SPAN" && obj.getAttribute("type") == "SVG"){
      	 	 value = obj.innerHTML;
      	 } else {
         	 var source = obj.src;
	         if (source != null && source != "" && (source.indexOf('.') == -1)) {
	         	if(source.indexOf("data:image/svg+xml;base64,") > -1){
	            	value = source;
	            } else {
	            	value = source.substr(22, source.length);
	            }
	         } else {
	            value = source
	         }
         }
      } else if (tagName == "svg") {
         var classes = obj.getAttribute('class').split('icon-');
         if(classes.length > 1) {
            value = "icon-"+classes[1].split(' ')[0];
         }

      } else if (tagName == "PROGRESS") {
         value = obj.value;
      } else if (tagName == "SPAN") {
         if (idType == "radiogroup") {
            var id = obj.id;
            $("#" + id).find('input').each(function() {
               if ($(this).attr("type") == "radio") {
                  var checked = $(this).is(":checked");
                  if (checked) {
                     value = $(this).val();
                  }
               }
            });
         } else {
             value = obj.textContent;
         }
      } else if(tagName == "BUTTON") {
         if ($(obj).hasClass("with-dropddown")) {
            var valArr = $(obj).siblings('div').find('ul li');
            var valArrlength = valArr.length;
            for(i=0;i<valArrlength;i++) {
               if(valArr[i].getAttribute("value") == obj.value) {
                  value = valArr[i].getAttribute("value");
               }
            }
         } else {
            var currObj = $(obj).find("#"+obj.id+"_txtcnt");
            if (currObj.length==0) {
               currObj = obj;
            }
            value = $(currObj).text();
         }
      } else if (!this.isNull(elmData) && elmData.type == "SORTCODEACCOUNT") {
         for (var i = 1; i <= 4; i++) {
            value = value + $("#"+obj.id+"_"+i).val();
            if(i != 4){
               value = value + "-";
            }
         }
      }
      return value;
   }, 
   /**
    * This API sets the field with given value.
    * @param {string} id This is the ID of the element  for which the value needs to be set.
    * @param {string} value This is the value which needs to be set to the obj
    * @example
    * apz.setElmValue(id, value);
    * apz.setElmValue("input_field_id", "100000");
    */
   setElmValue : function(id, value) {
	   /* Params contains the below values
	       *** id(DOM element ID), value ***
	   */
      this.setObjValue(document.getElementById(id), value);
   }, setValue : function(params) {
	  //Expects elmData, recNo, value
      var id = this.getID(params.elmData, params.recNo);
      var obj = document.getElementById(id);
      var value = params.value;
      if (this.isNull(value)) {
         value = "";
      }
      if (!this.isNull(obj)) {
         if (params.elmData.type == "GAUGE") {
            var myObj = this;
            if(!apz.isNull(value)){
                requirejs([this.getInfraPath() + "/appzillon/gauges.js"], function() {
                	var gaugeId = myObj.getObjIdWORowNumber(obj);
                   var gaugeObj = myObj.scrMetaData.gaugesMap[gaugeId];
                   var newGaugeObj = myObj.copyJSONObject(gaugeObj);
                   newGaugeObj.uiId = id;
                   newGaugeObj.value = value;
                   try {
                      var gauges = new Apz.Gauges(myObj);
                      gauges.paintGauge(newGaugeObj);
                   } catch(e) {
                      console.log("Problems with Gauges Library");
                   }
                });	
            }
         } else if (!this.isNull(params.elmData.mask)) {
            $('#' + id).attr("apzvalue", value);
            value = this.getMaskedValue(params.elmData.mask, value);
         } else {
            // //Formatting to be Handled Here...
            var dataType = params.elmData.dataType;
            if (dataType != "STRING") {//Bug 4404 Masking Changes
               var nodeId = params.elmData.nodeId;
               var presetDataType = params.elmData.preset;
               if ((dataType == "NUMBER" || dataType == "INTEGER")) {
                  if (params.elmData.skipFormat == "N") {
                     var decPoints = this.getDecimalPoints(params.elmData, params.recNo);
                     var args = {};
                     args.value = value;
                     args.decimalSep = this.decimalSep;
                     args.decimalPoints = decPoints;
                     args.mask = this.numberMask;
                     args.displayAsLiteral = params.elmData.displayAsLiteral;
                     value = this.formatNumber(args);
                  }
               } else if (dataType == "TIME") {
                  var serverTimeFormat = this.getServerTimeFormat(nodeId);
                  value = this.formatTime(params.value, serverTimeFormat, this.timeFormat);
               } else if (dataType == "DATETIME") {
                  var serverDateTimeFormat = this.getServerDateTimeFormat(nodeId);
                  var param = {};
                  param.val = value;
                  param.fromFormat = serverDateTimeFormat;
                  param.toFormat = this.dateTimeFormat;
                  value = this.formatDate(param);
               } else if (dataType == "DATE") {
                  var ltype = $('#' + id).attr('type');
                  var param = {};
                  param.val = value;
                  param.toFormat = this.dateFormat;
                  var serverdateFormat = this.getServerDateFormat(nodeId);
                  param.fromFormat = serverdateFormat;
                  if($('#' + id).hasClass("form-control--datepicker")){
                     value = this.formatJqueryDate(param);
                  }else{
                     value = this.formatDate(param);
                  }
               }
            }
            //// To clear value for the Image data model field
            if (obj.tagName == "IMG") {
               if ((this.isNull(params.elmData.nodeId)) && (this.isNull(params.elmData.name))) {
                  value = obj.getAttribute('src');
               }
            }
            if (!this.isNull(params.elmData.mask)) {
               $('#' + id).attr("apzvalue", value);
               //// Set Actual value to Obj
               value = this.getMaskedValue(params.elmData.mask, value);
            }
         }
         //// APZFIX
         if (params.elmData.isArray == "Y") {
            if(Array.isArray(params.value))
            value = params.value.toString();
         }
         if (params.elmData.type != "GAUGE") {
            this.setObjValue(obj, value);
         }
      }
   }, 
   /**
    * This API sets the object with value passed as parameter.
    * @param {Object} obj It is the DOM object whose value needs to be set
    * @param {string} value The value that needs to be set.
    * @example
    * apz.setObjValue(obj, value);
    * apz.setObjValue(document.getElementById("ELEMENT_ID"),"value")
    */
   setObjValue : function(obj, value) {
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
            // added code_AB 
            // multi checkbox dropdown checkbox indexing issue fix on loadData call
            var hasDefaultTitle = false;
             var dropDownDiv = document.getElementById(obj.id+"_div");
             var opts = $(dropDownDiv).find('ul li');
             var optsArray = Array.prototype.slice.call(opts);
             if (optsArray.length > 0 && optsArray[0].classList.contains('default-title')) {
               hasDefaultTitle = true;
               optsArray.shift();
             }
             var noOfElem = optsArray.length;
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
                var labelOptsArr = Array.prototype.slice.call(labelOpts);
                if (hasDefaultTitle) {
                   labelOptsArr.shift();
                }
                var chkOpts = $(dropDownDiv).find('input');
                for(var i = 0; i < noOfElem; i++) {
                   indx = valArr.indexOf(optsArray[i].getAttribute("data-value"));
                  
					   if(indx >-1) {
                      resArr.push(labelOptsArr[i].textContent);
                      $(chkOpts[i]).prop("checked",true);
	                   } else {
	                	   $(chkOpts[i]).prop("checked",false);
	                   }
					   
                }
                if (hasDefaultTitle && resArr.length == 0) {
                  resArr.push(opts[0].id.split('_')[opts[0].id.split('_').length - 1]);
                }
               resArr = resArr.join(', ');
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
            // modified code_AB // disabled the condition check for type text as we are handling it in the next block
         } else if (type != "file" && type != 'text') {
            this.changeTypeToText(obj);
            obj.value = value;
          } else if (type == 'text') {
            //  added code_AB // handled floating label value set issue
             if ($('#' + elmData.id + '_ul').length > 0 && $('#' + elmData.id + '_ul').hasClass('flbl')) {
               if (value.length > 0) {
                  if(!$('#' + elmData.id + '_ul').hasClass('flbl-float')) {
                     $('#' + elmData.id + '_ul').addClass('flbl-float');
                  }
               } else {
                  if($('#' + elmData.id + '_ul').hasClass('flbl-float')) {
                     $('#' + elmData.id + '_ul').removeClass('flbl-float');
                  }
               }
               this.changeTypeToText(obj);
               obj.value = value;
             } else {
               this.changeTypeToText(obj);
               obj.value = value;
             }
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
         //  added code_AB // handled floating label value set issue
         if ($('#' + elmData.id + '_ul').length > 0 && $('#' + elmData.id + '_ul').hasClass('flbl')) {
            if (value.length > 0) {
               if(!$('#' + elmData.id + '_ul').hasClass('flbl-float')) {
                  $('#' + elmData.id + '_ul').addClass('flbl-float');
               }
            } else {
               if($('#' + elmData.id + '_ul').hasClass('flbl-float')) {
                  $('#' + elmData.id + '_ul').removeClass('flbl-float');
               }
            }
            obj.value = value;
          } else {
            obj.value = value;
          }
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
         //ADPPS-298 textarea not allowing formatted text.
         if($(currObj)[0].classList.contains("span-newline"))
             $(currObj)[0].classList.remove("span-newline")
         if(value!=undefined && typeof value=="string" && value.indexOf('\n')!=-1){
             $(currObj)[0].classList.add("span-newline")
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
			 var $obj=$(obj);
			 if((value.substr(value.length-4)) == ".svg"){
	         	var currTheme = this.theme;
				myStatus = value.indexOf("styles/themes/" + currTheme + "/img");
				if (!this.isNull(myStatus) && myStatus != -1) {
				   var val=this.getFile(value);
				   $obj.html(val);
				} else {
				   var path = this.getStylesPath() + "/" + currTheme + "/img/" + value;
				   var val=this.getFile(path);
				   $obj.html(val);
				}
			} else {
			   $obj.html(value);
			}
         }
         else {
         	if(isSVG){
         		obj = this.swipeIdsForImage(obj);
         	}
            if (value != null && value != "" && value.indexOf('http') == -1) {
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
   },
   //////////////////////////////////////////////////////////////
   ///////////////////Loader ////////////////////////////////////
   //////////////////////////////////////////////////////////////
   getHTMLFileName : function(scr, lo,tmpl) {
      return scr + "_" + lo + "_" + tmpl + "_" + this.language + ".html";
   }, getIfaceName : function(iface) {
      var len = iface.length;
      if (len > 4) {
         var end4 = iface.substr(len - 4);
         var end2 = iface.substr(len - 2);
         if ((end4 == "_Req") || (end4 == "_Res")) {
            iface = iface.substr(0, len - 4);
         } else if ((end4 == "_i") || (end4 == "_o")) {
            iface = iface.substr(0, len - 2);
         } else {
            // //Could be Database..
            var ind = iface.lastIndexOf("_");
            if (ind >= 0) {
               var action = iface.substring(ind + 1);
               if (!this.isNull(action)) {
                  if ((action == "Query") || (action == "New") || (action == "Modify") || (action == "Delete") || (action == "Authorize")) {
                     iface = iface.substring(0, ind);
                  }
               }
            }
         }
      }
      return iface;
   }, 
   // added code_AB
   getCurContainerIfaceName: function (containerId) {
      var elmData, nodeId, ifaceName;
      var containerData = this.scrMetaData.containersMap[containerId];
      if (containerData.elms && containerData.elms.length !== -1) {
          for (var i = 0; i < containerData.elms.length; i++) {
              elmData = containerData.elms[i];
              nodeId = elmData.nodeId;
              if (this.containsKey(this.scrMetaData.nodesMap, nodeId)) {
                  ifaceName = this.scrMetaData.nodesMap[nodeId].ifaceName;
              }
              if (ifaceName) break;
          }
          return ifaceName;
      }
   },
   /**
    * This API returns the layout of required screen.
    * @param {object} params params include
    * <br>appId: This attribute indicates application Id of the app.
    * <br>scr: Name of the screen of required layout.  
    * <br>deviceGroup and orientation. (Optional)
    * @example
    * var params = {}
    * params.appId = "Test"
    * params.scr = "WelcomePage"
    * apz.getLayout(params);
    * @returns returns the layout attached.
    */
   getLayout : function(params) {
      var layout = "";
      if (!this.isNull(this.firstPageLayout)) {
         layout = this.firstPageLayout;
         delete this.firstPageLayout;
      } else {
         var appId = params.appId ? params.appId : this.currAppId;
         var scr = params.scr ? params.scr : this.currScr;
         var deviceGroup = params.deviceGroup ? params.deviceGroup : this.deviceGroup;
         var orientation = params.orientation ? params.orientation : this.orientation;
         var loObj = this.loMap[appId][scr];
         if (!loObj) {
             //// Citi changes for dynamic generated screens without loMap. 
             layout = this.loMap[appId][Object.keys(this.loMap[appId])[0]][deviceGroup][orientation];
         } else {
             layout = loObj[deviceGroup][orientation];
         }
      }
      return layout;
   }, 
   /**
    * This API returns the required design of given layout. 
    * @param {object} params params include
    * <br>appId: This attribute indicates application Id of the app.
    * <br>scr: This attribute indicates Id of the Screen
    * <br>layout: Name of the layout of required design
    * <br>Key: Id of the required design.
    * <br>devicegroups, orientation and customizer(Optional).
    * @example
    * var params = {}
    * params.appId = "TEST"
    * params.scr = "WelcomePage"
    * params.layout = "NewLayout"
    * params.key = "D1"
    * apz.getDesigns(params)
    * @returns returns the reuired design.
    */
   getDesigns : function(params) {
      var appId = params.appId ? params.appId : this.currAppId;
      var scr = params.scr ? params.scr : this.currScr;
      var layout = params.layout ? params.layout : this.getLayout(params);
      var deviceGroup = params.deviceGroup ? params.deviceGroup : this.deviceGroup;
      var orientation = params.orientation ? params.orientation : this.orientation;
      var customizer = params.customizer ? params.customizer : "N";
      var key = scr + this.idSep + layout, design = "";
      var res = {};
      if (!this.containsKey(this.loDefsMap[appId], key)) {
         /////Screen Loading Process
         //////Layout Definition
         params = {};
         params.id = "LODEF";
         params.scr = scr;
         params.lo = layout;
         params.runnerObj = this;
         params.runner = this.getFile;
         params.callBack = this.loadLayoutDef;
         params.callBackObj = this;
         params.path = this.getScrDefPath(appId) + "/" + scr + "_" + layout + ".json";
         params.appId = appId;
         params.async = false;
         this.getFile(params);
      }
      var designInfo = this.loDefsMap[appId][key];
      design = designInfo.defaultTemplate;
      if(this.customizer && designInfo.customize == "Y"){
         key = scr + this.idSep + layout + this.idSep + designInfo.defaultTemplate;
         if(!this.containsKey(this.customizer.config[appId], key)){
            params = {"scr":scr,"lo":layout,"customizer":customizer,"appId":appId};
            params.id = "CUSTOMIZERSERVICE";
            params.template = "";
            this.customizer.getTemplateCustomizedInfo(params);
         }
         key = scr + this.idSep + layout + this.idSep + params.template;
         var config = this.customizer.config[appId][key];
         if(config && config.isModified){
            design = config.template;
         }
      }
      res.icons = designInfo.icons;
      res.designs = designInfo.designs;
      res.currentDesign = design;
      res.designDisplayNames = designInfo.designDisplayNames;
      return res;
   }, getScreens : function(appId, type) {
	   var res = {};
	   appId = appId ? appId : this.currAppId;
	   res.screens = this.scrs[appId];
	   if(type == "PG"){
	      res.currentScreen = this.currScr;
	   } else {
		  res.currentScreen = this.childScr;
	   }
	   return res;
   }, getOrientations : function(deviceGroupName) {
      var res = {};
      res.orientations = [];
      for (var i = 0; i < this.deviceGroups.length; i++) {
         var group = this.deviceGroups[i];
         if(deviceGroupName == group.name){
            if(group.orientation == "ANY"){
               res.orientations.push("LANDSCAPE","PORTRAIT");
            } else {
               res.orientations.push(group.orientation);
            }
         }
      }
      res.currentOrientation = this.orientation;
      return res;
   },
   getDeviceGroupNames : function() {
      var res = {};
      res.groupNames = [];
      for (var i = 0; i < this.deviceGroups.length; i++) {
         var group = this.deviceGroups[i];
         res.groupNames.push(group.name);
      }
      res.currentGroupName = this.deviceGroup;
      return res;
   }, initScrDef : function(scrMeta) {
      ////Nodes
      var noOfNodes = scrMeta.nodes.length;
      for (var n = 0; n < noOfNodes; n++) {
         if (scrMeta.nodes[n].relType == "1:N") {
            scrMeta.nodes[n].currRec = -1;
         } else {
            scrMeta.nodes[n].currRec = 0;
         }
      }
      ////Containers
      var noOfContainers = scrMeta.containers.length;
      for (var c = 0; c < noOfContainers; c++) {
         scrMeta.containers[c].pageRows = 0;
         scrMeta.containers[c].totalRecs = 0;
         scrMeta.containers[c].currRec = -1;
         scrMeta.containers[c].currPage = 0;
         scrMeta.containers[c].totalPages = 0;
      }
   }, appendScrDef : function(parentMeta, childMeta) {
      var noOfChildIfaces = childMeta.ifaces.length;
      var noOfChildNodes = childMeta.nodes.length;
	  var noOfChildElms = childMeta.elms.length;
      var noOfChildCntnrs = childMeta.containers.length;
      var noOfChildGroups = childMeta.groups.length;
      var noOfChildScreens = childMeta.childScreens.length;
      var noOfParentIfaces = parentMeta.ifaces.length;
      var noOfParentNodes = parentMeta.nodes.length;
	  var noOfParentElms = parentMeta.elms.length;
      var noOfParentCntnrs = parentMeta.containers.length;
      var noOfParentGroups = parentMeta.groups.length;
      var noOfParentScreens = parentMeta.childScreens.length;
      var noOfChildCharts = childMeta.charts.length;
      var noOfParentCharts = parentMeta.charts.length;
      var noOfChildGauges = childMeta.gauges.length;
      var noOfParentGauges = parentMeta.gauges.length;
      for (key in childMeta) {
         if(key == "containers" || key == "containersMap"){
            if(key == "containers"){
               for (var i = 0; i < noOfChildCntnrs; i++) {
                  var containerName = childMeta.containers[i].name;
                  if(!parentMeta.containersMap[containerName]){
                     parentMeta.containers[noOfParentCntnrs] = childMeta.containers[i];
                     parentMeta.containersMap[containerName] = childMeta.containers[i];
                     noOfParentCntnrs++;
                  }
               }
            }
         } else if(key == "ifaces" || key == "ifacesMap"){
            if(key == "ifaces"){
               for (var i = 0; i < noOfChildIfaces; i++) {
                  var lifacename = childMeta.ifaces[i].name;
                  if(!parentMeta.ifacesMap[lifacename]){
                     parentMeta.ifaces[noOfParentIfaces] = childMeta.ifaces[i];
                     parentMeta.ifacesMap[lifacename] = childMeta.ifaces[i];
                     noOfParentIfaces++;
                  }
               }
            }
         } else if(key == "groups" || key == "groupsMap"){
            if(key == "groups"){
               for (var i = 0; i < noOfChildGroups; i++) {
                  var lgroupname = childMeta.groups[i].name;
                  if(!parentMeta.groupsMap[lgroupname]){
                     parentMeta.groups[noOfParentGroups] = childMeta.groups[i];
                     parentMeta.groupsMap[lgroupname] = childMeta.groups[i];
                     noOfParentGroups++;
                  }
               }
            }
         } else if(key == "nodes" || key == "nodesMap"){
            if(key == "nodes"){
               for (var i = 0; i < noOfChildNodes; i++) {
                  var nodeId = childMeta.nodes[i].id;
                  if(!parentMeta.nodesMap[nodeId]){
                     parentMeta.nodes[noOfParentNodes] = childMeta.nodes[i];
                     parentMeta.nodesMap[nodeId] = childMeta.nodes[i];
                     noOfParentNodes++;
                  }
               }
		 	}            
         }else if(key == "elms"||key == "elmsMap"){    
   			if(key == "elms"){ 
                  for (var i = 0; i < noOfChildElms; i++) {
                     var emlmId = childMeta.elms[i].id;
                     if(!parentMeta.elmsMap[emlmId]){
                        parentMeta.elms[noOfParentElms] = childMeta.elms[i];
                        parentMeta.elmsMap[emlmId] = childMeta.elms[i];
                        noOfParentElms++;
                     }
                  }
   			}            
         } else if(key == "elmsExtMap") {
            //Citi specific property
            this.copyJSONObjectWithFilter(childMeta[key], [], parentMeta[key]);
         } else if(key == "childScreens"){
             for (var i = 0; i < noOfChildScreens; i++) {
                var childName = childMeta.childScreens[i].name;
                parentMeta.childScreens[noOfParentScreens] = this.copyJSONObject(childMeta.childScreens[i]);
                noOfParentScreens++;
             }
         } else if(key == "uiInits"){
         	if(!parentMeta.uiInits){
         		parentMeta.uiInits = {};
         	}
            for (key in childMeta.uiInits) {
               if(!parentMeta.uiInits[key]){
                  parentMeta.uiInits[key] = [];
               }
               var allKeys = childMeta.uiInits[key];
               var parentScrKeys = parentMeta.uiInits[key].length;
               for (var i = 0; i < allKeys.length; i++) {
                 parentMeta.uiInits[key][parentScrKeys] = allKeys[i];
                 parentScrKeys++;
               }
            }
         } else if(key == "charts" || key == "chartsMap"){
            if(key == "charts"){
               for (var i = 0; i < noOfChildCharts; i++) {
                  var containerName = childMeta.charts[i].name;
                  if(!parentMeta.chartsMap[containerName]){
                     parentMeta.charts[noOfParentCharts] = childMeta.charts[i];
                     parentMeta.chartsMap[containerName] = childMeta.charts[i];
                     noOfParentCharts++;
                  }
               }
            }
         } else if(key == "gauges" || key == "gaugesMap"){
            if(key == "gauges"){
               for (var i = 0; i < noOfChildGauges; i++) {
                  var gaugeName = childMeta.gauges[i].name;
                  if(!parentMeta.gaugesMap[gaugeName]){
                     parentMeta.gauges[noOfParentGauges] = childMeta.gauges[i];
                     parentMeta.gaugesMap[gaugeName] = childMeta.gauges[i];
                     noOfParentGauges++;
                  }
               }
            }
         } else {
            parentMeta[key] = childMeta[key];
         }
      }
   },
   ////Get Device Info (NS CB)
   loadDeviceInfo : function(info) {
      this.deviceId = info.deviceId;
      this.deviceOs = info.deviceOs;
      this.deviceType = info.deviceType;
      this.screenSize = info.screenSize;
      this.screenPpi = info.screenPpi;
      this.deviceGroup = info.deviceGroup;
      this.orientation = info.orientation;
      this.lockRotation = info.lockRotation;
      ////Call Process Callback
      //this.processCB(res);
   },
   ////User Preferences(NS CB)
   loadUserPrefs : function(res) {
      if (res) {
         if (res.userPrefs) {
            var prefs = res.userPrefs;
            this.dateFormat = prefs.dateFormat ? prefs.dateFormat : "dd-MMM-yyyy";
            this.dateTimeFormat = prefs.dateTimeFormat ? prefs.dateTimeFormat : "dd-MMM-yyyy  HH-mm-ss";
            this.timeFormat = prefs.timeFormat ? prefs.timeFormat : "HH-mm-ss";
            this.dfltDecimals = prefs.dfltDecimals ? prefs.dfltDecimals : this.dfltDecimals;
            this.decimalSep = prefs.decimalSep ? prefs.decimalSep : this.decimalSep;
            this.thousandSep = prefs.thousandSep ? prefs.thousandSep : this.thousandSep;
            this.numberMask = prefs.numberMask ? prefs.numberMask : this.numberMask;
            this.theme = prefs.theme ? prefs.theme : this.theme;
            this.defaultTheme = prefs.theme ? prefs.theme : this.theme;
            this.language = prefs.language ? prefs.language : this.language;
            this.baseThemesMap = prefs.baseThemesMap ? JSON.parse(prefs.baseThemesMap) : this.baseThemesMap;
            this.enableAnimations = true;
         }
      }
      ////Call Process Callback
      //this.processCB(res);
   }, loadAppProps : function(res) {
      var props = res.props;
      this.appId = props.appId;
      this.currAppId = props.appId;
      this.scrDefsMap[this.appId] = {};
      //// Enhancement done and shared by Ravindra on 6-May-2019 for KeepMeSignedIn
      this.firstPagesMap[this.appId] = apz.firstPage || props.firstPage;
      this.loDefsMap[this.appId] = {};
      if(this.customizer){
         this.customizer.config[this.appId] = {};
         this.customizer.configMeta[this.appId] = {};
      }
      if(!this[this.appId]){
         this[this.appId] = {};
      }
      this.scrHtmls[this.appId] = {};
      this.ifacesMap[this.appId] = {};
      // modified code_AB
      this.version = props.appVersion;
      this.expiryDate = props.expiryDate;
      this.serverToken = props.serverToken;
      this.authenticationType = props.authenticationType;
      this.otpReqd = this.getBoolean(props.otpReqd);
      this.otaReqd = this.getBoolean(props.otaReqd);
      this.serverUrl = props.serverUrl;
      this.trackLocation = this.getBoolean(props.trackLocation);
      this.serverToken = props.serverToken;
      this.encryptionKey = props.encryptionKey;
      this.enableAnimations = this.getBoolean(props.enableAnimations);
      this.encryptionKeyFileName = props.encryptionKeyFileName;
      this.auditLogReqd = this.getBoolean(props.auditLogReqd);
      this.logLevel = props.logLevel;
      this.sendLog = props.sendLog;
      this.noOfLogLines = props.noOfLogLines;
      //// Enhancement done and shared by Ravindra on 6-May-2019 for KeepMeSignedIn
      this.firstPage = apz.firstPage || props.firstPage;
      this.firstPageLayout = props.firstPageLayout;
      this.mockServer= props.enableMockServer;
      this.defaultAuthorization = props.defaultAuthorization;
      this.googleMapsKey = props.googleMapsKey;
      this.dataIntegrity = props.dataIntegrity;
      this.encryption = props.payloadEncryption;
      if (this.googleMapsKey == undefined) {
          $.ajax({
                type: "GET",
                /// Providing relative path of map.html
                //url: window.location.href + "apps/" + apz.appId + "/screens/Map.html",
                url:apz.getScrPath(apz.appId)+"Map.html",
                dataType: "html",
                async: false,
                success: function(data) {
                    for (var i = 0; i < $(data).length; i++) {
                        var x = $(data)[i].src
                        if (x) {
                            if (x.indexOf('maps.googleapis.com') != -1) {
                                var start = x.indexOf('key')
                                var end = x.indexOf('callback')
                                apz.googleMapsKey = x.substring(start + 4, end - 1)
                            }
                        }
                    }
                },
                error: function(data) {
                    console.log('Loading Maps key Failed')
                }
            });
        }  
      // Offline support will not be available for Web and Simulator    
      if(this.deviceOs == "WEB" || this.deviceOs == "SIMULATOR") {
         this.offlineSupport = "N";
      } else {
         this.offlineSupport = props.offlineSupport;
      }
      ////Defaults
      try
      {
         this.animationsSupported = Modernizr.cssanimations;
      } catch (e) {
         this.animationsSupported = false;
      }
      if (!this.animationsSupported) {
         this.enableAnimations = false;
      }
      require.config({
         waitSeconds: 200,
         urlArgs: "v=" + this.version
      });
   },
   //////Project Definition
   loadProjDef : function(params) {
      def = JSON.parse(params.content);
      ///App Properties
      this.loadAppProps(def);
      ///User Preferences
      this.loadUserPrefs(def);
      this.msgs[this.currAppId] = def.msgs[this.language];
      this.lovs[this.currAppId] = def.lovs;
      this.appsMap[this.currAppId] = this.copyObjByVal({},{"scripts":def.scripts});
      this.lits[this.currAppId] = def.lits[this.language];
      this.ccys = def.ccys;
      this.workflows = def.workflows;
      this.prjScripts = def.scripts;
      this.chartStyles = def.chartstyles;   
      this.internalInterfaces = def.internalInterfaces;
      ////add Path
      this.addScriptsPath(this.currAppId, this.prjScripts);

      ////Declare Process for User Prefrences
      var proc = {};
      proc.threads = [];
      proc.id = "USERPREFLOAD";
      var params = {};
      //////////Get User Preferences////////////////////////
      params.id = "USERPREFS";
      params.runnerObj = this.ns;
      params.runner = this.ns.getUserPrefs;
      params.callBack = this.loadUserPrefs;
      params.callBackObj = this;
      proc.threads[proc.threads.length] = params;
      this.startProc(proc);
      if(this.workflow){
         this.workflow.buildWFArray();  //Nagaraj
      }
      ////Available Screens
      this.scrs[this.currAppId] = def.screens;
      ////Device Groups
      this.deviceGroups = def.deviceGroups;
      ////Layout Mapping
      this.loMap[this.currAppId] = def.loMap;
      ////Conversations
      this.convMap[this.currAppId] = def.conversations;
      if(Apz.customizerApp && this.isFunction(this.customizer.init)){
         this.customizer.init();
      }
   }, storeIfaceDef : function(appId, iface, def) {
      this.ifacesMap[appId][iface] = def;
   }, storeScrDef : function(args) {
	   //Expects appId, scr, lo, def
      var key = args.scr + this.idSep + args.lo + this.idSep + args.template;
      this.scrDefsMap[args.appId][key] = args.def;
   }, storeScrHtml : function(appId, scr, lo, tmpl,html) {
      var key = this.getHTMLFileName(scr, lo,tmpl);
      this.scrHtmls[appId][key] = html;
   },
   /////Load Interface Definition
   loadIfaceDef : function(params) {
      def = JSON.parse(params.content);
      var getArrayMember = function(obj, ind, def) {
         var val = def;
         if (obj) {
            val = obj[ind];
         }
         return val;
      };
      var ifaceObj = {};
      ifaceObj.extMap = {};
      iface = def.name;
      ifaceObj.name = def.name;
      ifaceObj.type = def.type;
      ifaceObj.dateFormat = def.dateFormat;
      ifaceObj.dateTimeFormat = def.dateTimeFormat;
      ifaceObj.timeFormat = def.timeFormat;
      ifaceObj.offline = def.offline;
      ifaceObj.amountMask = def.amountMask;
      ifaceObj.decimalSep = def.decimalSep;
      ifaceObj.thousandSep = def.thousandSep;
      ifaceObj.session = def.session;
      ifaceObj.encrypt = this.getBoolean(def.encrypt);
      ifaceObj.targetNs = def.targetNs;
      ifaceObj.endPointUrl = def.endPointUrl;
      ifaceObj.noOfReqNodes = def.noOfReqNodes;
      ifaceObj.noOfResNodes = def.noOfResNodes;
      ifaceObj.noOfFaultNodes = def.noOfReqNodes;
      ifaceObj.correctReq = this.getBoolean(def.correctReq);
      ifaceObj.correctRes = this.getBoolean(def.correctRes);
      ifaceObj.nsList = def.nsList;
      ifaceObj.ignoreNs = def.ignoreNs;
      var eCntr = 0;
      var noOfNodes = def.nodes.length;
      ifaceObj.nodes = [];
      ifaceObj.nodesMap = [];
      ifaceObj.elms = [];
      // Changes to accomodate citi behaviour(Citi expects it to be object)
      ifaceObj.elmsMap = this.getObjectOrArray();
      if (noOfNodes > 0) {
         for (var n = 0; n < noOfNodes; n++) {
            var dml = "REQ";
            if (n >= def.noOfReqNodes) {
               dml = "RES";
            }
            if (def.type == "DATABASE") {
               dml = "";
            }
            ////Init
            var nodeObj = {};
            var args = {};
            args.iface = iface;
            args.dml = dml;
            nodeObj.dml = dml;
            ////Node Properties
            nodeObj.name = def.nodes[n];
            args.node = nodeObj.name;
            nodeObj.id = this.getNodeId(args);
            nodeObj.ifaceName = iface;
            nodeObj.ifaceId = this.getIfaceIdFromDml(iface, dml);
            nodeObj.extName = getArrayMember(def.nExtName, n, "");
            args.node = getArrayMember(def.nParent, n, "")
            nodeObj.parent = this.getNodeId(args);
            nodeObj.multiRec = getArrayMember(def.nMultiRec, n, "N");
            args.node = getArrayMember(def.nMrParent, n, "")
            nodeObj.mrParent = this.getNodeId(args);
            nodeObj.relType = getArrayMember(def.nRelType, n, "1:1");
            nodeObj.ns = getArrayMember(def.nNs, n, "");
            nodeObj.nsAlias = getArrayMember(def.nNsAlias, n, "");
            nodeObj.isoTags = getArrayMember(def.nIsoTags, n, "");
            var parents = getArrayMember(def.nParents, n, "");
            nodeObj.parents = [];
            var nodeExtKey = "";
            if (!this.isNull(parents)) {
               var arr = parents.split("~");
               for (var s = 0; s < arr.length; s++) {
                  args.node = arr[s]
                  var pnrtId = this.getNodeId(args);
                  nodeObj.parents[s] = pnrtId;
                  if (dml == "RES") {
                     var prntExtName = ifaceObj.nodesMap[pnrtId].extName;
                     if (this.isNull(nodeExtKey)) {
                        nodeExtKey = prntExtName;
                     } else {
                        nodeExtKey = nodeExtKey + "~" + prntExtName;
                     }
                  }
               }
               ////Add Current Node aswell..
               if (dml == "RES") {
                  if (this.isNull(nodeExtKey)) {
                     nodeExtKey = nodeObj.extName;
                  } else {
                     nodeExtKey = nodeExtKey + "~" + nodeObj.extName;
                  }
                  ifaceObj.extMap[nodeExtKey] = nodeObj.name;
               }
            } else {
               ////Case of DML Root Node
               ifaceObj.extMap[nodeObj.name] = nodeObj.name;
            }
            var childs = getArrayMember(def.nChilds, n, "");
            nodeObj.childs = [];
            if (!this.isNull(childs)) {
               var arr = childs.split("~");
               for (var s = 0; s < arr.length; s++) {
                  args.node = arr[s];
                  nodeObj.childs[s] = this.getNodeId(args);
               }
            }
            ////Elements
            nodeObj.elms = [];
            // Changes to accomodate citi behaviour(Citi expects it to be object)
            nodeObj.elmsMap = this.getObjectOrArray();
            var noOfNodeElms = getArrayMember(def.noOfNodeElms, n, 0);
            for (var e = eCntr; e < (eCntr + noOfNodeElms); e++) {
               var elmObj = {};
               elmObj.name = def.elms[e];
               elmObj.id = this.getElmId(nodeObj.id, elmObj.name);
               elmObj.dml = dml;
               elmObj.nodeId = nodeObj.id;
               elmObj.nodeName = nodeObj.name;
               elmObj.ifaceName = iface;
               elmObj.ifaceId = nodeObj.ifaceId;
               elmObj.extName = getArrayMember(def.eExtName, e, "");
               elmObj.dataType = getArrayMember(def.eDataType, e, "STRING");
               elmObj.relNode = getArrayMember(def.eRelNode, e, "");
               elmObj.relElm = getArrayMember(def.eRelElm, e, "");
               elmObj.cents = getArrayMember(def.eCents, e, "N");
               elmObj.pad = getArrayMember(def.ePad, e, "N");
               elmObj.padChar = getArrayMember(def.ePadChar, e, "");
               elmObj.mand = getArrayMember(def.eMand, e, "N");
               elmObj.pattern = getArrayMember(def.ePattern, e, "");
               elmObj.maxDec = this.getInt(getArrayMember(def.eMaxDec, e, 9));
               elmObj.lenType = getArrayMember(def.eLenTyp, e, "V");
               elmObj.maxLen = this.getInt(getArrayMember(def.eMaxLen, e, 0));
               elmObj.minLen = this.getInt(getArrayMember(def.eMinLen, e, 0));
               elmObj.maxVal = this.getFloat(getArrayMember(def.eMaxVal, e, 0));
               elmObj.minVal = this.getFloat(getArrayMember(def.eMinVal, e, 0));
               elmObj.ns = getArrayMember(def.eNs, e, "");
               elmObj.nsAlias = getArrayMember(def.eNsAlias, e, "");
               elmObj.isArray = getArrayMember(def.eArr, e, "N");
               ////Ext Map
               if (dml == "RES") {
                  var elmExtKey = nodeExtKey + "~" + elmObj.extName;
                  ifaceObj.extMap[elmExtKey] = elmObj.name;
               }
               ///Add to Arrays
               nodeObj.elms[e - eCntr] = elmObj;
               nodeObj.elmsMap[elmObj.id] = elmObj;
               ifaceObj.elms[e] = elmObj;
               ifaceObj.elmsMap[elmObj.id] = elmObj;
            }
            eCntr = eCntr + noOfNodeElms;
            ////Add to Nodes Array
            ifaceObj.nodes[n] = nodeObj;
            ifaceObj.nodesMap[nodeObj.id] = nodeObj;
         }
      }
      ////Add to Store
      this.storeIfaceDef(params.appId, iface, ifaceObj);
   }, loadScrDef : function(params) {
      def = JSON.parse(params.content);
      var scr = params.scr;
      var lo = params.lo;
      var template = params.template;
      var type = params.type;
      /////Initialize
      var scrMeta = {};
      scrMeta.htmls = [];
      ////Counters
      var ifaceCntr = 0;
      var nodeCntr = 0;
      var elmCntr = 0;
      var elmLovRfCntr = 0;
      var elmLovBvCntr = 0;
      var noOfIfaces = 0;
      if (def.ifaces) {
         noOfIfaces = def.ifaces.length;
      }
      ////Populate Ifaces and Nodes
      scrMeta.ifaces = [];
      scrMeta.ifacesMap = [];
      scrMeta.nodes = [];
      scrMeta.nodesMap = [];
      scrMeta.icon = def.icon ? def.icon : "";
      scrMeta.scrType = def.screenType ? def.screenType : "MAIN";
      scrMeta.scripts = def.scripts;
      ///add Path
      this.addScriptsPath(params.appId, scrMeta.scripts);
      for (var i = 0; i < noOfIfaces; i++) {
         var ifaceId = def.ifaces[i];
         var ifaceName = this.getIfaceName(ifaceId);
         //var ifaceObj = this.getIfaceObj(ifaceName, params.appId);
         var ifaceObj = apz.copyObjByVal({}, this.getIfaceObj(ifaceName, params.appId));
         ////Add Iface
         if (!this.containsKey(scrMeta.ifacesMap, ifaceName)) {
            var noOfScrIfaces = scrMeta.ifaces.length;
            scrMeta.ifaces[noOfScrIfaces] = ifaceObj;
            scrMeta.ifacesMap[ifaceName] = ifaceObj;
            var noOfScrNodes = scrMeta.nodes.length;
            var noOfIfaceNodes = ifaceObj.nodes.length;
            for (var n = 0; n < noOfIfaceNodes; n++) {
               var nodeObj = ifaceObj.nodes[n];
               scrMeta.nodes[noOfScrNodes] = nodeObj;
               scrMeta.nodesMap[nodeObj.id] = nodeObj;
               noOfScrNodes = noOfScrNodes + 1;
            }
         }
      }
      //////Containers
      scrMeta.containers = [];
      // Changes to accomodate citi behaviour(Citi expects it to be object)
      scrMeta.containersMap = this.getObjectOrArray();
      scrMeta.elms = [];
      // Changes to accomodate citi behaviour(Citi expects it to be object)
      scrMeta.elmsMap = this.getObjectOrArray();
      scrMeta.elmsExtMap = {};
      var noOfCntnrs = 0;
      if (!this.isNull(def.containers)) {
         if (!this.isNull(def.containers.name)) {
            noOfCntnrs = def.containers.name.length;
         }
      }
      for (var c = 0; c < noOfCntnrs; c++) {
         var cntnrObj = {};
         cntnrObj.name = def.containers.name[c];
         cntnrObj.ui = def.containers.ui[c];
         cntnrObj.id = def.containers.name[c];
         cntnrObj.type = def.containers.type[c];
         cntnrObj.custom = def.containers.custom[c];
         cntnrObj.multiRec = def.containers.multiRec[c];
         cntnrObj.paginationStyle = def.containers.pgStyle[c];
         cntnrObj.readOnly = def.containers.ro[c];
         cntnrObj.pageSize = def.containers.pgSize[c];
         var childs = def.containers.childs[c];
         cntnrObj.childs = childs.split("~");
         if (!this.isNull(childs)) {
            cntnrObj.childs = childs.split("~");
         } else {
            cntnrObj.childs = [];
         }
         ////Add to Containers Array
         scrMeta.containers[c] = cntnrObj;
         scrMeta.containersMap[cntnrObj.name] = cntnrObj;
         /////Interfaces
         var noOfIfaces = def.containers.noOfIfaces[c];
         cntnrObj.ifaces = [];
         if (noOfIfaces > 0) {
            for (var i = ifaceCntr; i < (ifaceCntr + noOfIfaces); i++) {
               var ifaceName = def.containers.ifaces[i];
               cntnrObj.ifaces[i - ifaceCntr] = ifaceName;
            }
            ifaceCntr = ifaceCntr + noOfIfaces;
         }
         /////Nodes
         if(def.containers.noOfNodes){
            var noOfCntrNodes = def.containers.noOfNodes[c];
            cntnrObj.nodes = [];
            if (noOfCntrNodes > 0) {
               for (var n = nodeCntr; n < (nodeCntr + noOfCntrNodes); n++) {
                  var nodeId = def.containers.nodes[n];
                  cntnrObj.nodes[n - nodeCntr] = nodeId;
               }
               nodeCntr = nodeCntr + noOfCntrNodes;
            }
         }
         ////Elements
         var noOfCntrElms = def.containers.noOfElms[c];
         cntnrObj.elms = [];
         // Changes to accomodate citi behaviour(Citi expects it to be object)
         cntnrObj.elmsMap = this.getObjectOrArray();
         cntnrObj.elmsExtMap = {};
         if (noOfCntrElms > 0) {
            for (var e = elmCntr; e < (elmCntr + noOfCntrElms); e++) {
               ////Add to Screen Elements
               var elmObj = {};
               var elmId = def.containers.elms[e];
               var elmType = def.containers.elmType[e];
               var uiElm = def.containers.eUi[e];
               ////Add to Container/Screen Elements Array
               cntnrObj.elms[e - elmCntr] = elmObj;
               cntnrObj.elmsMap[elmId] = elmObj;
               scrMeta.elms[e] = elmObj;
               scrMeta.elmsMap[elmId] = elmObj;
               ////Layout Properties
               elmObj.id = elmId;
               elmObj.name = elmId;
               elmObj.container = cntnrObj.name;
               elmObj.type = elmType;
               elmObj.ui = uiElm;
               elmObj.custom = def.containers.eCustom[e];
               elmObj.readOnly = def.containers.eRo[e];
               if (def.containers.eEmail) {
                  elmObj.email = def.containers.eEmail[e];
               }
               if (def.containers.eTextdecoration) {
                  elmObj.textdecoration = def.containers.eTextdecoration[e];
               }
               elmObj.mask = def.containers.eMask[e];
               elmObj.runtimeFormat = def.containers.eRtFmt[e];
               elmObj.skipFormat = def.containers.eSkipFmt[e];
               elmObj.displayAsLiteral = def.containers.eLiteral[e];
               elmObj.lovId = def.containers.eLovId[e];
               elmObj.lovWidthClass = def.containers.eLovWidth[e];
               elmObj.lovMinWidth = def.containers.eLovMinWidth[e];
               elmObj.staticOptions =(def.containers.staticOptions)?def.containers.staticOptions[e]:"";
               elmObj.defVal = (def.containers.defVal)?def.containers.defVal[e]:"";
               var noOfLovRf = def.containers.noOfeLovRf[e];
               elmObj.returnFields = [];
               if (noOfLovRf > 0) {
                  for (var rf = elmLovRfCntr; rf < (elmLovRfCntr + noOfLovRf); rf++) {
                     elmObj.returnFields.push(def.containers.eLovRf[rf]);
                  }
                  elmLovRfCntr = elmLovRfCntr + noOfLovRf;
               }
               var noOfLovBv = def.containers.noOfeLovBv[e];
               elmObj.bindVariables = [];
               if (noOfLovBv > 0) {
                  for (var bv = elmLovBvCntr; bv < (elmLovBvCntr + noOfLovBv); bv++) {
                     elmObj.bindVariables.push(def.containers.eLovBv[bv]);
                  }
               }
               elmLovBvCntr = elmLovBvCntr + noOfLovBv;
               if (uiElm == "Y") {
                  elmObj.name = elmId;
               } else {
                  ////Copy Attributes From Interface Definition
                  var elmDet = elmId.split(this.idSep);
                  if (elmDet.length >= 5) {
                     var lelmIfaceObjname = elmDet[0]+this.idSep+elmDet[1];
                     var ifaceObj = this.getIfaceObj(lelmIfaceObjname, params.appId);
                     if(ifaceObj){
	                     var ifaceElmObj = ifaceObj.elmsMap[elmId];
	                     elmObj.name = ifaceElmObj.name;
	                     elmObj.id = ifaceElmObj.id;
	                     elmObj.nodeName = ifaceElmObj.nodeName;
	                     elmObj.nodeId = ifaceElmObj.nodeId;
	                     elmObj.ifaceName = ifaceElmObj.ifaceName;
	                     elmObj.ifaceId = ifaceElmObj.ifaceId;
	                     elmObj.dataType = ifaceElmObj.dataType;
	                     elmObj.relNode = ifaceElmObj.relNode;
	                     elmObj.relElm = ifaceElmObj.relElm;
	                     elmObj.cents = ifaceElmObj.cents;
	                     elmObj.pad = ifaceElmObj.pad;
	                     elmObj.padChar = ifaceElmObj.padChar;
	                     elmObj.mand = ifaceElmObj.mand;
	                     elmObj.pattern = ifaceElmObj.pattern;
	                     elmObj.maxDec = ifaceElmObj.maxDec;
	                     elmObj.lenType = ifaceElmObj.lenType;
	                     elmObj.maxLen = ifaceElmObj.maxLen;
	                     elmObj.minLen = ifaceElmObj.minLen;
	                     elmObj.maxVal = ifaceElmObj.maxVal;
	                     elmObj.minVal = ifaceElmObj.minVal;
                        elmObj.isArray = ifaceElmObj.isArray;
                        //Citi specific property                        
                        scrMeta.elmsExtMap[params.appId + "__" + scr + "__" + ifaceElmObj.extName] = elmId;
                        cntnrObj.elmsExtMap[params.appId + "__" + scr + "__" + ifaceElmObj.extName] = elmId;
                     }
                  }
               }
            }
            elmCntr = elmCntr + noOfCntrElms;
         }
      }
      ///////Charts
      scrMeta.charts = [];
      scrMeta.chartsMap = [];
      var noOfCharts = 0;
      if (!this.isNull(def.charts)) {
         noOfCharts = def.charts.length;
      }
      if (noOfCharts > 0) {
         for (var c = 0; c < noOfCharts; c++) {
            var chartObj = def.charts[c];
            chartObj.type = "CHART";
            chartObj.id = chartObj.name;
            chartObj.dispose = true;
            ////Add to Array
            scrMeta.charts[c] = chartObj;
            scrMeta.chartsMap[chartObj.name] = chartObj;
            ////Add Nodes
/*            if (!this.isNull(chartObj.nodes)) {
               var noOfChartNodes = chartObj.nodes.length;
               if (noOfChartNodes > 0) {
                  for (var n = 0; n < noOfChartNodes; n++) {
                     var nodeId = chartObj.nodes[n];
                     chartObj.nodes[n] = nodeId;
                  }
               }
            }*/
            ////Add to Containers
            scrMeta.containers[noOfCntnrs] = chartObj;
            scrMeta.containersMap[chartObj.name] = chartObj;
            noOfCntnrs = noOfCntnrs + 1;
         }
      }
      ////Gauges
      scrMeta.gauges = [];
      scrMeta.gaugesMap = [];
      var noOfGauges = 0;
      if (!this.isNull(def.gauges)) {
         noOfGauges = def.gauges.length;
      }
      if (noOfGauges > 0) {
         for (var c = 0; c < noOfGauges; c++) {
            var gaugeObj = def.gauges[c];
            gaugeObj.type = "GAUGE";
            gaugeObj.id = gaugeObj.name;
            gaugeObj.dispose = true;
            ////Add to Array
            scrMeta.gauges[c] = gaugeObj;
            scrMeta.gaugesMap[gaugeObj.name] = gaugeObj;
/*            if (!this.isNull(gaugeObj.nodes)) {
               var noOfGaugeNodes = gaugeObj.nodes.length;
               if (noOfGaugeNodes > 0) {
                  for (var n = 0; n < noOfGaugeNodes; n++) {
                     var nodeId = gaugeObj.nodes[n];
                     //cntnrObj.nodes[n] = nodeId;
                     // nodeId for GaugeObj is not coming with appId__ . To Be Visited again
                     if(!nodeId.indexOf(apz.appId)>-1) {
                        gaugeObj.nodes[n] = apz.appId+"__"+nodeId;
                     } 
                  }
               }
            }*/
            ////Add to Containers
            if (gaugeObj.widgetCategory == "CONTAINER") {
               scrMeta.containers[noOfCntnrs] = gaugeObj;
               scrMeta.containersMap[gaugeObj.name] = gaugeObj;
               noOfCntnrs = noOfCntnrs + 1;
            }
         }
      }
      ////Groups
      scrMeta.groups = [];
      scrMeta.groupsMap = [];
      var groupnodeCntr = 0;
      var groupContainerCntr = 0;
      var noOfGroups = 0;
      if (!this.isNull(def.groups) && !this.isNull(def.groups.name)) {
         noOfGroups = def.groups.name.length;
      }
      for (var g = 0; g < noOfGroups; g++) {
         var groupObj = {};
         groupObj.name = def.groups.name[g];
         /////Nodes
         var noOfGroupNodes = def.groups.noOfNodes[g];
         groupObj.nodes = [];
         groupObj.nodesMap = [];
         if (noOfGroupNodes > 0) {
            var i = 0;
            for (var n = groupnodeCntr; n < (groupnodeCntr + noOfGroupNodes); n++) {
               var grpNode = def.groups.nodes[n];
               groupObj.nodes[i] = grpNode;
               groupObj.nodesMap[grpNode] = grpNode;
               ////Set Group for Node
               scrMeta.nodesMap[grpNode].group = groupObj.name;
               i = i + 1;
            }
            groupnodeCntr = groupnodeCntr + noOfGroupNodes;
         }
         /////Containers
         var noOfGroupContainers = def.groups.noOfContainers[g];
         groupObj.containers = [];
         // Changes to accomodate citi behaviour(Citi expects it to be object)
         groupObj.containersMap = this.getObjectOrArray()
         if (noOfGroupContainers > 0) {
            for (var c = groupContainerCntr; c < (groupContainerCntr + noOfGroupContainers); c++) {
               var grpContainer = def.groups.containers[c];
               groupObj.containers[c - groupContainerCntr] = grpContainer;
               groupObj.containersMap[grpContainer] = grpContainer;
               ////Set Group for Container
               scrMeta.containersMap[grpContainer].group = groupObj.name;
            }
            groupContainerCntr = groupContainerCntr + noOfGroupContainers;
         }
         /////Add to Group Array
         scrMeta.groups[g] = groupObj;
         scrMeta.groupsMap[groupObj.name] = groupObj;
      }
      //// Child Screens
      scrMeta.childScreens = [];
      if(!this.isNull(def.childScreens)){
    	  var noOfChildScreens = def.childScreens.name.length;
      }
      if(noOfChildScreens > 0){
    	  for(var k = 0;k < noOfChildScreens;k++){
    		  scrMeta.childScreens[k] = {};
    		  scrMeta.childScreens[k].name = def.childScreens.name[k];
    		  scrMeta.childScreens[k].type = def.childScreens.type[k];
    		  scrMeta.childScreens[k].target = def.childScreens.target[k];
           scrMeta.childScreens[k].appId = params.appId;
    	  }
      }
      /////Screen properties for page
      if (def.scrProps) {
         scrMeta.scrProps = def.scrProps;
      }
      ///////UI Inits
      scrMeta.uiInits = def.uiInits ? def.uiInits : {};
      ////INitialize
      this.initScrDef(scrMeta);
      ////Add to Store

      var args = {};
      args.appId = params.appId;
      args.scr = scr;
      args.lo = lo;
      args.template = template;
      args.def = scrMeta;
      this.storeScrDef(args);
   },
   //Pre-Project loading steps
   loadProject :function(){
      /**
       * Performance improvement(Removing Render blocking)
       * Getting style array from the global variable "styles" defined in main html and loading it
       */
      var cssLoadCount = 0;
      function asyncLoadCss(url){
         var link = document.createElement("link");
         link.type = "text/css";
         link.rel = "stylesheet";
         link.href = url;
         link.onload = cssLoaded(link.href);
         document.getElementsByTagName("head")[0].appendChild(link);
      }
      function cssLoaded(link){
         cssLoadCount ++;
         if(cssLoadCount == styles.length){
            apz.loadProjectRunner();
         }
      }
      if(window.styles){
         for(var i=0;i<styles.length;i++){
            asyncLoadCss(styles[i]);
         }
      }else{
         apz.loadProjectRunner();
      }
   },
   /////Project Loading..
   loadProjectRunner : function() {
      ///Start Loader
      this.startLoader();
      this.currAppId = this.appId;
      ////Declare Process
      var proc = {};
      proc.threads = [];
      proc.id = "PROJECTLOAD";
      proc.callBack = this.projectLoaded;
      proc.callBackObj = this;
      var params = null;
      ///////////Get Device Info///////////////////////////
      params = {};
      params.id = "DEVICEINFO";
      params.runnerObj = this.ns;
      params.runner = this.ns.getDeviceInfo;
      params.callBack = this.loadDeviceInfo;
      params.callBackObj = this;
      proc.threads[proc.threads.length] = params;
      ///////////Load Process Definition////////////////////
      /*if (Apz.mcaApp) { condition has been moved to loadProjDef function
         this.mca.getProcessJson(proc);
      }*/
	  ///////////Initialize Angular Directives////////////////////
	  if(this.bindingEngine == "ANGULAR"){
		  this.data.init();
	  }
      ///////////Load Project Definition////////////////////
      params = {};
      params.id = "PROJDEF";
      params.runnerObj = this;
      params.runner = this.getFile;
      params.callBack = this.loadProjDef;
      params.callBackObj = this;
      params.path = this.getConfigPath() + "/" + "prjdef.json";
      params.async = true;
      proc.threads[proc.threads.length] = params;
      ///////////////////////Start/////////////////////////
      this.startProc(proc);
      ///// Start Orientation Listner
      params = {};
      params.id = "STARTORIENTATIONLISTENER";
      params.callBack = this.onOrientationChange;
      params.callBackObj = this;
      this.ns.startOrientationListener(params);
      if (this.deviceOs != "MCA" && this.deviceOs != "RICT" && this.deviceOs != "WEB" && this.deviceOs != "SIMULATOR") {
          ///// Keyboard open Listner
          params = {};
          params.id = "KEYBOARDLISTENER";
          params.callBack = this.keyboardAction;
          params.callBackObj = this;
          this.ns.startKeyboardListener(params);
       }
       this.initOneTimeEvents();
   }, projectLoaded : function(proc) {
	  //To support microApp running as Main app
	  this[this.currAppId] = {}; 
      var myObj = this;
      ////Load Project Level Scripts
      var scripts = [];
      scripts = this.prjScripts;
      requirejs(scripts, function() {
         myObj.enableAnimations = true;
         if(myObj.isFunction(myObj.app.projectLoaded)){
            myObj.app.projectLoaded();
         }
         if(Apz.customizerApp && myObj.isFunction(myObj.customizer.fnDesignerInitialise)){
         	 myObj.customizer.fnDesignerInitialise();
         }
         var params = {};
         params.scr = myObj.firstPage;
         params.animation = 0;
         params.userObj = {};
         //Used only in case of standalone microApp simulator!!!
         if(myObj.isFunction(myObj[myObj.currAppId].checkParams)){
             myObj[myObj.currAppId].checkParams(params);
         }
         myObj.launchScreen(params);
      });
   }, isAppLaunched : function(appId){
      return this.appsMap[appId];
   }, resetCurrAppId : function(appId){
	  this.currAppId = appId;
   }, closeSubScreen : function(params) {
	   /* Params  Contains the below attributes
		   * appId, div, callBack, callBackObj, params
		   */
	  if(!this.isNull(params.appId) && this[params.appId]){
		  delete this[params.appId];
	  }
	  if(!this.isNull(params.div)){
		  var parentObj = document.getElementById(params.div);
		  if(!this.isNull(parentObj)){
			  parentObj.innerHTML = "";
		  }
	      if (typeof  params.callBack == "function") {
	         if (params.callBackObj) {
	              params.callBack.call(params.callBackObj, params.params);
	         } else {
	              params.callBack(params.params);
	         }
	      }
	  } else {
		  console.log("Given DIV does not exist");
	  }
   }, 
   /**
    * This plugin will be used for launching a new screen from different app (i.e. micro app).
    * @param {object} params params include
    * <br>appId: The application ID from which mention screen will be launched. 
    * <br>scr: The name of the screen to be launched. 
    * <br>animation: The animation for the screen. It takes a value from 1 to 67 (optional). 
    * <br>div: Id of the current screen target DIV element inside which the new screen is to be launched (optional). 
    * <br>layout: The layout of the screen to be launched (optional). 
    * <br>template: The template of the screen to be launched (optional). 
    * <br>userObj: Object with parameters that is passed to launched screen (optional). 
    * @example
    * var params = {};
	 *	params.appId = “appId”;
	 *	params.scr = “screenId”;
	 *	params.animation = 17;
	 *	params.div = “current_div”;
	 *	params.userObj = {“templateName”:”newtemplate”};
	 *	apz.launchApp(params);
    */

   // Bug 51554 - App keeps loading in case of network failure
   // Fhook for preLaunchActivity/////////////////////////////////////////////////////
   launchApp: function(params) {
      /* Params  Contains the below attributes
       * appId, scr, animation, div, userObj 
       */
       var shouldLaunch = true;
       if(apz.isFunction(apz.app.preLaunchActivity)){
          shouldLaunch = apz.app.preLaunchActivity('launchApp', params);
        }
  
      if (shouldLaunch || shouldLaunch === undefined) {
            params.oldAppId = this.currAppId;
          this.currAppId = params.appId;
          //this.appId = params.appId;
          if(this.isAppLaunched(params.appId)){
             this.appLoaded(params);
          } else {
             params.callBack = this.microAppDefLoaded;
             params.callBackObj = this;
             params.runnerObj = this;
             params.path = this.getConfigPath(params.appId) + "/" + "prjdef.json";
             this.getFile(params);
          }
      }
      
   }, microAppDefLoaded : function(params){
      if(params.content){
         var def = JSON.parse(params.content);
         this.scrDefsMap[params.appId] = {};
         this.firstPagesMap[params.appId] = def.props.firstPage;
         this.loDefsMap[params.appId] = {};
         if(this.customizer){
            this.customizer.config[params.appId] = {};
            this.customizer.configMeta[params.appId] = {};
         }
         if(!this[params.appId]){
            this[params.appId] = {};
         }
         this.scrHtmls[params.appId] = {};
         this.ifacesMap[params.appId] = {};
         this.loMap[params.appId] = def.loMap;
         this.msgs[params.appId] = def.msgs[this.language];
         this.lovs[params.appId] = def.lovs;
         this.scrs[params.appId] = def.screens;
         this.lits[params.appId] = def.lits;
         this.appsMap[params.appId] = {"scripts":def.scripts};
         this.appLoaded(params);
      } else {
         this.currAppId = params.oldAppId;
         var param = {"code":"APZ-CNT-226"}
         this.dispMsg(param);
      }
   }, appLoaded : function(params) {
         if(this.isNull(params.scr)) {
            params.scr = this.firstPagesMap[params.appId];
         }
         var def = this.copyObjByVal({},this.appsMap[params.appId]);
         ////add Path
         this.addScriptsPath(params.appId, def.scripts);
		 var myObj = this;
         if(myObj.isFunction(myObj.app.projectLoaded)){
             myObj.app.projectLoaded();
         }

         var scripts = [];
         scripts = def.scripts;
         /* TBC - Darshan, Bug fix for 22751
         this.undefScripts(scripts); */
         requirejs(scripts, function() {
            if(myObj.scrs[params.appId].indexOf(params.scr) > -1){
	            if(!myObj.isNull(params.div)){
	                myObj.launchSubScreen(params);
	            }else{
	            	myObj.launchScreen(params);	
	            }
            } else {
            	myObj.currAppId = params.oldAppId;
                var param = {"code":"APZ-VAL-009"}
                myObj.dispMsg(param);
            }
         });
   }, 
   /**
    * This is the plugin launches a new screen within the application with or without animation.
    * @param {object} params params include
    * <br>scr: The name of the screen to be launched. This is a mandatory field.
    * <br>animation: The animation for the screen. It takes a value from 1 to 67. (optional)
    * <br>layout: The layout of the screen to be launched.
    * <br>template: The template of the screen to be launched.
    * <br>userObj: Object with parameters that is passed to launched screen. 
    * @example
    * var params = {};
	 * params.scr = “Promotions”; 
	 * params.animation = 56;
    * apz.launchScreen (params);   
    * @description
    *  <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | onLoad_ScreenName      | This callback will be called when the script corresponding to the screen is loaded |
    * | onShown_ScreenName     | This callback will be called when the screen completes loading     |
    * @example
    * apz.app.onLoad_ScreenName = function(){
    * alert('Started Loading....')
    * }
    * @example
    * apz.app.onShown_ScreenName = function(){
    * alert('Finished Loading')
    * }
    */

   // Bug 51554 - App keeps loading in case of network failure
   // Hook for preLaunchActivity/////////////////////////////////////////////////////
   launchScreen: function(params) {
      /* Params  Contains the below attributes
          *** appId, scr, animation, userObj, template ***
      */
      var shouldLaunch = true;
      if(apz.isFunction(apz.app.preLaunchActivity)){
         shouldLaunch = apz.app.preLaunchActivity('launchScreen', params);
       }
  
      if (shouldLaunch || shouldLaunch === undefined) {
             var lo;
          if(this.isNull(params.layout)){
              lo = this.getLayout({"appId":params.appId,"scr":params.scr});
          } else {
              lo = params.layout;
          }
         var proc = {};
         proc.scr = params.scr;
         proc.lo = lo;
         proc.type = "PG";
         proc.animation = params.animation ? params.animation : 0;
         proc.template = params.template ? params.template : "";
         proc.userObj = params.userObj ? params.userObj : {};
         proc.scroll = params.scroll;
         proc.appId = params.appId ? params.appId : this.currAppId;
         proc.newDiv = this.loadDiv;
         proc.oldDiv = this.currDiv;
         this.loadScreen(proc);
      }
      
  }, 
   /**
    * This API helps you launch a new screen within a DIV element of the current screen
    * @param {object} params params include
    * <br>scr: Name of the screen to be launched. (Mandatory field)
    * <br>div: Name of the parent screen DIV element inside which the new screen is to be launched. (Mandatory field)
    * <br>animation: The animation for the sub screen .The values for this parameter will be values from 1 to 67.
    * <br>layout: The layout of the screen to be launched.
    * <br>template: The template of the screen to be launched.
    * <br>userObj: Object with parameters that is passed to launched screen.
    * @example
    * var params = {};
	 * params.scr = “Promotions”;
	 * params.div = “grid_row_1”;
	 * params.animation = 40;
    * apz.launchSubScreen(params);
    */
   // Bug 51554 - App keeps loading in case of network failure
   // Fhook for preLaunchActivity/////////////////////////////////////////////////////
   launchSubScreen: function(params) {
      /* Params  Contains the below attributes
         *** appId, scr, animation, userObj, div ***
      */
      var shouldLaunch = true;
      if(apz.isFunction(apz.app.preLaunchActivity)){
         shouldLaunch = apz.app.preLaunchActivity('launchSubScreen', params);
      }

      if (shouldLaunch || shouldLaunch === undefined) {
            params.type = "CF";
         var parent = document.getElementById(params.div);
         if(this.isNull(parent)){
            console.log("Launch subscreen failed due to invalid parent id");
         } else {
            this.launchInDiv(params);
         }
      }
   
   }, launchInDiv : function(params) {
	   /* Params  Contains the below attributes
		   *** appId, scr, animation, userObj, div, type ***
	   */
	   var lo;
	   if(this.isNull(params.layout)){
		   lo = this.getLayout({"appId":params.appId,"scr":params.scr});
	   }else{
		   lo = params.layout;
	   }
      var proc = {};
      proc.scr = params.scr;
      proc.lo = lo;
      proc.type = params.type;
      proc.animation = params.animation ? params.animation : 0;
      proc.template = params.template ? params.template : "";
      proc.userObj = params.userObj ? params.userObj : {};
      proc.scroll = params.scroll;
      proc.appId = params.appId ? params.appId : this.currAppId;
      proc.newDiv = params.div;
      ////Create Divs
      proc.origDiv = proc.newDiv;
      var div1 = proc.newDiv + "_apz_1";
      var div2 = proc.newDiv + "_apz_2";
      var html = this.getHtml(proc.newDiv);
      var divObj = document.getElementById(proc.newDiv)
      var div1Obj = document.createElement("div");
      div1Obj.setAttribute("id", div1);
      var div2Obj = document.createElement("div");
      div2Obj.setAttribute("id", div2);
      this.clearHtml(proc.newDiv);
      divObj.appendChild(div1Obj);
      divObj.appendChild(div2Obj);
      $("#" + div1).addClass("lcol12");
      $("#" + div2).addClass("lcol12 sno ssp");
      this.setHtml(div1, html);
      proc.oldDiv = div1;
      proc.newDiv = div2;
      ///Call Load
      this.loadScreen(proc);
   }, loadScreen : function(proc) {
	////Validate Screen Authorization
      var authStatus = true;
      if(!this.isNull(Apz.Server) && this.defaultAuthorization == "Y"){
         authStatus = this.server.accessScreen(proc.appId, proc.scr);
      }
      if(authStatus || proc.type=="CF"){
         if (!authStatus && (proc.type=="CF")) {
            $("#"+proc.origDiv).addClass("sno");
         }else{
        	 $("#"+proc.origDiv).removeClass("sno");
         }
       ///Start Loader
         this.startLoader();
         proc.threads = [];
         proc.id = "SCRLOAD";
         proc.callBack = this.getScreenCustomizedInfo;
         proc.callBackObj = this;
         ///SetAnimation
         if (!this.enableAnimations) {
            proc.animation = 0;
         }
         var params = null;
         ////Layout Definition
         var key = proc.scr + this.idSep + proc.lo;
         if (!this.containsKey(this.loDefsMap[proc.appId], key)) {
            /////Screen Loading Process
            //////Layout Definition
            params = {};
            params.id = "LODEF";
            params.scr = proc.scr;
            params.lo = proc.lo;
            params.appId = proc.appId;
            params.runnerObj = this;
            params.runner = this.getFile;
            params.callBack = this.loadLayoutDef;
            params.callBackObj = this;
            params.path = this.getScrDefPath(proc.appId) + "/" + proc.scr + "_" + proc.lo + ".json";
            params.async = true;
            proc.threads[proc.threads.length] = params;
         }
         ////Start Process
         this.startProc(proc);
      } else {
         var params = {"code":"APZ-NOT-ALLOWED"}
         this.dispMsg(params);
      }
   }, loadLayoutDef : function(params){
      var key = params.scr + this.idSep + params.lo;
      var appId = params.appId ? params.appId : this.currAppId;
      var def = JSON.parse(params.content);
      this.loDefsMap[appId][key] = def;
   }, getScreenCustomizedInfo : function(proc) {
      var key = proc.scr + this.idSep + proc.lo;
      proc.id = "SCRCUSTOMIZEDINFO";
      proc.callBack = this.layoutDefLoaded;
      proc.callBackObj = this;
      proc.threads = [];
      var proceed = true;
      var loDef = this.loDefsMap[proc.appId][key];
      if(this.customizer && loDef.customize == "Y"){
         var defaultTemplate = loDef.defaultTemplate;
         var template = proc.template ? proc.template : defaultTemplate;
         key = key + this.idSep + template;
         if(this.containsKey(this.customizer.config[proc.appId], key)){
            if(this.isNull(proc.template)){
               var config = this.customizer.config[proc.appId][key];
               if(config.isModified){
                  proc.template = this.customizer.config[proc.appId][key].template;
               } else {
                  proc.template = defaultTemplate;
               }
            }
            key = proc.scr + this.idSep + proc.lo + this.idSep + proc.template;
            if(!this.containsKey(this.customizer.configMeta[proc.appId], key)){
               var params = {scr:proc.scr,lo:proc.lo,appId:proc.appId};
               params.id = "CUSTOMIZERMETA";
               params.runnerObj = this;
               params.runner = this.getFile;
               params.callBack = this.updatescrCustomizedMeta;
               params.callBackObj = this;
               params.async = true;
               params.template = proc.template;
               params.path = this.getDataFilesPath(proc.appId) + "/" + proc.scr + "cz.json";
               proc.threads[proc.threads.length] = params;
            }
         } else {
        	 proceed = false;
        	 proc.launchScreen = true;
            if(this.isNull(proc.template)){
            	proc.defaultTemplate = defaultTemplate;
            }
            this.customizer.getTemplateCustomizedInfo(proc);
         }
      }
      if(proceed){
    	  this.startProc(proc);
      }
   }, updatescrCustomizedMeta : function(params) {
      var key = params.scr + this.idSep + params.lo + this.idSep + params.template;
      var def = JSON.parse(params.content);
      this.customizer.configMeta[params.appId][key] = def;
   }, layoutDefLoaded : function(params){
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
      if (!this.containsKey(this.scrHtmls[proc.appId], key)) {
         params = {};
         params.id = "SCRHTML";
         params.runnerObj = this;
         params.runner = this.getFile;
         params.callBack = this.scrHtmlLoaded;
         params.callBackObj = this;
         params.path = this.getScrPath(proc.appId) + this.getHTMLFileName(proc.scr, proc.lo, proc.template);
         params.async = true;
         proc.threads[proc.threads.length] = params;
      }
      this.startProc(proc);
   },scrDefLoaded : function(params) {
      params.proc.loadScrDef = true;
      params.proc.scrDef = params.content;
   }, scrHtmlLoaded : function(params) {
      ///Store
	   this.storeScrHtml(params.proc.appId, params.proc.scr, params.proc.lo, params.proc.template,params.content);
      params.proc.scrHtml = params.content;
   }, scrFilesLoaded : function(proc) {
      ////Load Interfaces Process
      proc.threads = [];
      proc.id = "SCRLOAD";
      proc.callBack = this.ifacesLoaded;
      proc.callBackObj = this;
      if (proc.loadScrDef) {
         var params = null;
         var def = JSON.parse(proc.scrDef);
         var noOfIfaces = 0;
         if (def.ifaces) {
            noOfIfaces = def.ifaces.length;
         }
         for (var i = 0; i < noOfIfaces; i++) {
            if (!this.containsKey(this.ifacesMap[proc.appId], def.ifaces[i])) {
               params = {};
               params.id = "IFACEDEF";
               params.appId = proc.appId;
               params.runnerObj = this;
               params.runner = this.getFile;
               params.callBack = this.loadIfaceDef;
               params.callBackObj = this;
               params.path = this.getIfaceDefPath(proc.appId) + "/" + def.ifaces[i] + ".json";
               params.async = true;
               proc.threads[proc.threads.length] = params;
            }
         }
      }
      ////Start Process
      this.startProc(proc);
   }, ifacesLoaded : function(proc) {
      ////Load Screen Meta Data
      if (proc.loadScrDef) {
         var params = {};
         params.content = proc.scrDef;
         params.appId = proc.appId;
         params.scr = proc.scr;
         params.lo = proc.lo;
         params.template = proc.template;
         params.type = proc.type;
         this.loadScrDef(params);
      }
      this.loadScrContent(proc);
   }, loadScrContent : function(proc) {
      var key = proc.scr + this.idSep + proc.lo + this.idSep + proc.template;
      var scrMeta = apz.copyObjByVal({}, this.scrDefsMap[proc.appId][key]);      
      key = this.getHTMLFileName(proc.scr, proc.lo,proc.template);
      proc.scrHtml = this.scrHtmls[proc.appId][key];
      this.childScr = proc.scr;
      proc.uiInits = scrMeta.uiInits;
      ////Update Current Screen
      if(proc.type != "CF"){
         this.currScr = proc.scr;
         this.scrMetaData = this.copyJSONObjectWithFilter(scrMeta, ["childScreens"]);
         this.scrMetaData.childScreens = this.copyJSONObject(scrMeta.childScreens);
         //this.scrMetaData = this.copyJSONObject(scrMeta);
         ////Inject HTML
         this.setHtml(proc.newDiv, proc.scrHtml);
      } else {
         this.appendScrDef(this.scrMetaData, scrMeta);
         ////Inject HTML
         var newHtml = $('<div id="scr__'+proc.appId+'__'+proc.scr+'__main"></div>');
         var links = "", fileHtml = $(proc.scrHtml);
         if(fileHtml.length > 0){
            links = fileHtml.filter("link");
         }
         newHtml = newHtml.append(fileHtml.find('.pagebody').children()).append(links);
         newHtml = newHtml[0].outerHTML;
         this.setHtml(proc.newDiv, newHtml);
      }
      
      /////Update Ids
      var ids = $('#' + proc.oldDiv + ' [id]').map(function() {
         if (this.id != proc.oldDiv) {
            this.id = this.id + "_Dummy";
            if (this.id == "sidebar_Dummy") {
               $(this).css("display", "none");
            }
         }
      });
      ////Update Classes for subscreen
      if (proc.type != "PG") {
         $("div#scr__"+ proc.appId + '__' + proc.scr + "__main .pagebody").each(function(elm, val) {//RADS32ANIM
            val.id = "page-body_" + proc.scr;
            $("div#scr__" + proc.appId + '__' + proc.scr + "__main .pagebody").removeClass("pagebody");//RADS32ANIM
            $("div#scr__" + proc.appId + '__' + proc.scr + "__main .header").removeClass("header");
            $("div#scr__" + proc.appId + '__' + proc.scr + "__main .footer").removeClass("footer");
            $("div#scr__" + proc.appId + '__' + proc.scr + "__main").removeClass("rolepage");
         });
      }
      ////Update Image paths
      if(this.defaultTheme !== this.theme){
    	  var imgArr = $('img');
    	  for(var j=0;j<imgArr.length;j++){
    		  var src = $(imgArr[j]).attr("src");
    		  src = src.split("/");
    		  src[3] = this.theme;
    		  src = src.join("/");
    		  $(imgArr[j]).attr("src",src);
    	  }
    	  var portions = $('nav');
    	  for(var k=0;k<portions.length;k++){
    		  var bgimg = $(portions[k]).css("background-image");
              bgimg = bgimg.split("/");
              bgimg.splice(-3,1,this.theme);
              bgimg = bgimg.join("/");
              $(portions[k]).css("background-image",bgimg);
    	  }
      }
      ////Load Scripts
      var myObj = this;
      var scripts = [];
      scripts = this.copyObjByVal(scripts, this.scrMetaData.scripts);
      this.undefScripts(scripts);
      requirejs(scripts, function() {
         myObj.scrScriptsLoaded(proc);
      });
   }, undefScripts : function(scriptsArr) {
      if(!this.isNull(scriptsArr) && scriptsArr.length > 0) {
         for(i=0; i < scriptsArr.length; i++) {
            require.undef(scriptsArr[i]);
         // isTestMode is defined only for web and simulator containers. For other containers Qunit is not applicable.
            if(typeof(isTestMode) == "undefined" || (typeof(isTestMode) != "undefined" && !isTestMode)){
               if(scriptsArr[i].indexOf("/testscripts/") != -1){
                   scriptsArr.splice(i, 1);
                   i--;
               }
            }
         }
      }
   }, scrScriptsLoaded : function(proc) {
      ///Initialize UI Components
      this.init(proc);
      //RADS32ANIM
      ////Execute onload Scripts 
      var onLoad = "onLoad_" + proc.scr;
      if (this.app[onLoad]) {
         //Addition of screen object to make use of load method in a common script
         var scrObj = { "appId": proc.appId, "scr": proc.scr, "layout": proc.lo, "design": proc.template };
         this.app[onLoad](proc.userObj, scrObj);
      }
      //LaunnchCallForms present in the screen
      var childScrData = this.scrMetaData.childScreens;
      if(childScrData.length > 0){
         var children = childScrData[0];
         var targetId = children.target;
         var params = {};
         params.div = targetId;
         params.scr = children.name;
         params.appId = children.appId;
         this.launchSubScreen(params);
         childScrData.shift();
      }
      this.screenLoaded(proc);
   }, screenLoaded : function(proc) {
      /// Customizer screen here as DOM corrections, initializations are done and screen ready to show to user.
      // TBC Darshan - DOM manipulated at show screen level should be handled ??

      // added code_AB
      // preScreenLoadedOrientationActivity hook is provided to the app team 
      // to handle the screen orientation at the application layer in case the user uses any browser in mobile or tablet device
      if(this.isFunction(this.app.preScreenLoadedOrientationActivity)) {
	   	var param = {};
	   	param.scrId = proc.scr;
	   	param.appId = proc.appId? proc.appId : this.currAppId;
	   	param.deviceGroupDet = this.deviceGroupDet;
	   	param.isMobileBrowser = this.checkMobileOrTabletBrowser();
	   	this.app.preScreenLoadedOrientationActivity(param);
	   }
      var key = proc.scr + this.idSep + proc.lo;
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
   }, scrShowScreen : function(proc) {
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
         $("#scr__"+proc.appId+'__'+proc.scr+"__main").unwrap();
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
         //Addition of screen object to make use of shown method in a common script
         var scrObj = { "appId": proc.appId, "scr": proc.scr, "layout": proc.lo, "design": proc.template };
         this.app[onShown](proc.userObj, scrObj);
      }
    //If running in Qunit test mode, call the below hook which will be defined in the test script.
      if(typeof(isTestMode) != "undefined" && isTestMode){
//         console.log("calling test callback.----");
         var onShownTest = "onShown_test_" + proc.scr;
         if (this.app[onShownTest]) {
               var scrObj = { "appId": proc.appId, "scr": proc.scr, "layout": proc.lo, "design": proc.template };
               this.app[onShownTest](proc.userObj, scrObj);
         }
      }
     ///// Added for Citi default products launch as subproducts should wait for the payload call to finsh from parent products
     var scrdefsKey = proc.scr + "__" + proc.lo + "__" + proc.template;
     var tempScrDefMap = this.scrDefsMap[proc.appId][scrdefsKey];
     if (proc.userObj && (this.isNull(proc.userObj.launchDefaultProduct) || proc.userObj.launchDefaultProduct)) {
         if (tempScrDefMap && !this.isNull(tempScrDefMap.defaultProducts)) {
             for (var i = 0; i < tempScrDefMap.defaultProducts.length; i++) {
                 var param = {};
                 param.div = tempScrDefMap.defaultProducts[i] + "_Scr_div";
//                 console.log("scr show :"+ param.div+":"+scrdefsKey);
                 if (!this.isNull($("#" + param.div)[0])) {
                     /* var div = document.createElement("div");
                     div.setAttribute("id", param.div);
                     div.setAttribute("class", "productRoot");
                     if ($("." + tempScrDefMap.defaultProducts[i] + "RelCntr").length > 0) {
                         $("." + tempScrDefMap.defaultProducts[i] + "RelCntr").after(div);
                     } else {
                         $("#page-body").append(div);
                     } */
                     //Creation of div block happens at products.js while the internal content load happens here//
                     if(this.isNull($("#" + param.div).children()[0])){
                         var subProdId = tempScrDefMap.defaultProducts[i];
                         param.newDiv = param.div;
                         param.origDiv = param.div;
                         param.processHtml = true;
                         param.loaderRequired = true;
                         param.procType = "CF";
                         param.productId = subProdId;
                         param.launchDefaultProduct = proc.userObj.launchDefaultProduct;
                         this.products.getScreenContent(param);
                     }
                 }
             }
         }
     }
   }, flipPages : function(proc) {
      var bkp = this.currDiv;
      this.currDiv = this.loadDiv;
      this.loadDiv = bkp;
      ///Set Visibility
      $("#" + this.loadDiv).addClass("sno");
      $("#" + this.currDiv).removeClass("sno ssp");
   }, scrInit : function(proc) {
      ///Initialize elements which can happen only on showing contents..
      this.initDependentWidgets(proc);
      ///Readjust Height, Carousel init ui ints etc...
      if(proc.animation == 0){
         this.readjustHeight();
      }
      //////Stop Loader
      this.stopLoader();
      this.ns.hideSplash({});
   }, 
   /**
    * This API will return the device window width.
    * @example
    * var scrWidth = apz.getScreenWidth();
    */
   getScreenWidth : function() {
	   /*  Response contains below value
	       *** width ***
	   */
      return $(window).width();
   }, 
   /**
    * This API will return the device window Height.
    * @example
    * var scrHeight = apz.getScreenHeight();
    */
   getScreenHeight : function() {
	   /*  Response contains below value
	       *** height ***
	   */
      return screen.height;
   }, getInfraPath: function() {
       var path = "appzillon/scripts";
       if(this.deviceOs == "MCA"){
            path = "appzillon/controllers";
        }
        return path;
    }, getInfraStylesPath: function() {
       var path = "appzillon/styles";
        return path;
    }, getScrPath: function(appId) {
         if(!appId){
            appId = this.currAppId;
         }
        var path = "apps/" + appId + "/screens/";
        if(this.deviceOs == "MCA"){
        	path = this.mca.mcaSourcepath + "views/";
        }
        return path;
    }, getScrDefPath: function(appId) {
        var path = this.getScrPath(appId) + "scrdef";
        return path;
    }, getIfaceDefPath: function(appId) {
        var path = this.getScrPath(appId) + "ifacedef";
        return path;
    }, getMockRespPath: function(appId) {
        var path = this.getScrPath(appId) + "mockresponses";
        return path;
    }, getConfigPath: function(appId) {
        var path = this.getScrPath(appId) + "config";
        return path;
    }, getDataFilesPath: function(appId) {
        var path = this.getScrPath(appId) + "datafiles";
        return path;
    }, getStylesPath: function() {
        var path = "apps/styles/themes";
        return path;
    }, getScriptsPath: function(appId) {
         if(!appId){
            appId = this.currAppId;
         }
        var path = "apps/" + appId + "/scripts";
        if(this.deviceOs == "MCA"){
        	path = this.mca.mcaSourcepath + "controllers";
        }
        return path;
    },
   ////Other Module hooks
   callLov : function(elmId, cntnrId){
	   /* Params contains the below values
	       *** elmId, cntnrId ***
	   */
	  this.lov.callLov(elmId, cntnrId);
   },
   /**
    * This API will store the given key value pair.
    * @param {string} key This is the key in which the value needs to be stored.
    * @param {string} value This is the value that is to be stored in the key.
    * @example
    * apz.store(“name”, “Appzillon”);
    */
   store : function(key, value){
	   /* Params contains the below values
	       *** key, value ***
	   */
	   this.keyValuePair[key] = value;
   },
   /**
    * This API will retrieve the value from the given key.
    * @param {string} key This is the key whose value needs to be retrieved.
    * @example
    * apz.retrieve(“name”);
    */
   retrieve : function(key){
	   /* Params contains the below value
	       *** key ***
	       * Response contains below value
	       *** value ***
	   */
	   return this.keyValuePair[key];
   },    searchRecords: function(pcontainer, psearchcontent, filterObj) {
        /* CITI Enhancement - filterObj added for column level search with following structure
        filterObj = {"elementName":["possible","matching","values"],...}
        */
        var lsearchcontent = psearchcontent.toUpperCase();
        var params = {};
        /*params.containerId = pcontainer;
        params.action = "C";
        params.tracker = [];
        params.dataPointers = [];
        this.data.setData(params);*/
        var searchRecords = true;
        if (this.isFunction(this.app.presearchRecords)) {
            searchRecords = this.app.presearchRecords(pcontainer, lsearchcontent);
        }
        if (searchRecords) {
            /*if (this.isObjectEmpty(this.data.scrDataBackup)) {
                this.data.scrDataBackup = {};
                var ifacesArr = this.scrMetaData.containersMap[pcontainer].ifaces;
                for (i = 0; i < ifacesArr.length; i++) {
                    if (!apz.isNull(this.data.scrdata[ifacesArr[i]])) {
                        this.data.scrDataBackup[ifacesArr[i]] = this.copyJSONObject(this.data.scrdata[ifacesArr[i]]);
                    }
                    if (!apz.isNull(this.data.scrdata[ifacesArr[i] + "_Req"])) {
                        this.data.scrDataBackup[ifacesArr[i] + "_Req"] = this.copyJSONObject(this.data.scrdata[ifacesArr[i] + "_Req"]);
                    }
                    if (!apz.isNull(this.data.scrdata[ifacesArr[i] + "_Res"])) {
                        this.data.scrDataBackup[ifacesArr[i] + "_Res"] = this.copyJSONObject(this.data.scrdata[ifacesArr[i] + "_Res"]);
                    }
                }
            } else {
                this.data.appendData(this.copyJSONObject(this.data.scrDataBackup));
            }*/
            var lcontainerobj = this.scrMetaData.containersMap[pcontainer];
            var lcontainertype = lcontainerobj.type;
            var lnodes = lcontainerobj.nodes;
            var node = lnodes[0];
            var mrParent = this.scrMetaData.nodesMap[node].mrParent;

            /////// Check if no child or related cntr has been searched as it might mess the backup object if rerendered
            var relCntrs = this.getRelatedContainers(pcontainer);
            var proceedSearch = true;
            if(this.isRelatedContainerSearched(relCntrs)){
                proceedSearch = false;
                if(this.isFunction(this.app.resetSearchedContainers)){
                    proceedSearch = this.app.resetSearchedContainers(relCntrs);
                }
            }
            if(proceedSearch){
            ///////// Sub search to be done on whole data for backspace handling 
            if(this.data.scrDataBackup[mrParent]){
                var scrDataPtr = this.getParentPointer(mrParent);
                var nodeName = this.getNodeName(mrParent);
                scrDataPtr[nodeName] = this.data.scrDataBackup[mrParent];
            }
            //// CITI Enhancement - Check if filterObj is having the Keys for search
            if (!this.isNull(psearchcontent) || (!this.isNull(filterObj) && (Object.keys(filterObj).length > 0))) {  
                var lnode;
                var lnoofnodes = lnodes.length;
                var actNodes = new Array();
                for (var m = 0; m < lnoofnodes; m++) {
                    var allParents = this.scrMetaData.nodesMap[lnodes[m]].parents;
                    if (allParents.length > 0) {
                        for (var x = 0; x < allParents.length; x++) {
                            if ($.inArray(apz.getNodeName(allParents[x]), actNodes) == -1) {
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
                    //// Back up the Object by reference
                    for (var k = 0; k < Object.keys(lfinaloutput).length; k++) {
                        const key = Object.keys(lfinaloutput)[k];
                        if(!this.data.scrDataBackup[key]){
                            this.data.scrDataBackup[key] = lfinaloutput[key];
                        }
                    }
                    for (var x = 0; x < lnoofnodes; x++) {
                        lnode = lnodes[x];
                        if ($.inArray(this.scrMetaData.nodesMap[lnode].mrParent, mrParentNodes) == -1) {
                            mrParentNodes.push(this.scrMetaData.nodesMap[lnode].mrParent);
                        }
                    }
                    for (var r = 0; r < mrParentNodes.length; r++) {
                        indexArr = [];
                        var mrParNode = mrParentNodes[r];
                        if (!this.containsKey(lsearchednodes, mrParNode) && lfinaloutput[mrParNode]) {
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
                                params.filterObj = filterObj;
                                var searchRes = apz.searchNodeRecords(params);
                                if(searchRes){
                                    return searchRes;
                                }
                            }, false);
                            if (!this.scrMetaData.containersMap[pcontainer].searchIndex) {
                                //this.scrMetaData.containersMap[pcontainer].searchIndex = {};
                            }
                            //this.scrMetaData.containersMap[pcontainer].searchIndex[mrParNode] = indexArr;
                            //var mrParent = this.scrMetaData.nodesMap[lnode].mrParent;
                            var parPointer = this.getParentPointer(mrParNode);
                            if (!this.isNull(parPointer)) {
                                parPointer[apz.getNodeName(mrParNode)] = lfinaloutput[mrParNode];
                            }
                        }
                    }
                }
            } else {
                delete this.data.scrDataBackup[mrParent];
                this.scrMetaData.containersMap[pcontainer].searchIndex = null;
            }
            params = {};
            params.containerId = pcontainer;
            params.dataRecNo = 0;
            params.action = "C";
            params.tracker = [];
            this.data.getData(params);
             var searchData = this.copyJSONObject(this.data.scrdata);
            /*var ifacesArr = this.scrMetaData.containersMap[pcontainer].ifaces;
            for (i = 0; i < ifacesArr.length; i++) {
                if (!apz.isNull(this.data.scrDataBackup[ifacesArr[i]])) {
                    this.data.scrdata[ifacesArr[i]] = this.copyJSONObject(this.data.scrDataBackup[ifacesArr[i]]);
                }
                if (!apz.isNull(this.data.scrDataBackup[ifacesArr[i] + "_Req"])) {
                    this.data.scrdata[ifacesArr[i] + "_Req"] = this.copyJSONObject(this.data.scrDataBackup[ifacesArr[i] + "_Req"]);
                }
                if (!apz.isNull(this.data.scrDataBackup[ifacesArr[i] + "_Res"])) {
                    this.data.scrdata[ifacesArr[i] + "_Res"] = this.copyJSONObject(this.data.scrDataBackup[ifacesArr[i] + "_Res"]);
                }
            }*/
              //////////////////////////    NO REC FOUND /////////////////////////////

                if (apz.scrMetaData.containersMap[pcontainer].totalRecs == 0) {
                    if ($('#' + pcontainer + '_row_noresult').length == 0) {
                        $('#' + pcontainer + '_tbody').prepend('<tr id="' + pcontainer + '_row_noresult"class="pri NONE animated"  animationtype="NONE" role="row"><td colspan="6" class="norecord">No Records Found</td></tr>')
                    }
                    if ($('#' + pcontainer + '_row_noresult').hasClass('sno')) {
                        $('#' + pcontainer + '_row_noresult').removeClass('sno')
                    }

                } else {
                    if (!$('#' + pcontainer + '_row_noresult').length == 0 && !$('#' + pcontainer + '_row_noresult').hasClass('sno')) {
                        $('#' + pcontainer + '_row_noresult').addClass('sno')
                        $('.tabl-ctr').css('height', (parseFloat($('.tabl-ctr').css('height')) - 36).toString())
                    } else
                        $('#' + pcontainer + '_row_noresult').addClass('sno')
                }

                ///////////////////////////////////////////////////////////////////////
            }
        }
        if (this.isFunction(this.app.postsearchRecords)) {
               this.app.postsearchRecords(pcontainer, lsearchcontent,searchData);
        }
    },
    searchNodeRecords: function(params) {
        var pparentobj = params.parentObj;
        var pobj = params.obj;
        var psearchcontent = params.searchContent;
        var pnodes = params.actNodes;
        var psearchednodes = params.searchedNodes;
        var indexArr = params.indexArr;
        var index = params.index;
        var containerId = params.containerId;
        var mrParentNode = params.mrParentNode;
        var currentNode = params.currentNode;
        var containerElms = this.scrMetaData.containersMap[containerId].elmsMap;
        var actNodeId = "";
        var lsearchednodes = psearchednodes;
        var filterObj = params.filterObj;
        var matchedCount = 0;
        for (i in pobj) {
            if (!apz.isNull(pobj[i])) {
                var lval = pobj[i].toString().toUpperCase();
                if ($.type(pobj[i]) != "object" ){ 
                    var elmId = currentNode + "__" + i;
                    //// Backward compatibility for table level search
                    if(!apz.isNull(psearchcontent) && lval.indexOf(psearchcontent) != -1){
                        if (containerElms[elmId]) {
                             return pparentobj;
                        }
                    }
                    //// CITI Enhancement - Table column level search
                    else if(!apz.isNull(filterObj) && !(apz.isNull(filterObj[i])) && filterObj[i].indexOf(lval) != -1){
                        if (containerElms[elmId]) {   
                             matchedCount++;
                        }
                    }
                }else if ($.type(pobj[i]) == "object" && $.inArray(i, pnodes) != -1) {
                    var presNode = mrParentNode;
                    var elmIdIndex = presNode.lastIndexOf("__");
                    var nodePref = presNode.substring(0, elmIdIndex + 2);
                    var newCurrentNode = nodePref + i;
                    lsearchednodes.push(nodePref + i);
                    var newObj = this.copyJSONObject(params);
                    newObj.obj = pobj[i];
                    newObj.searchedNodes = lsearchednodes;
                    newObj.currentNode = newCurrentNode;
                    var litem = apz.searchNodeRecords(newObj);
                    if (!apz.isNull(litem)) {
                        return litem;
                    }
                }
            }
        }
        //// CITI Enhancement - Return row only if all filters matches
        if(!apz.isNull(filterObj) && matchedCount == Object.keys(filterObj).length){
             return pparentobj;
        }
    },sortRecords : function(params) {
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
      this.data.setData(obj);
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
      obj = {};
      obj.containerId = containerId;
      obj.dataRecNo = 0;
      obj.action = "C";
      obj.tracker = [];
      this.data.getData(obj);
   },  sortRows : function(params) {
	   /* Params contains the below attributes
	     *** containerId, element, sortType ,node ***
	     */
      var containerId = params.container;
      var node = params.node;
      var element = params.element;
      var sortType = params.sortType;
      var containerObj = this.scrMetaData.containersMap[containerId];
      var containerType = containerObj.type;
      var proceed = true;
	   var lnodes = containerObj.nodes;
      if(this.isFunction(this.app.preSortRows)){
         proceed = this.app.preSortRows(containerId,node,element,sortType);
      }
      if (proceed) {
         if (containerType == "TABLE" || containerType == "LIST") {
             var finalOutput = this.getAllRecords(containerObj);
             var nodeElm = element;
             nodeElm = nodeElm.substring(0, nodeElm.length - 2);
             var elmType = containerObj.elmsMap[nodeElm].dataType;
             var elmName = containerObj.elmsMap[nodeElm].name;
             // currNode is built by splitting element id which would mismatch for elements under default node-krish-28.06//
             /*var elmNodeNameArr = nodeElm.split("__");
             var elmNodeName = elmNodeNameArr[0] + "__" + elmNodeNameArr[1] + "__" + elmNodeNameArr[2] + "__" + elmNodeNameArr[3];
             params.currNode = elmNodeName;*/
             params.node = node;
             params.currElement = elmName;
             params.elmType = elmType;
             params.reverse = params.sortType;
             finalOutput[node].sort(this.sortNode(params));
             var ifaceID = apz.scrMetaData.nodesMap[node].ifaceName;
             var dmlType = apz.scrMetaData.nodesMap[node].dml;
             if (dmlType == "") {
                 dmlType = "REQ";
             }
             ifaceID = apz.getIfaceIdFromDml(ifaceID, dmlType);
             if (!this.isNull(this.data.scrdata[ifaceID])) {
                 var parentPtr = this.data.getParentDataPointer(node, 0);
                 var nodeName = this.getNodeName(node);
                 parentPtr[nodeName] = finalOutput[node];
             } else {
                 this.data.scrdata[ifaceID] = {};
                 var parentPtr = this.data.getParentDataPointer(node, 0);
                 var nodeName = this.getNodeName(node);
                 parentPtr[nodeName] = finalOutput[node];
             }
         }
      }
      if(this.isFunction(this.app.postSortRows)){
        this.app.postSortRows(containerId,node,element,sortType);
      }
   }, sortNode : function(params) {
      var reverse = params.reverse;
      var element = params.currElement;
      var currNode = params.node;
      var currNodeName = apz.getNodeName(currNode);
      var parentNode = params.parentNode;
      var elmType = params.elmType;
      var apzObj = this;
      reverse = !reverse ? 1 : -1;
      //APPFIX FOR DATE/DATE TIME SORT
      return function(a, b) {
         var currElmNodeParentNode = "";
         var nodesArr = [];
         var getNodePath = function(currNode) {
            if (apz.scrMetaData.nodesMap[currNode].relType!="1:N") {
               var allParentNodes = apz.scrMetaData.nodesMap[currNode].parents;
               for(var x=allParentNodes.length; x>0; x--) {
                  if (apz.scrMetaData.nodesMap[allParentNodes[x-1]].relType == "1:N") {
                     if (nodesArr.length==0) {
                        nodesArr.push(currNode);
                     }
                     break;
                  } else {
                     nodesArr.push(allParentNodes[x-1]);
                  }
               }
            } else {
               nodesArr = "";
            }
            return nodesArr;
         }
         var getNodeValue = function(nodePath,obj,currNode,currElm) {
            var nodesLen = nodePath.length;
            var val = "";
            var newObj = obj;
            if (nodesLen>0) {
               for(var q=0; q<nodesLen; q++) {
                  var nodeName = apz.getNodeName(nodePath[q]);
                  newObj = newObj[nodeName];
               }
               if (currNode==apz.getNodeName(nodePath[nodesLen-1])) {
                  val = newObj[currElm];
               } else {
                  if (newObj[currNode]) {
                     val = newObj[currNode][currElm];
                  } else {
                     val = {};
                  }
               }
               
            } else {
               val = obj[currElm];
            }
            return val;
         }
         var valNodePath = getNodePath(currNode);
         var val1 = getNodeValue(valNodePath, a, currNodeName, element);
         var val2 = getNodeValue(valNodePath, b, currNodeName, element);
         if (elmType == "DATE") {
             var fromFormat = apzObj.getServerDateFormat(currNode);
             var toFormat = apzObj.dateFormat;
             //formatDate expects an object
             val1 = apz.formatDate({ val: val1, fromFormat: fromFormat, toFormat: toFormat });
             val2 = apz.formatDate({ val: val2, fromFormat: fromFormat, toFormat: toFormat });
            //  return a, b, reverse * ((Date.parseExact(encodeURIComponent(val1), encodeURIComponent(toFormat)) > Date.parse(encodeURIComponent(val2), encodeURIComponent(toFormat))) - (Date.parse(encodeURIComponent(val2), encodeURIComponent(toFormat)) > Date.parse(encodeURIComponent(val1), encodeURIComponent(toFormat))));
            //date data type sorting issue
            return a, b, reverse * ((Date.parseExact(encodeURIComponent(val1), encodeURIComponent(toFormat)) > Date.parseExact(encodeURIComponent(val2), encodeURIComponent(toFormat))) - (Date.parseExact(encodeURIComponent(val2), encodeURIComponent(toFormat)) > Date.parseExact(encodeURIComponent(val1), encodeURIComponent(toFormat))));
         } else if (elmType == "DATETIME") {
             var fromFormat = apzObj.getServerDateTimeFormat(currNode);
             var toFormat = apzObj.dateTimeFormat;
             //formatDate expects an object
             val1 = apz.formatDate({ val: val1, fromFormat: fromFormat, toFormat: toFormat });
             val2 = apz.formatDate({ val: val2, fromFormat: fromFormat, toFormat: toFormat });
            //  return a, b, reverse * ((Date.parseExact(encodeURIComponent(val1), encodeURIComponent(toFormat)) > Date.parse(encodeURIComponent(val2), encodeURIComponent(toFormat))) - (Date.parse(encodeURIComponent(val2), encodeURIComponent(toFormat)) > Date.parse(encodeURIComponent(val1), encodeURIComponent(toFormat))));
            // added code_AB
            // Bug 53913 - DateTime data type sorting/formatting issue in Appzillon 3.7.5 version
            return a, b, reverse * ((Date.parseExact(encodeURIComponent(val1), encodeURIComponent(toFormat)) > Date.parseExact(encodeURIComponent(val2), encodeURIComponent(toFormat))) - (Date.parseExact(encodeURIComponent(val2), encodeURIComponent(toFormat)) > Date.parseExact(encodeURIComponent(val1), encodeURIComponent(toFormat))));
         }else if(elmType == "NUMBER"){
            return a, b, reverse * (val1 - val2);
         }else{
            return a, b, reverse * ((val1 > val2) - (val2 > val1));            
         }
      };
   }, getAllRecords : function(containerObj) {
      var finalOutput = {};
      var dataPointer;
      var nodes = containerObj.nodes;
      var noOfNodes = nodes.length;
      var node = nodes[0];
      var mrParent = this.scrMetaData.nodesMap[node].mrParent;
      var totalRecs = this.data.getNoOfRecs(mrParent);
      for (var r = 0 ; r <= totalRecs ; r++) {
         for(var n = 0; n < noOfNodes; n++) {
            node = nodes[n];
            mrParent = this.scrMetaData.nodesMap[node].mrParent;
            if (node!=mrParent || (node==mrParent && this.scrMetaData.nodesMap[node].relType=="1:N")) {
               if (r == 0) {
                  finalOutput[mrParent] = [];
               }
               dataPointer = this.data.getDataPointer(mrParent, r);
               if (dataPointer !== null) {
                  finalOutput[mrParent][r] = dataPointer;
               }
            }
         }
      }
      return finalOutput;
   }, getParentPointer : function (nodeId) {
      var lparent = this.scrMetaData.nodesMap[nodeId].parent;
      var parentCurrRec = -1;
      ////Get Parent's Current Record...
      if (!this.isNull(lparent)) {
         if (this.scrMetaData.nodesMap[lparent]) {
            parentCurrRec = this.scrMetaData.nodesMap[lparent].currRec;
         } else {
            parentCurrRec = 0;
         }
      } else {
         parentCurrRec = -1;
      }
      var lpointer = this.data.getDataPointer(lparent, parentCurrRec);
      return lpointer;
   }, getCurrTimeStamp : function() {
	   return new Date().format("%a, %d-%m-%Y %H:%M:%S");
   }, swipeIdsForImage : function(obj){
      var tagName = obj.tagName;
      var tagId = obj.id;
      if (tagName == "IMG") {
         $("#" + tagId + "_svg").attr("id", tagId).removeClass("sno");
         $(obj).attr("id", tagId + "_img").addClass("sno");
      } else {
         $("#" + tagId + "_img").attr("id", tagId).removeClass("sno");
         $(obj).attr("id", tagId + "_svg").addClass("sno");
      }
      return $("#" + tagId)[0];
   }, getLayoutForGroup: function(params) {      
      //Method expects scr{screen name},device group,appId,orientation
      //returns the layout attached
      layout = this.loMap[params.appId][params.scr][params.deviceGroup][params.orientation];
      return layout;
   }, getLayoutTemplates: function(params) {
      //Method expects scr{screen name},layout,appId,callBack
      //Returns all the temlplates as an array to the callBack
      var path = "apps/" + params.appId + "/screens/scrdef" + "/" + params.scr + "_" + params.layout + ".json";
      if (this.deviceOs == "MCA") {
         path = "apps/" + params.appId + "/views/scrdef" + "/" + params.scr + "_" + params.layout + ".json";
      }
      var proc = {};
      proc.threads = [];
      params.userCB = params.callBack;
      params.id = "LODEF";
      params.runnerObj = this;
      params.runner = this.getFile;
      params.callBack = this.getLayoutTemplatesCB;
      params.callBackObj = this;
      params.path = path;
      params.async = true;
      proc.threads[proc.threads.length] = params;
      ////Start Process
      this.startProc(proc);
   }, getLayoutTemplatesCB: function(params) {
      var templates = JSON.parse(params.content).designs;
      if (this.isFunction(params.userCB)) {
         params.userCB(templates);
      }
   }, 
   /**
    * This API returns the ID of the data element.
    * @param {Object} params  params include the following params attribues.
    *                         <br>appId:  This attribute indicates application Id of the app.(optional) if not provided current application ID will be considered.
    *                         <br>ifaceId: This attribute indicates Interface name of the mapped element.
    *                         <br>dmlType: This attribute indicates node type is request or Response( I/O).
    *                         <br>nodeName: This attribute indicates node name of the mapped element.
    *                         <br>elmName: This attribute indicates element name in the interface.
    * @example
    * newFunc = function(){
	 *	var params = {};
	 *	params.ifaceId = “dbservice”;
	 *	params.dmlType = “i”;
	 *	params.nodeName = “tbAccountMast”;
	 *	params.elmName = “acno”;
    *  }
    * apz.getDataObjId(params);
    */
   getDataObjId : function(params){
      var appId = params.appId ? params.appId : this.currAppId;
      return appId + this.idSep + params.ifaceId + this.idSep + params.dmlType + this.idSep + params.nodeName + this.idSep + params.elmName;
   }, 
   /**
    * This API returns the ID of the UI element (i.e. non data modal element).
    * @param {Object} params params include the following params attribues.
    * <br>appId: This attribute indicates application Id of the app.(optional) if not provided current application ID will be considered.
    * <br>scrId: This attribute indicates screen ID of the element(optional).If not given this value current screenId will be considered.
    * <br>elmName: This attribute indicates element ID given in the designer.
    * @example
    * newFunc = function(){
	 *	var params = {};
	 *	params.scrId = “Account”;
	 *	params.elmName = “acno”;
    * }
    *	apz.getUIObjId(params);
    */
   getUIObjId : function(params){
      var appId = params.appId ? params.appId : this.currAppId;
      var scrId = params.scrId ? params.scrId : this.currScr;
      return appId + this.idSep + scrId + this.idSep + params.elmName;
   }, 
   /**
    * This API returns the object of the data element.
    * @param {Object} params params include the following params attribues.
    * <br>appId: This attribute indicates application Id of the app.(optional) if not provided current application ID will be considered.
    * <br>ifaceId: This attribute indicates Interface name of the mapped element.
    * <br>dmlType: This attribute indicates node type is request or Response( I/O).
    * <br>nodeName: This attribute indicates node name of the mapped element.
    * <br>elmName: This attribute indicates element name in the interface. 
    * @example
    * newFunc = function(){
	 *	var params = {};
	 *	params.ifaceId = “dbservice”;
	 *	params.dmlType = “i”;
	 *	params.nodeName = “tbAccountMast”;
    * params.elmName = “acno”;
    * }
    * apz.getDataObj(params);
    */
   getDataObj : function(params){
      return document.getElementById(this.getDataObjId(params));
   }, 
   /**
    * This API returns the object of the UI element (i.e. non data modal element).
    * @param {Object} params params include the following attributes.
    * <br>appId: This attribute indicates application Id of the app.(optional) if not provided current application ID will be considered.
    * <br>scrId: This attribute indicates screen ID of the element(optional).If not given this value current screenId will be considered.
    * <br>elmName: This attribute indicates element ID given in the designer.
    * @example
    * newFunc = function(){
	 *	var params = {};
	 *	params.scrId = “Account”;
	 *	params.elmName = “acno”;
    * }
    *	apz.getUIObj(params);
    */
   getUIObj : function(params){
      return document.getElementById(this.getUIObjId(params));
   }, updatePaginationRecords : function(containerId) {
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
            // added code_AB
            if(!this.isNull(selectedObj) && selectedObj.tagName === 'INPUT' && selectedObj.type === 'number') {
               if (selectedPageSize < 1) {
                  selectedPageSize = 1;
                  selectedObj.value = 1;
               } else if (selectedPageSize > containerData.totalRecs) {
                  selectedPageSize = containerData.totalRecs;
                  selectedObj.value = containerData.totalRecs;
               }
               var selectedObjMinVal = selectedObj.getAttribute('min');
               var selectedObjMaxVal = selectedObj.getAttribute('max');
               if (!selectedObjMinVal) {
                  selectedObj.setAttribute('min', 1);
               } 
               if (!selectedObjMaxVal) {
                  selectedObj.setAttribute('max', containerData.totalRecs);
               }
            }
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
               params.containerId = containerId;
               params.dataRecNo = dataRecNo;
               params.action = "D";
               this.data.goToRecord(params);
            }
         //}
      }
      if(this.isFunction(this.app.postUpdatePageSize)){
         this.app.postUpdatePageSize(containerId);
      }
   }, populatePaginationDropdown : function(obj,options) {
      this.clearHtml(obj.id);
      for (var i = 0; i < options.length; i++) {
         opt = document.createElement('option');
         opt.value = options[i].val;
         opt.innerHTML = options[i].desc;
         obj.appendChild(opt);
      }
   }, populatePaginationInputStep : function(obj, params) {
      this.clearHtml(obj.id);
      var value = params.val;
      obj.value = value;
   }, backUpReq : function(req) {
	   /* Params contains the below attributes
	       *** id, callBackObj, callBack, proc, fwdData, runner, runnerObj ***
	   */
	   if (req) {
	      if (this.isNull(req.id)) {
	         req.id = this.getProcId();
	      }
	      var cbDet = this.copyJSONObjectWithFilter(req,[]);
	      cbDet.id = req.id;
	      //Reqiured to call thread Completed of the current instance
	      Apz.apzNativeServiceDet[req.id] = cbDet;
	      ///Remove circular dependency
	      if (req.proc) {
	          req.proc = null;
	      }
	      if (req.fwdData) {
	          req.fwdData = null;
	      }
	      if (req.runnerObj) {
	          req.runnerObj = null;
	      }
	      if (req.runner) {
	          req.runner = null;
	      }
	      if (req.callBack) {
	          req.callBack = null;
	      }
	      if (req.callBackObj) {
	          req.callBackObj = null;
	      }
	      if (req.userCallBack) {
	          req.userCallBack = null;
	      }
	      if (req.userCallBackObj) {
	          req.userCallBackObj = null;
	      }
	   }
	},getDefaultDesign: function(params) {
        var appId = params.appId ? params.appId : this.currAppId;
        var scr = params.scr ? params.scr : this.currScr;
        var layout = params.layout ? params.layout : this.getLayout(params);
        var key = scr + this.idSep + layout,
            design = "";
        if (!this.containsKey(this.loDefsMap[appId], key)) {
            design = "D0";
        } else {
            var designInfo = this.loDefsMap[appId][key];
            design = designInfo.defaultTemplate;
        }
        return design;
    },
    customUnFormatNumber: function(obj) {
        var value = $(obj).val();
        var inpValue = apz.unFormatNumber({ "decimalSep": apz.decimalSep, "displayAsLiteral": "N", "value": value });
        if(inpValue.indexOf("(")>-1){
            inpValue =  "-"+inpValue.replace(/[()]/g,"");
        }
        $(obj).val(inpValue);
    },
    validateChar: function(obj, evt) {
        var inpValue = $(evt.target).val();
        var deimalFlag = false;
        var decimalRepeatFlag = false;
        var allowed = false;
        var dotRestrict = true;
        var dotPosition = 15;
        var lengthValue = 18;
        var lid = apz.getObjIdWORowNumber(evt.target);
        var lelemobj = this.scrMetaData.elmsMap[lid];
        var length = inpValue.length;
        var minMaxError = "";
        var lmaxval ="";
        var lminval ="";
        var negativeNumber = true;
        var charAllowed = false;
        var allowDot = false;
        var targetSelectionStart = evt.target.selectionStart;
        if (inpValue.indexOf("-") > -1 || evt.key=="-") {
            dotPosition = 16;
            lengthValue = 19;
        }
        if(inpValue.indexOf("-") ==0 && targetSelectionStart<=inpValue.indexOf("-")){
            charAllowed = true;
        }
        if(evt.key=="." && inpValue.length-targetSelectionStart>2){
            allowDot = true;
        }
        if (inpValue.indexOf(".") > -1) {
                var inpSplitValue = inpValue.split(".");
                if (inpSplitValue[1].length >= 2 && targetSelectionStart > inpValue.indexOf(".") && !(evt.which == 46)) {
                    deimalFlag = true;
                }
                if (evt.key == ".") {
                    decimalRepeatFlag = true;
                }
                if (inpSplitValue[0].length >= dotPosition && targetSelectionStart >= inpValue.indexOf(".")) {
                    allowed = true;
                    if (inpSplitValue[1].length <= 2 && targetSelectionStart >= dotPosition) {
                        allowed = false;
                    }
                }
                if (inpValue.indexOf(".") <= dotPosition) {
                    dotRestrict = false;
                }
        }
        var allowNegative = false;
        var charCode = (evt.which) ? evt.which : evt.keyCode;
        var targetSelectionStart = evt.target.selectionStart;
        if (!apz.isNull(lelemobj)) {
        /* checking existance of attributes  */
            if(!apz.isNull(lelemobj.minVal)){
             lminval = parseInt(lelemobj.minVal);
            }
            if(!apz.isNull(lelemobj.maxVal)){
             lmaxval = parseInt(lelemobj.maxVal);
            }
        }
        if (targetSelectionStart == 0 && charCode == 45) {
            if (apz.isNull(lminval) ||(!apz.isNull(lminval) && lminval < 0)) {
                allowNegative = true;
            }

        }
        if(lelemobj.dataType == "INTEGER" && evt.key == "."){
             minMaxError = "inValidNumber";
             allowDot = true;
        }
        if(evt.key=="-"&& length<=15&&targetSelectionStart==0){
            if(!apz.isNull(lminval) && Number('-'+inpValue) > lminval){
                negativeNumber = false;
            }
        }

        if ((charCode > 31 && (charCode != 46 && (charCode < 48 || charCode > 57 || charCode == 45)) && !allowNegative) ||
            (evt.key != "." && length == dotPosition && dotRestrict) || length >= lengthValue || deimalFlag || decimalRepeatFlag || allowed || charAllowed || allowDot ) {
          if(negativeNumber){  
                evt.preventDefault();
                minMaxError = "inValidNumber";
            }
        }
       if (!this.isNull(lelemobj)) {
            var inputValue = (inpValue.slice(0,targetSelectionStart) + evt.key + inpValue.slice(targetSelectionStart));
            if (!apz.isNull(lminval)) {
                if (inputValue == "-" && lminval > 0) {
                    minMaxError = "APZ-VAL-006";
                } else if (inpValue + evt.key !== "-" && inputValue < lminval) {
                    minMaxError = "APZ-VAL-006";
                }
            }
            if (!apz.isNull(lmaxval)) {
                if (inputValue > lmaxval) {
                    minMaxError = "APZ-VAL-005";
                }
            }
        }
        this.showPopUpOnValidationError(minMaxError,obj,lelemobj,evt);
    },
    showPopUpOnValidationError :function(minMaxError,obj,lelemobj,evt){
         if (apz.isNull(minMaxError)) {
            if ($(obj).data("bs.popover")) {
                $(obj).popover('disable');
                $(obj).popover('hide');
            }
        } else {
            var msg = "";
            var lminval = lelemobj.minVal;
            var lmaxval = lelemobj.maxVal;
            if (minMaxError == "APZ-VAL-006") {
                msg = apz.msgs[apz.appId]["APZ-CITI-LTMIN"];
                if (!apz.isNull(msg)) {
                    (msg.indexOf("$min") > -1) ? msg = msg.replace("$min", lminval): msg = msg;
                    msg = msg.slice(1);
                }
                //evt.preventDefault();
            } else if (minMaxError == "APZ-VAL-005") {
                msg = apz.msgs[apz.appId]["APZ-CITI-GTMAX"];
                if (!apz.isNull(msg)) {
                    (msg.indexOf("$max") > -1) ? msg = msg.replace("$max", lmaxval) : msg = msg;
                    msg = msg.slice(1);
                }
                if(evt)
                evt.preventDefault();
            } else {
                msg = apz.msgs[apz.appId]["APZ-CITI-INVNUM"];
                if (!apz.isNull(msg)) {
                    (msg.indexOf("$min") > -1) ? msg = msg.replace("$min", lminval) : msg = msg;
                    (msg.indexOf("$max") > -1) ? msg = msg.replace("$max", lmaxval) : msg = msg;
                    msg = msg.slice(1);
                }
                if(evt)
                evt.preventDefault();
            }
            if (!apz.isNull(msg)) {
            /* msg changes for min max : investor  */
                if (apz.isNull(lminval) && apz.isNull(lmaxval)) {
                    if(lelemobj.decorations){
                        var decorsLen = lelemobj.decorations.length;
                            for(var l=0; l<decorsLen; l++){
                                var decorName = lelemobj.decorations[l].name.split('_')[2];
                                if (decorName.indexOf('BasicRuleSet') > -1) {
                                    var decorVal = lelemobj.decorations[l].value.split('|')[0];
                                    if (decorVal == "Num") {
                                        msg = apz.msgs[apz.appId]["APZ-CITI-INVNUM1"];
                                    } else if (decorVal == "FTxtC") {
                                        msg = apz.msgs[apz.appId]["APZ-CITI-INVNUM2"];
                                    }
                                    if (!apz.isNull(msg)) {
                                        msg = msg.slice(1);
                                    }
                                }
                            }                   
                        } else {
                        msg = apz.msgs[apz.appId]["APZ-CITI-INVNUM1"];
                        if (!apz.isNull(msg)) {
                            msg = msg.slice(1);
                        }
                    }
                }
            }
        }
        if (!apz.isNull(msg)) {
            if (apz.isNull($(obj).data("bs.popover"))) {
                this.initializeErrorPopover(obj.id, msg);
            } else {

                if ($(obj).data("bs.popover")) {
                    var popContent = '<span class="numbrError"><svg aria-hidden="true" role="presentation" class="ett-icon icon-error px20"><use xlink:href="#icon-error"></use></svg>' + msg + '</span>';
                    $(obj).data('bs.popover').options.content = popContent;
                    $(obj).popover('enable');
                    $(obj).popover('show');
                }
            }
        }
    },
    initializeErrorPopover: function(id, msg) {
        // if (!apz.isNull(apz.getElmValue(id))) {
        var objId = apz.getObjIdWORowNumber(document.getElementById(id));
        var placement = "bottom";
        if (apz.scrMetaData.containersMap[apz.scrMetaData.elmsMap[objId].container].type === "TABLE") {
            placement = "top";
        }
        $("#" + id).popover({
            html: true,
            placement: placement,
            trigger: 'focus',
            content: '<span class="numbrError"><svg aria-hidden="true" role="presentation" class="ett-icon icon-error px20"><use xlink:href="#icon-error"></use></svg>' +
                msg + '</span>',
        });
        $("#" + id).popover('show');
        // }
    },
    hidePopOver: function(obj) {
        if (!apz.isNull($(obj).data("bs.popover"))) {
            $(obj).popover('disable');
            $(obj).popover('hide');
        }

    },
    getRelatedContainers: function(containerId, containersList) {
        var contData = this.scrMetaData.containersMap[containerId];
        var group, groupData, container, nodes;
        if(contData){
            group = contData.group;
        }
        if(this.isNull(containersList)) {
            containersList = [];
            containersList.push(containerId);
        }     
        if (apz.isNull(group)) {
            ////Group is Null..
        } else {
            groupData = apz.scrMetaData.groupsMap[group];
            lnoofcontainers = groupData.containers.length;
            if (lnoofcontainers > 0) {
                for (var c = 0; c < lnoofcontainers; c++) {
                    container = groupData.containers[c];
                    if(!(containersList.indexOf(container) > -1) && containerId != container) {
                        containersList.push(container);
                    }
                }
            }
            nodes = groupData.nodes;                
        }
        if(!apz.isNull(nodes)) {
            var noOfNodes = nodes.length;
            var nodeId = "";
            var childNode;
            var noOfChilds = 0;
            if(noOfNodes > 0) {
                for(var n = 0; n < noOfNodes; n++) {
                    nodeId = nodes[n];
                    noOfChilds = apz.scrMetaData.nodesMap[nodeId].childs.length;
                    if(noOfChilds > 0) {
                        for(var c = 0; c < noOfChilds; c++) {
                            childNode = apz.scrMetaData.nodesMap[nodeId].childs[c];
                            var group = apz.scrMetaData.nodesMap[childNode].group;
                            if(!apz.isNull(group)) {
                                var containers = apz.scrMetaData.groupsMap[group].containers;
                                if (containers.length > 0) {
                                    container = containers[0];
                                    // Only child containers
                                    if(!(containersList.indexOf(container) > -1)) {
                                        containersList.push(container);
                                        this.getRelatedContainers(container, containersList);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return containersList;
    },
    isRelatedContainerSearched: function(relCntrs){
        var relCntrSearched = false;
        for (var c = 1; c < relCntrs.length; c++) {
            var cntrId = relCntrs[c];
            var cntrData = this.scrMetaData.containersMap[cntrId];
            var mrNode = cntrData ? cntrData.nodes[0] : "";
            if(this.data.scrDataBackup[mrNode]){
               relCntrSearched = true;
               break;
            }
        }
        return relCntrSearched;
    },
   preventTPSInclude : function() {
      // VAPT :: IS Audit :: Cross Domain Script Include changes
      const observer = new MutationObserver(mutations => {
         mutations.forEach(({ addedNodes }) => {
            addedNodes.forEach(node => {
               if(node.nodeType === 1 && node.tagName === 'SCRIPT') {
                  const src = node.src || '';
                  const type = node.type;
                  if (src && !src.includes(window.location.origin + window.location.pathname)) {
                     // Blocks the script tag execution in Safari, Chrome, Edge & IE
                     node.type = 'javascript/blocked';
                     node.parentElement.removeChild(node);
                     if (window.stop) {
                        window.stop();
                     }
                     var isFirefox = typeof InstallTrigger !== 'undefined';
                     if (isFirefox) {
                        // Blocks the script tag execution in Firefox
                        const beforeScriptExecuteListener = function (event) {
                           if(node.getAttribute('type') === 'javascript/blocked')
                              event.preventDefault()
                           node.removeEventListener('beforescriptexecute', beforeScriptExecuteListener)
                        }
                        node.addEventListener('beforescriptexecute', beforeScriptExecuteListener);
                     }
                     var error = new Error('Content Security Policy: The page\'s settings blocked the loading of a resource at ' + src);
                     console.error(error.message);
                     // console.log(error.stack);
                  }
               }
            })
         })
      })
      // Starts the monitoring
      observer.observe(document.documentElement, {
         childList: true,
         subtree: true
      })
   },
   onDpDwnChg:function(pObj){
      var selVal = pObj.value;
      var lId = $(pObj).parents(".editable-container.editable-inline").siblings("a")[0].id;
      $("#"+lId).attr("value",selVal);
      $(pObj).closest('form').submit();
   },
   downloadCoverageOutput: function(){ function download() {
	   var fileContents = JSON.stringify(__coverage__); var fileName = "coverage-output.json";
	   var pp = document.createElement('a'); pp.setAttribute('href', 'data:text/plain;charset=utf-8,' +
	   encodeURIComponent(fileContents)); pp.setAttribute('download', fileName); pp.click();
	   } setTimeout(function() {
	   download() }, 100);
   },
   getObjectOrArray:function(){
    return [];
   },
   resetApplication : function(){
      apz.appsMap={};
   }
};

//// Internet Explorer support extensions
if (typeof String.prototype.startsWith != 'function') {
    String.prototype.startsWith = function(value) {
        return this.substring(0, value.length) === value;
    }
};
if (typeof String.prototype.endsWith != 'function') {
    String.prototype.endsWith = function(str) {
        return this.substring(this.length - str.length, this.length) === str;
    }
};
if (typeof String.prototype.includes != 'function') {
    String.prototype.includes = function(str) {
        return this.indexOf(str) != -1 ? true : false;
    }
};
if (typeof String.prototype.trim !== 'function') {
    String.prototype.trim = function() {
        return this.replace(/^\s+|\s+$/g, '');
    }
};
if (typeof String.prototype.charAt !== 'function') {
    String.prototype.charAt = function(pos) {
        return this.substr(pos, 1);
    }
};
