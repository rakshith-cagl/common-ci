//----------------------BarcodePlugin.js------------------------------------------------------
var barcodeCaptureMgr = null;
var videoCameraTag = null;
WinContainer.plugin.barcode = function (jsonObj){
    try {
        barcodeCaptureMgr.close();
    } catch (e) {}
    
    var webCam = Windows.Media.Capture;
    barcodeCaptureMgr = new webCam.MediaCapture();
    var req_id = jsonObj.id;

    var captureInitSettings = null;
    captureInitSettings = new webCam.MediaCaptureInitializationSettings();
    captureInitSettings.audioDeviceId = "";
    captureInitSettings.videoDeviceId = "";
    captureInitSettings.photoCaptureSource = webCam.PhotoCaptureSource.photo;
    captureInitSettings.streamingCaptureMode = webCam.StreamingCaptureMode.video;
 
    Windows.Devices.Enumeration.DeviceInformation
        .findAllAsync(Windows.Devices.Enumeration.DeviceClass.videoCapture)
            .then(function (devList) {
                if(devList.length>1)
                captureInitSettings.videoDeviceId = devList[1].id;
                barcodeCaptureMgr.initializeAsync(captureInitSettings)
                    .done(function (result) {
                        try {
                            barcodeCaptureMgr.setPreviewRotation(webCam.VideoRotation.clockwise90Degrees);
							if(barcodeCaptureMgr.videoDeviceController.flashControl.supported)
                            barcodeCaptureMgr.videoDeviceController.flashControl.auto = true;
                            var focusSettings = Windows.Media.Devices.FocusSettings();
                            focusSettings.autoFocusRange = Windows.Media.Devices.AutoFocusRange.fullRange;
                            focusSettings.waitForFocus = true;
                            barcodeCaptureMgr.videoDeviceController.focusControl.configure(focusSettings);
                            
                            barcodeCaptureMgr.onfocuschanged = onFocusChanged;
                            barcodeCaptureMgr.onphotoconfirmationcaptured = onPhotoConfirmationCaptured;
                            barcodeCaptureMgr.onfailed = onFailedCapture;
                            var videoDev = barcodeCaptureMgr.videoDeviceController;// videoDeviceController;
                            var resolutions = barcodeCaptureMgr.videoDeviceController.getAvailableMediaStreamProperties(Windows.Media.Capture.MediaStreamType.photo);

                            videoDev.setMediaStreamPropertiesAsync(Windows.Media.Capture.MediaStreamType.photo, resolutions[4]);
                            lockOrientaionForBarcode();
                            displayBarCodeCamera();
                       
                        } catch (e) {
                            videoCameraTag = null;
                            barcodeCaptureMgr.close();    
                            return;
                        }
                    }, function (er) {
                        videoCameraTag = null;
                        barcodeCaptureMgr.close();
                       
                    });
            });
    function onFailedCapture(args) {
        var a=args;
    }

    function displayBarCodeCamera() {
        var intDiv = document.createElement("div");
        intDiv.id = "previewVideo";
        intDiv.style.position = "absolute";
        intDiv.style.top = "0%";
        intDiv.style.left = "0%";
        intDiv.style.zIndex = "10000";
        videoCameraTag = document.createElement("video");
        videoCameraTag.id = "barcodeVideo";//<video id="previewVideo" style="position:absolute;top:0%;left:0%;margin:0px 0px 0px -440px;"></video>
        videoCameraTag.style.position = "fixed";
        videoCameraTag.style.top = "0%";
        videoCameraTag.style.left = "0%";
        videoCameraTag.style.width = window.innerWidth + 'px';
        videoCameraTag.style.height = window.innerHeight + 'px';
        //videoCameraTag.style.margin = "0px 0px 0px -440px";
        //varies on different device.
        //videoCameraTag = document.getElementById("previewVideo");
        videoCameraTag.msZoom = true;
        //videoCameraTag.addEventListener("click", capturePhoto);
        videoCameraTag.src = URL.createObjectURL(barcodeCaptureMgr, { oneTimeOnly: true });
        videoCameraTag.play();
        intDiv.appendChild(videoCameraTag);
        document.body.appendChild(intDiv);
        document.body.style.overflowY = 'hidden';
        setTimeout(function () { capturePhoto(); }, 500);
    }
	WinJS.Application.onbackclick = function () {
        try{
            unlockOrientaionForBarcode();
            hideBarCodeCamera();
        } catch (e) { }
        return true;
    }
	function hideBarCodeCamera() {
	    document.body.style.overflowY = 'visible';
	    var videoDiv = document.getElementById("previewVideo");
	    videoCamerTag = document.getElementById("barcodeVideo");
	    videoCameraTag.src = null;
	    document.body.removeChild(videoDiv);
	    if (videoCameraTag)
	        videoCameraTag = null;
	    if (barcodeCaptureMgr)
	        try {
	            barcodeCaptureMgr.close();
	        } catch (e) {
	        }
	}
    function lockOrientaionForBarcode() {
        Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.portrait;
        
    }
    function unlockOrientaionForBarcode() {
        var ort = WinContainer.Settings.containerProperties["ortchosen"];
        if(ort=="POR")
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.portrait;
        else if(ort=="LAN")
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.landscape;
        else
            Windows.Graphics.Display.DisplayProperties.autoRotationPreferences =
        Windows.Graphics.Display.DisplayOrientations.none;
    }
    function onFocusChanged(args) {

    }
    function onPhotoConfirmationCaptured(args) {
     
    }
    function capturePhoto() {
       // videoCameraTag.removeEventListener("click", capturePhoto);
        var photoProperties = Windows.Media.MediaProperties.ImageEncodingProperties.createJpeg();
        photoProperties.height = 760;
        photoProperties.width = 1280;
        var stream = new Windows.Storage.Streams.InMemoryRandomAccessStream();
        barcodeCaptureMgr.videoDeviceController.focusControl.focusAsync().then(function () {
            barcodeCaptureMgr.capturePhotoToStreamAsync(photoProperties, stream)
            .then(function (s) {

                //unlockOrientaionForBarcode();
                WinContainer.plugin.sdk_decodeBarcodeStream(stream, req_id);
               // hideBarCodeCamera();
                stream.close();
            }, function (e) {
                var a = e;
            });
        }, function (e) {
            barcodeCaptureMgr.close();
        });   
    }
    WinContainer.plugin.captureBarcodeAgain = function () {
        capturePhoto();
    }
    WinContainer.plugin.closeBarcode = function () {
        unlockOrientaionForBarcode();
        hideBarCodeCamera();
    }

};


