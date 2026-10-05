appzillon={};
appzillon.plugin={};
appzillon.plugin.showSplash = function () {

    var splashdiv = document.getElementById("splashDiv");
    splashdiv.style.display = "block";

}
//appzillon.plugin.hideSplash = function () {
 //   var splashdiv = document.getElementById("splashDiv");
 //   splashdiv.style.display = "none";
//	document.body.style.overflowY = 'visible';
//	document.body.style.overflowY = 'auto';
//}
appzillon.plugin.uniqueDeviceId = function () {
    //var easID = new Windows.Security.ExchangeActiveSyncProvisioning.EasClientDeviceInformation();
    return appzillon.plugin.deviceId();
}
WinContainer.filebrowser = function (jsonObj) {
    WinContainer.removeAlertUI();
    try {
        var id = jsonObj.id;
        var fileCategory = jsonObj.fileCategory;
        var filter = jsonObj.filter;
        var location = jsonObj.location.toUpperCase();
        var openFile = jsonObj.openFile;
		var save = jsonObj.save == null ? 'N' : jsonObj.save;		
		var folderName;
        var filePath = null;
		
        if (save == 'N') {
            folderName = "temp\\filebrowser";
            filePath = "temp\\filebrowser\\";
        }
        else if(save == 'Y'){
            folderName = "filebrowser";
            filePath = "ms-appdata:///local\\filebrowser\\";
        }

        var filterString = new Array();

        if (filter.split(',').length > 0) {

            var filterParts = filter.split(',');
            var len = filterParts.length;

            for (i = 0; i < len; i++) {

                filterString[i] = filterParts[i];
            }
        }
        else
            filterString[0] = filter;

        // Create the picker object and set options
        var openPicker = new Windows.Storage.Pickers.FileOpenPicker();
        openPicker.viewMode = Windows.Storage.Pickers.PickerViewMode.thumbnail;


        if (fileCategory != null && fileCategory != "") {

            if (fileCategory == "PHOTO") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.picturesLibrary;
                openPicker.fileTypeFilter.replaceAll(["*"]);
                openPicker.fileTypeFilter.replaceAll([".png", ".jpg", ".jpeg"]);
            }
            else if (fileCategory == "AUDIO") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.musicLibrary;
                openPicker.fileTypeFilter.replaceAll(["*"]);
                openPicker.fileTypeFilter.replaceAll([".mp3", ".amr"]);
            }
            else if (fileCategory == "VIDEO") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.videosLibrary;
                openPicker.fileTypeFilter.replaceAll(["*"]);
                openPicker.fileTypeFilter.replaceAll([".mp4", ".mpg", ".mpeg"]);
            }
            else if (fileCategory == "DEFAULT" || fileCategory == "EXTERNAL") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.desktop;
                if (filter != null && filter != "") {
                    openPicker.fileTypeFilter.replaceAll(filterString);
                }
                else {
                    openPicker.fileTypeFilter.replaceAll(["*"]);
                }
                //openPicker.fileTypeFilter.replaceAll([".docx", ".xlsx", ".pptx"]);               
            }

        }
        else if (location != null && location != "") {

            if (location == "DOCUMENT") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.documentsLibrary;
            }
            else if (location == "DOWNLOADS") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.downloads;
            }
            else if (location == "DESKTOP") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.desktop;
            }
            else if (location == "COMPUTER") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.computerFolder;
            }
            else if (location == "PHOTO") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.picturesLibrary;
            }
            else if (location == "AUDIO") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.musicLibrary;
            }
            else if (location == "VIDEO") {
                openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.videosLibrary;
            }

            //openPicker.suggestedStartLocation = location;
            if (filterString[0] == "")
                openPicker.fileTypeFilter.replaceAll(["*"]);
            else
                openPicker.fileTypeFilter.replaceAll(filterString);

        }
        else if (filter != null && filter != "") {
            openPicker.fileTypeFilter.replaceAll(filterString);
        }

        else {
            openPicker.fileTypeFilter.replaceAll(["*"]);
        }
        WinContainer.Log.debug("Filebrowser Opened");

        openPicker.pickSingleFileAsync().then(function (fileItem) {
            if (fileItem) {
                if (openFile == "Y") {
                    WinContainer.Log.debug("File Selected From Filebrowser");
                    var path = fileItem.path;
                    var substrPath = path.substr(0, 8);
                    var localFolderPath = Windows.Storage.ApplicationData.current.localFolder.path;

                    var n = path.search(localFolderPath);

                    if (n != -1) {

                        Windows.System.Launcher.launchFileAsync(fileItem).then(function (response) {
                            WinContainer.Log.info("Filebrowser End");
                        },
                           function (responseFailed) {                       
                               WinContainer.failureCallback(id, ErrorCode.NO_FILE_SELECTED);
                               WinContainer.Log.error(responseFailed.description);
                           });
                    }
                    else {
                        WinContainer.Log.info("Filebrowser End");
                        Windows.System.Launcher.launchFileAsync(fileItem).then(function (response) { },
                            function (responseFailed) {
                                WinContainer.failureCallback(id, ErrorCode.FILE_BROWSER_FAIL);
                                WinContainer.Log.error(responseFailed.description);
                            });
                    }
                }
                else {
                    var temp = Windows.Storage.ApplicationData.current.localFolder;
                    temp.createFolderAsync(folderName, Windows.Storage.CreationCollisionOption.openIfExists)
                        .then(function (folder) {
                            fileItem.copyAsync(folder);
                            WinContainer.Log.info("Filebrowser End");
                           var data = JSON.stringify({ "filePath": filePath + fileItem.name }, null, " ");
                           var json = JSON.parse(data);
						   json.text = json.filePath;
                           json.id = id;
                           WinContainer.successCallback(json);
                       

                        }, function (e) {
                            WinContainer.Log.error(e.description);
                        });
                }
            }
        });
    }
    catch (err) {
        WinContainer.Log.error(err.description);
        WinContainer.failureCallback(id, ErrorCode.FILE_BROWSER_FAIL);
    }
}

appzillon.plugin.onOrientationChange = function (e) {

    switch (e.orientation) {
        case Windows.Devices.Sensors.SimpleOrientation.notRotated:
            appzillon.util.onOrientationChange("landscapelo");
            break;
        case Windows.Devices.Sensors.SimpleOrientation.rotated90DegreesCounterclockwise:
            appzillon.util.onOrientationChange("portraitlo");
            break;
        case Windows.Devices.Sensors.SimpleOrientation.rotated180DegreesCounterclockwise:
            appzillon.util.onOrientationChange("landscapelo");
            break;
        case Windows.Devices.Sensors.SimpleOrientation.rotated270DegreesCounterclockwise:
            appzillon.util.onOrientationChange("portraitlo");
            break;
        case Windows.Devices.Sensors.SimpleOrientation.faceup:
            break;
        case Windows.Devices.Sensors.SimpleOrientation.facedown:
            break;
        default:
            break;
    }
};
try {

    var orientationSensor = Windows.Devices.Sensors.SimpleOrientationSensor.getDefault();
    if(orientationSensor!= null)
    orientationSensor.addEventListener("orientationchanged", appzillon.plugin.onOrientationChange);
}
catch (e) {
}