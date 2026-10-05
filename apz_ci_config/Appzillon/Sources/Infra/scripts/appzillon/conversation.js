Apz.ConvUI = function(apz) {
    this.apz = apz;
    this.cnvUIId = null;
    this.dlgId = null;
    this.txnRef = null;
    this.count = 0;
    this.dialogsMap = {};
};
Apz.ConvUI.prototype = {
    getCnvUIWelcomeMsg : function(params) {
        /* Params contains the below attributes
         *** callBackObj,callBack ***
         * Response contains below attributes
         *** res, errCode ***
         */
        params.ifaceName = "getCnvUIWelcomeMsg";
        params.internal = true;
        params.userCallBackObj = params.callBackObj;
        params.userCallBack = params.callBack;
        params.callBack = this.getCnvUIWelcomeMsgCB;
        params.callBackObj = this;
        params.req = {};
        params.req.getCnvUIWelcomeMsgRequest = {};
        params.req.getCnvUIWelcomeMsgRequest.appId = this.apz.appId;
        this.apz.server.sendReq(params);
    },
    getCnvUIWelcomeMsgCB : function(params) {
        if (!this.apz.isNull(params.msgElmId)) {
            //Replaced the tokens in the object so that the Dev will get the updated message
            params.res.getCnvUIWelcomeMsgResponse.message = this.replaceToken(params.res.getCnvUIWelcomeMsgResponse.message);
            this.apz.setElmValue(params.msgElmId, params.res.getCnvUIWelcomeMsgResponse.message);
        }
        if (!this.apz.isNull(params.convUiId)) {
            this.populateConversations(document.getElementById(params.convUiId));
        }
        if (this.apz.isFunction(params.userCallBack)) {
            if (params.userCallBackObj) {
                params.userCallBack.call(params.userCallBackObj, params);
            } else {
                params.userCallBack(params);
            }
        }
    },
    populateConversations : function(eobj) {
        /* Params contains the below value
         *** obj ***
         */
        var options = [];
        for (var k = 0, len = this.apz.convMap[this.apz.appId].convids.length; k < len; k++) {
            var obj = {};
            obj.val = this.apz.convMap[this.apz.appId].convids[k];
            obj.desc = this.apz.convMap[this.apz.appId].convdescs[k];
            if(obj.val== "PleaseSelect"){
                options.unshift(obj);
            } else {
                options.push(obj);
            }
        }
        this.apz.populateDropdown(eobj, options);
    },
    genCnvUIDlg : function(params) {
        /* Params contains the below attributes
         *** div,cnvUIId,dlgId,txnRef,appId,callBackObj,callBack ***
         * Response contains below attributes
         *** res, errCode ***
         */
        params.req = {};
        if (this.apz.isNull(params.dlgId)) {
            params.ifaceName = "getFirstCnvUIDlg";
            params.req.getFirstCnvUIDlgRequest = {};
            params.req.getFirstCnvUIDlgRequest.appId = params.appId || this.apz.appId;
            params.req.getFirstCnvUIDlgRequest.Id = params.cnvUIId;
            this.loadCnvUIDetails(params);
        } else {
            params.ifaceName = "getCnvUIDlg";
            params.req.getCnvUIDlgRequest = {};
            params.req.getCnvUIDlgRequest.appId = params.appId || this.apz.appId;
            params.req.getCnvUIDlgRequest.Id = params.cnvUIId || this.cnvUIId;
            params.req.getCnvUIDlgRequest.dlgId = params.dlgId || this.dlgId;
            params.req.getCnvUIDlgRequest.txnRef = params.txnRef || this.txnRef;
            this.apz.data.buildData();
            params.req.getCnvUIDlgRequest.screenData = this.apz.data.scrdata;
        }
        params.internal = true;
        params.async = true;
        params.userCallBackObj = params.callBackObj;
        params.userCallBack = params.callBack;
        params.callBack = this.genCnvUIDlgCB;
        params.callBackObj = this;
        this.apz.server.sendReq(params);
    },
    genCnvUIDlgCB : function(params) {
        if (params.res.getFirstCnvUIDlgResponse) {
            this.cnvUIId = params.res.getFirstCnvUIDlgResponse.Id;
            this.dlgId = params.res.getFirstCnvUIDlgResponse.dlgId;
            this.txnRef = params.res.getFirstCnvUIDlgResponse.txnRef;
            params.scrDets = params.res.getFirstCnvUIDlgResponse.screenDetails;
        } else if (params.res.getCnvUIDlgResponse) {
            this.dlgId = params.res.getCnvUIDlgResponse.dlgId;
            params.scrDets = params.res.getCnvUIDlgResponse.screenDetails;
        }
        var parent = document.getElementById(params.div);
        if (this.apz.isNull(parent)) {
            console.log("Append subscreen failed due to invalid parent id");
        } else {
            params.scrDets.screenDef = JSON.parse(unescape(params.scrDets.screenDef));
            if(params.dlgId){
                this.dialogsMap[this.dlgId] = params.scrDets.screenDef.scr;
             }
            if (params.scrDets.screenDef.ifaces && params.scrDets.screenDef.ifaces.length > 0) {
                params.ifaces = apz.copyJSONObject(params.scrDets.screenDef.ifaces);
                this.getIntfDef(params);
            }else{
            	this.appendDialog(params);
            }
        }
    },
    loadCnvUIDetails : function(params) {
        var convIfaces = this.apz.convMap[this.apz.appId].convinterfaces[params.cnvUIId];
        if (!this.apz.isNull(convIfaces)) {
            var cnvIntfs = {};
            cnvIntfs.ifaces = convIfaces;
            cnvIntfs.appId = params.appId || this.apz.appId;
            this.getIntfDef(cnvIntfs);
        }
        var convScripts = this.apz.convMap[this.apz.appId].convscripts[params.CNVUIId];
        if (!this.apz.isNull(convScripts)) {
            this.apz.addScriptsPath(this.apz.appId, convScripts);
            requirejs(convScripts, function() {
                //For future needs
            });
        }
    },
    processNLPData : function(params) {
        /* Params contains the below attributes
         *** div,dlgId,scrData,callBackObj,callBack ***
         * Response contains below attributes
         *** res, errCode ***
         */
        params.ifaceName = "processNLPData";
        params.internal = true;
        params.req = {};
        params.req.processNLPDataRequest = {};
        params.req.processNLPDataRequest.dlgId = params.dlgId || this.dlgId;
        params.req.processNLPDataRequest.cnvUIId = params.cnvUIId || this.cnvUIId;
        params.req.processNLPDataRequest.screenData = params.scrData;
        this.apz.server.sendReq(params);
    },
/**** Will be uncommented once the response structure is decided ****/
/*    processNLPDataCB: function(params) {
        if (!this.apz.isNull(params.res.processNLPDataResponse) && params.paintResp === "Y") {
            var previousCov = $("#conversation__" + this.count + "__div");
            var nextDlgId = ++this.count;
            var nlpDialogId = 'conversation__' + nextDlgId + '__div';
            var dlgCls = (nextDlgId % 2) ? "rgt" : "lgt";
            var content = '<div id="' + nlpDialogId + '__wrapper" class="msg ' + dlgCls + '"><div id="' + div + '" class = "msg-content">';
            /// TBC - Temporary structure to display bean information
            var NLP_Resp = params.res.processNLPDataResponse;
            for (var key in NLP_Resp) {
                if (NLP_Resp.hasOwnProperty(key)) {
                    content = content + '<ul ><li >' + key + '</li ><li>' + NLP_Resp[key] + '</li></ul>'
                }
            }
            content = content +
                '<span class="ecn etw-50"><span class="etw-100 btr pri"><span><input class="ett-inpt pri etw-100" type="text" placeholder="Editable Input" onblur="apz.convui.nlpAction();";></span><span class="etw-5"><button class="ett-bttn pri med">Go</button></span></span></span></div></div>'
            previousCov.after(content);
            var offsetInf = $("#" + nlpDialogId).offset();
            window.scrollTo(offsetInf.left, offsetInf.top);
        }
        if (this.apz.isFunction(params.userCallBack)) {
            if (params.userCallBackObj) {
                params.userCallBack.call(params.userCallBackObj, params);
            } else {
                params.userCallBack(params);
            }
        }
    },*/
    getIntfDef : function(params) {
        var appId = params.appId || this.apz.appId;
        for (var k = 0; k < params.ifaces.length; k++) {
            if (this.apz.containsKey(this.apz.ifacesMap[appId], params.ifaces[k])) {
                params.ifaces.splice(k, 1);
            }
        }
        if (params.ifaces.length > 0) {
            params.ifaceName = "appzillonGetIntfDef";
            params.id = this.apz.getProcId();
            params.internal = true;
            params.async = true;
            params.callBack = this.getIntfDefCB;
            params.callBackObj = this;
            params.req = {};
            params.req.appzillonGetIntfDefRequest = {};
            params.req.appzillonGetIntfDefRequest.appId = appId;
            params.req.appzillonGetIntfDefRequest.interfaceId = params.ifaces;
            this.apz.server.sendReq(params);
        }else if(!this.apz.isNull(params.scrDets)){
        	this.appendDialog(params);
        }
    },
    getIntfDefCB : function(params) {
        if (!this.apz.isNull(params.res.appzillonGetIntfDefResponse)) {
            var intfs = params.res.appzillonGetIntfDefResponse;
            for (var k = 0, len = intfs.length; k < len; k++) {
                var obj = {};
                obj.appId = params.appId || this.apz.appId;
                obj.content = unescape(intfs[k].interfaceDef);
                this.apz.loadIfaceDef(obj);
            }
        }
        if(!this.apz.isNull(params.scrDets)){
        	this.appendDialog(params);
        }
    },
    nlpAction : function() {
        /// TBC - calling dummy function of app
        this.apz.app.dummyfunction();
    },
    replaceToken : function(key) {
        /* Params contains the below value
         *** key ***
         * Response contains below value
         *** key ***
         */
        if (!this.apz.isNull(key)) {
            key = key.replace("${APPID}", this.apz.appId).replace("${USERNAME}", this.apz.userName).replace("${FIRSTNAME}", this.apz.userFirstName).replace(
                "${LASTNAME}", this.apz.userLastName);
        }
        return key;
    }, clearDailog : function(params){
    	/* Params contains the below value
         *** appId,scr,dialog ***
        */
        var appid = params.appId || this.apz.appId;
        var scr = params.scr;
         if(this.apz.isNull(scr)) {
             scr = this.dialogsMap[params.dialog]
         }
         /* clearing the data */
         var layout = this.apz.getLayout({scr : scr});
         var design = this.apz.getDesigns({appId:appid,scr:scr,layout:layout}).currentDesign;
         var scrMap = this.apz.scrDefsMap[appid][scr+"__"+layout+"__"+design];
         for(var i=0;i<scrMap.containers.length;i++) {
             this.apz.data.clearContainerData(scrMap.containers[i].id);
         };
         apz.data.buildData();
         var scrId = $("#scr__"+ appid +"__"+ scr +"__main");
         scrId.closest(".msg").remove();
     }, appendHTML : function(params){
    	 /* Params contains the below attributes
          *** div,html,appId ***
         */
    	 var div = "conversation__" + (++this.count);
         var dlgCls = (this.count % 2) ? "rgt" : "lgt";
         $("#" + params.div).append('<div id="' + div + '__wrapper" class = "msg ' + dlgCls + '"><div id="' + div + '" class = "msg-content"></div></div>');
         $("#" + div).append(params.html);
     }, appendDialog : function(params){
         var authStatus = true;
         var key = '';
    	 var appId = params.appId || this.apz.appId; 
    	 var scrDef = params.scrDets.screenDef;
    	 var div = "conversation__" + (++this.count);
         var dlgCls = (this.count % 2) ? "rgt" : "lgt";
         $("#" + params.div).append('<div id="' + div + '__wrapper" class = "msg ' + dlgCls + '"><div id="' + div + '" class = "msg-content"></div></div>');
         $("#" + div).removeClass("sno");
         ////Load ScrHTML
         key = scrDef.scr + "_" + scrDef.layout + "_" + scrDef.id + "_" + this.apz.language + ".html";
         this.apz.scrHtmls[appId][key] = unescape(params.scrDets.screenHtml);
         var proc = {};
         proc.appId = appId
         proc.animation = params.animation ? params.animation : 0;
         proc.scroll = params.scroll;
         proc.scrDef = JSON.stringify(scrDef);
         proc.scr = scrDef.scr;
         proc.lo = scrDef.layout;
         proc.template = scrDef.id;
         proc.loadScrDef = true;
         proc.type = "CF";
         proc.newDiv = div;
         proc.origDiv = proc.newDiv;
         var div1 = proc.newDiv + "_apz_1";
         var div2 = proc.newDiv + "_apz_2";
         var html = this.apz.getHtml(proc.newDiv);
         var divObj = document.getElementById(proc.newDiv);
         var div1Obj = document.createElement("div");
         div1Obj.setAttribute("id", div1);
         var div2Obj = document.createElement("div");
         div2Obj.setAttribute("id", div2);
         this.apz.clearHtml(proc.newDiv);
         divObj.appendChild(div1Obj);
         divObj.appendChild(div2Obj);
         $("#" + div1).addClass("lcol12");
         $("#" + div2).addClass("lcol12 sno ssp");
         this.apz.setHtml(div1, html);
         proc.oldDiv = div1;
         proc.newDiv = div2;
         this.apz.ifacesLoaded(proc);
       if (this.apz.isFunction(params.userCallBack)) {
             if (params.userCallBackObj) {
                 params.userCallBack.call(params.userCallBackObj, params);
             } else {
                 params.userCallBack(params);
             }
         }
     }
};
