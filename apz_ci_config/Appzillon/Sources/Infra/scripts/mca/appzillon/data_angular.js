////Initialization
Apz.Data = function(apz) {
   this.apz = apz;
   this.apz.bindingEngine = "ANGULAR";
   this.angularAppName = "angularapp";
   this.angularControllerName = "angularappcontroller";
   this.angularApp = null;
   this.angularAppScope = null;
   //////////////Private Variables//////////////
   ////Model Data
   var scrdata = {};
   this.scrdata = function() {
      return scrdata;
   }
};
Apz.Data.prototype = {
   init : function(){
       this.angularApp = angular.module(this.angularAppName, []);
	   var dataObj = this;
       this.angularApp.controller(this.angularControllerName, ["$scope",
           function($scope) {
               var vm = this;
               $scope.apz = apz;
               dataObj.angularAppScope = $scope;
               $scope.data = {};
           }
       ]);
       // ///Pagination Filter
       angular.module(this.angularAppName).filter('pagination', function() {
           return function(input, start) {
               if (!dataObj.apz.isNull(input)) {
                   start = +start;
                   return input.slice(start);
               }
           };
       });
       // //Check Box Handling
       angular.module(this.angularAppName).directive("apzcheckbox", function markyMarkDirective() {
           return ({
               link: link,
               require: "ngModel",
               restrict: "A"
           });

           function link(scope, element, attributes, ngModelController, $index) {
               ngModelController.$formatters.push(formatInput);
               ngModelController.$parsers.push(parseOutput);

               function formatInput(value) {
                   var enable = false;
                   var checkedVal = element.attr('checkedval');
                  // var unCheckedVal = element.attr('uncheckedval');
                   var indeterminateVal = element.attr('indeterminateval');
                   if (value == checkedVal) {
                       enable = true;
                   }
                   return enable;
               }

               function parseOutput(value) {
                   var val = dataObj.apz.getElmValue(element[0].id);
                   return val;
               }
           }
       });
       // //Number Formatting
       angular.module(this.angularAppName).directive("apznumber", function markyMarkDirective() {
           return ({
               link: link,
               require: "ngModel",
               restrict: "A"
           });

           function link(scope, element, attributes, ngModelController) {
               ngModelController.$formatters.push(formatInput);
               ngModelController.$parsers.push(parseOutput);

               function formatInput(value) {
                   var obj = element[0];
                   var id = dataObj.apz.getObjIdWORowNumber(obj);
                   var decPoints = 0;
                   var displayLiteral = "N";
                   var val = value;
                   var skipFormat = "N";
                   if (dataObj.apz.scrMetaData.elmsMap && !dataObj.apz.isNull(dataObj.apz.scrMetaData.elmsMap[id])) {
                       var elmData = dataObj.apz.scrMetaData.elmsMap[id];
                       var recNo = dataObj.getObjRowNumber(obj);
                       decPoints = dataObj.apz.getDecimalPoints(elmData, recNo);
                       displayLiteral = elmData.displayAsLiteral;
                       skipFormat = elmData.skipFormat;
                   }
                   if (skipFormat == 'N') {
                      var params = {};
                      params.value = value;
                      params.decimalSep = dataObj.apz.decimalSep;
                      params.decimalPoints = decPoints;
                      params.mask = dataObj.apz.numberMask;
                      params.displayAsLiteral = displayLiteral;
                       val = dataObj.apz.formatNumber(params);
                    }
                   return val;
                   }
               }

               function parseOutput(value) {
                      var params = {};
                      params.value = value;
                      params.decimalSep = dataObj.apz.thousandSep;
                   return dataObj.apz.unFormatNumber(params);
               }
       });
       // //Date Formatting
       angular.module(this.angularAppName).directive("apzdate", function markyMarkDirective() {
           return ({
               link: link,
               require: "ngModel",
               restrict: "A"
           });

           function link(scope, element, attributes, ngModelController) {
               ngModelController.$formatters.push(formatDate);
               ngModelController.$parsers.push(parseDate);
               var usrDateFrmt = dataObj.apz.dateFormat;
               var serverDateFrmt = dataObj.apz.dfltServerDateFormat; // TBC - Should be created as a constant for default format?
               function formatDate(value) {
                var params = {};
                params.val = value;
                params.fromFormat = serverDateFrmt;
                params.toFormat = usrDateFrmt;
                return dataObj.apz.formatDate(params);
               }

               function parseDate(value) {
                var params = {};
                params.val = value;
                params.fromFormat = usrDateFrmt;
                params.toFormat = serverDateFrmt;
                return dataObj.apz.formatDate(params);
               }
           }
       });
       // //Date Time Formatting
       angular.module(this.angularAppName).directive("apzdatetime", function markyMarkDirective() {
           return ({
               link: link,
               require: "ngModel",
               restrict: "A"
           });

           function link(scope, element, attributes, ngModelController) {
               ngModelController.$formatters.push(formatDateTime);
               ngModelController.$parsers.push(parseDateTime);
               var usrDateTimeFrmt = dataObj.apz.dateTimeFormat;
               var serverDateTimeFrmt = dataObj.apz.dfltServerDateTimeFormat; // TBC - Should be created as a constant for default format?
               function formatDateTime(value) {
                var params = {};
                params.val = value;
                params.fromFormat = serverDateTimeFrmt;
                params.toFormat = usrDateTimeFrmt;
                return dataObj.apz.formatDate(params);
               }
               function parseDateTime(value) {
                var params = {};
                params.val = value;
                params.fromFormat = usrDateTimeFrmt;
                params.toFormat = serverDateTimeFrmt;
                return dataObj.apz.formatDate(params);
               }
           }
       });
       // Tags Initializion
       angular.module(this.angularAppName).directive("apztags", function markyMarkDirective() {
           return ({
               link: link,
               require: "ngModel",
               restrict: "A"
           });

           function link(scope, element, attributes, ngModelController) {
               ngModelController.$formatters.push(formatTags);
               // ngModelController.$parsers.push(parseTags);
               function formatTags(value) {
                   if (dataObj.apz.isNull(value)) {
                       value = "";
                   }
                   element.importTags("");
                   return value;
               }
           }
       });
       // /Masking
       angular.module(this.angularAppName).filter('apzmask', function() {
           return function(value, element) {
               var maskFormat = dataObj.apz.scrMetaData.elmsMap[element].mask;
               if (!dataObj.apz.isNull(maskFormat) && !dataObj.apz.isNull(value)) {
                   return dataObj.apz.getMaskedValue(maskFormat, value);
               }
           };
       });
       angular.bootstrap(document, [this.angularAppName]);
   },
   tagsDataInit : function(id) {
      apzJquery("#" + id).trigger("input");
   },
   resetView : function(){
         var noOfContainers = this.apz.scrMetaData.containers.length;
         var params = {};
         //params.tracker = null;
         /*params.recNo = 0;*/
      for (var i = 0; i < noOfContainers; i++) {
        var containerData = this.apz.scrMetaData.containers[i];
        params = {};
        params.recNo = 0;
        params.container = containerData.id;
        this.goToRecord(params);
      }
   },
   loadData : function(iface) {
       if (!this.apz.isNull(iface)) {
           var dModelPos = iface.lastIndexOf("_")
           if (dModelPos != -1) {
               var dModel = iface.substr(dModelPos + 1);
               if (dModel == "Res" || dModel == "Req") {
                   this.angularAppScope.data[iface] = this.apz.scrdata[iface];
               }
           } else {
               var reqIface = this.apz.getReqRoot(iface);
               var resIface = this.apz.getResRoot(iface);
               if (!this.apz.isNull(this.scrdata[reqIface])) {
                   this.angularAppScope.data[reqIface] = this.scrdata[reqIface];
               }
               if (!this.apz.isNull(this.scrdata[resIface])) {
                   this.angularAppScope.data[resIface] = this.scrdata[resIface];
               }
           }
       } else {
           this.angularAppScope.data = this.scrdata;
       }
       this.resetView();
       this.applyScope();
   },
   clearData : function(iface) {
       if (!this.apz.isNull(iface)) {
           var ifaces = {};
           var dModelPos = iface.lastIndexOf("_");
           if (dModelPos != -1) {
               var dModel = iface.substr(dModelPos + 1);
               if (dModel == "Res" || dModel == "Req") {
                   this.angularAppScope.data[iface] = null;
               }
               ifaces[iface] = "Y";
           } else {
               var reqIface = this.apz.getReqRoot(iface);
               var resIface = this.apz.getResRoot(iface);
               ifaces[reqIface] = "Y";
               ifaces[resIface] = "Y";
           }
           var bkpScpData = this.apz.copyJSONObject(this.angularAppScope.data);
           this.angularAppScope.data = {};
           // /Clear Full Data
           for (var iface in bkpScpData) {
               if (!this.apz.containsKey(ifaces, iface)) {
                   this.angularAppScope.data[iface] = this.scrdata[iface];
               }
           }
       } else {
           this.angularAppScope.data = {};
       }
       this.applyScope();
   },
   applyScope : function() {
      this.angularAppScope.$apply();
   },
   getContainerNodes : function(container) {
       var group = this.apz.scrMetaData.containersMap[container].group;
       if (this.apz.isNull(group)) {
           return this.apz.scrMetaData.containersMap[container].nodes;
       } else {
           return this.apz.scrMetaData.groupsMap[group].nodes;
       }
   },
   getStartRec : function(container) {
       var rec = 0;
       if (this.apz.scrMetaData.containersMap[container].currPage == 1) {
           rec = 0;
       } else if (this.apz.scrMetaData.containersMap[container].paginationStyle == "APPEND") {
           rec = 0;
       } else {
           rec = ((this.apz.scrMetaData.containersMap[container].currPage - 1) * (this.apz.scrMetaData.containersMap[container].pageSize));
       }
       return rec;
   },
   getLimit : function(container) {
       var rec = this.apz.scrMetaData.containersMap[container].pageSize;
       if (this.apz.scrMetaData.containersMap[container].paginationStyle == "APPEND") {
           rec = (this.apz.scrMetaData.containersMap[container].currPage) * this.apz.scrMetaData.containersMap[container].pageSize;
       }
       return rec;
   },
   getTotalRecords : function(container) {
       var recs = 0;
       var group = this.apz.scrMetaData.containersMap[container].group;
       var nodes = this.getContainerNodes(container);
       var noOfNodes = nodes.length;
       if (noOfNodes > 0) {
           recs = this.getNoOfRecs(nodes[0]);
       }
       if (recs === 0) {
           var multiRec = this.apz.scrMetaData.containersMap[container].multiRec;
           if (multiRec == "N") {
               recs = 1;
           }
       }
       return recs;
   },
   getTotalPages : function(recs, container) {
       var noOfPages = 1;
       var pageSize = this.apz.scrMetaData.containersMap[container].pageSize;
       noOfPages = Math.ceil(recs / pageSize);
       if (noOfPages === 0) {
           noOfPages = 1;
       }
       return noOfPages;
   },
   getCurrPage : function(recNo, container) {
       var currPage = 1;
       var oldCurrPage = this.apz.scrMetaData.containersMap[container].currPage;
       var pageSize = this.apz.scrMetaData.containersMap[container].pageSize;
       recNo = recNo + 1;
       currPage = Math.ceil(recNo / pageSize);
       if (currPage === 0) {
           currPage = 1;
       }
       // //Handle for Append Should not Decrement..                            TBC - Should it be handled for append case?
       // if (this.scrMetaData.containersmap[container].paginationStyle == "APPEND") {
       //    if (oldCurrPage > currPage) {
       //       currPage = oldCurrPage;
       //    }
       // }
       return currPage;
   },
   getRow : function(container, dataRec) {
       var row = dataRec;
       if (this.apz.scrMetaData.containersMap[container].paginationStyle !== "APPEND") {
           row = dataRec % this.apz.scrMetaData.containersMap[container].pageSize;
       }
       return row;
   },
   getDataRec : function(container, row) {
       var dataRec = -1;
       var containreObj = this.apz.scrMetaData.containersMap[container];
       if (containreObj.currPage == 0) {
           dataRec = 0;
       } else if ((containreObj.currPage == 1) || (containreObj.paginationStyle == "APPEND")) {
           dataRec = row;
       } else {
           dataRec = ((containreObj.currPage - 1) * (containreObj.pageSize)) + row;
       }
       return dataRec;
   },
   rowClicked : function(rowObj) {
       var rowId = rowObj.id;
       var index = rowId.lastIndexOf("_row_");
       var container = rowId.substring(0, index);
       var rowNo = this.apz.getInt(rowId.substring(index + 5));
       if (this.apz.scrMetaData.containersMap[container].currRow !== rowNo) {
           var recNo = this.getDataRec(container, rowNo);
           var params = {};
           params.tracker = null;
           params.container = container;
           params.recNo = recNo;
           this.goToRecord(params);
           this.appendnestList(container, rowNo);
           // / Adding Child List
           this.applyScope();
       }
   },
   goToRecord : function(params) {
	   //Expects tracker, container, recNo
       if (this.apz.isNull(params.tracker)) {
           tracker = {};
       }
       var multiRec = this.apz.scrMetaData.containersMap[params.container].multiRec;
       var nodes = this.getContainerNodes(params.container);
       var noOfNodes = nodes.length;
       var node = "";
       var containerType = this.apz.scrMetaData.containersMap[params.container].type;
       ////Set currRec for all Nodes
       if (noOfNodes > 0) {
           for (var i = 0; i < noOfNodes; i++) {
               node = nodes[i];
               this.apz.scrMetaData.nodesMap[node].currRec = params.recNo;
               ////Set Container currRec based on Master Node of the Container
               if (i === 0) {
                   if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {
                       this.apz.scrMetaData.containersMap[params.container].currRec = params.recNo;
                   } else {
                       this.apz.scrMetaData.containersMap[params.container].currRec = 0;
                   }
               }
           }
       }
       ////Set Total Records,Current Page, and Total Pages..
       var recs = 0;
       var totalPages = 0;
       var currPage = 0;
       recs = this.getTotalRecords(params.container);
       totalPages = this.getTotalPages(recs, params.container);
       currPage = this.getCurrPage(params.recNo, params.container);
       this.apz.scrMetaData.containersMap[params.container].totalRecs = recs;
       this.apz.scrMetaData.containersMap[params.container].totalPages = totalPages;
       this.apz.scrMetaData.containersMap[params.container].currPage = currPage;
       this.apz.scrMetaData.containersMap[params.container].currRow = this.getRow(params.container, params.recNo);
       ////Manipulate Pagination Controls
       var pgntnStyle = this.apz.scrMetaData.containersMap[params.container].paginationStyle;
       if ((!this.apz.isNull(pgntnStyle)) && apzJquery(document.getElementById(params.container)).is(":visible") && (containerType == "LIST" || containerType == "TABLE") && (pgntnStyle == "PAGE2")) {
           this.apz.showPageStyle2Controls(params.container);
       }
       ////Paint Chart and Gauges
       if (containerType == "GAUGE") {
        var myObj = this;
           var gaugeObj = this.apz.scrMetaData.gaugesMap[params.container];
           var gauges = new Apz.Gauges(myObj.apz);
           gauges.paintGauge(gaugeObj);
           this.applyScope();
       } else if (containerType == "CHART") {
		   var myObj = this;
         requirejs([this.apz.getInfraPath() + "/appzillon/charts.js"], function() {
            var chartObj = myObj.apz.scrMetaData.chartsMap[params.container];
            try {
              if(chartObj != undefined) {
                var charts = new Apz.Charts(myObj.apz);
                charts.paintChart(chartObj);
              }
            } catch(e) {
               console.log("Problems with Charts Library");
            }
         });
           this.applyScope();
       }
       ////Update Tracker
       if (this.apz.isNull(params.tracker)) {
        params.tracker = {};
       }
       params.tracker[params.container] = "Y";
       // //Child Processing...
       var childNode = null;
       var noOfChilds = 0;
       if (noOfNodes > 0) {
           for (var i = 0; i < noOfNodes; i++) {
               node = nodes[i];
               noOfChilds = this.apz.scrMetaData.nodesMap[node].childs.length;
               if (noOfChilds > 0) {
                   for (var c = 0; c < noOfChilds; c++) {
                       childNode = this.apz.scrMetaData.nodesMap[node].childs[c];
                       var group = this.apz.scrMetaData.nodesMap[childNode].group;
                       if (!this.apz.isNull(group)) {
                           var childContainers = this.apz.scrMetaData.groupsMap[group].containers;
                           if (childContainers.length > 0) {
                               var childContainer = childContainers[0];
                               if (!this.apz.containsKey(params.tracker, childContainer)) {
                                  var args = {};
                                  args.tracker = params.tracker;
                                  args.container = childContainer;
                                  args.recNo = 0;
                                   this.goToRecord(args);
                                  }
                               }
                           }
                       }
                   }
               }
           }
           if(containerType == "TABLE") {
              this.apz.setTableHeight(params.container,false);
              this.apz.initDataTable(document.getElementById(params.container+"_table"),false);
              if(apzJquery(document.getElementById(params.container+"_table")).hasClass('responsive') && apzJquery(document.getElementById(params.container+"_table")).find('tbody tr').length > 0) {
                this.apz.closeResponsiveTableRows(apzJquery(document.getElementById(params.container+"_table")));
              }
           }
   },
   changePage : function(container, ind, obj) {
       var checkChangePage = true;
       try {
           checkChangePage = this.apz.app.preChangePage(container, ind, obj);
           if (this.apz.isNull(checkChangePage)) {
               checkChangePage = true;
           }
       } catch (err) {
           checkChangePage = true;
       }
       if (checkChangePage) {
           var newPage = 0;
           if (ind == "N") {
               newPage = this.apz.scrMetaData.containersMap[container].currPage + 1;
           } else if (ind == "P") {
               newPage = this.apz.scrMetaData.containersMap[container].currPage - 1;
           } else if (ind == "F") {
               newPage = 1;
           } else if (ind == "L") {
               newPage = this.apz.scrMetaData.containersMap[container].totalPages;
           } else if (ind == "S") {
               if (this.apz.scrMetaData.containersMap[container].paginationStyle == "PAGE2") {
                   newPage = parseInt(this.apz.getElmValue(obj.id));
                   // this.setObjValue(obj,
                   // this.scrMetaData.containersmap[container].currPage);
               } else {
                   var cpId = container + "_cp";
                   try {
                       newPage = parseInt(document.getElementById(cpId).value);
                   } catch (e) {
                       newPage = this.apz.scrMetaData.containersMap[container].currPage;
                   }
               }
           }
           if ((newPage > 0) && (newPage <= this.apz.scrMetaData.containersMap[container].totalPages) && (newPage != this.apz.scrMetaData.containersMap[
               container].currPage)) {
               var dataRecNo = (newPage - 1) * this.apz.scrMetaData.containersMap[container].pageSize;
                var params = {};
                params.tracker = null;
                params.container = container;
                params.recNo = dataRecNo;
               this.goToRecord(params);
           }
           this.applyScope();
       }
       try {
           apz.app.postChangePage(container, ind, obj);
       } catch (err) {}
   },
   changeRow : function(container, ind) {
       var changeRow = true;
       try {
           changeRow = apz.app.preChangeRow(container, ind);
           if (this.apz.isNull(changeRow)) {
               changeRow = true;
           }
       } catch (err) {
           changeRow = true;
       }
       if (changeRow) {
           var newRec = 0;
           if (ind == "N") {
               newRec = this.apz.scrMetaData.containersMap[container].currRec + 1;
           } else if (ind == "P") {
               newRec = this.apz.scrMetaData.containersMap[container].currRec - 1;
           } else if (ind == "F") {
               newRec = 0;
           } else if (ind == "L") {
               newRec = this.apz.scrMetaData.containersMap[container].totalRecs - 1;
           } else if (ind == "S") {
               var cpId = container + "_cr";
               try {
                   newRec = parseInt(document.getElementById(cpId).innerHTML) - 1;
               } catch (e) {
                   newRec = this.apz.scrMetaData.containersMap[container].currRec;
               }
           }
           if ((newRec >= 0) && (newRec < this.apz.scrMetaData.containersMap[container].totalRecs) && (newRec != this.apz.scrMetaData.containersMap[container].currRec)) {
              var params = {};
              params.tracker = null;
              params.container = container;
              params.recNo = newRec;
               this.goToRecord(params);
              }
           }
           this.applyScope();
       try {
           apz.app.postChangeRow(container, ind);
       } catch (e) {}
   },
   createDataPointer: function(params) {
       var pointer = this.scrdata;
       var mrParent = this.apz.scrMetaData.nodesMap[params.nodeId].mrParent;
       if ((params.nodeId != null) && (params.nodeId != "") && (params.nodeId != "undefined") && (params.nodeId != undefined)) {
           var parents = this.apz.scrMetaData.nodesMap[params.nodeId].parents;
           var parentsLen = parents.length;
           var nodeId = "";
           var recNo = 0;
           for (var p = 0; p <= parentsLen; p++) {
               if (p == parentsLen) {
                   nodeId = params.nodeId;
                   recNo = params.dataRecNo;
               } else {
                   nodeId = parents[p];
                   recNo = this.apz.scrMetaData.nodesMap[nodeId].currRec;
                   if (this.apz.scrMetaData.nodesMap[nodeId].relType == "1:N") {
                       if (this.apz.scrMetaData.nodesMap[nodeId].mrParent == nodeId) {
                           if (recNo == -1) {
                               recNo = params.dataRecNo;
                           }
                       }
                   }
               }
               // //IDERES
               var nodeName = this.apz.getNodeName(nodeId);
               if (recNo >= 0) {
                   if (this.apz.scrMetaData.nodesMap[nodeId].relType == "1:N") {
                       if (!pointer[nodeName]) {
                           pointer[nodeName] = new Array();
                           recNo = 0;
                           // this.apz.scrMetaData.nodesmap[nodeId].currRec = 0;
                           pointer[nodeName][0] = {};
                       } else {
                           if (!pointer[nodeName][recNo]) {
                               pointer[nodeName][recNo] = this.getNewObject();
                           } else {
                               // //Record Exists.. If Insert then Create a new
                               // Record anyway...
                               if (params.force) {
                                   pointer[nodeName].splice(recNo, 0, this.getNewObject());
                               }
                           }
                       }
                       pointer = pointer[nodeName][recNo];
                   } else {
                       if (!pointer[nodeName]) {
                           pointer[nodeName] = this.getNewObject();
                       }
                       pointer = pointer[nodeName];
                   }
               } else {
                   pointer = null;
                   break;
               }
           }
       }
       return pointer;
   },
   getDataPointer: function(nodeId, rec) {
       var pointer = this.scrdata;
       if (!this.apz.isNull(nodeId)) {
           if (this.apz.isNull(this.apz.scrMetaData.nodesMap[nodeId])) {
               pointer = null;
           } else {
               var parents = this.apz.scrMetaData.nodesMap[nodeId].parents;
               var parentsLen = parents.length;
               var childNodeId = "";
               var recNo = 0;
               var mrParent = this.apz.scrMetaData.nodesMap[nodeId].mrParent;
               for (var p = 0; p <= parentsLen; p++) {
                   childNodeId = parents[p];
                   if (p == parentsLen) {
                       childNodeId = nodeId;
                       recNo = rec;
                   } else {
                       childNodeId = parents[p];
                       recNo = this.apz.scrMetaData.nodesMap[childNodeId].currRec;
                       if (this.apz.scrMetaData.nodesMap[childNodeId].relType == "1:N") {
                           if (this.apz.scrMetaData.nodesMap[nodeId].mrParent == childNodeId) {
                               if (recNo == -1) {
                                   recNo = rec;
                               }
                           }
                       }
                   }
                   var nodeName = this.apz.getNodeName(childNodeId);
                   if (this.apz.scrMetaData.nodesMap[childNodeId].relType == "1:N") {
                       if (!pointer[nodeName]) {
                           pointer = null;
                           break;
                       } else {
                           if (!pointer[nodeName][recNo]) {
                               pointer = null;
                               break;
                           }
                       }
                       pointer = pointer[nodeName][recNo];
                   } else {
                       if (!pointer[nodeName]) {
                           pointer = null;
                           break;
                       }
                       pointer = pointer[nodeName];
                   }
               }
           }
       }
       return pointer;
   },
   getNoOfRecs: function(nodeId) {
       var noOfRecs = 0;
       var parentNode = this.apz.scrMetaData.nodesMap[nodeId].parent;
       var parentCurrRec = -1;
       // //Get Parent's Current Record...
       if (!this.apz.isNull(parentNode)) {
           parentCurrRec = this.apz.scrMetaData.nodesMap[parentNode].currRec;
       } else {
           parentCurrRec = 0;
       }
       var pointer = this.getDataPointer(parentNode, parentCurrRec);
       if (pointer === null) {
           noOfRecs = 0;
       } else {
           var nodeName = this.apz.getNodeName(nodeId);
           if (!pointer[nodeName]) {
               noOfRecs = 0;
           } else {
               if (this.apz.scrMetaData.nodesMap[nodeId].relType == "1:N") {
                   noOfRecs = pointer[nodeName].length;
               } else {
                   noOfRecs = 1;
               }
           }
       }
       return noOfRecs;
   },
   isRowSelected: function(container, rowNo) {
       rowNo = this.getRow(container, rowNo);
       var selected = false;
       var id = container + "_selcb_" + rowNo;;
       var obj = document.getElementById(id);
       var className;
       if (!this.apz.isNull(obj)) {
           selected = obj.checked;
       } else {
           id = container + '_row_' + rowNo;
           className = apzJquery('#' + id)[0].className;
           if (className.indexOf('selected') != -1) {
               selected = true;
           }
       }
       return selected;
   },
   createRow: function(container) {
       var createRow = true;
       try {
           createRow = apz.app.preCreateRow(container);
           if (this.apz.isNull(createRow)) {
               createRow = true;
           }
       } catch (err) {
           createRow = true;
       }
       if (createRow) {
           var totalRecs = this.getTotalRecords(container);
           var dataRecNo = -1;
           var selRow = -1;
           var action = "L";
           var newRec = -1;
           var currRec = -1;
           var multiRec = this.apz.scrMetaData.containersMap[container].multiRec;
           if (multiRec == "Y") {
               // Get First Slected Row..
               var noOfRows = apzJquery("#" + container + "_tbody tr").length;
               for (var r = 0; r < noOfRows; r++) {
                   if (this.isRowSelected(container, r)) {
                       selRow = r;
                       break;
                   }
               }
               if (selRow >= 0) {
                   newRec = this.getDataRec(container, selRow);
                   action = "B";
               } else {
                   newRec = totalRecs;
                   action = "L";
               }
           } else {
               currRec = this.apz.scrMetaData.containersMap[container].currRec;
               if (currRec == (this.apz.scrMetaData.containersMap[container].totalRecs - 1)) {
                   newRec = totalRecs;
                   action = "L";
               } else {
                   newRec = this.getDataRec(container, this.apz.scrMetaData.containersMap[container].currRow) + 1;
                   action = "A";
               }
           }
           if (newRec >= 0) {
               var nodes = this.getContainerNodes(container);
               var noOfNodes = nodes.length;
               var node = "";
               if (noOfNodes > 0) {
                   for (var i = 0; i < noOfNodes; i++) {
                       node = nodes[i];
                       //if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {}
                       var force = false;
                       if (action !== "L") {
                           force = true;
                       }
                       var params = {};
                       params.nodeId = node;
                       params.dataRecNo = newRec;
                       params.force = force; 
                       this.createDataPointer(params);
                   }
               }
           }
       }
       try {
           apz.app.postCreateRow(container);
       } catch (err) {}
       // //Go To Record
       var params = {};
       params.tracker = null;
       params.container = container;
       params.recNo = newRec;
       this.goToRecord(params);
       // //Appy Scope
       this.applyScope();
   },
   removeRows: function(container) {
       var removeRow = true;
       try {
           removeRow = apz.app.removeRows(container);
           if (this.apz.isNull(removeRow)) {
               removeRow = true;
           }
       } catch (err) {
           removeRow = true;
       }
       if (removeRow) {
           var totalRecs = this.getTotalRecords(container);
           var currRec = this.apz.scrMetaData.containersMap[container].currRec;
           if (totalRecs > 0) {
               var newRec = 0;
               var nodes = this.getContainerNodes(container);
               var noOfNodes = nodes.length;
               var multiRec = this.apz.scrMetaData.containersMap[container].multiRec;
               if (multiRec == "Y") {
                   var currRecDeletd = false;
                   var recsBeforeCurrRec = 0;
                   var minRec = 9999999;
                   var maxRec = -1;
                   var noOfRows = apzJquery("#" + container + "_tbody tr").length;
                   if (noOfRows > 0) {
                       for (var r = noOfRows - 1; r >= 0; r--) {
                           dataRecNo = this.getDataRec(container, r);
                           if (this.isRowSelected(container, r)) {
                               // //Minimum Record Deleted
                               if (dataRecNo <= minRec) {
                                   minRec = dataRecNo;
                               }
                               // //Maximum Record Deleted
                               if (dataRecNo >= maxRec) {
                                   maxRec = dataRecNo;
                               }
                               // /Current Record Deleted
                               if (dataRecNo === currRec) {
                                   currRecDeletd = false;
                               }
                               // //No of records deleted before currRec
                               if (dataRecNo < currRec) {
                                   recsBeforeCurrRec = recsBeforeCurrRec + 1;
                               }
                               var node = "";
                               if (noOfNodes > 0) {
                                   for (var i = 0; i < noOfNodes; i++) {
                                       node = nodes[i];
                                       if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {
                                           totalRecs = totalRecs - 1;
                                           this.deleteRecord(node, dataRecNo);
                                       }
                                   }
                               }
                           }
                       }
                   }
                   if (currRecDeletd) {
                       // /First Rec of curr Page
                       var newRec = this.getDataRec(container, 0);
                   } else {
                       newRec = currRec - recsBeforeCurrRec;
                   }
               } else {
                   var node = "";
                   if (noOfNodes > 0) {
                       for (var i = 0; i < noOfNodes; i++) {
                           node = nodes[i];
                           this.deleteRecord(node, currRec);
                           newRec = currRec;
                           // //Decrement
                           totalRecs = totalRecs - 1;
                       }
                   }
               }
               // //Check if the new record is available..
               if (newRec > (totalRecs - 1)) {
                   newRec = totalRecs - 1;
               }
               if (newRec <= 0) {
                   newRec = 0;
               }
               // //Go To Record
               var params = {};
               params.tracker = null;
               params.container = container;
               params.recNo = newRec;
               this.goToRecord(params);
               // //Appy Scope
               this.applyScope();
           }
       }
       try {
           apz.app.postRemoveRows(container);
       } catch (err) {}
   },
   deleteRecord: function(nodeId, rec) {
    if (rec >= 0) {
        var nodeName = this.apz.getNodeName(nodeId);
        var parentNode = this.apz.scrMetaData.nodesMap[nodeId].parent;
        var parentCurrRec = -1;
        if (this.apz.isNull(parentNode)) {
            parentCurrRec = 0;
        } else {
            parentCurrRec = this.apz.scrMetaData.nodesMap[parentNode].currRec;
        }
        var lparentpointer = this.getDataPointer(parentNode, parentCurrRec);
        if (this.apz.scrMetaData.nodesMap[nodeId].relType == "1:N") {
            // //Delete the Record..
            if (lparentpointer[nodeName]) {
                lparentpointer[nodeName].splice(rec, 1);
            }
        } else {
            lparentpointer[nodeName] = null;
        }
    }
   },
   appendnestList : function(container, currRec) {
    var containerId = container + "_row_" + currRec;
    if (typeof(this.apz.scrMetaData.containersMap[container].childs) != "undefined" && this.apz.scrMetaData.containersMap[container].childs.length > 0) {
        var parentNode = document.getElementById(containerId);
        var childLen = this.apz.scrMetaData.containersMap[container].childs.length;
        var containerRecLen = this.apz.scrMetaData.containersMap[container].totalRecs
        for (var i = 0; i < childLen; i++) {
            for (var x = 0; x < containerRecLen; x++) {
                var containterIds = container + "_row_" + x;
                if (this.apz.scrMetaData.containersMap[container].currRow == x) {
                    var id = this.apz.scrMetaData.containersMap[container].childs[i] + "_list_div";
                    apzJquery("#" + containterIds + " " + "#" + id).removeClass('sno');
                } else {
                    var id = this.apz.scrMetaData.containersMap[container].childs[i] + "_list_div";
                    apzJquery("#" + containterIds + " " + "#" + id).addClass('sno');
                    apzJquery("#" + containterIds + " " + "#" + id).removeClass('childclass_0');
                }
            }
        }
     }
   },
   initRow: function() {
    /// Yet to implement
   },
   buildData: function(ifaceId) {
      ////Extract for Specified interface
      if (!this.apz.isNull(ifaceId)) {
         var ifaceName = this.apz.getIfaceName(ifaceId);
		 var ifaceType = this.apz.getIfaceType(ifaceId)
         var ifaceData = {};
		 var len = ifaceId.length;
		 var end4=ifaceId.substr(len - 4);
	     if(ifaceType == "DATABASE"){
			 ifaceData[ifaceName+"_Req"] = this.scrdata[ifaceName+"_Req"];
		 } else if ((end4 == "_Req") || (end4 == "_Res")) {
			  ifaceData[ifaceId] = this.scrdata[ifaceId];
		 } else {
			 ifaceData[ifaceId+"_Req"] = this.scrdata[ifaceId+"_Req"];
			 ifaceData[ifaceId+"_Res"] = this.scrdata[ifaceId+"_Res"];
		 }
         ifaceData = this.apz.copyJSONObject(ifaceData);
         this.apz.scrinterfaceData = ifaceData;
         return ifaceData;
      }
   },
   loadScrContent : function(type, deviceType, scrFilePath, scrId, loId, div, animation) {
		var params = {};
		params.path = scrFilePath;
		params.async = false;
		params.id = "DEVICEGROUP";
		params.callBack = null;
		content = this.apz.getFile(params);
		angular.element(document).injector().invoke(function($compile) {
		   var divObj = apzJquery("#" + div);
		   divObj.html(apzJquery(content));
		   divObj = apzJquery("#" + div);
		   //uibase.init();
		   // compile!!!
		   $compile(divObj.contents())(this.angularAppScope);
		   loader.adjustPrevPage(type, scrId, loId, div, animation);
		   this.apz.changeStyleTheme();
		   loader.loadScripts(type, scrId, loId, div, animation, loader.jsfiles);
		   this.angularAppScope.data = this.scrdata;
		});
    },
	getObjRowNumber : function(obj){
		var rowNo = obj.getAttribute('rowno');
		if (this.apz.isNull(rowNo)) {
		  rowNo = -1;
		}
		try {
		  rowNo = parseInt(rowNo);
		} catch (err) {
		  rowNo = -1;
		}
		return rowNo;
	}, getNewObject : function() {
		return {};
	}, loadJsonData : function(fileName, appId) {
      var filePath = this.apz.getDataFilesPath(appId) + "/" + fileName + ".json";
      var content = this.apz.getFile(filePath);
      if (!this.apz.isNull(content)) {
         content = JSON.parse(content);
         this.appendData(content);
         this.loadData(null);
      }
   }, appendData : function(newData) {
      for (key in newData) {
         this.scrdata[key] = newData[key];
      }
   }, unselectRow : function(containerId, rowNo) {
      rowNo = this.getRow(containerId, rowNo);
      var id = containerId + "_selcb_" + rowNo;
      var obj = document.getElementById(id);
      if (!this.apz.isNull(obj)) {
         apzJquery('#' + id)[0].checked = false;
      } else {
         id = containerId + '_row_' + rowNo;
         try {
            className = apzJquery('#' + id)[0].className;
            if (className.indexOf('selected') != -1) {
               apzJquery('#' + id).removeClass('selected');
            }
         } catch (err) {
         }
      }
   }, selectRow : function(currObj, event) {
    if(event.ctrlKey || event.metaKey) {
      apzJquery(currObj).toggleClass('selected');
      // throw 'This should be an uncaught exception.';
    }
   }
}
