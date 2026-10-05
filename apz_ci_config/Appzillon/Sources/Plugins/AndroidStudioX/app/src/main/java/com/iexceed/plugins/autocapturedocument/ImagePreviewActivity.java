package com.iexceed.plugins.autocapturedocument;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.iexceed.appzillonapp.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.failureCallback;
import static com.iexceed.plugins.autocapturedocument.ProminentObjectProcessor.outputTextRecognised;

public class ImagePreviewActivity extends AppCompatActivity {
    ImageView image;
    String filename = "bitmap.png";
    String visionText="";
    String visionWholeText="";
    Bitmap bmp=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_activity_main);
        try {
            image = findViewById(R.id.detectedImg);
            Intent intent = getIntent();
            if(intent.hasExtra("firebaseText")&&intent.hasExtra("firebaseWholeText")) {
                visionText = intent.getExtras().getString("firebaseText");
                visionWholeText = intent.getExtras().getString("firebaseWholeText");
            }
            FileInputStream is = this.openFileInput(filename);
             bmp = BitmapFactory.decodeStream(is);
            is.close();
         //   Bitmap bmp = BitmapFactory.decodeByteArray(bytearr, 0, bytearr.length);
            image.setImageBitmap(bmp);

        }catch (Exception e){
            failureCallback("Operation failed","");
            finish();
        }
    }

    public void retakePicture(View view) {
        outputTextRecognised[0]="";
        //Log.i("image preview", "retakePicture: "+ProminentObjectProcessor.confirmText());
        finish();
    }

    public void saveFile(View view) {
        Intent intent = new Intent();
        try {
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //Cleanup
            stream.close();
            intent.putExtra("bitmap", filename);
            intent.putExtra("ocrText",  visionText);
            intent.putExtra("ocrFullText",  visionWholeText);
            setResult(RESULT_OK, intent);
        }catch (Exception e){
           // setResult(RESULT_CANCELED,intent);
        }
        finish();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        bmp = null;
        super.onDestroy();
    }
}
