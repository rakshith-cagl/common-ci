function paintChart (chartObj) {
	prepareChart(chartObj);
}
var chartModelAxis = new Array ("Area2D", "Bar2D", "Column2D", "Line","Spline");
var chartModelPie = new Array ("Pie2D", "Doughnut2D");
var chartModalMS = new Array ("MSArea", "MSBar2D", "MSColumn2D", "MSLine");
var chartDetails = [];
for (var i = 0 ; i < chartModelAxis.length ; i++) {
	chartDetails[chartModelAxis[i]] = {};
	chartDetails[chartModelAxis[i]].dataModel = "Series";
}
for (var i = 0 ; i < chartModelPie.length ; i++) {
	chartDetails[chartModelPie[i]] = {};
	chartDetails[chartModelPie[i]].dataModel = "Shape";
}
for (var i = 0 ; i < chartModalMS.length ; i++) {
	chartDetails[chartModalMS[i]] = {};
	chartDetails[chartModalMS[i]].dataModel = "MS";
}
/* Preparing C3 chart data and other functionalities  */
function prepareChart (chartObj) {
	var c3chart = {};
	c3chart.chartObj = chartObj;
	if (apz.getDataType(chartObj.yAxisElement) == "Array") {
		if (chartObj.yAxisElement.length == 1) {
			chartObj.yAxisElement = chartObj.yAxisElement[0];
			chartObj.yAxisNode = chartObj.yAxisNode[0];
			chartObj.yDataType = chartObj.yDataType[0];
		}
	}
	if (apz.getDataType(chartObj.zAxisElement) == "Array") {
		if (chartObj.zAxisElement.length == 1) {
			chartObj.zAxisElement = chartObj.zAxisElement[0];
			chartObj.zAxisNode = chartObj.zAxisNode[0];
			chartObj.zDataType = chartObj.zDataType[0];
		}
	}
	var currTheme = apz.theme;

	if (!apz.isNull(chartObj.chartStyleSheet)) {
		if(!apz.isNull(apz.chartStyles) && apz.chartStyles.indexOf(chartObj.chartStyleSheet) > -1){
			chartObj.chartStyleSheetPath = apz.getStylesPath() + "/" + currTheme + "/csd/" + chartObj.chartStyleSheet + '.json';
		} else {
			//// TBC - How to get base theme name from app? Hardcoded appzillon for now in the path
			chartObj.chartStyleSheetPath = apz.getInfraStylesPath() + "/themes/"+apz.baseThemesMap[currTheme]+"/csd/" + chartObj.chartStyleSheet + '.json';
		}
	}
	var chartStyle = addVisualPrefs(chartObj);
	chartStyle.chartType = chartObj.chartType;
	////// check user defined function for manipulating data if provided
	/*if(apz.isFunction(apz.app['updateC3ChartBeforePrepare'])){
		apz.app.updateC3ChartBeforePrepare(chartObj, chartStyle, chartObj.id, c3chart);
	}*/
	if(chartObj.cbevents){
		var cbEvents = JSON.parse(chartObj.cbevents);
		if(cbEvents["ONBEFOREPREPARE"]){
				eval(cbEvents["ONBEFOREPREPARE"])(chartObj, chartStyle, chartObj.id, c3chart);
			}
	}
	
	applyChartStyle (chartStyle, chartObj.id);
	applyCosmeticChartStyle (chartObj.id, chartObj.chartType);
	
	getLegend(chartStyle, c3chart);
	
	initalizeC3Chart(c3chart, chartStyle, chartObj.id);
	getChartData(chartObj, chartStyle, c3chart);
	//showGrid(chartObj,chartStyle,c3chart);
	/// additional featuees for C3
	additionalFeatures (chartObj, c3chart);
	////// check user defined function for manipulating data if provided
	/*if(apz.isFunction(apz.app['updateC3ChartBeforeRender'])){
		apz.app.updateC3ChartBeforeRender(chartObj.chartType, c3chart.data, chartObj.id, c3chart);
	}*/	
	if(chartObj.cbevents){
		if(cbEvents["ONBEFORERENDER"]){
				eval(cbEvents["ONBEFORERENDER"])(chartObj.chartType, c3chart.data, chartObj.id, c3chart);
			}
	}
	renderChart(c3chart);
}