WinContainer.plugin.sdk_decodeBarcodeStream = function(stream, req_id) {
    var _stream = stream;
    return Windows.Graphics.Imaging.BitmapDecoder.createAsync(_stream)
                .done(function (_decoder) {                 
                    if (_decoder) {
                        var decoder = {};
                        decoder.bitmapPixelFormat = _decoder.bitmapPixelFormat;
                        decoder.bitmapAlphaMode = _decoder.bitmapAlphaMode;
                        decoder.pixelWidth = _decoder.pixelWidth;
                        decoder.pixelHeight = _decoder.pixelHeight;
                        _decoder.getPixelDataAsync().then(function (pixelDataProvider) {
                            var pixelData = pixelDataProvider.detachPixelData();
                            var pixels = null;
                            var format = null; // Assign these in the below switch block.

                            switch (decoder.bitmapPixelFormat) {
                                case Windows.Graphics.Imaging.BitmapPixelFormat.rgba16:
                                    // Allocate a typed array with the raw pixel data
                                    var pixelBufferView_U8 = new Uint8Array(pixelData);

                                    // Uint16Array provides a typed view into the raw 8 bit pixel data.
                                    pixels = new Uint16Array(pixelBufferView_U8.buffer);
                                    if (decoder.bitmapAlphaMode == Windows.Graphics.Imaging.BitmapAlphaMode.straight)
                                        format = ZXing.BitmapFormat.rgba32;
                                    else
                                        format = ZXing.BitmapFormat.rgb32;

                                    break;

                                case Windows.Graphics.Imaging.BitmapPixelFormat.rgba8:
                                    // For 8 bit pixel formats, just use the returned pixel array.
                                    pixels = pixelData;
                                    if (decoder.bitmapAlphaMode == Windows.Graphics.Imaging.BitmapAlphaMode.straight)
                                        format = ZXing.BitmapFormat.rgba32;
                                    else
                                        format = ZXing.BitmapFormat.rgb32;
                                    break;

                                case Windows.Graphics.Imaging.BitmapPixelFormat.bgra8:
                                    // For 8 bit pixel formats, just use the returned pixel array.
                                    pixels = pixelData;
                                    if (decoder.bitmapAlphaMode == Windows.Graphics.Imaging.BitmapAlphaMode.straight)
                                        format = ZXing.BitmapFormat.bgra32;
                                    else
                                        format = ZXing.BitmapFormat.bgr32;
                                    break;
                            }
                            var reader = new ZXing.BarcodeReader();
                            reader.autoRotate = true;
                            reader.tryHarder = true;

                            try {
                                var result = reader.decode(pixels, decoder.pixelWidth, decoder.pixelHeight, format);
                                reader = null;

                            } catch (e) {
                                WinContainer.failureCallback(req_id, "APZ-CNT-013", e.message);
                                WinContainer.Log.error(e.description);
                            }
                            if (result) {
                                WinContainer.plugin.closeBarcode();

                                var res = {};
                                res.id = req_id;
                                res.decodedData = result.text;
                                res.text = res.decodedData;
                                WinContainer.successCallback(res);
                            }
                            else {
                                WinContainer.plugin.captureBarcodeAgain();
                                return;
                            }
                        });
                    }
                }, function (e) {
                });
};