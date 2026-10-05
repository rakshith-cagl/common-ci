function UsbPluginClass() {
    console.log("UsbPluginClass.js: is created");
}

UsbPluginClass.prototype.usb_Read = function (successCallback, errorCallback, dbConfigJSON) {
    var outputFileName = dbConfigJSON.outputFileName;
    var inputFilePath = dbConfigJSON.inputFilePath;
    var appName = dbConfigJSON.appName;
    var Largs = [outputFileName,inputFilePath,appName];
    Bridge.exec(successCallback, errorCallback, "UsbPlugin", "usb_Read", Largs);
}
UsbPluginClass.prototype.usb_Write = function (successCallback, errorCallback, dbConfigJSON) {

    var inputFileName= dbConfigJSON.inputFileName;
    var destFilePath = dbConfigJSON.destFilePath;
    var appName = dbConfigJSON.appName;

    var Largs = [inputFileName,destFilePath,appName];
    Bridge.exec(successCallback, errorCallback, "UsbPlugin", "usb_Write", Largs);
}
UsbPluginClass.prototype.usb_Remove = function (successCallback, errorCallback, dbConfigJSON) {
    var filePath = dbConfigJSON.filePath;

    var Largs = [filePath];
    Bridge.exec(successCallback, errorCallback, "UsbPlugin", "usb_Remove", Largs);
}



var UsbPlugin = new UsbPluginClass();
