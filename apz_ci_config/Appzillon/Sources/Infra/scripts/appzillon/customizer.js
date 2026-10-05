Apz.Customizer = function (apz) {
  this.apz = apz;
  this.config = {};
  this.configMeta = {};
  this.dragged;
  this.draggedElement;
  this.currScr = this.apz.firstPage;
  this.prevScr = this.apz.firstPage;
  this.currDeviceGroup = '';
  this.currDesign = '';
  this.sDesignerData = {};
  this.sContainerData = {};
  this.isEditMode = false;
  this.isModified = false;
  this.sDDChanged = '';
  this.deviceGroups = [];
  this.appIdBackUp = '';
  this.userId = '';
};
Apz.Customizer.prototype = {
  customize: function (pTemplateData, ptemplateMetaData) {
    var lTempArray = [];
    this.apz.customizer.sContainerData = ptemplateMetaData.Containers[pTemplateData.template];
    if(!this.apz.isNull(this.apz.customizer.sContainerData)){
        for (var i = 0; i < this.apz.customizer.sContainerData.length; i++) {
          $('#' + this.apz.customizer.sContainerData[i].ContainerId).children().addClass('AppRow sno');
        }
    }
    if (!apz.isNull(pTemplateData) && pTemplateData.isModified) {
      var lTemplateList = pTemplateData.containers;
      for (var i = 0; i < lTemplateList.length; i++) {
        var lParentContainer = $('#' + lTemplateList[i].id);
        lTemplateList[i].sequence.forEach(function (item, index) {
          var lChildApp = $('#' + item);
          if (lParentContainer.find(lChildApp)) {
            lChildApp.removeClass('sno');
            lParentContainer.append(lChildApp);
          }
        })
      }
    } else {
      $('.AppRow').removeClass('sno');
    }
  },
  getTemplateCustomizedInfo: function (params) {
    params.id = 'CUSTOMIZERSERVICE';
    params.ifaceName = 'appzillonGetCustomizerDetails';
    params.internal = true;
    params.callBack = this.screenCustomizedInfoCB;
    params.callBackObj = this;
    var req = {};
    req.appzillonGetCustomizerDetailsRequest = {
      'screenId': params.scr,
      'appId': params.appId,
      'layoutId': params.lo
    };
    req.appzillonGetCustomizerDetailsRequest.templateId = params.template ? params.template : '';
    req.appzillonGetCustomizerDetailsRequest.customizer = params.customizer ? params.customizer : 'N';
    params.req = req;
    params.async = true;
    if (Apz.customizerApp) {
      this.appIdBackUp = this.apz.appId;
      this.apz.appId = 'Admin';
      this.apz.userId = this.userId;
      this.apz.mockServer = false;
    }else {
      if(this.apz.mockServer){
        return false;
      }
    }
    this.apz.server.sendReq(params);
  },
  screenCustomizedInfoCB: function (params) {
    if (Apz.customizerApp) {
      this.apz.appId = this.appIdBackUp;
      this.apz.mockServer = true;
    }
    if (params.errors) {
      var param = {
        'code': params.errors[0].errorCode
      };
      this.apz.dispMsg(param);
    } else {
      var key = ""
      if (!this.apz.isNull(params.res)) {
        var res = params.res.appzillonGetCustomizerDetailsResponse;
        params.template = res.template ? res.template : params.defaultTemplate;
        key = params.scr + this.apz.idSep + params.lo + this.apz.idSep + params.template;
        res.template = params.template;
        this.config[params.appId][key] = res;
      }
      if(params.launchScreen){
          if(!this.apz.containsKey(this.apz.customizer.configMeta[params.appId], key)){
             var metaInfo = this.apz.getFile(this.apz.getDataFilesPath(params.appId) + "/" + params.scr + "cz.json");
             this.apz.customizer.configMeta[params.appId][key] = JSON.parse(metaInfo);
          }
    	  this.apz.layoutDefLoaded(params);
      }
    }
  }
};