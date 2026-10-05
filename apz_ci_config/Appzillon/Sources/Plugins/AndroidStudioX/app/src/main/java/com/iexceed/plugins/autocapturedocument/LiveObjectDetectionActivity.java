/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iexceed.plugins.autocapturedocument;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

//import com.google.common.collect.ImmutableList;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.autocapturedocument.WorkflowModel.WorkflowState;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.defaultCaptureMode;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.deviceTextRecognition;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.documentAspectRatio;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.failureCallback;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.fetchOutput;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.isManualCapture;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.istoggleButtonReq;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.mFontColor;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.mOverlayColor;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.nativePreviewScreen;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.tPortMarginPercent;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.topMarginPercent;



/**
 * Demonstrates the object detection and visual search workflow using camera preview.
 */
public class LiveObjectDetectionActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "LiveObjectActivity";

    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private FrameGraphicOverlay frameGraphicOverlay;

    private ProgressBar searchProgressBar;
    public static FrameLayout cardFrame;
    public ConstraintLayout constraint;
    private WorkflowModel workflowModel;
    private WorkflowState currentWorkflowState;
    // private SearchEngine searchEngine;
    private Bitmap objectThumbnailForBottomSheet;
    private int IMG_PREVIEW = 18;
    public static int outputOrien = 8;
    public static float screenHeight;
    public static float screenWidth;
    public static DisplayMetrics display;
    public static TextView cScanningTextMsg;
    public static TextView cPageTitle;
    public static TextView cMsgTitle;
    public static TextView cMessage;
    public ToggleButton captureMode;
    public static ImageButton clickCapture;
    public static RectF frameRect;
    private int frameWidth = 0;
    private int frameHeight = 0;
    private int prevOrientation = 0;
    private Guideline horizonG;
    private Guideline landHorizonG;
    private LayerDrawable layerDrawable;
    public static Context lContext;
    public static Activity lActivity;
    public static boolean manualClick = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.e_activity_live_object);
            preview = findViewById(R.id.camera_preview);
            graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay);
            cardFrame = findViewById(R.id.edge_frame);
            frameGraphicOverlay = findViewById(R.id.frame_preview_graphic_overlay);
            graphicOverlay.setOnClickListener(this);
            cameraSource = new CameraSource(graphicOverlay);
            cScanningTextMsg = findViewById(R.id.scanningTextMsg);
            cPageTitle = findViewById(R.id.pagetitle);
            cMsgTitle = findViewById(R.id.msgTitle);
            cMessage = findViewById(R.id.messages);
            clickCapture = findViewById(R.id.captureButton);
            constraint = findViewById(R.id.constraintP);
            captureMode = findViewById(R.id.captureMode);
            captureMode.setTextColor(Color.parseColor(mFontColor));
            captureMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isManualCapture = true;
                        setToggleVisibility(View.VISIBLE);
                    } else {
                        isManualCapture = false;
                        setToggleVisibility(View.INVISIBLE);
                    }
                }
            });
            horizonG = findViewById(R.id.guide);
            landHorizonG = findViewById(R.id.landguide);
            layerDrawable = (LayerDrawable) getResources()
                    .getDrawable(R.drawable.lens);
       /* GradientDrawable gd = (GradientDrawable) clickCapture.getBackground();
        gd.setColor(Color.parseColor(mFontColor));*/

      /*  GradientDrawable gd1 = (GradientDrawable) captureMode.getBackground();
        gd1.setStroke(2, Color.parseColor(mFontColor));*/
            clickCapture.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    manualClick = true;
                    clickCapture.setVisibility(View.INVISIBLE);
                    // Log.i(TAG, "onClick: clicked");
                }
            });
            setButtonLenscolor();
            // searchProgressBar = findViewById(R.id.search_progress_bar);
            lContext = this.getApplicationContext();
            lActivity = this;
            getOrientation();
            ApzAutoCapturePlugin.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                  /*  if (!deviceTextRecognition) {
                        captureMode.setChecked(true);
                        captureMode.setVisibility(View.INVISIBLE);
                    } else {*/
                        if (istoggleButtonReq.equalsIgnoreCase("Y")) {
                            captureMode.setVisibility(View.VISIBLE);
                            if (defaultCaptureMode.equalsIgnoreCase("Manual")) {
                                captureMode.setChecked(true);
                            } else {
                                captureMode.setChecked(false);
                            }
                        } else {
                            captureMode.setVisibility(View.INVISIBLE);
                        }
                        // }
                    } catch (Exception e) {

                    }
                }
            });

            // outputRotate = getWindowManager().getDefaultDisplay().getRotation();
            OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
                @Override
                public void onOrientationChanged(int orientation) {
                    //   Log.i(TAG, "onOrientationChanged:   " + orientation + " device" + getResources().getConfiguration().orientation);
                    getOrientation();
                }
            };
            if (orientationEventListener.canDetectOrientation()) {
                orientationEventListener.enable();
            }
            setUpWorkflowModel();
        }catch(Exception e){
            failureCallback("Invalid json parameters","");
            finish();
        }
    }

    public void getOrientation() {
        try {
            outputOrien = getResources().getConfiguration().orientation;
            if (prevOrientation != 0) {
                if (prevOrientation == outputOrien) {
                    return;
                }
            }
            prevOrientation = outputOrien;
            getScreenWidth();
            getScreenHeight();
            final double width = Double.parseDouble(documentAspectRatio.split(":")[0]);
            final double height = Double.parseDouble(documentAspectRatio.split(":")[1]);
         //   Log.i(TAG, "Width:   " + screenWidth + " Height " + screenHeight);
            ApzAutoCapturePlugin.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ConstraintLayout.LayoutParams params = null;
                        if (outputOrien == 1) {
                            int topMargin=(int) (screenHeight * topMarginPercent) / 100;
                            frameWidth = (int) (screenWidth * (100 - tPortMarginPercent) / 100);
                            frameHeight = (int) (frameWidth * height / width);
                            frameHeight = ((frameHeight+topMargin)>=screenHeight)? ((int)screenHeight-topMargin):frameHeight;
                            params = new ConstraintLayout.LayoutParams(frameWidth, frameHeight);
                            params.topMargin = (int) (screenHeight * topMarginPercent) / 100;
                            params.topToTop = constraint.getId();
                            //  params.bottomToTop=horizonG.getId();
                            params.rightToRight = constraint.getId();
                            params.leftToLeft = constraint.getId();

                        } else if (outputOrien == 2) {
                            frameHeight = (int) (screenHeight * (100 - 43) / 100);
                            frameWidth = (int) (frameHeight * width / height);
                            frameWidth =frameWidth>screenWidth?(int)screenWidth:frameWidth;
                            params = new ConstraintLayout.LayoutParams(frameWidth, frameHeight);
                            params.topToBottom = cPageTitle.getId();
                          //  params.bottomToTop = landHorizonG.getId();
                            params.rightToRight = constraint.getId();
                            params.leftToLeft = constraint.getId();

                        }
                        cardFrame.setLayoutParams(params);

                        //frameGraphicOverlay.requestLayout();
                        frameGraphicOverlay.invalidate();
                         setButtonLenscolor();
                        cScanningTextMsg.setText(ApzAutoCapturePlugin.mScanStatus1);
                        cScanningTextMsg.setTextColor(Color.parseColor(mFontColor));
                        cPageTitle.setText(ApzAutoCapturePlugin.mPageTitle);
                        cPageTitle.setTextColor(Color.parseColor(mFontColor));
                        cMsgTitle.setText(ApzAutoCapturePlugin.mMessageTitle);
                        cMsgTitle.setTextColor(Color.parseColor(mFontColor));
                        cMessage.setText(ApzAutoCapturePlugin.mMessage);
                        cMessage.setTextColor(Color.parseColor(mFontColor));
                    } catch (Exception e) {
                       // failureCallback("Operation failed","");
                    }
                }
            });

        } catch (Exception e) {
          //  failureCallback("Operation failed","");
        }
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

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ///Full screen mode check NBF

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility)
                    {
                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                        {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });
