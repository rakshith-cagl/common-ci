
 Apz.Data.prototype.loadData = function(ifaceId, appId, scrId) {
    if (!this.apz.isNull(ifaceId)) {
        ifaceId = appId ? appId + "__" + ifaceId : this.apz.currAppId + "__" + ifaceId;
    }
    var containers = this.apz.scrMetaData.containers;
    var noOfContainers = containers.length;
    var prevScrName = "",splitId="",screenName="";
    if (noOfContainers > 0) {
        for (var c = 0; c < noOfContainers; c++) {
            var container = containers[c];
            if (!this.apz.isNull(ifaceId)) {
                if (container.ifaces.indexOf(ifaceId) > -1) {
                    this.resetContainer(container);
                    splitId = containers[c].name.split("__");
                    screenName = splitId[1]+'_'+splitId[0];
                    if(prevScrName!=screenName){
                        apz.compFactory[screenName].compFactory.changeDetectorRef.detectChanges();
                        prevScrName = screenName;
                    }
                }
            } else {
                this.resetContainer(container);
                splitId = containers[c].name.split("__");
                screenName = splitId[1]+'_'+splitId[0];
                if(prevScrName!=screenName){
                    apz.compFactory[screenName].compFactory.changeDetectorRef.detectChanges();
                    prevScrName = screenName;
                }
            }
         
        }
        
    }
   // this.updateStateContext(scrId);
} 

/* Apz.Data.prototype.buildData = function(ifaceId, appId) {
    /// No need to build anyting, but return scrdata as it has been built already.
    ////Extract for Specified interface
    if (!this.apz.isNull(ifaceId)) {
        var ifaceName = this.apz.getIfaceName(ifaceId);
        var ifaceType = this.apz.getIfaceType(ifaceId)
        var ifaceData = {};
        var len = ifaceId.length;
        var end4 = ifaceId.substr(len - 4);
        if (ifaceType == "DATABASE") {
            ifaceData[ifaceName + "_Req"] = this.scrdata[ifaceName + "_Req"];
        } else if ((end4 == "_Req") || (end4 == "_Res")) {
            ifaceData[ifaceId] = this.scrdata[ifaceId];
        } else {
            ifaceData[ifaceId + "_Req"] = this.scrdata[ifaceId + "_Req"];
            ifaceData[ifaceId + "_Res"] = this.scrdata[ifaceId + "_Res"];
        }
        ifaceData = this.apz.copyJSONObject(ifaceData);
        this.apz.scrinterfaceData = ifaceData;
        return ifaceData;
    }
} */

Apz.Data.prototype.resetContainer = function(container) {
    var params = {};
    params.recNo = 0;
    params.container = container.id;
    this.goToRecord(params);
}

Apz.Data.prototype.getDataPointer = function(nodeId, pRec, pointer) {
    if (!pointer) {
        pointer = this.scrdata;
    }
    if (!this.apz.isNull(nodeId)) {
        if (this.apz.isNull(this.apz.scrMetaData.nodesMap[nodeId])) {
            pointer = null;
        } else {
            var parents = this.apz.scrMetaData.nodesMap[nodeId].parents;
            var parentsLen = parents.length;
            var lNodeId = "";
            var recNo = 0;
            //    var lmrParent = this.apz.scrMetaData.nodesMap[nodeId].mrParent;
            for (var p = 0; p <= parentsLen; p++) {
                lNodeId = parents[p];
                if (p == parentsLen) {
                    lNodeId = nodeId;
                    recNo = pRec;
                } else {
                    lNodeId = parents[p];
                    recNo = this.apz.scrMetaData.nodesMap[lNodeId].currRec;

                    if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                        if (this.apz.scrMetaData.nodesMap[nodeId].mrParent == lNodeId) {
                            if (recNo == -1 || recNo !== pRec) {
                                recNo = pRec;
                            }

                        } else {
                            if (recNo == -1) {
                                recNo = 0;
                            }
                        }
                    }
                }
                ////IDERES
                var nodeName = this.apz.getNodeName(lNodeId);
                if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                    if (!pointer[nodeName]) {
                        pointer = null;
                        break;
                    } else {
                        if (!pointer[nodeName][recNo]) {
                            pointer = null;
                            break;
                        }
                    }
                    pointer = pointer[nodeName][recNo];
                } else {
                    if (!pointer[nodeName]) {
                        pointer = null;
                        break;
                    }
                    pointer = pointer[nodeName];
                }
            }
        }
    }
    return pointer;
}

Apz.Data.prototype.getParentDataPointer = function(nodeId, pRec, pointer) {
    if (!pointer) {
        pointer = this.scrdata;
    }
    if (!this.apz.isNull(nodeId)) {
        if (this.apz.isNull(this.apz.scrMetaData.nodesMap[nodeId])) {
            pointer = null;
        } else {
            var parents = this.apz.scrMetaData.nodesMap[nodeId].parents;
            var parentsLen = parents.length;
            var lNodeId = "";
            var recNo = 0;
            for (var p = 0; p < parentsLen; p++) {
                lNodeId = parents[p];
                if (p == parentsLen - 1) {
                    recNo = this.apz.scrMetaData.nodesMap[lNodeId].currRec;
                } else {
                    lNodeId = parents[p];
                    recNo = this.apz.scrMetaData.nodesMap[lNodeId].currRec;
                    if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                        if (this.apz.scrMetaData.nodesMap[nodeId].mrParent == lNodeId) {
                            if (recNo == -1) {
                                recNo = pRec;
                            }
                        } else {
                            if (recNo == -1) {
                                recNo = 0;
                            }
                        }
                    }
                }
                var nodeName = this.apz.getNodeName(lNodeId);
                if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                    if (!pointer[nodeName]) {
                        pointer = null;
                        break;
                    } else {
                        if (!pointer[nodeName][recNo]) {
                            pointer = null;
                            break;
                        }
                    }
                    pointer = pointer[nodeName][recNo];
                } else {
                    if (!pointer[nodeName]) {
                        pointer = null;
                        break;
                    }
                    pointer = pointer[nodeName];
                }
            }
        }
    }
    return pointer;
}

