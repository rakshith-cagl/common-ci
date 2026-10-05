WinContainer.Log = (function () {
    var _fatal = function (errormsg) {
        if (WinContainer.LogLevel.CURRENT >= WinContainer.LogLevel.FATAL) {
            console.log("F :" + errormsg);
            sendlog("F", errormsg);
        }
    }
    var _error = function (errormsg) {
        if (WinContainer.LogLevel.CURRENT >= WinContainer.LogLevel.ERROR) {
            console.log("E :" + errormsg);
            sendlog("E", errormsg);
        }
    }
    var _warning = function (errormsg) {
        if (WinContainer.LogLevel.CURRENT >= WinContainer.LogLevel.WARN) {
            console.log("W :" + errormsg);
            sendlog("W", errormsg);
        }
    }
    var _debug = function (errormsg) {
        if (WinContainer.LogLevel.CURRENT >= WinContainer.LogLevel.DEBUG) {
            console.log("D :" + errormsg);
            sendlog("D", errormsg);
        }
    }
    var _info = function (errormsg) {
        if (WinContainer.LogLevel.CURRENT >= WinContainer.LogLevel.INFO) {
            console.log("I :" + errormsg);
            sendlog("I", errormsg);
        }
    }
    function sendlog(level, description) {
        Apz.appendLog(level, description);
    }
    return {
        fatal: _fatal,
        error: _error,
        warn: _warning,
        debug: _debug,
        info: _info
    }
})();