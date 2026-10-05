Apz.Gauges = function(apz) {
   this.apz = apz;
   this.gaugeDetails = new Array();
   this.gaugeDetails['ANGULARGAUGE'] = {};
   this.gaugeDetails['ANGULARGAUGE'].dataModel = 'SG';
   this.gaugeDetails['BULB'] = {};
   this.gaugeDetails['BULB'].dataModel = 'SG';
   this.gaugeDetails['CYLINDER'] = {};
   this.gaugeDetails['CYLINDER'].dataModel = 'CG';
   this.gaugeDetails['HLED'] = {};
   this.gaugeDetails['HLED'].dataModel = 'SG';
   this.gaugeDetails['HLINEARGAUGE'] = {};
   this.gaugeDetails['HLINEARGAUGE'].dataModel = 'SG';
   this.gaugeDetails['THERMOMETER'] = {};
   this.gaugeDetails['THERMOMETER'].dataModel = 'CG';
   this.gaugeDetails['VLED'] = {};
   this.gaugeDetails['VLED'].dataModel = 'SG';
   this.gaugeDetails['HBULLET'] = {};
   this.gaugeDetails['HBULLET'].dataModel = 'MG';
   this.gaugeDetails['VBULLET'] = {};
   this.gaugeDetails['VBULLET'].dataModel = 'MG';
}
Apz.Gauges.prototype = {
   getGaugeData : function(gaugeObj) {
      var GaugedataModel = this.gaugeDetails[gaugeObj.gaugeType].dataModel;
      if (GaugedataModel == "SG") {
         this.prepareGaugeSG(gaugeObj);
      } else if (GaugedataModel == "MG") {
         this.prepareGaugeMG(gaugeObj);
      } else {
         this.prepareGaugeCG(gaugeObj);
      }
      try {
         this.manipulateGauge(gaugeObj);
      } catch (err) {
      }
   }, paintGauge : function(gaugeObj) {
      gaugeObj.gaugeData = {};
      gaugeObj.gaugeData.chart = {};
      if(!this.apz.isNull(this.apz.lits[this.apz.currAppId][gaugeObj.caption])){
    	  gaugeObj.gaugeData.chart.caption = this.apz.lits[this.apz.currAppId][gaugeObj.caption];
      }else{
    	  gaugeObj.gaugeData.chart.caption = this.apz.lits[this.apz.appId][gaugeObj.caption];
      }
      gaugeObj.gaugeData.chart.lowerLimit = gaugeObj.lowerLimit;
      gaugeObj.gaugeData.chart.upperLimit = gaugeObj.upperLimit;
      gaugeObj.gaugeData.chart.numberPrefix = gaugeObj.numberPrefix;
      gaugeObj.gaugeData.chart.numberSuffix = gaugeObj.numberSuffix;
      if (!this.apz.isNull(gaugeObj.height) && gaugeObj.height.indexOf("px") >= 0) {
         gaugeObj.height = gaugeObj.height.replace("px", "");
      } else {
         gaugeObj.height = gaugeObj.height;
      }
      if (this.apz.isNull(gaugeObj.height)) {
         gaugeObj.height = "300";
      }
      this.getGaugeData(gaugeObj);
      var myObj = this;
      requirejs([this.apz.getInfraPath() + "/fusioncharts/fusioncharts.js"], function() {
         myObj.renderGauge(gaugeObj);
      });
   }, renderGauge : function(gaugeObj) {
      var id = gaugeObj.id;
      var uiId = gaugeObj.uiId;
      var gauge;
      if(gaugeObj.widgetCategory == "CONTAINER"){
         uiId = id + "_gauge";
      } else {
         id = uiId + "_gauge";
      }
      if(gaugeObj.dispose && FusionCharts(id)){
         FusionCharts(id).dispose();
      }
      if (FusionCharts(id)) {
         gauge = FusionCharts(id);
      } else {
         gauge = new FusionCharts({
            swfUrl : gaugeObj.gaugeType, dataFormat : "json", renderer : "javascript", width : "100%", height : gaugeObj.height, id : id
         });
         gaugeObj.dispose = false;
      }
      if(this.apz.isFunction(this.apz.app['updateGaugeBeforeRender'])){
         this.apz.app.updateGaugeBeforeRender(gaugeObj.gaugeType, gaugeObj.gaugeData, gaugeObj.id, gauge);
      }
      gauge.setJSONData(gaugeObj.gaugeData);
      //Avoiding gauge paint if the element is not present in DOM
      if(!apz.isNull(document.getElementById(uiId))){
         gauge.render(uiId);
      }
   }, prepareGaugeSG : function(gaugeObj) {
      gaugeObj.gaugeData.chart.editMode = gaugeObj.editMode;
      this.prepareDataset(gaugeObj);
      if (gaugeObj.gaugeType == "HLINEARGAUGE") {
         gaugeObj.gaugeData.chart.pointerOnTop = "1";
      }
      gaugeObj.gaugeData.colorRange = {};
      gaugeObj.gaugeData.colorRange.color = new Array();
      for (var i = 0; i < gaugeObj.gaugeMaxRange.length; i++) {
         gaugeObj.gaugeData.colorRange.color[i] = {};
         gaugeObj.gaugeData.colorRange.color[i].minValue = gaugeObj.gaugeMinRange[i];
         gaugeObj.gaugeData.colorRange.color[i].maxValue = gaugeObj.gaugeMaxRange[i];
         gaugeObj.gaugeData.colorRange.color[i].code = gaugeObj.gaugeRangeColor[i];
      }
      if (gaugeObj.gaugeType == "ANGULARGAUGE") {
         gaugeObj.gaugeData.dials = {};
         gaugeObj.gaugeData.dials.dial = new Array();
         for (var i = 0; i < gaugeObj.valStr.length; i++) {
            gaugeObj.gaugeData.dials.dial[i] = {};
            gaugeObj.gaugeData.dials.dial[i].id = "Dial" + i;
            gaugeObj.gaugeData.dials.dial[i].value = gaugeObj.valStr[i];
            gaugeObj.gaugeData.dials.dial[i].tooltext = gaugeObj.gaugetooltext[i];         
            }
      } else if (gaugeObj.gaugeType == "HLINEARGAUGE") {
         gaugeObj.gaugeData.pointers = {};
         gaugeObj.gaugeData.pointers.pointer = new Array();
         for ( j = 0; j < gaugeObj.valStr.length; j++) {
            gaugeObj.gaugeData.pointers.pointer[j] = {};
            gaugeObj.gaugeData.pointers.pointer[j].id = "Pointer" + j;
            gaugeObj.gaugeData.pointers.pointer[j].value = gaugeObj.valStr[j];
            gaugeObj.gaugeData.pointers.pointer[j].tooltext = gaugeObj.gaugetooltext[j];
         }
      } else {
         for (var k = 0; k < gaugeObj.valStr.length; k++) {
            gaugeObj.gaugeData.value = gaugeObj.valStr[k];
         }
      }
   }, prepareGaugeMG : function(gaugeObj) {
      this.prepareDataset(gaugeObj);
      gaugeObj.gaugeData.colorRange = {};
      gaugeObj.gaugeData.colorRange.color = new Array();
      for (var i = 0; i < gaugeObj.gaugeMaxRange.length; i++) {
         gaugeObj.gaugeData.colorRange.color[i] = {};
         gaugeObj.gaugeData.colorRange.color[i].minvalue = gaugeObj.gaugeMinRange[i];
         gaugeObj.gaugeData.colorRange.color[i].maxvalue = gaugeObj.gaugeMaxRange[i];
         gaugeObj.gaugeData.colorRange.color[i].code = gaugeObj.gaugeRangeColor[i];
      }
      for (var k = 0; k < gaugeObj.valStr.length; k++) {
         gaugeObj.gaugeData.value = gaugeObj.valStr[k];
      }
      for (var m = 0; m < gaugeObj.trgStr.length; m++) {
         gaugeObj.gaugeData.target = gaugeObj.trgStr[m];
      }
   }, prepareGaugeCG : function(gaugeObj) {
      this.prepareDataset(gaugeObj);
      for (var k = 0; k < gaugeObj.valStr.length; k++) {
         gaugeObj.gaugeData.value = gaugeObj.valStr[k];
      }
   }, prepareDataset : function(gaugeObj) {
      var targetRecs = "";
      var trgStr = "";
      var valStr = "";
      var tooltextStr = "";
      gaugeObj.tooltextStr = [];
      gaugeObj.valStr = [];
      gaugeObj.trgStr = [];
      for (var k = 0; k < gaugeObj.gaugeValueNode.length; k++) {
         i = 0;
         //Fix for updating child guage when the parent row details changes
         if(this.apz.scrMetaData.nodesMap[gaugeObj.gaugeValueNode[k]].relType == "1:1"){
			 var mrParent = this.apz.scrMetaData.nodesMap[gaugeObj.gaugeValueNode[k]].mrParent;
			 i =  this.apz.scrMetaData.nodesMap[mrParent].currRec;
		   }
         // lvalrecs = appzillon.data.getNoOfRecs(gaugeObj.gaugeValueNode[k]);
         // gaugeValueNode has Nodes without appId_ .Temporary.To be Visited again.
         if(gaugeObj.widgetCategory !== "ELEMENT"){
               dataJsonPointer = this.apz.data.getDataPointer(gaugeObj.gaugeValueNode[k], i);
               valStr = this.getVal(dataJsonPointer, gaugeObj.gaugeValueElement[k], gaugeObj.gaugeValueType[k]);
         }else{
            valStr = gaugeObj.value;
         }
         gaugeObj.valStr[k] = valStr;
      }
      if (this.gaugeDetails[gaugeObj.gaugeType].dataModel == "MG") {
         var targetRecs = this.apz.data.getNoOfRecs(gaugeObj.gaugeTargetNode);
         for (var j = 0; j < targetRecs; j++) {
            datajsonpointer = this.apz.data.getDataPointer(gaugeObj.gaugeTargetNode, j);
            trgStr = this.getVal(datajsonpointer, gaugeObj.gaugeTargetElement[0], gaugeObj.gaugeTargetType);
            gaugeObj.trgStr[j] = trgStr;
         }
      }
   }, getVal : function(data, id, dType) {
      var val = 0;
      if (!this.apz.isNull(data)) {
         var elmName = id;
         var ind = id.lastIndexOf(this.apz.idSep);
         if (ind >= 0) {
            elmName = id.substr(ind + this.apz.idSep.length);
         }
         val = data[elmName];
         if (dType == "NUMBER") {
            val = this.convertAmt(val);
         }
      }
      return val;
   }, convertAmt : function(amt) {
      var re = /,/gi;
      if (!this.apz.isNull(amt)) {
         var str = amt.toString();
         str = str.replace(re, "");
         return parseFloat(str);
      }
   }
}
