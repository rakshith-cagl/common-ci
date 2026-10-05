/**
 * 
 * @namespace 
 */
Apz.Server = function(apz) {
   this.apz = apz;
   this.appSecToken = "N";
   this.loginStatus = false;
};
///////////////////Prototype Definition///////////////////////
Apz.Server.prototype = {
   getLocation: function(addrReq) {
	   /* Params contains the below value
	       *** addrReq ***
	   */
		if (addrReq) {
			this.apz.fetchAddr = true;
		} else {
			this.apz.fetchAddr = false;
		}
		nsReq = {};
		nsReq.id = "GETLOCATION";
		nsReq.callBack = this.setLocation;
		nsReq.callBackObj = this;
		this.apz.ns.getLocation(nsReq);
	}, setLocation: function(params) {
		this.apz.latitude = params.latitude;
		this.apz.longitude = params.longitude;
		if (this.apz.fetchAddr && (params.latitude !== "0" || params.longitude !== "0")) {
			this.getAddress();
		} else {
			this.apz.location = {
				"lat": this.apz.latitude,
				"lng": this.apz.longitude
			};
		}
   }, 
   getHeader : function(params) {
      if (this.apz.isNull(params.id)) {
         reqId = this.apz.getProcId();
      }else{
    	  reqId = params.id;
      }
      if (this.apz.isNull(params.async)) {
    	  params.async = false;
      }
      var getHeader = true;
      if(this.apz.isFunction(this.apz.app.preGetHeader)){
          getHeader = this.apz.app.preGetHeader(params);
         if (this.apz.isNull(getHeader)) {
            getHeader = true;
         }
      }
      if(getHeader){
         var header = {};
         header.appId = this.apz.appId;
         header.sessionId = this.apz.sessionId;
         header.deviceId = this.apz.deviceId;
         header.requestId = reqId;
         header.async = params.async;
         header.userId = this.apz.userId;
         header.screenId = params.scrName;
         header.status = true;
         header.source = "APPZILLON";
         ////InterfaceID manipulation for MicroApp 
         if(!params.internal){
        	 header.interfaceId = params.appId ? params.appId +"__"+ params.ifaceName : this.apz.currAppId+"__"+ params.ifaceName;
         }else{
        	 header.interfaceId = params.ifaceName;
         }
         header.os = this.apz.deviceType;
         header.location = this.apz.location;
         if(params.captchaRef && params.captchaString){
             header.captchaRef = params.captchaRef || '';
             header.captchaString = params.captchaString || '';
         }
      }
      if(this.apz.isFunction(this.apz.app.postGetHeader)){
         header = this.apz.app.postGetHeader(header);
      }
      return header;
   }, 
   /**
    * This API will be used for sending the request to Server. Depends on the buildReq flag value request will be build by appzillon based on the defined data model.
    * @param {object} params params include
    * <br><b>appId</b>: The application ID from which the call to the API is made.
    * <br>id: The unique ID which will be used in the call back function in case of asynchronous server calls.
    * <br>scrName: The name of the screen from where the request is sent.This is optional.
    * <br>ifaceName: The interface id. If data model is of database type then operation to be suffixed with underscore (e.g. ifaceid_New). 
    * <br>buildReq: It’s a flag "Y" or "N" indicating whether to build the request  from the screen  by appzillon if the flag value is 'Y'. If 'N' request will be sent in the req parameter.
    * <br>paintResp: It’s a flag "Y" or "N" indicating whether the response to be painted automatically or not. If value is 'Y', appzillon will paint the response on the screen.
    * <br>callback: The function called after response from the server.
    * <br>req: If buildReq is "N", then this would contain the valid request JSON object based on the requirement.
    * <br>async: This is a boolean value, this parameter is used to make server calls asynchronously,If the value is true then the calls to server would be asynchronous.
    * <br>callBackObj: The context where the callback function is available.
    * <br>internal: true / false. True for Appzillon provided interface; false for external interface. 
    * @example
    * var params = {};
    * params.appId= "XYACCOUNTS"
    * params.scrName ="account_details"
	 * params.ifaceName = "ACCOUNT_SUMM";	
	 * params.buildReq = "Y";
	 * params.paintResp ="Y";
	 * params.id ="Id_1";
    * params.req ="";
    * params.async =true;
    * params.callBackObj =this;
    * params.callback=this.SDNCallBack;
    * apz.server.callServer (params);  
    * 
    * @example
    * apz.app.preGetHeader = function(params) {  //params include id, async parameters
	 *	// content
    *     }
    * 
    * @example
    * apz.app.preGetHeader =function (params){
    *		params.id= uniqueId;
    *		params.async = true/false;
    *
	 *	return {true/false};
    *  }
    * @example
    * apz.app.preUpdateResponse = function(params){
    *  var rem = "Payment for  amount "+apz.getElmValue (“createPayment __i__createBillingTransaction__TransactionAmount”)+"  is accepted";
    *  alert(JSON.stringify(params.res));
    *  params.res.createPayment _Res.createBillingTransactionResponse.Response.message =rem;
    *  return true;
    * }
    * @example
    * apz.app.postUpdateResponse = function()
    *  {
    *  var rem = "Payment for  amount "+ apz.getElmValue (“createPayment __i__createBillingTransaction__TransactionAmount”)+"  is accepted";
    *  apz.data.scrdata. createPayment _Res.createBillingTransactionResponse.Response.message=rem;
    *  }
    * @description
    * <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | apz.app.preGetHeader       | This API (as a hook function) is used to manipulate the request header programmatically before appzillon builds the request header. This function should return a  boolean result true or false based on conditions written in the function. On returning true Appzillon will continue to build the request header, and if false it will stop.  |
    * | apz.app.postGetHeader      | This API(as a hook function) is used to manipulate the request header programmatically after appzillon builds the request header.|
    * | apz.app.preUpdateResponse  | This API (hook function) is used to modify the response object returned by the interface. It returns Boolean data. If true, the changes made will be available for screen rendering else the original response will be considered.|
    * | apz.app.postUpdateResponse | This API (as a hook function) is used to modify the response received from the server before it is made available for screen rendering.  |
    */
   callServer : function(params) {
      /* Params  Contains the below attributes
       * id,callBackObj,callBack,ifaceName,scrName, buildReq, req, paintResp,appId
       * async(boolean),
       * Response Contains below
       * res, errCode
       */
      if(this.apz.isFunction(this.apz.app.preCallServer)){
         this.apz.app.preCallServer();
      }
      params.internal = params.internal ? params.internal : false;
      params.apzIfaceName = params.appId ? params.appId+"__"+params.ifaceName : this.apz.currAppId+"__"+params.ifaceName;
      var ifaceName = this.apz.getIfaceName(params.ifaceName);
      var ifaceDet = this.apz.getIfaceObj(params.apzIfaceName, params.appId);
      if(this.apz.isNull(ifaceDet)){
    	  params.ifaceDet = {};
      }else{
    	  params.ifaceDet = ifaceDet;
      }
         if (Apz.Audit) {
         var log = {};
         log.action = 'SERVER';
         log.startTimeStamp = this.apz.getCurrTimeStamp();
         params.auditLog = log;
         }
      /////Build Data
      if (params.buildReq == "Y") {
         var buildData = true;
         params.req = {};
         if(this.apz.isFunction(this.apz.app.preBuildData)){
            buildData = this.apz.app.preBuildData(params.req);
            if (this.apz.isNull(buildData)) {
               buildData = true;
            }
         }
         if(buildData){
            var dataIface = ifaceName + "_Req";
            params.req = this.apz.data.buildData(dataIface, params.appId);
            this.correctReq(params);
         }
         if(this.apz.isFunction(this.apz.app.postBuildData)){
            this.apz.app.postBuildData(params.req);
         }
      }
      this.sendReq(params);
   }, getSessionFlag : function(params) {
      var sessionDet = "N";      
      try {
      	 if(params.session){
      	 	sessionDet = params.session;
      	 } else {
	    	 //// Internal interfaces session flag check
	         if(Object.keys(this.apz.internalInterfaces).indexOf(params.ifaceName) > -1) {            
	            sessionDet = !apz.isNull(this.apz.internalInterfaces[params.ifaceName].session) ? this.apz.internalInterfaces[params.ifaceName].session : "N";
	         } else if(params.apzIfaceName){
               var ifaceDet = this.apz.getIfaceObj(params.apzIfaceName, params.appId);
               if (!apz.isNull(ifaceDet)) {
                  sessionDet = !apz.isNull(ifaceDet.session) ? ifaceDet.session : "Y";
               }
           }
	     }
      } catch (e) {
         sessionDet = "N";
      }
      return sessionDet;
   },
   /**
    * 
    * @param {object} params 
    * @description
    * <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | apz.app.preRequestCall | This API will be called before sending request |
    * @example
    * apz.app.preRequestCall = function(){
    * //// write code to perform some action before the sendReq  execution.
	
	 * }

    */
   sendReq : function(params) {
      ////Populate URL
      params.url = this.apz.serverUrl;
      params.method = "POST";
      if (this.apz.deviceOs == "WEB") {
         params.url = "AppzillonWeb";
      }
      var reqFull = {};
      reqFull.appzillonHeader = this.getHeader(params);
      reqFull.appzillonBody = params.req ? params.req : {};
      params.reqFull = reqFull;
      if(this.apz.isFunction(this.apz.app.preRequestCall)){
         this.apz.app.preRequestCall();
      }
      var sessionFlag = apz.server.getSessionFlag(params);
      ////Ajax Call
      if (!this.apz.mockServer) {         
         var serverStatus = true;
         //Check server is up for devices only && check login status if session required.
         if (this.apz.offlineSupport == "Y" && 
         	((sessionFlag == "Y" && !apz.server.loginStatus) || 
         		apz.server.appSecToken != "Y")) {
            this.fnServerAvailable(params);
         } else {
            var nsParam = {params:params, runner:params.runner, runnerObj:params.runnerObj};
            nsParam.callBack = this.receiveSslRes;
            nsParam.callBackObj = this;
            this.apz.backUpReq(params);
            nsParam.reqId = params.id;
            if(nsParam.runner){
               if(nsParam.runnerObj){
                  nsParam.runner.call(nsParam.runnerObj, nsParam);
               } else {
                  nsParam.runner(nsParam);
               }
            } else {
                this.apz.ns.sendReq(nsParam);
            }
         }
      } else {
         params.status = true;
         var resJson = {};
         if (!apz.isNull(params.resCode)){
            resJson = this.apz.getFile(this.apz.getMockRespPath(params.appId) + "/" + params.resCode + ".json");
         } else {
            var ifaceName = this.apz.getIfaceName(params.ifaceName);
            resJson = this.apz.getFile(this.apz.getMockRespPath(params.appId) + "/" + ifaceName + ".json");
         }
         params.resFull = JSON.parse(resJson);
         this.receiveRes(params);
      }
   }, fnServerAvailable : function(params) {
      var serverUp = true;
      $.ajax({
         url: params.url,
         type: "HEAD",
         async: false,
         timeout: 1000,
         statusCode: {
            404: function(response) {
            serverUp = false;
            }
         },
         error: function(err) {
            if (err.status == 405) {
            apz.server.postfnServerAvailable(params);
            } else {
            params.callbackrequired = true;
            params.status = false;
            params.code = "APZ-SVR-ERR";
            apz.server.tankOfflineData(params);
            }
         }
      });
   }, setAppSecToken: function(appSecToken) {
      apz.server.appSecToken = appSecToken;
   }, setLoginStatus: function(loginStatus) {
      apz.server.loginStatus = loginStatus;
   }, postfnServerAvailable: function(params) {
      params.id = this.apz.getProcId();
      Apz.apzNativeServiceDet[params.id] = params;
      var nsReq = {};
      nsReq.fwdData = params;
      nsReq.keepAlive = true;
      nsReq.id = this.apz.getProcId();
      this.offlineId = params.id;
      nsReq.callBack = this.postfnServerAvailableCB;
      nsReq.callBackObj = this;
      if (apz.server.appSecToken != "Y") {
         this.apz.ns.refreshServerNonce(nsReq);
      } else {
         nsReq.status = true;
         apz.server.postfnServerAvailableCB(nsReq);
      }
   }, postfnServerAvailableCB: function(nsReq) {
      var params = nsReq.fwdData;
      this.fwdDataBk = params;
      if (nsReq.status) {
         //Setting appzillon.appSecToken
         this.setAppSecToken("Y");
         var sessionFlag = apz.server.getSessionFlag(params);
         if (sessionFlag == "Y" && !apz.server.loginStatus && this.apz.isFunction(this.apz.app.loginOnline)) {
            this.apz.app.loginOnline({ callBack: this.postLoginOnlineCallBack });
         } else {
            this.sendReq(params);
         }
      } else {
         params.callbackrequired = true;
         apz.server.tankOfflineData(params);
      }
   }, postLoginOnlineCallBack: function(params) {
      apz.server.setLoginStatus(params.status);
      if (apz.server.loginStatus == true && apz.server.appSecToken == "Y") {
         apz.server.sendReq(apz.server.fwdDataBk);
      } else {
         //apz.server.tankOfflineData(apz.server.fwdDataBk);
         apz.server.fwdDataBk.noTank = true;
         apz.server.fwdDataBk.status = false;
         apz.server.fwdDataBk.resFull = params.resFull;
         apz.server.receiveRes(apz.server.fwdDataBk); // If user cancels login
      }
   }, receiveSslRes : function(resp) {
      var params = this.apz.copyJSONObjectWithFilter(resp.params,[],Apz.apzNativeServiceDet[resp.reqId]);
      delete Apz.apzNativeServiceDet[resp.reqId];
      if(!resp.status && this.apz.isNull(params.callBack)){
         params.code = "";
         if(resp.errorCode){
            apz.stopLoader();
            params.code = resp.errorCode;
         } else {
            params.code = params.resFull.appzillonErrors.errors[0].errorCode;
         }
         var ifaceObj = params.ifaceDet;
         if (!this.apz.isNull(ifaceObj) && ifaceObj.offline == "Y") {
            this.tankOfflineData(params);
         }
         this.apz.dispMsg(params);
      } else {
         if(!apz.isNull(resp.errorCode)){
            var ifaceObj = params.ifaceDet;
            if (!this.apz.isNull(ifaceObj) && ifaceObj.offline == "Y") {
               this.tankOfflineData(params);
            }
            params.status = false;
            params.code = "";
            params.code = resp.errorCode;
            apz.stopLoader();
            this.apz.dispMsg(params);
        } else {
            this.receiveRes(params);
        }
      }
   },receiveRes : function(params) {
	   if (Apz.Audit && params.auditLog) {
		     var log = params.auditLog;
		     log.endTimeStamp = this.apz.getCurrTimeStamp();
	         log.field1 = params.appId ? params.appId+"__"+params.ifaceName : this.apz.currAppId+"__"+params.ifaceName;
	         log.field2 = '';
	         log.field3 = '';
	         log.field4 = '';
	         log.field5 = '';
	         this.apz.audit.auditLog(log);
	   }
      if (params.status) {
         ////Make Full Response for Mock
         if ((this.apz.mockServer)) {
            var resFullOrig = params.resFull;
            params.resFull = {};
            params.resFull.appzillonHeader = this.getHeader(params);
            params.resFull.appzillonBody = resFullOrig;
         } else {
             ////Take Session Id
             this.apz.sessionId = params.resFull.appzillonHeader.sessionId;
         }
         ////Populate Body/Errors
         params.res = params.resFull.appzillonBody;
         params.errors = params.resFull.appzillonErrors;
         this.processRes(params);
      } else {
         var ifaceObj = params.ifaceDet;
         if (!this.apz.isNull(ifaceObj) && ifaceObj.offline == "Y") {
            this.tankOfflineData(params);
         }
         params.code = "APZ-SVR-ERR";
         this.apz.dispMsg(params);
      }
   }, tankOfflineData: function(params) {
      var reqObj = {};
      reqObj.appId = params.appId;
      reqObj.ifaceName = params.ifaceName;
      reqObj.ifaceJson = params.reqFull;
      reqObj.respJson = "";
      reqObj.uploadStatus = params.status;
      reqObj.callId = "";
      reqObj.status = params.status;
      var myObj = this.apz;
      // requirejs([this.apz.getInfraPath() + "/appzillon/offline.js"], function() {
      try {
         if (myObj.isNull(apz.offline)) {
            myObj.offline = new Apz.Offline(myObj);
         }
         if (!params.noTank && !this.apz.isNull(params.ifaceDet) && params.ifaceDet.offline == "Y") {
            if (params.status && !myObj.isNull(myObj.offline.offlineRefNo)) {
            reqObj.uploadStatus = "SUCCESS";
            myObj.offline.persistOfflineData(reqObj);
            } else if (myObj.isNull(myObj.offline.offlineRefNo)) {
            reqObj.uploadStatus = "FAILURE";
            myObj.offline.persistOfflineData(reqObj);
            }
         }
         
         ////Call Callback..
         if (params.callbackrequired && this.apz.isFunction(params.callBack)) {
            if (params.callBackObj) {
               params.callBack.call(params.callBackObj, params);
            } else {
               params.callBack(params);
            }
         }
      } catch (e) {
         console.log("Problems with Offline Plugin" + e);
      }
      apz.stopLoader();
   }, processRes : function(params) {
      if(params.errors){
         if(params.errors[0] && params.errors[0].errorCode == "APZ-SMS-EX-003"){
         	params.errors[0].errorCode = "$APZ-SMS-EX-003";
            var param = {"code":"APZ-SMS-EX-003"};
            // Fix of Bug 52011 - Session Timeout alerts are displayed twice
            param.ignoreDispMsg = params.ignoreDispMsg;
            param.callBack = this.apz.app.sessionTimeoutCallBack;
            this.apz.dispMsg(param);
            // added code_AB
         } else if (params.errors[0] && params.errors[0].errorCode == 'APZ-CNT-331') {
            // fix for Bug 57801 - Not getting Session failure callback in older sessions in existing browser tabs
            params.errors[0].errorCode = "$APZ-CNT-331";
            var param = {'code': 'APZ-CNT-331'};
            param.ignoreDispMsg = params.ignoreDispMsg;
            param.callBack = this.apz.app.sessionTimeoutCallBack;
            this.apz.dispMsg(param);
         }
      } else if (!params.internal) {
         ////Correct Response
         this.correctRes(params);
         ////Update Response
         this.updateResponse(params);
         if (params.paintResp == 'Y') {
            var loadData = true;
            if(this.apz.isFunction(this.apz.app.preLoadData)){
               loadData = this.apz.app.preLoadData();
               if (this.apz.isNull(loadData)) {
                  loadData = true;
               }
            }
            if (loadData) {
               var ifaceName = this.apz.getIfaceName(params.ifaceName);
               this.apz.data.loadData(ifaceName, params.appId);
            }
            if(this.apz.isFunction(this.apz.app.postLoadData)){
               this.apz.app.postLoadData();
            }
         }
      }
      ////Call Callback..
      if (this.apz.isFunction(params.callBack)) {
         if (params.callBackObj) {
            params.callBack.call(params.callBackObj, params);
         } else {
            params.callBack(params);
         }
      }
   }, 
   updateResponse : function(params) {
         var rowdatapointer = {};
         var updateRes = true;
         if(this.apz.isFunction(this.apz.app.preUpdateResponse)){
            updateRes = this.apz.app.preUpdateResponse(params);
            if (this.apz.isNull(updateRes)) {
               updateRes = true;
            }
         }
         if(updateRes){
             this.updateDataResponse(params.res);
         }
         if(this.apz.isFunction(this.apz.app.postUpdateResponse)){
            this.apz.app.postUpdateResponse();
         }
   }, 
   /**
    * The login plugin authenticates the user to access the application.
    * @param {object} params params include
    * <br>userId: The registered user id.
    * <br>pwd: The password of the respective user.
    * <br>callBack: This is the function which is called after getting response from the server with below parameters
    * <br>isBiometric: This parameter contains value "Y" if biometric login needs to be enabled else should contain "N".This is an optional parameter.
    * <br>id: The unique ID which will be used in the call back function in case of asynchronous server calls.
    * <br>callBackObj: The object that contains the function. In most of the cases it’s "this".
    * <br>scrsAccessType: ["A","D","N"] are the possible values.
    * <br>ifacesAccessType: ["A","D","N"] are the possible values.
    * <br>controlsAccessType: ["A","D","N"] are the possible values.
    * <br>"A" - Authorized, will fetch only the authorized screens,interfaces or controls when assigned to respective field.
"D" - Denied, will fetch only the Unauthorized screens,interfaces or controls when assigned to respective field.
"N" - Not to query, will not fetch any details when assigned to the respective parameter.
    * @example
    * var params = {};
	 * params.userId = user;
	 * params.pwd= password;
	 * params. isBiometric = "N";
	 * params.callBack = loginCallback;
    * params.callBackObj =this;
	 * apz.server.login(params);
    */
   login : function(params) {
	   /* Params contains the below attributes
	       *** id, userId, pwd, callBackObj,callBack ***
	       * Response contains below attributes
	       *** res, errCode ***
	   */
      params.internal = true;
      params.ifaceName = "appzillonReLoginRequest";
      ////Update global userId
      this.apz.userId = params.userId;
      ////Build Request
      var req = {};
      req.loginRequest = {};
      req.loginRequest.appId = this.apz.appId;
      req.loginRequest.deviceId = this.apz.deviceId;
      req.loginRequest.sysDate = this.apz.getCurrTimeStamp();
      ////Request For privs
      if (!params.scrsAccessType) {
         params.scrsAccessType = "N";
      }
      if (!params.ifacesAccessType) {
         params.ifacesAccessType = "N";
      }
      if (!params.controlsAccessType) {
         params.controlsAccessType = "N";
      }
      req.loginRequest.scrsAccessType = params.scrsAccessType;
      req.loginRequest.ifacesAccessType = params.ifacesAccessType;
      req.loginRequest.controlsAccessType = params.controlsAccessType;
      req.loginRequest.userId = params.userId;
      req.loginRequest.pwd = params.pwd;
      //// Enhancement done and shared by Ravindra on 6-May-2019 for KeepMeSignedIn
      req.loginRequest.keepMeSignedIn = params.keepMeSignedIn || 'N';
      if(this.apz.isFunction(this.apz.app.preLogin)){
         req = this.apz.app.preLogin(req);
      }
      params.req = req;
      params.userCallBackObj = params.callBackObj;
      params.userCallBack = params.callBack;
      params.callBackObj = this;
      params.callBack = this.loginCB;
      params.runnerObj = this.apz.ns;
      params.runner = this.apz.ns.login;
	  this.sendReq(params);
   }, loginCB : function(params) {
      ////Populate User Data / Privileges
      if (params.res && params.res.loginResponse && params.res.loginResponse.status) {
         this.apz.store('LOGIN', this.apz.getCurrTimeStamp());
         this.setLoginStatus(params.res.loginResponse.status);
         ////User Data
         this.populateUserDet(params.res.loginResponse.userDet);
         ////Privileges
         if(!this.apz.isNull(params.res.loginResponse.userDet)){
        	 this.populatePrivs(params.res.loginResponse.userDet.privs); 
         }
      }
      /////Call User Callback..
      if (this.apz.isFunction(params.userCallBack)) {
         if (params.userCallBackObj) {
            params.userCallBack.call(params.userCallBackObj, params);
         } else {
            params.userCallBack(params);
         }
      }
   }, populateUserDet : function(userDet) {
      if (userDet) {
         this.apz.userId = userDet.id;
         this.apz.userName = userDet.name;
         this.apz.userExtId = userDet.extId;
         this.apz.userProfilePic = userDet.profilePic;
         this.apz.lastLogin = userDet.lastLogin;
         this.apz.loadUserPrefs(userDet);
      }
   }, populatePrivs : function(privs) {
      if (privs) {
         this.apz.privs.scrsAccessType = privs.scrsAccessType;
         this.apz.privs.ifacesAccessType = privs.ifacesAccessType;
         this.apz.privs.controlsAccessType = privs.controlsAccessType;
         this.apz.privs.scrs = privs.scrs;
         this.apz.privs.ifaces = privs.ifaces;
         this.apz.privs.controls = privs.controls;
         ////Check and Init
         if (!this.apz.privs.scrsAccessType) {
            this.apz.privs.scrsAccessType = "N";
         }
         if (!this.apz.privs.ifacesAccessType) {
            this.apz.privs.ifacesAccessType = "N";
         }
         if (!this.apz.privs.controlsAccessType) {
            this.apz.privs.controlsAccessType = "N";
         }
         if (!this.apz.privs.scrs) {
            this.apz.privs.scrs = [];
         }
         if (!this.apz.privs.ifaces) {
            this.apz.privs.ifaces = [];
         }
         if (!this.apz.privs.controls) {
            this.apz.privs.controls = [];
         }
      }
   },
   /**
    * 
    * @description
    * <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | apz.app.preapplyPrivileges      | This API (as a hook function) will be used as part of applyPrivileges function. This is used to add additional functionality if needed or skip the appzillon functionality by returning false. |
    * | apz.app.postapplyPrivileges  | This API (as a hook function) will be used as part of applyPrivileges function. This allows for adding additional functionality on top of appzillon functionality.|
    * @example
    * apz.app.preapplyPrivileges = function() {
    *         // write the function that is to be executed
    *      };
    * @example
    * apz.app.postapplyPrivileges = function() {
    *         // write the function that is to be executed
   *     };
    */
   applyPrivileges : function(){
	   var proceed = true;
	      if(this.apz.isFunction(this.apz.app.preapplyPrivileges)){
            proceed = this.apz.app.preapplyPrivileges()
            if (this.apz.isNull(proceed)) {
               proceed = true;
            } 
         }
	      if (proceed) {
	         //// To Hide if Control is Container and Disable if Control is Element	        
             var serverObj = this;
	         $("[apzcontrol]").filter(function(){
	            var controlID = this.attributes['apzcontrol'].value;
					if(!serverObj.apz.isNull(controlID)){
					   if(!serverObj.accessControl(controlID)){ ////APZCHANGE
						  serverObj.blockControl(this);
					   }              
					}
	            });	         
	      }
	      if(this.apz.isFunction(this.apz.app.postapplyPrivileges)){
	        this.apz.app.postapplyPrivileges();
	      }
	},
	accessControl : function (ctrlId){
		return this.hasPrivs(ctrlId,'controls');
	},
	accessInterfaces : function (ctrlId){
		return this.hasPrivs(ctrlId,'ifaces');
	},
	accessScreen : function (appId, scrId){
      appId = appId ? appId : this.apz.currAppId;
		return this.hasPrivs(appId+"__"+scrId,'scrs');
	},
	hasPrivs : function(objId, objType){
      var alwdOrDisalowd;
	   var grant = true;
	   var type = "9";
	   if(objType == 'scrs'){
	      alwdOrDisalowd = 'scrsAccessType';
	   }else if(objType == 'controls'){
	      alwdOrDisalowd = 'controlsAccessType';
	   }else if(objType == 'ifaces'){
		  alwdOrDisalowd = 'ifacesAccessType';
	   }
	   try{
	      type = this.apz.privs[alwdOrDisalowd];
	   }catch(err) {
	      type = "9";
	   }
	   
	   if (type == "D" ){
	      if(this.containsArrayElm(this.apz.privs[objType],objId)){
	         grant = false;         
	      }     
	      
	   }else if (type == "A" ){
	      grant = false;
	      if(this.containsArrayElm(this.apz.privs[objType],objId)){
	         grant = true;       
	      }     
	   }
	   return grant;
	},
	containsArrayElm : function(obj,key) {
	   var exists = false;
	   try{
		  if($.inArray(key,obj) > -1) {
			 exists = true;
		  }
	   }catch(e){
		  if(obj.includes(key)) {
			 exists = true;
		  }
	   }
	   return exists;
	},
    blockControl : function (obj){
        var tagName = obj.tagName;
        ////APZCHANGES Starts
        var lObj = $(obj);
        if(lObj.parent().hasClass("tabs")){
            lObj.addClass("sno");
          if(lObj.index() == 0 || (lObj.prev() && !lObj.siblings().hasClass('current'))){
           if(lObj.next()){
               lObj.siblings().removeClass("current");
               lObj.removeClass("current");
               lObj.next().addClass("current");
           }
          }
        } else if(lObj.hasClass("tabcontent")){
            lObj.addClass("sno");
            lObj.removeClass('visible');
          if(!lObj.siblings().hasClass('visible') && lObj.next()){
              lObj.next().addClass("visible");
          }
           ////APZCHANGES ENDS
        } else if((obj.hasAttribute("readonly") || obj.hasAttribute('disabled') || tagName == 'DIV' || tagName == 'LI' || tagName == 'SPAN' )){
            lObj.addClass("sno");
        } else {
            lObj.attr('disabled','disabled');
            lObj.css('pointer-events','none');
        }
    }, 
    /**
     * This API is used to logout the user from the application.
     * @param {object} params params include
     * <br>userId: This is the user ID of that needs to be logged out
     * <br>callBack:The call back function that will be called after apz.server.logout(params)
     * <br>callBackObj: The object that contains the function. In most of the cases it’s "this".
     * @example
     * var params = {};
     * params.userId = "user";
     * params.callBack = this.logoutCallBack;
     * params.callBackObj = this;
     * apz.server.logout(params);
     */
    logout : function(params) {
     /* Params contains the below attributes
         *** userId,callBackObj,callBack ***
         * Response contains below attributes
         *** res, errCode ***
     */
      if (Apz.Audit) {
         this.apz.audit.clearAuditDetails();
      }
      params.internal = true;
      params.ifaceName = "appzillonLogoutRequest";
      ////Update global userId
      this.apz.userId = params.userId;
      ////Reset the conversation object also for the session
      if(!this.apz.isNull(this.apz.convui)) {
         this.apz.convui = new Apz.ConvUI(this.apz);
      }
      ////Build Request
      var req = {};
      req.logoutRequest = {};
      req.logoutRequest.appId = this.apz.appId;
      req.logoutRequest.userId = params.userId;
      req.logoutRequest.deviceId = this.apz.deviceId;
      params.req = req;
       //Newly Added
      params.internal = true;
      params.userCallBackObj = params.callBackObj;
      params.userCallBack = params.callBack;
      params.callBackObj = this;
      params.callBack = this.logoutCB;
      this.sendReq(params);
   }, logoutCB : function(params) {
		if (params.res && params.res.status && this.apz.deviceOs != "WEB") {
			var nsReq = {fwdData:params};
			nsReq.id = this.apz.getProcId();
			nsReq.callBackObj = this;
         	nsReq.callBack = this.refreshServerNonceCB;
         	apz.server.setLoginStatus(false);
			this.apz.ns.refreshServerNonce(nsReq);
		} else {
			if (this.apz.isFunction(params.userCallBack)) {
				if (params.userCallBackObj) {
					params.userCallBack.call(params.userCallBackObj, params);
				} else {
					params.userCallBack(params);
				}
			}
		}
   }, refreshServerNonceCB : function(nsResp){
   		var params = nsResp.fwdData;
		if (this.apz.isFunction(params.userCallBack)) {
			if (params.userCallBackObj) {
				params.userCallBack.call(params.userCallBackObj, params);
			} else {
				params.userCallBack(params);
			}
		}
   }, 
   /**
    * The change password API is used to change the user password.
    * @param {object} params params include
    * <br>userId: The user ID for which the password is being modified.
    * <br>oldPassword: The current password of the user.
    * <br>newPassword: The new password of the user.
    * <br>confirmPassword: The new password captured once again for confirmation purpose.
    * <br>callback: This is the function which is called after getting response from the server .
    */
   changePassword : function(params) {
	   /* Params contains the below attributes
        *** userId, callBack, oldPassword, newPassword, confirmPassword ***
        * Response contains below attributes
        *** res, errCode ***
    */
		if (apz.val.validatePassword(params)) {
			var req = {};
			req.changePasswordRequest = {};
			req.changePasswordRequest.appId = this.apz.appId;
			req.changePasswordRequest.deviceId = this.apz.deviceId;
			req.changePasswordRequest.userId = params.userId;
			req.changePasswordRequest.pwd = params.oldPassword;
			req.changePasswordRequest.newPassword = params.newPassword;
			req.changePasswordRequest.confirmPassword = params.confirmPassword;
			req.changePasswordRequest.sysDate = this.apz.getCurrTimeStamp();
			req.changePasswordRequest.hashKey1 = this.apz.hashkey1;
			req.changePasswordRequest.hashKey2 = this.apz.hashkey2;
			req.changePasswordRequest.callBack = params.callBack;
			req.changePasswordRequest.callBackObj = params.callBackObj;
			params.req = req;
			params.ifaceName = "appzillonChangePassword";
			params.buildReq = "N";
			//Newly Added
			params.internal = true;
			params.runnerObj = this.apz.ns;
			params.runner = this.apz.ns.changePassword;
			this.sendReq(params);
		}
   },
   /////////////////Correction Functions///////////////////////////////
   correctReq : function(params) {
	  var ifaceName = this.apz.getIfaceName(params.apzIfaceName);
      var reqRoot = this.apz.getReqRoot(ifaceName);
      if (params.ifaceDet.type == "ISO8583") {
         this.apz.iso = new Apz.Iso(this.apz);
         this.apz.iso.convertRequest(params);
      } else {
         var reqd = params.ifaceDet.correctReq;
         if (reqd) {
            for (var node in params.req) {
               if (!this.apz.isNull(node)) {
                  var childNode = params.req[node];
                  this.correctReqNode(ifaceName, params.req, reqRoot, childNode, node, params.appId);
               }
            }
         }
      }
      ////Remove Root Node
      params.req = params.req[reqRoot];
   }, correctReqNode : function(ifaceName, parentNode, parentName, node, name, appId) {
      var type = this.apz.getDataType(node);
      var ifaceObj = this.apz.getIfaceObj(ifaceName, appId);
      if ((type == "Object") || (type == "Array")) {
         var extName = "";
         var newObjStr = "";
         var params = {};
         params.iface = ifaceName;
         params.dml = "REQ";
         params.node = name;
         var nodeId = this.apz.getNodeId(params);
         var nodeData = ifaceObj.nodesMap[nodeId];
         var newName = nodeData.extName;
         var nsAlias = nodeData.nsAlias;
         if (this.apz.isNull(newName)) {
            newName = name;
         }
         /*if (!this.apz.isNull(nsAlias)) {  TBC - Not required anymore as extName is coming with extension?
            newName = nsAlias + ":" + newName;
         }*/
         ///Rename
         var args = {};
         args.parentNode = parentNode;
         args.node = node;
         args.oldName = name;
         args.newName = newName;
         node = this.apz.renameNode(args);
         var noOfRecs = 0;
         if (type == "Array") {
            noOfRecs = node.length;
         } else {
            noOfRecs = 1;
         }
         for (var r = 0; r < noOfRecs; r++) {
            var arrMember = null;
            if (type == "Array") {
               arrMember = node[r];
            } else {
               arrMember = node;
            }
            var memType = this.apz.getDataType(arrMember);
            if ((memType == "Object") || (memType == "Array")) {
               var childNode = null;
               for (var lnode in arrMember) {
                  if (!this.apz.isNull(lnode)) {
                     childNode = arrMember[lnode];
                     this.correctReqNode(ifaceName, arrMember, name, childNode, lnode, appId);
                  }
               }
            }
         }
      } else {
         ///Element Processing
         var params = {};
         params.iface = ifaceName;
         params.dml = "REQ";
         params.node = parentName;
         var nodeId = this.apz.getNodeId(params);
         var elmId = this.apz.getElmId(nodeId, name);
         var nodeData = ifaceObj.nodesMap[nodeId];
         var newName = nodeData.elmsMap[elmId].extName;
         var nsAlias = nodeData.elmsMap[elmId].nsAlias;
         if (this.apz.isNull(newName)) {
            newName = name;
         }
         /*if (this.apz.isNull(nsAlias)) {   TBC - Not required anymore as extName is coming with extension?
            newName = nsAlias + ":" + newName;
         }*/
         var args = {};
         args.parentNode = parentNode;
         args.node = node;
         args.oldName = name;
         args.newName = newName;
         this.apz.renameNode(args);
      }
   }, correctRes : function(params) {
   	  if(!this.apz.isNull(params.res)){
		  var ifaceName = this.apz.getIfaceName(params.apzIfaceName);
	      var resRoot = this.apz.getResRoot(ifaceName);
	      ////Add DML Node
	      var copy = this.apz.copyJSONObject(params.res);
	      this.apz.clearJSONObject(params.res);
	      params.res[resRoot] = copy;
	      /////////
	      if (params.ifaceDet.type == "ISO8583") {
			  if(apz.isNull(this.apz.iso)){
			   this.apz.iso = new Apz.Iso(this.apz);
			  }
	         this.apz.iso.convertResponse(params);
	      } else {
	         var reqd = params.ifaceDet.correctRes;
	         if (reqd) {
	            for (var node in params.res) {
	               if (!this.apz.isNull(node)) {
	                  var childNode = params.res[node];
	                  this.correctResNode(ifaceName, params.res, resRoot, childNode, node, null, params.appId);
	               }
	            }
	         }
	      }
      }
   }, correctResNode : function(ifaceName, parentNode, parentName, node, name, parents, appId) {
      var type = this.apz.getDataType(node);
      var extName = "";
      //var lparents = pparents;
      if (this.apz.isNull(parents)) {
         parents = node;
      } else {
         parents = parents + "~" + node;
      }
      var apzName = null;
      var ifaceObj = this.apz.getIfaceObj(ifaceName, appId);
      try {
         apzName = ifaceObj.extMap[parents];
      } catch (err) {
         apzName = null;
      }
      if (!this.apz.isNull(apzName)) {
         ////Rename if Required
         if (node != apzName) {
            var newObjStr = JSON.stringify(node);
            parentNode[apzName] = JSON.parse(newObjStr);
            delete parentNode[node];
            node = parentNode[apzName];
         }
         if ((type == "Object") || (type == "Array")) {
            var params = {};
            params.iface = ifaceName;
            params.dml = "RES";
            params.node = apzName;
            var nodeId = this.apz.getNodeId(params);
            ////Convert to Multi Record..
            if (ifaceObj.nodesMap[nodeId].relType == "1:N") {
               if (type == "Object") {
                  var newObjStr = JSON.stringify(node);
                  var newArr = [];
                  newArr[0] = JSON.parse(newObjStr);
                  delete parentNode[apzName];
                  parentNode[apzName] = newArr;
                  node = parentNode[apzName];
                  type = "Array";
               }
            }
            var noOfRecs = 0;
            if (type == "Array") {
               noOfRecs = node.length;
            } else {
               noOfRecs = 1;
            }
            for (var r = 0; r < noOfRecs; r++) {
               var arrMemeber = null;
               if (type == "Array") {
                  arrMemeber = node[r];
               } else {
                  arrMemeber = node;
               }
               var memType = this.apz.getDataType(arrMemeber);
               if ((memType == "Object") || (memType == "Array")) {
                  var childNode = null;
                  for (var lnode in arrMemeber) {
                     if (!this.apz.isNull(lnode)) {
                        childNode = arrMemeber[lnode];
                        var childType = this.apz.getDataType(childNode);
                        //if ((lchildtype == "Object") || (lchildtype ==
                        // "Array")) {
                        this.correctResNode(ifaceName, arrMemeber, name, childNode, lnode, parents, appId);
                        //}
                     }
                  }
               }
            }
         } else {
            ////Element Processing.. No Childs ..
         }
      } else {
         ////Should we delete???
      }
   }, updateDataResponse : function(res) {
      for (key in res) {
         this.apz.data.scrdata[key] = res[key];
      }
   }, fetchBeaconDetails : function(params) {
	   /* Params contains the below attributes
	       *** appId ***
	       * Response contains below attributes
	       *** res, errCode ***
	   */
      params.internal = true;
      params.ifaceName = "appzillonFetchBeaconDetails";
      var req = {};
      req.appzillonBeaconFetchRequest = {};
      req.appzillonBeaconFetchRequest.appId = params.appId;
      params.req = req;
      this.sendReq(params);
   }, insertBeaconDetails : function(params) {
	   /* Params contains the below attributes
	       *** appId, deviceId ***
	       * Response contains below attributes
	       *** res, errCode ***
	   */
      params.internal = true;
      params.ifaceName = "appzillonInsertBeacon";
      var req = {};
      req.appzillonBeaconInsertRequest = {};
      req.appzillonBeaconInsertRequest.appId = params.appId;
      req.appzillonBeaconInsertRequest.deviceId = params.deviceId;
      params.req = req;
      this.sendReq(params);
   }, updateBeaconDetails : function(params) {
	   /* Params contains the below attributes
	       *** id, status ***
	       * Response contains below attributes
	       *** res, errCode ***
	   */
      params.internal = true;
      params.ifaceName = "appzillonUpdateBeaconDetails";
      var req = {};
      req.appzillonBeaconUpdateRequest = {};
      req.appzillonBeaconUpdateRequest.id = params.id;
      req.appzillonBeaconUpdateRequest.status = params.status;
      params.req = req;
      this.sendReq(params);
   },
   /**
    * This API will provide the authorization details for the logged in user if it is maintained in DB. The API will return the role of the logged in user, screens authorized to access, Interfaces(operations) authorized and the controls authorized for the user role.
    * @param {object} params params include
    * <br>callback: The function called after response from the server.
    * <br>callBackObj: The context where the callback function is available.
    * <br>async: This is a boolean value, this parameter is used to make server calls asynchronously,If the value is true then the calls to server would be asynchronous.
    * <br>screensreqd: ["A","D","N"] are the possible values.
    * <br>interfacesreqd: ["A","D","N"] are the possible values.
    * <br>controlsreqd: ["A","D","N"] are the possible values.
    * <br>"A" - Authorized, will fetch only the authorized screens,interfaces or controls when assigned to respective field.
"D" - Denied, will fetch only the Unauthorized screens,interfaces or controls when assigned to respective field.
"N" - Not to query, will not fetch any details when assigned to the respective parameter.
    * @example
    *  var params = {};
    *   params.screensreqd = "A";
    *   params.interfacesreqd = "D";
    *   params.controlsreqd = "N";
    *   params. async =true;
    *   params. callBackObj =this;
    *   params. callback=this.getPrivilegesCallBack;
    *   apz.server.fetchPreviligeDetails(params);
    */
   fetchPreviligeDetails : function(params){
	   /* Params contains the below attributes
	       *** screensreqd, interfacesreqd, controlsreqd ***
	       * Response contains below attributes
	       *** res, errCode ***
	   */
       params.internal = true;
       params.ifaceName = "appzillonFetchPrivilegeService";
       var req = {};
       req.authorizationRequest = {};
       req.authorizationRequest.appId = this.apz.appId;
       req.authorizationRequest.deviceId = this.apz.deviceId;
       req.authorizationRequest.userId = this.apz.userId;
       req.authorizationRequest.scrsAccessType = params.screensreqd;
       req.authorizationRequest.ifacesAccessType = params.interfacesreqd;
       req.authorizationRequest.controlsAccessType = params.controlsreqd;
       params.req = req;
       this.sendReq(params);
   },validateOTP : function(params){
	   /* Params contains the below attributes
	       *** otp ***
	       * Response contains below attributes
	       *** res, errCode ***
	   */
	    params.internal = true;
	    params.ifaceName = "appzillonValidateOTP";
		var req = {};
		req.validateOtpRequest = {};
		req.validateOtpRequest.otp = params.otp;
		params.req = req;
      this.sendReq(params);
   },validateandProcessOTP : function(params){
	   /* Params contains the below attributes
	       *** otp, refno ***
	       * Response contains below attributes
	       *** res, errCode ***
	   */
	    params.internal = true;
	    params.ifaceName = "appzillonValNProcessIface";
	    var req = {};
	    req.validateNProcessRequest = {};
	    req.validateNProcessRequest.RefNo = params.refno;
	    req.validateNProcessRequest.otp = params.otp;
	    params.req = req;
       this.sendReq(params);
   },fetchAugumentedRealityDetails : function(params){
	    params.internal = true;
	    params.ifaceName = "appzillonFetchARDetails";
	    var req = {};
	    req.fetchARDetails = params.ardetailsobj;
	    req.fetchARDetails.appId = this.apz.appId;
	    params.req = req;
       this.sendReq(params);
   },getAddress : function(){ //Method to get the complete address of a user by passing the lat & lng
 	  var reqStr = {
 		"latlng": parseFloat(this.apz.latitude) + "," + parseFloat(this.apz.longitude),
 		"key": apz.googleMapsKey
 	  }
 	  var myObj = this;
 	  $.ajax({
        url : "https://maps.googleapis.com/maps/api/geocode/json", 
        data : reqStr,
        dataType : 'json',
        success : function(res,status) {
     	   myObj.apz.location = {
                "latitude" : myObj.apz.latitude,
               	"longitude" : myObj.apz.longitude
           };
           if(res.status == "OK"){
              var firstResult = res.results[0].address_components;
              for (var k = 0; k < firstResult.length; k++) {
                 if (firstResult[k].types.indexOf("sublocality") > -1) {
                     myObj.apz.location.sublocality = firstResult[k].long_name;
                 } else if (firstResult[k].types.indexOf("administrative_area_level_2") > -1) {
                     myObj.apz.location.admin_area_lvl_2 = firstResult[k].long_name;
                 } else if (firstResult[k].types.indexOf("administrative_area_level_1") > -1) {
                     myObj.apz.location.admin_area_lvl_1 = firstResult[k].long_name;
                 } else if (firstResult[k].types.indexOf("country") > -1) {
                     myObj.apz.location.country = firstResult[k].long_name;
                 }
             }
             if(res.results[0].formatted_address){
                 myObj.apz.location.formattedAddress = res.results[0].formatted_address;
             }
           }else{
            	console.log("Google Map returned with status:-"+res.status);
           }
        },
        error : function(obj,type) {	
		   console.log("Google Maps Error:"+ type);
         	  myObj.apz.location = {
         		"latitude" : myObj.apz.latitude,
         		"longitude" : myObj.apz.longitude
         	  };
           }
        });
   }, callInternalService: function(params) {      
      /* Params  Contains the below attributes
       * id,callBackObj,callBack,ifaceName, req, appId, async(boolean)
       */
      params.internal = true;
      params.ifaceDet = {};
      if (Apz.Audit) {
         var log = {};
         log.action = 'SERVER';
         log.startTimeStamp = this.apz.getCurrTimeStamp();
         params.auditLog = log;
      }
      this.sendReq(params);
   },
   // Bug 51554 - App keeps loading in case of network failure
   checkConnectionStatus: function(){
      params = {};
      //Checking the connection status and showing popup in case of disconnection
      var connection = true;
      params.url = apz.serverUrl;
      if (this.apz.deviceOs == "WEB") {
       params.url = "AppzillonWeb";
      }
   
      $.ajax({
          url: params.url,
          type: "HEAD",
          async: false,
          timeout: 5000,
          statusCode: {
              404: function(response) {
                  connection = false;
              },
              403: function(response) {
                  connection = true;
              },
              0: function (response) {
                  connection = false;
              } 
           },
          error: function(jqXHR, textStatus, err) {
              if (!(jqXHR.status === 405 || jqXHR.status === 403 || err === 'Method Not Allowed') || textStatus === 'timeout' || jqXHR.status === 0) {
                  connection = false;
              } else {
                  connection = true;
              }
          }
      });
   
      return connection;
   }
}
