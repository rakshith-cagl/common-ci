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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.MainThread;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.iexceed.plugins.autocapturedocument.WorkflowModel.WorkflowState;

import java.io.IOException;
import java.util.List;

import com.iexceed.appzillonapp.R;

import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.deviceTextRecognition;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.documentType;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.failureCallback;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.holdTimeForAutoCapture;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.isManualCapture;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.mScanStatus1;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.mScanStatus2;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.mScanStatus3;
import static com.iexceed.plugins.autocapturedocument.ApzAutoCapturePlugin.timeOutForAutoCapture;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.cardFrame;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.frameRect;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.lActivity;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.manualClick;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.screenHeight;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.screenWidth;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.setTextViewMessage;
import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.setToggleVisibility;

/**
 * A processor to run object detector in prominent object only mode.
 */
public class ProminentObjectProcessor extends FrameProcessorBase<List<FirebaseVisionObject>> {

    private static final String TAG = "ProminentObjProcessor";
    private final FirebaseVisionObjectDetector detector;
    private final WorkflowModel workflowModel;
    private final ObjectConfirmationController confirmationController;
    private boolean initOpencv = false;
    private final int reticleOuterRingRadius;
    public static String[] outputTextRecognised = {""};
    private static boolean isTextDetectionInproress = false;
    private long captureHoldTimeStart;
    private long captureTimeOutStart;



    public ProminentObjectProcessor(GraphicOverlay graphicOverlay, WorkflowModel workflowModel) {
            this.workflowModel = workflowModel;

            confirmationController = new ObjectConfirmationController(graphicOverlay);
            //  cameraReticleAnimator = new CameraReticleAnimator(graphicOverlay);
            reticleOuterRingRadius =
                    graphicOverlay
                            .getResources()
                            .getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_radius);

