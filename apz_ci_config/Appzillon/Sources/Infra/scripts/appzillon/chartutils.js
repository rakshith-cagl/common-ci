function addVisualPrefs (chartObj) {
	var chartStyleDef = {};
	chartStyleDef.chart = {};
	if (!apz.isNull(chartObj.chartStyleSheetPath)) {
		var chartStyle = apz.getFile(chartObj.chartStyleSheetPath)
		if(!apz.isNull(chartStyle)){
			chartStyleDef = JSON.parse(chartStyle);
		}
	}
	if (chartStyleDef) {
		var attrNodeArr = ["caption", "subcaption", "xaxisname", "yaxisname", "numberprefix"];
		if (chartStyleDef.chart) {
			for (var i = 0; i < attrNodeArr.length; i++) {
				if (attrNodeArr[i] in chartStyleDef.chart)
					delete chartStyleDef.chart[attrNodeArr[i]];
			}
		}
	}
	return chartStyleDef;
}
function prepareDataset (params) {
	var arrIndex = -1;
	var grpArrIndex = -1;
	var xRecs = 0;
	var yRecs = 0;
	var yTotalRecs = 0;
	var dataJsonPointer = null;
	var data = null;
	var valStr = "";
	var lData = new Array();
	var xDistArr = new Array();
	var xGroupDistArr = new Array();
	var datasets = new Array();
	var xLen = 0;
	var xGroupLen = 0;
	var xRecReq = true;
	var isYChildOfX = isChild(params.yAxisNode, params.xAxisNode);
	var isXchildofY = isChild(params.xAxisNode, params.yAxisNode);
	var xGroupingReqd = false;
	var lnodes = params.nodes;
	var noOfNodes = params.length;
	if (params.xGroupBy.length > 0) {
		////Grouping is Required...
		xGroupingReqd = true;
	} else {
		grpArrIndex = 0;
		datasets[grpArrIndex] = {};
		datasets[grpArrIndex].id = "DATASET";
		datasets[grpArrIndex].data = new Array();
		xGroupDistArr[grpArrIndex] = 0;
		lData = datasets[grpArrIndex].data;
		datasets[grpArrIndex].xDistArray = new Array();
	}
	var yNodes;
	try {
		if (params.yAxisNode.length > 1) {
			yNodes = params.yAxisNode.split(",");
			lmultiy = true;
			yElms = params.yid.split(",");
		}
	} catch (err) {
	}
	var yElms;
	var lmultiy = false;
	if (isYChildOfX) {
		//Y is a Child of X
		xRecs = apz.data.getNoOfRecs(params.xAxisNode);
		for (var x = 0; x < xRecs; x++) {
			if(apz.scrMetaData.nodesMap[lnodes[0]].relType == "1:1"){
				var mrParent = apz.scrMetaData.nodesMap[lnodes[0]].mrParent;
				x =  apz.scrMetaData.nodesMap[mrParent].currRec;
			}
			dataJsonPointer = apz.data.getDataPointer(params.xAxisNode, x);
			data = dataJsonPointer;
			valStr = getVal(data, params.xAxisElement, params.xDataType);
			xRecReq = recordRequired(data, params.xFilter);
			if (params.chartType == "DragNode") {
				lData[arrIndex].x = valStr;
			}
			if (xRecReq) {
				grpArrIndex = getXGroupIndex(xGroupingReqd, params, data, xGroupDistArr, datasets);
				lData = datasets[grpArrIndex].data;
				arrIndex = getDataArrayIndex(params, datasets, grpArrIndex, valStr);
				serCurrRec(params.xAxisNode, x);
				////Prepare Y Data
				prepareYData(params, lData[arrIndex]);
			}
		}
	} else if (isXchildofY) {
		// X is a child of Y -- This can only be  a case where the relation is
		// 1:1
		yTotalRecs = apz.data.getNoOfRecs(params.yAxisNode);
		for (var ty = 0; ty < yTotalRecs; ty++) {
			serCurrRec(params.yAxisNode, ty);
			xRecs = apz.data.getNoOfRecs(params.xAxisNode);
			for (var x = 0; x < xRecs; x++) {
				if(apz.scrMetaData.nodesMap[lnodes[0]].relType == "1:1"){
					var mrParent = apz.scrMetaData.nodesMap[lnodes[0]].mrParent;
					x =  apz.scrMetaData.nodesMap[mrParent].currRec;
				}
				dataJsonPointer = apz.data.getDataPointer(params.xAxisNode, x);
				data = dataJsonPointer;
				valStr = getVal(data, params.xAxisElement, params.xDataType);
				xRecReq = recordRequired(data, params.xFilter);
				if (params.chartType == "DragNode") {
					lData[arrIndex].x = valStr;
				}
				if (xRecReq) {
					grpArrIndex = getXGroupIndex(xGroupingReqd, params, data, xGroupDistArr, datasets);
					lData = datasets[grpArrIndex].data;
					arrIndex = getDataArrayIndex(params, datasets, grpArrIndex, valStr);
					serCurrRec(params.xAxisNode, x);
					////Prepare Y Data
					prepareYData(params, lData[arrIndex]);
				}
			}
		}
	} else {
		//No Relation..
		if (params.xAxisNode == params.yAxisNode) {
			//Same Node Case
			if ( typeof (params.chartType) != "undefined" && params.chartType.indexOf("Spark") != -1) {
				params.yAxisFunction = "";
				params.zAxisFunction = "";
			}
			if (params.chartType == "BoxAndWhisker2D") {
				params.yAxisFunction = "";
			}
			xRecs = apz.data.getNoOfRecs(params.xAxisNode);
			for (var x = 0; x < xRecs; x++) {
				if(apz.scrMetaData.nodesMap[lnodes[0]].relType == "1:1"){
					var mrParent = apz.scrMetaData.nodesMap[lnodes[0]].mrParent;
					x =  apz.scrMetaData.nodesMap[mrParent].currRec;
				}
				dataJsonPointer = apz.data.getDataPointer(params.xAxisNode, x);
				data = dataJsonPointer;
				valStr = getVal(data, params.xAxisElement, params.xDataType);
				xRecReq = recordRequired(data, params.xFilter);
				if (xRecReq) {
					grpArrIndex = getXGroupIndex(xGroupingReqd, params, data, xGroupDistArr, datasets);
					lData = datasets[grpArrIndex].data;
					arrIndex = getDataArrayIndex(params, datasets, grpArrIndex, valStr);
					if (params.chartType == "BoxAndWhisker2D") {
						//valStr = data[params.yAxisElement];//IDERES
						valStr = getVal(data, params.yAxisElement, "STRING");
					} else {
						valStr = getVal(data, params.yAxisElement, params.yDataType);
					}
					lData[arrIndex].noOfRecs = lData[arrIndex].noOfRecs + 1;
					if (params.yAxisFunction == "SUM") {
						//lData[arrIndex].val = (Number(lData[arrIndex].val) + Number(valStr)).toString();
					lData[arrIndex].val = (Number(lData[arrIndex].val) + Number(valStr)).toFixed(2).toString();
					} else if (params.yAxisFunction == "AVERAGE") {
						//(lData[arrIndex].val)?(lData[arrIndex].val = (lData[arrIndex].val + valStr)/2):lData[arrIndex].val=valStr;
						lData[arrIndex].val = lData[arrIndex].val + valStr;
						if (arrIndex > 0) {
							lData[arrIndex].val = lData[arrIndex].val / 2;   
						} 
					} else if (params.yAxisFunction == "MIN") {
						(lData[arrIndex].val)?((valStr<lData[arrIndex].val)?(lData[arrIndex].val=valStr):""):(lData[arrIndex].val=valStr);
							
					} else if (params.yAxisFunction == "MAX") {
						(lData[arrIndex].val)?((valStr>lData[arrIndex].val)?(lData[arrIndex].val=valStr):""):(lData[arrIndex].val=valStr);
					} else {
						lData[arrIndex].val = valStr;
					}
					if (params.chartType == "DragNode") {
						lData[arrIndex].x = getVal(data, params.xAxisElement, params.xDataType);
						lData[arrIndex].y = getVal(data, params.yAxisElement, params.yDataType);
					}
				}
			}
		} else {
			//No Relation and Different...
			xRecs = apz.data.getNoOfRecs(params.xAxisNode);
			for (var x = 0; x < xRecs; x++) {
				if(apz.scrMetaData.nodesMap[lnodes[0]].relType == "1:1"){
					var mrParent = apz.scrMetaData.nodesMap[lnodes[0]].mrParent;
					x =  apz.scrMetaData.nodesMap[mrParent].currRec;
				}
				dataJsonPointer = apz.data.getDataPointer(params.xAxisNode, x);
				valStr = getVal(dataJsonPointer, params.xAxisElement, params.xDataType);
				if (params.xFilter.length > 0) {
					xRecReq = true;
					xRecReq = recordRequired(data, params.xFilter);
				} else {
					xRecReq = true;
				}
				if (params.chartType == "DragNode") {
					lData[arrIndex].x = valStr;
				}
				if (xRecReq) {
					grpArrIndex = getXGroupIndex(xGroupingReqd, params, data, xGroupDistArr, datasets);
					lData = datasets[grpArrIndex].data;
					arrIndex = getDataArrayIndex(params, datasets, grpArrIndex, valStr);
					serCurrRec(params.xAxisNode, x);
					////Prepare Y Data
					prepareYData(params, lData[arrIndex]);
				}
			}
		}
	}
	return datasets;
}
function isChild (pchildnodename, pparentnodename) {
	var lchildnode = apz.scrMetaData.nodesMap[pchildnodename];
	if (!apz.isNull(lchildnode)) {
		var lparent = lchildnode.parent;
		while (lparent !== "") {
			if (lparent == pparentnodename)
				return true;
			else if (!apz.isNull(apz.scrMetaData.nodesMap[lparent]))
				lparent = "";
			else
				lparent = apz.scrMetaData.nodesMap[lparent].parent;
		}
	}
	return false;
}
function getVal (data, id, dType) {
	var val;
	if (data != null) {
		var elmName = id;
		var lind = id.lastIndexOf(apz.idSep);
		if (lind >= 0) {
			elmName = id.substr(lind + apz.idSep.length);
		}
		val = data[elmName];
		if (dType == "NUMBER") {
			val = convertAmt(val);
		}
	} else {
		val = 0;
	}
	return val;
}
function recordRequired (data, filter) {
	var required = true;
	if (filter.length > 0) {
		for (var f = 0; f < filter.length; f++) {
			if (filter[f].val != getVal(data, filter[f].id, "STRING")) {
				required = false;
				break;
			}
		}
	}
	return required;
}
function getXGroupIndex (xGroupIngReqd, params, data, grpDistArr, datasets) {
	var data;
	var xGroupLen = 0;
	var xGroupId = getGroupId(data, params.xGroupBy);
	var grpArrIndex = getArrayIndex(grpDistArr, xGroupId);
	if (xGroupIngReqd) {
		if (grpArrIndex == -1) {
			xGroupLen = datasets.length;
			grpArrIndex = xGroupLen;
			datasets[grpArrIndex] = {};
			datasets[grpArrIndex].id = xGroupId;
			datasets[grpArrIndex].data = new Array();
			datasets[grpArrIndex].xDistArray = new Array();
			//xGroupDistArr[lxgroupid] = arrIndex;
			addToDistArray(grpDistArr, xGroupId, grpArrIndex);
			data = datasets[grpArrIndex].data;
		} else {
			data = datasets[grpArrIndex].data;
		}
	} else {
		data = datasets[0].data;
		grpArrIndex = 0;
	}
	return grpArrIndex;
}
function getDataArrayIndex (params, datasets, grpArrIndex, value) {
      var arrIndex = -1;
      var create = true;
      if (params.xAxisFunction == "DISTINCT") {
         arrIndex = getArrayIndex(datasets[grpArrIndex].xDistArray, value);
         if (arrIndex >= 0) {
            create = false;
         }
      }
      if (create) {
         arrIndex = datasets[grpArrIndex].data.length;
         datasets[grpArrIndex].data[arrIndex] = {};
         datasets[grpArrIndex].data[arrIndex].type = value;
         datasets[grpArrIndex].data[arrIndex].val = 0;
         datasets[grpArrIndex].data[arrIndex].noOfRecs = 0;
         //xDistArr[valStr] = xLen;
         addToDistArray(datasets[grpArrIndex].xDistArray, value, arrIndex);
      }
      return arrIndex;
}
function prepareYData (params, data) {
      var yRecs = 0;
      var lData;
      var datapointer;
      var valStr;
      yRecs = apz.data.getNoOfRecs(params.yAxisNode);
      for (var y = 0; y < yRecs; y++) {
         dataJsonPointer = null;
         lData = null;
         dataJsonPointer = apz.data.getDataPointer(params.yAxisNode, y);
         valStr = getVal(dataJsonPointer, params.yid, params.ydtyp);
         data.noOfRecs = data.noOfRecs + 1;
         if (params.yaxisfunction == "SUM") {
            data.val = data.val + valStr;
         } else if (params.yaxisfunction == "AVERAGE") {
            data.val = data.val + valStr;
         } else if (params.yaxisfunction == "MIN") {
            if (valStr < data.y) {
               data.val = valStr;
            }
         } else if (params.yaxisfunction == "MAX") {
            if (valStr > lData[arrIndex].y) {
               data.val = valStr;
            }
         } else {
            data.val = valStr;
         }
      }
      if (params.yaxisfunction == "AVERAGE") {
         data.val = data.val / data.noOfRecs;
      }
      if (params.chartType == "DragNode") {
         data.y = data.val;
      }
}
function serCurrRec (pnode, pcurrrec) {
	apz.scrMetaData.nodesMap[pnode].currRec = pcurrrec;
	for (var node in apz.scrMetaData.nodesMap) {
		if ((node != null) && (node != "")) {
			if ((apz.scrMetaData.nodesMap[node].parent == pnode)) {
				serCurrRec(node, 0);
			}
		}
	}
}
function convertAmt (amt) {
	var re = /,/gi;
	if (!apz.isNull(amt)) {
		var str = amt.toString();
		tr = str.replace(re, "");
		return parseFloat(str);
	}
}
function getGroupId (data, group) {
	var groupId;
	var len = group.length;
	for (var f = 0; f < len; f++) {
		if (f == 0) {
			groupId = getVal(data, group[f].id, "STRING");
		} else {
			groupId = groupId + "|" + getVal(data, group[f].id, "STRING");
		}
	}
	return groupId;
}
function getArrayIndex (array, index) {
	var lindex = -1;
	if ((array[index] != null) && (array[index] != "undefined")) {
		lindex = array[index];
	}
	return lindex;
}
function addToDistArray (array, index, val) {
	array[index] = val;
}
function getArrayIndex (array, index) {
	var lindex = -1;
	if ((array[index] != null) && (array[index] != "undefined")) {
		lindex = array[index];
	}
	return lindex;
}
function isChild (pchildnodename, pparentnodename) {
  var lchildnode = this.apz.scrMetaData.nodesMap[pchildnodename];
  if (!this.apz.isNull(lchildnode)) {
	 var lparent = lchildnode.parent;
	 while (lparent !== "") {
		if (lparent == pparentnodename)
		   return true;
		else if (!this.apz.isNull(this.apz.scrMetaData.nodesMap[lparent]))
		   lparent = "";
		else
		   lparent = this.apz.scrMetaData.nodesMap[lparent].parent;
	 }
  }
  return false;
};
