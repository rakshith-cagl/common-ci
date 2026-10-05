Apz.prototype.dropdownCheckbox = function(obj) {
  //To be overridden
  var drdnDivObj = $(obj).parents("#"+obj.id+"_ext:first");
	var apzObj = this;
	drdnDivObj.off('click.apzevent').on( 'click.apzevent', function() {
		if (!$(this).attr('disabled')) {
			apzObj.dropdownToggle(obj);
		}
	});
	$(obj).off('keydown.apzevent').on('keydown.apzevent', function(e) {
    if (!$(this).attr('disabled') && (e.which=="13" || e.which == "32")) {
      if(e.keyCode == 32){
        e.preventDefault();
      }
      apzObj.dropdownToggle(obj);
    }
  });
  var val = "";
  var setDropdownVal = function(curObj,event) { 
    event.preventDefault();
    if(event.keyCode == 13 || event.type == "change") {
      if (event.keyCode == 13) {
        curObj = $(curObj).find('input');
        if ($(curObj).is(':checked')){
          $(curObj).prop('checked', false);
        } else {
          $(curObj).prop('checked', true);
        }
      }
      var txtbox = $(obj);
      var allVals = [];
      $(curObj).closest('.sub-ctr').find('li.is-selected').removeClass('is-selected');
      $(curObj).closest('.sub-ctr').find(':checked').each(function() {
        //$(this).parent('li').addClass('is-selected');
        allVals.push($(this).next().text());
      });
      var allValsString = allVals.join(', ');
      txtbox.val(allValsString);
      /*if ($(obj).attr("onchange")) {
        $(obj).trigger("change");
      }*/
      event.stopPropagation();
    } else {
      apzObj.dropdownKeyAction(obj,curObj,event);
    }
  }
  drdnDivObj.find('li input').off('change.apzevent').on('change.apzevent', function(e) {
    setDropdownVal(this,e);
  });
  var searchDrdn = function(curObj,event){
    var value = val;
    var proceed = true;
    var totalOpts = $(curObj).parent().children('li').length;
    var index = 0;
    var getNextOpt = function(actObj,currOpt,nextOptObj,curValue) {
      nextOpt = $(nextOptObj).next('li');
      if (nextOptObj.text().toUpperCase().indexOf(curValue) != -1 && $(nextOptObj).text().toUpperCase().indexOf(curValue) == '0') {
        $(nextOptObj).parent().find('.hilt').removeClass('hilt');
        nextOptObj.addClass('hilt').focus();
        proceed = false;
      } else if ($(nextOptObj).next('li').length == 0 && index<totalOpts) {
        nextOpt = $(nextOptObj).parent().children('li:first');
        index = index + 1;
        getNextOpt(actObj,nextOptObj,nextOpt,curValue);
      } else {
        if (index<totalOpts) {
          index = index + 1;
          getNextOpt(actObj,nextOptObj,nextOpt,curValue);
        }
      }
    }
    if (proceed) {
      var nextOpt = $(curObj).next('li');
      if (nextOpt.length == 0) {
        nextOpt = $(curObj).parent().children('li:first');
      }
      getNextOpt(obj,curObj,nextOpt,value);
    }
  }
  var clear = false;
  drdnDivObj.find('li:not(.is-disabled)').off('keydown.apzevent').on( 'keydown.apzevent', function(e) {
    if (/[a-zA-Z0-9]/.test(String.fromCharCode(e.keyCode))) {
      if (!(val.length == '1' && val == String.fromCharCode(e.keyCode))) {
        val = val + String.fromCharCode(e.keyCode);
      }
      clearTimeout($.data(obj, 'timer'));
      var wait = setTimeout(function() {
        clear = true;
        val = "";
      }, 500);
      $(obj).data('timer', wait);
//      console.log($(this).children().text() + "   " + val);
      if ($(this).children().text().toUpperCase().indexOf(val)!=0 || clear) {
        clear = false;
        searchDrdn(this,e);
      }
    } else {
      setDropdownVal(this,e);
    }
  });
}
Apz.prototype.setObjValue = function(obj, value) {
     /* Params contains the below values
         *** obj(DOM element),value ***
     */
      var tagName = obj.tagName;
      var elmData = "";
      if (this.isNull(value)) {
         value = "";
      }
      var $obj = $(obj);
      var objRowNo = this.getObjRowNumber(obj);
      if (objRowNo!=-1) {
         var objRowId = this.getObjIdWORowNumber(obj);
         elmData = this.scrMetaData.elmsMap[objRowId];
      } else {
         elmData = this.scrMetaData.elmsMap[obj.id];
      }
      var idType = $(obj).attr('apztype');
      if(!this.isNull(elmData) && elmData.custom == "Y"){
         var custObj = apz[elmData.type];
         if(custObj && apz.isFunction(custObj.setObjValue)){
            custObj.setObjValue(obj, value);
         }
      } else if (tagName == "INPUT" || (tagName == "SPAN" && !this.isNull(elmData) && elmData.type == "DROPDOWN")) {
         var id = obj.id;
         var type = $('#' + id).attr('type');
         if (type == "hidden") {
            if (obj.classList.contains("appzillon_date")) {
               //$('#' + id)[0].nextElementSibling.innerHTML = value;
               obj.nextElementSibling.textContent = value;
            }
         } else if (type == "CHECKBOX") {
            /*if (idType == 'toggleswitch') {
               var selectedVal = $('#' + id).attr('selectedval');
               var unselectedVal = $('#' + id).attr('unselectedval');
               if (value == selectedVal) {
                  obj.value = selectedVal;
                  $('#' + id).prop('checked', true);
               } else {
                  obj.value = unselectedVal;
                  $('#' + id).prop('checked', false);
               }
            } else {*/
                var previousVal = this.getObjValue(obj);
               var checkedVal = $obj.attr('checkedval');
               var uncheckedVal = $obj.attr('uncheckedval');
               var indeterminateVal = $obj.attr('indeterminateval');
               obj.indeterminate = false;
               if (value == checkedVal) {
                  obj.value = checkedVal;
                  $obj.prop('checked', true);
               } else if (value == indeterminateVal) {
                  obj.value = indeterminateVal;
                  obj.indeterminate = true;
               } else {
                  obj.value = uncheckedVal;
                  $obj.prop('checked', false);
               }
               //Add to override
                if (!this.isNull(value) && previousVal != value) {
                    $obj.trigger("change");
                }
            //}
         } else if (!this.isNull(elmData) && elmData.type == "DROPDOWN") {
            //Pradeep changes taken from 3.5.13 source
             var previousVal = this.getObjValue(obj);
             var dropDownDiv = document.getElementById(obj.id+"_div");
             var opts = $(dropDownDiv).find('ul li');
             var noOfElem = $(dropDownDiv).find('ul li').length;
             var isWithCheckbox = false;
             var isWithSubOpt = false;
             if(tagName != "SPAN"){
                if($(opts[0]).find('label').length > 0) {
                   isWithCheckbox = true;
                } else if($(opts[0]).find('span').length > 0) {
                   isWithSubOpt = true;
                }
             }
             if($(obj).parent().hasClass("is-multi-tiered")) {
                var indx ;
                var resArr = [];
                var valArr = value.split(",");
                var labelOpts = $(dropDownDiv).find('label');
                var chkOpts = $(dropDownDiv).find('input');
                for(i=0;i<opts.length;i++) {
                   indx = valArr.indexOf(opts[i].getAttribute("data-value"));
                   if(indx >-1) {
                      //resArr.push(labelOpts[i].innerHTML);
                      resArr.push(labelOpts[i].textContent);
                      $(chkOpts[i]).prop("checked",true);
                   } else {
                     $(chkOpts[i]).prop("checked",false);
                   }
                }
                resArr = resArr.toString();
                $(obj).val(resArr);
             } else {
                if(isWithSubOpt) {
                   for (var i = 0; i < noOfElem; i++) {
                       if (opts[i].getAttribute("data-value") == value) {
                      obj.value = $(opts[i]).text();
                      $(opts[i]).addClass('is-selected');
                      } else {
                        $(opts[i]).removeClass('is-selected');
                      }
                   }
                } else {
                   for (var i = 0; i < noOfElem; i++) {
                      if (opts[i].getAttribute("data-value") == value) {
                        if(tagName != "SPAN")
                         //obj.value = opts[i].innerHTML;
                         obj.value = opts[i].textContent;
                        else
                           obj.innerHTML = opts[i].innerHTML;
                           $(opts[i]).addClass('is-selected');
                     } else {
                         $(opts[i]).removeClass('is-selected');
                     }
                  }
                  if ($(opts[0]).parent().find(".is-selected").length == 0) {
                     if(tagName != "SPAN")
                        obj.value = "";
                     else
                        obj.innerHTML = "";
                  }
               }
            }
            if (!this.isNull(obj.getAttribute("onChange")) && !this.isNull(value) && previousVal != value) {
               $("#" + obj.id).trigger("change");
            }
          } else if (!this.isNull(elmData) && elmData.type == "DROPDOWNWITHINPUT") {
            var opts = $(obj).siblings('div').find('ul li');
            var noOfElem = $(obj).siblings('div').find('ul li').length;
            var inputid = $(obj).parents('li:first').siblings('li').find('input')[0].id;
            for (var i = 0; i < noOfElem; i++) {
               if (opts[i].getAttribute("value") == value) {
                  //obj.value = opts[i].innerHTML;
                  var inputBoxId = $(obj).attr("id");
                  //$("#"+inputBoxId+"_input")[0].value = opts[i].innerHTML;
                  //$("#"+inputid).val(opts[i].innerHTML);
                  $(inputBoxObj).val(opts[i].textContent);
                  $(opts[i]).addClass('is-selected');
               } else {
                 $(opts[i]).removeClass('is-selected');
               }
            }
            if ($(opts[0]).parent().find(".is-selected").length == 0) {
               $("#"+inputid).val('');
               //obj.value = "";
            }
         } else if(type == "text" && idType == "tags"){
            $(obj).importTags(value);
              if(!apz.isNull($(obj).attr('readonly')) || !apz.isNull($(obj).attr('disabled'))){
                $(obj).parent().find('.tagsinput').find('a').remove();
              }
         } else if(idType == "stepper") {
            var minVal = parseFloat(obj.getAttribute("min"));
            var maxVal = parseFloat(obj.getAttribute("max"));
            var minusObj = $(obj).parent().siblings('li:first').children('button');
            var plusObj = $(obj).parent().siblings('li:last').children('button');
            obj.value = value;
            if(value <= minVal) {
               minusObj.attr('disabled','disabled');
               plusObj.removeAttr('disabled');
            } else if(value >= maxVal) {
               plusObj.attr('disabled','disabled');
               minusObj.removeAttr('disabled');
            } else if(!isNaN(value)) {
               plusObj.removeAttr('disabled');
               minusObj.removeAttr('disabled');
            }
         } else if (type != "file") {
             this.changeTypeToText(obj);
             obj.value = value;
          }
      } else if (tagName == "LI" || tagName == "DIV") {
            if (idType == "toggleswitch") {
                var id0 = obj.id + '_00';
                var id1 = obj.id + '_11';
                var id0Obj = document.getElementById(id0);
                var id1Obj = document.getElementById(id1);
                /*if ($('#' + id0).val() == value) {
                    $('#' + id0)[0].checked = true;
                    $('#' + id1)[0].checked = false;
                } else {
                    $('#' + id1)[0].checked = true;
                    $('#' + id0)[0].checked = false;
                }*/
                // added to default to  the first input selection
                if ($(id1Obj).val() == value) {
                    id1Obj.checked = true;
                    id0Obj.checked = false;
                } else {
                    id0Obj.checked = true;
                    id1Obj.checked = false;
                }
            } else if (idType == "radiogroup") {
                $obj.attr('value', value);
                var lid = obj.id;
                var id = lid + '_option_' + value;
                var radObj = document.getElementById(id);
                $(radObj).attr('checked', true);
            } else if (idType == "progress") {
                value = +value;
                value = value * 100;
                $(obj).css("width", "" + value + "%");

            }
            /*else {
                          var lid = obj.id;
                          $obj.attr('value', value);
                          var id = lid + '_option_' + value;
                          $('#' + id).attr('checked', true);
                    }*/ // redundant code
        } else if (tagName == "SELECT") {
            var noOfElem = obj.options.length;
            var makeNull = true;
            var valArr = "";
            var indx;
            var tagArr = [];
            if (!this.isNull(elmData) && elmData.isArray == "Y") {
                valArr = value;
                $(obj).parent().find('.select2-selection__rendered .select2-selection__choice').remove();
                for (var i = 0; i < noOfElem; i++) {
                    indx = valArr.indexOf(obj.options[i].value);
                    if (indx > -1) {
                        makeNull = false;
                        tagArr.push($(obj.options[i]).attr('value'));
                    } else {
                        $(obj.options[i]).prop('selected', false);
                    }
                }
                if (makeNull) {
                    $(obj).val("").trigger('change')
                } else {
                    $(obj).val(tagArr).trigger('change')
                }
            } else if ($(obj).parent().hasClass("mltt")) {
                valArr = value.split(",");
                var appender;
                $(obj).parent().find('.select2-selection__rendered .select2-selection__choice').remove();
                for (var i = 0; i < noOfElem; i++) {
                    indx = valArr.indexOf(obj.options[i].value);
                    if (indx > -1) {
                        makeNull = false;
                        tagArr.push($(obj.options[i]).attr('value'));
                    } else {
                        $(obj.options[i]).prop('selected', false);
                    }
                }
                if (makeNull) {
                    $(obj).val("").trigger('change')
                } else {
                    $(obj).val(tagArr).trigger('change')
                }
            } else {
                var optVal = "";
                for (var i = 0; i < noOfElem; i++) {
                    if (obj.options[i].value == value) {
                        makeNull = false;
                        obj.selectedIndex = i;
                        if ($(obj).parent().hasClass("acpt")) {
                            optVal = $(obj.options[i]).attr('value');
                            $(obj).val(optVal).trigger('change');
                        }
                    }
                }
                if (makeNull) {
                    obj.selectedIndex = "";
                    if ($(obj).parent().hasClass("acpt")) {
                        $("#" + obj.id).val("").trigger('change')
                    }
                }
            }
            if (!this.isNull(obj.getAttribute("onChange")) && !this.isNull(value)) {
                $("#" + obj.id).trigger("change")
            }
        } else if (tagName == "TEXTAREA") {
         obj.value = value;
      } else if (tagName == "DD") {
         //obj.innerHTML = value;
         obj.textContent = value;
      } else if (tagName == "DL") {
         var ddList = $(obj).find('dd');
         if (ddList.length > 0) {
            ddList.addClass('sno');
             var dropValue = value.split(","); 
            for (var i = 0; i < ddList.length; i++) {
               if (dropValue.indexOf($(ddList[i]).attr('value')) != -1) {
                  $(ddList[i]).removeClass('sno');
                  //break;
               }
            }
         }
      } else if (tagName == "P" || tagName == "H1" || tagName == "H2" || tagName == "H3" || tagName == "H4" || tagName == "H5" || tagName == "H6") {
         var currObj = $(obj).find("#"+obj.id+"_txtcnt");
         if (currObj.length==0) {
            currObj = obj;
         }
         $(currObj).text(value);
      } else if (tagName == "A") {
         var currObj = $(obj).find("#"+obj.id+"_txtcnt");
         if (currObj.length==0) {
            currObj = obj;
         }
         $(currObj).text(value);
         if(!this.isNull(value) && $(obj).hasClass("mbsc-comp editable")){
           var format = "";
         if(elmData.dataType == "DATE")
           format = this.dateformat;
         else if(elmData.dataType == "DATETIME")
           format = this.datetimeformat;
           var functionParam = mobiscroll.util.datetime.parseDate(format, value);
           $(obj).mobiscroll("setDate", functionParam);
         }
         if($(obj).data("editable") && value != null){
            $(obj).editable("setValue",value);
            $(obj).attr("value", value);
         }
      } else if (tagName == "IMG" || (tagName == "SPAN" && obj.getAttribute("type") == "SVG")) {
         var myStatus;
         var isSVG = false;
         if((tagName == "SPAN" && obj.getAttribute("type") == "SVG")){
          isSVG = true;
         }
         if (value != null && value != "" && (value.indexOf('.') == -1)) {
          if(isSVG){
            obj = this.swipeIdsForImage(obj);
          }
            myStatus = value.indexOf("data:image/");
            if (myStatus == -1) {
               myStatus = value.indexOf("data:image/jpg;base64,");
               if (myStatus == -1) {
                  obj.src = "data:image/png;base64," + value;
               } else {
                  obj.src = value;
               }
            } else {
               obj.src = value;
            }
         }
         else if ((value.indexOf('<svg') == 0) || (value.indexOf('<?xml') == 0) || (value.indexOf('<!DOCTYPE') == 0) || (value.substr(value.length-4)) == ".svg") {
       if(!isSVG){
        obj = this.swipeIdsForImage(obj);
       }
       var $obj=$(obj);
       if((value.substr(value.length-4)) == ".svg"){
            var currTheme = this.theme;
        myStatus = value.indexOf("styles/themes/" + currTheme + "/img");
        if (!this.isNull(myStatus) && myStatus != -1) {
           var val=this.getFile(value);
           $obj.html(val);
        } else {
           var path = this.getStylesPath() + "/" + currTheme + "/img/" + value;
           var val=this.getFile(path);
           $obj.html(val);
        }
      } else {
         $obj.html(value);
      }
         }
         else {
          if(isSVG){
            obj = this.swipeIdsForImage(obj);
          }
            if (value != null && value != "") {
               var currTheme = this.theme;
               myStatus = value.indexOf("styles/themes/" + currTheme + "/img");
               if (!this.isNull(myStatus) && myStatus != -1) {
                  obj.src = value;
               } else {
                  obj.src = this.getStylesPath() + "/" + currTheme + "/img/" + value;
               }
            } else {
               obj.src = value;
            }
         }
      } else if(tagName == "svg") {
         var classes =  obj.getAttribute("class").split("icon-");
         if(classes.length > 1) {
            $(obj).removeClass("icon-"+classes[1].split(' ')[0]).addClass(value);
            $(obj).find('use').attr("xlink:href","#" + value);
         } else {
            $(obj).addClass(value);
            $(obj).find('use').attr("xlink:href","#" + value);
         }
      } else if (tagName == "PROGRESS") {
         obj.value = value;
      } else if (tagName == "SPAN") {
            if (idType == "radiogroup") {
                var previousVal = this.getObjValue(obj);
                var lid = obj.id;
                $obj.attr('value', value);
                var id = lid + '_option_' + value;
                var radObj = document.getElementById(id);
                $(radObj).attr('checked', true);
                if (!(this.isNull(radObj))) {
                    if (!this.isNull(radObj.getAttribute("onChange")) && previousVal != value) {
                        $(radObj).trigger("change");
                    }
                } else if (this.isNull(value) && (previousVal != value)) {
                    var radioInps = $obj.find("input");
                    radioInps.prop("checked", false);
                    $(radioInps[0]).trigger("change");
                }
            } else {
                //obj.innerHTML = value;
                obj.textContent = value;
            }
        } else if(tagName == "BUTTON") {
         if ($(obj).hasClass("with-dropddown")) {
            var opts = $(obj).siblings('div').find('ul li');
            var noOfElem = $(obj).siblings('div').find('ul li').length;
            for (var i = 0; i < noOfElem; i++) {
               if (opts[i].getAttribute("value") == value) {
                  value = opts[i].innerHTML;
               }
            }
            $(obj).html(value);
         } else {
            var currObj = $(obj).find("#"+obj.id+"_txtcnt");
            if (currObj.length==0) {
               currObj = obj;
            }
            $(currObj).text(value);
         }
      } else if (!this.isNull(elmData) && elmData.type == "SORTCODEACCOUNT") {
         var codes = value.split("-");
         for (var i = 0; i < 4; i++) {
            if(codes[i]){
               $("#"+obj.id+"_"+(i+1)).val(codes[i]);
            }
         }
      }
   }
   
