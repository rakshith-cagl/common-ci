//---------File Operation----------
WinContainer.FileOperation = (function () {
    var folderPickerObj = {};
    var objectKeeper = {};
    function getInstance(id) {
        if (objectKeeper[id]) {
            return objectKeeper[id];
        } else {
            objectKeeper[id] = new FileClass(id);
            return objectKeeper[id];
        }
    }
    function FileClass(id) {
        this.id = id;
    }
    FileClass.prototype.uploadFile = function (req) {
        var res = {};
        res.id = this.id;

        try {
            var js = {};
            var fileToUpload;
            var fip;
            var fileName;
            var fileOverride = req.overWrite == 'Y' ? 'Y' : 'N';
            var name = ((req.fileName == null) ? "" : req.fileName);
            var fp = req.filePath;
            var destDir = req.destination.replace(/\\/g, "/");

            if (!(destDir == "" || destDir == null))
                //  destDir += "/";

            if (fp == undefined || fp == null || fp == "" || fp == " ") {
                try {
                    fileToUpload = $('#' + req.fieldID).text().replace(/\//g, "\\\\");
                    if (fileToUpload == undefined || fileToUpload === "")
                        fileToUpload = $('#' + req.fieldID).val().replace(/\//g, "\\\\");
                } catch (e) {
                    fileToUpload = req.filePath.replace(/\//g, "\\\\");
                }
            }
            else {
                fileToUpload = fp;
            }
            fip = fileToUpload;
            var fileData = getBlob(fip);
            fileToUpload = fileToUpload.replace(/\//g, "\\");
            var hasSession = false;
            if (req.sessionReq == "Y") {
                hasSession = true;
                js.appzillonHeader = apz.server.getHeader("appzillonUploadFile", req.screenId, "uploadReqId", true);
            }
            else 
                js.appzillonHeader = apz.server.getHeader("appzillonUploadFileWS", req.screenId, "uploadReqId", true);
            var type = fileData.type;
            var last_i = fip.lastIndexOf('\\');
            var fileName = fip.substring(last_i + 1);
                    var body = {};
                    body.fileDetails = [];
                    var arr = {};
                    arr.fileName = fileName;
                    arr.fileSize = fileData.size;
                    arr.fileNo = 1;
                    arr.fileType = type;
                    body.fileDetails.push(arr);
                    body.destination = destDir;
                    body.overWrite = fileOverride;
                    js.appzillonBody = body;
                    var stringjs = JSON.stringify(js);
                    var formData = new FormData();
                    var appID = WinContainer.Settings.containerProperties.MAINAPPID;
                    var serverurl = WinContainer.Settings.appProperties.serverUrl+"/upload";
                    formData.append(arr.fileName, fileData, arr.fileName);
                    formData.append("appzillonRequest", JSON.stringify(js));                
                    sendUploadRequest(hasSession, serverurl, formData, js.appzillonHeader, function (s, r, m) {
                        if (s) {
                          //  WinContainer.storeServerSession(r.requestKey, r.sessionId);
                            WinContainer.successCallback(res);
                            delete objectKeeper[res.id];
                        } else {                         
                            WinContainer.failureCallback(res.id, r, m);
                            try {
                                WinContainer.Log.error(r.toString());
                            } catch (e) { }
                            delete objectKeeper[res.id];
                        }
                    }, function (e) { });

        } catch (e) {
            WinContainer.Log.error(e.description);
            WinContainer.failureCallback(res.id, "APZ-CNT-007");
            delete objectKeeper[res.id];
        }
    }
    FileClass.prototype.uploadFileWithoutSession = function (req) {
        var res = {};
        res.id = this.id;

        try {
            var js = {};
            var fileToUpload;
            var fip;
            var fileName;
            var fileOverride = req.overWrite == 'Y' ? 'Y' : 'N';
            var name = ((req.fileName == null) ? "" : req.fileName);
            var fp = req.filePath;
            var destDir = req.destination.replace(/\\/g, "/");

            if (!(destDir == "" || destDir == null))
                destDir += "/";

            if (fp == undefined || fp == null || fp == "" || fp == " ") {
                try {
                    fileToUpload = $('#' + req.fieldID).text().replace(/\//g, "\\\\");
                    if (fileToUpload == undefined || fileToUpload === "")
                        fileToUpload = $('#' + req.fieldID).val().replace(/\//g, "\\\\");
                } catch (e) {
                    fileToUpload = req.filePath.replace(/\//g, "\\\\");
                }
            }
            else {
                fileToUpload = fp;
            }
            fip = fileToUpload;
            var fileData = getBlob(fip);
            fileToUpload = fileToUpload.replace(/\//g, "\\");
            js.appzillonHeader = apz.server.getHeader("appzillonUploadFileWS", req.screenId, "uploadReqId", true);
            var type = fileData.type;
            var last_i = fip.lastIndexOf('\\');
            var fileName = fip.substring(last_i + 1);
            var body = {};
            body.fileDetails = [];
            var arr = {};
            arr.fileName = fileName;
            arr.fileSize = fileData.size;
            arr.fileNo = 1;
            arr.fileType = type;
            body.fileDetails.push(arr);
            body.destination = destDir;
            body.overWrite = fileOverride;
            js.appzillonBody = body;
            var stringjs = JSON.stringify(js);
            var formData = new FormData();
            var appID = WinContainer.Settings.containerProperties.MAINAPPID;

            //formData.append(config.appId + "?" + config.screenId + "?" + config.userId + '?' + config.fileOverride, fileData, config.destDir + config.fileName);

            sendUploadRequest(true, config.url, formData, config.appzillonHeader, function (s, r, m) {
                if (s) {
                    //WinContainer.storeServerSession(r.requestKey, r.sessionId);
                    WinContainer.successCallback(res);
                    delete objectKeeper[res.id];
                } else {
                    WinContainer.failureCallback(res.id, r, m);
                    delete objectKeeper[res.id];
                }
            });
        } catch (e) {
            WinContainer.failureCallback(res.id, "APZ-CNT-007");
            delete objectKeeper[res.id];
        }
    }
    FileClass.prototype.downloadFile = function (req) {
        var res = {};
        res.id = this.id;
        var json = {};
        var filepath = req.filePath ;//+ req.fileName;
        var appzillonFilePushServiceRequest ={};
        appzillonFilePushServiceRequest.filePath = filepath;
        appzillonFilePushServiceRequest.fileName = req.fileName;
        appzillonFilePushServiceRequest.base64 = "Y";
        var body = {};
        var hasSession = false;
        if (req.sessionReq == "Y") {
            hasSession = true;
            json.appzillonHeader = apz.server.getHeader("appzillonFilePushService", req.screenId, "downloadReqId", true);
            body.appzillonFilePushServiceRequest = appzillonFilePushServiceRequest;
        }
        else {
            json.appzillonHeader = apz.server.getHeader("appzillonFilePushServiceWS", req.screenId, "downloadReqId", true);
            body.appzillonFilePushServiceWSRequest = appzillonFilePushServiceRequest;
        }
        json.appzillonBody = body;

        var serverurl = WinContainer.Settings.appProperties.serverUrl;
        sendDownloadRequest(hasSession, serverurl, JSON.stringify(json), function (s, r, m) {
            if (s) {
                saveDowloadFile(res.id, req, r);
            } else {          
                WinContainer.failureCallback(res.id, r, m);
                try {
                    WinContainer.Log.error(r.toString());
                } catch (e) { }
            }
        });
    }
    FileClass.prototype.downloadFileWithoutSession = function (req) {
        var res = {};
        res.id = this.id;

        var config = buildDownloadProp(req, true);

        sendDownloadRequest(true, config.url, config.data, function (s, r, m) {
            if (s) {
                saveDowloadFile(res.id, config, r);
            } else {
                WinContainer.failureCallback(res.id, r, m);
            }
        });
    }
    //-------util-----------------
    function getAbsolutePath(file) {
        if (file.indexOf("ms-appdata:") >= 0) {
            return file;
        }
        return 'ms-appdata:///local\\' + file.replace(/\//g, '\\').replace(/\\\\/g, '\\');
    }
    function getBlob(path) {
        var xhr = new XMLHttpRequest();
        var filePath = getAbsolutePath(path);
        try {
            xhr.open("GET", filePath, false);
            xhr.responseType = "blob";
            xhr.send(null);
            var status = xhr.status;
            if (status === 200 || status === 0 || status === 1100)
                return xhr.response;
            else return null;
        } catch (e) {
            return null;
        }
    }
    function getUrl(v) {
        var fileServer = WinContainer.Settings.appProperties.serverUrl;
        return fileServer.substr(0, fileServer.lastIndexOf('/')) + '/' + v;
    }
    function folderPickerCB(object) {
        var folderToken = "PickedFolderToken";
        if (!object.detail.folder) {
            WinContainer.failureCallback(folderPickerObj.id, "APZ-CNT-190");
            WinContainer.Log.error("invalid folder location");
        } else {
            var access = Windows.Storage.AccessCache.StorageApplicationPermissions.futureAccessList.add(object.detail.folder);
            Windows.Storage.ApplicationData.current.localSettings.values[folderToken] = access;
            savePhoneExternal(folderPickerObj.fileName, folderPickerObj.fileContent, function (s, r, m) {
                if (s) {
                    folderPickerObj = null;
                    var res = {};
                    res.fileName = result.fileName;
					res.text = result.fileName;
                    WinContainer.successCallback(res);
                } else {
                    folderPickerObj = null;
                    WinContainer.failureCallback(res.id, result, m);
                    WinContainer.Log.error(result);
                }
            });
        }
    }
    function savePhoneExternal(fileName, fileContent, callback, req_id) {

        if (Windows.Storage.ApplicationData.current.localSettings.values.hasKey("PickedFolderToken")) {
            // var token = Windows.Storage.ApplicationData.current.localSettings.values["PickedFolderToken"];
            //   storageFolder = Windows.Storage.AccessCache.StorageApplicationPermissions.futureAccessList.getFolderAsync(token);

            var lf = Windows.Storage.StorageFolder;
            lf.getFolderFromPathAsync("C:\\Data\\Users\\Public\\Downloads").done(function (df) {
                df.createFileAsync(fileName, Windows.Storage.CreationCollisionOption.generateUniqueName).then(function (file) {
                    if (fileContent) {
                        Windows.Storage.FileIO.writeBufferAsync(file, fileContent).then(function (w) {
                            var r = {};
                            r.fileName = file.name;
                            callback(true, r);
                        }, function (e) {
                            callback(false, "APZ-CNT-191", e.message);
                        });
                    }
                }, function (e) {
                    callback(false, "APZ-CNT-191", e.message);
                });
            },
            function (e) {
                callback(false, "APZ-CNT-191", e.message);
            });
        }
        else {
            folderPickerObj = {};
            folderPickerObj.id = req_id;
            folderPickerObj.fileContent = fileContent;
            folderPickerObj.fileName = fileName;
            var openPicker = new Windows.Storage.Pickers.FolderPicker;
            openPicker.fileTypeFilter.replaceAll(["*"]);
            openPicker.suggestedStartLocation = Windows.Storage.Pickers.PickerLocationId.downloads;
            openPicker.pickFolderAndContinue();
        }
    }
    function saveDesktopExternal(fileName, fileContent, callback) {
        var downloadsFolder = Windows.Storage.DownloadsFolder;
        downloadsFolder.createFileAsync(fileName, Windows.Storage.CreationCollisionOption.generateUniqueName).done(function (file) {
            Windows.Storage.FileIO.writeTextAsync(file, fileContent).then(function (w) {
                var r = {};
                r.fileName = file.name;
                callback(true, r);
            }, function (e) {
                callback(false, "APZ-CNT-191", e.message);
            });
        });
    }
    function sendUploadRequest(hasSession, url, formData, headerData, callback) {
        //url = "http://172.0.0.1:8888";
        var result = {};
        var xhr = new XMLHttpRequest();
        xhr.open("POST", url, true);

        //var contentType = "multipart/form-data; boundary=" + "WINDOWS8.1";
        //var contentType = "multipart/form-data; boundary=" + "WINDOWS8.1";  
        //xhr.setRequestHeader("Content-Type", contentType);
        //xhr.setRequestHeader("AppzillonHeader", JSON.stringify(headerData));

        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    var result = xhr.responseText;
                    var resJson = JSON.parse(xhr.responseText);
                    var status = resJson.appzillonHeader.status;
                    if (status == true) {
                        var check = false;
                        if (!hasSession) {
                            check = JSON.stringify(resJson.appzillonBody.appzillonUploadFileWSResponse).indexOf('success') >= 0 ? true : false;
                        } else {
                            check = JSON.stringify(resJson.appzillonBody.appzillonUploadFileResponse).indexOf('success') >= 0 ? true : false;
                            result.sessionId = resJson.appzillonHeader.sessionId;
                            result.requestKey = resJson.appzillonHeader.requestKey;
                        }
                        if (check) {
                            callback(true, result);
                        } else {
                            callback(false, "APZ-CNT-007");
                        }
                    } else {
                        callback(false, resJson.appzillonErrors[0].errorCode, resJson.appzillonErrors[0].errorMessage);
                    }
                } else {
                    callback(false, "APZ-CNT-007", "Error uploading file");
                }
            }
        }
        xhr.send(formData);
    }
    function sendDownloadRequest(hasSession, url, str, callback) {
        WinJS.xhr({
            type: "post",
            url: url,
            headers: {
                "Content-type": "application/json"
            },
            data: str
        }).done(function (req) {

            var res = JSON.parse(req.responseText);
            var s = res.appzillonHeader.status;
            if (s == true || s === "success") {

                var bodyFile;
                if (!hasSession) {
                    bodyFile = res.appzillonBody.appzillonFilePushServiceWSResponse;
                } else {
                    bodyFile = res.appzillonBody.appzillonFilePushServiceResponse;
                    bodyFile.requestKey = res.appzillonHeader.requestKey;
                    bodyFile.sessionId = res.appzillonHeader.sessionId;
                }

                var fileName = bodyFile.fileName;
                bodyFile.fileName = fileName.indexOf('/') >= 0 ? fileName.substr(fileName.lastIndexOf('/') + 1) : fileName;
                callback(true, bodyFile);
            } else {
                callback(false, res.appzillonErrors[0].errorCode, res.appzillonErrors[0].errorMessage);
                WinContainer.Log.error(res.appzillonErrors[0].errorMessage);
            }
        }, function (e) {
            callback(false, ErrorCode.File_UPLOAD_FAIL, e.message);
            WinContainer.Log.error(e.description);
        });
    }
    function buildDownloadProp(jsonObj, ws) {
        var j = {};

        var destDir = jsonObj.destinationPath;
        var filePath = jsonObj.filePath;
        if (!(filePath == "" || filePath == null))
            filePath += "/";
        var fileName = jsonObj.fileName;

        var isbase64 = jsonObj.base64 == 'Y' ? true : false;
        var external = jsonObj.downloadExternalPath == 'Y' ? true : false;

        var scrID = WinContainer.getScreenId();
        var userID = WinContainer.getUserId();

        if (destDir === "" || destDir === undefined || destDir === null) {
            destDir = "downloads";
        }
        destDir = destDir.replace(/\//g, "\\\\");
        var appzillonBody;
        var interfaceId;
        if (ws) {
            interfaceId = "appzillonFilePushServiceWS";
            appzillonBody = {
                "appzillonFilePushServiceWSRequest": {
                    "fileName": filepath,
                    "base64": "Y"
                }
            };
        } else {
            interfaceId = "appzillonFilePushService";
            appzillonBody = {
                "appzillonFilePushServiceRequest": {
                    "fileName": filepath,
                    "base64": "Y"
                }
            };
        }



        var header = '{"appzillonHeader":' + JSON.stringify(WinContainer.getServerHeader(interfaceId));
        var body = ',"appzillonBody":' + JSON.stringify(appzillonBody) + '}';
        j.url = WinContainer.Settings.appProperties.serverUrl;
        //j.appId = appID;
        j.data = header + body;
        j.destPath = destDir;
        j.isbase64 = isbase64;
        j.external = external;
        return j;
    }
    function buildUploadProp(jsonObj, ws) {
        var j = {};
        var fileToUpload;
        var fileName;
        var fileOverride = jsonObj.overWrite == 'Y' ? 'Y' : 'N';
        var name = ((jsonObj.fileName == null) ? "" : jsonObj.fileName);
        var fp = jsonObj.filePath;
        var destDir = jsonObj.destination.replace(/\\/g, "/");

        if (!(destDir == "" || destDir == null))
            destDir += "/";

        if (fp == undefined || fp == null || fp == "" || fp == " ") {

            // if (name == null || name == "") {
            //     fileName = fileToUpload.substr(fileToUpload.lastIndexOf("/") + 1);
            // }
            // else {
            //     fileName = name;
            // }
            try {
                fileToUpload = $('#' + jsonObj.fieldID).text().replace(/\//g, "\\\\");
                if (fileToUpload == undefined || fileToUpload === "")
                    fileToUpload = $('#' + jsonObj.fieldID).val().replace(/\//g, "\\\\");
            } catch (e) {
                fileToUpload = jsonObj.filePath.replace(/\//g, "\\\\");
            }
        }
        else {
            fileToUpload = fp;
        }

        //  j.url = getUrl('upload');
        //  j.appId = WinContainer.Settings.containerPropties.MAINAPPID;
        //    j.screenId = jsonObj.screenId;
        j.destDir = destDir;
        // j.filePath = fileToUpload;
        // j.fileName = fileName;
        //  j.overWrite = fileOverride;
        if (ws) {
        //    j.userId = "windows";
            j.appzillonHeader = WinContainer.getServerHeader("appzillonUploadFileWS", j.screenId);
        } else {
         //   j.userId = WinContainer.getUserId();
            j.appzillonHeader = WinContainer.getServerHeader("appzillonUploadFile", j.screenId);
        }
        var re = {};
        re.id = "internal";
        re.filePath = fileToUpload;
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        var filepath = localFolder.path + "\\" + fileToUpload;
        var type = "";
        Windows.Storage.StorageFile.getFileFromPathAsync(filepath).then(function (file) {
            type = file.contentType;
            var body = {};
            body.fileDetails = {};
            body.fileDetails.fileName = fileName;
            body.fileDetails.fileSize = _getFileSize(re);
            body.fileDetails.fileNo = 1;
            body.fileDetails.fileType = type;
            body.destination = destDir;
            body.overWrite = fileOverride;
            j.appzillonBody = body;
            j.filePath = filepath;
            return j;
        });
      
    }
    function saveDowloadFile(req_id, config, r) {
        var res = {};
        res.id = req_id;
        var fileData = r.file;
        var fileType = r.fileType;
        var fileName = r.fileName;

        try {
            if (config.base64 == "Y") {
                res.base64 = fileData;
				res.text =fileData;
                WinContainer.successCallback(res);
                return;
            } else {
                var fileContent = Windows.Security.Cryptography.CryptographicBuffer.decodeFromBase64String(fileData);
             //   if (!config.external) {
                    var localFolder = Windows.Storage.ApplicationData.current.localFolder;
                    localFolder.createFolderAsync(config.destinationPath, Windows.Storage.CreationCollisionOption.openIfExists).then(function (folder) {
                        folder.createFileAsync(fileName, Windows.Storage.CreationCollisionOption.generateUniqueName).done(function (newFile) {
                            try {
                                Windows.Storage.FileIO.writeBufferAsync(newFile, fileContent).then(function (s) {
                                    res.fileName = newFile.name;
									re.text = newFile.name;
                                    WinContainer.successCallback(res);
                                    delete objectKeeper[res.id];
                                });

                            } catch (e) {
                                WinContainer.failureCallback(res.id, "APZ-CNT-004", e.message);
                                delete objectKeeper[res.id];
                                WinContainer.Log.error(e.description);
                            }
                        });
                    }, function (e) {
                        WinContainer.Log.error(e.message);
                    });
               // }
               /* else {
                    //var storageFolder;
                    var saveExternal;
                    if (WinContainer.isPhone()) {
                        saveExternal = savePhoneExternal;
                    } else {
                        saveExternal = saveDesktopExternal;
                    }
                    saveExternal(fileName, fileContent, function (s, result, m) {
                        if (s) {
                            res.fileName = result.fileName;
                            WinContainer.successCallback(res);
                        } else {
                            WinContainer.failureCallback(res.id, result, m);
                        }
                    }, res.id);
                }*/
            }
        } catch (e) {
            WinContainer.Log.error(e.description);
        }
    }

    var _upload = function (req) {
        var fileOp = getInstance(req.id);
        fileOp.uploadFile(req);
    }
    var _download = function (req) {
        var fileOp = getInstance(req.id);
        fileOp.downloadFile(req);
    }
    var _uploadWithoutSession = function (req) {
        var fileOp = getInstance(req.id);
        fileOp.uploadFileWithoutSession(req);
    }
    var _downloadWithoutSession = function (req) {
        var fileOp = getInstance(req.id);
        fileOp.downloadFileWithoutSession(req);
    }
    var _fileToBase64 = function (req) {
        var res = {};
        res.id = req.id;
        var initalfilepath = req.filePath;
        var filepath;

        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        try {
            initalfilepath = initalfilepath.replace(new RegExp('/', 'g'), "\\");
            if (initalfilepath.search(new RegExp("ms-appdata:", 'g')) != -1) {
                filepath = initalfilepath.replace("ms-appdata:\\\\\\local", localFolder.path);

            }
            else {
                filepath = localFolder.path + "\\" + initalfilepath;
            }
            Windows.Storage.StorageFile.getFileFromPathAsync(filepath).done(function (file) {
                Windows.Storage.FileIO.readBufferAsync(file).done(function (buffer) {

                    var bytes = new Uint8Array(buffer.length);
                    var dataReader = Windows.Storage.Streams.DataReader.fromBuffer(buffer);
                    dataReader.readBytes(bytes);
                    dataReader.close();
                    var str = Windows.Security.Cryptography.CryptographicBuffer.encodeToBase64String(buffer);
                    res.text = str;
                    WinContainer.successCallback(res);
                }, function (e) {
                    WinContainer.failureCallback(res.id,"APZ-CNT-064");
                    WinContainer.Log.error(e.description);
                });
            }, function (e) {
                WinContainer.failureCallback(res.id, ErrorCode.FILE_NOT_FOUND);
                WinContainer.Log.error(e.message);
            });

        }
        catch (e) {
            WinContainer.failureCallback(res.id, ErrorCode.FILE_NOT_FOUND, e.message);
            WinContainer.Log.error(e.message);
        }
    }
    var _base64ToFile = function (req) {
        var res = {};
        res.id = req.id;

        var base64 = req.base64;
        var fileName = req.fileName;
        var filePath = req.filePath;
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        var decode = Windows.Security.Cryptography.CryptographicBuffer.decodeFromBase64String(base64);
        if (filePath == null) {
            try {

                localFolder.createFileAsync(fileName, Windows.Storage.CreationCollisionOption.replaceExisting).then(function (newFile) {
                    Windows.Storage.FileIO.writeBufferAsync(newFile, decode).then(function (s) {
                        res.filePath = newFile.path;
                        WinContainer.successCallback(res);
                    });
                });
            }
            catch (e) {
                WinContainer.failureCallback(res.id, "APZ-CNT-064", e.message);
                WinContainer.Log.error(e.description);
            }
        }
        else {
            try {
                localFolder.createFolderAsync(filePath, Windows.Storage.CreationCollisionOption.openIfExists).then(function (folder) {
                    folder.createFileAsync(fileName, Windows.Storage.CreationCollisionOption.replaceExisting).then(function (newFile) {
                        Windows.Storage.FileIO.writeBufferAsync(newFile, decode).then(function (s) {
                            res.fileName = newFile.name;
                            res.filePath = newFile.path;
                            var extension = newFile.name.slice((newFile.name.lastIndexOf(".") - 1 >>> 0) + 2);
                            if (extension == "pdf" || extension == "PDF" || extension == "Pdf") {
                                var j = {};
                                j.filePath = res.filePath;
                                j.id = req.id;
                                Windows.Storage.StorageFile.getFileFromPathAsync(newFile.path).then(function (file) {
                                    Windows.System.Launcher.launchFileAsync(file).then(function (done) {
                                        WinContainer.successCallback(req);
                                    }, function (e) {
                                        WinContainer.failureCallback(req.id, "APZ-CNT-064");
                                        WinContainer.Log.error(e.message);        //file open fail
                                    });
                                }, function (e) {
                                    WinContainer.failureCallback(req.id, "APZ-CNT-006");
                                    WinContainer.Log.error(e.message);
                                });
                            } else {
                                WinContainer.successCallback(res);
                            }
                        });
                    });
                });
            }
            catch (e) {
                WinContainer.failureCallback(res.id, "APZ-CNT-074", e.message);
                WinContainer.Log.error(e.message);
            }
        }
    }

    var _createFile = function (req) {
        var res = {};
        res.id = req.id;

        var folderName = req.filePath ? req.filePath.replace(/\//g, "\\") : "app";
        var fileName = req.fileName;
        var fileContent = req.fileContent ? req.fileContent : false;
        if (!fileContent) {
            WinContainer.failureCallback(req.id, ErrorCode.FILE_CONTENT_EMPTY, "empty content file");
            return;
        }
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        localFolder.createFolderAsync(folderName, Windows.Storage.CreationCollisionOption.openIfExists).then(function (folder) {
            folder.createFileAsync(fileName, Windows.Storage.CreationCollisionOption.replaceExisting).then(function (file) {
                Windows.Storage.FileIO.writeTextAsync(file, fileContent).then(function (write) {
                    WinContainer.successCallback(res);
                });

            });
        }, function (e) {
            WinContainer.failureCallback(res.id, 'APZ-CNT-191', e.message);
            WinContainer.Log.error(e.message);
        });
    }

    var _deleteFile = function (req) {
        var res = {};
        res.id = req.id;
        var folderName = req.filePath ? req.filePath.replace(/\//g, "\\") : "app";
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
     
        localFolder.getFileAsync(folderName).then(function (file) {
                file.deleteAsync().then(function (success) {
                    WinContainer.successCallback(res);
                });
            }, function (e) {
                WinContainer.failureCallback(res.id, 'APZ-CNT-070', e.message);
                WinContainer.Log.error(e.message);
            });
        }

    var _getContent = function (req) {
        var res = {};
        res.id = req.id;
        var folderName = req.filePath ? req.filePath.replace(/\//g, "\\") : "app";
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        localFolder.getFileAsync(folderName).then(function (file) {
                Windows.Storage.FileIO.readTextAsync(file).then(function (fileContent) {
                    res.text = res.content = fileContent;
                    WinContainer.successCallback(res);
                }, function (e) {
                    WinContainer.failureCallback(res.id, 'APZ-CNT-010', e.message);
                    WinContainer.Log.error(e.message);
                });
            }, function (e) {
                WinContainer.failureCallback(res.id, 'APZ-CNT-070', e.message);
                WinContainer.Log.error(e.message);
            });
        }

    var _getFileSize = function (req) {
        var res = {};
        res.id = req.id;
        var initalfilepath = req.filePath;
        var filepath;
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        initalfilepath = initalfilepath.replace(new RegExp('/', 'g'), "\\");
        if (initalfilepath.search(new RegExp("ms-appdata:", 'g')) != -1) {
            filepath = initalfilepath.replace("ms-appdata:\\\\\\local", localFolder.path);

        }
        else {
            filepath = localFolder.path + "\\" + initalfilepath;
        }
        Windows.Storage.StorageFile.getFileFromPathAsync(filepath).then(function (file) {
            file.getBasicPropertiesAsync().then(function (props) {
                res.fileSize = props.size / 1024;
				res.text = res.fileSize;
                if (req.id == "internal")
                    return props.size;
                WinContainer.successCallback(res);
            }, function (e) {
                WinContainer.failureCallback(res.id, ErrorCode.FILE_SIZE_GET_FAIL);
                WinContainer.Log.error(e.message);
            });
        }, function (e) {
            WinContainer.failureCallback(res.id, ErrorCode.FILE_FETCH_FAIL);
            WinContainer.Log.error(e.message);
        });
    }
    var _openfile = function (req){
        var filePath = req.filePath;
        filePath = filePath.replace(new RegExp('/', 'g'), "\\");
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        
       var launch = localFolder.path + "\\" + filePath;
        Windows.Storage.StorageFile.getFileFromPathAsync(launch).then(function(file){
            Windows.System.Launcher.launchFileAsync(file).then(function (don) {
            WinContainer.successCallback(req);
        }, function (e) {
            WinContainer.failureCallback(req.id, "APZ-CNT-236");             //file open fail
            WinContainer.Log.error(e.message);
        });
        }, function (e)
        {
            WinContainer.failureCallback(req.id, "APZ-CNT-006");
            WinContainer.Log.error(e.message);
        });
    }
    var _fileRead = function (req) {
        var filePath = req.filePath;
        filePath = filePath.replace(new RegExp('/', 'g'), "\\");
        var localFolder = Windows.Storage.ApplicationData.current.localFolder;
        var launch = localFolder.path + "\\" + filePath;
        Windows.Storage.StorageFile.getFileFromPathAsync(launch).then(function (file) {
            Windows.System.Launcher.launchFileAsync(file).then(function (don) {
                if (don)
                    WinContainer.successCallback(req);
                else {
                    WinContainer.failureCallback(req.id, "APZ-CNT-236");              //file open fail
                    WinContainer.Log.warn("File Open Fail");
                }
            }, function (e) {
                WinContainer.failureCallback(req.id, "APZ-CNT-236");             //file open fail
                WinContainer.Log.error(e.message);
            });
        }, function (e) {
            WinContainer.failureCallback(req.id, "APZ-CNT-006");
            WinContainer.Log.error(e.message);
        });
    }
    return {
        upload: _upload,
        download: _download,
        uploadWS: _uploadWithoutSession,
        downloadWS: _downloadWithoutSession,
        fileToBase64: _fileToBase64,
        base64ToFile: _base64ToFile,
        create: _createFile,
        del: _deleteFile,
        content: _getContent,
        folderPickerCallback: folderPickerCB,
        getfilesize: _getFileSize,
        openfile: _openfile,
        fileread: _fileRead
    }
})();
