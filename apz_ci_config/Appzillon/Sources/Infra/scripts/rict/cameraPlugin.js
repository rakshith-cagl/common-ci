function CameraPluginClass() {
    console.log("CameraPluginClass.js: is created");
}
CameraPluginClass.prototype.getPicture = function (successCallback, errorCallback, cameraJSON) {


    var args = [];
    Bridge.exec(successCallback, errorCallback, "Camera", "takePicture", args);
}
navigator.camera = new CameraPluginClass();
