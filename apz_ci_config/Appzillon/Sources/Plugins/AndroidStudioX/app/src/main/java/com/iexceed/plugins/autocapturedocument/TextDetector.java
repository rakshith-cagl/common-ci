package com.iexceed.plugins.autocapturedocument;

import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class TextDetector {
    private static final String TAG = "TextDetector";


    private FirebaseVisionImage fullImage;
    private FirebaseVisionImage objectImage;
    private TextDetectorHandler textDetectorHandler;
    private FirebaseVisionObject fireObject;

    public TextDetector(FirebaseVisionImage fullImage, FirebaseVisionImage objectImage , FirebaseVisionObject fireObject, TextDetectorHandler textDetectorHandler ){
        this.fullImage = FirebaseVisionImage.fromBitmap(fullImage.getBitmap());
        this.objectImage = FirebaseVisionImage.fromBitmap(objectImage.getBitmap());
        this.textDetectorHandler = textDetectorHandler;
        this.fireObject = fireObject;


    }
    public void findText(){
        try {
            final FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            textRecognizer.processImage(this.objectImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    try {
                        textRecognizer.close();
                    }catch(Exception e){

                    }
                   // Log.i(TAG, " checkVisionText onTextdetector: " + firebaseVisionText.getText() + "FirebaseVisionText " + firebaseVisionText);
                    textDetectorHandler.onTextDetected(firebaseVisionText, fullImage,objectImage, fireObject);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    try {
                        textRecognizer.close();
                    }catch(Exception ex){

                    }
                    textDetectorHandler.onTextDetected(null, fullImage,objectImage, fireObject);
                  //  Log.i(TAG, "checkVisionText failed: " + e);
                }
            });

        }catch (Exception e){}

    }



    interface TextDetectorHandler {
        void onTextDetected(FirebaseVisionText text, FirebaseVisionImage fullImage, FirebaseVisionImage objectImage, FirebaseVisionObject fireObject);
    }
}
