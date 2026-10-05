function AppVersionPluginClass() {
    console.log("AppVaersionPluginClass.js: is created");
}

AppVersionPluginClass.prototype.getAppVersion = function (successCallback, errorCallback, requestParams) {

    var  appName= requestParams.appName;


    var args = [appName];

    Bridge.exec(successCallback, errorCallback, "AppVersion", "getAppVersion", args);
}


var AppVersionPlugin = new AppVersionPluginClass();
