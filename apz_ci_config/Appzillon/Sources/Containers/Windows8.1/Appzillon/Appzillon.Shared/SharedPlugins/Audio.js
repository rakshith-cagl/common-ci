//--------Plugin files-----------
(function () {
    var audio_File = '';
    var audio;
    var audioObjectPresent = false;
    var isRecording;
    var isRecordingStart = false;
    var isAudioPlaying = false;
    var audioPause = false;
    var audioBase64 = false;
    var mediaCapture = null;
    WinContainer.audio = function (jsonObj) {

        var id = jsonObj.id;
        var location = jsonObj.location;
        var action = jsonObj.action;
        var fileName = jsonObj.fileName;


        if (action == "save") {
            //Save or Stop recording/audio
            WinContainer.saveRecording(id, false);
        }
        else if (action == "pause") {
            WinContainer.stopAudio(id);//Change - Added failure callback
        }
        else if (action == "play") {
            WinContainer.playAudio(id, location, fileName);
        }
        else if (action == "record") {
            isRecording = true;
            audioBase64 = jsonObj.base64 == 'Y' ? true : false;
            WinContainer.audioRecording(id, location, fileName);
        }
    }


    // Start the audio capture.
    WinContainer.audioRecording = function (id, location, fileName) {
        if (isAudioPlaying == false) {
            try {
                if (location == "default" || location == "external") {

                    WinContainer.saveRecording( null, true, function () {
                        mediaCapture = new Windows.Media.Capture.MediaCapture();
                        isRecordingStart = true;
                        var name = fileName + '.mp3';
                        if (location == "default") {
                            mediaCapture.initializeAsync().then(function () {
                                return Windows.Storage.ApplicationData.current.localFolder.createFileAsync(name, Windows.Storage.CreationCollisionOption.replaceExisting);
                            }).then(function (newFile) {
                                audio_File = newFile;
                                var encodingProfile = Windows.Media.MediaProperties.MediaEncodingProfile.createMp3(Windows.Media.MediaProperties.AudioEncodingQuality.high);

                                var audio = mediaCapture.startRecordToStorageFileAsync(encodingProfile, newFile);
                              

                            }).done(function () {
                                WinContainer.Log.debug("Audio Recording Started");
                                var data = JSON.stringify({ successMessage: "Recording Started....." }, null, " ");
                                var json = JSON.parse(data);
                                json.id = id;
                                WinContainer.successCallback(json);
                            });
                        }
                        else if (location == "external") {

                            mediaCapture.initializeAsync().then(function () {
                                return Windows.Storage.KnownFolders.musicLibrary.createFileAsync(name, Windows.Storage.CreationCollisionOption.replaceExisting);
                            }).then(function (newFile) {
                                audio_File = newFile;
                                var encodingProfile = Windows.Media.MediaProperties.MediaEncodingProfile.createMp3(Windows.Media.MediaProperties.AudioEncodingQuality.high);

                                var audio = mediaCapture.startRecordToStorageFileAsync(encodingProfile, newFile);
                              
                            }).done(function () {
                                WinContainer.Log.debug("Audio Recording Started");
                                var data = JSON.stringify({ successMessage: "Recording Started....." }, null, " ");
                                var json = JSON.parse(data);
                                json.id = id;
                                WinContainer.successCallback(json);
                            });
                        }
                   
                    });
                }
                else {
                    WinContainer.Log.error("Invalid file location selected");
                    WinContainer.failureCallback(id,ErrorCode.NO_FILE_SELECTED);
                }

            }
            catch (err) {
                WinContainer.Log.error(err.description);
                WinContainer.failureCallback(id, ErrorCode.AUDIO_RECORDING_FAIL);
            }
        }
        else {
            WinContainer.Log.warn("operation not possible in current state");
            WinContainer.failureCallback(id, ErrorCode.OPERATION_NOT_POSSIBLE_IN_CURRENT_STATE);
        }

    }

    // Save the audio capture.
    WinContainer.saveRecording = function (id,restart, callback) {

        try {
            if (isAudioPlaying && audioObjectPresent && !restart) {

                audio.pause();
                audio.src = "";
                audioPause = false;
                isAudioPlaying = false;
                WinContainer.Log.debug("audio play end");
                var data = JSON.stringify({ successMessage: "Audio stoppped" }, null, " ");
                var json = JSON.parse(data);
                json.id = id;
                WinContainer.successCallback(json);
            }

            if (!isRecordingStart) {
                if (callback && typeof (callback) === "function") {
                    callback();
                }
            }
            else
                mediaCapture.stopRecordAsync().then(function (result) {
                    isRecordingStart = false;
                    if (restart)
                        if (callback && typeof (callback) === "function") {
                            callback();
                            return;
                        }
                   // if (auditStartEntry)
                      //  WinContainer.sendAuditLog("AudioRecording", "END");
                    WinContainer.Log.debug("audio play end");
                    if (audioBase64) {
                        Windows.Storage.FileIO.readBufferAsync(audio_File).then(function (content) {
                            var base64String = Windows.Security.Cryptography.CryptographicBuffer.encodeToBase64String(content);
                            var data = JSON.stringify({ successMessage: "Recorded Successfully", base64: base64String }, null, " ");
                            var json = JSON.parse(data);
                            json.id = id;
                            WinContainer.successCallback(json);
                            WinContainer.Log.debug("audio Recorded Successfully");
                        })
                    } else {
                        WinContainer.Log.debug("audio Recorded Successfully");
                        var data = JSON.stringify({ successMessage: "Recorded Successfully" }, null, " ");
                        var json = JSON.parse(data);
                        json.id = id;
                        WinContainer.successCallback(json);
                    }


                    // -- Done -- //
                }, function (e) {
                    WinContainer.failureCallback(id, ErrorCode.AUDIO_FAIL);
                    WinContainer.Log.error(e.message);
                });
        }

        catch (err) {
            WinContainer.Log.error(err.description);
            WinContainer.failureCallback(id, ErrorCode.AUDIO_RECORDING_FAIL);
        }
    }
    function playingAudio() {
        isAudioPlaying = true;
        audioPause = false;
    }
    function pausedAudio() {
        audioPause = true;
        isAudioPlaying = false;
    }
    function endedAudio(id) {
        isAudioPlaying = false;
        audioPause = false;
        var data = JSON.stringify({ successMessage: "Audio Stopped..." }, null, " ");
        var json = JSON.parse(data);
        json.id = id;
        WinContainer.successCallback(json);

    }

    // To play audio. It retrieves audio file of name passed from localstorage and plays it.
    WinContainer.playAudio = function (id, location, fileName) {
        if (isRecordingStart == false) {

            if (isAudioPlaying) {
                var data = JSON.stringify({ successMessage: "Audio is already playing..." }, null, " ");
                var json = JSON.parse(data);
                json.id = id;
                WinContainer.successCallback(json);
                return;
            }
            //For resuming
            if (audioPause) {
                if (isRecordingStart == false) {
                    audio.play();
                    WinContainer.Log.debug("Audio Resumed");
                    var data = JSON.stringify({ successMessage: "Audio Resumed ...." }, null, " ");
                    var json = JSON.parse(data);
                    json.id = id;
                    WinContainer.successCallback(json);
                    return;
                }
                else {
                    WinContainer.Log.error("Audio play failed");
                    WinContainer.failureCallback(id, ErrorCode.PLAY_FAIL);
                }
            }
            try {
                var file_name = fileName + '.mp3';

                if (location == "default" || location == "external") {
                    if (location == "default") {
                        Windows.Storage.ApplicationData.current.localFolder.getFileAsync(file_name).then(function (fileItem) {
                            if (fileItem) {
                                if (!audioObjectPresent) {
                                    audio = document.createElement('audio');
                                    audio.addEventListener("playing", playingAudio, false);
                                    audio.addEventListener("pause", pausedAudio, false);
                                    audio.addEventListener("ended", function () { endedAudio(id); }, false);
                                    audioObjectPresent = true;
                                }
                                audio.src = URL.createObjectURL(fileItem);

                                var data = JSON.stringify({ successMessage: "Playing Audio ...." }, null, " ");
                                var json = JSON.parse(data);
                                json.id = id;
                                WinContainer.successCallback(json);
                                audio.play();
                                WinContainer.sendAuditLog("PlayAudio", "START");
                                WinContainer.storeLog("Playing Audio", "D");
                                audioPause = false;
                                isAudioPlaying = true;

                            }
                        }, function (error) {
                            WinContainer.Log.error(error.description);
                            WinContainer.failureCallback(id,ErrorCode.NO_FILE_SELECTED);
                        });
                    }
                    else if (location == "external") {

                        var audioPath = Windows.Storage.KnownFolders.musicLibrary;
                        audioPath.getFileAsync(file_name).then(function (fileItem) {
                            if (fileItem) {
                                if (!audioObjectPresent) {
                                    audio = document.createElement('audio');
                                    audioObjectPresent = true;
                                }
                                audio.src = URL.createObjectURL(fileItem);

                                var data = JSON.stringify({ successMessage: "Playing Audio ...." }, null, " ");
                                var json = JSON.parse(data);
                                json.id = id;
                                WinContainer.successCallback(json);
                                audio.play();
                                WinContainer.Log.debug("Audio play start");
                                audioPause = false;
                                isAudioPlaying = true;
                            }
                        }, function (error) {
                            WinContainer.Log.error(error.description);
                            WinContainer.failureCallback(id,ErrorCode.NO_FILE_SELECTED);
                        });
                    }
                }
                else {
                    WinContainer.Log.error("invalid location value");
                    WinContainer.failureCallback(id, ErrorCode.INVALID_LOCATION);
                }
            }
            catch (err) {
                WinContainer.Log.error(err.description);
                WinContainer.failureCallback(id, ErrorCode.NO_FILE_SELECTED);
            }

        }
        else {
            WinContainer.Log.fatal("audio play fail");
            WinContainer.failureCallback(id, ErrorCode.PLAY_FAIL);
        }
    }
    


    // To stop the audio stream being currently played.
    WinContainer.stopAudio = function (id) {//Change
        if (isAudioPlaying) {
            if (!audioPause) {
                WinContainer.Log.info("audio paused");
                var data = JSON.stringify({ successMessage: "Audio paused...." }, null, " ");
                var json = JSON.parse(data);
                json.id = id;
                WinContainer.successCallback(json);
                audio.pause();
                audioPause = true;
                isAudioPlaying = false;
            }
            else {
                var data = JSON.stringify({ successMessage: "Audio paused already..." }, null, " ");
                var json = JSON.parse(data);
                json.id = id;
                WinContainer.successCallback(json);
            }
        }
        else if (isRecordingStart) {
            // resume is not yet constructed.
            WinContainer.saveRecording(null, true, function () {
                WinContainer.Log.debug("recording paused");
                var data = JSON.stringify({ successMessage: "Recording paused...." }, null, " ");
                var json = JSON.parse(data);
                json.id = id;
                WinContainer.successCallback(json);
                isRecordingStart = false;
            });
        }
            //Change
        else if(isAudioPlaying == false && isRecordingStart == false)
        {
            WinContainer.Log.error("audio pause not supported in current state");
            var data = JSON.stringify({ errorCode: "APZ-CNT-034" }, null, " ");
            var json = JSON.parse(data);
            WinContainer.failureCallback(id,ErrorCode.PAUSE_FAIL);
        }
    }
})();