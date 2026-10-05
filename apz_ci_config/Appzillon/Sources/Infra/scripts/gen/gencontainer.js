Apz.GenContainer = function(apz) {
   this.apz = apz;
};
Apz.GenContainer.prototype = {
    genContainer: function(param, cntrData) {
        //// Generate defaults, grid to panelSection
        var genObj = param.genObj;
        var genUtils = param.genUtils;
        genUtils.updateCntrMetaData(param, cntrData);
        if(cntrData.widgettype == "TABLE"){
            this.genTable(param, cntrData);
        } else if(cntrData.widgettype == "FORM"){
            this.genForm(param, cntrData);
        } else if(cntrData.widgettype == "LIST" || cntrData.widgettype == "GRID"){
        	if (this.apz.isNull(cntrData.parentlist)) {
        		this.genList(param, cntrData);
        	} else {
				if (!param.templateDataObj)
				param.templateDataObj = {};
				if (!param.templateDataObj.containers)
				param.templateDataObj.containers = [];
        		param.templateDataObj.containers.push(cntrData);
        	}
        } else if(cntrData.widgettype == "CHART"){
            this.genChart(param, cntrData);
        } else if(cntrData.widgettype == "GAUGE"){
            this.genGauge(param, cntrData);
        } else if(cntrData.widgettype == "MENU"){
            this.genMenu(param, cntrData);
        } else if(cntrData.widgettype == "NAVBAR"){
        	this.genNavbar(param,cntrData);
        }
    },
    genForm: function(param, cntrData) {
        var props = cntrData;
        var genObj = param.genObj;
	    var genUtils = param.genUtils;
        var id = param.appId + this.apz.idSep + param.scr + this.apz.idSep + props.name;
        var cntrClsAttrs = genUtils.getCustomClass(cntrData);
        cntrClsAttrs += genUtils.getShowNoneClass(cntrData);
		var appearanceClass = !this.apz.isNull(cntrData.appearance) ? " " + cntrData.appearance.toLowerCase() : " pri";
		var vartnClass = !this.apz.isNull(cntrData.variation) ? " " + cntrData.variation.toLowerCase() : "";
		// var stateClass = !this.apz.isNull(cntrData.containerstate) ? " " + cntrData.containerstate : "";
		// var cssClass = !this.apz.isNull(cntrData.cssclasses) ? " " + cntrData.cssclasses : ""; 

        var paginationReqd = false;
        if (!this.apz.isNull(props.paginationrequired) && (props.paginationrequired=="Y")) {
        	var multiRec = "N";
        	var scrdef = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design];
        	if(scrdef && scrdef.containersMap[id]) {
        		multiRec = scrdef.containersMap[id].multiRec;
        	}
        	//var multiRec = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design].containersMap[id].multiRec;
        	if(multiRec == "Y"){
				paginationReqd = true;
			}
		}
		var layoutCls = " hor";
		if (cntrData.orientation=="VERTICAL" || cntrData.orientation=="VER") {
			layoutCls = " ver";
		}
        genObj.scrhtml += '<div id="' + id + '" class="crt-form '+layoutCls+appearanceClass+vartnClass+cntrClsAttrs+'" '+genUtils.getCntrEvents(cntrData)+'>';
        //genObj.scrhtml += '<div id="' + id + '" class="crt-form '+layoutCls+appearanceClass+vartnClass+cntrClsAttrs+'">';
        var uiDescription = cntrData.uidescription;
        var uiDescArr = [];
        if(!this.apz.isNull(uiDescription)) {
        	uiDescArr = uiDescription.split("|");
        }
        var isToolTip = false;
        var toolTipClass = "";
        if(!this.apz.isNull(uiDescArr[2]) && uiDescArr[2] == "Tooltip") {
        	toolTipClass = "sno";
        }
        if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] == "Top"){
        	genObj.scrhtml += '<span id="' + id + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
        if (!this.apz.isNull(props.title) || !this.apz.isNull(props.icon) || paginationReqd) {
			genObj.scrhtml += '<ul id="' + id + '_title" class="ttl">';
			this.getContainerTitle(param, cntrData);
			if (paginationReqd) {
				var containerState = props.containerstate;
				var pagination = props.paginationrequired;
				genObj.scrhtml += '<li class="pgn-ctr">';
				genUtils.getPrevNextRecButtons(param, cntrData);
				if (containerState!="READONLY" || containerState!="RO") {
					genUtils.genAddDelRecButtons(param, cntrData);
				}
				genObj.scrhtml += "</li>";
			}
			genObj.scrhtml += "</ul>";
		}
		if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] != "Top"){
        	genObj.scrhtml += '<span id="' + id + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
        if (cntrData.childs.length>0) {
        	for (var b=0; b<cntrData.childs.length; b++) {
        		var childObj = cntrData.childs[b];
        		childObj.cntrData = childObj.parent = cntrData;
        		childObj.rowNo = -1;
        		this.genSecRow(param, childObj);
        	}
        }
        genObj.scrhtml += '</div>';
    },
    genList: function(param,cntrData) {
        var genObj = param.genObj;
        var containerData = cntrData;
	    var genUtils = param.genUtils;
        var containerClsAttr = genUtils.getListClassAttributes(containerData);
		var id = param.appId + "__" + param.scr + "__" + containerData.name;
        var pageSize = containerData.pagesize;
        var multiRecord = "N";
        var scrdef = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design];
        if(scrdef && scrdef.containersMap[id]) {
        	multiRecord = scrdef.containersMap[id].multiRec;
        } 
        //var multiRecord = "N";
		var noOfRows = 0;
		var variationCls = !this.apz.isNull(containerData.variation) ? " " + containerData.variation.toLowerCase() : "";
		var cssClass = genUtils.getCustomClass(cntrData);
		var containerId = "id='" + id + "' ";
		var containerClass = "class='crt-list" + containerClsAttr + "' ";
		var titleClass = "class='ttl' ";
		var pageSizeAttr = "";
		var wrappedClass = ""; 
		//var targetOverlayId = param.appId+ "__" + param.scr + "__" + containerData.targetoverlayid;
		/*if (!this.apz.isNull(containerData.targetoverlayid)) {
			containerClass = "class='crt-list u-relative " + containerClsAttr + "' ";
		}*/
		var singleRec = false;
		if(multiRecord=="N"){
			singleRec = true;
		}
		var paginationReqd = false;
		if (containerData.paginationstyle=="PAGE1") {
			paginationReqd = true;
		}
		if (multiRecord=="N"){ 
			pageSize = "999";
			noOfRows = 1;
		} else {
			if (this.apz.isNull(pageSize)) {
				pageSize = "999";
				noOfRows = 1;
				if (singleRec) {
					pageSize = "1";
				}
			} else {
				noOfRows = parseInt(pageSize);
				if (pageSize=="999") {
					noOfRows = 1;
				}
			}
		}
		pageSizeAttr = "pagesize='" + pageSize + "'";
		genObj.scrhtml+= "<div " + containerId + containerClass + pageSizeAttr + ">";
		var uiDescription = cntrData.uidescription;
        var uiDescArr = [];
        if(!this.apz.isNull(uiDescription)) {
            uiDescArr = uiDescription.split("|");
        }
        var isToolTip = false;
        var toolTipClass = "";
        if(!this.apz.isNull(uiDescArr[2]) && uiDescArr[2] == "Tooltip") {
            toolTipClass = "sno";
        }
        if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] == "Top"){
            genObj.scrhtml += '<span id="' + id + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
		if (containerData.title!="" || containerData.icon!=""|| paginationReqd) {
			genObj.scrhtml+=  "<ul id='" + id + "_title' " + titleClass + ">";
			this.getContainerTitle(param,containerData);
			if (paginationReqd) {
				genObj.scrhtml+="<li class='pgn-ctr'>";
				if (containerData.dynamicpagesize=="Y" && containerData.paginationstyle=="PAGE1") {
					genUtils.genDynamicPageSize(param,cntrData);
				}
				genUtils.getPrevNextRecButtons(param,cntrData);
				genObj.scrhtml+="</li>";
			}
			genObj.scrhtml+= "</ul>";
		}
		if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] != "Top"){
            genObj.scrhtml += '<span id="' + id + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
		genObj.scrhtml+= "<ul class='" + containerData.appearance + variationCls + cssClass +"'>";
		/*if (!this.apz.isNull(containerData.targetoverlayid)) {
			genObj.scrhtml+= "<li id='" + id + "_dropdown' class='etb-slct ett-slct pri scb-col100 ' "
			+ "onclick='apz.toggleOverlay(this,&quot;" + targetOverlayId
			+ "&quot;,event);'><input class='sub-elt' placeholder='Select Option' readonly onkeyup='apz.toggleOverlay(this,&quot;" + targetOverlayId
			+ "&quot;,event);' type='text'><span class='sub-elt1 etw-5'><svg aria-hidden='true' role='presentation' class='ett-icon icon-down px34'><use xlink:href='#icon-down'></use></svg></span></li>";
		}*/
		var lnoofsecrows = containerData.childs.length;
		var hiddenClass = "";
		var loopCount = -1;
		var noOfContainers = param.templateDataObj ? param.templateDataObj.containers.length : 0;
		for (var r = 0; r < noOfRows; r++) {
			var lindex = r.toString();
			if (multiRecord=="Y") {
				//lindex = "{{$index}}";
				hiddenClass = "";
			} else {
				lindex = -1;
			}
			loopCount = r;
			if (r > 0) {
				hiddenClass = "sno";
			}
			for (var s = 0; s < lnoofsecrows; s++) {
				var scrrowdataobj = containerData.childs[s];
				var secRowClsAttr = "srb" + genUtils.getSectionRowClassAttributes(scrrowdataobj);
				/*if (!this.apz.isNull(containerData.targetoverlayid)) {
					secRowClsAttr += " sno";
				}*/
				if (r!=0) {
					secRowClsAttr += " sno ";
				}
				var childCls = "";
				for (var c = 0; c < noOfContainers; c++) {
					var containerDataObj = param.templateDataObj.containers[c];
					if (containerDataObj.parentlist==containerData.name) {
						childCls = " chl";
					}
				}
				wrappedClass = " wrapped";
				genObj.scrhtml+= "<li  id ='" + id + "_row_" + lindex + "' class='"
						+ secRowClsAttr + wrappedClass + childCls +"' rowno='" + lindex + "' ";
				// / Add Events
				genObj.scrhtml+= " " + this.getListEvents(param,cntrData);
				genObj.scrhtml+= ">";
				if (multiRecord=="Y") {
					scrrowdataobj.parent=cntrData
					scrrowdataobj.cntrData = cntrData;
					scrrowdataobj.rowNo = r;
					this.genSecRow(param,scrrowdataobj);
				} else {
					scrrowdataobj.parent=cntrData
					scrrowdataobj.cntrData = cntrData;
					scrrowdataobj.rowNo = -1;
					this.genSecRow(param,scrrowdataobj);
				}
				genObj.scrhtml+= "</li>";
			}
		}
		if (containerData.paginationstyle=="APPEND" && containerData.containerstate=="READONLY" || containerData.containerstate=="RO") {
			genObj.scrhtml+= "<span id='listfoot" + idSep + "_span' class='listfooter'>";
			genObj.scrhtml+="<span class='rft'>";
			genObj.scrhtml+= "<a id='" + id + "_loadmore_btn' class='loadmore' "
					+ genUtils.getPaginationEvent("onclick", id, "N", false) + " href='javascript:;''>Load More </a>";
			genObj.scrhtml+= "<a id='" + id + "_loadless_btn' class='loadless' "
					+ genUtils.getPaginationEvent("onclick", id, "P", false) + " href='javascript:;'> Load Less</a>";
			genObj.scrhtml+="</span>";
			genObj.scrhtml+= "</span>";
		}
		genObj.scrhtml+= "</ul>";
		if (containerData.paginationstyle=="PAGE2") {
			genUtils.genNumberedPaginationButtons(param,cntrData);
		} else if (containerData.paginationstyle=="PAGE3") {
			genUtils.genInfoandNumberedPagination(param,cntrData);
		}
		genObj.scrhtml+="</div>";
    },
    genTable: function(param,containerData) {
		var props = containerData;

        var genObj = param.genObj;
	    var genUtils = param.genUtils;
		var vartnClass = !this.apz.isNull(props.variation) ? " " + props.variation.toLowerCase() : "";
		var apprnceClass = !this.apz.isNull(props.appearance) ? " " + props.appearance.toLowerCase() : " pri";
		var id = param.appId + "__" + param.scr + "__" + props.name;
		var tableClass = genUtils.getTableClass(props);
		var titleReqd = true;
		var paginationReqd = false;
		var styleAttr = "";
		var pageSize = props.pagesize;
		var multiRec = "N";
        var scrdef = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design];
        if(scrdef && scrdef.containersMap[id]) {
        	multiRec = scrdef.containersMap[id].multiRec;
		}
		if(props.animation == "FDN"){
			props.animation = " fadeIn";
		} else if (props.animation == "FDNP"){
			props.animation = " fadeInUp";
		} else if(props.animation == "FDND"){
			props.animation = " fadeInDown";
		} else if(props.animation == "ZON"){
			props.animation = " zoomIn";
		}
		//var multiRec = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design].containersMap[id].multiRec;			   
		//var multiRec = "N";
		var searchable = props.searchable;
		var tableResp = props.responsive;
		var tableHt = props.tableheight;
		var uRelClass = " u-relative";
		var tableHeightClass = "";
		var noOfRows = 0;
		/// TBC ///
		//var noOfOverlays = templateDataObj.overlays.length;  /// overlay data
		//var noOfOverlays = param.overlays.length;
		///////
		var expandalbleRequired = false;
		var tabIndex = "";
		var snoCls = genUtils.getShowNoneClass(props);
		var cssCls = genUtils.getCustomClass(props);
		var containerClsAttr = snoCls + cssCls;
		if (props.nativetable!="Y") {
			if ((props.containerstate=="READONLY" || props.containerstate=="RO") && tableResp=="Y") {
				expandalbleRequired = true;
				containerClsAttr = containerClsAttr + " res";
			} else if (this.apz.isNull(tableHt)) {
				styleAttr = ' style="height:250px"';
			}
		}
		if (props.containerstate=="READONLY" || props.containerstate=="RO") {
			containerClsAttr = containerClsAttr + " readonly";
		}
		/*if (noOfOverlays > 0 && $.inArray(param.overlays,id)) {
			containerClsAttr = " liscont height-transition height-transition-hidden sno " + containerClsAttr + "";
			tabIndex = 'tabindex="-1"';
		}*/
		if (multiRec=="N") {
			pageSize = "999";
			noOfRows = 1;
		} else {
			if (this.apz.isNull(pageSize) || pageSize == "999") {
				pageSize = "999";
				noOfRows = 1;
			} else {
				if (pageSize.trim().indexOf(" RECORDS") != -1) {
					pageSize = pageSize.substring(0, pageSize.indexOf(" "));
				}
				noOfRows = parseInt(pageSize);
			}
		}
		if (!this.apz.isNull(props.paginationstyle) && (props.paginationstyle=="PAGE1")) {
			paginationReqd = true;
		}
		genObj.scrhtml += '<div id="' + id + '" class="crb-tabl crt-tabl' + containerClsAttr + apprnceClass + vartnClass + uRelClass + '">';
		var uiDescription = containerData.uidescription;
        var uiDescArr = [];
        if(!this.apz.isNull(uiDescription)) {
            uiDescArr = uiDescription.split("|");
        }
        var isToolTip = false;
        var toolTipClass = "";
        if(!this.apz.isNull(uiDescArr[2]) && uiDescArr[2] == "Tooltip") {
            toolTipClass = "sno";
        }
        if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] == "Top"){
            genObj.scrhtml += '<span id="' + id + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
		if ((titleReqd) || (paginationReqd)) {
			genObj.scrhtml += '<ul id="' + id + 'ul_ttl" class ="ttl">';
			if (titleReqd) {
				this.getContainerTitle(param, containerData);
			}
			if (searchable=="Y") {
				genObj.scrhtml += '<li id="' + id + '_li_search" class ="srh-ctr">';
				genObj.scrhtml += '<span id="' + id + '_span_search" class ="srh">';
				genObj.scrhtml += '<span><input id="' + id + '_input_search" class ="srh-inp" onkeyup="apz.searchRecords(&quot;' + id
						+ '&quot;,this.value)" onblur="(function(){apz.data.scrDataBackup={};})()" type="search" '+tabIndex+' placeholder="Search....."></span>';
				genObj.scrhtml += '<span class="srh-icn"><svg aria-hidden="true" role="presentation" class="icon icon-search px24"><use xlink:href="#icon-search"></use></svg></span>';
				genObj.scrhtml += "</span>";
				genObj.scrhtml += "</li>";
			}
			if (paginationReqd || (props.containerstate!="READONLY" || props.containerstate!="RO")) {
				genObj.scrhtml += '<li class="pgn-ctr">';
				if (paginationReqd) {
					genUtils.getPrevNextRecButtons(param,props);
				}
				if (props.containerstate!="READONLY" || props.containerstate!="RO") {
					genUtils.genAddDelRecButtons(param,props);
				}
				genObj.scrhtml += '</li>';
			}
			genObj.scrhtml += '</ul>';
		}
		if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] != "Top"){
            genObj.scrhtml += '<span id="' + id + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
		var rowEvent = 'onclick="apz.data.rowClicked(this,event);"';
		var expandableIcon = props.expandableicon;
		var collapsibleIcon = props.collapsibleicon;
		if (expandalbleRequired) {
			rowEvent = "";
		}
		var tableId = 'id="' + id + '_table"';
		var tblCtrClass = "tabl-ctr" + tableHeightClass;
		var tableHeightAttr = "250";
		if (!this.apz.isNull(tableHt) && !((props.containerstate=="READONLY" || props.containerstate=="RO") && tableResp=="Y") && props.nativetable!="Y") {
			styleAttr = ' style="height:'+tableHt+'px"';
			tableHeightAttr = tableHt;
		}
		if(props.hovereffect=="HOVER" && !((props.containerstate=="READONLY" || props.containerstate=="RO") && tableResp=="Y")) {
			tblCtrClass = tblCtrClass + " hvr";
		}
		genObj.scrhtml += '<div class ="'+tblCtrClass+'" '+styleAttr+'>';
		genObj.scrhtml += '<table ' + tableId + tableClass + genUtils.getCntrEvents(props) +' role="grid" aria-describedby="'+id+'_table_info"';
		if (props.nativetable!="Y") {
			genObj.scrhtml += ' data-tableheight ="' + tableHeightAttr + '"';
		}
		genObj.scrhtml += ">";
		genObj.scrhtml += "<thead>";
		genObj.scrhtml += '<tr role="row">';
		if (props.rowselectorrequired=="Y") {
			var selectorType = "CHECKBOX";
			var selectorName = props.name;
			var selectorClass = "group-checkable";
			if (props.rowselectortype=="RADIO") {
				selectorClass = "group-checkable sno";
				selectorType = "RADIO";
			}
			genObj.scrhtml += '<th id="' + id + '_0_th" class="cen" style="max-width:45px;" dtyp="Boolean" aria-controls aria-label >';
			genObj.scrhtml += '<span class="etb-chek ett-chek' + apprnceClass + '"><input id="' + id + '_0"  type="' + selectorType + '" name="' + selectorName + '" class="' + selectorClass
					+ '" onclick ="apz.toggleRowSelection(&quot;' + id + '_table' + '&quot;)">';
			genObj.scrhtml += '<label class="flb" for="' + id + '_0"></label></span><div class="fht-cell"></div></th>';
		}
		var advancedTabClass = "";
		if (this.apz.isNull(props.expandableicon) && this.apz.isNull(props.collapsibleicon)) {
			advancedTabClass = "dticon control dt-cssicon";
		} else {
			if (this.apz.isNull(props.expandableicon)) {
				expandableIcon = props.collapsibleicon;
			} else if (this.apz.isNull(props.collapsibleicon)) {
				collapsibleIcon = props.expandableicon;
			}
			advancedTabClass = "dticon control";
		}
		if (expandalbleRequired && (props.expandableposition=="LEFT" || props.expandableposition=="LFT")) {
			genObj.scrhtml += 
					'<th id="' + id + '_0__exp_th" class="' + advancedTabClass + '" style="width:8px;" cellminwidth="8px"  cellmaxwidth="8px" dtyp="Boolean" >';
			genObj.scrhtml += '<i class="icon"></i></th>';
		}
		var noOfSecRows = props.childs.length;
		for (var s = 0; s < noOfSecRows; s++) {
			var secRowData = props.childs[s];
			secRowData.cntrData = props;
			secRowData.parent=props
			secRowData.portion = "HEADER";
			secRowData.rowNo = 0;
			this.genSecRow(param,secRowData);
		}
		if (expandalbleRequired && (props.expandableposition!="LEFT" || props.expandableposition!="LFT")) {
			genObj.scrhtml += 
					'<th id="' + id + '_0__exp_th" class="' + advancedTabClass + '" style="width:8px;" cellminwidth="8px"  cellmaxwidth="8px" dtyp="Boolean">';
			genObj.scrhtml += '<i class="icon"></i></th>';
		}
		genObj.scrhtml += '</tr>';
		if(props.filtercolumns=="Y"){
			this.genFilterHeaders(param,props.childs,props);
		}
		genObj.scrhtml += '</thead>';
		genObj.scrhtml += '<tbody id="' + id + '_tbody">';
		for (var r = 0; r < noOfRows; r++) {
			var firstSecRow = props.childs[0];
			var index = r.toString();
			var displayClass = "";
			displayClass = "sno";
			var apprCls = !this.apz.isNull(firstSecRow.appearance) ? " "+firstSecRow.appearance.toLowerCase() : " pri";
			var varCls = !this.apz.isNull(firstSecRow.variation) ? " "+firstSecRow.variation.toLowerCase() : "";
			var rowCssCls = " "+firstSecRow.cssclasses;
			displayClass = displayClass + varCls + apprCls+rowCssCls;
			displayClass = 'class="' + displayClass + '" ';
			genObj.scrhtml += '<tr id="' + id + '_row_' + index + '" rowno="' + index + '" ' + displayClass + rowEvent + ' animationtype="'+props.animation+'" onclick ="apz.data.rowClicked(this,event)" role="row">';
			if (props.rowselectorrequired=="Y") {
				var selectorType = "CHECKBOX";
				var selectorName = props.name;
				genObj.scrhtml += '<td class="cen" style="max-width:45px;"  dtyp="Boolean" tabindex="0">';
				if (props.rowselectortype=="RADIO") {
					selectorType = "RADIO";
					genObj.scrhtml += '<span class="apzview"><input id="' + id + '_selcb_' + index + '" rowno="' + index + '" type="' + selectorType + '" name="'
							+ selectorName + '" class="group-checkable"><label for="' + id + '_selcb_' + index + '" class="rlabel"></label></span>';
				} else {
					genObj.scrhtml += 
							'<span class="ett-chek etb-chek ' + apprnceClass + '"><input id="' + id + '_selcb_' + index + '" rowno="' + index + '" type="' + selectorType + '" onclick = "apz.rowSelectorClicked(event)" name="' + selectorName + '" class="group-checkable">';
				}
				genObj.scrhtml += '<label class="flb" for="' + id + '_selcb_' + index + '"></label></span></td>';
			}
			if (expandalbleRequired && (props.expandableposition=="LEFT" || props.expandableposition=="LFT")) {
				genObj.scrhtml += '<td id="' + id + '_exp_' + index + '" class="' + advancedTabClass + '" targetclass="' + collapsibleIcon + '" style="width:8px;"  dtyp="Boolean" >';
				if (!this.apz.isNull(expandableIcon) && !this.apz.isNull(collapsibleIcon)) {
					genObj.scrhtml += '<svg aria-hidden="true" role="presentation" class="icon ' + expandableIcon + ' px24"><use xlink:href="#' + expandableIcon + '"></use></svg>';
				}
				genObj.scrhtml += "</td>";
			}
			for (var s = 0; s < noOfSecRows; s++) {
				var secRow = props.childs[s];
				secRow.parent = props;
				secRow.container = props;
				secRowData.portion = "BODY";
				secRow.rowNo = r;
				this.genSecRow(param,secRow);
			}
			if (expandalbleRequired && (props.expandableposition!="LEFT" || props.expandableposition!="LFT")) {
				genObj.scrhtml += 
						'<td id="' + id + '_exp_' + index + '" class="' + advancedTabClass + '" targetclass="' + collapsibleIcon + '" style="width:8px;"  dtyp="Boolean">';
				if (!this.apz.isNull(expandableIcon) && !this.apz.isNull(collapsibleIcon)) {
					genObj.scrhtml += '<svg aria-hidden="true" role="presentation" class="icon ' + expandableIcon + ' px24"><use xlink:href="#' + expandableIcon + '"></use></svg>';
				}
				genObj.scrhtml += '</td>';
			}
			genObj.scrhtml += '</tr>';
		}
		genObj.scrhtml += '</tbody>';
		genObj.scrhtml += "</table>";
		genObj.scrhtml += "</div>";
		if (props.paginationstyle=="APPEND" && (props.containerstate=="READONLY" || props.containerstate=="RO")) {
			genObj.scrhtml += '<ul id="' + id + '_footer" class="plml">';
			genObj.scrhtml += '<li id="' + id + '_footer_li">';
			genObj.scrhtml += '<a id="' + id + '_loadmore_btn" class="loadmore" onclick="apz.data.changePage(&quot;' + id + '&quot;,&quot;N&quot;,&quot;&quot;)" href="javascript:;">Load More</a>';
			genObj.scrhtml += '<a id="' + id + '_loadless_btn" class="loadless" onclick="apz.data.changePage(&quot;' + id + '&quot;,&quot;P&quot;,&quot;&quot;)" href="javascript:;">Load Less</a>';
			genObj.scrhtml += '</li>';
			if (props.dynamicpagesize=="Y") {
				genUtils.genDynamicPageSize(param,props);
			}
			genObj.scrhtml += "</ul>";
		}
		if (props.paginationstyle=="PAGE2") {
			genUtils.genNumberedPaginationButtons(param,props);
		} else if (props.paginationstyle=="PAGE3") {
			genUtils.genInfoandNumberedPagination(param,props);
		}
		genObj.scrhtml += '<div id="' + id + '_spinner" class="loading1 with-dimmer sno"></div>';
		genObj.scrhtml += '</div>';
    },
    genFilterHeaders :function(param,secRows,props){
    	var length = secRows.length,secRowCol="",widgetType="",dataType="",elementData="",hideClass="";
    	var genObj = param.genObj;
    	var genUtils = param.genUtils;
    	if(length){
	    	genObj.scrhtml += "<tr role='row' class='tbl-flt'>"
	    		if(props.rowselectorrequired=="Y"){
	    			genObj.scrhtml += "<th></th>";
	    		}
	    		for(var i = 0; i< length;i++){
		    		 secRowCol = secRows[i].childs[0];
		    		 for(var j = 0 ; j<secRowCol.childs.length;j++){
		    		 	hideClass="";
		    		 	elementData = secRowCol.childs[j];
		    		 	 widgetType = elementData.widgettype;
		    		 	 if (elementData.options=="N") {
							hideClass = "sno";
						}
		    		 	 dataType = elementData.datatype;
		    		 	 var widgetId = genUtils.getElementId(param, elementData,true)+"_filter";
		    		 	 if(dataType == "DATE" || dataType == "DATETIME"){
		    		 	 	genObj.scrhtml +='<th class=" '+hideClass+'" aria-controls="tb1" aria-label=""><span class="ecn"><span class="ett-date etw-100 icr pri"><svg class="ett-icon icon-calendar px24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-calendar"></use></svg><input id="'+widgetId+'" type="text" class="ett-inpt etw-100 pri hasDatepicker filterdate" onblur="apz.onDateFilterBlur(this,event)"  placeholder="DD / MM / YYYY" title="Date Input"></span></span></th>';
		    		 	 }else{
		    		 	 	//var uniqueId = elementData.id;
		    		 	 	genObj.scrhtml +='<th class="'+hideClass+' " aria-controls="tb1 " aria-label=" "><div id="'+widgetId+'_ext" class="etb-slct ett-slct pri etw-100 is-multi-tiered mltc tbl-collist table-selectall filterdropdown" enabled="enabled" style=""><ul id="'+widgetId+'_select" class="sno"></ul><input id="'+widgetId+'" class="sub-elt" type="text" readonly="readonly" value="" placeholder="filter"><span id="'+widgetId+'_span" class="sub-elt1"><svg aria-hidden="true" role="presentation" class="icon icon-down px34"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-down"></use></svg></span><div id="'+widgetId+'_div" class="sub-ctr" original-id="'+widgetId+'"><span class="ecn col-srh"><span class="icl pri"><svg class="ett-icon icon-search px24"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-search"></use></svg><input class="ett-inpt etw-100 pri" type="text" id="'+widgetId+'_search" onkeyup="apz.filterKeyAction(event.target)" placeholder="Search"></span><span class="etb-chek ett-chek pri hor" aria-describedby=""><span id="Admin__Dashboard__el_cbx_2_xlabelspan" for="Admin__Dashboard__el_cbx_2" class=""><input id="'+widgetId+'_selectall" type="CHECKBOX" placeholder="filter"><label id="'+widgetId+'_option_one_label1" for="'+widgetId+'_selectall" aria-label="one">Select all</label> </span></span></span><ul id="'+widgetId+'_so" role="menu"></ul><span class="ok-bttn"><span><button id="'+widgetId+'_cancel" class="ett-bttn sml tsp " onclick="apz.filterCloseAction(\''+widgetId+'\',event)">Cancel</button></span><span><button id="'+widgetId+'_ok" class="ett-bttn sml pri" onclick="apz.filterSubmitAction(\''+widgetId+'\',event)">ok</button></span></span></div></div>'
		    		 	 }
		    		 }
	    		}
	    	genObj.scrhtml+="</tr>";
    	}
    	
    },
    genChart : function(param, chartObj) {
    	var genUtils = param.genUtils;
    	var genObj = param.genObj;
        var name = param.appId + "__" + param.scr + "__" + chartObj.name;
		var snoCls = genUtils.getShowNoneClass(chartObj);
		var cssCls = genUtils.getCustomClass(chartObj);
		var apprnceClass = !this.apz.isNull(chartObj.appearance) ? " " + chartObj.appearance : " pri";
		var height = "";
		if(chartObj.height){
			height = ' style="height:' + chartObj.height + chartObj.heighttype + '"';
		}
		genObj.scrhtml += '<div id="' + name + '_chbox" class="crt-chrt'+ apprnceClass + snoCls + cssCls + '">';
		if (((!this.apz.isNull(chartObj.title)) || (!this.apz.isNull(chartObj.icon)))) {
			genObj.scrhtml += '<ul id="' + name + '_title" class="ttl">';
			this.getContainerTitle(param, chartObj);
			genObj.scrhtml += '</ul>';
		}
		genObj.scrhtml += '<div id="' + name + '_chart" class="chrt-crt" ' + height + '></div></div>';
    },
    genGauge : function(param, gaugeObj) {
    	var genUtils = param.genUtils;
    	var genObj = param.genObj;
        var name = param.appId + "__" + param.scr + "__" + gaugeObj.name;
		var snoCls = genUtils.getShowNoneClass(gaugeObj);
		var cssCls = genUtils.getCustomClass(gaugeObj);
		var apprnceClass = !this.apz.isNull(gaugeObj.appearance) ? " " + gaugeObj.appearance : " pri";
		var height = "";
		if(gaugeObj.height){
			height = ' style="height:' + gaugeObj.height + gaugeObj.heighttype + '"';
		}
		genObj.scrhtml += '<div id="' + name + '_gaug" class="crt-gaug'+ apprnceClass + snoCls + cssCls + '">';
		if (((!this.apz.isNull(gaugeObj.title)) || (!this.apz.isNull(gaugeObj.icon)))) {
			genObj.scrhtml += '<ul id="' + name + '_title" class="ttl">';
			this.getContainerTitle(param, gaugeObj);
			genObj.scrhtml += '</ul>';
		}
		genObj.scrhtml += '<div id="' + name + '_gauge" class="gaug" ' + height + '></div></div>';
    },
    genMenu : function(param, menuObj) {
    	var genUtils = param.genUtils;
    	var genObj = param.genObj;
    	var containerData = menuObj;
        var id = param.appId + "__" + param.scr + "__" + menuObj.name;
		var menuCls = "crt-vmnu";
		var apprCls = !this.apz.isNull(menuObj.appearance) ? " " + menuObj.appearance : " pri";
		if (containerData.responsive != "N" && containerData.orientation=="HORIZONTAL") {
			menuCls = "crt-hmnu";
		} else if (containerData.responsive == "N") {
			var icnTopCls = "";
			if (containerData.iconposition == "TOP") {
				icnTopCls = " mic";
			}
			menuCls = "topnav crt-acmn crb-acmn "+apprCls+icnTopCls;
		}
		if (containerData.options == "N") {
			menuCls = menuCls + " sno";
		}
		if (containerData.iconposition == "TOP") {
			menuCls = menuCls + " icp";
		}
		var vartnClass = !this.apz.isNull(menuObj.variation) ? " "+menuObj.variation : "";
		if (containerData.responsive == "N" && containerData.contextmenu != "Y") {
			genObj.scrhtml += '<ul id ="' + id + '" class="' + menuCls + vartnClass + '">';
		} else if (containerData.contextmenu == "Y") {
			genObj.scrhtml += '<div class=" ett-ctmu etb-ctmu '+apprCls+'"><div id="'+id+'" class=" cmnu-ctr" ><ul>';				
		} else {
			genObj.scrhtml += '<div id = "' + id + '" class="' + menuCls + vartnClass + '">';
			genObj.scrhtml += '<ul id = "' + id + '_ul" >';
		}
		if (containerData.menuitems && containerData.menuitems.length>0) {
			var lnoofmenuitems = containerData.menuitems.length;
			var lsidebarmenu = null;
			var lparent = "";
			llist = [];
			childMenuList = [];
			for (var s = 0; s < lnoofmenuitems; s++) {
				lsidebarmenu = containerData.menuitems[s];
				lparent = lsidebarmenu.parentname.toUpperCase();
				if (lparent == "NONE") {
					if ($.inArray(llist,lparent) == -1) {
						llist.push(lsidebarmenu);
					}
				} else {
					if ($.inArray(childMenuList,lparent) == -1) {
						childMenuList.push(lsidebarmenu);
					}
				}
			}
			var licon = "";
			var lselect = "";
			for (var p = 0; p < llist.length; p++) {
				lsidebarmenu = llist[p];
				licon = lsidebarmenu.menuicon;
				var childNameAttr = "";
				if (containerData.responsive=="N" && lsidebarmenu.selected=="SELECTED") {
					lselect = "current";
				}
				for (var a = 0; a<childMenuList.length; a++) {
					var allMenuData = childMenuList[a];
					if (allMenuData.parentname == lsidebarmenu.menuname){
						childNameAttr = ' rel = "'+id+'_'+lsidebarmenu.menuname+'_ul"';
					}
				}
				var sideBarMenuId = id + "_" + lsidebarmenu.menuname;
				genObj.scrhtml += '<li id="' + sideBarMenuId + '_li" class = " ' + lselect + '"';
				lselect = "";
				genObj.scrhtml += " " + this.getMenuEvents(containerData);
				genObj.scrhtml += ">";
				if (containerData.contextmenu!="Y") {
					genObj.scrhtml += '<a href="javascript:;" class="" '+childNameAttr+'>';
				}
				var tagName = "i";
				if (containerData.responsive == "N" || containerData.contextmenu == "Y") {
					tagName = "span";
				}
				genObj.scrhtml += '<'+tagName+'><svg id="' + sideBarMenuId + '_menu_' + licon + ' " class="icon ' + licon + ' px24"><use xlink:href="#'
						+ licon + '"></use></svg></'+tagName+'>';
					var lsubmenuname = this.apz.getLabel(lsidebarmenu.menuname);
					if (containerData.responsive != "N" && containerData.contextmenu != "Y") {
						genObj.scrhtml += '<p>' + lsubmenuname + '</p>';
					} else if (containerData.contextmenu == "Y"){
						genObj.scrhtml += '<a href="javascript:;">' + lsubmenuname + '</a>';
					} else {
						genObj.scrhtml += lsubmenuname;
					}
				if (containerData.contextmenu != "Y") {
					genObj.scrhtml += '</a>';
				}
				if (containerData.responsive == "N" && containerData.contextmenu != "Y") {
					this.genSubMenuWrapper(param,menuObj,lsidebarmenu);
				}
				genObj.scrhtml += "</li>";
				if (containerData.responsive != "N" && containerData.contextmenu != "Y") {
					this.genSubMenuWrapper(param,menuObj,lsidebarmenu);
				}
			}
		}
		genObj.scrhtml += "</ul>";
		var menuIcon = containerData.icon;
		if (containerData.responsive != "N" && containerData.contextmenu != "Y") {
			genObj.scrhtml += "</div>";
			genObj.scrhtml += '<a class="animateddrawer ett-bttn tsp med" id="'+id+'-mobiletoggle" style="display: none;"><svg class="px24 '+menuIcon+'"><use xlink:href="#'+menuIcon+'"></use></svg></a>';
		} else if (containerData.contextmenu == "Y") {
			genObj.scrhtml += "</div></div>";
		}
    },
    genNavbar :function(param, cntrData){
    	var genUtils = param.genUtils;
    	var genObj = param.genObj;
        var id = param.appId + this.apz.idSep + param.scr + this.apz.idSep + cntrData.name;
		var containerVariation = this.getNavBarClassAttributes(param,cntrData);
		genObj.scrhtml += "<div id = '" + id + "' class='crt-navb" + containerVariation +"'>";
		//// No of Section Rows
		var noOfSecrows = cntrData.childs.length;
		for (var s = 0; s < noOfSecrows; s++) {
			/*var secRowDataObj = cntrData.childs[s];
			childObj.cntrData = childObj.parent = cntrData;
        	childObj.rowNo = -1;
        		this.genSecRow(param, childObj);
			//genSectionRow(appParams, appData, templateDataObj, containerData, secRow, defltTheme, osParams.name, contents);
			this.genSecRow(param,secRowDataObj)*/
			var childObj = cntrData.childs[s];
    		childObj.cntrData = childObj.parent = cntrData;
    		childObj.rowNo = -1;
    		this.genSecRow(param, childObj);
		}
		genObj.scrhtml += "</div>";
    },
    getNavBarClassAttributes :function(param,cntrData){
    	var classStr = " ";
    	var genUtils = param.genUtils;
		classStr = classStr + genUtils.getAlignmentClass(cntrData.contentalignment);
		classStr = classStr + genUtils.getCustomClass(cntrData);
		classStr = classStr + genUtils.getShowNoneClass(cntrData);
		var apprnceClass = !this.apz.isNull(cntrData.appearance) ? " " + cntrData.appearance.toLowerCase() : " pri";
		var varClass = !this.apz.isNull(cntrData.variation) ? " " + cntrData.variation.toLowerCase() : "";
		classStr = classStr + apprnceClass + varClass;
		return classStr;
    },
    genSubMenuWrapper : function(param,menuObj,currMenuRec) {
        var genObj = param.genObj;
        var containerData = menuObj;
        var lname = "";
		var licon = "";
		var lparentname = "";
		lname = currMenuRec.menuname;
		licon = currMenuRec.menuicon;
		var lmnitems = containerData.menuitems.length;
		var lmenuobj = null;
		var lcount = 0;
		var lflag = true;
		var subMenuCls = "";
		var id = param.appId + "__" + param.scr + "__" + menuObj.name + "_" + currMenuRec.menuname;
		if (containerData.responsive != "N") {
			subMenuCls = "smnu";
		}
		for (var mn = 0; mn < lmnitems; mn++) {
			lmenuobj = containerData.menuitems[mn];
			if (lmenuobj.parentname==lname) {
				lcount++;
				if (lcount == 1) {
					genObj.scrhtml += '<ul id = "' + id + '_ul" class="'+subMenuCls+'" >';
					lflag = false;
				}
				this.genSubMenu(param,menuObj,lmenuobj);
			}
		}
		if (!lflag) {
			genObj.scrhtml += "</ul>";
		}
    },
    genSubMenu : function(param,containerData,subMenuObj) {
        var genObj = param.genObj;
		var id = param.appId + "__" + param.scr + "__" + containerData.name + "_" + subMenuObj.menuname;
		var lname = subMenuObj.menuname;
		var lmnitems = containerData.menuitems.length;
		var lflag = true;
		var lclass = "";
		var subMenuCls = "";
		var licon = subMenuObj.menuicon;
		if (containerData.responsive == "N" && subMenuObj.selected == "SELECTED") {
			lclass = "current";
		}
		genObj.scrhtml += '<li id = "' + id + '_li" class  = "' + lclass + '"';
		genObj.scrhtml += " " + this.getMenuEvents(containerData);
		genObj.scrhtml += ">";
		genObj.scrhtml += '<a class = "" href="javascript:;">';
		var tagName = "i";
		if (containerData.responsive == "N") {
			tagName = "span";
		}
		genObj.scrhtml += '<'+tagName+'><svg id="'+id+'_menu_' + licon + ' " class="icon ' + licon + ' px24"><use xlink:href="#' + licon + '"></use></svg></'+tagName+'>';
		var lsubmenuname = this.apz.getLabel(lname);
		if(containerData.responsive != "N") {
			genObj.scrhtml += "<p>"+lsubmenuname+"</p>";
		} else {
			genObj.scrhtml += ""+lsubmenuname+"";
		}
		genObj.scrhtml += "</a>";
		var menuobj = null;
		var lcount = 0;
		for (var mn = 0; mn < lmnitems; mn++) {
			menuobj = containerData.menuitems[mn];
			if (menuobj.parentname==lname) {
				lcount++;
				if (lcount == 1) {
					if (containerData.responsive != "N") {
						subMenuCls = "smnu";
					}
					genObj.scrhtml += '<ul id = "' + id + '_ul" class="'+subMenuCls+'">';
					lflag = false;
				}
				this.genSubMenu(param,containerData,menuobj);
			}
		}
		if (!lflag) {
			genObj.scrhtml += "</ul>";
		}
		genObj.scrhtml += "</li>";
    },
	getMenuEvents : function(containerData) {
		var levents = "";
		var levent = null;
		if (containerData.events) {
			for (var i = 0; i < containerData.events.length; i++) {
				levent = containerData.events[i];
				if (!this.apz.isNull(levent["function"]) && !this.apz.isNull(levent.name)) {
					var lfunction = levent["function"] ? levent["function"].replace('"', '&quot;') : "";
					levents += levent.name + '="' + lfunction + '"';
				}
			}
		}
		return levents;
	},
    genSecRow : function(param,secRowDataObj) {
        var props = secRowDataObj;
        var genObj = param.genObj;
        var containerData = secRowDataObj.cntrData;
        var classAttrs = param.genUtils.getSectionRowClassAttributes(secRowDataObj);
        var id = param.appId + "_" + param.scr + "_" + props.name;
        var wrappedClass = "";
		if (secRowDataObj.hasRespSecCol=="Y" || containerData.orientation=="VERTICAL" || containerData.orientation=="VER ") {
			wrappedClass = " wrapped";
		}
        if(secRowDataObj.cntrData.widgettype=="FORM"){
        	genObj.scrhtml += '<span id="' + id + '" class="srb '+classAttrs+wrappedClass+'">';
     	}
     	if(secRowDataObj.cntrData.widgettype=="NAVBAR"){
        	genObj.scrhtml += "<ul id = '" + id + "' class='srb " + classAttrs + wrappedClass + "'>";
     	}
        if (props.childs && props.childs.length>0) {
        	for (var c=0; c<props.childs.length; c++) {
        		var currChild = props.childs[c];
        		if (currChild.type=="SECTIONCOLUMN") {
        			currChild.parent = secRowDataObj;
        			currChild.rowNo = secRowDataObj.rowNo;
        			currChild.cntrData = secRowDataObj.cntrData;
        			if (secRowDataObj.portion) {
        				currChild.portion = secRowDataObj.portion;
        			}
        			this.genSecCol(param,currChild);
        		}
        	}
        }
        if(secRowDataObj.parent.widgettype=="LIST"){
	        if((secRowDataObj.rowNo==0 && secRowDataObj.parent.type=="CONTAINER")) {
	        	var allContainers = param.templateDataObj ? param.templateDataObj.containers : [];
	        	var noOfContainers = allContainers.length;
				for (var c = 0; c < noOfContainers; c++) {
					var containerDataObj = allContainers[c];
					if (containerDataObj.parentlist==containerData.name){
						this.genInnerList(param,containerDataObj);
					}
				}
			}
		}
		if(secRowDataObj.cntrData.widgettype=="FORM"){
        	genObj.scrhtml += '</span>';
     	}
     	// Closing of ul tag is done in genList function.
     	/*if(secRowDataObj.cntrData.widgettype=="LIST"){
        	genObj.scrhtml += '</ul>';
     	}*/
    },
    genSecCol : function(param,secColData) {
    	var props = secColData;
        var genObj = param.genObj;
        var containerData = secColData.cntrData;
		var controlId = param.genUtils.getControlIdforWidget(param,secColData);
        var id = param.appId + "_" + param.scr + "_" + props.name;
        var secWidthClass = param.genUtils.getWidthClass(secColData);
        var secColStyleAttr = param.genUtils.getSectionColStyleAttributes(secColData);
		var groupTitle = secColData.fieldsettitle;
		var fieldset = secColData.fieldset;
		var classAttrs = param.genUtils.getSectionColClassAttributes(secColData);
        if(secColData.cntrData.widgettype=="FORM"){
        	genObj.scrhtml += '<span id="' + id + '" class="'+classAttrs+'" '+secColStyleAttr+controlId+'>';
        	if (fieldset=="Y") {
				genObj.scrhtml += '<fieldset>';
				genObj.scrhtml += '<legend>' + groupTitle + '</legend>';
			}
    	}else if(secColData.cntrData.widgettype=="LIST"){
    		genObj.scrhtml+= "<span  id='" + id + "_li' class='"+classAttrs+"' " + secColStyleAttr + controlId + ">";
    	}else if(secColData.cntrData.widgettype=="NAVBAR"){
    		genObj.scrhtml+= "<li id='" + id + "' class='" + classAttrs + "' " + secColStyleAttr + ">";
    	}
        if (props.childs && props.childs.length>0) {
        	for (var d=0; d<props.childs.length; d++) {
        		var currChild = props.childs[d];
        		currChild.parent = secColData;
    			currChild.cntrData = secColData.cntrData;
    			currChild.rowNo = secColData.rowNo;
    			if (secColData.portion) {
    				currChild.portion = secColData.portion;
    			}
        		if (currChild.type=="PRESENTATIONELEMENT") {
        			if (secColData.cntrData.widgettype=="FORM") {
        				this.genFormWrapper(param,currChild);
        			} else if (secColData.cntrData.widgettype=="TABLE") {
        				currChild.index = d.toString();
        				this.genTableWrapper(param,currChild);
        			} else if (secColData.cntrData.widgettype=="LIST") {
        				this.genListWrapper(param,currChild);
        			} else if(secColData.cntrData.widgettype=="NAVBAR"){
        				this.genNavbarWrapper(param,currChild);
        			}
        		} else if (currChild.type=="SECTIONROW") {
					if(secColData.cntrData.widgettype=="LIST"){
						var rowDataObjName = param.appId + "__" + param.scr + "__" + currChild.name;
						var secRowClsAttr = "srb " + param.genUtils.getSectionRowClassAttributes(currChild);
						genObj.scrhtml+= "<span id ='" + rowDataObjName + "_row' class='" + secRowClsAttr+ "' >";
					}
        			this.genSecRow(param,currChild);
					// Span was added irrespective of LIST container making issue in nested sections
        			if(secColData.cntrData.widgettype=="LIST"){
	        			genObj.scrhtml+= "</span>";
	        		}
        		}
        	}
        }
		//if (props.childs.length < 1) {
		if(this.apz.isNull(props.childs)) {
			genObj.scrhtml+= '&nbsp;';
		}
        if (secColData.cntrData.widgettype=="FORM" || secColData.cntrData.widgettype=="LIST") {
        	if (fieldset=="Y") {
				genObj.scrhtml+= '</fieldset>';
			}
        	genObj.scrhtml += '</span>';
        }
        if(secColData.cntrData.widgettype=="NAVBAR"){
        	genObj.scrhtml += '</li>';
        }
    },
    genFormWrapper : function(param, props) {
        var genObj = param.genObj;
        var genUtils = param.genUtils;
		var containerData = props.cntrData;
		var secColData = props.parent;
        var id = genUtils.getElementId(param, props);
        var lblId = id + "_grp_lbl";
		var widgetType = props.widgettype, dataType = props.datatype, icon = props.icon,
				lovname = props.lovname;
		var cssCls = props.cssclasses || "";
		var visibilityClass = "",wrappedClass="",labelPosClass="",appearanceClass="",fltngLbl="",iconClickFun="";
		if (props.options=="N") {
			visibilityClass = " sno";
		}
		if (((!this.apz.isNull(secColData.phonewidth) || !this.apz.isNull(secColData.tabletwidth)
				|| !this.apz.isNull(secColData.desktopwidth) || !this.apz.isNull(secColData.wswidth)) && secColData.width!="CUSTOM" && !this.apz.isNull(secColData.width))
				|| (containerData.orientation=="VERTICAL" || containerData.orientation=="VER")) {
			wrappedClass = " wrapped";
		}
		var labiconPos = props.labeliconposition;
		var labliconSize = " " +props.labeliconsize;
		var iconSemanticsClass = !this.apz.isNull(props.labeliconsemantics) ? " "+props.labeliconsemantics.toLowerCase() : "";
		var iconAppearanceClass = !this.apz.isNull(props.labeliconappearance) ? " "+props.labeliconappearance.toLowerCase() : " pri";
		if (!this.apz.isNull(props.labelicon)) {
			labelPosClass = (props.labeliconposition == "RIGHT" || props.labeliconposition == "RGT") ? " icr" : " icl";
		}
		var vartnClass = !this.apz.isNull(props.variation) ? " " + props.variation.toLowerCase() : "";
		if (props.radiotype != "NATIVE" && props.checkboxtype!="NATIVE") {
			appearanceClass = !this.apz.isNull(props.appearance) ? " " + props.appearance.toLowerCase() : " pri";
		}
		if ((containerData.orientation=="VERTICAL" || containerData.orientation=="VER") && props.floatinglabel=="Y") {
			fltngLbl = " flbl";
		}
		if (!this.apz.isNull(props.labeliconfunction)) {
			iconClickFun = props.labeliconfunction.replace('"', '&quot;');
			iconClickFun = "onclick='"+iconClickFun+"'";
		}
		var uiDescription = props.uidescription;
        var uiDescArr = [];
        if(!this.apz.isNull(uiDescription)) {
            uiDescArr = uiDescription.split("|");
        }
        var isToolTip = false;
        var toolTipClass = "";
        if(!this.apz.isNull(uiDescArr[2]) && uiDescArr[2] == "Tooltip") {
            toolTipClass = "sno";
        }
        if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] == "Top"){
            genObj.scrhtml += '<span id="' + id + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
		genObj.scrhtml += '<ul id="' + id + '_ul" class="eoc srb ' + visibilityClass + wrappedClass + fltngLbl + cssCls + '">';
		var lblWidthCls = genUtils.getElementResponsiveWidthClass(props.labelwidth,"");
		genObj.scrhtml += '<li class="' + lblWidthCls + '">';
		var lblAlignment = genUtils.getElmLabelAlignAttrs(props);
		var arrListObj = ["BADGE","BUTTON","IMAGE","HYPERLINK","ICON"];
		if (arrListObj.indexOf(widgetType) == -1 && props.custom=="N") {
			var title = this.apz.getLabel(props.title);
			title = !this.apz.isNull(title) ? title : "";
			if(widgetType=="LABEL") {
				title = "";
			}
			if (!this.apz.isNull(props.labelicon) && props.labeliconclickable=="Y" && (labiconPos!="RIGHT" || labiconPos!="RGT")) {
				genObj.scrhtml += '<button id="'+id+'_lblbutton" class="ett-bttn tsp sml lbtn px20" '+iconClickFun+' aria-label="help"><svg id="'+id+'_lblicon" aria-hidden="true" class="ett-icon '
							+ props.labelicon + labliconSize + iconAppearanceClass + iconSemanticsClass + '"><use xlink:href ="#' + props.labelicon + '"></use></svg></button>';
			}
			genObj.scrhtml += '<label id="' + lblId + '"  for="' + id + '" class="flb ' + 
							 labelPosClass + lblAlignment + '" title="' + title + '">';
			if (!this.apz.isNull(props.labelicon) && props.labeliconclickable!="Y") {
				if (labiconPos=="RIGHT" || labiconPos=="RGT") {
					genObj.scrhtml += title + '<svg  id="'+id+'_lblicon" aria-hidden="true" role="presentation" class="ett-icon '
							+ props.labelicon + ' ' + labliconSize + iconAppearanceClass + iconSemanticsClass + '"><use xlink:href ="#' + props.labelicon + '"></use></svg>';
				} else {
					genObj.scrhtml += '<svg  id="'+id+'_lblicon" aria-hidden="true" role="presentation" class="ett-icon ' + props.labelicon + ' '
							+ labliconSize + iconAppearanceClass + iconSemanticsClass + '"><use xlink:href ="#' + props.labelicon + '"></use></svg>' + title;
				}
			} else {
				if (this.apz.isNull(title)) {
					title = "&nbsp;";
				}
				genObj.scrhtml += title;
			}
			genObj.scrhtml += "</label>";
			if (!this.apz.isNull(props.labelicon) && props.labeliconclickable=="Y" && (labiconPos=="RIGHT" || labiconPos=="RGT")) {
				genObj.scrhtml += '<button id="'+id+'_lblbutton" class="ett-bttn tsp sml lbtn px20" '+iconClickFun+' aria-label="help"><svg  id="'+id+'_lblicon" aria-hidden="true" class="ett-icon ' + props.labelicon + ' '
							+ labliconSize + iconAppearanceClass + iconSemanticsClass + '"><use xlink:href = "#' + props.labelicon + '"></use></svg></button>';
			}
		} else if (props.custom == "Y" && props.labelrequired=="Y") {
			var title = this.apz.getLabel(props.title);
			genObj.scrhtml += '<label id="' + lblId + '"  for="' + id + '" class="flb" title="' + title + '">';
			if (!this.apz.isNull(props.labelicon)) {
				if (labiconPos=="RIGHT" || labiconPos=="RGT") {
					genObj.scrhtml += title + ' <svg id="'+id+'_lblicon" aria-hidden="true" role="presentation" class="ett-icon '
							+ elementData.labelicon + ' ' + labliconSize + iconAppearanceClass + iconSemanticsClass + '"><use xlink:href = "#' + props.labelicon + '"></use></svg>';
				} else {
					genObj.scrhtml += '<svg  id="'+id+'_lblicon" aria-hidden="true" role="presentation" class="ett-icon ' + props.labelicon + ' '
							+ labliconSize + iconAppearanceClass + iconSemanticsClass + '"><use xlink:href ="#' + props.labelicon + '"></use></svg> ' + title;
				}
			} else {
				if (this.apz.isNull(title)) {
					title = "&nbsp;";
				}
				genObj.scrhtml += title;
			}
			genObj.scrhtml += "</label>";
		} else if (props.custom=="N") {
			genObj.scrhtml += '<label class="flb">&nbsp</label>';
		}
		genObj.scrhtml += '</li>';
		var orientation = "";
		if (widgetType=="CHECKBOXGROUP") {
			if (props.orientation=="VERTICAL" || props.orientation=="VER") {
				orientation = " ver";
			}
		}
		var lavailwidthcls = "etw-60";
		if (!this.apz.isNull(props.labelwidth) && props.labelwidth !="CUSTOM" && props.labelwidth !="NONE") {
			var widthNum = 100 - parseInt(props.labelwidth);
			lavailwidthcls = "etw-"+ widthNum.toString();
		}
		var wrapId = id + "_span";
		var apzType = "";
		if (widgetType=="RADIO") {
			wrapId = id;
			apzType = ' apztype="radiogroup"';
		}
		genObj.scrhtml += '<li  id="' + id + '_li" class="eic ' + lavailwidthcls + '">';
		if ((widgetType=="RADIO" || widgetType=="CHECKBOX")) {
			var hardCodClass = "";
			var orientationCls = "";
			if (widgetType=="CHECKBOX") {
				hardCodClass = "etb-chek ett-chek hor";
				genObj.scrhtml += '<span id="' + wrapId + '" ' + apzType + ' class="' + hardCodClass
						+ orientationCls + appearanceClass + vartnClass + '">';
			} else if (widgetType=="RADIO") {
				hardCodClass = "etb-rdio ett-rdio";
				if (props.radiotype=="BUTTONBAR" || props.radiotype=="BB") {
					hardCodClass = "ett-rdgp etb-rdgp";
				}
				if (props.orientation=="H" || props.orientation=="HOR") {
					orientationCls = " hor";
				} else {
					orientationCls = " ver";
				}
				if(!this.apz.isNull(props.hint)){
					genObj.scrhtml += '<span id="' + wrapId + '" ' + apzType + ' class="' + hardCodClass
						+ orientationCls + appearanceClass + vartnClass + '" aria-describedby="'+id+'_hnt">';
				}else{
					genObj.scrhtml += '<span id="' + wrapId + '" ' + apzType + ' class="' + hardCodClass
							+ orientationCls + appearanceClass + vartnClass + '" aria-describedby="">';
					
				}
			}
		}
        var widgetGnrtr = new Apz.GenWidget(this.apz);
        widgetGnrtr.genElement(param, props);
		if ((widgetType=="RADIO") || widgetType=="CHECKBOX") {
			genObj.scrhtml += '</span>';
		}
		if (!this.apz.isNull(props.hint)) {
			var hint = this.apz.getLabel(props.hint);
			genObj.scrhtml += '<span id ="'+id+'_hnt" class="htx">' + hint + '</span>';
		}
		genObj.scrhtml += '</li></ul>';
		if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] != "Top"){
            genObj.scrhtml += '<span id="' + id + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
    }, getContainerTitle : function(param, containerData) {
    	var genObj = param.genObj;
		var iconStr = containerData.icon;
		var iconSize = containerData.iconsize;
		if (this.apz.isNull(iconSize)) {
			iconSize = " px24";
		}
		genObj.scrhtml += '<li class="ttl-ctr"><ul class="ttv">';
		if (!this.apz.isNull(containerData.icon)) {
			genObj.scrhtml += '<li class="icn">';
			genObj.scrhtml += '<svg aria-hidden="true" class="ett-icon ' + iconStr + iconSize + '"><use xlink:href="#' + iconStr + '"></use></svg>';
			genObj.scrhtml += '</li>';
		}
		if ((!this.apz.isNull(containerData.title))) {
			genObj.scrhtml += '<li class="lbl">';
			var title = this.apz.getLabel(containerData.title);
			genObj.scrhtml += '<h4>' + title + '</h4>';
			genObj.scrhtml += "</li>";
		}
		genObj.scrhtml += "</ul></li>";
	},genListWrapper :function(param,elementData){
		var genObj = param.genObj;
		var widgetType = elementData.widgettype;
		var genUtils = param.genUtils;
		var orientationClass = "";
		var dropdownVariationCls = "";
		var rowNo = elementData.rowNo;
		var localRowNo = rowNo!=-1 ? ' rowno ="'+rowNo.toString()+'"' : "";
		var cssCls = genUtils.getCustomClass(elementData);
		var elmId = genUtils.getElementId(param,elementData);
		var vartnClass = !this.apz.isNull(elementData.variation) ? " " + elementData.variation.toLowerCase() : "";
		var visibilityClass = genUtils.getShowNoneClass(elementData);
		var elmnName = elementData.elementname, icon = elementData.icon, dataType = elementData.datatype,
				lovName = elementData.lovname;
		var nodeDataObj = null;
		var elmDataObj = null;
		if (!this.apz.isNull(elementData.orientation)) {
			orientationClass = elementData.orientation=="H" ? " hor" : " ver";
		}
		var lookAndFeelClass = "";
		var uiDescription = elementData.uidescription;
        var uiDescArr = [];
        if(!this.apz.isNull(uiDescription)) {
            uiDescArr = uiDescription.split("|");
        }
        var isToolTip = false;
        var toolTipClass = "";
        if(!this.apz.isNull(uiDescArr[2]) && uiDescArr[2] == "Tooltip") {
            toolTipClass = "sno";
        }
        //id is not defined. should be elmId//
        if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] == "Top"){
            genObj.scrhtml += '<span id="' + elmId + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
		if ((widgetType=="DROPDOWN")&& (elementData.dropdowntype=="WITHSUBOPTIONS" || elementData.dropdowntype=="WSO"|| elementData.dropdowntype=="AUTOCOMPLETE" || elementData.dropdowntype=="ATO"
			|| elementData.dropdowntype=="MULTISELECTCHECKBOX" || elementData.dropdowntype=="MSC")) {
			dropdownVariationCls = " ddn"; 
		}
		if (widgetType=="CHECKBOXGROUP") {
			if (elementData.orientation=="VERTICAL" || elementData.orientation=="VER") {
				orientationClass = " ver";
			}
		}
		var widthClass = genUtils.getWidthClass(elementData);
		var classStr = "";
		var appearanceClass = !this.apz.isNull(elementData.appearance) ? " " + elementData.appearance.toLowerCase() : " pri";
		classStr = "class='ecn " + widthClass + orientationClass + lookAndFeelClass + visibilityClass
				+ dropdownVariationCls + cssCls + vartnClass + "' ";
		var widgetGnrtr = new Apz.GenWidget(this.apz);
		if (widgetType=="CHECKBOX" || widgetType=="TEXTAREA"
				|| widgetType=="DROPDOWN" || widgetType=="SLIDER"
				|| widgetType=="TAGS") {
			if (widgetType=="CHECKBOX") {
				if(elementData=="NATIVE") {
					appearanceClass = "";
				}
				classStr = "class = 'etb-chek ett-chek hor" + appearanceClass + vartnClass +"'";
			}
			genObj.scrhtml+= "<span id='" + elmId + "_span_" + rowNo + "' " + classStr + ">";
			widgetGnrtr.genElement(param, elementData);
			genObj.scrhtml+= "</span>";
		} else if (widgetType=="RADIO") {
			var hardCodClass = "etb-rdio ett-rdio";
			if (elementData.radiotype=="BUTTONBAR" || elementData.radiotype=="BB") {
				hardCodClass = "ett-rdgp etb-rdgp";
			}
			classStr = "class = '" + hardCodClass + orientationClass + appearanceClass + vartnClass + " '";
			var newElmId = genUtils.getElementId(param,elementData);			
			genObj.scrhtml+= "<span id='" + newElmId + "'" + localRowNo + " apztype='radiogroup' " + classStr + ">";
			widgetGnrtr.genElement(param, elementData);
			genObj.scrhtml+= "</span>";
		} else {
			widgetGnrtr.genElement(param, elementData);
		}
		if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] != "Top"){
            genObj.scrhtml += '<span id="' + elmId + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
	}, genTableWrapper : function(param,elementData) {
        var genObj = param.genObj;
        var genUtils = param.genUtils;
        var containerData = elementData.cntrData;
		var elementId = genUtils.getElementId(param,elementData);
		var cssCls = genUtils.getCustomClass(elementData);
		var requiredCls = "",headerStyleAttr="", mandatory = elementData.mandatory;
		var widgetType = elementData.widgettype, dataType = elementData.datatype, icon = elementData.icon, lovName = elementData.lovname;
		var sortable = containerData.sortable;
		var elementState = genUtils.getElementState(elementData);
		var vartnClass = !this.apz.isNull(elementData.variation) ? " " + elementData.variation.toLowerCase() : "";
		var hardCodClass = "";
		var cellWidth = "";
		var cellWidthType=!this.apz.isNull(elementData.tablecellwidthtype) ? " " + elementData.tablecellwidthtype : "";
		if(!this.apz.isNull(cellWidthType)){
			cellWidth = elementData.tablecellwidth + cellWidthType.toLowerCase();
		}else{
			cellWidth = "PX";
		}
		var index = elementData.index;
		var colId = param.appId + "__" + param.scr + "__" + containerData.name + "_col_" + index;
		var spanReqd = false;
		var secColData = elementData.parent;
		var horAlignCls = genUtils.getAlignmentClass(secColData.horizontalalignment);
		var headerAlignCls = horAlignCls, colAlignCls = horAlignCls;
		var appearanceCls = !this.apz.isNull(secColData.appearance) ? " "+secColData.appearance.toLowerCase() : " pri";
		var secColVarCls = !this.apz.isNull(secColData.variation) ? " "+secColData.variation.toLowerCase() : "";
		var ElmappearanceCls = !this.apz.isNull(elementData.appearance) ? " "+elementData.appearance.toLowerCase() : " pri";
		var checkBoxClass = "";
		var copyElmData = this.apz.copyJSONObjectWithFilter(elementData,["parent"]);
		copyElmData.rowNo = -1;
		var sortBtnElmId = genUtils.getElementId(param, copyElmData);
		var idAria = param.appId + "__" + param.scr + "__" + containerData.name;
		if (widgetType=="INPUTBOX" || widgetType=="DROPDOWN" || widgetType=="RADIO"
				|| widgetType=="CHECKBOX" || widgetType=="TAGS" || widgetType=="SLIDER") {
			spanReqd = true;
		}
		if(widgetType=="CHECKBOX") {
			if(elementData.checkboxtype=="NATIVE") {
				ElmappearanceCls = "";
			}
			checkBoxClass = "ett-chek etb-chek hor" + ElmappearanceCls;
		}
		var headerAllignment = elementData.headeralignment;
		var columnAllignment = elementData.columnalignment;
		if (!this.apz.isNull(headerAllignment)) {
			headerAlignCls = " "+headerAllignment;
		}
		if (!this.apz.isNull(columnAllignment)) {
			colAlignCls = " "+columnAllignment;
		}
		var amountClass = "";
		if (dataType=="NUMBER" || dataType=="INTEGER") {
			amountClass = "ui-amount ";
		} else {
			amountClass = "";
		}
		if (mandatory=="Y") {
			requiredCls = "req";
		}
		var hideClass = "";
		if (elementData.options=="N") {
			hideClass = " sno";
		}
		if (!this.apz.isNull(horAlignCls)) {
			horAlignCls = " " + horAlignCls;
		}
		if (!this.apz.isNull(cellWidth)) {
			headerStyleAttr =  ' style="width:' + cellWidth + '"';
		}
		if (elementData.portion=="HEADER") {
			if(sortable=="Y"){
			genObj.scrhtml+= '<th id="' + colId + '_th" lovid="' + elementId + '" class="sorting ' + amountClass + secColVarCls +appearanceCls+' ' + headerAlignCls + ' ' + requiredCls + hideClass
					+ '" aria-controls="'+idAria+'_table" aria-label="'+elementData.title+'" '+headerStyleAttr+' onclick="apz.sortAction(&quot;'+param.appId+'__' +param.scr + '__' + containerData.name + '&quot;,&quot;'
						+ sortBtnElmId + '_0&quot;, this);">';
			}else{
				genObj.scrhtml+= '<th id="' + colId + '_th" lovid="' + elementId + '" class="' + amountClass + secColVarCls +appearanceCls+' ' + headerAlignCls + ' ' + requiredCls + hideClass
						+ '" aria-controls="'+idAria+'_table" aria-label="'+elementData.title+'" '+headerStyleAttr+'>';
			}
			genObj.scrhtml+= this.apz.getLabel(elementData.title);
			genObj.scrhtml+= "</th>";
		} else {
			genObj.scrhtml+= '<td id="td_' + elementId + '" class="'+ secColVarCls + appearanceCls + colAlignCls + hideClass + cssCls + '" ' + '>';		
			if (spanReqd && widgetType!="RADIO") {
				genObj.scrhtml+= '<span class="eoc ' +  checkBoxClass + vartnClass + '">';
			} else if (spanReqd && (widgetType=="RADIO")) {
				var orientationCls = " hor";
				if (elementData.orientation!="H") {
					orientationCls = " ver";
				}
				hardCodClass = "etb-rdio ett-rdio"+orientationCls;
				if (elementData.radiotype=="BUTTONBAR" || elementData.radiotype=="BB") {
					hardCodClass = "ett-rdgp etb-rdgp";
				}
				genObj.scrhtml+= '<span id="' + elementId + '" class="'+ hardCodClass + ElmappearanceCls + vartnClass + '" apztype="radiogroup">';
			}
	        var widgetGnrtr = new Apz.GenWidget(this.apz);
	        widgetGnrtr.genElement(param, elementData);
			if (spanReqd) {
				genObj.scrhtml+= '</span>';
			}
			genObj.scrhtml+= '</td>';
		}
	},getListEvents:function(param,cntrData){
    	var eventStr = "";
		var overlayReq = false;
		var containerData =cntrData;
		var targetOverlayId = param.appId+ "__" + param.scr + "__" + containerData.targetoverlayid;
		var defFunction = "apz.data.selectRow(this, event); apz.data.rowClicked(this,event);";
		if (!this.apz.isNull(containerData.targetoverlayid)) {
			defFunction = defFunction + " apz.toggleOverlay(this,&quot;"+ targetOverlayId + "&quot;,event);";
			overlayReq = true;
		}
		var eventData = null;
		for (var i = 0; i < containerData.events.length; i++) {
			eventData = containerData.events[i];
			if (!this.apz.isNull(eventData["function"]) && !this.apz.isNull(eventData.name)) {
				var lupdatedfunction = eventData["function"].replace("\"", "&quot;");
				if (eventData.name=="ONCLICK") {
					eventStr += "onclick='" + defFunction + lupdatedfunction + "'";
				} else {
					eventStr += eventData.name + "='" + lupdatedfunction + "'";
				}
			}
		}
		if (eventStr=="") {
			eventStr = "onclick='" + defFunction + "'";
		} else {
			if (!eventStr.contains("onclick=")) {
				eventStr += "onclick='" + defFunction + "'";
			}
		}
		return eventStr;
    },
    genNavbarWrapper : function(param,elementData){
    	var genObj = param.genObj;
    	var genUtils = param.genUtils;
    	var classStr = "", orientationClass = "";
		var cssCls = genUtils.getCustomClass(elementData);
		var widgetType = elementData.widgettype;
		var elmId = genUtils.getElementId(param, elementData);
		var dataType = elementData.datatype;
		var visibilityClass = genUtils.getShowNoneClass(elementData);
		var elmState = genUtils.getElementState(elementData);
		var lovname = elementData.lovname;
		var icon = elementData.icon;
		var widthClass = genUtils.getWidthClass(elementData);
		var appearanceClass = !this.apz.isNull(elementData.appearance) ? " "+elementData.appearance.toLowerCase() :" pri" ;
		var vartnClass = !this.apz.isNull(elementData.variation) ? " "+elementData.variation.toLowerCase() :"" ;			
		var elmSpecClass = "";
        var uiDescription = elementData.uidescription;
        var uiDescArr = [];
        if(!this.apz.isNull(uiDescription)) {
            uiDescArr = uiDescription.split("|");
        }
        var isToolTip = false;
        var toolTipClass = "";
        if(!this.apz.isNull(uiDescArr[2]) && uiDescArr[2] == "Tooltip") {
            toolTipClass = "sno";
        }
        if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] == "Top"){
            genObj.scrhtml += '<span id="' + elmId + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
		if(widgetType=="CHECKBOX") {
			if(elementData.checkboxtype=="NATIVE") {
				appearanceClass = "";
			}
			elmSpecClass = " ett-chek etb-chek hor" + appearanceClass + vartnClass;
		}
		if (widgetType=="RADIO") {
			if (elementData.orientation=="H") {
				orientationClass = " hor";
			} else {
				orientationClass = " ver";
			}
		}
		if (!widthClass=="") {
			widthClass = " " + widthClass;
		}
		classStr = "class=\"ecn " + orientationClass + visibilityClass + cssCls + widthClass + elmSpecClass + "\" ";
		 var widgetGnrtr = new Apz.GenWidget(this.apz);
	      // widgetGnrtr.genElement(param, elementData);
		if (widgetType=="CHECKBOX"|| widgetType=="TEXTAREA" || widgetType=="DROPDOWN"
				|| widgetType=="SLIDER" || widgetType=="TAGS") {
			genObj.scrhtml+= "<span id='" + elmId + "_span' " + classStr + ">";
			widgetGnrtr.genElement(param, elementData);
			genObj.scrhtml+= "</span>";
		} else if (widgetType=="RADIO") {
			var hardCodClass = "etb-rdio ett-rdio";
			if (elementData.radiotype=="BUTTONBAR" || elementData.radiotype=="BB") {
				hardCodClass = "ett-rdgp etb-rdgp";
			}
			classStr = "class = '"+ hardCodClass + orientationClass + appearanceClass + vartnClass +visibilityClass+" '";
			genObj.scrhtml+= "<span id='" + elmId + "' apztype='radiogroup' " + classStr + ">";
			widgetGnrtr.genElement(param, elementData);
			genObj.scrhtml+=  "</span>";
		} else {
			widgetGnrtr.genElement(param, elementData);
		}
		if(!this.apz.isNull(uiDescArr[1]) && uiDescArr[1] != "Top"){
            genObj.scrhtml += '<span id="' + elmId + '_uidesc" class="uiDesc ' + toolTipClass + '">' + uiDescArr[0] + '</span>';
        }
    }, genInnerList : function(param,containerData) {
    	var props = containerData;
        var genObj = param.genObj;
	    var genUtils = param.genUtils;
		var variationCls = !this.apz.isNull(props.variation) ? " " + props.variation.toLowerCase() : "";
		var apprnceClass = !this.apz.isNull(props.appearance) ? " " + props.appearance.toLowerCase() : " pri";
		var id = param.appId + "__" + param.scr + "__" + props.name;
        var containerClsAttr = genUtils.getListClassAttributes(containerData);
		var pageSize = props.pagesize;
		var multiRecord = "N";
        var scrdef = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design];
        if(scrdef && scrdef.containersMap[id]) {
        	multiRecord = scrdef.containersMap[id].multiRec;
        }
		//var multiRecord = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design].containersMap[id].multiRec;
        //var multiRecord = "N";
		var containerId = 'id="' + id + '" ';
		var containerClass = 'class="crt-list chl-ctr' + containerClsAttr + '" ';
		var titleClass = 'class="ttl" ';
		var pageSizeAttr = "";
		var wrappedClass = "";
		/*var targetOverlayId = appData.prjdata.appid + "__" + screenData.name + "__" + containerData.targetoverlayid;
		if (!Utils.isNull(containerData.targetoverlayid)) {
			containerClass = "class=\"crt-list u-relative" + containerClsAttr + "\" ";
		}
		int noOfOverlays = templateDataObj.overlays.size();
		if (noOfOverlays > 0 && templateDataObj.overlays.contains(id)) {
			containerClass = "class=\"crt-list liscont height-transition height-transition-hidden" + containerClsAttr
					+ "\" ";
		}*/
		var singleRec = false;
		if(multiRecord=="N"){
			singleRec = true;
		}
		var paginationReqd = false;
		if (containerData.paginationstyle=="PAGE1") {
			paginationReqd = true;
		}
		if (multiRecord=="N"){ 
			pageSize = "999";
			noOfRows = 1;
		} else {
			if (this.apz.isNull(pageSize)) {
				pageSize = "999";
				noOfRows = 1;
				if (singleRec) {
					pageSize = "1";
				}
			} else {
				noOfRows = parseInt(pageSize);
				if (pageSize=="999") {
					noOfRows = 1;
				}
			}
		}
		pageSizeAttr = "pagesize='" + pageSize + "'";
		genObj.scrhtml+= "<div " + containerId + containerClass + pageSizeAttr + ">";
		if (containerData.title!="" || containerData.icon!=""|| paginationReqd) {
			genObj.scrhtml+=  "<ul id='" + id + "_title' " + titleClass + ">";
			this.getContainerTitle(param,containerData);
			if (paginationReqd) {
				genObj.scrhtml+="<li class='pgn-ctr'>";
				if (containerData.dynamicpagesize=="Y" && containerData.paginationstyle=="PAGE1") {
					genUtils.genDynamicPageSize(param,containerData);
				}
				genUtils.getPrevNextRecButtons(param,containerData);
				genObj.scrhtml+="</li>";
			}
			genObj.scrhtml+= "</ul>";
		}
		genObj.scrhtml+= "<ul class='" + containerData.appearance + variationCls + "'>";
		/*if (!Utils.isNull(containerData.targetoverlayid)) {
			GenUtils.append(contents, null, "<li id=\"" + id + "_dropdown\" class=\"dropdown \" "
					+ "onclick=\"apz.toggleOverlay(this,&quot;" + targetOverlayId
					+ "&quot;,event);\"><input class=\"dropdown-text\" placeholder=\"Select Option\" type=\"text\" onkeyup=\"apz.toggleOverlay(this,&quot;" + targetOverlayId
					+ "&quot;,event);\"><span class=\"dropdown-button\"><svg  aria-hidden=\"true\" role=\"presentation\" class=\"icon icon-down px24\"><use xlink:href=\"#icon-down\"></use></svg></span></li>");
		}*/
		var lnoofsecrows = containerData.childs.length;
		var hiddenClass = "";
		var loopCount = -1;
		var allContainers = param.templateDataObj ? param.templateDataObj.containers : [];
		var noOfContainers = allContainers.length;
		for (var r = 0; r < noOfRows; r++) {
			var lindex = r.toString();
			if (multiRecord=="Y") {
				hiddenClass = "";
			}
			loopCount = r;
			if (r > 0) {
				hiddenClass = "sno";
			}
			for (var s = 0; s < lnoofsecrows; s++) {
				var scrrowdataobj = containerData.childs[s];
				var secRowClsAttr = "srb" + genUtils.getSectionRowClassAttributes(scrrowdataobj);
				/*if (!this.apz.isNull(containerData.targetoverlayid)) {
					secRowClsAttr += " sno";
				}*/
				if (r!=0) {
					secRowClsAttr += " sno ";
				}
				var childCls = "";
				for (var c = 0; c < noOfContainers; c++) {
					var containerDataObj = allContainers[c];
					if (containerDataObj.parentlist==containerData.name) {
						childCls = " chl";
					}
				}
				wrappedClass = " wrapped";
				genObj.scrhtml+= "<li  id ='" + id + "_row_" + lindex + "' class='"
						+ secRowClsAttr + wrappedClass + childCls +"' rowno='" + lindex + "' ";
				// / Add Events
				genObj.scrhtml+= " " + this.getListEvents(param,containerData);
				genObj.scrhtml+= ">";
				if (multiRecord=="Y") {
					scrrowdataobj.parent=containerData
					scrrowdataobj.cntrData = containerData;
					scrrowdataobj.rowNo = r;
					this.genSecRow(param,scrrowdataobj);
				} else {
					scrrowdataobj.parent=containerData
					scrrowdataobj.cntrData = containerData;
					scrrowdataobj.rowNo = -1;
					this.genSecRow(param,scrrowdataobj);
				}
				genObj.scrhtml+= "</li>";
			}
		}
		if (containerData.paginationstyle=="APPEND" && (containerData.containerstate=="READONLY" || containerData.containerstate=="RO")) {
			genObj.scrhtml+= "<span id='listfoot" + idSep + "_span' class='listfooter'>";
			genObj.scrhtml+="<span class='rft'>";
			genObj.scrhtml+= "<a id='" + id + "_loadmore_btn' class='loadmore' "
					+ genUtils.getPaginationEvent("onclick", id, "N", false) + " href='javascript:;''>Load More </a>";
			genObj.scrhtml+= "<a id='" + id + "_loadless_btn' class='loadless' "
					+ genUtils.getPaginationEvent("onclick", id, "P", false) + " href='javascript:;'> Load Less</a>";
			genObj.scrhtml+="</span>";
			genObj.scrhtml+= "</span>";
		}
		genObj.scrhtml+= "</ul>";
		if (containerData.paginationstyle=="PAGE2") {
			genUtils.genNumberedPaginationButtons(param,containerData);
		} else if (containerData.paginationstyle=="PAGE3") {
			genUtils.genInfoandNumberedPagination(param,containerData);
		}
		genObj.scrhtml+="</div>";
	}
}
