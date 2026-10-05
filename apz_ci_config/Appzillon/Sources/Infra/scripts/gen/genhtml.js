Apz.GenHTML = function(apz) {
   this.apz = apz;
   //////////////Private Variables//////////////
   ////Model Data
   this.scrhtml = {};
};
 //var designDef = {};
 //////////////////Prototype Definition///////////////////////
Apz.GenHTML.prototype = {
   	genPortion : function(params) {
	 params.genObj = this;
	 params.genUtils = new Apz.GenUtils(this.apz);
	 var screenDetails = params.scr + "__" + params.layout + "__" + params.design;
	 this.apz.scrDefsMap[params.appId][screenDetails].defaultProducts =(!this.apz.isNull(params.designDef.defaultProducts)) ? params.designDef.defaultProducts : "";
	 this.apz.scrDefsMap[params.appId][screenDetails].decorations =(!this.apz.isNull(params.designDef.decorations)) ? params.designDef.decorations : [];
	 this.apz.scrDefsMap[params.appId][screenDetails].parentId = (!this.apz.isNull(params.designDef.parentId)) ? params.designDef.parentId : "";
	 this.apz.scrDefsMap[params.appId][screenDetails].combinedParentId = (!this.apz.isNull(params.designDef.combinedParentId)) ? params.designDef.combinedParentId : "";
	 this.apz.scrDefsMap[params.appId][screenDetails].rootId = (!this.apz.isNull(params.designDef.rootId)) ? params.designDef.rootId : "";
	 this.apz.scrDefsMap[params.appId][screenDetails].headerId = (!this.apz.isNull(params.designDef.headerId)) ? params.designDef.headerId : "";
	 this.apz.scrDefsMap[params.appId][screenDetails].prodName = (!this.apz.isNull(params.designDef.prodName)) ? params.designDef.prodName : "";
	 this.apz.scrDefsMap[params.appId][screenDetails].prodId = (!this.apz.isNull(params.designDef.prodId)) ? params.designDef.prodId : "";
	 this.apz.scrDefsMap[params.appId][screenDetails].prodItemId = (!this.apz.isNull(params.designDef.prodItemId)) ? params.designDef.prodItemId : "";
	 this.apz.scrDefsMap[params.appId][screenDetails].portItemId = (!this.apz.isNull(params.designDef.portItemId)) ? params.designDef.portItemId : "";

	 if(!this.apz.isNull(params.designDef.subProducts)){
	 	this.apz.scrDefsMap[params.appId][screenDetails].subProducts = params.designDef.subProducts;
	 }
  	//alert(params.appId);
	 var noOfChilds = params.designDef.childs.length;
	 for (var i = 0;i < noOfChilds; i++ ){
		 var portion = params.designDef.childs[i];
		 if( portion.widgettype == 'HEADER' ){
			 params.header = portion;
		 }else if(portion.widgettype == 'BODY'){
			 params.body = portion;
		 }else if(portion.widgettype == 'FOOTER'){
			 params.footer = portion;
		 }else if(portion.widgettype == 'SIDEBAR'){
			  params.sidebar = portion;
		 }
	 }
	 this.scrhtml = '<div id="scr__'+ params.appId + '__'+ params.scr + '__main" class="rolepage"  >';
	 this.genSideBar(params);
	 this.genHeader(params);
	 this.genBody(params);
	 this.genFooter(params);
	 this.genToolTip(params);
	 this.scrhtml = this.scrhtml + '</div>';
	 //console.log(this.scrhtml);
	 return this.scrhtml;
	},
	genHeader : function(params){
	var loHeader = params.header;
		if(!this.apz.isNull(params.header.childs)){
			var noOfChilds = loHeader.childs.length;
			var styleAttrbs = this.getStyleAttributes(params);
			var cssClass = "";
			var vartnClass = "";
			var apprnceClass = "";
			var headClasses = "apz-nav apz-nav-horizontal pnb-head pnt-head apz-nav-open" + cssClass + vartnClass + apprnceClass ;
			var headerBehaviour = ' headertype = '+params.header.behaviour;
			if(noOfChilds>0){ 
				this.scrhtml = this.scrhtml + '<nav id="header" class=' + headClasses +headerBehaviour;
				if (!this.apz.isNull(styleAttrbs)) {
					this.scrhtml = this.scrhtml + 'style = ' + styleAttrbs;
				}
				this.scrhtml = this.scrhtml + '>';
				for (var c = 0; c < noOfChilds; c++) {
						var bodyChild = loHeader.childs[c];
						if (bodyChild.type=="GRIDROW") {
							var lgridrow =  bodyChild;
							this.genGridRow(params,lgridrow);
						} else if (bodyChild.type=="POPUP") {
							var popupObj =  bodyChild;
							if (popupObj.widgettype=="MODAL") {
								this.genModal(params,popupObj);
							} else if (popupObj.widgettype=="DIALOG") {
								this.genDialog(params,popupObj);
							} else if (popupObj.widgettype=="POPOVER") {
								this.genPopover(params,popupObj);
							}
						}
				}
				this.scrhtml = this.scrhtml + '</nav>';	
			}	
			//alert('In Header'+ noOfChilds);
		}
		
	},
	genBody : function(params){
		if(!this.apz.isNull(params.body.childs)){
				var widthClass = this.getElementResponsiveWidthClass(params.body.width, "");
				var defltTheme = this.apz.defaultTheme;
				var vartnClass = params.body.variation ? params.body.variation : "";
				var appearanceClass = params.body.appearance? params.body.appearance : " pri";
				var styleAttrbts = this.getStyleAttributes(params);
				if(!this.apz.isNull(styleAttrbts)){
						styleAttrbts = ' style = "'+ styleAttrbts+'"';
				}
				this.scrhtml = this.scrhtml + '<section id="page-body"   class="pagebody pnb-body pnt-body '+ widthClass + vartnClass + '"'+ styleAttrbts+ '>';				
				var noOfChilds = params.body.childs.length;
				for(var ch = 0; ch < noOfChilds; ch++){
					var widgetType= params.body.childs[ch].type;
					if(widgetType == 'GRIDROW'){
						var childObj =  params.body.childs[ch];
						childObj.parent = params.body;
						this.genGridRow(params, childObj);
					}else if(widgetType == 'POPUP'){
						var popupObj = params.body.childs[ch];
						if(popupObj.widgettype == 'MODAL'){
							this.genModal(params,popupObj);
						}else if(popupObj.widgettype == 'DIALOG'){
							this.genDialog(params,popupObj);
						}else if(popupObj.widgettype == 'POPOVER'){
							this.genPopover(params,popupObj);
						}
					}else if(widgetType == 'CHILDSCREEN'){
						if(!this.apz.isNull(params.body.childscreens)){
						  var noOfChildScrs = params.body.childscreens.length;
						  for(var chs = 0; chs < noOfChildScrs; chs ++){
							  this.scrhtml = this.scrhtml + '<div id=' + params.appId + '__' + params.scr + '__receiver></div>';
						  }
						}
					}
				}
		
		this.scrhtml = this.scrhtml + '</section>';		
			//alert('In Body');
		}
		
	},
	genFooter : function(params){
		if(!this.apz.isNull(params.footer.childs)){
			var genObj = params.genObj;
			var lfooter = params.footer;
			var cssClasses = "";
			var noOfChilds = lfooter.childs.length;
			var styleAttrbs = this.getStyleAttributes(params);
			var vartnClass = !this.apz.isNull(lfooter.variation) ? " " + lfooter.variation : "";
			var footerClasses = this.getFooterClasses(lfooter);
			var footerBehaviour = " footertype = '"+lfooter.behaviour+"'";
			if (!this.apz.isNull(lfooter.cssclasses)) {
				cssClasses = " " + lfooter.cssclasses;
			}
			genObj.scrhtml+= "<nav id='footer'    class='" + footerClasses + "'"+footerBehaviour+"";
					if (!styleAttrbs=="") {
						genObj.scrhtml+= "style='" + styleAttrbts + "'";
					}
					genObj.scrhtml+=">";
					for (var c = 0; c < noOfChilds; c++) {
						var childObj = lfooter.childs[c];
						if (childObj.type=="GRIDROW") {
							var lgridrow = childObj;
							this.genGridRow(params, childObj);
						} else if (childObj.type=="POPUP") {
							var popupObj = childObj;
							if (popupObj.widgettype=="MODAL") {
								this.genModal(params,popupObj);
							} else if (popupObj.widgettype=="DIALOG") {
								this.genDialog(params, popupObj);
							} else if (popupObj.widgettype=="POPOVER") {
								this.genPopover(params, popupObj);
							}
						}
					}
					genObj.scrhtml+= "</nav>";
			//alert('In Footer');
		}
		
	},
	genSideBar : function(params){
		var loSideBar = params.sidebar;
		var genObj = params.genObj;
		if(!this.apz.isNull(params.sidebar.childs)){
			var noOfChilds = params.sidebar.childs.length;
			//alert('In Sidebar');
			var styleAttrbts = this.getStyleAttributes(params);
			var backdropClass = "";
			var sideBarClasses = this.getSidebarClasses(loSideBar);
			var sidebarBehaviour = " sidebartype = '"+loSideBar.behaviour+"'";
			if (noOfChilds > 0) {
				genObj.scrhtml+= "<nav id='sidebar'   class='" + sideBarClasses + "' "+sidebarBehaviour+"";
				if (!styleAttrbts=="") {
					genObj.scrhtml+= "style='" + styleAttrbts + "'";
				}
				genObj.scrhtml+= ">";
				for (var c = 0; c < noOfChilds; c++) {
					var bodyChild = loSideBar.childs[c];
					if (bodyChild.type=="GRIDROW") {
						var lgridrow =  bodyChild;
						this.genGridRow(params,lgridrow);
					} else if (bodyChild.type=="POPUP") {
						var popupObj =  bodyChild;
						if (popupObj.widgettype=="MODAL") {
							this.genModal(params,popupObj);
						} else if (popupObj.widgettype=="DIALOG") {
							this.genDialog(params,popupObj);
						} else if (popupObj.widgettype=="POPOVER") {
							this.genPopover(params,popupObj);
						}
					}
				}
				genObj.scrhtml+=  "</nav>";
			}
			if ((loSideBar.state=="OPEN") && (!loSideBar.behaviour=="STATIC")) {
				backdropClass = "in";
			}
			genObj.scrhtml+= "<div id='backdrop' class='backdrop" + backdropClass + "'>";
			genObj.scrhtml+= "</div>";
		}
		
	},
	genGridRow : function(params, gridRowObj){
		var id = params.appId + "__" + params.scr + "__" + gridRowObj.name;
		var gridRowClasses = this.getGridRowClassAttributes(gridRowObj);
		var controlId = gridRowObj.controlid;
		var wrapCenterCls = "";
		if (gridRowObj.centercolumns == ('YES')) {
			wrapCenterCls = " colcenter";
		}
		if (!this.apz.isNull(controlId)) {
			controlId = 'apzcontrol = "' + controlId + '" ';
		} else
			controlId = "";
		
			this.scrhtml = this.scrhtml + '<div id="' + id + '" class="grb'+ wrapCenterCls + gridRowClasses +'" ' + controlId +'>';
		//console.log(JSON.stringify(gridRowObj));
			if(!this.apz.isNull(gridRowObj.childs)){
				var noofGridCols = gridRowObj.childs.length;
			//	alert(noofGridCols);
				for (var g = 0; g < noofGridCols; g++) {
					var childObj = gridRowObj.childs[g];
					childObj.parent = gridRowObj
					this.genColumn(params, childObj);
				}
			}
			this.scrhtml = this.scrhtml +'</div>';
		
		
	},
	genColumn : function(params, gridColObj){
		var id = params.appId + "__" + params.scr + "__" + gridColObj.name;
		var widthClass = "";
		var draggableClass = "";
		var laligncolumncenter = "";
		var llayoutgridcolclassattrs = this.getGridColClassAttributes(gridColObj);
		var llayoutgridcolstyleattrs = this.getGridColStyleAttributes(gridColObj);
		if(!this.apz.isNull(llayoutgridcolstyleattrs)){
			llayoutgridcolstyleattrs = ' style = "'+ llayoutgridcolstyleattrs+'"';
		}
		var lcontrolid = this.getControlIdforWidget(gridColObj);
		var lnoofchilds = gridColObj.childs ? gridColObj.childs.length : 0;
			widthClass = this.getColumnWidthClass(gridColObj);
			this.scrhtml = this.scrhtml + '<div id="'+ id +'"'+ lcontrolid;
			this.scrhtml = this.scrhtml + ' class="'+ widthClass +''+ laligncolumncenter +''+ llayoutgridcolclassattrs +''+ draggableClass + '" '+ llayoutgridcolstyleattrs +'>';
			for (var c = 0; c < lnoofchilds; c++) {
				var childObj= gridColObj.childs[c];
				childObj.parent = gridColObj;
				if (childObj.type == ('GRIDROW')) {
					this.genGridRow(params, childObj);
				} else if (childObj.type == ('PANEL')) {
					this.genPanel(params, childObj);
				}
			}
			this.scrhtml = this.scrhtml +  '</div>';
		
	},
	genPanel : function(params, panelObj){
			var id = params.appId + "__" + params.scr + "__" + panelObj.name;
			var title = "";
			var currentClass = '';
			var panelSection = '';
			var cssClasses = "";
			var controlId = this.getControlIdforWidget(panelObj);
			var paneSecControlId = "";
			var nrspClass = "";
			var vartnClass = panelObj.variation ? " "+panelObj.variation : "";
			var aprnceClass = panelObj.appearance ?  " "+panelObj.appearance : " pri";
			var visiblityClass = this.getShowNoneClass(panelObj.options);
			var marginClass = this.getPanelMarginClass(panelObj);
			var panelStyleAttr = this.getPanelStyleAttr(panelObj);
			var isSelected = "";
			var respTab = false;
			if (panelObj.responsive ==('YES') || this.apz.isNull(panelObj.responsive)) {
				respTab = true;
			}
			var panelObjTitle = this.apz.getLabel(panelObj.title);
			if (!this.apz.isNull(panelObj.cssclasses)) {
				cssClasses = " " + panelObj.cssclasses;
			}
			
			if (panelObj.widgettype == ('TAB')) {
				var orientationClass = panelObj.orientation == ('VERTICAL') ? " ver" : " hor";
				var noofChilds = panelObj.childs.length;
				var visibleClass = "";
				if (!respTab) {
					nrspClass = " nrsp";
				}
				this.scrhtml = this.scrhtml + '<div class="plt-tabs ' + marginClass + '" ' + panelStyleAttr + '><div id="' + id + '_div" ' + controlId + ' class="pst-tabs psb-tabs ' + nrspClass + orientationClass + aprnceClass + cssClasses + visiblityClass + vartnClass+ '" ' + this.getPanelEvents(panelObj) + '>';
				this.scrhtml = this.scrhtml +  '<ul id="'+ id +'_ul" class="tabs" role="tablist">';
				for (var k = 0; k < noofChilds; k++) {
					panelSection = panelObj.childs[k];
					id = params.appId + "__" + params.scr + "__" + panelSection.name;
					if (k == 0) {
						currentClass = "current";
						isSelected = "true";
					} else {
						currentClass = "";
						isSelected = "false";
					}
					visibleClass = this.getShowNoneClass(panelSection.options);
					if (!this.apz.isNull(panelSection.controlid)) {
						paneSecControlId = ' apzcontrol = "' + params.appId + '__' + params.scr + '__'+ panelSection.controlid+'"';
					}					
					this.scrhtml = this.scrhtml + '<li id="' + id + '_li" '+ paneSecControlId +' class="' + visibleClass + currentClass + '" aria-selected="'+isSelected+'" aria-expanded="'+isSelected+'" aria-controls="'+id+'_li" role="tab" '+ this.getPanelSectionEvents(panelSection, panelObj) +'>';
					if (!this.apz.isNull(panelSection.icon)) {
						this.scrhtml = this.scrhtml + '<span><svg role="presentation" aria-hidden="true" class="ett-icon "' + panelSection.icon + '" px24"><use xlink:href="#"' + panelSection.icon + '"></use></svg></span>';
					}
					
					title = this.apz.getLabel(panelSection.title);
					this.scrhtml = this.scrhtml + '<span><a href="javascript:;">' + title + '</a></span>';					
					this.scrhtml = this.scrhtml + '</li>';
				}
				this.scrhtml = this.scrhtml + '</ul>';
				if(respTab) {
					this.scrhtml = this.scrhtml + '<div class="tabs-ctr">';
				}
				this.genTab(params, panelObj);
				this.scrhtml = this.scrhtml + '</div></div>';
				if(respTab) {
				  this.scrhtml = this.scrhtml + '</div>';
				}
			} else if (panelObj.widgettype == ('CAROUSEL')) {
				var loopAttr = "";
				if (panelObj.loop == ('YES')) {
					loopAttr = 'data-loop = "y"';
				} else {
					loopAttr = 'data-loop = "n"';
				}
				this.scrhtml = this.scrhtml + '<div class="plt-caro ' + marginClass + aprnceClass + '" ' + panelStyleAttr + '>';
				if (!this.apz.isNull(panelObj.icon) || !this.apz.isNull(panelObjTitle)) {
					this.scrhtml = this.scrhtml + '<ul class="ttl"><li class="icn">';
					this.scrhtml = this.scrhtml + '<svg role="presentation" aria-hidden="true" class="icon '+ panelObj.icon +' px24"><use xlink:href="#"'+ panelObj.icon +'"></use></svg></li>';
					this.scrhtml = this.scrhtml + '<li class="lbl"><h4>"'+ panelObjTitle +'"</h4></li></ul>';
				}
				this.scrhtml = this.scrhtml +' <div id="' + id + '_div" '  + controlId +' class="swiper-container'+ cssClasses + visiblityClass + vartnClass +'" ' + loopAttr + ' ' + this.getPanelEvents(panelObj) + '>';
				this.scrhtml = this.scrhtml + '<ul id="' + id + '_ul" class="swiper-wrapper">';
				this.genCarousel(params, panelObj);
				this.scrhtml = this.scrhtml + '</ul></div></div>';
			} else if (panelObj.widgettype == ('ACCORDION')) {
				this.scrhtml = this.scrhtml +'<div id="' + id + '_div" ' + controlId + ' '+ panelStyleAttr +' class="plt-acco' + marginClass + cssClasses + visiblityClass + aprnceClass + vartnClass + '" role = "tablist" '+ this.getPanelEvents(panelObj) + '>';
				this.genAccordian(params, panelObj);
				this.scrhtml = this.scrhtml + '</div>';
			} else if (panelObj.widgettype == ('COLLAPSIBLE')) {
				this.scrhtml = this.scrhtml + '<div id="' + id + '_div"' + controlId +' '+ panelStyleAttr +' class="plt-coll'+ marginClass + cssClasses + visiblityClass + aprnceClass + vartnClass + '" role = "tablist" '+ this.getPanelEvents(panelObj) +'>';
				this.genCollapsibleBox(params, panelObj);
				this.scrhtml = this.scrhtml + '</div>';
			} else if (panelObj.widgettype == ('SIMPLE')) {
				this.scrhtml = this.scrhtml + '<div id="'+ id + '_div" ' + controlId +'  '+ panelStyleAttr +' class="plt-simp' + marginClass + cssClasses + visiblityClass + vartnClass + '" '+ this.getPanelEvents(panelObj) + '>';
				this.genSimplePanel(params, panelObj);
				this.scrhtml = this.scrhtml + '</div>';
			}
		

	},
	genSimplePanel : function (params, panelObj){
			var title = "";
			var icon = "";
			var id = "";
			var cssClasses = "";
			var controlId = "";
			var noofChilds = panelObj.childs.length;
			for (var p = 0; p < noofChilds; p++) {
				var panelSection = panelObj.childs[p];
				panelSection.parent = panelObj;
				id = params.appId + "__" + params.scr + "__" + panelSection.name;
				icon = panelSection.icon;
				var visibilityClass = this.getShowNoneClass(panelSection.options);
				if (!this.apz.isNull(panelSection.controlid)) {
						controlId = ' apzcontrol = "' + params.appId + '__' + params.scr + '__'+ panelSection.controlid+'"';
				}
				var spinnerClasss = this.getSpinnerClass(panelSection);
				var vartnClass = panelSection.variation ? panelSection.variation : "";
				var theme = panelSection.appearance ? panelSection.appearance : " pri";
				var psTheme = panelSection.theme ? panelSection.theme : "";
				if (!this.apz.isNull(panelSection.cssclasses)) {
					cssClasses = " " + panelSection.cssclasses;
				}
				title = this.apz.getLabel(panelSection.title);
				var padCls = this.getPanelSectionPaddingClass(panelSection);
				var panelStyleAttr = this.getPanelSectionStyleAttr(panelSection);
				this.scrhtml = this.scrhtml +  '<div id="' + id +'" ' + controlId + ' class="pst-simp '+ " " +theme + visibilityClass + spinnerClasss + cssClasses + vartnClass + " "+ psTheme + padCls + '" ' + panelStyleAttr + ' ' + this.getPanelSectionEvents(panelSection, panelObj) + '>';
				if (!this.apz.isNull(title) || !this.apz.isNull(icon)) {
					this.scrhtml = this.scrhtml + '<ul id="' + id +'_ul" class="ttl">';
					if (!this.apz.isNull(icon)) {
						this.scrhtml = this.scrhtml +'<li class="icn">';
						this.scrhtml = this.scrhtml +'<svg  role="presentation" aria-hidden="true" class="icon "'+ icon +'" px24"><use xlink:href="#"'+ icon +'"></use></svg>';
						this.scrhtml = this.scrhtml + '</li>';
					}
					if (!this.apz.isNull(title)) {
						this.scrhtml = this.scrhtml +'<li class="lbl">';
					//	title = GenUtils.getLITDesc(language, panelSection.title, appData.lits);
						this.scrhtml = this.scrhtml +'<h4>' + title + '</h4>';
						this.scrhtml = this.scrhtml +'</li>';
					}
					this.scrhtml = this.scrhtml +'</ul>';
				}
				if(!this.apz.isNull(panelSection.childs)){
					//alert(JSON.stringify(panelSection.childs));
					var planePaneChilds = panelSection.childs.length;
					for (var c = 0; c < planePaneChilds; c++) {
						var childObj = panelSection.childs[c];
						childObj.parent = panelSection;
						if (childObj.type == ('GRIDROW')) {
							this.genGridRow(params, childObj);
						} else if (childObj.type == ('CONTAINER')) {

							this.genContainer(params, childObj)
						}
					}
				}
				var spinnerContent = this.getSpinnerDiv(panelSection, id);
				if (!this.apz.isNull(spinnerContent)) {
					this.scrhtml = this.scrhtml + spinnerContent;
				}
				this.scrhtml = this.scrhtml + '</div>';
			}
		

	},
	genTab : function(params, paneData){
		var id = "";
		var title = "";
		var cssClasses = "", vartnClass = "";
		var visiblityClass = "";
		var stateCls = " expand";
		var styleCls = "";
		var noofChilds = paneData.childs.length;
		var iconCls = "icon-chevron-down";
		for (var p = 0; p < noofChilds; p++) {
			panelSection = paneData.childs[p];
			//panelSection.parent = panelObj;
			panelSection.parent = paneData;
			id = params.appId + "__" + params.scr + "__" + panelSection.name;;
			var themeApplied = false;
			var controlId = "";
			if (!this.apz.isNull(panelSection.controlid)) {
				controlId = ' apzcontrol = "' + params.appId + '__' + params.scr + '__'+ panelSection.controlid+'"';
			}
			var iconName = panelSection.icon;
			var currClass = "";
			var isSelected = "false";
			var isHidden = "true";
			if (paneData.childs[0].name == (panelSection.name)) {
				currClass = " current";
				isSelected = "true";
				isHidden = "false";
			}
			if (!this.apz.isNull(panelSection.cssclasses)) {
				cssClasses = " " + panelSection.cssclasses;
			}
			if (panelSection.state == ('OPEN')) {
				if (!themeApplied) {
					themeApplied = true;
				}
			}
			vartnClass = panelSection.variation ? " "+panelSection.variation : " ";
			visiblityClass = this.getShowNoneClass(panelSection.options);
			var spinnerClasss = this.getSpinnerClass(panelSection);
			var theme =  panelSection.appearance ? " "+panelSection.appearance : " pri";
			var padCls = this.getPanelSectionPaddingClass(panelSection);
			var panelStyleAttr = this.getPanelSectionStyleAttr(panelSection);
			if(paneData.responsive == ('YES') || this.apz.isNull(paneData.responsive)) {
				this.scrhtml = this.scrhtml + '<div class="acco" role="tabpanel" aria-labelledby="'+id+'_li" aria-hidden="'+isHidden+'">';
				this.scrhtml = this.scrhtml + '<ul id="' + id + '_ul" class="ttl'+ currClass + '" aria-selected="'+isSelected+'" aria-expanded="'+isSelected+'" aria-controls="'+id+'_ul" role="tab" "'+ this.getPanelSectionEvents(panelSection, paneData) + '">';
				if (!this.apz.isNull(iconName)) {
					this.scrhtml = this.scrhtml +  '<li id="' + id + '_icon_li" class="icn">';
				this.scrhtml = this.scrhtml + '<svg role="presentation" aria-hidden="true" class="ett-icon '+ iconName + '" px24"><use xlink:href="#'+ iconName +'"></use></svg>';
					this.scrhtml = this.scrhtml + '</li>';
				}
				this.scrhtml = this.scrhtml + '<li id="' + id + '_title_li" class="lbl">';
				title = this.apz.getLabel(panelSection.title);
					if (this.apz.isNull(title)) {
						title = "&nbsp";
					}
				this.scrhtml = this.scrhtml +'<h4 class="' + stateCls + '">"' + title + '"</h4>';
				
				this.scrhtml = this.scrhtml +'</li>';
				this.scrhtml = this.scrhtml + '<li id="' + id + '_tools_li" class="tls">';
				this.scrhtml = this.scrhtml + '<a class="' + stateCls + styleCls +'"><svg  role="presentation" aria-hidden="true" class="ett-icon ' + iconCls + ' px24"><use xlink:href="#'+ iconCls +'"></use></svg></a>';
				this.scrhtml = this.scrhtml + '</li>';
				this.scrhtml = this.scrhtml + '</ul>';
			}
			if (paneData.childs[0].name == (panelSection.name)) {
				this.scrhtml = this.scrhtml + '<div id="'+ id +'" data-apr="lcol12-ptbox" '+ controlId +' class="tabcontent active'+ theme + spinnerClasss + visiblityClass	+ cssClasses + vartnClass + padCls +'" aria-hidden="'+isHidden+'" '+ panelStyleAttr +'>';
			} else {
				this.scrhtml = this.scrhtml + '<div id="'+ id +'" data-apr="lcol12-ptbox" '+ controlId +' class="tabcontent u-relative'+ spinnerClasss + visiblityClass	+ cssClasses + vartnClass + padCls + '" aria-hidden="'+isHidden+'" ' + panelStyleAttr +'>';
			}
			var tabChilds = !this.apz.isNull(panelSection.childs) ? panelSection.childs.length : 0;
			for (var c = 0; c < tabChilds; c++) {
				var childObj = panelSection.childs[c];
				childObj.parent = panelSection;
				if (childObj.type == ('GRIDROW')) {
					this.genGridRow(params, childObj);
				} else if (childObj.type == ('CONTAINER')) {
					this.genContainer(params, childObj);
				}
			}
			var spinnerContent = this.getSpinnerDiv(panelSection, id);
			if (!this.apz.isNull(spinnerContent)) {
				this.scrhtml = this.scrhtml + spinnerContent;
			}
			this.scrhtml = this.scrhtml + '</div>';
			if(paneData.responsive == ('YES') || this.apz.isNull(paneData.responsive)) {
				this.scrhtml = this.scrhtml + '</div>';
			}
		}
	}, genCarousel : function(params,paneData){
		var noofChilds = paneData.childs.length;
		var cssClasses = "";
		var controlId = "";
		var appearanceClass = "";
		var panelSection = null;
		for (var p = 0; p < noofChilds; p++) {
			panelSection = paneData.childs[p];
			var id = params.appId + "__" + params.scr + "__" + panelSection.name;
			var controlId = "";
			if (!this.apz.isNull(panelSection.controlid)) {
				controlId = ' apzcontrol = "' + params.appId + '__' + params.scr + '__'+ panelSection.controlid+'"';
			}
			if (!this.apz.isNull(panelSection.cssclasses)) {
				cssClasses = " " + panelSection.cssclasses;
			}
			appearanceClass = !this.apz.isNull(panelSection.appearance) ? " " + panelSection.appearance : "";
			var spinnerClasss = this.getSpinnerClass(panelSection);
			var visibilityClass = this.getShowNoneClass(panelSection.options);
			var vartnClass = !this.apz.isNull(panelSection.variation) ? " " + panelSection.variation : "";
			var padCls = this.getPanelSectionPaddingClass(panelSection);
			var panelStyleAttr = this.getPanelSectionStyleAttr(panelSection);
			this.scrhtml+= "<li id='" + id + "' " + controlId + "class='pst-caro swiper-slide" + cssClasses + visibilityClass + vartnClass + padCls + "' " + panelStyleAttr
					+ " " + this.getPanelSectionEvents(panelSection, paneData) + ">";
			var carouselChilds = panelSection.childs ? panelSection.childs.length : 0;
			for (var c = 0; c < carouselChilds; c++) {
				var childObj = panelSection.childs[c];
				if (childObj.type=="GRIDROW") {
					var pgridrow =  dataObj;
					this.genRow(params,childObj);
				} else if (childObj.type=="CONTAINER") {
					this.genContainer(params, childObj);
				}
			}
			var spinnerContent = this.getSpinnerDiv(panelSection, id);
			if (!this.apz.isNull(spinnerContent)) {
				this.scrhtml+=  spinnerContent;
			}
			this.scrhtml+= "</li>";
			var k = paneData.childs.length;
			if (paneData.childs[k - 1].name==panelSection.name) {
				this.scrhtml+=  "</ul>";
				this.scrhtml+=  "<div id='" + id + "_pagination' " + "class='swiper-pagination" + spinnerClasss + vartnClass + "' ></div>";
				this.scrhtml+=  "<div id='" + id + "_next' "
						+ "class='swiper-button-next' ><button class='ett-bttn tsp med' aria-label='Next'><svg aria-hidden='true' class='ett-icon icon-arrow-right px24'><use xlink:href='#icon-arrow-right'></use></svg></button></div>";
				this.scrhtml+=  "<div id='" + id + "_prev' "
						+ "class='swiper-button-prev'><button class='ett-bttn tsp med' aria-label='Previous'><svg aria-hidden='true' class='ett-icon icon-arrow-left px24'><use xlink:href='#icon-arrow-left'></use></svg></button></div>";
			}
		}

	}, genAccordian : function(params, paneData) {
		var title = "";
		var id = "";
		var cssClasses = "";
		var noofChilds = paneData.childs.length;
		var controlId = "";
		if (!this.apz.isNull(paneData.controlid)) {
				controlId = ' apzcontrol = "' + params.appId + '__' + params.scr + '__'+ paneData.controlid+'"';
		}
		var appearance = !this.apz.isNull(paneData.appearance) ? " " + paneData.appearance : " pri";
		for (var p = 0; p < noofChilds; p++) {
			panelSection = paneData.childs[p];
			var themeApplied = false;
			id = params.appId + "__" + params.scr + "__" + panelSection.name;
			var iconName = panelSection.icon;
			var spinnerClasss = this.getSpinnerClass(panelSection);
			var vartnClass = !this.apz.isNull(panelSection.variation) ? " " + panelSection.variation : "";
			var stateCls = " expand";
			var iconCls = "icon-chevron-down";
			var styleCls = "";
			var visibilityClass = this.getShowNoneClass(panelSection.options);
			var hthClass = panelSection.state == "OPEN" ? " htou" : " height-transition-hidden";
			var styleAttr = panelSection.state == "OPEN" ? " style = \"max-height:1200px\"" : " style = \"max-height:0px\"";
			if (panelSection.state == "OPEN") {
				if (!themeApplied) {
					stateCls = " collapse";
					styleCls = " acc-hd";
					themeApplied = true;
				}
			}
			if (!this.apz.isNull(panelSection.cssclasses)) {
				cssClasses = " " + panelSection.cssclasses;
			}
			var isSelected = panelSection.state == "OPEN" ? "true" : "false";
			var isHidden = panelSection.state == "OPEN" ? "false" : "true";
			var plSecApprnc = !this.apz.isNull(panelSection.appearance) ? " " + panelSection.appearance : " pri";
			var padCls = this.getPanelSectionPaddingClass(panelSection);
			var panelStyleAttr = this.getPanelSectionStyleAttr(panelSection);
			this.scrhtml += "<div id=\"" + id + "_div\" " + controlId + "class=\"pst-acco pst acco" + appearance + visibilityClass + plSecApprnc + spinnerClasss + cssClasses
					+ vartnClass + padCls + "\" " + panelStyleAttr + " " + this.getPanelSectionEvents(panelSection, paneData) + ">";
			this.scrhtml += "<ul id=\"" + id + "_ul\" class=\"ttl" + styleCls + "\" aria-selected=\""+isSelected+"\" aria-expanded=\""+isSelected+"\" aria-controls=\""+id+"_ul\" onclick=\"apz.accordionAction(this)\">";
			if (!this.apz.isNull(iconName)) {
				this.scrhtml += "<li id=\"" + id + "_icon_li\" class=\"icn\">";
				this.scrhtml += "<svg role=\"presentation\" aria-hidden=\"true\" class=\"icon " + iconName + " px24\"><use xlink:href=\"#" + iconName + "\"></use></svg>";
				this.scrhtml += "</li>";
			}
			this.scrhtml += "<li id=\"" + id + "_title_li\" class=\"lbl\">";
				title = this.apz.getLabel(panelSection.title);
				if (this.apz.isNull(title)) {
					title = "&nbsp";
				}
			this.scrhtml += "<h4 class=\"" + stateCls + "\">" + title + "</h4>";
			this.scrhtml += "</li>";
			this.scrhtml += "<li id=\"" + id + "_tools_li\" class=\"tls\">";
			this.scrhtml += "<a class=\"" + stateCls + styleCls + "\" aria-label=\""+iconCls+"\" role=\"button\"><svg aria-hidden=\"true\" class=\"icon " + iconCls + " px24\"><use xlink:href=\"#" + iconCls + "\"></use></svg></a>";
			this.scrhtml += "</li>";
			this.scrhtml += "</ul>";
			this.scrhtml += "<div id=\"" + id + "\" " + "class=\"ctr height-transition" + hthClass + spinnerClasss + "\" aria-hidden=\""+isHidden+"\" aria-labelledby=\""+id+"_ul\" role=\"tabpanel\"" + styleAttr + "" + ">";
			var accordianChilds = panelSection.childs.length;
			for (var c = 0; c < accordianChilds; c++) {
				var childObj = panelSection.childs[c];
				childObj.parent = panelSection;
				if (childObj.type == ('GRIDROW')) {
					this.genGridRow(params, childObj);
				} else if (childObj.type == ('CONTAINER')) {
					this.genContainer(params, childObj);
				}
			}
			var spinnerContent = this.getSpinnerDiv(panelSection, id);
			if (!this.apz.isNull(spinnerContent)) {
				this.scrhtml += spinnerContent;
			}
			this.scrhtml += "</div>";
			this.scrhtml += "</div>";
		}
	},
	genCollapsibleBox : function(params, paneData) {
		var id = "";
		var title = "";
		var cssClasses = "";
		var noofChilds = paneData.childs.length;
		var controlId = "";
		var panelSection = "";
		var appearance = !this.apz.isNull(paneData.appearance) ? " " + paneData.appearance : " pri";
		for (var p = 0; p < noofChilds; p++) {
			panelSection = paneData.childs[p];
			var stateCls = " expand";
			var iconCls = "icon-chevron-down";
			var vartnClass = !this.apz.isNull(panelSection.variation) ? " " + panelSection.variation : "";
			if (!this.apz.isNull(panelSection.controlid)) {
				controlId = ' apzcontrol = "' + params.appId + '__' + params.scr + '__'+ panelSection.controlid+'"';
			}
			var hthClass = (panelSection.state=="OPEN") ? " htou" : " height-transition-hidden";
			var styleAttr = (panelSection.state=="OPEN") ? ' style = "max-height:1200px"' : ' style = "max-height:0px"';
			if (this.apz.isNull(panelSection.cssclasses)) {
				cssClasses = " " + panelSection.cssclasses;
			}
			var icon = panelSection.icon;
			id = params.appId + "__" + params.scr + "__" + panelSection.name;
			if (panelSection.state=="OPEN") {
				stateCls = " collapse";
			}
			var visibilityClass = this.getShowNoneClass(panelSection.options);
			var spinnerClasss = this.getSpinnerClass(panelSection);
			var aprnceClass = !this.apz.isNull(panelSection.appearance) ? " " + panelSection.appearance : " pri";
			var padCls = this.getPanelSectionPaddingClass(panelSection);
			var panelStyleAttr = this.getPanelSectionStyleAttr(panelSection);
			var isSelected = (panelSection.state=="OPEN") ? "true" : "false";
			var isHidden = (panelSection.state=="OPEN") ? "false" : "true";
			this.scrhtml = this.scrhtml + '<div id="' + id + '_div" ' + controlId + 'class="pst-coll pst' + appearance + aprnceClass + spinnerClasss + cssClasses + vartnClass
					+ visibilityClass + padCls + '" ' + panelStyleAttr + '>';
			this.scrhtml = this.scrhtml + '<ul id="' + id + '_ul" class="ttl" aria-selected="'+isSelected+'" aria-expanded="'+isSelected+'" aria-controls="'+id+'_ul" onclick="apz.collapsibleAction(this)">';
			if (this.apz.isNull(icon)) {
				this.scrhtml = this.scrhtml + '<li id="' + id + '_li1" class="icn">';
				this.scrhtml = this.scrhtml + '<svg role="presentation" aria-hidden="true" class="icon ' + icon + ' px24"><use xlink:href="#' + icon + '"></use></svg>';
				this.scrhtml = this.scrhtml + "</li>";
			}
			this.scrhtml = this.scrhtml + '<li id="' + id + '_li2" class="lbl">';
			title = this.apz.getLabel(panelSection.title)
			if (this.apz.isNull(title)) {
				title = "&nbsp";
			}
			this.scrhtml = this.scrhtml + '<h4 class="' + stateCls + '">' + title + '</h4>';
			this.scrhtml = this.scrhtml + "</li>";
			this.scrhtml = this.scrhtml + '<li id="' + id + '_li3" class="tls">';
			this.scrhtml = this.scrhtml + '<a class="' + stateCls + '"><svg role="presentation" aria-hidden="true" class="icon ' + iconCls + ' px24"><use xlink:href="#' + iconCls + '"></use></svg></a>';
			this.scrhtml = this.scrhtml + "</li>";
			this.scrhtml = this.scrhtml + "</ul>";
			this.scrhtml = this.scrhtml + 
					'<div id="' + id + '" class="ctr height-transition' + hthClass + spinnerClasss + '" aria-hidden="'+isHidden+'" aria-labelledby="'+id+'_ul" role="tabpanel" ' + styleAttr + '' + ' ' + this.getPanelSectionEvents(panelSection, paneData) + '>';
			var collapsibleChilds = panelSection.childs.length;
			for (var c = 0; c < collapsibleChilds; c++) {
				var childObj = panelSection.childs[c];
				childObj.parent = panelSection;
				if (childObj.type == ('GRIDROW')) {
					this.genGridRow(params, childObj);
				} else if (childObj.type == ('CONTAINER')) {
					this.genContainer(params, childObj);
				}
			}
			var spinnerContent = this.getSpinnerDiv(panelSection, id);
			if (!this.apz.isNull(spinnerContent)) {
				this.scrhtml = this.scrhtml + spinnerContent;
			}
			this.scrhtml = this.scrhtml + "</div>";
			this.scrhtml = this.scrhtml + "</div>";
		}
	}, genContainer : function(params, cntrObj){
		var cntrGenerator = new Apz.GenContainer(this.apz);
		cntrGenerator.genContainer(params, cntrObj);
	},genModal :function(params,popupData){
			//String modalName = popupData.name;
		var genObj = params.genObj;
		var modalName = params.appId + '__'+ params.scr + "__" + popupData.name;
		var noOfGridRows = popupData.childs.length;
		var widthClass = this.getColumnWidthClass(popupData);
		var cssClasses = "";
		var vartnClass = !this.apz.isNull(popupData.variation) ? " " + popupData.variation : ""; 
		var draggableClass = "";
		var title = this.apz.getLabel(popupData.title);
		if (!this.apz.isNull(popupData.cssclasses)) {
			cssClasses = " " + popupData.cssclasses;
		}
		if (popupData.draggablemodal=="Y") {
			draggableClass = " modal-draggable";
		}
		genObj.scrhtml+="<div id='" + modalName + "' class='modal fade srb " + cssClasses + vartnClass + "' role='dialog' aria-labelledby='"+modalName+"Label' aria-hidden='true'>";
		genObj.scrhtml+= "<div id='" + modalName + "_window' class='modal-window" + widthClass + draggableClass + "' role='document'>";
		genObj.scrhtml+= "<ul class='modal-header'><li>";
		genObj.scrhtml+="<h1>" + title + "</h1>";
		genObj.scrhtml+= "</li><li>";
		genObj.scrhtml+= "<button id='" + modalName
				+ "_close' class='close ett-bttn tsp med' type='button' data-dismiss='modal' aria-label='Close'><svg aria-hidden='true' class='ett-icon icon-remove px24'><use xlink:href='#icon-remove'></use></svg></button></li></ul>";
		genObj.scrhtml+= "<div id='" + modalName + "_content' class='modal-cnt'>";
		for (var g = 0; g < noOfGridRows; g++) {
			var lgridrow = popupData.childs[g];
			this.genGridRow(params, lgridrow);
		}
		genObj.scrhtml+= "</div>";
		if (popupData.modalfooter=="Y") {
			genObj.scrhtml+= "<ul id='" + modalName + "_footer' class='modal-footer'><li id='" + modalName + "_li'>";
			genObj.scrhtml+="<button id='" + modalName + "_footer_close' type='button' class='ett-bttn sec med' data-dismiss='modal'>Close</button>";
			genObj.scrhtml+="<button id='" + modalName + "_footer_save' type='button' class='ett-bttn pri med'>Save changes</button>";
			genObj.scrhtml+= "</li></ul>";
		}
		genObj.scrhtml+= "</div></div>";
	},genDialog:function(params,popupData){
		var genObj = params.genObj;
		var dialogName = params.appId + '__'+ params.scr + "__" + popupData.name;
		var noOfGridRows = popupData.childs.length;
		var widthClass = this.getColumnWidthClass(popupData);
		var cssClasses = "";
		var title = this.apz.getLabel(popupData.title);
		var vartnClass = !this.apz.isNull(popupData.variation) ? " " + popupData.variation : "";
		if (!this.apz.isNull(popupData.cssclasses)) {
			cssClasses = " " + popupData.cssclasses;
		}
		genObj.scrhtml+= "<div id='" + dialogName + "' class='mdl-dlg fade srb " + cssClasses + vartnClass + "' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>";
		genObj.scrhtml+= "<div id='" + dialogName + "_dialog' class='modal-dialog" + widthClass + "'>";
		genObj.scrhtml+= "<h3>" + title + "</h3>";
		for (var g = 0; g < noOfGridRows; g++) {
			var lgridrow = popupData.childs[g];
			this.genGridRow(params,lgridrow);
		}
		genObj.scrhtml+= "<div id='" + dialogName + "_footer' class='modal-footer'>";
		var okTitle = this.apz.getLabel(popupData.oktitle);
		var cancelTitle = this.apz.getLabel(popupData.canceltitle);
		if (popupData.footertype=="OK") {
			genObj.scrhtml+=  "<button id='" + dialogName + "_footer_ok' type='button' class='ett-bttn pri med' data-dismiss='modal'>" + okTitle + "</button>";
		} else if (popupData.footertype=="OKCANCEL") {
			genObj.scrhtml+="<button id='" + dialogName + "_footer_ok' type='button' class='ett-bttn pri med' data-dismiss='modal'>" + okTitle + "</button>";
			genObj.scrhtml+= "<button id='" + dialogName + "_footer_cancel' type='button' class='ett-bttn pri inf med' data-dismiss='modal'>" + cancelTitle + "</button>";
		}
		//}
		genObj.scrhtml+=  "</div>";
		genObj.scrhtml+=  "</div></div>";
	},genPopover : function(params,popupData){
		var genObj = params.genObj;
		var genUtils = params.genUtils;
		var popUpId = params.appId + '__'+ params.scr + "__" + popupData.name;
		var noOfGridRows = popupData.childs.length;
		var cssClasses = "";
		var vartnClass = !this.apz.isNull(popupData.variation) ? " " + popupData.variation : "";
		var widthCls = genUtils.getMaxWidthClass(popupData.width);
		var responsiveWidthClass = genUtils.getWidthClass(popupData);
		var styleAttr = genUtils.getSectionColStyleAttributes(popupData);
		if (!this.apz.isNull(popupData.cssclasses)) {
			cssClasses = " " + popupData.cssclasses;
		}
		genObj.scrhtml+=  "<div id='" + popUpId + "' class='sno " + widthCls + responsiveWidthClass +" " + cssClasses + vartnClass + "' " + styleAttr + " aria-hidden='true'>";
		for (var g = 0; g < noOfGridRows; g++) {
			var lgridrow = popupData.childs[g];
			this.genGridRow(params,lgridrow);
		}
		genObj.scrhtml+=  "</div>";
	},genToolTip: function(params)  {
		 this.scrhtml = this.scrhtml + '<div id="'+ params.appId + "_" + params.scr +'" "tooltip" class="sno" style="width:200px;position: absolute;">';
		 this.scrhtml = this.scrhtml + '<p id="tooltext">';
		 this.scrhtml = this.scrhtml + "</p>";
		 this.scrhtml = this.scrhtml + "</div>";
	},
	getSpinnerClass: function(panelObj) {
		var spinnerClass = "";
		if (panelObj.spinner == ('YES')) {
			spinnerClass = " u-relative";
		}
		return spinnerClass;
	},
	getSpinnerDiv: function(paneData,divId) {
		var sizeClass = "";
		var spinnerDiv = "";
		var tspCls = "with-dimmer";
		if (paneData.spinner == ('YES')) {
			if (paneData.spinnersize == ('LARGE')) {
				sizeClass = " loading--lg";
			} else if (paneData.spinnersize == ('SMALL')) {
				sizeClass = " loading--sm";
			}
			if (paneData.transpspinner == ('YES')) {
				tspCls = "with-backtrans";
			}
			spinnerDiv = '<div id="' + divId + '_spinner" class="loading '+ tspCls + sizeClass +' sno">';
			if (!this.apz.isNull(paneData.spinnertext)) {
				spinnerDiv += "<span>"+paneData.spinnertext+"</span>";
			}
			spinnerDiv += "</div>";
		}
		return spinnerDiv;
	},
	getStyleAttributes:function (portionObj) {
		var styleAttrbts = "";
		if (! portionObj.widgettype == ('BODY')) {
			if (! portionObj.bgcolor == ("")) {
				styleAttrbts = "background-color:#" +  portionObj.bgcolor + ";";
			}
			if (! portionObj.bgimage == ("")) {
				styleAttrbts += 'background-image:url(&quot;styles/themes/' + defaultTheme + '/img/' +  portionObj.bgimage + '&quot;)';
				if (! portionObj.bgrepeat == ("")) {
					styleAttrbts += "background-repeat:" +  portionObj.bgrepeat + ";";
				}
				if (! portionObj.bgattachment == ("")) {
					styleAttrbts += "background-attachment:" +  portionObj.bgattachment + ";";
				}
				if (! portionObj.bgposition == ("")) {
					styleAttrbts += "background-position:" +  portionObj.bgposition + ";";
				}
				if (! portionObj.bgsize == ("")) {
					styleAttrbts += "background-size:" +  portionObj.bgsize + ";";
				}
			}
		}
		if (!this.apz.isNull( portionObj.customwidth)) {
			styleAttrbts = styleAttrbts + " width:" +  portionObj.customwidth +  portionObj.customwidthtype + ";";
		}
		return styleAttrbts;
	},
	getGridRowClassAttributes :function (gridObj){
		var classAttr = "";
		if (!this.apz.isNull(gridObj.cssclasses)) {
			classAttr = classAttr + " " + gridObj.cssclasses;
		}
		if (gridObj.options==('NO')) {
			classAttr = classAttr + " ssp";
		} else if (gridObj.options==("setmargin")) {
			classAttr = classAttr + " set-row-margin";
		}
		if (gridObj.equalize==('YES')) {
			classAttr = classAttr + " equalize";
		}
		if (!this.apz.isNull(classAttr)) {
			classAttr = " " + classAttr;
		}
		var vartnClass = gridObj.variation ? " " + gridObj.variation : "";
		classAttr = classAttr +  vartnClass;
		if(!this.apz.isNull(classAttr)){
			classAttr = " " + classAttr;
		}
		return classAttr;
	},
	getGridColClassAttributes : function (gridColData) {
		var classAttr = "";
		if(!this.apz.isNull(gridColData.labelalignment)){
			//alert('1');
		    classAttr = classAttr + this.getAlignmentClass(gridColData.labelalignment);
		}
		var varCls = gridColData.variation ? gridColData.variation : "";
		if (gridColData.boxshadow == 'YES') {
			classAttr = classAttr + " box-shadow";
		}
		if (!this.apz.isNull(gridColData.cssclasses)) {
			classAttr = classAttr + " " + gridColData.cssclasses;
		}
		if (gridColData.options == 'NO') {
			classAttr = classAttr + " " + "ssp";
		}
		classAttr = classAttr + varCls;
		return classAttr;
	},

	// //Layout Grid column style attributes
	getGridColStyleAttributes: function (gridColData) {
		var styleAttr = "";
		if (!this.apz.isNull(gridColData.maxwidth)) {
			styleAttr = styleAttr + " max-width:" + gridColData.maxwidth + ";";
		}
		if (!this.apz.isNull(gridColData.minwidth)) {
			styleAttr = styleAttr + " min-width:" + gridColData.minwidth + ";";
		}
		//if ((gridColData.customwidth != null) && (!this.apz.isNull(gridColData.customwidth))) {
		if (!this.apz.isNull(gridColData.customwidth)) {	
			styleAttr = styleAttr + " width:" + gridColData.customwidth + gridColData.customwidthtype + ";";
		}
		return styleAttr;
	},
	getPanelMarginClass: function(panelObj) {
		var marginClass = " ";
		var marginType = panelObj.leftmargin;
		var marginLeftValCls = this.getMarginPaddingValueClass(panelObj.leftmarginvalue);
		var marginRightValCls = this.getMarginPaddingValueClass(panelObj.rightmarginvalue);
		var marginTopValCls = this.getMarginPaddingValueClass(panelObj.topmarginvalue);
		var marginBottomValCls = this.getMarginPaddingValueClass(panelObj.bottommarginvalue);
		//if (marginType == ('ALL')) {
		if (marginType == 'ALL') {	
			if (!this.apz.isNull(marginLeftValCls)) {
				//if (marginLeftValCls == ('NULL')) {
				if (marginLeftValCls == 'NULL') {
					marginClass += "ma0";
				} else {
					marginClass += "ma" + marginLeftValCls;
				}
			}
		} else {
			//if (!this.apz.isNull(marginLeftValCls) && !marginLeftValCls == ('NULL')) {
			if (!this.apz.isNull(marginLeftValCls) && marginLeftValCls != 'NULL') {
				marginClass += " ml" + marginLeftValCls;
			}
			//if (!this.apz.isNull(marginRightValCls) && !marginRightValCls == ('NULL')) {
			if (!this.apz.isNull(marginRightValCls) && marginRightValCls != 'NULL') {
				marginClass += " mr" + marginRightValCls;
			}
			//if (!this.apz.isNull(marginTopValCls) && !marginTopValCls == ('NULL')) {
			if (!this.apz.isNull(marginTopValCls) && marginTopValCls != 'NULL') {
				marginClass += " mt" + marginTopValCls;
			}
			//if (!this.apz.isNull(marginBottomValCls) && !marginBottomValCls == ('NULL')) {
			if (!this.apz.isNull(marginBottomValCls) && marginBottomValCls != 'NULL') {
				marginClass += " mb" + marginBottomValCls;
			}
			//if (marginLeftValCls == ('NULL') || marginRightValCls == ('NULL') || marginTopValCls == ('NULL') || marginBottomValCls == ('NULL') || !this.apz.isNull(marginClass)) {
			if (marginLeftValCls == 'NULL' || marginRightValCls == 'NULL' || marginTopValCls == 'NULL' || marginBottomValCls == 'NULL' || !this.apz.isNull(marginClass)) {
				marginClass += " ma0";
			}
		}
		return marginClass;
	},
	 getMarginPaddingValueClass: function (marVal) {
		var valueClass = "";
		if(marVal == ('SMALL')) {
			valueClass = "1";
		} else if(marVal == ('MEDIUM')) {
			valueClass = "2";
		} else if(marVal == ('LARGE')) {
			valueClass = "3";
		} else if (marVal == ('CUSTOM')) {
			valueClass = "";
		} else if (marVal == ('NULL')) {
			valueClass = "NULL";
		}
		return valueClass;
	},
	getPanelStyleAttr:function(panelData) {
		var styleAttr = "";
		var marginType = panelData.leftmargin;
		if (marginType == ('ALL')) {
			if (panelData.leftmarginvalue == ('CUSTOM')) {
				styleAttr += " margin:" + panelData.leftmargincustom + panelData.leftmargincustomtype + ";";
			}
		} else {
			if (panelData.leftmarginvalue == ('CUSTOM')) {
				styleAttr += " margin-left:" + panelData.leftmargincustom + panelData.leftmargincustomtype + ";";
			}
			if (panelData.rightmarginvalue == ('CUSTOM')) {
				styleAttr += " margin-right:" + panelData.rightmargincustom + panelData.rightmargincustomtype + ";";
			}
			if (panelData.topmarginvalue == ('CUSTOM')) {
				styleAttr += " margin-top:" + panelData.topmargincustom + panelData.topmargincustomtype + ";";
			}
			if (panelData.bottommarginvalue == ('CUSTOM')) {
				styleAttr += " margin-bottom:" + panelData.bottommargincustom + panelData.bottommargincustomtype + ";";
			}
		}
		styleAttr = " style='"+styleAttr+"'";
		return styleAttr;
	},
	
	getPanelSectionPaddingClass : function(panelSecObj) {
		var paddingClass = " ";
		var paddingType = !this.apz.isNull(panelSecObj.leftpadding) ? panelSecObj.leftpadding : "ALL";
		var paddingLeftValCls = this.getMarginPaddingValueClass(panelSecObj.leftpaddingvalue);
		var paddingRightValCls = this.getMarginPaddingValueClass(panelSecObj.rightpaddingvalue);
		var paddingTopValCls = this.getMarginPaddingValueClass(panelSecObj.toppaddingvalue);
		var paddingBottomValCls = this.getMarginPaddingValueClass(panelSecObj.bottompaddingvalue);
		if (paddingType == ('ALL')) {
			if (!this.apz.isNull(paddingLeftValCls)) {
				if (paddingLeftValCls == ('NULL')) {
					paddingClass += "pa0";
				} else {
					paddingClass += "pa" + paddingLeftValCls;
				}
			}
		} else {
			if (!this.apz.isNull(paddingLeftValCls) && !paddingLeftValCls == ('NULL')) {
				paddingClass += " pl" + paddingLeftValCls;
			}
			if (!this.apz.isNull(paddingRightValCls) && !paddingRightValCls == ('NULL')) {
				paddingClass += " pr" + paddingRightValCls;
			}
			if (!this.apz.isNull(paddingTopValCls) && !paddingTopValCls == ('NULL')) {
				paddingClass += " pt" + paddingTopValCls;
			}
			if (!this.apz.isNull(paddingBottomValCls) && !paddingBottomValCls == ('NULL')) {
				paddingClass += " pb" + paddingBottomValCls;
			}
			if (paddingLeftValCls == ('NULL') || paddingRightValCls == ('NULL') || paddingTopValCls == ('NULL') || paddingBottomValCls == ('NULL') || !this.apz.isNull(paddingClass)) {
				paddingClass += " pa0";
			}
		}
		return paddingClass;
	},
	
	 getPanelSectionStyleAttr : function (panelSecObj) {
		var styleAttr = "";
		var paddingType = panelSecObj.leftpadding;
		if (paddingType == ('ALL')) {
			if (panelSecObj.leftpaddingvalue == ('CUSTOM')) {
				styleAttr += " padding:" + panelSecObj.leftpaddingcustom + panelSecObj.leftpaddingcustomtype + ";";
			}
		} else {
			if (panelSecObj.leftpaddingvalue == ('CUSTOM')) {
				styleAttr += " padding-left:" + panelSecObj.leftpaddingcustom + panelSecObj.leftpaddingcustomtype + ";";
			}
			if (panelSecObj.rightpaddingvalue == ('CUSTOM')) {
				styleAttr += " padding-right:" + panelSecObj.rightpaddingcustom + panelSecObj.rightpaddingcustomtype + ";";
			}
			if (panelSecObj.toppaddingvalue == ('CUSTOM')) {
				styleAttr += " padding-top:" + panelSecObj.toppaddingcustom + panelSecObj.toppaddingcustomtype + ";";
			}
			if (panelSecObj.bottompaddingvalue == ('CUSTOM')) {
				styleAttr += " padding-bottom:" + panelSecObj.bottompaddingcustom + panelSecObj.bottompaddingcustomtype + ";";
			}
		}
		styleAttr = ' style="'+styleAttr+'"';
		return styleAttr;
	},
	getPanelEvents :function(panelObj) {
		var eventStr = "";
		var eventobj = null;
		if (panelObj.events) {
			for (var i = 0; i < panelObj.events.length; i++) {
				eventobj = panelObj.events[i];
				if (!this.apz.isNull(eventobj["function"]) && !this.apz.isNull(eventobj.name)) {
					var lupdatedfunction = eventobj["function"].replace(/"|'/g, '&quot;');
					eventStr += eventobj.name + "=\"" + lupdatedfunction + "\"";
				}
			}
		}
		return eventStr;
	},
	getPanelSectionEvents :function(panelSectionObj, panelObj) {
		var eventStr = "";
		var tabEvent = "";
		var onclickReq = true;
		var eventobj = null;
		if (panelObj.widgettype == "TAB") {
			tabEvent = "apz.tabAction(this);";
		}
		if (panelSectionObj.events) {
			for (var i = 0; i < panelSectionObj.events.length; i++) {
				eventobj = panelSectionObj.events[i];
				if (!this.apz.isNull(eventobj["function"]) && !this.apz.isNull(eventobj.name)) {
					var lupdatedfunction = eventobj["function"].replace(/"|'/g, '&quot;');
					eventStr += eventobj.name + "='" + lupdatedfunction + "'";
					if (eventobj.name == ("ONCLICK")) {
						eventStr = "";
						onclickReq = false;
						eventStr += eventobj.name + "='" + tabEvent + lupdatedfunction + "'";
					}
				}
			}
		}
		if (onclickReq) {
			if (panelObj.widgettype == "TAB") {
				tabEvent = "onclick = apz.tabAction(this); ";
				eventStr = tabEvent + eventStr;
			}
		}
		return eventStr;
	},

	getControlIdforWidget :function (dataObj) {
		
		if (!this.apz.isNull(dataObj.controlid)) {
			controlId = ' apzcontrol = "' + params.appId + '__' + params.scr + '__'+ dataObj.controlid+'"';
		} else {
			controlId = "";
		}
		return controlId;
	},
	getAlignmentClass:function(alignment) {
		var className = "";
		if (alignment == 'LEFT') {
			className = " lft";
		} else if (alignment == 'CENTER') {
			className = " cen";
		} else if (alignment == 'RIGHT') {
			className = " rht";
		}
		return className;
	},
	
	getColumnWidthClass:function(colObj) {
		var columnWidth = "";
		var phnWidth = colObj.phonewidth;
		var tabWidth = colObj.tabletwidth;
		var deskWidth = colObj.desktopwidth;
		var wsWidth = colObj.wswidth;
		var width = colObj.width;
		if ((!this.apz.isNull(phnWidth) || !this.apz.isNull(tabWidth) || !this.apz.isNull(deskWidth) || !this.apz.isNull(wsWidth)) && width!=('CUSTOM') && !this.apz.isNull(width)) {
			if (!this.apz.isNull(phnWidth)) {
				columnWidth = this.getColumnResponsiveWidthClass(phnWidth, "sml");
			}
			var tabletWidth = this.getTabletColumnWidthClass(phnWidth, tabWidth);
			var DesktopWidth = this.getDesktopColumnWidthClass(phnWidth, tabWidth, deskWidth);
			var widescreenWidth = this.getWSColumnWidthClass(phnWidth, tabWidth, deskWidth, wsWidth);
			if (!this.apz.isNull(columnWidth) && !this.apz.isNull(tabletWidth)) {
				columnWidth = columnWidth + " " + tabletWidth;
			} else {
				columnWidth = tabletWidth;
			}
			if (!this.apz.isNull(columnWidth) && !this.apz.isNull(DesktopWidth)) {
				columnWidth = columnWidth + " " + DesktopWidth;
			} else {
				columnWidth = DesktopWidth;
			}
			if (!this.apz.isNull(columnWidth) && !this.apz.isNull(widescreenWidth)) {
				columnWidth = columnWidth + " " + widescreenWidth;
			} else {
				columnWidth = widescreenWidth;
			}
		} else {
			columnWidth = this.getColumnResponsiveWidthClass(width, "col");
		}
		return " "+columnWidth;
	},
	getElementResponsiveWidthClass : function(width,classSuffix) {
		var widthClass = "";
		if (width==("0") || width==("etw-100")) {
			widthClass = "etw-" + classSuffix + "0";
		} else if (width==("5") || width==("etw-95")) {
			widthClass = "etw-" + classSuffix + "5";
		} else if (width==("10") || width==("etw-90")) {
			widthClass = "etw-" + classSuffix + "10";
		} else if (width==("15") || width==("etw-85")) {
			widthClass = "etw-" + classSuffix + "15";
		} else if (width==("20") || width==("etw-80")) {
			widthClass = "etw-" + classSuffix + "20";
		} else if (width==("25") || width==("etw-75")) {
			widthClass = "etw-" + classSuffix + "25";
		} else if (width==("30") || width==("etw-70")) {
			widthClass = "etw-" + classSuffix + "30";
		} else if (width==("35") || width==("etw-65")) {
			widthClass = "etw-" + classSuffix + "35";
		} else if (width==("40") || width==("etw-60")) {
			widthClass = "etw-" + classSuffix + "40";
		} else if (width==("45") || width==("etw-55")) {
			widthClass = "etw-" + classSuffix + "45";
		} else if (width==("50") || width==("etw-50")) {
			widthClass = "etw-" + classSuffix + "50";
		} else if (width==("55") || width==("etw-45")) {
			widthClass = "etw-" + classSuffix + "55";
		} else if (width==("60") || width==("etw-40")) {
			widthClass = "etw-" + classSuffix + "60";
		} else if (width==("65") || width==("etw-35")) {
			widthClass = "etw-" + classSuffix + "65";
		} else if (width==("70") || width==("etw-30")) {
			widthClass = "etw-" + classSuffix + "70";
		} else if (width==("75") || width==("etw-25")) {
			widthClass = "etw-" + classSuffix + "75";
		} else if (width==("80") || width==("etw-20")) {
			widthClass = "etw-" + classSuffix + "80";
		} else if (width==("85") || width==("etw-15")) {
			widthClass = "etw-" + classSuffix + "85";
		} else if (width==("90") || width==("etw-10")) {
			widthClass = "etw-" + classSuffix + "90";
		} else if (width==("95") || width==("etw-5")) {
			widthClass = "etw-" + classSuffix + "95";
		} else if (width==("100") || width==("etw-0")) {
			widthClass = "etw-" + classSuffix + "100";
		} else if (width==('DEFAULT')) {
			widthClass = "etw-" + classSuffix + "etw-40";
		} else if (width==('CUSTOM')) {
			widthClass = "";
		}
		return widthClass;
	},
	getColumnResponsiveWidthClass:function(width,classSuffix) {
		var columnWidth = "";
		if (width == ("1")) {
			columnWidth = "gcb-" + classSuffix + "1";
		} else if (width == ("2")) {
			columnWidth = "gcb-" + classSuffix + "2";
		} else if (width == ("3")) {
			columnWidth = "gcb-" + classSuffix + "3";
		} else if (width == ("4")) {
			columnWidth = "gcb-" + classSuffix + "4";
		} else if (width == ("5")) {
			columnWidth = "gcb-" + classSuffix + "5";
		} else if (width == ("6")) {
			columnWidth = "gcb-" + classSuffix + "6";
		} else if (width == ("7")) {
			columnWidth = "gcb-" + classSuffix + "7";
		} else if (width == ("8")) {
			columnWidth = "gcb-" + classSuffix + "8";
		} else if (width == ("9")) {
			columnWidth = "gcb-" + classSuffix + "9";
		} else if (width == ("10")) {
			columnWidth = "gcb-" + classSuffix + "10";
		} else if (width == ("11")) {
			columnWidth = "gcb-" + classSuffix + "11";
		} else if (width == ("12")) {
			columnWidth = "gcb-" + classSuffix + "12";
		}
		return columnWidth;
	},
	
	getTabletColumnWidthClass : function( phnWidth,tabWidth) {
		var columnWidth = "";
		if (!this.apz.isNull(tabWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(tabWidth, "med");
		} else if (!this.apz.isNull(phnWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(phnWidth, "med");
		}
		return columnWidth;
	},
	 getDesktopColumnWidthClass: function (phnWidth, tabWidth,deskWidth) {
		var columnWidth = "";
		if (!this.apz.isNull(deskWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(deskWidth, "lar");
		} else if (!this.apz.isNull(tabWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(tabWidth, "lar");
		} else if (!this.apz.isNull(phnWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(phnWidth, "lar");
		}
		return columnWidth;
	},
	getWSColumnWidthClass: function(phnWidth, tabWidth,deskWidth,wideScrWidth) {
		var columnWidth = "";
		if (!this.apz.isNull(wideScrWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(wideScrWidth, "xxl");
		} else if (!this.apz.isNull(deskWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(deskWidth, "xxl");
		} else if (!this.apz.isNull(tabWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(tabWidth, "xxl");
		} else if (!this.apz.isNull(phnWidth)) {
			columnWidth = this.getColumnResponsiveWidthClass(phnWidth, "xxl");
		}
		return columnWidth;
	},getShowNoneClass: function(visibilityProp) {
		var classStr = "";
		if (visibilityProp == ('NO')) {
			classStr = " sno";
		}
		return classStr;
	}, getFooterClasses:function(footerData) {
		var cssClass = !this.apz.isNull(footerData.cssclasses) ? " " + footerData.cssclasses : "";
		var vartnClass = !this.apz.isNull(footerData.variation) ? " " + footerData.variation : "";
		var apprnceClass = !this.apz.isNull(footerData.appearance) ? " " + footerData.appearance : " pri";
		//String behvrClass = !Utils.isNull(footerData.behaviour) ? " " + footerData.behaviour : "";
		var footClasses = "apz-nav apz-nav-horizontal pnb-foot pnt-foot apz-nav-open" + cssClass + vartnClass + apprnceClass ;
		return footClasses;
	},getSidebarClasses :function(sidebarData){
		var positionClass = "";
		var stateClass = "";
		var navigationClass = "";
		if (sidebarData.location=="RIGHT") {
			positionClass = " rht";
		} else {
			positionClass = " lft";
		}
		if (sidebarData.state=="OPEN") {
			stateClass = " apz-nav-open";
		} else {
			stateClass = " closed";
		}
		var cssClass = !this.apz.isNull(sidebarData.cssclasses) ? " " + sidebarData.cssclasses : "";
		var vartnClass = !this.apz.isNull(sidebarData.variation) ? " " + sidebarData.variation : "";
		var apprnceClass = !this.apz.isNull(sidebarData.appearance) ? " " + sidebarData.appearance : " pri";
		//String behaviourCls = !Utils.isNull(sidebarData.behaviour) ? " " + sidebarData.behaviour : " STATIC";
		var sidebarClasses = "apz-nav apz-nav-vertical pnb-sdbr pnt-sdbr" + positionClass + stateClass + navigationClass + cssClass + vartnClass + apprnceClass;
		return sidebarClasses;
	}
}
