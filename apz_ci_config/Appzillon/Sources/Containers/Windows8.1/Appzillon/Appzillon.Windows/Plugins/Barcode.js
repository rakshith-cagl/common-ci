//--------Plugin files-----------
WinContainer.plugin.barcode = function (req) {
    WinContainer.removeAlertUI();  
    WinContainer.plugin.scanBarCode(req.id);
}

// Bar code / QR code scanner.
WinContainer.plugin.scanBarCode = function (req_id) {

    var camCapture = new Windows.Media.Capture.CameraCaptureUI();
    camCapture.captureFileAsync(Windows.Media.Capture.CameraCaptureUIMode.photo).then(function (file) {
        if (file) {
            return file.openAsync(Windows.Storage.FileAccessMode.readWrite);
        }
    }).then(function (stream) {
        if (stream) {
            return Windows.Graphics.Imaging.BitmapDecoder.createAsync(stream);
        }
    }).done(function (decoder) {
        if (decoder) {
            decoder.getPixelDataAsync().then(function (pixelDataProvider) {
                var pixelData = pixelDataProvider.detachPixelData();
                var pixels, format; // Assign these in the below switch block.

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
                try {
                    var result = reader.decode(pixels, decoder.pixelWidth, decoder.pixelHeight, format);
                } catch (e) {
                    WinContainer.failureCallback(req_id, "APZ-CNT-013", e.message);
                    WinContainer.Log.error(e.description);
                }
                //var result = reader.decode(pixels, decoder.pixelWidth, decoder.pixelHeight, format);
                if (result) {
                    var res = {};
                    res.id = req_id;
                    res.text = res.decodedData = result.text;
                    WinContainer.successCallback(res);
                }
                else {
                    WinContainer.failureCallback(req_id, "APZ-CNT-013", e.message);
                }
            });
        }
    });
}