Apz.Data.prototype.getContainerParentData = function(containerId, dataRecNo, dataContext) {
    var containerData = this.apz.scrMetaData.containersMap[containerId];
    var containerType = containerData.type;
    var lnodes = containerData.nodes;
    var noOfNodes = lnodes.length;
    var currRecDataPointers = [];
    if (noOfNodes > 0) {
        ////Set Current Records and Populate Data Pointers for all Nodes for the Record Sent..
        for (var n = 0; n < noOfNodes; n++) {
            let nodeId = lnodes[n];
            if ((containerType == 'FORM' || containerType == 'NAVBAR' || containerType == 'LIST') && this.apz.scrMetaData.nodesMap[nodeId].relType == "1:1") {
                var mrParent = this.apz.scrMetaData.nodesMap[nodeId].mrParent;
                dataRecNo = this.apz.scrMetaData.nodesMap[mrParent].currRec;
            }
            let dataPointer = this.getParentDataPointer(nodeId, dataRecNo, dataContext);
            if (dataPointer !== null) {
                var nodeName = apz.getNodeName(nodeId);
                if (dataPointer[nodeName]) {
                    currRecDataPointers[nodeId] = dataPointer[nodeName];
                }
                this.apz.scrMetaData.nodesMap[nodeId].currRec = dataRecNo == -1 ? 0 : dataRecNo;
            } else {
                if (this.apz.scrMetaData.nodesMap[nodeId].relType == "1:N") {
                    this.apz.scrMetaData.nodesMap[nodeId].currRec = -1;
                } else {
                    this.apz.scrMetaData.nodesMap[nodeId].currRec = 0;
                }
                currRecDataPointers[nodeId] = null;
            }
        }
    }
    return currRecDataPointers;
}

Apz.Data.prototype.updateStateContext = function(scrId) {
    if (apz.statesMap[scrId]) {
        apz.statesMap[scrId].setState({});
    } else {
        let screens = Object.keys(apz.statesMap);
        for (let s = 0; s < screens.length; s++) {
            apz.statesMap[screens[s]].setState({});
        }
    }
}

Apz.Data.prototype.createRow = function(container, scrContext) {
    var createRow = true;
    try {
        createRow = apz.app.preCreateRow(container);
        if (this.apz.isNull(createRow)) {
            createRow = true;
        }
    } catch (err) {
        createRow = true;
    }
    if (createRow) {
        let containerMetaData = this.apz.scrMetaData.containersMap[container];
        var totalRecs = this.getTotalRecords(container);
        var dataRecNo = -1;
        var selRow = -1;
        var action = "L";
        var newRec = -1;
        var currRec = -1;
        var multiRec = containerMetaData.multiRec;
        if (multiRec == "Y") {
            // Get First Slected Row..
            /* Yet to be managed as react doesn't prefer to touch dom directly
            var noOfRows = apzJquery("#" + container + "_tbody tr").length;
            for (var r = 0; r < noOfRows; r++) {
                if (this.isRowSelected(container, r)) {
                    selRow = r;
                    break;
                }
            }*/
            if (selRow >= 0) {
                newRec = this.getDataRec(container, selRow);
                action = "B";
            } else {
                newRec = totalRecs;
                action = "L";
            }
        } else {
            currRec = containerMetaData.currRec;
            if (currRec == (containerMetaData.totalRecs - 1)) {
                newRec = totalRecs;
                action = "L";
            } else {
                newRec = this.getDataRec(container, containerMetaData.currRow) + 1;
                action = "A";
            }
        }
        if (newRec >= 0) {
            var nodes = this.getContainerNodes(container);
            var noOfNodes = nodes.length;
            var node = "";
            if (noOfNodes > 0) {
                for (var i = 0; i < noOfNodes; i++) {
                    node = nodes[i];
                    //if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {}
                    var force = false;
                    if (action !== "L") {
                        force = true;
                    }
                    var params = {};
                    params.nodeId = node;
                    params.dataRecNo = newRec;
                    params.force = force;
                    this.createDataPointer(params);
                }
            }
        }
    }
    try {
        apz.app.postCreateRow(container);
    } catch (err) {}
    // //Go To Record
    var params = {};
    params.tracker = null;
    params.container = container;
    params.recNo = newRec;
    this.goToRecord(params);
   // this.reRenderComponent(container);
    //apz.data.updateStateContext();
    this.reRenderComponent(container);
}

Apz.Data.prototype.createContainerRecord = function(params) {
    //Expects containerId, dataRecNo, action
    var group = this.apz.scrMetaData.containersMap[params.containerId].group;
    var nodes;
    if (this.apz.isNull(group)) {
        nodes = this.apz.scrMetaData.containersMap[params.containerId].nodes;
    } else {
        nodes = this.apz.scrMetaData.groupsMap[group].nodes;
    }
    var noOfNodes = nodes.length;
    var node = "";
    var multiRec = this.apz.scrMetaData.containersMap[params.containerId].multiRec;
    if (noOfNodes > 0) {
        /////Create New data Records..
        for (var n = 0; n < noOfNodes; n++) {
            node = nodes[n];
            ////Call Set data
            if (params.action == "B") {
                var containerTracker = [];
                var dataPointers = [];
                params.dataPointers = dataPointers;
                params.tracker = containerTracker;
                this.setData(params);
            }
            ////Create New Record...The Last Flag(true) is to Ensure Force insert even if the record exists
            var forceFlag = false;
            if ((params.action == "A") || (params.action == "B")) {
                forceFlag = true;
            }
            var args = {};
            args.nodeId = node;
            args.dataRecNo = params.dataRecNo;
            args.force = forceFlag;
            args.pointer = params.pointer;
            this.createDataPointer(args);
            ////Manipulate Current Record..
            if (params.action == "B") {
                if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {
                    this.apz.scrMetaData.nodesMap[node].currRec = this.apz.scrMetaData.nodesMap[node].currRec + 1;
                }
            }
        }
    }
}

