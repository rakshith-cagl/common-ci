//----------------------CameraPlugin.js------------------------------------------------------
var mediaCaptureMgr = null;
var photoselection = false;
(function () {
    var videoCameraTag = null;
    var nativeCameraOrt = null;
    var isOrtFromCameraStart = false;
    var pluginCameraProperties = null;
    var frontCamera = false;
    var isOrientationChanged = false;
    var compressionLevel = 100;
    var ort_pref;
    var photojson = {};
    WinContainer.plugin.initCameraProperties = function (jsonObj) {
        pluginCameraProperties = {
            htMax: 720,
            wdMax: 1280,
            width: null,
            height: null,
            cropBounds: null,
            zoom: null,
            flash: false,
            flashAuto: false
        };
        try {
            compressionLevel = parseInt(jsonObj.quality);
            compressionLevel = (compressionLevel > 0 && compressionLevel <= 100) ? compressionLevel : 100;
            compressionLevel = 1 - compressionLevel / 100;
        } catch (e) {
            compressionLevel = 0;
        }
      //  try {
      //      var zoom = parseInt(jsonObj.zoom);
      //      zoom ? pluginCameraProperties.zoom = zoom : pluginCameraProperties.zoom = null;
      //  } catch (e) { }
        var isFlash = jsonObj.flash;
        isFlash ? isFlash == 'Y' ? pluginCameraProperties.flash = true : pluginCameraProperties.flash = false : pluginCameraProperties.flasAuto = true;
        try {
            if (jsonObj.crop == "Y") {
                var cX = 0;
                var cY = 0;
                var cWd = jsonObj.targetWidth;
                var cHt = jsonObj.targetHeight;
                var cropBounds = {};
                (cY >= 0 && cY < 1280) ? cropBounds.x = cY : cropBounds.x = 1279;
                (cX >= 0 && cX < 720) ? cropBounds.y = pluginCameraProperties.htMax - (cX + cWd) : cropBounds.y = 719;
                (cHt > 0 && (cHt + cY) < 1280) ? cropBounds.width = cHt : cropBounds.width = 1280 - cY;
                (cWd > 0 && (cWd + cX) < 720) ? cropBounds.height = cWd : cropBounds.height = 720 - cropBounds.y;
                pluginCameraProperties.cropBounds = cropBounds;
            } else {
                pluginCameraProperties.cropBounds = null;
            }
        } catch (e) {
            pluginCameraProperties.cropBounds = null;
        }
    }
    WinContainer.plugin.camera = function (jsonObj) {
        try {
            mediaCaptureMgr.close();
        } catch (e) { }
        photojson = jsonObj;
        if (jsonObj.sourceType == "Photo") {
            var js = {};
            js.filter = ".png,.jpg,.jpeg,.bmp";
            js.fileCategory = "PHOTO";
            photoselection = true;
            WinContainer.plugin.initCameraProperties(jsonObj);
            WinContainer.filebrowser(js);
            return;
        }
        var webCam = Windows.Media.Capture;
        mediaCaptureMgr = new webCam.MediaCapture();
        compressionLevel = parseInt(jsonObj.compressionLevel);
        compressionLevel = (compressionLevel > 0 && compressionLevel <= 100) ? compressionLevel : 100;
        frontCamera = jsonObj.frontCamera == 'Y' ? true : false;
        WinContainer.plugin.initCameraProperties(jsonObj); 
        var captureInitSettings = null;
        captureInitSettings = new webCam.MediaCaptureInitializationSettings();
        captureInitSettings.audioDeviceId = "";
        captureInitSettings.videoDeviceId = "";
        captureInitSettings.photoCaptureSource = webCam.PhotoCaptureSource.photo;
        captureInitSettings.streamingCaptureMode = webCam.StreamingCaptureMode.video;

        Windows.Devices.Enumeration.DeviceInformation
            .findAllAsync(Windows.Devices.Enumeration.DeviceClass.videoCapture)
                .then(function (devList) {
                    if (devList.length > 1) {
                        if(frontCamera)
                            captureInitSettings.videoDeviceId = devList[0].id;
                        else
                            captureInitSettings.videoDeviceId = devList[1].id;
                    }else{
						WinContainer.plugin.initCameraProperties(jsonObj);
						frontCamera=false;
					}
                        
                    mediaCaptureMgr.initializeAsync(captureInitSettings)
                        .done(function (result) {
                            try {
                                isOrtFromCameraStart = false;
                                lockOrientaionForCamera();
                                var videoDev = mediaCaptureMgr.videoDeviceController;
                                var ort = Windows.Graphics.Display.DisplayInformation.getForCurrentView().currentOrientation;
                                console.log("new Ort="+ort);
                                try {
                                    if (videoDev.flashControl.supported) {
                                        if (pluginCameraProperties.flashAuto)
                                            videoDev.flashControl.auto = true;
                                        else if (pluginCameraProperties.flash)
                                            videoDev.flashControl.assistantLightEnabled = true;
                                        else
                                            videoDev.flashControl.assistantLightEnabled = false;

                                    }
                                  //  if (videoDev.zoomControl.supported && pluginCameraProperties.zoom && videoDev.zoomControl.max >= pluginCameraProperties.zoom && videoDev.zoomControl.min <= pluginCameraProperties.zoom)
                                  //      videoDev.zoomControl.value = pluginCameraProperties.zoom;
                                    if (videoDev.focusControl.supported) {
                                        var focusSettings = Windows.Media.Devices.FocusSettings();
                                        focusSettings.autoFocusRange = Windows.Media.Devices.AutoFocusRange.fullRange;
                                        focusSettings.waitForFocus = true;
                                        mediaCaptureMgr.videoDeviceController.focusControl.configure(focusSettings);
                                    }

                                } catch (e) {
                                    WinContainer.Log.warn(e.description);
                                }
                                
                                mediaCaptureMgr.onfocuschanged = onFocusChanged;
                                mediaCaptureMgr.onphotoconfirmationcaptured = onPhotoConfirmationCaptured;
                                mediaCaptureMgr.onfailed = onFailedCapture;
                                var resolutions = videoDev.getAvailableMediaStreamProperties(Windows.Media.Capture.MediaStreamType.photo);
                                if (frontCamera) {
                                    if (!isOrientationChanged)
                                        nativeCameraOrt = 7;
                                    mediaCaptureMgr.setPreviewRotation(Windows.Media.Capture.VideoRotation.clockwise270Degrees);
                                    resolutions = resolutions[0];
                                } else {
                                    if (!isOrientationChanged)
                                        nativeCameraOrt = 6;
                                    resolutions = resolutions[4];
                                    mediaCaptureMgr.setPreviewRotation(Windows.Media.Capture.VideoRotation.clockwise90Degrees);
                                }
                                videoDev.setMediaStreamPropertiesAsync(Windows.Media.Capture.MediaStreamType.photo, resolutions)
                                    .then(function () {
                                        displayNativeCamera();
                                        isOrtFromCameraStart = true;
                                       // WinContainer.plugin.changeCameraRotation(ort);
                                    }, function (e) {
                                        mediaCaptureMgr.close();
                                    });
                            } catch (e) {

                                try {
                                    mediaCaptureMgr.close();
                                    videoCameraTag = null;
                                } catch (e) { }
                                return;
                            }

                        }, function (er) {
                            videoCameraTag = null;
                            try {
                                mediaCaptureMgr.close();
                            } catch (e) { }
                        });
                });
        function onFailedCapture(args) {
            WinContainer.Log.error(args.message);
        }

        function displayNativeCamera() {
            var intDiv = document.createElement("div");
            intDiv.id = "previewVideo";
            intDiv.style.position = "absolute";
            intDiv.style.top = "0%";
            intDiv.style.left = "0%";
            intDiv.style.zIndex = "10000";
            videoCameraTag = document.createElement("video");
            videoCameraTag.id = "nativeCameraVideo";
            videoCameraTag.style.position = "fixed";
            videoCameraTag.style.top = "0%";
            videoCameraTag.style.left = "0%";
            //videoCameraTag.style.margin = "0px 0px 0px 0px";
            videoCameraTag.style.width = window.innerWidth + 'px';
            videoCameraTag.style.height = window.innerHeight + 'px';
            videoCameraTag.addEventListener("click", capturePhoto);
            videoCameraTag.src = URL.createObjectURL(mediaCaptureMgr, { oneTimeOnly: true });
            videoCameraTag.msZoom = true;
            if (frontCamera)
                videoCameraTag.msHorizontalMirror = true;
            videoCameraTag.play();
            intDiv.appendChild(videoCameraTag);
            document.body.appendChild(intDiv);
            // document.body.style.overflowY = 'hidden';
        }
        var app1 = WinJS.Application;
        app1.onbackclick = function () {
            try {
                unlockOrientaionForCamera();
                hideNativeCamera();
            } catch (e) { WinContainer.Log.error(e.description); }
            return true;
        }
        function hideNativeCamera() {
            document.body.style.overflowY = 'visible';
            var videoDiv = document.getElementById("previewVideo");
            videoCameraTag = document.getElementById("nativeCameraVideo");
            videoCameraTag.src = null;
            document.body.removeChild(videoDiv);
            if (videoCameraTag)
                videoCameraTag = null;
            if (mediaCaptureMgr)
                try {
                    setTimeout(function () { unlockOrientaionForCamera(); }, 0);
                    mediaCaptureMgr.close();
                } catch (e) {
                }
        }

        function onFocusChanged(args) {

        }
        function onPhotoConfirmationCaptured(args) {

        }
        function capturePhoto() {
            videoCameraTag.removeEventListener("click", capturePhoto);
            var colPro = Windows.Storage.CreationCollisionOption.replaceExisting;
            Windows.Storage.ApplicationData.current.localFolder.createFileAsync("temp_camera_Pic_Myphoto.jpg", colPro)
                .then(function (newFile) {
                    var photoStorage = newFile;
                    var photoProperties = Windows.Media.MediaProperties.ImageEncodingProperties.createJpeg();
                    if (!frontCamera) {
                        mediaCaptureMgr.videoDeviceController.focusControl.focusAsync().then(function () {
                            mediaCaptureMgr.capturePhotoToStorageFileAsync(photoProperties,
                                   photoStorage).then(function (result) {
                                       WinContainer.plugin.performCameraOperation(photoStorage, jsonObj);
                                       hideNativeCamera();
                                       mediaCaptureMgr.close();
                                       isOrtFromCameraStart = false;
                                   }, function (e) {
                                       try {
                                           mediaCaptureMgr.close();
                                       } catch (e) {
                                       }
                                   });
                        }, function (e) {
                            hideNativeCamera();
                            mediaCaptureMgr.close();
                        });
                    }
                    else {
                        mediaCaptureMgr.capturePhotoToStorageFileAsync(photoProperties, photoStorage).then(function (result) {
                                      WinContainer.plugin.performCameraOperation(photoStorage, jsonObj);
                                      hideNativeCamera();
                                      mediaCaptureMgr.close();
                                      isOrtFromCameraStart = false;
                                  }, function (e) {
                                      try {
                                          mediaCaptureMgr.close();
                                      } catch (e) {
                                      }
                                  });
                    }
                });

        }
    };
    WinContainer.plugin.performCameraOperation = function (photoStorage, jsonObj) {
      //  var htmlid = jsonObj.elementId;
        var action = jsonObj.action;
        var fileName = jsonObj.fileName;
        var req_id = jsonObj.id;

        var res = {};
        res.id = jsonObj.id;

        if (fileName == undefined || fileName == null || fileName == '')
            fileName = 'CAM_PIC.jpg';
        else
            fileName = fileName + '.jpg';

        var collision = Windows.Storage.CreationCollisionOption.generateUniqueName;
        if (nativeCameraOrt == null)
            nativeCameraOrt = Windows.Storage.FileProperties.PhotoOrientation.normal;
         var ort = (nativeCameraOrt == 5 && frontCamera) ? 7 : nativeCameraOrt;

        WinContainer.plugin.reencodePhotoAsync(photoStorage, ort, fileName, collision)
            .then(function (photo) {
                try {
                    if (action == 'save') {
                            collision = Windows.Storage.CreationCollisionOption.replaceExisting;
                        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
                        localFolder.createFolderAsync("photo", Windows.Storage.CreationCollisionOption.openIfExists)
                            .then(function (folder) {
                                WinContainer.Log.debug("Camera Closed");
                                photo.copyAsync(folder, fileName, collision).then(function (imgFile) {
                                    var imgsrc = "ms-appdata:///local/photo/" + imgFile.name + '?' + Date.now();
                                    res.path = imgsrc;
                                    WinContainer.successCallback(res);
                                    photo.deleteAsync();
                                }, function (e) {
                                    var a = e;
                                }, function (e) {
                                    var a = e;
                                });
                            });
                    }
                  
                    if (action == 'base64') {
                        WinContainer.plugin.getBase64FromImageAsync(photo)
                            .then(function (base64Data) {
                                res.encodedImage = base64Data;
                                WinContainer.successCallback(res);
                                photo.deleteAsync();
                            });

                    }
                  
                    if (action == 'base64_Save') {
                        var imgsrc;
                            collision = Windows.Storage.CreationCollisionOption.replaceExisting;
                        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
                        localFolder.createFolderAsync("photos", Windows.Storage.CreationCollisionOption.openIfExists)
                            .then(function (folder) {
                                photo.copyAsync(folder, fileName, collision).then(function (imgFile) {
                                    imgsrc = "ms-appdata:///local/photos/" + imgFile.name + '?' + Date.now();

                                    WinContainer.plugin.getBase64FromImageAsync(photo).then(function (base64Data) {
                                        res.path = imgsrc;
                                        res.encodedImage = base64Data;
                                        WinContainer.successCallback(res);
                                        photo.deleteAsync();
                                    });
                                }, function (e) {
                                    var a = e;
                                }, function (e) {
                                    var a = e;
                                });
                            });
                    }
                   // photoStorage.deleteAsync();
                } catch (e) {
                    var a = e;
                }

            }, function (e) {
                var a = e;
            });
    };
    WinContainer.plugin.getBase64FromImageAsync = function (image) {
        var base64 = {
            encode: function (data) {
                var str = "";
                for (var i = 0; i < data.length; i++)
                    str += String.fromCharCode(data[i]);

                return btoa(str).split(/(.{75})/).join("\n").replace(/\n+/g, "\n").trim();
            }
        }
        var reader = null;
        return image.getThumbnailAsync(Windows.Storage.FileProperties.ThumbnailMode.singleItem)
            .then(function (thumbnail) {
                var inputStream = thumbnail.getInputStreamAt(0);
                reader = new Windows.Storage.Streams.DataReader(inputStream);
                return reader.loadAsync(thumbnail.size);
            }).then(function (actSize) {
                var array = new Array(actSize);
                reader.readBytes(array);
                reader.close();
                return base64.encode(array);
            });

    }
    function lockOrientaionForCamera() {
        try {
            ort_pref = Windows.Graphics.Display.DisplayInformation.autoRotationPreferences;
        } catch (e) { }
        Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.portrait;

    }
    function unlockOrientaionForCamera() {
        try {
            Windows.Graphics.Display.DisplayInformation.autoRotationPreferences = ort_pref;
        } catch (e) {
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
    }
    WinContainer.plugin.changeCameraRotation = function (ort) {
        isOrientationChanged = true;
        switch (ort) {
            case "PU": nativeCameraOrt = frontCamera ? 7 : 5;
                break;
            case "LL": nativeCameraOrt = 3;
                break;
            case "LU": nativeCameraOrt = 1;
                break;
            default: nativeCameraOrt = frontCamera ? 7 : 6;
                break;
        }
        console.log("-----------Ort======== " + ort);
    }
    WinContainer.plugin.reencodePhotoAsync = function (tempStorageFile, currentRotation, photoFile, collision) {
        var inputStream = null;
        var outputStream = null;
        var decoder = null;
        var encoder = null;
        var photoStorage = null;

        return tempStorageFile.openAsync(Windows.Storage.FileAccessMode.read).then(function (stream) {
            inputStream = stream;
            return Windows.Graphics.Imaging.BitmapDecoder.createAsync(inputStream);

        }).then(function (_decoder) {
            decoder = _decoder;
            return Windows.Storage.ApplicationData.current.temporaryFolder.createFileAsync(photoFile,
                collision);

        }).then(function (file) {
            photoStorage = file;
            return photoStorage.openAsync(Windows.Storage.FileAccessMode.readWrite);

        }).then(function (stream) {
            outputStream = stream;
            outputStream.size = 0;
            return Windows.Graphics.Imaging.BitmapEncoder.createForTranscodingAsync(outputStream, decoder);

        }).then(function (_encoder) {
            encoder = _encoder;
            try {
                if (isNaN(photojson.targetWidth) && isNaN(photojson.targetHeight)) {
                    photojson.targetWidth = 612;
                    photojson.targetHeight = 816;
                }
                else {
                    var gratio = decoder.pixelWidth / decoder.pixelHeight;
                    if (isNaN(photojson.targetWidth)) {
                        photojson.targetWidth = (uint)(gratio * photojson.targetHeight);
                    }
                    else if (isNaN(photojson.targetHeight)) {
                        photojson.targetHeight = (uint)(gratio * photojson.targetWidth);
                    }
                }
            } catch (e) { }
            encoder.bitmapTransform.scaledWidth = photojson.targetWidth;
            encoder.bitmapTransform.scaledHeight = photojson.targetHeight;
            
            var properties = new Windows.Graphics.Imaging.BitmapPropertySet();
            properties.insert("System.Photo.Orientation",
                new Windows.Graphics.Imaging.BitmapTypedValue(
                    currentRotation,
                    Windows.Foundation.PropertyType.uint16));
            return encoder.bitmapProperties.setPropertiesAsync(properties);

        }).then(function () {
            return encoder.flushAsync();
        }).then(function () {
            inputStream.close();
            outputStream.close();
            return photoStorage;
        }, function (e) {
            var a = e;
        });
    };
    WinContainer.plugin.selectedPhoto = function (filesobj) {
        var photo =  filesobj.detail.files.size > 0 ?  filesobj.detail.files[0]:null;
        if(photo !=null)
        {
            WinContainer.plugin.performCameraOperation(photo, photojson);
        }
        else
        {
            var json = {};
            json.errorMessage = "No Photo file chosen";
            callFunctionOrNamespace(photojson.failureCallback, json);
        }

    };
})();
