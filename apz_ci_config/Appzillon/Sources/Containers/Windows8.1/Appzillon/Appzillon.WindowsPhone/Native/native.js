appzillon={};
appzillon.plugin={};
//appzillon.plugin.showSplash = function () {

    //var splashdiv = document.getElementById("splashDiv");
   // splashdiv.style.display = "block";

//}
event_id = null;

appzillon.plugin.initDeviceSettings = function () {
	var os = Windows.Security.ExchangeActiveSyncProvisioning.EasClientDeviceInformation().operatingSystem.toUpperCase();
}
appzillon.plugin.uniqueDeviceId = function () {  
    //var easID = new Windows.Security.ExchangeActiveSyncProvisioning.EasClientDeviceInformation();
    return WinContainer.deviceId();
}


appzillon.plugin.biometricAuth = function (json) {
    appzillon.util.displayMessage("APZ-CNT-022",null);
}
appzillon.plugin.validateVibrate = function validatevibrate(jsonobject) {
    var lcheck = false;
    var ltimepresent = appzillon.util.containsKey(jsonobject, "time");

    if (!ltimepresent) {
        appzillon.util.displayMessage("APZ-CNT-185", null);
    } else {

        var ltimechecknull = false;
        var ltimecheckval = false;
        if (!appzillon.util.isNull(jsonobject.time)) {
            ltimechecknull = true;
            if (!(isNaN(jsonobject.time) || jsonobject.time === Infinity || jsonobject.time === "Infinity" || jsonobject.time <= 0)) {
                ltimecheckval = true;
            }
        }

        if (!ltimechecknull) {
            appzillon.util.displayMessage("APZ-CNT-185", null);
        } else if (!ltimecheckval) {
            appzillon.util.displayMessage("APZ-CNT-132", null);
        } else {
            lcheck = true;
        }
    }

    return lcheck;
};
appzillon.plugin.onOrientationChange = function (e) {
    try{
	switch (e.orientation) {
        case Windows.Devices.Sensors.SimpleOrientation.notRotated:
         //   appzillon.plugin.changeCameraRotation("PU");
          //  appzillon.util.onOrientationChange("portraitlo");
            break;
        case Windows.Devices.Sensors.SimpleOrientation.rotated90DegreesCounterclockwise:
         //   appzillon.plugin.changeCameraRotation("LU");
          //  appzillon.util.onOrientationChange("landscapelo");
            
            break;
        case Windows.Devices.Sensors.SimpleOrientation.rotated180DegreesCounterclockwise:
            var ort = Windows.Graphics.Display.DisplayInformation.getForCurrentView().currentOrientation;
       //     appzillon.plugin.changeCameraRotation("PL");
        //    appzillon.util.onOrientationChange("portraitlo");
            break;
        case Windows.Devices.Sensors.SimpleOrientation.rotated270DegreesCounterclockwise:
        //    appzillon.plugin.changeCameraRotation("LL");
         //   appzillon.util.onOrientationChange("landscapelo");
            break;
        case Windows.Devices.Sensors.SimpleOrientation.faceup:
            break;
        case Windows.Devices.Sensors.SimpleOrientation.facedown:
            break;
        default:
            break;
    }
	}catch(e){
	}
}
try {

    var orientationSensor = Windows.Devices.Sensors.SimpleOrientationSensor.getDefault();
    orientationSensor.addEventListener("orientationchanged", appzillon.plugin.onOrientationChange);
}
catch (e) {
}
appzillon.plugin.call = function (num) {
 
                              appzillon.plugin.sendAuditLog("Call","START");
                           
    Windows.ApplicationModel.Calls.PhoneCallManager.showPhoneCallUI(num, "");
        appzillon.plugin.sendAuditLog("Call","END");
}