Apz.Data.prototype.createDataPointer = function(params) {
    //Expects nodeId, dataRecNo, force
    var pointer = params.pointer;
    if (!pointer) {
        pointer = this.scrdata;
    }
    var mrParent = this.apz.scrMetaData.nodesMap[params.nodeId].mrParent;
    if ((params.nodeId != null) && (params.nodeId != "") && (params.nodeId != "undefined") && (params.nodeId != undefined)) {
        var parents = this.apz.scrMetaData.nodesMap[params.nodeId].parents;
        var parentsLen = parents.length;
        var lNodeId = "";
        var recNo = 0;
        for (var p = 0; p <= parentsLen; p++) {
            if (p == parentsLen) {
                lNodeId = params.nodeId;
                recNo = params.dataRecNo;
            } else {
                lNodeId = parents[p];
                //Pradeep Changes for pointing to exact scrData element
                recNo = params.dataRecNo || this.apz.scrMetaData.nodesMap[lNodeId].currRec;
                if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                    if (this.apz.scrMetaData.nodesMap[params.nodeId].mrParent == lNodeId) {
                        if (recNo == -1) {
                            recNo = params.dataRecNo;
                        }
                    }
                }
            }
            ////IDERES
            var nodeName = this.apz.getNodeName(lNodeId);
            if (recNo >= 0) {
                if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                    if (!pointer[nodeName]) {
                        pointer[nodeName] = new Array();
                        recNo = 0;
                        pointer[nodeName][0] = {};
                    } else {
                        if (!pointer[nodeName][recNo]) {
                            pointer[nodeName][recNo] = {};
                        } else {
                            ////Record Exists.. If Insert then Create a new Record anyway...
                            if (params.force) {
                                //Pradeep Changes for avoiding creation of extra node
                                //if(!pointer[nodeName][recNo]){
                                    var childNodesParent = apz.scrMetaData.nodesMap[params.nodeId].parent;
                                    if(lNodeId!=childNodesParent)
                                    pointer[nodeName].splice(recNo, 0, {});
                               // }
                            }
                        }
                    }
                    pointer = pointer[nodeName][recNo];
                } else {
                    if (!pointer[nodeName]) {
                        pointer[nodeName] = {};
                    }
                    pointer = pointer[nodeName];
                }
            } else {
                pointer = null;
                break;
            }
        }
    }
    return pointer;
}

Apz.Data.prototype.removeRows = function(container) {
    var removeRow = true;
    var ctrMetaData = this.apz.scrMetaData.containersMap[container];
    try {
        removeRow = apz.app.removeRows(container);
        if (this.apz.isNull(removeRow)) {
            removeRow = true;
        }
    } catch (err) {
        removeRow = true;
    }
    if (removeRow) {
        var totalRecs = this.getTotalRecords(container);
        var currRec = ctrMetaData.currRec;
        if (totalRecs > 0) {
            var newRec = 0;
            var nodes = this.getContainerNodes(container);
            for(var i = 0; i <nodes.length;i++){
                var nodesParent = apz.scrMetaData.nodesMap[nodes[i]].parent;
                if(nodes.indexOf(nodesParent)!=-1){
                    nodes.splice(i,1);
                }
            }
            var noOfNodes = nodes.length;
            var multiRec = ctrMetaData.multiRec;
            if (multiRec == "Y") {
                var currRecDeletd = false;
                var recsBeforeCurrRec = 0;
                var minRec = 9999999;
                var maxRec = -1;
                var noOfRows = ctrMetaData.pageRows;
                if (noOfRows > 0) {
                    for (var r = noOfRows - 1; r >= 0; r--) {
                        let dataRecNo = this.getDataRec(container, r);
                        if (this.isRowSelected(container, r)) {
                            // //Minimum Record Deleted
                            if (dataRecNo <= minRec) {
                                minRec = dataRecNo;
                            }
                            // //Maximum Record Deleted
                            if (dataRecNo >= maxRec) {
                                maxRec = dataRecNo;
                            }
                            // /Current Record Deleted
                            if (dataRecNo === currRec) {
                                currRecDeletd = false;
                            }
                            // //No of records deleted before currRec
                            if (dataRecNo < currRec) {
                                recsBeforeCurrRec = recsBeforeCurrRec + 1;
                            }
                            var node = "";
                            if (noOfNodes > 0) {
                                for (var i = 0; i < noOfNodes; i++) {
                                    node = nodes[i];
                                    if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {
                                        totalRecs = totalRecs - 1;
                                        this.deleteRecord(node, dataRecNo);
                                    }
                                }
                            }
                        }
                    }
                }
                if (currRecDeletd) {
                    // /First Rec of curr Page
                    var newRec = this.getDataRec(container, 0);
                } else {
                    newRec = currRec - recsBeforeCurrRec;
                }
            } else {
                var node = "";
                if (noOfNodes > 0) {
                    for (var i = 0; i < noOfNodes; i++) {
                        node = nodes[i];
                        this.deleteRecord(node, currRec);
                        newRec = currRec;
                        // //Decrement
                        totalRecs = totalRecs - 1;
                    }
                }
            }
            // //Check if the new record is available..
            if (newRec > (totalRecs - 1)) {
                newRec = totalRecs - 1;
            }
            if (newRec <= 0) {
                newRec = 0;
            }
            // //Go To Record
            var params = {};
            params.tracker = null;
            params.container = container;
            params.recNo = newRec;
            this.goToRecord(params);
            this.reRenderComponent(container);
            //this.updateStateContext();
            //this.reInitializeComponent(container);
        }
    }
    try {
        apz.app.postRemoveRows(container);
    } catch (err) {}
}