/////Full screen mode NBF
        workflowModel.markCameraFrozen();

        currentWorkflowState = WorkflowState.NOT_STARTED;
        cameraSource.setFrameProcessor(new ProminentObjectProcessor(graphicOverlay, workflowModel));
        // cameraSource.setFrameProcessor(new TextRecognitionProcessor());
        workflowModel.setWorkflowState(WorkflowState.DETECTING);
        //getScreenHeight();
        //getScreenWidth();
        getOrientation();
        if(captureMode.getVisibility()==View.VISIBLE&& captureMode.isChecked()) {
            if (isManualCapture) {
                setToggleVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentWorkflowState = WorkflowState.NOT_STARTED;
        stopCameraPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
            objectThumbnailForBottomSheet=null;
            workflowModel=null;
            cameraSource = null;
        }
        // searchEngine.shutdown();
    }

    @Override
    public void onBackPressed() {
        failureCallback("Operation cancelled","");
        finish();
        //  super.onBackPressed();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

    }

    public static void setTextViewMessage(final String message) {
        ApzAutoCapturePlugin.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    cScanningTextMsg.setText(message);
                } catch (Exception e) {

                }
            }
        });
    }

    public static void setToggleVisibility(final int visibility) {
        ApzAutoCapturePlugin.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (visibility) {
                        case View.VISIBLE:
                            clickCapture.setVisibility(View.VISIBLE);
                            break;
                        default:
                            clickCapture.setVisibility(View.INVISIBLE);
                            break;
                    }

                } catch (Exception e) {

                }
            }
        });
    }

    public void setButtonLenscolor() {
        ApzAutoCapturePlugin.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    layerDrawable = (LayerDrawable) getResources()
                            .getDrawable(R.drawable.lens);
                    GradientDrawable gradientDrawable0 = (GradientDrawable) layerDrawable
                            .findDrawableByLayerId(R.id.ring0);
                    gradientDrawable0.setColor(Color.parseColor(mFontColor));
                    GradientDrawable gradientDrawable1 = (GradientDrawable) layerDrawable
                            .findDrawableByLayerId(R.id.ring1);
                    gradientDrawable1.setColor(Color.parseColor(mOverlayColor));
                    GradientDrawable gradientDrawable2 = (GradientDrawable) layerDrawable
                            .findDrawableByLayerId(R.id.ring2);
                    gradientDrawable2.setColor(Color.parseColor(mFontColor));
                    clickCapture.setBackground(layerDrawable);
                } catch (Exception e) {

                }
            }
        });
    }

    private void startCameraPreview() {
        if (!workflowModel.isCameraLive() && cameraSource != null) {
            try {
                workflowModel.markCameraLive();
                preview.start(cameraSource);
            } catch (IOException e) {
             //   Log.e(TAG, "Failed to start camera preview!", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private void stopCameraPreview() {
        if (workflowModel.isCameraLive()) {
            workflowModel.markCameraFrozen();
            //flashButton.setSelected(false);
            preview.stop();
        }
    }


    private void setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel.class);

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel.workflowState.observe(
                this,
                new Observer<WorkflowState>() {
                    @Override
                    public void onChanged(WorkflowState workflowState) {
                        if (workflowState == null) {
                            return;
                        }
                        if (workflowState == WorkflowState.CONFIRMED) {
                            stopCameraPreview();
                        }
                        currentWorkflowState = workflowState;
                     //   Log.d(TAG, "Current workflow state: " + currentWorkflowState.name());
                        LiveObjectDetectionActivity.this.stateChangeInManualSearchMode(workflowState);
               /* if (PreferenceUtils.isAutoSearchEnabled(LiveObjectDetectionActivity.this)) {
                  stateChangeInAutoSearchMode(workflowState);
                } else {
                  LiveObjectDetectionActivity.this.stateChangeInManualSearchMode(workflowState);
                }*/
                    }
                });

        // Observes changes on the object to search, if happens, fire product search request.
        workflowModel.objectToSearch.observe(
                this, new Observer<DetectedObject>() {
                    @Override
                    public void onChanged(DetectedObject object) {
                        try {
                            FirebaseVisionImage image = workflowModel.getFireImage();
                            workflowModel.setFrameImage(null);
                            if (image != null) {
                                objectThumbnailForBottomSheet = image.getBitmap();
                            } else {
                                objectThumbnailForBottomSheet = object.getObjectThumbnail();
                            }
                            stopCameraPreview();
                            startAnAct(objectThumbnailForBottomSheet);
                        }catch(Exception e){
                            failureCallback("Error occured","");
                        }
                    }
                });


    }

    public void startAnAct(Bitmap objectThumbnailForBottomSheet) {
        try {
            if (objectThumbnailForBottomSheet != null) {
               /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                objectThumbnailForBottomSheet.compress(Bitmap.CompressFormat.JPEG, 100, stream);*/
              //  byte[] bytearr = stream.toByteArray();
                String fbtext = "";
                String fbfulltext = "";

                if (deviceTextRecognition) {
                    String oldLine ="";
                    String newLine ="";
                    FirebaseVisionText fireText = workflowModel.getFireText();
                    fbfulltext = fireText.getText();
                    if(!fbfulltext.isEmpty()) {
                        if (!TextValidator.getModifiedPassportLine().isEmpty()){
                            oldLine = fbfulltext.substring(fbfulltext.lastIndexOf("\n"));
                            newLine = "\n" + TextValidator.getModifiedPassportLine();
                            fbfulltext = fbfulltext.replace(oldLine, newLine);
                        }
                        if (!TextValidator.getModifiedTD1Line().isEmpty()) {
                            String[] splitTexts = fbfulltext.split("\n");
                            oldLine = splitTexts[splitTexts.length - 3];
                            newLine = TextValidator.getModifiedTD1Line() + "\n";
                            fbfulltext = fbfulltext.replace(oldLine, newLine);
                        }
                    }
                    fbtext = ocrText(fireText,oldLine,newLine).toString();
                    //    Log.i(TAG, " checkVisionText liveMainaActivity: " + fireText.getText() + " FirebaseVisionText " + fireText);
                }/*else{
                    String fireText = workflowModel.getTesseractText();
                    fbtext = "";
                    fbfulltext = fireText;
                }*/

//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (nativePreviewScreen.equalsIgnoreCase("Y")) {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                    objectThumbnailForBottomSheet.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                 //   objectThumbnailForBottomSheet.recycle();
                    Intent intent = new Intent(this, ImagePreviewActivity.class);
                    intent.putExtra("bitmap", filename);
                    intent.putExtra("firebaseText", fbtext);
                    intent.putExtra("firebaseWholeText", fbfulltext);
                    startActivityForResult(intent, IMG_PREVIEW);
                } else {
                    fetchOutput(objectThumbnailForBottomSheet, fbtext, fbfulltext);
                    finish();
                }
            }
        }catch(Exception e){
            failureCallback("Error occured","");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();
        try {

          //  Log.i(TAG, "onActivityResult: liveobj");

            if (resultCode == RESULT_OK) {
                if (data != null) {
                  String filename = data.getStringExtra("bitmap");
                    String array = data.getExtras().getString("ocrText");
                    String textStr = data.getExtras().getString("ocrFullText");
                    FileInputStream is = this.openFileInput(filename);
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    is.close();
                    fetchOutput(bmp, array, textStr);
                }
                finish();
            }/* else if (resultCode == RESULT_CANCELED) {
                setResult(RESULT_CANCELED, intent);
            }*/


            // }
        } catch (Exception e) {
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }


    private void stateChangeInManualSearchMode(WorkflowState workflowState) {
        //   boolean wasPromptChipGone = (promptChip.getVisibility() == View.GONE);
        // boolean wasSearchButtonGone = (searchButton.getVisibility() == View.GONE);

        //  searchProgressBar.setVisibility(View.GONE);
        switch (workflowState) {
            case DETECTING:
            case DETECTED:
            case CONFIRMING:
                // promptChip.setVisibility(View.VISIBLE);
                //   promptChip.setText(R.string.prompt_point_at_an_object);
                // searchButton.setVisibility(View.GONE);
                startCameraPreview();
                break;
            case CONFIRMED:
                //  promptChip.setVisibility(View.GONE);
                //  searchButton.setVisibility(View.VISIBLE);
                //  searchButton.setEnabled(true);
                //  searchButton.setBackgroundColor(Color.WHITE);
                //startCameraPreview();
                stopCameraPreview();
                break;
            case SEARCHING:
                //  promptChip.setVisibility(View.GONE);
                //  searchButton.setVisibility(View.VISIBLE);
                //  searchButton.setEnabled(false);
                // searchButton.setBackgroundColor(Color.GRAY);
                // searchProgressBar.setVisibility(View.VISIBLE);
                stopCameraPreview();
                break;
            case SEARCHED:
                // promptChip.setVisibility(View.GONE);
                //  searchButton.setVisibility(View.GONE);
                stopCameraPreview();
                break;
            default:
                //  promptChip.setVisibility(View.GONE);
                //  searchButton.setVisibility(View.GONE);
                break;
        }


    }

    public JSONArray ocrText(FirebaseVisionText firebaseVisionText,String oldLine,String newLine) {
        JSONArray array = new JSONArray();
        try {
            List<FirebaseVisionText.TextBlock> blockText = firebaseVisionText.getTextBlocks();
            for (int i = 0; i < blockText.size(); i++) {
                String text = blockText.get(i).getText();
            if((!newLine.isEmpty())&&(text.contains(oldLine))){
                text =text.replace(oldLine,newLine);
            }
                JSONObject wholeObj = new JSONObject();
                JSONArray boundsArr = new JSONArray();
                Point[] cornerPts = blockText.get(i).getCornerPoints();
                for (int j = 0; j < cornerPts.length; j++) {
                    JSONObject boundsObj = new JSONObject();
                    boundsObj.put("x", cornerPts[j].x);
                    boundsObj.put("y", cornerPts[j].y);
                    boundsArr.put(j, boundsObj);
                }
                wholeObj.put("text", text);
                wholeObj.put("bounds", boundsArr);
                array.put(i, wholeObj);
            }
           // Log.i("previewActivity", "ocrText: " + array.toString());
        } catch (Exception e) {
            //Log.i("previewActivity", "ocrText: Failed ");

        }

        return array;
    }
}