appzillon.plugin.smsSend = function (json) {
if (json.type == "BG") {
        appzillon.util.displayMessage('APZ-CNT-245', '', null);
        return;
    }
      appzillon.plugin.sendAuditLog("SMS","START");
    var chat = Windows.ApplicationModel.Chat;
    var sms = new chat.ChatMessage();
    sms.body = json.message;
    sms.recipients.append(json.phoneNo);
    chat.ChatMessageManager.showComposeSmsMessageAsync(sms).done(function(){
         appzillon.plugin.sendAuditLog("SMS","END");
});
       
}
WinContainer.smsReceive = function (pNumber, pText) {
    appzillon.util.displayMessage('APZ-CNT-022', '', null);
}
WinContainer.onBackPressed = function () {
    try {
        var j = {};
        j.id = event_id;
        j.keepAlive = true;
        j.event = "backButton";
        WinContainer.successCallback(j);
    }catch(e){}
}
WinContainer.plugin.controlEvent = function (json) {
    event_id = json.id;
    var backButton = Windows.Phone.UI.Input.HardwareButtons;
    if (json.allEvents == 'on') {
        backButton.onbackpressed = WinContainer.onBackPressed;
    }
    else {
        if (json.backButtonEvent=='on') 
            backButton.onbackpressed = WinContainer.onBackPressed;
        else
            backButton.onbackpressed = null;
    }    
}



appzillon.plugin.voice = function (json) {
    if (!appzillon.plugin.validateCallback(json))
        return;
    try {
        var speechRecognizer = new Windows.Media.SpeechRecognition.SpeechRecognizer();
        appzillon.plugin.sendAuditLog("VoiceSupport", "START");
        speechRecognizer.compileConstraintsAsync().done(function () {
            speechRecognizer.recognizeWithUIAsync().done(function (result) {
                if (result.text) {
                    speechRecognizer.close();
                    // appzillon.plugin.sendAuditLog("VoiceSupport", "END");
                    json.text = result.text;
                    WinContainer.successCallback(json);
                }
            }, function (error) {
                appzillon.plugin.storeLog(error.description, "E");
                speechRecognizer.close();
             //   var data = JSON.stringify({ errorCode: "APZ-CNT-082" }, null, " ");
              //  var json = JSON.parse(data);
                WinContainer.failureCallback(json.id, "APZ-CNT-082");
            });

        }, function (error) {
            appzillon.plugin.storeLog(error.description, "E");
            WinContainer.failureCallback(json.id, "APZ-CNT-082");
        });
    }
    catch (e) {
        appzillon.plugin.storeLog(e.description, "E");
        WinContainer.failureCallback(json.id, "APZ-CNT-082");
    }
}

/*var fileBrowserJsonObject;
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
    openPicker.pickSingleFileAndContinue().then(function (eventObject) {
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
                    appzillon.plugin.sendAuditLog("FileBrowser", "END");
                    appzillon.plugin.storeLog("File Opened", "D");
                },
                   function (e) {
                       if (auditStartEntry)
                           appzillon.plugin.sendAuditLog("FileBrowser", "END");
                       appzillon.plugin.storeLog(e.description, "E");
                       //  var data = JSON.stringify({ errorCode: "APZ-CNT-082" }, null, " ");
                       //  var json = JSON.parse(data);
                       WinContainer.failureCallback(id, ErrorCode.NO_FILE_SELECTED);
                   });
            }
            else {
                var temp = Windows.Storage.ApplicationData.current.localFolder;
                temp.createFolderAsync(folderName, Windows.Storage.CreationCollisionOption.openIfExists)
                    .then(function (folder) {
                        fileItem.copyAsync(folder).then(function (s) {

                            appzillon.plugin.sendAuditLog("FileBrowser", "END");
                            appzillon.plugin.storeLog("File Selected", "D");
                            // var data = JSON.stringify({ "filePath": filePath + fileItem.name }, null, " ");
                            //   var json = JSON.parse(data);
                            WinContainer.failureCallback(id, ErrorCode.FILE_BROWSER_FAIL);
                      
                        }, function (e) {
                            appzillon.plugin.sendAuditLog("FileBrowser", "END");
                            appzillon.plugin.storeLog("File Selected", "D");
                            // var data = JSON.stringify({ "filePath": filePath + fileItem.name }, null, " ");
                            //  var json = JSON.parse(data);
                            WinContainer.failureCallback(id, ErrorCode.FILE_BROWSER_FAIL);
      
                        });


                    }, function (e) {
                        if (auditStartEntry)
                            appzillon.plugin.sendAuditLog("FileBrowser", "END");
                        appzillon.plugin.storeLog(e.description, "E");
                        //  var data = JSON.stringify({ errorCode: "APZ-CNT-082" }, null, " ");
                        //var json = JSON.parse(data);
                        WinContainer.failureCallback(id, ErrorCode.FILE_BROWSER_FAIL);
                 
                    });
            }
        }

    });
}*/