Apz.Data.prototype.deleteRecord = function(nodeId, rec) {
    if (rec >= 0) {
        var nodeName = this.apz.getNodeName(nodeId);
        var parentNode = this.apz.scrMetaData.nodesMap[nodeId].parent;
        var parentCurrRec = -1;
        if (this.apz.isNull(parentNode)) {
            parentCurrRec = 0;
        } else {
            parentCurrRec = this.apz.scrMetaData.nodesMap[parentNode].currRec;
        }
        var lparentpointer = this.getDataPointer(parentNode, parentCurrRec);
        if (this.apz.scrMetaData.nodesMap[nodeId].relType == "1:N") {
            // //Delete the Record..
            if (lparentpointer[nodeName]) {
                lparentpointer[nodeName].splice(rec, 1);
            }
        } else {
            lparentpointer[nodeName] = null;
        }
    }
}

Apz.Data.prototype.saveAndRender = function(props, event) {
   this.setPropsData(props, event.target.value);
}

Apz.Data.prototype.saveValue = function(props, event) {
    this.setPropsData(props, event.target.value);
}

Apz.Data.prototype.saveCheckbox = function(props, event) {
    if(event.target.checked){
        this.setPropsData(props, event.target.getAttribute("checkedval"));
    } else if(event.target.indeterminate){
        this.setPropsData(props, event.target.getAttribute("indeterminateval"));
    } else {
        this.setPropsData(props, event.target.getAttribute("uncheckedval"));
    }
}

Apz.Data.prototype.saveSimpleDropdown = function(id, props, event) {
    let curVal = event.target.getAttribute("data-value");
    apz.data.setPropsData(props, curVal);
}

Apz.Data.prototype.saveMultiTagsDropdown = function(id, props, event) {
    let curVal = $(event.target).val().toString();
    apz.data.setPropsData(props, curVal);
}

