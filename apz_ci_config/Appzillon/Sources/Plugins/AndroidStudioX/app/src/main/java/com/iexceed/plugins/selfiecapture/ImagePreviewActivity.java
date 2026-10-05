package com.iexceed.plugins.selfiecapture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.iexceed.appzillonapp.R;

import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.failureCallback;

public class ImagePreviewActivity extends AppCompatActivity {
    ImageView image;
    byte[] bytearr;
    String visionText;
    String visionWholeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_img_preview);
        try {
            image = findViewById(R.id.detectedImg);
            Intent intent = getIntent();
            bytearr = intent.getByteArrayExtra("bitmap");
            Bitmap bmp = BitmapFactory.decodeByteArray(bytearr, 0, bytearr.length);
            image.setImageBitmap(bmp);
            bmp = null;
        }catch (Exception e){
            failureCallback("Operation failed");
            finish();
        }
    }

    public void retakePicture(View view) {
     //   outputTextRecognised[0]="";
        //Log.i("image preview", "retakePicture: "+ProminentObjectProcessor.confirmText());
        Intent intent = new Intent(this, LivePreviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void saveFile(View view) {
        Intent intent = new Intent();
        try {
            intent.putExtra("bitmap", bytearr);
            setResult(RESULT_OK, intent);

        }catch (Exception e){
            setResult(RESULT_CANCELED,intent);
        }
        finish();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LivePreviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // finish();
    }


}
