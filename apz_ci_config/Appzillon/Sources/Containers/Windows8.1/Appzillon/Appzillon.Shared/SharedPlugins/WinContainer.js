var WinContainer = {};
WinContainer.plugin = {};
var keybd_id = "";
var ort_id = "";
var devgrp_set = false;
var devgrp = {};
WinContainer.successCallback = function (res) {
    res.status = true;
    Apz.nativeServiceCB(res);
};
//-----Id & Error Code-----------
WinContainer.failureCallback = function (id, errorCode) {
    var res = {};
    res.id = id;
    res.errorCode = errorCode;
    res.status = false;
    Apz.nativeServiceCB(res);
};
/*Apz.callNative = function (req) {
    WinContainer.notif(req);
};*/
//--------------NOTIFICATION--------
WinContainer.setCursorBusy = function () {
    MSApp.execUnsafeLocalFunction(function () {
        var $input = $('<div id="preloader" style=" position:fixed; opacity:0.8; top:0; left:0; right:0; bottom:0; background-color:#fff; z-index:100000;"><div id="status" style="width:200px; height:200px; position:absolute; left:50%; top:50%; background-image:url(styles/loader.gif); background-repeat:no-repeat; background-position:center; margin:-100px 0 0 -100px;">&nbsp;</div></div>');
        $input.appendTo($("body"));//styles/default/img/loader.gif
    });
};

WinContainer.setCursorNormal = function () {
    $("#preloader").remove();
};

