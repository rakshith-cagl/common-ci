function PrinterPluginClass() {
    console.log("PrinterPluginClass.js: is created");
}
PrinterPluginClass.prototype.printtext = function (successCallback, errorCallback, requestParams) {

    var args = [requestParams.text];


    Bridge.exec(successCallback, errorCallback, "Printer", "text", args);
}
PrinterPluginClass.prototype.printimg = function (successCallback, errorCallback, requestParams) {

    var args = [requestParams.text, requestParams.width, requestParams.height,requestParams.path];

    Bridge.exec(successCallback, errorCallback, "Printer", "image", args);
}
PrinterPluginClass.prototype.printbmppathimg = function (successCallback, errorCallback, requestParams) {

    var args = [requestParams.path];

    Bridge.exec(successCallback, errorCallback, "Printer", "path", args);
}
var PrinterPlugin = new PrinterPluginClass();
