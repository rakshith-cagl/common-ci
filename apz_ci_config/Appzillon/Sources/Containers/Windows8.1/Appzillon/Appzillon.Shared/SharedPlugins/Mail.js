//--------MailPlugin files-----------
WinContainer.Mail = (function () {
    var _id = null;
    function externalMail(uId, mailid, ccIdList, subject, body) {
        var mailto = new Windows.Foundation.Uri("mailto:" + mailid + "?subject=" + subject + "&body=" + body + "&cc=" + ccIdList);
        var options = new Windows.System.LauncherOptions();
        options.displayApplicationPicker = true;
        Windows.System.Launcher.launchUriAsync(mailto, options).done();
        var res = {};
        res.id = uId;
        WinContainer.successCallback(res);

    }
    var _Execute = function (req) {
        _id = req.id;
        var recipientMailId = req.recipientMailId;
        var senderMailId = req.senderMailId;
        var ccIdList = req.ccIdList;
        var internal = req.internal;
        var subject = req.subject;
        var body = req.body;
        var interfaceID = req.interfaceID;
            externalMail(_id, recipientMailId, ccIdList.replace(',', ';'), subject, body);    
    }

    return {
        InvokeEmail: _Execute
    }
})();
