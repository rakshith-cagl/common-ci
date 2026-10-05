/**
 * Validations 
 * @namespace 
 */
Apz.Val = function(apz) {
   ////Core Instance
   this.apz = apz;
   this.gblockdata = [];
};
Apz.Val.prototype = {
   /**
    * This API validates the elements on the screen and returns true/false.
    * @param {string} scr The name of the screen which needs to be validated.
    * @example
    * apz.val.validateScreen("Login");
    */
   validateScreen : function(scr) {
	   /* Params contains the below value
	       *** scrName ***
	   */
      var lresult = true;
      var lerror = "";
      var lerrdet = {};
      var lscrdiv = "scr__" + this.apz.currAppId + '__' + scr + "__main";
      var lerrors = new Array();
      var myObj = this;
      var elmsValue = this.apz.scrMetaData.elms;
      $("#" + lscrdiv + " input").each(function() {
         if (this.type == "radio") {
         } else if (this.classList.contains('pagination-input')) {
         } else if (this.type == "checkbox") {
         } else if ($(this).closest("tr").hasClass("ssp")) {
         } else {
            lerror = myObj.validateInputAct(this, false);
            if (lerror != false) {
               lerrdet = {}, lerrdet.error = lerror;
               lerrdet.element = this;
               lerrors[lerrors.length] = lerrdet;
            }
            var lid = this.getAttribute("id");
            if (lerrors.length > 0) {
               lresult = false;
            }
         }
      });
      $("#" + lscrdiv + " select").each(function() {
         if ($(this).closest("tr").hasClass("ssp")) {
         } else {
            lerror = myObj.validateInputAct(this, false);
            if (lerror != false) {
               lerrdet = {}, lerrdet.error = lerror;
               lerrdet.element = this;
               lerrors[lerrors.length] = lerrdet;
            }
            var lid = this.getAttribute("id");
            if (lerrors.length > 0) {
               lresult = false;
            }
         }
      });
 for (var i = 0; i < elmsValue.length; i++) {
        if (elmsValue[i].custom != "N") {
          var custObj = this.apz[elmsValue[i].type];
          var custDomObject = document.getElementById(elmsValue[i].id);
          if (!this.apz.isNull(custDomObject) && $(custDomObject).is(":visible") && $(custDomObject).css('display') !== 'none' && !$(custDomObject).closest("tr").hasClass("showspace")) {
            if (custObj && this.apz.isFunction(custObj.validateObj)) {
              lerror = custObj.validateObj(custDomObject, elmsValue[i]);
              if (!this.apz.isNull(lerror) && lerror != false) {
                 lerrdet = {}, lerrdet.error = lerror;
                 lerrdet.element = custDomObject;
                 lerrors[lerrors.length] = lerrdet;
              }
            }
          } else {
            var customErrors = [];
            var containerName = elmsValue[i].container;
            var containerData = myObj.apz.scrMetaData.containersMap[containerName];
            if (containerData.multiRec == "Y") {
              var obj = {
                "totalRecs": containerData.totalRecs,
                "pageSize": containerData.pageSize,
                "currentPage": containerData.currPage,
                "totalPages": containerData.totalPages
              };
              if (obj.pageSize == 999) {
                customErrors = this.validateCustomElements(obj.totalRecs, elmsValue[i], custObj);
              } else {
                  if (obj.currentPage > 0 && obj.currentPage == obj.totalPages) {
                    var fixedPageSize = obj.totalRecs % obj.pageSize;
                    fixedPageSize = (fixedPageSize==0)?1:fixedPageSize;
                    customErrors = this.validateCustomElements(fixedPageSize, elmsValue[i], custObj);
                  } else {
                    customErrors = this.validateCustomElements(obj.totalRecs, elmsValue[i], custObj);
                  }
                }
              }
              lerrors.concat(customErrors);
            }
          }
        }
        if (lerrors.length > 0) {
           lresult = false;
        }
      return lresult;
   },validateCustomElements : function(totalRecs,element,custObj){
      var lresult = true, lerror = "";
      var custDomObject, lerrdet = {}, lerrors = [];
      for (var i = 0; i < totalRecs; i++) {
        custDomObject = document.getElementById(element.id + "_" + i);
        if (!this.apz.isNull(custDomObject) && $(custDomObject).is(":visible") && $(custDomObject).css('display') !== 'none' && !$(custDomObject).closest("tr").hasClass("showspace")) {
          if (custObj && this.apz.isFunction(custObj.validateObj)) {
            lerror = custObj.validateObj(custDomObject, element);
            if (!this.apz.isNull(lerror) && lerror != false) {
               lerrdet = {}, lerrdet.error = lerror;
               lerrdet.element = custDomObject;
               lerrors[lerrors.length] = lerrdet;
            }
          }
        }
      }
      return lerrors;
   },
   /**
    * This API used for validating given control and displays the provided error message.
    * @param {object} params params includes following
    * <br>id: This attribute is of element ID to be validated.</br>
    * <br>message : This attribute is of the message to be displayed(optional).</br>
    * <br>code : This attribute is of message code to be displayed in case of error (optional).</br>
    * @example
    * var params = {};
    * params.id = “element_input_1”;
    * params.message = “This is not a valid Date”;
    * apz.val.validateControl(params);
    */
   validateControl: function(params) {
        //overridden since its a cosmetic change//
       ///Expects id, message and/or code
       var objId = params.id;
         var pinput = $("#"+objId)[0];
         var errMsg = this.validateInputAct(pinput, false);
        $input = $(pinput);
        var lid = this.apz.getObjIdWORowNumber(pinput);
         var elmObj = this.apz.scrMetaData.elmsMap[lid];
        var $inputParent = $input.parent();
        if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN" && pinput.tagName=="INPUT") {
         $inputParent = $input.parent().parent();
        }
         if (!apz.isNull(errMsg)) {
            if(!apz.isNull(params.message)){
               desc = params.message;
            } else if (!apz.isNull(params.code)){
               desc = apz.msgs[this.apz.currAppId][params.code];
               desc = desc.substring(1);
            } else {
               errMsg = apz.msgs[this.apz.currAppId][errMsg];
               desc = errMsg.substring(1);
            }
            if($input.closest("ul").hasClass("hrow")){
              $input.closest("li").addClass("vcn");
            } else {
              $input.parents('.srb').addClass("vcn");
            }
            $input.addClass("err");
            $inputParent.children(".vtx").remove();
            $inputParent.append('<p class="vtx">' + desc + '</p>');
         } else {
           if($input.closest("ul").hasClass("hrow")){
              $input.closest("li").removeClass("vcn");
           } else {
              $input.parents('.srb').removeClass("vcn");
           }
          if($inputParent[0].lastChild.tagName == "P"){
            $inputParent[0].removeChild($inputParent[0].lastChild);
          }
        }
   }, 
   /**
    * This API will be used for validating the given container.
    * @param {string} contId This filed contains the container ID to be validated.
    * @example
    * apz.val.validateContainer(“test__Screen__container_1”);
    */
   // with Bug 54095 fix changes_AB
   validateContainer :function(contId) {
      /* Params contains the below value
          *** contId ***
      */
     var isHidden = false;
     var lresult = true;
     var lerror = "";
     var lerrdet = {};
     var lerrors = new Array();
     var myObj = this;
     $("#" + contId + " input").each(function() {
         // Implemented mandatory field validation for radio buttons
        if (this.type == "radio") {
            // added code_AB
            lerror = myObj.validateInputAct(this, false);
           if (lerror != false) {
              lerrdet = {}, lerrdet.error = lerror;
              lerrdet.element = this;
              // pushing the error into the lerrors array
              lerrors[lerrors.length] = lerrdet;
           }
           var lid = this.getAttribute("id");
           // in case of radio button the entire radio button form acts as an entire element(containing 2 radio buttons), 
           // so it will iterate two times to validate two radio buttons for the single radio button form element
           // now even if you select any of the radio buttons it will find an error for the next button which is not selected
           // for that reason checking the lerrors lenth is greater than 1 or not rather that checking with 0
           if (lerrors.length > 1) {
              lresult = false;
           }
  
        } else if (this.classList.contains('pagination-input')) {
        } else if (this.type == "checkbox") {
        } else if ($(this).closest("tr").hasClass("ssp")) {
        } else {
            // added code_AB
            // after implementing the radio button validation, even you have selected one of the buttons, 
            //the lerrors array will have one error for the next button which is not selected,
            // hence considering a new array for lerrors
            lerrors = [];
  
           lerror = myObj.validateInputAct(this, false);
           if (lerror != false) {
              lerrdet = {}, lerrdet.error = lerror;
              lerrdet.element = this;
              lerrors[lerrors.length] = lerrdet;
           }
           var lid = this.getAttribute("id");
  
           // added code_AB
           // Handling mandatory but hidden elements
           checkHiddenElement(lid);
           //added code_AB
           // if hidden element then skip
           if (lerrors.length > 0 && !isHidden) {
              lresult = false;
           }
        }
     });
     $("#" + contId + " select").each(function() {
        if ($(this).closest("tr").hasClass("ssp")) {
        } else {
           lerror = myObj.validateInputAct(this, false);
           if (lerror != false) {
              lerrdet = {}, lerrdet.error = lerror;
              lerrdet.element = this;
              lerrors[lerrors.length] = lerrdet;
           }
           var lid = this.getAttribute("id");
           if (lerrors.length > 0) {
              lresult = false;
           }
        }
     });
  
     //added code_AB
     // handling mandatory validation for textarea
     $("#" + contId + " textarea").each(function() {
         lerror = myObj.validateInputAct(this, false);
       if (lerror != false) {
          lerrdet = {}, lerrdet.error = lerror;
          lerrdet.element = this;
          lerrors[lerrors.length] = lerrdet;
       }
       var lid = this.getAttribute("id");
  
       // added code_AB
       // Handling mandatory but hidden elements
       checkHiddenElement(lid);
       //added code_AB
       // if hidden element then skip
       if (lerrors.length > 0 && !isHidden) {
          lresult = false;
       }
     })
  
     // to check the mandatory but hidden elements
     function checkHiddenElement(lid) {
          if(lid) {
              if ($('#'+apz.scrMetaData.elmsMap[lid].container) && $('#'+apz.scrMetaData.elmsMap[lid].container).length > 0) {
                  if ($('#'+apz.scrMetaData.elmsMap[lid].container)[0].classList.length > 0) {
                      $('#'+apz.scrMetaData.elmsMap[lid].container)[0].classList.forEach(function(el){
                          if(el === 'sno') {
                              isHidden = true;
                          }
                      })
                  }
              }
          }
      }
  
     return lresult;
  }, 
   /**
    * This API will be used for validating the element on the screen. On validation failure control will be highlighted with red background.
    * @param {object} pinput The object that needs to be validated.
    * @example
    * apz.val.validateInput(document.getElementById("ElementId"));
    * apz.val.validateInput(obj);
    */
   validateInput : function(pinput) {
      return this.validateInputAct(pinput, true);
   }, 
   /**
    * This API will be used for validating element on the screen. The validation rules are provided at the interface level. 
    * @param {object} pinput The object that needs to be validated.
    * @param {boolean} ponline This will have "true" or "false".If set to true an alert will pop up in case of invalid object else if set to false alert will not pop up.
    * @example
    * apz.val.validateInputAct(document.getElementById("ElementId"), true);
    * 
    */
   // with Bug 54095 fix changes_AB
   validateInputAct : function(pinput, ponline) {
      var lresult = true;
      var lerror = "";
      if (!this.apz.isNull(pinput) && $(pinput).is(":visible") && !$(pinput).closest("tr").hasClass("ssp")) {
         //Initializing the values type and mandatory flag
         var lid = this.apz.getObjIdWORowNumber(pinput);
         var elmObj = this.apz.scrMetaData.elmsMap[lid];
   
         // added code_AB
         // the elmObj detail is not available at the radio button element itself,
         // taking the elmObj detail from the radio button form element which is the parent of parent of the radio buttons
         if (pinput.type === 'radio') {
             elmObj = this.apz.scrMetaData.elmsMap[$('#' + lid).parent().parent()[0].id];
         }
   
         if(elmObj !== undefined){ 
         var ldtype = "STRING";
         var lmandatory = "N";
         var isCustom = "N";
         var isEmail = "N";
         var isCamelCase = false;
         if (!this.apz.isNull(elmObj)) {
            ldtype = elmObj.dataType;
            lmandatory = elmObj.mand;
            isCustom = elmObj.custom;
            isEmail = elmObj.email;
            if(elmObj.textdecoration && elmObj.textdecoration == "C") {
              isCamelCase = true;
            }
         }
         if(isCustom == "Y"){
            var custObj = this.apz[elmObj.type];
            if(custObj && this.apz.isFunction(custObj.validateObj)){
              lerror = custObj.validateObj(pinput,elmObj);
            }
         } else {
           var inputVal = pinput.value;
   
           // added code_AB
           // setting the pinput value by checking with the checked property of the radio button element
           if(pinput.type === 'radio') {
               inputVal = $('#'+pinput.id).prop("checked")? pinput.value : "";
           }
   
           if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN") {
              inputVal = this.apz.getObjValue(pinput);
           }
           if (!this.apz.isNull(inputVal) && !this.apz.isNull(elmObj)) {
              if ((ldtype == "NUMBER" || ldtype == "INTEGER")) {
                 lerror = this.validateNumberObj(pinput,elmObj.displayAsLiteral);
              } else if (ldtype == "DATE") {
                 lerror = this.validateDateObj(pinput);
              } else if (ldtype == "DATETIME") {
                 lerror = this.validateDateTimeObj(pinput);
              } else if(isEmail == "Y") {
                  lerror = this.validateEmailObj(pinput);
              } else if(isCamelCase) {
                lerror = this.validateCamelCase(pinput);
              } else {
                 lerror = this.validateStringObj(pinput);
              }
           } else {
              if (lmandatory == "Y") {
                 lresult = false;
                 lerror = "APZ-VAL-001";
              }
           }
         }
         // //Result Handling....
         if (lerror != "") {
            if (!$(pinput).parent()[0].classList.contains("ssp")) {
               this.addClass(pinput);
               if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN") {
                  $(pinput).parent('div').addClass('err');
               }
               if (ponline == true) {
                  var params = {"code":lerror}
                  this.apz.dispMsg(params);
               }
            }
   
            //added code_AB
            // in case of any error for the textarea, remove the pri class and attach the err class
            if (pinput.type === 'textarea' && $(pinput)[0].classList.contains('ett-texa')) {
                if ($(pinput)[0].classList.contains('pri')) {
                    $(pinput).removeClass('pri');
                    $(pinput).addClass('err')
                }
            }
         } else {
            this.removeClass(pinput);
            if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN") {
               $(pinput).parent('div').removeClass('err');
             // added code_AB
             // in case of no error for textarea, remove the err class and attach the pri class
            } else if (!this.apz.isNull(elmObj) && elmObj.type=="TEXTAREA") {
                $(pinput).removeClass('err');
                $(pinput).addClass('pri');
            }
         }
        }
      }
      return lerror;
   }, addClass : function(pobj) {
      var ltagname = pobj.tagName;
      if (ltagname == "INPUT") {
         var lid = pobj.id;
         var ltype = $('#' + lid).attr('type');
         if (ltype == "hidden") {
            if (pobj.classList.contains("appzillon_date")) {
               var lclasslist = document.getElementById(lid).nextSibling.nextElementSibling;
               lclasslist.className = lclasslist.className + "  err  ";
            }
         } else if (ltype == "checkbox") {
         } else {
            $(pobj).addClass("err");
         }
      } else if ((ltagname == "LI") || (ltagname == "SELECT") || (ltagname == "TEXTAREA") || (ltagname == "DD") || (ltagname == "P") || (ltagname == "SPAN") || (ltagname == "A")) {
         $(pobj).addClass("err");
      }
   }, removeClass : function(pobj) {
      var ltagname = pobj.tagName;
      if (ltagname == "INPUT") {
         var lid = pobj.id;
         var ltype = $('#' + lid).attr('type');
         if (ltype == "hidden") {
            if (pobj.classList.contains("appzillon_date")) {
               var lclasslist = document.getElementById(lid).nextSibling.nextElementSibling;
               lclasslist.className = lclasslist.className.replace("err", '');
            }
         } else if (ltype == "checkbox") {
         } else {
            $(pobj).removeClass("err");
         }
      } else if ((ltagname == "LI") || (ltagname == "SELECT") || (ltagname == "TEXTAREA") || (ltagname == "DD") || (ltagname == "P") || (ltagname == "SPAN") || (ltagname == "A")) {
         $(pobj).removeClass("err");
      }
   }, 
   /**
    * This API validates value of the passed DOM object as a string.
    * @param {object} pobj This is the object whose value needs to be validated as a string.
    * @example
    * apz.val.validateStringObj(obj);
    * apz.val.validateStringObj(document.getElementById("ElementId"));
    */
   validateStringObj : function(pobj) {
	   /* Params contains the below value
	       *** obj(DOM element) ***
	       * Response contains below value
	       *** boolean ***
	   */
      var lerror = "";      
      var lvalue = this.apz.getObjValue(pobj);
      lerror = this.validateString(pobj, lvalue);
      return lerror;
   }, validateEmailObj : function(pobj) {
      // should return true if @ is not present or . is not present and if no successor after .
      var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
      var lvalue = this.apz.getObjValue(pobj);
      var lresult = emailPattern.test(lvalue);
      var lerror = "";
      if (!lresult) {
        lerror = "APZ-CNT-128";
      }
      return lerror;
   }, validateCamelCase : function(pobj) {
        var lvalue = this.apz.getObjValue(pobj);
        var charArr = lvalue.split(" ");
        var lresult = true;
        var lerror = "";
        for(var i=0;i< charArr.length; i++) {
          if(charArr[i].length > 0) {
              if(charArr[i][0] == charArr[i][0].toUpperCase()) {
                lresult = false;
                break;
              }
            }
        }
        if (!lresult) {
        lerror = "APZ-CNT-128";
      }
      return lerror;
   },
   /**
    * 
    * @param {object} pobj 
    * @param {string} pval 
    * @description
    * <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | apz.app.preValidateString | This API will be called before validating String. |
    * | apz.app.postRowClicked | This API will be called after validating String. |
    * @example
    * apz.app.preValidateString = function(pobj){
    * //// write code to perform some action before the validateString execution.
	 * }
    * @example
    * apz.app.postValidateString = function(pobj){
	 * 	//// write code to perform some action just after validateString execution.
	 * }
    * 
    */
   validateString : function(pobj, pval) {
      var lerror = "";
      var checkvalString = true;
      var lid = this.apz.getObjIdWORowNumber(pobj);
      if(this.apz.isFunction(this.apz.app.preValidateString)){
         checkvalString = this.apz.app.preValidateString(pval);
         if (this.apz.isNull(checkvalString)) {
            checkvalString = true;
         }
      }
      if(checkvalString){
         var lresult = true;
         var lelemobj = this.apz.scrMetaData.elmsMap[lid];
         if (!this.apz.isNull(lelemobj)) {
            var lmaxlength = lelemobj.maxLen;
            var lminlength = lelemobj.minLen;
            var lpattern = lelemobj.pattern;
            if (!this.apz.isNull(pval)) {
               if (lresult == true) {
                  if (!this.apz.isNull(lmaxlength)) {
                     if (pval.length > lmaxlength) {
                        lresult = false;
                        lerror = "APZ-VAL-002";
                     }
                  }
               }
               if (lresult == true) {
                  if (!this.apz.isNull(lminlength)) {
                     if (pval.length < lminlength) {
                        lresult = false;
                        lerror = "APZ-VAL-003";
                     }
                  }
               }
               //Pattern Provided by user will get highest priority
               if (lresult == true) {
                  if (!this.apz.isNull(lpattern)) {
                     lpattern = this.apz.replaceAll({"string":lpattern,"key":'/',"replaceData":''});
                     var lregex = new RegExp(lpattern);
                     try {
                        if (!lregex.test(pval)) {
                           lresult = false;
                           lerror = "APZ-CNT-128";
                        }
                     } catch (err) {
                        lresult = false;
                        lerror = "APZ-CNT-128";
                     }
                  }
               }
            }
         }
      }
      if(this.apz.isFunction(this.apz.app.postValidateString)){
         this.apz.app.postValidateString(pobj);
      }
      return lerror;
   }, 
   /**
    * This API validates entered number in the fields.
    * @param {object} pobj The object whose value needs to be validated as a number.
    * @param {string} displayAsLiteral Used to format numbers to display in terms of number or literal (Ex. 10,00,000 or 1L). Takes Value “Y” or “N”.
    * @example
    * apz.val.validateNumberObj(document.getElementById("ElementId"),”N”);
    */
   validateNumberObj : function(pobj,displayAsLiteral) {
	   /* Params contains the below values
	       *** obj(DOM element), displayAsLiteral(Y/N) ***
	       * Response contains below value
	       *** boolean ***
	   */
      var lerror = "";
      var lvalue = this.apz.getObjValue(pobj);
      lerror = this.validateNumber(pobj, lvalue,displayAsLiteral);
      return lerror;
   }, 
   /**
    * 
    * @param {object} pobj 
    * @param {string} pval 
    * @param {string} displayAsLiteral
    * @description
    * <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | apz.app.preValidateNumber | This API will be called before validating Number. |
    * | apz.app.postValidateNumber | This API will be called after validating Number. |
    * @example
    * apz.app.preValidateNumber = function(pobj){
    * //// write code to perform some action before the validateNumber execution.
	 * }
    * @example
    * apz.app.postValidateNumber = function(pobj){
	 * 	//// write code to perform some action just after validateNumber execution.
	 * }
    * 
    */
   validateNumber : function(pobj, pval,displayAsLiteral) {
      var lerror = "";
      var lresult = true;
      var lcheckvalnumber = true;
      var lid = this.apz.getObjIdWORowNumber(pobj);
      if(this.apz.isFunction(this.apz.app.preValidateNumber)){
         lcheckvalnumber = this.apz.app.preValidateNumber(pobj);
         if (this.apz.isNull(lcheckvalnumber)) {
            lcheckvalnumber = true;
         }
      }
      if (lcheckvalnumber) {
      var lelemobj = this.apz.scrMetaData.elmsMap[lid];
      if (!this.apz.isNull(lelemobj)) {
         var lminval = lelemobj.minVal;
         var lmaxval = lelemobj.maxVal;
         var lminlen = lelemobj.minLen;
         var lmaxlen = lelemobj.maxLen;
         var ldtype = lelemobj.dataType;
         var unformatedNumber;
         if (!this.apz.isNull(pval)) {
            //// Checking for Number or Integer
            if (ldtype == "NUMBER") {
               lresult = this.isNumber(pval,displayAsLiteral);
            } else {
               lresult = this.isInt(pval,displayAsLiteral);
            }
            if (!lresult) {
               lerror = "APZ-VAL-010";
               lresult = "false";
            } else {
               lmaxval = parseInt(lmaxval);
               lminval = parseInt(lminval);
               var params = {};
               params.value = pval;
               params.displayAsLiteral = lelemobj.displayAsLiteral;
               params.decimalSep = this.apz.decimalSep;
               unformatedNumber = this.apz.unFormatNumber(params);
            }
         }
         if (lresult == true) {
            if (!this.apz.isNull(lminval)) {
               if (unformatedNumber < lminval) {
                  lresult = false;
                  lerror = "APZ-VAL-006";
               }
            }
         }
         if (lresult == true) {
            if (!this.apz.isNull(lmaxval)) {
               if (unformatedNumber > lmaxval) {
                  lresult = false;
                  lerror = "APZ-VAL-005";
               }
            }
         }
         ///////// Added as per CITI request
         if (lresult == true) {
            if (!this.apz.isNull(lminlen)) {
               if (unformatedNumber.length < lminlen) {
                  lresult = false;
                  lerror = "APZ-VAL-006";
               }
            }
         }
         if (lresult == true) {
            if (!this.apz.isNull(lmaxlen)) {
               if (unformatedNumber.length > lmaxlen) {
                  lresult = false;
                  lerror = "APZ-VAL-005";
               }
            }
         }
      }
   }
    if(this.apz.isFunction(this.apz.app.postValidateNumber)){
         this.apz.app.postValidateNumber(pobj);
      }
      return lerror;
   }, 
   /**
    * This API validates the date according to the user date format.
    * @param {object} pobj The object whose value needs to be validated with date format.
    * @example
    * apz.val.validateDateObj(obj);
    * apz.val.validateDateObj(document.getElementById("ElementId"));
    */
   validateDateObj : function(pobj) {
	   /* Params contains the below value
	       *** obj(DOM element) ***
	       * Response contains below value
	       *** boolean ***
	   */
      var lerror = "";     
      var lvalue = this.apz.getObjValue(pobj);
      lerror = this.validateDate(pobj, lvalue);
      return lerror;
   }, 
   /**
    * 
    * @param {object} pobj 
    * @param {string} pval
    * @description
    * <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | apz.app.preValidateDate | This API will be called before validating Date. |
    * | apz.app.postValidateDate | This API will be called after validating Date. |
    * @example
    * apz.app.preValidateDate = function(pobj){
    * //// write code to perform some action before the validateDate execution.
	 * }
    * @example
    * apz.app.postValidateDate = function(pobj){
	 * 	//// write code to perform some action just after validateDate execution.
	 * }
    *  
    */
   validateDate : function(pobj, pval) {
      var lerror = "";
      var checkValDate = true;
      if(this.apz.isFunction(this.apz.app.preValidateDate)){
         checkValDate = this.apz.app.preValidateDate(pobj);
         if (this.apz.isNull(checkValDate)) {
            checkValDate = true;
         }
      }
      if(checkValDate){
         var lresult = true;
         if (!this.apz.isNull(pval)) {
            var luserformat = this.apz.dateFormat;
            var lcheckdate = this.isDate(pval, luserformat);
            if (!lcheckdate) {
               lresult = false;
               lerror = "APZ-VAL-008";
            }
         }
      }
      if(this.apz.isFunction(this.apz.app.postValidateDate)){
         this.apz.app.postValidateDate(pobj);
      }
      return lerror;
   }, 
   /**
    * This API validates the dateTime with given user date-time format.
    * @param {object} pobj The object whose value needs to be validated with date-time format.
    * @example
    * apz.val.validateDateObj(obj);
    * apz.val.validateDateObj(document.getElementById("ElementId"));
    */
   validateDateTimeObj : function(pobj) {
	   /* Params contains the below value
	       *** obj(DOM element) ***
	       * Response contains below value
	       *** boolean ***
	   */
      var lerror = "";
      var lvalue =  this.apz.getObjValue(pobj);
      lerror = this.validateDateTime(pobj, lvalue);
      return lerror;
   },
   /**
    * 
    * @param {object} pobj 
    * @param {string} pval
    * @description
    * <b>Callbacks</b><br>
    * |  Name | Description   |
    * | :------------- |:-------------|
    * | apz.app.preValidateDateTimeObj | This API will be called before validating DateTime. |
    * | apz.app.postValidateDateTimeObj | This API will be called after validating DateTime. |
    * @example
    * apz.app.preValidateDateTimeObj = function(pobj){
    * //// write code to perform some action before the validateDateTime execution.
	 * }
    * @example
    * apz.app.postValidateDateTimeObj = function(pobj){
	 * 	//// write code to perform some action just after validateDateTime execution.
	 * }
    *  
    */
   validateDateTime : function(pobj, pval) {
      var lerror = "";
      var lresult = true;
      var lcheckvaldate = true;
      if(this.apz.isFunction(this.apz.app.preValidateDateTimeObj)){
         lcheckvaldate = this.apz.app.preValidateDateTimeObj(pobj);
         if (this.apz.isNull(lcheckvaldate)) {
            lcheckvaldate = true;
         }
      }
      if (lcheckvaldate) {
         if (!this.apz.isNull(pval)) {
            var luserformat = this.apz.dateTimeFormat;
            var lcheckdate = this.isDate(pval, luserformat);
            if (!lcheckdate) {
               lresult = false;
               lerror = "APZ-VAL-008";
            }
         }
      }
      if(this.apz.isFunction(this.apz.app.postValidateDateTimeObj)){
         this.apz.app.postValidateDateTimeObj(pobj);
      }
      return lerror;
   }, isInt : function(pvalue,displayAsLiteral) {
      var lchecknumber = this.isNumber(pvalue,displayAsLiteral);
      var lerror = false;
      var lvalue = pvalue;
      var linteger = '';
      if (lchecknumber) {
         linteger = lvalue.toString().indexOf('.');
         if (linteger === -1) {
            lerror = true;
         }
      }
      return lerror;
   }, isFloat : function(pvalue,displayAsLiteral) {
      var lchecknumber = this.isNumber(pvalue,displayAsLiteral);
      var lerror = false;
      var lvalue = pvalue;
      var lfloat = '';
      if (lchecknumber) {
         lfloat = lvalue.toString().indexOf('.');
         if (lfloat != -1) {
            lerror = true;
         }
      }
      return lerror;
   }, isNumber : function(pvalue,displayAsLiteral) {
      var params = {};
      params.value = pvalue;
      params.decimalSep = this.apz.decimalSep;
      params.displayAsLiteral = displayAsLiteral;
      var lval =  this.apz.unFormatNumber(params);
      var lerror = false;
      try {
         if ($.isNumeric(lval)) {
            lerror = true;
         }
      } catch (err) {
         lerror = false;
      }
      return lerror;
   }, isString : function(pvalue) {
      var lerror = false;
      if ($.type(pvalue).toLowerCase() == "string") {
         lerror = true;
      }
      return lerror;
   }, isDate : function(pvalue, pdateformat) {
      var lerror = false;
      try {
        var dateValueArr = pvalue.split(" - ");
        if(dateValueArr.length == 2) {
          var ldatecheck1 = Date.parseExact(dateValueArr[0], pdateformat);
          var ldatecheck2 = Date.parseExact(dateValueArr[1], pdateformat);
          if (!this.apz.isNull(ldatecheck1) && !this.apz.isNull(ldatecheck2)) {
            lerror = true;
         }
        } else {
          var ldatecheck = Date.parseExact(pvalue, pdateformat);
          if (!this.apz.isNull(ldatecheck)) {
            lerror = true;
         }
        }
      } catch (err) {
         lerror = false;
      }
      return lerror;
   },
   // //////////////// Validate the Password //////////
   /**
    * This API validates password values.
    * @param {object} args args includes 
    * <br>oldPassword: The previous password of the user.</br>
    * <br>newPassword: The new password to be set by the user.</br>
    * <br>confirmPassword: The new password captured once again for confirmation purpose.</br>
    * @example
    * var obj = {};
    * obj.oldPassword = "Password@1";
	 * obj.newPassword = "Password@123";
	 * obj.confirmPassword = "Password@123";
    * apz.val.validatePassword(obj);
    * 
    */
   validatePassword : function(args) {
      //Expects newPassword,confirmPassword,oldPassword
	   /* Params contains the below attributes
	       *** oldPassword, newPassword, confirmPassword ***
	       * Response contains below attributes
	       *** boolean ***
	   */
      var lreturn = true;
         var params = {};
      if (this.apz.isNull(args.newPassword)) {
         // //if new password is null.
         params.code = "APZ-SVR-PNL";
         this.apz.dispMsg(params);
         lreturn = false;
      } else if (this.apz.isNull(args.confirmPassword)) {
         // //if confirm password is null.
         params.code = "APZ-SVR-PNL";
         this.apz.dispMsg(params);
         lreturn = false;
      } else if (this.apz.isNull(args.oldPassword)) {
         // // if old password is null.
         params.code = "APZ-SVR-PNL";
         this.apz.dispMsg(params);
         lreturn = false;
      } else {
         if (args.oldPassword == args.newPassword) {
            // //if old password and new password are same.
            params.code = "APZ-PSW-RULE";
            this.apz.dispMsg(params);
            lreturn = false;
         } else if (args.newPassword != args.confirmPassword) {
            // //if new password and confirm password are not same.
            params.code = "APZ-SVR-PNC";
            this.apz.dispMsg(params);
            lreturn = false;
         }
      }
      return lreturn;
   }
}