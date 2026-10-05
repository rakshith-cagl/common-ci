/**
 * @namespace
 */
 Apz.Lov = function(apz) {
	this.apz = apz;
	this.lovData = {};
    this.lovData.ifaceId = "appzillonLOVReq";
	this.lovData.pageSize = "25";
	this.lovData.totalPages = "";
	// Return fields.
	this.lovData.returnFieldIDs = new Array();
	this.lovData.returnFieldColIDMap = new Array();
	// Resultset fields.
	this.lovData.resultFieldColumns = new Array();
	this.lovData.resultFieldLabels = new Array();
	this.lovData.resultFieldVisible = new Array();
	this.lovData.resultFieldDatatypes = new Array();
	this.lovData.resultFieldColumnStr = "";
	this.lovData.resultFieldDatatypeStr = "";
	// Filter fields.
	this.lovData.filterFieldIDs = new Array();
	this.lovData.filterFieldValStr = "";
	// Bind variables.
	this.lovData.bindVariableIDs = new Array();
	this.lovData.bindVariableValueStr = "";
	// LOV request
	this.lovData.reqObj = {};
	this.lovData.queryResult = [];
	// Changes for offline server availability check enhancement
	this.lovData.session = "";
}
Apz.Lov.prototype = {
	validateLovData : function(elmId, cntnrId,appId){
		/// Entry point on blur of the lov fields
		var lovId = "";
		var params = {};
		params.elmId = elmId;
		params.cntnrId = cntnrId;
		params.appId = appId;
		this.lovDetails(params);
		this.lovData.filterFieldValStr = this.apz.getElmValue(elmId);
		lovId = this.apz.scrMetaData.containersMap[cntnrId].elmsMap[elmId].lovId;
		this.prepareLOVrequest(lovId, elmId);
		if (this.apz.isNull(this.lovData.queryResult.appzillonLOVReqResponse)) {
			this.displayLov();
			this.apz.setElmValue(this.lovData.filterFieldIDs[0] + "Filter",this.apz.getElmValue(elmId));
			this.toggleLov();
		} else if (this.lovData.queryResult.appzillonLOVReqResponse.result.resultset.length != 1) {
			this.displayLov();
			this.apz.setElmValue(this.lovData.filterFieldIDs[0] + "Filter",this.apz.getElmValue(elmId));
			this.toggleLov();
		} else if (this.lovData.queryResult.appzillonLOVReqResponse.result.resultset.length == 1) {
			var serverLOVResponse = this.lovData.queryResult.appzillonLOVReqResponse;
			if (!this.lovData.isME) {
				for (var n = 0; n < this.lovData.resultFieldColumns.length; n++) {
					var returnTo = this.lovData.returnFieldColIDMap[this.lovData.resultFieldColumns[n]];
					if (!this.apz.isNull(returnTo)) {
						this.apz.setElmValue(returnTo,serverLOVResponse.result.resultset[0][this.lovData.resultFieldColumns[n]]);
					}
				}
			} else {
				for (var n = 0; n < this.lovData.resultFieldColumns.length; n++) {
					var returnTo = this.lovData.returnFieldColIDMap[this.lovData.resultFieldColumns[n]];
					if (!this.apz.isNull(returnTo)) {
						returnTo = returnTo + this.lovData.clickedRow;
						var val = serverLOVResponse.result.resultset[0][this.lovData.resultFieldColumns[n]];
						if (this.apz.isNull(val)) {
							val = '';
						}
						this.apz.setElmValue(returnTo,val);
					}
				}
			}
		}
	}, 
	/**
	 * 
	 * @param {string} elmId 
	 * @param {string} cntnrId 
	 * @param {string} appId 
	 * @description
	 * <b>Callbacks</b><br>
	 * |  Name | Description   |
	 * | :------------- |:-------------|
	 * | apz.app.preCallLov      | This callback will be called before the call LOV |
	 * | apz.app.postCallLov     | This callback will be called after the call LOV    |
	 * @example
	 *apz.app.preCallLov = function("html_lov", "container_form_1"){
	 * 	//// write code to perform some action before the call LOV.
	 * 	return true/false;
	 * }
	 * @example
	 * 	apz.app.postCallLov  = function (elmId, containerId) {
     *        // content 
     *     };
	 */
	callLov : function(elmId, cntnrId,appId, angularComponentContext) {
		/// Entry point on click of the lov button
		var callLov = true;
		this.isToggleReq = true;
		if(this.apz.isFunction(this.apz.app.preCallLov)){
			callLov = this.apz.app.preCallLov(elmId, cntnrId);
			if (this.apz.isNull(callLov)) {
				callLov = true;
			}
		}
		
		if (angularComponentContext && this.apz.isFunction(angularComponentContext.beforeCallLov)) {
			callLov = angularComponentContext.beforeCallLov(elmId, cntnrId);
			if (this.apz.isNull(callLov)) {
				callLov = true;
			}
		}
		if (callLov) {
			var params = {};
			params.elmId = elmId;
			params.cntnrId = cntnrId;
			params.appId = appId;
			this.lovDetails(params);
			var finalElmId = elmId;
			if(this.lovData.isME){
				finalElmId = finalElmId.substr(0, finalElmId.lastIndexOf('_'));
			}
			var elmData = this.apz.scrMetaData.containersMap[cntnrId].elmsMap[finalElmId];
			lovId =  elmData.lovId;
			if(!this.apz.isNull(this.apz.lovs[appId][lovId]) && this.apz.lovs[appId][lovId].queryByDefault == "Y" ){
				this.prepareLOVrequest(lovId, elmId);
			}else{
				this.lovData.queryResult.appzillonLOVReqResponse = "";
			}
			//this.displayLov();
		}
		if(this.apz.isFunction(this.apz.app.postCallLov)){
			this.apz.app.postCallLov(elmId, cntnrId);
		}
		
		if(angularComponentContext && this.apz.isFunction(angularComponentContext.afterCallLov)){
			angularComponentContext.afterCallLov(elmId, cntnrId);
		}
	}, lovDetails : function(params) {
		//Expects elmId, cntnrId,appId
		var lovId = "";
		var lovObj = "";
		var elmData = "";
		var multiRec = this.apz.scrMetaData.containersMap[params.cntnrId].multiRec;
		this.lovData.isME = false;
		this.lovData.currpage = '1';
		this.lovData.filterFieldValStr = "";
		this.lovData.clickedRow = -1;
		if (multiRec == "Y") {
			this.lovData.isME = true;
			this.lovData.clickedRow = params.elmId.substr(params.elmId.lastIndexOf('_'), params.elmId.length);
			params.elmId = params.elmId.substr(0, params.elmId.lastIndexOf('_'));
		}
		elmData = this.apz.scrMetaData.containersMap[params.cntnrId].elmsMap[params.elmId];
		lovId = elmData.lovId;
		lovObj = this.apz.lovs[params.appId][lovId];
		this.lovData.colClass = elmData.lovWidthClass;
		this.lovData.minWidth = elmData.lovMinWidth;
		this.populateReturnFieldDetails(elmData, lovObj);
		this.populateResultSetFieldDetails(lovObj);
		this.bindVariableDetails(elmData);
		this.getBindValues();
		this.setSessionFlag(lovObj);
		var args = {};
		args.filterObj = lovObj;
		args.lovId = lovId;
		args.elmId = params.elmId;
		this.populatefilterfields(args);
		this.getLOVTitle(params.elmId);
		this.setLOVWidth();
	}, populateReturnFieldDetails : function(elmData, lovObj) {
		this.lovData.returnFieldIDs = elmData.returnFields;
		if(lovObj && lovObj.columns){
			for (var col = 0; col < lovObj.columns.length; col++) {
				this.lovData.returnFieldColIDMap[lovObj.columns[col]] = this.lovData.returnFieldIDs[col];
			}
		}
	}, populateResultSetFieldDetails : function(lovObj) {
		var sep = "";
		var val = "";
		this.lovData.resultFieldColumnStr = "";
		this.lovData.resultFieldDatatypeStr = "";
		var length = lovObj.titles.length;
		lovObj.tlabel = [];
		for(let i = 0; i < length; i++){
			lovObj.tlabel.push(this.apz.getLabel(lovObj.titles[i]));
		}
		this.lovData.resultFieldColumns = lovObj.columns;
		this.lovData.resultFieldLabels = lovObj.tlabel;
		this.lovData.resultFieldVisible = lovObj.visible;
		this.lovData.resultFieldDatatypes = lovObj.dtypes;
		if(this.lovData.resultFieldColumns){
			for (var i = 0; i < this.lovData.resultFieldColumns.length; i++) {
				if (this.lovData.resultFieldColumns[i] != null && this.lovData.resultFieldColumns[i] != "") {
					if (i != 0) {
						sep = "|";
					}
					val = this.lovData.resultFieldColumns[i];
					this.lovData.resultFieldColumnStr = this.lovData.resultFieldColumnStr + sep + val;
				}
			}
		}
		if(this.lovData.resultFieldDatatypes){
			for (var j = 0; j < this.lovData.resultFieldDatatypes.length; j++) {
				sep = "";
				if (this.lovData.resultFieldDatatypes[j] != null && this.lovData.resultFieldDatatypes[j] != "") {
					if (j != 0) {
						sep = "|";
					}
					val = this.lovData.resultFieldDatatypes[j];
					this.lovData.resultFieldDatatypeStr = this.lovData.resultFieldDatatypeStr + sep + val;
				}
			}
		}
	}, bindVariableDetails : function(elmData) {
		this.lovData.bindVariableIDs = elmData.bindVariables;
	}, getBindValues : function() {
		this.lovData.bindVariableValueStr = "";
		var sep = "";
		var val = "";
		for (var n = 0; n < this.lovData.bindVariableIDs.length; n++) {
			if (this.lovData.bindVariableIDs[n] != null && this.lovData.bindVariableIDs[n] != "") {
				if (n != 0) {
					sep = "|";
				}
				if (this.lovData.bindVariableIDs[n].toLowerCase() == "user.lang") {
					val = this.apz.language;
				} else {
					if (this.lovData.isME) {
						try {
							val = this.apz.getElmValue(this.lovData.bindVariableIDs[n] + this.lovData.clickedRow);
						} catch (err) {
							val = this.apz.getElmValue(this.lovData.bindVariableIDs[n]);
						}
					} else {
						val = this.apz.getElmValue(this.lovData.bindVariableIDs[n]);
					}
				}
				this.lovData.bindVariableValueStr = this.lovData.bindVariableValueStr + sep + val;
			}
		}
	}, populatefilterfields : function(params) {
		//Expects filterObj, lovId, elmId
		var filterFieldLabels = new Array();
		var filtersFlags = params.filterObj.filters ? params.filterObj.filters : new Array();
		this.lovData.filterFieldIDs = [];
		var j = 0;
		for (var i = 0; i < filtersFlags.length; i++) {
			if (filtersFlags[i] == "Y") {
				// filterFieldLabels[j] = params.filterObj.titles[i];
				filterFieldLabels[j] = params.filterObj.tlabel[i];
				this.lovData.filterFieldIDs[j] = params.filterObj.columns[i];
				j++;
			}
		}
		$(lovheader).empty();
		if (this.lovData.filterFieldIDs.length >= 1) {
			var l_header =
				'<ul class="srb eoc wrapped"><li class="etw-40"><label class="elabel" for="">&nbsp;</label></li><li class="etw-60 eic rht"><button class="ett-bttn pri med icr" onclick="apz.lov.reQuery(' +
				"'" + params.lovId + "'" + ',' + "'" + params.elmId + "'" +
				');" title="Go"><svg aria-hidden="true" class="ett-icon icon-search px24  "><use xlink:href="#icon-search"></use></svg><span id="lit_search"></span></button></li></ul>';
				
		} else {
			$("#lovheader").addClass("sno");
		}
		var obj = $(lovheader);
		for (var n = 0; n < this.lovData.filterFieldIDs.length; n++) {
			var l_reductionfield =
				'<ul class="srb eoc wrapped ma1"><li class="etw-40"><label class="elabel" for="">' +
				filterFieldLabels[n] + '</label></li><li class="etw-60 eic"><span class="ecn etw-100"><input type="text" class="ett-inpt pri" id="' + this.lovData.filterFieldIDs[
					n] + "Filter" + '" placeholder="' + filterFieldLabels[n] + '"/></span></li></ul>';
			this.appendHTML('#lovheader', l_reductionfield);
		}
		this.appendHTML('#lovheader', l_header);
		if (!this.apz.isNull(this.apz.validatedata) && this.apz.validatedata == 'true') {
			for (var j = 0; j < this.lovData.filterFieldIDs.length; j++) {
				document.getElementById(this.lovData.filterFieldIDs[j] + "Filter").value = document.getElementById(params.elmId).value + "%";
			}
			this.getreQueryFilterValues(params.lovId, params.elmId);
		}
	}, getLOVTitle : function(elmId) {
		$(lovtitle).empty();
		var lovTitle = "";
		var l_title = "";
		if (!this.apz.isNull($('#' + elmId).closest('td')[0])) {
			var cellIdx = $('#' + elmId).closest('td')[0].cellIndex;
			lovTitle = $($('#' + elmId).closest('table').find('th')[cellIdx]).text().replace(/\n/g, '');
		} else {
			var fieldLabel = elmId + '_lbl';
			lovTitle = $('#' + fieldLabel).text();
		}
		l_title =  lovTitle;
		$(lovtitle).append(l_title);
	},setLOVWidth : function() {
		$('#lovModal_window').addClass(this.lovData.colClass);
		//this.appendHTML(,winClass);
		
	}, appendHTML : function(id, string) {
		var deviceType = this.apz.deviceType;
		if (deviceType == 'WIN8SURFACE' || deviceType == 'WIN8.1PHONE' || deviceType == 'WIN8.1SURFACE') {
			MSApp.execUnsafeLocalFunction(function() {
				$(id).append(string);
			});
		} else {
			$(id).append(string);
		}
	}, setValues : function(){
		if(!apz.isNull(this.lovData.queryResult.appzillonLOVReqResponse)){
			var tbody = $("#tablebox").find("table").find("tbody");
			var presp = this.lovData.queryResult.appzillonLOVReqResponse.result.resultset;
			for(var a=0; a < presp.length; a++){
				var lrow =  tbody.find('tr')[a];
	            var noofcolumns = this.lovData.resultFieldColumns.length;
	            for(var b=0; b < noofcolumns; b++){
	            	var lval = presp[a][this.lovData.resultFieldColumns[b]];
	            	if (apz.isNull(lval)) {
	                    lval = "";
	                }
	                var lcol = $(lrow).find('td')[b];
	                $(lcol).text(lval);
	            }
			}
		}
	},
	/**
	 * 
	 * @param {string} lovId 
	 * @param {string} elmId 
	 * @description
     * <b>Callbacks</b><br>
     * |  Name | Description   |
     * | :------------- |:-------------|
     * | apz.app.postGetreQueryFilterValues | This API will be called after reQuery the FilterValues. |

     * @example
     * apz.app.postGetreQueryFilterValues = function(lovId, elmId, this.lovData.filterFieldValStr){
	 * 	//// write code to perform some action just after getreQueryFilterValues execution.
	 * }
     *   
	 * 
	 */
	getreQueryFilterValues : function(lovId, elmId) {
		var sep = "";
		var fltrFldsStr = "";
		var fltrFld = "";
		this.lovData.filterFieldValStr = "";
		for (var n = 0; n < this.lovData.filterFieldIDs.length; n++) {
			if (n != 0) {
				sep = "|";
			}
			fltrFld = this.lovData.filterFieldIDs[n] + "Filter";
			this.lovData.filterFieldValStr = this.lovData.filterFieldValStr + sep + document.getElementById(fltrFld).value;
		}
		this.lovData.filterFieldValStr = this.lovData.filterFieldValStr + sep;
		//// Filter Fields Handle APZLOVFILTERHOOK
		if(this.apz.isFunction(this.apz.app.postGetreQueryFilterValues)){
			fltrFldsStr = this.apz.app.postGetreQueryFilterValues(lovId, elmId, this.lovData.filterFieldValStr);
		} else {
			fltrFldsStr = this.lovData.filterFieldValStr;
		}

		this.lovData.filterFieldValStr = fltrFldsStr;
	}, displayLov : function() {
		var lovTable = '';
		$('#tablebox').empty();
		$("#lovcolumn").removeClass().addClass("lcolcenter " + this.lovData.colClass + "");
		$("#lovcolumn").css("min-width", this.lovData.minWidth);
		var width = this.getLovTableWidth();
		var serverLOVResponse = "";
		if (!this.apz.isNull(this.lovData.queryResult.appzillonLOVReqResponse)) {
			this.lovData.totalPages = this.lovData.queryResult.appzillonLOVReqResponse.totalpages;
		} else {
			this.lovData.totalPages = 0;
		}
		lovTable +=		
			/*'<ul class="ttl"><li class="ttext"><h4>Results</h4></li><li> </li><li class="tprevnext"><button class="icon icon-arrow--left size2 px34" id="prevPage" onclick="apz.lov.prevPage();"title="Previous"></button></li><li class="tpagecol"><input id="pageno" type="text" class="apz-element pageno" value="' +
			this.lovData.currpage + '"></li><li class="tpagetext"><span>/&nbsp;</span><span>' + this.lovData.totalPages + '</span></li><li class="tprevnext"><button class="icon icon-arrow--right size2 px34" id="nextPage" onclick="apz.lov.nextPage(this);"title="Next"></button></li><li class="tprevnext rft"> <button id="pagefrwdicon" class="icon icon-circle-arrow-right size2 px34" onclick="apz.lov.nextPage(this)" tabindex = "0"></button></li></ul></ul>';*/
			'<ul class="ttl"><li class="ttl-ctr"><ul class="ttv"><li class="lbl"><h4 id = "lit_result"></h4></li></ul></li><li class="pgn-ctr"><ul class="pgn"><li class="pnx"><button class="ett-bttn tsp med" id="prevPage" href="javascript:;" aria-label="Previous Page" onclick="apz.lov.prevPage();"><svg class="icon icon-chevron-left px16"><use xlink:href="#icon-chevron-left"></use></svg></button></li><li class="pcl"><input id="pageno" type="text" class="pageno" value="'+this.lovData.currpage + '"></li><li class="ptx"><span>/&nbsp;</span><span>' + this.lovData.totalPages + '</span></li><li class="pnx"><button class="ett-bttn tsp med" href="javascript:;" aria-label="Next Page" onclick="apz.lov.nextPage(this)"><svg class="icon icon-chevron-right px16"><use xlink:href="#icon-chevron-right"></use></svg></button></li></ul></li></ul>';
		lovTable +=
			'<div class="tabl-ctr hvr"><table class="tabl" summary="Description of the table"   border="0" cellpadding="0" cellspacing="0" ><thead><tr>';
		var nooflabels = this.lovData.resultFieldLabels.length;
		var dispClass='';
		for (var i = 0; i < this.lovData.resultFieldLabels.length; i++) {
			dispClass = '';
			if(this.lovData.resultFieldVisible[i]=='N'){
				dispClass ='sno'
			}
			var label = this.lovData.resultFieldLabels[i];
			label = this.apz.getLabel(label);
			lovTable += '<th style="width: ' + width / nooflabels + 'px" class='+ dispClass +'>' + label + '</th>';
		}
		lovTable += '</tr></thead><tbody data-provides="rowlink">';
		if (!this.apz.isNull(this.lovData.queryResult.appzillonLOVReqResponse)) {
			serverLOVResponse = this.lovData.queryResult.appzillonLOVReqResponse;
			for (var n = 0; n < serverLOVResponse.result.resultset.length; n++) {
				if (!this.lovData.isME) lovTable += '<tr data-dismiss="modal" onclick = "apz.lov.returnRow(' + n + ');apz.lov.toggleLov();">';
				else lovTable += '<tr data-dismiss="modal" onclick = "apz.lov.returnMEtablerow(' + n + ');apz.lov.toggleLov();">';
				var noofcolumns = this.lovData.resultFieldColumns.length;
				for (var m = 0; m < this.lovData.resultFieldColumns.length; m++) {
					dispClass = '';
					if(this.lovData.resultFieldVisible[m]=='N'){
						dispClass ='sno'
					}
					lovTable += '<td style="width: ' + width / noofcolumns + 'px" class='+ dispClass +'></td>';
				}
				lovTable += '</tr>';
			}
		}
		lovTable += '</tbody></table></div>';
		this.appendHTML('#tablebox', lovTable);
		this.setValues();
	}, getLovTableWidth : function() {
		var scrWidth = $(window).width();
		var widthPct;
		var width;
		if (this.lovData.colClass == 'lcol1') {
			widthPct = "6.5";
		} else if (this.lovData.colClass == 'lcol2') {
			widthPct = "15";
		} else if (this.lovData.colClass == 'lcol3') {
			widthPct = "23.5";
		} else if (this.lovData.colClass == 'lcol4') {
			widthPct = "32";
		} else if (this.lovData.colClass == 'lcol5') {
			widthPct = "40.5";
		} else if (this.lovData.colClass == 'lcol6') {
			widthPct = "49";
		} else if (this.lovData.colClass == 'lcol7') {
			widthPct = "57.5";
		} else if (this.lovData.colClass == 'lcol8') {
			widthPct = "66.5";
		} else if (this.lovData.colClass == 'lcol9') {
			widthPct = "74.5";
		} else if (this.lovData.colClass == 'lcol10') {
			widthPct = "83.5";
		} else if (this.lovData.colClass == 'lcol11') {
			widthPct = "91.5";
		} else if (this.lovData.colClass == 'lcol12') {
			widthPct = "100";
		}
		try {
			width = scrWidth * (widthPct / 100);
		} catch (e) {
			width = scrWidth;
		}
		if (this.lovData.minWidth.indexOf("px") > -1) {
			var minWidthVal = parseInt(this.lovData.minWidth.split('px')[0]);
		} else {
			var minWidthVal = parseInt(this.lovData.minWidth.split('em')[0]) * 16;
		}
		if (width < minWidthVal) {
			width = minWidthVal;
		}
		return width;
	}, fetchLOVData : function(reqObj) {
		var params = {};
		params.internal = true;
		params.ifaceName = this.lovData.ifaceId;
		params.req = reqObj.request;
		params.callBack = reqObj.callBack;
		params.callBackObj = reqObj.callBackObj;
		params.session = this.lovData.session;
		params.async = false;
		if(reqObj.async){
			params.async = reqObj.async;
		}
		this.apz.server.sendReq(params);
	}, prepareLOVrequest : function(lovId, elmId) {
		var reqObj = {};
		var body = {};
		body.elementid = elmId;
		body.lovid = lovId;
		body.interfaceid = this.lovData.ifaceId;
		body.userid = this.apz.userId;
		body.appid = this.apz.appId;
		body.resultfieldcolumnstr = this.lovData.resultFieldColumnStr;
		body.resultfielddatatypestr = this.lovData.resultFieldDatatypeStr;
		body.filterfieldvalstr = this.lovData.filterFieldValStr;
		body.bindvariablevaluestr = this.lovData.bindVariableValueStr;
		body.pagesize = this.lovData.pageSize;
		body.currpage = this.lovData.currpage;
		reqObj.request = {};
		reqObj.request["appzillonLOVReqRequest"] = body;
		reqObj.callBack = this.callLOVResult;
		reqObj.callBackObj = this;
		this.lovData.reqObj = reqObj;
		this.fetchLOVData(reqObj);
	},
	/**
	 * 
	 * @param {number} row 
	 * @description
     * <b>Callbacks</b><br>
     * |  Name | Description   |
     * | :------------- |:-------------|
     * | apz.app.postReturnrow | This API will be called after returnRow method. |

     * @example
     * apz.app.postReturnrow = function(row){
	 * 	//// write code to perform some action just after returnRow execution.
	 * }
     *  
	 */
	returnRow : function(row) {
		var serverLOVResponse = this.lovData.queryResult.appzillonLOVReqResponse;
		var rowClicked = serverLOVResponse.result.resultset[row];
		for (var n = 0; n < this.lovData.resultFieldColumns.length; n++) {
			var returnTo = this.lovData.returnFieldColIDMap[this.lovData.resultFieldColumns[n]];
			if (!this.apz.isNull(returnTo)){
			    this.apz.setElmValue(returnTo,rowClicked[this.lovData.resultFieldColumns[n]]);	
			}
		}
		if(this.apz.isFunction(this.apz.app.postReturnrow)){
			this.apz.app.postReturnrow(row);
		}
	}, callLOVResult : function(params) {
		this.lovData.queryResult = [];
		if (params.status && params.resFull.appzillonHeader.status) {
			this.lovData.queryResult = params.res;
		} else {
			this.isToggleReq = false;
			if(params.errors && params.errors[0].errorCode.indexOf("$") != 0){
				apz.dispMsg({"code":params.errors[0].errorCode});
			}
		}
		this.displayLov();
		if(this.isToggleReq) {
			this.toggleLov();
			this.isToggleReq = false;
		}
	}, returnMEtablerow : function(row) {
		var serverLOVResponse = this.lovData.queryResult.appzillonLOVReqResponse;
		var rowClicked = serverLOVResponse.result.resultset[row];
		for (var n = 0; n < this.lovData.resultFieldColumns.length; n++) {
			var returnTo = this.lovData.returnFieldColIDMap[this.lovData.resultFieldColumns[n]];
			if (!this.apz.isNull(returnTo)) {
				returnTo = returnTo + this.lovData.clickedRow;
				var val = rowClicked[this.lovData.resultFieldColumns[n]];
				if (this.apz.isNull(val)) {
					val = '';
				}
				this.apz.setElmValue(returnTo,val);
			}
		}
	}, 
	/**
	 * 
	 * @param {string} lovId 
	 * @param {string} elmId
	 * @description
     * <b>Callbacks</b><br>
     * |  Name | Description   |
     * | :------------- |:-------------|
     * | apz.app.postReQuery | This API will be called after reQuery. |

     * @example
     * apz.app.postReQuery = function(lovId, elmId){
	 * 	//// write code to perform some action just after reQuery execution.
	 * }
     *   
	 */
	reQuery : function(lovId, elmId) {
		this.lovData.currpage = '1';		// Set current page as 1 in case user want to search records with some conditions.
		this.getreQueryFilterValues(lovId, elmId);
		this.prepareLOVrequest(lovId, elmId);
		this.displayLov();
		if(this.apz.isFunction(this.apz.app.postReQuery)){
			this.apz.app.postReQuery(lovId, elmId);
		}
	}, 
	/**
	 * 
	 * @param {object} obj
	 * @description
     * <b>Callbacks</b><br>
     * |  Name | Description   |
     * | :------------- |:-------------|
     * | apz.app.postNextpage | This API will be called after Nextpage. |

     * @example
     * apz.app.postNextpage = function(){
	 * 	//// write code to perform some action just after Nextpage execution.
	 * }
     *    
	 */
	nextPage : function(obj) {
		val = document.getElementById('pageno').value;
		if(val != this.lovData.currpage && obj.id!="nextPage"){
			if (val > 0 && val <= this.lovData.totalPages){
				this.lovData.currpage = val;
			}
		} else if (this.lovData.currpage < this.lovData.totalPages){
			this.lovData.currpage = parseInt(this.lovData.currpage) + 1;
		}
		this.lovData.reqObj.request.appzillonLOVReqRequest.currpage = this.lovData.currpage.toString();
		this.fetchLOVData(this.lovData.reqObj);
		this.displayLov();
		if(this.apz.isFunction(this.apz.app.postNextpage)){
			this.apz.app.postNextpage();
		}
	}, 
	/**
	 * @description
     * <b>Callbacks</b><br>
     * |  Name | Description   |
     * | :------------- |:-------------|
     * | apz.app.postPrevpage | This API will be called after Prevpage. |

     * @example
     * apz.app.postPrevpage = function(){
	 * 	//// write code to perform some action just after Prevpage execution.
	 * }
     *  
	 */
	prevPage : function() {
		if (this.lovData.currpage > 1) {
			this.lovData.currpage = parseInt(this.lovData.currpage) - 1;
			this.lovData.reqObj.request.appzillonLOVReqRequest.currpage = this.lovData.currpage.toString();
			this.fetchLOVData(this.lovData.reqObj);
			this.displayLov();
		}
		if(this.apz.isFunction(this.apz.app.postPrevpage)){
			this.apz.app.postPrevpage();
		}
	}, toggleLov : function() {
		var lovTableSearch = this.apz.getLabel('LIT_SEARCH');
		var lovTableLabel = this.apz.getLabel('LIT_RESULT');
		var params = {"targetId":"lovModal"}
		this.apz.toggleModal(params);
		if(!document.getElementById("lit_search") == null){
	    	document.getElementById("lit_search").innerHTML = lovTableSearch;
	    }
	    if(!document.getElementById("lit_result") == null){
	   	 	document.getElementById("lit_result").innerHTML = lovTableLabel;
		}	
		
	}, setSessionFlag : function(lovObj) {
		if(lovObj && lovObj.sessionRequired){
			this.lovData.session = lovObj.sessionRequired;
		} else {
			this.lovData.session = "N";
		}
	}
}
