Apz.GenWidget = function(apz) {
   this.apz = apz;
};
Apz.GenWidget.prototype = {
	genElement: function(param, elementData) {
		var genUtils = param.genUtils;
		genUtils.updateGlobalMapInfo(param, elementData);
		if(elementData.widgettype == "INPUTBOX"){
			this.genInputBox(param, elementData);
		} else if(elementData.widgettype == "CHECKBOX"){
			this.genCheckbox(param, elementData);
		} else if(elementData.widgettype == "RADIO"){
			this.genRadio(param, elementData);
		} else if(elementData.widgettype == "TEXT"){
			this.genText(param, elementData);
		} else if(elementData.widgettype == "DROPDOWN"){
			this.genDropdown(param, elementData);
		} else if(elementData.widgettype == "BUTTON"){
			this.genButton(param, elementData);
		} else if(elementData.widgettype == "IMAGE"){
			this.genImage(param, elementData);
		} else if(elementData.widgettype =="ICON"){
			this.genIcon(param,elementData);
		} else if(elementData.widgettype =="TOGGLESWITCH") {
			this.genToggleSwitch(param,elementData);
		} else if(elementData.widgettype =="FILEBROWSER"){
			this.genFileBrowser(param,elementData);
		} else if(elementData.widgettype =="TEXTAREA"){
			this.genTextArea(param,elementData);
		} else if(elementData.widgettype =="HYPERLINK"){
			this.genHyperlink(param,elementData);
		} else if(elementData.widgettype =="TAGS"){
			this.genTags(param,elementData);
		} else if(elementData.widgettype =="BADGE"){
			this.genBadge(param,elementData);
		} else if(elementData.widgettype =="CHECKBOXGROUP") {
			this.genCheckBoxGroup(param,elementData);
		} else if(elementData.widgettype =="GAUGE") {
			this.genGauge(param,elementData);
		} else if(elementData.widgettype =="INPUTWITHBUTTON") {
			this.genInputWithButton(param,elementData);
		} else if(elementData.widgettype =="LABEL") {
			this.genLabel(param,elementData);
		} else if(elementData.widgettype =="PROGRESSBAR") {
			this.genProgressBar(param,elementData);
		} else if(elementData.widgettype =="SLIDER") {
			this.genSlider(param,elementData);
		} else if(elementData.widgettype =="STEPPER") {
			this.genStepper(param,elementData);
		}
	},
	genInputBox : function(param, props) {
		var genObj = param.genObj;
        var genUtils = param.genUtils;
	    var hasFormula = "";
		var required = "";
		var elementState = "";
		var buttonState = "";
		var placeHolder = "";
		var type = "text";
		var iconClickEvent = props.iconevent ? props.iconevent.replace('"', '&quot;') : "";
		var defaultValue = !this.apz.isNull(props.defaultvalue) ? props.defaultvalue : "";
		var maxLength = !this.apz.isNull(props.maxstringlength) ? props.maxstringlength : "";
		var dataType = props.datatype;
		var mandatory = !this.apz.isNull(props.mandatory) ? props.mandatory : "N";
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		var lovName = !this.apz.isNull(props.lovname) ? props.lovname : "";
		var icon = !this.apz.isNull(props.icon) ? props.icon : "";
		var iconPosition = !this.apz.isNull(props.iconposition) ? props.iconposition : "LEFT";
		var symbol = !this.apz.isNull(props.symbol) ? props.symbol : "";
		var nonWrapCls = genUtils.getClassesForNonWrappedElms(param, props);
		var ariaDisabled = "";
		var isAppzillon = (props.inputtype=="OSSPECIFIC") ? false : true;
		var isDate = dataType=="DATE" ? true : false;
		var isDateTime = dataType=="DATETIME" ? true : false;
		var isdateOrDateTime = (dataType=="DATE" || dataType=="DATETIME") ? true : false;
		var hasIcon = !this.apz.isNull(icon);
		var hasIconEvent = !this.apz.isNull(props.iconevent) ? true : false;
		var hasLov = (!this.apz.isNull(lovName) && lovName!="Select") ? true : false;
		var iconPosRight = (iconPosition=="RIGHT" || iconPosition=="RGT") ? true : false;
		var iconPosLeft = (iconPosition=="LEFT" || iconPosition=="LFT") ? true : false;
		var containerData = props.cntrData;
		var isIpsElem = (!this.apz.isNull(props.externalWidgetType)&&props.externalWidgetType=="IPS") ? true : false;
		if (!hasIcon) {
			if (isdateOrDateTime) {
				if(isDate) {
					icon = "icon-calendar";
				} else {
					icon = "icon-date-time";
				}
				
			} else {
				icon = "icon-grid";
			}
		}
		var lovIcon = icon, lovIconEvent = iconClickEvent;
		if(!isAppzillon) {
			lovIcon = "icon-grid";
			lovIconEvent = "";
		}
		var semanticsStr = !this.apz.isNull(props.lovsemantics) ? " " + props.lovsemantics : " inf";
		var lovappearance = !this.apz.isNull(props.lovappearance) ? " " + props.lovappearance : " pri";
		var minMaxWidthCls = genUtils.getElmStyleAttributes(props);
		var widthClass = genUtils.getWidthClass(props);
		var elementClasses = genUtils.getClassesForElement(param, props);
		var controlId = genUtils.getControlIdforWidget(param, props);
		var elementId = genUtils.getElementId(param, props);
		var secondInpId = "";
		if (props.password=="Y") {
			type = "password";
		}
		if (dataType=="NUMBER" || dataType=="INTEGER") {
			type = "Tel";
			if (this.apz.deviceId=="IOS" && dataType=="NUMBER") {
				type = "number";
			}
		}
		if(props.url && props.url == "Y") {
			type = "url";
		}
		if (!this.apz.isNull(props.formula)) {
			hasFormula = ' formula = "' + props.formula + '" ';
		}
		if (mandatory=="Y") {
			required = 'required="required"';
		}
		var iconPositionClass = iconPosRight ? "icr" : "icl";
		if (isAppzillon && symbol=="Y") {
			iconPositionClass = iconPosRight ? "syr" : "syl";
		}
		if ((isAppzillon && !hasLov && (hasIconEvent || isdateOrDateTime)) || (isdateOrDateTime && hasLov)) {
			iconPositionClass = iconPosRight ? "cir" : "cil";
		}
		if(!isAppzillon && isdateOrDateTime) {
			iconPositionClass = "btr";
		}
		genObj.scrhtml += '<span class="ecn ' + widthClass + nonWrapCls + '" style="' + minMaxWidthCls+ '">';
		elementState = genUtils.getElementState(props);
		if (props.state=="DISABLED" || props.state=="DS") {
			buttonState = ' disabled="disabled"';
			ariaDisabled = ' aria-disabled="true"';
		}
		if (containerData.containerstate=="READONLY" || containerData.containerstate=="RO") {
			if (props.state=="DISABLED" || props.state=="DS") {
				elementState += ' readonly="readonly"';
			} else {
				elementState = ' readonly="readonly"';
			}
			buttonState = ' disabled="disabled"';
			ariaDisabled = ' aria-disabled="true"';
		}
		maxLength = !this.apz.isNull(props.maxstringlength) ? props.maxstringlength : "";
		if (hasIcon || symbol=="Y" || isdateOrDateTime || hasLov) {
			var dateClass = "";
			var lovClass = "";
			if (isdateOrDateTime) {
				dateClass = " ett-date";
			}
			if (hasLov && !(isdateOrDateTime)) {
				if (iconPosLeft) {
					lovClass = " ett-lofv btl";
				} else {
					lovClass = " ett-lofv btr";
				}
			}
			var appearanceCls = !this.apz.isNull(props.appearance) ? " " + props.appearance.toLowerCase() : " pri";
				genObj.scrhtml += '<span class="etw-100 ' + iconPositionClass + ' ' + appearanceCls
						+ dateClass + lovClass + '">';
		}
		if (iconPosLeft) {
			if (isAppzillon && hasIcon && !hasIconEvent && symbol=="N" && !(isdateOrDateTime || hasLov)) {
				if(!isIpsElem){
					genObj.scrhtml += 
							'<svg aria-hidden="true" role="presentation" class="ett-icon ' + icon + ' px24"><use xlink:href="#' + icon + '"></use></svg>';
				}else{
					genObj.scrhtml += 
							'<svg aria-hidden="true" role="presentation" class="ett-icon ' + icon + ' px24" onclick="apz.products.processRule(document.getElementById(\''+elementId+'\'),event)"><use xlink:href="#' + icon + '"></use></svg>';
				}
			} else if (isAppzillon && !hasIcon && symbol=="Y") {
				var symbolVal = props.symbolvalue;
				genObj.scrhtml += ' <span>' + symbolVal + '</span>';
			} else if (isAppzillon && hasIcon && hasIconEvent && !(isdateOrDateTime) && !hasLov) {
				genObj.scrhtml += 
						'<button id="' + elementId + '_button" class="ett-bttn tsp sml" onclick="'
								+ iconClickEvent + '" ' + buttonState+ ' aria-label="'+props.icon+'" '+ariaDisabled+'><svg aria-hidden="true" class="ett-icon ' + props.icon
								+ ' px24"><use xlink:href="#' + props.icon + '"></use></svg></button>';
			} else if (isdateOrDateTime || hasLov) {
				var lovTitle = "List of Values";
				var targetid = elementId;
				if (!this.apz.isNull(props.targethtmlid) && !this.apz.isNull(props.targethtmlid.trim())) {
					targetid = props.targethtmlid;
				}
				if (isAppzillon && isdateOrDateTime) {
					genObj.scrhtml += '<button id="' + elementId
							+ '_button" class="ett-bttn tsp sml" onclick="apz.showCalendar(&quot;' + targetid + '&quot;,&quot;'
							+ dataType + '&quot;,&quot;' + props.rangepicker + '&quot;,&quot;' + props.multipleinput + '&quot;,&quot;'
							+ secondInpId + '&quot;); ' + iconClickEvent + '"  ' + buttonState+ ' aria-label="Calendar" '+ariaDisabled+'><svg aria-hidden="true" class="ett-icon '
							+ icon + ' px24"><use xlink:href="#' + icon + '"></use></svg></button>';
				} else if (isAppzillon && hasLov && !(isdateOrDateTime)) {
					genObj.scrhtml += 
							'<span class="etw-5"><button id="' + elementId + '_lov" class="ett-bttn med'
									+ lovappearance + semanticsStr + '" onclick="apz.lov.callLov(&quot;' + elementId + '&quot;,&quot;'
									+ param.appId + '__' + param.scr + '__' + containerData.name
									+ '&quot;,&quot;' + param.appId + '&quot;);' + lovIconEvent + '" title="' + lovTitle + '" aria-label="LOV" ' + buttonState + ariaDisabled+'><svg aria-hidden="true" class="ett-icon ' + lovIcon
									+ ' px24"><use xlink:href="#' + lovIcon + '"></use></svg></button></span>';
				}
			}
		}
		if ((hasLov && !(isdateOrDateTime)) || (!isAppzillon && isdateOrDateTime)) {
			genObj.scrhtml += "<span>";
		}
		var nativeDateAttr = props.datetype=="OSSPECIFIC" ? 'type = "Date"' : 'type = "'+type+'"';
		genObj.scrhtml += 
				'<input id="' + elementId + '" ' + controlId + elementClasses + localRowNo + ' ' + nativeDateAttr + hasFormula + '';
		genObj.scrhtml += ' ' + required;
		var finalDefaultValue = defaultValue;
		//placeHolder = this.apz.getLabel(props.placeholder);
		placeHolder = !this.apz.isNull(props.placeholder) ? this.apz.getLabel(props.placeholder) : "";
		if (props.translatedefaultvalue=="Y") {
			finalDefaultValue = this.apz.getLabel(defaultValue);
		}
		var toolTip = genUtils.getElementToolTip(props.tooltip);
		var textTransform = "";
		if(!this.apz.isNull(props.textdecoration) && props.textdecoration != "C") {
			var textdecoration = "";
			if(props.textdecoration = "L") {
				textdecoration = "lowercase";
			} else if(props.textdecoration = "U") {
				textdecoration = "uppercase";
			}
			textTransform = " style = \"text-transform : " + textdecoration + "\"";
		}
		genObj.scrhtml += ' maxlength ="' + maxLength + '" placeholder="' + placeHolder+ '" ' + elementState + textTransform + toolTip + '  value="' + finalDefaultValue + '" ';
		genObj.scrhtml += genUtils.getElementEvents(param, props);
		genObj.scrhtml += ">";
		if ((hasLov && !(isdateOrDateTime)) || (!isAppzillon && isdateOrDateTime)) {
			genObj.scrhtml += "</span>";
		}
		if (iconPosRight || (!isAppzillon && hasLov) || (!isAppzillon && isdateOrDateTime)) {
			var dateClickEvent = iconClickEvent;
			var dateIcon = icon;
			if(!isAppzillon) {
				dateClickEvent = "";
				dateIcon = "icon-calendar";
			}
			if (isAppzillon && hasIcon && !hasIconEvent && symbol=="N" && !(isdateOrDateTime || hasLov)) {
				if(!isIpsElem){
					genObj.scrhtml += 
							'<svg aria-hidden="true" role="presentation" class="ett-icon ' + icon + ' px24"><use xlink:href="#' + icon + '"></use></svg>';
				}else{
					genObj.scrhtml += 
							'<svg aria-hidden="true" role="presentation" class="ett-icon ' + icon + ' px24" onclick="apz.products.processRule(document.getElementById(\''+elementId+'\'),event)"><use xlink:href="#' + icon + '"></use></svg>';
				}
			} else if (isAppzillon && !hasIcon && symbol=="Y") {
				var symbolVal = !this.apz.isNull(props.symbolvalue) ? props.symbolvalue : "";
				genObj.scrhtml += " <span>" + symbolVal + "</span>";
			} else if (isAppzillon && hasIcon && hasIconEvent && !(isdateOrDateTime) && !hasLov) {
				genObj.scrhtml += 
						'<button id="' + elementId + '_button" class="ett-bttn tsp sml" onclick="'
								+ iconClickEvent + '" aria-label="'+icon+'" ' + buttonState+ ariaDisabled +'><svg aria-hidden="true" class="ett-icon ' + icon
								+ ' px24"><use xlink:href="#' + icon + '"></use></svg></button>';
			} else if (isdateOrDateTime || hasLov) {
				var lovTitle = "List of Values";
				var targetid = elementId;
				if (!this.apz.isNull(props.targethtmlid) && !this.apz.isNull(props.targethtmlid.trim())) {
					targetid = props.targethtmlid;
				}
				if (isdateOrDateTime) {
					if(!isAppzillon) {
						genObj.scrhtml += '<span id="' + elementId + '_span" class ="etw-5">';
					}
					genObj.scrhtml += '<button id="' + elementId
							+ '_button" class="ett-bttn tsp sml" onclick="apz.showCalendar(&quot;' + targetid + '&quot;,&quot;'
							+ dataType + '&quot;,&quot;' + props.rangepicker + '&quot;,&quot;' + props.multipleinput + '&quot;,&quot;'
							+ secondInpId + '&quot;); ' + dateClickEvent + '" aria-label="Calendar" ' + buttonState+ ariaDisabled+'><svg aria-hidden="true" class="ett-icon '
							+ dateIcon + ' px24"><use xlink:href="#' + dateIcon + '"></use></svg></button>';
					if(!isAppzillon) {
						genObj.scrhtml += "</span>";
					}
				} else if (hasLov && !(isdateOrDateTime)) {
					genObj.scrhtml += 
							'<span class="etw-5"><button id="' + elementId + '_lov" class="ett-bttn med'
									+ lovappearance + semanticsStr + '" onclick="apz.lov.callLov(&quot;' + elementId + '&quot;,&quot;'
									+ param.appId + '__' + param.scr + '__' + containerData.name
									+ '&quot;,&quot;' + param.appId + '&quot;);' + lovIconEvent + '" title="' + lovTitle + '" aria-label="LOV" ' + buttonState+ ariaDisabled+'><svg aria-hidden="true" class="ett-icon ' + lovIcon
									+ ' px24"><use xlink:href="#' + lovIcon + '"></use></svg></button></span>';
				}
			}
		}
		if (hasIcon || symbol=="Y" || isdateOrDateTime || hasLov) {
				genObj.scrhtml += " </span>";
		}
		genObj.scrhtml += "</span>";
	}, genDropdown : function(param,props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var containerData = props.cntrData;
		var elementId = genUtils.getElementId(param,props);
		var elementClasses = genUtils.getClassesForElement(param,props);
		var elementState = "";
		var multiple = "";
		var required = "";
		var optionTitle = "";
		var optionValue = "";
		var optionId = "";
		var optionDesc = "";
		var selectedString = "";
		var selectedCls = "", checkedString = "";
		var defaultValue = !this.apz.isNull(props.defaultvalue) ? props.defaultvalue : "";
		var optionObj = "";
		var elementStyleAttrs = genUtils.getElmStyleAttributes(props);
		var controlId = genUtils.getControlIdforWidget(param,param);
		var mandatory = props.mandatory;
		var noOfOptions = 0;
		if (props.staticoptions) {
			noOfOptions = props.staticoptions.length;
		}
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		if (props.multiselect=="Y" || props.dropdowntype=="MULTISELECTTAGS" || props.dropdowntype=="MST") {
			multiple = "multiple";
		}
		if (mandatory=="Y") {
			required = ' required="required"';
		}
		elementState = genUtils.getElementState(props);
		if (containerData.containerstate=="READONLY" || containerData.containerstate=="RO") {
			elementState = ' disabled="disabled"';
		}
		var tooltipCls = "";
		if (!this.apz.isNull(props.tooltip)) {
			tooltipCls = " tooltipcls";
		}
		var widthClass = genUtils.getWidthClass(props);
		if (!this.apz.isNull(widthClass)) {
			widthClass = " " + widthClass;
		}
		var lid = 'id="' + elementId + '"';
		var firstValue = "";
		if (!this.apz.isNull(defaultValue)) {
			for (var o = 0; o < noOfOptions; o++) {
				optionObj = props.staticoptions[o];
				optionTitle = optionObj.description;
				optionValue = optionObj.value;
				if (o==0) {
					firstValue = optionTitle;
				}
				if (optionValue==defaultValue) {
					break;
				} else {
					optionTitle = firstValue;
				}
			}
		} else {
			if (noOfOptions > 0) {
				optionObj = props.staticoptions[0];
				optionTitle = optionObj.description;
				optionValue = optionObj.value;
			}
		}
		if (props.dropdowntype=="NATIVE") {
			var toolTip = genUtils.getElementToolTip(props.tooltip);
			genObj.scrhtml += '<select ' + lid + ' ' + elementClasses + toolTip + localRowNo + multiple;
			genObj.scrhtml += " " + required;
			genObj.scrhtml += genUtils.getElementEvents(param,props);
			genObj.scrhtml += 'value="' + defaultValue + '" ' + elementState + ' style="' + elementStyleAttrs + '">';
		} else if ((props.dropdowntype=="AUTOCOMPLETE" || props.dropdowntype=="ATO" || props.dropdowntype=="MULTISELECTTAGS" || props.dropdowntype=="MST")) {
			var autoClass = "";
			if (props.dropdowntype=="AUTOCOMPLETE" || props.dropdowntype=="ATO") {
				autoClass = " acpt";
			} else {
				autoClass = " mltt";
			}
			var appearanceClass = !this.apz.isNull(props.appearance) ? " " + props.appearance.toLowerCase() : " pri";
			var toolTip = genUtils.getElementToolTip(props.tooltip);
			genObj.scrhtml += '<div class="etb-slct ett-slct' + autoClass + appearanceClass + widthClass
				+ tooltipCls + '" ' + elementState + ' ' + toolTip + ' ><select ' + lid + ' ' + elementClasses + localRowNo + multiple;
			genObj.scrhtml += " " + required;
			genObj.scrhtml += genUtils.getElementEvents(param,props);
			genObj.scrhtml += 'value="' + defaultValue + '" ' + elementState + ' style="' + elementStyleAttrs + '">';
		} else if (((props.dropdowntype=="SIMPLE" || props.dropdowntype=="SIMP" || this.apz.isNull(props.dropdowntype)
				|| props.dropdowntype=="WITHSUBOPTIONS" || props.dropdowntype=="WSO" || props.dropdowntype=="MULTISELECTCHECKBOX" || props.dropdowntype=="MSC"))) {
			var toolTip = genUtils.getElementToolTip(props.tooltip);
			genObj.scrhtml += '<div id="' + elementId + '_ext" ' + controlId + elementClasses + toolTip + elementState + ' style="' + elementStyleAttrs + '">';
			genObj.scrhtml += '<input id="' + elementId + '" class="sub-elt" ' + localRowNo + ' type="text"';
			genObj.scrhtml += " " + required;
			genObj.scrhtml += genUtils.getElementEvents(param,props);
			optionDesc = this.apz.getLabel(optionTitle);
			genObj.scrhtml +=' readonly="readonly"';
			genObj.scrhtml += ' value="' + optionDesc + '" >';
			genObj.scrhtml += '<span id="' + elementId
				+ '_span" class="sub-elt1"><svg aria-hidden="true" role="presentation" class="icon icon-down px34"><use xlink:href="#icon-down"></use></svg></span>';
			genObj.scrhtml += '<div id="' + elementId + '_div" class="sub-ctr" original-id="'+elementId+'"><ul id="' + elementId + '_so" role="menu">';
		}
		var endOptGrp = false;
		for (var o = 0; o < noOfOptions; o++) {
			optionObj = props.staticoptions[o];
			optionTitle = optionObj.description;
			optionValue = optionObj.value;
			optionId = elementId + "_option_" + optionTitle;
			if (optionValue==defaultValue || (this.apz.isNull(defaultValue) && o == 0)) {
				selectedString = 'selected="selected"';
				selectedCls = " is-selected";
				checkedString = ' checked="checked"';
			} else {
				selectedString = "";
				selectedCls = "";
				checkedString = "";
			}
			if ((props.dropdowntype=="NATIVE" || props.dropdowntype=="AUTOCOMPLETE" || props.dropdowntype=="ATO"|| props.dropdowntype=="MULTISELECTTAGS" || props.dropdowntype=="MST" )) {
				optionDesc = this.apz.getLabel(optionTitle);
				if (!this.apz.isNull(optionObj) && !this.apz.isNull(optionObj.group) && optionObj.group=="Y") {
					if (endOptGrp) {
						genObj.scrhtml += "</optgroup>";
					}
					endOptGrp = true;
					genObj.scrhtml += '<optgroup  id="' + optionId + '"  label="' + optionDesc + '">';
				} else {
					genObj.scrhtml += '<option  id="' + optionId + '"  value="' + optionValue + '" ' + selectedString + ' >' + optionDesc + '</option>';
				}
			} else if ((props.dropdowntype=="SIMPLE"|| props.dropdowntype=="SIMP" || this.apz.isNull(props.dropdowntype))) {
				optionDesc = this.apz.getLabel(optionTitle);
				genObj.scrhtml += '<li  id="' + optionId + '" class="' + selectedCls
						+ '" data-value="' + optionValue + '" tabindex="0" ' + selectedString + ' >' + optionDesc + '</li>';
			} else if (props.dropdowntype=="WITHSUBOPTIONS" || props.dropdowntype=="WSO") {
				genObj.scrhtml += '<li  id="' + optionId + '" class="' + selectedCls + '"  value="'
						+ optionValue + '" data-value="' + optionValue + '" tabindex="0" ' + selectedString + '>';
				optionDesc = this.apz.getLabel(optionTitle);
				genObj.scrhtml += '<span id="' + optionId + '_span" class="menu-expand"></span>' + optionDesc + '</li>';
			} else if (props.dropdowntype=="MULTISELECTCHECKBOX" || props.dropdowntype=="MSC") {
				genObj.scrhtml += '<li  id="' + optionId + '" class="form-group--checkbox" value="' + optionValue + '" tabindex="0" ' + selectedString + '>';
				optionDesc = this.apz.getLabel(optionTitle);
				genObj.scrhtml += '<input id="' + optionId + '_input" type="checkbox" ' + checkedString + '><label id="'
					+ optionId + '_label" for="' + optionId + '_input" aria-label="' + optionDesc+ '" readonly="readonly">' + optionDesc + '</label></li>';
			}
		}
		if (props.dropdowntype=="NATIVE" || props.dropdowntype=="MULTISELECTTAGS" || props.dropdowntype=="MST" || props.dropdowntype=="AUTOCOMPLETE" || props.dropdowntype=="ATO") {
			if (endOptGrp) {
				genObj.scrhtml += "</optgroup>";
			}
			genObj.scrhtml += "</select>";
			if (props.dropdowntype=="MULTISELECTTAGS" || props.dropdowntype=="MST" || props.dropdowntype=="AUTOCOMPLETE" || props.dropdowntype=="ATO") {
				genObj.scrhtml += "</div>";
			}
		} else if (((props.dropdowntype=="SIMPLE" || props.dropdowntype=="SIMP" || this.apz.isNull(props.dropdowntype)
				|| props.dropdowntype=="WITHSUBOPTIONS" || props.dropdowntype=="WSO" || props.dropdowntype=="MULTISELECTCHECKBOX" || props.dropdowntype=="MSC"))) {
			genObj.scrhtml += "</ul></div></div>";
		}
	}, genCheckbox : function(param,props) {
		var elementData = props;
	    var genObj = param.genObj;
		var containerData = props.cntrData;
		var rowNo = "",events="",defVal="";
	    var genUtils = param.genUtils;
		var elementId = "";
		var controlId = genUtils.getControlIdforWidget(param,props);
		var optionTitle = "";
		var elementTitle = "";
		var optionValue = "";
		var lcheckedstr = "";
		var elementState = genUtils.getElementState(props);
		var lcheckedval = "";
		var lindeterminateval = "";
		var luncheckedval = "";
		var ldefaultvalue = !this.apz.isNull(props.defaultvalue) ? props.defaultvalue : "";
		var mandatory = props.mandatory;
		var type = props.widgettype;
		var hint = props.hint;
		var hintReq = false;
		var noOfOptions = 0;
		var toolTip = "";
		var appearanceClass = !this.apz.isNull(props.appearance) ? " " + props.appearance.toLowerCase() : " pri";
		if (props.checkboxtype == "GENERIC" || props.checkboxtype == "APZ") {
			appearanceClass = !this.apz.isNull(props.appearance) ? " " + props.appearance.toLowerCase() : " pri";
		} else {
			appearanceClass = !this.apz.isNull(props.appearance) ? " " + props.appearance.toLowerCase() : " ";
		}
		var optionObj = "";
		var elementClasses = genUtils.getClassesForElement(param,props);
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		var varCls = "";
		var title = "";
		var elementTitle="";
		elementId = genUtils.getElementId(param,props);
		if (containerData.containerstate=="READONLY" || containerData.containerstate=="RO") {
				elementState = ' disabled="disabled"';
		}
		noOfOptions = 0;
		if (props.staticoptions) {
			noOfOptions = props.staticoptions.length;
		}
		//genObj.scrhtml += '<span id="' + elementId + '_xlabelspan" ' + localRowNo + ' for="'+ elementId + '" class="'+varCls+'">';
		if(props.parent.widgettype=="CHECKBOXGROUP"){
			genObj.scrhtml += '<span id="' + elementId + '_xlabelspan" ' + localRowNo + ' for="'+ elementId + '" class="'+varCls+props.cssclasses+'">';
		}else{	
			genObj.scrhtml += '<span id="' + elementId + '_xlabelspan" ' + localRowNo + ' for="'+ elementId + '" class="'+varCls+'">';
		}
		toolTip = genUtils.getElementToolTip(props.tooltip);
		if(!this.apz.isNull(props.hint)) {
			genObj.scrhtml += '<input id="' + elementId + '" aria-describedby="'+elementId+'_hnt" aria-labelledby="'+elementId+'_lbl" ' + toolTip + localRowNo + controlId;
			}else {
			genObj.scrhtml += '<input id="' + elementId + '" aria-describedby="" aria-labelledby="'+elementId+'_lbl" ' + toolTip + localRowNo + controlId;
   		}
	    if(mandatory=="Y") {
		 genObj.scrhtml+= 'required="required"';
		}
		genObj.scrhtml += genUtils.getElementEvents(param,props);
		if (noOfOptions > 0) {
			for (var o = 0; o < noOfOptions; o++) {
				optionObj = props.staticoptions[o];
				optionValue = optionObj.value;
				optionTitle = optionObj.description;
			  if (optionValue == ldefaultvalue && (o == 0)) {
				lcheckedstr = 'checked="checked"';
				lcheckedval = optionValue;
			  } else if (o == 0) {
					lcheckedval = optionValue;
			  } else if (o == 1) {
					luncheckedval = optionValue;
			  } else if (o == 2) {
					lindeterminateval = optionValue;
			  }
			}
		}
		if(!this.apz.isNull(lcheckedval)) {
			lcheckedval = 'checkedval="' + lcheckedval + '"';
		}
		if (!this.apz.isNull(luncheckedval)) {
			luncheckedval = 'uncheckedval="'+ luncheckedval+'"';
		}
		if (!this.apz.isNull(lindeterminateval)) {
			lindeterminateval = 'indeterminateval="' + lindeterminateval + '"';
		}
		genObj.scrhtml+= elementClasses + lcheckedstr + ' type="' + type + '" ' + elementState
					+ ' value="' + ldefaultvalue + '" ' + lcheckedval + luncheckedval + lindeterminateval;
		genObj.scrhtml+= ">";
		var title = "";
		elementTitle = this.apz.getLabel(props.title);
		if(elementData.parent.type == "PRESENTATIONELEMENT") {
			title = elementTitle;
		}
		genObj.scrhtml+= '<label id="'+elementId+'_lbl" class="flb" for="' + elementId + '">' + title + '</label>';
		genObj.scrhtml+= "</span>";
	}, genRadio : function(param,props) {
		var genObj = param.genObj;	
	    var genUtils = param.genUtils;
		var containerData = props.cntrData;
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		var elementId = "";
		var controlId = genUtils.getControlIdforWidget(param,props);
		var optionTitle = "";
		var optionValue = "";
		var optionState = "";
		var optionHint = "";
		var optionId = "";
		var optionlabelId = "";
		var selectedString = "";
		var variation = "";
		var index = "";
		var orientation = props.orientation;
		var elementState = genUtils.getElementState(props);
		var elementClasses = genUtils.getClassesForElement(param,props);
		var elementStyleAttrs = genUtils.getElmStyleAttributes(props);
		if (containerData.type == "TABLE" || containerData.type == "LIST") {
			index = props.rowNo.toString();
		}
		var noOfOptions = 0;
		if (props.staticoptions) {
			noOfOptions = props.staticoptions.length;
		}
		if (containerData.containerstate=="READONLY") {
			elementState = ' disabled="disabled"';
		}
		var toolTip = "";
		elementId = genUtils.getElementId(param,props);
		var optionObj = "";
		if (props.radiotype=="BUTTONBAR" || props.radiotype=="BB") {
			genObj.scrhtml += '<div id="' + elementId + '_div" class="button-group-buttons" apztype="radiogroup" data-toggle="buttons">';
		}
		for (var o = 0; o < noOfOptions; o++) {
			optionObj = props.staticoptions[o];
			optionTitle = optionObj.description;
			optionValue = optionObj.value;
			optionState = optionObj.state;
			optionHint = optionObj.hint;
			if (optionState=="DISABLED") {
				elementState = ' disabled="disabled"';
			} else {
				if (!containerData.containerstate=="READONLY" || !containerData.containerstate=="RO") {
					elementState = ' enabled="enabled"';
				}
			}
			var labelClass = "rlb";
			if (props.radiotype=="BUTTONBAR" || props.radiotype=="BB") {
				labelClass = "rdio-btn";
			}
			optionId = elementId + "_option_" + optionValue;
			optionlabelId = optionId + "_lbl";
			if (optionValue==props.defaultvalue) {
				selectedString = 'checked="checked"';
			} else {
				selectedString = "";
			}
			if (props.radiotype=="BUTTONBAR" || props.radiotype=="BB") {
				genObj.scrhtml += '<div id="' + optionlabelId + '_' + index + '" class="rdio-btn-grp">';
			} else {
				genObj.scrhtml += '<span id="' + optionlabelId + '_' + index + '"' + localRowNo + ' for="' + optionId + '" class="' + variation + '" >';
			}
			toolTip = genUtils.getElementToolTip(props.tooltip);
			genObj.scrhtml += 
					'<input id="' + optionId + '" ' + toolTip + elementClasses + ' style="'+ elementStyleAttrs + '" name="' + elementId + '_' + index
							+ '" type="radio" value="' + optionValue + '" ' + selectedString + elementState
							+ controlId +' aria-labelledby="' + optionId + '_span_' + index + '"';
			genObj.scrhtml += " " + genUtils.getElementEvents(param,props);
			genObj.scrhtml += ">";
			toolTip = genUtils.getElementToolTip(props.tooltip);
			optionTitle = this.apz.getLabel(optionTitle);
			optionHint = this.apz.getLabel(optionHint);
			genObj.scrhtml += '<label id="' + optionId + '_span_' + index + '" class="' + labelClass + '" '
				+ localRowNo + ' for="' + optionId + '" title="' + optionTitle + '">' + optionTitle + '</label>';
			if(orientation=="V" && !this.apz.isNull(optionHint)) {
				genObj.scrhtml += '<span id="' + optionId + '_hint_' + index + '" class="htx" >'+optionHint+'</span>';
			}
			if (props.radiotype=="BUTTONBAR"|| props.radiotype=="BB") {
				genObj.scrhtml += "</div>";
			} else {
				genObj.scrhtml += "</span>";
			}
		}
		if (props.radiotype=="BUTTONBAR"|| props.radiotype=="BB") {
			genObj.scrhtml += "</div>";
		}
	}, genText : function(param, props) {
		var genObj = param.genObj;
	    var genUtils = param.genUtils;
		var elementId = genUtils.getElementId(param,props);
		var headings = genUtils.getHeading(props);
		var defaultValue = !this.apz.isNull(props.defaultvalue) ? props.defaultvalue : "";
		var iconPosition = props.iconposition;
		var containerData = props.cntrData;
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		var elementClasses = genUtils.getClassesForElement(param,props);
		var controlId = genUtils.getControlIdforWidget(param,props);
		var icon = props.icon;
		var icnSize = genUtils.getIconSize(props);
		var finalDefaultValue = defaultValue;
		var toolTip = genUtils.getElementToolTip(props.tooltip);
		if (props.translatedefaultvalue=="Y") {
			finalDefaultValue = this.apz.getLabel(defaultValue);
		}
		var elementTitle = "";
		elementTitle = finalDefaultValue;
		genObj.scrhtml += '<' + headings + ' id="' + elementId + '" ' + controlId + elementClasses + toolTip + localRowNo;
		genObj.scrhtml += genUtils.getElementEvents(param,props);
		genObj.scrhtml += ">";
		if (!this.apz.isNull(icon) && this.apz.isNull(props.symbolvalue) && (iconPosition=="LEFT" || props.iconposition=="LFT")) {
			genObj.scrhtml += '<svg aria-hidden="true" role="presentation" class="ett-icon ' + props.icon + icnSize+'"><use xlink:href="#' + props.icon + '"></use></svg>';
		}
		if (props.symbol=="Y" && this.apz.isNull(props.icon)) {
			if (props.iconposition=="LEFT" || props.iconposition=="LFT") {
				genObj.scrhtml += '<span>' + props.symbolvalue + '</span>';
			}
		}
		genObj.scrhtml += '<span id="'+elementId+'_txtcnt">' + elementTitle + '</span>';
		if (!this.apz.isNull(icon) && this.apz.isNull(props.symbolvalue) && (iconPosition=="RIGHT" || props.iconposition=="RGT") ) {
			genObj.scrhtml += '<svg aria-hidden="true" role="presentation" class="ett-icon ' + props.icon + icnSize+'"><use xlink:href="#' + props.icon + '"></use></svg>';
		}
		if (props.symbol=="Y" && this.apz.isNull(props.icon)) {
			if (props.iconposition=="RIGHT" || props.iconposition=="RGT") {
				genObj.scrhtml += '<span>' + props.symbolvalue + '</span>';
			}
		}
		genObj.scrhtml += '</' + headings + '>';

	}, genButton : function(param, props) {
			var genObj = param.genObj;
		    var genUtils = param.genUtils;
			var elementId = "";
			var elementData = props;
			var elementClasses = genUtils.getClassesForElement(param, props);
			var containerData = props.cntrData;
			var localRowNo = "";
			if (containerData.type == "TABLE" || containerData.type == "LIST") {
				localRowNo = ' rowno ="0"';
			}
			var controlId = genUtils.getControlIdforWidget(param,props);
			var elementState = "";
			var elementTitle = "";
			var toolTip = "";
			var width = genUtils.getElmStyleAttributes(props);
			elementState = genUtils.getElementState(props);
			if (containerData.containerstate == "READONLY" || containerData.containerstate == "RO") {
				elementState = " disabled=\"disabled\"";
			}
			if (!this.apz.isNull(width)) {
				width = "style=\"" + width + "\" ";
			}
			elementId = genUtils.getElementId(param, props);
			//int noOfLangs = appParams.langs.size();
			genObj.scrhtml += "<button " + controlId + " id=\"" + elementId + "\"" + localRowNo + " type=\"button\"" + controlId;
			genObj.scrhtml += " " + genUtils.getButtonElementEvents(param, props);
			var elmTitle = elementData.title ? elementData.title:"";
			if (containerData.widgettype == "TABLE") {
				elmTitle = elementData.defaultvalue;
			}
			//for (int l = 0; l < noOfLangs; l++) {
				//var language = appParams.langs.get(l);
				//elementTitle = GenUtils.getLITDesc(language, elmTitle, appData.lits);
				elementTitle = elmTitle;
				toolTip = genUtils.getElementToolTip(elementData.tooltip);
				if(elementData.state == "DISABLED" || elementData.state=="DS" || containerData.containerstate == "READONLY" || containerData.containerstate == "RO") {
					genObj.scrhtml += elementClasses + elementState + width + toolTip + " aria-disabled = \"true\" >";
				}else {
					genObj.scrhtml += elementClasses + elementState + width + toolTip + " >";
				}
				genObj.scrhtml += genUtils.getButtonIcon(param, elementData, elementTitle, elementId);
			//}
			genObj.scrhtml += "</button>";
	}, genImage : function(param, props) {
			var genObj = param.genObj;
		    var genUtils = param.genUtils;
		    var elementData = props;
		    var containerData = props.cntrData;
			var svgCont = "";
			var svngNams = "";
			/*if (elementData.defaultimage.contains(".svg")) {
				svngNams = elementData.defaultimage;
			}*/
			if (!this.apz.isNull(elementData.defaultimage) && elementData.defaultimage.includes(".svg")) {
				svngNams = elementData.defaultimage;
			}
			if (!this.apz.isNull(svngNams)) {
				//imgData = themeData.imagefiles.filesmap.get(svngNams);
				//var imgPath = DataUtils.getObjectFilePath(imgData);
				var imgPath = "apps/styles/themes/" + this.apz.theme + "/img/" + elementData.defaultimage + "";
				//svgCont = Appzillon.files.getFileContent(imgPath, Const.RESOURCELOCATION_LOCAL);
				svgCont = this.apz.getFile({"path" : imgPath});
			}
			var sourcePath = "";
			var lsource = elementData.defaultimage;
			if (!this.apz.isNull(lsource)  && lsource != "DEFAULT") {
				//sourcePath = GenUtils.getCssLocation(osName, appData.prjdata.appid) + "/" + theme + "/img/" + lsource;
				sourcePath = "apps/styles/themes/" + this.apz.theme + "/img/" + elementData.defaultimage + "";
				// given same path as imgPath
			}
			var elementClasses = genUtils.getClassesforImage(param, elementData);
			var controlId = genUtils.getControlIdforWidget(param, props);
			var elementId = "";
			var lcustwidth = "";
			var lcustheight = "";
			//var lngsrctag = genUtils.getngsrc(elementData, templateDataObj);
			if (elementData.size == "CUSTOM") {
				if(!this.apz.isNull(elementData.customwidth)) {
					lcustwidth = "width:" + elementData.customwidth + elementData.customwidthtype + ";";
				}
				if(!this.apz.isNull(elementData.customheight)) {
					lcustheight = "height:" + elementData.customheight + elementData.customheighttype + ";";
				}
			}
			var toolTip = "";
			elementId = genUtils.getElementId(param, elementData);
			var localRowNo = "";
			if (containerData.type == "TABLE" || containerData.type == "LIST") {
				localRowNo = ' rowno ="0"';
			}
			//int noOfLangs = appParams.langs.size();
			//for (int l = 0; l < noOfLangs; l++) {
				//var language = appParams.langs.get(l);
				toolTip = genUtils.getElementToolTip(props.tooltip);
				//var elementTitle = genUtils.getLITDesc(language, elementData.title, appData.lits);
				var elementTitle = !this.apz.isNull(elementData.title) ? elementData.title : "";
				if (!this.apz.isNull(svgCont)) {
					genObj.scrhtml += "<span  id=\"" + elementId + "\" type=\"SVG\"" + toolTip + localRowNo + elementClasses + " style=\"" + lcustwidth + " " + lcustheight + "\"" + controlId
									+ genUtils.getElementEvents(param, elementData)
									+ " title=\"" + elementTitle + "\">";
					genObj.scrhtml += svgCont;
					genObj.scrhtml += "</span>";
					genObj.scrhtml += "<img  id=\"" + elementId + "_img\"" + toolTip + localRowNo + " src=\"" + sourcePath + "\" "
									+ "class = \"sno\" style=\"" + lcustwidth + " " + lcustheight + "\""
									+ controlId;
					genObj.scrhtml += " " + genUtils.getElementEvents(param, elementData)
									+ " title=\"" + elementTitle + "\"/>";
				} else {
					genObj.scrhtml += "<img  id=\"" + elementId + "\"" + toolTip + localRowNo + " src=\"" + sourcePath + "\" "
									+ elementClasses + "style=\"" + lcustwidth + " " + lcustheight + "\""
									+ controlId;
					genObj.scrhtml += " " + genUtils.getElementEvents(param, elementData)
									+ " title=\"" + elementTitle + "\"/>";
					genObj.scrhtml += "<span  id=\"" + elementId + "_svg\" type=\"SVG\"" + toolTip + localRowNo
									+ "class = \"sno\" style=\"" + lcustwidth + " " + lcustheight + "\""
									+ controlId
									+ genUtils.getElementEvents(param, elementData)
									+ " title=\"" + elementTitle + "\">";
					genObj.scrhtml += svgCont;
					genObj.scrhtml += "</span>";
				}
			//}
	}, genIcon : function(param,props){
			var genObj = param.genObj;
			var elementId = "";
			var genUtils = param.genUtils;
			var controlId = genUtils.getControlIdforWidget(param, props);
			elementId = genUtils.getElementId(param,props);
			var elementClasses = genUtils.getClassesForElement(param,props);
			var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
			//int noOfLangs = appParams.langs.size();
			//for (var l = 0; l < noOfLangs; l++) {
				//var language = appParams.langs.get(l);
			var toolTip = genUtils.getElementToolTip(props.tooltip);
			genObj.scrhtml+= "<svg aria-hidden='true' id='" + elementId + "' " + toolTip + localRowNo + elementClasses + controlId;
			//}
			genObj.scrhtml+= " " + genUtils.getElementEvents(param,props)+ " ><use xlink:href='#" + props.icon + "'></use></svg>";
	}, genToggleSwitch : function(param, props) {
	    var genObj = param.genObj;
	    var genUtils = param.genUtils;
	    var containerData = props.cntrData;
	    var elementData = props;
		var elementClasses = genUtils.getClassesForElement(param, props);
		var elementId = "";
		var type = "radio";
		var elementState = "";
		var optionValue = "";
		var checked = "";
		var disabledClass = elementData.state == "DISABLED" ? " disabled" : "";
		var nonWrapCls = genUtils.getClassesForNonWrappedElms(param, props);
		var widthCls = genUtils.getWidthClass(props);
		var controlId = genUtils.getControlIdforWidget(param, props);
		var defaultValue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		var localRowNo = genUtils.getRowno(param, props);
		var vartnClass = !this.apz.isNull(elementData.variation) ? " " + elementData.variation.toLowerCase() : "";
		var toggleswitchtype = !this.apz.isNull(elementData.toggleswitchtype) ? elementData.toggleswitchtype : "WITHLABEL";
		var elementStyleAttrs = genUtils.getElmStyleAttributes(elementData);
		var elementEvents = genUtils.getElementEvents(param, props);
		var noOfOptions = elementData.staticoptions.length;
		var toolTip = "";
		elementId = genUtils.getElementId(param, props);
		elementState = genUtils.getElementState(elementData);
		if (containerData.containerstate == "READONLY" || containerData.containerstate == "RO") {
			elementState = " disabled=\"disabled\"";
		}
		var appearanceCls = !this.apz.isNull(elementData.appearance) ? " " + elementData.appearance.toLowerCase() : " pri";
		if (toggleswitchtype == "WITHLABEL" || toggleswitchtype == "WL") {
				genObj.scrhtml += "<div id=\"" + elementId + "\" class=\"ett-swch " + appearanceCls + " " + widthCls
							+ disabledClass + nonWrapCls + vartnClass + "\" apztype=\"toggleswitch\" "
						    + " aria-labelledby = \"" + elementId + "_ctrl_div\" " + " style=\""
							+ elementStyleAttrs + "\">";
			var switchOption = "";
			/** added to limit 2 options to ToggleSwitch **/
			noOfOptions = noOfOptions > 2 ? 2 : noOfOptions;
			for (var o = 0; o < noOfOptions; o++) {
				checked = "";
				let desc = elementData.staticoptions[o].description;
				if ((o + 1) == 1) {
					switchOption = !this.apz.isNull(desc) ? desc : elementData.switchoption1;
				} else {
					switchOption = !this.apz.isNull(desc) ? desc : elementData.switchoption2;
				}
				if (noOfOptions > 0) {
					optionObj = elementData.staticoptions[o];
					optionValue = optionObj.value;
				}
				if ((optionValue == defaultValue) || (defaultValue == "" && o == 0)) {
					checked = " checked=\"checked\"";
				}
				genObj.scrhtml += "<input " + checked + " name=\"" + elementId + "\" id=\"" + elementId + "_" + o + o
								+ "\" type=\"" + type + "\" value=\"" + optionValue + "\" " + " " + localRowNo + elementEvents;
				toolTip = genUtils.getElementToolTip(elementData.tooltip);
				genObj.scrhtml += elementClasses + toolTip + elementState + controlId;
				genObj.scrhtml += ">";
				genObj.scrhtml += "<label " + elementClasses + toolTip
						+ " for=\"" + elementId + "_" + o + o + "\" >";
				if ((o + 1) == 1) {
					switchOption = !this.apz.isNull(desc) ? desc : elementData.switchoption1;
				} else {
					switchOption = !this.apz.isNull(desc) ? desc : elementData.switchoption2;
				}
				genObj.scrhtml += switchOption;
				genObj.scrhtml += "</label>";
			}
			genObj.scrhtml += "<span class=\"tslide\"></span>";
			genObj.scrhtml += "</div>";
		} else {
			var selectedval = "", unselectedval = "";
			if (noOfOptions > 0) {
				for (var o = 0; o < noOfOptions; o++) {
					optionObj = elementData.staticoptions[o];
					optionValue = optionObj.value;
					if ((optionValue == defaultValue) && (o == 0)) {
						checked = " checked=\"checked\"";
						selectedval = optionValue;
					} else if (o == 0) {
						selectedval = optionValue;
					} else if (o == 1) {
						unselectedval = optionValue;
					}
				}
			}
			if (!this.apz.isNull(selectedval)) {
				selectedval = " checkedval=\"" + selectedval + "\" ";
			}
			if (!this.apz.isNull(unselectedval)) {
				unselectedval = " uncheckedval=\"" + unselectedval + "\" ";
			}
			if (toggleswitchtype == "WITHOUTLABEL" || toggleswitchtype == "WOL") {
				genObj.scrhtml += "<label id=\"" + elementId + "_ctrl_label\" class=\"ett-togl " + appearanceCls + " "
							+ disabledClass + nonWrapCls + vartnClass + " " + widthCls + "\""
							+ " style=\"" + elementStyleAttrs + "\">";
				toolTip = genUtils.getElementToolTip(elementData.tooltip);
				genObj.scrhtml += "<input id=\"" + elementId + "\" " + localRowNo + elementClasses + elementState
							+ checked + toolTip + elementEvents + " apztype=\"toggleswitch\" type=\"CHECKBOX\""
							+ selectedval + unselectedval + ">";
				genObj.scrhtml += "<div class=\"slider\"></div></label>";
			}
		}
	}, genFileBrowser : function(param,props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var containerData = props.cntrData;
		var elementId = "";
		var elementState = "";
		var required = "";
		var nonWrapCls = genUtils.getClassesForNonWrappedElms(param, props);
		var controlId = genUtils.getControlIdforWidget(param, props);
		var mandatory = elementData.mandatory;
		var icon = !this.apz.isNull(elementData.icon) ? elementData.icon : "";
		var defaultValue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		var buttonClickFun = elementData.buttonclickevent ? elementData.buttonclickevent.replace('"', '&quot;') : "";
		var elementClasses = genUtils.getClassesForElement(param,props);
		var vartnClass = !this.apz.isNull(elementData.variation) ? " " + elementData.variation.toLowerCase() : "";
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		if (mandatory=="Y") {
			required = ' required="required"';
		}
		elementId = genUtils.getElementId(param,props);
		var toolTip = "";
		var appearanceClass = !this.apz.isNull(elementData.appearance) ? " " + elementData.appearance.toLowerCase() : " pri";
		var disabledClass = (elementData.state=="DISABLED" || elementData.state=="DS") ? " disabled" : "";
		elementState = genUtils.getElementState(props);
		if (containerData.containerstate=="READONLY" || containerData.containerstate=="RO") {
			elementState = ' disabled="disabled"';
		}
		genObj.scrhtml+= '<ul ' + controlId + ' class="nrb ett-file ' + nonWrapCls + appearanceClass + disabledClass + vartnClass + '"><li>';
		genObj.scrhtml+= '<a class="filebox ett-bttn med' + appearanceClass + disabledClass + '" href="javascript:;">';
    	//genObj.scrhtml+= '<svg class="ett-icon icon-upload px24"><use xlink:href="#icon- upload "></use></svg>';//Added by Krish
    	genObj.scrhtml += '<svg class="ett-icon icon-cloud-upload px24"><use xlink:href="#icon-cloud-upload"></use></svg>'; //Added by Krish : investor
		toolTip = genUtils.getElementToolTip(elementData.tooltip);
		genObj.scrhtml+= '<input id="' + elementId + '"' + localRowNo + toolTip + ' type="file"' + '';
		genObj.scrhtml+= " " + required + "";
		genObj.scrhtml+= " " + genUtils.getElementEvents(param,props);
		if (elementData.browsefiletype!="SINGLEFILE" || elementData.browsefiletype!="SFL") {
			genObj.scrhtml+= ' multiple = "multiple"';
		}
    
		var minMaxWidthCls = genUtils.getElmStyleAttributes(props);
		var finalDefaultValue = defaultValue;
		if (elementData.translatedefaultvalue=="Y") {
			finalDefaultValue = this.apz.getLabel(defaultValue);
		}
		genObj.scrhtml+= elementClasses + ' style="' + minMaxWidthCls + '" ' + elementState + '  value="' + finalDefaultValue + '"/>';
		genObj.scrhtml += "Upload"; //Changed by Krish : investor
		genObj.scrhtml+= "</a>";
		if (elementData.uploadbuttonrequired=="Y") {
			genObj.scrhtml+= '<a class="ett-bttn icl med inf' + appearanceClass + disabledClass
					+ '" href="javascript:;" onclick="' + buttonClickFun + '" role="button" aria-label="Upload"';
			genObj.scrhtml+= ' title="Upload"><svg aria-hidden="true" class="icon icon-upload px24">';
			genObj.scrhtml+= '<use xlink:href="#icon-upload"></use></svg>';
			genObj.scrhtml+= "Upload</a></li>";
			genObj.scrhtml+= "<li>";
			genObj.scrhtml+= '<span id="selectedfiles" class="filevalue" >';
			////// selected file names will get added here////////
			genObj.scrhtml+= '<svg aria-hidden="true" role="presentation" class="icon ' + icon + ' px24"><use xlink:href="#'
					+ icon + '"></use></svg>';
			genObj.scrhtml+= "</span>";
		}
		genObj.scrhtml+= "</li>";
		genObj.scrhtml+= "</ul>";
	}, genTags : function(param,props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var containerData = props.cntrData;
		var type = "text";
		var maxLength = "";
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		var mandatory = elementData.mandatory;
		var dataType = elementData.datatype;
		var ldefaultvalue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		var elementId = genUtils.getElementId(param,props);
		var elementClasses = genUtils.getClassesForElement(param,props);
		var stateClass = (elementData.state=="DISABLED") ? " dis" : "";
		var disAttr = "";
		var dispnone = (elementData.state=="DISABLED") ? " display:none" : "";
		var widthClass = genUtils.getWidthClass(props);
		var appearanceClass = !this.apz.isNull(elementData.appearance) ? " " + elementData.appearance.toLowerCase() : " pri";
		var controlId = genUtils.getControlIdforWidget(param, props);
		var minMaxWidthCls = genUtils.getElmStyleAttributes(props);
		var elementState = genUtils.getElementState(props);
		var icon = !this.apz.isNull(elementData.icon) ? elementData.icon : "";
		if (containerData.containerstate=="READONLY" || containerData.containerstate=="RO") {
			disAttr = ' disabled="disabled"';
			elementState = ' readonly="readonly"';
		}
		var required = "";
		if (mandatory=="Y") {
			required = 'required="required"';
		}
		if (dataType=="NUMBER" || dataType=="INTEGER") {
			type = "Tel";
		}
		var toolTip = genUtils.getElementToolTip(props.tooltip);
		var finalDefaultValue = ldefaultvalue;
		var placeHolder = "";
		if (!this.apz.isNull(elementData.placeholder)) {
			placeHolder = this.apz.getLabel(elementData.placeholder);
		}
		var vartnClass = !this.apz.isNull(elementData.variation) ? " " + elementData.variation : "";
		if (elementData.translatedefaultvalue=="Y") {
			finalDefaultValue = this.apz.getLabel(ldefaultvalue);
		}
		var hasFormula = "";
		genObj.scrhtml+= '<div class="ett-tags etb-tags '  + widthClass + appearanceClass + vartnClass +  stateClass + '"' + disAttr + '><input id="'
					+ elementId + '" ' + controlId + elementClasses + toolTip + localRowNo+ ' style="' + minMaxWidthCls + dispnone + '" type="'
						+ type + '" ' + hasFormula;
		genObj.scrhtml+= " " + required + "";
		genObj.scrhtml+= ' maxlength ="' + maxLength + '" placeholder="' + placeHolder + '" ' + elementState + ' value="' + finalDefaultValue + '" ';
		genObj.scrhtml+= " " + genUtils.getElementEvents(param,props);
		genObj.scrhtml+= '><svg class="icon ' + icon + ' px24"><use xlink:href="#'+ icon + '"></use></svg></div>';
	}, genTextArea : function(param,props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var containerData = props.cntrData;
		var elementState = "";
		var maxLength = !this.apz.isNull(elementData.maxstringlength) ? elementData.maxstringlength : "";
		var lcustwidth = "";
		var lcustheight = "";
		var buttonState = "";
		var defaultValue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		var dataType = elementData.datatype;
		var mandatory = elementData.mandatory;
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		var lovName = elementData.lovname;
		var iconPosition = elementData.iconposition;
		var nonWrapCls = genUtils.getClassesForNonWrappedElms(param, props);
		var isDate = dataType=="DATE" ? true : false;
		var isDateTime = dataType=="DATETIME" ? true : false;
		var hasLov = (!this.apz.isNull(lovName) && lovName!="Select") ? true : false;
		var iconPosRight = (iconPosition=="RIGHT" || iconPosition=="RGT") ? true : false;
		var iconPosLeft = (iconPosition=="LEFT" || iconPosition=="LFT") ? true : false;
		if (elementData.width=="CUSTOM") {
			lcustwidth = "width:" + elementData.customwidth + elementData.customwidthtype + ";";
		}
		if(!this.apz.isNull(elementData.customheight) ? elementData.customheight : "") {
			lcustheight = "height:" + elementData.customheight +" "+ elementData.customheighttype + ";";
		}
		var lovIcon = "icon-grid";
		var semanticsStr = !this.apz.isNull(elementData.lovsemantics) ? " " + elementData.lovsemantics : " inf";
		var lovappearance = !this.apz.isNull(elementData.lovappearance) ? " " + elementData.lovappearance : " pri";
		var widthClass = genUtils.getWidthClass(props);
		var elementClasses = genUtils.getClassesForElement(param,props);
		var controlId = genUtils.getControlIdforWidget(param, props);
		var elementId = genUtils.getElementId(param,props);
		var iconPositionClass = "";
		if (hasLov) {
			genObj.scrhtml+= '<span class="ecn ' + widthClass + nonWrapCls + '" style="' + lcustwidth + '">';
		}
		elementState = genUtils.getElementState(props);
		if (elementData.state=="DISABLED" || elementData.state=="DS") {
			buttonState = ' disabled="disabled"';
		}
		if (containerData.containerstate=="READONLY" || containerData.containerstate=="RO") {
			if (elementData.state=="DISABLED" || elementData.state=="DS") {
				elementState += ' readonly="readonly"';
			} else {
				elementState = ' readonly="readonly"';
			}
			buttonState = ' disabled="disabled"';
		}
		if (hasLov) {
			var lovClass = "";
			if (iconPosLeft) {
				lovClass = " ett-lofv btl txar";
			} else {
				lovClass = " ett-lofv btr txar";
			}
			var appearanceCls = !this.apz.isNull(elementData.appearance) ? " " + elementData.appearance.toLowerCase() : " pri";
			genObj.scrhtml+= '<span class="etw-100 ' + iconPositionClass + appearanceCls + lovClass + '">';
		}
		if (iconPosLeft) {
			if (hasLov) {
				var lovTitle = "List of Values";
				genObj.scrhtml+= 
						'<span class="etw-5"><button id="' + elementId + '_lov" class="ett-bttn med' + lovappearance + semanticsStr + '" onclick="apz.lov.callLov(&quot;' + elementId + '&quot;,&quot;'
								+ param.appId + '__' + param.scr + '__' + containerData.name + '&quot;,&quot;' + param.appId + '&quot;);' + '" title="' + lovTitle + '" aria-label="LOV" ' + buttonState
								+ '><svg aria-hidden="true" class="ett-icon '+lovIcon+' px24"><use xlink:href="#' + lovIcon + '"></use></svg></button></span>';
			}
		}
		if (hasLov) {
			genObj.scrhtml+= "<span>";
		}
		var toolTip = genUtils.getElementToolTip(props.tooltip);
		genObj.scrhtml+= '<textarea id="' + elementId + '" ' + toolTip + elementClasses + localRowNo + controlId;
		genObj.scrhtml+= " " + genUtils.getElementEvents(param,props);
		var finalDefaultValue = defaultValue;
		var placeHolder = "";
		if (!this.apz.isNull(elementData.placeholder)) {
			placeHolder = this.apz.getLabel(elementData.placeholder);
		}
		if (elementData.translatedefaultvalue=="Y") {
			finalDefaultValue = this.apz.getLabel(defaultValue);
		}
		if (!hasLov) {
			genObj.scrhtml+= 
					elementState + ' style="' + lcustheight + lcustwidth + '" placeholder="' + placeHolder + '" cols="75" maxlength ="' + maxLength + '" value="' + finalDefaultValue + '" ';
		} else {
			genObj.scrhtml+= 
					elementState + ' style="' + lcustheight + '" placeholder="' + placeHolder + '" cols="75" maxlength ="' + maxLength + '" value="' + finalDefaultValue + '" ';
		}
		genObj.scrhtml+= '>' + finalDefaultValue + '';
		genObj.scrhtml+= "</textarea>";
		var hasFormula = "";
		if (hasLov) {
			genObj.scrhtml+= "</span>";
		}
		if (iconPosRight) {
			if (hasLov) {
				var lovTitle = "List of Values";
				genObj.scrhtml+= 
						'<span class="etw-5"><button id="' + elementId + '_lov" class="ett-bttn med' + lovappearance + semanticsStr + '" onclick="apz.lov.callLov(&quot;' + elementId + '&quot;,&quot;'
								+ param.appId + '__' + param.scr + '__' + containerData.name + '&quot;,&quot;' + param.appId + '&quot;);' + '" title="' + lovTitle + '" aria-label="LOV" ' + buttonState
								+ '><svg aria-hidden="true" class="ett-icon ' + lovIcon + ' px24"><use xlink:href="#' + lovIcon + '"></use></svg></button></span>';
			}
		}
		if (hasLov) {
			genObj.scrhtml+= " </span></span>";
		}
	}, genHyperlink : function(param,props) {
		var genObj = param.genObj;
		var elementId = "";
		var genUtils = param.genUtils;
		var elementData = props;
		var controlId = genUtils.getControlIdforWidget(param, props);
		var defaultValue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		var toolTip = "";
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		var elementClasses = genUtils.getClassesForElement(param,props);
		elementId = genUtils.getElementId(param,props);
		var url = "javascript:";
		if (!this.apz.isNull(elementData.url)) {
			url = elementData.url;
		}
		toolTip = genUtils.getElementToolTip(props.tooltip);
		genObj.scrhtml+= '<a ' + controlId + ' id="' + elementId + '" '
				+ toolTip + localRowNo + elementClasses + ' href="' + url + '" aria-label="Hyperlink" role="button"';
		genObj.scrhtml+= genUtils.getElementEvents(param,props) + ">";
		if (!this.apz.isNull(elementData.icon) && (elementData.iconposition=="LEFT" || elementData.iconposition=="LFT")) {
			genObj.scrhtml+= '<svg aria-hidden="true" class="icon ' + elementData.icon
					+ ' px18"><use xlink:href="#' + elementData.icon + '"></use></svg>';
		}
		var finalDefaultValue = defaultValue;
		if (elementData.translatedefaultvalue=="Y") {
			finalDefaultValue = this.apz.getLabel(defaultValue);
		}
		genObj.scrhtml+= '<span id="'+elementId+'_txtcnt">'+finalDefaultValue+'</span>';
		if (!this.apz.isNull(elementData.icon) && (elementData.iconposition=="RIGHT" || elementData.iconposition=="RGT")) {
			genObj.scrhtml+= '<svg aria-hidden="true" class="icon ' + elementData.icon + ' px18"><use xlink:href="#'
					+ elementData.icon + '"></use></svg>';
		}
		genObj.scrhtml+= "</a>";
	}, genBadge : function(param,props) {
		var genObj = param.genObj;
		var elementId = "";
		var genUtils = param.genUtils;
		var elementData = props;
		var elementId = genUtils.getElementId(param,props);
		var elementClasses = genUtils.getClassesForElement(param,props);
		var controlId = genUtils.getControlIdforWidget(param, props);
		var defaultValue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		var localRowNo = props.rowNo!=-1 ? ' rowno ="'+props.rowNo.toString()+'"' : "";
		var toolTip = genUtils.getElementToolTip(elementData.tooltip);
		genObj.scrhtml+= '<span id="' + elementId + '" ' + controlId + elementClasses + localRowNo + toolTip + '';
		genObj.scrhtml+= "" + genUtils.getElementEvents(param,props) + ">";
		var finalDefaultValue = defaultValue;
		if (elementData.translatedefaultvalue=="Y") {
			finalDefaultValue = this.apz.getLabel(defaultValue);
		}
		genObj.scrhtml+= finalDefaultValue;
		genObj.scrhtml+= "</span>";
	}, genCheckBoxGroup : function(param,props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var elmId = genUtils.getElementId(param,props);
		var appearanceClass = " pri";
		if (elementData.checkboxtype == "GENERIC" || elementData.checkboxtype == "APZ") {
			appearanceClass = !this.apz.isNull(elementData.appearance) ? " " + elementData.appearance.toLowerCase() : " pri";
		}
		var vartnClass = !this.apz.isNull(elementData.variation) ? " " + elementData.variation.toLowerCase() : "";
		var orientationClass = elementData.orientation == "VERTICAL" ? " ver" : " hor";
		var cssClass = genUtils.getCustomClass(elementData);
		var noOfChilds = elementData.childs.length;
		if(!this.apz.isNull(elementData.hint)) {
			genObj.scrhtml += "<span class=\"etb-chek ett-chek" + appearanceClass + orientationClass + vartnClass + cssClass +"\" aria-describedby=\""+elmId+"_hnt\">";
		} else {
			genObj.scrhtml += "<span class=\"etb-chek ett-chek" + appearanceClass + orientationClass + vartnClass + cssClass +"\" aria-describedby=\"\">";				
		}
		for (var c = 0; c < noOfChilds; c++) {
			var childObj = elementData.childs[c];
			childObj.cntrData = childObj.parent = elementData;
			childObj.rowNo = -1;
			genUtils.updateGlobalMapInfo(param, childObj);
			this.genCheckbox(param, childObj);
		}
		genObj.scrhtml += "</span>";
	}, genGauge : function(param,props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var elementId = genUtils.getElementId(param,props);
		var elementClasses = genUtils.getClassesForElement(param,props);
		var controlId = genUtils.getControlIdforWidget(param, props);
		var customHeight = !this.apz.isNull(elementData.customheight) ? elementData.customheight + elementData.customheighttype + ";" : elementData.customheighttype + ";";
		var localRowNo = genUtils.getRowno(param,props);
		var toolTip = genUtils.getElementToolTip(elementData.tooltip);
		genObj.scrhtml += "<div id=\"" + elementId + "\"" + controlId + elementClasses
					+ localRowNo + toolTip + " style=\"height:" + customHeight + "\"";
		genObj.scrhtml += genUtils.getElementEvents(param,props);
		genObj.scrhtml += "></div>";
	}, genInputWithButton : function(param,props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var containerData = props.cntrData;
		var required = "";
		var elementState = "";
		var placeHolder = "";
		var type = "text";
		var disabledClass = "";
		var customDirective = "";
		var defaultValue = elementData.defaultvalue;
		var finalDefaultValue = !this.apz.isNull(defaultValue) ? defaultValue : "";
		var maxLength = !this.apz.isNull(elementData.maxstringlength) ? elementData.maxstringlength : "";
		var elementName = elementData.elementname;
		var dataType = elementData.datatype;
		var isDate = dataType == "DATE";
		var isDateTime = dataType == "DATETIME";
		elementState  = genUtils.getElementState(elementData);
		if(elementData.state == "DISABLED" || elementData.state == "DS") {
			disabledClass = " disabled";
		}
		if (containerData.containerstate == "READONLY" || containerData.containerstate == "RO") {
			disabledClass = " disabled";
			if(elementData.state == "DISABLED" || elementData.state == "DS") {
				elementState += " readonly=\"readonly\"";
			} else {
				elementState = " readonly=\"readonly\"";
			}
		}
		var mandatory = !this.apz.isNull(props.mandatory) ? props.mandatory : "N";
		if (mandatory=="Y") {
			required = 'required="required"';
		}
		var buttonClickFunction = !this.apz.isNull(elementData.buttonclickevent) ? elementData.buttonclickevent.replace("\"", "&quot;") : "";
		var minMaxWidthCls = genUtils.getElmStyleAttributes(elementData);
		var elementClasses = genUtils.getClassesForElement(param,props);
		var controlId = genUtils.getControlIdforWidget(param,props);
		var elementId = genUtils.getElementId(param,props);
		var btnSemCls = !this.apz.isNull(elementData.semantics) ? " " + elementData.semantics : "";
		var widthClass = genUtils.getWidthClass(props);
		var localRowNo = genUtils.getRowno(param,props);
		var nonWrapCls = genUtils.getClassesForNonWrappedElms(param, elementData);
		var vartnClass = !this.apz.isNull(elementData.variation) ? " " + elementData.variation.toLowerCase() : "";
		var appearanceClass = !this.apz.isNull(elementData.appearance) ? " " + elementData.appearance.toLowerCase() : " pri";
		var buttonPosition = !this.apz.isNull(elementData.buttonposition) ? elementData.buttonposition : "RIGHT";
		var iconPosCls = "";
		if (elementData.password == "YES") {
			type = "password";
		}
		if (dataType == "NUMBER" || dataType == "INTEGER") {
			type = "Tel";
			if (osName == "IOS" && dataType == "NUMBER") {
				type = "number";
			}
		}
		if (!this.apz.isNull(elementData.icon)) {
			if (elementData.iconposition == "RIGHT" || elementData.iconposition == "RGT") {
				iconPosCls = " icr";
			} else {
				iconPosCls = " icl";
			}
		}
		if (!this.apz.isNull(widthClass)) {
			widthClass = " " + widthClass;
		}
		var butPosCls = " btl";
		if (buttonPosition == "RIGHT" || buttonPosition == "RGT") {
			butPosCls = " btr";
		}
		genObj.scrhtml += "<span id=\"" + elementId + "_span\" " + controlId + " class=\"ecn"
				+ widthClass + nonWrapCls + vartnClass + "\">";
		genObj.scrhtml += "<span class=\"etw-100" + butPosCls + appearanceClass + "\">";
		var nativeDateAttr = elementData.datetype == "OSSPECIFIC" ? "type = \"Date\"" : "type = \""+type+"\"";
		if (buttonPosition == "RIGHT" || buttonPosition == "RGT") {
				placeHolder = !this.apz.isNull(elementData.placeholder) ? elementData.placeholder : "";
				var toolTip = genUtils.getElementToolTip(elementData.tooltip);
				genObj.scrhtml += "<span><input id=\"" + elementId + "\" " + localRowNo + nativeDateAttr + elementClasses
						+ toolTip + " style=\"" + minMaxWidthCls + "\" "  + "";
				genObj.scrhtml += " " + required + " maxlength =\"" + maxLength
						+ "\" placeholder=\"" + placeHolder + "\" " + elementState + "";
				genObj.scrhtml += " value=\"" + finalDefaultValue + "\" " + genUtils.getElementEvents(param, elementData);
				genObj.scrhtml += "></span>";
				if(elementData.state == "DISABLED" || elementData.state == "DS" || containerData.containerstate == "READONLY" || containerData.containerstate == "RO"){
				genObj.scrhtml += "<span class=\"etw-5\"><button id=\"" + elementId + "_button\" class=\"ett-bttn med"
						+ appearanceClass + iconPosCls +" " + btnSemCls + disabledClass + "\" onclick=\"" + buttonClickFunction + "\" aria-disabled=\"true\">";
				}else{
					genObj.scrhtml += "<span class=\"etw-5\"><button id=\"" + elementId + "_button\" class=\"ett-bttn med"
							+ appearanceClass + iconPosCls +" " + btnSemCls + disabledClass + "\" onclick=\"" + buttonClickFunction + "\">";						
				}
				placeHolder = !this.apz.isNull(elementData.placeholder) ? elementData.placeholder : "";
				var btnTitle = !this.apz.isNull(elementData.buttontitle) ? elementData.buttontitle : "";
				genObj.scrhtml += genUtils.getButtonIcon(param, elementData, btnTitle, elementId);
				genObj.scrhtml +=  "</button></span>";
		} else if (buttonPosition == "LEFT" || buttonPosition == "LFT") {
				placeHolder = !this.apz.isNull(elementData.placeholder) ? elementData.placeholder : "";
				var toolTip = genUtils.getElementToolTip(elementData.tooltip);
				if(elementData.state == "DISABLED" || elementData.state == "DS" || containerData.containerstate == "READONLY" ||  containerData.containerstate == "RO"){
				genObj.scrhtml += "<span class=\"etw-5\"><button id=\"" + elementId + "_button\" class=\"ett-bttn med"
						+ appearanceClass + iconPosCls +" " + btnSemCls + disabledClass + "\" onclick=\"" + buttonClickFunction + "\" aria-disabled=\"true\">";
				}else{
					genObj.scrhtml += "<span class=\"etw-5\"><button id=\"" + elementId + "_button\" class=\"ett-bttn med"
							+ appearanceClass + iconPosCls +" " + btnSemCls + disabledClass + "\" onclick=\"" + buttonClickFunction + "\">";						
				}
				placeHolder = !this.apz.isNull(elementData.placeholder) ? elementData.placeholder : "";
				var btnTitle = !this.apz.isNull(elementData.buttontitle) ? elementData.buttontitle : "";
				genObj.scrhtml += genUtils.getButtonIcon(param, elementData, btnTitle, elementId);
				genObj.scrhtml += "</button></span>";
				genObj.scrhtml += "<span><input id=\"" + elementId + "\" " + localRowNo + nativeDateAttr + elementClasses
						+ toolTip + " style=\"" + minMaxWidthCls + "\" " + "";
				genObj.scrhtml += " " + required + " maxlength =\"" + maxLength
						+ "\" placeholder=\"" + placeHolder + "\" " + elementState + "";
				genObj.scrhtml +=  " value=\"" + finalDefaultValue + "\" "
						+ genUtils.getElementEvents(param, elementData);
				genObj.scrhtml += "></span>";
		}
		genObj.scrhtml += "</span></span>";
	}, genLabel : function(param, props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var containerData = props.cntrData;
		var elementClasses = genUtils.getClassesForElement(param, elementData);
		var localRowNo = genUtils.getRowno(param, elementData);
		var elementId = genUtils.getElementId(param, elementData);
		var controlId = genUtils.getControlIdforWidget(param, elementData);
		var hasIcon = !this.apz.isNull(elementData.icon);
		var iconSize = elementData.iconsize;
		var iconSemanticsClass = !this.apz.isNull(elementData.iconsemantics) ? " "+elementData.iconsemantics : "";
		var iconAppearanceClass = !this.apz.isNull(elementData.iconappearance) ? " "+elementData.iconappearance : " pri";
		var toolTip = genUtils.getElementToolTip(elementData.tooltip);
        genObj.scrhtml += "<label id=\"" + elementId + "\" " + controlId + elementClasses + toolTip + localRowNo;
		genObj.scrhtml += genUtils.getElementEvents(param, elementData) + ">";
		var elementTitle = !this.apz.isNull(elementData.title) ? elementData.title : "";
		if(hasIcon) {
			if(elementData.iconposition == "RIGHT" || elementData.iconposition == "RGT") {
				genObj.scrhtml += "<svg aria-hidden=\"true\" role=\"presentation\" class=\"ett-icon "+elementData.icon + " " + iconSize + iconAppearanceClass + iconSemanticsClass +"\"><use xlink:href = \"#"+elementData.icon+"\"></use></svg>";
			} else{
				genObj.scrhtml += "<svg aria-hidden=\"true\" role=\"presentation\" class=\"ett-icon "+elementData.icon + " " + iconSize + iconAppearanceClass + iconSemanticsClass +"\"><use xlink:href = \"#"+elementData.icon+"\"></use></svg>"+elementTitle;
			}
		} else {
			genObj.scrhtml += elementTitle;
		}
		genObj.scrhtml += "</label>";
	}, genProgressBar : function(param, props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var containerData = props.cntrData;
		var elementNodeName = "";
		var elementName = "";
		var elementId = genUtils.getElementId(param, elementData);
		var elementClasses = genUtils.getClassesForElement(param, elementData);
		var elementStyleAttrs = genUtils.getElmStyleAttributes(elementData);
		var controlId = genUtils.getControlIdforWidget(param, elementData);
		var defaultValue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		var localRowNo = genUtils.getRowno(param, elementData);
		var value = "";
		var widthClass = genUtils.getWidthClass(elementData);
		var widthValText = "";
        if(this.apz.isNull(widthClass)) {
			widthClass = " etw-100";
        } else {
        	widthClass = " " + widthClass;
        }
		var stateClass = elementData == "INDETERMINATE" ? "indeterminate" : "determinate";
		var semanticsClass = this.apz.isNull(elementData.semantics) ? "" : " "+elementData.semantics;
		elementName = elementData.elementname;
		if (elementData.state == "INDETERMINATE") {
			value = "";
		} else {
			value = " value=\"" + defaultValue + "\"";
		}
		var toolTip = genUtils.getElementToolTip(elementData.tooltip);
		genObj.scrhtml += "<div id=\"" + elementId + "_div_wrapper\" class = \"ecn" + widthClass + "\" style=\"" + elementStyleAttrs + "\"><div id=\"" + elementId + "_div\"" + controlId + elementClasses + toolTip + localRowNo
						+ " " + "" + "" + value + ""
						+ genUtils.getElementEvents(param, elementData) + ">";
		if (elementData.progresstype == "OSSPECIFIC") {
			genObj.scrhtml += "<progress id=\"" + elementId + "\" apztype = \"progress\" class=\"\" " + value + "></progress></div></div>";
		} else {
			var widthVal = 0;
			if(!this.apz.isNull(defaultValue)) {
				widthVal = parseFloat(defaultValue) * 1000;
				/** Added By Harish.Intentionally Done to avoid Floating point precision Error in Java**/
				widthVal = widthVal/10;
			}
			if(!elementData.state == "INDETERMINATE") {
				widthValText = widthVal+"%";
			}
			genObj.scrhtml += "<div id=\"" + elementId + "\" class=\"" + stateClass + semanticsClass + "\" style=\"width:" + widthVal + "%\" apztype = \"progress\">"+widthValText+"</div></div></div>";
		}
	}, genSlider : function(param, props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var containerData = props.cntrData;
		var elementId = "";
		var controlId = genUtils.getControlIdforWidget(param, elementData);
		var elementState = "";
		var defaultValue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		elementState = genUtils.getElementState(elementData);
		var elementStyleAttrs = genUtils.getElmStyleAttributes(elementData);
		var toolTip = "";
		var elementClasses = genUtils.getClassesForElement(param, elementData);
		if (containerData.containerstate == "READONLY") {
			elementState = " disabled=\"disabled\"";
		}
		var localRowNo = genUtils.getRowno(param, elementData);
		elementId = genUtils.getElementId(param, elementData);
		toolTip = genUtils.getElementToolTip(elementData.tooltip);
		genObj.scrhtml += "<input id=\"" + elementId + "\" " + toolTip + localRowNo + " type=\"range\"" + controlId;
		genObj.scrhtml += " " + genUtils.getElementEvents(param, elementData);
		var max = !this.apz.isNull(elementData.max) ? elementData.max : "";
		var min = !this.apz.isNull(elementData.min) ? elementData.min : "";
		genObj.scrhtml += elementClasses + "style=\"" + elementStyleAttrs + "\"" + elementState + " value=\""
						+ defaultValue + "\" max=\"" + max + "\" min=\"" + min + "\" name=\""
						+ elementData.name + "\" >";
	}, genStepper : function(param, props) {
		var genObj = param.genObj;
		var genUtils = param.genUtils;
		var elementData = props;
		var containerData = props.cntrData;
		var elementNodeName = "";
		var elementName = "";
		var step = "";
		var elementState = genUtils.getElementState(elementData);
		var nonWrapCls = genUtils.getClassesForNonWrappedElms(param, elementData);
		var placeholder = !this.apz.isNull(elementData.placeholder) ? elementData.placeholder : "";
		var elementId = genUtils.getElementId(param, elementData);
		var elementClasses = genUtils.getClassesForElement(param, elementData);
		var appearanceClass = !this.apz.isNull(elementData.appearance) ? " " + elementData.appearance.toLowerCase() : " pri";
		var controlId = genUtils.getControlIdforWidget(param, elementData);
		var elementStyleAttrs = genUtils.getElmStyleAttributes(elementData);
		var defaultValue = !this.apz.isNull(elementData.defaultvalue) ? elementData.defaultvalue : "";
		var localRowNo = genUtils.getRowno(param, elementData);
		elementName = elementData.elementname;
		var leftIcon = "icon-minus";
		var rightIcon = "icon-plus";
		var max = !this.apz.isNull(elementData.maxvalue) ? elementData.maxvalue : "";
		var min = !this.apz.isNull(elementData.minvalue) ? elementData.minvalue : "";
		if (!this.apz.isNull(elementData.lefticon)) {
			leftIcon = elementData.lefticon;
		}
		if (!this.apz.isNull(elementData.righticon)) {
			rightIcon = elementData.righticon;
		}
		step = !this.apz.isNull(elementData.step) ? " step=\"" + elementData.step + "\"" : " step=\"\"";
		var vartnClass = !this.apz.isNull(elementData.variation) ? " " + elementData.variation : "";
		var disabledClass = "";
		if (elementData.state == "DISABLED") {
			disabledClass = " disabled";
		}
		if (containerData.containerstate == "READONLY") {
			elementState = " disabled=\"disabled\"";
			disabledClass = " disabled";
		}
		genObj.scrhtml += "<ul id=\"" + elementId + "_stepper\" " + controlId
				+ " class=\"ett-stpr etb-stpr " + nonWrapCls  + appearanceClass + vartnClass + "\">";
		genObj.scrhtml += "<li>";
		genObj.scrhtml += "<button id=\"" + elementId + "_button_left\" class=\"ett-bttn med tsp "
						+ disabledClass + "" + "\" onclick=\"apz.handleStepperclick(this);\" " + elementState
						+ " aria-label=\"Minus\">";
		genObj.scrhtml += "<svg aria-hidden=\"true\" class=\"ett-icon " + leftIcon + " px24\"><use xlink:href=\"#"
				+ leftIcon + "\"></use></svg></button></li>";
		var toolTip = genUtils.getElementToolTip(elementData.tooltip);
		genObj.scrhtml +=  "<li><input id=\"" + elementId + "\" " + localRowNo + elementClasses + toolTip + " style=\"" + elementStyleAttrs + "\" " + " apztype=\"stepper\" type=\"text\" max=\"" + max + "\" min=\"" + min + "\" placeholder=\"" + placeholder + "\" " + "value=\"" + defaultValue + "\" " + elementState + step + "" + genUtils.getElementEvents(param, elementData) + "" + ">";
		genObj.scrhtml +=  "</li><li>";
		genObj.scrhtml +=  "<button id=\"" + elementId + "_button_right\" class=\"ett-bttn med stpr-add tsp"
						+ disabledClass + "\" " + elementState + " onclick=\"apz.handleStepperclick(this);\"aria-label = \"Plus\">";
		genObj.scrhtml +=  "<svg aria-hidden=\"true\" class=\"ett-icon " + rightIcon + " px24\"><use xlink:href=\"#"
				+ rightIcon + "\"></use></svg></button></li>";
		genObj.scrhtml +=  "</ul>";
	}
}
