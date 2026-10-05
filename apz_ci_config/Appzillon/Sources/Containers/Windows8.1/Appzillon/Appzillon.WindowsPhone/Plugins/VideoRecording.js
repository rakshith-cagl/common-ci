//----------------------CameraPlugin.js------------------------------------------------------
var videoCaptureMgr = null;
var videoCameraTag = null;
(function () {
    var captureInitSettings = null;
    function initCameraSettings() {
        var webCam = Windows.Media.Capture;
        
        videoCaptureMgr = new webCam.MediaCapture();
        captureInitSettings = new webCam.MediaCaptureInitializationSettings();
        captureInitSettings.audioDeviceId = "";
        captureInitSettings.videoDeviceId = "";
        captureInitSettings.photoCaptureSource = webCam.PhotoCaptureSource.auto;
        captureInitSettings.streamingCaptureMode = webCam.StreamingCaptureMode.audioAndVideo;

        Windows.Devices.Enumeration.DeviceInformation
            .findAllAsync(Windows.Devices.Enumeration.DeviceClass.videoCapture)
                .then(function (devList) {
                    if (devList.length > 1)
                        captureInitSettings.videoDeviceId = devList[1].id;
                });
    }
    function displayNativeCamera() {
        var intDiv = document.createElement("div");
        intDiv.id = "previewVideo";
        intDiv.style.position = "absolute";
        intDiv.style.top = "0%";
        intDiv.style.left = "0%";
        intDiv.style.zIndex = "99999";
        videoCameraTag = document.createElement("video");
        videoCameraTag.id = "nativeCameraVideo";
        videoCameraTag.style.position = "fixed";
        videoCameraTag.style.top = "0%";
        videoCameraTag.style.left = "0%";
        //videoCameraTag.style.margin = "0px 0px 0px 0px";
        videoCameraTag.style.width = window.innerWidth + 'px';
        videoCameraTag.style.height = window.innerHeight + 'px';
        videoCameraTag.addEventListener("click", startRecording);
        videoCameraTag.src = URL.createObjectURL(videoCaptureMgr, { oneTimeOnly: true });
        videoCameraTag.msZoom = true;
        videoCameraTag.play();
        //var img = document.createElement('img');
        //img.id = 'recordVideo';
        //img.src = 'ms-appx:///images/record.png';
        //img.style.position = 'fixed';
        //img.style.bottom = '0%';
        //img.style.left = '50%';
        //img.style.margin = '0 0 0 -57px';
        //img.style.zIndex = '99999';
        //img.onclick = startRecording;
        //intDiv.appendChild(img);
        intDiv.appendChild(videoCameraTag);
        
        document.body.appendChild(intDiv);
      
         document.body.style.overflowY = 'hidden';
    }
   
    function hideNativeCamera() {
        document.body.style.overflowY = 'visible';
        var videoDiv = document.getElementById("previewVideo");
        videoCameraTag = document.getElementById("nativeCameraVideo");
        videoCameraTag.src = null;
        document.body.removeChild(videoDiv);
        if (videoCameraTag)
            videoCameraTag = null;
        if (videoCaptureMgr)
            try {
                videoCaptureMgr.close();
            } catch (e) {
            }
    }
    function lockOrientaionForVideo() {
        Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.landscape;

    }
    function unlockOrientaionForVideo() {
        var ort = WinContainer.Settings.containerProperties["ortchosen"];
        if (ort == "POR")
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.portrait;
        else if (ort == "LAN")
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.landscape;
        else
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.none;
    }
    function onFocusChanged(args) {

    }
    function onPhotoConfirmationCaptured(args) {

    }
    var recording = false;
    function startRecording() {
        if (recording) {
            stopRecord();
            return;
        }
        
        lowLagRecord.startAsync().done(function (result) {
            recording = true;
        }, function (e) {
            videoCaptureMgr.close();
        });
    }
    function stopRecord() {
        var video = document.getElementById("nativeCameraVideo");
        video.removeEventListener('click', startRecording);
        lowLagRecord.stopAsync().done(function () {
            
                // Playback the recorded video.
                recording = false;
            try {
                video.src = URL.createObjectURL(recordFile, { oneTimeOnly: true });
                var appBar = document.createElement('div');
                appBar.style.position = 'fixed';
                appBar.style.width = '100%';
                appBar.style.zIndex = 99999;
                appBar.style.bottom = '0px';
                appBar.style.textAlign = 'center';
                appBar.style.backgroundColor = "rgba(229,229,229, 0)";
                var btPlay = document.createElement('button');
                btPlay.id = 'vPlay';
                btPlay.textContent = 'Play';
                btPlay.style.width = "30%";
                btPlay.style.fontSize = '20px';
                btPlay.style.margin = '5px';
                btPlay.onclick = function (e) {
                    if ( btPlay.textContent == 'Play') {
                        video.play();
                        document.getElementById('vPlay').textContent = 'Pause';
                    }
                    else {
                        video.pause();
                        document.getElementById('vPlay').textContent = 'Play';
                    }
                    
                }
                
                video.onended = function (e) {
                    document.getElementById('vPlay').textContent = 'Play';
                }
                var btDone = document.createElement('button');
                btDone.id = 'vDone';
                btDone.textContent = 'Done';
                btDone.style.width = "30%";
                btDone.style.fontSize = '20px';
                btDone.onclick = function (e) {
                    unlockOrientaionForVideo();
                    hideNativeCamera();
                    var json = { filePath: 'video/' + recordFile.name.replace('.mp4', '') };
                    json.id = id;
                    WinContainer.successCallback(json);
                };
                appBar.appendChild(btPlay);
                appBar.appendChild(btDone);
                document.getElementById('previewVideo').appendChild(appBar);
                   

                } catch (e) {

                }
                lowLagRecord.finishAsync().done(function () {
                });
        });
        
    }
    var recordFile = null;
    var lowLagRecord = null;
    var jsonObject = null;
    initCameraSettings();
    var id = "";
    WinContainer.plugin.videoRecording = function (j) {
        id = j.id;
        var app1 = WinJS.Application;
        app1.onbackclick = function () {
            try {
                unlockOrientaionForVideo();
                recording = false;
                hideNativeCamera();
            } catch (e) { }
            return true;
        }
        jsonObject = j;
        var writeOp = j.overwrite.toUpperCase() == 'Y' ? Windows.Storage.CreationCollisionOption.replaceExisting : Windows.Storage.CreationCollisionOption.generateUniqueName;
        try {
            videoCaptureMgr.close();
        } catch (e) { }
        var webCam = Windows.Media.Capture;
        videoCaptureMgr = new webCam.MediaCapture();
        videoCaptureMgr.initializeAsync(captureInitSettings).done(function (result) {
            try {
                videoCaptureMgr.addEventListener("recordlimitationexceeded", function (e) {
                    var json = { errorCode: 'APZ-CNT-199', errorDescription: 'Record limitation exceeded' };
                    WinContainer.failureCallback(j.id, 'APZ-CNT-199');
                    try {
                        WinContainer.Log.fatal(e.detail.toString());
                    } catch (e) { }
                });
                videoCaptureMgr.addEventListener("failed", function (e) {
                    var json = { errorCode: 'APZ-CNT-199', errorDescription: 'Camera failed' };
                    WinContainer.failureCallback(j.id, 'APZ-CNT-199');
                    try {
                        WinContainer.Log.fatal(e.detail.toString());
                    } catch (e) { }
                });
                var videoDev = videoCaptureMgr.videoDeviceController;
                try {
                    videoDev.flashControl.auto = true;
                } catch (e) { }
                var resolutions = videoDev.getMediaStreamProperties(Windows.Media.Capture.MediaStreamType.videoPreview);
                lockOrientaionForVideo();
                videoCaptureMgr.setRecordRotation(Windows.Media.Capture.VideoRotation.none);
                videoCaptureMgr.setPreviewRotation(Windows.Media.Capture.VideoRotation.none);
                videoDev.setMediaStreamPropertiesAsync(Windows.Media.Capture.MediaStreamType.videoPreview, resolutions)
                    .then(function () {
                       
                        var recordFormat = Windows.Media.MediaProperties.MediaEncodingProfile.createMp4(Windows.Media.MediaProperties.VideoEncodingQuality.auto);
                        Windows.Storage.ApplicationData.current.localFolder.createFileAsync("video\\"+jsonObject.fileName+'.mp4', writeOp).then(function (newFile) {
                            recordFile = newFile;
                            videoCaptureMgr.prepareLowLagRecordToStorageFileAsync(recordFormat, recordFile).done(function (result) {
                                lowLagRecord = result;
                                displayNativeCamera();
                            });
                        });
                    }, function (e) {
                        videoCaptureMgr.close();
                        var json = { errorCode: 'APZ-CNT-199', errorDescription: 'Camera failed' };
                        WinContainer.failureCallback(j.id, 'APZ-CNT-199');
                        WinContainer.Log.error(e.description);
                    });

            } catch (e) {
                var json = { errorCode: 'APZ-CNT-199', errorDescription: 'Camera failed' };
                WinContainer.failureCallback(j.id, 'APZ-CNT-199');
                WinContainer.Log.error(e.description);
            }
        });

    }

    
})();