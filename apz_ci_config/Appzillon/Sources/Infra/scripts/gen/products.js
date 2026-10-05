Apz.Products = function (apz) {
    this.apz = apz;
    this.Region = "";
    this.Pname = "";
    this.RPID = "";
    this.PrdID = "";
    this.PPID = "";
    this.PGroup = "";
    this.RootQuoteId = "";
    this.HeaderId = "";
    this.currentProductId = "";
    this.baseProductId = "";
    this.productsMap = {};
    this.IsSubProduct = false;
    this.SeqNum = 1;
    this.NumOfRecToDisp = 100;
    this.action = "";
    this.CitiSCQuComplFlag = "N";
    this.CitiImplementationQuComplFlag = "N";
    this.CitiMandatoryQuComplFlag = "N";
    this.CitiPercentageComplete = "";
    this.CitiDDQPercentage = "";
    this.urlConfigMap = {};
    this.Save = "SAVE";
    this.Submit = "SUBMIT";
    this.sDataMap = {};
    this.Id = "";
    this.idMapper = {};
    this.seqMapper = {};
    this.idMapper1 = {};
    this.seqMapper1 = {};
    this.RootSeq = "";
    this.QuoteResponse = {};
    this.QuoteIdBackUpMap = {};
    this.RootPrdShortName = "";
    this.accessToken = "";
    /// Added for dealId to be passed in callAPI function, initialized by developer 26 march 2020
    this.dealId = "";
    this.containerOptionsMap = {};
    /* declaration of new tags for copy : investors */
    this.CitiCopiedProduct = "N";
    this.CitiCopiedDDQ = "N";
    /* ends here */
    /*Mapping for upload with attachment : investors*/
    this.uploadMapperArray = {};
    this.showColArray = {};
    this.autocompleteRespArr = {};
    /* ends here*/
    //Reason for showing/hiding attributes
    this.displayRuleMapper = {};
    this.gDulipcateSubProductData = [];
    this.mainKey = "";
    this.immediateChildKey = "";
};
//Declaring global variables
Apz.Products.prototype = {
    getProductJson: function (params) {
        /* Params contains the below attributes
         *** cnvUIId,dlgId,txnRef,appId,callBackObj,callBack ***
         * Response contains below attributes
         *** res, errCode ***
         */
        params.req = {};
        params.ifaceName = "apzParseMetaJSON";
        params.req.appId = params.appId || this.apz.currAppId;
        params.req.scrId = params.scrId;
        params.req.interfaceId = params.interfaceId;
        params.internal = true;
        params.async = true;
        params.callBack = this.getProductJsonCB;
        params.callBackObj = this;
        this.apz.server.sendReq(params);
    },
    getProductJsonCB: function (params) {
        if (!this.apz.isNull(params.res.screenDef)) {
            params.res.screenDef = JSON.parse(params.res.screenDef);
            if (params.res.screenDef.ifaces && params.res.screenDef.ifaces.length > 0 && !params.res.ifaceDef) {
                params.ifaces = apz.copyJSONObject(params.res.screenDef.ifaces);
                this.getIntfDef(params);
            } else {
                if (params.res.screenDef.ifaces.length > 1) {
                    this.getIntfDef(params);
                } else {
                    var obj = {};
                    obj.appId = params.appId || this.apz.currAppId;
                    obj.content = params.res.ifaceDef;
                    this.apz.loadIfaceDef(obj);
                }
                this.appendProduct(params);
                if (this.apz.isFunction(params.userCallBack)) {
                    if (params.userCallBack) {
                        params.userCallBack.call(params.userCallBackObj, params);
                    } else {
                        params.userCallBack(params);
                    }
                }
            }
        } else {
            apz.stopLoader();
            console.log("Response Not found for the Screen:" + params.scrId);
        }
    },
    appendProduct: function (params) {
        var appId = params.appId || this.apz.currAppId;
        var key = '',
            scrdefsKey = '',
            lodefsKey = '';
        params.layout = this.apz.getLayout({ "appId": params.appId, "scr": params.scrId });
        lodefsKey = params.scrId + "__" + params.layout;
        this.apz.loDefsMap[appId][lodefsKey] = JSON.parse(params.res.layoutDef);
        var currentDesign = this.apz.getDesigns({ "appId": appId, "scr": params.scrId, "layout": params.layout }).currentDesign;
        params.design = currentDesign;
        var insertionRequired = false;
        var authStatus = true;
        var scrDef = params.res.screenDef;
        var div = params.div;
        //$("#" + div).removeClass("sno");
        key = params.scrId + "_" + params.layout + "_" + currentDesign + "_" + this.apz.language + ".html";
        scrdefsKey = params.scrId + "__" + params.layout + "__" + currentDesign;
        params.res.ifaceDef = JSON.parse(params.res.ifaceDef);
        if (!this.apz.isNull(params.res.screenHtml)) {
            this.apz.scrDefsMap[appId][scrdefsKey] = params.res.screenDef;
            this.apz.ifacesMap[appId][params.res.ifaceDef.name] = params.res.ifaceDef;
        }
        if (this.apz.isNull(params.res.screenHtml)) {
            var proc = {};
            proc.appId = appId;
            proc.animation = params.animation ? params.animation : 0;
            proc.scroll = params.scroll;
            proc.scrDef = JSON.stringify(scrDef);
            proc.scr = scrDef.scr;
            proc.lo = params.layout;
            proc.template = scrDef.id;
            proc.loadScrDef = true;
            proc.type = params.procType;
            proc.content = JSON.stringify(scrDef);
            this.apz.loadScrDef(proc);
            this.genHtml = new Apz.GenHTML(apz);
            var scrDefTemp = params.res.screenDef;
            try {
                var screenDesignTemp = JSON.parse(params.res.screenDesign);
            } catch (e) {
                console.log(e.stack);
            }
            params.res.screenHtml = this.genHtml.genPortion({
                designDef: screenDesignTemp,
                appId: appId,
                scr: scrDefTemp.scr,
                layout: params.layout,
                design: scrDefTemp.id
            });
            insertionRequired = true;
        }
        this.apz.scrHtmls[appId][key] = params.res.screenHtml;
        if (params.processHtml) {
            this.addDefaultShownHook(params.scrId, params.interfaceId);
            var proc = {};
            proc.appId = appId;
            proc.animation = params.animation ? params.animation : 0;
            proc.scroll = params.scroll;
            proc.scrDef = JSON.stringify(scrDef);
            proc.scr = params.scrId;
            proc.lo = params.layout;
            proc.template = currentDesign;
            proc.loadScrDef = false;
            proc.type = params.procType;
            proc.newDiv = params.div;
            proc.origDiv = params.div;
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
            $("#" + div1).addClass("lcol12 sno ssp");
            $("#" + div2).addClass("lcol12");
            this.apz.setHtml(div1, html);
            proc.oldDiv = (params.procType != "PG") ? div2 : params.origDiv;
            proc.newDiv = (params.procType != "PG") ? div1 : proc.newDiv;
            if (!proc.userObj) {
                proc.userObj = { launchDefaultProduct: params.launchDefaultProduct };
            } else {
                proc.userObj.launchDefaultProduct = params.launchDefaultProduct;
            }
            this.apz.loadScrContent(proc);
            this.currentProductId = params.productId;
            var productList = this.getProductHierarchyTree(apz.currAppId, params.productId);
            var tempProductList = this.productsMap;
            for (var i = 0; i < productList.length; i++) {
                tempProductList = tempProductList[productList[i]];
            }
            tempProductList[params.productId] = {};
            var tempScrDefMap = this.apz.scrDefsMap[appId][scrdefsKey];
            /* Moved generation of internal content of default product to appzillon.js to ensure the synchronous behaviour*/
            if (this.apz.isNull(params.launchDefaultProduct) || params.launchDefaultProduct) {
                if (!this.apz.isNull(tempScrDefMap.defaultProducts)) {
                    for (var i = 0; i < tempScrDefMap.defaultProducts.length; i++) {
                        var param = {};
                        param.div = tempScrDefMap.defaultProducts[i] + "_Scr_div";
                        if (this.apz.isNull($("#" + param.div)[0])) {
                            var div = document.createElement("div");
                            div.setAttribute("id", param.div);
                            div.setAttribute("class", "productRoot");
                            if ($("." + tempScrDefMap.defaultProducts[i] + "RelCntr").length > 0) {
                                $("." + tempScrDefMap.defaultProducts[i] + "RelCntr").after(div);
                            } else {
                                $("#page-body").append(div);
                            }
                            /*  var subProdId = tempScrDefMap.defaultProducts[i];
                             param.newDiv = param.div;
                             param.origDiv = param.div;
                             param.processHtml = true;
                             param.loaderRequired = true;
                             param.procType = "CF";
                             param.productId = subProdId;
                             param.launchDefaultProduct = params.launchDefaultProduct;
                             this.getScreenContent(param); */
                        }
                    }
                }
            }
            if (tempScrDefMap.subProducts) {
                for (var i = 0; i < tempScrDefMap.subProducts.length; i++) {
                    var subProdId = tempScrDefMap.subProducts[i];
                    if (tempScrDefMap.defaultProducts.indexOf(subProdId) == -1) {
                        var param = {};
                        var subProdId = tempScrDefMap.subProducts[i];
                        param.productId = subProdId;
                        param.processHtml = false;
                        param.loaderRequired = false;
                        param.div = params.div;
                        this.getScreenContent(param);
                    }
                }
            }
        }
        if (apz.products.baseProductId == params.productId) {
            var baseProdDecoration = apz.scrDefsMap[params.appId][scrdefsKey].decorations;
            if (!apz.isNull(baseProdDecoration)) {
                for (var i = 0; i < baseProdDecoration.length; i++) {
                    if (baseProdDecoration[i].Name.split("_")[2].trim() == "Pname") {
                        if (baseProdDecoration[i].Value.indexOf("=") >= 0) {
                            apz.products.RootPrdShortName = baseProdDecoration[i].Value.split("=").pop().trim();
                        } else {
                            apz.products.RootPrdShortName = baseProdDecoration[i].Value.trim();
                        }
                    }
                }
            }
        }
        if (this.apz.isFunction(params.userCallBack)) {
            if (params.userCallBackObj) {
                params.userCallBack.call(params.userCallBackObj, params);
            } else {
                params.userCallBack(params);
            }
        }
        if (insertionRequired) {
            var ifaceDefKey = appId + "__" + params.interfaceId + "_" + "Intf";
            var reqAfterChange = {};
            reqAfterChange.ifaceName = "apzPersistHTMLInfo";
            reqAfterChange.internal = true;
            reqAfterChange.async = true;
            reqAfterChange.userCallBackObj = "";
            reqAfterChange.userCallBack = "";
            reqAfterChange.req = {};
            reqAfterChange.req.screenHtml = params.res.screenHtml;
            reqAfterChange.req.scrId = params.scrId;
            reqAfterChange.req.screenDef = JSON.stringify(this.apz.scrDefsMap[appId][scrdefsKey]);
            reqAfterChange.req.ifaceDef = JSON.stringify(this.apz.ifacesMap[appId][ifaceDefKey]);
            reqAfterChange.req.layoutDef = params.res.layoutDef;
            reqAfterChange.appId = appId;
            this.apz.server.sendReq(reqAfterChange);
        }
        this.processRootAPI({ "screenDets": scrdefsKey });
    },
    getIntfDef: function (params) {
        var appId = params.appId || this.apz.currAppId;
        for (var k = 0; k < params.res.screenDef.ifaces.length; k++) {
            if (this.apz.containsKey(this.apz.ifacesMap[appId], params.ifaces[k])) {
                params.ifaces.splice(k, 1);
            }
        }
        if (params.res.screenDef.ifaces.length > 0) {
            params.ifaceName = "appzillonGetIntfDef";
            params.id = this.apz.getProcId();
            params.internal = true;
            params.async = true;
            params.callBack = this.getIntfDefCB;
            params.callBackObj = this;
            params.req = {};
            params.req.appzillonGetIntfDefRequest = {};
            params.req.appzillonGetIntfDefRequest.appId = appId;
            params.req.appzillonGetIntfDefRequest.interfaceId = params.params.res.screenDef.ifaces.name;
            this.apz.server.sendReq(params);
        } else if (!this.apz.isNull(params.scrDets)) {
            this.appendProduct(params);
        }
    },
    getIntfDefCB: function (params) {
        if (!this.apz.isNull(params.res.appzillonGetIntfDefResponse)) {
            var intfs = params.res.appzillonGetIntfDefResponse;
            for (var k = 0, len = intfs.length; k < len; k++) {
                var obj = {};
                obj.appId = params.appId || this.apz.currAppId;
                obj.content = unescape(intfs[k].ifaceDef);
                this.apz.loadIfaceDef(obj);
            }
        }
        if (!this.apz.isNull(params.scrDets)) {
            this.appendProduct(params);
        }
    },
    /**
     * Method to fetch the dependent Attributes and containers
     */
    fnFetchDependents: function (obj) {
        /*This API is used to fetch the dependents of an element*/
        let currElm = this.apz.scrMetaData.elmsMap[obj.id];
        let result = {
            "relatedelms": [],
            "relatedcntrs": []
        };
        if (this.apz.isNull(currElm)) {
            var id = this.apz.getObjIdWORowNumber(obj);
            if (obj.getAttribute("type") == "radio") {
                if ($(obj).parent('div').attr("apztype") == "toggleswitch") {
                    id = this.apz.getObjIdWORowNumber($(obj).parent('div')[0]);
                } else {
                    if ($(obj).parents('span.etb-rdio:first').attr("apztype") == "radiogroup") {
                        id = this.apz.getObjIdWORowNumber($(obj).parents('span.etb-rdio:first')[0]);
                    } else if ($(obj).parents('span.etb-rdgp:first').attr("apztype") == "radiogroup") {
                        id = this.apz.getObjIdWORowNumber($(obj).parents('span.etb-rdgp:first')[0]);
                    }
                }
            }
            currElm = this.apz.scrMetaData.elmsMap[id];
            if (!this.apz.isNull(currElm.relatedelms)) {
                result.relatedelms = currElm.relatedelms;
            }
            if (!this.apz.isNull(currElm.relatedcontainers)) {
                result.relatedcntrs = currElm.relatedcontainers;
            }
        } else {
            if (!this.apz.isNull(currElm.relatedelms)) {
                result.relatedelms = currElm.relatedelms;
            }
            if (!this.apz.isNull(currElm.relatedcontainers)) {
                result.relatedcntrs = currElm.relatedcontainers;
            }
        }
        return result;
    },
    /**
     * The entry point for prcessing the rules of an Attribute
     */
    processRule: function (obj) {
        /* This API is used to process the object and decide the operations to be performed on the element */
        let processRule = true;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preProcessRule)) {
            processRule = this.apz[this.apz.currAppId].preProcessRule(obj);
        }
        if (processRule) {
            this.fnProcessSelfRule(obj);
            let dependents = this.fnFetchDependents(obj).relatedelms;
            let dependentCntrs = this.fnFetchDependents(obj).relatedcntrs;
            var id = this.apz.getObjIdWORowNumber(obj);
            for (let k = 0, len = dependents.length; k < len; k++) {
                if (dependents[k] != id && this.apz.scrMetaData.elmsMap[dependents[k]]) {
                    let rules = this.apz.scrMetaData.elmsMap[dependents[k]].decorations;
                    let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[dependents[k]].container];
                    if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                        for (let j = 0; j < containerObj.totalRecs; j++) {
                            let rowNo = j;
                            let params = {
                                "rules": rules,
                                "id": dependents[k],
                                "rowNo": rowNo
                            };
                            this.applyRules(params);
                        }
                    } else {
                        let rowNo = null;
                        let params = {
                            "rules": rules,
                            "id": dependents[k],
                            "rowNo": rowNo
                        };
                        this.applyRules(params);
                    }
                }
            }
            this.checkContainerVisibility();
            for (let k = 0, len = dependentCntrs.length; k < len; k++) {
                if (!this.apz.isNull(this.apz.scrMetaData.containersMap[dependentCntrs[k]])) {
                    let rules = this.apz.scrMetaData.containersMap[dependentCntrs[k]].decorations;
                    let params = {
                        "rules": rules,
                        "id": dependentCntrs[k]
                    };
                    this.applycntrRulesOnAttrChange(params);
                }
            }
            this.checkParentContainerCardinality(obj);
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postProcessRule)) {
            this.apz[this.apz.currAppId].postProcessRule(obj);
        }
    },
    /**
     * Method to process the self rules of an Attribute
     */
    fnProcessSelfRule: function (obj, event) {
        let processSelfRule = true;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preProcessSelfRule)) {
            processSelfRule = this.apz[this.apz.currAppId].preProcessSelfRule(obj);
        }
        if (processSelfRule) {
            let id = this.apz.getObjIdWORowNumber(obj);
            let rowNo = this.apz.getObjRowNumber(obj);
            if (obj.getAttribute("type") == "radio") {
                if ($(obj).parent('div').attr("apztype") == "toggleswitch") {
                    id = this.apz.getObjIdWORowNumber($(obj).parent('div')[0]);
                } else {
                    if ($(obj).parents('span.etb-rdio:first').attr("apztype") == "radiogroup") {
                        id = this.apz.getObjIdWORowNumber($(obj).parents('span.etb-rdio:first')[0]);
                    } else if ($(obj).parents('span.etb-rdgp:first').attr("apztype") == "radiogroup") {
                        id = this.apz.getObjIdWORowNumber($(obj).parents('span.etb-rdgp:first')[0]);
                    }
                }
            }
            let rules = this.apz.scrMetaData.elmsMap[id].decorations;
            let params = {
                "rules": rules,
                "id": id,
                "rowNo": rowNo,
                "processingSelfRule": true
            };
            this.applyRules(params);
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postProcessSelfRule)) {
            this.apz[this.apz.currAppId].postProcessSelfRule(obj);
        }
    },
    /**
     * Method to process the elements present inside a screen div
     */
    fnProcessChildElems: function (screenName) {
        let ProcessChildElems = true;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preProcessChildElems)) {
            ProcessChildElems = this.apz[this.apz.currAppId].preProcessChildElems();
        }
        if (ProcessChildElems) {
            let screenDetails = this.apz.scrDefsMap[apz.currAppId][screenName];
            if (!this.apz.isNull(screenDetails) && !this.apz.isNull(screenDetails.decorations)) {
                for (let j = 0; j < screenDetails.decorations.length; j++) {
                    let rule = screenDetails.decorations[j].Name.split("_");
                    if (rule[2].startsWith("LinkedItem")) {
                        let json = {
                            "typeOfAPI": "LinkedItem",
                            "rule": screenDetails.decorations[j].Value
                        };
                        this.callAPI(json);
                    }
                }
            }
            if (screenDetails && screenDetails.elmsMap) {
                for (key in screenDetails.elmsMap) {
                    if (screenDetails.elmsMap.hasOwnProperty(key)) {
                        if (screenDetails.elmsMap[key].decorations) {
                            let rules = screenDetails.elmsMap[key].decorations;
                            let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[key].container];
                            if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                                for (let j = 0; j < containerObj.totalRecs; j++) {
                                    let rowNo = j;
                                    let params = {
                                        "rules": rules,
                                        "id": key,
                                        "rowNo": rowNo
                                    };
                                    this.applyRules(params);
                                }
                            } else {
                                let rowNo = null;
                                let params = {
                                    "rules": rules,
                                    "id": key,
                                    "rowNo": rowNo
                                };
                                this.applyRules(params);
                            }
                        }
                    }
                }
            }
            this.checkContainerVisibility();
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postProcessChildElems)) {
            this.apz[this.apz.currAppId].postProcessChildElems();
        }
    },
    /**
     * Method to process all the elements present in the DOM
     */
    fnProcessScreenElems: function () {
        let ProcessScreenElems = true;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preProcessScreenElems)) {
            ProcessScreenElems = this.apz[this.apz.currAppId].preProcessScreenElems();
        }
        if (ProcessScreenElems) {
            let screenDetails = this.apz.scrMetaData.elmsMap;
            if (screenDetails) {
                for (key in screenDetails) {
                    if (screenDetails.hasOwnProperty(key)) {
                        if (screenDetails[key].decorations) {
                            let rules = screenDetails[key].decorations;
                            let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[key].container];
                            if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                                for (let j = 0; j < containerObj.totalRecs; j++) {
                                    let rowNo = j;
                                    let params = {
                                        "rules": rules,
                                        "id": key,
                                        "rowNo": rowNo
                                    };
                                    this.applyRules(params);
                                }
                            } else {
                                let rowNo = null;
                                let params = {
                                    "rules": rules,
                                    "id": key,
                                    "rowNo": rowNo
                                };
                                this.applyRules(params);
                            }
                        }
                    }
                }
            }
            this.checkContainerVisibility();
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postProcessScreenElems)) {
            this.apz[this.apz.currAppId].postProcessScreenElems();
        }
    },
    /**
     * Method to process the rules of containers present inside a screen
     */
    fnprocessChildContainers: function (screenName) {
        let processChildContainers = true;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preprocessChildContainers)) {
            processChildContainers = this.apz[this.apz.currAppId].preprocessChildContainers();
        }
        if (processChildContainers) {
            let screenDetails = this.apz.scrDefsMap[apz.currAppId][screenName];
            if (screenDetails && screenDetails.containers) {
                for (let key = 0; key < screenDetails.containers.length; key++) {
                    if (screenDetails.containers[key].decorations && screenDetails.containers[key].decorations.length > 0) {
                        let params = {
                            "rules": screenDetails.containers[key].decorations,
                            "id": screenDetails.containers[key].id
                        };
                        this.applyContainerRules(params);
                    }
                }
            }
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postprocessChildContainers)) {
            this.apz[this.apz.currAppId].postprocessChildContainers();
        }
    },
    /**
     * Single point to start the processing of rules of all elements and containers in a screen
     */
    processScreen: function (params) {
        /*This Api is to be called once the screen is shown to process the rules of the elements in the screen, if the rules have to be processed to the elements on the screen launched, the scr[screen name],layout and design names of the screen must be passed inside an object */
        let ProcessScreen = true;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preProcessScreen)) {
            ProcessScreen = this.apz[this.apz.currAppId].preProcessScreen();
        }
        if (ProcessScreen) {
            if (!this.apz.isNull(params) && !this.apz.isNull(params.scr)) {
                let scr = params.scr + this.apz.idSep + params.layout + this.apz.idSep + params.design;
                this.fnProcessChildElems(scr);
                this.fnprocessChildContainers(scr);
            } else {
                this.fnProcessScreenElems();
            }
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postProcessScreen)) {
            this.apz[this.apz.currAppId].postProcessScreen();
        }
    },
    /**
     * Method which applies the rules once for an Attribute.
     */
    applyRules: function (params) {
        let rules = params.rules,
            id = params.id,
            rowNo = params.rowNo;
        for (let i = 0, ruleLen = rules.length; i < ruleLen; i++) {
            let rule = rules[i].name.split("_");
            if (rule[1].trim() === this.RootPrdShortName) {
                if (rule[2].trim() === "Pname") {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.fnPname(json);
                } else if (rule[2].trim() === "RPID") {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.fnRPID(json);
                } else if (rule[2].trim() === "PPID") {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.fnPPID(json);
                } else if (rule[2].trim() === "Cardinality") {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.Cardinality(json);
                }
            }
        }
        this.applyElementRules(params);
    },
    /**
     * Method which applies the rules based on other attribute's value
     */
    applyElementRules: function (params) {
        let rules = params.rules,
            id = params.id,
            rowNo = params.rowNo;
        for (let i = 0, ruleLen = rules.length; i < ruleLen; i++) {
            let rule = rules[i].name.split("_");
            if (rule[0].trim() === "DX" && rule[1].trim() === this.RootPrdShortName) {
                if (rule[2].trim() === "Show" || rule[2].trim() === "Hide") {
                    let json = {
                        "id": id
                    };
                    json.showAttr = rules[i].value;
                    if (rule[2].trim() === "Show") {
                        this.Show(json);
                    } else {
                        this.Hide(json);
                    }
                } else if (rule[2].startsWith("SetValue")) {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.DXSetvalue(json);
                } else if (rule[2].startsWith("SetRule")) {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.SetRule(json);
                } else if (rule[2].startsWith("Mapping")) {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.Mapping(json);
                } else if (rule[2].startsWith("Rvalue")) {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.Rvalue(json);
                }
            } else if (rule[1].trim() === this.RootPrdShortName) {
                //handling multiple show conditions for inflight changes : investors
                if (rule[2].startsWith("Show") || rule[2].startsWith("Hide")) {
                    /*Hide and Show for multirecord elements has been handled in handleDisplay method,whereas, Hiding or showing an element in one row is illogical, hence that is not handled.*/
                    if (this.apz.isNull(params.processingSelfRule)) {
                        //// "rowNo" : rowNo added on 2 april 2020 - Requirement was to capture multi record from the screen. 
                        //// Form was the only option and it captures single record, so, team added html manually by changing it to list and thus multi record was achieved
                        //// Update it was not showing the list on screen as element in list is containing _rowNo which was missing without this passed
                        //// "rowNo" : rowNo  ends 
                        let json = {
                            "id": id,
                            "rowNo": rowNo
                        };
                        json.showAttr = rules[i].value;
                        //handling multiple show conditions for inflight changes : investors
                        if (rule[2].startsWith("Show")) {
                            this.Show(json);
                        } else {
                            this.Hide(json);
                        }
                    }
                } else if (rule[2].startsWith("SetValue")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.Setvalue(json);
                } else if (rule[2].startsWith("Mapping")) {
                    /*Mapping is no more available in new grammar*/
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.Mapping(json);
                } else if (rule[2].startsWith("GetValue")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.GetValue(json);
                } else if (rule[2].startsWith("CMan")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.CMan(json);
                } else if (rule[2].startsWith("Rvalue")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.Rvalue(json);
                } else if (rule[2].trim() === "API") {
                    if (this.apz.scrMetaData.elmsMap[id].externalWidgetType !== "DPF" && this.apz.scrMetaData.elmsMap[id].externalWidgetType !== "IPS") {
                        let json = {
                            "id": id
                        };
                        json.rule = rules[i].value;
                        this.callElementAPI(json);
                    }
                } else if (rule[2].startsWith("LinkedItem")) {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.fetchLinkedItem(json);
                } else if (rule[2].startsWith("UIDescp")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.UIDescp(json);
                } else if (rule[2].startsWith("CopyValueToTag")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.CopyValueToTag(json);
                } else if (rule[2].startsWith("MIN")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.fnNumberValidate(json);
                } else if (rule[2].startsWith("MAX")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.fnNumberValidate(json);
                } else if (rule[2].startsWith("InFlight")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.InFlightRejection(json);
                } else if (rule[2].startsWith("BasicRuleSet") && rules[i].value.startsWith("FIL")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.setUploadMapper(json);
                } else if (rule[2].startsWith("ElasticSearch")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.callElasticSearch(json);
                }
                // Investors: Inflight delete Question handling starts
                else if (rule[2].startsWith("DeletedQues")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "rowNo": rowNo
                    };
                    this.InflightDeleteQuestion(json);
                }
                // Investors: Inflight delete Question handling ends
            }
        }
    },
    /**
     * Method which applies the rules on containers/Relations
     */
    applyContainerRules: function (params) {
        let rules = params.rules,
            id = params.id;
        for (let i = 0, ruleLen = rules.length; i < ruleLen; i++) {
            let rule = rules[i].name.split("_");
            if (rule[1].trim() === this.RootPrdShortName) {
                if (rule[2].trim() === "Cardinality") {
                    let json = {
                        "id": id
                    };
                    json.rule = rules[i].value;
                    this.containerCardinality(json);
                }
            }
        }
        this.applycntrRulesOnAttrChange(params);
    },
    /**
     * Method which applies the rules for the containers on change of an Attribute 
     */
    applycntrRulesOnAttrChange: function (params) {
        let rules = params.rules,
            id = params.id;
        let optionsArray = [];
        if (this.containerOptionsMap[params.id]) {
            optionsArray = this.apz.copyJSONObject(this.containerOptionsMap[params.id]);
        }
        for (let i = 0, ruleLen = rules.length; i < ruleLen; i++) {
            let rule = rules[i].name.split("_");
            if (rule[1].trim() === this.RootPrdShortName) {
                if (rule[2].trim() === "Show" || rule[2].trim() === "Hide") {
                    this.apz.scrMetaData.containersMap[id].hasDisplayRule = true;
                    let json = {
                        "id": id
                    };
                    json.showAttr = rules[i].value;
                    if (rule[2].trim() === "Show") {
                        this.showContainer(json);
                    } else {
                        this.hideContainer(json);
                    }
                } else if (rule[2].trim() === "UIDescp") {
                    let json = {
                        "id": id,
                        "rule": rules[i].value
                    };
                    this.containerUIDescp(json);
                } else if (rule[2].startsWith("Rvalue")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value,
                        "options": optionsArray
                    };
                    this.containerRvalue(json);
                }
                // Investors: Inflight delete Question handling starts
                else if (rule[2].startsWith("DeletedQues")) {
                    let json = {
                        "id": id,
                        "rule": rules[i].value
                    };
                    this.InflightDeleteQuestion(json);
                }
                // Investors: Inflight delete Question handling ends
            }
        }
        if (optionsArray.length >= 0) {
            $("#" + id + "_DDN").empty();
            this.fnInitMultiSelectDropdown(this.baseProductId, id + "_DDN", optionsArray, optionsArray.length);
            if (document.getElementById(id + "_DDN")) {
                this.resetMultiselectDDN(this.baseProductId, document.getElementById(id + "_DDN"), this.updateDropDownScrData);
            }
        }
    },
    /**
     * Method to apply the cardinality for a container
     */
    containerCardinality: function (params) {
        /* shyam changes removal of this  */
        if (apz.scrMetaData.containersMap[params.id].type === "FORM") {
            let defaultCardinality = parseInt(params.rule.split("|")[2], 10);
            /* shyam changes : removal of this  */
            let elmsMap = apz.scrMetaData.containersMap[params.id].elmsMap;
            for (let key in elmsMap) {
                if (defaultCardinality && elmsMap[key].type === "CHECKBOX") {
                    let checkedValue = $("#" + elmsMap[key].id).attr("checkedval");
                    this.apz.setElmValue(elmsMap[key].id, checkedValue);
                    defaultCardinality--;
                } else if (defaultCardinality === 0) {
                    break;
                }
            }
        }
    },
    /**
     * Method to handle the Show rule of a container
     */
    showContainer: function (params) {
        if (this.RuleProcessor(params)) {
            this.apz.showContainer(params.id);
            this.handleDefaultProductDisplay(params.id, "show");
        } else {
            this.apz.hideContainer(params.id);
            this.handleDefaultProductDisplay(params.id, "hide");
        }
    },
    /**
     * Method to handle the Hode rule of a container
     */
    hideContainer: function (params) {
        if (this.RuleProcessor(params)) {
            this.apz.hideContainer(params.id);
            this.handleDefaultProductDisplay(params.id, "hide");
        } else {
            this.apz.showContainer(params.id);
            this.handleDefaultProductDisplay(params.id, "show");
        }
    },
    /**
     * Method to handle the display of UI Description of a container
     */
    containerUIDescp: function (params) {
        let rule = params.rule.split("|").splice(3).join("|");
        if (!this.apz.isNull(rule)) {
            let json = {
                showAttr: rule
            };
            let result = this.RuleProcessor(json);
            if (result) {
                let elmId = params.id + "_uidesc";
                let parent = $("#" + elmId).parent();
                if ($(parent).hasClass("crt-form")) {
                    function checkChildProducts(newparent) {
                        if ($(newparent).next().hasClass("productRoot")) {
                            checkChildProducts($(newparent).next());
                        } else {
                            if ($(newparent).attr("id") == $(parent).attr("id")) {
                                this.apz.show(elmId);
                            } else {
                                let outerHtml = $("#" + elmId)[0].outerHTML;
                                $("#" + elmId).remove();
                                $(newparent).after(outerHtml);
                                // parent container UI Desc position at the last : investors
                                this.apz.show(elmId);
                            }
                            return;
                        }
                    }
                    checkChildProducts(parent);
                } else {
                    this.apz.show(elmId);
                }
            } else {
                this.apz.hide(params.id + "_uidesc");
            }
        }
    },
    /**
     * Method to remove the values from Multi-select dropdown based on condition
     */
    containerRvalue: function (params) {
        if (apz.scrMetaData.containersMap[params.id].type === "FORM") {
            if (params.options.length > 0) {
                let lastFound = params.rule.lastIndexOf("|");
                let valueToRemove = params.rule.substr(lastFound + 1, params.rule.length).trim();
                let showRule = params.rule.substr(0, lastFound).trim();
                if (showRule.startsWith("(") && (showRule.indexOf("&&") < 0) && (showRule.indexOf("||") < 0)) {
                    showRule = showRule.split("(")[1];
                    showRule = showRule.split(")")[0];
                }
                let json = {
                    "showAttr": showRule
                };
                if (this.RuleProcessor(json)) {
                    for (let k = 0; k < params.options.length; k++) {
                        if (valueToRemove === params.options[k].id) {
                            apz.setElmValue(params.options[k].id, $("#" + params.options[k].id).attr("uncheckedval"));
                            params.options.splice(k, 1);
                            break;
                        }
                    }
                }
            }
        }
    },
    /**
     * Method to reset the tags in multiselect DDN
     */
    resetMultiselectDDN: function (prodid, pobj, funcName) {
        $("#" + pobj.id).val(null).trigger("change");
        var appId = this.apz.currAppId;
        var valuesArray = [];
        let selectedValues = [];
        $(pobj).find("option").each(function () {
            let val = apz.getElmValue($(this).val());
            if (val === "y") {
                let temp = {};
                temp.id = $(this).val();
                temp.text = $(this).text();
                valuesArray.push(temp);
                selectedValues.push(temp.id);
            }
        });
        this.fnInitMultiSelectDropdown(prodid, pobj.id, valuesArray, valuesArray.length, funcName);
        if (!$(pobj).parent().hasClass("mltt")) {
            $(pobj).parent().addClass("mltt");
        }
        this.apz.setObjValue($("#" + pobj.id)[0], selectedValues.join(","));
        /*if (valuesArray.length > 0) {
            this.checkParentContainerCardinality(document.getElementById(valuesArray[0].id));
        }*/
    },
    /**
     * Investors: Method to identify the deleted question
     */
    InflightDeleteQuestion: function (params) {
        if (params.rule == "Y") {
            $("#" + params.id).addClass("deletedQuestion");
        }
    },
    /**
     * Method to apply parent container cardinality when an attribute is changed.
     */
    checkParentContainerCardinality: function (obj) {
        let elmObj = this.apz.scrMetaData.elmsMap[obj.id];
        let MltDDNFlag = false;
        if (!this.apz.isNull(elmObj)) {
            let cntrObj = this.apz.scrMetaData.containersMap[elmObj["container"]];
            if (elmObj.type == "CHECKBOX" && cntrObj.type == "FORM") {
                for (let i = 0; i < cntrObj.decorations.length; i++) {
                    let rule = cntrObj.decorations[i].name.split("_");
                    if (rule[1].trim() === this.RootPrdShortName && rule[2].trim() == "Cardinality") {
                        let maxCardinality = Number(cntrObj.decorations[i].value.split("|")[1]);
                        if (!isNaN(maxCardinality)) {
                            let currentCardinality = 0;
                            let selectedValues = [];
                            $("#" + elmObj["container"]).find("input").each(function () {
                                if ($(this).prop("checked")) {
                                    currentCardinality++;
                                    selectedValues.push($(this).closest("ul").find("label").text());
                                }
                            });
                            if (maxCardinality === currentCardinality) {
                                $("#" + elmObj["container"] + "_DDN").find("option").each(function () {
                                    if (selectedValues.indexOf(this.label) < 0) {
                                        $(this).attr("disabled", "disabled");
                                    }
                                });
                            } else {
                                $("#" + elmObj["container"] + "_DDN").find("option").each(function () {
                                    if (selectedValues.indexOf(this.label) < 0) {
                                        $(this).removeAttr("disabled");
                                    }
                                });
                            }
                            if ($("#" + elmObj["container"] + "_DDN").data("isOpen")) {
                                MltDDNFlag = true;
                            }
                            this.debounceInitDropdown(elmObj["container"], MltDDNFlag);
                        }
                    }
                }
            }
        }
    },
    /**
     * Method to handle the default product display on Show/Hide of a container
     */
    handleDefaultProductDisplay: function (id, flag) {
        if (apz.scrMetaData.containersMap[id].defaultProducts && apz.scrMetaData.containersMap[id].defaultProducts.length > 0) {
            for (let h = 0; h < apz.scrMetaData.containersMap[id].defaultProducts.length; h++) {
                if (flag === "show") {
                    $("#" + apz.scrMetaData.containersMap[id].defaultProducts[h] + "_Scr_div").removeClass("sno");
                    //empty relationship has default product : investor
                    this.checkContainerVisibilityofScreen(apz.scrMetaData.containersMap[id].defaultProducts[h]);
                } else if (flag === "hide") {
                    $("#" + apz.scrMetaData.containersMap[id].defaultProducts[h] + "_Scr_div").addClass("sno");
                    //reset default product within a relationship
                    this.setDefaultValueForDefaultProductAttributes(apz.scrMetaData.containersMap[id].defaultProducts[h]);
                }
            }
        }
    },
    //reset default product within a relationship
    setDefaultValueForDefaultProductAttributes: function (scrName) {
        if (this.SetDefaultValueOnHide && this.apz.scrDefsMap[apz.currAppId][scrName] + "_Scr__NewLayout__D0") {
            let elms = this.apz.scrDefsMap[this.apz.currAppId][scrName + "_Scr__NewLayout__D0"].elms;
            for (let k = 0; k < elms.length; k++) {
                let container = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[elms[k].id].container];
                if (container.type == "FORM") {
                    let params = {
                        "id": elms[k].id
                    };
                    this.SetDefaultValue(params, elms[k].id);
                } else if (container.type == "LIST" || container.type == "TABLE") {
                    for (let j = 0; j < container.totalRecs; j++) {
                        let params = {
                            "id": elms[k].id
                        };
                        this.SetDefaultValue(params, elms[k].id + "_" + j);
                    }
                }
            }
            $("#" + scrName + "_Scr_div").find(".customMultiSelect").each(function () {
                apz.products.resetMultiselectDDN(apz.products.baseProductId, this, apz.products.updateDropDownScrData);
            });
        }
    },
    SetRule: function (params) {
        /* This specifies the values that needs to be set */
        let rule = params.rule.split("=");
        let lhs = rule[0].trim().split(".");
        let rhs = rule[1].trim();
        var values = window;
        for (var k = 0; k < lhs.length; k++) {
            if (k == lhs.length - 1) {
                values[lhs[k]] = rhs;
            } else {
                values = values[lhs[k]];
            }
        }
    },
    fnRPID: function (params) {
        /* This specifies the root product ID*/
        let rule = params.rule.split("=");
        let rhs = rule[1].trim();
        this.RPID = rhs;
    },
    fnPPID: function (params) {
        /* This specifies the root product ID*/
        let rule = params.rule.split("=");
        let rhs = rule[1].trim();
        this.PPID = rhs;
    },
    Cardinality: function (params) {
        /* This specifies the cardinality */
    },
    fnPname: function (params) {
        /* This gives the name of the product */
        let rule = params.rule.split("=");
        let rhs = rule[1].trim();
        this.Pname = rhs;
    },
    /**
     * Method to set the value of an element/tag by fetching from another element
     */
    CopyValueToTag: function (params) {
        let val = "";
        if (document.getElementById(params.id)) {
            val = apz.getElmValue(params.id);
        }
        if (document.getElementById(params.rule)) {
            apz.setElmValue(params.rule, val);
        }
    },
    /**
     * Method to handle the Show rule of an Attribute
     */
    Show: function (params) {
        let RuleRes = this.RuleProcessor(params);
        this.handleDisplay(params, RuleRes, "showElement", "hideElement");
    },
    /**
     * Method to handle the Hide rule of an Attribute
     */
    Hide: function (params) {
        let RuleRes = this.RuleProcessor(params);
        this.handleDisplay(params, RuleRes, "hideElement", "showElement");
    },
    /**
     * Method to handle the Show/Hide of an Attribute considering parent container
     */
    handleDisplay: function (params, RuleRes, met1, met2) {
        let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[params.id].container];
        var elmName = this.apz.scrMetaData.elmsMap[params.id].name;
        if (!this.displayRuleMapper[params.id])
            this.displayRuleMapper[params.id] = {};
        this.displayRuleMapper[params.id]["RuleValue"] = params.showAttr;
        met1.indexOf("show") == 0 ? this.displayRuleMapper[params.id]["RuleName"] = "Show" : this.displayRuleMapper[params.id]["RuleName"] = "Hide";
        if (!RuleRes) {
            this.displayRuleMapper[params.id]["ValidCases"] = [];
            this.displayRuleMapper[params.id]["ValidCases"][0] = params.showAttr;
            this.displayRuleMapper[params.id]["Visibility"] = met2;
        } else {
            this.displayRuleMapper[params.id]["Visibility"] = met1;
        }
        let origId = params.id.split("__");
        this.displayRuleMapper[params.id]["OrigId"] = origId[origId.length - 1];
        if (this.displayRuleMapper[params.id].ValidCases && this.displayRuleMapper[params.id].ValidCases.length > 0) {
            let arrLen = this.displayRuleMapper[params.id].ValidCases.length;
            for (let i = 0; i < arrLen; i++) {
                if (this.displayRuleMapper[params.id].RuleValue.indexOf(this.displayRuleMapper[params.id].ValidCases[i]) == -1) {
                    this.displayRuleMapper[params.id].ValidCases.splice(i, 1);
                }
            }
        }
        if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
            for (let j = 0; j < containerObj.totalRecs; j++) {
                let id = params.id + "_" + j;
                if (RuleRes) {
                    this.apz[met1](id);
                    if (this.SetDefaultValueOnHide && met1 == "hideElement") {
                        this.SetDefaultValue(params, id);
                    }
                } else {
                    this.apz[met2](id);
                    if (this.SetDefaultValueOnHide && met1 == "showElement") {
                        this.SetDefaultValue(params, id);
                    }
                }
            }
            if (containerObj.type === "LIST") {
                let arr = containerObj.name.split('__');
                let title = arr[0] + "__" + arr[1] + "__" + elmName + "_title";
                let header = $("#" + containerObj.name + "_header");
                if (RuleRes) {
                    this.apz[met1](title);
                } else {
                    this.apz[met2](title);
                }
            }
        } else {
            if (RuleRes) {
                this.apz[met1](params.id);
                if (this.SetDefaultValueOnHide && met1 == "hideElement") {
                    this.SetDefaultValue(params, params.id);
                }
            } else {
                this.apz[met2](params.id);
                if (this.SetDefaultValueOnHide && met1 == "showElement") {
                    this.SetDefaultValue(params, params.id);
                }
            }
        }
    },
    /**
     * Method to set the Default value of an Attribute
     */
    SetDefaultValue: function (params, id) {
        let elmObj = this.apz.scrMetaData.elmsMap[params.id];
        let defaultValue = "";
        for (let k = 0; k < elmObj.decorations.length; k++) {
            if (elmObj.decorations[k].name.split("_")[2] == "BasicRuleSet") {
                defaultValue = elmObj.decorations[k].value.split("|")[3];
            }
        }
        this.apz.setElmValue(id, defaultValue);
        this.processRule(document.getElementById(id));
        // data reset : investors
        if ($("#" + id).hasClass("dataAvailable")) {
            $("#" + id).removeClass("dataAvailable");
        }
    },
    /**
     * Method to process the Setvalue rule of an Attribute
     */
    Setvalue: function (params) {
        if (this.apz.isNull(this.applyrulesOnlyOnUserAction) || this.applyrulesOnlyOnUserAction == false) {
            let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[params.id].container];
            var elmName = this.apz.scrMetaData.elmsMap[params.id].name;
            let valueToCompare = params.rule.split("|")[0].trim();
            let operatorToCompare = "=";
            if (valueToCompare.substr(0, 2) == "!=") {
                operatorToCompare = "!";
                valueToCompare = valueToCompare.substr(2);
            }
            if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                params.id = params.id + "_" + params.rowNo;
                if (document.getElementById(params.id) && (this.operation(operatorToCompare, this.apz.getElmValue(params.id), valueToCompare))) {
                    let elementsToSet = params.rule.split("|")[1].trim().split(",");
                    for (let j = 0, len = elementsToSet.length; j < len; j++) {
                        let element = elementsToSet[j].split("=")[0].trim() + "_" + params.rowNo;
                        if (element.charAt(0) === ".") {
                            element = element.substr(1);
                        }
                        if (!this.apz.isNull(document.getElementById(element))) {
                            this.apz.setElmValue(element, this.processArithmeticOperators(elementsToSet[j].split("=")[1].trim()));
                        }
                    }
                }
            } else {
                if (document.getElementById(params.id) && (this.operation(operatorToCompare, this.apz.getElmValue(params.id), valueToCompare))) {
                    let elementsToSet = params.rule.split("|")[1].trim().split(",");
                    for (let j = 0, len = elementsToSet.length; j < len; j++) {
                        let element = elementsToSet[j].split("=")[0].trim();
                        if (element.charAt(0) === ".") {
                            element = element.substr(1);
                        }
                        if (!apz.isNull(this.apz.scrMetaData.elmsMap[element])) {
                            let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[element].container];
                            if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                                for (let k = 0; k < containerObj.totalRecs; k++) {
                                    let currElement = element + "_" + k;
                                    if (!this.apz.isNull(document.getElementById(currElement))) {
                                        this.apz.setElmValue(currElement, this.processArithmeticOperators(elementsToSet[j].split("=")[1].trim()));
                                    }
                                }
                            } else {
                                if (!this.apz.isNull(document.getElementById(element))) {
                                    this.apz.setElmValue(element, this.processArithmeticOperators(elementsToSet[j].split("=")[1].trim()));
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    /**
     * Method to process the Arithmetic operators present in an expression
     */
    processArithmeticOperators: function (expr) {
        /* This method supports only one operator in an expression currently */
        let result = "";
        if (expr.indexOf("+") >= 0 || expr.indexOf("-") >= 0 || expr.indexOf("*") >= 0 || expr.indexOf("/") >= 0 || expr.indexOf("%") >= 0) {
            let index = expr.indexOf("+") || expr.indexOf("-") || expr.indexOf("*") || expr.indexOf("/") || expr.indexOf("%");
            result = this.operation(expr.substr(index, 1), this.fnFetchValue(expr.substr(0, index - 1).trim()), this.fnFetchValue(expr.substr(index + 1).trim()));
        } else {
            result = this.fnFetchValue(expr);
        }
        if (isNaN(result)) {
            result = expr;
        }
        return result;
    },
    DXSetvalue: function (params) {
        /* This method is used to support old setValue method */
        /* Set value here refers to setting a particular value to output argument */
        let rule, operator;
        if (params.rule.indexOf("!=") > 0 || params.rule.indexOf("=") > 0) {
            if (params.rule.indexOf("!=") > 0) {
                rule = params.rule.split("!=");
                operator = "!="
            } else {
                rule = params.rule.split("=");
                operator = "="
            }
            let splitRight = rule[1].split(",");
            let value = this.fnFetchValue(rule[0].trim());
            if (this.operation(operator, value, splitRight[0].trim())) {
                if (!this.apz.isNull(document.getElementById(params.id)))
                    this.apz.setElmValue(params.id, splitRight[1].trim());
            }
        } else {
            let split = params.rule.split(",");
            if (!this.apz.isNull(document.getElementById(params.id)))
                this.apz.setElmValue(params.id, this.fnFetchValue(split[1].trim()));
        }
    },
    /**
     * Method used to set a value to an attribute based on another Attribute's value
     */
    GetValue: function (params) {
        if (this.apz.isNull(this.applyrulesOnlyOnUserAction) || this.applyrulesOnlyOnUserAction == false) {
            let lastFound = params.rule.lastIndexOf('|');
            let Expression = params.rule.substr(0, lastFound).trim();
            let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[params.id].container];
            if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                params.id = params.id + "_" + params.rowNo;
                let json = {
                    "showAttr": Expression
                };
                if (this.RuleProcessor(json)) {
                    if (document.getElementById(params.id)) {
                        this.apz.setElmValue(params.id, this.fnFetchValue(params.rule.substr(lastFound + 1).trim(), params.rowNo));
                    }
                }
            } else {
                let json = {
                    "showAttr": Expression
                };
                if (this.RuleProcessor(json)) {
                    if (document.getElementById(params.id)) {
                        this.apz.setElmValue(params.id, this.fnFetchValue(params.rule.substr(lastFound + 1).trim()));
                    }
                }
            }
        }
    },
    /**
     * Method to validate the Min/Max rules of an Attribute
     */
    fnNumberValidate: function (params) {
        let pObj = '';
        let status = true;
        let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[params.id].container];
        if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
            pObj = document.getElementById(params.id + "_" + params.rowNo);
        } else {
            pObj = document.getElementById(params.id);
        }
        if (!this.apz.isNull(this.apz.getObjValue(pObj))) {
            let error = this.apz.val.validateInputAct(pObj, false);
            if (this.apz.isNull(error)) {
                if ($(pObj).data("bs.popover")) {
                    $(pObj).popover('disable');
                }
            } else {
                if (this.apz.isFunction(this.apz.app.preEnablePopover)) {
                    status = this.apz.app.preEnablePopover(obj);
                }
                if (status) {
                    let msg = "";
                    if (error === "APZ-VAL-006") {
                        msg = this.apz.msgs[this.apz.appId]["APZ-CITI-LTMIN"];
                    } else if (error === "APZ-VAL-005") {
                        msg = this.apz.msgs[this.apz.appId]["APZ-CITI-GTMAX"];
                    } else {
                        msg = this.apz.msgs[this.apz.appId]["APZ-CITI-INVNUM"];
                    }
                    if (!apz.isNull(msg)) {
                        msg = msg.slice(1);
                    } else {
                        msg = "Invalid Number";
                    }
                    this.initializeErrorPopover(pObj.id, msg);
                }
                if (this.apz.isFunction(this.apz.app.postEnablePopover)) {
                    this.apz.app.postEnablePopover(obj);
                }
            }
        }
    },
    /**
     * Method to validate a Number field and display errors if there are any.
     */
    formatNumberControl: function (obj) {
        if ($(obj).data("bs.popover")) {
            $(obj).popover('disable');
            $(obj).popover('hide');
        }
        var id = obj.id;
        var elmData = null;
        var recNo = -1;
        var status = true;
        recNo = this.apz.getObjRowNumber(obj);
        id = this.apz.getObjIdWORowNumber(obj);
        try {
            elmData = this.apz.scrMetaData.elmsMap[id];
        } catch (e) {
            elmData = null;
        }
        var value = this.apz.getObjValue(obj);
        if (!this.apz.isNull(value)) {
            if (this.apz.isFunction(this.apz.app.preFormatNumberControl)) {
                status = this.apz.app.preFormatNumberControl(obj);
            }
            if (status) {
                /* this is being handled in appzillon.js*/
                //var errorMsg = this.apz.val.validateNumber(obj, value, elmData.displayAsLiteral);
                //if (this.apz.isNull(errorMsg)) {
                var params = {};
                params.value = value;
                params.decimalSep = this.apz.decimalSep;
                params.mask = this.apz.numberMask;
                params.displayAsLiteral = elmData.displayAsLiteral;
                var decimalPoints = this.apz.getDecimalPoints(elmData, recNo);
                params.decimalPoints = decimalPoints;
                value = this.apz.unFormatNumber(params);
                params.value = value;
                if (value.indexOf("(") != -1) {
                    params.value = "-" + value.replace(/[()]/g, "");
                }
                value = this.apz.formatNumber(params);
                if (value.indexOf("-") > -1) {
                    value = "(" + value.substr(1, value.length) + ")";
                }
                this.apz.setObjValue(obj, value);
                if (elmData.mand === "Y") {
                    $(obj).removeClass("borderRed");
                    $(obj).addClass("borderGreen");
                }
                if ($(obj).hasClass("iosnumber")) {
                    obj.value = '';
                }
            } else {
                if (elmData.mand === "Y") {
                    $(obj).removeClass("borderGreen");
                    $(obj).addClass("borderRed");
                }
            }
            if (this.apz.isFunction(this.apz.app.postFormatNumberControl)) {
                this.apz.app.postFormatNumberControl(obj);
            }
        }
    },
    validateFunctionKeys: function (obj, event) {
        var keyAscii = event.which;
        if (keyAscii == 46 || keyAscii == 8 || keyAscii == 37 || keyAscii == 39 || keyAscii == 38 || keyAscii == 40) {
            var id = obj.id;
            var elmData = null;
            id = this.apz.getObjIdWORowNumber(obj);
            try {
                elmData = this.apz.scrMetaData.elmsMap[id];
            } catch (e) {
                elmData = null;
            }
            var value = this.apz.getObjValue(obj);
            var minMaxError = "";
            if (!this.isNull(elmData)) {
                var lminval = parseInt(elmData.minVal);
                var lmaxval = parseInt(elmData.maxVal);
                if (!apz.isNull(lminval)) {
                    if (value < lminval) {
                        minMaxError = "APZ-VAL-006";
                    }
                }
                if (!apz.isNull(lmaxval)) {
                    if (value > lmaxval) {
                        minMaxError = "APZ-VAL-005";
                    }
                }
            }
            apz.showPopUpOnValidationError(minMaxError, obj, elmData);
        }
    },
    /* inflight excluding options : investor  */
    InFlightRejection: function (params) {
        let pObj = "";
        let elmObj = this.apz.scrMetaData.elmsMap[params.id];
        let containerObj = this.apz.scrMetaData.containersMap[elmObj.container];
        let rejValues = [];
        rejValues = params.rule.split('|');
        if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
            pObj = document.getElementById(params.id + "_" + params.rowNo);
        } else {
            pObj = document.getElementById(params.id);
        }
        for (let i = 0; i < rejValues.length; i++) {
            if (elmObj.type === "DROPDOWN") {
                $(pObj).siblings().find("li[data-value='" + rejValues[i] + "']").addClass("sno");
            } else if (elmObj.type === "RADIO") {
                if (apz.getElmValue(elmObj.id) !== rejValues[i]) {
                    $(pObj).find('#' + elmObj.id + "_option_" + rejValues[i] + "_lbl_").addClass("sno");
                }
            }
        }
    },
    setUploadMapper: function (params) {
        let pObj = "";
        let elmObj = this.apz.scrMetaData.elmsMap[params.id];
        let dispName = elmObj.displayName;
        let keyVals = Object.keys(apz.scrMetaData.elmsMap);
        for (let i = 0; i < keyVals.length; i++) {
            let scrElmObj = apz.scrMetaData.elmsMap[keyVals[i]];
            if (scrElmObj.displayName == "ATT:" + dispName) {
                let fileId = params.id;
                let attachId = scrElmObj.id;
                apz.products.uploadMapperArray[fileId] = attachId;
            }
        }
    },
    Mapping: function (params) {
        //Mapped element changes are done here
        let rule = params.rule.split(",");
        let Referee = this.fnFetchValue(rule[0].trim());
        let valueToSet = rule[1].split("=");
        let valuesToCompare = valueToSet[1].split("||");
        let tempAssign = this.apz.scrDefsMap[this.apz.currAppId];
        for (var key in tempAssign) {
            if (tempAssign.hasOwnProperty(key)) {
                if (tempAssign[key].decorations && tempAssign[key].decorations.length > 0) {
                    for (let i = 0, len = tempAssign[key].decorations.length; i < len; i++) {
                        for (let k = 0; k < valuesToCompare.length; k++) {
                            if (valuesToCompare[k].split(".")[0].trim() == tempAssign[key].decorations[i].Name.trim()) {
                                let valuesArray = tempAssign[key].decorations[i].Value.split(":")[1].split(",");
                                if (valuesArray.indexOf(Referee) >= 0) {
                                    let lhs = valueToSet[0].trim().split(".");
                                    let bwindow = window;
                                    for (let j = 0; j < lhs.length; j++) {
                                        if (j == lhs.length - 1) {
                                            bwindow[lhs[j]] = tempAssign[key].decorations[i].Value.split(":")[0];
                                            this.apz.setElmValue(params.id, tempAssign[key].decorations[i].Value.split(":")[0]);
                                            this.processRule(document.getElementById(params.id));
                                        } else {
                                            bwindow = bwindow[lhs[j]];
                                        }
                                    }
                                } else {
                                    valuesToCompare.splice(k, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    },
    /**
     * Method to remove a particular value from a dropdown based on a condition
     */
    Rvalue: function (params) {
        let lastFound = params.rule.lastIndexOf('|');
        let Expression = params.rule.substr(0, lastFound).trim();
        let Value = params.rule.substr(lastFound + 1).trim();
        let elmId = params.id;
        let json = {
            "showAttr": Expression
        };
        let containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[params.id].container];
        if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
            elmId = params.id + "_" + params.rowNo;
        }
        if (this.apz.scrMetaData.elmsMap[params.id].type === "DROPDOWN" && document.getElementById(elmId) && apz.scrMetaData.uiInitsMap[params.id][6] === "SIMPLE") {
            let optionsArray = [];
            let presentOptions = $("#" + elmId).parent().find("ul").find("li");
            for (let i = 0; i < presentOptions.length; i++) {
                let tempObj = {};
                tempObj.val = $(presentOptions[i]).attr("data-value");
                tempObj.desc = $(presentOptions[i]).text();
                optionsArray.push(tempObj);
            }
            for (let k = 0, len = optionsArray.length; k < len; k++) {
                if (optionsArray[k] && optionsArray[k].val === Value) {
                    optionsArray.splice(k, 1);
                }
            }
            if (!this.RuleProcessor(json)) {
                optionsArray.push({
                    "val": Value,
                    "desc": Value
                });
            }
            this.apz.populateDropdown(document.getElementById(elmId), optionsArray);
        }
    },
    /**
     * Method to apply the madatory rule for an Attribute based on the condition provided.
     */
    CMan: function (params) {
        let ElmObj = this.apz.scrMetaData.elmsMap[params.id];
        let containerObj = this.apz.scrMetaData.containersMap[ElmObj.container];
        let json = {
            "showAttr": params.rule,
            "rowNo": params.rowNo
        };
        if (this.RuleProcessor(json)) {
            this.apz.scrMetaData.elmsMap[params.id].mand = "Y";
            if (containerObj.type === "FORM") {
                // Investors: handled for dropdown and radio
                if (ElmObj.type === "INPUTBOX" || ElmObj.type === "TEXTAREA" || ElmObj.type === "DROPDOWN" || ElmObj.type === "RADIO") {
                    // Changes by Siva and Karthik :Checking the values and appeding borderRed class for form
                    if (apz.isNull(apz.getElmValue(params.id))) {
                        $("#" + params.id).attr("required", "required");
                        $("#" + params.id).closest("ul").find("label").addClass("req");
                        $("#" + params.id).removeClass("borderGreen");
                        $("#" + params.id).addClass("borderRed");
                    } else {
                        $("#" + params.id).removeAttr("required");
                        $("#" + params.id).closest("ul").find("label").removeClass("req");
                        $("#" + params.id).addClass("borderGreen");
                        $("#" + params.id).removeClass("borderRed");
                    }
                }
            } else if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                // Investors: handled for dropdown and radio
                if (ElmObj.type === "INPUTBOX" || ElmObj.type === "TEXTAREA" || ElmObj.type === "DROPDOWN" || ElmObj.type === "RADIO") {
                    //changes by Siva and Karthik:Checking the values and appeding borderRed class for List
                    if (!apz.isNull(params.id) && $("#" + params.id).length > 0 && apz.isNull(apz.getElmValue(params.id))) {
                        $("#" + params.id + "_" + params.rowNo).attr("required", "required");
                        $("#" + params.id + "_" + params.rowNo).removeClass("borderGreen");
                        $("#" + params.id + "_" + params.rowNo).addClass("borderRed");
                    } else {
                        $("#" + params.id + "_" + params.rowNo).removeAttr("required");
                        $("#" + params.id + "_" + params.rowNo).addClass("borderGreen");
                        $("#" + params.id + "_" + params.rowNo).removeClass("borderRed");
                    }
                }
            }
        } else {
            this.apz.scrMetaData.elmsMap[params.id].mand = "N";
            if (containerObj.type === "FORM") {
                if (ElmObj.type === "INPUTBOX" || ElmObj.type === "TEXTAREA" || ElmObj.type === "DROPDOWN" || ElmObj.type === "RADIO") {
                    // Investors: handled for dropdown and radio
                    $("#" + params.id).removeAttr("required");
                    $("#" + params.id).closest("ul").find("label").removeClass("req");
                    $("#" + params.id).removeClass("borderGreen borderRed");
                }
            } else if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                if (ElmObj.type === "INPUTBOX" || ElmObj.type === "TEXTAREA" || ElmObj.type === "DROPDOWN" || ElmObj.type === "RADIO") {
                    // Investors: handled for dropdown and radio
                    $("#" + params.id + "_" + params.rowNo).removeAttr("required");
                    $("#" + params.id + "_" + params.rowNo).removeClass("borderGreen borderRed");
                }
            }
        }
    },
    /**
     * Method to handle the display of UI description/additional Info of an Attribute
     */
    UIDescp: function (params) {
        let rule = params.rule.split("|").splice(3).join("|");
        if (!this.apz.isNull(rule)) {
            let json = {
                showAttr: rule
            };
            let result = this.RuleProcessor(json);
            let ElmObj = this.apz.scrMetaData.elmsMap[params.id];
            let containerObj = this.apz.scrMetaData.containersMap[ElmObj.container];
            if (result) {
                if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                    this.apz.showElement(params.id + "_" + params.rowNo + "_uidesc");
                } else {
                    this.apz.showElement(params.id + "_uidesc");
                }
            } else {
                if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                    this.apz.hideElement(params.id + "_" + params.rowNo + "_uidesc");
                } else {
                    this.apz.hideElement(params.id + "_uidesc");
                }
            }
        }
    },
    /**
     * Method to process the expression in any rule.
     */
    RuleProcessor: function (params) {
        //This method will take the rule to be processed and returns a boolean
        let RuleToProcess = params.showAttr;
        if (RuleToProcess.indexOf("ShowAttr:") >= 0) {
            RuleToProcess = RuleToProcess.split("ShowAttr:")[1];
        }
        let prevRes = true;
        let result = true;
        let OperatorToCompare = '';
        let j = 0;
        while (j < RuleToProcess.length) {
            if (RuleToProcess.charAt(j) == "(") {
                let closingIndex = RuleToProcess.indexOf(')', j);
                let splitRule = RuleToProcess.substr(j + 1, closingIndex - j - 1);
                if (splitRule.indexOf("&&") >= 0 || splitRule.indexOf("||") >= 0) {
                    let json = {
                        "showAttr": splitRule,
                        "rowNo": params.rowNo,
                        "id": params.id
                    };
                    result = this.breakRule(json);
                    prevRes = this.operation(OperatorToCompare, prevRes, result);
                    RuleToProcess = RuleToProcess.slice(closingIndex + 1).trim();
                    j = 0;
                } else {
                    j = closingIndex;
                }
                continue;
            } else if (RuleToProcess.charAt(j) == "&" && RuleToProcess.charAt(j + 1) == "&") {
                if (RuleToProcess.substr(0, j - 1) == "") {
                    OperatorToCompare = "&&";
                    RuleToProcess = RuleToProcess.slice(2).trim();
                    j = 0;
                    continue;
                } else {
                    let json = {
                        "showAttr": RuleToProcess.substr(0, j),
                        "rowNo": params.rowNo
                    };
                    result = this.breakRule(json);
                    prevRes = this.operation(OperatorToCompare, prevRes, result);
                    RuleToProcess = RuleToProcess.slice(j).trim();
                    j = 0;
                    continue;
                }
            } else if (RuleToProcess.charAt(j) == "|" && RuleToProcess.charAt(j + 1) == "|") {
                if (RuleToProcess.substr(0, j - 1) == "") {
                    OperatorToCompare = "||";
                    RuleToProcess = RuleToProcess.slice(2).trim();
                    j = 0;
                    continue;
                } else {
                    let json = {
                        "showAttr": RuleToProcess.substr(0, j),
                        "rowNo": params.rowNo,
                        "id": params.id
                    };
                    result = this.breakRule(json);
                    prevRes = this.operation(OperatorToCompare, prevRes, result);
                    RuleToProcess = RuleToProcess.slice(j).trim();
                    j = 0;
                    continue;
                }
            } else {
                if (RuleToProcess.trim() != "" && RuleToProcess.indexOf("&&") < 0 && RuleToProcess.indexOf("||") < 0) {
                    let json = {
                        "showAttr": RuleToProcess,
                        "rowNo": params.rowNo,
                        "id": params.id
                    };
                    result = this.breakRule(json);
                    prevRes = this.operation(OperatorToCompare, prevRes, result);
                    RuleToProcess = "";
                }
            }
            j++;
        }
        return prevRes;
    },
    /**
     * This method is used to do the string manupulation and break the rule
     */
    breakRule: function (params) {
        /*Params expects rule as showAttr*/
        let showAttr = params.showAttr;
        let prevInd = 0;
        let prevRes = true;
        let operatorArray = new Array();
        let operationsArray = new Array();
        for (let j = 0; j < showAttr.length; j++) {
            if ((showAttr.charAt(j) == "&" && showAttr.charAt(j + 1) == "&") || (showAttr.charAt(j) == "|" && showAttr.charAt(j + 1) == "|")) {
                operationsArray.push(showAttr.substring(prevInd, j));
                operatorArray.push(showAttr.substring(j, j + 2));
                j = j + 2;
                prevInd = j;
            }
        }
        operationsArray.push(showAttr.substring(prevInd));
        let Individualoperations = new Array();
        operationsArray.forEach(function (element, index) {
            prevInd = 0;
            Individualoperations[index] = new Array();
            if (element.includes("isNull") || element.includes("isNotNull")) {
                if (element.indexOf("isNull") > 0) {
                    Individualoperations[index][0] = element.substring(0, element.indexOf("isNull"));
                    Individualoperations[index][2] = "null";
                    Individualoperations[index][1] = "=";
                } else if (element.indexOf("isNotNull") > 0) {
                    Individualoperations[index][0] = element.substring(0, element.indexOf("isNotNull"));
                    Individualoperations[index][2] = "null";
                    Individualoperations[index][1] = "!=";
                }
            } else {
                for (let k = 0; k < element.length; k++) {
                    if (element.charAt(k) == "!" && element.charAt(k + 1) == "=") {
                        Individualoperations[index][0] = element.substring(prevInd, k);
                        Individualoperations[index][2] = element.substring(k, k + 1);
                        Individualoperations[index][1] = element.substring(k + 2);
                        break;
                    } else if ((element.charAt(k) == ">" || element.charAt(k) == "<") && element.charAt(k + 1) !== "=") {
                        Individualoperations[index][0] = element.substring(prevInd, k);
                        Individualoperations[index][2] = element.substring(k, k + 1);
                        Individualoperations[index][1] = element.substring(k + 1);
                        break;
                    } else if ((element.charAt(k) == ">" || element.charAt(k) == "<") && element.charAt(k + 1) == "=") {
                        Individualoperations[index][0] = element.substring(prevInd, k);
                        // handling >= expression : investor
                        Individualoperations[index][2] = element.substring(k, k + 2);
                        Individualoperations[index][1] = element.substring(k + 2);
                        break;
                    } else if (element.charAt(k) == "=") {
                        Individualoperations[index][0] = element.substring(prevInd, k);
                        Individualoperations[index][2] = element.substring(k, k + 1);
                        Individualoperations[index][1] = element.substring(k + 1);
                        break;
                    }
                }
            }
        });
        for (let v = 0; v <= operatorArray.length; v++) {
            if (v == 0) {
                prevRes = this.operation(Individualoperations[v][2].trim(), this.fnFetchValue(Individualoperations[v][0].trim(), params.rowNo), Individualoperations[v][1].trim());
                if (prevRes == true) {
                    this.fetchSatisfiedCase(Individualoperations[v], params);
                }
            } else {
                let currRes = this.operation(Individualoperations[v][2], this.fnFetchValue(Individualoperations[v][0].trim(), params.rowNo), Individualoperations[v][1].trim());
                if (operatorArray[v - 1] == "||") {
                    if (v == 1) {
                        if (prevRes == true)
                            this.fetchSatisfiedCase(Individualoperations[v - 1], params);
                        if (currRes == true)
                            this.fetchSatisfiedCase(Individualoperations[v], params);
                    } else {
                        if (prevRes == true && currRes == true) {
                            this.fetchSatisfiedCase(Individualoperations[v - 1], params);
                        }
                    }
                }
                prevRes = this.operation(operatorArray[v - 1], prevRes, currRes);
            }
        }
        return prevRes;
    },
    fetchSatisfiedCase: function (opArray, json) {
        if (json.id) {
            if (!this.displayRuleMapper[json.id]) {
                this.displayRuleMapper[json.id] = {};
                this.displayRuleMapper[json.id]["ValidCases"] = [];
            }
            let satisfyRule = opArray[0] + opArray[2] + opArray[1];
            if (this.displayRuleMapper[json.id]["ValidCases"].indexOf(satisfyRule) == -1)
                this.displayRuleMapper[json.id]["ValidCases"].push(satisfyRule);
        }
    },
    /**
     * Supporting method to perform the Arithmetic operations while breaking a rule
     */
    processArithOptsInBreakRule: function (expr) {
        /* This method supports only one operator in an expression currently */
        let result = expr;
        if (expr.indexOf("+") >= 0 || expr.indexOf("-") >= 0 || expr.indexOf("*") >= 0 || expr.indexOf("/") >= 0 || expr.indexOf("%") >= 0) {
            let index = expr.indexOf("+") || expr.indexOf("-") || expr.indexOf("*") || expr.indexOf("/") || expr.indexOf("%");
            result = this.operation(expr.substr(index, 1), this.fnFetchValue(expr.substr(0, index - 1).trim()), this.fnFetchValue(expr.substr(index + 1).trim()));
        }
        return result;
    },
    /**
     * Method to fetch the value of an Attribute/constant to be replaced in the expression
     */
    fnFetchValue: function (element, rowNo) {
        let result = element;
        switch (element) {
            case "apz.products.Region":
                result = this.Region;
                break;
            case "apz.products.Pname":
                result = this.Pname;
                break;
            case "apz.products.RPID":
                result = this.RPID;
                break;
            case "apz.products.PrdID":
                result = this.PrdID;
                break;
            case "apz.products.PPID":
                result = this.PPID;
                break;
            default:
                if (element.charAt(0) === "&") {
                    element = element.substr(1);
                }
                if (element.charAt(0) === ".") {
                    element = element.substr(1);
                }
                if (element.charAt(0) === ":") {
                    element = element.substr(1);
                }
                element = element.trim();
                if (!isNaN(parseFloat(element))) {
                    result = parseFloat(element);
                } else if (!this.apz.isNull(document.getElementById(element))) {
                    result = this.apz.getElmValue(element);
                } else if (!this.apz.isNull(rowNo) && !this.apz.isNull(document.getElementById(element + "_" + rowNo))) {
                    result = this.apz.getElmValue(element + "_" + rowNo);
                } else if (element in this) {
                    result = this[element];
                }
                break;
        }
        return result;
    },
    /**
     * Method to perform the operations present in an expression, Mostly returns a Boolean.
     */
    operation: function (operator, val1, val2) {
        let result;
        if (isNaN(Number(val2))) {
            val1 = val1.toLowerCase();
            val2 = val2.toLowerCase();
        }
        switch (operator) {
            case "":
                result = val2;
                break;
            case "=":
                result = val1 == val2;
                break;
            case "!":
                result = val1 != val2;
                break;
            case "!=":
                result = val1 != val2;
                break;
            case "&&":
                result = val1 && val2;
                break;
            case "||":
                result = val1 || val2;
                break;
            case ">":
                result = parseFloat(val1) > parseFloat(val2);
                break;
            case "<":
                result = parseFloat(val1) < parseFloat(val2);
                break;
            case ">=":
                result = parseFloat(val1) >= parseFloat(val2);
                break;
            case "<=":
                result = parseFloat(val1) <= parseFloat(val2);
                break;
            case "+":
                result = parseFloat(val1) + parseFloat(val2);
                break;
            case "-":
                result = parseFloat(val1) - parseFloat(val2);
                break;
            case "*":
                result = parseFloat(val1) * parseFloat(val2);
                break;
            case "/":
                result = parseFloat(val1) / parseFloat(val2);
                break;
            case "%":
                result = parseFloat(val1) % parseFloat(val2);
                break;
            case "null":
                if (val2 == "=") {
                    result = this.apz.isNull(val1);
                } else if (val2 == "!=") {
                    result = !this.apz.isNull(val1);
                }
                break
            default:
                result = true;
        }
        return result;
    },
    rejectedValue: function (params) {
        /* params expect appId, elementData object */
        let result = true;
        /* Clearing the previous error classes */
        $(".err").removeClass("err");
        /* If the element data from elements map is passed */
        if (params.elementData) {
            if (this.apz.getElmValue(params.elementData.id) == params.elementData.attributeRejectedValue.DisplayValue) {
                //Behavior is to be decided to perform on the element passed the case
                this.apz.val.addClass($("#" + params.elementData.id));
                result = false;
            }
        } else {
            let rejectedElems = new Array();
            /* Fetching all the elements in the screen */
            let elms = this.apz.scrMetaData.elmsMap;
            for (let key in elms) {
                if (elms[key].attributeRejectedValue) {
                    if (this.apz.getElmValue(elms[key].id) == elms[key].attributeRejectedValue.DisplayValue) {
                        //Behavior is to be decided to perform on the elements 
                        rejectedElems.push(elms[key].id);
                        this.apz.val.addClass($("#" + elms[key].id));
                        result = false;
                    }
                }
            }
        }
        return result;
    },
    /**
     * Method to handle the conatiner visibility based on the Attributes present inside the container.
     */
    checkContainerVisibility: function () {
        let scrContainers = apz.scrMetaData.containers;
        for (let i = 0, len = scrContainers.length; i < len; i++) {
            if (!this.apz.scrMetaData.containersMap[scrContainers[i].id].hasDisplayRule) {
                $("#" + scrContainers[i].id).removeClass("sno");
                $("#" + scrContainers[i].id).parent().parent().removeClass("sno");
            }
            this.hideCntr(scrContainers[i]);
        }
    },
    /* relationship with default product as sub product  */
    checkContainerVisibilityofScreen: function (scrName) {
        if (apz.scrDefsMap[apz.currAppId][scrName + "_Scr__NewLayout__D0"] && apz.scrDefsMap[apz.currAppId][scrName + "_Scr__NewLayout__D0"].containers) {
            let scrContainers = apz.scrDefsMap[apz.currAppId][scrName + "_Scr__NewLayout__D0"].containers;
            for (let i = 0, len = scrContainers.length; i < len; i++) {
                $("#" + scrContainers[i].id).removeClass("sno");
                if (this.apz.scrMetaData.containersMap[scrContainers[i].id] && !this.apz.scrMetaData.containersMap[scrContainers[i].id].hasDisplayRule) {
                    $("#" + scrContainers[i].id).parent().parent().removeClass("sno");
                }
                this.hideCntr(scrContainers[i]);
            }
        }
    },
    hideCntr: function (screenContainer) {
        let elementsHidden = true;
        for (let key in screenContainer.elmsMap) {
            if (screenContainer.elmsMap.hasOwnProperty(key)) {
                if (this.apz.scrMetaData.containersMap[screenContainer.id].multiRec == "Y")
                    key = key + "_0";
                if (!$("#" + key).is(":hidden")) {
                    elementsHidden = false;
                }
            }
        }
        if (elementsHidden) {
            $("#" + screenContainer.id).addClass("sno");
        }
    },
    resetRules: function (params) {
        if (params.key === "Pname") {
            this.fnResetPname();
        } else if (params.key === "RPID") {
            this.fnResetRPID();
        } else if (params.key === "PPID") {
            this.fnResetPPID();
        }
    },
    fnResetPname: function (params) {
        this.Pname = "";
    },
    fnResetRPID: function (params) {
        this.RPID = "";
    },
    fnResetPPID: function (params) {
        this.PPID = "";
    },
    /**
     * Method to call the API and set the values of Attributes as soon as the product is launched.
     */
    processRootAPI: function (params) {
        if (!this.apz.isNull(this.apz.scrDefsMap[this.apz.currAppId][params.screenDets]) && this.apz.scrDefsMap[this.apz.currAppId][params.screenDets].decorations) {
            let decorations = this.apz.scrDefsMap[this.apz.currAppId][params.screenDets].decorations;
            if (!this.apz.isNull(decorations)) {
                let json = {
                    typeOfAPI: "root"
                };
                for (let i = 0, ruleLen = decorations.length; i < ruleLen; i++) {
                    let rule = decorations[i].Name.split("_");
                    if (rule[2] === "API") {
                        json.rule = decorations[i].Value;
                    } else if (rule[2] === "APIOutputMap") {
                        json.outputMaps = decorations[i].Value;
                    }
                }
                if (!this.apz.isNull(json.rule)) {
                    this.callAPI(json);
                }
            }
        }
    },
    /**
     * Method to call an API everytime the Attribute's rules are processed.
     */
    callElementAPI: function (params) {
        params.typeOfAPI = "element";
        this.callAPI(params);
    },
    /**
     * Method to call an API and set an Attribute's value returned from the API.
     */
    fetchLinkedItem: function (params) {
        let json = {
            "typeOfAPI": "LinkedItem",
            "elmId": params.id,
            "rule": params.rule
        };
        this.callAPI(json);
    },
    /**
     * Starting point of a Dynamic LOV, This method prepares the details required to call the API and to prepare the LOV.
     */
    createDynamicLOV: function (obj, event) {
        let elmId = obj.id.indexOf("_button");
        elmId = obj.id.substr(0, elmId);
        let rowNo = this.apz.getObjRowNumber(document.getElementById(elmId));
        elmId = this.apz.getObjIdWORowNumber(document.getElementById(elmId));
        let ElmDecorations = this.apz.scrMetaData.elmsMap[elmId].decorations;
        let json = {
            "typeOfAPI": "DLOV",
            "elmId": obj.id,
            "outputMap": [],
            "modalHeader": "",
            "rowNo": rowNo
        };
        for (let j = 0, len = ElmDecorations.length; j < len; j++) {
            let rule = ElmDecorations[j].name.split("_");
            if (rule[2].startsWith("APIOutputMap")) {
                json.outputMap.push(ElmDecorations[j].value);
            } else if (rule[2] === "API") {
                json.rule = ElmDecorations[j].value;
                let separatedRule = ElmDecorations[j].value.split("|");
                json.modalHeader = separatedRule[separatedRule.length - 1];
            }
        }
        //pickapplet changes : investors
        if (this.apz.isFunction(apz.app.customDlovMethod)) {
            apz.app.customDlovMethod(json);
        } else {
            this.callAPI(json);
        }
    },
    /**
     * Method to prepare the payload of an API call and to do an ajax.
     */
    callAPI: function (params) {
        let APIDets = params.rule;
        params.async = false;
        let reqObj = params.rule.split("|");
        params.url = this.urlConfigMap[reqObj[0].trim()];
        let citiAPIObj = {};
        for (let k = 0, len = reqObj.length; k < len; k++) {
            if (reqObj[k].indexOf(":") > 0) {
                citiAPIObj[reqObj[k].split(":")[0].trim()] = reqObj[k].split(":")[1].trim();
            }
        }
        for (let key in citiAPIObj) {
            citiAPIObj[key] = this.replaceAPIElements(citiAPIObj[key], params.rowNo, params.typeOfAPI);
        }
        params.req = citiAPIObj;
        if (this.PGroup == "INVESTORS") {
            params.req = this.formReq(params);
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preAPICall)) {
            this.apz[this.apz.currAppId].preAPICall(params);
        }
        let reqStr = JSON.stringify(params.req);
        let myObj = this;
        if (params.url) {
            $.ajax({
                url: params.url,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('access_token', apz.products.accessToken);
                    ///// Passing dealId in API request 26 March 2020
                    if (apz.products.dealId) {
                        xhr.setRequestHeader('dealId', apz.products.dealId);
                    }
                },
                type: "POST",
                cache: false,
                data: reqStr,
                contentType: 'application/json',
                dataType: 'json',
                async: params.async,
                success: function (res) {
                    params.status = true;
                    params.resFull = res;
                    if (myObj.apz.isFunction(myObj.apz.app.postRequestCallSuccess)) {
                        myObj.apz.app.postRequestCallSuccess(params);
                    }
                    myObj.receiveRes(params);
                },
                error: function (error) {
                    params.status = false;
                    params.error = error;
                    if (myObj.apz.isFunction(myObj.apz.app.postRequestCallFailure)) {
                        myObj.apz.app.postRequestCallFailure(params);
                    }
                    myObj.receiveRes(params);
                }
            });
        }
        // Investors: added post hook
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postcallAPI)) {
            status = this.apz[this.apz.currAppId].postcallAPI();
        }
    },
    /**
     * Method to replace the values of the variables present in an API rule
     */
    replaceAPIElements: function (SearchExpr, rowNo, APIType) {
        let s = 0;
        if (SearchExpr[0] === "&") {
            let elm = SearchExpr.substr(s + 1).trim();
            SearchExpr = this.fnFetchValue(elm);
        } else {
            while (s < SearchExpr.length) {
                if (SearchExpr[s] === "&") {
                    let startIndex = s - 2;
                    let keyToFindTheEnd = "]";
                    let EndIndex = SearchExpr.indexOf(keyToFindTheEnd, s);
                    let DOMElm = SearchExpr.substr(s + 1, EndIndex - s - 2);
                    if (apz.scrMetaData.elmsMap[DOMElm] && apz.scrMetaData.containersMap[apz.scrMetaData.elmsMap[DOMElm].container]) {
                        let containerObj = apz.scrMetaData.containersMap[apz.scrMetaData.elmsMap[DOMElm].container];
                        if (containerObj.type == "LIST" || containerObj.type === "TABLE") {
                            DOMElm = DOMElm + "_" + rowNo;
                        }
                    }
                    SearchExpr = SearchExpr.replace(SearchExpr.substr(startIndex, EndIndex - startIndex + 1), this.fnFetchValue(DOMElm));
                    s = EndIndex;
                } else {
                    s++;
                }
            }
        }
        if (apz.getDataType(SearchExpr) === "STRING" && APIType !== "LinkedItem") {
            SearchExpr = SearchExpr.replace(/,/g, "|");
        }
        return SearchExpr;
    },
    receiveRes: function (params) {
        if (params.status) {
            if (params.typeOfAPI == "root") {
                let outputMaps = params.outputMaps;
                outputMaps = outputMaps.split("|");
                for (let i = 0; i < outputMaps.length; i++) {
                    if (document.getElementById(outputMaps[i].split(",")[0].trim())) {
                        apz.setElmValue(outputMaps[i].split(",")[0].trim(), params.resFull[outputMaps[i].split(",")[1].trim()]);
                    }
                }
            } else if (params.typeOfAPI == "element") {
                let ElmDecorations = this.apz.scrMetaData.elmsMap[params.id].decorations;
                for (let j = 0, len = ElmDecorations.length; j < len; j++) {
                    let rule = ElmDecorations[j].name.split("_");
                    if (rule[2].startsWith("APIOutputMap")) {
                        let outputMaps = ElmDecorations[j].value;
                        outputMaps = outputMaps.split("|");
                        for (let i = 0; i < outputMaps.length; i++) {
                            if (document.getElementById(outputMaps[i].split(",")[0].trim())) {
                                apz.setElmValue(outputMaps[i].split(",")[0].trim(), params.resFull[outputMaps[i].split(",")[1].trim()]);
                            }
                        }
                    }
                }
            } else if (params.typeOfAPI == "DLOV") {
                this.prepareDynamicLOV(params);
            } else if (params.typeOfAPI == "BlankQuery") {
                this.initAutoComplete(params);
            } else if (params.typeOfAPI == "LinkedItem") {
                this.loadLinkedItemData(params);
            }
        } else {
            //API call failed to respond properly
        }
        if (this.apz.isFunction(params.callBack)) {
            if (params.callBackObj) {
                params.callBack.call(params.callBackObj, params);
            } else {
                params.callBack(params);
            }
        }
    },
    /**
     * Method to set the value of Attributes from the LinkedItem Response.
     */
    loadLinkedItemData: function (params) {
        let currOutPutMap = params.rule.replace(/&nbs&/g, " ").split("|");
        let domElm = currOutPutMap[1].trim().split(",");
        if (domElm.length > 1) {
            let values = params.resFull.Value.split(",");
            for (let ind = 0; ind < domElm.length; ind++) {
                let currdomElm = domElm[ind].replace(/[\[\]&]/g, "").trim();
                let value = values[ind];
                this.setLinkedItemValue(currdomElm, value);
            }
        } else {
            domElm = domElm[0];
            if (!this.apz.isNull(params.resFull)) {
                this.setLinkedItemValue(domElm, params.resFull.Value);
            }
        }
    },
    /**
     * Method to set the value of attributes by checking the container
     */
    setLinkedItemValue: function (domElm, value) {
        let elmObj = this.apz.scrMetaData.elmsMap[domElm];
        if (elmObj && this.apz.scrMetaData.containersMap[elmObj.container]) {
            let containerObj = this.apz.scrMetaData.containersMap[elmObj.container];
            if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                for (let j = 0; j < containerObj.totalRecs; j++) {
                    domElm = domElm + "_" + j;
                    if (document.getElementById(domElm)) {
                        apz.setElmValue(domElm, value);
                    }
                }
            } else {
                if (document.getElementById(domElm)) {
                    apz.setElmValue(domElm, value);
                }
            }
        }
    },
    debounce: function (func, wait, context, immediate) {
        var timeout;
        return function () {
            var context = apz.products,
                args = arguments;
            var later = function () {
                timeout = null;
                if (!immediate) func.apply(context, args);
            };
            var callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) func.apply(context, args);
        };
    },
    /**
     * Method to prepare the details required for an IPS/autocomplete Attribute
     */
    elasticSearch: function (obj) {
        this.elasticSearchLov = this.debounce(function () {
            let obj = arguments[0];
            if (this.isNull(this.autocompleteRespArr[obj.id])) {
                let elmId = obj.id;
                let rowNo = this.apz.getObjRowNumber(document.getElementById(elmId));
                elmId = this.apz.getObjIdWORowNumber(document.getElementById(elmId));
                let ElmDecorations = this.apz.scrMetaData.elmsMap[elmId].decorations;
                let inpValue = $("#" + obj.id).val();
                this.SearchText = inpValue;
                this.SOEID = apz[apz.appId].soeid;
                let json = {
                    "typeOfAPI": "AutoComplete",
                    "elmId": obj.id,
                    "outputMap": [],
                    "rowNo": rowNo,
                    // Investors: added one more parameter
                    "searchText": this.SearchText
                };
                for (let j = 0, len = ElmDecorations.length; j < len; j++) {
                    let rule = ElmDecorations[j].name.split("_");
                    if (rule[2].startsWith("APIOutputMap")) {
                        json.outputMap.push(ElmDecorations[j].value);
                    } else if (rule[2].startsWith("PickAppletType")) {
                        json.typeOfAPI = ElmDecorations[j].value;
                    } else if (rule[2].startsWith("NumberOfRec")) {
                        this.NumOfRecToDisp = ElmDecorations[j].value;
                    } else if (rule[2].startsWith("StartNumber")) {
                        this.StartNum = ElmDecorations[j].value;
                    }
                    // Investors: New grammar DefaultQuery starts
                    else if (rule[2].startsWith("DefaultQuery")) {
                        this.DefaultQuery = ElmDecorations[j].value;
                    }
                    // Investors: New grammar DefaultQuery ends
                    else if (rule[2] === "API") {
                        json.rule = ElmDecorations[j].value;
                        let separatedRule = ElmDecorations[j].value.split("|");
                        json.modalHeader = separatedRule[separatedRule.length - 2];
                    } else if (rule[2] === "ColShow") {
                        this.showColArray[json.elmId] = ElmDecorations[j].value;
                    }
                }
                this.callAPI(json);
            } else {
                let params = {};
                params = this.autocompleteRespArr[obj.id];
                this.initAutoComplete(params);
            }
        }, 100);
    },
    /**
     * Method which creates an LOV dynamically based on the response of the API.
     */
    prepareDynamicLOV: function (params) {
        /* addtion of prePrepareDynamicLOV method  */
        let status = true;
        if (this.apz.isFunction(this.apz.app.prePrepareDynamicLOV)) {
            status = this.apz.app.prePrepareDynamicLOV(params);
        }
        if (status) {
            if (params.resFull && (this.apz.getDataType(params.resFull) == "Object" || params.resFull.length == 1)) {
                if (this.apz.getDataType(params.resFull) == "Array") {
                    params.resFull = params.resFull[0];
                }
                this.setSingleDlovValues(params);
            } else {
                if (!document.getElementById("lovModal")) {
                    $("body").append('<div id="lovModal"> </div>');
                }
                $("#lovModal").empty().css("display", "block");
                //Header for the modal must be  taken from API
                let FinalOutPutMap = params.outputMap.join("***").replace(/ /g, "&nbs&");
                $("#lovModal").append(
                    '<div id="citi_DLOV_Modal" class="modal fade srb in" role="dialog" aria-labelledby="citi_DLOV_ModalLabel" aria-hidden="false" style="display: block; padding-right: 12px;"><div id="citi_DLOV_Modal_window" class="modal-window gcb-col10" role="document"><ul class="modal-header"><li><h1>' +
                    params.modalHeader +
                    '</h1></li><li><button id="citi_DLOV_Modal_close" class="close ett-bttn tsp med" type="button" data-dismiss="modal" aria-label="Close"><svg aria-hidden="true" class="ett-icon icon-remove px24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-remove"></use></svg></button></li></ul><div id="citi_DLOV_Modal_content" class="modal-cnt"><div id="citi_DLOV__gr_row_2" class="grb"><div id="citi_DLOV__gr_col_2" class=" gcb-col12"><div id="citi_DLOV__pl_pnl_2_div" class="plt-simp "><div id="citi_DLOV__ps_pls_2" class="pst-simp pri modalDataTable"></div></div></div></div></div><ul class="modal-footer" style="display:flex;flex-direction:row-reverse;"><li><button id="citi_DLOV__el_btn_7" type="button" class="ett-bttn pri med" enabled="enabled" onclick=apz.products.setDLOVFields("' +
                    FinalOutPutMap + '","' + params.rowNo +
                    '",event)><span id="citi_DLOV__el_btn_7_txtcnt">Pick</span></button><button id="citi_DLOV__el_btn_8" type="button" class="ett-bttn pri med inf" onclick=apz.toggleModal({"targetId":"citi_DLOV_Modal"}) enabled="enabled"><span id="citi_DLOV__el_btn_8_txtcnt">Close</span></button></li></ul></div><div class="modal-backdrop fade in" tabindex="0"></div><div class="modal-backdrop fade in" tabindex="0"></div></div>'
                );
                $("#citi_DLOV__ps_pls_2").append(
                    '<table id="citi_DLOV__table" class="tabl responsive wborder ro dtr-inline modalTable" width="100%"></table>');
                let columns = params.rule.split("|");
                columns = columns[columns.length - 2];
                columns = columns.split(":")[1];
                columns = columns.split(",");
                let columnsData = [];
                for (let k = 0; k < columns.length; k++) {
                    columnsData[k] = {
                        "title": columns[k].trim()
                    }
                }
                let outDataColumns = params.rule.split("|");
                outDataColumns = outDataColumns[outDataColumns.length - 3];
                outDataColumns = outDataColumns.split(":")[1];
                outDataColumns = outDataColumns.split(",");
                let dataSet = [];
                if (params.resFull) {
                    if (this.apz.getDataType(params.resFull) == "Array") {
                        for (let a = 0; a < params.resFull.length; a++) {
                            dataSet[a] = [];
                            for (let y = 0; y < outDataColumns.length; y++) {
                                if (!this.apz.isNull(params.resFull[a][columns[y]])) {
                                    dataSet[a].push('<span style=\"display:none\">' + outDataColumns[y] + '_apz_</span>' + params.resFull[a][columns[
                                        y]]);
                                } else {
                                    dataSet[a].push('<span style=\"display:none\">' + outDataColumns[y] + '_apz_</span>' + "");
                                }
                            }
                        }
                    } else if (this.apz.getDataType(params.resFull) == "Object") {
                        dataSet[0] = [];
                        for (let y = 0; y < outDataColumns.length; y++) {
                            if (!this.apz.isNull(params.resFull[columns[y]])) {
                                dataSet[0].push('<span style=\"display:none\">' + outDataColumns[y] + '_apz_</span>' + params.resFull[columns[y]]);
                            } else {
                                dataSet[0].push('<span style=\"display:none\">' + outDataColumns[y] + '_apz_</span>' + "");
                            }
                        }
                    }
                } else {
                    dataSet[0] = ["No  Records Found!"];
                }
                let table = $('#citi_DLOV__table').DataTable({
                    data: dataSet,
                    info: false,
                    pagingType: "input",
                    bLengthChange: false,
                    columns: columnsData,
                    "fnDrawCallback": function (tableSettings) {
                        $("#citi_DLOV__table tbody tr").each(function () {
                            $(this).off('click').on("click", function () {
                                $(".ui-state-select").removeClass("ui-state-select");
                                $(this).addClass("ui-state-select");
                            });
                        });
                    }
                });
                $("#citi_DLOV__table tbody tr").each(function () {
                    $(this).click(function () {
                        $(".ui-state-select").removeClass("ui-state-select");
                        $(this).addClass("ui-state-select");
                    });
                });
                apz.toggleModal({
                    "targetId": "citi_DLOV_Modal"
                });
            }
            if (this.apz.isFunction(this.apz.app.postDLOVCreation)) {
                this.apz.app.postDLOVCreation(params);
            }
        }
    },
    /**
     * Method used to initialize the autocomplete for IPS Attribute
     */
    initAutoComplete: function (params) {

        /// Comment for 37973 
        ////if (params.resFull && (this.apz.getDataType(params.resFull) == "Object" || params.resFull.length == 1)) {
        if (params.resFull && (this.apz.getDataType(params.resFull) == "Object")) {
            if (this.apz.getDataType(params.resFull) == "Array") {
                params.resFull = params.resFull[0];
            }
            /// Comment for 37973        
            //this.setSingleDlovValues(params);
            //// } else {
        }
        let lovArray = [];
        let res = params.resFull;
        //helper function for applications to tweak the response//
        if (this.apz.isFunction(apz.app.alterResponse)) {
            res = apz.app.alterResponse(params.resFull);
        }
        //if the application fails to return, use existing//
        if (this.isNull(res)) {
            res = params.resFull;
        }
        this.sendParams = params;
        let lovDescription = "",
            lovValue = "",
            loCurrentValue = "";
        $.map(res, function (item) {
            lovDescription = "", loCurrentValue = "";
            let elmId = apz.products.sendParams.elmId;
            for (let keys in item) {
                if (!this.apz.isNull(item[keys]) && item[keys] !== "null") {
                    if (!this.apz.products.isNull(this.apz.products.showColArray)) {
                        if (this.apz.products.showColArray[elmId] == keys) {
                            lovDescription = lovDescription + item[keys];
                        }
                    } else {
                        lovDescription = lovDescription + item[keys] + " - ";
                    }
                }
                loCurrentValue = this.apz.isNull(loCurrentValue) ? keys + ":" + item[keys] : loCurrentValue + "," + keys + ":" + item[keys];
            }
            lovArray.push({ label: lovDescription, value: loCurrentValue });
        });
        // Investors: DefaultQuery flag code starts here
        if (this.PGroup == "INVESTORS" && (this.DefaultQuery == undefined || this.DefaultQuery == "Y") && this.isNull(this.autocompleteRespArr[
            params.elmId])) {
            this.autocompleteRespArr[params.elmId] = params;
        }
        var myObj = this;
        let minLength = 1;
        if (myObj.PGroup == "INVESTORS") {
            if (this.DefaultQuery == undefined || this.DefaultQuery == "Y") {
                minLength = 0;
            } else {
                minLength = 1;
            }
        }
        // Investors: DefaultQuery flag code starts here
        var options = {
            source: lovArray,
            // Investors: minLength
            minLength: minLength,
            select: function (event, ui) {
                let finalOutputArg = params.outputMap.join("***").replace(/ /g, "&nbs&");
                myObj.setAutoCompleteFields(finalOutputArg, params.rowNo, ui.item.value);
                event.preventDefault();
            },
            messages: {
                noResults: '',
                results: function () { }
            },
            search: function (event, ui) {
                //shift+tab issue fix--avoiding search after delay//
                if (event.which == 16)
                    event.preventDefault();
            },
            response: function (event, ui) {
                if (ui.content.length == 1) {
                    let lproceed = true;
                    //Single element default selection for investors should happen based on rule.//
                    if (apz.products.PGroup == "INVESTORS") {
                        let decorLen = apz.scrMetaData.elmsMap[apz.getObjIdWORowNumber(this)].decorations.length;
                        for (let i = 0; i < decorLen; i++) {
                            let decorName = apz.scrMetaData.elmsMap[apz.getObjIdWORowNumber(this)].decorations[i].name.split("_")[2];
                            if (decorName.indexOf("DEFSEL") > -1) {
                                let decorVal = apz.scrMetaData.elmsMap[apz.getObjIdWORowNumber(this)].decorations[i].value;
                                if (decorVal != "Yes") {
                                    lproceed = false;
                                }
                            }
                        }
                    }
                    if (lproceed) {
                        ui.item = ui.content[0];
                        $(this).data('ui-autocomplete')._trigger('select', 'autocompleteselect', ui);
                        $(this).autocomplete('close');
                    }
                }
            },
            focus: function (event, ui) {
                //to avoid setting value of menu to i/p box
                event.preventDefault();
            },
            // Investors: to avoid setting value of menu to i/p box starts
            close: function (event, ui) {
                /// Comment for 37973 - console issue, function not defined as this is not containing window object;
                if (apz.isFunction(apz.app.onCloseAutoCompleteFields)) {
                    apz.app.onCloseAutoCompleteFields(event, ui);
                }
            }
        };
        if ($("#" + params.elmId).attr("autocomplete")) {
            // $("#" + params.elmId).autocomplete(options, "source", lovArray);4132058
            if (this.PGroup == "INVESTORS") {
                $("#" + params.elmId).autocomplete(options, "source", lovArray).off('focus').on('focus', function () {
                    if ($(this).autocomplete("widget").is(":visible")) {
                        return;
                    }
                    if (this.DefaultQuery == undefined || this.DefaultQuery == "Y") {
                        $(this).data("ui-autocomplete").search($(this).val());
                    }
                });
                $("#" + params.elmId).focus();
            } else {
                $("#" + params.elmId).autocomplete(options, "source", lovArray);
            }
        } else {
            $("#" + params.elmId).autocomplete(options);
            if (this.PGroup == "INVESTORS") {
                let elmIdWORowNum = this.apz.getObjIdWORowNumber(document.getElementById(params.elmId));
                let cntrObj = apz.scrMetaData.containersMap[apz.scrMetaData.elmsMap[elmIdWORowNum].container];
                if (cntrObj.multiRec == "Y") {
                    for (let r = 0; r < cntrObj.totalRecs - 1; r++) {
                        $("#" + elmIdWORowNum + "_" + r).autocomplete(options);
                        let paramObj = apz.copyJSONObject(params);
                        paramObj.elmId = elmIdWORowNum + "_" + r;
                        paramObj.rowNo = r;
                        if (this.isNull(this.autocompleteRespArr[paramObj.elmId])) {
                            this.autocompleteRespArr[paramObj.elmId] = paramObj;
                            this.showColArray[paramObj.elmId] = this.showColArray[params.elmId];
                        }
                    }
                }
            }
        }
        /// Comment for 37973 
        //}
    },
    /**
     * Method to set the APIOutputMap if the response contains only one record
     */
    setSingleDlovValues: function (params) {
        let outHeaderMap = {};
        // Investors: since req format changed we modified this method starts.
        if (this.PGroup == "INVESTORS") {
            var outKeys = params.req.outputs.fieldName;
            var headerKeys = params.modalHeader.split("Header:")[1].split(",");
        } else {
            var outKeys = params.req.out.split("|");
            var headerKeys = params.req.Header.split("|");
        }
        // Investors: since req format changed we modified this method ends.
        for (let k = 0; k < outKeys.length; k++) {
            outHeaderMap[outKeys[k]] = headerKeys[k];
        }
        let outputMap = params.outputMap;
        for (let i = 0; i < outputMap.length; i++) {
            let currOutPutMap = outputMap[i].split("|");
            for (let j = 0; j < currOutPutMap.length; j++) {
                let domElm = currOutPutMap[j].split(",")[0];
                let refField = currOutPutMap[j].split(",")[1].trim();
                let elmObj = this.apz.scrMetaData.elmsMap[domElm];
                let containerObj = this.apz.scrMetaData.containersMap[elmObj.container];
                if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                    domElm = domElm + "_" + params.rowNo;
                }
                if (document.getElementById(domElm) && !this.apz.isNull(params.resFull[outHeaderMap[refField]])) {
                    apz.setElmValue(domElm, params.resFull[outHeaderMap[refField]]);
                }
            }
        }
    },
    /**
     * Method used to set the value to the Attributes based on the row selected in Pickapplet.
     */
    setDLOVFields: function (params, rowNo, event) {
        if ($("#citi_DLOV__table tbody tr.ui-state-select").length === 1) {
            let selectedRowObject = {};
            $("#citi_DLOV__table tbody tr.ui-state-select").find("td").each(function () {
                let text = $(this).text().split("_apz_");
                selectedRowObject[text[0].trim()] = text[1].trim();
            });
            let outputMap = params.split("***");
            for (let i = 0; i < outputMap.length; i++) {
                let currOutPutMap = outputMap[i].replace(/&nbs&/g, " ").split("|");
                for (let j = 0; j < currOutPutMap.length; j++) {
                    let domElm = currOutPutMap[j].split(",")[0];
                    let refField = currOutPutMap[j].split(",")[1].trim();
                    this.setElmDLOVFields(domElm, selectedRowObject, refField, rowNo);
                }
            }
            apz.toggleModal({
                "targetId": "citi_DLOV_Modal"
            });
            if (this.apz.isFunction(this.apz.app.postSetDLOVFields)) {
                this.apz.app.postSetDLOVFields(params, rowNo);
            }
        } else {
            this.apz.dispMsg({
                "message": "Please select a record",
                "type": "I"
            });
        }
    },
    /**
     * Method to set the values from the pickapplet based on the container.
     */
    setElmDLOVFields: function (domElm, selectedRowObject, refField, rowNo) {
        let status = true;
        if (this.apz.isFunction(this.apz.app.preSetElmDLOVFields)) {
            status = this.apz.app.preSetElmDLOVFields(domElm, selectedRowObject, refField, rowNo);
        }
        if (status) {
            let elmObj = this.apz.scrMetaData.elmsMap[domElm];
            let containerObj = this.apz.scrMetaData.containersMap[elmObj.container];
            if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                domElm = domElm + "_" + rowNo;
            }
            if (document.getElementById(domElm) && !this.apz.isNull(selectedRowObject[refField])) {
                apz.setElmValue(domElm, selectedRowObject[refField]);
            }
        }
        if (this.apz.isFunction(this.apz.app.postSetElmDLOVFields)) {
            this.apz.app.postSetElmDLOVFields(domElm, selectedRowObject, refField, rowNo);
        }
    },
    /**
     * Method the set the Value to the Attributes based on the selection from dropdown
     */
    setAutoCompleteFields: function (EleoutputMap, rowNo, value, params) {
        let selectedRowObject = {};
        let values = value.split(",");
        //value expectes a "key:value,key:value"--if value has , it should not be splitted//
        for (let i = 1, len = values.length; i < len; i++) {
            if (values[i] && values[i].indexOf(":") == -1) {
                values[i - 1] = values[i - 1].concat("," + values[i]);
                values.splice(i, 1);
                i--;
            }
        }
        for (let k = 0, len = values.length; k < len; k++) {
            if (values[k].indexOf(":") > 0) {
                selectedRowObject[values[k].split(":")[0].trim()] = values[k].split(":")[1].trim();
            }
        }
        let outputMaps = EleoutputMap.split("***");
        for (let i = 0; i < outputMaps.length; i++) {
            let currOutPutMap = outputMaps[i].replace(/&nbs&/g, " ").split("|");
            for (let j = 0; j < currOutPutMap.length; j++) {
                let domElm = currOutPutMap[j].split(",")[0];
                let refField = currOutPutMap[j].split(",")[1].trim();
                let elmObj = this.apz.scrMetaData.elmsMap[domElm];
                let containerObj = this.apz.scrMetaData.containersMap[elmObj.container];
                if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                    domElm = domElm + "_" + rowNo;
                }
                if (document.getElementById(domElm) && !this.apz.isNull(selectedRowObject[refField])) {
                    apz.setElmValue(domElm, selectedRowObject[refField]);
                    this.fnValidateScreenElements();
                }
            }
        }
    },
    /**
     * Method to get the region in which the passed country belongs
     */
    getRegionFromCountry: function (cntr) {
        let region = "";
        for (let key in this.countryList) {
            if (this.countryList.hasOwnProperty(key)) {
                if (this.countryList[key].indexOf(cntr) >= 0) {
                    region = key;
                }
            }
        }
        return region;
    },
    /**
     * Method to check the mandatory elements in the screen.
     */
    fnValidateScreenElements: function () {
        var validationResult = true;
        for (var key in this.apz.scrMetaData.elmsMap) {
            var containerObj = this.apz.scrMetaData.containersMap[this.apz.scrMetaData.elmsMap[key].container];
            if (containerObj.type === "LIST" || containerObj.type === "TABLE") {
                for (var j = 0; j < containerObj.totalRecs; j++) {
                    if (validationResult) {
                        validationResult = this.fnValidateElements(key, key + "_" + j);
                    } else {
                        this.fnValidateElements(key, key + "_" + j);
                    }
                }
            } else {
                if (validationResult) {
                    validationResult = this.fnValidateElements(key, key);
                } else {
                    this.fnValidateElements(key, key);
                }
            }
        }
        return validationResult;
    },

    /**
        * Method to support validation of mandatory elements at the container level
    */
    fnValidateCntrElms: function (cntrId) {
        let validationResult = true;
        let cntrData = apz.scrMetaData.containersMap[cntrId];
        let cntrElmsMap = (cntrData) ? cntrData.elmsMap : null;
        if (!apz.isNull(cntrElmsMap)) {
            if (cntrData.type === "LIST" || cntrData.type === "TABLE") {
                for (let key in cntrElmsMap) {
                    for (let j = 0; j < cntrData.totalRecs; j++) {
                        if (validationResult) {
                            validationResult = this.fnValidateElements(key, key + "_" + j);
                        } else {
                            this.fnValidateElements(key, key + "_" + j);
                        }
                    }
                }
            } else {
                for (let key in cntrElmsMap) {
                    if (validationResult) {
                        validationResult = this.fnValidateElements(key, key);
                    } else {
                        this.fnValidateElements(key, key);
                    }
                }
            }
        }
        return validationResult;
    },
    /**
     * Supporting method to perform the mandatory check based on the container.
     */
    fnValidateElements: function (key, id) {
        var validationResult = true;
        if ($("#" + id).is(":visible") && this.apz.scrMetaData.elmsMap[key].mand && this.apz.scrMetaData.elmsMap[key].mand == "Y") {
            if (this.apz.scrMetaData.elmsMap[key].type == "DROPDOWN") {
                var value = this.apz.getElmValue(id);
                if (value == "PS" || value == "") {
                    $("#" + id).removeClass("borderGreen");
                    $("#" + id).addClass("borderRed");
                    validationResult = false;
                } else {
                    $("#" + id).removeClass("borderRed");
                    $("#" + id).addClass("borderGreen");
                }
            } else if ((this.apz.scrMetaData.elmsMap[key].type == "INPUTBOX" || this.apz.scrMetaData.elmsMap[key].type == "INPUTWITHBUTTON" || this.apz.scrMetaData.elmsMap[key].type == "TEXTAREA") && (!$("#" + id).prop("readonly") || this.apz.scrMetaData.elmsMap[key].externalWidgetType == "DPF")) {
                if (this.apz.isNull(this.apz.getElmValue(id))) {
                    $("#" + id).removeClass("borderGreen");
                    $("#" + id).addClass("borderRed");
                    validationResult = false;
                } else {
                    let EmailResult = "";
                    if (this.apz.scrMetaData.elmsMap[key].email == "Y") {
                        EmailResult = this.apz.val.validateEmailObj(document.getElementById(id));
                    }
                    if (EmailResult == "") {
                        $("#" + id).removeClass("borderRed");
                        $("#" + id).addClass("borderGreen");
                    } else {
                        $("#" + id).removeClass("borderGreen");
                        $("#" + id).addClass("borderRed");
                    }
                }
                let apzObj = this;
                if (this.apz.scrMetaData.elmsMap[key].dataType !== "INTEGER" && this.apz.scrMetaData.elmsMap[key].dataType !== "NUMBER") {
                    $("#" + id).off("blur").on("blur", function () {
                        if (apz.scrMetaData.elmsMap[key].externalWidgetType && apz.scrMetaData.elmsMap[key].externalWidgetType == "IPS" && $("#" + id).data("ui-autocomplete")) {
                            $("#" + id).autocomplete("close");
                        }
                        apzObj.fnValidateInputElement(this, key);
                    });
                }
            }
        } else if (this.apz.scrMetaData.elmsMap[key].email == "Y") {
            let apzObj = this;
            let status = true;
            $("#" + id).off("blur").on("blur", function (event) {
                let EmailResult = "";
                EmailResult = apzObj.apz.val.validateEmailObj(this);
                if (EmailResult == "") {
                    if ($(this).data("bs.popover")) {
                        $(this).popover('disable');
                    }
                } else {
                    if (apzObj.apz.isFunction(apzObj.apz.app.preEnablePopover)) {
                        status = apzObj.apz.app.preEnablePopover(this);
                    }
                    if (status && !apzObj.apz.isNull(apzObj.apz.getElmValue(this.id))) {
                        if ($(this).data("bs.popover")) {
                            $(this).popover('enable');
                            $(this).popover('show');
                        } else {
                            msg = apzObj.apz.msgs[apzObj.apz.appId]["APZ-CITI-INVEMAIL"];
                            if (!apzObj.apz.isNull(msg)) {
                                msg = msg.slice(1);
                            } else {
                                msg = "Invalid Email";
                            }
                            apzObj.initializeErrorPopover(this.id, msg);
                        }
                    }
                    if (apzObj.apz.isFunction(apzObj.apz.app.postEnablePopover)) {
                        apzObj.apz.app.postEnablePopover(this);
                    }
                    event.stopPropagation();
                }
            });
        }
        return validationResult;
    },
    /**
     * Method to check perform the mandatory check on an individual element
     */
    fnValidateInputElement: function (obj, key) {
        let status = true;
        if (this.apz.isNull(this.apz.getObjValue(obj))) {
            $(obj).removeClass("borderGreen");
            $(obj).addClass("borderRed");
            if (this.apz.isFunction(this.apz.app.preEnablePopover)) {
                status = this.apz.app.preEnablePopover(obj);
            }
            if (status) {
                if ($(obj).data("bs.popover")) {
                    $(obj).popover('enable');
                    $(obj).popover('show');
                } else {
                    let msg = "";
                    if (this.apz.scrMetaData.elmsMap[key].email == "Y") {
                        msg = this.apz.msgs[this.apz.appId]["APZ-CITI-INVEMAIL"];
                        if (!apz.isNull(msg)) {
                            msg = msg.slice(1);
                        } else {
                            msg = "Invalid Email";
                        }
                        this.initializeErrorPopover(obj.id, msg);
                    } else {
                        msg = this.apz.msgs[this.apz.appId]["APZ-CITI-INVINP"];
                        if (!apz.isNull(msg)) {
                            msg = msg.slice(1);
                        } else {
                            msg = "Invalid Input";
                        }
                        this.initializeErrorPopover(obj.id, msg);
                    }
                }
            }
            if (this.apz.isFunction(this.apz.app.postfnValidateInputElement)) {
                this.apz.app.postfnValidateInputElement(obj);
            }
        } else { }
        if (this.apz.isFunction(this.apz.app.postEnablePopover)) {
            this.apz.app.postEnablePopover(obj);
        }
    },
    /**
     * Method to initialize the popover for Invalid inputs during validation
     */
    initializeErrorPopover: function (id, message) {
        if (!this.apz.isNull(this.apz.getElmValue(id))) {
            let objId = this.apz.getObjIdWORowNumber(document.getElementById(id));
            let placement = "bottom";
            if (apz.scrMetaData.containersMap[apz.scrMetaData.elmsMap[objId].container].type === "TABLE") {
                placement = "top";
            }
            $("#" + id).popover({
                html: true,
                placement: placement,
                trigger: 'focus',
                content: '<span><svg aria-hidden="true" role="presentation" class="ett-icon icon-error px20"><use xlink:href="#icon-error"></use></svg>' + message + '</span>',
            });
            $("#" + id).popover('show');
        }
    },
    launchSubProduct: function (subProductId, pobj, event, divId, checkedProductId, launchDefaultProduct) {
        if (typeof subProductId == "object") {
            var paramObject = subProductId;
            subProductId = paramObject.subProductId;
            pobj = paramObject.pobj;
            event = paramObject.event;
            divId = paramObject.divId;
            checkedProductId = paramObject.checkedProductId;
            launchDefaultProduct = paramObject.launchDefaultProduct;
        }
        var status = true;
        checkedProductId = (!this.apz.isNull(checkedProductId)) ? checkedProductId : pobj.id.split("__")[1].split("_")[0];
        var prdForList = subProductId;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preLaunchSubProduct)) {
            status = this.apz[this.apz.currAppId].preLaunchSubProduct(prdForList, pobj, event, divId, checkedProductId);
        }
        if (!this.apz.isNull(pobj) && this.apz.isNull(this.apz.scrMetaData.elmsMap[pobj.id].subProducts)) {
            var elmId = this.apz.getObjIdWORowNumber(pobj);
            this.apz.scrMetaData.elmsMap[elmId].subProducts = subProductId;
        }
        var subProductDivId = subProductId + "_Scr_div";
        var layout = "",
            currentDesign = "";
        if (status != false) {
            var prdIdForProductList = subProductId;
            var subProductScrId = subProductId + "_Scr";
            var appId = apz.currAppId;
            layout = this.apz.getLayout({ "appId": appId, "scr": subProductScrId });
            currentDesign = this.apz.getDefaultDesign({ "appId": appId, "scr": subProductScrId, "layout": layout });
            var screenDefKey = subProductScrId + "__" + layout + "__" + currentDesign;
            var isProdLaunched = true;
            var parentObj = $("#" + divId);
            if (!divId) {
                var subProdRelObj = $("." + subProductId + "RelCntr");
                if (subProdRelObj.length > 0) {
                    parentObj = subProdRelObj;
                } else {
                    parentObj = $("#page-body");
                }
            }
            var defaultLaunchId = subProductId + "_Scr_div";
            if (!document.getElementById(defaultLaunchId)) {
                isProdLaunched = false;
                var div = document.createElement("div");
                div.setAttribute("id", defaultLaunchId);
                div.setAttribute("class", "productRoot");
                if (!this.apz.isNull(subProdRelObj)) {
                    var currObj = parentObj;
                    var sequenceFlag = true;
                    while (sequenceFlag) {
                        if (currObj.next().hasClass("productRoot")) {
                            currObj = currObj.next();
                        } else {
                            sequenceFlag = false;
                        }
                    }
                    // Investors: Performance changes starts
                    currObj["0"].parentNode.insertBefore(div, currObj["0"].nextSibling);
                    //$(div).insertAfter(currObj);
                    // Investors: Performance changes ends
                } else {
                    parentObj.append(div);
                }
            }
            var childProductIds = "";
            if (!isProdLaunched) {
                this.addDefaultShownHook(subProductScrId, subProductId);
                if (!this.apz.isNull(this.apz.scrDefsMap[appId][screenDefKey])) {
                    var scrDef = this.apz.scrDefsMap[appId][screenDefKey];
                    var proc = {};
                    proc.appId = appId;
                    proc.animation = 0;
                    // proc.scroll = params.scroll;
                    proc.scrDef = JSON.stringify(scrDef);
                    proc.scr = subProductScrId;
                    proc.lo = layout;
                    proc.template = currentDesign;
                    proc.loadScrDef = false;
                    proc.type = "CF";
                    proc.newDiv = subProductDivId;
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
                    var params = {};
                    params.productId = prdIdForProductList;
                    params.pobj = pobj;
                    params.event = event;
                    params.layout = layout;
                    params.design = currentDesign;
                    params.interfaceId = subProductId;
                    params.checkedProductId = checkedProductId;
                    params.launchDefaultProduct = launchDefaultProduct;
                    params.div = divId;
                    this.getScreenContentCB(params);
                } else {
                    var processHtml = true;
                    var params = {};
                    params.productId = prdIdForProductList;
                    params.processHtml = processHtml;
                    params.loaderRequired = true;
                    params.pobj = pobj;
                    params.layout = layout;
                    params.design = currentDesign;
                    params.event = event;
                    params.div = defaultLaunchId;
                    params.checkedProductId = checkedProductId;
                    params.userCallBack = this.getScreenContentCB;
                    params.userCallBackObj = this;
                    params.newDiv = params.div;
                    params.origDiv = params.div;
                    params.procType = "CF";
                    this.getScreenContent(params);
                }
            }
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postLaunchSubProduct)) {
            this.apz[this.apz.currAppId].postLaunchSubProduct(prdForList, pobj, event, divId, checkedProductId);
        } else {
            if (pobj.tagName == "INPUT" && pobj.type == "checkbox") {
                var checked = false;
                var scrName = subProductId + "_Scr";
                if ($(pobj).prop("checked")) {
                    checked = true;
                }
                if (checked) {
                    //changes By Siva and Karthik : To update the scrdata content before loadData gets triggered.
                    if (pobj.id.split("i").length > 0 && pobj.id.split("i")[0].split('__').length > 0) {
                        let lIntf = pobj.id.split("i")[0].split('__')[0] + '__' + pobj.id.split("i")[0].split('__')[1] + "_Req";
                        if (!apz.isNull(lIntf) && apz.data.scrdata[lIntf]) {
                            if (pobj.id.split("i")[0].split('__')[1] && pobj.id.split("i")[0].split('__')[1].split('Intf').length > 0) {
                                let lDataMapId = pobj.id.split("i")[0].split('__')[1].split('_Intf')[0];
                                let lRel = pobj.id.split("i")[0].split('__')[1].split('Intf')[0] + 'Rel';
                                if (apz.getDataType(apz.data.scrdata[lIntf]) === "Array") {
                                    let lScrDataLen = apz.data.scrdata[lIntf].length - 1;
                                    if (apz.products.sDataMap[lDataMapId] && apz.getDataType(apz.products.sDataMap[lDataMapId])) {
                                        if (apz.products.sDataMap[lDataMapId][lScrDataLen].referOther) {
                                            if (apz.data.scrdata[lIntf][lScrDataLen][lRel] && apz.data.scrdata[lIntf][lScrDataLen][lRel][pobj.id.split('__')[pobj.id.split('__').length - 1]]) {
                                                let lVal = apz.data.scrdata[lIntf][lScrDataLen][lRel][pobj.id.split('__')[pobj.id.split('__').length - 1]];
                                                if (lVal !== apz.getElmValue(pobj.id)) {
                                                    apz.data.scrdata[lIntf][lScrDataLen][lRel][pobj.id.split('__')[pobj.id.split('__').length - 1]] = apz.getElmValue(pobj.id);
                                                }
                                            }
                                        }
                                    }

                                }

                            }
                        }

                    }
                    this.addProductsToMap(apz.currAppId, subProductId, pobj);
                    $("#" + subProductId + "_Scr_div").removeClass("sno");
                    this.checkContainerVisibility();
                } else {
                    this.removeProductFromHierarchyTree(apz.currAppId, subProductId, checkedProductId, pobj);
                    $("#" + subProductId + "_Scr_div").addClass("sno");
                }
            }
        }
    },
    getScreenContent: function (param) {
        var params = {};
        params.id = "";
        params.appId = param.appId || this.apz.currAppId;
        params.scrId = param.productId + "_Scr";
        params.interfaceId = param.productId;
        params.layout = this.apz.getLayout({ "appId": params.appId, "scr": params.scrId });
        params.div = param.div;
        params.processHtml = param.processHtml;
        params.userCallBack = param.userCallBack;
        params.userCallBackObj = param.userCallBackObj;
        params.productId = param.productId;
        params.pobj = param.pobj;
        params.event = param.event;
        //params.div = divId;
        params.newDiv = param.newDiv;
        params.origDiv = param.origDiv;
        params.procType = param.procType;
        params.launchDefaultProduct = param.launchDefaultProduct;
        if (param.loaderRequired) {
            this.apz.startLoader();
        }
        var fileContent = this.apz.getFile(this.apz.getScrPath(this.apz.appId) + "products/" + params.scrId + ".json");
        if (fileContent) {
            params.res = JSON.parse(fileContent);
            this.getProductJsonCB(params);
        } else {
            this.getProductJson(params);
        }
    },
    launchProduct: function (productId, divId, appId, launchDefaultProduct) {
        if (typeof productId == "object") {
            var paramObject = productId;
            productId = paramObject.productId;
            divId = paramObject.divId;
            appId = paramObject.appId;
            launchDefaultProduct = paramObject.launchDefaultProduct;
        }
        var status = true;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preLaunchProduct)) {
            status = this.apz[this.apz.currAppId].preLaunchProduct();
        }
        if (status != false) {
            this.baseProductId = productId;
            this.currentProductId = productId;
            var params = {};
            params.productId = productId;
            params.processHtml = true;
            params.loaderRequired = true;
            params.appId = (!this.apz.isNull(appId)) ? appId : this.apz.currAppId;
            params.launchDefaultProduct = launchDefaultProduct;
            if (!this.apz.isNull(divId)) {
                params.div = divId;
                params.newDiv = divId;
                params.origDiv = divId;
                params.procType = "CF";
            } else {
                params.div = productId + "_Scr_div";
                if (this.apz.isNull($("#" + params.div)[0])) {
                    var div = document.createElement("div");
                    div.setAttribute("id", params.div);
                    div.setAttribute("class", "productRoot");
                    $("#" + this.apz.loadDiv).append(div);
                    params.newDiv = this.apz.loadDiv;
                    params.origDiv = this.apz.currDiv;
                    params.procType = "PG";
                }
            }
            this.getScreenContent(params);
        }
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postLaunchProduct)) {
            this.apz[this.apz.currAppId].postLaunchProduct();
        }
    },
    elementChecked: function (pobj) {
        var selected = false;
        if (pobj.type == "checkbox") {
            selected = pobj.checked;
        } else if (pobj.type == "dropdown") {
            if (!this.apz.isNull($(pobj).val())) {
                selected = true;
            }
        }
        return selected;
    }, retrieveFmSeqMap: function (seqMapper, lMultiRecRow, pushVal) {
        var seqMapper1 = seqMapper;
        for (var i = 0; i < lMultiRecRow.length; i++) {
            if (lMultiRecRow[i] instanceof Array) {
                if (i == lMultiRecRow.length - 1) { pushVal = true; }
                var mapper = this.retrieveFmSeqMap(seqMapper1, lMultiRecRow[i], pushVal);
                seqMapper1 = mapper;
            } else {
                if (lMultiRecRow[i - 1] instanceof Array) { pushVal = true }
                if ((pushVal && i == lMultiRecRow.length - 1)) {
                    //  if(idMapper1.indexOf(quote.Id)!==-1){
                    // idMapper1.push(quote.Id);
                    if (!this.isNull(seqMapper1)) {
                        return seqMapper1[lMultiRecRow[i]];
                    } else {
                        return undefined;
                    }
                    // }
                } else {
                    if (!this.isNull(seqMapper1)) {
                        // idMapper1[lMultiRecRow[i]] = idMapper1[lMultiRecRow[i]]?idMapper1[lMultiRecRow[i]]:[];
                        seqMapper1[lMultiRecRow[i]] = seqMapper1[lMultiRecRow[i]];
                        seqMapper1 = seqMapper1[lMultiRecRow[i]];
                    } else {
                        seqMapper1 = undefined;
                    }
                }

            }
        }
        return seqMapper1;
    },
    retrieveFmIdMap: function (idMapper, lMultiRecRow, pushVal) {
        var idMapper1 = idMapper;
        for (var i = 0; i < lMultiRecRow.length; i++) {
            if (lMultiRecRow[i] instanceof Array) {
                if (i == lMultiRecRow.length - 1) { pushVal = true; }
                var mapper = this.retrieveFmIdMap(idMapper1, lMultiRecRow[i], pushVal);
                idMapper1 = mapper;
            } else {
                if (lMultiRecRow[i - 1] instanceof Array) { pushVal = true }
                if ((pushVal && i == lMultiRecRow.length - 1)) {
                    //  if(idMapper1.indexOf(quote.Id)!==-1){
                    // idMapper1.push(quote.Id);
                    if (!this.isNull(idMapper1)) {
                        return idMapper1[lMultiRecRow[i]];
                    } else {
                        return undefined;
                    }
                    // }
                } else {
                    if (!this.isNull(idMapper1)) {
                        // idMapper1[lMultiRecRow[i]] = idMapper1[lMultiRecRow[i]]?idMapper1[lMultiRecRow[i]]:[];
                        idMapper1[lMultiRecRow[i]] = idMapper1[lMultiRecRow[i]];
                        idMapper1 = idMapper1[lMultiRecRow[i]];
                    } else {
                        idMapper1 = undefined;
                    }
                }

            }
        }
        return idMapper1;
    },
    addToSeqMapper: function (seqMapper, quote, lMultiRecRow, pushVal) {

        var seqMapper1 = seqMapper;
        if (lMultiRecRow != undefined) {
            for (var i = 0; i < lMultiRecRow.length; i++) {
                if (lMultiRecRow[i] instanceof Array) {
                    if (i == lMultiRecRow.length - 1) { pushVal = true; }
                    var mapper = this.addToSeqMapper(seqMapper1, quote, lMultiRecRow[i], pushVal);
                    seqMapper1 = mapper;
                } else {
                    if (lMultiRecRow[i - 1] instanceof Array) {
                        pushVal = true
                    }
                    if ((pushVal && i == lMultiRecRow.length - 1)) {
                        if (seqMapper1.indexOf(quote.SequenceNumber) == -1) {
                            //idMapper1.push(quote.Id);
                            seqMapper1[lMultiRecRow[i]] = quote.SequenceNumber;
                        }
                    } else {
                        seqMapper1[lMultiRecRow[i]] = seqMapper1[lMultiRecRow[i]] ? seqMapper1[lMultiRecRow[i]] : [];
                        seqMapper1 = seqMapper1[lMultiRecRow[i]];
                    }

                }
            }
        } else {
            if (seqMapper1.indexOf(quote.SequenceNumber) == -1) {
                seqMapper1.push(quote.SequenceNumber)
            }
        }
        return seqMapper1;

    },
    addToIdMapper: function (idMapper, quote, lMultiRecRow, pushVal) {

        var idMapper1 = idMapper;
        if (lMultiRecRow != undefined) {
            for (var i = 0; i < lMultiRecRow.length; i++) {
                if (lMultiRecRow[i] instanceof Array) {
                    if (i == lMultiRecRow.length - 1) { pushVal = true; }
                    var mapper = this.addToIdMapper(idMapper1, quote, lMultiRecRow[i], pushVal);
                    idMapper1 = mapper;
                } else {
                    if (lMultiRecRow[i - 1] instanceof Array) {
                        pushVal = true
                    }
                    if ((pushVal && i == lMultiRecRow.length - 1)) {
                        if (idMapper1 && idMapper1.indexOf(quote.Id) == -1) {
                            //idMapper1.push(quote.Id);
                            idMapper1[lMultiRecRow[i]] = quote.Id;
                        }
                    } else {
                        idMapper1[lMultiRecRow[i]] = idMapper1[lMultiRecRow[i]] ? idMapper1[lMultiRecRow[i]] : [];
                        idMapper1 = idMapper1[lMultiRecRow[i]];
                    }

                }
            }
        } else {
            if (idMapper1.indexOf(quote.Id) == -1) {
                idMapper1.push(quote.Id)
            }
        }
        return idMapper1;
    },
    updateIdMapper: function (id, quote, lMultiRecProductRow) {
        //Use a global variable PGroup to mention Product name.
        var mapperObj = this.idMapper[id + "_Scr"];
        if (!mapperObj) {
            mapperObj = this.idMapper[id + "_Scr"] = [];
        }
        if (mapperObj.indexOf(quote.Id) == -1) {
            mapperObj.push(quote.Id);
        }
        var seqObj = this.seqMapper[id + "_Scr"];
        if (!seqObj) {
            seqObj = this.seqMapper[id + "_Scr"] = [];
        }
        if (seqObj.indexOf(quote.SequenceNumber) == -1) {
            if (lMultiRecProductRow != undefined) {
                seqObj[lMultiRecProductRow] = quote.SequenceNumber;
            } else {
                seqObj.push(quote.SequenceNumber);
            }
        }
        if (apz.products.PGroup == "INVESTORS") {
            // Investors: Sequence number changes starts
            if (window.parent.apz.ivtsvc.InvesterNavigator.maxSequenceNo == quote.SequenceNumber) {
                //this.SeqNum = this.SeqNum + 1;
                window.parent.apz.ivtsvc.InvesterNavigator.maxSequenceNo++;
            } else if (window.parent.apz.ivtsvc.InvesterNavigator.maxSequenceNo < quote.SequenceNumber) {
                //this.SeqNum = quote.SequenceNumber + 1;
                window.parent.apz.ivtsvc.InvesterNavigator.maxSequenceNo = quote.SequenceNumber + 1;
            }
            // Investors: Sequence number changes ends
        } else {
            if (parent.apz.dealde.dealdetails.maxSeqNum == quote.SequenceNumber) {
                parent.apz.dealde.dealdetails.maxSeqNum++;
            } else if (parent.apz.dealde.dealdetails.maxSeqNum < quote.SequenceNumber) {
                parent.apz.dealde.dealdetails.maxSeqNum = quote.SequenceNumber + 1;
            }
        }
    },
	getArrCount:function(lMulti,input){
		for(var i=0; i<lMulti.length;i++){
			if(lMulti[i] instanceof Array){
				input =this.getArrCount(lMulti[i],input);
			}else{
				input++;
			}
		}
		return input;
	},
    // getId: function (param) {
    //     var id = "";
    //     if (param.newId == "N" && this.idMapper[param.scr]) {
    //         var rowNo = 0;
    //         if (param.rowNo) {
    //             rowNo = param.rowNo;
    //         }
    //         id = this.idMapper[param.scr][rowNo];
    //     }
    //     if (this.apz.isNull(id)) {
    //         var dt = new Date();
    //         var unqId = dt.getDate() + "" + dt.getMonth() + dt.getHours() + dt.getMinutes() + dt.getSeconds() + dt.getMilliseconds() + parseInt(Math.random() * 100);
    //         id = unqId; //this.apz.getProcId();
    //     }
    //     return id
    // },
    // getSeqNum: function (param) {
    //     var seq = "";
    //     if (param.newId == "N" && this.seqMapper[param.scr]) {
    //         var rowNo = 0;
    //         if (param.rowNo) {
    //             rowNo = param.rowNo;
    //         }
    //         seq = this.seqMapper[param.scr][rowNo];
    //     }
    //     if (this.apz.isNull(seq)) {
    //         if (param.product == "INVESTORS") {
    //             // Investors: Sequence number changes starts
    //             //seq = ++this.SeqNum;
    //             seq = ++window.parent.apz.ivtsvc.InvesterNavigator.maxSequenceNo;
    //             // Investors: Sequence number changes ends
    //         } else {
    //             seq = ++parent.apz.dealde.dealdetails.maxSeqNum;
    //         }
    //     }
    //     return seq;
    // },
    getId: function (param) {
        if (apz.isNull(this.idMapper1[param.scr])) {
            this.idMapper1[param.scr] = [];
        }
        var id = "";
        if (param.newId == "N" && this.idMapper1[param.scr]) {
            var scr = param.scr + "__NewLayout__D0";
            var nestCount = 0;
            var rowNo = 0;
            if (param.rowNo && param.rowNo instanceof Array) {
                rowNo = param.rowNo
                while (apz.scrDefsMap[apz.appId][scr].combinedParentId.split("_").length > 1) {
                    nestCount += 1;
                    var parent = apz.scrDefsMap[apz.appId][scr].combinedParentId;
                    scr = parent + "_Scr__NewLayout__D0";
                }
                var arrCount = this.getArrCount(param.rowNo, 0);
                if (arrCount == 2 && nestCount == 2) {
                    param.rowNo1 = [[param.rowNo[0], param.rowNo[1]], 0];
                } else if (arrCount == 2 && nestCount == 3) {
                    param.rowNo1 = [[param.rowNo[0], param.rowNo[1]], [0, 0]];
                }
                else if (arrCount == 3 && nestCount >= 3) {
                    param.rowNo1 = [param.rowNo[0], [param.rowNo[1], 0]];
                } else if (arrCount >= 4 && nestCount >= 4) {
                    param.rowNo1 = param.rowNo;
                } else {
                    param.rowNo1 = param.rowNo;
                }
                id = this.retrieveFmIdMap(this.idMapper1[param.scr], param.rowNo1, false);
            } else {
                if(param.rowNo!= undefined){
                    rowNo = param.rowNo;
                }
                id = this.idMapper1[param.scr][rowNo]
            }
        }
        if (this.isNull(id)) {
            var dt = new Date();
            var unqId = dt.getDate() + "" + dt.getMonth() + dt.getHours() + dt.getMinutes() + dt.getSeconds() + dt.getMilliseconds() + parseInt(Math.random() * 100);
            id = unqId
            if (param.rowNo1) {
                param.item.Id = id;
                if (param.rowNo1[0] instanceof Array) {
                    this.addToIdMapper(this.idMapper1[param.scr], param.item, param.rowNo1, false);
                } else {
                    this.addToIdMapper(this.idMapper1[param.scr], param.item, param.rowNo1, true);
                }
            } else {
                 if(param.rowNo != undefined){
                    this.idMapper1[param.scr][param.rowNo] = id;
                }else{
                    this.idMapper1[param.scr].push(id);  
                }
            }
        }
        return id;
    },
    getSeqNum: function (param) {
        var seq = "";
        if (apz.isNull(this.seqMapper1[param.scr])) {
            this.seqMapper1[param.scr] = [];
        }
        if (param.newId == "N" && this.seqMapper1[param.scr]) {
            var scr = param.scr + "__NewLayout__D0";
            var nestCount = 0;
            var rowNo = 0;
            if (param.rowNo && param.rowNo instanceof Array) {
                rowNo = param.rowNo
                while (apz.scrDefsMap[apz.appId][scr].combinedParentId.split("_").length > 1) {
                    nestCount += 1;
                    var parentProdId = apz.scrDefsMap[apz.appId][scr].combinedParentId;
                    scr = parentProdId + "_Scr__NewLayout__D0";
                }
                var arrCount = this.getArrCount(param.rowNo, 0);
                if (arrCount == 2 && nestCount == 2) {
                    param.rowNo1 = [[param.rowNo[0], param.rowNo[1]], 0];
                } else if (arrCount == 2 && nestCount == 3) {
                    param.rowNo1 = [[param.rowNo[0], param.rowNo[1]], [0, 0]];
                }
                else if (arrCount == 3 && nestCount >= 3) {
                    param.rowNo1 = [param.rowNo[0], [param.rowNo[1], 0]];
                } else if (arrCount >= 4 && nestCount >= 4) {
                    param.rowNo1 = param.rowNo;
                } else {
                    param.rowNo1 = param.rowNo;
                }
                seq = this.retrieveFmSeqMap(this.seqMapper1[param.scr], param.rowNo1, false);
            } else {
                if(param.rowNo != undefined){
                    rowNo = param.rowNo;
                }
                seq = this.seqMapper1[param.scr][rowNo]
            }
        }
        if (this.isNull(seq)) {
            if (param.product == "INVESTORS") {
                seq = ++window.parent.apz.ivtsvc.InvesterNavigator.maxSequenceNo
            } else {
                seq = ++parent.apz.dealde.dealdetails.maxSeqNum
            }
            if (param.rowNo1) {
                param.item.SequenceNumber = seq;
                if (param.rowNo1[0] instanceof Array) {
                    this.addToSeqMapper(this.seqMapper1[param.scr], param.item, param.rowNo1, false);
                } else {
                    this.addToSeqMapper(this.seqMapper1[param.scr], param.item, param.rowNo1, true);
                }
            } else {
                if(param.rowNo != undefined){
                    this.seqMapper1[param.scr][param.rowNo] = seq;
                }else{
                    this.seqMapper1[param.scr].push(seq);  
                }
            }
        }
        return seq;
    },
    getParentId: function (param) {
        var appId = param.appId || this.apz.currAppId;
        var key = this.getKey(param);
        return this.apz.scrDefsMap[appId][key].parentId;
    },
    getRootId: function (param) {
        var appId = param.appId || this.apz.currAppId;
        var key = this.getKey(param);
        return this.apz.scrDefsMap[appId][key].rootId;
    },
    getHeaderId: function (param) {
        var appId = param.appId || this.apz.currAppId;
        var key = this.getKey(param);
        return this.apz.scrDefsMap[appId][key].headerId;
    },
    getProductName: function (param) {
        var appId = param.appId || this.apz.currAppId;
        var key = this.getKey(param);
        return this.apz.scrDefsMap[appId][key].prodName;
    },
    getProductId: function (param) {
        var appId = param.appId || this.apz.currAppId;
        var key = this.getKey(param);
        return this.apz.scrDefsMap[appId][key].prodId;
    },
    getProdItemId: function (param) {
        var appId = param.appId || this.apz.currAppId;
        var key = this.getKey(param);
        return this.apz.scrDefsMap[appId][key].prodItemId;
    },
    getPortItemId: function (param) {
        var appId = param.appId || this.apz.currAppId;
        var key = this.getKey(param);
        return this.apz.scrDefsMap[appId][key].portItemId;
    },
    getKey: function (param) {
        var scr = param.scr || this.apz.currScr;
        var layout = param.layout || "NewLayout";
        var design = param.design || "D0";
        return scr + "__" + layout + "__" + design;
    },
    getPayloadDefaults: function (param) {
        var res = null;
        var appId = param.appId || this.apz.currAppId;
        var key = this.getKey(param);
        if (this.apz.containsKey(this.apz.scrDefsMap[appId], key)) {
            var defObj = this.apz.scrDefsMap[appId][key];
            var res = { Id: this.getId(param) };
            // res["ParentQuoteItemId"] = defObj.parentId;
            res["RootQuoteItemId"] = defObj.rootId;
            res["HeaderId"] = defObj.headerId;
            res["Name"] = defObj.prodName;
            res["ProductId"] = defObj.prodId;
            res["ProdItemId"] = defObj.prodItemId;
            res["PortItemId"] = defObj.portItemId;
        }
        return res;
    },
    removeProductsFromMap: function (productId) {
        var childProductIds = this.productsMap[productId];
        if (childProductIds.length > 0) {
            for (var i = 0; i < childProductIds.length; i++) {
                if (this.productsMap[childProductIds[i]].length > 0) {
                    this.removeProductsFromMap(childProductIds[i])
                }
                delete this.productsMap[childProductIds[i]];
            }
        }
        this.productsMap[productId] = [];
        this.currentProductId = productId;
    },
    getExternalName: function (params) {
        var appId = params.appId || this.apz.currAppId;
        var scr = params.scr || this.apz.childScr;
        var ifaceType = params.ifaceId.slice(-3);
        var ifaceId = params.ifaceId.substring(0, params.ifaceId.length - 4);
        if (this.apz.isNull(params.nodeName)) {
            params.nodeName = params.ifaceId;
        }
        if (this.apz.isNull(params.ifaceType)) {
            params.ifaceType = (ifaceType == "Req") ? "i" : "o";
        }
        params.ifaceId = ifaceId;
        var elementId = apz.getNodeId({ "iface": params.ifaceId, "dml": params.ifaceType, "node": params.nodeName }) + "__" + params.elmName;
        //return apz.ifacesMap[appId][appId+"__"+scr+"_"+params.ifaceId ].elmsMap[appId+"__"+scr+"_"+elementId].extName;
        return apz.ifacesMap[appId][params.ifaceId].elmsMap[elementId].extName;
    },
    getScreenContentCB: function (param) {
        var screenDefKey = param.productId + "_Scr" + "__" + param.layout + "__" + param.design;
        this.processRootAPI({ "screenDets": screenDefKey });
        var childProductIds = apz.scrDefsMap[apz.currAppId][screenDefKey].defaultProducts;
        this.currentProductId = param.productId;
        var productList = this.getProductHierarchyTree(apz.currAppId, param.productId);
        var tempProductList = this.productsMap;
        for (var i = 0; i < productList.length; i++) {
            tempProductList = tempProductList[productList[i]];
        }
        tempProductList[param.productId] = {};
        if (!this.apz.isNull(childProductIds) && (this.apz.isNull(param.launchDefaultProduct) || param.launchDefaultProduct)) {
            for (var i = 0; i < childProductIds.length; i++) {
                var childProductId = childProductIds[i];
                var postProductId = param.interfaceId.split("_").pop();
                var subProdId = childProductId;
                this.launchSubProduct(subProdId, param.pobj, param.event, param.div, postProductId, param.launchDefaultProduct);
            }
        }
    },
    getElementDefaultValue: function (pobj) {
        if (pobj.tagName == "TD") {
            pobj = ($("#" + pobj.id.slice(3))[0]);
        }
        if (pobj.tagName == "UL") {
            pobj = ($("#" + pobj.id.slice(0, -3))[0]);
        }
        if (pobj.tagName == "SPAN") {
            if (pobj.id.lastIndexOf("_span") > 0) {
                pobj = ($("#" + pobj.id.substring(0, pobj.id.lastIndexOf("_span")))[0]);
            } else {
                pobj = ($("#" + pobj.id)[0]);
            }
        }
        var id = this.apz.getObjIdWORowNumber(pobj);
        var currElm = this.apz.scrMetaData.elmsMap[id];
        var elmValue;
        if (!this.apz.isNull(currElm)) {
            if (!this.apz.isNull(currElm.decorations)) {
                if (currElm.decorations[0].name.startsWith("DX_")) {
                    elmValue = currElm.decorations[0].value.split("|")[3];
                } else {
                    elmValue = currElm.decorations[0].value.split("|")[2].split(":")[1];
                }
            }
        }
        return elmValue;
    },
    getProductHierarchyTree: function (appId, subProductId) {
        var value = true;
        var backTraverseProduct = [];
        while (value) {
            var layout = this.apz.getLayout({ "appId": appId, "scr": subProductId + "_Scr" });
            var currentDesign = this.apz.getDesigns({ "appId": appId, "scr": subProductId + "_Scr", "layout": layout }).currentDesign;
            var screenDefKey = subProductId + "_Scr" + "__" + layout + "__" + currentDesign;
            var currentScreenDef = apz.scrDefsMap[appId][screenDefKey];
            subProductId = currentScreenDef.combinedParentId;
            if (this.apz.isNull(subProductId)) {
                value = false;
            } else {
                value = true;
                backTraverseProduct.push(subProductId);
            }
        }
        return backTraverseProduct.reverse();
    },
    removeProductFromHierarchyTree: function (appId, subProductId, checkedProductId, pobj) {
        var appId = appId || apz.currAppId;
        var layout = this.apz.getLayout({ "appId": appId, "scr": subProductId + "_Scr" });
        var currentDesign = this.apz.getDesigns({ "appId": appId, "scr": subProductId + "_Scr", "layout": layout }).currentDesign;
        var screenDefKey = subProductId + "_Scr" + "__" + layout + "__" + currentDesign;
        var productList = this.getProductHierarchyTree(appId, subProductId);
        var tempProductList = this.productsMap;
        for (var i = 0; i < productList.length; i++) {
            tempProductList = tempProductList[productList[i]];
        }
        $(pobj).data("childProducts", JSON.stringify(tempProductList[subProductId]));
        delete tempProductList[subProductId];
    },
    addProductsToMap: function (appId, productId, pobj) {
        var childData = $(pobj).data("childProducts");
        if (!this.apz.isNull(childData)) {
            var productList = this.getProductHierarchyTree(appId, productId);
            var tempProductList = this.productsMap;
            for (var i = 0; i < productList.length; i++) {
                tempProductList = tempProductList[productList[i]];
            }
            var backUpProductsMap = JSON.parse(childData);
            tempProductList[productId] = backUpProductsMap;
        }
    },
    removeSpecialChars: function (str) {
        if (str.indexOf("-") > -1) {
            str = str.substring(str.indexOf("-") + 1);
        }
        str = str.replace("@", "");
        str = str.replace("#", "");
        return str;
    },
    buildDeal: function (params) {
        apz.data.buildData();
        var lProductId = apz.products.baseProductId;
        var lBaseProduct = apz.products.productsMap;
        var lPayloadDefaults = apz.products.getPayloadDefaults({
            "scr": lProductId + "_Scr"
        });
        try {
            if (params.product == "INVESTORS") {
                // Investors: Sequence number changes starts
                //this.getMaxSeqNumber(params);
                window.parent.apz.ivtsvc.InvesterNavigator.maxSequenceNo++;
                // Investors: Sequence number changes ends
            } else {
                parent.apz.dealde.dealdetails.maxSeqNum++;
            }
            var lPayload = {};
            var rootQuoteName = "ListOfCitidealexquoteio";
            var prebuild = true;
            if (this.apz.isFunction(this.apz[this.apz.currAppId].preBuildDeal)) {
                prebuild = this.apz[this.apz.currAppId].preBuildDeal();
                if (this.apz.isNull(prebuild)) {
                    prebuild = true;
                }
            }
            if (prebuild) {
                lPayload[rootQuoteName] = {};
                lPayload[rootQuoteName].ListOfQuoteItem = {};
                lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem = {};
                lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem = {};
                lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem = this.getChildren(params, lProductId, false);
                lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.Id = apz.products.RootQuoteId;
                if (lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiDealExQuoteItem != undefined) {
                    if (apz.getDataType(lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiDealExQuoteItem) != "Array") {
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiDealExQuoteItem.ParentQuoteItemId = apz.products.RootQuoteId;
                    } else {
                        var quoteObj = lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiDealExQuoteItem;
                        for (var i = 0; i < quoteObj.length; i++) {
                            quoteObj[i].ParentQuoteItemId = apz.products.RootQuoteId;
                        }
                    }
                }
                if (lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem != undefined) {
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.ProdItemId = "";
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.HeaderId = apz.products.HeaderId;
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.ProductId = lPayloadDefaults.ProductId;
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.Name = lPayloadDefaults.Name;
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.PortItemId = "";
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.RootQuoteItemId = apz.products.RootQuoteId;
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.ParentQuoteItemId = "";
                    var rootSqnNo = apz.products.RootSeq;
                    if (apz.isNull(rootSqnNo)) {
                        rootSqnNo = this.SeqNum;
                    }
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.SequenceNumber = rootSqnNo;
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CfgStateCode = "User Requested Item";
                    lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.Id = apz.products.RootQuoteId;
                    //updating root product in mapper//
                   // apz.products.seqMapper[lPayloadDefaults.ProductId.split("1-")[1] + "_Scr"].pop();
                   // apz.products.seqMapper[lPayloadDefaults.ProductId.split("1-")[1] + "_Scr"].push(rootSqnNo);
                    apz.products.seqMapper1[lPayloadDefaults.ProductId.split("1-")[1] + "_Scr"].pop();
                    apz.products.seqMapper1[lPayloadDefaults.ProductId.split("1-")[1] + "_Scr"].push(rootSqnNo);
                    var flag = "N";
                    if (params.action && params.action.toUpperCase() == apz.products.Submit) { //// if Action is submit CitiMandatoryQuComplFlag and CitiImplementationQuComplFlag should be Y.
                        flag = "Y";
                    }
                    if (params.product == "INVESTORS") {
                        flag = this.CitiMandatoryQuComplFlag
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiPercentageComplete = this.CitiPercentageComplete;
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiDDQPercentage = this.CitiDDQPercentage;
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiSCQuComplFlag = this.CitiSCQuComplFlag;
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiMandatoryQuComplFlag = this.CitiMandatoryQuComplFlag;
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiImplementationQuComplFlag = this.CitiImplementationQuComplFlag;
                        /* addtional tags */
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiCopiedProduct = this.CitiCopiedProduct;
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiCopiedDDQ = this.CitiCopiedDDQ;
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.sDealTypeChanged = "N";
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiINVProdRelName = "";
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiINVUIDisplayName = "";
                    } else {
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.LeadTime = "";
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiMandatoryQuComplFlag = (params.flags && params.flags.CitiMandatoryQuComplFlag) ? params.flags.CitiMandatoryQuComplFlag : flag;
                        lPayload[rootQuoteName].ListOfQuoteItem.QuoteItem.ListOfCitiDealExQuoteItem.CitiDealExQuoteItem.CitiImplementationQuComplFlag = (params.flags && params.flags.CitiImplementationQuComplFlag) ? params.flags.CitiImplementationQuComplFlag : flag;
                    }
                }
                if (this.apz.isFunction(this.apz[this.apz.currAppId].postBuildDeal)) {
                    this.apz[this.apz.currAppId].postBuildDeal(lPayload);
                }
                if (params.product == "INVESTORS") {
                    // Investors: Sequence number changes starts
                    window.parent.apz.ivtsvc.InvesterNavigator.maxSequenceNo++;
                    // Investors: Sequence number changes ends
                    var finalPayload = {};
                    finalPayload.body = {};
                    finalPayload.body.JSONMessage = lPayload;
                    return finalPayload;
                } else {
                    parent.apz.dealde.dealdetails.maxSeqNum++;
                    return lPayload;
                }
            }
        } catch (err) {
            console.log(err);
        }
    },
    getChildren: function (params, pProdId, pIsSubProduct, lMultiRecProductRow, defCntrName, elemID, subProdData) {
        /* method paramter changes to handle default product element proprties */

        var s = {};
        var lSubProds = [];
        var lSubProdsLength = 0;
        var item = {};
        var lResult = [];
        var itemsXA = [];
        var childs = [];
        var lInterfaceDetails = apz.scrMetaData.ifaces;
        var lIfaceElmsArray = {};
        lInterfaceDetails.forEach(function (pIface) {
            if (apz.currAppId + "__" + pProdId + "_Intf" == pIface.name) {
                lIfaceElmsArray = pIface.elmsMap;
            }
        });
        var lCitiDealExQuoteItem = {};
        lCitiDealExQuoteItem["CitiDealExQuoteItem"] = this.buildQuoteItem(params, pProdId, undefined, lMultiRecProductRow, defCntrName, elemID);
        var isFlagAvailable = false;
        for (pIfaceElm in lIfaceElmsArray) {
            var lAddAttr = true;
            /*
			//BUG : 54452 , Commented by Arul on 06-Oct-21.
			var lScrElem = $("#" + lIfaceElmsArray[pIfaceElm].id + "_ul");
            if (!lScrElem.length > 0) {
                lScrElem = $("#td_" + lIfaceElmsArray[pIfaceElm].id + "_" + lMultiRecProductRow);
            }
            if (!lScrElem.length > 0) {
                lScrElem = $("#" + lIfaceElmsArray[pIfaceElm].id + "_" + lMultiRecProductRow);
            }*/
            //CCF Multirecord parent-child changes starts//
            var scrMetaElm = this.apz.scrMetaData.elmsMap[lIfaceElmsArray[pIfaceElm].id];
            if (apz.scrMetaData.containersMap[scrMetaElm.container] && apz.scrMetaData.containersMap[scrMetaElm.container].referOther) {
                isFlagAvailable = true;
            }
            //CCF Multirecord parent-child changes ends//
            if (apz.isNull(scrMetaElm.decorations)) {
                lAddAttr = false;
            }
            if (lIfaceElmsArray[pIfaceElm].nodeName == pProdId + "_Rel") {
                lIsSubProduct = true;
            }
            if (!lIsSubProduct) {
				/*
				// BUG : 54452 , Commented by Arul on 06-Oct-21
                if (params.product == "INVESTORS" && lScrElem.hasClass("sno")) {
                    lAddAttr = false;
                }
				*/
				//BUG : 54452 , Added by Arul on 06-OCt-21
				if (params.product == "INVESTORS"){
					var lScrElem = $("#" + lIfaceElmsArray[pIfaceElm].id + "_ul");
					if (!lScrElem.length > 0) {
						lScrElem = $("#td_" + lIfaceElmsArray[pIfaceElm].id + "_" + lMultiRecProductRow);
					}
					if (!lScrElem.length > 0) {
						lScrElem = $("#" + lIfaceElmsArray[pIfaceElm].id + "_" + lMultiRecProductRow);
					}
                    lAddAttr = lScrElem.hasClass("sno") ? false : true;
                }
				//End of changes.
                var lIsAvailable = false;
                if (lMultiRecProductRow != undefined) {
                    if (lMultiRecProductRow > -1) {
                        lIsAvailable = $("#" + lIfaceElmsArray[pIfaceElm].id + "_" + lMultiRecProductRow).hasClass("dataAvailable");
                    }
                } else {
                    lIsAvailable = $("#" + lIfaceElmsArray[pIfaceElm].id).hasClass("dataAvailable");
                }
                if (lAddAttr || lIsAvailable) {
                    lAddAttr = true;
                } else {
                    lAddAttr = false;
                }
            }
            if (!lIsSubProduct) {
                if (params.product == "TTS" && scrMetaElm.basicrulesetpresent == "N") {
                    lAddAttr = false;
                }
            }
            if (lAddAttr) {
                var lIsSubProduct = false;
                if (lIfaceElmsArray[pIfaceElm].nodeName == pProdId + "_Rel") {
                    lIsSubProduct = true;
                }
                if (lIsSubProduct) {
                    //buildquoteitem for the rel
                    if (lMultiRecProductRow != undefined) {
                        if (lMultiRecProductRow > -1 || (apz.getDataType(lMultiRecProductRow) == 'Array')) {
                            var lNodeName = lIfaceElmsArray[pIfaceElm].nodeName;
                            var lElmName = lIfaceElmsArray[pIfaceElm].name;

                            //CCF Multirecord parent-child changes start//
                            var parentCtrId, parentMetaCtrId, parentMetaData;
                            if (!this.isNull(defCntrName)) {
                                parentCtrId = defCntrName.split("__")[1]
                                    ? apz.scrDefsMap[apz.currAppId][defCntrName.split("__")[1] + "__NewLayout__D0"].combinedParentId
                                    : apz.scrDefsMap[apz.currAppId][defCntrName + "__NewLayout__D0"].combinedParentId;
                                parentMetaCtrId = parentCtrId.split("_")[2]
                                    ? apz.currAppId + "__" + parentCtrId + "_Scr__" + parentCtrId.split("_")[2]
                                    : "";
                                parentMetaData = parentMetaCtrId
                                    ? apz.scrMetaData.containersMap[parentMetaCtrId]
                                    : "";
                            } else {
                                parentCtrId = scrMetaElm.container.split("__");
                                parentMetaCtrId = apz.scrDefsMap[apz.currAppId][parentCtrId[1] + "__NewLayout__D0"];
                                parentMetaData = parentMetaCtrId.containersMap[scrMetaElm.container];
                            }
                            var nodeData;
                            if (parentMetaData && parentMetaData.referOther) {
                                /**
                                    problem Step: this.getIterativeGDupSubPrdData was getting called for every iface Element
                                    optimization step : Storing gSubDupDataProduct if available already
                                */
                                let gSubDupDataProduct = this.getIterativeGDupSubPrdData(pProdId, gDulipcateSubProductData, lMultiRecProductRow, true);


                                if (this.isNull(gSubDupDataProduct)) {
                                    nodeData = apz.data.scrdata[apz.currAppId + "__" + pProdId + "_Intf_Req"][lNodeName];
                                } else {
                                    //nodeData = gSubDupDataProduct;
                                    //CCF changes added by Siva
                                    nodeData = gSubDupDataProduct[lNodeName] ? gSubDupDataProduct[lNodeName] : gSubDupDataProduct;
                                }
                            } else {
                                //nodeData = apz.data.scrdata[apz.currAppId + "__" + pProdId + "_Intf_Req"][lNodeName];
                                //CCF changes added by Siva
                                nodeData = apz.data.scrdata[apz.currAppId + "__" + pProdId + "_Intf_Req"][lMultiRecProductRow][lNodeName];
                            }

                            //CCF Multirecord parent-child changes end//

                            if (nodeData) {
                                lValue = nodeData[lElmName];
                            }
                        }
                    } else {
                        try {
                            lValue = apz.getElmValue(lIfaceElmsArray[pIfaceElm].id);
                        } catch (e) {
                            lDummyVal = true;
                        }
                    }
                    if (lValue == "y") {
                        var lSubProduct = scrMetaElm.subProducts;
                        if (lSubProduct != undefined) {
                            let scrId = scrMetaElm.container.split("__");
                            //CCF Multirecord parent-child changes starts//
                            if (apz.scrMetaData.containersMap[scrMetaElm.container].referOther || (apz.scrMetaData.containersMap[apz.currAppId + "__" + scrId[1] + "__" + scrId[1].split("_")[2]] && apz.scrMetaData.containersMap[apz.currAppId + "__" + scrId[1] + "__" + scrId[1].split("_")[2]].referOther)) {
                                for (let key in apz.scrDefsMap[apz.currAppId][lSubProduct + "_Scr__NewLayout__D0"].containersMap) {
                                    apz.scrDefsMap[apz.currAppId][lSubProduct + "_Scr__NewLayout__D0"].containersMap[key].referOther = true;
                                    apz.scrMetaData.containersMap[key].referOther = true;
                                }
                            }
                            //CCF Multirecord parent-child changes ends//
                            //sub product existing 
                            if (apz.products.isProductLaunched(apz.appId, lSubProduct)) {
                                // sub product is launched
                                var ifaceInfo = apz.scrMetaData.ifacesMap[this.apz.currAppId + "__" + lSubProduct + "_Intf"];
                                //krish--30-04-MultiRec product coming as sub-product(FPB) refer subproduct's data and not parent's data
                                var lSubScrData = apz.data.scrdata[apz.currAppId + "__" + lSubProduct + "_Intf_Req"];
                                var lSubScrDataLen = 0;
                                /*(apz.getDataType(lSubScrData)!== "Object"){
                                    lSubScrDataLen = lSubScrData.length;
                                }*/
                                //CCF Changes added by Siva
                                if (lSubScrData) {
                                    lSubScrDataLen = lSubScrData.length;
                                }
                                // Deep Changes aug25th
                                if (!apz.isNull(lSubScrDataLen) && ifaceInfo && ifaceInfo.elms.length > 0) {
                                    if (apz.scrMetaData.containersMap[scrMetaElm.container].referOther) {
                                        let gSubPrdDupDataProduct = this.getIterativeGDupSubPrdData(lSubProduct, gDulipcateSubProductData, lMultiRecProductRow);
                                        //Corrected by Karthik on 31-08-2021
										//lSubScrDataLen = gSubPrdDupDataProduct.length || (Object.keys(gSubPrdDupDataProduct).length > 0) ? 1 : 0;
                                        lSubScrDataLen = (gSubPrdDupDataProduct.length) || ((Object.keys(gSubPrdDupDataProduct).length > 0) ? 1 : 0);
                                        for (var i = 0; i < lSubScrDataLen; i++) {
                                            var lSubScrData = gSubPrdDupDataProduct[i] || gSubPrdDupDataProduct;
                                            if (lSubScrData != undefined) {
                                                if (apz.getDataType(lMultiRecProductRow[0]) == "Array") {
                                                    if (apz.getDataType(lMultiRecProductRow[lMultiRecProductRow.length - 1]) == "Array") {
                                                        var lrecData = lMultiRecProductRow[lMultiRecProductRow.length - 1];
                                                        lrecData[lrecData.length - 1] = i;
                                                    } else {
                                                        lMultiRecProductRow[lMultiRecProductRow.length - 1] = [lMultiRecProductRow[lMultiRecProductRow.length - 1], i];
                                                    }
                                                }
                                                var lChildQuoteItem = this.getChildren(params, lSubProduct, true, lMultiRecProductRow);
                                                lChildQuoteItem.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                                this.IsSubProduct = true;
                                                lResult.push(lChildQuoteItem);
                                            } else {
                                                var lNonSubProduct = this.buildQuoteItem(params, lSubProduct, lIfaceElmsArray[pIfaceElm], lMultiRecProductRow);
                                                if (apz.getDataType(lNonSubProduct) != "Array") {
                                                    lNonSubProduct.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                                }
                                                this.sIsSubProduct = false;
                                                lResult.push(lNonSubProduct);
                                            }
                                        }
                                    } else {
                                        if (lSubScrDataLen > 0) {
                                            for (var i = 0; i < lSubScrDataLen; i++) {
                                                var lSubScrData = apz.data.scrdata[apz.currAppId + "__" + lSubProduct + "_Intf_Req"][i];
                                                if (lSubScrData != undefined) {
                                                    var lChildQuoteItem = this.getChildren(params, lSubProduct, true, i);
                                                    lChildQuoteItem.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                                    this.IsSubProduct = true;
                                                    //var lMergedChild = $.extend(lQuoteItem, lChildQuoteItem);
                                                    lResult.push(lChildQuoteItem);
                                                } else {
                                                    var lNonSubProduct = this.buildQuoteItem(params, lSubProduct, lIfaceElmsArray[pIfaceElm], lMultiRecProductRow);
                                                    if (apz.getDataType(lNonSubProduct) != "Array") {
                                                        lNonSubProduct.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                                    }
                                                    this.sIsSubProduct = false;
                                                    lResult.push(lNonSubProduct);
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    var lSubScrData = apz.data.scrdata[apz.currAppId + "__" + lSubProduct + "_Intf_Req"];
                                    if (!apz.products.isNull(lSubScrData)) {
                                        var lChildQuoteItem = this.getChildren(params, lSubProduct, true, lMultiRecProductRow, undefined,
                                            lIfaceElmsArray[pIfaceElm]);
                                        lChildQuoteItem.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                        this.sIsSubProduct = true;
                                        lResult.push(lChildQuoteItem);
                                    } else {
                                        var lNonSubProduct = this.buildQuoteItem(params, lSubProduct, lIfaceElmsArray[pIfaceElm], lMultiRecProductRow);
                                        if (apz.getDataType(lNonSubProduct) != "Array") {
                                            lNonSubProduct.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                        }
                                        this.sIsSubProduct = false;
                                        lResult.push(lNonSubProduct);
                                    }
                                }
                            } else {
                                var lNonSubProduct = this.buildQuoteItem(params, lSubProduct, lIfaceElmsArray[pIfaceElm], lMultiRecProductRow);
                                if (apz.getDataType(lNonSubProduct) != "Array") {
                                    lNonSubProduct.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                }
                                this.sIsSubProduct = false;
                                lResult.push(lNonSubProduct);
                            }
                        } else {
                            if (!isFlagAvailable) {
                                var ctnr = scrMetaElm.container;
                                ctnr = ctnr.substring(ctnr.lastIndexOf("__") + 2, ctnr.length);
                                var nodeName = scrMetaElm.nodeName;
                                var parntId = nodeName.substr(0, nodeName.indexOf('_Rel')).substr(nodeName.substr(0, nodeName.indexOf('_Rel')).lastIndexOf('_') + 1, nodeName.substr(0, nodeName.indexOf('_Rel')).length);
                                var currId = scrMetaElm.externalid;
                                var prodId = parntId + "_" + ctnr + "_" + currId;
                                var name = scrMetaElm.name;
                                var lNonSubProduct = this.buildQuoteItem(params, prodId, undefined, lMultiRecProductRow);
                                if (apz.getDataType(lNonSubProduct) != "Array") {
                                    lNonSubProduct.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                }
                                this.sIsSubProduct = false;
                                lResult.push(lNonSubProduct);
                            }
                        }
                    }
                    //check if prod in rel is is prodsmap.if so then call getchildren with prod and dump inside quoteitem
                } else {
                    var lAttr = lIfaceElmsArray[pIfaceElm].extName;
                    var lbiphipr = scrMetaElm.biphier;
                    var lreapprvl = scrMetaElm.reApproval;
                    var luidispname = scrMetaElm.displayName;
                    /* investor : XAId changes */
                    var lOrigId = "1-" + scrMetaElm.name;
                    /* ends here */
                    var lValue = "";
                    var lDummyVal = false;
                    if (lMultiRecProductRow != undefined) {
                        if (apz.getDataType(lMultiRecProductRow) == "Array")
                            var llMultiRecProductRow = lMultiRecProductRow[lMultiRecProductRow.length - 1];
                        if (apz.getDataType(llMultiRecProductRow) == "Array") {
                            llMultiRecProductRow = llMultiRecProductRow[llMultiRecProductRow.length - 1];
                        }
                        if ((llMultiRecProductRow != undefined && llMultiRecProductRow > -1) || lMultiRecProductRow > -1) {
                            var lNodeName = lIfaceElmsArray[pIfaceElm].nodeName;
                            var lElmName = lIfaceElmsArray[pIfaceElm].name;

                            //CCF Multirecord parent-child changes starts//
                            var parentCtrId, parentMetaCtrId, parentMetaData;
                            if (!this.isNull(defCntrName)) {
                                parentCtrId = defCntrName.split("__")[1]
                                    ? apz.scrDefsMap[apz.currAppId][defCntrName.split("__")[1] + "__NewLayout__D0"].combinedParentId
                                    : apz.scrDefsMap[apz.currAppId][defCntrName + "__NewLayout__D0"].combinedParentId;
                                parentMetaCtrId = parentCtrId.split("_")[2]
                                    ? apz.currAppId + "__" + parentCtrId + "_Scr__" + parentCtrId.split("_")[2]
                                    : "";
                                parentMetaData = parentMetaCtrId
                                    ? apz.scrMetaData.containersMap[parentMetaCtrId]
                                    : "";
                                if (parentMetaData == undefined)
                                    parentMetaData = apz.scrMetaData.containersMap[scrMetaElm.container];
                            } else {
                                parentCtrId = scrMetaElm.container.split("__");
                                parentMetaCtrId = apz.scrDefsMap[apz.currAppId][parentCtrId[1] + "__NewLayout__D0"];
                                parentMetaData = parentMetaCtrId.containersMap[scrMetaElm.container];
                            }
                            if (parentMetaData && parentMetaData.referOther) {
                                let resObj;
                                let gSubDupDataProduct = this.getIterativeGDupSubPrdData(pProdId, gDulipcateSubProductData, lMultiRecProductRow);

                                if (this.isNull(gSubDupDataProduct)) {
                                    if (apz.getDataType(apz.data.scrdata[lNodeName]) == "Array") {
                                        //lValue = apz.data.scrdata[lNodeName][lMultiRecProductRow[lMultiRecProductRow.length-1]][lElmName];
                                        //Praveena changes
                                        lMultiRecProductRow = (Array.isArray(lMultiRecProductRow)) ? lMultiRecProductRow[lMultiRecProductRow.length - 1] : lMultiRecProductRow;
                                        lValue = apz.data.scrdata[lNodeName][lMultiRecProductRow][lElmName];
                                    } else {
                                        lValue = apz.data.scrdata[lNodeName][lElmName];
                                    }
                                } else {
                                    lValue = gSubDupDataProduct[lElmName];
                                }
                            } else {
                                /** Deal express multi level product payload issue */
                                lMultiRecProductRow = (Array.isArray(lMultiRecProductRow)) ? lMultiRecProductRow[lMultiRecProductRow.length - 1] : lMultiRecProductRow;
                                lValue = apz.data.scrdata[lNodeName][lMultiRecProductRow][lElmName];
                            }
                            //CCF Multirecord parent-child changes ends//
                        }
                    } else {
                        try {
                            lValue = apz.getElmValue(lIfaceElmsArray[pIfaceElm].id);
                            if (lValue == "PS") {
                                lValue = "";
                            }
                        } catch (e) {
                            lDummyVal = true;
                        }
                    }
                    if (params.product == "INVESTORS") {
                        /** changes for Removing "," from Number fields **/
                        if (scrMetaElm.dataType && (scrMetaElm.dataType == "NUMBER" || scrMetaElm.dataType == "INTEGER")) {
                            //lValue = lValue.replace(/,/g,'');
                            lValue = this.apz.unFormatNumber({ "value": lValue, "decimalSep": ".", "displayAsLiteral": "N" });
                        }
                        /** changes ends **/
                        if (scrMetaElm["DXP_" + apz.products.Pname + "_UIName"]) {
                            luidispname = scrMetaElm["DXP_" + apz.products.Pname + "_UIName"];
                        }
                        /*   upload changes : investor  */
                        if (scrMetaElm.type == "FILEBROWSER") {
                            lValue = $("#" + scrMetaElm.id + "_li").find("#selectedfiles").text();
                            if (apz.isNull(lValue)) {
                                lValue = "";
                            } else {
                                lValue = lValue.substr(0, lValue.lastIndexOf("X"));
                            }
                        }
                        /*   ends here   */
                        if (!lDummyVal) {
                            itemsXA.push({
                                "CitiReapproval": lreapprvl,
                                "CitiBIPHierarchy": lbiphipr,
                                "CitiUIDisplayNAME": luidispname,
                                "Attribute": lAttr,
                                "DisplayName": lAttr,
                                "Value": lValue,
                                "CfgStateCode": "User Requested Item",
                                /* investor : XAId changes */
                                "XAId": lOrigId
                            });
                        }
                    } else {
						//Code corrected for setting correct display Name by Arul on 11-10-2021.
                        /*itemsXA.push({
                            "Attribute": lAttr,
                            "DisplayName": lAttr,
                            "Value": lValue,
                            "CfgStateCode": "User Requested Item"
                        });*/
						let hookObj = {
                            "Attribute": lAttr,
                            "DisplayName": lAttr,
                            "Value": lValue,
                            "CfgStateCode": "User Requested Item"
                        };
						
						//Added hook, so that they can update value of "PS" as well as they can set it for specific display Name.
						if (this.apz.isFunction(this.apz[this.apz.currAppId].preGetChildrenValue)) {
							//It accepts 2 params, 1. Object ,2. DisplayName as per Product template.
							this.apz[this.apz.currAppId].preGetChildrenValue(hookObj,luidispname);
						}
						
						itemsXA.push(hookObj);
						//End of changes.
                    }
                }
            }
        }

        //CCF Multirecord parent-child changes starts//
        if (!isFlagAvailable) {
            var referOtherObj = apz.scrDefsMap[apz.currAppId][pProdId + "_Scr__NewLayout__D0"].containersMap[Object.keys(apz.scrDefsMap[apz.currAppId][pProdId + "_Scr__NewLayout__D0"].containersMap)[0]];
            if (!this.isNull(referOtherObj) && referOtherObj.referOther)
                isFlagAvailable = true;
        }
        if (isFlagAvailable) {
            var lDefaultProds = apz.scrDefsMap[apz.currAppId][pProdId + "_Scr__NewLayout__D0"]["defaultProducts"];
            if (lDefaultProds != undefined) {
                for (var f = 0; f < lDefaultProds.length; f++) {
                    for (let key in apz.scrDefsMap[apz.currAppId][lDefaultProds[f] + "_Scr__NewLayout__D0"].containersMap) {
                        apz.scrDefsMap[apz.currAppId][lDefaultProds[f] + "_Scr__NewLayout__D0"].containersMap[key].referOther = true;
                        apz.scrMetaData.containersMap[key].referOther = true;
                    }
                }
                isFlagAvailable = false;
            }
        }
        //CCF Multirecord parent-child changes ends//
        this.buildDefaultProds(pProdId, lResult, lCitiDealExQuoteItem, params, lMultiRecProductRow);
        if (itemsXA.length > 0) {
            lCitiDealExQuoteItem["CitiDealExQuoteItem"]["ListOfQuoteItemXA"] = {};
            lCitiDealExQuoteItem["CitiDealExQuoteItem"]["ListOfQuoteItemXA"]["QuoteItemXaChild"] = itemsXA;
        } else {
            lCitiDealExQuoteItem["CitiDealExQuoteItem"]["ListOfQuoteItemXA"] = "";
        }
        if (!pIsSubProduct) {
            if (lResult.length > 0) {
                if (lResult.length == 1) {
                    lCitiDealExQuoteItem["CitiDealExQuoteItem"]["CitiDealExQuoteItem"] = lResult[0];
                } else {
                    lCitiDealExQuoteItem["CitiDealExQuoteItem"]["CitiDealExQuoteItem"] = lResult;
                }
            }
        } else {
            if (apz.isNull(lChildQuoteItem)) {
                if (lResult.length > 0) {
                    if (lResult.length == 1) {
                        lCitiDealExQuoteItem["CitiDealExQuoteItem"]["CitiDealExQuoteItem"] = lResult[0];
                    } else {
                        lCitiDealExQuoteItem["CitiDealExQuoteItem"]["CitiDealExQuoteItem"] = lResult;
                    }
                }
                lCitiDealExQuoteItem = lCitiDealExQuoteItem["CitiDealExQuoteItem"];
            } else if (subProdData) {
                if (lResult.length > 0) {
                    if (lResult.length == 1) {
                        lCitiDealExQuoteItem["CitiDealExQuoteItem"]["CitiDealExQuoteItem"] = lResult[0];
                    } else {
                        lCitiDealExQuoteItem["CitiDealExQuoteItem"]["CitiDealExQuoteItem"] = lResult;
                    }
                }
                lCitiDealExQuoteItem = lCitiDealExQuoteItem["CitiDealExQuoteItem"];
            } else {
                /*f (lResult.length == 1) {
                        lCitiDealExQuoteItem = lResult[0];
                 } else {
                     lCitiDealExQuoteItem = lResult;
                 }*/
                //CCF Changes added by Siva
                if (lResult.length > 0) {
                    if (lResult.length == 1) {
                        lCitiDealExQuoteItem["CitiDealExQuoteItem"]["CitiDealExQuoteItem"] = lResult[0];
                    } else {
                        lCitiDealExQuoteItem["CitiDealExQuoteItem"]["CitiDealExQuoteItem"] = lResult;
                    }
                }
                lCitiDealExQuoteItem = lCitiDealExQuoteItem["CitiDealExQuoteItem"];

            }

        }
        return lCitiDealExQuoteItem;
        //}
    },
    buildDefaultProds: function (pProdId, lResult, lCitiDealExQuoteItem, params, lMultiRecProductRow) {
        var lDefaultProds = apz.scrDefsMap[apz.currAppId][pProdId + "_Scr__NewLayout__D0"]["defaultProducts"];
        if (lDefaultProds != undefined) {
            for (var f = 0; f < lDefaultProds.length; f++) {
                //var lScrData;
                //Scoping issue wrt lScrData so commenting it
                let lScrData;

                let cntrName = pProdId.split("_")[2];
                //CCF Multirecord parent-child changes starts//
                let cntrMetaData;
                if (cntrName)
                    cntrMetaData = apz.scrMetaData.containersMap[apz.currAppId + "__" + pProdId + "_Scr__" + pProdId.split("_")[2]];
                else
                    cntrMetaData = apz.scrMetaData.containersMap[apz.currAppId + "__" + pProdId + "_Scr__" + pProdId];
                if (this.isNull(cntrMetaData)) {
                    let defsCtrId = apz.currAppId + "__" + pProdId + "_Scr__" + lDefaultProds[f].split("_")[1];
                    cntrMetaData = apz.scrDefsMap[apz.currAppId][pProdId + "_Scr__NewLayout__D0"].containersMap[defsCtrId];
                }

                if (cntrMetaData.referOther && !apz.isNull(gDulipcateSubProductData)) {
                    let gSubSpecificData = this.getIterativeGDupSubPrdData(lDefaultProds[f], gDulipcateSubProductData, lMultiRecProductRow);
                    //Deep changes
                    if (!this.isNull(gSubSpecificData)) {
                        lScrData = gSubSpecificData;
                    }else if(apz.getDataType(gSubSpecificData) === "Array"){
                        lScrData = gSubSpecificData;
                    } else {
                       // lScrData = apz.data.scrdata[apz.currAppId + "__" + lDefaultProds[f] + "_Intf_Req"];
                    }
                } else {
                    lScrData = apz.data.scrdata[apz.currAppId + "__" + lDefaultProds[f] + "_Intf_Req"];
                }
                //CCF Multirecord parent-child changes ends//

                // Investors: Default product payload changes starts here

                var defCntrName = apz.currAppId + "__" + lDefaultProds[f] + "_Scr__" + lDefaultProds[f].split('_')[2];
                if (this.PGroup == "INVESTORS") {
                    var lBuildDefaultProduct = true;
                    // var defCntrName = apz.currAppId + "__" + lDefaultProds[f] + "_Scr__" + lDefaultProds[f].split('_')[2];
                    var lContainer = defCntrName;
                    for (key in apz.scrMetaData.containersMap) {
                        if (key.indexOf(defCntrName) > -1) {
                            lContainer = key;
                        }
                    }
                    if ($("#" + lContainer).hasClass("sno")) {
                        var lScrMapElm = lDefaultProds[f] + "_Scr__NewLayout__D0";
                        var lParentID = apz.scrDefsMap[apz.currAppId][lScrMapElm].combinedParentId;
                        var lPortID = apz.scrDefsMap[apz.currAppId][lScrMapElm].portItemId.split("1-")[1];
                        var lRelID = apz.currAppId + "__" + lParentID + "_Scr__" + lPortID;
                        if ($("#" + lRelID).hasClass("sno")) {
                            var lDecoration = apz.scrMetaData.containersMap[lRelID].decorations;
                            //var lSearchRes = lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_Show"]);
                            var lSearchRes = lDecoration.find(function (itemVal) {
                                return itemVal.name == ["DXP_" + apz.products.Pname + "_Show"];
                            });
                            if (lSearchRes && !apz.isNull(lSearchRes.value)) {
                                var lIndex = lSearchRes.value.split("DDR=").length - 1;
                                var elmDDR = lSearchRes.value.split("DDR=")[lIndex];
                                elmDDR = elmDDR.slice(0, 1);
                                if (apz.products["DDR"] == elmDDR) {
                                    lBuildDefaultProduct = false;
                                } else {
                                    var defaultProdName = apz.scrDefsMap[apz.currAppId][lScrMapElm].prodName;
                                    if (defaultProdName) {
                                        for (var key in apz.scrMetaData.containersMap[lContainer].elmsMap) {
                                            var lElm = apz.scrMetaData.elmsMap[key];
                                            if (lElm.displayName) {
                                                var lElmName = lElm.displayName;
                                                if (lElmName.startsWith("X:")) {
                                                    lElmName = lElmName.split("X:")[1];
                                                    if (defaultProdName == lElmName) {
                                                        if (apz.getElmValue(lElm.id) != "Y") {
                                                            lBuildDefaultProduct = false;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (lBuildDefaultProduct) {
                        if (apz.products.sDealTypeChanged == "Y") {
                            if ($("#" + lContainer).length > 0) {
                                if ($("#" + lContainer).hasClass("sno")) {
                                    lBuildDefaultProduct = false;
                                }
                            } else {
                                if ($("#" + lDefaultProds[f] + "_Scr_div").length > 0) {
                                    if ($("#" + lDefaultProds[f] + "_Scr_div").hasClass("sno")) {
                                        lBuildDefaultProduct = false;
                                    }
                                }
                            }
                        }
                    }
                    if (lBuildDefaultProduct && !$("#" + lContainer).hasClass("deletedDefaultProduct")) {
                        if (lScrData != undefined) {
                            var lScrDataLen = lScrData.length;
                            if (!apz.isNull(lScrDataLen)) {
                                if (lScrDataLen > 0) {
                                    for (var i = 0; i < lScrDataLen; i++) {
                                        //   var defCntrName = apz.currAppId+"__"+lDefaultProds[f]+"_Scr__"+lDefaultProds[f].split('_')[2]; 
                                        var lChildQuoteItem = this.getChildren(params, lDefaultProds[f], true, i, defCntrName);
                                        lChildQuoteItem.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                        this.sIsSubProduct = true;
                                        lResult.push(lChildQuoteItem);
                                    }
                                }
                            } else {
                                // var defCntrName = apz.currAppId+"__"+lDefaultProds[f]+"_Scr__"+lDefaultProds[f].split('_')[2]; 
                                var lChildQuoteItem = this.getChildren(params, lDefaultProds[f], true, lMultiRecProductRow, defCntrName);
                                lChildQuoteItem.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                this.sIsSubProduct = true;
                                lResult.push(lChildQuoteItem);
                            }
                        }
                    }
                }
                // Investors: Default product payload changes ends here 
                else {
                    if (lScrData != undefined) {
                        var lScrDataLen = lScrData.length;
                        var lChildQuoteItem = "";
                        var defsMapData = apz.scrDefsMap[apz.currAppId][lDefaultProds[f] + "_Scr__NewLayout__D0"];
                        var subProds = defsMapData.subProducts;
                        var defaultProds = defsMapData.defaultProducts;
                        var subDefaultPrdExists = false;
                        for (var k = 0; k < subProds.length; k++) {
                            if (!defaultProds.includes(subProds[k])) {
                                subDefaultPrdExists = true;
                                break;
                            }
                        }
                        if (!apz.isNull(lScrDataLen)) {
                            if (lScrDataLen > 0) {
                                for (var i = 0; i < lScrDataLen; i++) {
                                    if (lMultiRecProductRow != undefined) {

                                        if (subDefaultPrdExists) {

                                            lChildQuoteItem = this.getChildren(params, lDefaultProds[f], true, [lMultiRecProductRow, i], defCntrName, '', true);
                                        } else {

                                            lChildQuoteItem = this.getChildren(params, lDefaultProds[f], true, [lMultiRecProductRow, i], defCntrName);
                                        }

                                    } else {
                                        lChildQuoteItem = this.getChildren(params, lDefaultProds[f], true, i, defCntrName);
                                    }
                                    lChildQuoteItem.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                                    this.sIsSubProduct = true;
                                    lResult.push(lChildQuoteItem);
                                }
                            }
                        } else if (subDefaultPrdExists) {
                            lChildQuoteItem = this.getChildren(params, lDefaultProds[f], true, lMultiRecProductRow, defCntrName, '', true);
                            lChildQuoteItem.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                            this.sIsSubProduct = true;
                            lResult.push(lChildQuoteItem);

                        } else {
                            // var defCntrName = apz.currAppId+"__"+lDefaultProds[f]+"_Scr__"+lDefaultProds[f].split('_')[2]; 
                            var lChildQuoteItem = this.getChildren(params, lDefaultProds[f], true, lMultiRecProductRow, defCntrName);
                            lChildQuoteItem.ParentQuoteItemId = lCitiDealExQuoteItem["CitiDealExQuoteItem"].Id;
                            this.sIsSubProduct = true;
                            lResult.push(lChildQuoteItem);
                        }
                    }
                }
            }
        }
    },
    buildQuoteItem: function (params, prodId, pIfaceElm, lMultiRecProductRow, defCntrName, elemID) {
        /* method paramter changes to handle default product element proprties */
        var lProductID = prodId;
        var item = {};
        var lPayloadDefaults = this.getPayloadDefaults({ "scr": prodId + "_Scr", "newId": "N",  rowNo: lMultiRecProductRow,item: item });
        if (!lPayloadDefaults) {
            item.ProdItemId = "1-" + pIfaceElm.name.replace("1-", "");
            item.Name = pIfaceElm.extName;
            item.PortItemId = "1-" + pIfaceElm.name.replace("1-", "");
        } else {
            item.ProdItemId = "1-" + lPayloadDefaults.ProdItemId.replace("1-", "");
            item.Name = lPayloadDefaults.Name;
            item.PortItemId = "1-" + lPayloadDefaults.PortItemId.replace("1-", "");
        }
        item.HeaderId = apz.products.HeaderId;
        // item.SequenceNumber = this.getSeqNum({ "scr": prodId + "_Scr", "newId": "N", "rowNo": lMultiRecProductRow, "product": params.product });
        // item.Id = this.getId({ "scr": prodId + "_Scr", "newId": "N", "rowNo": lMultiRecProductRow });
        item.SequenceNumber = this.getSeqNum({
            scr: prodId + "_Scr",
            newId: "N",
            rowNo: lMultiRecProductRow,
            product: params.product,
            item: item
        });
        item.Id = this.getId({
            scr: prodId + "_Scr",
            newId: "N",
            rowNo: lMultiRecProductRow,
            item: item
        });
        //this.updateIdMapper(prodId, item, lMultiRecProductRow);
        if (prodId.indexOf("_") > -1) {
            var prodIdArray = prodId.split("_");
            prodId = prodIdArray[prodIdArray.length - 1];
        }
        item.ProductId = "1-" + prodId.replace("1-", "");
        if (!apz.isNull(this.Id)) {
            var id = this.Id;
            item.ParentQuoteItemId = id;
        } else {
            item.ParentQuoteItemId = apz.products.RootQuoteId;
        }
        this.Id = item.Id;
        item.RootQuoteItemId = this.RootQuoteId;
        item.CfgStateCode = "User Requested Item";
        if (params.product == "INVESTORS") {
            /*  handle default product element proprties */
            if (!pIfaceElm) {
                pIfaceElm = elemID;
            }
            if (pIfaceElm != undefined) {
                var lbiphipr = apz.scrMetaData.elmsMap[pIfaceElm.id].biphier;
                var lreapprvl = apz.scrMetaData.elmsMap[pIfaceElm.id].reApproval;
                item.CitiSPUIApprovalFlg = lreapprvl;
                item.CitiQuoteItemBIPHierarchy = lbiphipr;
                /* investor : Relationship UI Name changes  */
                var lUIDisplayName = "";
                if (apz.scrMetaData.elmsMap[pIfaceElm.id]["DXP_" + apz.products.Pname + "_UIName"] && !apz.isNull(apz.scrMetaData.elmsMap[pIfaceElm.id]
                ["DXP_" + apz.products.Pname + "_UIName"])) {
                    lUIDisplayName = apz.scrMetaData.elmsMap[pIfaceElm.id]["DXP_" + apz.products.Pname + "_UIName"];
                } else if (apz.scrMetaData.elmsMap[pIfaceElm.id].displayName && !apz.isNull(apz.scrMetaData.elmsMap[pIfaceElm.id].displayName)) {
                    lUIDisplayName = apz.scrMetaData.elmsMap[pIfaceElm.id].displayName;
                } else if (apz.scrMetaData.elmsMap[pIfaceElm.id].name && !apz.isNull(apz.scrMetaData.elmsMap[pIfaceElm.id].name)) {
                    lUIDisplayName = apz.scrMetaData.elmsMap[pIfaceElm.id].name;
                }
                item.CitiINVUIDisplayName = lUIDisplayName;
                var lProdRelName = "";
                var lContainerID = apz.scrMetaData.elmsMap[pIfaceElm.id].container;
                var lDecoration = apz.scrMetaData.containersMap[lContainerID].decorations;
                // var lSearchRes = lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_UIName"]);
                var lSearchRes = lDecoration.find(function (itemVal) {
                    return itemVal.name == ["DXP_" + apz.products.Pname + "_UIName"];
                });
                if (lSearchRes && !apz.isNull(lSearchRes.value)) {
                    lProdRelName = lSearchRes.value;
                } /*else if (lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"])) {
                    lProdRelName = lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"]).value;
                }*/else if (lDecoration.find(function (itemVal) {
                    return itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"];
                })) {
                    lProdRelName = lDecoration.find(function (itemVal) {
                        return itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"];
                    });
                } else if (apz.scrMetaData.containersMap[lContainerID].dispName && !apz.isNull(apz.scrMetaData.containersMap[lContainerID].dispName)) {
                    lProdRelName = apz.scrMetaData.containersMap[lContainerID].dispName;
                } else if (apz.scrMetaData.containersMap[lContainerID].extName && !apz.isNull(apz.scrMetaData.containersMap[lContainerID].extName)) {
                    lProdRelName = apz.scrMetaData.containersMap[lContainerID].extName;
                }
                item.CitiINVProdRelName = lProdRelName;
                /* ends here */
            } else if (defCntrName != undefined) {
                var lFlag = false;
                for (key in apz.scrMetaData.containersMap) {
                    if (key.indexOf(defCntrName) > -1) {
                        defCntrName = key;
                        lFlag = true;
                    }
                }
                if (lFlag) {
                    var lbiphipr = apz.scrMetaData.containersMap[defCntrName].biphier;
                    var lreapprvl = apz.scrMetaData.containersMap[defCntrName].reApproval;
                    item.CitiSPUIApprovalFlg = lreapprvl;
                    item.CitiQuoteItemBIPHierarchy = lbiphipr;

                    var lProdRelName = "";

                    var lScrMapElm = lProductID + "_Scr__NewLayout__D0";
                    var lParentID = apz.scrDefsMap[apz.currAppId][lScrMapElm].combinedParentId;
                    var lPortID = apz.scrDefsMap[apz.currAppId][lScrMapElm].portItemId.split("1-")[1];
                    var lRelID = apz.currAppId + "__" + lParentID + "_Scr__" + lPortID;
                    var lDecoration = apz.scrMetaData.containersMap[lRelID].decorations;
                    //var lSearchRes = lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_UIName"]);
                    var lSearchRes = lDecoration.find(function (itemVal) {
                        return itemVal.name == ["DXP_" + apz.products.Pname + "_UIName"];
                    });
                    if (lSearchRes && !apz.isNull(lSearchRes.value)) {
                        lProdRelName = lSearchRes.value;
                    } /*else if (lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"])) {
                    lProdRelName = lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"]).value;
                }*/else if (lDecoration.find(function (itemVal) {
                        return itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"];
                    })) {
                        lProdRelName = lDecoration.find(function (itemVal) {
                            return itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"];
                        });
                    } else if (apz.scrMetaData.containersMap[lRelID].dispName && !apz.isNull(apz.scrMetaData.containersMap[lRelID].dispName)) {
                        lProdRelName = apz.scrMetaData.containersMap[lRelID].dispName;
                    } else if (apz.scrMetaData.containersMap[lRelID].extName && !apz.isNull(apz.scrMetaData.containersMap[lRelID].extName)) {
                        lProdRelName = apz.scrMetaData.containersMap[lRelID].extName;
                    }
                    item.CitiINVProdRelName = lProdRelName;

                    var lUIDisplayName = "";
                    var lContainerID = defCntrName;
                    var lDecoration = apz.scrMetaData.containersMap[lContainerID].decorations;
                    //var lSearchRes = lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_UIName"]);
                    var lSearchRes = lDecoration.find(function (itemVal) {
                        return itemVal.name == ["DXP_" + apz.products.Pname + "_UIName"];
                    });
                    if (lSearchRes && !apz.isNull(lSearchRes.value)) {
                        lUIDisplayName = lSearchRes.value;
                    } /*else if (lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"])) {
                    lUIDisplayName = lDecoration.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"]).value;
                }*/else if (lDecoration.find(function (itemVal) {
                        return itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"];
                    })) {
                        lUIDisplayName = lDecoration.find(function (itemVal) {
                            return itemVal.name == ["DXP_" + apz.products.Pname + "_DispName"];
                        });
                    } else if (apz.scrMetaData.containersMap[lContainerID].dispName && !apz.isNull(apz.scrMetaData.containersMap[lContainerID].dispName)) {
                        lUIDisplayName = apz.scrMetaData.containersMap[lContainerID].dispName;
                    } else if (apz.scrMetaData.containersMap[lContainerID].extName && !apz.isNull(apz.scrMetaData.containersMap[lContainerID].extName)) {
                        lUIDisplayName = apz.scrMetaData.containersMap[lContainerID].extName;
                    }
                    item.CitiINVUIDisplayName = lUIDisplayName;
                } else {
                    item.CitiSPUIApprovalFlg = "N";
                    item.CitiQuoteItemBIPHierarchy = "";
                    item.CitiINVProdRelName = "";
                    item.CitiINVUIDisplayName = "";
                }
            } else {
                item.CitiSPUIApprovalFlg = "N";
                item.CitiQuoteItemBIPHierarchy = "";
            }
        } else {
            item.LeadTime = "";
            item.CitiImplementationQuComplFlag = "N";
            item.CitiMandatoryQuComplFlag = "N";
        }
        return item;
    },
    fnAddMultiSelectDropdown: function (pProdId, pContainerId, rowNum, fnName, isMand) {
        if (apz.isNull(rowNum)) {
            rowNum = -1;
        }
        var scr = pProdId + "_Scr";
        var layout = this.apz.getLayout({ "appId": apz.appId, "scr": scr });
        var currDesign = this.apz.getDesigns({ "appId": apz.appId, "scr": scr, "layout": layout }).currentDesign;
        var scrName = scr + "__" + layout + "__" + currDesign;
        var lContainersMap = apz.scrDefsMap[apz.appId][scrName].containersMap;
        var lRequiredContainersMapArr = [];
        for (var eachKey in lContainersMap) {
            var nodesArr = lContainersMap[eachKey].nodes;
            var nodesLen = lContainersMap[eachKey].nodes.length;
            for (var k = 0; k < nodesLen; k++) {
                if (nodesArr[k].indexOf("_Rel") != -1) {
                    if (eachKey.indexOf(pContainerId) > -1) {
                        var lDDNId = eachKey + '_DDN';
                        if ($("#" + lDDNId).length == 0) {
                            lRequiredContainersMapArr.push(eachKey);
                        }
                    }
                }
            }
        }
        let ApzObj = this;
        lRequiredContainersMapArr.forEach(function (lContainerName) {
            var lContainer = lContainersMap[lContainerName];
            if (lContainer.type == "FORM") {
                var lElmsMapArr = Object.keys(lContainer.elmsMap);
                var lElmsMap = lContainer.elmsMap;
                var lTotalElms = lElmsMapArr.length;
                var count = 0;
                var lSelectOptions = [];
                lElmsMapArr.forEach(function (lElmName) {
                    var lElm = lElmsMap[lElmName];
                    if (lElm.type == "CHECKBOX" && !$("#" + lElm.id + "_ul").hasClass("sno")) {
                        var lSelectOption = {};
                        lSelectOption.id = lElm.id;
                        var lText = $("#" + lElm.id + "_grp_lbl").text();
                        if (lText !== "Please Select") {
                            lSelectOption.text = lText;
                            var lCheckboxUl = lElmName + "_ul";
                            lSelectOptions.push(lSelectOption);
                            count++;
                        }
                    }
                });
                ApzObj.containerOptionsMap[lContainerName] = lSelectOptions;
                if (lSelectOptions.length > 0) {
                    //// Hide only the section rows as ui description will also be a span.
                    $("#" + lContainerName + ">span.srb").css({
                        "visibility": "hidden",
                        "display": "block",
                        "height": "0px"
                    });
                    //Checkbox to radio button conversion
                    let decors = apz.scrMetaData.containersMap[lContainerName].decorations;
                    //let searchRes = decors.find(itemVal => itemVal.name == ["DXP_" + apz.products.Pname + "_ConvertItem"]);
                    let searchRes = decors.find(function (itemVal) {
                        return itemVal.name == ["DXP_" + apz.products.Pname + "_ConvertItem"];
                    });
                    if (!this.apz.isNull(searchRes) && searchRes.value.trim() == "RDO") {
                        var lCntTitle = lContainerName + "_title";
                        var lRdoId = "";
                        var attrRowNum = "";
                        if (rowNum == -1) {
                            lRdoId = lContainerName + '_RDO';
                        } else {
                            lRdoId = lContainerName + '_RDO_' + rowNum;
                            attrRowNum = "rowNo='" + rowNum + "'";
                        }
                        if (apz.isNull($("#" + lCntTitle + " li:first-child").siblings()[0])) {
                            let opt1Id = lSelectOptions[0].id;
                            let opt1Text = lSelectOptions[0].text;
                            let opt2Id = lSelectOptions[1].id;
                            let opt2Text = lSelectOptions[1].text;
                            //  $("#" + lCntTitle).append("<li class='eic etw-60'><div class='etb-slct ett-slct mltt etw-100 customDrop'><p>hello</p></div></li>");
                            $("#" + lCntTitle).append('<li id="' + lContainerName + '_li" class="eic etw-60"><span id="' + lRdoId + '" ' + attrRowNum + ' apztype="radiogroup" class="etb-rdio ett-rdio ver pri borderRed customRadio" aria-describedby="" value=""><span id="' + opt1Id + '_option_' + opt1Text + '_lbl_" for="' + opt1Id + '_option_' + opt1Text + '" class=""><input id="' + opt1Id + '_option_' + opt1Text + '" class="" style="" name="' + lContainerName + '_" type="radio" value="Yes" aria-labelledby="' + opt1Id + '_option_' + opt1Text + '_span_" onchange="apz.products.processRadio(this, event);"><label id="' + opt1Id + '_option_' + opt1Text + '_span_" class="rlb" for="' + opt1Id + '_option_' + opt1Text + '" title="' + opt1Text + '">' + opt1Text + '</label></span><span id="' + opt2Id + '_option_' + opt2Text + '_lbl_" for="' + opt2Id + '_option_' + opt2Text + '" class=""><input id="' + opt2Id + '_option_' + opt2Text + '" class="" style="" name="' + lContainerName + '_" type="radio" value="No" aria-labelledby="' + opt2Id + '_option_' + opt2Text + '_span_" onchange="apz.products.processRadio(this, event);"><label id="' + opt2Id + '_option_' + opt2Text + '_span_" class="rlb" for="' + opt2Id + '_option_' + opt2Text + '" title="' + opt2Text + '">' + opt2Text + '</label></span></span></li>');
                            $("#" + lContainerName + " > ul").removeClass("ttl").addClass("eoc srb");
                            $("#" + lContainerName + " > ul li:first-child").addClass("etw-40");
                        }
                    } else {
                        var lCntTitle = lContainerName + "_title";
                        var lDDNId = "";
                        var attrRowNum = "";
                        if (rowNum == -1) {
                            lDDNId = lContainerName + '_DDN';
                        } else {
                            lDDNId = lContainerName + '_DDN_' + rowNum;
                            attrRowNum = "rowNo='" + rowNum + "'";
                        }
                        $("#" + lCntTitle).append("<li class='eic etw-60'><div class='etb-slct ett-slct mltt etw-100 customDrop'><select id='" +
                            lDDNId + "' " + attrRowNum + " class = 'customMultiSelect' ></select></div></li>");
                        $("#" + lContainerName + " > ul").removeClass("ttl").addClass("eoc srb");
                        $("#" + lContainerName + " > ul li:first-child").addClass("etw-40");
                        apz.products.fnInitMultiSelectDropdown(pProdId, lDDNId, lSelectOptions, lTotalElms, fnName, isMand);
                    }
                }
            }
        });
    },
    fnInitMultiSelectDropdown: function (pProdId, drpDwnId, drpDwnVals, lTotalElms, fnSelectName, userMand) {
        let contId = drpDwnId.split("_DDN")[0];
        let contProps = this.apz.scrMetaData.containersMap[contId];
        let MinCardinality = 0;
        let isMand = false;
        if (contProps && contProps.decorations) {
            for (var n = 0; n < contProps.decorations.length; n++) {
                if (contProps.decorations[n].name.split("_")[1] === this.RootPrdShortName && contProps.decorations[n].name.split("_")[2] === "BasicRuleSet") {
                    var valueArr = contProps.decorations[n].value.split("|");
                    if (valueArr[1] == "Man") {
                        isMand = true;
                        break;
                    } else {
                        isMand = false;
                        break;
                    }
                }
            }
        }
        for (var p = 0; p < contProps.decorations.length; p++) {
            if (contProps.decorations[p].name.split("_")[1] === this.RootPrdShortName && contProps.decorations[p].name.split("_")[2] === "Cardinality") {
                var valueArr = contProps.decorations[p].value.split("|");
                if (valueArr[0] > 0) {
                    MinCardinality = valueArr[0];
                    isMand = true;
                    break;
                } else {
                    isMand = false;
                    break;
                }
            }
        }
        isMand = userMand || isMand;
        contProps.mand = isMand;
        if (!apz.isNull($("#" + drpDwnId).data("select2"))) {
            $("#" + drpDwnId).select2("destroy");
        } else {
            $("#" + drpDwnId).siblings().remove();
        }
        $("#" + drpDwnId).select2({
            data: drpDwnVals,
            multiple: true,
            closeOnSelect: false,
        });
        /* multi select search (startsWith) : investor  */
        if (apz.products.PGroup == "INVESTORS") {
            function search(term, text, elm) {
                if (text.toUpperCase().indexOf(term.toUpperCase()) == 0) {
                    if (!apz.isNull($('.select2-results__options')[0])) {
                        let liElmsLen = $('.select2-results__options').children().length;
                        for (let childVal = 0; childVal < liElmsLen; childVal++) {
                            if (!$('.select2-results__options').children()[childVal].classList.contains('sno') && $('.select2-results__options').children()[
                                childVal].innerText == text) {
                                return true;
                            }
                        }
                    } else {
                        return true;
                    }
                }
                return false;
            }
            $.fn.select2.amd.require(['select2/compat/matcher'], function (f) {
                $("#" + drpDwnId).select2({
                    closeOnSelect: false,
                    matcher: f(search)
                });
                apz.products.validateDropDown();
            });
        }
        var subProds = drpDwnVals.map(function (obj) {
            return obj.id.split("__").pop();
        });
        if (isMand) {
            $("#" + drpDwnId).next().find(".select2-selection--multiple").addClass("borderRed");
            if (MinCardinality > 0) {
                $("#" + drpDwnId).data("minCardinality", MinCardinality);
            }
        }
        $(".select2-selection__rendered").scrollLeft(0);
        $('#' + drpDwnId).off('select2:select').on('select2:select', function (e) {
            var data = e.params.data;
            apz.setElmValue(data.id, $("#" + data.id).attr("checkedval"));
            $("#" + data.id).trigger("change");
            $(".select2-selection__rendered").scrollLeft(0);
        });
        $('#' + drpDwnId).off("select2:unselect").on("select2:unselect", function (e) {
            var data = e.params.data;
            apz.setElmValue(data.id, $("#" + data.id).attr("uncheckedval"));
            $("#" + data.id).trigger("change");
            $(".select2-selection__rendered").scrollLeft(0);
        });
        $('#' + drpDwnId).on("select2:opening", function (e) {
            var lSelect2UlId = "select2-" + this.id + "-results";
            /* multi select search (startsWith) : investor  */
            if (apz.products.PGroup == "INVESTORS") {
                $('#' + drpDwnId).siblings().children(':first-child').find('input').attr('onkeyup', 'apz.products.filterDrpDwn("' + drpDwnId + '");');
            }
            if (lTotalElms > 5 && lTotalElms < 10) {
                setTimeout(function () {
                    if (apz.products.PGroup == "INVESTORS") {
                        $("#" + lSelect2UlId).addClass("multi2Li");
                    } else {
                        $("#" + lSelect2UlId + " li").css({
                            "width": "49%",
                            "float": "left"
                        });
                    }
                }, 50);
            } else if (lTotalElms >= 10 && lTotalElms <= 50) {
                setTimeout(function () {
                    if (apz.products.PGroup == "INVESTORS") {
                        $("#" + lSelect2UlId).addClass("multi3Li");
                    } else {
                        $("#" + lSelect2UlId + " li").css({
                            "width": "33.3%",
                            "float": "left"
                        });
                    }
                }, 20);
            } else if (lTotalElms > 50) {
                setTimeout(function () {
                    if (apz.products.PGroup == "INVESTORS") {
                        $("#" + lSelect2UlId).addClass("multi5Li");
                    } else {
                        $("#" + lSelect2UlId + " li").css({
                            "width": "20%",
                            "float": "left"
                        });
                    }
                }, 20);
            }
        });
        $('#' + drpDwnId).off("select2:open").on("select2:open", function (e) {
            $(this).data("isOpen", true);
            /* re-inialize options for inflight logic : investor  */
            setTimeout(function () {
                if (apz.isFunction(apz.app.fnDropDownOpen)) {
                    apz.app.fnDropDownOpen(pProdId, $("#" + e.currentTarget.getAttribute('id')), subProds);
                }
            }, 10);
        });
        $('#' + drpDwnId).off("select2:close").on("select2:close", function (e) {
            $(this).data("isOpen", false);
            if (apz.isFunction(fnSelectName)) {
                fnSelectName(pProdId, this, subProds);
            }
            $(".select2-selection__rendered").scrollLeft(0);
        });
    },
    updateDropDownScrData: function (pProdId, pobj, relatedOpts, event) {
        var rowNum = pobj.getAttribute("rowno");
        if (apz.isNull(rowNum)) {
            rowNum = -1;
        }
        var appId = apz.currAppId;
        var dropDownValue = apz.getObjValue(pobj);
        dropDownValue = dropDownValue.split(",");
        var selectOptArray = [];
        for (var i = 0; i < dropDownValue.length; i++) {
            selectOptArray.push(dropDownValue[i].split("__").pop());
        }
        if (rowNum == -1) {
            if (apz.data.scrdata[appId + "__" + pProdId + "_Intf_Req"] && apz.data.scrdata[appId + "__" + pProdId + "_Intf_Req"] && apz.data.scrdata[appId + "__" + pProdId + "_Intf_Req"][pProdId + "_Rel"]) {
                var scrData = apz.data.scrdata[appId + "__" + pProdId + "_Intf_Req"][pProdId + "_Rel"];
                for (var keys in scrData) {
                    if (relatedOpts.indexOf(key) > -1) {
                        var optId = appId + "__" + pProdId + "_Intf__i__" + pProdId + "_Rel__" + keys;
                        var optObj = $("#" + optId);
                        if (selectOptArray.indexOf(keys) > -1) {
                            if (!apz.isNull(optObj.attr("checkedval"))) {
                                apz.setElmValue(optId, $("#" + optId).attr("checkedval"));
                            }
                        } else {
                            if (!apz.isNull(optObj.attr("uncheckedval"))) {
                                apz.setElmValue(optId, $("#" + optId).attr("uncheckedval"));
                            }
                        }
                    }
                }
            }
        } else {
            if (apz.data.scrdata[appId + "__" + pProdId + "_Intf_Req"] && apz.data.scrdata[appId + "__" + pProdId + "_Intf_Req"][rowNum] && apz.data.scrdata[appId + "__" + pProdId + "_Intf_Req"][rowNum][pProdId + "_Rel"]) {
                var scrData = apz.data.scrdata[appId + "__" + pProdId + "_Intf_Req"][rowNum][pProdId + "_Rel"];
                for (var keys in scrData) {
                    var optId = appId + "__" + pProdId + "_Intf__i__" + pProdId + "_Rel__" + keys;
                    var optObj = $("#" + optId);
                    if (selectOptArray.indexOf(keys) > -1) {
                        if (!apz.isNull(optObj.attr("checkedval"))) {
                            apz.setElmValue(optId, $("#" + optId).attr("checkedval"));
                        }
                    } else {
                        if (!apz.isNull(optObj.attr("uncheckedval"))) {
                            apz.setElmValue(optId, $("#" + optId).attr("uncheckedval"));
                        }
                    }
                }
            }
        }
    },
    loadDealData: function (data) {
        apz.data.buildData();
        this.QuoteResponse = data;
        var quote = data['ListOfQuote']['ListOfQuote']['Quote'];
        var parentQuoteItem = quote['ListOfQuoteItem']['QuoteItem'];
        //Karthik Changes : Handling for parentQuoteItem  in case of Array.
        if (apz.getDataType(parentQuoteItem) == "Array") {
            apz.products.RootQuoteId = parentQuoteItem[0].Id;
            apz.products.HeaderId = parentQuoteItem[0].HeaderId;
            apz.products.RootSeq = parentQuoteItem[0].SequenceNumber;
        } else {
            apz.products.RootQuoteId = parentQuoteItem.Id;
            apz.products.HeaderId = parentQuoteItem.HeaderId;
            apz.products.RootSeq = parentQuoteItem.SequenceNumber;
        }
        if (apz.getDataType(parentQuoteItem) == "Array") {
            for (var i = 0; i < parentQuoteItem.length; i++) {
                this.loadParentQuoteItem(parentQuoteItem[i], quote);
            }
        } else {
            this.loadParentQuoteItem(parentQuoteItem, quote);
        }
    },
    // loadParentQuoteItem: function (parentQuoteItem, quote) {
    //     var lProductID = parentQuoteItem.ProductId.replace("1-", "");
    //     var ProductsMap = [lProductID];
    //     if (apz.products.productsMap[lProductID]) {
    //         this.updateIdMapper("RootID", quote);
    //         this.updateIdMapper(lProductID, parentQuoteItem);
    //         this.loadProductsData(parentQuoteItem);
    //     } else { }
    // },
    loadParentQuoteItem: function (parentQuoteItem, quote) {
        var lProductID = parentQuoteItem.ProductId.replace("1-", "");
        var ProductsMap = [lProductID];
        if (apz.products.productsMap[lProductID]) {
            this.updateIdMapper("RootID", quote);
            this.updateIdMapper(lProductID, parentQuoteItem);
            if (!this.idMapper1["RootID_Scr"]) {
                this.idMapper1["RootID_Scr"] = [];
            }
            this.addToIdMapper(this.idMapper1["RootID_Scr"], quote);
            if (!this.seqMapper1["RootID_Scr"]) {
                this.seqMapper1["RootID_Scr"] = [];
            }
            this.addToSeqMapper(this.seqMapper1["RootID_Scr"], quote);
            if (!this.idMapper1[lProductID + "_Scr"]) {
                this.idMapper1[lProductID + "_Scr"] = [];
            }
            this.addToIdMapper(this.idMapper1[lProductID + "_Scr"], parentQuoteItem);
            if (!this.seqMapper1[lProductID + "_Scr"]) {
                this.seqMapper1[lProductID + "_Scr"] = [];
            }
            this.addToSeqMapper(this.seqMapper1[lProductID + "_Scr"], parentQuoteItem);
            this.loadProductsData(parentQuoteItem)
        } else { }
    },
    loadSubProducts: function (parentQuoteItem, productId, parentRec) {
        // select the relationships
        // load the sub product details
        var prevRow;
        var countRow = 0;;
        var childquote = parentQuoteItem["QuoteItem"];
        var lParentProdId = parentQuoteItem.ProductId.replace("1-", "");
        var lDefaultProds = [];
        var lIsDefaultProduct = false,
            isTriggered = false;
        var combinedId = "",
            protId = "";
        if (childquote) {
            var containerId = apz.currAppId + "__" + productId + "_Scr" + "__" + lParentProdId;
            cntrObj = apz.scrMetaData.containersMap[containerId];
            if (cntrObj && parentRec > 0) {
                var dataRecNo = this.apz.data.getDataRec(containerId, parentRec);
                var params = {};
                params.containerId = containerId;
                params.dataRecNo = dataRecNo;
                params.action = "N";
                this.apz.data.goToRecord(params);
            }
            var lType = apz.getDataType(childquote);
            if (lType == "Array") {
                /* Added multiRecMap for maintaining proper rowNo for multiRecs - Darshan 19-06 */
                let multiRecMap = {};
                for (var i = 0; i < childquote.length; i++) {
                    if (!(childquote[i].ProductId.indexOf("1-") > -1)) {
                        childquote[i].ProductId = "1-" + childquote[i].ProductId;
                    }
                    var lRelationCheckBoxId = childquote[i].ProductId;
                    var childElement = $("#scr__" + apz.currAppId + "__" + productId + "_Scr__main").find("." + lRelationCheckBoxId).find("input[type=checkbox]");
                    var lChildQuote = childquote[i];
                    /* multiple containers having same checkbox( multi select options) : investors  */
                    if (childElement.length > 1) {
                        var possibOrigId = lChildQuote.ProdItemId.split("-")[1];
                        for (var c = 0; c < childElement.length; c++) {
                            var child = childElement[c];
                            var idFrags = child.id.split("__");
                            var domOrigId = idFrags[idFrags.length - 1];
                            if (domOrigId.indexOf(possibOrigId) > -1) {
                                childElement = $(child);
                                break;
                            };
                        }
                    }
                    protId = childquote[i].PortItemId.replace("1-", "");
                    var prodId = childquote[i].ProductId.replace("1-", "");
                    combinedId = lParentProdId + "_" + protId + "_" + prodId;
                    if (!this.sDataMap[combinedId]) {
                        this.sDataMap[combinedId] = [];
                    }
                    if (this.apz.getDataType(this.sDataMap[combinedId]) == "Array") {
                        if (this.sDataMap[combinedId].indexOf(lChildQuote) == -1) {
                            //CCF Multirecord parent-child changes starts//
                            if (parentQuoteItem.referOther) {
                                lChildQuote.referOther = true;
                                if (parentRec) {
                                    lChildQuote.parentRec = parentRec;
                                }
                                if (!this.isNull(parentQuoteItem.rowNo)) {
                                    lChildQuote.rowNo = parentQuoteItem.rowNo;
                                    let scrDefsMapData = apz.scrDefsMap[apz.currAppId][productId + "_Scr__NewLayout__D0"];
                                    let subPrdId = lParentProdId + "_" + lChildQuote.PortItemId.replace("1-", "") + "_" + lChildQuote.ProductId.replace("1-", "");
                                    let isSubPrd = true;
                                    if (scrDefsMapData.defaultProducts.includes(subPrdId) && scrDefsMapData.subProducts.includes(subPrdId)) {
                                        isSubPrd = false;
                                    }
                                    if (isSubPrd) {
                                        lChildQuote.subRow = parentQuoteItem["subRow"];
                                    } else {
                                        lChildQuote.subRow = 0;
                                        if (i > 0 && lChildQuote.ProductId == childquote[i - 1].ProductId) {
                                            lChildQuote.subRow = childquote[i - 1].subRow + 1;
                                        }
                                    }

                                    lChildQuote["tabRowNo"] = parentQuoteItem["tabRowNo"];
                                }
                                let ctrId = apz.currAppId + "__" + combinedId + "_Scr" + "__" + prodId;
                                if (apz.scrMetaData.containersMap[ctrId] && apz.scrMetaData.containersMap[ctrId].multiRec == "Y" && apz.scrMetaData.containersMap[ctrId].type == "TABLE") {
                                    if (!this.isNull(prevRow) && prevRow != lChildQuote.rowNo) {
                                        countRow = 0;
                                    } else {
                                        prevRow = lChildQuote.rowNo;
                                        countRow++;
                                    }
                                    lChildQuote.tableRow = countRow;
                                }
                            }
                            if (lChildQuote["tabRowNo"] == undefined) {
                                if (apz.isNull(parentQuoteItem.referOther || lChildQuote.referOther)) {
                                    lChildQuote["tabRowNo"] = 0;
                                } else {
                                    lChildQuote["tabRowNo"] = i;
                                }

                            }

                            this.sDataMap[combinedId].push(lChildQuote);
                            //CCF Multirecord parent-child changes ends//
                        }
                    }
                    /*  krish--23.04 
                     * MultiRecord container as default product(INV) requires parentRec 
                     * MultiRecord container as default product of subproduct(CBE) requires i.
                     * MultiRecord container coming as a subproduct requires i(FPB)-02.05
                     * Multiselect Dropdown present in row of multirecord container(CBE) requires parentRec
                     */
                    let isDpdwn = false;
                    if (!apz.isNull(childElement[0]) && !apz.isNull($("option[value='" + childElement[0].id + "']")[0])) {
                        isDpdwn = $("option[value='" + childElement[0].id + "']").parent().hasClass('customMultiSelect');
                        if (isDpdwn) {
                            let cntrDecors = apz.scrMetaData.containersMap[apz.scrMetaData.elmsMap[childElement[0].id].container].decorations;
                            //let lSearchRes = cntrDecors.find(itemVal => itemVal.name == ["DXP_" + apz.products.RootPrdShortName + "_BasicRuleSet"]);
                            let lSearchRes = cntrDecors.find(function (itemVal) {
                                return itemVal.name == ["DXP_" + apz.products.RootPrdShortName + "_BasicRuleSet"];
                            });
                            if (!apz.isNull(lSearchRes) && lSearchRes.value.split("|")[0] == "Grid")
                                isDpdwn = false;
                        }
                    }

                    /* Update multiRecMap with latest rowNo for multiRecs - Darshan 19-06 */
                    let commonProdId = lChildQuote.ProductId;
                    if (this.apz.isNull(multiRecMap[commonProdId])) {
                        multiRecMap[commonProdId] = 0;
                    } else {
                        ++multiRecMap[commonProdId];
                    }
                    var multiRecRow = [];
                    var quoteArr = [];
                    if (parentQuoteItem.QuoteItem) {
                        var parQuote = parentQuoteItem.QuoteItem;
                        for (var q = 0; q < parQuote.length; q++) {
                            if (parQuote[q].ProductId == lChildQuote.ProductId) {
                                quoteArr.push(parQuote[q]);
                            }
                        }
                        if (quoteArr.length > 0) {
                            for (var k = 0; k < quoteArr.length; k++) {
                                if (parentQuoteItem.nestLevel == undefined) {
                                    quoteArr[k].nestLevel = 1;
                                } else {
                                    quoteArr[k].nestLevel = parentQuoteItem.nestLevel + 1;
                                }
                            }
                        }
                        if (lChildQuote.nestLevel == 1) {
                            multiRecRow = [lChildQuote.rowNo, lChildQuote.tabRowNo]
                        }
                        else if (lChildQuote.nestLevel == 2) {
                            multiRecRow = [[lChildQuote.rowNo, lChildQuote.tabRowNo]];
                        } else if (lChildQuote.nestLevel == 3) {
                            multiRecRow = [[lChildQuote.rowNo, lChildQuote.tabRowNo], multiRecMap[commonProdId]];
                        } else if (lChildQuote.nestLevel >= 4) {
                            var parDefsMap = apz.scrDefsMap[apz.currAppId][productId + "_Scr__NewLayout__D0"];
                            if (parDefsMap && (parDefsMap.defaultProducts.includes(combinedId) && parDefsMap.subProducts.includes(combinedId))) {

                                multiRecRow = [[lChildQuote.rowNo, lChildQuote.tabRowNo], [lChildQuote.subRow, 0]];
                            } else {
                                multiRecRow = [[lChildQuote.rowNo, lChildQuote.tabRowNo], [lChildQuote.subRow, multiRecMap[commonProdId]]];
                            }
                        }
                        var mapperObj = this.idMapper1[combinedId + "_Scr"];
                        if (!mapperObj) {
                            mapperObj = this.idMapper1[combinedId + "_Scr"] = []
                        }
                        var seqObj = this.seqMapper1[combinedId + "_Scr"];
                        if (!seqObj) {
                            seqObj = this.seqMapper1[combinedId + "_Scr"] = []
                        }
                        if (parentRec != undefined) {
                            this.addToIdMapper(mapperObj, lChildQuote, multiRecRow, false);
                            this.addToSeqMapper(seqObj, lChildQuote, multiRecRow, false);
                        } else {
                            this.addToIdMapper(mapperObj, lChildQuote, parentRec, false);
                            this.addToSeqMapper(seqObj, lChildQuote, parentRec, false);
                        }
                    }
                    // if (parentRec != undefined && !isDpdwn) {
                    //     /* Avoided passing i for rowNo (parameter) as multiRecMap has the updated value - Darshan 19-06 */
                    //     this.updateIdMapper(combinedId, lChildQuote, multiRecMap[commonProdId]);
                    // } else {
                    //     this.updateIdMapper(combinedId, lChildQuote, parentRec);
                    // }
                    if (apz.scrDefsMap[apz.currAppId][combinedId + "_Scr__NewLayout__D0"]) {
                        lDefaultProds = apz.scrDefsMap[apz.currAppId][combinedId + "_Scr__NewLayout__D0"]["defaultProducts"];
                    }
                    lIsDefaultProduct = false;
                    if (lDefaultProds.length > 0) {
                        var lChildId = childquote[i].PortItemId.replace("1-", "") + "_" + childquote[i].ProductId.replace("1-", "")
                        for (var j = 0; j < lDefaultProds.length; j++) {
                            if (lDefaultProds[j].indexOf(lChildId) > -1) {
                                lIsDefaultProduct = true;
                                break;
                            }
                        }
                    }
                    if (childElement && !lIsDefaultProduct) {
                        var lCheckBoxId = $(childElement).attr("id");
                        if (lCheckBoxId) {
                            /* investor : copy deal changes  */
                            var lAddAttr = true;
                            if (apz.products.product == "INVESTORS") {
                                var lCheckBoxULId = $("#" + lCheckBoxId).parents('ul')[0].getAttribute('id');
                                if (apz.products.sDealTypeChanged == "Y") {
                                    var lContainerID = apz.scrMetaData.elmsMap[lCheckBoxId].container;
                                    if ($("#" + lContainerID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                $("#" + lCheckBoxId).addClass("dataAvailable");
                                var lContainerId = apz.scrMetaData.elmsMap[lCheckBoxId].container;
                                var lDDNId = lContainerId + "_DDN";
                                var ddnObj = $("#" + lDDNId)[0];
                                //Checkbox-Radio Conversion//
                                var lRdoId = lContainerId + "_RDO";
                                var rdoObj = $("#" + lRdoId)[0];
                                if (!this.apz.isNull(ddnObj)) {
                                    $(ddnObj).parent().addClass("mltt");
                                    var availOpts = this.apz.getObjValue(ddnObj);
                                    var lExistingOptionsArray = lCheckBoxId;
                                    if (!this.apz.isNull(availOpts)) {
                                        lExistingOptionsArray = availOpts + "," + lExistingOptionsArray;
                                    }
                                    this.apz.setObjValue(ddnObj, lExistingOptionsArray);
                                } else if (!this.apz.isNull(rdoObj) && $(rdoObj).hasClass('customRadio')) {
                                    var rdoOpt = $("#" + lContainerId + "_RDO").find('input[id^=' + lCheckBoxId + ']');
                                    this.apz.setObjValue(rdoObj, rdoOpt.val());
                                }
                                isTriggered = true;
                                //$(childElement).trigger("click");
                                /* handling for read only mode : investor  */
                                // Investors: Performance related changes starts
                                if (childElement.get()[0].getAttribute("disabled") == "disabled") {
                                    childElement.get()[0].removeAttribute("disabled");
                                    childElement[0].click();
                                    //$(childElement.get()[0]).trigger("click");
                                    childElement.get()[0].setAttribute("disabled", "disabled");
                                } else {
                                    // $(childElement).trigger("click");
                                    for (let i = 0, childCount = childElement.length; i < childCount; i++) {
                                        if (apz.getElmValue(childElement[i].id) != "y") {
                                            childElement[i].click();
                                        }

                                    }
                                    // Investors: Performance related changes ends
                                }
                            }
                        }
                    } else {
                        //default prod with no checkbox
                        this.loadProductsData(lChildQuote);
                    }
                }
            } else {
                if (!(childquote.ProductId.indexOf("1-") > -1)) {
                    childquote.ProductId = "1-" + childquote.ProductId;
                }
                var lRelationCheckBoxId = childquote.ProductId;
                var childElement = $("#scr__" + apz.currAppId + "__" + productId + "_Scr__main").find("." + lRelationCheckBoxId).find(
                    "input[type=checkbox]");
                /* multiple containers having same checkbox( multi select options) : investors  */
                if (childElement.length > 1) {
                    var possibOrigId = childquote.ProdItemId.split("-")[1];
                    for (var c = 0; c < childElement.length; c++) {
                        var child = childElement[c];
                        var idFrags = child.id.split("__");
                        var domOrigId = idFrags[idFrags.length - 1];
                        if (domOrigId.indexOf(possibOrigId) > -1) {
                            childElement = $(child);
                            break;
                        };
                    }
                }
                protId = childquote.PortItemId.replace("1-", "");
                var prodId = childquote.ProductId.replace("1-", "");
                combinedId = lParentProdId + "_" + protId + "_" + prodId;
                let tempBackUp;
                var tempArrBackUp = [];
                //CCF Multirecord parent-child changes starts//
                if (!this.isNull(this.sDataMap[combinedId])) {
                    childquote.referOther = true;
                    tempBackUp = apz.products.sDataMap[combinedId];
                    if (this.apz.getDataType(tempBackUp) === "Array") {
                        for (let i = 0; i < tempBackUp.length; i++) {
                            tempArrBackUp.push(tempBackUp[i]);
                        }
                        if (this.isNull(parentQuoteItem.rowNo)) {

                            childquote.rowNo = !apz.isNull(childquote.rowNo) ? childquote.rowNo : tempBackUp.length;
                        } else {
                            childquote.rowNo = parentQuoteItem.rowNo;
                        }
                        tempArrBackUp.push(childquote);
                        this.sDataMap[combinedId] = tempArrBackUp;
                    } else {
                        tempBackUp.referOther = true;
                        if (this.isNull(tempBackUp.rowNo)) {
                            tempBackUp.rowNo = 0;
                            childquote.rowNo = 1;
                        } else {
                            childquote.rowNo = parentQuoteItem.rowNo;
                        }

                        this.sDataMap[combinedId] = [tempBackUp, childquote];
                    }
                } else {
                    if (parentQuoteItem.referOther) {
                        childquote.referOther = true;
                        childquote.rowNo = parentQuoteItem.rowNo;
                    }
                    this.sDataMap[combinedId] = [];

                    this.sDataMap[combinedId].push(childquote)//19thAug2020
                }

                //World Link SeqMapper:Deep changes

                var multiRecRow = [];
                    var quoteArr = [];
                    if (parentQuoteItem.QuoteItem) {
                        var parQuote = parentQuoteItem.QuoteItem;
                        for (var q = 0; q < parQuote.length; q++) {
                            if (parQuote[q].ProductId == lChildQuote.ProductId) {
                                quoteArr.push(parQuote[q]);
                            }
                        }
                        if (quoteArr.length > 0) {
                            for (var k = 0; k < quoteArr.length; k++) {
                                if (parentQuoteItem.nestLevel == undefined) {
                                    quoteArr[k].nestLevel = 1;
                                } else {
                                    quoteArr[k].nestLevel = parentQuoteItem.nestLevel + 1;
                                }
                            }
                        }
                        if (childquote.nestLevel == 1) {
                            multiRecRow = [childquote.rowNo, childquote.tabRowNo]
                        }
                        else if (childquote.nestLevel == 2) {
                            multiRecRow = [[childquote.rowNo, childquote.tabRowNo]];
                        } else if (childquote.nestLevel == 3) {
                            multiRecRow = [[childquote.rowNo, childquote.tabRowNo], multiRecMap[commonProdId]];
                        } else if (childquote.nestLevel >= 4) {
                            var parDefsMap = apz.scrDefsMap[apz.currAppId][productId + "_Scr__NewLayout__D0"];
                            if (parDefsMap && (parDefsMap.defaultProducts.includes(combinedId) && parDefsMap.subProducts.includes(combinedId))) {

                                multiRecRow = [[childquote.rowNo, childquote.tabRowNo], [childquote.subRow, 0]];
                            } else {
                                multiRecRow = [[childquote.rowNo, lChildchildquoteQuote.tabRowNo], [childquote.subRow, multiRecMap[commonProdId]]];
                            }
                        }
                        var mapperObj = this.idMapper1[combinedId + "_Scr"];
                        if (!mapperObj) {
                            mapperObj = this.idMapper1[combinedId + "_Scr"] = []
                        }
                        var seqObj = this.seqMapper1[combinedId + "_Scr"];
                        if (!seqObj) {
                            seqObj = this.seqMapper1[combinedId + "_Scr"] = []
                        }
                        if (multiRecRow.length >0) {
                            this.addToIdMapper(mapperObj, childquote, multiRecRow, false);
                            this.addToSeqMapper(seqObj, childquote, multiRecRow, false);
                        } else {
                            this.addToIdMapper(mapperObj, childquote, parentRec, false);
                            this.addToSeqMapper(seqObj, childquote, parentRec, false);
                        }
                    }

                //Deep changes ends

                //CCF Multirecord parent-child changes ends//
                this.updateIdMapper(combinedId, childquote, parentRec);
                ////////////////////19thAug2020/////////////////////
                if (apz.scrDefsMap[apz.currAppId][combinedId + "_Scr__NewLayout__D0"]) {
                    lDefaultProds = apz.scrDefsMap[apz.currAppId][combinedId + "_Scr__NewLayout__D0"]["defaultProducts"];
                }
                lIsDefaultProduct = false;
                if (lDefaultProds.length > 0) {
                    var lChildId = childquote.PortItemId.replace("1-", "") + "_" + childquote.ProductId.replace("1-", "")
                    for (var j = 0; j < lDefaultProds.length; j++) {
                        if (lDefaultProds[j].indexOf(lChildId) > -1) {
                            lIsDefaultProduct = true;
                            break;
                        }
                    }
                }
                /////////////////19thAug2020/////////////////
                if (!apz.isNull(childElement)) {
                    var lCheckBoxId = $(childElement).attr("id");
                    if (!apz.isNull(lCheckBoxId)) {
                        /* investor : copy deal changes  */
                        var lAddAttr = true;
                        if (apz.products.product == "INVESTORS") {
                            var lCheckBoxULId = $("#" + lCheckBoxId).parents('ul')[0].getAttribute('id');
                            if (apz.products.sDealTypeChanged == "Y") {
                                var lContainerID = apz.scrMetaData.elmsMap[lCheckBoxId].container;
                                if ($("#" + lContainerID).hasClass("sno")) {
                                    lAddAttr = false;
                                }
                            }
                        }
                        if (lAddAttr) {
                            $("#" + lCheckBoxId).addClass("dataAvailable");
                            var lContainerId = apz.scrMetaData.elmsMap[lCheckBoxId].container;
                            var lDDNId = lContainerId + "_DDN";
                            var ddnObj = $("#" + lDDNId)[0];
                            //Checkbox-Radio conversion//
                            var lRdoId = lContainerId + "_RDO";
                            var rdoObj = $("#" + lRdoId)[0];
                            if (!this.apz.isNull(ddnObj)) {
                                $(ddnObj).parent().addClass("mltt");
                                var availOpts = this.apz.getObjValue(ddnObj);
                                var lExistingOptionsArray = lCheckBoxId;
                                if (!this.apz.isNull(availOpts)) {
                                    lExistingOptionsArray = availOpts + "," + lExistingOptionsArray;
                                }
                                this.apz.setObjValue(ddnObj, lExistingOptionsArray);
                            } else if (!this.apz.isNull(rdoObj) && $(rdoObj).hasClass('customRadio')) {
                                var rdoOpt = $("#" + lContainerId + "_RDO").find('input[id^=' + lCheckBoxId + ']');
                                this.apz.setObjValue(rdoObj, rdoOpt.val());
                            }
                            isTriggered = true;
                            /* handling for read only mode : investor  */
                            // $(childElement).trigger("click");
                            // Investors: Performance related changes starts
                            if (childElement.get()[0].getAttribute("disabled") == "disabled") {
                                childElement.get()[0].removeAttribute("disabled");
                                $(childElement.get()[0]).trigger("click");
                                childElement.get()[0].setAttribute("disabled", "disabled");
                            }
                            // Investors: Performance related changes ends
                            else {
                                $(childElement).trigger("click");
                            }
                        }
                    } else if (childElement && !lIsDefaultProduct) {
                        if (!this.isNull(parentQuoteItem.rowNo)) {
                            childquote.rowNo = parentQuoteItem.rowNo;
                            childquote.subRow = parentQuoteItem.subRow;
                            childquote["tabRowNo"] = parentQuoteItem["tabRowNo"];
                        }
                        if (apz.isNull(childquote["tabRowNo"])) {
                            childquote["tabRowNo"] = parentQuoteItem["tabRowNo"];
                        }


                    } else {
                        this.loadProductsData(childquote, combinedId);
                    }
                } else {
                    this.loadProductsData(childquote, combinedId);
                }
            }
            if (!apz.isNull(combinedId) && isTriggered) {
                apz.data.buildData(productId + "_Intf");
            }
        }
    },
    loadProductsData: function (pProduct, prodId) {
        if (apz.getDataType(pProduct) === "Array") {
            for (var i = 0; i < pProduct.length; i++) {
                this.loadProductData(pProduct[i], prodId, i);
            }
        } else {
            this.loadProductData(pProduct, prodId);
        }
    },
    loadProductData: function (pProduct, lContainerId, parentRec) {
        this.QuoteIdBackUpMap[pProduct.ProductId] = pProduct.Id;
        var prdId = pProduct.ProductId.replace("1-", "");
        if (lContainerId) {
            prdId = lContainerId
        }
        var dataMap = {};
        if (apz.data.scrdata[apz.appId + "__" + prdId + "_Intf_Req"]) {
            var ifaceData = apz.data.scrdata[apz.appId + "__" + prdId + "_Intf_Req"];
            if ($.isArray(ifaceData)) {
                for (var i = 0; i < ifaceData.length; i++) {
                    for (var key in ifaceData[i]) {
                        if (apz.getDataType(ifaceData[i][key]) !== "Array" && apz.getDataType(ifaceData[i][key]) !== "Object") {
                            dataMap[key] = {};
                            dataMap[key].id = key;
                            dataMap[key].ExternalName = apz.products.getExternalName({
                                "scr": prdId + "_Scr",
                                "ifaceId": apz.appId + "__" + prdId + "_Intf_Req",
                                "elmName": key
                            });
                        }
                    }
                }
            } else {
                for (var key in apz.data.scrdata[apz.appId + "__" + prdId + "_Intf_Req"]) {
                    if (apz.getDataType(apz.data.scrdata[apz.appId + "__" + prdId + "_Intf_Req"][key]) !== "Array" &&
                        apz.getDataType(apz.data.scrdata[apz.appId + "__" + prdId + "_Intf_Req"][key]) !== "Object") {
                        dataMap[key] = {};
                        dataMap[key].id = key;
                        dataMap[key].ExternalName = apz.products.getExternalName({
                            "scr": prdId + "_Scr",
                            "ifaceId": apz.appId + "__" + prdId + "_Intf_Req",
                            "elmName": key
                        });
                    }
                }
            }
        } else {
            var lScrDataKeys = Object.keys(apz.data.scrdata);
            var lScrDataKeysLen = lScrDataKeys.length;
            var lProductRel = pProduct.PortItemId.replace("1-", "") + "_" + prdId;
            for (var i = 0; i < lScrDataKeysLen; i++) {
                if (lScrDataKeys[i].indexOf(lProductRel) > -1) {
                    prdId = lScrDataKeys[i];
                }
            }
            if (apz.data.scrdata[prdId]) {
                if (apz.getDataType(apz.data.scrdata[prdId]) == "Array") {
                    var lCurrProdRow = apz.data.scrdata[prdId];
                    for (var i = 0; i < lCurrProdRow.length; i++) {
                        for (var key in apz.data.scrdata[prdId][i]) {
                            if (apz.getDataType(apz.data.scrdata[prdId][i][key]) !== "Array" && apz.getDataType(apz.data.scrdata[prdId][i][key]) !== "Object") {
                                dataMap[key] = {};
                                dataMap[key].id = key;
                                dataMap[key].ExternalName = apz.products.getExternalName({
                                    "scr": prdId + "_Scr",
                                    "ifaceId": prdId,
                                    "elmName": key
                                });
                            }
                        }
                    }
                } else {
                    for (var key in apz.data.scrdata[prdId]) {
                        if (apz.getDataType(apz.data.scrdata[prdId][key]) !== "Array" && apz.getDataType(apz.data.scrdata[prdId][key]) !== "Object") {
                            dataMap[key] = {};
                            dataMap[key].id = key;
                            dataMap[key].ExternalName = apz.products.getExternalName({
                                "scr": prdId + "_Scr",
                                "ifaceId": prdId,
                                "elmName": key
                            });
                        }
                    }
                }
            } else if (lContainerId) {
                //// Create new node as no data exists
                this.createAttributes(pProduct, dataMap, lContainerId);
            }
        }
        if (lContainerId) {
            this.setAttributes(pProduct, dataMap, lContainerId, parentRec);
        } else {
            this.setAttributes(pProduct, dataMap, "", parentRec);
        }
    },
    setAttributes: function (pProduct, pDataMap, lContainerId, parentRec) {
        var prdId = pProduct.ProductId.replace("1-", "");
        if (lContainerId) {
            prdId = lContainerId;
        }
        var ifaceId = apz.appId + "__" + prdId + "_Intf_Req";
        if (!apz.data.scrdata[ifaceId]) {
            var lScrDataKeys = Object.keys(apz.data.scrdata);
            var lScrDataKeysLen = lScrDataKeys.length;
            var lProductRel = pProduct.PortItemId.replace("1-", "") + "_" + prdId;
            for (var i = 0; i < lScrDataKeysLen; i++) {
                if (lScrDataKeys[i].indexOf(lProductRel) > -1) {
                    prdId = lScrDataKeys[i];
                }
            }
        }
        if (pProduct["ListOfQuoteItemXA"]) {
            if (pProduct["ListOfQuoteItemXA"]["QuoteItemXa"]) {
                var lQuoteItemXa = pProduct["ListOfQuoteItemXA"]["QuoteItemXa"];
                var lNoOflQuoteItemXa = lQuoteItemXa.length;
                var ifaceDataType = apz.getDataType(apz.data.scrdata[ifaceId]);
                if (ifaceDataType == "Array") {
                    var lRow = apz.data.scrdata[ifaceId].length;
                    //CCF Multirecord parent-child changes starts//
                    if (pProduct.referOther) {
                        let recNo = !this.isNull(pProduct.parentRec) ? pProduct.parentRec : pProduct.rowNo;

                        if (this.gDulipcateSubProductData.length > 0) {
                            let parentRefKey = this.getReferOtherKey(prdId);
                            let parentIface = apz.currAppId + "__" + parentRefKey + "_Intf_Req";
                            let gDupSpecificData = this.getSpecificDupSubPrdData(parentIface, this.gDulipcateSubProductData);
                            if (!this.isNull(gDupSpecificData[pProduct.rowNo])) {
                                if (!this.isNull(gDupSpecificData[pProduct.rowNo].data[parentIface][pProduct.tabRowNo])) {
                                    let isOnlySubPrd = this.checkOnlySubPrd(prdId);
                                    var parentArray = this.getQueryParArray(prdId);
                                    if (isOnlySubPrd) {
                                        let subPrdIfaceName = apz.currAppId + "__" + parentArray[parentArray.length - 1] + "_Intf_Req";
                                        if (this.isNull(gDupSpecificData[pProduct.rowNo].data[parentIface][pProduct.tabRowNo][subPrdIfaceName][pProduct.subRow][ifaceId])) {
                                            lRow = 0;
                                            // handling multiple list inside list
                                            if ((pProduct.subRow > 0 || pProduct.tabRowNo > 0 || pProduct.rowNo > 0) && Object.keys(pDataMap).length > 0) {
                                                apz.data.scrdata[ifaceId].length = 1;
                                                /**
                                                     Manual construction of scrData and updating Rel values to empty
                                                */
                                                this.modifyScrData(ifaceId, prdId, lRow);
                                            }
                                        } else {
                                            lRow = gDupSpecificData[pProduct.rowNo].data[parentIface][pProduct.tabRowNo][subPrdIfaceName][pProduct.subRow][ifaceId].length;
                                        }

                                    } else {
                                        if (this.isNull(gDupSpecificData[pProduct.rowNo].data[parentIface][pProduct.tabRowNo][ifaceId])) {
                                            lRow = 0;
                                            if (pProduct.parentRec > 0 && Object.keys(pDataMap).length > 0) {
                                                apz.data.scrdata[ifaceId].length = 1;
                                                /**
                                                  Manual construction of scrData and updating Rel values to empty
                                                 */
                                                this.modifyScrData(ifaceId, prdId, lRow);
                                            }
                                        } else {
                                            lRow = gDupSpecificData[pProduct.rowNo].data[parentIface][pProduct.tabRowNo][ifaceId].length;
                                        }
                                    }

                                }
                            } else {
                                lRow = 0;
                                apz.data.scrdata[ifaceId].length = 1;
                                /**
                                Manual construction of scrData and updating Rel values to empty
                               */
                                this.modifyScrData(ifaceId, prdId, lRow);
                            }

                        }
                    }
                    if (!apz.data.scrdata[ifaceId][lRow] && Object.keys(pDataMap).length > 0) {
                        if (lRow != 0) {
                            apz.data.scrdata[ifaceId][lRow] = apz.copyJSONObject(apz.data.scrdata[ifaceId][lRow - 1]);

                            this.modifyScrData(ifaceId, prdId, lRow);

                        } else {
                            apz.data.scrdata[ifaceId][lRow] = {};
                        }

                    }
                    //CCF Multirecord parent-child changes ends//
                } else if (ifaceDataType == "Object") {
                    var lValue = lQuoteItemXa.Value;
                    var lAttr = lQuoteItemXa.Attribute;
                    var elmId = apz.scrMetaData.elmsExtMap[apz.appId + "__" + prdId + "_Scr__" + lAttr];
                    if (elmId) {
                        var elmObj = apz.scrMetaData.elmsMap[elmId];
                        var internalName = elmObj.name;
                        /* investor : copy deal changes  */
                        var lAddAttr = true;
                        if (apz.products.product == "INVESTORS") {
                            var lElementID = elmObj.id;
                            lElementID = $("#" + lElementID).parents('ul')[0].getAttribute('id');
                            if (apz.products.sDealTypeChanged == "Y") {
                                if ($("#" + lElementID).hasClass("sno")) {
                                    lAddAttr = false;
                                }
                            }
                        }
                        if (lAddAttr) {
                            apz.data.scrdata[ifaceId][internalName] = lValue;
                            if (apz.products.product == "INVESTORS") {
                                if (!apz.isNull(lValue)) {
                                    apz.setElmValue(elmObj.id, lValue);
                                    $("#" + elmObj.id).addClass("dataAvailable");
                                }
                            }
                        }
                    }
                }
                for (var i = 0; i < lNoOflQuoteItemXa; i++) {
                    var lValue = lQuoteItemXa[i].Value;
                    var lAttr = lQuoteItemXa[i].Attribute;
                    var elmId = apz.scrMetaData.elmsExtMap[apz.appId + "__" + prdId + "_Scr__" + lAttr];
                    if (elmId && Object.keys(pDataMap).length > 0) {
                        var elmObj = apz.scrMetaData.elmsMap[elmId];
                        var internalName = elmObj.name;
                        /* investor : copy deal changes  */
                        var lAddAttr = true;
                        if (apz.getDataType(apz.data.scrdata[ifaceId]) == "Array") {
                            if (apz.products.product == "INVESTORS") {
                                var lElementID = elmObj.id + "_" + lRow;
                                if (apz.products.sDealTypeChanged == "Y") {
                                    if ($("#" + lElementID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                apz.data.scrdata[ifaceId][lRow][internalName] = lValue;
                                if (apz.products.product == "INVESTORS") {
                                    if (!apz.isNull(lValue)) {
                                        $("#" + elmObj.id + "_" + lRow).addClass("dataAvailable");
                                    }
                                }
                            }
                        } else {
                            if (apz.products.product == "INVESTORS") {
                                var lElementID = elmObj.id;
                                lElementID = $("#" + lElementID).parents('ul')[0].getAttribute('id');
                                if (apz.products.sDealTypeChanged == "Y") {
                                    if ($("#" + lElementID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                apz.data.scrdata[ifaceId][internalName] = lValue;
                                if (apz.products.product == "INVESTORS") {
                                    if (!apz.isNull(lValue)) {
                                        apz.setElmValue(elmObj.id, lValue);
                                        $("#" + elmObj.id).addClass("dataAvailable");
                                    }
                                }
                            }
                        }
                    }
                }
                //Deep changes if lQuoteItemXa is an object: case handled now
                if (apz.getDataType(lQuoteItemXa) == "Object") {
                    var lValue = lQuoteItemXa.Value;
                    var lAttr = lQuoteItemXa.Attribute;
                    var elmId = apz.scrMetaData.elmsExtMap[apz.appId + "__" + prdId + "_Scr__" + lAttr];
                    if (elmId && Object.keys(pDataMap).length > 0) {
                        var elmObj = apz.scrMetaData.elmsMap[elmId];
                        var internalName = elmObj.name;
                        /* investor : copy deal changes  */
                        var lAddAttr = true;
                        if (apz.getDataType(apz.data.scrdata[ifaceId]) == "Array") {
                            if (apz.products.product == "INVESTORS") {
                                var lElementID = elmObj.id + "_" + lRow;
                                if (apz.products.sDealTypeChanged == "Y") {
                                    if ($("#" + lElementID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                apz.data.scrdata[ifaceId][lRow][internalName] = lValue;
                                if (apz.products.product == "INVESTORS") {
                                    if (!apz.isNull(lValue)) {
                                        $("#" + elmObj.id + "_" + lRow).addClass("dataAvailable");
                                    }
                                }
                            }
                        } else {
                            if (apz.products.product == "INVESTORS") {
                                var lElementID = elmObj.id;
                                lElementID = $("#" + lElementID).parents('ul')[0].getAttribute('id');
                                if (apz.products.sDealTypeChanged == "Y") {
                                    if ($("#" + lElementID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                apz.data.scrdata[ifaceId][internalName] = lValue;
                                if (apz.products.product == "INVESTORS") {
                                    if (!apz.isNull(lValue)) {
                                        apz.setElmValue(elmObj.id, lValue);
                                        $("#" + elmObj.id).addClass("dataAvailable");
                                    }
                                }
                            }
                        }
                    }
                }
                //CCF Multirecord parent-child changes starts//
                if (pProduct.referOther) {
                    let tempObj = {};
                    tempObj.rowNo = pProduct.rowNo;
                    tempObj.data = {};
                    let TabRow = pProduct["tabRowNo"];
                    if (apz.getDataType(apz.data.scrdata[ifaceId]) === "Object") {
                        tempObj.data[ifaceId] = $.extend(true, {}, apz.data.scrdata[ifaceId]);
                    } else {
                        tempObj.data[ifaceId] = $.extend(true, [], apz.data.scrdata[ifaceId]);
                    }
                    //Deep changes to create gDulipcate array if iface is not present.
                    let gDupSubObj = this.createGDubSubProd(ifaceId, pProduct, prdId);
                    let existObj = gDupSubObj.find(function (eachData) {
                        return eachData.rowNo == tempObj.rowNo
                    });
                    let refOtherIfaceId = apz.currAppId + "__" + this.getReferOtherKey(prdId) + "_Intf_Req";
                    if (this.isNull(existObj)) {
                        gDupSubObj.push(tempObj);
                    } else {
                        if (gDupSubObj[tempObj.rowNo].data[refOtherIfaceId][TabRow] == undefined) {
                            //gDupSubObj[tempObj.rowNo].data[refOtherIfaceId][TabRow] = tempObj.data[ifaceId][TabRow];
                            //CCF Changes added by Siva
                            if (tempObj.data[ifaceId][TabRow] == undefined) {
                                gDupSubObj[tempObj.rowNo].data[ifaceId] = tempObj.data[ifaceId];
                            } else {
                                gDupSubObj[tempObj.rowNo].data[refOtherIfaceId][TabRow] = tempObj.data[ifaceId][TabRow];
                            }
                        } else {
                            var parentArray = this.getQueryParArray(prdId);
                            var isOnlySubPrd = this.checkOnlySubPrd(prdId);
                            if (isOnlySubPrd) {
                                var subPrdIfaceName = apz.currAppId + "__" + parentArray[parentArray.length - 1] + "_Intf_Req";
                                if (!apz.isNull(gDupSubObj[tempObj.rowNo].data[refOtherIfaceId][TabRow][subPrdIfaceName])) {
                                    let gSubObj = gDupSubObj[tempObj.rowNo].data[refOtherIfaceId][TabRow][subPrdIfaceName];
                                    if (!apz.isNull(gSubObj[pProduct.subRow])) {
                                        gSubObj[pProduct.subRow][ifaceId] = tempObj.data[ifaceId];
                                    }

                                } else {
                                    gDupSubObj[tempObj.rowNo].data[refOtherIfaceId][TabRow][ifaceId] = tempObj.data[ifaceId];
                                }
                            } else {
                                gDupSubObj[tempObj.rowNo].data[refOtherIfaceId][TabRow][ifaceId] = tempObj.data[ifaceId];
                            }
                        }
                    }

                }
                //CCF Multirecord parent-child changes ends//
            }
        }
        if (apz.isFunction(apz.app.postSetAttributes)){
            apz.app.postSetAttributes(prdId);
        }
        if (apz.data.scrdata[apz.appId + "__" + prdId + "_Intf_Req"]) {
            apz.data.loadData(prdId + "_Intf", this.apz.currAppId);
        } else {
            apz.data.loadData(null);
        }
        if (pProduct.QuoteItem) {
            this.loadSubProducts(pProduct, prdId, parentRec);
        }
    },
    createAttributes: function (pProduct, pDataMap, prdId) {
        if (!apz.data.scrdata[apz.appId + "__" + prdId + "_Intf_Req"]) {
            var lScrDataKeys = Object.keys(apz.data.scrdata);
            var lScrDataKeysLen = lScrDataKeys.length;
            var lProductRel = pProduct.PortItemId.replace("1-", "") + "_" + prdId;
            for (var i = 0; i < lScrDataKeysLen; i++) {
                if (lScrDataKeys[i].indexOf(lProductRel) > -1) {
                    prdId = lScrDataKeys[i];
                }
            }
        }
        var nodeName = apz.appId + "__" + prdId + "_Intf_Req";
        var nodeId = apz.getNodeId({
            "iface": apz.appId + "__" + prdId + "_Intf",
            "dml": "REQ",
            "node": nodeName
        });
        var nodeDtls = apz.scrMetaData.nodesMap[nodeId];
        if (nodeDtls && nodeDtls.multiRec == "Y") {
            apz.data.scrdata[nodeName] = [];
        } else {
            apz.data.scrdata[nodeName] = {};
        }
        var quoteItem = pProduct["ListOfQuoteItemXA"];
        if (quoteItem) {
            if (apz.getDataType(quoteItem) === "Array") {
                for (var i = 0; i < quoteItem.length; i++) {
                    this.createAttribute(prdId, quoteItem[i]["QuoteItemXa"], nodeName);
                }
            } else {
                this.createAttribute(prdId, quoteItem["QuoteItemXa"], nodeName);
            }
        }
    },
    createAttribute: function (prdId, quoteItemXa, nodeName) {
        if (quoteItemXa) {
            var lNoOflQuoteItemXa = quoteItemXa.length;
            if (apz.getDataType(apz.data.scrdata[nodeName]) == "Array") {
                var lRow = apz.data.scrdata[nodeName].length;
                if (!apz.data.scrdata[nodeName][lRow]) {
                    apz.data.scrdata[nodeName][lRow] = {};
                }
            }
            for (var i = 0; i < lNoOflQuoteItemXa; i++) {
                var lValue = quoteItemXa[i].Value;
                var lAttr = quoteItemXa[i].Attribute;
                var elmId = apz.scrMetaData.elmsExtMap[apz.appId + "__" + prdId + "_Scr__" + lAttr];
                if (elmId) {
                    var elmObj = apz.scrMetaData.elmsMap[elmId];
                    var internalName = elmObj.name;
                    /* investor : copy deal changes  */
                    var lAddAttr = true;
                    if (apz.data.scrdata[apz.appId + "__" + nodeName + "_Intf_Req"]) {
                        if (apz.products.product == "INVESTORS") {
                            var lElementID = elmObj.id;
                            lElementID = $("#" + lElementID).parents('ul')[0].getAttribute('id');
                            if (apz.products.sDealTypeChanged == "Y") {
                                if ($("#" + lElementID).hasClass("sno")) {
                                    lAddAttr = false;
                                }
                            }
                        }
                        if (lAddAttr) {
                            apz.data.scrdata[apz.appId + "__" + nodeName + "_Intf_Req"][internalName] = lValue;
                            if (apz.products.product == "INVESTORS") {
                                if (!apz.isNull(lValue)) {
                                    $("#" + elmObj.id).addClass("dataAvailable");
                                }
                            }
                        }
                    } else {
                        if (apz.getDataType(apz.data.scrdata[nodeName]) == "Array") {
                            if (apz.products.product == "INVESTORS") {
                                var lElementID = elmObj.id + "_" + lRow;
                                if (apz.products.sDealTypeChanged == "Y") {
                                    if ($("#" + lElementID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                apz.data.scrdata[nodeName][lRow][internalName] = lValue;
                                if (!apz.isNull(lValue)) {
                                    $("#" + elmObj.id + "_" + lRow).addClass("dataAvailable");
                                }
                            }
                        } else {
                            if (apz.products.product == "INVESTORS") {
                                var lElementID = elmObj.id;
                                lElementID = $("#" + lElementID).parents('ul')[0].getAttribute('id');
                                if (apz.products.sDealTypeChanged == "Y") {
                                    if ($("#" + lElementID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                apz.data.scrdata[nodeName][internalName] = lValue;
                                if (!apz.isNull(lValue)) {
                                    $("#" + elmObj.id).addClass("dataAvailable");
                                }
                            }
                        }
                    }
                }
            }
            //Deep changes : if quoteItemXa is an object: case is handled.
            if (apz.getDataType(quoteItemXa) == "Object") {
                var lValue = quoteItemXa.Value;
                var lAttr = quoteItemXa.Attribute;
                var elmId = apz.scrMetaData.elmsExtMap[apz.appId + "__" + prdId + "_Scr__" + lAttr];
                if (elmId) {
                    var elmObj = apz.scrMetaData.elmsMap[elmId];
                    var internalName = elmObj.name;
                    /* investor : copy deal changes  */
                    var lAddAttr = true;
                    if (apz.data.scrdata[apz.appId + "__" + nodeName + "_Intf_Req"]) {
                        if (apz.products.product == "INVESTORS") {
                            var lElementID = elmObj.id;
                            lElementID = $("#" + lElementID).parents('ul')[0].getAttribute('id');
                            if (apz.products.sDealTypeChanged == "Y") {
                                if ($("#" + lElementID).hasClass("sno")) {
                                    lAddAttr = false;
                                }
                            }
                        }
                        if (lAddAttr) {
                            apz.data.scrdata[apz.appId + "__" + nodeName + "_Intf_Req"][internalName] = lValue;
                            if (apz.products.product == "INVESTORS") {
                                if (!apz.isNull(lValue)) {
                                    $("#" + elmObj.id).addClass("dataAvailable");
                                }
                            }
                        }
                    } else {
                        if (apz.getDataType(apz.data.scrdata[nodeName]) == "Array") {
                            if (apz.products.product == "INVESTORS") {
                                var lElementID = elmObj.id + "_" + lRow;
                                if (apz.products.sDealTypeChanged == "Y") {
                                    if ($("#" + lElementID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                apz.data.scrdata[nodeName][lRow][internalName] = lValue;
                                if (!apz.isNull(lValue)) {
                                    $("#" + elmObj.id + "_" + lRow).addClass("dataAvailable");
                                }
                            }
                        } else {
                            if (apz.products.product == "INVESTORS") {
                                var lElementID = elmObj.id;
                                lElementID = $("#" + lElementID).parents('ul')[0].getAttribute('id');
                                if (apz.products.sDealTypeChanged == "Y") {
                                    if ($("#" + lElementID).hasClass("sno")) {
                                        lAddAttr = false;
                                    }
                                }
                            }
                            if (lAddAttr) {
                                apz.data.scrdata[nodeName][internalName] = lValue;
                                if (!apz.isNull(lValue)) {
                                    $("#" + elmObj.id).addClass("dataAvailable");
                                }
                            }
                        }
                    }
                }

            }
        }
    },
    isProductLaunched: function (appId, subProductId) {
        var value = true;
        var backTraverseProduct = [];
        var receivedProdId = subProductId;
        while (value) {
            var layout = this.apz.getLayout({ "appId": appId, "scr": subProductId + "_Scr" });
            var currentDesign = this.apz.getDesigns({ "appId": appId, "scr": subProductId + "_Scr", "layout": layout }).currentDesign;
            var screenDefKey = subProductId + "_Scr" + "__" + layout + "__" + currentDesign;
            var defObj = apz.scrDefsMap[appId][screenDefKey];
            if (defObj) {
                var currentScreenDef = defObj;
                subProductId = currentScreenDef.combinedParentId;
                if (this.apz.isNull(subProductId)) {
                    value = false;
                } else {
                    value = true;
                    backTraverseProduct.push(subProductId);
                }
            } else {
                backTraverseProduct = [];
                break;
            }
        }
        backTraverseProduct = backTraverseProduct.reverse();
        var tempProductList = this.productsMap;
        for (var i = 0; i < backTraverseProduct.length; i++) {
            tempProductList = tempProductList[backTraverseProduct[i]];
        }
        if (backTraverseProduct.length > 0 && apz.scrDefsMap[appId][backTraverseProduct[backTraverseProduct.length - 1] + "_Scr__NewLayout__D0"] && apz.scrDefsMap[appId][backTraverseProduct[backTraverseProduct.length - 1] + "_Scr__NewLayout__D0"]["containersMap"]) {
            var temp = apz.scrDefsMap[appId][backTraverseProduct[backTraverseProduct.length - 1] + "_Scr__NewLayout__D0"]["containersMap"];
            if (temp[Object.keys(temp)[0]].referOther) {
                return true;
            } else {
                return tempProductList[receivedProdId] ? true : false;
            }
        } else {
            return tempProductList[receivedProdId] ? true : false;
        }
    },
    getMaxSeqNumber: function (params) {
        let url, method, reqStr;
        /* shyam changes  */
        if (!apz.isNull(params) && params.product == "INVESTORS") {
            let payload = {
                "body": {
                    "QuoteId": apz.products.HeaderId,
                    "InputMethodName": "GetMaxSequenceNum"
                }
            };
            url = apz.products.urlConfigMap.getMaxSequenceNumber;
            method = "POST";
            reqStr = JSON.stringify(payload);
        }
        let apzObj = this;
        let lparams = {
            "url": url,
            "method": method,
            "reqStr": reqStr || {},
            "product": params.product || "",
            successCB: function (data) {
                if (!apz.isNull(params) && params.product == "INVESTORS") {
                    apz.products.SeqNum = parseInt(data.MaxSeqNum) + 1;
                }
            },
            failureCB: function (data) {
                if (params.product == "INVESTORS") {
                    if (apz.isFunction(apz[apz.currAppId].postGetMaxSeqFailure)) {
                        apz[apz.currAppId].postGetMaxSeqFailure();
                    } else {
                        apz.dispMsg({
                            "Type": "E",
                            "message": "Failed In Get Max SequenceNumber"
                        });
                    }
                }
            }
        };
        this.callServer(lparams);
    },
    /**
     * Method with reusable ajax code
     */
    callServer: function (params) {
        if (params.url) {
            var lParams = {
                url: params.url,
                method: params.method || "POST",
                cache: false,
                data: params.reqStr || {},
                contentType: "application/json;charset=UTF-8",
                dataType: 'json',
                async: params.async || false,
                success: params.successCB,
                error: params.failureCB
            }
            if (params.product == "INVESTORS") {
                lParams.headers = {
                    "token": apz.products.accessToken
                }
            } else {
                lParams.beforeSend = function (xhr) {
                    xhr.setRequestHeader('access_token', apz.products.accessToken)
                }
            }
            $.ajax(lParams);
        }
    },
    attachPercentCompletedEvent: function (pProduct, statusDivId) {
        var lCurrentProductDiv = pProduct;
        $('#' + lCurrentProductDiv).on("change", 'input', function () {
            setTimeout(function () {
                apz.products.processDiv(lCurrentProductDiv, statusDivId);
            }, 100);
        });
        $('#' + lCurrentProductDiv).on("change", "textarea", function () {
            apz.products.processDiv(lCurrentProductDiv, statusDivId);
        });
        $('#' + lCurrentProductDiv).on("change", 'select', function () {
            setTimeout(function () {
                apz.products.processDiv(lCurrentProductDiv, statusDivId);
            }, 100);
        });
        $('#' + lCurrentProductDiv).on("change", 'input[type="Tel"]', function () {
            var elmObj = apz.scrMetaData.elmsMap[this.id];
            if (elmObj) {
                apz.scrMetaData.elmsMap[this.id].displayAsLiteral = "N";
            }
        });
        (function ($) {
            $.each(['show', 'hide'], function (i, ev) {
                var el = $.fn[ev];
                $.fn[ev] = function () {
                    this.trigger(ev);
                    return el.apply(this, arguments);
                };
            });
        })(jQuery);
        $('#' + lCurrentProductDiv + ' input').on("show", function () {
            apz.products.processDiv(lCurrentProductDiv, statusDivId);
        });
        $('#' + lCurrentProductDiv + ' input').on("hide", function () {
            apz.products.processDiv(lCurrentProductDiv, statusDivId);
        });
    },
    /* investor : dropdown search  provided by sachin  */
    initDropdownOnFocusKeypress: function (pId) {
        var val = "";
        $("#" + pId).on('keydown', function (e) {
            var drdnDivObj = $(this).parents("#" + this.id + "_ext:first");
            if (/[a-zA-Z0-9]/.test(String.fromCharCode(e.keyCode))) {
                if (!(val.length == '1' && val == String.fromCharCode(e.keyCode))) {
                    val = val + String.fromCharCode(e.keyCode);
                }
                clearTimeout($.data(this, 'timer'));
                var wait = setTimeout(function () {
                    val = "";
                }, 500);
                $(this).data('timer', wait);
                searchDrdn(drdnDivObj, this);
            } else {
                //setDropdownVal(drdnDivObj,this);
            }
        });
        var searchDrdn = function (curObj, event) {
            var value = val;
            var proceed = true;
            var totalOpts = $(curObj).find('li').length;
            var index = 0;
            var getNextOpt = function (actObj, currOpt, nextOptObj, curValue) {
                nextOpt = $(nextOptObj).next('li');
                if (nextOptObj.text().toUpperCase().indexOf(curValue) != -1 && $(nextOptObj).text().toUpperCase().indexOf(curValue) == '0') {
                    $(nextOptObj).parent().find('.is-selected').removeClass('is-selected');
                    nextOptObj.addClass('is-selected').focus();
                    $(actObj).val(nextOptObj.text());
                    if ($(actObj).attr("onchange")) {
                        $(actObj).trigger("change");
                    }
                    proceed = false;
                } else if ($(nextOptObj).next('li').length == 0 && index < totalOpts) {
                    nextOpt = $(nextOptObj).parent().children('li:first');
                    index = index + 1;
                    getNextOpt(actObj, nextOptObj, nextOpt, curValue);
                } else {
                    if (index < totalOpts) {
                        index = index + 1;
                        getNextOpt(actObj, nextOptObj, nextOpt, curValue);
                    }
                }
            }
            if (proceed) {
                var nextOpt = $(curObj).next('li');
                if (nextOpt.length == 0) {
                    nextOpt = $(curObj).find('li:first');
                }
                getNextOpt(event, curObj, nextOpt, value);
            }
        }
    },
    processDiv: function (pProductDiv, statusDiv) {
        var countOfEmpty = 0;
        var countOfFilled = 0;
        var lCurrentProductDiv = pProductDiv;
        $('#' + lCurrentProductDiv + ' input[type="text"],#' + lCurrentProductDiv + ' input[type="tel"]').each(function (index) {
            var lCurrId = $(this).attr("id");
            var lParentIdCheck = $("#" + this.id).parents("ul").prop("tagName")
            if (lParentIdCheck != "UL") {
                var lMandatoryCheck = $("#" + this.id).attr("Required");
                if (!apz.isNull(lMandatoryCheck)) {
                    var lRowId = $("#" + this.id).parents("tr")[0].id;
                    if (($("#" + lRowId).hasClass("sno") == false && ($("#" + lRowId).hasClass("ssp")) == false) && ($("#" + lRowId).is(
                        ":visible"))) {
                        if ($("#" + this.id).val() == "") {
                            $(this).removeClass("borderGreen");
                            $(this).addClass("borderRed");
                            countOfEmpty++;
                        } else {
                            $(this).removeClass("borderRed");
                            $(this).addClass("borderGreen");
                            countOfFilled++;
                        }
                    }
                }
            }
            // Investors: process div changes starts here for inputbox
            else if (lParentIdCheck == "UL" && $("#" + lCurrId).attr('rowno') != undefined) {
                var lMandatoryCheck = $("#" + this.id).attr("Required");
                if (!apz.isNull(lMandatoryCheck)) {
                    var lRowId = $("#" + this.id).parents("li")[0].id;
                    if (($("#" + lRowId).hasClass("sno") == false && ($("#" + lRowId).hasClass("ssp")) == false) && ($("#" + lRowId).is(
                        ":visible"))) {
                        if ($("#" + this.id).val() == "") {
                            $(this).removeClass("borderGreen");
                            $(this).addClass("borderRed");
                            countOfEmpty++;
                        } else {
                            $(this).removeClass("borderRed");
                            $(this).addClass("borderGreen");
                            countOfFilled++;
                        }
                    }
                }
            }
            // Investors: process div changes ends here for inputbox
            else {
                if (!$(this).closest("ul").hasClass("sno") && $(this).closest("ul").css("height") != "0px") {
                    var lCurrLabel = lCurrId + "_grp_lbl";
                    // Investors: process div changes 
                    if ($("#" + lCurrLabel).length > 0) {
                        if ($("#" + lCurrLabel).hasClass("req")) {
                            if ($("#" + this.id).val() == "" || $("#" + this.id).val() == "PS") {
                                $(this).removeClass("borderGreen");
                                $(this).addClass("borderRed");
                                countOfEmpty++;
                            } else {
                                $(this).removeClass("borderRed");
                                $(this).addClass("borderGreen");
                                countOfFilled++;
                            }
                        }
                    }
                }
            }
        });
        $('#' + lCurrentProductDiv + ' textarea').each(function (index) {
            var lParentIdCheck = $("#" + this.id).parents("ul").prop("tagName");
            // Investors: process div changes for textarea
            var lCurrId = $(this).attr("id");
            if (lParentIdCheck != "UL" && $("#" + this.id).is(":visible")) {
                if ($("#" + this.id).val() == "") {
                    if ($("#" + this.id).hasClass("req")) {
                        $(this).removeClass("borderGreen");
                        $(this).addClass("borderRed");
                        countOfEmpty++;
                    }
                } else {
                    if ($("#" + this.id).hasClass("req")) {
                        $(this).removeClass("borderRed");
                        $(this).addClass("borderGreen");
                        countOfFilled++;
                    }
                }
            }
            // Investors: process div changes starts here for textarea
            else if (lParentIdCheck == "UL" && $("#" + lCurrId).attr('rowno') != undefined) {
                var lRowId = $("#" + this.id).parents("li")[0].id;
                if (($("#" + lRowId).hasClass("sno") == false && ($("#" + lRowId).hasClass("ssp")) == false) && ($("#" + lRowId).is(":visible"))) {
                    if ($("#" + this.id).val() == "") {
                        if ($("#" + this.id).hasClass("req")) {
                            $(this).removeClass("borderGreen");
                            $(this).addClass("borderRed");
                            countOfEmpty++;
                        }
                    } else {
                        if ($("#" + this.id).hasClass("req")) {
                            $(this).removeClass("borderRed");
                            $(this).addClass("borderGreen");
                            countOfFilled++;
                        }
                    }
                }
            }
            // Investors: process div changes ends here for textarea
            else {
                if (!$(this).closest("ul").hasClass("sno") && $(this).closest("ul").css("height") != "0px") {
                    var lCurrId = $(this).attr("id");
                    var lCurrLabel;
                    if ($(lCurrId).attr("rowno") != false && !$("#td_" + lCurrId).hasClass("sno")) {
                        lCurrLabel = lCurrId;
                    } else {
                        lCurrLabel = lCurrId + "_grp_lbl";
                    }
                    /** added by harish. TBC with shyam **/
                    if (lCurrLabel && lCurrLabel.indexOf("_grp_lbl") == -1) {
                        lCurrLabel = lCurrId + "_grp_lbl"
                    }
                    if ($("#" + this.id).val() == "") {
                        if ($("#" + lCurrLabel).hasClass("req")) {
                            $(this).removeClass("borderGreen");
                            $(this).addClass("borderRed");
                            countOfEmpty++;
                        }
                    } else {
                        if ($("#" + lCurrLabel).hasClass("req")) {
                            $(this).removeClass("borderRed");
                            $(this).addClass("borderGreen");
                            countOfFilled++;
                        }
                    }
                }
            }
        });
        $('#' + lCurrentProductDiv + ' select').each(function (index) {
            var lCurrId = $(this).attr("id");
            var lId = $("#" + lCurrId).parents("ul")[0].id;
            var lRowId = $("#" + lId).parent().attr("id");
            if (($("#" + lRowId).hasClass("sno") == false && ($("#" + lRowId).hasClass("ssp")) == false) && ($("#" + lRowId).is(":visible")) &&
                ($("#" + lRowId).css("visibility") != "hidden")) {
                if ($("#" + lCurrId).val().length != 0) {
                    if ($("#" + lCurrId).next().find(".select2-selection--multiple").hasClass("borderRed") || $("#" + lCurrId).next().find(".select2-selection--multiple").hasClass("borderGreen")) {
                        $("#" + lId).find(".select2-selection--multiple").removeClass("borderRed");
                        $("#" + lId).find(".select2-selection--multiple").addClass("borderGreen");
                        countOfFilled++;
                    }
                } else {
                    if ($("#" + lCurrId).next().find(".select2-selection--multiple").hasClass("borderRed") || $("#" + lCurrId).next().find(".select2-selection--multiple").hasClass("borderGreen")) {
                        $("#" + lId).find(".select2-selection--multiple").removeClass("borderGreen");
                        $("#" + lId).find(".select2-selection--multiple").addClass("borderRed");
                        countOfEmpty++;
                    }
                }
            } else if ($("#" + lCurrId).hasClass("listMultiselect") && $("#" + lCurrId).is(":visible")) {
                if ($("#" + lCurrId).val().length != 0 || $("#" + lCurrId).hasClass("lCount")) {
                    //if ($("#" + lCurrId).next().find(".select2-selection--multiple").hasClass("borderRed") || $("#" + lCurrId).next().find(".select2-selection--multiple").hasClass("borderGreen")) {
                    $("#" + lCurrId).next().find(".select2-selection--multiple").removeClass("borderRed");
                    $("#" + lCurrId).next().find(".select2-selection--multiple").addClass("borderGreen");
                    countOfFilled++;
                    //}
                } else {
                    //if ($("#" + lCurrId).next().find(".select2-selection--multiple").hasClass("borderRed") || $("#" + lCurrId).next().find(".select2-selection--multiple").hasClass("borderGreen")) {
                    $("#" + lCurrId).next().find(".select2-selection--multiple").removeClass("borderGreen");
                    $("#" + lCurrId).next().find(".select2-selection--multiple").addClass("borderRed");
                    countOfEmpty++;
                    //}
                }
            }
            // Investors: process div changes ends here for multiselect
        });
        $('#' + lCurrentProductDiv + ' span[apztype="radiogroup"]').each(function (index) {
            if ((!$(this).closest("ul").hasClass("sno")) && $(this).closest("ul").css("height") != "0px") {
                var lCurrId = $(this).attr("id");
                var lCurrLabel = lCurrId + "_grp_lbl";
                if ($("#" + lCurrLabel).hasClass("req")) {
                    if (apz.getElmValue(this.id) == "") {
                        $(this).removeClass("borderGreen");
                        $(this).addClass("borderRed");
                        countOfEmpty++;
                    } else {
                        $(this).removeClass("borderRed");
                        $(this).addClass("borderGreen");
                        countOfFilled++;
                    }
                }
                // Investors: process div changes starts here for radio
                else if ($("#" + lCurrId).hasClass("listRadio")) { //ListRadio
                    if (apz.getElmValue(lCurrId) == "") {
                        $("#" + lCurrId).removeClass("borderGreen");
                        $("#" + lCurrId).addClass("borderRed");
                        countOfEmpty++;
                    } else {
                        $("#" + lCurrId).removeClass("borderRed");
                        $("#" + lCurrId).addClass("borderGreen");
                        countOfFilled++;
                    }
                }
                // Investors: process div changes ends here for radio
                else if ($(this).hasClass("customRadio")) { //Checkbox to radio conversion
                    if (apz.getObjValue($(this)[0]) == "") {
                        $(this).removeClass("borderGreen");
                        $(this).addClass("borderRed");
                        countOfEmpty++;
                    } else {
                        $(this).removeClass("borderRed");
                        $(this).addClass("borderGreen");
                        countOfFilled++;
                    }
                }
            }
        });
        var lTotalQues = countOfFilled + countOfEmpty;
        var lStatus = "INCOMPLETE";
        if (lTotalQues == countOfFilled && lTotalQues != 0) {
            lStatus = "COMPLETED";
        }
        var lPercentCompleteStr = "STATUS " + lStatus + " -  " + countOfFilled + " OF " + lTotalQues + " COMPLETED";
        if (countOfFilled == lTotalQues) {
            $("#" + statusDiv).addClass("statusGreen");
            $("#" + statusDiv).removeClass("statusOrange");
        } else {
            $("#" + statusDiv).addClass("statusOrange");
            $("#" + statusDiv).removeClass("statusGreen");
        }
        $("#" + statusDiv).text(lPercentCompleteStr);
        this.fnSetMandFlag(countOfFilled, lTotalQues);
        if (this.apz.isFunction(this.apz[this.apz.currAppId].postProcessDiv)) {
            this.apz[this.apz.currAppId].postProcessDiv(pProductDiv, statusDiv, countOfFilled, lTotalQues);
        }
    },
    fnSetMandFlag: function (noOfElms, totalQuotes) {
        if (noOfElms == totalQuotes) {
            this.CitiMandatoryQuComplFlag = "Y";
            this.CitiImplementationQuComplFlag = "Y";
            this.CitiSCQuComplFlag = "Y";
        } else {
            this.CitiMandatoryQuComplFlag = "N";
            this.CitiImplementationQuComplFlag = "N";
            this.CitiSCQuComplFlag = "N";
        }
        /* investor : percent changes  */
        //var percent = (noOfElms / totalQuotes) * 100;
        var percent = noOfElms + "-" + totalQuotes;
        this.CitiPercentageComplete = percent;
        this.CitiDDQPercentage = percent;
    },
    addDefaultShownHook: function (subProductScrId, subProductId, pobj) {
        /* investor : params changes */
        if (!apz.app["onShown_" + subProductScrId]) {
            apz.app["onShown_" + subProductScrId] = function (userObj, scrObj) {
                apz.data.buildData();
                var lProductID = subProductId.split("_")[2];
                apz.products.fnAddMultiSelectDropdown(subProductId, lProductID, undefined, apz.products.updateDropDownScrData);
                for (sDataMapKey in apz.products.sDataMap) {
                    if (subProductId.indexOf(sDataMapKey.replace("1-", "")) > -1) {
                        var lProduct = apz.products.sDataMap[sDataMapKey];
                        /* investor : processScreen need to be called before loading the data  */
                        apz.products.processScreen(scrObj);
                        apz.products.loadProductsData(lProduct, subProductId);
                    }
                }
                apz.products.processScreen(scrObj);
                apz.products.fnAddMultiSelectDropdown(subProductId, lProductID, undefined, apz.products.updateDropDownScrData);
                apz.products.initScreenSpecificOperations(subProductId, lProductID, scrObj);
                if (apz.isFunction(apz[apz.currAppId].postLaunchAddPadding)) {
                    apz[apz.currAppId].postLaunchAddPadding(subProductScrId, subProductId, scrObj);
                }
            }
        }
    },
    /* added by krishna  */
    isNull: function (pObj) {
        let result = false;
        if (apz.isNull(pObj)) {
            result = true;
        }
        if (!result && typeof (pObj) === "object") {
            if (Object.keys(pObj).length > 0) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
    },
    validateDropDown: function () {
        let selLen = $("select").length;
        for (let i = 0; i < selLen; i++) {
            let drpDwnId = $("select")[i].getAttribute('id');
            let contId = drpDwnId.split("_DDN")[0];
            let contProps = this.apz.scrMetaData.containersMap[contId];
            let MinCardinality = 0;
            let isMand = false;
            if (contProps && contProps.decorations) {
                for (var n = 0; n < contProps.decorations.length; n++) {
                    if (contProps.decorations[n].name.split("_")[1] === this.RootPrdShortName && contProps.decorations[n].name.split("_")[2] ===
                        "BasicRuleSet") {
                        var valueArr = contProps.decorations[n].value.split("|");
                        if (valueArr[1] == "Man") {
                            isMand = true;
                            break;
                        } else {
                            isMand = false;
                            break;
                        }
                    }
                }
            }
            for (var p = 0; p < contProps.decorations.length; p++) {
                if (contProps.decorations[p].name.split("_")[1] === this.RootPrdShortName && contProps.decorations[p].name.split("_")[2] ===
                    "Cardinality") {
                    var valueArr = contProps.decorations[p].value.split("|");
                    if (valueArr[0] > 0) {
                        MinCardinality = valueArr[0];
                        isMand = true;
                        break;
                    } else {
                        isMand = false;
                        break;
                    }
                }
            }
            contProps.mand = isMand;
            if (isMand) {
                $("#" + drpDwnId).next().find(".select2-selection--multiple").addClass("borderRed");
                if (MinCardinality > 0) {
                    $("#" + drpDwnId).data("minCardinality", MinCardinality);
                }
                if ($("#" + drpDwnId).val().length > 0) {
                    $("#" + drpDwnId).next().find(".select2-selection--multiple").removeClass("borderRed").addClass("borderGreen");
                }
                if (!$("#" + drpDwnId).next().find(".select2-search__field").hasClass("multifocusclass")) {
                    $("#" + drpDwnId).next().find(".select2-search__field").addClass("multifocusclass");
                    $("#" + drpDwnId).next().find(".select2-search__field").attr('onfocus', 'apz.app.fnMultifocus(this)');
                    $("#" + drpDwnId).next().find(".select2-search__field").attr('onblur', 'apz.app.fnMultiBlur(this)');
                }
            }
        }
    },
    filterDrpDwn: function (selTag) {
        let d = selTag.split('_DDN')[0];
        let ctrDecors = apz.scrMetaData.containersMap[d].decorations;
        let decorName = [];
        let decorVals = [];
        let valHide = "";
        for (let i = 0; i < ctrDecors.length; i++) {
            decorName = ctrDecors[i].name.split("_");
            if (decorName[1].trim() === apz.products.RootPrdShortName && decorName[2].trim() === "InFlight") {
                decorVals = ctrDecors[i].value.split("|");
                for (let j = 0; j < decorVals.length; j++) {
                    valHide = $("option[value='" + decorVals[j] + "']").text();
                    let liElems = $('.select2-results__option');
                    for (let k = 0; k < liElems.length; k++) {
                        let liElem = liElems[k].innerHTML;
                        if (liElem.indexOf(valHide) > -1) {
                            liElems[k].classList.add('sno');
                        }
                    }
                }
            }
        }
        let searchedTerm = $("#" + selTag).next().find(".select2-search__field").val();
        lvalHide = searchedTerm.toLowerCase();
        let liElems = $('.select2-results__option');
        for (let k = 0; k < liElems.length; k++) {
            let liElem = liElems[k].innerHTML;
            if (!liElem.toLowerCase().startsWith(lvalHide)) {
                liElems[k].classList.add('sno');
                if (liElems[k].classList.contains('select2-results__option--highlighted') && liElems[k].classList.contains('sno')) {
                    liElems[k].classList.remove('select2-results__option--highlighted');
                    $(liElems[k]).siblings().not('.sno')[0].classList.add('select2-results__option--highlighted');
                }
            }
        }
    },
    formReq: function (params) {
        //Request format for Investors created from former pickapplet API(Presently Autocomplete) request//
        let elmId = this.apz.getObjIdWORowNumber(document.getElementById(params.elmId));
        let decorations = apz.scrMetaData.elmsMap[elmId].decorations;
        // Investors: formReq changes starts here
        for (let i = 0; i < decorations.length; i++) {
            let decorName = decorations[i].name.split('_')[2];
            if (decorName.indexOf("BasicRuleSet") > -1) {
                let decorVal = decorations[i].value;
                if (decorVal.indexOf("IPS") > -1) {
                    if (params.req && params.req.In) {
                        let keyVal = params.req.In.split("|");
                        let SearchExpArray = params.req.SearchExpr.split("AND");
                        let intArr = [];
                        let extObj = {};
                        for (let j = 0; j < keyVal.length; j++) {
                            let intObj = {};
                            intObj.fieldName = keyVal[j];
                            intObj.fieldValue = "";
                            for (let k = 0; k < SearchExpArray.length; k++) {
                                if (SearchExpArray[k].indexOf("[" + keyVal[j] + "]") >= 0) {
                                    intObj.fieldValue = SearchExpArray[k].split("=")[1].trim().split("'")[1];
                                }
                            }
                            if (intObj.fieldValue == "" && params[keyVal[j]] != undefined) {
                                intObj.fieldValue = params[keyVal[j]];
                            }
                            intArr.push(intObj);
                        }
                        extObj.inputs = intArr;
                        extObj.outputs = {};
                        extObj.outputs.fieldName = params.req.out.split("|");
                        return extObj;
                    }
                } else {
                    return params.req;
                }
            }
        }
        // Investors: formReq changes ends here
        return null;
    },
    callElasticSearch: function (pObj) {
        //Element level rule to call Autocomplete API during load time//
        // Investors: callElasticSearch changes starts here
        let status = true;
        if (this.apz.isFunction(this.apz[this.apz.currAppId].preCallElasticSearch)) {
            status = this.apz[this.apz.currAppId].preCallElasticSearch(pObj);
        }
        if (status) {
            if (pObj.rule.trim() == "Yes") {
                let elmId = pObj.id;
                if (!apz.isNull(pObj.rowNo) && pObj.rowNo >= 0) {
                    elmId = elmId + "_" + pObj.rowNo;
                }
                if (this.isNull(this.autocompleteRespArr)) {
                    let domObj = document.getElementById(elmId);
                    this.elasticSearchLov(domObj);
                } else {
                    let params = {};
                    if (!this.isNull(this.autocompleteRespArr[elmId])) {
                        params = this.autocompleteRespArr[elmId];
                        this.initAutoComplete(params);
                    } else {
                        let domObj = document.getElementById(elmId);
                        this.elasticSearchLov(domObj);
                    }
                }
            }
        }
        // Investors: callElasticSearch changes ends here
    },
    customSearchFunction: function (pObj, evt) {
        //Triggered onchange of autocomplete to help users manipulate results//
        if (apz.isFunction(apz.app.customSearchFunction)) {
            apz.app.customSearchFunction(pObj, evt);
        }
    },
    processRadio: function (pObj, evt) {
        //Triggered onchange of masked Radio-checkbox when one is checked, others should be unchecked//
        let chkBxId = pObj.id.split('_option_' + pObj.value)[0];
        let relChkBoxes = $(pObj).parent().siblings().find('input[type=radio]');
        let relChkBoxId = "";
        for (let i = 0, chkBxs = relChkBoxes.length; i < chkBxs; i++) {
            relChkBoxId = relChkBoxes[i].getAttribute('id').split('_option')[0];
            apz.setElmValue(relChkBoxId, $("#" + relChkBoxId).attr("uncheckedval"));
        }
        apz.setElmValue(chkBxId, $("#" + chkBxId).attr("checkedval"));
        $("#" + chkBxId).trigger('change');
    },
    initProductReload: function (rootId) {
        apz.data.scrdata = {};
        apz.scrMetaData.ifaces = [];
        apz.scrMetaData.nodes = [];
        apz.scrMetaData.elms = [];
        apz.scrMetaData.containers = [];
        apz.scrMetaData.groups = [];
        apz.scrMetaData.childScreens = [];
        apz.scrMetaData.charts = [];
        apz.scrMetaData.gauges = [];
        apz.scrMetaData.containersMap = {};
        apz.scrMetaData.ifacesMap = {};
        apz.scrMetaData.groupsMap = {};
        apz.scrMetaData.nodesMap = {};
        apz.scrMetaData.elmsMap = {};
        apz.scrMetaData.chartsMap = {};
        apz.scrMetaData.gaugesMap = {};
        apz.products = new Apz.Products(apz);
        $("#" + rootId).html("");
    },
    initScreenSpecificOperations: function (combinedProductId, productID, scrObj) {
        var scrDefKey = scrObj.scr + apz.idSep + scrObj.layout + apz.idSep + scrObj.design;
        var scrDef = apz.scrDefsMap[scrObj.appId][scrDefKey];
        for (var i = 0; i < scrDef.containers.length; i++) {
            var cntrObj = scrDef.containers[i];
            if (cntrObj.filterColumns == "Y") {
                apz.initFilterTable(cntrObj.id);
                apz.populateTableFilters(cntrObj.id);
            }
        }
    },
    /**
        Function takes Product ID as an argument,
        return parent refOther ifaceId
    */
    getReferOtherKey: function (prdId) {
        var parId = prdId, next = prdId;
        var sDataMapData = "";
        var defsMapData = apz.scrDefsMap[apz.currAppId][prdId + "_Scr__NewLayout__D0"];
        if (this.sDataMap[prdId] instanceof Array) {
            sDataMapData = this.sDataMap[prdId][0]
        } else {
            sDataMapData = this.sDataMap[prdId];
        }
        while (sDataMapData.referOther) {
            parId = next;
            next = defsMapData.combinedParentId
            defsMapData = apz.scrDefsMap[apz.currAppId][next + "_Scr__NewLayout__D0"];
            sDataMapData = this.sDataMap[next][0]
        }
        return parId;
    },
    /**
        Function takes product Id as an argument,
        return parent's list till the parent refOther
    */
    getQueryParArray: function (prdId) {
        //let combinedProductId=this.getCombinedPrdId(prdId,gsubProdData);
        let combinedProductId = this.getReferOtherKey(prdId);
        //let combinedProductId = "7SV2IPZ_7SV2V1O_7SSF081";
        let parArray = [];
        var gSubPrdId = apz.currAppId + "__" + prdId + "_Intf_Req";
        var parentPrdId = prdId;
        while (combinedProductId != parentPrdId) {
            parentPrdId = this.apz.scrDefsMap[apz.currAppId][parentPrdId + "_Scr__NewLayout__D0"].combinedParentId;
            parArray.unshift(parentPrdId);
        }
        return parArray;
    },
    /**
        Function modifies the ScrData manually and updates Rel elements to empty value
    */
    modifyScrData: function (ifaceId, prdId, lRow) {
        if (Object.keys(apz.data.scrdata[ifaceId][lRow]).includes(prdId + "_Rel")) {
            for (let key in apz.data.scrdata[ifaceId][lRow][prdId + "_Rel"]) {
                apz.data.scrdata[ifaceId][lRow][prdId + "_Rel"][key] = "";
            }
        }
    },
    /**
        Creating gDulipcateSubProductData Array if element is not present.
    */
    createGDubSubProd: function (ifaceId, pProduct, prdId) {
        var refProdObj;
        var exist = "";
        var referOtherKey = this.getReferOtherKey(prdId);
        if (this.gDulipcateSubProductData.length > 0) {
            for (var i = 0; i < this.gDulipcateSubProductData.length; i++) {
                var gsubItem = this.gDulipcateSubProductData[i];
                var keys = Object.keys(gsubItem);
                if (keys[0] == apz.currAppId + "__" + referOtherKey + "_Intf_Req") {
                    refProdObj = gsubItem[keys[0]];
                    break;
                }
            }
            return refProdObj;
        } else {
            var newSubObj = {};
            newSubObj[ifaceId] = [];
            pProduct.grandParent = ifaceId;
            this.mainKey = ifaceId;
            this.immediateChildKey = ifaceId;
            this.gDulipcateSubProductData.push(newSubObj);
            return this.gDulipcateSubProductData[0][ifaceId];
        }
    },
    /**
        This function takes ifaceId and Product object as arguments
        It checks whether the given ifaceId is part of the main parent(uniqueId in gDubplicateSubproduct)
    */
    gDuplicateAvailable: function (ifaceId, pProduct) {
        var splitIface = ifaceId.split("_");
        var splitImdCldKey = this.immediateChildKey.split("_");
        var exist = false;
        for (let i = 2; i < 5; i++) {
            if (splitImdCldKey.includes(splitIface[i])) {
                exist = true;
                this.immediateChildKey = ifaceId;
                pProduct.grandParent = this.mainKey;
                break
            }
        }
        return exist;
    },
    /**
        This function takes product.grandparent(parent ProductID) and global gDubplicateSubproduct as argument
        It returns specific object from the global gDubplicateSubproduct array
    */
    getSpecificDupSubPrdData: function (unqKey, gsubProdData) {
        let dupSubProdData;
        if (!apz.isNull(unqKey) && !apz.isNull(gsubProdData)) {
            if (gsubProdData.length > 0) {
                for (var i = 0; i < gsubProdData.length; i++) {
                    var gsubItem = gsubProdData[i];
                    var keys = Object.keys(gsubItem);
                    if (keys[0] == unqKey) {
                        dupSubProdData = gsubItem[keys[0]];
                        break;
                    }
                }
            }
        }
        return dupSubProdData;
    },
    /**
        Function return the parent refOther Product ID
    */
    getCombinedPrdId: function (prdId, gsubProdData) {
        var combineParentId = "";
        var productId = prdId;
        var gSubPrdId = apz.currAppId + "__" + prdId + "_Intf_Req";
        var parentPrdId = prdId;
        while (apz.isNull(gsubProdData[gSubPrdId]) && gSubPrdId.split("_").length > 1) {
            parentPrdId = this.apz.scrDefsMap[apz.currAppId][parentPrdId + "_Scr__NewLayout__D0"].combinedParentId;
            gSubPrdId = apz.currAppId + "__" + parentPrdId + "_Intf_Req";
        }
        return parentPrdId;
    },
    /**
        Return true if ProductId is present only inside SubProducts array
    */
    checkOnlySubPrd: function (prdId) {
        var isOnlySubPrd = false;
        var scrDefsId = apz.scrDefsMap[apz.currAppId][prdId + "_Scr__NewLayout__D0"].combinedParentId;
        var scrDefsMapData = apz.scrDefsMap[apz.currAppId][scrDefsId + "_Scr__NewLayout__D0"];
        if (!(scrDefsMapData.defaultProducts.includes(prdId) && scrDefsMapData.subProducts.includes(prdId))) {
            isOnlySubPrd = true;
        }
        return isOnlySubPrd;
    },
    /**
        Returns Specific Product interface Data from gDulipcateSubProductData Array
    */
    getIterativeGDupSubPrdData: function (prdId, gsubProdData, lMultiRecRow, fromSubProduct) {
        let dupSubProdData = [];
        let parentIfaceName = apz.currAppId + "__" + apz.scrMetaData.combinedParentId + "_Intf_Req";
        let ifaceName = apz.currAppId + "__" + prdId + "_Intf_Req";
        let combinedProductId = this.getCombinedPrdId(prdId, gsubProdData);
        let defCombinedProductId = "", defCmdIdLength = false;
        let dupSubPrdData = "", lMultiRecDataRow = "", lMultiRow = "";
        let prdIfaceName = apz.currAppId + "__" + prdId + "_Intf_Req";
        var innerArray = "";
        if (fromSubProduct) {
            prdIfaceName = prdId + "_Rel";
        }

        if (apz.getDataType(lMultiRecRow) == "Array") {
            ifaceName = apz.currAppId + "__" + combinedProductId + "_Intf_Req";
            dupSubPrdData = gsubProdData[ifaceName];
            var parArry = this.getcombinedIdHierarchy(prdId, gsubProdData);
            var parentId = "";
            for (var i = 0; i < lMultiRecRow.length; i++) {
                innerArray = lMultiRecRow[i];
                if (apz.getDataType(innerArray) == "Array") {
                    parentId = apz.currAppId + "__" + parArry[i] + "_Intf_Req";
                    if (i == lMultiRecRow.length - 1 && parArry.length > lMultiRecRow.length) {
                        parentId = apz.currAppId + "__" + parArry[parArry.length - 1] + "_Intf_Req";
                    }
                    if (i == 0) {
                        dupSubPrdData = (dupSubPrdData[innerArray[0]]) ? dupSubPrdData[innerArray[0]]["data"][parentId][innerArray[1]] : {};
                    } else {
                        dupSubPrdData = (dupSubPrdData[parentId]) ? dupSubPrdData[parentId][innerArray[0]][prdIfaceName][innerArray[1]] : {};
                    }

                } else {
                    if (i == 0) {
                        //dupSubPrdData =(dupSubPrdData[innerArray])?dupSubPrdData[innerArray]["data"][ifaceName]:{};
                        //CCF Changes added by Siva
                        var prdReqIfaceName = apz.currAppId + "__" + prdId + "_Intf_Req";
                        if (prdReqIfaceName == ifaceName) {
                            dupSubPrdData = (dupSubPrdData[innerArray]) ? dupSubPrdData[innerArray]["data"][prdReqIfaceName] : {};
                        } else {
                            if (dupSubPrdData[innerArray] && (dupSubPrdData[innerArray]["data"][prdReqIfaceName] != undefined)) {
                                dupSubPrdData = dupSubPrdData[innerArray]["data"][prdReqIfaceName];
                            } else {
                                dupSubPrdData = (dupSubPrdData[innerArray]) ? dupSubPrdData[innerArray]["data"][ifaceName] : {}
                            }
                        }
                    } else {
                        if (apz.getDataType(lMultiRecRow[i - 1]) == "Array") {
                            if (fromSubProduct) {
                                var prdReqIfaceName = apz.currAppId + "__" + prdId + "_Intf_Req";
                                if (i == lMultiRecRow.length - 1 && parArry.length > lMultiRecRow.length) {
                                    prdReqIfaceName = apz.currAppId + "__" + parArry[parArry.length - 1] + "_Intf_Req";
                                }
                                dupSubPrdData = (dupSubPrdData[prdReqIfaceName] && dupSubPrdData[prdReqIfaceName].length > 0) ? dupSubPrdData[prdReqIfaceName][innerArray][prdIfaceName] : {};
                            } else if (apz.isNull(dupSubPrdData[prdIfaceName])) {
                                var prdReqIfaceName = apz.currAppId + "__" + parArry[i] + "_Intf_Req";
                                if (i == lMultiRecRow.length - 1 && parArry.length > lMultiRecRow.length) {
                                    prdReqIfaceName = apz.currAppId + "__" + parArry[parArry.length - 1] + "_Intf_Req";
                                }
                                dupSubPrdData = (dupSubPrdData[prdReqIfaceName] && dupSubPrdData[prdReqIfaceName].length > 0) ? dupSubPrdData[prdReqIfaceName][innerArray][prdIfaceName] : {};
                            }
                            else {
                                dupSubPrdData = (dupSubPrdData[prdIfaceName]) ? dupSubPrdData[prdIfaceName][innerArray] : {};

                            }
                        } else {
                            if (parArry.length > 0 && fromSubProduct) {
                                var prdReqIfaceName = apz.currAppId + "__" + prdId + "_Intf_Req";
                                dupSubPrdData = (dupSubPrdData[innerArray][prdReqIfaceName]) ? dupSubPrdData[innerArray][prdReqIfaceName][prdIfaceName] : {};
                            } else {
                                if (!apz.isNull(dupSubPrdData[innerArray])) {
                                    dupSubPrdData = (dupSubPrdData[innerArray][prdIfaceName]) ? (dupSubPrdData[innerArray][prdIfaceName]) : dupSubPrdData[innerArray];
                                }
                            }
                        }
                    }
                }
            }
        } else {
            //  ifaceName = apz.currAppId + "__" + combinedProductId + "_Intf_Req";
            //  if(!apz.isNull(gsubProdData[ifaceName][lMultiRecRow])){
            //     dupSubPrdData = gsubProdData[ifaceName][lMultiRecRow]["data"][ifaceName];
            // }

            //CCF Changes added by Siva
            combinedProductId = apz.currAppId + "__" + combinedProductId + "_Intf_Req";
            if (!apz.isNull(gsubProdData[combinedProductId][lMultiRecRow])) {
                dupSubPrdData = gsubProdData[combinedProductId][lMultiRecRow]["data"][ifaceName];
            }
        }

        return dupSubPrdData || {};

    },
    /**
        Returns Parent Hierarchy till refOther Parent Product
    */
    getcombinedIdHierarchy: function (prdId, gsubProdData) {
        let combinedProductId = this.getCombinedPrdId(prdId, gsubProdData);
        let parArray = [];
        var gSubPrdId = apz.currAppId + "__" + prdId + "_Intf_Req";
        var parentPrdId = prdId;
        while (combinedProductId != parentPrdId) {
            parentPrdId = this.apz.scrDefsMap[apz.currAppId][parentPrdId + "_Scr__NewLayout__D0"].combinedParentId;
            parArray.unshift(parentPrdId);
        }
        return parArray;
    },
    debounceInitDropdown: Apz.prototype.debounce(function (objId, MltDDNFlag) {
        $("#" + objId + "_DDN").select2({
            "closeOnSelect": false,
        });
        if (MltDDNFlag) {
            $("#" + objId + "_DDN").select2("open");
        }
        if (this.apz.scrMetaData.containersMap[objId].mand && this.apz.scrMetaData.containersMap[objId].mand == true) {
            let minCardinality = parseInt($("#" + objId + "_DDN").data("minCardinality"));
            minCardinality = minCardinality - 1 || 0;
            if (!this.isNull($("#" + objId + "_DDN").val()) && $("#" + objId + "_DDN").val().length > minCardinality) {
                $("#" + objId).find(".select2-selection--multiple").addClass("borderGreen");
            } else {
                $("#" + objId).find(".select2-selection--multiple").addClass("borderRed");
            }
        }
    }, 100)
}
Apz.prototype.getDesigns = function (params) {
    var appId = params.appId ? params.appId : this.currAppId;
    var scr = params.scr ? params.scr : this.currScr;
    var layout = params.layout ? params.layout : this.getLayout(params);
    var deviceGroup = params.deviceGroup ? params.deviceGroup : this.deviceGroup;
    var orientation = params.orientation ? params.orientation : this.orientation;
    var customizer = params.customizer ? params.customizer : "N";
    var key = scr + this.idSep + layout,
        design = "";
    var res = {};
    var defaultDesign = "";
    if (!this.containsKey(this.loDefsMap[appId], key)) {
        defaultDesign = this.getDefaultDesign(params);
    }
    var designInfo = this.loDefsMap[appId][key];
    if (designInfo) {
        design = designInfo.defaultTemplate;
        res.icons = designInfo.icons;
        res.designs = designInfo.designs;
        res.currentDesign = design;
        res.designDisplayNames = designInfo.designDisplayNames;
    } else {
        res.currentDesign = defaultDesign;
    }
    return res;
}