Apz.prototype.initPopover = function(params) {
    /// Expects targetId, position, trigger
    var apzObj = this;
    var popoverCls = $('#'+params.targetId).attr("class");
    var obj = $("#"+params.targetId)[0];
    var triggerEvent = "click";
    if (!this.isNull(params.trigger)) {
      triggerEvent = params.trigger;
    }
    var position = params.position.toLowerCase();
        $("#"+params.elmId).popover({
        html: true,
        placement:position,
        content: obj,
        trigger: triggerEvent
    });
    $("#"+params.elmId).on("show.bs.popover", function(e){ 
      var popOverObj = $(this).data("bs.popover").tip();
      popOverObj.addClass(popoverCls);
      popOverObj.removeClass("sno");
      $("#" + params.targetId).removeClass();
      apzObj.initDataTables($('#'+params.targetId+' .responsive:visible'));
      apzObj.initFixedHeaderTables($('#'+params.targetId+' [id].fixedheader:visible'));
      if(apzObj.isFunction(apzObj.app.onPopoverOpen)){
        apzObj.app.onPopoverOpen(this,params.targetId,e);
      }
    });
    $("#"+params.elmId).on("hide.bs.popover", function(e){
      var popOverObj = $(this).data("bs.popover").tip();
      if(apzObj.isFunction(apzObj.app.onPopoverClose)){
        apzObj.app.onPopoverClose(this,params.targetId,e);
      }
    });
  }
