//--------LocalePlugin files-----------
WinContainer.Locale=(function () {

    var _Execute = function (req) {
        var res = {};
        res.id = req.id;
        res.locale = navigator.userLanguage; //en-US
        WinContainer.successCallback(res);
    }
    return {
        Execute:_Execute
    }
})();