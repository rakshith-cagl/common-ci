WinContainer.Whatsapp = (function () {
    var SendWhatsappmsg = function (obj) {
        var req = {};
        req.id = obj.id;
        var text = obj.text;
        var uri;
        if (text == null || text == undefined)
            uri = "whatsapp://app";
        else
        uri = "whatsapp://send?text=" + text;
        Windows.System.Launcher.launchUriAsync(new Windows.Foundation.Uri(uri)).then(function (e) {
            if (e) {
                WinContainer.successCallback(req);
            } else {
                WinContainer.failureCallback(req.id, "APZ-CNT-077");
                WinContainer.Log.debug("Whatsapp Open Fail");
            }

        });
    }
    return {
        msg: SendWhatsappmsg
    }
})();