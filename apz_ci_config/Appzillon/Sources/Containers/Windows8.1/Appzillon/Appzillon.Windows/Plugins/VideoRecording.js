WinContainer.plugin.videoRecording = function (jsonObj) {
    WinContainer.removeAlertUI();
    try {
        var captureUI = Windows.Media.Capture.CameraCaptureUI();
       // appzillon.plugin.sendAuditLog("Camera", "START");
        var writeOp = jsonObj.overwrite == 'Y' ? Windows.Storage.NameCollisionOption.replaceExisting : Windows.Storage.NameCollisionOption.generateUniqueName;

        captureUI.captureFileAsync(Windows.Media.Capture.CameraCaptureUIMode.video).then(function (capturedItem) {
            if (capturedItem) {
                var localFolder = Windows.Storage.ApplicationData.current.localFolder.getFolderAsync("video").then(function (folder) {
                    capturedItem.copyAsync(folder,jsonObj.fileName, writeOp).then(function (fileDone) {
                        var json = { filePath: 'video/' + jsonObj.fileName };
                        json.id = jsonObj.id;
                        WinContainer.successCallback(json);
                    }, function (e) {
                        var json = { errorCode: 'APZ-CNT-199', errorDescription: 'Video error' };
                        WinContainer.failureCallback(j.id, 'APZ-CNT-199');
                        WinContainer.Log.error(e.description);
                    });
                });
                
            }
        });
    } catch (e) {
        var json = { errorCode: 'APZ-CNT-199', errorDescription: 'Video error' };
        WinContainer.failureCallback(j.id, 'APZ-CNT-199');
        WinContainer.Log.error(e.description);
    }
}