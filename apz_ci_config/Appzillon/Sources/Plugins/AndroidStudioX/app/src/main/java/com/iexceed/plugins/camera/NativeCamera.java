package com.iexceed.plugins.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.util.Base64;
import android.util.TypedValue;
import android.widget.Toast;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.CameraUtils;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.UserSettings;
import com.iexceed.appzillonapp.BuildConfig;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.iexceed.appzillonapp.AppzillonMainScreen.ASSET_APP_LOC;
import static com.iexceed.appzillonapp.AppzillonMainScreen.SANDBOX_LOC;
import static com.theartofdev.edmodo.cropper.CropImage.getActivityResult;

public class NativeCamera extends Activity {

    private Uri mImageCaptureUri;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_IMAGE_FROM_GALLERY = 4;

    private String CROP;
    private String CROP_BOX;
    private int cmpLevel;
    private String FILENAME;
    public static boolean setCropping = false;//Bug #9476
    private String TAG = "nativeCamera";
    private SharedPreferences settings;
    final static String properties = "USER_PREFS";
    final int BUFFER_SIZE = 1024 * 8;
    File forTestingPath;
    public static File toStoreTempImage;
    public String mEncodingType;
    public Bitmap.CompressFormat mCompressFormat;
    private String mEncodingFormat;
    String sourceType;
    File pictureFile;
    String ACTION;
    Context context;
    public int HTMLWIDTH = 0;
    public int HTMLHEIGHT = 0;
    private String completePath;
    private boolean mUncompressed = false;
	private String frontcamera = "N";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.native_camera_layout);
        context = this;
        setCropping = false;
        settings = getApplication().getSharedPreferences(properties, 0);

        Intent extras = getIntent();
        String jsonstr = extras.getExtras().getString("jsonStr");

        try {
            JSONObject jsonObj = new JSONObject(jsonstr);
            FILENAME = jsonObj.optString("fileName");
            ACTION = jsonObj.optString("action");
            CROP = jsonObj.optString("crop");
            CROP_BOX = jsonObj.optString("cropBox");
            sourceType = jsonObj.optString("sourceType");
            mEncodingType = jsonObj.optString("encodingType");
			frontcamera = jsonObj.optString("frontCamera");
            String uncompressed = jsonObj.optString("unCompressed");
            mUncompressed = uncompressed.equalsIgnoreCase("Y");

            String compressionLevel = jsonObj.optString("quality");
            if (!compressionLevel.equals("")) {
                cmpLevel = Integer.parseInt(compressionLevel);
            } else {
                cmpLevel = 100;
            }
            if (sourceType.equalsIgnoreCase("") || sourceType == null) {
                sourceType = "Camera";
            }
            String targetWidth = jsonObj.optString("targetWidth");
            if (!targetWidth.isEmpty() && targetWidth != null) {
                HTMLWIDTH = Integer.parseInt(targetWidth);
            }
            String targetHeight = jsonObj.optString("targetHeight");
            if (!targetHeight.isEmpty() && targetHeight != null) {
                HTMLHEIGHT = Integer.parseInt(targetHeight);
            }

            if (!CROP.equalsIgnoreCase("") && CROP.equalsIgnoreCase("Y")) {
                setCropping = true;
            }

        } catch (JSONException e) {
            ApzLogger.e(TAG, e.toString());
        }

        if (sourceType.equalsIgnoreCase("Camera")) {
            setCompressFormat(mEncodingType);
            try {
//                forTestingPath = new File(SANDBOX_LOC + File.separator + ASSET_APP_LOC + "appzillonTemp"
//                        + File.separator);
                forTestingPath = AppzillonUtils.getApzFile(SANDBOX_LOC + File.separator + ASSET_APP_LOC + "appzillonTemp" + File.separator,null);
                if (!forTestingPath.exists()) {
                    forTestingPath.mkdirs();
                }
                completePath = forTestingPath.getAbsolutePath() + File.separator +
                        String.valueOf(System.currentTimeMillis())
                        + mEncodingFormat;
                toStoreTempImage = new File(forTestingPath,
                        String.valueOf(System.currentTimeMillis())
                                + mEncodingFormat);
                Intent intent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                mImageCaptureUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, toStoreTempImage);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);
				if(frontcamera.equalsIgnoreCase("Y")) {
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
               }
                intent.putExtra("return-data", true);
                intent.putExtra("outputX", dpToPixel(HTMLWIDTH));
                intent.putExtra("outputY", dpToPixel(HTMLHEIGHT));
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                
            }
        } else if (sourceType.equalsIgnoreCase("Photo")) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, PICK_IMAGE_FROM_GALLERY);

        }

    }

    public int dpToPixel(int dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    private void setCompressFormat(String mEncodingType2) {
        if (mEncodingType2.equalsIgnoreCase("JPG")
                || mEncodingType2.equalsIgnoreCase("JPEG")) {
            mCompressFormat = Bitmap.CompressFormat.JPEG;
            mEncodingFormat = ".jpg";
        } else if (mEncodingType2.equalsIgnoreCase("PNG")) {
            mCompressFormat = Bitmap.CompressFormat.PNG;
            mEncodingFormat = ".png";
        } else {
            mCompressFormat = Bitmap.CompressFormat.JPEG;
            mEncodingFormat = ".jpg";
        }

    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            Intent in = new Intent();
            setResult(RESULT_CANCELED, in);
            finish();
            return;
        }
        // handle result of pick image chooser
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = getActivityResult(data);
            if (resultCode == RESULT_OK) {

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                // bmOptions.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(result.getUri().getPath(),
                        bmOptions);
                if (bitmap != null) {
                    byte[] finalBitmapBytes = CameraUtils.getBytesFromBitmap(bitmap,
                            cmpLevel, mCompressFormat);

                    Bitmap finalCompressedImage = CameraUtils.compressImage(
                            finalBitmapBytes, mEncodingFormat, mUncompressed, HTMLWIDTH,
                            HTMLHEIGHT, setCropping, result.getUri().getPath());
                    processBitmap(finalCompressedImage);
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PICK_FROM_CAMERA) {
            if (setCropping) {
                startCropImageActivity(mImageCaptureUri);
            } else {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                //bmOptions.inJustDecodeBounds = false;
                Bitmap finalBitmap = BitmapFactory.decodeFile(completePath, bmOptions);
                if (finalBitmap != null) {
                    byte[] finalBitmapBytes = CameraUtils.getBytesFromBitmap(finalBitmap, cmpLevel, mCompressFormat);
                    Bitmap finalCompressedImage = CameraUtils.compressImage(finalBitmapBytes, mEncodingFormat, mUncompressed, HTMLWIDTH,
                            HTMLHEIGHT, setCropping, mImageCaptureUri.getPath());
                    processBitmap(finalCompressedImage);

                } else {
                    Intent in = new Intent();
                    in.putExtra("error", "Exception in decoding the file");
                    setResult(RESULT_CANCELED, in);
                }
            }
        } else if (requestCode == PICK_IMAGE_FROM_GALLERY) {
            String picturePath;
            try {
                Uri selectedImage = data.getData();
                if (selectedImage.toString()
                        .contains("file:///")) {
                    picturePath = selectedImage
                            .toString()
                            .substring(7);
                } else {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                }
                FILENAME = picturePath.substring(picturePath.lastIndexOf('/') + 1,
                        picturePath.lastIndexOf('.'));
                setCompressFormat(picturePath.substring(picturePath
                        .lastIndexOf('.') + 1));

                if (setCropping) {
                    //                startCropImageActivity(FileProvider.getUriForFile(NativeCamera.this,
                    //                        BuildConfig.APPLICATION_ID + ".provider",
                    //                        new File(picturePath)));
                    startCropImageActivity(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, new File(picturePath)));
                } else {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    //bmOptions.inJustDecodeBounds = false;
                    Bitmap finalBitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
                    if (finalBitmap != null) {
                        byte[] finalBitmapBytes = CameraUtils.getBytesFromBitmap(finalBitmap, cmpLevel, mCompressFormat);

                        Bitmap finalCompressedImage = CameraUtils.compressImage(finalBitmapBytes, mEncodingFormat, mUncompressed, HTMLWIDTH,
                                HTMLHEIGHT, setCropping, picturePath);
                        processBitmap(finalCompressedImage);
                    } else {
                        Intent in = new Intent();
                        in.putExtra("error", "Exception in decoding the file");
                        setResult(RESULT_CANCELED, in);
                    }
                }
            } catch (Exception e) {
                Intent in = new Intent();
                in.putExtra("error", "APZ-CNT-010");
                setResult(RESULT_FIRST_USER, in);
                finish();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        try {
            if (CROP_BOX.equalsIgnoreCase("SQUARE")) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setMultiTouchEnabled(true)
                        .start(this);
            } else {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setMultiTouchEnabled(true)
                        .start(this);
            }

        } catch (Exception e) {
            ApzLogger.i("PROBLEM", "" + e.toString());
        }
    }


    private void processBitmap(Bitmap croppedandcompressedIMG) {

        if (ACTION.equalsIgnoreCase("base64_Save")) {
            String pathToImage = saveImage(croppedandcompressedIMG);
            if (pathToImage != null) {
                String base64Image = getBase64Image(croppedandcompressedIMG);
                if (base64Image != null) {
                    UserSettings.setBase64Image(base64Image, settings);
                    Intent in = new Intent();
                    in.putExtra("url", pathToImage);
                    setResult(RESULT_OK, in);
                } else {
                    ApzLogger.e(TAG, "Exception in base64 image");
                    Intent in = new Intent();
                    in.putExtra("error", "Exception in base64 image");
                    setResult(RESULT_CANCELED, in);
                }

            } else {
                ApzLogger.e(TAG, "Exception in saving image");
                Intent in = new Intent();
                in.putExtra("error", "Exception in saving image");
                setResult(RESULT_CANCELED, in);
            }
        } else if (ACTION.equalsIgnoreCase("base64")) {
            String base64Image = getBase64Image(croppedandcompressedIMG);
            if (base64Image != null) {
                UserSettings.setBase64Image(base64Image, settings);
                Intent in = new Intent();
                setResult(RESULT_OK, in);
            } else {
                ApzLogger.e(TAG, "Exception in getting base64 image");
                Intent in = new Intent();
                in.putExtra("error", "Error in getting base64");
                setResult(RESULT_CANCELED, in);
            }
        } else if (ACTION.equalsIgnoreCase("save")) {
            String pathToImage = saveImage(croppedandcompressedIMG);
            if (pathToImage != null) {
                Intent in = new Intent();
                in.putExtra("url", pathToImage);
                setResult(RESULT_OK, in);
            } else {
                ApzLogger.e(TAG, "Exception in saving image");
                Intent in = new Intent();
                in.putExtra("error", "Exception in saving image");
                setResult(RESULT_CANCELED, in);
            }
        }
        finish();

    }

    private String getBase64Image(Bitmap bitmap) {
        String returnBase64 = null;
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(mCompressFormat, cmpLevel, baos); // mBitmap is the
            // bitmap object
            byte[] b = baos.toByteArray();
            String base64Image = Base64.encodeToString(b, Base64.NO_WRAP);
            returnBase64 = base64Image;
            baos.close();
            baos = null;
            bitmap = null;

        } catch (OutOfMemoryError e) {
            ApzLogger.e(TAG, "Exception in base64 image" + e);
            Intent in = new Intent();
            in.putExtra("error", e.getMessage());
            setResult(RESULT_CANCELED, in);
        } catch (IOException e) {
            ApzLogger.e(TAG, "Exception in base64 image" + e);
            Intent in = new Intent();
            in.putExtra("error", e.getMessage());
            setResult(RESULT_CANCELED, in);
        }

        return returnBase64;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "cropped/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + mEncodingFormat);
        return uriSting;

    }

    private String saveImage(Bitmap bitmp) {

        String photoFile;
        String photoFilepath = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddmmyyhhmmss");
        String date = dateFormat.format(new Date());
        photoFile = FILENAME + date + mEncodingFormat;
        try {

            File pictureFileDir = getDir("photo");
            if (pictureFileDir != null) {
                String filenameWithPath = pictureFileDir.getPath()
                        + File.separator + photoFile;
//                pictureFile = new File(filenameWithPath);
                pictureFile = AppzillonUtils.getApzFile(filenameWithPath,null);
                if (pictureFile.exists()) {
                    if (pictureFile.delete()) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent mediaScanIntent = new Intent(
                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri
                                    .parse("file://"
                                            + Environment
                                            .getExternalStorageDirectory()); // out
                            mediaScanIntent.setData(contentUri);
                            this.sendBroadcast(mediaScanIntent);
                        } else {
                            sendBroadcast(new Intent(
                                    Intent.ACTION_MEDIA_MOUNTED,
                                    Uri.parse("file://"
                                            + Environment
                                            .getExternalStorageDirectory())));
                        }
                        // Abhishek 18 March 2015 to handle the crash while
                        // sending broadcast in KITKAT and above END
                        try {

                            boolean isCreated = pictureFile.createNewFile();

                        } catch (IOException e) {
                           
                        } catch (OutOfMemoryError e) {
                            Intent in = new Intent();
                            in.putExtra("error", e.getMessage());
                            setResult(RESULT_CANCELED, in);
                        }

                    }
                }
                FileOutputStream fos = new FileOutputStream(pictureFile);

                final BufferedOutputStream bos = new BufferedOutputStream(fos,
                        BUFFER_SIZE);
                bitmp.compress(mCompressFormat, cmpLevel, bos);
                bos.flush();
                bos.close();
                fos.close();
                photoFilepath = filenameWithPath;

                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[]{pictureFile.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
//                                ApzLogger.i(TAG, "ExternalStorage Scanned "
//                                        + path + ":");
//                                ApzLogger.i(TAG, "ExternalStorage -> uri="
//                                        + uri);
                            }
                        });
            }

        } catch (FileNotFoundException e) {
            ApzLogger.e(TAG, e.getMessage());
        } catch (IOException e) {
            ApzLogger.e(TAG, e.getMessage());
        } catch (OutOfMemoryError e) {
            ApzLogger.e(TAG, "Exception in saving image" + e);
            Intent in = new Intent();
            in.putExtra("error", e.getMessage());
            setResult(RESULT_CANCELED, in);
        }
        return photoFilepath;

    }

    private File getDir(String loc) {
        if (MediaUtils.isSDCardPresent()) {
            File sdDir = new File(SANDBOX_LOC
                    + File.separator + ASSET_APP_LOC + loc);
            try {
                if (sdDir.exists()) {
                    return sdDir;
                } else {
                    if (sdDir.mkdirs()) {
                        return sdDir;
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {
               
                ApzLogger.e(TAG, "Create directory failed : " + e.getMessage());
            }
        } else {
            Intent in = new Intent();
            in.putExtra("sdcard_status",
                    getResources().getString(R.string.sdcard_unavailable));
            setResult(RESULT_CANCELED, in);
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        setResult(RESULT_CANCELED, in);
        finish();
    }

}
