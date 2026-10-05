function FingerScanPluginClass() {
    console.log("FingerScanPluginClass.js: is created");
}
FingerScanPluginClass.prototype.scan = function (successCallback, errorCallback, requestParams) {

    var args = [];

    Bridge.exec(successCallback, errorCallback, "FingurePrint", "scan", args);
}
FingerScanPluginClass.prototype.verify = function (successCallback, errorCallback, requestParams) {

    var args = [requestParams.filePath];

    Bridge.exec(successCallback, errorCallback, "FingurePrint", "verify", args);
}
var FingerScanPlugin = new FingerScanPluginClass();
