function PlayaudioPluginClass() {
    console.log("PlayaudioPluginClass.js: is created");
}
PlayaudioPluginClass.prototype.play = function (successCallback, errorCallback, requestParams) {

    var args = [requestParams.path];

    Bridge.exec(successCallback, errorCallback, "Media", "playaudio", args);
}
var PlayaudioPlugin = new PlayaudioPluginClass();
