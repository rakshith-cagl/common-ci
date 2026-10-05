function FileOperationPluginClass() {
    console.log("FileOperationPluginClass.js: is created");
}


FileOperationPluginClass.prototype.fileOps = function(successCallback, errorCallback, jargs) {
    var  operationType= jargs.operationType;
    var      filePath = jargs.filePath;
    var     fileContent = jargs.fileContent;
    

    var Largs = [operationType, filePath, fileContent];


    Bridge.exec(successCallback, errorCallback, "FileOpsPlugin", "fileOps", Largs);
}

FileOperationPluginClass.prototype.encryptFile = function(successCallback, errorCallback, jargs) {

    var filePath = jargs.fileName;
    var inputFilePath = jargs.inputFilePath;
    var outputFilePath = jargs.outputFilePath;

    var Largs = [filePath, inputFilePath,outputFilePath];



    Bridge.exec(successCallback, errorCallback, "FileOpsPlugin", "encryptFile", Largs);
}

FileOperationPluginClass.prototype.decryptFile = function(successCallback, errorCallback, jargs) {
    var fileName = jargs.fileName;
    var filePath = jargs.filePath;
    var outputFilePath = jargs.outputFilePath;

    var Largs = [fileName,filePath, outputFilePath];



    Bridge.exec(successCallback, errorCallback, "FileOpsPlugin", "decryptFile", Largs);
}

FileOperationPluginClass.prototype.compress = function (successCallback, errorCallback, dbConfigJSON) {
    var operationType = dbConfigJSON.operationType;
    var outputFileName = dbConfigJSON.outputFileName;
    var inputFilesWithDelimeter = dbConfigJSON.inputFilesWithDelimeter;
    var Largs = [operationType,outputFileName,inputFilesWithDelimeter];
    Bridge.exec(successCallback, errorCallback, "FileOpsPlugin", "compress", Largs);
}
FileOperationPluginClass.prototype.extract = function (successCallback, errorCallback, dbConfigJSON) {
    var operationType = dbConfigJSON.operationType;
    var inputFileName = dbConfigJSON.inputFileName;
    var inputFilesWithDelimeter = dbConfigJSON.inputFilesWithDelimeter;


    var Largs = [operationType,inputFileName,inputFilesWithDelimeter];
    Bridge.exec(successCallback, errorCallback, "FileOpsPlugin", "extract", Largs);
}
var FileOperationPlugin = new FileOperationPluginClass();
