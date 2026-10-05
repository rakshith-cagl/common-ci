package com.iexceed.plugins.selfiecapture

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.iexceed.appzillonapp.R
import com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.Companion.failureCallback

class ImagePreviewActivity : AppCompatActivity() {
    var image: ImageView? = null
    var bytearr: ByteArray? = null
    var visionText: String? = null
    var visionWholeText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.face_img_preview)
        try {
            image = findViewById(R.id.detectedImg)
            val intent = intent
            bytearr = intent.getByteArrayExtra("bitmap")
            var bmp = BitmapFactory.decodeByteArray(bytearr, 0, bytearr!!.size)
            image!!.setImageBitmap(bmp)
            bmp = null
        } catch (e: Exception) {
            failureCallback("Operation failed")
            finish()
        }
    }

    fun retakePicture(view: View?) {
        //   outputTextRecognised[0]="";
        //Log.i("image preview", "retakePicture: "+ProminentObjectProcessor.confirmText());
        val intent = Intent(this, LivePreviewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun saveFile(view: View?) {
        val intent = Intent()
        try {
            intent.putExtra("bitmap", bytearr)
            setResult(RESULT_OK, intent)
        } catch (e: Exception) {
            setResult(RESULT_CANCELED, intent)
        }
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent(this, LivePreviewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        // finish();
    }
}