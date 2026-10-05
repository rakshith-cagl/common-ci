WinContainer.deepLink = function (Jobj) {
    var id = Jobj.id;
    var action = Jobj.action;
    if (action == "deepLinking") {
        var protocol = Jobj.packageName + ":";
        var launch = Windows.Foundation.Uri(protocol);
        Windows.System.Launcher.launchUriAsync(launch).then(function (s) {
            if (s) {
                var json = {};
                json.successMessage = "success";
                json.id =id;
                WinContainer.successCallback(json);
            } else {
                WinContainer.failureCallback(id, "APZ-CNT-309");
            }
        });
    }
}