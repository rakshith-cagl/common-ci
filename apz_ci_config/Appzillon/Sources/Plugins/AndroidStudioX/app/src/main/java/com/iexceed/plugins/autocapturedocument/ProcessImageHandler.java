package com.iexceed.plugins.autocapturedocument;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;

public interface ProcessImageHandler {
    void onImageDetected(FirebaseVisionImage fireImage);
}