function initalizeC3Chart (c3chart, chartStyle, chartId) {
	var chartType = chartStyle.chartType;
	//c3chart.grid = getGrid();
	//pradeep changes 
	c3chart.data = {};
	c3chart.data.columns = [];
	/// prepare first data set
	c3chart.axis = {};
	c3chart.axis.x = {};
	c3chart.axis.y = {};
	c3chart.axis.x.type = "category";
	c3chart.axis.x.categories = [];
	
	c3chart.axis.y.padding = {};
	c3chart.axis.y.padding.top = 40;
	c3chart.axis.y.padding.bottom = 0;
	c3chart.axis.y.min = 0
	
	c3chart.axis.y.show = false;
	c3chart.axis.x.show = false;
	
	c3chart.bar = {};
	c3chart.bar.width = {};
	c3chart.bar.width.ratio = 0.5;
	
	var chartDataModel = chartDetails[chartType].dataModel;
	if (chartDataModel == "Series") { 
		///// Random color for each palette can be user defined function 
		c3chart.data.color = function (color, d) {return getRandomColor(color, d);};
	} else if (chartDataModel == "Shape") {
	} else if (chartDataModel == "MS") {
	}
	
	if (chartType.indexOf("Bar2D") > -1 ) {
		c3chart.data.type = "bar";
		c3chart.axis.rotated = "rotated";
	} else if (chartType.indexOf("Column2D") > -1) {
		c3chart.data.type = "bar";
	} else if (chartType == "Pie2D") {
		c3chart.data.type = "pie";
	} else if (chartType == "Doughnut2D") {
		c3chart.data.type = "donut";
	} else if (chartType.indexOf("Area") > -1 ) {
		c3chart.data.type = "area";
		///// Apply color fill 
		applyPlotFill (chartId + "_chart", chartStyle);
	} else if (chartType.indexOf("Line") > -1 ) {
		c3chart.data.type = "line";
	}else if (chartType.indexOf("Spline") > -1 ) {
		c3chart.data.type = "spline";
	}

	c3chart.grid = showGrid(c3chart);

	//c3chart.transition = {duration: 2000};
}
function getChartData (chartObj, chartStyle, c3chart) {

	c3chart.bindto = '#' + chartObj.id + '_chart';
	/// Title Props
	c3chart.title = getTitle(chartObj);

	//// perparing data for chart from scrdata or from user defined data 
	var chartDataModel = chartDetails[chartObj.chartType].dataModel;
	if (chartDataModel == "Series") { 
		prepareChartSS(chartObj, c3chart);
	} else if (chartDataModel == "Shape") {
		prepareChartPie(chartObj, chartStyle, c3chart);
	} else if (chartDataModel == "MS") {
		prepareChartMS(chartObj, chartStyle, c3chart);
	}
	
	////// JS hook function 
	if(chartObj.jsHook){
		c3chart.data.onclick = function (d, elem) {
			var param = "";
			if (chartDataModel == "Series") { 
				param = c3chart.axis.x.categories[d.x] + "," + d.value;
			} else if (chartDataModel == "Shape") {
				param = d.name + "," + d.value;
			} else if (chartDataModel == "MS") {
				param = d.name + "|" + c3chart.axis.x.categories[d.x] + "," + d.value;
			}
			
			window[chartObj.jsHook](param);
		}
	}
	//if (chartObj.jsHook) {
		if (chartObj.events) {
			var chartParam="";
			var chartEvent = JSON.parse(chartObj.events);
			function returnParam(d,elm){
				var param = {};
				if (chartDataModel == "Series") { 
					//param = c3chart.axis.x.categories[d.x] + "," + d.value;
					param.value = c3chart.axis.x.categories[d.x] + "," + d.value;
					param.data= c3chart.data;
					param.chart = c3chart;
					param.chartInstance = c3chart.chartInstance;
				} else if (chartDataModel == "Shape") {
					//param = d.name + "," + d.value;
					param.value =  d.name + "," + d.value;
					param.data= c3chart.data;
					param.chart = c3chart;
					param.chartInstance = c3chart.chartInstance;
				} else if (chartDataModel == "MS") {
					//param = d.name + "|" + c3chart.axis.x.categories[d.x] + "," + d.value;
					param.value = d.name + "|" + c3chart.axis.x.categories[d.x] + "," + d.value;
					param.data= c3chart.data;
					param.chart = c3chart;
					param.chartInstance = c3chart.chartInstance;
				}
				return param;
			}
			if(chartEvent["ONCLICK"]){
				c3chart.data.onclick=function(d,elm){
				    chartParam = returnParam(d,elm);
					eval(chartEvent["ONCLICK"])(chartParam);
				}
			}
			if(chartEvent["ONMOUSEOVER"]){
				c3chart.data.onmouseover=function(d,elm){
				    chartParam = returnParam(d,elm);
					eval(chartEvent["ONMOUSEOVER"](chartParam));
				}
			}
			if(chartEvent["ONMOUSEOUT"]){
				c3chart.data.onmouseout=function(d,elm){
				    chartParam = returnParam(d,elm);
					eval(chartEvent["ONMOUSEOUT"])(chartParam);
				}
			}
		/*c3chart.data.onmouseout = function (d, elem) {
			console.log("mouseout");
		}
		c3chart.data.onmouseover = function (d, elem) {
			console.log("onmouseover");
		}
		c3chart.data.onmouseover = function (d, elem) {
			console.log("onmouseover");
		}*/
	}	
	if(chartObj.cbevents){
		var cbEvents = JSON.parse(chartObj.cbevents);
		//var c3ChartData = c3chart.data;
		c3ChartData = {data:c3chart.data,chart:c3chart}
		if(cbEvents["ONINIT"]){
			c3chart.oninit = function(){
				eval(cbEvents["ONINIT"])(c3ChartData);
			}
		}
		if(cbEvents["ONRENDERED"]){
			c3chart.onrendered = function(){
				eval(cbEvents["ONRENDERED"])(c3ChartData);
			}
		}
		if(cbEvents["ONMOUSEOVER"]){
			c3chart.onmouseover = function(){
				eval(cbEvents["ONMOUSEOVER"])(c3ChartData);
			}
		}
		if(cbEvents["ONMOUSEOUT"]){
			c3chart.onmouseout = function(){
				eval(cbEvents["ONMOUSEOUT"])(c3ChartData);
			}
		}
		if(cbEvents["ONRESIZE"]){
			c3chart.onresize = function(){
				eval(cbEvents["ONRESIZE"])(c3ChartData);
			}
		}
		if(cbEvents["ONRESIZED"]){
			c3chart.onresized = function(){
				eval(cbEvents["ONRESIZED"])(c3ChartData);
			}
		}
	}
	/*c3chart.onrendered = function(){
		console.log("i am rendered");
	}
	c3chart.oninit = function(){
		console.log("i am inside oninit");
	}
	c3chart.onmouseover = function(){
		console.log("i am inside onmouseover");
	}
	c3chart.onmouseout = function(){
		console.log("i am inside onmouseout");
	}
	c3chart.onresize = function(){
		console.log("i am inside onresize");
	}
	c3chart.onresized = function(){
		console.log("i am inside onresized");
	}*/
	/// other properties after data prep
	c3chart.tooltip = getToolTip(chartStyle, c3chart); ///// not done complete
	updateShowValue(c3chart, chartObj, chartStyle);
	applyChartProperties (c3chart, chartStyle);
	updateAxisNames (chartObj, c3chart);
}

