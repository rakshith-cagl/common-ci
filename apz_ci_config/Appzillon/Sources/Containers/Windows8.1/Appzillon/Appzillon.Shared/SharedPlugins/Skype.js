WinContainer.Skype = (function () {

    var Call = function (obj) {
        var req = {};
        req.id = obj.id;
        var userid = obj["userId"];
        var type = obj["type"];
        if (type == "audio")
            Windows.System.Launcher.launchUriAsync(new Windows.Foundation.Uri("skype:" + userid + "?call&video=true")).then(function (s) {
                if (s) {
                    WinContainer.successCallback(req);
                } else {
                    WinContainer.failureCallback(req.id, ErrorCode.SKYPE_CALL_FAIL);
                    WinContainer.Log.warn("skype launch failed");
                }
            });
        else {
            Windows.System.Launcher.launchUriAsync(new Windows.Foundation.Uri("skype:" + userid + "?call&video=false")).then(function (s) {
                if (s) {
                    WinContainer.successCallback(req);
                } else {
                    WinContainer.failureCallback(req.id, ErrorCode.SKYPE_CALL_FAIL);
                    WinContainer.Log.warn("skype launch failed");
                }
            });
        }
    }

    return {
        call: Call
    }

})();