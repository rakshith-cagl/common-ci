var fileBrowserJsonObject;
WinContainer.filebrowser = function (json) {
    fileBrowserJsonObject = json;
    var openPicker = new Windows.Storage.Pickers.FileOpenPicker();
    //appzillon.plugin.sendAuditLog("FileBrowser","START");
    //appzillon.plugin.storeLog("FileBrowser Initialized", "D");
    var filterType = '';
    openPicker.fileTypeFilter.replaceAll(["*"]);
    if (json.filter) {
        filterType = json.filter.split(',');
        openPicker.fileTypeFilter.replaceAll(filterType);
    }
    if (json.fileCategory) {
        var fileCategory = json.fileCategory.toUpperCase();
        if (fileCategory == "PHOTO") {
            openPicker.fileTypeFilter.replaceAll([".png", ".jpg", ".jpeg"]);
        }
        else if (fileCategory == "AUDIO") {
            openPicker.fileTypeFilter.replaceAll([".mp3", ".amr"]);
        }
        else if (fileCategory == "VIDEO") {
            openPicker.fileTypeFilter.replaceAll([".mp4", ".mpg", ".mpeg"]);
        }
    }
    openPicker.pickSingleFileAndContinue();
}
fileOpenPickerCallback = function (eventObject) {
    var save = fileBrowserJsonObject.save == null ? false : fileBrowserJsonObject.save.toUpperCase() == "Y" ? true : false;
    var files = eventObject.detail.files;
    var fileItem = files.size > 0 ? files[0] : null;
    var id = eventObject.id;
    var folderName = null;
    var filePath = null;
    if (!save) {
        folderName = "temp\\filebrowser";
        filePath = "temp\\filebrowser\\";
    }
    else {
        folderName = "filebrowser";
        filePath = "ms-appdata:///local\\filebrowser\\";
    }
    if (fileItem) {

        if (fileBrowserJsonObject.openFile == "Y") {
            Windows.System.Launcher.launchFileAsync(fileItem).then(function (s) {
              //  appzillon.plugin.sendAuditLog("FileBrowser", "END");
              //  appzillon.plugin.storeLog("File Opened", "D");
            },
               function (e) {
                   if (auditStartEntry)
                   //    appzillon.plugin.sendAuditLog("FileBrowser", "END");
                //   appzillon.plugin.storeLog(e.description, "E");
                   //  var data = JSON.stringify({ errorCode: "APZ-CNT-082" }, null, " ");
                   //  var json = JSON.parse(data);
                       WinContainer.failureCallback(id, "APZ-CNT-009");
               });
        }
        else {
            var temp = Windows.Storage.ApplicationData.current.localFolder;
            temp.createFolderAsync(folderName, Windows.Storage.CreationCollisionOption.replaceExisting)
                .then(function (folder) {
                    fileItem.copyAsync(folder).then(function (s) {

                        //appzillon.plugin.sendAuditLog("FileBrowser", "END");
                       // appzillon.plugin.storeLog("File Selected", "D");
                         var data = JSON.stringify({ "filePath": filePath + fileItem.name }, null, " ");
                         var json = JSON.parse(data);
						 json.text = json.filePath;
                         json.id = fileBrowserJsonObject.id;
                        WinContainer.successCallback(json);

                    }, function (e) {
                       // appzillon.plugin.sendAuditLog("FileBrowser", "END");
                      //  appzillon.plugin.storeLog("File Selected", "D");
                        // var data = JSON.stringify({ "filePath": filePath + fileItem.name }, null, " ");
                        //  var json = JSON.parse(data);
                        WinContainer.failureCallback(id, "APZ-CNT-227");

                    });


                }, function (e) {
                    if (auditStartEntry)
                       // appzillon.plugin.sendAuditLog("FileBrowser", "END");
                  //  appzillon.plugin.storeLog(e.description, "E");
                    //  var data = JSON.stringify({ errorCode: "APZ-CNT-082" }, null, " ");
                    //var json = JSON.parse(data);
                        WinContainer.failureCallback(id, "APZ-CNT-227");

                });
        }
    }

}