Apz.Val.prototype.validateControl = function(params) {
      ///Expects id, message and/or code
      var objId = params.id;
      var pinput = $("#"+objId)[0];
      var errMsg = this.validateInputAct(pinput, false);
      params.errMsg = errMsg;
      if (!this.apz.isNull(errMsg)) {
        this.showErrorMsg(params);
      } else {
        this.removeErrors(params.id);
      }
   }
Apz.Val.prototype.showErrorMsg = function(params) {
      /// Expects id, message or code
      var objId = params.id;
      var pinput = $("#"+objId)[0];
      $input = $(pinput);
      var lid = this.apz.getObjIdWORowNumber(pinput);
      var elmObj = this.apz.scrMetaData.elmsMap[lid];
      var $inputParent = $input.parent();
      if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN" && pinput.tagName=="INPUT") {
        $inputParent = $input.parent().parent();
      }
      var errMsg = params.errMsg;
      var addErrText = function(errText) {
        if($input.closest("ul").hasClass("hrow")){
          $input.closest("li").addClass("vcn");
        } else {
          $input.parents('.srb:first').addClass("vcn");
        }
        $input.addClass("err");
        $inputParent.children(".vtx").remove();
        $inputParent.append('<p class="vtx">' + errText + '</p>');
      }
      if (!apz.isNull(errMsg) || !apz.isNull(params.message) || !apz.isNull(params.code)) {
        if(!apz.isNull(params.message)){
          desc = params.message;
        } else if (!apz.isNull(params.code)){
          desc = apz.msgs[this.apz.currAppId][params.code];
          desc = desc.substring(1);
        } else {
          errMsg = apz.msgs[this.apz.currAppId][errMsg];
          desc = errMsg.substring(1);
        }
        addErrText(desc);
      }
    }
Apz.Val.prototype.removeErrors = function(id) {
      var clearErr = function(obj) {
        $input = $(obj);
        var lid = this.apz.getObjIdWORowNumber(obj);
        var elmObj = this.apz.scrMetaData.elmsMap[lid];
        var $inputParent = $input.parent();
        if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN" && obj.tagName=="INPUT") {
          $inputParent = $input.parent().parent();
        }
        if ($input.closest("ul").hasClass("hrow")) {
            $input.closest("li").removeClass("vcn");
        } else {
            $input.parents('.srb:first').removeClass("vcn");
        }
        if ($inputParent[0].lastChild.tagName == "P") {
            $inputParent[0].removeChild($inputParent[0].lastChild);
        }
        $input.removeClass("err");
      }
      clearErr(document.getElementById(id));
      $("#"+id +" .err").each(function() {
        clearErr(this);
      });
    }
Apz.prototype.getObjectOrArray = function(){
  return {};
}