            FirebaseVisionObjectDetectorOptions.Builder optionsBuilder =
                    new FirebaseVisionObjectDetectorOptions.Builder()
                            .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE);

            this.detector = FirebaseVision.getInstance().getOnDeviceObjectDetector(optionsBuilder.build());

    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
           // Log.e(TAG, "Failed to close object detector!", e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionObject>> detectInImage(FirebaseVisionImage image) {
        //   boolean isChecked=checkVisionText(image);
        return detector.processImage(image);
    }

    @MainThread
    @Override
    protected void onSuccess(
            FirebaseVisionImage image,
            List<FirebaseVisionObject> objects,
            GraphicOverlay graphicOverlay) {
        try {
                if (!workflowModel.isCameraLive()) {
                return;
            }

            //add    graphicOverlay.add(new ObjectReticleGraphic(graphicOverlay, cameraReticleAnimator));
            //add   cameraReticleAnimator.start();
            setTextViewMessage(mScanStatus1);
            final FirebaseVisionImage fireImageManual = image;
            final List<FirebaseVisionObject> fireObjsManual = objects;
            //manual click changes
            if (isManualCapture) {
                if (manualClick) {
                    FirebaseVisionObject manualObject = null;
                    if (objects.isEmpty()) {
                        manualObject = null;
                    } else {
                        manualObject = objects.get(0);
                    }
                    processFrame(image, manualObject);
                    manualClick = false;
                } else {
                    setTextViewMessage(mScanStatus1);
                    confirmationController.reset();
                    workflowModel.setWorkflowState(WorkflowState.DETECTED);
                }
            } else {
                //timeout check
                if(timeOutForAutoCapture!=0) {
                    if (captureTimeOutStart == 0) {
                        captureTimeOutStart = System.nanoTime();
                    } else {
                        long finishTime = System.nanoTime();
                        long timeElapsed = finishTime - captureTimeOutStart;
                        if (timeElapsed / 1000000 >= (timeOutForAutoCapture) * 1000) {
                            failureCallback("Auto capture timed out", "APZ-CNT-340");
                            lActivity.finish();
                        }
                    }
                }
                //auto capture
                if (objects.isEmpty()) {
                    confirmationController.reset();
                    workflowModel.setWorkflowState(WorkflowState.DETECTING);
                } else {

                    int objectIndex = 0;
                    FirebaseVisionObject object = objects.get(objectIndex);
                    if (objectBoxOverlapsConfirmationReticle(graphicOverlay, object)) {
                        if (captureHoldTimeStart == 0) {
                            captureHoldTimeStart = System.nanoTime();
                        } else {
                            setTextViewMessage(mScanStatus3);
                            long finishTime = System.nanoTime();
                            long timeElapsed = finishTime - captureHoldTimeStart;
                            //Log.i(TAG, "onSuccess: " + timeElapsed);
                            if (timeElapsed / 1000000 >= (holdTimeForAutoCapture) * 1000) {
                                setTextViewMessage(mScanStatus1);
                                processFrame(image, object);
                            }
                        }
                    } else {
                        // Object detected but user doesn't want to pick this one.
                        resetObject();
                        //  captureHoldTimeStart = 0;
                    }
                }

            }
        } catch (Exception e) {
            captureHoldTimeStart = 0;
            resetObject();
        }
        graphicOverlay.clear();
        if (!objects.isEmpty()) {
           /* //add graphicOverlay.add(new ObjectReticleGraphic(graphicOverlay, cameraReticleAnimator));
            //add  cameraReticleAnimator.start();
        } else {*/
            if (objectBoxOverlapsConfirmationReticle(graphicOverlay, objects.get(0))) {
                // User is confirming the object selection.
                //add   cameraReticleAnimator.cancel();
                graphicOverlay.add(
                        new ObjectGraphicInProminentMode(
                                graphicOverlay, objects.get(0), confirmationController));
                if (!confirmationController.isConfirmed()) {
                    // Shows a loading indicator to visualize the confirming progress if in auto search mode.
                    //  graphicOverlay.add(new ObjectConfirmationGraphic(graphicOverlay, confirmationController));
                }
            } else {
                // Object is detected but the confirmation reticle is moved off the object box, which
                // indicates user is not trying to pick this object.
                graphicOverlay.add(
                        new ObjectGraphicInProminentMode(
                                graphicOverlay, objects.get(0), confirmationController));
                //add   graphicOverlay.add(new ObjectReticleGraphic(graphicOverlay, cameraReticleAnimator));
                //add  cameraReticleAnimator.start();
            }
        }
        graphicOverlay.invalidate();

    }


    private void processFrame(final FirebaseVisionImage image, final FirebaseVisionObject object) {
        try {
            Bitmap bitmap = getCroppedBitmap(image, "temp");
            Bitmap scaleBmpCard = convertToBlackWhite(bitmap, "grayscale");
            FirebaseVisionImage objectImage = FirebaseVisionImage.fromBitmap(scaleBmpCard);
            if (deviceTextRecognition) {
                if (!confirmationController.isConfirmed()) {
                    if (!isTextDetectionInproress) {
                        setTextViewMessage(mScanStatus1);
                        workflowModel.setFireText(null);
                        isTextDetectionInproress = true;
                        TextDetector tt = new TextDetector(image, objectImage, object, new TextDetector.TextDetectorHandler() {
                            @Override
                            public void onTextDetected(FirebaseVisionText text, FirebaseVisionImage fullImage, FirebaseVisionImage objectImage, FirebaseVisionObject fireObject) {
                                isTextDetectionInproress = false;
                                try {
                                    if (text != null) {
                                        if (TextValidator.validateDetectedText(text.getText(),documentType)) {
                                            workflowModel.setFireText(text);
                                            Bitmap bitmap = getCroppedBitmap(image, "final");
                                            FirebaseVisionImage finalObjectImage = FirebaseVisionImage.fromBitmap(bitmap);
                                            confirmObject(image, object, finalObjectImage);
                                        } else {
                                            resetObject();
                                        }
                                    } else {
                                        resetObject();
                                    }
                                } catch (Exception e) {
                                    failureCallback("Exception occured", "");
                                    lActivity.finish();
                                }
                            }
                        });
                        tt.findText();
                    }

                }
            }
        } catch (Exception e) {
            captureHoldTimeStart = 0;
            resetObject();
        }
    }

    private void resetObject() {
        if (isManualCapture) {
            setToggleVisibility(View.VISIBLE);
        }
        // captureHoldTimeStart = 0;
        confirmationController.reset();
        workflowModel.setWorkflowState(WorkflowState.DETECTED);
    }

    private void confirmObject(FirebaseVisionImage image, FirebaseVisionObject object, FirebaseVisionImage objectImage) {
        setTextViewMessage(mScanStatus2);
        confirmationController.reset();
        confirmationController.setConfirmed();
        workflowModel.confirmingObject(
                new DetectedObject(object, 0, image), confirmationController.getProgress(), objectImage);
    }

    public static Bitmap convertToBlackWhite(Bitmap colorBmp, String mAction) {
        Bitmap bmpMonochrome = Bitmap.createBitmap(colorBmp.getWidth(), colorBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        //set contrast
        ColorMatrix contrastMatrix = new ColorMatrix();
//change contrast
        if (mAction.equalsIgnoreCase("grayscale")) {
            contrastMatrix.setSaturation(0);
        } else if (mAction.equalsIgnoreCase("BW")) {
            contrastMatrix.setSaturation(0);
            contrastMatrix.set(new float[]{
                    128, 128, 128, 0, -(128) * 255,
                    128, 128, 128, 0, -(128) * 255,
                    128, 128, 128, 0, -(128) * 255,
                    0, 0, 0, 1, 0});
        }
//apply contrast
        Paint contrastPaint = new Paint();
        contrastPaint.setColorFilter(new ColorMatrixColorFilter(contrastMatrix));
        canvas.drawBitmap(colorBmp, 0, 0, contrastPaint);
        return bmpMonochrome;
    }

    private Bitmap getCroppedBitmap(FirebaseVisionImage image, String type) {
        Bitmap bitmap=null;
        try {
            float bmpWidth = image.getBitmap().getWidth();
            float bmpHeight = image.getBitmap().getHeight();

           //    Log.i(TAG, "onSuccess: preview" + preWidth + " h " + preHeight);
            int frameL = 0;
            int frameT = 0;
            int frameW = 0;
            int frameH = 0;

            Rect frame = new Rect(cardFrame.getLeft(), cardFrame.getTop(), cardFrame.getRight(), cardFrame.getBottom());
            //   RectF frame = overlayFrame.translateRect(new Rect(cardFrame.getLeft(), cardFrame.getTop(), cardFrame.getRight(), cardFrame.getBottom()));
            float imgSize = bmpWidth * bmpHeight;
            float screenSize = screenWidth * screenHeight;
            float conRatioW = (bmpWidth / screenWidth);
            float conRatioH = (bmpHeight / screenHeight);
            frameL = Math.round(frame.left * conRatioW);
            frameT = Math.round(frame.top * conRatioH);
            frameW = Math.round(frame.width() * conRatioW);
            frameH = Math.round(frame.height() * conRatioH);

            int addWidth = (int) ((frameW * 0.2));
            int addHeight = (int) ((frameH * 0.2));
            if (type.equalsIgnoreCase("final")) {
                frameL = (frameL - (addWidth / 2)) < 0 ? 0 : frameL - (addWidth / 2);
                frameT = (frameT - (addHeight / 2)) < 0 ? 0 : frameT - (addHeight / 2);
                frameW = (frameW + addWidth + frameL) > bmpWidth ? (int) (bmpWidth - frameL) : frameW + addWidth;
                frameH = (frameH + addHeight + frameT) > bmpHeight ? (int) (bmpHeight - frameT) : frameH + addHeight;
            }


            // Log.i(TAG, "onSuccess: bitmap w*h " + bmpWidth + " " + bmpHeight + "  frame w*h " + frame.width() + " " + frame.height() + "  object w*h " + objBounds.width() + " " + objBounds.height());
             bitmap =
                    Bitmap.createBitmap(
                            image.getBitmap(),
                            frameL,
                            frameT,
                            frameW,
                            frameH);

        }catch(Exception ex){

        }
        return bitmap;
    }


    private boolean objectBoxOverlapsConfirmationReticle(
            GraphicOverlay graphicOverlay, FirebaseVisionObject object) {
        RectF boxRect = graphicOverlay.translateRect(object.getBoundingBox());
        frameRect = new RectF(cardFrame.getLeft(), cardFrame.getTop(), cardFrame.getRight(), cardFrame.getBottom());
        float reticleCenterX = graphicOverlay.getWidth() / 2f;
        float reticleCenterY = graphicOverlay.getHeight() / 2f;
        return ((frameRect.intersect(boxRect) || frameRect.contains(boxRect)));

    }

    @Override
    protected void onFailure(Exception e) {
       // Log.e(TAG, "Object detection failed!", e);
    }

}