Apz.Data.prototype.rowClicked = function(rowObj, event, componentContext) {
    if (!this.apz.isNull(event) && !($(event.target).hasClass('dticon') || $(event.target).parent().hasClass('dticon') || $(event.target).parent().parent().hasClass('dticon'))) {
        var rowClicked = true;
        var rowId = rowObj.id;
        var ind = rowId.lastIndexOf("_row_");
        var containerId = rowId.substring(0, ind);
        var rowNo = this.apz.getInt(rowId.substring(ind + 5)); 
        if (this.apz.isFunction(componentContext.beforeRowClicked)) {
        	//In Angular, instead defining call back hooks of row clicked under "apz.app" object in a common js file and handle for different containers in same method, with this change 
        	//of getting component context and invoking row click methods of component provides more angular way of handling things. 
            rowClicked = componentContext.beforeRowClicked(containerId, rowNo, event);
            if (this.apz.isNull(rowClicked)) {
                rowClicked = true;
            }
        }
        var contData = this.apz.scrMetaData.containersMap[containerId];
        if (rowClicked && !this.apz.isNull(contData) && this.apz.isNull(contData.searchIndex)) {
            var dataRecNo = this.getDataRec(containerId, rowNo);
            var currRec = contData.currRec;
            if (dataRecNo != currRec) {

                var params = {};
                params.tracker = null;
                params.container = containerId;
                params.recNo = dataRecNo;
                this.goToRecord(params);
                this.appendnestList(containerId, rowNo);
               // this.reRenderComponent(containerId);
                //List inside List Changes
               // apz.data.updateStateContext();
            }
        }
        if (this.apz.isFunction(componentContext.afterRowClicked)) {
        	componentContext.afterRowClicked(containerId, rowNo, event);
        }
        var parentContId = $("#" + containerId).attr("targetoverlayid");
        if (!this.apz.isNull(parentContId)) {
            if (this.apz.isFunction(this.apz.app["overlayClicked_" + parentContId])) {
                if (!this.apz.isNull(parentContId) && parentContId != containerId) {
                    this.apz.app["overlayClicked_" + parentContId](parentContId, containerId, rowNo);
                    this.apz.searchRecords(containerId, "");
                    if ($("#" + containerId).find(".srh-inp").length > 0) {
                        $("#" + containerId).find(".srh-inp").val("");
                    }
                    this.apz.closeOverlay($('#' + parentContId + "_dropdown")[0], containerId, "Y");
                }
            }
        }
    }
}
Apz.Data.prototype.appendnestList = function(containerId, currRec) {
    var containerid = containerId + "_row_" + currRec;
      if ( typeof (this.apz.scrMetaData.containersMap[containerId].childs) != "undefined" && this.apz.scrMetaData.containersMap[containerId].childs.length > 0) {
         var parentNode = document.getElementById(containerid);
         var len = this.apz.scrMetaData.containersMap[containerId].childs.length;
         for ( i = 0; i < len; i++) {
            parentNode.appendChild(document.getElementById(this.apz.scrMetaData.containersMap[containerId].childs[i]));
         }
         this.reRenderComponent(containerId);
      }
}
Apz.Data.prototype.goToRecord = function(params) {
    //Expects tracker, container, recNo
    var ctrMetaData = this.apz.scrMetaData.containersMap[params.container];
    ctrMetaData.update = true;
    var multiRec = ctrMetaData.multiRec;
    var nodes = this.getContainerNodes(params.container);
    var noOfNodes = nodes.length;
    var node = "";
    var containerType = this.apz.scrMetaData.containersMap[params.container].type;
    ////Set currRec for all Nodes
    if (noOfNodes > 0) {
        for (var i = 0; i < noOfNodes; i++) {
            node = nodes[i];
            this.apz.scrMetaData.nodesMap[node].currRec = params.recNo;
            ////Set Container currRec based on Master Node of the Container
            if (i === 0) {
                if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {
                    this.apz.scrMetaData.containersMap[params.container].currRec = params.recNo;
                } else {
                    this.apz.scrMetaData.containersMap[params.container].currRec = 0;
                }
            }
        }
    }
    ////Set Total Records,Current Page, and Total Pages..
    var recs = 0;
    var totalPages = 0;
    var currPage = 0;
    var pageRows = 0;
    recs = this.getTotalRecords(params.container);
    totalPages = this.getTotalPages(recs, params.container);
    currPage = this.getCurrPage(params.recNo, params.container);
    this.apz.scrMetaData.containersMap[params.container].totalRecs = recs;
    this.apz.scrMetaData.containersMap[params.container].totalPages = totalPages;
    this.apz.scrMetaData.containersMap[params.container].currPage = currPage;
    this.apz.scrMetaData.containersMap[params.container].currRow = this.getRow(params.container, params.recNo);
    pageRows = this.getPageRows(params.container);
    this.apz.scrMetaData.containersMap[params.container].pageRows = pageRows;
    ////Manipulate Pagination Controls
    var pgntnStyle = this.apz.scrMetaData.containersMap[params.container].paginationStyle;
    if ((!this.apz.isNull(pgntnStyle)) && $(document.getElementById(params.container)).is(":visible") && (containerType == "LIST" || containerType == "TABLE") && (pgntnStyle == "PAGE2")) {
       // this.apz.showPageStyle2Controls(params.container);
    }
    ////Paint Chart and Gauges
    if (containerType == "GAUGE") {
        var myObj = this;
        var gaugeObj = this.apz.scrMetaData.gaugesMap[params.container];
        var gauges = new Apz.Gauges(myObj.apz);
        gauges.paintGauge(gaugeObj);
      //  this.updateStateContext();
    } else if (containerType == "CHART") {
        var myObj = this;
        requirejs([this.apz.getInfraPath() + "/appzillon/charts.js"], function() {
            var chartObj = myObj.apz.scrMetaData.chartsMap[params.container];
            try {
                if (chartObj != undefined) {
                    var charts = new Apz.Charts(myObj.apz);
                    charts.paintChart(chartObj);
                }
            } catch (e) {
                console.log("Problems with Charts Library");
            }
        });
       // this.updateStateContext();
    }
    ////Update Tracker
    if (this.apz.isNull(params.tracker)) {
        params.tracker = {};
    }
    params.tracker[params.container] = "Y";
    // //Child Processing...
    var childNode = null;
    var noOfChilds = 0;
    if (noOfNodes > 0) {
        for (var i = 0; i < noOfNodes; i++) {
            node = nodes[i];
            noOfChilds = this.apz.scrMetaData.nodesMap[node].childs.length;
            if (noOfChilds > 0) {
                for (var c = 0; c < noOfChilds; c++) {
                    childNode = this.apz.scrMetaData.nodesMap[node].childs[c];
                    var group = this.apz.scrMetaData.nodesMap[childNode].group;
                    if (!this.apz.isNull(group)) {
                        var childContainers = this.apz.scrMetaData.groupsMap[group].containers;
                        if (childContainers.length > 0) {
                            var childContainer = childContainers[0];
                            if (!this.apz.containsKey(params.tracker, childContainer)) {
                                var args = {};
                                args.tracker = params.tracker;
                                args.container = childContainer;
                                args.recNo = 0;
                                this.goToRecord(args);
                            }
                        }
                    }
                }
            }
        }
    }
    /*if(containerType == "TABLE") {
       this.apz.setTableHeight(params.container,false);
       this.apz.initDataTable(document.getElementById(params.container+"_table"),false);
       if(apzJquery(document.getElementById(params.container+"_table")).hasClass('responsive') && apzJquery(document.getElementById(params.container+"_table")).find('tbody tr').length > 0) {
         this.apz.closeResponsiveTableRows(apzJquery(document.getElementById(params.container+"_table")));
       }
    }*/
}
/* Apz.Data.prototype.setDropDownData = function(event) {
//77
var value = apz.getElmValue(event.currentTarget.id);
let elmId = this.apz.getObjIdWORowNumber($("#"+event.currentTarget.id)[0]);
let elmData = this.apz.scrMetaData.elmsMap[elmId];
let containerId = elmData.container
let rowNo = Number($("#"+event.currentTarget.id).attr("rowNo"));

let values = this.apz.data.getvaluesforDropDown(elmData,containerId,rowNo);
let elmValue = elmId.split("_").pop();
values[elmValue]=value;
//apz.setElmValue(""+elmId+rowNo,elmValue);
//this.reRenderComponent(containerId);

} */

Apz.Data.prototype.setElmData = function(id,value,fromAngular){
   // var value = value || apz.getElmValue(id); //value from param is different from actual for multiselectTags drop down 
   var value =  apz.getElmValue(id);
    var obj = $("#"+id)[0];
    var recievedId = id;
    var elmId = this.apz.getObjIdWORowNumber(obj);
    var rowNumber =this.apz.getObjRowNumber(obj);
    if(obj.tagName === 'SPAN'){
        const indexval = recievedId.indexOf("txtcnt")
        if(indexval != -1){
            if(elmId.indexOf("txtcnt") !=-1){
                elmId = elmId.slice().replace("_txtcnt","")
            }
            if( rowNumber !== -1){
                var lastIndex = elmId.lastIndexOf("_");
                elmId = elmId.substring(0, lastIndex);
            }
        }

    }
    

    let elmData = this.apz.scrMetaData.elmsMap[elmId];
    let containerId = elmData.container;
    let values = this.apz.data.getElmData(elmData,containerId,rowNumber);
    let elmValue = elmId.split("_").pop();
    values[elmValue]=value;
    if(!fromAngular){
         apz.data.reRenderComponent(containerId);
    }
}

