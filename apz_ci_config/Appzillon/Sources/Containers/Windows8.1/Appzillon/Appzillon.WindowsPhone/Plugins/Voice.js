WinContainer.Voice = (function () {
    var req_id;
    var startSR = function (req) {
        req_id = req.id;
        try {

          // var lang = new Windows.Globalization.Language();
            var speechRecognizer = new Windows.Media.SpeechRecognition.SpeechRecognizer();
           speechRecognizer.uiOptions.audiblePrompt = "Speak what you want to do";
           speechRecognizer.uiOptions.exampleText = "Logout";
           speechRecognizer.uiOptions.isReadBackEnabled = true;
           speechRecognizer.uiOptions.showConfirmation = true;
            speechRecognizer.compileConstraintsAsync().then(function () {
                speechRecognizer.recognizeWithUIAsync().done(function (result) {
                    if (result.text) {
                        speechRecognizer.close();
                        var res = {};
                        res.id = req_id;
                        res.text = result.text;
                        WinContainer.successCallback(res);
                    }
                }, function (erro) {
                    WinContainer.failureCallback(req_id, ErrorCode.SPEECH_RECOGNITION_FAILED, e.message);
                    WinContainer.Log.error(erro.message);
                });

            }, function (error) {
                WinContainer.failureCallback(req_id, ErrorCode.SPEECH_RECOGNITION_FAILED, e.message);
                WinContainer.Log.error(error.message);
            });
        }
        catch (e) {
            WinContainer.failureCallback(req_id, ErrorCode.SPEECH_RECOGNITION_FAILED, e.message);
            WinContainer.Log.error(e.description);
        }
    }
    return {
        execute: startSR
    }
})();

