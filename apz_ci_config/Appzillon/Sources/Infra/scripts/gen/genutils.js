Apz.GenUtils = function(apz) {
   this.apz = apz;
};
Apz.GenUtils.prototype = {
	getElementId : function(param,elementData, ignoreRowNo) {
		var props = elementData;
		var elementId = "",name="",apzNode=false;
		var containerData = elementData.cntrData;
		var rowNo = elementData.rowNo;
        var elementIfaceName = props.interfacename;
        var elemntDmlType = props.datamodeltype;
        var elementNodeName = props.nodename;
        var elementName = props.elementname;
        var rowIndex = "";
        if (rowNo!=-1) {
        	rowIndex = rowNo.toString();
        }
        if (!this.apz.isNull(elementIfaceName) && !this.apz.isNull(elemntDmlType) && !this.apz.isNull(elementNodeName) && !this.apz.isNull(elementName)) {
            var nodeType = elementNodeName.split(elementIfaceName)[1];
           // if (nodeSplit.length==2 && nodeSplit[0]==elementIfaceName) {
            //	var nodeType = nodeSplit[nodeSplit.length - 1];
		        if (nodeType=="_Req") {
		        	name = param.appId + this.apz.idSep + elementIfaceName + "_Req";
		            apzNode = true;
		        } else if (nodeType=="_Res") {
		        	name = param.appId + this.apz.idSep + elementIfaceName + "_Res";
		            apzNode = true;
		        } else if (nodeType=="_Flt") {
		        	name = param.appId + this.apz.idSep + elementIfaceName + "_Flt";
		            apzNode = true;
		        }
            //}
            if (apzNode) {
                elementId = elementIfaceName + this.apz.idSep + this.getDMLId(elemntDmlType) + this.apz.idSep + name + this.apz.idSep + elementData.elementname;
            } else {
                elementId = elementIfaceName + this.apz.idSep + this.getDMLId(elemntDmlType) + this.apz.idSep + elementNodeName + this.apz.idSep + elementName;
            }
        } else {
            elementId = param.scr + this.apz.idSep + props.name;
        }
        if (rowNo != -1 && !ignoreRowNo) {
            elementId = elementId + "_" + rowIndex;
        }
        if (!this.apz.isNull(elementId)) {
            elementId = param.appId + this.apz.idSep + elementId;
        }
        return elementId;
	}, getControlIdforWidget :function (params,props) {
        if (!this.apz.isNull(props.controlid)) {
            controlId = 'apzcontrol = ' + params.appId + '__' + params.scr + '__'+ props.controlid;
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
    }, getElmLabelAlignAttrs : function(elementData) {
    	var classAttr = "";
		classAttr = this.getAlignmentClass(elementData.labelalignment);
		if (elementData.mandatory == "Y") {
			classAttr = classAttr + " req";
		}
		return classAttr;
    }, getDMLId : function(dmlType) {
    	var dmlId = "i";
    	if (dmlType=="RESPONSEDATAMODEL") {
    		dmlId = "o";
    	} else if (dmlType=="FAULTDATAMODEL") {
    		dmlId = "f";
    	}
    	return dmlId;
    }, getElementResponsiveWidthClass : function(width,classSuffix) {
		var widthClass = "";
		if (width=="0" || width=="etw-100") {
			widthClass = "etw-" + classSuffix + "0";
		} else if (width=="5" || width=="etw-95") {
			widthClass = "etw-" + classSuffix + "5";
		} else if (width=="10" || width=="etw-90") {
			widthClass = "etw-" + classSuffix + "10";
		} else if (width=="15" || width=="etw-85") {
			widthClass = "etw-" + classSuffix + "15";
		} else if (width=="20" || width=="etw-80") {
			widthClass = "etw-" + classSuffix + "20";
		} else if (width=="25" || width=="etw-75") {
			widthClass = "etw-" + classSuffix + "25";
		} else if (width=="30" || width=="etw-70") {
			widthClass = "etw-" + classSuffix + "30";
		} else if (width=="35" || width=="etw-65") {
			widthClass = "etw-" + classSuffix + "35";
		} else if (width=="40" || width=="etw-60") {
			widthClass = "etw-" + classSuffix + "40";
		} else if (width=="45" || width=="etw-55") {
			widthClass = "etw-" + classSuffix + "45";
		} else if (width=="50" || width=="etw-50") {
			widthClass = "etw-" + classSuffix + "50";
		} else if (width=="55" || width=="etw-45") {
			widthClass = "etw-" + classSuffix + "55";
		} else if (width=="60" || width=="etw-40") {
			widthClass = "etw-" + classSuffix + "60";
		} else if (width=="65" || width=="etw-35") {
			widthClass = "etw-" + classSuffix + "65";
		} else if (width=="70" || width=="etw-30") {
			widthClass = "etw-" + classSuffix + "70";
		} else if (width=="75" || width=="etw-25") {
			widthClass = "etw-" + classSuffix + "75";
		} else if (width=="80" || width=="etw-20") {
			widthClass = "etw-" + classSuffix + "80";
		} else if (width=="85" || width=="etw-15") {
			widthClass = "etw-" + classSuffix + "85";
		} else if (width=="90" || width=="etw-10") {
			widthClass = "etw-" + classSuffix + "90";
		} else if (width=="95" || width=="etw-5") {
			widthClass = "etw-" + classSuffix + "95";
		} else if (width=="100" || width=="etw-0") {
			widthClass = "etw-" + classSuffix + "100";
		} else if (width=="DEFAULT") {
			widthClass = "etw-" + classSuffix + "etw-40";
		} else if (width=="CUSTOM") {
			widthClass = "";
		}
		return widthClass;
	}, getElmStyleAttributes : function(elementData) {
		var styleAttr = "";
		if (elementData.width=="CUSTOM" && !this.apz.isNull(elementData.customwidth)) {
			styleAttr += " width:" + elementData.customwidth + elementData.customwidthtype + ";";
		}
		if (!this.apz.isNull(elementData.customheight)) {
			styleAttr += " height:" + elementData.customheight + elementData.customheighttype + ";";
		}
		if (elementData.widgettype=="BUTTON") {
			if (!this.apz.isNull(elementData.buttonwidth)) {
				styleAttr += " width:" + elementData.buttonwidth + "px" + ";";
			}
		}
		return styleAttr;
	}, getElmContentAlignAttrs : function(elementData) {
		var className = this.getAlignmentClass(elementData.contentalignment);
		return className;
	}, getClassesForElement : function(param,elementData) {
		var containerData = elementData.cntrData;
		var classStr = "",dropdownVariation = "",toggleSwitchVar = "",tooltipCls = "",sizeClass = "",iosCls = "",variationClass = "",iconPos = "";
		var icon = elementData.icon;
		var fontSizeClass = !this.apz.isNull(elementData.fontsize) ? " fs"+elementData.fontsize:"";		
		var nonWrappedCls = this.getClassesForNonWrappedElms(param,elementData);
		var vartnClass = !this.apz.isNull(elementData.variation) ? " "+elementData.variation.toLowerCase() : "" ;
		var mandatoryClass = elementData.labelmandatory=="Y" ? " req" : "";
	    var semanticsStr  = !this.apz.isNull(elementData.semantics) ? " " + elementData.semantics : "";
	    if(elementData.semantics == "INF"){
			semanticsStr = " inf";
		} else if (elementData.semantics == "SUS"){
			semanticsStr = " suc";
		} else if(elementData.semantics == "WAN"){
			semanticsStr = " war";
		} else if(elementData.semantics == "ERR"){
			semanticsStr = " err";
		} else if(elementData.semantics == "INV"){
			semanticsStr = " wht";
		} 
		var widthClass = this.getWidthClass(elementData);
		var cssClass = "";
		var appearanceclass = !this.apz.isNull(elementData.appearance)? " "+elementData.appearance.toLowerCase():" pri";
		var lovName = elementData.lovname;
		var hasLov = !this.apz.isNull(lovName) && lovName != "Select";
		if (!this.apz.isNull(widthClass)) {
			widthClass = " " + widthClass;
		}
		if (elementData.widgettype=="TEXTAREA" && hasLov ){
			widthClass = " etw-100";
		}
		var dataType = elementData.datatype;
		var contentClass = this.getElmContentAlignAttrs(elementData);
		if (elementData.dropdowntype=="SQUARE") {
			variationClass = " dsq";
		}
		if (elementData.widgettype=="HYPERLINK") {
			if (elementData.dropdowntype=="PRIMARY" || elementData.dropdowntype=="PRI") {
				variationClass = " txtlink-p";
			} else if (elementData.dropdowntype=="SECONDARY" || elementData.dropdowntype=="SEC") {
				variationClass = " txtlink-s";
			} else if (elementData.dropdowntype=="TERTIARY" || elementData.dropdowntype=="TER") {
				variationClass = " txtlink-t";
			}
		}
		if (dataType=="NUMBER" || dataType=="INTEGER") {
			if (this.apz.deviceId=="IOS" && dataType=="NUMBER") {
				iosCls = " iosnumber";
			}
		}
		if (!this.apz.isNull(icon) || elementData.symbol=="Y") {
			iconPos = " icl";
			if (elementData.symbol=="Y") {
				iconPos = " syl";
			}
			if (elementData.iconposition=="RIGHT" || elementData.iconposition=="RGT") {
				iconPos = " icr";
				if (elementData.symbol=="Y") {
					iconPos = " syr";
				}
			}
		}
		var disabledClass = "";
		if(elementData.state=="DISABLED" || containerData.containerstate=="READONLY" || containerData.containerstate=="RO") {
			disabledClass = " select-disabled";
		}
		if (elementData.widgettype=="DROPDOWN") {
			if (elementData.dropdowntype=="WITHSUBOPTIONS" || elementData.dropdowntype=="WSO") {
				dropdownVariation = " is-tiered subo";
			} else if ( (this.apz.isNull(elementData.dropdowntype) || elementData.dropdowntype=="DEFAULT")) {
				dropdownVariation = " dropdown ";
			} else if (elementData.dropdowntype=="MULTISELECTCHECKBOX" || elementData.dropdowntype=="MSC") {
				dropdownVariation = " is-multi-tiered mltc";
			} else if (elementData.dropdowntype=="AUTOCOMPLETE" || elementData.dropdowntype=="ATO") {
				dropdownVariation = "";
			} else if (elementData.dropdowntype=="MULTISELECTTAGS" || elementData.dropdowntype=="MST") {
				dropdownVariation = " ";
			}
		}
		var stateClass = "";
		var btnIconClass = "";
		if (elementData.state=="DISABLED") {
			stateClass = " disabled";
		} else if(elementData.state=="READONLY" || elementData.state=="RO") {
			stateClass = " readonly";
		}
		if (elementData.widgettype=="TOGGLESWITCH" && elementData.dropdowntype=="BUTTON") {
			toggleSwitchVar = " cmn-toggle cmn-toggle-round-flat";
		}
		if (!this.apz.isNull(elementData.tooltip)) {
			tooltipCls = " tooltipcls";
		}
		if (elementData.widgettype=="INPUTBOX") {
		    var jqueryDateCls = ""; 
			if ((elementData.datetype=="JQUERY")) {
				jqueryDateCls = " form-control";
				if(this.apz.isNull(elementData.icon)) {
					jqueryDateCls = " form-control form-control--datepicker";
				}
			}
			var inputClass = elementData.inputtype=="OSSPECIFIC" ? "ett-inpn" : "ett-inpt" ;
			if(elementData.inputtype=="OSSPECIFIC") {
				appearanceclass = " ";
			}
			classStr = 'class="etw-100 ' + inputClass + iosCls + appearanceclass + contentClass + stateClass + tooltipCls + jqueryDateCls + vartnClass + cssClass +'"';
		} else if (elementData.widgettype == "INPUTWITHBUTTON") {
			classStr = "class=\"ett-inpt etw-100" + iosCls + appearanceclass + contentClass + tooltipCls + stateClass + cssClass +"\"";
		} else if (elementData.widgettype == "STEPPER") {
			classStr = "class=\"ett-inpt etw-100" + iosCls + appearanceclass + contentClass + stateClass + tooltipCls + "\"";
		} else if (elementData.widgettype == "DROPDOWN") {
			if (elementData.dropdowntype=="AUTOCOMPLETE" || elementData.dropdowntype=="MULTISELECTTAGS" || elementData.dropdowntype=="ATO" || elementData.dropdowntype=="MST") {
				classStr = 'class="' + iosCls + contentClass + dropdownVariation + vartnClass + disabledClass+ cssClass +'"';
			} else if (elementData.dropdowntype=="NATIVE"){
				classStr = 'class="etb-slcn ett-slcn ' + iosCls + appearanceclass + contentClass + widthClass + dropdownVariation + tooltipCls + disabledClass + vartnClass + cssClass +'"';
			} else {
				classStr = 'class="etb-slct ett-slct ' + iosCls + appearanceclass + contentClass + widthClass + dropdownVariation + tooltipCls + disabledClass + vartnClass + cssClass +'"' ;
			}
		} else if (elementData.widgettype=="RADIO") {
			classStr = 'class="' + iosCls + tooltipCls + cssClass +'"';
		} else if (elementData.widgettype=="CHECKBOX") {
			classStr = 'class="' + iosCls + tooltipCls + cssClass +'"';
		} else if (elementData.widgettype == "SLIDER") {
			if(elementData.rangetype == "OSSPECIFIC") {
				classStr = "class=\"ett-rngn " + iosCls + widthClass + tooltipCls + vartnClass + "\"";
			} else {
				classStr = "class=\"ett-rnge " + iosCls + widthClass + appearanceclass + tooltipCls + vartnClass + "\"";
			}
		} else if (elementData.widgettype=="TEXT") {
			var ettClass = this.getEttClassForText(elementData.elementtype);
			classStr = 'class="' + ettClass + iosCls + appearanceclass + semanticsStr + fontSizeClass + tooltipCls + nonWrappedCls + vartnClass + iconPos + cssClass +'"';
		} else if (elementData.widgettype == "LABEL") {
			classStr = "class=\"ett-labl flb" + iosCls + appearanceclass + semanticsStr + tooltipCls + nonWrappedCls + vartnClass + iconPos + mandatoryClass + cssClass +"\"" ;		
		} else if (elementData.widgettype == "BUTTON") {
			sizeClass = this.getButtonSizeClass(elementData.size);
			var icoPosition = "";
			if(!this.apz.isNull(icon)) {
				icoPosition = this.getButtonIconPositionClass(elementData.iconposition);
			}
			if (elementData.buttonasicon == "Y") {
				btnIconClass = " btn-icn";
			}
			classStr = 'class="ett-bttn' + widthClass + iosCls + appearanceclass + sizeClass + semanticsStr + icoPosition + tooltipCls + nonWrappedCls +  stateClass + vartnClass + btnIconClass + cssClass +'"';		
		} else if (elementData.widgettype=="ICON") {
			var imageSize = this.getIconSizeClass(elementData.size);
			classStr = "class='ett-icon " + elementData.icon + appearanceclass + semanticsStr + iosCls + imageSize + tooltipCls + nonWrappedCls + vartnClass + cssClass +"'";
		} else if (elementData.widgettype=="TAGS") {
			classStr = "class='" + iosCls + contentClass + tooltipCls + "'";
		} else if (elementData.widgettype=="TEXTAREA") {
			classStr = "class='ett-texa" + iosCls + appearanceclass + contentClass + widthClass + tooltipCls + vartnClass + cssClass +"'";
		} else if (elementData.widgettype=="HYPERLINK") {
			classStr = "class='ett-hypl"+ semanticsStr + appearanceclass + variationClass + iosCls + iconPos + tooltipCls + nonWrappedCls + vartnClass + cssClass +"'";
		} else if (elementData.widgettype=="FILEBROWSER") {
			classStr = "class='" + semanticsStr + iosCls + widthClass + tooltipCls + cssClass +"'";
		} else if (elementData.widgettype == "PROGRESSBAR") {
			if (elementData.progresstype == "OSSPECIFIC") {
				classStr = "class=\"ett-prgn" + iosCls + tooltipCls + nonWrappedCls + vartnClass + "\"";
			} else {
				classStr = "class=\"ett-prgs" + iosCls + appearanceclass + tooltipCls + nonWrappedCls + vartnClass + "\"";
			}
		} else if (elementData.widgettype=="BADGE") {
			classStr = "class='ett-badg" + semanticsStr + iosCls + appearanceclass + variationClass + tooltipCls + nonWrappedCls + vartnClass+"'";
		}
		return classStr;
	}, getEttClassForText : function(heading) {
		var ettClass = "";
		if (heading=="" || heading=="HD1") {
			ettClass = "ett-hed1";
		} else if (heading=="HEADING2" || heading=="HD2") {
			ettClass = "ett-hed2";
		} else if (heading=="HEADING3" || heading=="HD3") {
			ettClass = "ett-hed3";
		} else if (heading=="HEADING4" || heading=="HD4") {
			ettClass = "ett-hed4";
		} else if (heading=="HEADING5" || heading=="HD5") {
			ettClass = "ett-hed5";
		} else if (heading=="HEADING6" || heading=="HD6") {
			ettClass = "ett-hed6";
		} else if (heading=="PARAGRAPHTEXT" || heading=="PTXT") {
			ettClass = "ett-para";
		}
		return ettClass;
	}, getElementState : function(elementData) {
		var classStr = "";
		var state = "";
		state = elementData.state;
		if (state=="READONLY" || state=="RO") {
			classStr = ' readonly="readonly"';
		} else if (state=="DISABLED" || state=="DS") {
			classStr = ' disabled="disabled"';
		} else if (state=="ENABLED" || state=="EN") {
			classStr = ' enabled="enabled"';
		}
		return classStr;
	}, getClassesForNonWrappedElms : function(param,elementData) {
		var contData = elementData.cntrData;
		var nonWrapCls = "";
		var widgetType = elementData.widgettype;
		if (contData.widgettype=="LIST" || contData.widgettype=="NAVBAR") {
			if (!(widgetType=="CHECKBOX" || widgetType=="RADIO"
					|| widgetType=="TEXTAREA" || widgetType=="DROPDOWN" || widgetType=="SLIDER"
					|| widgetType=="TAGS")) {
				nonWrapCls = this.getShowNoneClass(elementData) + this.getCustomClass(elementData);
			}
		}
		return nonWrapCls;
	}, getShowNoneClass : function(elementData) {
		var visClass = "";
		if (elementData.options=="N") {
			visClass = " sno";
		}
		return visClass;
	}, getShowSpaceClass: function(visibilityProp) {
		var classStr = "";
		if (visibilityProp == "N") {
			classStr = " ssp";
		}
		return classStr;
	}, getCustomClass : function(elementData) {
		var cssCls = "";
		if (!this.apz.isNull(elementData.cssclasses)) {
			cssCls = " " + elementData.cssclasses;
		}
		return cssCls;
	}, getElementToolTip : function(toolTip) {
		var toolTipStr = this.apz.getLabel(toolTip);
		if (this.apz.isNull(toolTipStr)) {
			toolTipStr = "";
		} else {
			toolTipStr = " original-title=\"" + toolTipStr + "\" ";
		}
		return toolTipStr;
	}, getElementEvents : function(param,elementData) {
		var containerData = elementData.cntrData;
		var eventStr = "";
		var onBlurReqd = false;
		var onclickReqd = false;
		var onchangeReqd = false;
		var keyUp = false, focus = false;
		var keyPress = false;
		var keyDown = false;
		var elementDataType = elementData.datatype;
		var elementState = !this.apz.isNull(elementData.state) ? elementData.state : "ENABLED";
		var lelmid = this.getElementId(param,elementData);
		var eventobj = "";
		var isDmlElm = false;
		if (!this.apz.isNull(elementData.interfacename) && !this.apz.isNull(elementData.datamodeltype) && !this.apz.isNull(elementData.nodename) && !this.apz.isNull(elementData.elementname)) {
			isDmlElm = true;
		}
		for (var i = 0; i < elementData.events.length; i++) {
			eventobj = elementData.events[i];
			if (!this.apz.isNull(eventobj["function"]) && !this.apz.isNull(eventobj.name)) {
				var lupdatedfunction = eventobj["function"] ? eventobj["function"].replace(/"|'/g, '&quot;') : "";
				if (eventobj.name=="ONBLUR" && !onBlurReqd) {
					onBlurReqd = true;
					var funs = "";
					funs = lupdatedfunction;
					if ((elementDataType=="NUMBER" || elementDataType=="INTEGER") && (elementData.widgettype=="INPUTBOX") && (elementState=="ENABLED" || elementState=="EN")) {
						if (isDmlElm) {
							funs = funs + "; apz.products.formatNumberControl(this); ";
						} else {
							funs = funs + "; apz.formatNumberUIControl(this); ";
						}
					}
					/*if ((elementDataType=="DATE")  && (elementState=="ENABLED")) {
						funs = funs + " apz.val.validateInput(this);";
					}*/
					if ((!this.apz.isNull(elementData.formula)) && (elementData.widgettype=="INPUTBOX")) {
						funs = funs + "apz.deriveValue(this,&quot;" + elementData.formula.replace(" ", "") + "&quot;); ";
					}
					if ((elementData.widgettype=="INPUTBOX" || elementData.widgettype=="TEXTAREA"
							|| elementData.widgettype=="INPUTWITHBTN") && (!this.apz.isNull(elementData.lovname) && elementData.lovname!="Select")
							&& elementData.autolov=="Y") {
						funs = funs + "apz.lov.validateLovData(&quot;" + lelmid + "&quot;,&quot;" + containerData.name + "&quot;,&quot;"+param.appId+"&quot;)";
					}
					if(elementData.widgettype=="STEPPER") {
						funs = funs + " apz.validateStepperVal(this)";
					}
					if(elementData.widgettype == "INPUTBOX" && (elementData.email == "Y" || elementData.textdecoration == "C")) {
						funs = funs + " apz.val.validateInput(this);";
					}
					eventStr += eventobj.name + '="' + funs + '"';
				} else if (eventobj.name=="ONKEYUP" && elementData.runtimeamountformat=="Y" && (elementData.donotformat!="Y")
				&& (elementDataType=="NUMBER" || elementDataType=="INTEGER")) {
					var funs = "";
					if (isDmlElm) {
						funs = "apz.products.formatNumberControl(this); ";
						funs = funs + " apz.products.validateFunctionKeys(this,event);";
					} else {
						funs = "apz.formatNumberUIControl(this); ";
					}
					funs = funs + lupdatedfunction;
					eventStr += eventobj.name + '="' + funs + '"';
					keyUp = true;
				} 
				else if (eventobj.name=="ONKEYPRESS" && (elementDataType=="NUMBER" || elementDataType=="INTEGER")) {
					var funs = "";
					funs = "apz.validateChar(this, event)";
					funs = funs + lupdatedfunction;
					eventStr += eventobj.name + '="' + funs + '"';
					keyPress = true;
				}else if(eventobj.name=="ONKEYDOWN" && (elementDataType=="NUMBER" || elementDataType=="INTEGER")){
					var funs = "";
					funs = "apz.hidePopOver(this)";
					funs = funs + lupdatedfunction;
					eventStr += eventobj.name + '="' + funs + '"';
					keyDown = true;
				}else if (eventobj.name=="ONFOCUS") {
					if (this.apz.deviceId=="IOS" && elementData.datatype=="NUMBER") {
						var funs = "apz.changeTypeToNumber(this);";
						if (containerData.widgettype=="TABLE") {
							funs = funs + " apz.onRowElmChange(this); ";
						}
						funs = funs + lupdatedfunction;
						eventStr += eventobj.name + '="' + funs + '"';
						eventStr += ' ontouchstart="apz.changeTypeToNumber(this);"';
						focus = true;
					} else if ((containerData.widgettype=="TABLE") && !focus) {
						var funs = " apz.onRowElmChange(this); ";
						funs = funs + lupdatedfunction;
						eventStr += eventobj.name + '="' + funs + '"';
						focus = true;
					} else if(elementData.datatype=="NUMBER" || elementData.datatype=="INTEGER"){
						var funs = " apz.customUnFormatNumber(this); ";
						funs = funs + lupdatedfunction;
						eventStr += eventobj.name + '="' + funs + '"';
						focus = true;
					}else {
						eventStr += eventobj.name + '="' + lupdatedfunction + '"';
					}
				} else if (eventobj.name=="ONCHANGE" && elementData.widgettype=="FILEBROWSER" && !onchangeReqd) {
					onchangeReqd = true;
					var funs = "apz.setSelectedFileName(this); ";
					funs = funs + lupdatedfunction;
					eventStr += eventobj.name + '="' + funs + '"';
				} else {
					eventStr += eventobj.name + '="' + lupdatedfunction + '"';
				}
				if ((eventobj.name=="ONCLICK") 
						&& ((elementData.widgettype=="BUTTON") || (elementData.widgettype=="IMAGE")
								|| (elementData.widgettype=="ICON") || (elementData.widgettype=="HYPERLINK"))) {
					onclickReqd = true;
					var funs = "";
					funs = funs + lupdatedfunction;
					eventStr += eventobj.name + '="' + funs + '"';
				}
			}
		}
		if(!keyPress){
			if (elementDataType=="NUMBER" || elementDataType=="INTEGER"){
				var funs = "";
				/*if (isDmlElm) {
					funs = "apz.products.formatNumberControl(this); ";
				} else {
					funs = "apz.formatNumberUIControl(this); ";
				}*/
				funs = "apz.validateChar(this, event)";
				eventStr += ' onkeypress ="' + funs + '"';
			}
		}
		if(!keyDown){
			if (elementDataType=="NUMBER" || elementDataType=="INTEGER"){
				var funs = "";
				/*if (isDmlElm) {
					funs = "apz.products.formatNumberControl(this); ";
				} else {
					funs = "apz.formatNumberUIControl(this); ";
				}*/
				funs = "apz.hidePopOver(this)";
				eventStr += ' onkeydown ="' + funs + '"';
			}
		}
		if (!focus) {
			if (this.apz.deviceId=="IOS" && elementData.datatype=="NUMBER") {
				if (containerData.widgettype=="TABLE") {
					eventStr += ' onfocus="apz.onRowElmChange(this); apz.changeTypeToNumber(this); "';
				} else {
					eventStr += ' onfocus="apz.changeTypeToNumber(this);"';
				}
				eventStr += ' ontouchstart="apz.changeTypeToNumber(this);"';
			} else if (containerData.widgettype=="TABLE") {
				eventStr += ' onfocus="apz.onRowElmChange(this); "';
			}else if(elementData.datatype=="NUMBER" || elementData.datatype=="INTEGER"){
				eventStr += ' onfocus="apz.customUnFormatNumber(this); "';
			}
		}
		if (!onclickReqd) {
			// //Navigation Screen
			if (!this.apz.isNull(elementData.navscreen)) {
				var funs = " apz.nextScreen(&quot;" + elementData.navscreen + "&quot;,this)";
				eventStr += ' onclick ="' + funs + '"';
			}
		}
		if (!onBlurReqd) {
			var funs = "";
			if ((elementDataType=="NUMBER" || elementDataType=="INTEGER") && (elementData.widgettype=="INPUTBOX") && (elementState=="ENABLED" || elementState=="EN")) {
				if (isDmlElm) {
					funs = funs + " apz.products.formatNumberControl(this); ";
				} else {
					funs = funs + " apz.formatNumberUIControl(this); ";
				}
			}
			if ((elementDataType=="DATE") && (elementState=="ENABLED" || elementState=="EN")) {
				//funs = funs + " apz.formatDateObj(this); ";
			}
			if ((!this.apz.isNull(elementData.formula)) && (elementData.widgettype=="INPUTBOX")) {
				funs = funs + " apz.deriveValue(this,&quot;" + elementData.formula.replace(" ", "") + "&quot;); ";
			}
			if ((elementData.widgettype=="INPUTBOX" || elementData.widgettype=="TEXTAREA"
					|| elementData.widgettype=="INPUTWITHBTN") && (!this.apz.isNull(elementData.lovname) && elementData.lovname!="Select")
					&& elementData.autolov=="Y") {
				funs = funs + " apz.lov.validateLovData(&quot;" + lelmid + "&quot;,&quot;" + containerData.name + "&quot;,&quot;"+param.appId+"&quot;)";
			}
			/*if ((elementDataType=="DATE" || elementDataType=="DATETIME") && elementData.widgettype=="INPUTBOX") {
				funs = funs + " apz.val.validateInput(this);";
			}*/
			if(elementData.widgettype=="STEPPER") {
				funs = funs + " apz.validateStepperVal(this);";
			}
			if(elementData.widgettype == "INPUTBOX" && (elementData.textdecoration == "C" || elementData.email == "Y")) {
						funs = funs + " apz.val.validateInput(this);";
			}
			if (!this.apz.isNull(funs)) {
				eventStr += ' onblur ="' + funs + '"';
			}
		}
		if (!onchangeReqd) {
			if (elementData.widgettype=="FILEBROWSER") {
				var funs = "apz.setSelectedFileName(this);";
				eventStr += ' onchange ="' + funs + '"';
			}
		}
		if (!keyUp) {
			if ((elementData.runtimeamountformat=="Y" && elementData.donotformat!="Y")
			&& (elementDataType=="NUMBER" || elementDataType=="INTEGER")) {
				var funs = "";
				if (isDmlElm) {
					funs = "apz.products.formatNumberControl(this); ";
					funs = funs + " apz.products.validateFunctionKeys(this,event);";
				} else {
					funs = "apz.formatNumberUIControl(this); ";
				}
				eventStr += ' onkeyup ="' + funs + '"';
			}
			if((elementData.runtimeamountformat=="N" && elementData.donotformat=="N")&& (elementDataType=="NUMBER" || elementDataType=="INTEGER")){
				if (isDmlElm) {
					funs = " apz.products.validateFunctionKeys(this,event);";
				}
				eventStr += ' onkeyup ="' + funs + '"';
			}
		}
		return eventStr;
	}, getHeading : function(elementData){
		var headingType = "";
		if (elementData.elementtype=="HEADING1" || elementData.elementtype=="HD1") {
			headingType = "h1";
		} else if (elementData.elementtype=="HEADING2" || elementData.elementtype=="HD2") {
			headingType = "h2";
		} else if (elementData.elementtype=="HEADING3" || elementData.elementtype=="HD3") {
			headingType = "h3";
		} else if (elementData.elementtype=="HEADING4" || elementData.elementtype=="DEFAULT" || elementData.elementtype=="HD4") {
			headingType = "h4";
		} else if (elementData.elementtype=="HEADING5" || elementData.elementtype=="HD5") {
			headingType = "h5";
		} else if (elementData.elementtype=="HEADING6" || elementData.elementtype=="HD6") {
			headingType = "h6";
		} else if (elementData.elementtype=="PARAGRAPHTEXT"|| elementData.elementtype=="" || elementData.elementtype=="PTXT") {
			headingType = "p";
		}
		return headingType;
	},getIconSize: function(elementData){
		var iconSize = "";
		if (elementData.elementtype=="HEADING1" || elementData.elementtype=="HD1") {
			iconSize = " px32";
		} else if (elementData.elementtype=="HEADING2" || elementData.elementtype=="HD2") {
			iconSize = " px28";
		} else if (elementData.elementtype=="HEADING3" || elementData.elementtype=="HD3") {
			iconSize = " px24";
		} else if (elementData.elementtype=="HEADING4" || elementData.elementtype=="HD4") {
			iconSize = " px20";
		} else if (elementData.elementtype=="HEADING5" || elementData.elementtype=="HD5") {
			iconSize = " px18";
		} else if (elementData.elementtype=="HEADING6" || elementData.elementtype=="HD6") {
			iconSize = " px16";
		} else if (elementData.elementtype=="PARAGRAPHTEXT" || elementData.elementtype=="" || elementData.elementtype=="PTXT") {
			iconSize = " px16";
		}
		return iconSize;
	}, getPrevNextRecButtons : function(param,props) {
        var genObj = param.genObj;
        var containerId = param.appId + this.apz.idSep + param.scr + this.apz.idSep + props.name;
        var paginationId = containerId + '_cr';
        var totalPageId = containerId + '_tr';
        if(props.widgettype != "FORM"){
        	paginationId = containerId + '_cp';
        	totalPageId = containerId + '_tp';
        }
		genObj.scrhtml += '<ul class="pgn">';
		genObj.scrhtml += '<li class="pnx">';
		genObj.scrhtml += '<button id="' + containerId + '_prev_btn" class="ett-bttn tsp med" href="javascript:;" title="Previous"  onclick="apz.data.changeRow(&quot;'
				+ containerId + '&quot;,&quot;P&quot;,&quot;&quot;)" aria-label="Previous page"><svg aria-hidden="true" class="icon icon-chevron-left px16"><use xlink:href="#icon-chevron-left"></use></svg></button>';
		genObj.scrhtml += "</li>";
		genObj.scrhtml += '<li class="pcl">';
		genObj.scrhtml += 
			'<input id="' + paginationId + '" type="text" class="pageno" value="" onchange="apz.data.changePage(&quot;' + containerId + '&quot;,&quot;S&quot;,&quot;&quot;)">';
		genObj.scrhtml += "</li>";
		genObj.scrhtml += '<li class="ptx">';
		genObj.scrhtml += '<span>/&nbsp;</span><span id="' + totalPageId + '"></span>';
		genObj.scrhtml += "</li>";
		genObj.scrhtml += '<li class="pnx">';
		genObj.scrhtml += '<button id="' + containerId + '_next_btn" class="ett-bttn tsp med" href="javascript:;" title="Next" onclick="apz.data.changeRow(&quot;' + containerId
				+ '&quot;,&quot;N&quot;,&quot;&quot;)" aria-label="Next page"><svg aria-hidden="true" class="icon icon-chevron-right px16"><use xlink:href="#icon-chevron-right"></use></svg></button>';
		genObj.scrhtml += "</li>";
		genObj.scrhtml += "</ul>";
	}, genAddDelRecButtons : function(param,props) {
        var genObj = param.genObj;
        var containerId = param.appId + this.apz.idSep + param.scr + this.apz.idSep + props.name;		
		genObj.scrhtml += '<ul class="adr-ctr">';
		genObj.scrhtml += '<li class="add">';
		genObj.scrhtml += '<span><button id="' + containerId + '_add_btn" class="ett-bttn tsp med" href="javascript:;" title="Add Record"  onclick="apz.data.createRow(&quot;'
			+ containerId + '&quot;)" aria-label="Add Record"><svg aria-hidden="true" class="icon icon-plus px16"><use xlink:href="#icon-plus"></use></svg></button></span></li>';
		genObj.scrhtml += '<li class="rmv"><span><button id="' + containerId
			+ '_rem_btn" class="ett-bttn tsp med" href="javascript:;" title="Remove Record" onclick="apz.data.removeRows(&quot;'+ containerId
			+ '&quot;)" aria-label="Remove Record"><svg aria-hidden="true" class="icon icon-minus px16"><use xlink:href="#icon-minus"></use></svg></button></span>';
		genObj.scrhtml += "</li>";
		genObj.scrhtml += "</ul>";
	}, getTableClass : function(containerData) {
		var classAttr = "tabl";
		if (containerData.nativetable!="Y") {
			if(containerData.containerstate=="READONLY" && containerData.responsive=="Y") {
				classAttr = classAttr + " responsive wborder ro";
			} else {
				classAttr = classAttr + " fixedheader";
			}
		}
		if (!this.apz.isNull(classAttr)) {
			classAttr = 'class="' + classAttr + '" ';
		}
		return classAttr;
	}, getCntrEvents : function(containerData) {
		var eventStr = "";
		var eventDataObj = "";
		if (containerData.events) {
			for (var i = 0; i < containerData.events.length; i++) {
				eventDataObj = containerData.events[i];
				if (!this.apz.isNull(eventDataObj["function"]) && !this.apz.isNull(eventDataObj.name)) {
					var lupdatedfunction = eventDataObj["function"] ? eventDataObj["function"].replace(/"/g, "&quot;") : "";
					eventStr += eventDataObj.name + '="' + lupdatedfunction + '"';
				}
			}
		}
		return " " + eventStr;
	},getListClassAttributes : function(containerData){
		var classAttr = "";
		classAttr = this.getVerticalAlignmentClass(containerData.verticalalignment);
		classAttr = classAttr + this.getAlignmentClass(containerData.contentalignment);
		classAttr = classAttr + this.getShowNoneClass(containerData);
		classAttr = classAttr + this.getCustomClass(containerData);
		var apprnceClass = !this.apz.isNull(containerData.appearance) ? " " + containerData.appearance.toLowerCase() : " pri";
		var vartnClass = !this.apz.isNull(containerData.variation) ? " " + containerData.variation.toLowerCase() : "";
		classAttr = classAttr + apprnceClass + vartnClass;
		return classAttr;
	}, getVerticalAlignmentClass:function(vAlignment){
		var vAlignmentClass = "";
		if (vAlignment=="TOP") {
			vAlignmentClass = " top";
		} else if (vAlignment=="MIDDLE") {
			vAlignmentClass = " mid";
		} else if (vAlignment=="BOTTOM") {
			vAlignmentClass = " btm";
		}
		return vAlignmentClass;
	},getSectionRowClassAttributes :function(secRowData){
		var classAttr = "";
		classAttr = this.getShowNoneClass(secRowData);
		if(!this.apz.isNull(secRowData.variation)) {
			classAttr = classAttr + " "+ secRowData.variation;
		}
		var cssClass = !this.apz.isNull(secRowData.cssclasses) ? " "+ secRowData.cssclasses: "";
		var apprCls = !this.apz.isNull(secRowData.appearance) ? " "+secRowData.appearance.toLowerCase() : " pri";
		classAttr = classAttr + apprCls + cssClass;
		return classAttr;
	},getPaginationEvent :function(eventType,elementName,flag,thisReqd){
		var leventstring = "";
		leventstring = eventType + "='apz.data.changePage(&quot;" + elementName + "&quot;,&quot;" + flag + "&quot;,&quot;&quot;)' ";
		if (thisReqd) {
			leventstring = eventType + "='apz.data.changePage(&quot;" + elementName + "&quot;,&quot;" + flag + "&quot;,this)' ";
		}
		return leventstring;
	},genNumberedPaginationButtons :function(param,cntrData){
		var containerData = cntrData;
    	var genObj = param.genObj;
    	var containerId = param.appId + "__" + param.scr + "__" + containerData.name;
		var event = "_btn' class='ett-bttn tsp sml' href='javascript:;''  onclick='apz.data.changePage(&quot;" + containerId + "&quot;,&quot;S&quot;, this)'";
		genObj.scrhtml+=  "<ul id='" + containerId + "_pagination_ul' class='pbtn'>";
		if (containerData.dynamicpagesize=="Y") {
			this.genDynamicPageSize(param,cntrData);
		}
		genObj.scrhtml+= "<li id='" + containerId + "_pagination_li' class=''>";
		genObj.scrhtml+=  "<a id='" + containerId + "_first_btn' aria-label='First Page' role='button' class='ett-bttn tsp sml'  href='javascript:;' title='First'  onclick='apz.data.changePage(&quot;" + containerId
				+ "&quot;,&quot;F&quot;,&quot;&quot;)'><svg aria-hidden='true' class='ett-icon icon-arrow-prev px18'><use xlink:href='#icon-arrow-prev'></use></svg></a>";
		genObj.scrhtml+= "<a id='" + containerId + "_prev_btn' aria-label='Previous Page' role='button' class='ett-bttn tsp sml' href='javascript:;' title='Previous'  onclick='apz.data.changePage(&quot;" + containerId
				+ "&quot;,&quot;P&quot;,&quot;&quot;)'><svg aria-hidden='true' class='ett-icon icon-arrow-left px18'><use xlink:href='#icon-arrow-left'></use></svg></a>";
		genObj.scrhtml+= "<a id='" + containerId + "_1" + event + "' >1</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_2" + event + "' >2</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_3" + event + "' >3</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_4" + event + "' >4</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_5" + event + "'>5</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_next_btn' aria-label='Next Page' role='button' class='ett-bttn tsp sml' href='javascript:;' title='Next' onclick='apz.data.changePage(&quot;" + containerId
				+ "&quot;,&quot;N&quot;,&quot;&quot;)'><svg aria-hidden='true' class='ett-icon icon-arrow-right px18'><use xlink:href='#icon-arrow-right'></use></svg></a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_last_btn' aria-label='Last Page' role='button' class='ett-bttn tsp sml' href='javascript:;' title='Last' onclick='apz.data.changePage(&quot;" + containerId
				+ "&quot;,&quot;L&quot;,&quot;&quot;)'><svg aria-hidden='true' class='ett-icon icon-arrow-next px18'><use xlink:href='#icon-arrow-next'></use></svg></a>";
		genObj.scrhtml+= "</li>";
		genObj.scrhtml+=  "</ul>";
	},genInfoandNumberedPagination :function(param,cntrData){
		var containerData = cntrData;
    	var genObj = param.genObj;
    	var containerId = param.appId + "__" + param.scr + "__" + containerData.name;
		var event = "_btn' class='ett-bttn tsp sml' href='javascript:;'  onclick='apz.data.changePage(&quot;" + containerId + "&quot;,&quot;S&quot;, this)'";
		genObj.scrhtml+=  "<ul id='" + containerId + "_pagination_ul' class='plbl'>";
		if (containerData.dynamicpagesize=="Y") {
			this.genDynamicPageSize(param,cntrData);
		}
		genObj.scrhtml+=  "<li id='" + containerId + "_pagination_li' class=' rht'>";
		genObj.scrhtml+=  "<span id='" + containerId + "_info'> Showing 0 to 0 of 0 entries</span>";
		genObj.scrhtml+=  "<a id='" + containerId + "_1" + event + " >1</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_2" + event + " >2</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_3" + event + " >3</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_4" + event + " >4</a>";
		genObj.scrhtml+=  "<a id='" + containerId + "_5" + event + " >5</a>";
		genObj.scrhtml+=  "</li>";
		genObj.scrhtml+=  "</ul>";
	},genDynamicPageSize :function(param,cntrData){
    	var containerData = cntrData;
    	var genObj = param.genObj;
    	var containerId = param.appId + "__" + param.scr + "__" + containerData.name;
    	var optDescription = "", optValue = "", optionId="", pageSizeDefValue = containerData.dynamicpagesizevalue;
		var apprCls = !this.apz.isNull(containerData.appearance) ? " "+containerData.appearance.toLowerCase() : " pri";
		var pagCls = "pgsize";
		var pagination = containerData.paginationstyle;
		if ((!this.apz.isNull(pagination)) && (pagination=="PAGE1")) {
			pagCls = "pgs-ctr";
		}
		if (containerData.dynamicpagesize=="Y") {
			var noOfOpts = containerData.staticoptions.length;
			var optionObj = "";
			if (containerData.paginationstyle == "PAGE2" || containerData.paginationstyle == "PAGE3") {
				genObj.scrhtml+= "<li id=\"" + containerId + "_dps_ext_li\">";
				pgnCls = "pgsize";
			}
			genObj.scrhtml+= "<ul id='"  + containerId + "_dps_ul' class='"+pagCls+"'>";
			genObj.scrhtml+="<li id='"  + containerId + "_dps_label'><p>Items Per Page</p></li>";
			genObj.scrhtml+= "<li id='" + containerId + "_dps_li'>";
			genObj.scrhtml+= "<select id='" + containerId + "_dps' class='etb-slct ett-slct"+apprCls+"' tabindex='0' onchange='apz.updatePaginationRecords(&quot;"+containerId+"&quot;)''>";
			if (noOfOpts > 0) {
				optionObj = containerData.staticoptions[0];
				optDescription = optionObj.description;
				optValue = optionObj.value;
				if (this.apz.isNull(optValue)) {
					optDescription = "";
				}
			}
			for (var i=0; i<noOfOpts; i++) {
				var selectedString = "";
				optionObj = containerData.staticoptions[i];
				optDescription = optionObj.description;
				optValue = optionObj.value;
				optionId = containerId + "_option_" + optDescription;
				if (optValue==pageSizeDefValue || (this.apz.isNull(pageSizeDefValue) && i == 0)) {
					selectedString = "selected='selected'";
				} else {
					selectedString = "";
				}
					genObj.scrhtml += "<option  id=\"" + optionId + "\"  value=\"" + optValue
					+ "\" " + selectedString + " >" + optDescription + "</option>";
			}
			genObj.scrhtml+= "</select>";
			genObj.scrhtml+= "</li>";
			genObj.scrhtml+= "</ul>";
			if (containerData.paginationstyle == "PAGE2" || containerData.paginationstyle == "PAGE3") {
				genObj.scrhtml += "</li>";
			}
		}
    }, getWidthClass:function(widthData){
    	var generalWidth = "";
		var width = widthData.width;
		var phnWidth = widthData.phonewidth;
		var tabWidth = widthData.tabletwidth;
		var deskWidth = widthData.desktopwidth;
		var wsWidth = widthData.wswidth;
		var widgetCat = widthData.type;
		if ((!this.apz.isNull(phnWidth) || !this.apz.isNull(tabWidth) || !this.apz.isNull(deskWidth) || !this.apz.isNull(wsWidth)) && width!="CUSTOM" && !this.apz.isNull(width)) {
			if (!this.apz.isNull(phnWidth)) {
				if (widthData.type=="SECTIONCOLUMN") {
					generalWidth = this.getGeneralResponsiveWidthClass(phnWidth, "sml");
				} else if (widthData.type=="PRESENTATIONELEMENT") {
					generalWidth = this.getElementResponsiveWidthClass(phnWidth, "sml");
				}
			}
			var tabletWidth = this.getTabletGeneralWidthClass(phnWidth, tabWidth,widgetCat);
			var DesktopWidth = this.getDesktopGeneralWidthClass(phnWidth, tabWidth, deskWidth,widgetCat);
			var widescreenWidth = this.getWSGeneralWidthClass(phnWidth, tabWidth, deskWidth, wsWidth,widgetCat);
			if (!this.apz.isNull(generalWidth) && !this.apz.isNull(tabletWidth)) {
				generalWidth = generalWidth + " " + tabletWidth;
			} else {
				generalWidth = tabletWidth;
			}
			if (!this.apz.isNull(generalWidth) && !this.apz.isNull(DesktopWidth)) {
				generalWidth = generalWidth + " " + DesktopWidth;
			} else {
				generalWidth = DesktopWidth;
			}
			if (!this.apz.isNull(generalWidth) && !this.apz.isNull(widescreenWidth)) {
				generalWidth = generalWidth + " " + widescreenWidth;
			} else {
				generalWidth = widescreenWidth;
			}
		} else {
			if (widthData.type=="SECTIONCOLUMN") {
				generalWidth = this.getGeneralResponsiveWidthClass(width, "col");
			} else if (widthData.type=="PRESENTATIONELEMENT") {
				generalWidth = this.getElementResponsiveWidthClass(width, "");
			}
		}
		return generalWidth;
    },getGeneralResponsiveWidthClass :function(width, classSuffix){
    	var widthClass = "";
		if (width=="0" || width=="scb-col100") {
			widthClass = "scb-" + classSuffix + "0";
		} else if (width=="5" || width=="scb-col95") {
			widthClass = "scb-" + classSuffix + "5";
		} else if (width=="10" || width=="scb-col90") {
			widthClass = "scb-" + classSuffix + "10";
		} else if (width=="15" || width=="scb-col85") {
			widthClass = "scb-" + classSuffix + "15";
		} else if (width=="20" || width=="scb-col80") {
			widthClass = "scb-" + classSuffix + "20";
		} else if (width=="25" || width=="scb-col75") {
			widthClass = "scb-" + classSuffix + "25";
		} else if (width=="30" || width=="scb-col70") {
			widthClass = "scb-" + classSuffix + "30";
		} else if (width=="35" || width=="scb-col65") {
			widthClass = "scb-" + classSuffix + "35";
		} else if (width=="40" || width=="scb-col60") {
			widthClass = "scb-" + classSuffix + "40";
		} else if (width=="45" || width=="scb-col55") {
			widthClass = "scb-" + classSuffix + "45";
		} else if (width=="50" || width=="scb-col50") {
			widthClass = "scb-" + classSuffix + "50";
		} else if (width=="55" || width=="scb-col45") {
			widthClass = "scb-" + classSuffix + "55";
		} else if (width=="60" || width=="scb-col40") {
			widthClass = "scb-" + classSuffix + "60";
		} else if (width=="65" || width=="scb-col35") {
			widthClass = "scb-" + classSuffix + "65";
		} else if (width=="70" || width=="scb-col30") {
			widthClass = "scb-" + classSuffix + "70";
		} else if (width=="75" || width=="scb-col25") {
			widthClass = "scb-" + classSuffix + "75";
		} else if (width=="80" || width=="scb-col20") {
			widthClass = "scb-" + classSuffix + "80";
		} else if (width=="85" || width=="scb-col15") {
			widthClass = "scb-" + classSuffix + "85";
		} else if (width=="90" || width=="scb-col10") {
			widthClass = "scb-" + classSuffix + "90";
		} else if (width=="95" || width=="scb-col5") {
			widthClass = "scb-" + classSuffix + "95";
		} else if (width=="100" || width=="scb-col0") {
			widthClass = "scb-" + classSuffix + "100";
		} else if (width=="DEFAULT") {
			widthClass = "scb-" + classSuffix + "scb-col40";
		} else if (width=="CUSTOM") {
			widthClass = "";
		}
		return widthClass;
    },getMaxWidthClass:function(width){
    	var widthClass = "";
		if (width=="0") {
			widthClass = "scb-max0";
		} else if (width=="5") {
			widthClass = "scb-max5";
		} else if (width=="10") {
			widthClass = "scb-max10";
		} else if (width=="15") {
			widthClass = "scb-max15";
		} else if (width=="20") {
			widthClass = "scb-max20";
		} else if (width=="25") {
			widthClass = "scb-max25";
		} else if (width=="30") {
			widthClass = "scb-max30";
		} else if (width=="35") {
			widthClass = "scb-max35";
		} else if (width=="40") {
			widthClass = "scb-max40";
		} else if (width=="45") {
			widthClass = "scb-max45";
		} else if (width=="50") {
			widthClass = "scb-max50";
		} else if (width=="55") {
			widthClass = "scb-max55";
		} else if (width=="60") {
			widthClass = "scb-max60";
		} else if (width=="65") {
			widthClass = "scb-max65";
		} else if (width=="70") {
			widthClass = "scb-max70";
		} else if (width=="75") {
			widthClass = "scb-max75";
		} else if (width=="80") {
			widthClass = "scb-max80";
		} else if (width=="85") {
			widthClass = "scb-max85";
		} else if (width=="90") {
			widthClass = "scb-max90";
		} else if (width=="95") {
			widthClass = "scb-max95";
		} else if (width=="100") {
			widthClass = "scb-max100";
		}
		return widthClass;
    },getTabletGeneralWidthClass:function(phnWidth,tabWidth,widgetCategory){
    	var columnWidth = "";
    	var width = "";
		if (!this.apz.isNull(tabWidth)) {
			width = tabWidth;
		} else if (!this.apz.isNull(phnWidth)) {
			width = phnWidth;
		}
		if (!this.apz.isNull(width)) {
			if (widgetCategory =="SECTIONCOLUMN") {
				columnWidth = this.getGeneralResponsiveWidthClass(width, "med");
			} else if (widgetCategory =="PRESENTATIONELEMENT") {
				columnWidth = this.getElementResponsiveWidthClass(width, "med");
			}
		}
		return columnWidth;
    },getDesktopGeneralWidthClass:function(phnWidth,tabWidth,deskWidth,widgetCategory){
    	var columnWidth = "";
    	var width = "";
		if (!this.apz.isNull(deskWidth)) {
			width = deskWidth;
		} else if (!this.apz.isNull(tabWidth)) {
			width = tabWidth;
		} else if (!this.apz.isNull(phnWidth)) {
			width = phnWidth;
		}
		if (!this.apz.isNull(width)) {
			if (widgetCategory =="SECTIONCOLUMN") {
				columnWidth = this.getGeneralResponsiveWidthClass(width, "lar");
			} else if (widgetCategory =="PRESENTATIONELEMENT") {
				columnWidth = this.getElementResponsiveWidthClass(width, "lar");
			}
		}
		return columnWidth;
    },getWSGeneralWidthClass:function(phnWidth,tabWidth,deskWidth,wsWidth,widgetCategory){
    	var columnWidth = "";
    	var width = "";
		if (!this.apz.isNull(wsWidth)) {
			width = wsWidth;
		} else if (!this.apz.isNull(deskWidth)) {
			width = deskWidth;
		} else if (!this.apz.isNull(tabWidth)) {
			width = tabWidth;
		} else if (!this.apz.isNull(phnWidth)) {
			width = phnWidth;
		}
		if (!this.apz.isNull(width)) {
			if (widgetCategory =="SECTIONCOLUMN") {
				columnWidth = this.getGeneralResponsiveWidthClass(width, "xxl");
			} else if (widgetCategory =="PRESENTATIONELEMENT") {
				columnWidth = this.getElementResponsiveWidthClass(width, "xxl");
			}
		}
		return columnWidth;
    },getSectionColStyleAttributes :function(secColData){
    	var styleAttr = "";
		styleAttr += this.getMinMaxStyleAttributes(secColData.maxwidth, secColData.maxwidth);
		//if ((secColData.width=="CUSTOM") && (secColData.customwidth != null) && (!secColData.customwidth=="")) {
		if ((secColData.width=="CUSTOM") && (!this.apz.isNull(secColData.customwidth))) {	
			styleAttr = styleAttr + " width:" + secColData.customwidth + secColData.customwidthtype + ";";
		}
		if (!this.apz.isNull(styleAttr)) {
			styleAttr = 'style="' + styleAttr + '" ';
		}
		return styleAttr;
    },getMinMaxStyleAttributes :function(minWidth,maxWidth){
    	var styleAttr = "";
		if (!maxWidth=="") {
			styleAttr += " max-width:" + maxWidth + ";";
		}
		if (!minWidth=="") {
			styleAttr += " min-width:" + minWidth + ";";
		}
		return styleAttr;
    }, getSectionColClassAttributes : function(secColData) {
		var classAttr = "";
		var widthClass = this.getWidthClass(secColData);
		classAttr = this.getVerticalAlignmentClass(secColData.verticalalignment);
		classAttr = classAttr + this.getAlignmentClass(secColData.horizontalalignment);
		classAttr = classAttr + this.getShowNoneClass(secColData);
		classAttr = classAttr + this.getCustomClass(secColData);
		var semanticsCls = !this.apz.isNull(secColData.semantics) ? " " + secColData.semantics : "";
		var vartnClass = !this.apz.isNull(secColData.variation) ? " " + secColData.variation : "";
		var apprnceClass = !this.apz.isNull(secColData.appearance) ? " " + secColData.appearance.toLowerCase() : " pri";
		classAttr = classAttr + vartnClass + apprnceClass + semanticsCls ;
		if (!this.apz.isNull(widthClass)) {
			classAttr = classAttr + " " + widthClass;
		}
		return classAttr;
	}, getButtonElementEvents : function(param, elementData) {
    	var eventStr = "";
		var onClickReqd = true;
		var eventobj = "";
		for (var i = 0; i < elementData.events.length; i++) {
			eventobj = elementData.events[i];
			if (!this.apz.isNull(eventobj["function"]) && !this.apz.isNull(eventobj.name)) {
				var lupdatedfunction = eventobj["function"].replace(/"|'/g, "&quot;");
				if (eventobj.name == "ONCLICK") {
					onClickReqd = false;
					var funs = "";
					var luserfun = lupdatedfunction;
					if (elementData.state == "DISABLED" || elementData.state == "DS") {
						funs = "apz.handleDisabled(event, this); ";
					} /*else if (eventobj.name == "ONCLICK" && ((templateDataObj.sidebar != null) && templateDataObj.sidebar.gridrows.size() > 0)
							&& luserfun.contains("utils.handleSidebar")) {
						funs = funs + "apz.toggleSidebar(this); ";
						luserfun = "";
					}*/
					if (elementData.spinner == "Y") {
						funs = " apz.handleBtnSpinner({'obj':this,'text':'"+elementData.spinnertext+"','timeout':'"+elementData.spinnertime+"'}); " + funs;
					}
					funs = funs + luserfun;
					eventStr += eventobj.name + "=\"" + funs + "\"";
				} else {
					eventStr += eventobj.name + "=\"" + lupdatedfunction + "\"";
				}
			}
		}
		if (onClickReqd) {
			var funs = "";
			if (!this.apz.isNull(elementData.navscreen)) {
				funs = "apz.nextScreen(&quot;" + elementData.navscreen + "&quot;,this)";
			}
			if (elementData.state == "DISABLED" || elementData.state == "DS") {
				funs = "apz.handleDisabled(this); ";
			}
			if (elementData.spinner == "Y") {
				funs = " apz.handleBtnSpinner({'obj':this,'text':'"+elementData.spinnertext+"','timeout':'"+elementData.spinnertime+"'}); " + funs;
			}
			if (!this.apz.isNull(funs)) {
				eventStr += "onclick" + "=\"" + funs + "\"";
			}
		}
		return eventStr;
    }, getButtonIcon : function(param, elementData, title, elementId) {
    	var iconPosition = elementData.iconposition;
		var elementIcon = elementData.icon;
		var genUtils = param.genUtils;
		var titleStr = title;
		var iconSemCls = !this.apz.isNull(elementData.iconsemantics) ? " " + elementData.iconsemantics : "";
		var iconSizeClass = genUtils.getIconSizeClass(elementData.iconsize);
		if (!this.apz.isNull(elementIcon)) {
				if ((iconPosition == "LEFT" || iconPosition == "LFT") || iconPosition == "DEFAULT" || iconPosition == "" || iconPosition == "LFT") {
					elementIcon = "<svg id=\"btn_icon_" + elementIcon + "\" aria-hidden=\"true\" role=\"presentation\" class=\"ett-icon " + elementIcon + " " + iconSizeClass + " " + iconSemCls + "\"><use xlink:href=\"#"
							+ elementIcon + "\"></use></svg><span id=\""+elementId+"_txtcnt\">" + titleStr + "</span>";
				} else if (iconPosition == "RIGHT" || iconPosition == "RGT") {
					elementIcon = "<span id=\""+elementId+"_txtcnt\">" + titleStr + "</span><svg id=\"btn_icon_" + elementIcon + "\" aria-hidden=\"true\" role=\"presentation\" class=\"ett-icon " + elementIcon + " " + iconSizeClass + " " + iconSemCls
							+ " \"><use xlink:href=\"#" + elementIcon + "\"></use></svg>";
				} else if (iconPosition == "TOP") {
					elementIcon = "<svg id=\"btn_icon_" + elementIcon + "\"  aria-hidden=\"true\" role=\"presentation\" class=\"ett-icon " + elementIcon + " " + iconSizeClass + " " + iconSemCls + "\"><use xlink:href=\"#"
							+ elementIcon + "\"></use></svg><div id=\""+elementId+"_txtcnt\">" + titleStr + "</div>";
				} else if (iconPosition == "BOTTOM") {
					elementIcon = "<div id=\""+elementId+"_txtcnt\">" + titleStr + "</div><svg id=\"btn_icon_" + elementIcon + "\" aria-hidden=\"true\" role=\"presentation\" class=\"ett-icon " + elementIcon + " " + iconSizeClass + " " + iconSemCls
							+ "\"><use xlink:href=\"#" + elementIcon + "\"></use></svg>";
				}
			//}Property not available
		} else {
			elementIcon = "<span id=\""+elementId+"_txtcnt\">" + titleStr + "</span>";
		}
		return elementIcon;
    }, getButtonSizeClass : function(sizeProp) {
    	var sizeClass = "";
		if(sizeProp == "LARGE") {
			sizeClass = " lar";
		} else if(sizeProp == "MEDIUM" || sizeProp == "MED") {
			sizeClass = " med";
		} else if(sizeProp == "SMALL" || sizeProp == "SML") {
			sizeClass = " sml";
		} else if(sizeProp == "MINI" || sizeProp == "MIN") {
			sizeClass = " min";
		}
		return sizeClass;
		
	}, getButtonIconPositionClass : function(iconposition) {
		var iconPositionClass = "";
		if (iconposition == "LEFT" || iconposition == "LFT") {
			iconPositionClass = " icl";
		} else if (iconposition == "RIGHT" || iconposition == "RGT") {
			iconPositionClass = " icr";
		} else if (iconposition == "TOP" || iconposition == "TOP") {
			iconPositionClass = " ict";
		} else if (iconposition == "BOTTOM") {
			iconPositionClass = " icb";
		}
		return iconPositionClass;
	}, getIconSizeClass : function(iconSize) {
		var classStr = " px24";
		if (!this.apz.isNull(iconSize)) {
			if (iconSize == "PX12" || iconSize == "12px") {
				classStr = " px12";
			} else if (iconSize == "PX14" || iconSize == "14px") {
				classStr = " px14";
			} else if (iconSize == "PX16" || iconSize == "16px") {
				classStr = " px16";
			} else if (iconSize == "PX18" || iconSize == "18px") {
				classStr = " px18";
			} else if (iconSize == "PX20" || iconSize == "20px") {
				classStr = " px20";
			} else if (iconSize == "PX22" || iconSize == "22px") {
				classStr = " px22";
			} else if (iconSize == "PX24" || iconSize == "24px") {
				classStr = " px24";
			} else if (iconSize == "PX28" || iconSize == "28px") {
				classStr = " px28";
			} else if (iconSize == "PX34" || iconSize == "34px") {
				classStr = " px34";
			} else if (iconSize == "PX40" || iconSize == "40px") {
				classStr = " px40";
			} else if (iconSize == "PX48" || iconSize == "48px") {
				classStr = " px48";
			} else if (iconSize == "PX52" || iconSize == "52px") {
				classStr = " px52";
			} else if (iconSize == "PX64" || iconSize == "64px") {
				classStr = " px64";
			} else if (iconSize == "PX72" || iconSize == "72px") {
				classStr = " px72";
			} else if (iconSize == "PX84" || iconSize == "84px") {
				classStr = " px84";
			} else if (iconSize == "PX128" || iconSize == "128px") {
				classStr = " px128";
			} else if (iconSize == "PX256" || iconSize == "256px") {
				classStr = " px256";
			} else if (iconSize == "PX512" || iconSize == "512px") {
				classStr = " px512";
			}
			classStr = " " + classStr;
		}
		return classStr;
	}, getClassesforImage : function(param, elementData) {
		var classStr = "";
		var vartnClasses = !this.apz.isNull(elementData.variation) ? " " + elementData.variation.toLowerCase() : "";
		var nonWrapCls = this.getClassesForNonWrappedElms(param, elementData);
	    var appearanceClass = !this.apz.isNull(elementData.appearance) ? " "+elementData.appearance: "";
	    if(elementData.appearance == "BXD"){
			appearanceClass = " box";
		} else if(elementData.appearance == "ROD"){
			appearanceClass = " rbx";
		} else if(elementData.appearance == "CIRC"){
			appearanceClass = " cir";
		}
		var imageClsAttr = this.getImageClassAttributes(elementData);
		if (!this.apz.isNull(imageClsAttr)) {
			imageClsAttr = " " + imageClsAttr;
		}
		classStr = "class='ett-imge" + appearanceClass + imageClsAttr + nonWrapCls + vartnClasses + "' ";
		return classStr;
	}, getImageClassAttributes : function(elementData) {
		var classAttr = "";
		if (elementData.size == "16") {
			classAttr = " tb16";
		} else if (elementData.size == "24") {
			classAttr = " tb24 ";
		} else if (elementData.size == "36") {
			classAttr = " tb36 ";
		} else if (elementData.size == "52") {
			classAttr = " tb52 ";
		} else if (elementData.size == "64") {
			classAttr = " tb64 ";
		} else if (elementData.size == "72") {
			classAttr = " tb72 ";
		} else if (elementData.size == "80") {
			classAttr = " tb80 ";
		} else if (elementData.size == "100") {
			classAttr = " tb100 ";
		} else if (elementData.size == "114") {
			classAttr = " tb114 ";
		} else if (elementData.size == "150") {
			classAttr = " tb150 ";
		} else if (elementData.size == "180") {
			classAttr = " tb180 ";
		} else if (elementData.size == "256") {
			classAttr = " tb256 ";
		} else if (elementData.size == "512") {
			classAttr = " tb512 ";
		} else if (elementData.size == "FULLW") {
			classAttr = " tbfullw ";
		}
		return classAttr;
	}, getRowno :function(param,props){
		var rowStr = "";
		var rowNo = props.rowNo;
		var index = this.getRowIndex(param,props);
		if (rowNo != -1) {
			rowStr = "rowno=\"" + index + "\" ";
		}
		return rowStr;
	}, getRowIndex :function(param,props){
		var index = "";
		var rowNo = props.rowNo;
		if (rowNo != -1) {
			index = rowNo.toString();
			/*if (appData != null) {
				if (appData.prjdata.bindingengine.equals(Const.BINDINGENGINE_ANGULAR) && (DataUtils.isDMlElement(elementData))) {
					index = "{{$index}}";
				}
			}*/
		}
		return index;
	},isDMLElm: function(elementData){
 var isDmlElm = false
	  if (!this.apz.isNull(elementData.interfacename) && !this.apz.isNull(elementData.datamodeltype) && !this.apz.isNull(elementData.nodename) && !this.apz.isNull(elementData.elementname)) {
			 isDmlElm = true;
		}
return isDmlElm;
	}, updateGlobalMapInfo: function(param, elementData){
		var genUtils = param.genUtils;
		var elementId = genUtils.getElementId(param,elementData,true);
	    var elmObj = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design].elmsMap[elementId];
		//// Generate Elements
		if(elementData.decorations){ 
		    if(elementData.decorations.length>0){
		        elmObj.decorations = elementData.decorations;
		        for (var i = 0; i < elmObj.decorations.length; i++) {
		        	var decor = elmObj.decorations[i];
		        	elmObj[decor.name] = decor.value;
		        }
		    }
		}
		if(elementData.relatedelms && elementData.relatedelms.length>0){ 
			elmObj.relatedelms = elementData.relatedelms;
		}
		if(elementData.relatedcontainer && elementData.relatedcontainer.length>0){ 
			elmObj.relatedcontainers = elementData.relatedcontainer;
		}
		if(elementData.attributeRejectedValue){
			elmObj.attributeRejectedValue = elementData.attributeRejectedValue;		
		}
		if(elementData.externalid){
			elmObj.externalid = elementData.externalid; 
		}
		if(elementData.externalWidgetType){
			elmObj.externalWidgetType = elementData.externalWidgetType; 
		}
		elmObj.biphier = (elementData.biphier)?elementData.biphier:"";
		elmObj.reApproval = (elementData.reapproval)?elementData.reapproval:"";
		elmObj.displayName = (elementData.displayname)?elementData.displayname:"";
		elmObj.combinedProductId = (elementData.combinedproductid)?elementData.combinedproductid:"";
		elmObj.basicrulesetpresent = (elementData.basicrulesetpresent)?elementData.basicrulesetpresent:"";
		elmObj.defaultVal = (elementData.defaultvalue)?elementData.defaultvalue:"";
	}, updateCntrMetaData: function(param, cntrData){
		var cntrId = this.getCntrId(param, cntrData);
		var cntrMetaObj = apz.scrDefsMap[param.appId][param.scr+"__"+param.layout+"__"+param.design].containersMap[cntrId];
		if(cntrMetaObj){
			cntrMetaObj.extName = (cntrData.externalname)?cntrData.externalname:"";
			cntrMetaObj.decorations = (cntrData.decorations)?cntrData.decorations:"";
			cntrMetaObj.biphier = (cntrData.biphier)?cntrData.biphier:"";
			cntrMetaObj.reApproval = (cntrData.reapproval)?cntrData.reapproval:"";
      /* addtional attribute : investor  */
			cntrMetaObj.dispName = (cntrData.displayname)?cntrData.displayname:"";
			if(cntrData.defaultProducts && cntrData.defaultProducts.length > 0){
				cntrMetaObj.defaultProducts = cntrData.defaultProducts;
			}
			cntrMetaObj.filterColumns = (cntrData.filtercolumns)?cntrData.filtercolumns:"N";
			cntrMetaObj.filterOnline = (cntrData.filteronline)?cntrData.filteronline:"N";
		}
	}, getCntrId: function(param, cntrData){
		return param.appId + this.apz.idSep + param.scr + this.apz.idSep + cntrData.name;
	}
}

