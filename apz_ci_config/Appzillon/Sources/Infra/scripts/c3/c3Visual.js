/*const hexToRgb = hex => hex.replace(/^#?([a-f\d])([a-f\d])([a-f\d])$/i,(m, r, g, b) => '#' + r + r + g + g + b + b)
	.substring(1)
	.match(/.{2}/g)
	.map(x => parseInt(x, 16));*/
//Deep changes
var hexToRgb = function(hex){
	return  hex.replace(/^#?([a-f\d])([a-f\d])([a-f\d])$/i,function(m, r, g, b){'#' + r + r + g + g + b + b})
	.substring(1)
	.match(/.{2}/g)
	.map(function(x){return parseInt(x, 16)});
}
var colorpatterndefault = ['#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5', '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5'];
var colorpattern;
var patterncount = 0;
function addStyleStringOne(str) {
	var c3NewStyle = document.createElement('style');
	c3NewStyle.setAttribute('type', 'text/css');
	c3NewStyle.innerHTML = str;
	document.getElementsByTagName("head")[0].appendChild(c3NewStyle);
}
function addStyleString(str) {
	var c3NewStyle = document.getElementById('c3New');
	if (c3NewStyle == null) {
		c3NewStyle = document.createElement('style');
		c3NewStyle.id = "c3New";
		c3NewStyle.setAttribute('type', 'text/css');
		c3NewStyle.innerHTML = str;
	} else {
		c3NewStyle.innerHTML += ('\n' + str) 
	}
	document.getElementsByTagName("head")[0].appendChild(c3NewStyle);
}
function removeStyleSheet () {
	var c3NewStyle = document.getElementById('c3New');
	if (c3NewStyle != null) {
		c3NewStyle.remove();
	}
}
function initPalette (chartStyle) {
	///// delete old style sheet for better visualization
	//Pradeep changes for adding multiple charts with different style(commented removeStylesheet() function)
	//removeStyleSheet();
	///// may happen to override other chart style in case screen has multiple charts 
 	var palette = chartStyle.chart.palettecolors;
	if (palette) {
		colorpattern = palette.split(',');
		for (var i = 0; i < colorpattern.length ; i++) {
			colorpattern[i] = "#" + colorpattern[i];
		}
		patterncount = colorpattern.length;
	} else {
		colorpattern = colorpatterndefault;
	}
}
function getRandomColor(color, d) {
	if (d.index > -1) {
		var newcolor = "";
		if (patterncount > 0) {
			var rem = d.index % patterncount;
			newcolor = colorpattern[rem];
		} else { 
			///Color for random .. 
			newcolor = getNewColorCode();
		}
		return colorpattern[d.index] || (colorpattern[d.index] = newcolor); 
	}
	return color;
}
function getNewColorCode () {
	var letters = '0123456789ABCDEF';
	newcolor = '#';
	for (var i = 0; i < 6; i++) {
		newcolor += letters[Math.floor(Math.random() * 16)];
	}
}
function addBackgroundColor (chartStyle, chartId) {
	//var chartbgcolor = chartStyle.chart.bgcolor ? chartStyle.chart.bgcolor : 'DCDCDC';
	var chartbgcolor = chartStyle.chart.bgcolor ? chartStyle.chart.bgcolor : '';
	var bgalpha = chartStyle.chart.bgalpha ? chartStyle.chart.bgalpha.split(',')[0] : 0;
	chartbgcolor = chartbgcolor ? chartbgcolor : 'fff';
	
	bgalpha = bgalpha ? bgalpha/100 : 1;
	if (chartbgcolor) {
		chartbgcolor = "#" + chartbgcolor;
		chartbgcolor = hexToRgb(chartbgcolor);
		chartbgcolor = chartbgcolor[0] + ',' + chartbgcolor[1] + ',' + chartbgcolor[2] + ',' + bgalpha;
	}
	addStyleString('#' + chartId + '_chart.chrt-crt svg{\n background-color: rgba(' + chartbgcolor + ');\n}');
	
	//// Add border color
	///if (chartStyle.chart.showborder == 1) {
		var bordercolor = chartStyle.chart.bordercolor;
		var borderthickness = chartStyle.chart.borderthickness;
		var borderalpha = chartStyle.chart.borderalpha;
		//bordercolor = bordercolor ? bordercolor : '000';
		bordercolor = bordercolor ? bordercolor : '';
		borderthickness = borderthickness ? borderthickness : 1;
		borderalpha = borderalpha ? borderalpha/100 : 1;
		if (bordercolor) {
			bordercolor = "#" + bordercolor;
			bordercolor = hexToRgb(bordercolor);
			bordercolor = bordercolor[0] + ',' + bordercolor[1] + ',' + bordercolor[2] + ',' + borderalpha;
		}

		addStyleString('#' + chartId + '_chart svg{\n border: ' + borderthickness + 
			'px groove rgba(' +  bordercolor + ')\n}');
	//}
}
function applyPlotFill (chartId, chartStyle) {
	var plotfillcolor = chartStyle.chart.plotfillcolor;
	if (plotfillcolor) {
		plotfillcolor = "#" + plotfillcolor;
		plotfillcolor = hexToRgb(plotfillcolor);
		plotfillcolor = plotfillcolor[0] + ',' + plotfillcolor[1] + ',' + plotfillcolor[2];
		
		var style = ".c3-area { \n";
		style += "fill: rgb(" + plotfillcolor + ") !important; \n";
		style += "}";
		addStyleString('#' + chartId + ' ' + style);
	}
}
function applyPlotColor (chartStyle, chartId) {
	////// Apply color to chart
	var showplotborder = chartStyle.chart.showplotborder ? chartStyle.chart.showplotborder : 0;
	if (showplotborder == 1) {
		var plotbordercolor = "#" + (chartStyle.chart.plotbordercolor ? chartStyle.chart.plotbordercolor : 'fff');
		var plotborderthickness = chartStyle.chart.plotborderthickness ? chartStyle.chart.plotborderthickness : 1;
		var opacity = 1 - ((chartStyle.chart.plotborderalpha ? chartStyle.chart.plotborderalpha : 100) / 100);
		
		//// get chart type and apply it accordingly
		var style = "";
		
		var chartDataModel = chartDetails[chartStyle.chartType].dataModel;
		if (chartDataModel == "Shape") {
			style = '.c3-chart-arc path {\n'
		} else if (chartDataModel == "Series" || chartDataModel == "MS") {
			style = '.c3-bar {\n';
		}
		
		style += (plotbordercolor ? ' stroke: ' + plotbordercolor + ' !important;' : '');
		style += (opacity ? '\n stroke-opacity: ' + opacity + ' !important;' : '');
		style += (plotborderthickness ? '\n stroke-width: ' + plotborderthickness +'px !important;\n}': '');
		
		
		
		addStyleString('#' + chartId + '_chart ' + style);	
	}
}
function applyOtherVisualProperties	(chartId, chartType) {
	/* if (chartType == "Bar2D") {
		//// set it to hide tick in bar chart
		addStyleString('#' + chartId + '_chart .tick line {\n display: none;\n} \n.domain {\n display: none;\n}');
	} */
	addStyleString('#' + chartId + '_chart .tick line {\n display: none;\n} \n.domain {\n display: none;\n}');
}
function getStyleSheet (chartStyle, name) {
	if (!chartStyle.styles) 
		return {};
	var definition = chartStyle.styles.definition;
	var style = {};
	for (var i = 0 ; i < definition.length ; i++) {
		if (definition[i]['name'] == name) {
			style = definition[i];
			break;
		}
	}
	return style;
}
function applyLabelProps (chartStyle, ruleId) {
	applyTextProperties(chartStyle, ruleId, 'namedatalabels');
}
function applyVisualProps(chartStyle, chartId) {
	applyTextProperties(chartStyle, '#' + chartId + '_chart .c3-title', 'namecaption');
	applyTextProperties(chartStyle, '#' + chartId + '_chart .c3-axis-x-label', 'namexaxisname');
	applyTextProperties(chartStyle, '#' + chartId + '_chart .c3-axis-y-label', 'nameyaxisname');
	applyTextProperties(chartStyle, '#' + chartId + '_chart .c3-axis-x .tick tspan', 'namedatavalues');
	applyTextProperties(chartStyle, '#' + chartId + '_chart .c3-axis-y .tick tspan', 'nameyaxisvalues');
}
function applyTextProperties (chartStyle, ruleName, definition) {
	var style = getStyleSheet(chartStyle, definition);
	addStyleString(ruleName + ' {\n fill: #' 
		+ (style.color ? style.color : "000" ) + ' !important;' 
		+ '\n font-family: ' + (style.font ? style.font : "Arial") 
		+ ';\n text-decoration: ' + (style.underline == 1 ? 'underline' : 'none') 
		+ ';\n font-weight: ' + (style.bold == 1 ? 'bold' : 'normal') 
		+ ';\n font-size: ' + (style.size ? style.size + "px" : "10px") 
		+ ';\n font-style: ' + (style.italic == 1 ? 'italic' : 'normal') 
		+ ';\n}');
}
function applyCanvasChanges(chartStyle, chartId) {
	
	var canvasbgcolor = chartStyle.chart.canvasbgcolor;
	var canvasbgalpha = chartStyle.chart.canvasbgalpha;
	var canvasbgratio = chartStyle.chart.canvasbgratio;
	var canvasbgangle = chartStyle.chart.canvasbgangle;
	var canvasbordercolor = chartStyle.chart.canvasbordercolor;
	var canvasborderthickness = chartStyle.chart.canvasborderthickness;
	var canvasborderalpha = chartStyle.chart.canvasborderalpha;

	canvasbgalpha = canvasbgalpha ? canvasbgalpha/100 : 1;
	canvasborderalpha = canvasborderalpha ? canvasborderalpha/100 : 1;
	
	var chartDataModel = chartDetails[chartStyle.chartType].dataModel;
	if (chartDataModel == "Shape") {
		canvasbgalpha = 0;
		canvasborderalpha = 0;
	}
	
	var style = '#' + chartId + '_chart .c3-event-rects {\n fill: #' 
		+ (canvasbgcolor ? canvasbgcolor : 'fff') + ' !important' 
		+ ';\n stroke: #' + (canvasbordercolor ? canvasbordercolor : '000')
		+ ';\n stroke-opacity: ' + canvasborderalpha
		+ ';\n stroke-linejoin: revert'
		+ ';\n stroke-width: ' + (canvasborderthickness ? canvasborderthickness + 'px': '3px')	
		+ ';\n fill-opacity: ' + canvasbgalpha + '!important;\n}';
	
	addStyleString(style);
}