function BarcodePluginClass() {
    console.log("BarcodePluginClass.js: is created");
}
BarcodePluginClass.prototype.scan = function (successCallback, errorCallback, requestParams) {

    var args = [];

    Bridge.exec(successCallback, errorCallback, "Barcode", "barcode", args);
}
BarcodePluginClass.prototype.scanA4 = function (successCallback, errorCallback, requestParams) {

    var args = [];

    Bridge.exec(successCallback, errorCallback, "Barcode", "A4", args);
}
BarcodePluginClass.prototype.printBarcode = function (successCallback, errorCallback, requestParams) {

    var args = [requestParams.scannedData];

    Bridge.exec(successCallback, errorCallback, "Barcode", "printBarcode", args);
}
var BarcodePlugin = new BarcodePluginClass();