//-----------------OTP--------------
WinContainer.getDeviceType = function () {
    return WinContainer.isPhone() ? "WIN8.1PHONE" : "WIN8.1SURFACE";
};
WinContainer.deviceId = function () {
    var token = Windows.System.Profile.HardwareIdentification.getPackageSpecificToken(null);
    return Windows.Security.Cryptography.CryptographicBuffer.encodeToBase64String(token.id);
};
WinContainer.serverToken = function () {
    return WinContainer.Settings.appProperties["serverToken"];
};
//-----------OTP-----------------------------------------------
//-----------Startup-------------------------------------------
WinContainer.start = function () {
    switch (appprops.logLevel) {
        case "D":
            WinContainer.LogLevel.CURRENT = 4;
            break;
        case "I":
            WinContainer.LogLevel.CURRENT = 3;
            break;
        case "W":
            WinContainer.LogLevel.CURRENT = 2;
            break;
        case "E":
            WinContainer.LogLevel.CURRENT = 1;
            break;
        case "F":
            WinContainer.LogLevel.CURRENT = 0;
            break;
        default:
            WinContainer.LogLevel.CURRENT = -1;
            break;
    }
    var localSetting = Windows.Storage.ApplicationData.current.localSettings;
    if (localSetting.values["APPZILLONWIPEOUT"])
        window.close();
    else {
        function sendRequest() {

            if (WinContainer.Settings.appProperties["trackLocation"].value == "Y") {
                Windows.Devices.Geolocation.Geolocator().getGeopositionAsync().then(function (cord) {
                    WinContainer.multiFactorRequest(cord.coordinate.latitude, cord.coordinate.longitude);
                }, function (e) {
                    WinContainer.multiFactorRequest('', '');
                });
            } else {
                WinContainer.multiFactorRequest('', '');
            }
        }
        sendRequest();
        WinContainer.appExpiry();
        WinContainer.checkFirstRun = function () {
            var setting = Windows.Storage.ApplicationData.current.localSettings;
            var firstRanFlag = setting.values["FIRSTRUNFLAG"];
            if (firstRanFlag == null || firstRanFlag == undefined) {
                WinContainer.Log.info("First Run");
                WinContainer.createDatabase();
                WinContainer.CopyDatabase();
                WinContainer.CopyStaticFiles();
                WinContainer.createStorageFolders();
                setting.values["FIRSTRUNFLAG"] = true;
                WinContainer.Log.info("First Run initializations complete");
            }
        }
        WinContainer.checkFirstRun();
    }
}
WinContainer.setDeviceOrientation = function () {
    WinContainer.fetchDeviceInfo(function (json) {
        WinContainer.setOrientation(json);
    });
}
WinContainer.createDatabase = function () {
    var dbjson = {};
    dbjson.notes = 'CREATE TABLE Notes (id TEXT PRIMARY KEY, notes TEXT)';
    dbjson.offlinedata = 'create table tb_asmi_offline_data(refno integer primary key autoincrement,userid varchar,screenid varchar,interfaceid varchar,interfacepayload varchar,screenpayload varchar,screenresponse varchar,status varchar,appjsonstr varchar,appreqbody varchar, starttime varchar, endtime varchar)';
    dbjson.template = 'CREATE TABLE TB_ASMI_TEMPLATE_DATA (TEMPLATEREFNO INTEGER PRIMARY KEY AUTOINCREMENT, TEMPLATENAME VARCHAR, SCREENID VARCHAR, SCREENPAYLOAD VARCHAR)';
    dbjson.appsDB = 'APPSDB';
    var createDB = WinContainer.isPhone() ? WinContainer.phonecreateDatabase : WinContainer.surfacecreateDatabase;
    createDB(dbjson);
}
WinContainer.phonecreateDatabase = function (json) {
    WinContainer.store("PLUGIN.DEVICETYPE", "WIN8.1PHONE");
    var dataB = new SQLiteWinRT.Database(Windows.Storage.ApplicationData.current.localFolder, json.appsDB);
    dataB.openAsync().then(function () {
        dataB.executeStatementAsync(json.notes)
            .then(function () {
                dataB.executeStatementAsync(json.offlinedata)
                    .then(function () {
                        dataB.executeStatementAsync(json.template)
                            .then(function () {
                                dataB.close();
                            });
                    }, function (e) {
                        dataB.close();
                    });
            }, function (e) {
                dataB.close();
            });
    }, function (e) {
        dataB.close();
    });
}
WinContainer.surfacecreateDatabase = function (json) {
    try {
        var dbPath = Windows.Storage.ApplicationData.current.localFolder.path + "\\" + json.appsDB;
        SQLite3JS.openAsync(dbPath)
          .then(function (db) {
               db.runAsync(json.notes)
              .then(function () {
                  db.close();
                  SQLite3JS.openAsync(dbPath)
                      .then(function (db) {
                           db.runAsync(json.offlinedata)
                          .then(function () {
                              db.runAsync(json.template)
                                  .then(function () {
                                      db.close();
                                  });

                          }, function (err) {
                              try {
                                  db.close();
                              }
                              catch (e) {
                              }
                          });
                      });

              }, function (err) {
                  try {
                      db.close();
                  }
                  catch (e) {
                  }
              });
          });
    } catch (e) {
    }
}
WinContainer.createStorageFolders = function () {

    var localFolder = Windows.Storage.ApplicationData.current.localFolder;
    var video = localFolder.createFolderAsync("video", Windows.Storage.CreationCollisionOption.openIfExists);
    var audio = localFolder.createFolderAsync("audio", Windows.Storage.CreationCollisionOption.openIfExists);
    var photo = localFolder.createFolderAsync("photo", Windows.Storage.CreationCollisionOption.openIfExists);
    var docs = localFolder.createFolderAsync("docs", Windows.Storage.CreationCollisionOption.openIfExists);
}
WinContainer.CopyStaticFiles = function () {
    var setting = Windows.Storage.ApplicationData.current.localSettings;
    var check = setting.values["APPZILLONCOPYSTATICFILE"];
    if (check)
        return;
    try {
        var filepath = apz.getConfigPath() + "/" + "Files.json";        
        var parsedfile = WinContainer.getFile(filepath);
        //var parsedfile = JSON.parse(fileoutput);
        var jsonarry = parsedfile.Files;
        var installFolder = Windows.ApplicationModel.Package.current.installedLocation;
        localFolder = Windows.Storage.ApplicationData.current.localFolder;
        var staticFolder = 'apps\\' + WinContainer.Settings.containerProperties.MAINAPPID + '\\staticfiles';
        installFolder.getFolderAsync(staticFolder).then(function (folder) {

            folder.getFilesAsync().then(function (files) {
                if (files == null) {
                    setting.values["APPZILLONCOPYSTATICFILE"] = true;
                    return;
                }
                setting.values["APPZILLONCOPYSTATICFILE"] = true;
                files.forEach(function (result) {
                    if (jsonarry[result.name] == '')
                        result.copyAsync(localFolder);
                    else
                        localFolder.createFolderAsync(jsonarry[result.name].replace(/\//g, "\\").substring(0, 1) == "\\" ? jsonarry[result.name].replace(/\//g, "\\").substring(1) : jsonarry[result.name].replace(/\//g, "\\"), Windows.Storage.CreationCollisionOption.openIfExists).then(function (newfolder) {
                            result.copyAsync(newfolder);
                        });
                });
            });
        });
    }
    catch (e) {
        setting.values["APPZILLONCOPYSTATICFILE"] = true;
    }
}
WinContainer.CopyDatabase = function () {
    Windows.Storage.ApplicationData.current.localFolder.getFolderAsync('temp').then(function (f) {
        f.deleteAsync();
    }, function (e) { });
    var installFolder = Windows.ApplicationModel.Package.current.installedLocation;
    var localFolder = Windows.Storage.ApplicationData.current.localFolder;
    installFolder.getFolderAsync("sqlite").then(function (folder) {
        CopyDatabaseFiles(folder, localFolder);
    },
    function (folder) {
        console.log("folder not found: " + folder.name);
        return;
    })
    function CopyDatabaseFiles(folder, localFolder) {
        if (folder == null)
            return;
        folder.getFilesAsync().then(function (files) {
            if (files != null) {
                files.forEach(function (result) {
                    WinContainer.Log.debug("copying to sandbox file : " + result.displayName);
                    result.copyAsync(localFolder);
                });
            }
        });
    }
}
WinContainer.appExpiry = function () {
    var val = "";
    var applicationData = Windows.Storage.ApplicationData.current;
    var localSettings = applicationData.localSettings;
    var val = localSettings.values["APPEXPIRED"];
    if (val == null) {
        var app_exp = WinContainer.Settings.containerProperties["APPEXPIRED"];
        if (app_exp == "Y") {
            localSettings.values["APPEXPIRED"] = "Y";
            var msg = Windows.UI.Popups.MessageDialog("Your App has expired");
            msg.showAsync().then(function () {
                window.close();
            });
        }
        else {
            localSettings.values["APPEXPIRED"] = "N";
        }
    }

    else if (val == "Y") {
        var msg = Windows.UI.Popups.MessageDialog("Your App has expired");
        msg.showAsync().then(function () {
            window.close();
        });
    }
    else {
        var dateString = WinContainer.Settings.appProperties["expiryDate"];
        if (dateString == undefined) {
            Windows.UI.Popups.MessageDialog("No app expirydate found");
            return;
        }
        var date = dateString.split("/");

        try {
            var final_date = new Date(date[2], date[1] - 1, date[0]);
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth(); //January is 0!

            var yyyy = today.getFullYear();

            var final_today = new Date(yyyy, mm, dd, 0, 0, 0, 0);
            if (final_date < final_today) {
                localSettings.values["APPEXPIRED"] = "Y";
                var msg = Windows.UI.Popups.MessageDialog("Your App has expired");
                msg.showAsync().then(function () {
                    window.close();
                });
            }
        }
        catch (e) {
            var msg = Windows.UI.Popups.MessageDialog("App expiry date is not matching");
            msg.showAsync().then(function () {
                window.close();
            });
        }
    }
}
WinContainer.wipeOut = function (json) {
    try {
        var applicationData = Windows.Storage.ApplicationData.current;
        var localSettings = applicationData.localSettings;

        applicationData.clearAsync().done(function () {
            for (var key in localStorage)
                localStorage.removeItem(key);

            localSettings.values.remove("APPEXPIRED");
            localSettings.values["APPEXPIRED"] = "Y";
            localSettings.values["APPZILLONWIPEOUT"] = true;
            try {
                MSApp.terminateApp();
            } catch (e) { }
            window.close();
        }, function (e) {
            setTimeout(function () { WinContainer.wipeOut(json); }, 500);
        });
    }
    catch (e) {
        WinContainer.failureCallback(json.id, "APZ-CNT-224");       //wipeout failed
    }
}
WinContainer.multiFactorRequest = function (lat, long) {
    try {
        var localSettings = Windows.Storage.ApplicationData.current.localSettings;
        var isMFDone = localSettings.values['MULTIFACTOR'];
        var wipeOut = localSettings.values["APPZILLONWIPEOUT"];
        if (wipeOut) {
            window.close();
            return;
        }
        if (isMFDone) {
            WinContainer.appMasterRequest(WinContainer.Settings.appProperties["appId"]);
            return;
        }
        else {
            var easClientDeviceInformation = new Windows.Security.ExchangeActiveSyncProvisioning.EasClientDeviceInformation();
            var deviceId = WinContainer.deviceId();
            var appId = WinContainer.Settings.appProperties["appId"];
            var os = easClientDeviceInformation.operatingSystem.toUpperCase();
            var model = easClientDeviceInformation.systemProductName;
            var width = screen.width;
            var height = screen.height;
            var resolution = width + "X" + height;
            var deviceInformation = Windows.Networking.Connectivity.NetworkInformation.getHostNames();
            var deviceName = deviceInformation[0].displayName;

            WinContainer.storeLog("Sending MultiFactor Request", "D");
            var obj = {
                "appzillonHeader": {
                    "preLogin": "true",
                    "appId": appId,
                    "screenId": "lauchApp",
                    "requestKey": "000NEW",
                    "interfaceId": "appzillonDeviceRegistration",
                    "status": true,
                    "sessionId": "",
                    "deviceId": deviceId,
                    "userId": "windows",
                    "longitude": long,
                    "latitude": lat,
                    "origination": WinContainer.getIP(),
                    "source": "APPZILLON"
                },
                "appzillonBody": {
                    "deviceRegisterRequest": {
                        "appId": appId,
                        "os": os,
                        "osVersion": '8.1',
                        "deviceId": deviceId,
                        "deviceName": deviceName,
                        "mobile1": "",
                        "mobile2": "",
                        "model": model,
                        "make": model,
                        "screenResolution": resolution,
                        "longitude": long,
                        "latitude": lat
                    }
                }
            };

            var json = JSON.stringify(obj);
            var linternalserverurl = WinContainer.Settings.appProperties["serverUrl"];

            $.ajax({

                url: linternalserverurl,
                type: "POST",
                cache: false,
                data: json,
                dataType: 'json',
                contentType: "application/json",
                success: function (presp) {
                    if (presp.appzillonHeader.status == true || presp.appzillonErrors[0].errorCode == 'APZ-DM-039')
                        localSettings.values['MULTIFACTOR'] = true;
                    Debug.writeln("response ::::  " + JSON.stringify(presp));
                    var appId = WinContainer.Settings.appProperties["appId"];
                    WinContainer.appMasterRequest(appId);
                },
                error: function (e) {
                    WinContainer.Log.error('MultifactorRegistraion Status=' + e.status + ' StatusText=' + e.statusText);
                }
            });
        }
    } catch (e) {
        WinContainer.Log.error(e.description);
    }
}
WinContainer.appMasterRequest = function (appId, getInstruction) {

    try {
        var easClientDeviceInformation = new Windows.Security.ExchangeActiveSyncProvisioning.EasClientDeviceInformation();
        var os = easClientDeviceInformation.operatingSystem.toUpperCase();
        var data = {};

        data.appzillonHeader = {};
        data.appzillonHeader.preLogin = "true";
        data.appzillonHeader.appId = appId;
        data.appzillonHeader.deviceId = WinContainer.deviceId();
        data.appzillonHeader.sessionId = "";
        data.appzillonHeader.userId = "windows";
        data.appzillonHeader.status = true;
        data.appzillonHeader.requestKey = "";
        data.appzillonHeader.interfaceId = "appzillonGetAppMasterDetails";
        data.appzillonHeader.screenId = "login";
        data.appzillonHeader.origination = WinContainer.getIP();
        data.appzillonHeader.source = "APPZILLON";

        data.appzillonBody = {};
        data.appzillonBody.appzillonAppMasterRequest = {};
        data.appzillonBody.appzillonAppMasterRequest.appId = appId;
        data.appzillonBody.appzillonAppMasterRequest.deviceId = WinContainer.deviceId();
        data.appzillonBody.appzillonAppMasterRequest.os = os;
        var appH = JSON.stringify(data);
        var server = WinContainer.Settings.appProperties["serverUrl"];
        $.ajax({
            url: server,
            type: "POST",
            cache: false,
            data: appH,
            dataType: 'json',
            contentType: "application/json",
            success: function (presp) {
                WinContainer.Notification.initnotification();
                var pstatus = presp.appzillonHeader.status;
                if (pstatus == true) {

                    var localSettings = Windows.Storage.ApplicationData.current.localSettings;
                    var jsonRes = presp.appzillonBody[appId];
                    try {
                        if (presp.appzillonBody[appId].expired == 'Y') {
                            localSettings.values["APPEXPIRED"] = "Y";
                            WinContainer.appExpiry();
                            return;
                        }
                        else
                            localSettings.values["APPEXPIRED"] = "N";
						if(presp.appzillonBody[appId].wipeout == 'Y')
						{
							WinContainer.wipeOut();
							return;
						}
                        if (presp.appzillonBody[appId].remoteDebug == 'Y') 
                            WinContainer.LogLevel.CURRENT = 4;
                        if (presp.appzillonBody[appId].remoteDebug == 'N')
                            WinContainer.LogLevel.CURRENT = 0;

                    } catch (e) {
               
                    }
                    try {
                        if (getInstruction.instruction) {
                            jsonRes.id = getInstruction.id;
                            WinContainer.successCallback(jsonRes);
                        }
                        //  else
                        // appzillon.util.instructions(jsonRes);
                    } catch (e) {
                    }
                } else {
                    try {
                        if (getInstruction.instruction) {
                            var eCode = presp.appzillonErrors[0] ? presp.appzillonErrors[0].errorCode : false;
                            if (eCode) {
                                WinContainer.failureCallback(getInstruction.id, eCode);
                            }
                        }
                    } catch (e) { }
                }
            },
            error: function (e) {
                try {
                    if (getInstruction.instruction) {
                        WinContainer.failureCallback(getInstruction.id, "APZ-CNT-258");
                    }
                    WinContainer.Log.error('AppmasterDetails Status=' + e.status + ' StatusText=' + e.statusText);
                } catch (e) { }
            }
        });
    } catch (e) {
        WinContainer.Log.error(e.description);
    }
}
//-----------Startup-------------------------------------------
replaceAll = function (src, search, replacement) {
    return src.split(search).join(replacement);
};

