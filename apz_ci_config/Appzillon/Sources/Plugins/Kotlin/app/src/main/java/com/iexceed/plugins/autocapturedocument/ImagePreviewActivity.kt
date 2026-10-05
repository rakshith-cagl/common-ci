package com.iexceed.plugins.autocapturedocument

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.iexceed.appzillonapp.R

class ImagePreviewActivity : AppCompatActivity() {
    var image: ImageView? = null
    var filename = "bitmap.png"
    var visionText: String? = ""
    var visionWholeText: String? = ""
    var bmp: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.e_activity_main)
        try {
            image = findViewById(R.id.detectedImg)
            val intent = intent
            if (intent.hasExtra("firebaseText") && intent.hasExtra("firebaseWholeText")) {
                visionText = intent.extras!!.getString("firebaseText")
                visionWholeText = intent.extras!!.getString("firebaseWholeText")
            }
            val `is` = openFileInput(filename)
            bmp = BitmapFactory.decodeStream(`is`)
            `is`.close()
            //   Bitmap bmp = BitmapFactory.decodeByteArray(bytearr, 0, bytearr.length);
            image!!.setImageBitmap(bmp)
        } catch (e: Exception) {
            ApzAutoCapturePlugin.failureCallback("Operation failed", "")
            finish()
        }
    }

    fun retakePicture(view: View?) {
        ProminentObjectProcessor.outputTextRecognised[0] = ""
        finish()
    }

    fun saveFile(view: View?) {
        val intent = Intent()
        try {
            val stream = openFileOutput(filename, MODE_PRIVATE)
            bmp!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            //Cleanup
            stream.close()
            intent.putExtra("bitmap", filename)
            intent.putExtra("ocrText", visionText)
            intent.putExtra("ocrFullText", visionWholeText)
            setResult(RESULT_OK, intent)
        } catch (e: Exception) {
            //handle exception
        }
        finish()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        bmp = null
        super.onDestroy()
    }
}