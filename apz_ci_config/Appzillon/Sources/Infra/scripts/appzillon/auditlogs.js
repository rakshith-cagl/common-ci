Apz.Audit = function(apz) {
   ////Core Instance
   this.apz = apz;
   this.elmSep = "~";
   this.recSep = "!";
   this.audit = [];
};
Apz.Audit.prototype = {
   auditLog : function(audit) {
	   /* Params contains the below attributes
	       *** startTimeStamp, endTimeStamp, field1, field2, field3,  field4, field5 ***
	   */
      if (this.apz.auditLogReqd) {
         var log = {};
		 var endTimeStamp = this.apz.getCurrTimeStamp();
         log.appId = this.apz.appId;
         log.sessionId = this.apz.sessionId;
         log.deviceId = this.apz.deviceId;
         log.userId = this.apz.userId;
         log.action = audit.action;
         log.startTimeStamp = audit.startTimeStamp;
		 if(!this.apz.isNull(audit.endTimeStamp)){
			 endTimeStamp = audit.endTimeStamp;
		 }
         log.endTimeStamp = endTimeStamp;
         log.field1 = audit.field1;
         log.field2 = audit.field2;
         log.field3 = audit.field3;
         log.field4 = audit.field4;
         log.field5 = audit.field5;
         this.append(log);
      }
   }, append : function(audit) {
      this.audit[this.audit.length] = audit;
   }, persistAudit : function() {
      var body = {};
      body.appzillonAuditLogRequest = this.audit;
      ////Call Server
      var params = {};
      params.id = "AUDITLOG";
      params.ifaceName = "appzillonAuditLog";
      params.req = body;
      params.callBackObj = this;
      params.callBack = this.persistAuditCB;
      params.internal = true;
      this.apz.server.sendReq(params);
   }, persistAuditCB : function(params) {
      ////Handle Failure here..
      if (params.status) {
         this.audit = [];
      } else {
         ///Send Failed..
      }
   },clearAuditDetails : function(){
	   var params = {};
	   params.action = 'LOGIN';	
	   params.startTimeStamp = this.apz.retrieve('LOGIN');
       params.endTimeStamp = this.apz.getCurrTimeStamp();
	   params.field1 = "appzillonLogoutRequest";
	   params.field2 = '';
	   params.field3 = '';
	   params.field4 = '';
	   params.field5 = '';
	   this.auditLog(params);
	   this.apz.store('LOGIN', "");
	   this.persistAudit();
   },sendLog : function(params){
	   var req = {};
	   req.appzillonErrorLoggingRequest = {};
	   req.appzillonErrorLoggingRequest.error = params.msg;
	   params.ifaceName = 'appzillonErrorLogging';
	   params.req = req;
	   params.internal = true;
	   this.apz.server.sendReq(params);
   }
}