WinContainer.otpRequired = function () {
    return WinContainer.Settings.appProperties["otpReqd"];
};
WinContainer.startKeybdLstn = function (show) {
    var keybd = Windows.UI.ViewManagement.InputPane.getForCurrentView();
    keybd.onhiding = onhidekbd;
    keybd.onshowing = onkbdshow;
    keybd_id = show.id;
    show.event = "started";
    show.keepAlive = true;
    WinContainer.successCallback(show);
};
onhidekbd = function (evt) {
    if (keybd_id != "") {
        var js = {};
        js.id = keybd_id;
        js.event = "hide";
        js.keepAlive = true;
        WinContainer.successCallback(js);
    }
}
onkbdshow = function (evt) {
    if (keybd_id != "") {
        var js = {};
        js.id = keybd_id;
        js.event = "show";
        js.keepAlive = true;
        evt.handled = true;
        // evt.preventDefault();
        WinContainer.successCallback(js);
    }
    return true;
}
WinContainer.stopKeybdLstn = function (hide) {
    var keybd = Windows.UI.ViewManagement.InputPane.getForCurrentView();
    keybd_id = "";
    keybd.removeEventListener("hiding", onhidekbd);
    keybd.removeEventListener("showing", onkbdshow);
    hide.event = "stopped";
    WinContainer.successCallback(hide);
};
WinContainer.getOTPDateTime = function () {
    function dateToString(date) {
        var weekday = new Array("Sun", "Mon", "Tue", "Wed", "Thu",
                    "Fri", "Sat");
        var dateOfString = weekday[date.getDay()] + ", ";


        var month = date.getMonth() + 1;
        var day = date.getDate();
        dateOfString += (("" + day).length < 2 ? "0" : "") + day + " ";
        dateOfString += (("" + month).length < 2 ? "0" : "") + month + " ";
        dateOfString += date.getFullYear();
        return dateOfString;
    }
    var currentdate = new Date();
    var datetime = "";
    datetime += dateToString(currentdate);
    var cur_hours = currentdate.getHours();
    var cur_mins = currentdate.getMinutes();
    var cur_secs = currentdate.getSeconds();
    datetime += " ";
    datetime += (("" + cur_hours).length < 2 ? "0" : "") + cur_hours + ":";
    datetime += (("" + cur_mins).length < 2 ? "0" : "") + cur_mins + ":";
    datetime += (("" + cur_secs).length < 2 ? "0" : "") + cur_secs;
    return datetime;
};
//To calculate the hash value using sha256 algorithm.
WinContainer.hashSHA256 = function (param1, param2) {
    var param = param1 + param2;
    var hashValue = ShaCode.HashSHA256.sha256(param);
    return hashValue;
};
WinContainer.generateOTP = function (req) {
    var res = {};
    res.id = req.id;
    var user = req.userId;
    var pin = req.pwd;
    var hashSHA256 = new ShaCode.HashSHA256();
    var udid = WinContainer.deviceId();
    var datetime = req.date;

    var serverToken = req.serverToken;
    var salt = user + serverToken;
    if (serverToken == undefined) {
        salt = req.salt;
    }
    // To get the encrypted pin using SHA256 algorithm.
    var encryptedPin = WinContainer.hashSHA256(pin, salt);

    // To get final OTP using SHA256 algorithm.
    var otp = hashSHA256.hashValue("", "", "", user, encryptedPin, datetime);
    res.text = otp;
    // res.loginTimeStamp = datetime;
    WinContainer.successCallback(res);
};
//--------------Infra dependency----
WinContainer.getScreenId = function () {
    try
    {
        return apz.currScr;
    } catch (e) {
        // var src = apz.data.scrdata();
        return "";
    }
};
WinContainer.getUserId = function () {
    try {
        return apz.currScr;
    } catch (e) {

        return "";
    }
};
WinContainer.getServerHeader = function (interfaceId, scrid, sessionid, async) {
    try {
        return apz.server.getHeader("appzillonUploadFile", scrid, "uploadReqId", true);
    } catch (e) {
        return "error occured while runnning getHeader";
    }
};
WinContainer.storeServerSession = function (key, session) {
    WinContainer.store('SERVER.REQUESTKEY', key);
    WinContainer.store('SERVER.SESSIONID', session);
};
WinContainer.store = function (key, value) {
    localStorage.setItem(key, value);
}
WinContainer.retrieve = function (key) {
    return localStorage.getItem(key);
}
WinContainer.storeLog = function ()
{ }
WinContainer.fetchDeviceInfo = function (callback) {
    if (devgrp_set) {
        callback(devgrp);
        return;
    } else {
        Windows.Storage.StorageFile.getFileFromApplicationUriAsync(new Windows.Foundation.Uri("ms-appx:///apps/" + WinContainer.Settings.appProperties.appId + "/screens/config/devicegroups.json")).then(function (file) {
            Windows.Storage.FileIO.readTextAsync(file).then(function (text) {
                fileout = text;
                var js = JSON.parse(fileout);
                if (js == null || js == undefined)
                    return;
                else if (js.deviceGroups.length == 1) {
                    devgrp.orientation = js.deviceGroups[0].orientation;
                    devgrp.name = js.deviceGroups[0].name;
                    devgrp_set = true;
                    callback(devgrp);
                }
                else {
                    var json;
                    var wt = window.screen.availWidth;
                    var ht = window.screen.availHeight;
                    var hmin;
                    var wmin;
                    var psum;
                    var arr = js.deviceGroups;
                    for (var i = 0; i < arr.length; i++) {
                        var cdw = arr[i].width - wt;
                        var cdh = arr[i].height - ht;
                        var csum = Math.abs(cdw) + Math.abs(cdh);
                        if (i == 0) {
                            psum = csum;
                            json = arr[i];
                            hmin = cdh;
                            wmin = cdw;
                        }
                        else {
                            if (csum < psum) {
                                psum = csum;
                                json = arr[i];
                                hmin = cdh;
                                wmin = cdw;
                            }
                            else if (csum == psum) {
                                if ((cdh > 0 || cdw > 0) && (hmin < 0 && wmin < 0)) {
                                    json = arr[i];
                                    hmin = cdh;
                                    wmin = cdw;
                                }
                            }
                        }
                    }

                    devgrp.orientation = json.orientation;
                    devgrp.name = json.name;
                    devgrp_set = true;
                    callback(devgrp);
                }
            }, function (erro) {
                WinContainer.Log.error(erro.description);
                devgrp.orientation = "";
                devgrp.name = "";
                devgrp_set = false;
                callback(devgrp);
            })

        }, function (err) {
            devgrp.orientation = "";
            devgrp.name = "";
            devgrp_set = false;
            callback(devgrp);
            WinContainer.Log.error(err.description);
        });
    }
}


