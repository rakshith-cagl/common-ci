function EsignaturePluginClass() {
    console.log("EsignaturePluginClass.js: is created");
}
EsignaturePluginClass.prototype.captureSign = function (successCallback, errorCallback, requestParams) {
      var filePath =requestParams.filePath;
    
    var args = [filePath];

    Bridge.exec(successCallback, errorCallback, "EsignaturePlugin", "captureSign", args);
}

var EsignaturePlugin = new EsignaturePluginClass();
