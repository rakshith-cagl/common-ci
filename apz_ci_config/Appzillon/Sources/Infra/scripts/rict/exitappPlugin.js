function ExitPluginClass() {
    console.log("ExitPluginClass.js: is created");
}
ExitPluginClass.prototype.exitApp = function (successCallback, errorCallback, cameraJSON) {
    var args = [];
    Bridge.exec(function(cameraResponse){
    	successCallback(cameraResponse.data);
    }, errorCallback, "Exit", "", args);
}
navigator.app = new ExitPluginClass();