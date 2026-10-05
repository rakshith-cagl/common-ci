Apz.Mca = function (apz) {
    this.apz = apz;
    this.prevProcess = null;
    this.currProcess = null;
    this.mainProcess = {};
    this.processes = {};
    this.processKey = "";
    this.contextRoot = "";
    this.firstPage = "";
    this.mcaSourcepath = "";
    this.bootStrapData;
    this.backup = {};
    this.childParams = {};
    this.mcaUIComponentId = null;
};
///////////////////Prototype Definition///////////////////////
Apz.Mca.prototype = {
    getProcessJson: function (proc) {
        var params = {},
            processId, path;
        params.id = "PROCESSJSON";
        params.runnerObj = this.apz;
        params.runner = this.apz.getFile;
        params.mainProc = true;
        var mcaData = getMCABootstrapData();
        var params = {},
            processId, path;
        if (mcaData) {
            this.processKey = mcaData.context.id;
            this.contextRoot = mcaData.config.contextRoot + "/eps";
            if (!this.apz.isNull(mcaData.context.processJson) &&
                !this.apz.isObjectEmpty(mcaData.context.processJson)) {
                params.content = JSON.stringify(mcaData.context.processJson);
                this.loadProcessJson(params);
                processId = mcaData.context.processJson.process.id;
            } else {
                path = "/" + this.contextRoot + "/" +
                    mcaData.context.processPath + "?processId=" +
                    this.processKey;
                var content = this.apz.getFile(path);
                if (!this.apz.isNull(content)) {
                    var jsonObj = JSON.parse(content);
                    mcaData.context.processJson = jsonObj;
                    params.content = content;
                    this.loadProcessJson(params);
                    processId = jsonObj.process.id;
                } else {
                    var params = {
                        "code": "APZ-MCA-ERR"
                    }
                    this.apz.dispMsg(params);
                }
            }
            this.bootStrapData = mcaData;
        }
        this.apz.appId = processId.substr(processId.lastIndexOf("/") + 1);
        apz.loadProject();
    },
    loadChildProcess: function (params) {
        /* to be reviewed - for Child process */
        this.createBackUp();
        apz.appId = params.processId
            .substr(params.processId.lastIndexOf("/") + 1);
        // params.path = params.processId; //Temp

        var childProcessId = params.processId.substr(0, 1) == "/" ? params.processId.substr(1, params.processId.length) : params.processId;
        params.path = "/" + this.contextRoot + "/" + childProcessId + "?processId=" + this.processKey;

        params.content = this.apz.getFile(params.path);
        params.mainProc = false;
        params.content = this.apz.getFile(params.path);
        this.loadProcessJson(params);
        var path = this.apz.getConfigPath() + "/" + "prjdef.json";
        var defContent = this.apz.getFile(path);
        this.loadChildProjDef(defContent, params);
    },
    loadChildProjDef: function (content, params) {
        def = JSON.parse(content);
        // /App Properties
        this.apz.loadAppProps(def);
        // /User Preferences
        // this.apz.loadUserPrefs(def);
        this.apz.msgs[this.apz.currAppId] = def.msgs[this.apz.language];
        this.apz.theme = def.userPrefs.theme;
        this.apz.lovs[this.apz.currAppId] = def.lovs;
        this.apz.lits[this.apz.currAppId] = def.lits[this.apz.language];
        this.apz.ccys = def.ccys;
        this.apz.prjScripts = [];
        this.apz.prjScripts = def.scripts;
        // //add Path
        this.apz.addScriptsPath(this.apz.prjScripts);
        // //Layout Mapping
        this.apz.loMap[this.apz.currAppId] = def.loMap;
        // Load Child Process Json
        params.mainProc = false;
        // var params = {};

        //      var proc = {};
        //      proc.threads = [];
        //      proc.id = "CHILDPROCESSJSON";
        //      params.id = "CHILDPROCESSJSON";
        //      params.runnerObj = this.apz;
        //      params.runner = this.apz.getFile;
        //      params.callBack = this.loadProcessJson;
        //      params.callBackObj = this;
        //      params.path = "/" + this.contextRoot + "/" + params.processId
        //              + "?processId=" + this.processKey;
        //      params.async = true;
        //      proc.threads[proc.threads.length] = params;
        this.mcaProjectLoaded(params);
    },
    mcaProjectLoaded: function (params) {
        var myObj = this.apz;
        myObj.enableAnimations = true;
        var ldiv = params.div;
        var param = {
            "scr": this.firstPage,
            "div": ldiv,
            "animation": params.animation
        };
        this.childParams = params;
        if (params.modal) {
            param.type = "SS";
            param.div = "gtsa__searchgtsatemplate__popupRow"; //to be changed
            this.apz.launchInDiv(param);
            var params = {};
            //params.targetId = apz.appId+""+"__mcaModal";


            params.targetId = "gtsa__searchgtsatemplate__popupHeadDiv"; //Temp Change
            //  params.callBack = this.chilProcModalCB;
            params.callBackObj = this;
            apz.toggleModal(params);
        } else {
            param.type = "PG";
            myObj.launchScreen(param);
        }
    },
    chilProcModalCB: function () {
        this.apz.appId = this.backup.appId;
        this.apz.currAppId = this.backup.currAppId;
        this.apz.ifacesMap[this.apz.currAppId] = this.backup.ifacesMap;
        this.apz.scrMetaData = this.backup.scrMetaData;
        this.apz.msgs = this.backup.messages;
        this.apz.lits = this.backup.lits;
        this.apz.ccys = this.backup.ccys;
        this.currProcess = this.backup.currProcess;
        this.apz.loMap = this.backup.loMap;
        this.apz.theme = this.backup.theme;

        var params = {};
        params = this.childParams;
        if (this.apz.isFunction(params.callBack)) {
            if (params.callBackObj) {
                params.callBack.call(params.callBackObj, params);
            } else {
                params.callBack(params);
            }
        }
    },
    createBackUp: function () {
        this.backup = {};
        this.backup.appId = this.apz.appId;
        this.backup.currAppId = this.apz.appId;
        this.backup.ifacesMap = apz.copyJSONObject(this.apz.ifacesMap[this.backup.currAppId]);
        //  this.backup.scrMetaData =  apz.copyJSONObject(this.apz.scrMetaData);
        this.backup.scrMetaData = {};
        Object.assign(this.backup.scrMetaData, this.apz.scrMetaData);
        this.backup.messages = {};
        Object.assign(this.backup.messages, this.apz.msgs);
        //  this.backup.messages =  apz.copyJSONObject(this.apz.msgs);
        this.backup.lits = {};
        Object.assign(this.backup.lits, this.apz.lits);
        //  this.backup.lits =  apz.copyJSONObject(this.apz.lits);
        this.backup.ccys = {};
        Object.assign(this.backup.ccys, this.apz.ccys);
        //this.backup.ccys =  apz.copyJSONObject(this.apz.ccys);
        this.backup.loMap = {};
        Object.assign(this.backup.loMap, this.apz.loMap);
        //this.backup.loMap =  apz.copyJSONObject(this.apz.loMap);
        this.backup.theme = {};
        Object.assign(this.backup.theme, this.apz.theme);
        //  this.backup.loMap =  apz.copyJSONObject(this.apz.loMap);
        //  this.backup.theme =  apz.copyJSONObject(this.apz.theme);
        this.backup.currProcess = this.currProcess;
    },
    loadProcessJson: function (params) {
        var process = null;
        var firstscreenpath = "";
        var viewpath = "";
        var def = JSON.parse(params.content);
        if (def) {
            process = def.process;
            if (process.tasks) {
                process.tasksMap = [];
                var noOfTasks = process.tasks.length;
                if (noOfTasks > 0) {
                    process.currTask = process.tasks[0];
                    firstscreenpath = process.tasks[0].view.src;
                    for (var i = 0; i < noOfTasks; i++) {
                        var task = process.tasks[i];
                        process.tasksMap[task.id] = task;
                        task.eventsMap = [];
                        // /Loop for events also
                        if (task.events) {
                            var noOfEvents = task.events.length;
                            for (var e = 0; e < noOfEvents; e++) {
                                var event = task.events[e];
                                task.eventsMap[event.name] = event;
                            }
                        }
                    }
                }
            }
            viewpath = process.views[firstscreenpath].uri;
            this.firstPage = viewpath.substring(viewpath.indexOf("views") + 6, viewpath.length);
            //  alert(this.firstPage);
            //this.mcaSourcepath=this.contextRoot + viewpath.substring(1,viewpath.indexOf("views"));

            this.mcaUIComponentId = viewpath.split("/")[3];
            this.mcaSourcepath = this.mcaUIComponentId + "/" + viewpath.substring(2, viewpath.indexOf("views"));

            //  this.mcaSourcepath= viewpath.substring(2,viewpath.indexOf("views"));
            // //Assign Main Process
            if (params.mainProc) {
                this.mainProcess = process;
                this.currProcess = process;
            } else {
                // to be reviewed - for Child process
                this.currProcess = process;
            }
        }
    },
    callServer: function (params) {
        /*
         * Params Contains the below attributes
         * id,callBackObj,callBack,ifaceName,scrName, buildReq, req,
         * async(boolean), Response Contains below res, errCode
         */
        params.apzIfaceName = params.appId ? params.appId + "__" + params.ifaceName : this.apz.currAppId + "__" + params.ifaceName;
        var ifaceName = this.apz.getIfaceName(params.apzIfaceName);
        var ifaceDet = this.apz.getIfaceObj(ifaceName, params.appId);
        if (ifaceDet) {
            params.ifaceDet = ifaceDet;
        } else {
            // /Error
        }
        // //Populate URL
        var uri = this.currProcess.endpoints[params.endpointName].uri
        params.url = "/" + this.contextRoot + uri;
        if (uri.indexOf("?") === -1) {
            params.url += "?processId=" + this.processKey;
        } else {
            params.url += "&processId=" + this.processKey;
        }
        params.method = this.currProcess.endpoints[params.endpointName].method;
        //populate Request params
        if (params.uriParams) {
            params.url = this.populateRequestParams(params);
        }
        // ///Build Data
        var dataIface = params.ifaceName + "_Req";
        if (params.buildReq == "Y") {
            if (this.apz.isNull(params.req)) {
                params.req = this.apz.data.buildData(dataIface);
                // //Correct Request
                this.correctReq(params);
            }
        }
        this.sendReq(params);
    },
    populateRequestParams: function (params) {
        var url = params.url;
        if (params.uriParams) {
            for (var i = 0; i < params.uriParams.length; i++) {
                var name = "{" + params.uriParams[i].name + "}";
                var value = params.uriParams[i].value;
                var arguments = {};
                arguments.string = params.url;
                arguments.key = name;
                arguments.replaceData = value;
                url = this.apz.replace(arguments);
            }
        }
        return url;
    },
    sendReq: function (params) {
        // //Ajax Setup
        $.ajaxSetup({
            headers: {
                'cache-control': 'no-cache',
                'Access-Control-Allow-Origin': '*'
            }
        });
        // //Ajax Call
        if (!this.apz.mockServer) {
            var reqStr = "";
            if (typeof params.req === 'object') {
                reqStr = JSON.stringify(params.req);
            } else {
                reqStr = params.req + "&";
            }
            var myObj = this;
            $.ajax({
                url: params.url,
                type: params.method,
                cache: true,
                data: reqStr,
                contentType: 'application/json',
                dataType: 'json',
                async: params.async,
                success: function (res) {
                    params.status = true;
                    params.resFull = res;
                    myObj.receiveRes(params);
                },
                error: function (res) {
                    params.status = false;
                    var fatalError = {};
                    if (res.responseText) {
                        fatalError = JSON.parse(res.responseText);
                    }
                    if (fatalError.fatalError) {
                        myObj.apz.data.scrdata[myObj.apz.currAppId + "__fatal_Res"] = fatalError;
                        myObj.apz.launchScreen({
                            "scr": "fatalscr"
                        });
                        //myObj.apz.data.loadData(myObj.apz.currAppId + "__fatal");
                    } else {
                        myObj.receiveRes(params);
                    }
                }
            });
        } else {
            params.status = true;
            var resJson = {};
            if (!apz.isNull(params.resCode)) {
                resJson = this.apz.getFile(this.apz.getMockRespPath() + "/" +
                    params.resCode + ".json");
            } else {
                var ifaceName = this.apz.getIfaceName(params.ifaceName);
                resJson = this.apz.getFile(this.apz.getMockRespPath() + "/" +
                    ifaceName + ".json");
            }
            params.res = JSON.parse(resJson);
            params.resFull = JSON.parse(resJson);
            this.receiveRes(params);
        }
    },
    receiveRes: function (params) {
        if (params.status) {
            // //Populate Body/Errors
            params.res = params.resFull;
            // params.errors = params.resFull.appzillonErrors; //TBC-Darshan
            // Error comes in which object?
            // //Process Response
            this.processRes(params);
        } else {
            var params = {
                "code": "APZ-SVR-ERR"
            }
            this.apz.dispMsg(params);
        }
    },
    processRes: function (params) {
        if (params.res) {
            // //Correct Response
            this.correctRes(params);
            // //Update Response
            this.updateResponse(params.res);
            if (params.paintResp == 'Y') {
                var loadData = true;
                if (this.apz.isFunction(this.apz.app.preLoadData)) {
                    loadData = this.apz.app.preLoadData();
                    if (this.apz.isNull(loadData)) {
                        loadData = true;
                    }
                }
                if (loadData) {
                    this.apz.data.loadData(params.ifaceName, params.appId);
                }
                if (this.apz.isFunction(this.apz.app.postLoadData)) {
                    this.apz.app.postLoadData();
                }
            }
        }
        // //Call Callback..
        if (this.apz.isFunction(params.callBack)) {
            if (params.callBackObj) {
                params.callBack.call(params.callBackObj, params);
            } else {
                params.callBack(params);
            }
        }
    },
    execEvent: function (eventName, params) {
        if (apz.containsKey(apz.mca.currProcess.currTask.eventsMap, eventName)) {
            var event = apz.mca.currProcess.currTask.eventsMap[eventName];
            if (event.action.transition) {
                // /This is Transition...
                var newTaskId = event.action.transition["task-id"];
                var task = apz.mca.currProcess.tasksMap[newTaskId];
                var view = apz.mca.currProcess.views[task.view.src];
                if (view) {
                    var scrUri = apz.mca.currProcess.views[task.view.src].uri;
                    var newScr = scrUri
                        .substr(scrUri.lastIndexOf("/") + 1);
                    apz.mca.currProcess.currTask = task;
                    var animation = params.animation;
                    var div = params.div;
                    // if(animation) {
                    this.setScreen(newScr, div, animation);
                    // } else {
                    // apz.launchScreen(newScr);
                    // }
                }
            } else if (event.action.services) {
                // /Server Calls..Should we take user Hook?
                var noOfIfaces = event.action.services.length;
                if (noOfIfaces > 0) {
                    for (var i = 0; i < noOfIfaces; i++) {
                        var endpointName = event.action.services[i];
                        //var param = params.services[i];
                        var param = params.services[0];
                        if (!param.serviceName) {
                            param.serviceName = endpointName
                        }
                        if (param) {
                            if (param.serviceName == endpointName) {
                                var serverParams = {};
                                serverParams.buildReq = param.buildReq;
                                serverParams.req = param.req; // Previously
                                // buildReqStr
                                serverParams.paintResp = param.paintResp; /* TBC - Required */
                                serverParams.id = param.id; /* Previously callerId */
                                serverParams.ifaceName = param.ifaceName; // Previously
                                serverParams.endpointName = endpointName; /* Nagaraj Changes for interface name with - */
                                serverParams.uriParams = param.uriParams; /* Nagaraj Changes for interface name with - */
                                serverParams.async = param.async;
                                serverParams.callBackObj = param.callBackObj;
                                serverParams.callBack = param.callBack; // Previously
                                // callback
                                this.callServer(serverParams);
                                break;
                            }
                        }
                    }
                }
            } else if (event.action.process) {
                // to be reviewed - for Child process
                var procid = event.action.process["id"];
                if (procid) {
                    params.processId = procid;
                    this.loadChildProcess(params);
                }
            }
        } else {
            alert("Invalid Event");
        }
    },
    setScreen: function (newScr, div, animation) {
        if (!animation) {
            animation = 0;
        }
        var params = {
            "scr": newScr,
            "div": div,
            "animation": animation
        };
        if (div) {
            params.type = "SS";
            this.apz.launchInDiv(params);
        } else {
            params.type = "PG";
            this.apz.launchScreen(params);
        }
    },
    /////////////////Correction Functions///////////////////////////////
    correctReq: function (params) {
        var ifaceName = this.apz.getIfaceName(params.apzIfaceName);
        var reqRoot = this.apz.getReqRoot(ifaceName);
        if (params.ifaceDet && params.ifaceDet.type == "ISO8583") { //condition params.ifaceDet && added  
            this.apz.iso = new Apz.Iso(this.apz);
            this.apz.iso.convertRequest(params);
        } else {
            //var reqd = params.ifaceDet.correctReq;   //commented and added below line with condition params.ifaceDet
            var reqd = params.ifaceDet ? params.ifaceDet.correctReq : true; //condition params.ifaceDet
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
    },
    correctReqNode: function (ifaceName, parentNode, parentName, node, name, appId) {
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
    },
    correctRes: function (params) {
        if (!this.apz.isNull(params.res)) {
            var ifaceName = this.apz.getIfaceName(params.apzIfaceName);
            var resRoot = this.apz.getResRoot(ifaceName);
            ////Add DML Node
            var copy = this.apz.copyJSONObject(params.res);
            this.apz.clearJSONObject(params.res);
            params.res[resRoot] = copy;
            /////////
            if (params.ifaceDet && params.ifaceDet.type == "ISO8583") { // Added params.ifaceDet &&  condition 
                this.apz.iso.convertResponse(params);
            } else {
                //var reqd = params.ifaceDet.correctRes;
                var reqd = params.ifaceDet ? params.ifaceDet.correctRes : true; // Added params.ifaceDet
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
    },
    correctResNode: function (ifaceName, parentNode, parentName, node, name, parents, appId) {
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
    },
    updateResponse: function (res) {
        for (key in res) {
            this.apz.data.scrdata[key] = res[key];
        }
    },
    modTenCheck: function (value) {
        var sum = 0;
        var currentNum = 0;
        for (var i = 0; i < value.length; i++) {
            currentNum = parseInt(value.charAt(i));
            if (i % 2 == 0) {
                currentNum += currentNum;
                if (currentNum > 9) {
                    currentNum -= 9;
                }
            }
            sum += currentNum;
        }
        returnVal = sum % 10;
        if (returnVal == 0) {
            return;
        } else {
            return false;
        }
    },
    modElevenCheck: function (value) {
        var ct = 0;
        checksum = 0;
        number = value.substring(1);
        while (ct < 7) {
            var ch = number.substring(ct, ct + 1);
            if (ch >= 0 && ch <= 9) {
                checksum = checksum + (7 - ct) * ch;
                ct = ct + 1;
            } else {
                return false;
            }
        }
        var remainder = checksum % 11;
        if (remainder == 0) {
            return;
        } else {
            return false;
        }
    },
    removeErrors: function (rowid) {
        $(".err").each(function () {
            $input = $(this);
            var $inputParent = $input.parent();
            if ($input.closest("ul").hasClass("hrow")) {
                $input.closest("li").removeClass("vcn");
            } else {
                $input.parents('.srb:first').removeClass("vcn");
            }
            if ($inputParent[0].lastChild.tagName == "P") {
                $inputParent[0].removeChild($inputParent[0].lastChild);
            }

            if (!($(this).is("ul") || $(this).is("ul"))) {
                $(this).removeClass("err");
            }
        });

        apz.hide(rowid);
    },
    showErrorMsg: function (params) {
        /// Expects id, message or code
        var objId = params.id;
        var pinput = $("#" + objId)[0];
        $input = $(pinput);
        var lid = apz.getObjIdWORowNumber(pinput);
        var elmObj = apz.scrMetaData.elmsMap[lid];
        var $inputParent = $input.parent();
        if (!apz.isNull(elmObj) && elmObj.type == "DROPDOWN" && pinput.tagName == "INPUT") {
            $inputParent = $input.parent().parent();
        }
        var errMsg = params.errMsg;
        var addErrText = function (errText) {
            if ($input.closest("ul").hasClass("hrow")) {
                $input.closest("li").addClass("vcn");
            } else {
                $input.parents('.srb:first').addClass("vcn");
            }

            $input.addClass("err");
            $inputParent.children(".vtx").remove();
            $inputParent.append('<p class="vtx">' + errText + '</p>');
        }
        if (!apz.isNull(errMsg) || !apz.isNull(params.message) || !apz.isNull(params.code)) {
            if (!apz.isNull(params.message)) {
                desc = params.message;
            } else if (!apz.isNull(params.code)) {
                desc = apz.msgs[apz.currAppId][params.code];
                desc = desc.substring(1);
            } else {
                errMsg = apz.msgs[apz.currAppId][errMsg];
                desc = errMsg.substring(1);
            }
            addErrText(desc);
        }
    },
    allowOnlyNumericData: function (obj, e) {
         if (!( $.inArray( e.keyCode, [ 46, 9, 8 ] ) !== -1 || ( e.shiftKey && e.keyCode === 9 ) ||
            ( e.keyCode === 65 && e.ctrlKey === true ) || ( e.keyCode === 37 || e.keyCode === 39 ) ||
            ( e.ctrlKey === true && e.keyCode === 86 ) || !( ( e.shiftKey || ( e.keyCode < 48 || e.keyCode > 57 ) ) &&
            ( e.keyCode < 96 || e.keyCode > 105 ) ) ) ) {
            e.preventDefault();
        }
    },
    allowOnlyAmountData: function (obj, e, id, int_dig_len, frac_dig_len) {

        var amount = apz.getElmValue(id);

        var amountVal = amount;

        amount = amount.split('.');

        //var amount_int = amount [ 0 ];

        var amount_dec = amount[1];

        if ($.inArray(e.keyCode, [46, 9, 8]) !== -1 || (e.shiftKey && e.keyCode === 9) || (e.keyCode === 65 && e.ctrlKey === true) || (e.keyCode === 37 || e.keyCode === 39) ||

            !((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) || (e.keyCode === 110)) {

            if (obj.value && obj.value !== '0.00' && obj.value.indexOf('.') >= 0 && (e.keyCode === 110)) {

                e.preventDefault();

            } else if (obj.value === '0.00' && (e.keyCode === 110)) {

                apz.setElmValue(obj.id, '');

            }

            if (amountVal.length >= int_dig_len && (obj.value.indexOf('.') < 0 || (obj.value.indexOf('.') >= 0 && (amount_dec.length === 0 || amount_dec.length == 1)))) {

                if (obj.value.indexOf('.') < 0) {

                    if (!(e.keyCode === 110 || $.inArray(e.keyCode, [46, 9, 8]) !== -1 || (e.shiftKey && e.keyCode === 9) || (e.keyCode === 65 && e.ctrlKey === true) || (e.keyCode === 37 || e.keyCode === 39))) {

                        e.preventDefault();

                    }

                }

            }

            if (obj.value.indexOf('.') >= 0 && amount_dec.length > frac_dig_len - 1) {
                if (!($.inArray(e.keyCode, [46, 9, 8]) !== -1 || (e.shiftKey && e.keyCode === 9) || (e.keyCode === 65 && e.ctrlKey === true) || (e.keyCode === 37 || e.keyCode === 39))) {
                    e.preventDefault();
                }
            }
        } else {
            e.preventDefault();
        }
    },
    amountOnBlur: function (obj, e, id, int_dig_len, frac_dig_len) {

        var amount = apz.getElmValue(id);

        var amountVal = amount;

        amount = amount.split('.');

        var amount_int = amount[0];

        var amount_dec = amount[1];

        if (amount_int.length > int_dig_len) {

            amount[0] = amount[0].substring(0, int_dig_len);

            apz.setElmValue(id, amount);

        }

        if (amount_dec === undefined && !apz.isNull(amountVal)) {

            amount[1] = '00';

            amount = amount.join('.');

            apz.setElmValue(id, amount);

        } else if (amount_dec == '0' || amount_dec === '') {

            amount[1] = '00';

            amount = amount.join('.');

            apz.setElmValue(id, amount);

        } else if (amount_dec !== undefined) {

            if (amount_dec.length == frac_dig_len - 1) {

                amount[1] = amount[1] + '0';

                amount = amount.join('.');

                apz.setElmValue(id, amount);

            }

        } else if (amount_dec !== undefined) {

            if (amount_dec.length > frac_dig_len) {

                amount[1] = amount[1].substring(0, frac_dig_len);

                amount = amount.join('.');

                apz.setElmValue(id, amount);

            }

        }

    },
    accountDropdownMandatory: function (listId, msg) {
        var isNull = false;
        if (!$("#" + listId + "_dropdown").hasClass("sno") && !apz.isNull(msg)) {
            $("#" + listId).addClass("vcn");
            $("#" + listId).children(".vtx").remove();
            $("#" + listId).append('<p class="vtx">' + msg + '</p>');
            isNull = true;
        } else {
            $("#" + listId).removeClass("vcn");
            $("#" + listId).children(".vtx").remove();
            isNull = false;
        }
        return isNull;
    },
    populateOverlayData: function (parentContId, childcontainerId, prowNo, fromArray, toArray) {
        var rowNo = '_' + prowNo;
        $el = $('#' + childcontainerId);
        $el.css('border', '0');
        for (var i = 0; i < fromArray.length; i++) {
            apz.setElmValue(toArray[i], apz.getElmValue(fromArray[i] + rowNo));
        }
        apz.mca.accountDropdownMandatory(parentContId, '');
    },
    populateAccountDropDown: function (parentContId, containerId, rowNo) {
        if (!apz.isNull(parentContId) && parentContId !== containerId) {
            apz.app['overlayClicked_' + parentContId](parentContId, containerId, rowNo);
            apz.closeOverlay(apzJquery('#' + parentContId).children('ul').children('li')[1], containerId, 'Y');
        }
    },
    buildQueryData: function (pjson) {
        var paramArray = [];
        for (var d in pjson) {
            paramArray.push(encodeURIComponent(d) + '=' +
                encodeURIComponent(pjson[d]));
        }
        return paramArray.join('&');
    },
    showGroupErrorMsg :function(id, errorMessage) {
        $("#" + id + "_div").addClass("pnlerr");
        $("#" + id + "_div").append("<span class='err'>" + errorMessage + "</span>");
    }
}