function MagneticcardPluginClass() {
    console.log("MagneticcardPluginClass.js: is created");
}
MagneticcardPluginClass.prototype.swipe = function (successCallback, errorCallback, requestParams) {

    var args = [];

    Bridge.exec(successCallback, errorCallback, "Magneticcard", "swipe", args);
}
var MagneticcardPlugin = new MagneticcardPluginClass();