Apz.Data.prototype.getElmData = function(elmData,containerId,rowNo){
    var values = "";
    if(elmData.nodeId){
        let startRec = this.getStartRec(containerId);
        let nodeId = elmData.nodeId;
        let nodesMapData = apz.scrMetaData.nodesMap[nodeId];
        if(nodesMapData && nodesMapData.multiRec=='Y' && rowNo== -1){
            rowNo = nodesMapData.currRec;
        }
        let nodeData = this.getDataPointer([elmData.nodeId], rowNo + startRec);
        if (nodeData) {
            if (this.apz.getDataType(nodeData) == "Array") {
              values = nodeData[rowNo];
              
            } else {
              values = nodeData;
            }
          }
    }
    return values;
}
/* Apz.Data.prototype.setTagsInputData = function(id) {
    //77
    var value = apz.getElmValue(id);
    let elmId = this.apz.getObjIdWORowNumber($("#"+id)[0]);
    let elmData = this.apz.scrMetaData.elmsMap[elmId];
    let containerId = elmData.container
    let rowNo = Number($("#"+id).attr("rowNo"));
    
    let values = this.apz.data.getvaluesforTagsInput(elmData,containerId,rowNo);
    let elmValue = elmId.split("_").pop();
    values[elmValue]=value;
    //apz.setElmValue(""+elmId+rowNo,elmValue);
    //this.reRenderComponent(containerId);
    
    }
    Apz.Data.prototype.getvaluesforTagsInput = function(elmData,containerId,rowIndex) {
    
        var values = "";
        if(elmData.nodeId){
            let startRec = this.getStartRec(containerId);
            let nodeData = this.getDataPointer([elmData.nodeId], rowIndex + startRec);
            if (nodeData) {
                if (this.apz.getDataType(nodeData) == "Array") {
                  values = nodeData[rowIndex];
                  
                } else {
                  values = nodeData;
                }
              }
        }
        return values;
    }
Apz.Data.prototype.getvaluesforDropDown = function(elmData,containerId,rowIndex) {
    
    var values = "";
    if(elmData.nodeId){
        let startRec = this.getStartRec(containerId);
        let nodeData = this.getDataPointer([elmData.nodeId], rowIndex + startRec);
        if (nodeData) {
            if (this.apz.getDataType(nodeData) == "Array") {
              values = nodeData[rowIndex];
              
            } else {
              values = nodeData;
            }
          }
    }
    return values;
}
 */
Apz.Data.prototype.getContainerNodes = function(container) {
    let ctrMetaData = this.apz.scrMetaData.containersMap[container];
    if(!ctrMetaData){
        return [];
    }
    var group = ctrMetaData.group;
    let nodes = "";
    if (this.apz.isNull(group)) {
        nodes = this.apz.scrMetaData.containersMap[container].nodes;
    } else {
        nodes = this.apz.scrMetaData.groupsMap[group].nodes;
    }
    if(apz.isNull(nodes)){
        nodes = [];
    }
    return nodes;
}

Apz.Data.prototype.getRow = function(container, dataRec) {
    var row = dataRec;
    if (this.apz.scrMetaData.containersMap[container].paginationStyle !== "APPEND") {
        row = dataRec % this.apz.scrMetaData.containersMap[container].pageSize;
    }
    return row;
}

Apz.Data.prototype.getTotalRecords = function(container) {
    var recs = 0;
    var group = this.apz.scrMetaData.containersMap[container].group;
    var nodes = this.getContainerNodes(container);
    var noOfNodes = nodes.length;
    if (noOfNodes > 0) {
        recs = this.getNoOfRecs(nodes[0]);
    }
    if (recs === 0) {
        var multiRec = this.apz.scrMetaData.containersMap[container].multiRec;
        if (multiRec == "N") {
            recs = 1;
        }
    }
    return recs;
}

Apz.Data.prototype.getTotalPages = function(recs, container) {
    var noOfPages = 1;
    var containerData = apz.scrMetaData.containersMap[container];
    if(containerData.type=="FORM"){
        var nodes = containerData.nodes;
        if(nodes && nodes.length>0){
           if(this.apz.scrMetaData.nodesMap[nodes[0]].multiRec=='Y'){
            noOfPages= recs;
           }
        }
     }else{
        var pageSize = this.apz.scrMetaData.containersMap[container].pageSize;
        noOfPages = Math.ceil(recs / pageSize);
        if (noOfPages === 0) {
            noOfPages = 1;
        }
     }
    
    return noOfPages;
}

Apz.Data.prototype.getCurrPage = function(recNo, container) {
    var currPage = 1;
    var oldCurrPage = this.apz.scrMetaData.containersMap[container].currPage;
    var pageSize = this.apz.scrMetaData.containersMap[container].pageSize;
    recNo = recNo + 1;
    currPage = Math.ceil(recNo / pageSize);
    if (currPage === 0) {
        currPage = 1;
    }
    // //Handle for Append Should not Decrement..                            TBC - Should it be handled for append case?
    // if (this.scrMetaData.containersmap[container].paginationStyle == "APPEND") {
    //    if (oldCurrPage > currPage) {
    //       currPage = oldCurrPage;
    //    }
    // }
    return currPage;
}

Apz.Data.prototype.getPageRows = function(container) {
    var pageRows = 0;
    var ctrMetaData = this.apz.scrMetaData.containersMap[container];
    var totalRecs = ctrMetaData.totalRecs;
    var pageSize = ctrMetaData.pageSize;
    var currPage = ctrMetaData.currPage;
    var totalPages = ctrMetaData.totalPages;
    if(totalPages == 1){
        pageRows = totalRecs;
    } else if(currPage == totalPages){
        //pradeep changes
        pageRows = totalRecs % pageSize;
        if(pageRows == 0 && totalRecs>0){
            pageRows = Math.ceil(totalRecs/totalPages);
           // pageRows = Math.ceil(totalRecs/pageSize);
        }
      // pageRows = Math.ceil(totalRecs/pageSize);
    } else if(totalPages > currPage){
        pageRows = pageSize;
    }
    return pageRows;
}

