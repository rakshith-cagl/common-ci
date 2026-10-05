Apz.Charts = function(apz) {
   this.apz = apz;
   this.renderingCount = 0;
   this.chartDetails = [];
   this.backupRecCounters = [];
   var chartDatamodelSS = new Array("Area2D", "Bar2D", "Column2D", "Column3D", "Doughnut2D", "Doughnut3D", "FCExporter", "Line", "Pareto2D", "Pareto3D", "Pie2D", "Pie3D", "SSGrid", "Funnel", "Pyramid", "Spline", "SplineArea", "Waterfall2D", "Kagi");
   for (var i = 0; i < chartDatamodelSS.length; i++) {
      this.chartDetails[chartDatamodelSS[i]] = {}
      this.chartDetails[chartDatamodelSS[i]].dataModel = "SS";
   }
   var chartDatamodelMS = new Array("Marimekko", "MSArea", "MSBar2D", "MSBar3D", "MSColumn2D", "MSColumn3D", "MSColumn3DLineDY", "MSColumnLine3D", "MSCombi2D", "MSCombi3D", "MSCombiDY2D", "MSLine", "MSStackedColumn2DLineDY", "ScrollArea2D", "ScrollColumn2D", "ScrollCombi2D", "ScrollCombiDY2D", "ScrollLine2D", "ScrollStackedColumn2D", "StackedArea2D", "StackedBar2D", "StackedBar3D", "StackedColumn2D", "StackedColumn2DLine", "StackedColumn3D", "StackedColumn3DLine", "StackedColumn3DLineDY", "ZoomLine", "LogMSColumn2D", "LogMSLine", "MSSpline", "MSSplineArea", "InverseMSArea", "InverseMSColumn2D", "InverseMSLine", "Radar", "BoxAndWhisker2D", "MSStepLine", "ErrorBar2D", "ErrorLine");
   for (var i = 0; i < chartDatamodelMS.length; i++) {
      this.chartDetails[chartDatamodelMS[i]] = {};
      this.chartDetails[chartDatamodelMS[i]].dataModel = 'MS';
   }
   var chartDatamodelSP = new Array("SparkLine", "SparkColumn", "SparkWinLoss");
   for (var i = 0; i < chartDatamodelSP.length; i++) {
      this.chartDetails[chartDatamodelSP[i]] = {};
      this.chartDetails[chartDatamodelSP[i]].dataModel = 'SP';
   }
   var chartDatamodelSB = new Array("ErrorScatter", "SelectScatter", "Scatter");
   for (var i = 0; i < chartDatamodelSB.length; i++) {
      this.chartDetails[chartDatamodelSB[i]] = {};
      this.chartDetails[chartDatamodelSB[i]].dataModel = 'SB';
   }
   var chartDatamodelDG = new Array("DragColumn2D", "DragLine", "DragArea","DragNode");
   for (var i = 0; i < chartDatamodelDG.length; i++) {
      this.chartDetails[chartDatamodelDG[i]] = {};
      this.chartDetails[chartDatamodelDG[i]].dataModel = 'DG';
   }
   this.chartDetails['Bubble'] = {};
   this.chartDetails['Bubble'].dataModel = 'BS';
   this.chartDetails['MSStackedColumn2D'] = {};
   this.chartDetails['MSStackedColumn2D'].dataModel = 'SC';
   this.chartDetails['MultiAxisLine'] = {};
   this.chartDetails['MultiAxisLine'].dataModel = 'MA';
   this.chartDetails['MultiLevelPie'] = {};
   this.chartDetails['MultiLevelPie'].dataModel = 'MP';
   this.chartDetails['HeatMap'] = {};
   this.chartDetails['HeatMap'].dataModel = 'HM';
};
Apz.Charts.prototype = {
   groupFunction : function(pArray, fnname) {
      var lArray = pArray;
      var arrLen = lArray.length;
      var presult = 0;
      switch (true) {
         case (fnname == 'sum'):
            var lsum = 0;
            for (var i = 0; i < arrLen; i++) {
               lsum = lsum + parseInt(pArray[i]);
            }
            presult = lsum;
            break;
         case (fnname == "min"):
            var lmin = pArray[0];
            for (var i = 0; i < arrLen; i++) {
               if (parseInt(pArray[i]) < lmin)
                  lmin = parseInt(pArray[i]);
            }
            presult = lmin;
            break;
         case (fnname == "max"):
            var lmax = pArray[0];
            for (var i = 0; i < arrLen; i++) {
               if (parseInt(pArray[i]) > lmax)
                  lmax = parseInt(pArray[i]);
            }
            presult = lmax;
            break;
         case (fnname == "avg"):
            var lsum = 0;
            var lavg = 0;
            for (var i = 0; i < arrLen; i++) {
               lsum = lsum + parseInt(pArray[i]);
            }
            if (lsum == 0)
               ( lavg = lsum);
            else
               lavg = lsum / arrLen;
            presult = lavg;
            break;
         default:
            alert('not a valid function');
            break;
      }
      return presult;
   }, distinct : function(pArray) {
      var arr = [];
      for (var i = 0; i < pArray.length; i++) {
         if (apzJquery.inArray(pArray[i], arr) == -1)
            arr.push(pArray[i]);
      }
      return arr;
   }, isChild : function(pchildnodename, pparentnodename) {
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
   }, backupRecCounters : function() {
      for (var node in this.apz.scrMetaData.nodesMap) {
         if ((node != null) && (node != "")) {
            this.backupRecCounters[node] = this.apz.scrMetaData.nodesMap[node].currRec;
         }
      }
   }, arestoreRecCounters : function() {
      for (var node in appzillon.data.scrmetadata.nodesmap) {
         if ((node != null) && (node != "")) {
            this.apz.scrMetaData.nodesMap[node].currRec = this.backupRecCounters[node];
         }
      }
   }, serCurrRec : function(pnode, pcurrrec) {
      this.apz.scrMetaData.nodesMap[pnode].currRec = pcurrrec;
      for (var node in this.apz.scrMetaData.nodesMap) {
         if ((node != null) && (node != "")) {
            if ((this.apz.scrMetaData.nodesMap[node].parent == pnode)) {
               this.serCurrRec(node, 0);
            }
         }
      }
   }, addVisualPrefs : function(chartObj) {
      var chartStyleDef = "";
      if (!this.apz.isNull(chartObj.chartStyleSheetPath)) {
         var chartStyle = apz.getFile(chartObj.chartStyleSheetPath)
         if(!this.apz.isNull(chartStyle)){
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
            chartObj.chartData.chart = chartStyleDef.chart;
         }
         if (chartStyleDef.styles) {
            chartObj.chartData.styles = chartStyleDef.styles;
         }
      }
   }, getChartData : function(chartObj) {
      chartObj.chartData = {};
      chartObj.chartData.chart = {};
      this.addVisualPrefs(chartObj);
      chartObj.chartData.chart.caption = this.apz.getLabel(chartObj.caption);
      chartObj.chartData.chart.subCaption = this.apz.getLabel(chartObj.subCaption);
      chartObj.chartData.chart.xAxisName = this.apz.getLabel(chartObj.chartXTitle);
      chartObj.chartData.chart.yAxisName = this.apz.getLabel(chartObj.chartYTitle);
      var dataModel = this.chartDetails[chartObj.chartType].dataModel;
      if (dataModel == "SS") {
         this.prepareChartSS(chartObj);
      } else if (dataModel == "MS") {
         this.prepareChartMS(chartObj);
      } else if (dataModel == "SP") {
         this.prepareChartSP(chartObj);
      } else if (dataModel == "SB") {
         this.prepareChartSB(chartObj);
      } else if (dataModel == "DG") {
         this.prepareChartDG(chartObj);
      } else if (dataModel == "BS") {
         this.prepareChartBS(chartObj);
      } else if (dataModel == "SC") {
         this.prepareChartSC(chartObj);
      } else if (dataModel == "MA") {
         this.prepareChartMA(chartObj);
      } else if (dataModel == "MP") {
         this.prepareChartMP(chartObj);
      } else if (dataModel == "HM") {
         this.prepareChartHM(chartObj);
      }
      try {
         this.manipulateChart();
      } catch (err) {
      }
   }, paintChart : function(chartObj) {
      var myObj = this;
      requirejs([this.apz.getInfraPath() + "/fusioncharts/fusioncharts.js"], function() {
         myObj.prepareChart(chartObj);
      });
   }, prepareChart : function(chartObj) {
      if (this.apz.getDataType(chartObj.yAxisElement) == "Array") {
         if (chartObj.yAxisElement.length == 1) {
            chartObj.yAxisElement = chartObj.yAxisElement[0];
            chartObj.yAxisNode = chartObj.yAxisNode[0];
            chartObj.yDataType = chartObj.yDataType[0];
         }
      }
      if (this.apz.getDataType(chartObj.zAxisElement) == "Array") {
         if (chartObj.zAxisElement.length == 1) {
            chartObj.zAxisElement = chartObj.zAxisElement[0];
            chartObj.zAxisNode = chartObj.zAxisNode[0];
            chartObj.zDataType = chartObj.zDataType[0];
         }
      }
      this.renderingCount = this.renderingCount + 1;
      var currTheme = this.apz.theme;
      if (this.apz.isNull(chartObj.height)) {
         chartObj.height = "300";
      }
      var chartHeight = chartObj.height;
      if (chartObj.height.indexOf("px") >= 0) {
         chartObj.height = chartObj.height.replace("px", "");
      }
      if (!this.apz.isNull(chartObj.chartStyleSheet)) {
         if(!this.apz.isNull(this.apz.chartStyles) && this.apz.chartStyles.indexOf(chartObj.chartStyleSheet) > -1){
            chartObj.chartStyleSheetPath = this.apz.getStylesPath() + "/" + currTheme + "/csd/" + chartObj.chartStyleSheet + '.json';
         } else {
            //// TBC - How to get base theme name from app? Hardcoded appzillon for now in the path
            chartObj.chartStyleSheetPath = this.apz.getInfraStylesPath() + "/themes/"+this.apz.baseThemesMap[currTheme]+"/csd/" + chartObj.chartStyleSheet + '.json';
         }
      }
      this.getChartData(chartObj);
      this.renderChart(chartObj);
   }, renderChart : function(chartObj) {
      if (chartObj.dispose && FusionCharts(chartObj.id)) {
         try {
            FusionCharts(chartObj.id).dispose();
         } catch (e) {
            console.log(e.stack);
         }
      }
      var chart = "";
      //extend Constructor properties
      var constructorProperties = {};
      if(this.apz.isFunction(this.apz.app['extendChartConstructor'])){
         this.apz.app.extendChartConstructor(chartObj.chartType, chartObj.chartData, chartObj.id, constructorProperties);
      }
      
      if(FusionCharts(chartObj.id)){
         chart = FusionCharts(chartObj.id);
      } else {
    	  var fcObj = {
    	            swfUrl : chartObj.chartType, dataFormat : "json", renderer : "javascript", width : "100%", height : chartObj.height, id : chartObj.id
          };
    	  for(var key in constructorProperties){
    		  if(constructorProperties.hasOwnProperty(key)){
    			  fcObj[key] = constructorProperties[key];
    		  }
    	  }
         chart = new FusionCharts(fcObj);
         chartObj.dispose = false;
      }   
      //User Defined
      if(this.apz.isFunction(this.apz.app['updateChartBeforeRender'])){
         this.apz.app.updateChartBeforeRender(chartObj.chartType, chartObj.chartData, chartObj.id, chart);
      }
      chart.setJSONData(chartObj.chartData);
      chart.render(chartObj.id + "_chart");
   }, render : function(Data, ChartType, Id, Width, Height, p1, p2, Div) {
      this.renderingCount = this.renderingCount + 1;
      if (FusionCharts(Id)) {
         try {
            FusionCharts(Id).dispose();
         } catch (e) {
         }
      }
      var myChart = new FusionCharts(ChartType, Id, Width, Height, p1, p2);
      myChart.setJSONData(Data);
      try {
         myChart.render(Div);
      } catch (e) {
      }
   }, prepareChartSS : function(chartObj) {
      chartObj.xFilter = new Array();
      chartObj.yFilter = new Array();
      chartObj.xGroupBy = new Array();
      var data = new Array();
      var xArr = new Array();
      data = this.prepareDataset(chartObj);
      xArr = data[0].data;
      chartObj.chartData.data = new Array();
      var xLen = xArr.length;
      if (xLen > 0) {
         for (var i = 0; i < xLen; i++) {
            chartObj.chartData.data[i] = {};
            chartObj.chartData.data[i].label = xArr[i].type;
            chartObj.chartData.data[i].value = xArr[i].val;
            if (!this.apz.isNull(chartObj.jsHook)) {
               chartObj.chartData.data[i].link = "j-" + chartObj.jsHook + "-" + xArr[i].type + "," + xArr[i].val;
            }
         }
      }
      return xArr;
   }, prepareChartSP : function(chartObj) {
      chartObj.xFilter = new Array();
      chartObj.yFilter = new Array();
      chartObj.xGroupBy = new Array();
      var data = new Array();
      var xArr = new Array();
      data = this.prepareDataset(chartObj);
      xArr = data[0].data;
      chartObj.chartData.dataSet = new Array();
      chartObj.chartData.dataSet[0] = {};
      chartObj.chartData.dataSet[0].data = new Array();
      var xLen = xArr.length;
      if (xLen > 0) {
         for (var i = 0; i < xLen; i++) {
            chartObj.chartData.dataSet[0].data[i] = {};
            chartObj.chartData.dataSet[0].data[i].value = xArr[i].val;
         }
      }
      return xArr;
   }, buildMPChartData : function(jsonObj, parentNode) {
      var childNode = new Array();
      childNode = this.apz.scrMetaData.nodesMap[parentNode].childs;
      if (childNode.length > 0) {
         jsonObj.category = new Array();
         for (var i = 0; i < childNode.length; i++) {
            if (childNode[i] != "") {
               jsonObj.category[i] = {};
               jsonObj.category[i].label = childNode[i];
               this.buildMPChartData(jsonObj.category[i], childNode[i]);
            }
         }
      }
   }, prepareChartMP : function(chartObj) {
      chartObj.chartData.category = new Array();
      chartObj.chartData.category[0] = {};
      chartObj.chartData.category[0].label = chartObj.parentNode;
      this.buildMPChartData(chartObj.chartData.category[0], chartObj.parentNode);
   }, prepareChartMA : function(chartObj) {
      var zRecs = 0;
      var dataJsonPointer = null;
      var valStr = "";
      var zArr = new Array();
      var xArr = new Array();
      var categDistArr = new Array();
      var categ;
      var seriesDistArr = new Array();
      var series;
      var categIndex = -1;
      var zDistArr = new Array();
      var xDistArr = new Array();
      var arrIndex = -1;
      var lData = new Array();
      var xLen = 0;
      var zLen = 0;
      var dataLen = 0;
      var isXChildOfZ = this.isChild(chartObj.xAxisNode, chartObj.zAxisNode);
      var isYChildOfZ = this.isChild(chartObj.yAxisNode, chartObj.zAxisNode);
      var isYChildOfX = this.isChild(chartObj.yAxisNode, chartObj.xAxisNode);
      var isZChildOfX = this.isChild(chartObj.zAxisNode, chartObj.xAxisNode);
      //////Categories
      chartObj.chartData.categories = new Array();
      chartObj.chartData.categories[0] = {};
      chartObj.chartData.categories[0].category = new Array();
      ////Cases
      if (isXChildOfZ) {
         ////X is a Child of Z
         zRecs = this.apz.data.getNoOfRecs(chartObj.zAxisNode);
         for (var z = 0; z < zRecs; z++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.zAxisNode, z);
            valStr = this.getVal(dataJsonPointer, chartObj.zAxisElement, chartObj.zDataType);
            if (chartObj.zAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(zDistArr, valStr);
               if (arrIndex == -1) {
                  zLen = zArr.length;
                  arrIndex = zLen;
                  zArr[arrIndex] = {};
                  zArr[arrIndex].z = valStr;
                  zArr[arrIndex].xArr = new Array();
                  this.addToDistArray(zDistArr, valStr, arrIndex);
               }
            } else {
               zLen = zArr.length;
               arrIndex = zLen;
               zArr[arrIndex] = {};
               zArr[arrIndex].z = valStr;
               zArr[arrIndex].xArr = new Array();
               this.addToDistArray(zDistArr, valStr, arrIndex);
            }
            //SEt Z Current Record..
            this.serCurrRec(chartObj.zAxisNode, z);
            chartObj.xFilter = new Array();
            chartObj.yFilter = new Array();
            chartObj.xGroupBy = new Array();
            //Get X Records
            data = this.prepareDataset(chartObj);
            zArr[arrIndex].xArr = data[0].data;
         }
         zLen = zArr.length;
         if (zLen > 0) {
            chartObj.chartData.axis = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.axis[z] = {};
               chartObj.chartData.axis[z].title = zArr[z].z;
               chartObj.chartData.axis[z].dataSet = new Array();
               chartObj.chartData.axis[z].dataSet[0] = {};
               chartObj.chartData.axis[z].dataSet[0].seriesName = zArr[z].z;
               chartObj.chartData.axis[z].dataSet[0].data = new Array();
               xLen = zArr[z].xArr.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     categ = zArr[z].xArr[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        this.addToDistArray(categDistArr, categ, categIndex);
                     }
                     chartObj.chartData.axis[z].dataSet[0].data[categIndex] = {};
                     chartObj.chartData.axis[z].dataSet[0].data[categIndex].value = zArr[z].xArr[x].val;
                     chartObj.chartData.axis[z].dataSet[0].data[categIndex].link = "j-" + chartObj.jsHook + "-" + chartObj.chartData.axis[z].title + "|" + zArr[z].xArr[x].type + "," + zArr[z].xArr[x].val;
                  }
               }
            }
         }
      } else if (isZChildOfX) {
         ////Z is a Child of X
         xRecs = this.apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.xAxisNode, x);
            valStr = this.getVal(dataJsonPointer, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].ZArr = new Array();
                  this.addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               xArr[xLen] = {};
               xArr[xLen].x = valStr;
               xArr[xLen].ZArr = new Array();
               this.addToDistArray(xDistArr, valStr, xLen);
            }
            //Set X Current Record..
            this.serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            chartObj.xAxisNode = chartObj.zAxisNode;
            chartObj.xAxisElement = chartObj.zAxisElement;
            chartObj.xDataType = chartObj.zDataType;
            chartObj.xAxisFunction = chartObj.zAxisFunction;
            chartObj.xFilter = new Array();
            chartObj.yFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.zGroupBy = new Array();
            data = this.prepareDataset(chartObj);
            xArr[xLen].ZArr = data[0].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            chartObj.chartData.axis = new Array();
            for (var x = 0; x < xLen; x++) {
               categ = xArr[x].x;
               categIndex = this.getArrayIndex(categDistArr, categ);
               if (categIndex == -1) {
                  categIndex = chartObj.chartData.categories[0].category.length;
                  chartObj.chartData.categories[0].category[categIndex] = {};
                  chartObj.chartData.categories[0].category[categIndex].label = categ;
                  categDistArr[categ] = categIndex;
               }
               zLen = xArr[x].ZArr.length;
               if (zLen > 0) {
                  for (var z = 0; z < zLen; z++) {
                     series = xArr[x].zArr[z].type;
                     seriesIndex = this.getArrayIndex(seriesDistArr, series);
                     if (seriesIndex == -1) {
                        seriesIndex = chartObj.chartData.axis.length;
                        chartObj.chartData.axis[seriesIndex] = {};
                        chartObj.chartData.axis[seriesIndex].title = series;
                        chartObj.chartData.axis[seriesIndex].dataSet = new Array();
                        chartObj.chartData.axis[seriesIndex].dataSet[0] = {};
                        chartObj.chartData.axis[seriesIndex].dataSet[0].seriesName = series;
                        chartObj.chartData.axis[seriesIndex].dataSet[0].data = new Array();
                        this.addToDistArray(seriesDistArr, series, seriesIndex);
                     }
                     dataLen = chartObj.chartData.axis[seriesIndex].dataSet[0].data.length;
                     chartObj.chartData.axis[seriesIndex].dataSet[0].data[categIndex] = {};
                     chartObj.chartData.axis[seriesIndex].dataSet[0].data[categIndex].value = xArr[x].zArr[z].val;
                     chartObj.chartData.axis[seriesIndex].dataSet[0].data[categIndex].link = "j-" + chartObj.jsHook + "-" + series + "|" + categ + "," + xArr[x].zArr[z].val;
                  }
               }
            }
         }
      } else {
         chartObj.xFilter = new Array();
         chartObj.yFilter = new Array();
         chartObj.xGroupBy = new Array();
         chartObj.xGroupBy[0] = {};
         chartObj.xGroupBy[0].id = chartObj.zAxisElement;
         chartObj.xGroupBy[0].dtyp = "STRING";
         chartObj.xGroupBy[0].val = null;
         data = this.prepareDataset(chartObj);
         zLen = data.length;
         if (zLen > 0) {
            chartObj.chartData.axis = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.axis[z] = {};
               chartObj.chartData.axis[z].title = data[z].id;
               chartObj.chartData.axis[z].dataSet = new Array();
               chartObj.chartData.axis[z].dataSet[0] = {};
               chartObj.chartData.axis[z].dataSet[0].seriesName = data[z].id;
               chartObj.chartData.axis[z].dataSet[0].data = new Array();
               xLen = data[z].data.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     categ = data[z].data[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        categDistArr[categ] = categIndex;
                     }
                     chartObj.chartData.axis[z].dataSet[0].data[categIndex] = {};
                     chartObj.chartData.axis[z].dataSet[0].data[categIndex].value = data[z].data[x].val;
                     chartObj.chartData.axis[z].dataSet[0].data[categIndex].link = "j-" + chartObj.jsHook + "-" + data[z].id + "|" + categ + "," + data[z].data[x].val;
                  }
               }
            }
         }
      }
      return zArr;
   }, prepareChartMS : function(chartObj) {
      var zRecs = 0;
      var dataJsonPointer = null;
      var data = null;
      var valStr = "";
      var zArr = new Array();
      var xArr = new Array();
      var categDistArr = new Array();
      var categ;
      var seriesDistArr = new Array();
      var series;
      var categIndex = -1;
      var zDistArr = new Array();
      var xDistArr = new Array();
      var arrIndex = -1;
      var data = new Array();
      var xLen = 0;
      var zLen = 0;
      var dataLen = 0;
      var xRecs = 0;
      var isXChildOfZ = this.isChild(chartObj.xAxisNode, chartObj.zAxisNode);
      var isYChildOfZ = this.isChild(chartObj.yAxisNode, chartObj.zAxisNode);
      var isYChildOfX = this.isChild(chartObj.yAxisNode, chartObj.xAxisNode);
      var isZChildOfX = this.isChild(chartObj.zAxisNode, chartObj.xAxisNode);
      ////Basic Chart Data
      if (chartObj.chartType == "MSColumn3DLineDY" || chartObj.chartType == "MSCombiDY2D") {
         chartObj.chartData.chart.PYAxisName = chartObj.chartYTitle;
      } else {
         chartObj.chartData.chart.yaxisname = chartObj.chartYTitle;
      }
      //////Categories
      chartObj.chartData.categories = new Array();
      chartObj.chartData.categories[0] = {};
      chartObj.chartData.categories[0].category = new Array();
      ////Cases
      if (isXChildOfZ) {
         ////X is a Child of Z
         zRecs = this.apz.data.getNoOfRecs(chartObj.zAxisNode);
         for (var z = 0; z < zRecs; z++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.zAxisNode, z);
            valStr = this.getVal(dataJsonPointer, chartObj.zAxisElement, chartObj.zDataType);
            if (chartObj.zAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(zDistArr, valStr);
               if (arrIndex == -1) {
                  zLen = zArr.length;
                  arrIndex = zLen;
                  zArr[arrIndex] = {};
                  zArr[arrIndex].z = valStr;
                  zArr[arrIndex].xArr = new Array();
                  this.addToDistArray(zDistArr, valStr, arrIndex);
               }
            } else {
               zLen = zArr.length;
               arrIndex = zLen;
               zArr[arrIndex] = {};
               zArr[arrIndex].z = valStr;
               zArr[arrIndex].xArr = new Array();
               this.addToDistArray(zDistArr, valStr, arrIndex);
            }
            //SEt Z Current Record..
            this.serCurrRec(chartObj.zAxisNode, z);
            //Get X Records
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            data = this.prepareDataset(chartObj);
            zArr[arrIndex].xArr = data[0].data;
         }
         zLen = zArr.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = zArr[z].z;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = zArr[z].xArr.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     categ = zArr[z].xArr[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        //categDistArr[categ] = categIndex;
                        this.addToDistArray(categDistArr, categ, categIndex);
                     }
                     chartObj.chartData.dataSet[z].data[categIndex] = {};
                     chartObj.chartData.dataSet[z].data[categIndex].value = zArr[z].xArr[x].val;
                     chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + chartObj.chartData.dataSet[z].seriesName + "|" + zArr[z].xArr[x].type + "," + zArr[z].xArr[x].val;
                  }
               }
            }
         }
      } else if (isZChildOfX) {
         ////Z is a Child of X
         xRecs = this.apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.xAxisNode, x);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].zarr = new Array();
                  this.addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               arrIndex = xLen;
               xArr[arrIndex] = {};
               xArr[arrIndex].x = valStr;
               xArr[arrIndex].zarr = new Array();
               this.addToDistArray(xDistArr, valStr, arrIndex);
            }
            //Set X Current Record..
            this.serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            chartObj.xAxisNode = chartObj.zAxisNode;
            chartObj.xAxisElement = chartObj.zAxisElement;
            chartObj.xDataType = chartObj.zDataType;
            chartObj.xAxisFunction = chartObj.zAxisFunction;
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            chartObj.zGroupBy = new Array();
            data = this.prepareDataset(chartObj);
            xArr[arrIndex].zarr = data[0].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var x = 0; x < xLen; x++) {
               categ = xArr[x].x;
               categIndex = this.getArrayIndex(categDistArr, categ);
               if (categIndex == -1) {
                  categIndex = chartObj.chartData.categories[0].category.length;
                  chartObj.chartData.categories[0].category[categIndex] = {};
                  chartObj.chartData.categories[0].category[categIndex].label = categ;
                  categDistArr[categ] = categIndex;
               }
               zLen = xArr[x].zarr.length;
               if (zLen > 0) {
                  for (var z = 0; z < zLen; z++) {
                     series = xArr[x].zarr[z].type;
                     seriesIndex = this.getArrayIndex(seriesDistArr, series);
                     if (seriesIndex == -1) {
                        seriesIndex = chartObj.chartData.dataSet.length;
                        chartObj.chartData.dataSet[seriesIndex] = {};
                        chartObj.chartData.dataSet[seriesIndex].seriesName = series;
                        chartObj.chartData.dataSet[seriesIndex].data = new Array();
                        this.addToDistArray(seriesDistArr, series, seriesIndex);
                     }
                     dataLen = chartObj.chartData.dataSet[seriesIndex].data.length;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex] = {};
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].value = xArr[x].zarr[z].val;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].link = "j-" + chartObj.jsHook + "-" + series + "|" + categ + "," + xArr[x].zarr[z].val;
                  }
               }
            }
         }
      } else {
         chartObj.xFilter = new Array();
         chartObj.yFilter = new Array();
         chartObj.xGroupBy = new Array();
         chartObj.xGroupBy[0] = {};
         chartObj.xGroupBy[0].id = chartObj.zAxisElement;
         chartObj.xGroupBy[0].dtyp = "STRING";
         chartObj.xGroupBy[0].val = null;
         data = this.prepareDataset(chartObj);
         zLen = data.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = data[z].id;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = data[z].data.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     categ = data[z].data[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        categDistArr[categ] = categIndex;
                     }
                     chartObj.chartData.dataSet[z].data[categIndex] = {};
                     chartObj.chartData.dataSet[z].data[categIndex].value = data[z].data[x].val;
                     chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + data[z].id + "|" + categ + "," + data[z].data[x].val;
                  }
               }
            }
         }
      }
      if (chartObj.chartType == "MSStackedColumn2DLineDY") {
            this.convertToDualY(chartObj);
         
      }
      return zArr;
   }, prepareChartHM : function(chartObj) {
      var zRecs = 0;
      var dataJsonPointer = null;
      var data = null;
      var valStr = "";
      var zArr = new Array();
      var xArr = new Array();
      var categDistArr = new Array();
      var categ;
      var seriesDistArr = new Array();
      var series;
      var categIndex = -1;
      var zDistArr = new Array();
      var xDistArr = new Array();
      var arrIndex = -1;
      var data = new Array();
      var xLen = 0;
      var zLen = 0;
      var dataLen = 0;
      var isXChildOfZ = this.isChild(chartObj.xAxisNode, chartObj.zAxisNode);
      var isYChildOfZ = this.isChild(chartObj.yAxisNode, chartObj.zAxisNode);
      var isYChildOfX = this.isChild(chartObj.yAxisNode, chartObj.xAxisNode);
      var isZChildOfX = this.isChild(chartObj.zAxisNode, chartObj.xAxisNode);
      //////rows
      chartObj.chartData.rows = {};
      chartObj.chartData.rows.row = new Array();
      //////columns
      chartObj.chartData.columns = {};
      chartObj.chartData.columns.column = new Array();
      chartObj.chartData.dataSet = new Array();
      chartObj.chartData.dataSet[0] = {};
      chartObj.chartData.dataSet[0].data = new Array();
      ////colorRange
      chartObj.chartData.colorRange = {};
      ////Cases
      if (isXChildOfZ) {
         ////X is a Child of Z
         zRecs = this.apz.data.getNoOfRecs(chartObj.zAxisNode);
         for (var z = 0; z < zRecs; z++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.zAxisNode, z);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.zAxisElement, chartObj.zDataType);
            if (chartObj.zAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(zDistArr, valStr);
               if (arrIndex == -1) {
                  zLen = zArr.length;
                  arrIndex = zLen;
                  zArr[arrIndex] = {};
                  zArr[arrIndex].z = valStr;
                  zArr[arrIndex].xArr = new Array();
                  this.addToDistArray(zDistArr, valStr, arrIndex);
               }
            } else {
               zLen = zArr.length;
               arrIndex = zLen;
               zArr[arrIndex] = {};
               zArr[arrIndex].z = valStr;
               zArr[arrIndex].xArr = new Array();
               this.addToDistArray(zDistArr, valStr, arrIndex);
            }
            //SEt Z Current Record..
            this.serCurrRec(chartObj.zAxisNode, z);
            //Get X Records
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            data = this.prepareDataset(chartObj);
            zArr[arrIndex].xArr = data[0].data;
         }
         zLen = zArr.length;
         if (zLen > 0) {
            var i = 0;
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.columns.column[z] = {};
               chartObj.chartData.columns.column[z].id = zArr[z].id;
               chartObj.chartData.columns.column[z].label = zArr[z].id;
               xLen = zArr[z].xArr.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     chartObj.chartData.rows.row[x] = {};
                     chartObj.chartData.rows.row[x].id = zArr[z].xArr[x].type;
                     chartObj.chartData.rows.row[x].label = zArr[z].xArr[x].type;
                     chartObj.chartData.dataSet[0].data[i] = {};
                     chartObj.chartData.dataSet[0].data[i].rowid = zArr[z].xArr[x].type;
                     chartObj.chartData.dataSet[0].data[i].columnid = zArr[z].id;
                     chartObj.chartData.dataSet[0].data[i].value = zArr[z].xArr[x].val;
                     chartObj.chartData.dataSet[0].data[i].link = "j-" + chartObj.jsHook + "-" + zArr[z].xArr[x].type + "|" + zArr[z].id + "," + zArr[z].xArr[x].val;
                     i++;
                  }
               }
            }
         }
         chartObj.chartData.colorRange.gradient = "1";
      } else if (isZChildOfX) {
         ////Z is a Child of X
         xRecs = this.apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.xAxisNode, x);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].zArr = new Array();
                  this.addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               arrIndex = xLen;
               xArr[arrIndex] = {};
               xArr[arrIndex].x = valStr;
               xArr[arrIndex].zArr = new Array();
               this.addToDistArray(xDistArr, valStr, arrIndex);
            }
            //Set X Current Record..
            this.serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            chartObj.xAxisNode = chartObj.zAxisNode;
            chartObj.xAxisElement = chartObj.zAxisElement;
            chartObj.xDataType = chartObj.zDataType;
            chartObj.xAxisFunction = chartObj.zAxisFunction;
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            chartObj.zGroupBy = new Array();
            data = this.prepareDataset(chartObj);
            xArr[arrIndex].zArr = data[0].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            var i = 0;
            for (var x = 0; x < xLen; x++) {
               chartObj.chartData.columns.column[x] = {};
               chartObj.chartData.columns.column[x].id = xArr[x].id;
               chartObj.chartData.columns.column[x].label = xArr[x].id;
               zLen = xArr[x].zArr.length;
               if (zLen > 0) {
                  for (var z = 0; z < zLen; z++) {
                     chartObj.chartData.rows.row[z] = {};
                     chartObj.chartData.rows.row[z].id = xArr[x].zArr[z].type;
                     chartObj.chartData.rows.row[z].label = xArr[x].zArr[z].type;
                     chartObj.chartData.dataSet[0].data[i] = {};
                     chartObj.chartData.dataSet[0].data[i].rowid = xArr[x].zArr[z].type;
                     chartObj.chartData.dataSet[0].data[i].columnid = xArr[x].id;
                     chartObj.chartData.dataSet[0].data[i].value = xArr[x].zArr[z].val;
                     chartObj.chartData.dataSet[0].data[i].link = "j-" + chartObj.jsHook + "-" + xArr[x].zArr[z].type + "|" + xArr[x].id + "," + xArr[x].zArr[z].val;
                     i++;
                  }
               }
            }
         }
         chartObj.chartData.colorRange.gradient = "1";
      } else {
         chartObj.xFilter = new Array();
         chartObj.yFilter = new Array();
         chartObj.xGroupBy = new Array();
         chartObj.xGroupBy[0] = {};
         chartObj.xGroupBy[0].id = chartObj.zAxisElement;
         chartObj.xGroupBy[0].dtyp = "STRING";
         chartObj.xGroupBy[0].val = null;
         data = this.prepareDataset(chartObj);
         zLen = data.length;
         if (zLen > 0) {
            var i = 0;
            for (var z = 0; z < zLen; z++) {
               xLen = data[z].data.length;
               chartObj.chartData.columns.column[z] = {};
               chartObj.chartData.columns.column[z].id = data[z].id;
               chartObj.chartData.columns.column[z].label = data[z].id;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     chartObj.chartData.rows.row[x] = {};
                     chartObj.chartData.rows.row[x].id = data[z].data[x].type;
                     chartObj.chartData.rows.row[x].label = data[z].data[x].type;
                     chartObj.chartData.dataSet[0].data[i] = {};
                     chartObj.chartData.dataSet[0].data[i].rowid = data[z].data[x].type;
                     chartObj.chartData.dataSet[0].data[i].columnid = data[z].id;
                     chartObj.chartData.dataSet[0].data[i].value = data[z].data[x].val;
                     chartObj.chartData.dataSet[0].data[i].link = "j-" + chartObj.jsHook + "-" + data[z].data[x].type + "|" + data[z].id + "," + data[z].data[x].val;
                     i++;
                  }
               }
            }
         }
         chartObj.chartData.colorRange.gradient = "1";
      }
      return zArr;
   }, prepareChartBW : function(chartObj) {
      var zRecs = 0;
      var dataJsonPointer = null;
      var data = null;
      var valStr = "";
      var zArr = new Array();
      var xArr = new Array();
      var categDistArr = new Array();
      var categ;
      var seriesDistArr = new Array();
      var series;
      var categIndex = -1;
      var zDistArr = new Array();
      var xDistArr = new Array();
      var arrIndex = -1;
      var lData = new Array();
      //lData[0].data ={  };
      var multiData = new Array();
      var larr = new Array();
      var xLen = 0;
      var zLen = 0;
      var dataLen = 0;
      var isYChildOfZ;
      var isYChildOfX;
      var multiRec;
      var yNodes = chartObj.yAxisNode.split(",");
      var yId = chartObj.yAxisElement.split(",");
      var yDtyp = chartObj.yDataType.split(",");
      var isXChildOfZ = this.isChild(chartObj.xAxisNode, chartObj.zAxisNode);
      var isZChildOfX = this.isChild(chartObj.zAxisNode, chartObj.xAxisNode);
      //////Categories
      chartObj.chartData.categories = new Array();
      chartObj.chartData.categories[0] = {};
      chartObj.chartData.categories[0].category = new Array();
      ////Cases
      if (isXChildOfZ) {
         ////X is a Child of Z
         zRecs = this.apz.data.getNoOfRecs(chartObj.zAxisNode);
         for (var z = 0; z < zRecs; z++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.zAxisNode, z);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.zAxisElement, chartObj.zDataType);
            if (chartObj.zAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(zDistArr, valStr);
               if (arrIndex == -1) {
                  zLen = zArr.length;
                  arrIndex = zLen;
                  lData[arrIndex] = {};
                  lData[arrIndex].data = new Array();
                  zArr[arrIndex] = {};
                  zArr[arrIndex].z = valStr;
                  zArr[arrIndex].xArr = new Array();
                  this.addToDistArray(zDistArr, valStr, arrIndex);
               }
            } else {
               zLen = zArr.length;
               arrIndex = zLen;
               lData[arrIndex] = {};
               lData[arrIndex].data = new Array();
               zArr[arrIndex] = {};
               zArr[arrIndex].z = valStr;
               zArr[arrIndex].xArr = new Array();
               this.addToDistArray(zDistArr, valStr, arrIndex);
            }
            //SEt Z Current Record..
            this.serCurrRec(chartObj.zAxisNode, z);
            //Get X Records
            for (var i = 0; i < yNodes.length; i++) {
               chartObj.xFilter = new Array();
               chartObj.xGroupBy = new Array();
               chartObj.yNode = yNodes[i];
               chartObj.yid = yId[i];
               chartObj.yDataType = yDtyp[i];
               chartObj.yFilter = new Array();
               multiData = this.prepareDataset(chartObj);
               larr[i] = multiData[0];
            }
            for (var x = 0; x < multiData[0].data.length; x++) {
               multiRec = "";
               for (var y = 0; y < yNodes.length; y++) {
                  if (multiRec == "") {
                     multiRec = larr[y].data[x].val;
                     multiRecType = larr[y].data[x].type;
                  } else {
                     multiRec = larr[y].data[x].val + "," + multiRec;
                     multiRecType = larr[y].data[x].type;
                  }
               }
               lData[arrIndex].data[x] = {};
               lData[arrIndex].data[x].val = multiRec;
               lData[arrIndex].data[x].type = multiRecType;
            }
            zArr[arrIndex].xArr = lData[arrIndex].data;
         }
         zLen = zArr.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = zArr[z].z;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = zArr[z].xArr.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     categ = zArr[z].xArr[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        this.addToDistArray(categDistArr, categ, categIndex);
                     }
                     chartObj.chartData.dataSet[z].data[categIndex] = {};
                     chartObj.chartData.dataSet[z].data[categIndex].value = zArr[z].xArr[x].val;
                     chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + chartObj.chartData.dataSet[z].seriesName + "|" + zArr[z].xArr[x].type + "," + zArr[z].xArr[x].val;
                  }
               }
            }
         }
      } else if (isZChildOfX) {
         ////Z is a Child of X
         xRecs = this.apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.xAxisNode, x);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  lData[arrIndex] = {};
                  lData[arrIndex].data = new Array();
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].zArr = new Array();
                  this.addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               arrIndex = xLen;
               lData[arrIndex] = {};
               lData[arrIndex].data = new Array();
               xArr[arrIndex] = {};
               xArr[arrIndex].x = valStr;
               xArr[arrIndex].zArr = new Array();
               this.addToDistArray(xDistArr, valStr, arrIndex);
            }
            //Set X Current Record..
            this.serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            for (var i = 0; i < yNodes.length; i++) {
               chartObj.xFilter = new Array();
               chartObj.xGroupBy = new Array();
               chartObj.yAxisNode = yNodes[i];
               chartObj.yAxisElement = yId[i];
               chartObj.yDataType = yDtyp[i];
               chartObj.yaxisfunction = chartObj.yaxisfunction;
               chartObj.yFilter = new Array();
               multiData = this.prepareDataset(chartObj);
               larr[i] = multiData[0];
            }
            for (var x = 0; x < multiData[0].data.length; x++) {
               multiRec = "";
               for (var y = 0; y < yNodes.length; y++) {
                  if (multiRec == "") {
                     multiRec = larr[y].data[x].val;
                     multiRecType = larr[y].data[x].type;
                  } else {
                     multiRec = larr[y].data[x].val + "," + multiRec;
                     multiRecType = larr[y].data[x].type;
                  }
               }
               lData[arrIndex].data[x] = {};
               lData[arrIndex].data[x].val = multiRec;
               lData[arrIndex].data[x].type = multiRecType;
            }
            xArr[arrIndex].zArr = lData[arrIndex].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var x = 0; x < xLen; x++) {
               categ = xArr[x].x;
               categIndex = this.getArrayIndex(categDistArr, categ);
               if (categIndex == -1) {
                  categIndex = chartObj.chartData.categories[0].category.length;
                  chartObj.chartData.categories[0].category[categIndex] = {};
                  chartObj.chartData.categories[0].category[categIndex].label = categ;
                  categDistArr[categ] = categIndex;
               }
               zLen = xArr[x].zArr.length;
               if (zLen > 0) {
                  for (var z = 0; z < zLen; z++) {
                     series = xArr[x].zArr[z].type;
                     seriesIndex = this.getArrayIndex(seriesDistArr, series);
                     if (seriesIndex == -1) {
                        seriesIndex = chartObj.chartData.dataSet.length;
                        chartObj.chartData.dataSet[seriesIndex] = {};
                        chartObj.chartData.dataSet[seriesIndex].seriesName = series;
                        chartObj.chartData.dataSet[seriesIndex].data = new Array();
                        //seriesDistArr[series]= seriesIndex;
                        this.addToDistArray(seriesDistArr, series, seriesIndex);
                     }
                     dataLen = chartObj.chartData.dataSet[seriesIndex].data.length;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex] = {};
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].value = xArr[x].zArr[z].val;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].link = "j-" + chartObj.jsHook + "-" + series + "|" + categ + "," + xArr[x].zArr[z].val;
                  }
               }
            }
         }
      } else {
         for (var i = 0; i < yNodes.length; i++) {
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yAxisNode = yNodes[i];
            chartObj.yAxisElement = yId[i];
            chartObj.yDataType = yDtyp[i];
            chartObj.yaxisfunction = chartObj.yaxisfunction;
            chartObj.yFilter = new Array();
            chartObj.xGroupBy[0] = {};
            chartObj.xGroupBy[0].id = chartObj.zAxisElement;
            chartObj.xGroupBy[0].dtyp = "STRING";
            chartObj.xGroupBy[0].val = null;
            multiData = this.prepareDataset(chartObj);
            larr[i] = multiData[0];
         }
         zLen = lData.length;
         for (var z = 0; z < zLen; z++) {
            for (var x = 0; x < multiData[0].data.length; x++) {
               multiRec = "";
               for (var y = 0; y < yNodes.length; y++) {
                  if (multiRec == "") {
                     multiRec = larr[y].data[x].val;
                     multiRecType = larr[y].data[x].type;
                  } else {
                     multiRec = larr[y].data[x].val + "," + multiRec;
                     multiRecType = larr[y].data[x].type;
                  }
               }
               lData[z].data[x] = {};
               lData[z].data[x].val = multiRec;
               lData[z].data[x].type = multiRecType;
            }
         }
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = lData[z].id;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = lData[z].data.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     categ = lData[z].data[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        categDistArr[categ] = categIndex;
                     }
                     chartObj.chartData.dataSet[z].data[categIndex] = {};
                     chartObj.chartData.dataSet[z].data[categIndex].value = lData[z].data[x].val;
                     chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + lData[z].id + "|" + categ + "," + lData[z].data[x].val;
                  }
               }
            }
         }
      }
      return zArr;
   }, prepareChartSC : function(chartObj) {
      var zRecs = 0;
      var dataJsonPointer = null;
      var data = null;
      var valStr = "";
      var zArr = new Array();
      var xArr = new Array();
      var categDistArr = new Array();
      var categ;
      var seriesDistArr = new Array();
      var series;
      var categIndex = -1;
      var zDistArr = new Array();
      var xDistArr = new Array();
      var arrIndex = -1;
      var lData = new Array();
      var xLen = 0;
      var zLen = 0;
      var dataLen = 0;
      var zNodes = new Array();
      var zId = new Array();
      var zDtyp = new Array();
      var yNodes = new Array();
      var yId = new Array();
      var yDtyp = new Array();
      //////Categories
      chartObj.chartData.categories = new Array();
      chartObj.chartData.categories[0] = {};
      chartObj.chartData.categories[0].category = new Array();
      if (!this.apz.isNull(chartObj.zAxisNode)) {
         zNodes = chartObj.zAxisNode.split(",");
      } else {
         zNodes = chartObj.zAxisNode;
      }
      if (!this.apz.isNull(chartObj.zAxisElement)) {
         zId = chartObj.zAxisElement.split(",");
      } else {
         zId = chartObj.zAxisElement;
      }
      if (!this.apz.isNull(chartObj.zDataType)) {
         zDtyp = chartObj.zDataType.split(",");
      } else {
         zDtyp = chartObj.zDataType;
      }
      if (!this.apz.isNull(chartObj.yAxisNode)) {
         yNodes = chartObj.yAxisNode.split(",");
      } else {
         yNodes = chartObj.yAxisNode;
      }
      if (!this.apz.isNull(chartObj.yAxisElement)) {
         yId = chartObj.yAxisElement.split(",");
      } else {
         yId = chartObj.yAxisElement;
      }
      if (!this.apz.isNull(chartObj.yDataType)) {
         yDtyp = chartObj.yDataType.split(",");
      } else {
         yDtyp = chartObj.yDataType;
      }
      var isXChildOfZ, isYChildOfZ, isYChildOfX, isZChildOfX;
      chartObj.chartData.dataSet = new Array();
      for ( i = 0; i < zNodes.length; i++) {
         chartObj.chartData.dataSet[i] = {};
         chartObj.chartData.dataSet[i].dataSet = new Array();
         xLen = 0;
         xArr = new Array();
         xRecs = this.apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.xAxisNode, x);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].zArr = new Array();
                  this.addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               arrIndex = xLen;
               xArr[arrIndex] = {};
               xArr[arrIndex].x = valStr;
               xArr[arrIndex].zArr = new Array();
               this.addToDistArray(xDistArr, valStr, arrIndex);
            }
            //Set X Current Record..
            this.serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            chartObj.xAxisNode = zNodes[i];
            chartObj.xAxisElement = zId[i];
            chartObj.xDataType = zDtyp[i];
            chartObj.xAxisFunction = chartObj.zAxisFunction;
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yAxisNode = yNodes[i];
            chartObj.yAxisElement = yId[i];
            chartObj.yDataType = yDtyp[i];
            chartObj.yaxisfunction = chartObj.yaxisfunction;
            chartObj.yFilter = new Array();
            chartObj.zGroupBy = new Array();
            lData = this.prepareDataset(chartObj);
            xArr[arrIndex].zArr = lData[0].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            chartObj.chartData.dataSet[i].dataSet = {}
            chartObj.chartData.dataSet[i].dataSet = new Array();
            for (var x = 0; x < xLen; x++) {
               categ = xArr[x].x;
               categIndex = this.getArrayIndex(categDistArr, categ);
               if (categIndex == -1) {
                  categIndex = chartObj.chartData.categories[0].category.length;
                  chartObj.chartData.categories[0].category[categIndex] = {};
                  chartObj.chartData.categories[0].category[categIndex].label = categ;
                  categDistArr[categ] = categIndex;
               }
               zLen = xArr[x].zArr.length;
               if (zLen > 0) {
                  for (var z = 0; z < zLen; z++) {
                     series = xArr[x].zArr[z].type;
                     seriesIndex = this.getArrayIndex(seriesDistArr, series);
                     if (seriesIndex == -1) {
                        seriesIndex = chartObj.chartData.dataSet[i].dataSet.length;
                        chartObj.chartData.dataSet[i].dataSet[seriesIndex] = {};
                        chartObj.chartData.dataSet[i].dataSet[seriesIndex].seriesName = series;
                        chartObj.chartData.dataSet[i].dataSet[seriesIndex].data = new Array();
                        //seriesDistArr[series]= seriesIndex;
                        this.addToDistArray(seriesDistArr, series, seriesIndex);
                     }
                     dataLen = chartObj.chartData.dataSet[i].dataSet[seriesIndex].data.length;
                     chartObj.chartData.dataSet[i].dataSet[seriesIndex].data[categIndex] = {};
                     chartObj.chartData.dataSet[i].dataSet[seriesIndex].data[categIndex].value = xArr[x].zArr[z].val;
                     //this.chartData.dataSet[i].dataSet[seriesIndex].data[categIndex].link
                     // = "j-" + chartObj.jsHook + "-" + series + "|" + categ +
                     // "," + xArr[x].zarr[z].val;
                  }
               }
            }
         }
      }
      return zArr;
   }, prepareChartDG : function(chartObj) {
      var zRecs = 0;
      var dataJsonPointer = null;
      var data = null;
      var valStr = "";
      var zArr = new Array();
      var xArr = new Array();
      var categDistArr = new Array();
      var categ;
      var seriesDistArr = new Array();
      var series;
      var categIndex = -1;
      var zDistArr = new Array();
      var xDistArr = new Array();
      var arrIndex = -1;
      var lData = new Array();
      var xLen = 0;
      var zLen = 0;
      var dataLen = 0;
      var isXChildOfZ = this.isChild(chartObj.xAxisNode, chartObj.zAxisNode);
      var isYChildOfZ = this.isChild(chartObj.yAxisNode, chartObj.zAxisNode);
      var isYChildOfX = this.isChild(chartObj.yAxisNode, chartObj.xAxisNode);
      var isZChildOfX = this.isChild(chartObj.zAxisNode, chartObj.xAxisNode);
      //////Categories
      chartObj.chartData.categories = new Array();
      chartObj.chartData.categories[0] = {};
      chartObj.chartData.categories[0].category = new Array();
      ////Cases
      if (isXChildOfZ) {
         ////X is a Child of Z
         zRecs = this.apz.data.getNoOfRecs(chartObj.zAxisNode);
         for (var z = 0; z < zRecs; z++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.zAxisNode, z);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.zAxisElement, chartObj.zDataType);
            if (chartObj.zAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(zDistArr, valStr);
               if (arrIndex == -1) {
                  zLen = zArr.length;
                  arrIndex = zLen;
                  zArr[arrIndex] = {};
                  zArr[arrIndex].z = valStr;
                  zArr[arrIndex].xArr = new Array();
                  this.addToDistArray(zDistArr, valStr, arrIndex);
               }
            } else {
               zLen = zArr.length;
               arrIndex = zLen;
               zArr[arrIndex] = {};
               zArr[arrIndex].z = valStr;
               zArr[arrIndex].xArr = new Array();
               this.addToDistArray(zDistArr, valStr, arrIndex);
            }
            //SEt Z Current Record..
            this.serCurrRec(chartObj.zAxisNode, z);
            //Get X Records
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            lData = this.prepareDataset(chartObj);
            zArr[arrIndex].xArr = lData[0].data;
         }
         zLen = zArr.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = zArr[z].z;
               chartObj.chartData.dataSet[z].id = zArr[z].z;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = zArr[z].xArr.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     categ = zArr[z].xArr[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        this.addToDistArray(categDistArr, categ, categIndex);
                     }
                     chartObj.chartData.dataSet[z].data[categIndex] = {};
                     if (chartObj.chartType == "DragNode") {
                        chartObj.chartData.dataSet[z].data[categIndex].y = lData[z].data[x].y;
                        chartObj.chartData.dataSet[z].data[categIndex].x = lData[z].data[x].x;
                        chartObj.chartData.dataSet[z].data[categIndex].width = "50";
                        chartObj.chartData.dataSet[z].data[categIndex].height = "50";
                        chartObj.chartData.dataSet[z].data[categIndex].name = lData[z].id;
                        chartObj.chartData.dataSet[z].data[categIndex].id = lData[z].id + "_" + categIndex;
                        chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + lData[z].id + "|" + categ + "," + lData[z].data[x].val;
                     } else {
                        chartObj.chartData.dataSet[z].data[categIndex].value = zArr[z].xArr[x].val;
                        chartObj.chartData.dataSet[z].data[categIndex].id = zArr[z].z + "_" + categIndex;
                        chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + chartObj.chartData.dataSet[z].seriesName + "|" + zArr[z].xArr[x].type + "," + zArr[z].xArr[x].val;
                     }
                  }
               }
            }
         }
      } else if (isZChildOfX) {
         ////Z is a Child of X
         xRecs = this.apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.xAxisNode, x);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].zArr = new Array();
                  this.addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               arrIndex = xLen;
               xArr[arrIndex] = {};
               xArr[arrIndex].x = valStr;
               xArr[arrIndex].zArr = new Array();
               this.addToDistArray(xDistArr, valStr, arrIndex);
            }
            //Set X Current Record..
            this.serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            chartObj.xAxisNode = chartObj.zAxisNode;
            chartObj.xAxisElement = chartObj.zAxisElement;
            chartObj.xDataType = chartObj.zDataType;
            chartObj.xAxisFunction = chartObj.zAxisFunction;
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            chartObj.zGroupBy = new Array();
            lData = this.prepareDataset(chartObj);
            xArr[arrIndex].zArr = lData[0].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var x = 0; x < xLen; x++) {
               categ = xArr[x].x;
               categIndex = this.getArrayIndex(categDistArr, categ);
               if (categIndex == -1) {
                  categIndex = chartObj.chartData.categories[0].category.length;
                  chartObj.chartData.categories[0].category[categIndex] = {};
                  chartObj.chartData.categories[0].category[categIndex].label = categ;
                  categDistArr[categ] = categIndex;
               }
               zLen = xArr[x].zArr.length;
               if (zLen > 0) {
                  for (var z = 0; z < zLen; z++) {
                     series = xArr[x].zArr[z].type;
                     seriesIndex = this.getArrayIndex(seriesDistArr, series);
                     if (seriesIndex == -1) {
                        seriesIndex = chartObj.chartData.dataSet.length;
                        chartObj.chartData.dataSet[seriesIndex] = {};
                        chartObj.chartData.dataSet[seriesIndex].seriesName = series;
                        chartObj.chartData.dataSet[seriesIndex].id = series;
                        chartObj.chartData.dataSet[seriesIndex].data = new Array();
                        //seriesDistArr[series]= seriesIndex;
                        this.addToDistArray(seriesDistArr, series, seriesIndex);
                     }
                     dataLen = chartObj.chartData.dataSet[seriesIndex].data.length;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex] = {};
                     if (chartObj.chartType == "DragNode") {
                        chartObj.chartData.dataSet[z].data[categIndex].y = lData[z].data[x].y;
                        chartObj.chartData.dataSet[z].data[categIndex].x = lData[z].data[x].x;
                        chartObj.chartData.dataSet[z].data[categIndex].width = "50";
                        chartObj.chartData.dataSet[z].data[categIndex].height = "50";
                        chartObj.chartData.dataSet[z].data[categIndex].name = lData[z].id;
                        chartObj.chartData.dataSet[z].data[categIndex].id = lData[z].id + "_" + categIndex;
                        chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + lData[z].id + "|" + categ + "," + lData[z].data[x].val;
                     } else {
                        chartObj.chartData.dataSet[seriesIndex].data[categIndex].value = xArr[x].zArr[z].val;
                        chartObj.chartData.dataSet[seriesIndex].data[categIndex].id = series + "_" + categIndex;
                        chartObj.chartData.dataSet[seriesIndex].data[categIndex].link = "j-" + chartObj.jsHook + "-" + series + "|" + categ + "," + xArr[x].zArr[z].val;
                     }
                  }
               }
            }
         }
      } else {
         chartObj.xFilter = new Array();
         chartObj.yFilter = new Array();
         chartObj.xGroupBy = new Array();
         chartObj.xGroupBy[0] = {};
         chartObj.xGroupBy[0].id = chartObj.zAxisElement;
         chartObj.xGroupBy[0].dtyp = "STRING";
         chartObj.xGroupBy[0].val = null;
         lData = this.prepareDataset(chartObj);
         zLen = lData.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = lData[z].id;
               chartObj.chartData.dataSet[z].id = lData[z].id;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = lData[z].data.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     categ = lData[z].data[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        categDistArr[categ] = categIndex;
                     }
                     chartObj.chartData.dataSet[z].data[categIndex] = {};
                     if (chartObj.chartType == "DragNode") {
                        chartObj.chartData.dataSet[z].data[categIndex].y = lData[z].data[x].y;
                        chartObj.chartData.dataSet[z].data[categIndex].x = lData[z].data[x].x;
                        chartObj.chartData.dataSet[z].data[categIndex].width = "50";
                        chartObj.chartData.dataSet[z].data[categIndex].height = "50";
                        chartObj.chartData.dataSet[z].data[categIndex].name = lData[z].id;
                        chartObj.chartData.dataSet[z].data[categIndex].id = lData[z].id + "_" + categIndex;
                        chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + lData[z].id + "|" + categ + "," + lData[z].data[x].val;
                     } else {
                        chartObj.chartData.dataSet[z].data[categIndex].value = lData[z].data[x].val;
                        chartObj.chartData.dataSet[z].data[categIndex].id = lData[z].id + "_" + categIndex;
                        chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + lData[z].id + "|" + categ + "," + lData[z].data[x].val;
                     }
                     
                  }
               }
            }
         }
      }
      return zArr;
   }, prepareDataset : function(params) {
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
      var isYChildOfX = this.isChild(params.yAxisNode, params.xAxisNode);
      var isXchildofY = this.isChild(params.xAxisNode, params.yAxisNode);
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
         xRecs = this.apz.data.getNoOfRecs(params.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
			 
			   if(this.apz.scrMetaData.nodesMap[lnodes[0]].relType == "1:1"){
				 var mrParent = this.apz.scrMetaData.nodesMap[lnodes[0]].mrParent;
				 x =  this.apz.scrMetaData.nodesMap[mrParent].currRec;
			   }
            dataJsonPointer = this.apz.data.getDataPointer(params.xAxisNode, x);
            data = dataJsonPointer;
            valStr = this.getVal(data, params.xAxisElement, params.xDataType);
            xRecReq = this.recordRequired(data, params.xFilter);
            if (params.chartType == "DragNode") {
               lData[arrIndex].x = valStr;
            }
            if (xRecReq) {
               grpArrIndex = this.getXGroupIndex(xGroupingReqd, params, data, xGroupDistArr, datasets);
               lData = datasets[grpArrIndex].data;
               arrIndex = this.getDataArrayIndex(params, datasets, grpArrIndex, valStr);
               this.serCurrRec(params.xAxisNode, x);
               ////Prepare Y Data
               this.prepareYData(params, lData[arrIndex]);
            }
         }
      } else if (isXchildofY) {
         // X is a child of Y -- This can only be  a case where the relation is
         // 1:1
         yTotalRecs = this.apz.data.getNoOfRecs(params.yAxisNode);
         for (var ty = 0; ty < yTotalRecs; ty++) {
            this.serCurrRec(params.yAxisNode, ty);
            xRecs = this.apz.data.getNoOfRecs(params.xAxisNode);
            for (var x = 0; x < xRecs; x++) {
				if(this.apz.scrMetaData.nodesMap[lnodes[0]].relType == "1:1"){
				 var mrParent = this.apz.scrMetaData.nodesMap[lnodes[0]].mrParent;
				 x =  this.apz.scrMetaData.nodesMap[mrParent].currRec;
			   }
               dataJsonPointer = this.apz.data.getDataPointer(params.xAxisNode, x);
               data = dataJsonPointer;
               valStr = this.getVal(data, params.xAxisElement, params.xDataType);
               xRecReq = this.recordRequired(data, params.xFilter);
               if (params.chartType == "DragNode") {
                  lData[arrIndex].x = valStr;
               }
               if (xRecReq) {
                  grpArrIndex = this.getXGroupIndex(xGroupingReqd, params, data, xGroupDistArr, datasets);
                  lData = datasets[grpArrIndex].data;
                  arrIndex = this.getDataArrayIndex(params, datasets, grpArrIndex, valStr);
                  this.serCurrRec(params.xAxisNode, x);
                  ////Prepare Y Data
                  this.prepareYData(params, lData[arrIndex]);
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
            xRecs = this.apz.data.getNoOfRecs(params.xAxisNode);
            for (var x = 0; x < xRecs; x++) {
			  if(this.apz.scrMetaData.nodesMap[lnodes[0]].relType == "1:1"){
				 var mrParent = this.apz.scrMetaData.nodesMap[lnodes[0]].mrParent;
				 x =  this.apz.scrMetaData.nodesMap[mrParent].currRec;
			   }
               dataJsonPointer = this.apz.data.getDataPointer(params.xAxisNode, x);
               data = dataJsonPointer;
               valStr = this.getVal(data, params.xAxisElement, params.xDataType);
               xRecReq = this.recordRequired(data, params.xFilter);
               if (xRecReq) {
                  grpArrIndex = this.getXGroupIndex(xGroupingReqd, params, data, xGroupDistArr, datasets);
                  lData = datasets[grpArrIndex].data;
                  arrIndex = this.getDataArrayIndex(params, datasets, grpArrIndex, valStr);
                  if (params.chartType == "BoxAndWhisker2D") {
                     //valStr = data[params.yAxisElement];//IDERES
                     valStr = this.getVal(data, params.yAxisElement, "STRING");
                  } else {
                     valStr = this.getVal(data, params.yAxisElement, params.yDataType);
                  }
                  lData[arrIndex].noOfRecs = lData[arrIndex].noOfRecs + 1;
                  if (params.yAxisFunction == "SUM") {
                     lData[arrIndex].val = lData[arrIndex].val + valStr;
                  } else if (params.yAxisFunction == "AVERAGE") {
                     lData[arrIndex].val = lData[arrIndex].val + valStr;
                     if (arrIndex > 0) {
                        lData[arrIndex].val = lData[arrIndex].val / 2;
                     }
                  } else if (params.yAxisFunction == "MIN") {
                     if (valStr < lData[xLen].y) {
                        lData[xLen].val = valStr;
                     }
                  } else if (params.yAxisFunction == "MAX") {
                     if (valStr > lData[arrIndex].y) {
                        lData[arrIndex].val = valStr;
                     }
                  } else {
                     lData[arrIndex].val = valStr;
                  }
                  if (params.chartType == "DragNode") {
                     lData[arrIndex].x = this.getVal(data, params.xAxisElement, params.xDataType);
                     lData[arrIndex].y = this.getVal(data, params.yAxisElement, params.yDataType);
                  }
               }
            }
         } else {
            //No Relation and Different...
            xRecs = this.apz.data.getNoOfRecs(params.xAxisNode);
            for (var x = 0; x < xRecs; x++) {
				if(this.apz.scrMetaData.nodesMap[lnodes[0]].relType == "1:1"){
				 var mrParent = this.apz.scrMetaData.nodesMap[lnodes[0]].mrParent;
				 x =  this.apz.scrMetaData.nodesMap[mrParent].currRec;
			   }
               dataJsonPointer = this.apz.data.getDataPointer(params.xAxisNode, x);
               valStr = this.getVal(dataJsonPointer, params.xAxisElement, params.xDataType);
               if (params.xFilter.length > 0) {
                  xRecReq = true;
                  xRecReq = this.recordRequired(data, params.xFilter);
               } else {
                  xRecReq = true;
               }
               if (params.chartType == "DragNode") {
                  lData[arrIndex].x = valStr;
               }
               if (xRecReq) {
                  grpArrIndex = this.getXGroupIndex(xGroupingReqd, params, data, xGroupDistArr, datasets);
                  lData = datasets[grpArrIndex].data;
                  arrIndex = this.getDataArrayIndex(params, datasets, grpArrIndex, valStr);
                  this.serCurrRec(params.xAxisNode, x);
                  ////Prepare Y Data
                  this.prepareYData(params, lData[arrIndex]);
               }
            }
         }
      }
      return datasets;
   }, getXGroupIndex : function(xGroupIngReqd, params, data, grpDistArr, datasets) {
      var data;
      var xGroupLen = 0;
      var xGroupId = this.getGroupId(data, params.xGroupBy);
      var grpArrIndex = this.getArrayIndex(grpDistArr, xGroupId);
      if (xGroupIngReqd) {
         if (grpArrIndex == -1) {
            xGroupLen = datasets.length;
            grpArrIndex = xGroupLen;
            datasets[grpArrIndex] = {};
            datasets[grpArrIndex].id = xGroupId;
            datasets[grpArrIndex].data = new Array();
            datasets[grpArrIndex].xDistArray = new Array();
            //xGroupDistArr[lxgroupid] = arrIndex;
            this.addToDistArray(grpDistArr, xGroupId, grpArrIndex);
            data = datasets[grpArrIndex].data;
         } else {
            data = datasets[grpArrIndex].data;
         }
      } else {
         data = datasets[0].data;
         grpArrIndex = 0;
      }
      return grpArrIndex;
   }, getDataArrayIndex : function(params, datasets, grpArrIndex, value) {
      var arrIndex = -1;
      var create = true;
      if (params.xAxisFunction == "DISTINCT") {
         arrIndex = this.getArrayIndex(datasets[grpArrIndex].xDistArray, value);
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
         this.addToDistArray(datasets[grpArrIndex].xDistArray, value, arrIndex);
      }
      return arrIndex;
   }, prepareYData : function(params, data) {
      var yRecs = 0;
      var lData;
      var datapointer;
      var valStr;
      yRecs = this.apz.data.getNoOfRecs(params.yAxisNode);
      for (var y = 0; y < yRecs; y++) {
         dataJsonPointer = null;
         lData = null;
         dataJsonPointer = this.apz.data.getDataPointer(params.yAxisNode, y);
         valStr = this.getVal(dataJsonPointer, params.yid, params.ydtyp);
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
   }, convertAmt : function(amt) {
      var re = /,/gi;
      if (!this.apz.isNull(amt)) {
         var str = amt.toString();
         tr = str.replace(re, "");
         return parseFloat(str);
      }
   }, flipTblChart : function(fgp) {
      var chartDiv = document.getElementById( fgp + "_chart");
      var tableDiv = document.getElementById( fgp + "_tablebox");
      var gtBtnDiv = document.getElementById( fgp + "_cp_grid");
      var mvBtnsDiv = document.getElementById( fgp + "_nav_grid");
      var adBtnDiv = document.getElementById( fgp + "_add_del_grid");
      if (chartDiv.style.display == 'block') {
         tableDiv.style.display = 'block';
         chartDiv.style.display = 'none';
         gtBtnDiv.style.display = 'block';
         mvBtnsDiv.style.display = 'block';
         if (adBtnDiv) {
            adBtnDiv.style.display = 'block';
         }
      } else {
         chartDiv.style.display = 'block';
         tableDiv.style.display = 'none';
         gtBtnDiv.style.display = 'none';
         mvBtnsDiv.style.display = 'none';
         if (adBtnDiv) {
            adBtnDiv.style.display = 'none';
         }
      }
   }, getVal : function(data, id, dType) {
      var val;
      if (data != null) {
         var elmName = id;
         var lind = id.lastIndexOf(this.apz.idSep);
         if (lind >= 0) {
            elmName = id.substr(lind + this.apz.idSep.length);
         }
         val = data[elmName];
         if (dType == "NUMBER") {
            val = this.convertAmt(val);
         }
      } else {
         val = 0;
      }
      return val;
   }, addToDistArray : function(array, index, val) {
      array[index] = val;
   }, getArrayIndex : function(array, index) {
      var lindex = -1;
      if ((array[index] != null) && (array[index] != "undefined")) {
         lindex = array[index];
      }
      return lindex;
   }, recordRequired : function(data, filter) {
      var required = true;
      if (filter.length > 0) {
         for (var f = 0; f < filter.length; f++) {
            if (filter[f].val != this.getVal(data, filter[f].id, "STRING")) {
               required = false;
               break;
            }
         }
      }
      return required;
   }, getGroupId : function(data, group) {
      var groupId;
      var len = group.length;
      for (var f = 0; f < len; f++) {
         if (f == 0) {
            groupId = this.getVal(data, group[f].id, "STRING");
         } else {
            groupId = groupId + "|" + this.getVal(data, group[f].id, "STRING");
         }
      }
      return groupId;
   }, convertToDualY : function(params) {
      if (this.apz.isNull(params.dataGroups) && this.apz.isNull(params.lineSet)) {
         this.convertToDefaultDualY(2, 1, params);
      } else {
         var noOfSets = 0;
         var series;
         var dataGroupsLength = params.dataGroups.length;
         var dataSetLength = 0;
         var lineSetLength = params.lineSet.length;
         var dataGroups = new Array();
         var lineSet = new Array();
         var seriesArray = new Array();
         if (dataGroupsLength > 0) {
            for (var g = 0; g < dataGroupsLength; g++) {
               dataGroups[g] = {};
               dataGroups[g].dataSet = new Array();
               dataSetLength = params.dataGroups[g].dataSets.length;
               if (dataSetLength > 0) {
                  for (var d = 0; d < dataSetLength; d++) {
                     dataGroups[g].dataSet[d] = {};
                     series = params.dataGroups[g].dataSets[d];
                     dataGroups[g].dataSet[d].seriesName = series;
                     dataGroups[g].dataSet[d].data = new Array();
                     seriesArray[series] = {};
                     seriesArray[series].type = "GROUP";
                     seriesArray[series].group = g;
                     seriesArray[series].dataSet = d;
                  }
               }
            }
         }
         if (lineSetLength > 0) {
            lineSet = new Array();
            for (var l = 0; l < lineSetLength; l++) {
               lineSet[l] = {};
               series = params.lineSet[l]
               seriesArray[series] = {};
               seriesArray[series].type = "LINESET";
               seriesArray[series].group = g;
               seriesArray[series].dataSet = l;
               lineSet[l].seriesName = series;
               lineSet[l].data = new Array();
            }
         }
         //Now Loop thru Chart Datasets
         var noOfChartDataSets = chartObj.chartData.dataSet.length;
         var groupIndex = -1;
         var dataSetIndex = -1;
         var type = "";
         if (noOfChartDataSets > 0) {
            for (var c = 0; c < noOfChartDataSets; c++) {
               series = chartObj.chartData.dataSet[c].seriesName;
               type = seriesArray[series].type;
               groupIndex = seriesArray[series].group;
               dataSetIndex = seriesArray[series].dataSet;
               if (type == "LINESET") {
                  lineSet[dataSetIndex] = chartObj.chartData.dataSet[c];
               } else {
                  dataGroups[groupIndex].dataSet[dataSetIndex] = chartObj.chartData.dataSet[c];
               }
            }
         }
         chartObj.chartData.dataSet = dataGroups;
         chartObj.chartData.lineSet = lineSet;
         var l = 0;
         l = l + 1;
      }
   }, convertToDefaultDualY : function(groupSize, lineGroupSize , chartObj) {
      var dataGroups = new Array();
      var lineSet = new Array();
      var noOfChartDataSets = chartObj.chartData.dataSet.length;
      var dataGroups = new Array();
      var lineSet = new Array();
      var groupSetsSize = noOfChartDataSets - lineGroupSize;
      var dataGrpIndex = -1;
      var dataGrpDataSetIndex = -1;
      var grpCounter = groupSize;
      if (noOfChartDataSets >= lineGroupSize) {
         for (var l = 0; l < lineGroupSize; l++) {
            lineSet[l] = chartObj.chartData.dataSet[noOfChartDataSets - 1];
         }
         if (groupSetsSize > 0) {
            ///Sets are there
            grpCounter = groupSize;
            for (var d = 0; d < groupSetsSize; d++) {
               if (grpCounter == groupSize) {
                  grpCounter = 0;
                  dataGrpIndex = dataGrpIndex + 1;
                  dataGroups[dataGrpIndex] = {};
                  dataGrpDataSetIndex = -1;
                  dataGroups[dataGrpIndex].dataSet = new Array();
               }
               grpCounter = grpCounter + 1;
               dataGrpDataSetIndex = dataGrpDataSetIndex + 1;
               dataGroups[dataGrpIndex].dataSet[dataGrpDataSetIndex] = {};
               dataGroups[dataGrpIndex].dataSet[dataGrpDataSetIndex] = chartObj.chartData.dataSet[d];
            }
         }
         chartObj.chartData.dataSet = dataGroups;
         chartObj.chartData.lineSet = lineSet;
      }
   }, prepareChartBS : function(chartObj) {
      var zRecs = 0;
      var dataJsonPointer = null;
      var data = null;
      var valStr = "";
      var zArr = new Array();
      var xArr = new Array();
      var categDistArr = new Array();
      var categ;
      var seriesDistArr = new Array();
      var series;
      var categIndex = -1;
      var zDistArr = new Array();
      var xDistArr = new Array();
      var arrIndex = -1;
      var lData = new Array();
      var xLen = 0;
      var zLen = 0;
      var dataLen = 0;
      var xMin = 99999999999999999;
      var xMax = -99999999999999999;
      var lval = 0;
      var isXChildOfZ = this.isChild(chartObj.xAxisNode, chartObj.zAxisNode);
      var isYChildOfZ = this.isChild(chartObj.yAxisNode, chartObj.zAxisNode);
      var isYChildOfX = this.isChild(chartObj.yAxisNode, chartObj.xAxisNode);
      var isZChildOfX = this.isChild(chartObj.zAxisNode, chartObj.xAxisNode);
      var labelVal = new Array();
      //////Categories
      chartObj.chartData.categories = new Array();
      chartObj.chartData.categories[0] = {};
      chartObj.chartData.categories[0].category = new Array();
      ////Cases
      if (isXChildOfZ) {
         ////X is a Child of Z
         zRecs = this.apz.data.getNoOfRecs(chartObj.zAxisNode);
         for (var z = 0; z < zRecs; z++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.zAxisNode, z);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.zAxisElement, chartObj.zDataType);
            if (chartObj.zAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(zDistArr, valStr);
               if (arrIndex == -1) {
                  zLen = zArr.length;
                  arrIndex = zLen;
                  zArr[arrIndex] = {};
                  zArr[arrIndex].z = valStr;
                  zArr[arrIndex].xArr = new Array();
                  this.addToDistArray(zDistArr, valStr, arrIndex);
               }
            } else {
               zLen = zArr.length;
               arrIndex = zLen;
               zArr[arrIndex] = {};
               zArr[arrIndex].z = valStr;
               zArr[arrIndex].xArr = new Array();
               this.addToDistArray(zDistArr, valStr, arrIndex);
            }
            //SEt Z Current Record..
            this.serCurrRec(chartObj.zAxisNode, z);
            //Get X Records
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            lData = this.prepareDataset(chartObj);
            zArr[arrIndex].xArr = lData[0].data;
         }
         zLen = zArr.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = zArr[z].z;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = zArr[z].xArr.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     if (z < 1) {
                        lval = zArr[0].xArr[x].type;
                        lval = this.convertAmt(lval);
                        if (lval < xMin) {
                           xMin = lval;
                        }
                        if (lval > xMax) {
                           xMax = lval;
                        }
                     }
                     chartObj.chartData.dataSet[z].data[x] = {};
                     chartObj.chartData.dataSet[z].data[x].x = zArr[z].xArr[x].type;
                     chartObj.chartData.dataSet[z].data[x].y = zArr[z].xArr[x].val;
                     chartObj.chartData.dataSet[z].data[x].z = zArr[z].xArr[x].val;
                     chartObj.chartData.dataSet[z].data[x].link = "j-" + chartObj.jsHook + "-" + zArr[z].z + "|" + zArr[z].xArr[x].type + "," + zArr[z].xArr[x].val;
                  }
               }
            }
         }
      } else if (isZChildOfX) {
         ////Z is a Child of X
         xRecs = this.apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.xAxisNode, x);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].zArr = new Array();
                  this.addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               arrIndex = xLen;
               xArr[arrIndex] = {};
               xArr[arrIndex].x = valStr;
               xArr[arrIndex].zArr = new Array();
               this.addToDistArray(xDistArr, valStr, arrIndex);
            }
            //Set X Current Record..
            this.serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            chartObj.xAxisNode = chartObj.zAxisNode;
            chartObj.xAxisElement = chartObj.zAxisElement;
            chartObj.xDataType = chartObj.zDataType;
            chartObj.xAxisFunction = chartObj.zAxisFunction;
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            chartObj.zGroupBy = new Array();
            lData = this.prepareDataset(chartObj);
            xArr[arrIndex].zArr = lData[0].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var x = 0; x < xLen; x++) {
               categ = xArr[x].x;
               categIndex = this.getArrayIndex(categDistArr, categ);
               zLen = xArr[x].zArr.length;
               if (zLen > 0) {
                  for (var z = 0; z < zLen; z++) {
                     if (x < 1) {
                        lval = zArr[0].xArr[z].type;
                        lval = this.convertAmt(lval);
                        if (lval < xMin) {
                           xMin = lval;
                        }
                        if (lval > xMax) {
                           xMax = lval;
                        }
                     }
                     series = xArr[x].zArr[z].type;
                     seriesIndex = this.getArrayIndex(seriesDistArr, series);
                     if (seriesIndex == -1) {
                        seriesIndex = chartObj.chartData.dataSet.length;
                        chartObj.chartData.dataSet[seriesIndex] = {};
                        chartObj.chartData.dataSet[seriesIndex].seriesName = series;
                        chartObj.chartData.dataSet[seriesIndex].data = new Array();
                        //seriesDistArr[series]= seriesIndex;
                        this.addToDistArray(seriesDistArr, series, seriesIndex);
                     }
                     dataLen = chartObj.chartData.dataSet[seriesIndex].data.length;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex] = {};
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].x = categ;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].y = xArr[x].zArr[z].val;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].z = xArr[x].zArr[z].val;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].link = "j-" + chartObj.jsHook + "-" + series + "|" + categ + "," + xArr[x].zArr[z].val;
                  }
               }
            }
         }
      } else {
         chartObj.xFilter = new Array();
         chartObj.yFilter = new Array();
         chartObj.xGroupBy = new Array();
         chartObj.xGroupBy[0] = {};
         chartObj.xGroupBy[0].id = chartObj.zAxisElement;
         chartObj.xGroupBy[0].dtyp = "STRING";
         chartObj.xGroupBy[0].val = null;
         lData = this.prepareDataset(chartObj);
         zLen = lData.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = lData[z].id;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = lData[z].data.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     if (z < 1) {
                        lval = lData[0].data[x].type;
                        lval = this.convertAmt(lval);
                        if (lval < xMin) {
                           xMin = lval;
                        }
                        if (lval > xMax) {
                           xMax = lval;
                        }
                     }
                     categ = lData[z].data[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     /*if (categIndex ==  - 1) {
                      categIndex = gchartdata.categories[0].category.length;
                      gchartdata.categories[0].category[categIndex] = {};
                      gchartdata.categories[0].category[categIndex].label =
                      categ;
                      categDistArr[categ] = categIndex;
                      }*/
                     chartObj.chartData.dataSet[z].data[categIndex] = {};
                     chartObj.chartData.dataSet[z].data[categIndex].x = categ;
                     chartObj.chartData.dataSet[z].data[categIndex].y = lData[z].data[x].val;
                     chartObj.chartData.dataSet[z].data[categIndex].z = lData[z].data[x].val;
                     chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + lData[z].id + "|" + categ + "," + lData[z].data[x].val;
                  }
               }
            }
         }
      }
      var xStart = parseInt(xMin);
      var xEnd = parseInt(xMax);
      var xIncr = (xEnd - xStart) / 9;
      xIncr = parseInt(xIncr);
      var j = 1
      for (var i = 0; i < 7; i++) {
         labelVal[i] = xIncr * j;
         chartObj.chartData.categories[0].category[i] = {};
         chartObj.chartData.categories[0].category[i].x = labelVal[i];
         chartObj.chartData.categories[0].category[i].label = labelVal[i];
         j = j + 2;
      }
      return zArr;
   }, prepareChartSB : function(chartObj) {
      var zRecs = 0;
      var dataJsonPointer = null;
      var data = null;
      var valStr = "";
      var zArr = new Array();
      var xArr = new Array();
      var categDistArr = new Array();
      var categ;
      var seriesDistArr = new Array();
      var series;
      var categIndex = -1;
      var zDistArr = new Array();
      var xDistArr = new Array();
      var arrIndex = -1;
      var lData = new Array();
      var xLen = 0;
      var zLen = 0;
      var dataLen = 0;
      var xMin = 99999999999999999;
      var xMax = -99999999999999999;
      var lVal = 0;
      var isXChildOfZ = this.isChild(chartObj.xAxisNode, chartObj.zAxisNode);
      var isYChildOfZ = this.isChild(chartObj.yAxisNode, chartObj.zAxisNode);
      var isYChildOfX = this.isChild(chartObj.yAxisNode, chartObj.xAxisNode);
      var isZChildOfX = this.isChild(chartObj.zAxisNode, chartObj.xAxisNode);
      var labelVal = new Array();
      //////Categories
      chartObj.chartData.categories = new Array();
      chartObj.chartData.categories[0] = {};
      chartObj.chartData.categories[0].category = new Array();
      ////Cases
      if (isXChildOfZ) {
         ////X is a Child of Z
         zRecs = this.apz.data.getNoOfRecs(chartObj.zAxisNode);
         for (var z = 0; z < zRecs; z++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.zAxisNode, z);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.zAxisElement, chartObj.zDataType);
            if (chartObj.zAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(zDistArr, valStr);
               if (arrIndex == -1) {
                  zLen = zArr.length;
                  arrIndex = zLen;
                  zArr[arrIndex] = {};
                  zArr[arrIndex].z = valStr;
                  zArr[arrIndex].xArr = new Array();
                  this.addToDistArray(zDistArr, valStr, arrIndex);
               }
            } else {
               zLen = zArr.length;
               arrIndex = zLen;
               zArr[arrIndex] = {};
               zArr[arrIndex].z = valStr;
               zArr[arrIndex].xArr = new Array();
               this.addToDistArray(zDistArr, valStr, arrIndex);
            }
            //SEt Z Current Record..
            this.serCurrRec(chartObj.zAxisNode, z);
            //Get X Records
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            lData = this.prepareDataset(chartObj);
            zArr[arrIndex].xArr = lData[0].data;
         }
         zLen = zArr.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = zArr[z].z;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = zArr[z].xArr.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     if (z < 1) {
                        lVal = zArr[0].xArr[x].type;
                        lVal = this.convertAmt(lVal);
                        if (lVal < xMin) {
                           xMin = lVal;
                        }
                        if (lVal > xMax) {
                           xMax = lVal;
                        }
                     }
                     chartObj.chartData.dataSet[z].data[x] = {};
                     chartObj.chartData.dataSet[z].data[x].x = zArr[z].xArr[x].type;
                     chartObj.chartData.dataSet[z].data[x].y = zArr[z].xArr[x].val;
                     chartObj.chartData.dataSet[z].data[x].link = "j-" + chartObj.jsHook + "-" + chartObj.chartData.dataSet[z].seriesName + "|" + zArr[z].xArr[x].type + "," + zArr[z].xArr[x].val;
                  }
               }
            }
         }
      } else if (isZChildOfX) {
         ////Z is a Child of X
         xRecs = this.apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = this.apz.data.getDataPointer(chartObj.xAxisNode, x);
            data = dataJsonPointer;
            valStr = this.getVal(data, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = this.getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].zArr = new Array();
                  this.addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               arrIndex = xLen;
               xArr[arrIndex] = {};
               xArr[arrIndex].x = valStr;
               xArr[arrIndex].zArr = new Array();
               this.addToDistArray(xDistArr, valStr, arrIndex);
            }
            //Set X Current Record..
            this.serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            chartObj.xAxisNode = chartObj.zAxisNode;
            chartObj.xAxisElement = chartObj.zAxisElement;
            chartObj.xDataType = chartObj.zDataType;
            chartObj.xAxisFunction = chartObj.zAxisFunction;
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            chartObj.zGroupBy = new Array();
            lData = this.prepareDataset(chartObj);
            xArr[arrIndex].zArr = lData[0].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var x = 0; x < xLen; x++) {
               categ = xArr[x].x;
               categIndex = this.getArrayIndex(categDistArr, categ);
               zLen = xArr[x].zArr.length;
               if (zLen > 0) {
                  for (var z = 0; z < zLen; z++) {
                     if (x < 1) {
                        lVal = xArr[0].zArr[z].type;
                        lVal = this.convertAmt(lVal);
                        if (lVal < xMin) {
                           xMin = lVal;
                        }
                        if (lVal > xMax) {
                           xMax = lVal;
                        }
                     }
                     series = xArr[x].zArr[z].type;
                     seriesIndex = this.getArrayIndex(seriesDistArr, series);
                     if (seriesIndex == -1) {
                        seriesIndex = chartObj.chartData.dataSet.length;
                        chartObj.chartData.dataSet[seriesIndex] = {};
                        chartObj.chartData.dataSet[seriesIndex].seriesName = series;
                        chartObj.chartData.dataSet[seriesIndex].data = new Array();
                        //seriesDistArr[series]= seriesIndex;
                        this.addToDistArray(seriesDistArr, series, seriesIndex);
                     }
                     dataLen = chartObj.chartData.dataSet[seriesIndex].data.length;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex] = {};
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].x = xArr[x].x;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].y = xArr[x].zArr[z].val;
                     chartObj.chartData.dataSet[seriesIndex].data[categIndex].link = "j-" + chartObj.jsHook + "-" + series + "|" + categ + "," + xArr[x].zArr[z].val;
                  }
               }
            }
         }
      } else {
         chartObj.xFilter = new Array();
         chartObj.yFilter = new Array();
         chartObj.xGroupBy = new Array();
         chartObj.xGroupBy[0] = {};
         chartObj.xGroupBy[0].id = chartObj.zAxisElement;
         chartObj.xGroupBy[0].dtyp = "STRING";
         chartObj.xGroupBy[0].val = null;
         lData = this.prepareDataset(chartObj);
         zLen = lData.length;
         if (zLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var z = 0; z < zLen; z++) {
               chartObj.chartData.dataSet[z] = {};
               chartObj.chartData.dataSet[z].seriesName = lData[z].id;
               chartObj.chartData.dataSet[z].data = new Array();
               xLen = lData[z].data.length;
               if (xLen > 0) {
                  for (var x = 0; x < xLen; x++) {
                     if (z < 1) {
                        lVal = lData[0].data[x].type;
                        lVal = this.convertAmt(lVal);
                        if (lVal < xMin) {
                           xMin = lVal;
                        }
                        if (lVal > xMax) {
                           xMax = lVal;
                        }
                     }
                     categ = lData[z].data[x].type;
                     categIndex = this.getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        categDistArr[categ] = categIndex;
                     }
                     if (categIndex == -1) {
                        categIndex = x;
                     }
                     chartObj.chartData.dataSet[z].data[categIndex] = {};
                     chartObj.chartData.dataSet[z].data[categIndex].x = categ;
                     chartObj.chartData.dataSet[z].data[categIndex].y = lData[z].data[x].val;
                     chartObj.chartData.dataSet[z].data[categIndex].link = "j-" + chartObj.jsHook + "-" + lData[z].id + "|" + categ + "," + lData[z].data[x].val;
                  }
               }
            }
         }
      }
      var xStart = parseInt(xMin);
      var xEnd = parseInt(xMax);
      var xIncr = (xEnd - xStart) / 9;
      xIncr = parseInt(xIncr);
      var j = 1
      for (var i = 0; i < 7; i++) {
         labelVal[i] = xIncr * j;
         chartObj.chartData.categories[0].category[i] = {};
         chartObj.chartData.categories[0].category[i].x = labelVal[i];
         chartObj.chartData.categories[0].category[i].label = labelVal[i];
         j = j + 2;
      }
      return zArr;
   }
}
