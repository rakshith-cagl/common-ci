package com.scanlibrary;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.iexceed.appzillonapp.AppzillonMainScreen;

class ApzOCR {

    public static String detectText(byte[] bitmap){
        String detectedText = "";
        Bitmap textBitmap = BitmapFactory.decodeByteArray(bitmap,0,bitmap.length);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(AppzillonMainScreen.activity).build();
        if (!textRecognizer.isOperational()) {
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = AppzillonMainScreen.activity.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(AppzillonMainScreen.activity, "Low Storage", Toast.LENGTH_LONG).show();
            }
        }
        Frame frame = new Frame.Builder().setBitmap(textBitmap).build();
        SparseArray<TextBlock> text = textRecognizer.detect(frame);
        for (int i = 0; i < text.size(); i++) {
            TextBlock textBlock = text.valueAt(i);
            if (textBlock != null && textBlock.getValue() != null) {
                detectedText += textBlock.getValue();
            }
        }
        return detectedText;
    }
}