Apz.Data.prototype.getStartRec = function(container) {
    var rec = 0;
    let containerMetaData = this.apz.scrMetaData.containersMap[container];
    if (containerMetaData.currPage == 1) {
        rec = 0;
    } else if (containerMetaData.paginationStyle == "APPEND") {
        rec = 0;
    } else {
        rec = ((containerMetaData.currPage - 1) * (containerMetaData.pageSize));
    }
    return rec;
}

Apz.Data.prototype.getLimit = function(container) {
    let containerMetaData = this.apz.scrMetaData.containersMap[container];
    var rec = containerMetaData.pageSize;
    //if (containerMetaData.paginationStyle == "APPEND") {
        rec = (containerMetaData.currPage) * containerMetaData.pageSize;
        rec = (rec > containerMetaData.totalRecs) ? containerMetaData.totalRecs : rec;
    //}
    return rec;
}

Apz.Data.prototype.getPageRecords = function(container) {
    var ctrMetaData = apz.scrMetaData.containersMap[container];
    var mrParent = "";
    let cntrData = {};
    var nodes = this.getContainerNodes(container);
    if (nodes.length > 0) {
        let firstNode = nodes[0];
        mrParent = apz.scrMetaData.nodesMap[firstNode].mrParent;
        let curRec = apz.scrMetaData.nodesMap[mrParent].currRec;
        cntrData = apz.data.getContainerParentData(container, curRec);
        let startRec = this.getStartRec(container);
        let endRec = this.getLimit(container);
        if (cntrData[firstNode] && (this.apz.getDataType(cntrData[firstNode]) == "Array")) {
            if(ctrMetaData.multiRec == "Y"){
                cntrData[firstNode] = cntrData[firstNode].slice(startRec, endRec);
            } else {
                cntrData[firstNode] = cntrData[firstNode][ctrMetaData.currRec];
            }
        }
    }
    return cntrData;
}

Apz.Data.prototype.setPropsData = function(props, value) {
    var elmMetaData = apz.scrMetaData.elmsMap[props.id];
    if (!props.dataAvail && elmMetaData.nodeId) {
        var ctrMetaData = apz.scrMetaData.containersMap[elmMetaData.container];
        var nodeMetaData = apz.scrMetaData.nodesMap[elmMetaData.nodeId];
        let params = {};
        params.containerId = elmMetaData.container;
        params.dataRecNo = nodeMetaData.currRec;
        params.action = "L";
        this.createContainerRecord(params);
        var ctrDatas = apz.data.getContainerParentData(ctrMetaData.id, nodeMetaData.currRec, null);
        var elmData = ctrDatas[elmMetaData.nodeId];
        elmData[props.name] = value;
        props.data[props.name] = value;
    } else {
        props.data[props.name] = value;
    }
    apz.data.updateStateContext();
}

Apz.Data.prototype.changeRow = function(container, ind) {
    var changeRow = true;
    try {
        changeRow = apz.app.preChangeRow(container, ind);
        if (this.apz.isNull(changeRow)) {
            changeRow = true;
        }
    } catch (err) {
        changeRow = true;
    }
    if (changeRow) {
        var newRec = 0;
        if (ind == "N") {
            newRec = this.apz.scrMetaData.containersMap[container].currRec + 1;
        } else if (ind == "P") {
            newRec = this.apz.scrMetaData.containersMap[container].currRec - 1;
        } else if (ind == "F") {
            newRec = 0;
        } else if (ind == "L") {
            newRec = this.apz.scrMetaData.containersMap[container].totalRecs - 1;
        } else if (ind == "S") {
            var cpId = container + "_cr";
            try {
                newRec = parseInt(document.getElementById(cpId).innerHTML) - 1;
            } catch (e) {
                newRec = this.apz.scrMetaData.containersMap[container].currRec;
            }
        }
        if ((newRec >= 0) && (newRec < this.apz.scrMetaData.containersMap[container].totalRecs) && (newRec != this.apz.scrMetaData.containersMap[container].currRec)) {
           var params = {};
           params.tracker = null;
           params.container = container;
           params.recNo = newRec;
            this.goToRecord(params);
           }
        }
        this.reRenderComponent(container);
        //this.updateStateContext();
        //componentChangeDetection();
        //apz.compFactory.changeDetectorRef.detectChanges();
       // this.reInitializeComponent(container);
    try {
        apz.app.postChangeRow(container, ind);
    } catch (e) {}
}
Apz.Data.prototype.reRenderComponent=function(id){
    var splitId = id.split("__");
    var screenName = splitId[1]+'_'+splitId[0];
    var componentInfo = apz.compFactory[screenName];
    if(componentInfo){
        componentInfo.compFactory.changeDetectorRef.detectChanges();
    }
   
}

