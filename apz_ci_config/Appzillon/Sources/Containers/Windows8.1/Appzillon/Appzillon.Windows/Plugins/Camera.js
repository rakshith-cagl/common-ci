var mediaCapture = new Windows.Media.Capture.MediaCapture();  // Variable for audio plugin.
mediaCapture.initializeAsync();
var photojson = {};
WinContainer.plugin.camera = function (jsonObj) {
    var res = {};
    res.id = jsonObj.id;
    photojson = jsonObj;
    try {
        if (jsonObj.sourceType == "Photo") {
            var action = jsonObj.action;
            //var htmlid = jsonObj.elementId;
            var openPicker = new Windows.Storage.Pickers.FileOpenPicker();
            openPicker.viewMode = Windows.Storage.Pickers.PickerViewMode.thumbnail;
            openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.picturesLibrary;
            openPicker.fileTypeFilter.replaceAll(["*"]);
            openPicker.fileTypeFilter.replaceAll([".png", ".jpg", ".jpeg", ".bmp"]);
            openPicker.pickSingleFileAsync().then(function (fileItem) {
                if (fileItem) {
                    var extension = fileItem.path.substr(fileItem.path.lastIndexOf('.') + 1);
                    if (!(extension == "png" || extension == "jpg" || extension == "jpeg")) {
                        var json = {};
                        json.errorMessage = "file not supported";
                        WinContainer.failureCallback(jsonObj.id,json);
                        return;
                    }
                    WinContainer.plugin.reencodePhotoAsync(fileItem, jsonObj.fileName + "." + extension).then(function (file) {
                        var temp = Windows.Storage.ApplicationData.current.localFolder;
                        temp.createFolderAsync("temp", Windows.Storage.CreationCollisionOption.openIfExists)
                            .then(function (folder) {
                                file.copyAsync(folder, jsonObj.fileName + "." + extension, Windows.Storage.NameCollisionOption.generateUniqueName).then(function (capturedItem) {
                                    capturedItem.getThumbnailAsync(Windows.Storage.FileProperties.ThumbnailMode.singleItem).then(function (thumbnail) {
                                        var inputStream = thumbnail.getInputStreamAt(0);
                                        var reader = new Windows.Storage.Streams.DataReader(inputStream);
                                        reader.loadAsync(thumbnail.size).done(function (actSize) {
                                            var array = new Array(actSize);
                                            reader.readBytes(array);
                                            reader.close();

                                            // Conversion into base64.
                                            var base64Data = base64.encode(array);
                                            try {
                                               if (action == "save" || action == "base64_Save") {

                                                    var tempname = "";

                                                    localFolder.createFolderAsync("photo", Windows.Storage.CreationCollisionOption.openIfExists).then(function (folder) {
                                                        //temp = 1;
                                                            capturedItem.copyAsync(folder, name, Windows.Storage.NameCollisionOption.replaceExisting).then(function () {
                                                                imgsrc = "ms-appdata:///local/photo/" + name;
                                                                var data = JSON.stringify({
                                                                    successMessage: "",
                                                                    path: imgsrc,
                                                                    encodedImage: ""
                                                                }, null, " ");
                                                                var json = JSON.parse(data);
                                                                if (action == "base64_Save") {
                                                                    json.encodedImage = base64Data;
                                                                }
                                                                json.id = jsonObj.id;
                                                                WinContainer.successCallback(json);
                                                            });
                                                        

                                                    });

                                                } else if (action == "base64") {
                                                    var data = JSON.stringify({
                                                        successMessage: "",
                                                        path: null,
                                                        encodedImage: base64Data
                                                    }, null, " ");
                                                    var json = JSON.parse(data);
                                                    json.id = jsonObj.id;
                                                    WinContainer.successCallback(json);
                                                }
                                            } catch (e) {
                                                var data = JSON.stringify({
                                                    errorCode: "APZ-CNT-082"
                                                }, null, " ");
                                                var json = JSON.parse(data);
                                                json.id = jsonObj.id;
                                                WinContainer.failureCallback(jsonObj.id,json);
                                            }
                                        });
                                    });
                                });
                            });
                    });
                }
            });
            return;
        }
    } catch (e) { }
    WinContainer.removeAlertUI();
    try {
       // var htmlid = jsonObj.elementId;
        var fileName = jsonObj.fileName;
        var action = jsonObj.action;
        var name = fileName + ".jpg";
        var captureUI = Windows.Media.Capture.CameraCaptureUI();

        captureUI.captureFileAsync(Windows.Media.Capture.CameraCaptureUIMode.photo).then(function (capturedItem) {
            if (capturedItem) {
                //var base64= base64.encode(capturedItem.Array);
                var localFolder = Windows.Storage.ApplicationData.current.localFolder;
                WinContainer.plugin.reencodePhotoAsync(capturedItem, name).then(function (file) {
                    file.copyAsync(localFolder, name, Windows.Storage.NameCollisionOption.generateUniqueName).then(function (fileDone) {
                        fileDone.getThumbnailAsync(Windows.Storage.FileProperties.ThumbnailMode.singleItem).then(function (thumbnail) {
                            var inputStream = thumbnail.getInputStreamAt(0);
                            var reader = new Windows.Storage.Streams.DataReader(inputStream);
                            reader.loadAsync(thumbnail.size).done(function (actSize) {
                                var array = new Array(actSize);
                                reader.readBytes(array);
                                reader.close();

                                // Conversion into base64.
                                var base64Data = base64.encode(array);

                                try {
                                     if (action == "save" || action == "base64_Save") {

                                        var tempname = "";

                                        localFolder.createFolderAsync("photo", Windows.Storage.CreationCollisionOption.openIfExists).then(function (folder) {
                                            //temp = 1;
                                                capturedItem.copyAsync(folder, name, Windows.Storage.NameCollisionOption.replaceExisting).then(function () {
                                                    imgsrc = "ms-appdata:///local/photo/" + name;
                                                    res.path = imgsrc;

                                                    if (action == "base64_Save") {
                                                        res.encodedImage = base64Data;
                                                    }
                                                    WinContainer.successCallback(res);
                                                });
                                            

                                        });
                                    } else if (action == "base64") {
                                        res.encodedImage = base64Data;
                                        WinContainer.successCallback(res);

                                    } 
                                    localFolder.getFileAsync(name).then(function (file) {
                                        file.deleteAsync();
                                    });

                                } catch (e) {
                                    WinContainer.failureCallback(res.id, "APZ-CNT-082");
                                    WinContainer.Log.error(e.description);
                                }
                            });

                        }, function (e) {
                            WinContainer.failuerCallback(res.id, "APZ-CNT-211");
                            WinContainer.Log.error(e.message);
                        });
                    }, function (e) {
                        WinContainer.failureCallback(res.id, "APZ-CNT-211");
                        WinContainer.Log.error(e.description);
                    });
                });
            }
     }, function (e) {
            WinContainer.failureCallback(res.id, "APZ-CNT-211");
            WinContainer.Log.error(e.description);
        });
    } catch (e) {
        WinContainer.failureCallback(res.id, "APZ-CNT-211");
        WinContainer.Log.error(e.description);
    }

};
WinContainer.plugin.reencodePhotoAsync = function (tempStorageFile, photoFile, currentRotation, collision) {
    var inputStream = null;
    var outputStream = null;
    var decoder = null;
    var encoder = null;
    var photoStorage = null;
    currentRotation = 1;
        collision = Windows.Storage.CreationCollisionOption.replaceExisting;
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
var base64 = {
    encode: function (data) {
        var str = "";
        for (var i = 0; i < data.length; i++)
            str += String.fromCharCode(data[i]);

        return btoa(str).split(/(.{75})/).join("\n").replace(/\n+/g, "\n").trim();
    }
};
