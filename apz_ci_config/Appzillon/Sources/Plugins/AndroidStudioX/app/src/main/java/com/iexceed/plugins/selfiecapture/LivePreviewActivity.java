// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.iexceed.plugins.selfiecapture;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.annotation.KeepName;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.iexceed.appzillonapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.faceFontColor;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.faceInstruction1;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.faceOverlayColor;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.facePageTitle;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.failureCallback;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.fetchOutput;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.nativePreviewScreen;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.setInstructionTextView;

/**
 * Demo app showing the various features of ML Kit for Firebase. This class is used to
 * set up continuous frame processing on frames from a camera source.
 */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback,
        CompoundButton.OnCheckedChangeListener {
    private static final String FACE_DETECTION = "Face Detection";
    private static final String OBJECT_DETECTION = "Object Detection";
    private static final String AUTOML_IMAGE_LABELING = "AutoML Vision Edge";
    private static final String TEXT_DETECTION = "Text Detection";
    private static final String BARCODE_DETECTION = "Barcode Detection";
    private static final String IMAGE_LABEL_DETECTION = "Label Detection";
    private static final String CLASSIFICATION_QUANT = "Classification (quantized)";
    private static final String CLASSIFICATION_FLOAT = "Classification (float)";
    private static final String FACE_CONTOUR = "Face Contour";
    private static final String TAG = "LivePreviewActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private static final int FACE_IMG_PREV = 88;
    public static FaceDetectionProcessor.onFaceDetectionHandler handler;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private OvalGraphicOverlay ovalGraphicOverlay;
    public static FrameLayout ovalFrame;
    private String selectedModel = FACE_CONTOUR;
    private int frameWidth = 0;
    private int frameHeight = 0;
    public static float screenHeight;
    public static float screenWidth;
    public static DisplayMetrics display;
    public static TextView fPageTitle;
    public static TextView fInstruction1;
    public static TextView fInstruction2;
    public static TextView fInstruction3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "onCreate");
            setContentView(R.layout.activity_live_preview);

            preview = findViewById(R.id.firePreview);
            if (preview == null) {
                Log.d(TAG, "Preview is null");
            }
            graphicOverlay = findViewById(R.id.fireFaceOverlay);
            ovalFrame = findViewById(R.id.oval_frame);
            ovalGraphicOverlay = findViewById(R.id.ovalOverlay);
            fPageTitle = findViewById(R.id.face_pagetitle);
            fInstruction1 = findViewById(R.id.face_instruction1);
            fInstruction2 = findViewById(R.id.face_instruction2);
            fInstruction3 = findViewById(R.id.face_instruction3);
            GradientDrawable gd = (GradientDrawable) ovalFrame.getBackground();
            gd.setStroke(2, Color.parseColor(faceOverlayColor));
            if (graphicOverlay == null) {
                Log.d(TAG, "graphicOverlay is null");
            }
            selectedModel = FACE_DETECTION;
            // Hide the toggle button if there is only 1 camera
            if (Camera.getNumberOfCameras() == 1) {
                failureCallback("Camera invalid");
                // facingSwitch.setVisibility(View.GONE);
                //throw error
            }

            //if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
      /*  } else {
            getRuntimePermissions();
        }*/
        }catch(Exception e){
            failureCallback("Invalid json parameters");
        }
    }
/*
    @Override
    public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        selectedModel = parent.getItemAtPosition(pos).toString();
        Log.d(TAG, "Selected model: " + selectedModel);
        preview.stop();
        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
            startCameraSource();
        } else {
            getRuntimePermissions();
        }
    }*/
    public void changeFrameDimension(){
        getScreenWidth();
        getScreenHeight();
        frameWidth = (int) (screenWidth * 0.8);
        frameHeight = (int) (frameWidth * 8/7);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(frameWidth, frameHeight, Gravity.CENTER);
        ovalFrame.setLayoutParams(params);
        fPageTitle.setText(facePageTitle);
        setInstructionTextView(faceInstruction1);
        fPageTitle.setTextColor(Color.parseColor(faceFontColor));
        fInstruction1.setTextColor(Color.parseColor(faceFontColor));
        fInstruction2.setTextColor(Color.parseColor(faceFontColor));
        fInstruction3.setTextColor(Color.parseColor(faceFontColor));
    }
    public float getScreenWidth() {
        display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        screenWidth = display.widthPixels;
        return screenWidth;
    }

    public float getScreenHeight() {
        display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        screenHeight = display.heightPixels;
        return screenHeight;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
        if (cameraSource != null) {
            if (isChecked) {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
            } else {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
            }
        }
        preview.stop();
        startCameraSource();
    }


    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
            cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
        //startCameraSource();
        //ovalGraphicOverlay.invalidate();

        try {
            switch (model) {
                case FACE_DETECTION:
                    Log.i(TAG, "Using Face Detector Processor");
                    cameraSource.setMachineLearningFrameProcessor(new FaceDetectionProcessor(getResources()));
                    break;
                case FACE_CONTOUR:
                    Log.i(TAG, "Using Face Contour Detector Processor");
                    cameraSource.setMachineLearningFrameProcessor(new FaceContourDetectorProcessor());
                    break;
                default:
                    Log.e(TAG, "Unknown model: " + model);
            }

        handler = new FaceDetectionProcessor.onFaceDetectionHandler() {
            @Override
            public void onFaceDetected(FirebaseVisionFace face, Bitmap firebaseImgBmp) {
               preview.stop();
                startAnAct(firebaseImgBmp);
                Log.i(TAG, "onFaceDetected: "+face +firebaseImgBmp);
            }
        };
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: " + model, e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        changeFrameDimension();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
    @Override
    public void onBackPressed() {
        failureCallback("Operation cancelled");
        finish();
        //  super.onBackPressed();

    }
    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }
    public void startAnAct(Bitmap objectThumbnailForBottomSheet) {
        try {
            if (objectThumbnailForBottomSheet != null) {
                if (nativePreviewScreen.equalsIgnoreCase("Y")) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    objectThumbnailForBottomSheet.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytearr = stream.toByteArray();
                    Intent intent = new Intent(this, ImagePreviewActivity.class);
                    //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("bitmap", bytearr);
                    startActivityForResult(intent, FACE_IMG_PREV);
                } else {
                    fetchOutput(objectThumbnailForBottomSheet);
                    finish();
                }
            }
        }catch (Exception e){

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();
        try {

            Log.i(TAG, "onActivityResult: liveobj");

            if (resultCode == RESULT_OK) {
                if (data != null) {
                    byte[] bytearr = data.getByteArrayExtra("bitmap");
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytearr, 0, bytearr.length);
                    fetchOutput(bmp);
                }
            } else if (resultCode == RESULT_CANCELED) {
                setResult(RESULT_CANCELED, intent);
            }
            finish();
            // }
        } catch (Exception e) {
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

}
