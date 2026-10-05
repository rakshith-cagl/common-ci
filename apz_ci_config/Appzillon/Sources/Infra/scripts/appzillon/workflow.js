Apz.Wf = function(apz){
   this.apz = apz;
   this.workFlowArray = new Array();
   this.currWorkFlowId = "";
   this.currStageId = "";
   this.currScreenId = "";
   this.currSeqNo = "";
   this.isWorkFlow = "Y";
   this.wfTxnRef = "";
   this.scrData = "";
   this.scrIface = "";
}
Apz.Wf.prototype = {
   buildWFArray : function() {
      var workFlowId;
      var stageId;
      var scrId;
      var seqNo;
      try {
		var wfObj = this.apz.workflows;
         if (wfObj != undefined) {
            for(var wfIdx = 0; wfIdx < wfObj.length; wfIdx++) {
               workFlowId = wfObj[wfIdx].workflowid;
               if (wfObj[wfIdx].workflowdetails != undefined) {
                  var wfDetObj = wfObj[wfIdx].workflowdetails;
                  for (var wfDetIdx = 0; wfDetIdx < wfDetObj.length; wfDetIdx++) {
                     stageId = wfDetObj[wfDetIdx].stageid;
                     scrId = wfDetObj[wfDetIdx].screenid;
                     seqNo = wfDetObj[wfDetIdx].seqno;
                     if (this.workFlowArray[workFlowId] == undefined) {
                        this.workFlowArray[workFlowId] = new Array();
                     }
                     this.workFlowArray[workFlowId][seqNo] = {};
                     this.workFlowArray[workFlowId][scrId] = {};
                     this.workFlowArray[workFlowId][stageId] = {};
                     this.workFlowArray[workFlowId][seqNo].stageId = stageId;
                     this.workFlowArray[workFlowId][seqNo].screenId = scrId;
                     this.workFlowArray[workFlowId][scrId].stageId = stageId;
                     this.workFlowArray[workFlowId][scrId].seqNo = seqNo;
                     this.workFlowArray[workFlowId][stageId].screenId = scrId;
                     this.workFlowArray[workFlowId][stageId].seqNo = seqNo;
                  }
               }
            }
         }
      } catch (e) {  }
   },
   getStageID : function(wfObj) {
      return this.workFlowArray[wfObj.workFlowId][wfObj.id].stageID;
   },
   getScreenID : function(wfObj) {
      return this.workFlowArray[wfObj.workFlowId][wfObj.id].screenId;
   },
   getSeqNo : function(wfObj) {
      return this.workFlowArray[wfObj.workFlowId][wfObj.id].seqNo;
   },
   setStageID : function(wfObj) {
      this.workFlowArray[wfObj.workFlowId][wfObj.id].stageID = wfObj.val;
   },
   setScreenID : function(wfObj) {
      this.workFlowArray[wfObj.workFlowId][wfObj.id].screenId = wfObj.val;
   },
   setSeqNo : function(wfObj) {
      this.workFlowArray[wfObj.workFlowId][wfObj.id].seqNo = wfObj.val;
   },
   setCurrWorkflowDetails : function(wfObj) {
      this.currWorkFlowId = wfObj.workflowId;
      this.currStageId = wfObj.stageId;
      this.currScreenId = wfObj.screenId;
      this.currSeqNo = wfObj.seqNo;
	  this.scrIface = wfObj.iface;
   },
   getCurrWorkflowID : function() {
      return this.currWorkFlowId;
   },
   getCurrStageID : function() {
      return this.currStageId;
   },
   getCurrScreenID : function() {
      return this.currScreenId;
   },
   getCurrSeqNo : function() {
      return this.currSeqNo;
   },
   getNextStageID : function(action) {
      var nextStageId;
      try {
         //nextStageID = appzillon.app.getNextStageId(action);
         if (!this.apz.isNull(nextStageId) && nextStageId != this.currSeqNo) {
            //this.workflowArray[this.currWorkflowID][parseInt(this.currSeqNo) + 1].stageID=pnextStageID;
            //this.setStageID(this.currWorkflowID,parseInt(this.currSeqNo) + 1,nextStageID);
            return nextStageId.toString()
         }
      } catch (err) {   }
      if (action != "update" && action != "reject") {
         nextStageId = this.workFlowArray[this.currWorkFlowId][parseInt(this.currSeqNo) + 1].stageId;
      } else {
         nextStageId = this.workFlowArray[this.currWorkFlowId][parseInt(this.currSeqNo)].stageId;
      }
      return nextStageId;
   },queryWFDetails : function(ifcae,scrid) {
      var userId = this.apz.userId;
      var screenID = this.apz.currScr;
      var iFaceId = "appzillonWorkflowQueryDb";
      var appId = this.apz.appId;
      var body = {};
      var request = {};
      body.appzillonWorkflowQueryDbRequest = {};
      body.appzillonWorkflowQueryDbRequest.userId = userId;
      body.appzillonWorkflowQueryDbRequest.iFaceId = iFaceId;
      body.appzillonWorkflowQueryDbRequest.appId = appId;
       var reqObj = {};
     reqObj.origIfaceName = ifcae;
       reqObj.ifaceName = "appzillonWorkflowQueryDb";
       reqObj.callBackObj = this;
       reqObj.callBack =this.queryWFDetailsCB;
       reqObj.req = body;//request;//scrreq;
      reqObj.scrName=screenID;
       reqObj.async = false;
       this.apz.server.callServer(reqObj);
   },
    queryWFDetailsCB : function(param){
         param.ifaceName = param.origIfaceName;
         param.resFull.appzillonHeader.interfaceId="mytask_Res";
         var body ={};
        body.dashboard ={};
        body.dashboard =param.resFull.appzillonBody["appzillonWorkflowQueryDbResponse"];
        param.res = param.resFull.appzillonBody=body;
        this.apz.server.correctRes(param);
        this.apz.server.updateResponse(param);
        this.apz.data.loadData("mytask");
   }, launchDashboardWF : function(contId, pobj) {
      var screenData = "";
      var params = {};
      var tableRowObj = {};
      if(this.apz.scrMetaData.containersMap[contId].type == "TABLE"){
        var tableId = contId + "_table";
        var noOfRows = $("#"+tableId)[0].rows.length;        
        for (var i = 1; i < noOfRows; i++) {
           var selected = $('#'+tableId)[0].rows[i].cells[0].childNodes[0].checked;
           var index = this.apz.data.getDataRec(contId, i);
           if (selected) {
               ltableRowObj = $('#'+tableId)[0].rows[index];
               params = this.getDashboardWFProp(ltableRowObj,{});
           }
        }
      } else if(this.apz.scrMetaData.containersMap[contId].type == "LIST" && !this.apz.isNull(pobj)){
         params = this.getDashboardWFProp(pobj,{});
      }
      if(!this.apz.isNull(params.nextScrId)){
        this.apz.launchScreen(params.nextScrId); 
      }
   }, getDashboardWFProp :function(pobj,params){
         $(pobj).find('p').each(function(){
            if($(this).attr('id').indexOf('dashboard__workflowId')>0){
              params.workFlowId = $(this).text();
            }else if($(this).attr('id').indexOf('dashboard__nextStageId')>0){
              params.id = $(this).text();
            }else if($(this).attr('id').indexOf('dashboard__wfTxnRef')>0){
              params.wfTxnRef = $(this).text();
            }else if($(this).attr('id').indexOf('dashboard__screenData')>0){
              params.screenData = $(this).text();
            }else if($(this).attr('id').indexOf('dashboard__interfaceId')>0){
              params.interfaceId = $(this).text();
            }
        });
        params.nextScrId = this.getScreenID(params);
        this.isWorkFlow = "Y";
        this.wfTxnRef = params.wfTxnRef;
        if(!this.apz.isNull(params.screenData) && !this.apz.isObjectEmpty(params.screenData)){
          this.apz.data.scrdata[params.interfaceId] = JSON.parse(params.screenData);
        }
        return params;
   }, launchDashboardWFStage : function(wfObj) {
      this.isWorkFlow = "Y";
      var iFaceid = "appzillonWorkflowDashboardQuery";
      var nextScrId = this.getScreenID(wfObj.workFlowID, wfObj.nextStageId);
      var nextSeqNo = this.getSeqNo(wfObj.workFlowID, wfObj.nextStageId);
      var appId = this.apz.appId;
      this.setCurrWorkflowDetails(wfObj.workFlowID, wfObj.nextStageId, nextScrId, nextSeqNo);
      var body = {};
      var request = {};
      body.appzillonWorkFlowDashboardQueryRequest = {};
      body.appzillonWorkFlowDashboardQueryRequest.wfTxnRef = wfObj.wfTxnRef;
      body.appzillonWorkFlowDashboardQueryRequest.workFlowId = wfObj.workFlowID;
      body.appzillonWorkFlowDashboardQueryRequest.stageId = wfObj.nextStageId;
      body.appzillonWorkFlowDashboardQueryRequest.screenId = wfObj.screenId;
      body.appzillonWorkFlowDashboardQueryRequest.seqNo = wfObj.seqNo;
      body.appzillonWorkFlowDashboardQueryRequest.userId = wfObj.userId;
      body.appzillonWorkFlowDashboardQueryRequest.iFaceId = "appzillonWorkflowPersist";
      body.appzillonWorkFlowDashboardQueryRequest.appId = appId;
      var lheader = this.apz.Server.getHeader(iFaceid, wfObj.screenId);
      request.appzillonHeader = lheader;
      request.appzillonBody = body;
      var scrReq = JSON.stringify(request);
      var reqObj = {};
       reqObj.ifaceName = iFaceid;
       reqObj.callBack ="";
       reqObj.req = scrReq;
       reqObj.async = false;
       this.apz.Server.callServer(reqObj);
      var jsonRespObj = this.apz.scrResp;
      jsonResp = jsonRespObj.appzillonWorkFlowDashboardQueryResponse;
      this.scrData = JSON.stringify(jsonResp);
      this.launchScreen(nextScrId);
   },
   launchWFStage : function(wfObj) {
      this.isWorkFlow = "Y";
      var screenId = this.getScreenID(wfObj.workFlowID, wfObj.stageID);
      var interfaceid = "appzillonWorkflowQuery";
      var userId = this.apz.userId;
      var appId = this.apz.appId;
      var seqNo = this.getSeqNo(wfObj.workFlowID, wfObj.stageID);
      this.setCurrWorkflowDetails(wfObj.workFlowID, wfObj.stageID, screenId, seqNo);
      var body = {};
      var lrequest = {};
      body.appzillonWorkFlowPersist = {};
      body.appzillonWorkflowQueryRequest.workflowId = wfObj.workFlowID;
      body.appzillonWorkflowQueryRequest.stageId = wfObj.stageID;
      body.appzillonWorkflowQueryRequest.screenId = screenId;
      body.appzillonWorkflowQueryRequest.seqNo = seqNo.toString();
      body.appzillonWorkflowQueryRequest.userId = userId;
      body.appzillonWorkflowQueryRequest.interfaceId = interfaceid;
      body.appzillonWorkflowQueryRequest.appId = appId;
      var lheader = this.apz.Server.getHeader(interfaceid, screenId);
      lrequest.appzillonHeader = lheader;
      lrequest.appzillonBody = body;
      var scrreq = JSON.stringify(lrequest);
      var reqObj = {};
       reqObj.ifaceName = "appzillonWorkflowQuery";
       reqObj.callBack ="";
       reqObj.req = scrReq;
       reqObj.async = false;
       this.apz.Server.callServer(reqObj);
      var jsonrespobj = this.apz.scrResp;
      var jsonresp = jsonrespobj.appzillonBody.appzillonWorkflowQueryResponse;
      jsonresp = jsonresp.screenData;
      var nextStageId = jsonrespobj.appzillonBody.appzillonWorkflowQueryResponse.nextStageId;
      var currScrID = this.getScreenID(workflowID, nextStageId);
      this.apz.data.scrdata = JSON.stringify(jsonresp);
      this.apz.launchScreen(currScrID);
   },
   loadWorkflowData : function() {
      var workFlow = this.isWorkFlow;
      if (workFlow == "Y") {
         this.isWorkFlow = "N";
         this.apz.scrResp = JSON.parse(this.scrData);
         var lresp = {};
         lresp.res = this.apz.scrResp;
         this.apz.Server.updateResponse(lresp);
      }
   },
   persistWFStage : function(action) {
      this.apz.data.buildData();
      var status;
      var iFaceId = "appzillonWorkflowPersist";
      var nextStageId = this.getNextStageID(action);
      var userId = this.apz.userId;
      var wfTxnRef = this.wfTxnRef; //APZDOUBT
      if ((wfTxnRef == "undefined") || (wfTxnRef == undefined)) {
         wfTxnRef = "";
      }
      var appId = this.apz.appId;
      var body = {};
      var request = {};
	  var liface =this.scrIface;
	  
	 // appzillonWorkflowPersistRequest
	  //appzillonWorkflowPersistRequest
      body.appzillonWorkflowPersistRequest = {};
      body.appzillonWorkflowPersistRequest.workflowId = this.currWorkFlowId;
      body.appzillonWorkflowPersistRequest.stageId = this.currStageId;
      body.appzillonWorkflowPersistRequest.screenId = this.currScreenId;
      body.appzillonWorkflowPersistRequest.seqNo = this.currSeqNo.toString();
      body.appzillonWorkflowPersistRequest.action = action;
      body.appzillonWorkflowPersistRequest.nextStageId = nextStageId;
      body.appzillonWorkflowPersistRequest.userId = userId;
      body.appzillonWorkflowPersistRequest.interfaceId = liface;
      body.appzillonWorkflowPersistRequest.appId = appId;
      body.appzillonWorkflowPersistRequest.wfTxnRef = wfTxnRef;
      body.appzillonWorkflowPersistRequest.screenData = JSON.stringify(this.apz.data.scrdata[liface]);
	    request = body;      
      var reqObj = {};
       reqObj.ifaceName = iFaceId;
       reqObj.callBack = this.persistWFStageCB;
       reqObj.callBackObj = this;
       reqObj.req = request;
       reqObj.async = false;
       this.apz.server.callServer(reqObj);
       status = this.apz.scrResp.appzillonWorkFlowPersistResponse.status;
      if (status == "success" && action == "update") {
         this.wfTxnRef = "";
      } else {
         this.wfTxnRef = "";
      }
      return status;
   }, persistWFStageCB : function(resObj){
      var lobj = {};
      if(resObj.status == true){
          this.apz.scrResp = resObj.res;
          lobj.code = 'APZ-WF-001';
          this.apz.dispMsg(lobj);
      }else{
        lobj.code = 'APZ-WF-002';
        this.apz.dispMsg(lobj);
      }
   }, updateWFStage : function(action) {
      this.apz.buildData(null);
      var iFaceId = "appzillonWorkflowPersist";
      var nextStageID = this.getNextStageID(action);
      var userId = this.apz.userId;
      var wfTxnRef = this.wfTxnRef;
      if ((wfTxnRef == "undefined") || (wfTxnRef == undefined)) {
         wfTxnRef = "";
      }
      var lastStage = "N";
      if (this.getCurrSeqNo() == this.workflowArray[this.currWorkFlowId].length - 1)
         lastStage = "Y";
      var appId = this.apz.appId;
      var body = {};
      var request = {};
      body.appzillonUpdateTaskRequest = {};
      var screendata = JSON.stringify(this.apz.scrdata);
      body.appzillonUpdateTaskRequest.lastStage = lastStage;
      body.appzillonUpdateTaskRequest.workflowId = this.currWorkFlowId;
      body.appzillonUpdateTaskRequest.stageId = this.currStageId;
      body.appzillonUpdateTaskRequest.screenId = this.currScreenId;
      body.appzillonUpdateTaskRequest.seqNo = this.currSeqNo.toString();
      body.appzillonUpdateTaskRequest.action = action;
      body.appzillonUpdateTaskRequest.nextStageId = nextStageId;
      body.appzillonUpdateTaskRequest.userId = userId;
      body.appzillonUpdateTaskRequest.interfaceId = iFaceId;
      body.appzillonUpdateTaskRequest.appId = appId;
      body.appzillonUpdateTaskRequest.txnRefNum = wfTxnRef;
      body.appzillonUpdateTaskRequest.version = this.currSeqNo.toString();
      body.appzillonUpdateTaskRequest.screenData = screendata;
      var lheader = this.apz.Server.getHeader(iFaceId, this.currScreenId);
      request.appzillonHeader = lheader;
      request.appzillonHeader.interfaceId = "updateTaskRepair";
      request.appzillonBody = body;
      var scrReq = JSON.stringify(request);
      var reqObj = {};
          reqObj.ifaceName = iFaceId;
          reqObj.callBack ="";
          reqObj.req = scrReq;
          reqObj.async = false;
          this.apz.Server.callServer(reqObj);
      var jsonRespObj = this.apz.scrResp;
      var status = jsonRespObj.appzillonUpdateTaskResponse.status;
      if (status == "success") {
         this.wfTxnRef = "";
      }
      return Status;
   }
}