function showGrid(c3chart){
	/*c3chart.grid= {
        x: {
            show: true
        },
        y: {
            show: true
        }
    }*/
    var grid= {
        x: {
            show: true
        },
        y: {
            show: true
        }
    }
    //c3chart.grid_x_show= true;
	return grid;
}
function applyChartProperties (c3chart, chartStyle) {
	//// padding props
	var charttopmargin = chartStyle.chart.charttopmargin;
	var chartrightmargin = chartStyle.chart.chartrightmargin;
	var chartbottommargin = chartStyle.chart.chartbottommargin;
	var chartleftmargin = chartStyle.chart.chartleftmargin;

	var padding = {};
	if (charttopmargin) {
		padding.top = charttopmargin;
	}
	if (chartrightmargin) {
		padding.right = chartrightmargin;
	}
	if (chartbottommargin) {
		padding.bottom = chartbottommargin;
	}
	if (chartleftmargin) {
		padding.left = chartleftmargin;
	}
	
	c3chart.padding = padding;
	
	//// show/hide axis values and label
	var showyval = chartStyle.chart.showyaxisvalues ? chartStyle.chart.showyaxisvalues : 1;
	if (showyval == 1) {
		c3chart.axis.y.show = true;
	}
	var showxval = chartStyle.chart.showlabels ? chartStyle.chart.showlabels : 1;
	if (showxval == 1) {
		c3chart.axis.x.show = true;
	}
}
function prepareChartSS (chartObj, c3chart) {
	chartObj.xFilter = new Array();
	chartObj.yFilter = new Array();
	chartObj.xGroupBy = new Array();
	var data = new Array();
	var xArr = new Array();
	data = prepareDataset(chartObj);
	xArr = data[0].data;
	var xLen = xArr.length;
	if (xLen > 0) {
		
		///// prepare categories and data set for c3 chart
		c3chart.data.columns[0] = [];
		for (var i = 0; i < xLen; i++) {
			c3chart.data.columns[0][i] = xArr[i].val;
			c3chart.axis.x.categories[i] = xArr[i].type;
		}
		c3chart.data.columns[0].unshift('data1')
	}
	return xArr;
}
function prepareChartMS (chartObj, chartStyle, c3chart) {
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
      var isXChildOfZ = isChild(chartObj.xAxisNode, chartObj.zAxisNode);
      var isYChildOfZ = isChild(chartObj.yAxisNode, chartObj.zAxisNode);
      var isYChildOfX = isChild(chartObj.yAxisNode, chartObj.xAxisNode);
      var isZChildOfX = isChild(chartObj.zAxisNode, chartObj.xAxisNode);
      ////Basic Chart Data
      /* if (chartObj.chartType == "MSColumn3DLineDY" || chartObj.chartType == "MSCombiDY2D") {
         chartObj.chartData.chart.PYAxisName = chartObj.chartYTitle;
      } else {
         chartObj.chartData.chart.yaxisname = chartObj.chartYTitle;
      } */
      //////Categories
/*       chartObj.chartData.categories = new Array();
      chartObj.chartData.categories[0] = {};
      chartObj.chartData.categories[0].category = new Array(); */
      ////Cases
      if (isXChildOfZ) {
         ////X is a Child of Z
         zRecs = apz.data.getNoOfRecs(chartObj.zAxisNode);
         for (var z = 0; z < zRecs; z++) {
            dataJsonPointer = apz.data.getDataPointer(chartObj.zAxisNode, z);
            valStr = getVal(dataJsonPointer, chartObj.zAxisElement, chartObj.zDataType);
            if (chartObj.zAxisFunction == "DISTINCT") {
               arrIndex = getArrayIndex(zDistArr, valStr);
               if (arrIndex == -1) {
                  zLen = zArr.length;
                  arrIndex = zLen;
                  zArr[arrIndex] = {};
                  zArr[arrIndex].z = valStr;
                  zArr[arrIndex].xArr = new Array();
                  addToDistArray(zDistArr, valStr, arrIndex);
               }
            } else {
               zLen = zArr.length;
               arrIndex = zLen;
               zArr[arrIndex] = {};
               zArr[arrIndex].z = valStr;
               zArr[arrIndex].xArr = new Array();
               addToDistArray(zDistArr, valStr, arrIndex);
            }
            //SEt Z Current Record..
            serCurrRec(chartObj.zAxisNode, z);
            //Get X Records
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            data = prepareDataset(chartObj);
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
                     categIndex = getArrayIndex(categDistArr, categ);
                     if (categIndex == -1) {
                        categIndex = chartObj.chartData.categories[0].category.length;
                        chartObj.chartData.categories[0].category[categIndex] = {};
                        chartObj.chartData.categories[0].category[categIndex].label = categ;
                        //categDistArr[categ] = categIndex;
                        addToDistArray(categDistArr, categ, categIndex);
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
         xRecs = apz.data.getNoOfRecs(chartObj.xAxisNode);
         for (var x = 0; x < xRecs; x++) {
            dataJsonPointer = apz.data.getDataPointer(chartObj.xAxisNode, x);
            data = dataJsonPointer;
            valStr = getVal(data, chartObj.xAxisElement, chartObj.xDataType);
            if (chartObj.xAxisFunction == "DISTINCT") {
               arrIndex = getArrayIndex(xDistArr, valStr);
               if (arrIndex == -1) {
                  xLen = xArr.length;
                  arrIndex = xLen;
                  xArr[arrIndex] = {};
                  xArr[arrIndex].x = valStr;
                  xArr[arrIndex].zarr = new Array();
                  addToDistArray(xDistArr, valStr, arrIndex);
               }
            } else {
               xLen = xArr.length;
               arrIndex = xLen;
               xArr[arrIndex] = {};
               xArr[arrIndex].x = valStr;
               xArr[arrIndex].zarr = new Array();
               addToDistArray(xDistArr, valStr, arrIndex);
            }
            //Set X Current Record..
            serCurrRec(chartObj.xAxisNode, x);
            //Get Z Records
            chartObj.xAxisNode = chartObj.zAxisNode;
            chartObj.xAxisElement = chartObj.zAxisElement;
            chartObj.xDataType = chartObj.zDataType;
            chartObj.xAxisFunction = chartObj.zAxisFunction;
            chartObj.xFilter = new Array();
            chartObj.xGroupBy = new Array();
            chartObj.yFilter = new Array();
            chartObj.zGroupBy = new Array();
            data = prepareDataset(chartObj);
            xArr[arrIndex].zarr = data[0].data;
         }
         xLen = xArr.length;
         if (xLen > 0) {
            chartObj.chartData.dataSet = new Array();
            for (var x = 0; x < xLen; x++) {
               categ = xArr[x].x;
               categIndex = getArrayIndex(categDistArr, categ);
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
                     seriesIndex = getArrayIndex(seriesDistArr, series);
                     if (seriesIndex == -1) {
                        seriesIndex = chartObj.chartData.dataSet.length;
                        chartObj.chartData.dataSet[seriesIndex] = {};
                        chartObj.chartData.dataSet[seriesIndex].seriesName = series;
                        chartObj.chartData.dataSet[seriesIndex].data = new Array();
                        addToDistArray(seriesDistArr, series, seriesIndex);
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
         data = prepareDataset(chartObj);
         zLen = data.length;
		 c3chart.data.groups = [];
		 c3chart.data.groups[0] = [];
		c3chart.data.colors = {};
			var chartColor = chartStyle.chart.palettecolors;
			if (chartColor) {
				chartColor = chartColor.split(",");
		}
         if (zLen > 0) {
			 for (var z = 0; z < zLen; z++) {
				c3chart.data.columns[z] = [];
				var seriesName = data[z].id;
				xLen = data[z].data.length;
				for (var x = 0; x < xLen; x++) {
					c3chart.data.columns[z][x] = data[z].data[x].val;
					c3chart.axis.x.categories[x] = data[z].data[x].type;
				}
				c3chart.data.columns[z].unshift(seriesName);
				//c3chart.data.groups[0][z] = seriesName;
				if (chartColor) {
					c3chart.data.colors[seriesName] = "#" + getColorForIndex(z, chartColor);
				}
			 }
         }
      }
      if (chartObj.chartType == "MSStackedColumn2DLineDY") {
            this.convertToDualY(chartObj);
         
      }
      return zArr;
}
function getColorForIndex (index, arr) {
	if (arr[index]) {
		return arr[index];
	} else {
		return getColorForIndex(index - arr.length, arr);
	}
}
function prepareChartPie (chartObj, chartStyle, c3chart) {
	chartObj.xFilter = new Array();
	chartObj.yFilter = new Array();
	chartObj.xGroupBy = new Array();
	var data = new Array();
	var xArr = new Array();
	data = prepareDataset(chartObj);
	xArr = data[0].data;
	var xLen = xArr.length;
	if (xLen > 0) {
		c3chart.data.colors = {};
		var count = 0; 
		var zeroValues = chartStyle.chart.showzeropies ? chartStyle.chart.showzeropies : 0;
		///// prepare categories and data set for c3 chart
		for (var i = 0; i < xLen; i++) {
			if (zeroValues == 1 && xArr[i].val == 0) {
				continue;
			}
			c3chart.data.columns[count] = [];
			c3chart.data.columns[count][0] = xArr[i].type;
			c3chart.data.columns[count][1] = xArr[i].val;
			///// add color from here for data set 
			c3chart.data.colors[xArr[i].type] = getRandomColor(getNewColorCode(), {index:count});
			count++;
		}
	}
	return xArr;
}
function renderChart (c3chart) {
	var chart = c3.generate(c3chart);
	c3chart.chartInstance = chart;
}
function applyChartStyle (chartStyle, chartId) {
	initPalette(chartStyle);
	addBackgroundColor(chartStyle, chartId);
	applyPlotColor(chartStyle, chartId);
	applyVisualProps(chartStyle, chartId);
	applyCanvasChanges(chartStyle, chartId);
}
function applyCosmeticChartStyle (chartId, chartType) {
	applyOtherVisualProperties(chartId, chartType);
}
function getTitle (chartObj) {
	var caption = apz.getLabel(chartObj.caption);
	return caption ? {text : caption, position : 'center'} : {};
}
function getLegend (chartStyle, c3chart) {
	c3chart.legend = {};
	c3chart.legend.show = chartStyle.chart.showlegend == "1" ? true : false;
	/// Apply legend style as well 
	
}
function getGrid () {
	var grid = {};
	grid.focus = {};
	grid.focus.show = false;
	return grid;
}
function getToolTip (chartStyle, c3chart) {
	var tooltipObj = {show: false};
	var chart = chartStyle.chart;
	var chartDataModel = chartDetails[chartStyle.chartType].dataModel;
	var tooltip = chart.showtooltip;
	tooltip = tooltip == null ? true : (tooltip == 1 ? true : false);
	if (tooltip) { 
		var bgcolor = getNull(chart.tooltipbgcolor) ? "#fff" : "#" + chart.tooltipbgcolor;
		var tooltipborder = getNull(chart.tooltipbordercolor) ? "#000" : "#" + chart.tooltipbordercolor;
		var tooltipshadow = getNull(chart.showtooltipshadow) ? chart.showtooltipshadow : 0;
		tooltipshadow = tooltipshadow == 1 ? 'box-shadow: 1px 1px 2px ' + tooltipborder + ';' : '';
		
		var contents = function (d, defaultTitleFormat, defaultValueFormat, color) {
			/// to check for ms stacked and other multi axis values 
			var data = "";
			if (chartDataModel == "MS") {
				var len = d.length;
				data = c3chart.axis.x.categories[d[0].x];
				for (var i = 0 ; i < len ; i++) {
					data += "<br>";
					data += d[i].id + ", " + getProperValue(d[i].value);
				}
			} else if (chartDataModel == "Series") {
				data = c3chart.axis.x.categories[d[0].x] + ", "	+ getProperValue(d[0].value);
			} else if (chartDataModel == "Shape") {
				data = d[0].name + ", " + (d[0].ratio * 100).toFixed(2) + '%';
			}
			return '<div style="border:1px groove ' + tooltipborder + '; margin: 15px 15px; padding: 1px; background-color:' + bgcolor 
				+ '; color:#000;' + tooltipshadow + '">'
				+ data + '</div>'  
		};
		tooltipObj.format = {
	/* 		title: function (x, index) { debugger; return 'Data ' + x; },
			name: function (name, ratio, id, index) { debugger; return name; },
			value: function (value, ratio, id, index) {debugger; return ratio; } */
		};
		tooltipObj.show = tooltip;
		tooltipObj.grouped = true;
		tooltipObj.contents = contents;
	}
	return tooltipObj;
}
function updateShowValue (c3chart, chartObj, chartStyle) {
	var showVal = chartStyle.chart.showvalues ? chartStyle.chart.showvalues : 1;
	var showLabel = chartStyle.chart.showlabels ? chartStyle.chart.showlabels : 1;
	var showpercentvalues = chartStyle.chart.showpercentvalues ? chartStyle.chart.showpercentvalues : 0;
	showpercentvalues = showpercentvalues == 0 ? false : true;
	var chartDataModel = chartDetails[chartObj.chartType].dataModel;
	if (chartDataModel == "Series" || chartDataModel == "MS") { 
		c3chart.data.labels = {};
		if (showVal == 1) {
			c3chart.data.labels.format = function (value, id, i, j) {return getProperValue(value);};
		}
		applyLabelProps (chartStyle, '#' + chartObj.id + "_chart .c3-chart-texts .c3-text");
	} else if (chartDataModel == "Shape") {
		c3chart.pie = {};
		c3chart.pie.label = {};
		c3chart.pie.padAngle = .01;
		c3chart.pie.label.threshold = 0.05;
		//c3chart.pie.expand = false;
		c3chart.donut = {};
		c3chart.donut.label = {};
		c3chart.donut.padAngle = .01;
		c3chart.donut.label.threshold = 0.05;
		//c3chart.donut.expand = false;
		if (showLabel == 0 && showVal == 0) {
			c3chart.pie.label.show = false;
			c3chart.donut.label.show = false;
		} else {
			c3chart.pie.label.format = function(value, ratio, id) {
				var label = "";
				if (showLabel == 1) {
					label = id;
				}
				if (showVal == 1) {
					if (showLabel == 1) { 
						label += ", ";
					}
					label += showpercentvalues ? (ratio * 100).toFixed(2) + "%" : getProperValue(value);
				}
				return label;
			};
			//// find a logic to identify this and remove similar code			
			c3chart.donut.label.format = function(value, ratio, id) {
				var label = "";
				if (showLabel == 1) {
					label = id;
				}
				if (showVal == 1) {
					if (showLabel == 1) { 
						label += ", ";
					}
					label += showpercentvalues ? (ratio * 100).toFixed(2) + "%" : getProperValue(value);
				}
				return label;
			};
		}
		applyLabelProps (chartStyle, '#' + chartObj.id + '_chart .c3-chart-arc.c3-target text ');
	}
}
function addPadding (c3chart, side) {
	if (c3chart.padding[side]) {
		c3chart.padding[side] += 10;
	} else {
		c3chart.padding[side] = 10;
	}
}
function updateAxisNames (chartObj, c3chart) {
	var xaxisname = this.apz.getLabel(chartObj.chartXTitle);
	var yaxisname = this.apz.getLabel(chartObj.chartYTitle);
	c3chart.axis.x.tick = {};
	c3chart.axis.x.tick.centered = true;
	c3chart.axis.x.tick.culling = false;
	c3chart.axis.x.tick.multiline = true;
	c3chart.axis.x.tick.multilineMax = 2;
	c3chart.axis.y.tick = {};
	
	
	////// function to divide the axis number by 1000 for better clarity as C3 is showing all the numbers
	c3chart.axis.y.tick.format = function (value) { return getProperValue(value); }
	//c3chart.axis.y.tick.count = 2;
	if (xaxisname) {
		c3chart.axis.x.label = {};
		c3chart.axis.x.label.text = xaxisname;
		c3chart.axis.x.label.class = 'c3-texts';
		///// Apply classes differently for bar chart
		c3chart.axis.x.label.position = chartObj.chartType.indexOf("Bar") > -1 ? "outer-middle" : "outer-center";
		chartObj.chartType.indexOf("Bar") > -1 ? '' : addPadding(c3chart, 'bottom');
	}
	if (yaxisname) {
		c3chart.axis.y.label = {};
		c3chart.axis.y.label.text = yaxisname;
		///// Apply classes differently for bar chart
		c3chart.axis.y.label.position = cchartObj.chartType.indexOf("Bar") > -1 ? "outer-center" : "outer-middle";
		chartObj.chartType.indexOf("Bar") > -1 ? addPadding(c3chart, 'bottom') : '';
	}
}

////// Additional hooks function 
function getProperValue (value) {
	var lval = value + "";
	switch (lval.length) {
		case 4:
		case 5:
		case 6:
			return parseFloat((value/1000).toFixed(2)) + "K";
			break;
		case 7:
		case 8:
		case 9: 
			return parseFloat((value/1000000).toFixed(2)) + "M";
			break;
		case 10:
		case 11:
		case 12:
			lval = parseFloat((value/1000000000).toFixed(2)) + "B";
			break;
		case 13:
		case 14:
		case 15:
			lval = parseFloat((value/1000000000000).toFixed(2)) + "T";
			break;
		case 16:
		case 17:
		case 18:
			lval = parseFloat((value/1000000000000000).toFixed(2)) + "Z";
			break;
	}
	return lval;
}
/// Additional featuees for C3
function additionalFeatures (chartObj, c3chart) {
	if (chartObj.zoomchart && chartObj.zoomchart == "Y") {
		c3chart.zoom = {};
		c3chart.zoom.enabled = true;
		c3chart.zoom.type = 'drag';
	}
	if (chartObj.subchart && chartObj.subchart == "Y") {
		c3chart.subchart = {};
		c3chart.subchart.show = true;
		///// check to hide sub chart axis value 
		var subVal = chartObj.subvalue ? chartObj.subvalue : 6;
		c3chart.axis.x.extent = [-1, subVal];
	}
}
function getNull (content) {
	if (content == null || content == "undefined" || content == "" || content == "null" || content == undefined) {
		return true;
	}
	return false;
}
function addCenterText (id, text1, text2) {
	var label = d3.select('#' + id + '_chart text.c3-chart-arcs-title');
	label.html(''); 
	label.insert('tspan').text(text1).attr('dy', 0).attr('x', 0).attr('class','big-font');
	label.insert('tspan').text(text2).attr('dy', 20).attr('x', 0);
}

/* 
c3chart.data.onmouseover = function (d) {}
c3chart.data.onmouseout = function (d) {}
c3chart.legend.item.onmouseover = function (id) {}
c3chart.legend.item.onmouseout = function (id) {}
c3chart.onmouseover = function () {}
c3chart.onmouseout = function () {}
*/
