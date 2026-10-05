Apz.Offline = function(apz) {
	this.apz = apz;
	this.offlineTable = "TB_ASMI_OFFLINE_DATA";
	this.offlineScreenName = "OfflineDataSummary";
	this.offlineNodeName = "OfflineDataSummary";
	this.offlineIfaceName = "OfflineDataSummary";
	this.offlineRefNo = "";
	this.offlineScrName = "";
	this.callId = "";
	this.offlineStatus = "";
	this.offlineAction = "SUMMARY";
	this.isOfflineScr = "true";
	this.offlineArr = [];
	this.offlineFailData = [];
	this.offlineSucessData = [];
	this.offlineCallBack = "";
	this.serverIfaceId = "";
	this.offlineIfaceId= "";
};
Apz.Offline.prototype = {

	loadOfflineScreen : function() {
		this.offlineAction = "SUMMARY";
		var sql = 'SELECT REFNO,SCREENID,INTERFACEID,STATUS,STARTTIME,ENDTIME FROM ' + this.offlineTable;
		this.offlineData(sql);
	},
	persistOfflineData : function(offlineObj){
		/* 
		Params  Contains the below attributes
		ifaceName, ifaceJson, respJson, uploadStatus,callId ,status ,CallBack,	
	  */
	  	var scrName = this.getScreenNameWithAppId();
		if(scrName !== this.offlineScrName) {
			//this.offlineRefNo = '';
			this.offlineScrName = '';
		}
		var sql = "";
		var screenJson = {};
		var appId = offlineObj.appId ? offlineObj.appId : this.apz.currAppId;
		var apzIfaceName = appId+"__"+offlineObj.ifaceName;
		var ifaces = this.apz.ifacesMap[appId];
		/*////////////Providing values for CallBack////////////////////////*/
		this.OfflineStatus = offlineObj.uploadStatus;
		if(offlineObj.callBack) {
			this.offlineCallBack = offlineObj.callBack;
		}
		if(offlineObj.callBackObj) {
			this.offlineCallBackObj = offlineObj.callBackObj;
		}		
		this.offlineIfaceId = apzIfaceName;
		this.callId = offlineObj.callId;
		this.apz.data.buildData();
		for (var key in ifaces) {
		  	if(apz.data.scrdata[key]){
		  		screenJson[key] = apz.copyJSONObject(apz.data.scrdata[key]);
		  	} else if(apz.data.scrdata[key+"_Req"]){
		  		screenJson[key+"_Req"] = apz.copyJSONObject(apz.data.scrdata[key+"_Req"]);
		  	} else if(apz.data.scrdata[key+"_Res"]){
		  		screenJson[key+"_Res"] = apz.copyJSONObject(apz.data.scrdata[key+"_Res"]);
		  	}
		}
		screenJson = JSON.stringify(screenJson);
		try {
			var jsonResp = JSON.stringify(offlineObj.respJson.appzillonBody);
			jsonResp = jsonResp.replace(/'/gi, "A@#ZP%K");
		} catch (err) {
			var jsonResp = "";
		}
		if(!this.apz.isNull(offlineObj.ifaceJson)){
			var ifaceJsonObj = offlineObj.ifaceJson;
			var ifaceJsonStr = JSON.stringify(ifaceJsonObj);
			var ifaceBodyStr = JSON.stringify(ifaceJsonObj.appzillonBody);
		/*	if(!this.apz.isNull(this.apz.ifacesMap[interfaceID]) && !this.apz.isNull(this.apz.ifacesMap[interfaceID].encrypt) && this.apz.ifacesMap[interfaceID].encrypt == "Y") {
				interfaceBodyStr = appzillon.server.requeststore.reqjsonstr;
				interfaceJsonObj.appzillonBody = JSON.parse(interfaceBodyStr);
				OfflineObj.interfaceJson = JSON.stringify(interfaceJsonObj);
				jsonresp = interfaceBodyStr;
			}
			*/
		}
		if(this.apz.isNull(this.offlineRefNo)) {
			sql = "INSERT INTO " + this.offlineTable + "(USERID,SCREENID,INTERFACEID,SCREENPAYLOAD,SCREENRESPONSE,STATUS,APPJSONSTR,APPREQBODY,STARTTIME,ENDTIME) VALUES('" + this.apz.userId + "','" + scrName + "','" + this.offlineIfaceId + "','" + screenJson + "','" + jsonResp + "','" + offlineObj.uploadStatus + "','" + ifaceJsonStr + "','" + ifaceBodyStr + "','" + this.sysDateTime() + "','" + this.sysDateTime() + "')";
			this.offlineAction = "INSERTNEW"
			this.offlineData(sql);
		} else if(this.offlineAction == "SUBMIT") {
			if(offlineObj.uploadStatus === "SUCCESS") {
				this.offlineAction = "DELETEDATA";
				this.deleteOfflineData();
			} else {
				var setClause = "INTERFACEID='" + this.offlineIfaceId + "',SCREENPAYLOAD='" + screenJson + "',SCREENRESPONSE='" + jsonResp + "',STATUS='" + offlineObj.uploadStatus + "',APPJSONSTR='" + ifaceJsonStr + "',APPREQBODY='" + ifaceBodyStr + "',ENDTIME='" + this.sysDateTime() + "'";
				var whereClause = "REFNO='" + this.offlineRefNo + "'";				
				sql = "UPDATE " + this.offlineTable + " SET " + setClause + " WHERE " + whereClause;
				this.offlineAction = "UPDATESCREEN";
				this.offlineData(sql);
			}
		} else {
			var setClause = "INTERFACEID='" + this.offlineIfaceId + "',SCREENPAYLOAD='" + screenJson + "',SCREENRESPONSE='" + jsonResp + "',STATUS='" + offlineObj.uploadStatus + "',APPJSONSTR='" + ifaceJsonStr + "',APPREQBODY='" + ifaceBodyStr + "'";
			var whereClause = "REFNO='" + this.offlineRefNo + "'";
			sql = "UPDATE " + this.offlineTable + " SET " + setClause + " WHERE " + whereClause;
			this.offlineAction = "UPDATE";
			this.offlineData(sql);
		}
	},
	deleteOfflineData : function(referenceNo) {
		 /* Params contains the below value
	       *** referenceNo ***
	   */
		var sql = "DELETE FROM " + this.offlineTable + " WHERE REFNO IN ";
		if(this.apz.isNull(referenceNo)) {
			var tbl = this.getTableUIObj();
			var rowsLen = tbl.rows.length;
			var refNoIn = "(";
			for(var i = 1; i < rowsLen; i++) {
				if($(tbl.rows[i].cells[0].childNodes[0]).find('input[type="checkbox"]').is(":checked")) {
					refNoIn += "'" + this.apz.getObjValue($(tbl.rows[i].cells[1]).find("[rowno]")[0]) + "',";
				}
			}
			refNoIn = refNoIn.substring(0, refNoIn.length - 1) + ")";
			this.isOfflineScr = "true";
			sql = sql + refNoIn;
		} else {
			this.isOfflineScr = "false";
			sql += "(";
			for(var i = 0; i < referenceNo.length; i++) {
				sql += "'" + referenceNo[i] + "',";
			}
			sql = sql.substr(0, sql.length - 1);
			sql += ")";
		}
		this.offlineAction = "DELETEDATA";
		this.offlineData(sql);
	},
	submitTransactions : function(transaction) {
	   /* Params contains the below attributes
	       *** interfaceName, refNo, action, async(boolean) ***
	   */
		if(!this.apz.isNull(transaction)){
			var sql = '';
			var interfaceName = transaction.interfaceName;
			var refNo = transaction.refNo;
			var async = transaction.async;
			transaction.action = "SUBMITALL";
			if(!this.apz.isNull(interfaceName) && !this.apz.isNull(refNo)) {
				sql = 'SELECT SCREENID,INTERFACEID,REFNO,APPREQBODY FROM ' + this.offlineTable + ' WHERE INTERFACEID= \'' + interfaceName + '\' AND REFNO = \'' + refNo + '\'';
			} else if(!this.apz.isNull(interfaceName) && interfaceName != '*' && this.apz.isNull(refNo)) {
				sql = 'SELECT SCREENID,INTERFACEID,REFNO,APPREQBODY FROM ' + this.offlineTable + ' WHERE INTERFACEID= \'' + interfaceName + '\'';
			} else if(this.apz.isNull(interfaceName) && !this.apz.isNull(refNo)) {
				sql = 'SELECT SCREENID,INTERFACEID,REFNO,APPREQBODY FROM ' + this.offlineTable + ' WHERE REFNO= \'' + refNo + '\'';
			} else if(interfaceName == '' || interfaceName == '*') {
				sql = 'SELECT SCREENID,INTERFACEID,REFNO,APPREQBODY FROM ' + this.offlineTable + ' WHERE STATUS= \'FAILURE\'';
			}
			this.offlineAction = transaction.action;
			this.serverAction = async;
			if(!this.apz.isNull(sql)){
				this.offlineData(sql);
			}
		}else{
			var tbl = this.getTableUIObj();
			if(!this.apz.isNull(tbl)){
				var lrows = tbl.rows.length;
				if(!this.multiSelected(tbl)) {
					for(var i = 0; i < lrows; i++) {
						if (tbl.rows[i].parentElement && tbl.rows[i].parentElement.tagName != "THEAD") {
							var rowSelc = $(tbl.rows[i].cells[0].childNodes[0]).find('input[type="checkbox"]').is(":checked");
							if (rowSelc) {
								var rowObj = tbl.rows[i];
								var referenceNo = this.apz.getObjValue(
									$(rowObj.cells[1]).find("[rowno]")[0]
								);
								var screenId = this.apz.getObjValue(
									$(rowObj.cells[2]).find("[rowno]")[0]
								);
								var interfaceId = this.apz.getObjValue(
									$(rowObj.cells[3]).find("[rowno]")[0]
								);
								var status = this.apz.getObjValue(
									$(rowObj.cells[4]).find("[rowno]")[0]
								);
								if (status == "FAILURE") {
									this.serverIfaceId = interfaceId;
									this.offlineScrName = screenId;
									this.subReqRefNo = referenceNo;
									var sql = "SELECT SCREENID,INTERFACEID,REFNO,APPREQBODY FROM " + this.offlineTable + " WHERE REFNO = " + referenceNo;
									this.offlineAction = "SUBMIT";
									this.offlineData(sql);
								}
							}
						}
					}
				}
			}
		}
	},
	offlineData : function(query) {
		var dateBase = "";
		if(this.deviceType == "WEB") {
			dateBase = apz.offline.apz.offlineDB;
		} else {
			dateBase = "APPSDB";
		}
		var jsonObj = {
			"queryId" : "offlineQuery",
			"callBack" : apz.offline.offlineCB,
			"callBackObj" : this,
			"databaseName" : dateBase,
			"executeQuery" : query
		};
		this.apz.ns.executeSql(jsonObj);
	},
	offlineCB : function(resObj){
		var fwdData = resObj.fwdData;
		if(resObj.status){
			this.success(resObj.sqlResult);
		} else {
			this.failure(resObj.sqlResult);
		}
	},
	success : function(resStr) {
		if(resStr == "success" || resStr == "Success") {
			this.executeSqlCallBack('S');
		} else if(this.offlineAction == "SUMMARY"){
			this.executeSqlCallBack(resStr);
		} else {
			var resObj = JSON.parse(resStr);
			this.offlineArr = this.getOfflineArray(resObj);
			if (this.offlineArr.length > 0) {
				var scrName = this.getScreenNameWithAppId();
				var jsonObj = {};
				for (var key in this.offlineArr[0])
					jsonObj[key.toUpperCase()] = this.offlineArr[0][key];
				this.isOfflineCall = "true";
				this.offlineRefNo = parseInt(jsonObj.REFNO);
				this.offlineScrid = scrName;
				this.offlineArr = this.offlineArr.slice(1);
				var reqObj = {};
				reqObj.ifaceName = this.getIfaceNameWithoutAppId(jsonObj.INTERFACEID);
				reqObj.req = JSON.parse(jsonObj.APPREQBODY);
				reqObj.callBack = this.postServerCallBack;
				reqObj.callBackObj = this;
				reqObj.buildReq = "N";
				reqObj.async = false;
				this.apz.server.callServer(reqObj);
			}
		}
	},
	failure : function(pjson) {
		this.executeSqlCallBack('F');
	},
	postServerCallBack : function(params) {	
		var appId = params.appId ? params.appId : apz.currAppId;
		var apzIfaceName = appId + "__" + params.ifaceName;	
		this.callId = params.callId;		
		this.offlineIfaceId = apzIfaceName;
		this.OfflineStatus = params.uploadStatus;
		if (params.status && !params.errors) {
		//replacing 'this' as scope points to params//
			if (this.offlineSucessData.indexOf(apz.offline.offlineRefNo) == -1)
				this.offlineSucessData.push(apz.offline.offlineRefNo);
		} else {
			if (apz.offline.offlineFailData.indexOf(apz.offline.offlineRefNo) == -1)
				apz.offline.offlineFailData.push(apz.offline.offlineRefNo);
		}
		if(this.offlineArr.length > 0){
			var scrName = this.getScreenNameWithAppId();
			var jsonObj = {};
			for (var key in this.offlineArr[0])
			jsonObj[key.toUpperCase()] = this.offlineArr[0][key];
			this.isOfflineCall = "true";
			this.offlineRefNo = parseInt(jsonObj.REFNO);
			this.offlineScrid = scrName;
			this.offlineArr = this.offlineArr.slice(1);
			var reqObj = {};
			reqObj.ifaceName = this.getIfaceNameWithoutAppId(jsonObj.INTERFACEID);
			reqObj.req = JSON.parse(jsonObj.APPREQBODY);
			reqObj.callBack = this.postServerCallBack;
			reqObj.callBackObj = this;
			reqObj.buildReq = "N";
			reqObj.async = false;
			apz.server.callServer(reqObj);
		}else{

			apz.currScr = apz.offline.getScreenNameWithoutAppId(apz.offline.offlineScrid);
			if (this.offlineSucessData.length > 0) {
				apz.offline.deleteOfflineData(this.offlineSucessData);
			}
			if (apz.offline.offlineFailData.length > 0) {
				var setClause = "ENDTIME='" + apz.offline.sysDateTime() + "'";
				var whereClause = "REFNO IN ";
				whereClause += "(";
				for (var i = 0; i < apz.offline.offlineFailData.length; i++) {
					whereClause += "'" + apz.offline.offlineFailData[i] + "',";
				}
				whereClause = whereClause.substr(0, whereClause.length - 1);
				whereClause += ")";
				var sql = "UPDATE " + apz.offline.offlineTable + " SET " + setClause + " WHERE " + whereClause;
				apz.offline.offlineAction = "UPDATESCREEN";
				apz.offline.isOfflineScr = 'false';
				//console.log(sql);
				apz.offline.offlineData(sql);
				//apz.offline.offlineAction = "SUBMITALL";
			}
			apz.offline.offlineRefNo = "";
			apz.offline.offlineScrName = "";
		}
	},
	executeSqlCallBack : function(result) {
		var offlineCBObj = {
			callId: this.callId,
			ifaceId: this.offlineIfaceId,
			status: this.OfflineStatus
		};
		switch (this.offlineAction) {
			case "SUMMARY":
				var finalRes = '{"' + this.offlineNodeName + '":' + result + '}';
				this.offlineDataInfo = finalRes;
				this.isOfflineCall = "true";
				if(this.apz.appId == this.apz.currAppId){
					this.apz.launchScreen({"scr":this.offlineScreenName});
				} else {
					this.apz.launchApp({"appId":this.apz.appId,"scr":this.offlineScreenName});
				}
				break;
			case "INSERTNEW":
				if (result == "S") {
					if (!this.apz.isNull(this.offlineCallBack)) {
						try {
							offlineCBObj.statusCode = "APZ-OFF-001";
							this.offlineCallBack(offlineCBObj);
						} catch (ce) {}
					}
				} else{
					if (!this.apz.isNull(this.offlineCallBack)) {
						try {
							offlineCBObj.statusCode = "APZ-OFF-002";
							this.offlineCallBack(offlineCBObj);
						} catch (ce) {}
					}
				}
				break;
			case "UPDATE":
				if (result == "S") {
					if (!this.apz.isNull(this.offlineCallBack)) {
						try {
							offlineCBObj.statusCode = "APZ-OFF-005";
							this.offlineCallBack(offlineCBObj);
						} catch (ce) {}
					}
				} else {
					if (!this.apz.isNull(offlineCallBack)) {
						try {
							offlineCBObj.statusCode = "APZ-OFF-006";
							this.offlineCallBack(offlineCBObj);
						} catch (ce) {}
					}
				}
				break;
			case "DELETEDATA":
				if (result == "S") {
					if(this.apz.isOfflineScr == 'true') {
						this.apz.isOfflineScr = "false";
						this.offlineAction = "SUMMARY";
						this.offlineData('SELECT REFNO,SCREENID,INTERFACEID,STATUS,STARTTIME,ENDTIME FROM ' + this.offlineTable);
					} else {
						this.offlineSucessData = [];
					}
					if (!this.apz.isNull(this.offlineCallBack)) {
						try {
							offlineCBObj.statusCode = "APZ-OFF-003";
							this.offlineCallBack(offlineCBObj);
						} catch (ce) {}
					}
				} else {
					if (!this.apz.isNull(this.offlineCallBack)) {
						try {
							offlineCBObj.statusCode = "APZ-OFF-004";
							this.offlineCallBack(offlineCBObj);
						} catch (ce) {}
					}
				}
				setTimeout(function() {
					apz.offline.loadOfflineScreen();
				}, 100);
				break;
			case "UPDATESCREEN":
				if(this.isOfflineScr == 'false') {
					apz.offline.offlineFailData = [];
				}
				apz.offline.offlineAction = "SUBMITALL";
				break;
			case "SUBMIT":
				var ifaceBody = result;
				var ref = this.apz.subReqRefNo;
				this.apz.currScr = this.getScreenNameWithoutAppId(this.offlineScrName);
				this.apz.isOfflineCall = "true";
				this.apz.offlineRefNo = ref;
				var jsonStr = ifaceBody.substring(ifaceBody.indexOf('"appreqbody":"') + 14, ifaceBody.length - 3);
				var reqObj = {};
				reqObj.ifaceName = this.getIfaceNameWithoutAppId(this.serverIfaceId);
				reqObj.req = jsonStr;
				reqObj.CallBack = "";
				reqObj.buildReq = "N";
				reqObj.async = 1;
				this.apz.server.callServer(reqObj);
				break;
			}			
	},	
	multiSelected : function(tbl) {
		var count = 0;
		var rowsLen = tbl.rows.length;
		for(var i = 0; i < rowsLen; i++) {
			if($(tbl.rows[i].cells[0].childNodes[0]).find('input[type="checkbox"]').is(":checked")) {
				count = count + 1;
			}
		}
		if(count > 1) {
			return true;
		}
		return false;
	},
	getTableUIObj : function() {
		var tableId = this.apz.appId + "__" + this.offlineScreenName + "__" + this.offlineNodeName + "_table";
		return document.getElementById(tableId);
	},
	getOfflineArray : function(result) {
		var resArr = [];
		for( i = 0; i < result.length; i++) {
			resArr.push(result[i]);
		}
		return resArr;
	},	
	sysDateTime : function() {
		var dObj = new Date();
		var day = dObj.getDate();
		var month = dObj.getMonth() + 1;
		var year = dObj.getFullYear();
		var hr = dObj.getHours();
		var sec = dObj.getSeconds();
		var min = dObj.getMinutes();
		if(day < 10) {
			day = "0" + day;
		}
		if(month < 10) {
			month = "0" + month;
		}
		if(min < 10) {
			min = "0" + min;
		}
		if(sec < 10) {
			sec = "0" + sec;
		}
		if(hr < 10) {
			hr = "0" + hr;
		}
		var dateval = year + "-" + month + "-" + day + " " + hr + ":" + min + ":" + sec;
		return dateval;
	},
	getScreenNameWithAppId : function(scrName){
		scrName = scrName ? scrName : this.apz.currScr;
		return this.apz.appId + "__" + scrName;
	},
	getScreenNameWithoutAppId : function(scrName){
		if(!scrName){
			return this.apz.currScr;
		} else {
			if(scrName.indexOf(this.apz.appId+"__") == 0){
				return scrName.substr(this.apz.appId+"__".length);
			} else {
				return scrName;
			}
		}
	},
	getIfaceNameWithoutAppId : function(ifaceName){
		var firstSepIndex = ifaceName.indexOf("__");
		return ifaceName.substr(firstSepIndex+2);
	}
};