WinContainer.GetDeviceInfo = function (obj) {
    try {
        // var callback = "appzillon.data.getDeviceInfoCB";
        var setting = {};
        setting.deviceId = WinContainer.deviceId();
        setting.deviceOs = new Windows.Security.ExchangeActiveSyncProvisioning.EasClientDeviceInformation().operatingSystem;
        setting.screenPpi = Windows.Graphics.Display.DisplayProperties.logicalDpi;
        setting.screenSize = (window.screen.availWidth) + "X" + (window.screen.availHeight);
        WinContainer.fetchDeviceInfo(function (json) {
            setting.deviceType = WinContainer.isPhone() ? "WIN8.1PHONE" : "WIN8.1SURFACE";
            setting.deviceGroup = json.name;

            if (Windows.Graphics.Display.DisplayInformation.autoRotationPreferences > 0) {
                setting.lockRotation = true;
                setting.orientation = json.orientation;
            }
            else {
                setting.lockRotation = false;
                var orientation = Windows.Graphics.Display.DisplayInformation.getForCurrentView().currentOrientation;
                if (orientation == 1 || orientation == 4)
                    setting.orientation = "LANDSCAPE";
                else if (orientation == 2 || orientation == 8)
                    setting.orientation = "PORTRAIT";
            }
            setting.OTAREQUIRED = WinContainer.Settings.containerProperties["OTAREQUIRED"];
            setting.HASHKEY1 = setting.deviceId;
            setting.HASHKEY2 = "";
            setting.id = obj.id;
            WinContainer.successCallback(setting);
        });

    }
    catch (ex) {
        WinContainer.failureCallback(obj.id, "APZ-CNT-103");
    }
}