Apz.Data.prototype.changePage = function(container, ind, event) {
    var checkChangePage = true;
    let containerMetaData = this.apz.scrMetaData.containersMap[container];
    try {
        checkChangePage = this.apz.app.preChangePage(container, ind, event);
        if (this.apz.isNull(checkChangePage)) {
            checkChangePage = true;
        }
    } catch (err) {
        checkChangePage = true;
    }
    if (checkChangePage) {
        var newPage = 0;
        if (ind == "N") {
            newPage = containerMetaData.currPage + 1;
        } else if (ind == "P") {
            newPage = containerMetaData.currPage - 1;
        } else if (ind == "F") {
            newPage = 1;
        } else if (ind == "L") {
            newPage = containerMetaData.totalPages;
        } else if (ind == "S") {
            if (containerMetaData.paginationStyle == "PAGE2" || containerMetaData.paginationStyle == "PAGE3") {
                newPage = parseInt(event.currentTarget.text);
            } else {
                var cpId = container + "_cp";
                try {
                    newPage = parseInt(document.getElementById(cpId).value);
                } catch (e) {
                    newPage = containerMetaData.currPage;
                }
            }
        }
        if(containerMetaData.type=="FORM"){
            if(newPage > 0 && (newPage != containerMetaData.currPage)){
                var dataRecNo = newPage-1;
                var params = {};
                params.tracker = null;
                params.container = container;
                params.recNo = dataRecNo;
                this.goToRecord(params);
            }
        }else{
            if ((newPage > 0) && (newPage <= containerMetaData.totalPages) && (newPage != containerMetaData.currPage)) {
                var dataRecNo = (newPage - 1) * containerMetaData.pageSize;
                var params = {};
                params.tracker = null;
                params.container = container;
                params.recNo = dataRecNo;
                this.goToRecord(params);
            }
        }
       // apz.data.updateStateContext();
       this.reRenderComponent(container);
    }
    try {
        apz.app.postChangePage(container, ind, obj);
    } catch (err) {}
}


/* Apz.Data.prototype.getPageNumbers = function(metaData) {
    let classValue = "ett-bttn tsp sml";
    let startPage = metaData.currPage - 4;
    if (startPage < 1) {
        startPage = 1;
    }
    let endPage = startPage + 5;
    if (endPage > metaData.totalPages) {
        endPage = metaData.totalPages + 1;
    }
    let pages = [];
    for (let i = startPage; i < endPage; i++) {
        pages.push(i);
    }
    return pages.map((page, index) => {
        let activeCls = (page == metaData.currPage) ? "active " : "";
        return('<a id={metaData.id + "_"+(index+1)+"_btn"} key={index} className={activeCls+classValue} onClick={(e) => apz.data.changePage(metaData.id, "S", e)}>{page}</a>')
    })
} */
Apz.Data.prototype.getPageNumbers = function(metaData){
    let startPage = metaData.currPage - 4;
    if (startPage < 1) {
        startPage = 1;
    }
    let endPage = startPage + 5;
    if (endPage > metaData.totalPages) {
        endPage = metaData.totalPages + 1;
    }
    let pages = [];
    for (let i = startPage; i < endPage; i++) {
        pages.push(i);
    }
    return pages;
}
Apz.Data.prototype.handleStepperclick = function (props,pobj) { 
        var inpVal = props.value;
        var inpContent = props.content.direct;
        var step = inpContent.step;
        var maxVal = inpContent.max;
        var minVal = inpContent.min;
        var minusObj = $(pobj).parent().parent().children('li:first').children('button');
        var plusObj = $(pobj).parent().parent().children('li:last').children('button');
        if (step == "" || step == undefined) {
            step = 1;
        }
        var val = 0;
        if (!apz.isNull(inpVal)) {
            val = inpVal;
            if(!isNaN(inpVal) && $.isNumeric(inpVal)){
                inpVal = parseFloat(inpVal);
                step = parseFloat(step);
                if($(pobj).hasClass('stpr-add')) {
                    if((inpVal+step) <=  maxVal ){
                        if((inpVal+step) < minVal){
                            val = minVal;
                        } else {
                            val= (inpVal+step); 
                        }
                    }
                } else {
                    if((inpVal-step) >= minVal){
                        if((inpVal - step) > maxVal){
                            val = maxVal;
                        } else {
                            val = (inpVal-step);
                        }
                    } 
                }
            } else{
                alert('Please enter valid Number');                                                                         
            } 
        }else{                                                   
            val = minVal;                                                     
        }
        if(val <= minVal) {
            minusObj.attr('disabled','disabled');
            plusObj.removeAttr('disabled');
        } else if(val >= maxVal) {
            plusObj.attr('disabled','disabled');
            minusObj.removeAttr('disabled');
        } else if (!isNaN(val)){
            minusObj.removeAttr('disabled');
            plusObj.removeAttr('disabled');
        }
        this.setPropsData(props,val)
    }

Apz.Data.prototype.resetData = function(obj,value=""){
    if(!apz.isNull(obj) && !apz.isNull(obj.props)){
        this.setPropsData(obj.props,value);
    }
}


Apz.Data.prototype.breadCrumbAction = function(obj,componentContext){
    var proceed = true;
    if (this.apz.isFunction(componentContext.beforeBreadcrumbAction)) {
        proceed = componentContext.beforeBreadcrumbAction(obj);
    }
    if (proceed != false) {
        var jqueryObj = $(obj);
        if (!(jqueryObj).hasClass("active")) {
            jqueryObj.addClass("active");
            jqueryObj.siblings('li').removeClass("active");
        }
    }
    if (this.apz.isFunction(componentContext.afterBreadcrumbAction)) {
    	componentContext.afterBreadcrumbAction(obj);
    }
}

Apz.Data.prototype.collapsibleAction = function(obj,componentContext){
	if (this.apz.isFunction(componentContext.beforeCollapsibleAction)) {
		componentContext.beforeCollapsibleAction(obj);
    }
    if ($(obj).parent().find('.ctr').hasClass("slideup")) {
        $(obj).attr({ 'aria-selected': 'true', 'aria-expanded': 'true' });
        this.apz.slideDownTransition(obj);
    } else {
        this.apz.slideUpTransition(obj);
        $(obj).attr({ 'aria-selected': 'false', 'aria-expanded': 'false' });
    }
    if (this.apz.isFunction(componentContext.afterCollapsibleAction)) {
    	componentContext.afterCollapsibleAction(obj);
    }
}