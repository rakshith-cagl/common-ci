function FileSyncPluginClass() {
    console.log("FileSyncPluginClass.js: is created");
}


FileSyncPluginClass.prototype.fileSyncUpload = function(successCallback, errorCallback, jargs) {

    var filePath = jargs.filePath;
    var fileName = jargs.fileName;
    var appName  = jargs.appName;

    var Largs = [filePath, fileName,appName];
    Bridge.exec(successCallback, errorCallback, "FileSync", "fileSyncUpload", Largs);
}

FileSyncPluginClass.prototype.fileSyncDownload= function(successCallback, errorCallback, jargs) {

    var filePath = jargs.filePath;
    var appName  = jargs.appName;
    var clientTransactionId=jargs.clientTransactionId;
    var Largs = [filePath,appName,clientTransactionId];
    Bridge.exec(successCallback, errorCallback, "FileSync", "fileSyncDownload", Largs);
}


FileSyncPluginClass.prototype.UpdateFileSyncStatus= function(successCallback, errorCallback, jargs) {

    var appName = jargs.appName;


    var Largs = [appName];
    Bridge.exec(successCallback, errorCallback, "FileSync", "UpdateFileSyncStatus", Largs);
}
FileSyncPluginClass.prototype.fileSyncSendAck= function(successCallback, errorCallback, jargs) {


    var fileNameAck = jargs.fileNameAck;
    var cid = jargs.cid;
    var appName = jargs.appName;


    var Largs = [fileNameAck,cid,appName];

    Bridge.exec(successCallback, errorCallback, "FileSync", "fileSyncSendAck", Largs);

}

var FileSyncPlugin = new FileSyncPluginClass();