//--------------API-----------------
WinContainer.hideSplash = function () {
    var splashdiv = document.getElementById("splashDiv");
    splashdiv.style.display = "none";
    document.body.style.overflowY = 'visible';
    document.body.style.overflowY = 'auto';
}
WinContainer.getAbsolutePath = function (p) {
    if (path.indexOf("ms-appdata:") >= 0) {
        return path;
    }
    return 'ms-appdata:///local\\' + path.replace(/\//g, '\\').replace(/\\\\/g, '\\');
};
WinContainer.getLocation = function (req) {
    Windows.Devices.Geolocation.Geolocator().getGeopositionAsync().then(function (cord) {
        var location = {};
        location.id = req.id;
        location.latitude = cord.coordinate.latitude;
        location.longitude = cord.coordinate.longitude;
        WinContainer.successCallback(location);
    }, function (e) {
        WinContainer.failureCallback(req.id, "APZ-CNT-092");
    });
};
WinContainer.terminate = function () {
    window.close();
};
WinContainer.setOrientation = function (Object) {
    try {
        var orientation = Object["orientation"];
        var neworientation = orientation.substring(0, 3).toUpperCase();
        if (neworientation == "POR") {
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences = Windows.Graphics.Display.DisplayOrientations.portrait;
        }
        else if (neworientation == "LAN") {
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences = Windows.Graphics.Display.DisplayOrientations.landscape;
        }
        else {
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences = Windows.Graphics.Display.DisplayOrientations.none;
        }
        WinContainer.Settings.containerProperties["ortchosen"] = neworientation;
        Object.status = true;
        WinContainer.successCallback(Object);
    } catch (e)
    { }
};
WinContainer.lockRotation = function (jsonObj) {
    var res = {};
    res.id = jsonObj.id;
    try {
        var orientation = Windows.Graphics.Display.DisplayProperties.currentOrientation;
      
        if (orientation == Windows.Graphics.Display.DisplayOrientations.portrait) {
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences = Windows.Graphics.Display.DisplayOrientations.portrait;
            WinContainer.Settings.containerProperties["ortchosen"] = "POR";
            var data = JSON.stringify({ currentOrientation: "Portrait" }, null, " ");
            var json = JSON.parse(data);
            json.id = res.id;
            WinContainer.successCallback(json);
        }
        else if (orientation == Windows.Graphics.Display.DisplayOrientations.portraitFlipped) {
            WinContainer.Settings.containerProperties["ortchosen"] = "POR";
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences = Windows.Graphics.Display.DisplayOrientations.portraitFlipped;

            var data = JSON.stringify({ currentOrientation: "PortraitFlipped" }, null, " ");
            var json = JSON.parse(data);
            json.id = res.id;
            WinContainer.successCallback(json);
        }
        else if (orientation == Windows.Graphics.Display.DisplayOrientations.landscape) {
            WinContainer.Settings.containerProperties["ortchosen"] = "LAN";
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences = Windows.Graphics.Display.DisplayOrientations.landscape;
            var data = JSON.stringify({ currentOrientation: "Landscape" }, null, " ");
            var json = JSON.parse(data);
            json.id = res.id;
            WinContainer.successCallback(json);
        }
        else if (orientation == Windows.Graphics.Display.DisplayOrientations.landscapeFlipped) {
            WinContainer.Settings.containerProperties["ortchosen"] = "LAN";
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences = Windows.Graphics.Display.DisplayOrientations.landscapeFlipped;
            var data = JSON.stringify({ currentOrientation: "landscapeFlipped" }, null, " ");
            var json = JSON.parse(data);
            json.id = res.id;
            WinContainer.successCallback(json);
        }
    }
    catch (err) {
        WinContainer.Log.error(err.description);
        WinContainer.failureCallback(res.id, "APZ-CNT-082");
    }
};
WinContainer.unlockRotation = function (jsonObj) {
    var res = {};
    res.id = jsonObj.id;
    try {
        WinContainer.Settings.containerProperties["ortchosen"] = "ANY";
        Windows.Graphics.Display.DisplayProperties.autoRotationPreferences = Windows.Graphics.Display.DisplayOrientations.none;
        var data = JSON.stringify({ successMessage: "Successfully unlocked the rotation" }, null, " ");
        WinContainer.Log.info("Successfully unlocked the rotation");
        var json = JSON.parse(data);
        json.id = res.id;
        WinContainer.successCallback(json);
    }
    catch (err) {
        WinContainer.Log.error(err.description);
        WinContainer.failureCallback(res.id, "APZ-CNT-098");
    }
};
WinContainer.hideRefresh = function (json) {
    var pr = document.getElementById("progress");
    if (pr != undefined) {
        pr.style.top = "-50px";
        document.body.removeChild(pr);
        if (id != "") {
            WinContainer.successCallback(json);
        }
    }
    else {
        WinContainer.failureCallback(json.id, "APZ-CNT-082");
    }
};
WinContainer.LstnNotif = function (json) {
    notif_id = json.id;
    json.event = "started";
    json.keepAlive = true;
    WinContainer.successCallback(json);
};
WinContainer.LstnNotifstop = function (json) {
    notif_id = "";
    json.event = "stopped";
    WinContainer.successCallback(json);
}
WinContainer.GetAppInstruction = function (json) {
    var getInstruction = {};
    getInstruction.id = json.id;
    getInstruction.instruction = true;
    var app = json.appId;
    var url = WinContainer.Settings.appProperties.serverUrl;
    WinContainer.appMasterRequest(app, getInstruction);
}
WinContainer.vibrate = function (json) {
    if (!WinContainer.isPhone()) {
        WinContainer.failureCallback(id, ErrorCode.VIBRATE_NOT_SUPPORTED);
        return;
    }
    var id = json.id;
    var duration = parseInt(json.time) > 100 ? duration = parseInt(json.time) : 100;
    try {
        var vibrationDevice = Windows.Phone.Devices.Notification.VibrationDevice.getDefault();
        if (vibrationDevice) {
            if (duration >= 5000) {
                WinContainer.Log.info("Vibration Start");
                vibrationDevice.vibrate(5000);
            }
            else {
                WinContainer.Log.info("Vibration Start");
                vibrationDevice.vibrate(duration);
            }
            WinContainer.Log.info("Vibration End");
            var data = JSON.stringify({ successMessage: "Phone Vibrating" }, null, " ");
            var json = JSON.parse(data);
            json.id = id;
            WinContainer.successCallback(json);
        }
        else {
            WinContainer.Log.info("Vibrate not supported");
            WinContainer.failureCallback(id, ErrorCode.VIBRATE_NOT_SUPPORTED);
        }


    }
    catch (Exception) {
        WinContainer.Log.error(Exception.description);
        //  var data = JSON.stringify({ errorCode: "APZ-CNT-082" }, null, " ");
        //  var json = JSON.parse(data);
        WinContainer.failureCallback(id, ErrorCode.VIBRATION_REQUEST_FAIL);
    }


};
WinContainer.startOrtLstn = function (js) {
    var disp = Windows.Graphics.Display.DisplayInformation.getForCurrentView();
    ort_id = js.id;
    js.event = "started";
    disp.addEventListener("orientationchanged", onOrientationChanged);
    js.keepAlive = true;
	// Ruthvik 11-04-17 modified by adding js.orientaion to view current orientation
    var curortn = Windows.Graphics.Display.DisplayProperties.currentOrientation;
    if (curortn == '2' || curortn == '8')
    {
        js.orientation = "PORTRAIT";
    }
    if (curortn == '1' || curortn == '4')
    {
        js.orientation = "LANDSCAPE";
    }
    WinContainer.successCallback(js);
    WinContainer.Log.info("orientation listener started");
    WinContainer.Log.info(js.orientation);
};
function onOrientationChanged(evt) {
    if (ort_id != "") {
        var disp = Windows.Graphics.Display.DisplayInformation.getForCurrentView();
        var j = {};
        j.id = ort_id;
        j.event = "orientation_change";
        j.keepAlive = true;
        j.status = true;
        
        var orientation = disp.currentOrientation;
        if (orientation == 1 || orientation == 4)
            //Ruthvik 11-04-17 Changed LANDSCAPE from small to caps
            j.orientation = "LANDSCAPE";
        else if (orientation == 2 || orientation == 8)
            //Ruthvik 11-04-17 Changed PORTRAIT from small to caps
            j.orientation = "PORTRAIT";
        WinContainer.successCallback(j);
    }
};
//Ruthvik 18-04-17 modified by adding stopOrientationListener feature
WinContainer.stopOrientationListener = function (js) {
    var disp = Windows.Graphics.Display.DisplayInformation.getForCurrentView();
    disp.removeEventListener("orientationchanged", onOrientationChanged);
    js.event = "stopped";
    js.status = true;
    js.keepAlive = false;
    WinContainer.Log.info("Orientation Stopped");
    WinContainer.successCallback(js);
};
WinContainer.call = function (req) {
    if (!WinContainer.isPhone()) {
        WinContainer.failureCallback(req.id, 'APZ-CNT-022', "Device doenot Support call");
    } else {
        Windows.ApplicationModel.Calls.PhoneCallManager.showPhoneCallUI(req.phoneNo, "");
        req.status = true;
        WinContainer.successCallback(req);
    }
};
WinContainer.getMissedCalls = function (req) {
    WinContainer.failureCallback(req.id, 'APZ-CNT-022', "Device doenot Support SMS");
};
WinContainer.SMS = (function () {
    var sendSms = function (req) {
        if (!WinContainer.isPhone()) {
            WinContainer.failureCallback(req.id, 'APZ-CNT-022', "Device doenot Support SMS");
            return;
        }
        if (req.type == "BG") {
            WinContainer.failureCallback(req.id, 'APZ-CNT-245', "Background SMS is not supported");
            return;
        }
        var chat = Windows.ApplicationModel.Chat;
        var sms = new chat.ChatMessage();
        sms.body = req.message;
        if (req.phoneNo)
            sms.recipients.append(req.phoneNo);
        chat.ChatMessageManager.showComposeSmsMessageAsync(sms).done(function () {
            WinContainer.successCallback(req);
        });
        return {
            send: sendSms
        }
    }
    var receiveSms = function (req) {
      
        WinContainer.failureCallback(req.id, 'APZ-CNT-022', "Device does not Support SMS receive");
    }
    var startSmsListener = function (req) {
        WinContainer.failureCallback(req.id, 'APZ-CNT-022', "Device does not Support SMS listener");
    }
    var stopSmsListener = function (req) {
        WinContainer.failureCallback(req.id, 'APZ-CNT-022', "Device does not Support SMS listener");
    }
    var getInboxSms = function (req) {
        WinContainer.failureCallback(req.id, 'APZ-CNT-022', "Device doenot Support Inbox SMS");
    }
    return {
        send: sendSms,
        receive: receiveSms,
        startListener: startSmsListener,
        stopListener: stopSmsListener,
        getInbox: getInboxSms
    }
})();
WinContainer.youtube = function (json) {
    if (json.url.indexOf("youtube") != -1) {
        WinContainer.openUrl(json);
    } else {
        WinContainer.failureCallback(json.id, "APZ-CNT-131");
    }
};
WinContainer.openUrl = function (req) {
    try {
        var uri = new Windows.Foundation.Uri(req.url);
        Windows.System.Launcher.launchUriAsync(uri).then(function (s) {
            if (s) {
                WinContainer.successCallback(req);
            } else {
                WinContainer.failureCallback(req.id, "APZ-CNT-082");
            }
        });
    }
    catch (e) {
        WinContainer.failureCallback(req.id, "APZ-CNT-082");
        WinContainer.Log.error(e.description);
    }
};
WinContainer.getIP = function (req) {

    var networkInfo = Windows.Networking.Connectivity.NetworkInformation;
    var connectionProfile = networkInfo.getInternetConnectionProfile();
    var hosts = networkInfo.getHostNames();

    var res = {};
    res.id = "";
    if (req != null && req != undefined)
        res.id = req.id;
    res.ip = "";
    try {
        for (var i = 0; i < hosts.length; i++) {
            try {
                if (hosts[i].ipInformation != null) {
                    if (connectionProfile.networkAdapter.networkAdapterId == hosts[i].ipInformation.networkAdapter.networkAdapterId) {
                        res.ip = hosts[i].displayName;
                        if (req == undefined) {
                            return res.ip;
                        }
                    }
                }
            } catch (e) {
            }
        }
          if (req != undefined) {
                  if (res.ip !== "") {
                      WinContainer.successCallback(res);
                  }
                  else {
					  //Ruthvik 11-04-17 Changed to latest Error Code
                      WinContainer.failureCallback(req.id, "APZ-CNT-059");
                  }
          }
    }
    catch (e) {
           if (req != undefined) {
               WinContainer.failureCallback(req.id, "APZ-CNT-082", e.message);
                   WinContainer.Log.error(e.description);
           }else
           {
           return "";
           }
    }
};
WinContainer.getAppversion = function (req) {
    var package = Windows.ApplicationModel.Package.current;
    var id = package.id;
    var vr = id.version;
    if (req.appId != WinContainer.Settings.appProperties.appId)
    {
        WinContainer.failureCallback(req.id, "APZ-CNT-154");                     // OTA Unsupported and user tried to access app other than main app
    }
   // req.appVersion = vr.major + '.' + vr.minor + '.' + vr.build + '.' + vr.revision;
    req.appVersion = WinContainer.Settings.appProperties.appVersion;
    WinContainer.successCallback(req);
    return req.version;
};
//WinContainer.offlineData = function (query) {
//    jsonObj = {
//        "id" : "offlineID",
//        "successCallback": "WinContainer.offlineSuccess",
//        "failureCallback": "WinContainer.offlineFailure",
//        "databaseName" : "APPSDB",
//        "executeQuery" : query
//    }
//    WinContainer.executeSql(jsonObj);
//}

//WinContainer.offlineFailure = function (json) {
//  //  appzillon.offline.executeSqlCallBack('F');
//}

//WinContainer.offlineSuccess = function (json) {
//    if(json.sqlResult == "success") {
//     //   appzillon.offline.executeSqlCallBack('S');
//    }
//    else {
//        var res = json.sqlResult;
//        res=JSON.stringify(res);
//        res = res.replace(/\\\"/g, '\"');
//   //     appzillon.offline.executeSqlCallBack(res);
//        }
//}
//---------------Phone Social Login-----------------------------------------------------------------
WinContainer.webAuthenticationType = null;
WinContainer.continueWebAuthentication = function (args) {
    var args = args.detail.webAuthenticationResult.responseData;
    switch (WinContainer.webAuthenticationType) {
        case "GOOGLEPLUS":
            WinContainer.GooglePlus.loginSuccess(args);
            break;
        case "FACEBOOK":
            WinContainer.Facebook.loginSuccess(args);
            break;
        case "LINKEDIN":
            WinContainer.LinkedIn.loginSuccess(args);
            break;
        case "TWITTER":
            WinContainer.Twitter.loginSuccess(args);
            break;
        default:
            console.log("WinContainer.webAuthenticationType =" + WinContainer.webAuthenticationType);
    }
};
WinContainer.getCurrentApplicationCallbackUri = function () {
    return Windows.Security.Authentication.Web.WebAuthenticationBroker.getCurrentApplicationCallbackUri().absoluteUri;
};
WinContainer.isPhone = function () {
    if (navigator.userAgent.indexOf("Phone") >= 0)
        return true;
    else
        false;
};
WinContainer.Properties = (function () {

    var properties = {};

    function init(file) {
        var httpfreq = new XMLHttpRequest();
        try {
            httpfreq.open("GET", "ms-appx:///" + file, false);
            httpfreq.send(null);
            var status = httpfreq.status;
            if ((status === 200) || (status === 0) || (status === 1100)) {
                properties = JSON.parse(httpfreq.responseText);
            }
        } catch (e) {
        }
    }
    init("containerprops.json");
    return {
        getString: function (key) {
            return properties[key];
        }
    }

})();
//-------------------Alert Synchronization-------------------------------------------
(function () {
    var isDisplaying = false;
    var msgAlertArray = [];
    var msgD;
    WinContainer.betaAlert = function (msg) {
        msgAlertArray.push(msg);
        if (!isDisplaying)
            nativeAlert();
    }
    WinContainer.removeAlertUI = function () {
        try {
            msgD.cancel();
        }
        catch (e) { }
        msgAlertArray = [];
    }
    function nativeAlert() {
        isDisplaying = true;
        try {
            if (msgAlertArray.length > 0) {
                var msg = msgAlertArray.shift();
                msgD = Windows.UI.Popups.MessageDialog(msg).showAsync();
                msgD.done(function () {
                    (msgAlertArray.length > 0) ? nativeAlert() : isDisplaying = false;
                }, function (e) {
                    isDisplaying = false;
                });
            }
        } catch (e) {
            isDisplaying = false;
        }
    };
})();
function toRadian(degrees) {
    return degrees * (Math.PI / 180);
}
function toDegrees(radians) {
    return radians * (180 / Math.PI);
}
function getDistanceBetweenPoints(p1Lat, p1Long, p2Lat, p2Long) {
    p2Lat = parseFloat(p2Lat);
    p2Long = parseFloat(p2Long);
    var R = 6378137; // Earth’s mean radius in meter
    var dLat = toRadian(p2Lat - p1Lat);
    var dLong = toRadian(p2Long - p1Long);
    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(toRadian(p1Lat)) * Math.cos(toRadian(p2Long)) *
        Math.sin(dLong / 2) * Math.sin(dLong / 2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    var d = (R * c);
    return d; // returns the distance in meter
}
function onNotif(e) {
   
    var pushNotifications = Windows.Networking.PushNotifications;
    var notificationContent;
    var applicationData = Windows.Storage.ApplicationData.current;
    var localSettings = applicationData.localSettings;
    var js = {};
    switch (e.notificationType) {
        case pushNotifications.PushNotificationType.toast:
            notificationTypeName = "Toast";
            notificationContent = e.toastNotification.content;
            //-----Changed for reference number
            var param = e.toastNotification.content.getElementsByTagName("toast")[0].getAttribute("launch");
            localSettings.values["isFromNotifHandler"] = true;
            //appzillon.plugin.notif("active", param);
            break;
        case pushNotifications.PushNotificationType.tile:
            notificationTypeName = "Tile";
            notificationContent = e.tileNotification.content;
            break;
        case pushNotifications.PushNotificationType.badge:
            notificationTypeName = "Badge";
            notificationContent = e.badgeNotification.content;
            break;
    }
    WinContainer.storeNotif(Math.floor(Date.now() / 1000), notificationContent.innerText,"N");
    var notif_flag = Windows.Storage.ApplicationData.current.localSettings.values["NOTIFICATION_LSTN"];
    if (notif_flag != undefined && notif_flag != null && notif_flag != "") {
        js.id = notif_flag;
        js.text = notificationContent;
        js.keepAlive = true;
        js.status = true;
        WinContainer.successCallback(js);
    }
}
WinContainer.storeNotif = function (tstamp, msg, flag) {
    if (WinContainer.isPhone())
    {
        var dataB = new SQLiteWinRT.Database(Windows.Storage.ApplicationData.current.localFolder, "APPSDB");
        dataB.openAsync().then(function () {
            dataB.executeStatementAsync('create  table IF NOT EXISTS tb_notifications(id integer primary key autoincrement,message varchar(500),timeStamp varchar(8),readFlag varchar(1))').then(function () {
                dataB.executeStatementAsync('INSERT INTO tb_notifications(message,timeStamp,readFlag) VALUES("' + msg + '",' + tstamp + ',"' + flag + '")').then(function () {
                    dataB.close();
                }, function (e) {
                    dataB.close();
                });         
            }, function (e) {
                dataB.close();
            });   
        });

    } else {
        var dbPath = Windows.Storage.ApplicationData.current.localFolder.path + "\\" + "APPSDB";
        SQLite3JS.openAsync(dbPath)
            .then(function (db) {
                db.runAsync('create  table IF NOT EXISTS tb_notifications(id integer primary key autoincrement,message varchar(500),timeStamp varchar(8),readFlag varchar(1))')
                    .then(function () {
                        db.allAsync('INSERT INTO tb_notifications(message,timeStamp,readFlag) VALUES("' + msg + '",' + tstamp + ',"' + flag + '")')
                            .then(function () {
                                db.close();
                            }, function (e) {
                                db.close();
                            });
                    }, function (e) {
                        db.close();
                    });
            });
            }   
}
function alert(msg) {
    WinContainer.betaAlert(msg);
};
WinContainer.LogLevel = {
    INFO: 3,
    DEBUG: 4,
    WARN: 2,
    ERROR: 1,
    FATAL: 0,
    CURRENT:null
};
WinContainer.getFile = function (file) {
    var httpfreq = new XMLHttpRequest();
    try {
        httpfreq.open("GET", "ms-appx:///" + file, false);
        httpfreq.send(null);
        var status = httpfreq.status;
        if ((status === 200) || (status === 0) || (status === 1100)) {
            return JSON.parse(httpfreq.responseText);
        } else
            return null;
    } catch (e) {
        WinContainer.Log.fatal(e.description);
        return null;
    }
};