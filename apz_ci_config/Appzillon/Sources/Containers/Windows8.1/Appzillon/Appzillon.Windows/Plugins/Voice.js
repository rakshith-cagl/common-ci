WinContainer.Voice=(function () {
    var SR,req_id;

    var startSR = function (req) {
        req_id = req.id;
        try {
            var credentials = new Bing.Speech.SpeechAuthorizationParameters();
            credentials.clientId = "i-exceed-speech-control";
            credentials.clientSecret = "jVxL+V8ceiQbWxWYoNZBF/wr+3PIC1/IoOku51z5Qm0=";
            SR = new Bing.Speech.SpeechRecognizer("en-US", credentials);
            
            SR.recognizeSpeechToTextAsync().done(
                    function (result) {
                        try{
                            if (typeof (result.text) == "string") {
                                var resultString = result.text;
                                resultString = resultString.substring(0, resultString.length - 1);
                                SR.stopListeningAndProcessAudio();

                                var res = {};
                                res.id = req_id;
                                res.text = resultString;
                                WinContainer.successCallback(res);
                            } else {
                                WinContainer.failureCallback(req_id, ErrorCode.SPEECH_RECOGNITION_FAILED, e.message);
                            }
                        } catch (e) {
                            WinContainer.failureCallback(req_id, ErrorCode.SPEECH_RECOGNITION_FAILED, e.message);
                            WinContainer.Log.error(e.description);
                        }
                    },function (e) {
                        WinContainer.failureCallback(req_id, ErrorCode.SPEECH_RECOGNITION_FAILED, e.message);
                        WinContainer.Log.error(e.message);
                    }
                );
        } catch (e) {
            WinContainer.failureCallback(req_id, ErrorCode.SPEECH_RECOGNITION_FAILED, e.message);
            WinContainer.Log.error(e.message);
        }
    }
    return {
        execute: startSR
    }
})(); 