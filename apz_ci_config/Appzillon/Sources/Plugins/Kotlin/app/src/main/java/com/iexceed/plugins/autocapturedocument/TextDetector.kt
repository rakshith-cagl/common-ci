package com.iexceed.plugins.autocapturedocument

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextDetector constructor(
    private val fullImage: InputImage,
    private val objectImage: InputImage,
    private val fireObject: DetectedObject,
    private val textDetectorHandler: TextDetectorHandler
) {
    fun findText() {
        try {
            val textRecognizer: TextRecognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            textRecognizer.process(objectImage)
                .addOnSuccessListener(object : OnSuccessListener<Text?> {
                    public override fun onSuccess(firebaseVisionText: Text?) {
                        try {
                            textRecognizer.close()
                        } catch (e: Exception) {
                        }
                        textDetectorHandler.onTextDetected(
                            firebaseVisionText,
                            fullImage,
                            objectImage,
                            fireObject
                        )
                    }
                }).addOnFailureListener(object : OnFailureListener {
                public override fun onFailure(e: Exception) {
                    try {
                        textRecognizer.close()
                    } catch (ex: Exception) {
                    }
                    textDetectorHandler.onTextDetected(null, fullImage, objectImage, fireObject)
                }
            })
        } catch (e: Exception) {
        }
    }

    open interface TextDetectorHandler {
        fun onTextDetected(
            text: Text?,
            fullImage: InputImage?,
            objectImage: InputImage?,
            fireObject: DetectedObject?
        )
    }

    companion object {
        private val TAG: String = "TextDetector"
    }
}