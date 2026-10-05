Apz.Iso = function(apz) {
   ////Core Instance
   this.apz = apz;
   this.blockData = [];
};
Apz.Iso.prototype = {
   getISO8583Date10 : function(pdate) {
      var now = pdate;
      if (this.apz.isNull(now)) {
         now = new Date();
      }
      var ldate = "";
      var hours = now.getHours();
      var minutes = now.getMinutes();
      var seconds = now.getSeconds();
      var dd = now.getDate();
      var month = now.getMonth();
      if (month < 10) {
         month = "0" + month;
      }
      if (hours < 10) {
         hours = "0" + hours;
      }
      if (minutes < 10) {
         minutes = "0" + minutes;
      }
      if (seconds < 10) {
         seconds = "0" + seconds;
      }
      if (dd < 10) {
         dd = "0" + dd;
      }
      ldate = "" + month + dd + hours + minutes + seconds;
      return ldate;
   }, getISO8583Time6 : function(pdate) {
      var now = pdate;
      if (this.apz.isNull(now)) {
         now = new Date();
      }
      var ldate = "";
      var hours = now.getHours();
      var minutes = now.getMinutes();
      var seconds = now.getSeconds();
      var dd = now.getDate();
      var month = now.getMonth();
      if (month < 10) {
         month = "0" + month;
      }
      if (hours < 10) {
         hours = "0" + hours;
      }
      if (minutes < 10) {
         minutes = "0" + minutes;
      }
      if (seconds < 10) {
         seconds = "0" + seconds;
      }
      if (dd < 10) {
         dd = "0" + dd;
      }
      ldate = "" + hours + minutes + seconds;
      return ldate;
   }, getISO8583Date4 : function(pdate) {
      var now = pdate;
      if (this.apz.isNull(now)) {
         now = new Date();
      }
      var ldate = "";
      var hours = now.getHours();
      var minutes = now.getMinutes();
      var seconds = now.getSeconds();
      var dd = now.getDate();
      var month = now.getMonth();
      if (month < 10) {
         month = "0" + month;
      }
      if (hours < 10) {
         hours = "0" + hours;
      }
      if (minutes < 10) {
         minutes = "0" + minutes;
      }
      if (seconds < 10) {
         seconds = "0" + seconds;
      }
      if (dd < 10) {
         dd = "0" + dd;
      }
      ldate = "" + month + dd;
      return ldate;
   }, lpad : function(pstr, plen, ppadchar) {
      var lstr = "";
      if ( typeof pstr === 'string') {
         lstr = pstr;
      } else {
         lstr = "" + pstr;
      }
      while (lstr.length < plen)
      lstr = ppadchar + lstr;
      return lstr;
   }, rpad : function(pstr, plen, ppadchar) {
      var lstr = "";
      if ( typeof pstr === 'string') {
         lstr = pstr;
      } else {
         lstr = "" + pstr;
      }
      while (lstr.length < plen)
      lstr = lstr + ppadchar;
      return lstr;
   }, getCents : function(pnum) {
      var lnum = Number(pnum);
      lnum = lnum * 100;
      return lnum;
   }, convertNumber : function(pnum) {
      var lnum = "";
      if ((this.apz.isNull(pnum)) && (pnum != 0)) {
         lnum = pnum;
         lnum = Number(lnum);
         lnum = lnum / 100;
      } else {
         lnum = 0;
      }
      return lnum;
   }, rtrim : function(pstr, pchar) {
      var lval = pstr;
      return lval;
   }, ltrim : function(pstr, pchar) {
      var lval = pstr;
      return lval;
   }, convertISO8583Date10 : function(pisoval, ptofmt) {
      var lval = pisoval;
      lval = Date.parseExact(pisoval, "MMddhhmmss").toString(ptofmt);
      return lval;
   }, convertISO8583Time6 : function(pisoval, ptofmt) {
      var lval = pisoval;
      lval = Date.parseExact(pisoval, "hhmmss").toString(ptofmt);
      return lval;
   }, convertISO8583Date4 : function(pisoval, ptofmt) {
      var lval = pisoval;
      lval = Date.parseExact(pisoval, "MMdd").toString(ptofmt);
      return lval;
   }, convertRequest : function(params) {
      ////RADSCB
      var pifacename = params.apzIfaceName;
      var ppayload = params.req;
      var lreqroot = this.apz.getReqRoot(pifacename);
      var lifacename = pifacename;
      var lproceed = true;
      try {
         lproceed = this.preConvertRequest();
         if (this.apz.isNull(ppayload)) {
            lproceed = true;
         }
      } catch (err) {
         lproceed = true;
      }
      if (lproceed) {
         this.blockData = new Array();
         var ltagkey = "";
         var childnode = null;
         for (var lnode in ppayload) {
            if ((lnode != null) && (lnode != undefined) && (lnode != "undefined")) {
               childnode = ppayload[lnode];
               this.convertNode(lifacename, childnode, lnode, params.appId);
            }
         }
         // //Loop Thru Nodes and Populate Fields Accordingly..
         var ifaceObj = this.apz.getIfaceObj(lifacename, params.appId)
         var lnoofnodes = ifaceObj.nodes.length;
         for (var n = 0; n < lnoofnodes; n++) {
            var lnodedata = ifaceObj.nodes[n];
            var lnodename = lnodedata.name;
            if ((!this.apz.isNull(lnodename)) && (lnodedata.dml == "REQ")) {
               // If isotags is not null then add to tags
               var lsotagsstr = lnodedata.isotags;
               if (!this.apz.isNull(lsotagsstr)) {
                  var ldata = this.blockData[lnodename];
                  if (!this.apz.isNull(ldata)) {
                     var ltags = lsotagsstr.split(",");
                     var ltag = "";
                     if (ltags.length > 0) {
                        for (var t = 0; t < ltags.length; t++) {
                           ltag = ltags[t];
                           var lelmid = this.apz.getElmId(lnodedata.id, ltag);
                           var lelmdata = lnodedata.elmsmap[lelmid];
                           if (ldata.length > 0) {
                              var ltaglentype = "F";
                              try {
                                 ltaglentype = lelmdata.lentyp;
                              } catch (er) {
                                 ltaglentype = "V";
                              }
                              if (ltaglentype == "V") {
                                 var lmaxlength = 0;
                                 if (!this.apz.isNull(lelmdata)) {
                                    if (!this.apz.isNull(lelmdata.maxlen)) {
                                       parseInt(lelmdata.maxlen);
                                    }
                                 }
                                 if (lmaxlength.toString() == "NaN") {
                                    lmaxlength = 999;
                                 }
                                 if (lmaxlength > 99) {
                                    ppayload[lreqroot][ltag] = ldata.substring(0, 999);
                                    ldata = ldata.substr(999);
                                 } else {
                                    ppayload[lreqroot][ltag] = ldata.substring(0, 99);
                                    ldata = ldata.substr(99);
                                 }
                              } else if (ltaglentype == "F") {
                                 var len = 0;
                                 try {
                                    len = parseInt(lelmdata.minlen);
                                    if (len.toString() == "NaN") {
                                       len = 0;
                                    }
                                 } catch (er) {
                                    len = 0;
                                 }
                                 if ((len == null) || (len == "") || (len == "undefined") || (len == undefined)) {
                                    len = 0;
                                 }
                                 ppayload[lreqroot][ltag] = ldata.substring(0, len);
                                 ldata = ldata.substr(len);
                              }
                           } else {
                              ppayload[lreqroot][ltag] = "";
                           }
                           //this.blockData[node] = ldata;
                           ////RADSCB
                        }
                     }
                  }
                  ////Delete From Payload
                  delete
                  ppayload[lreqroot][lnodename];
               }
            }
         }
      }
      ////RADSCB
      try
      {
         this.postConvertRequest();
      } catch (err) {
      }
   }, convertNode : function(pifacename, pnode, pname, appId) {
      var lreqroot = this.apz.getReqRoot(pifacename);
      var params = {};
      params.iface = pifacename;
      params.dml = "REQ";
      params.node = pname;
      var lnodeid = this.apz.getNodeId(params);
      var lnodedata = this.apz.getIfaceObj(pifacename, appId).nodesMap[lnodeid];
      var ltype = this.apz.getDataType(pnode);
      if ((ltype == "Object") || (ltype == "Array")) {
         if (ltype == "Object") {
            var lnoofelms = lnodedata.elms.length;
            if (lnoofelms > 0) {
               if (pname == lreqroot) {
                  ////Convert Only Cents
                  for (var i = 0; i < lnoofelms; i++) {
                     var lelmdata = lnodedata.elms[i];
                     if (lelmdata.dtyp == "NUMBER" || lelmdata.dtyp == "INTEGER") {
                        if (lelmdata.cents == "Y") {
                           var lval = this.getCents(pnode[lelmname]);
                           pnode[lelmdata.name] = lval.toString();
                        }
                     }
                  }
               } else {
                  if (!this.blockData[pname]) {
                     this.blockData[pname] = ""
                  }
                  for (var i = 0; i < lnoofelms; i++) {
                     var lelmdata = lnodedata.elms[i];
                     this.blockData[pname] = this.blockData[pname] + this.padField(lelmdata, pnode[lelmdata.name], appId);
                  }
               }
            }
         }
         var childnode = null;
         for (var lnode in pnode) {
            if ((lnode != null) && (lnode != undefined) && (lnode != "undefined")) {
               childnode = pnode[lnode];
               ltype = this.apz.getDataType(childnode);
               if (ltype == "Array") {
                  var llen = childnode.length;
                  var gchildnnode = null;
                  var lgchildtype = "STRING";
                  if (llen > 0) {
                     for (var i = 0; i < llen; i++) {
                        gchildnnode = childnode[i];
                        lgchildtype = this.apz.getDataType(childnode[i]);
                        if (lgchildtype == "Object") {
                           this.convertNode(pifacename, gchildnnode, lnode, appId);
                        }
                     }
                  }
               } else if (ltype == "Object") {
                  this.convertNode(pifacename, childnode, lnode, appId);
               }
            }
         }
      }
   },
   // /////////////Response Manipulation/////////////////////
   convertResponse : function(params) {
      // //Loop Thru Nodes and Populate Fields Accordingly..
      ////RADSCB
      var pifacename = params.apzIfaceName;
      var ppayload = params.res;
      var lresroot = this.apz.getResRoot(pifacename);
      var lproceed = true;
      try {
         lproceed = this.preConvertResponse();
         if (this.apz.isNull(ppayload)) {
            lproceed = true;
         }
      } catch (err) {
         lproceed = true;
      }
      if (lproceed) {
         var ifaceObj = this.apz.getIfaceObj(pifacename, params.appId);
         var lnoofnodes = ifaceObj.nodes.length;
         for (var n = 0; n < lnoofnodes; n++) {
            var lnodedata = ifaceObj.nodes[n];
            var lnodename = lnodedata.name;
            if ((!this.apz.isNull(lnodename)) && (lnodedata.dml == "RES")) {
               // If isotags is not null then add to tags
               var ldata = "";
               var lnoofelms = lnodedata.elms.length;
               var lsotagsstr = lnodedata.isotags;
               if (!this.apz.isNull(lsotagsstr)) {
                  var ltags = lsotagsstr.split(",");
                  for (var t = 0; t < ltags.length; t++) {
                     ltag = ltags[t];
                     ldata = ldata + ppayload[lresroot][ltag];
                  }
                  //ldata = ldata.substr(3);
                  if (lnoofelms > 0) {
                     if (!this.apz.isNull(ldata)) {
                        var lmultirec = lnodedata.multirec;
                        if (lmultirec == "Y") {
                           ppayload[lresroot][lnodename] = new Array();
                        } else {
                           ppayload[lresroot][lnodename] = {};
                        }
                        var lread = false;
                        if (ldata.length > 0) {
                           lread = true;
                        }
                        while (lread > 0) {
                           var rec = {};
                           for (var e = 0; e < lnoofelms; e++) {
                              var lval = "";
                              var llen = 0;
                              var lminlen = 0;
                              var lmaxlen = 0;
                              var lelmdata = lnodedata.elms[e];
                              var llentype = lelmdata.lentyp;
                              if (llentype == "F") {
                                 llen = parseInt(lelmdata.minlen);
                              } else {
                                 lminlen = parseInt(lelmdata.minlen);
                                 lmaxlen = parseInt(lelmdata.maxlen);
                                 if (lmaxlen == 999) {
                                    llen = parseInt(ldata.substr(0, 3));
                                    ldata = ldata.substr(3);
                                 } else {
                                    llen = parseInt(ldata.substr(0, 2));
                                    ldata = ldata.substr(3);
                                 }
                              }
                              lval = ldata.substr(0, llen);
                              ldata = ldata.substr(llen);
                              lval = this.ripField(lelmdata, lval, params.appId);
                              rec[lelmdata.name] = lval;
                           }
                           if (lmultirec == "Y") {
                              ppayload[lresroot][lnodename][ppayload[lresroot][lnodename].length] = rec;
                              if (ldata.length > 0) {
                                 lread = true;
                              } else {
                                 lread = false;
                              }
                           } else {
                              ppayload[lresroot][lnodename] = rec;
                              lread = false;
                              if (ldata.length > 0) {
                                 ppayload[lresroot][ltag] = ldata;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      try {
         this.postConvertResponse();
      } catch (err) {
      }
      var i = 0;
      i = i + 1;
   }, padField : function(pelmdata, pval, appId) {
      var lval = pval;
      var lelemid = pelmdata.id;
      var llentype = pelmdata.lentyp;
      var llen = pelmdata.minlen;
      var lcent = pelmdata.cents;
      if ((llen == null) || (llen == "") || (llen == "undefined") || (llen == undefined)) {
         llen = 0;
      }
      llen = parseInt(llen);
      var ldatatype = pelmdata.dtyp;
      var lpaddingtype = pelmdata.pad;
      var lpadchar = pelmdata.padchar;
      var lpattern = pelmdata.pattern;
      if (lpadchar == "ZERO") {
         lpadchar = "0";
      } else if (lpadchar == "SPACE") {
         lpadchar = " ";
      }
      if (ldatatype == "STRING") {
         if ((lval == null) || (lval == "") || (lval == "undefined") || (lval == undefined)) {
            lval = "";
         }
         if (lpaddingtype == "L") {
            lval = this.lpad(lval, llen, lpadchar);
         } else if (lpaddingtype == "R") {
            lval = this.rpad(lval, llen, lpadchar);
         }
      } else if (ldatatype == "NUMBER" || ldatatype == "INTEGER") {
         if ((lval == null) || (lval == "") || (lval == "undefined") || (lval == undefined)) {
            lval = 0;
         }
         if (lpaddingtype == "L") {
            lval = this.lpad(lval, llen, lpadchar);
         } else if (lpaddingtype == "R") {
            lval = this.rpad(lval, llen, lpadchar);
         }
         if (lcent == "Y") {
            lval = this.getCents(lval);
         }
         lval = lval.toString();
      } else if ((ldatatype == "DATE") || (ldatatype == "DATETIME")) {
         if ((lval == null) || (lval == "") || (lval == "undefined") || (lval == undefined)) {
            ldate = new Date();
         } else {
            var lserverformt = this.apz.getIfaceObj(pelmdata.ifacename, appId).dateformat;
            try {
               ldate = new Date(Date.parseExact(lval, lserverformt));
            } catch (de) {
               ldate = new Date();
            }
         }
         if (!this.apz.isNull(lpattern)) {
            lval = ldate.toString(lpattern);
         } else {
            if (llen == 10) {
               lval = this.getISO8583Date10(ldate);
            } else if (llen == 6) {
               lval = this.getISO8583Time6(ldate);
            } else if (llen == 4) {
               lval = this.getISO8583Date4(ldate);
            }
         }
      }
      return lval;
   }, ripField : function(pelmdata, pval, appId) {
      var lval = pval;
      var lelemid = pelmdata.id;
      var llentype = pelmdata.lentyp;
      var llen = pelmdata.minlen;
      var lcent = pelmdata.cents;
      if ((llen == null) || (llen == "") || (llen == "undefined") || (llen == undefined)) {
         llen = 0;
      }
      llen = parseInt(llen);
      var ldatatype = pelmdata.dtyp;
      var lpaddingtype = pelmdata.pad;
      var lpadchar = pelmdata.padchar;
      var lpattern = pelmdata.pattern;
      if (ldatatype == "STRING") {
         if ((lval == null) || (lval == "") || (lval == "undefined") || (lval == undefined)) {
            lval = "";
         }
         if (lpaddingtype == "L") {
            lval = this.ltrim(lval, lpadchar);
         } else if (lpaddingtype == "R") {
            lval = this.rtrim(lval, lpadchar);
         }
      } else if (ldatatype == "NUMBER" || ldatatype == "INTEGER") {
         if ((lval == null) || (lval == "") || (lval == "undefined") || (lval == undefined)) {
            lval = 0;
         }
         //lval = parseInt(lval);
         if (lpaddingtype == "L") {
            lval = this.ltrim(lval, lpadchar);
         } else if (lpaddingtype == "R") {
            lval = this.rtrim(lval, lpadchar);
         }
         if (lcent == "Y") {
            lval = this.convertNumber(lval);
         }
      } else if ((ldatatype == "DATE") || (ldatatype == "DATETIME")) {
         var lserverformt = this.apz.getIfaceObj(pelmdata.ifacename, appId).dateformat;
         if ((lval == null) || (lval == "") || (lval == "undefined") || (lval == undefined)) {
            lval = "";
         }
         if (!this.apz.isNull(lval)) {
            if (!this.apz.isNull(lpattern)) {
               try {
                  lval = Date.parseExact(lval, lpattern).toString(lserverformt);
               } catch(e) {
               }
            } else {
               if (llen == 10) {
                  lval = this.convertISO8583Date10(lval, lserverformt);
               } else if (llen == 6) {
                  lval = this.convertISO8583Time6(lval, lserverformt);
               } else if (llen == 4) {
                  lval = this.convertISO8583Date4(lval, lserverformt);
               }
            }
         }
      }
      return lval;
   }
}
