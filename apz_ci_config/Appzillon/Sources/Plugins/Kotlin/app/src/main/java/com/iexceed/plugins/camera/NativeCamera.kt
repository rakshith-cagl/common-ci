package com.iexceed.plugins.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.FileProvider
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.FileUtils
import com.iexceed.common.UserSettings
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import com.iexceed.utils.localstorage.FileAccessHelper
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.getActivityResult
import com.theartofdev.edmodo.cropper.CropImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
 
class NativeCamera : Activity() {
    private var mImageCaptureUri: Uri? = null
    private var CROP: String? = null
    private var CROP_BOX: String? = null
    private var cmpLevel = 0
    private var FILENAME: String? = null
    private val TAG = "nativeCamera"
    private val BUFFER_SIZE = 1024 * 8
    private var forTestingPath: File? = null
    private var mEncodingType: String? = null
    private var mCompressFormat: Bitmap.CompressFormat? = null
    private var mEncodingFormat: String? = null
    var sourceType: String? = null
    private var pictureFile: File? = null
    private var ACTION: String? = null
    var context: Context? = null
    private var HTMLWIDTH = 0
    private var HTMLHEIGHT = 0
    private var completePath: String? = null
    private var mUncompressed = false
    private var frontcamera = "N"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.native_camera_layout)
        context = this
        setCropping = false
        val extras: Intent = intent
        val jsonStr: String = extras.extras?.getString("jsonStr")!!
        try {
            val jsonObj = JSONObject(jsonStr)
            extractParams(jsonObj)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
        if (sourceType.equals("Camera", ignoreCase = true)) {
            setCompressFormat(mEncodingType)
            try {
//                forTestingPath = new File(SANDBOX_LOC + File.separator + ASSET_APP_LOC + "appzillonTemp"
//                        + File.separator);
                forTestingPath = AppzillonUtils.getApzFile(
                    AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "appzillonTemp" + File.separator,
                    null
                )
                if (!forTestingPath!!.exists()) {
                    forTestingPath!!.mkdirs()
                }
                completePath =
                    forTestingPath!!.absolutePath + File.separator + System.currentTimeMillis()
                        .toString() + mEncodingFormat
                toStoreTempImage =
                    File(forTestingPath, System.currentTimeMillis().toString() + mEncodingFormat)
                val intent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
                )
                mImageCaptureUri =
                    FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, toStoreTempImage!!)
                intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    mImageCaptureUri
                )

                if (frontcamera.equals("Y", ignoreCase = true)) {
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                }
                intent.putExtra("return-data", true)
                intent.putExtra("outputX", dpToPixel(HTMLWIDTH))
                intent.putExtra("outputY", dpToPixel(HTMLHEIGHT))
                startActivityForResult(intent, PICK_FROM_CAMERA)
            } catch (ex:Exception){
                ApzLogger.e("","exc")
            }
        } else if (sourceType.equals("Photo", ignoreCase = true)) {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, PICK_IMAGE_FROM_GALLERY)
        }
    }

    private fun extractParams(jsonObj: JSONObject) {
        FILENAME = jsonObj.optString("fileName")
        ACTION = jsonObj.optString("action")
        CROP = jsonObj.optString("crop")
        CROP_BOX = jsonObj.optString("cropBox")
        sourceType = jsonObj.optString("sourceType")
        mEncodingType = jsonObj.optString("encodingType")
        frontcamera = jsonObj.optString("frontCamera")
        val uncompressed: String = jsonObj.optString("unCompressed")
        mUncompressed = uncompressed.equals("Y", ignoreCase = true)
        val compressionLevel: String = jsonObj.optString("quality")
        cmpLevel = if (compressionLevel != "") {
            compressionLevel.toInt()
        } else {
            100
        }
        if (sourceType.equals("", ignoreCase = true) || sourceType == null) {
            sourceType = "Camera"
        }
        val targetWidth: String = jsonObj.optString("targetWidth")
        if (targetWidth.isNotEmpty()) {
            HTMLWIDTH = targetWidth.toInt()
        }
        val targetHeight: String = jsonObj.optString("targetHeight")
        if (targetHeight.isNotEmpty()) {
            HTMLHEIGHT = targetHeight.toInt()
        }
        if (!CROP.equals("", ignoreCase = true) && CROP.equals("Y", ignoreCase = true)) {
            setCropping = true
        }
    }

    private fun dpToPixel(dp: Int): Int {
        val r: Resources = resources
        val px: Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics)
        return px.toInt()
    }

    private fun setCompressFormat(mEncodingType2: String?) {
        if (mEncodingType2.equals("PNG", ignoreCase = true)) {
            mCompressFormat = Bitmap.CompressFormat.PNG
            mEncodingFormat = ".png"
        } else {
            mCompressFormat = Bitmap.CompressFormat.JPEG
            mEncodingFormat = ".jpg"
        }
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            setResult(RESULT_CANCELED, Intent())
            finish()
            return
        }
        // handle result of pick image chooser
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            handlePickImageResponse(data, resultCode)
        } else if (requestCode == PICK_FROM_CAMERA) {
            if (setCropping) {
                startCropImageActivity(mImageCaptureUri)
            } else {
                val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
                //bmOptions.inJustDecodeBounds = false;
                val finalBitmap: Bitmap = BitmapFactory.decodeFile(completePath, bmOptions)
                val finalBitmapBytes: ByteArray =
                    CameraUtils.getBytesFromBitmap(finalBitmap, cmpLevel, mCompressFormat)!!
                val triple = Triple(HTMLWIDTH,HTMLHEIGHT, setCropping)
                val finalCompressedImage: Bitmap = CameraUtils.compressImage(
                    this, finalBitmapBytes, mEncodingFormat!!, mUncompressed,
                    mImageCaptureUri!!.path, triple
                )!!
                processBitmap(finalCompressedImage)
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY) {
            val picturePath: String
            try {
                val selectedImage: Uri = data?.data!!
                if (selectedImage.toString()
                        .contains("file:///")
                ) {
                    picturePath = selectedImage
                        .toString()
                        .substring(7)
                } else {
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor = contentResolver.query(
                        selectedImage,
                        filePathColumn, null, null, null
                    )!!
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    picturePath = cursor.getString(columnIndex)
                    cursor.close()
                }
                FILENAME = picturePath.substring(
                    picturePath.lastIndexOf('/') + 1,
                    picturePath.lastIndexOf('.')
                )
                setCompressFormat(
                    picturePath.substring(
                        picturePath
                            .lastIndexOf('.') + 1
                    )
                )
                if (setCropping) {
                    startCropImageActivity(
                        FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID,
                            File(picturePath)
                        )
                    )
                } else {
                    val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
                    //bmOptions.inJustDecodeBounds = false;
                    val finalBitmap: Bitmap = BitmapFactory.decodeFile(picturePath, bmOptions)
                    val finalBitmapBytes: ByteArray =
                        CameraUtils.getBytesFromBitmap(finalBitmap, cmpLevel, mCompressFormat)!!
                    val triple = Triple(HTMLWIDTH,HTMLHEIGHT, setCropping)
                    val finalCompressedImage: Bitmap = CameraUtils.compressImage(
                        this, finalBitmapBytes, mEncodingFormat!!, mUncompressed,
                        picturePath, triple
                    )!!
                    processBitmap(finalCompressedImage)
                }
            } catch (e: Exception) {
                val intent = Intent()
                intent.putExtra("error", "APZ-CNT-010")
                setResult(RESULT_FIRST_USER, intent)
                finish()
            }
        }
    }

    private fun handlePickImageResponse(data: Intent?, resultCode: Int) {
        val result: CropImage.ActivityResult = getActivityResult(data)
        if (resultCode == RESULT_OK) {
            val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
            // bmOptions.inJustDecodeBounds = true;
            val bitmap: Bitmap = BitmapFactory.decodeFile(
                result.uri.path,
                bmOptions
            )
            val finalBitmapBytes: ByteArray = CameraUtils.getBytesFromBitmap(
                bitmap,
                cmpLevel, mCompressFormat
            )!!
            val triple = Triple(HTMLWIDTH,HTMLHEIGHT, setCropping)
            val finalCompressedImage: Bitmap = CameraUtils.compressImage(
                this, finalBitmapBytes, mEncodingFormat!!, mUncompressed,
                result.uri.path, triple
            )!!
            processBitmap(finalCompressedImage)
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, "Cropping failed: " + result.error, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun startCropImageActivity(imageUri: Uri?) {
        try {
            if (CROP_BOX.equals("SQUARE", ignoreCase = true)) {
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setMultiTouchEnabled(true)
                    .start(this)
            } else {
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setMultiTouchEnabled(true)
                    .start(this)
            }
        } catch (e: Exception) {
            ApzLogger.i("PROBLEM", "" + e.toString())
        }
    }

    private fun processBitmap(croppedandcompressedIMG: Bitmap) {
        if (ACTION.equals("base64_Save", ignoreCase = true)) {
            val pathToImage = saveImage(croppedandcompressedIMG)
            if (pathToImage != null) {
                sendBase64Result(croppedandcompressedIMG, pathToImage)
            } else {
                val intent = Intent()
                intent.putExtra("error", "Exception in saving image")
                setResult(RESULT_CANCELED, intent)
            }
        } else if (ACTION.equals("base64", ignoreCase = true)) {
            val base64Image = getBase64Image(croppedandcompressedIMG)
            if (base64Image != null) {
                UserSettings.setBase64Image(base64Image, EncryptedPrefHelper.getPrefs())
                setResult(RESULT_OK, Intent())
            } else {
                ApzLogger.e(TAG, "Exception in getting base64 image")
                val intent = Intent()
                intent.putExtra("error", "Error in getting base64")
                setResult(RESULT_CANCELED, intent)
            }
        } else if (ACTION.equals("save", ignoreCase = true)) {
            val pathToImage = saveImage(croppedandcompressedIMG)
            if (pathToImage != null) {
                val intent = Intent()
                intent.putExtra("url", pathToImage)
                setResult(RESULT_OK, intent)
            } else {
                val intent = Intent()
                intent.putExtra("error", "Exception in saving image")
                setResult(RESULT_CANCELED, intent)
            }
        }
        finish()
    }

    private fun sendBase64Result(
        croppedandcompressedIMG: Bitmap,
        pathToImage: String?
    ) {
        val base64Image = getBase64Image(croppedandcompressedIMG)
        if (base64Image != null) {
            UserSettings.setBase64Image(base64Image, EncryptedPrefHelper.getPrefs())
            val intent = Intent()
            intent.putExtra("url", pathToImage)
            setResult(RESULT_OK, intent)
        } else {
            ApzLogger.e(TAG, "Exception in base64 image")
            val intent = Intent()
            intent.putExtra("error", "Exception in base64 image")
            setResult(RESULT_CANCELED, intent)
        }
    }

    private fun getBase64Image(bitmap: Bitmap): String? {
        var returnBase64: String? = null
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(mCompressFormat, cmpLevel, baos) // mBitmap is the
            // bitmap object
            val b = baos.toByteArray()
            val base64Image = Base64.encodeToString(b, Base64.NO_WRAP)
            returnBase64 = base64Image
            baos.close()
        } catch (e: OutOfMemoryError) {
            ApzLogger.e(TAG, "Exception in base64 image$e")
            val intent = Intent()
            intent.putExtra("error", e.message)
            setResult(RESULT_CANCELED, intent)
        } catch (e: IOException) {
            ApzLogger.e(TAG, "Exception in base64 image$e")
            val intent = Intent()
            intent.putExtra("error", e.message)
            setResult(RESULT_CANCELED, intent)
        }
        return returnBase64
    }

    val filename: String
        get() {
            val file = File(
                FileAccessHelper.getExternalFileDirFile(context!!), "cropped/Images"
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            return (file.absolutePath + "/"
                    + System.currentTimeMillis() + mEncodingFormat)
        }

    private fun saveImage(bitmp: Bitmap): String? {
        val photoFile: String
        var photoFilepath: String? = null
        val dateFormat = SimpleDateFormat("ddmmyyhhmmss")
        val date = dateFormat.format(Date())
        photoFile = FILENAME + date + mEncodingFormat
        try {
            val pictureFileDir = getDir("photo")
            if (pictureFileDir != null) {
                val filenameWithPath = (pictureFileDir.path
                        + File.separator + photoFile)
                //                pictureFile = new File(filenameWithPath);
                pictureFile = AppzillonUtils.getApzFile(filenameWithPath, null)
                if (pictureFile!!.exists() && pictureFile!!.delete()) {
                    val contentUri: Uri = Uri
                        .parse(
                            ("file://"
                                    + FileAccessHelper.getExternalFileDirFile(context!!))
                        )
                    MediaScannerConnection.scanFile(context, arrayOf(File(contentUri.toString()).absolutePath), null) { _, _ ->
                        // Scanning is complete, and the file is now available in the media database
                        Log.d("TAG","SUCCESS")
                    }
                    try {
                        pictureFile!!.createNewFile()
                    } catch (e: IOException) {
                        //Sonar fix
                    } catch (e: OutOfMemoryError) {
                        val intent = Intent()
                        intent.putExtra("error", e.message)
                        setResult(RESULT_CANCELED, intent)
                    }
                }
                val fos = FileAccessHelper.getFileOutPutStream(pictureFile!!)
                val bos = BufferedOutputStream(
                    fos,
                    BUFFER_SIZE
                )
                bitmp.compress(mCompressFormat, cmpLevel, bos)
                bos.flush()
                bos.close()
                fos.close()
                photoFilepath = filenameWithPath
            }
        } catch (e: FileNotFoundException) {
            ApzLogger.e(TAG, e.message!!)
        } catch (e: IOException) {
            ApzLogger.e(TAG, e.message!!)
        } catch (e: Exception) {
            val intent = Intent()
            intent.putExtra("error", e.message)
            setResult(RESULT_CANCELED, intent)
        }
        return photoFilepath
    }

    private fun getDir(loc: String): File? {
        if (FileUtils.isSDCardPresent()) {
            val sdDir = File(
                AppzillonMainScreen.SANDBOX_LOC
                        + File.separator + AppzillonMainScreen.ASSET_APP_LOC + loc
            )
            try {
                return if (sdDir.exists()) {
                    sdDir
                } else {
                    if (sdDir.mkdirs()) {
                        sdDir
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                ApzLogger.e(TAG, "Create directory failed : " + e.message)
            }
        } else {
            val intent = Intent()
            intent.putExtra(
                "sdcard_status",
                resources.getString(R.string.sdcard_unavailable)
            )
            setResult(RESULT_CANCELED, intent)
        }
        return null
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED,  Intent())
        finish()
    }

    companion object {
        private const val PICK_FROM_CAMERA = 1
        private const val PICK_IMAGE_FROM_GALLERY = 4
        var setCropping = false
        const val properties = "USER_PREFS"
        var toStoreTempImage: File? = null
    }
}