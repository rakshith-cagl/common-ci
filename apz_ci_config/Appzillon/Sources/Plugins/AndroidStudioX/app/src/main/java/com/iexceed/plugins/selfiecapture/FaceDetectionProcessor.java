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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.iexceed.plugins.selfiecapture.CameraImageGraphic;
import com.iexceed.plugins.selfiecapture.FrameMetadata;
import com.iexceed.plugins.selfiecapture.GraphicOverlay;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.selfiecapture.VisionProcessorBase;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.blinkEyeDetection;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.blinkInstruction;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.faceInstruction1;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.faceInstruction2;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.faceInstruction3;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.faceScanningMsg;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.holdTimeForCapture;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.holdTimeInstruction;
import static com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.setInstructionTextView;
import static com.iexceed.plugins.selfiecapture.LivePreviewActivity.handler;
import static com.iexceed.plugins.selfiecapture.LivePreviewActivity.ovalFrame;

/**
 * Face Detector Demo.
 */
public class FaceDetectionProcessor extends VisionProcessorBase<List<FirebaseVisionFace>> {

    private static final String TAG = "FaceDetectionProcessor";

    private final FirebaseVisionFaceDetector detector;

    private final Bitmap overlayBitmap;
    private boolean isDetected;
    private onFaceDetectionHandler faceHandler;
    private boolean openRightEye = false;
    private boolean openLeftEye = false;
    private boolean closeRightEye = false;
    private boolean closeLeftEye = false;
    private long  startTime;

    public FaceDetectionProcessor(Resources resources) {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);

        overlayBitmap = BitmapFactory.decodeResource(resources, R.drawable.button_blue);
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.add(imageGraphic);
        }

        if (faces.isEmpty()) {
            setFalse();
            //   fInstruction.setText("Position face within the oval frame.");
//            fInstruction.setText(faceInstruction1);
            setInstructionTextView(faceInstruction1);
        } else {

                FirebaseVisionFace face = faces.get(0);
                int cameraFacing =
                        frameMetadata != null ? frameMetadata.getCameraFacing() :
                                Camera.CameraInfo.CAMERA_FACING_BACK;
                FaceGraphic faceGraphic = new FaceGraphic(graphicOverlay, face, cameraFacing, overlayBitmap);
                graphicOverlay.add(faceGraphic);
                if (!isDetected) {
                    isDetected = false;
                    if (ovalContainsFace(face, graphicOverlay)) {
                        if (compareArea(face, graphicOverlay, "greaterThan0.5")) {
                            Log.i(TAG, "Blink: Eye detection AFTER check " + "right eye open +" + openRightEye + "\n" + "left eye open +" + openLeftEye + "\n" + "left eye close +" + closeLeftEye + "\n" + "right eye close +" + closeRightEye);
                            if (blinkEyeDetection.equalsIgnoreCase("Y")) {
                                if (!(openLeftEye && openRightEye && closeLeftEye && closeRightEye)) {
                                    startTime=0;
//                                    fInstruction.setText(blinkInstruction);
                                    setInstructionTextView(blinkInstruction);
                                    checkEyeProbability(face);
                                }
                                else {
                                    if(startTime==0) {
                                        startTime = System.nanoTime();
                                    }else {
//                                        fInstruction.setText("Hold steady");
                                        setInstructionTextView(holdTimeInstruction);
                                        long finishTime = System.nanoTime();
                                        long timeElapsed = finishTime - startTime;
                                        Log.i(TAG, "onSuccess: " + timeElapsed);
                                        if (timeElapsed / 1000000 >= (holdTimeForCapture)*1000) {
//                                            fInstruction.setText(faceScanningMsg);
                                            setInstructionTextView(faceScanningMsg);
                                            //    if (face.getRightEyeOpenProbability() > 0.7 && face.getLeftEyeOpenProbability() > 0.7) {
                                            isDetected = true;
                                            handler.onFaceDetected(face, originalCameraImage);
                                        }
                                    }
                                  //  }
                                }
                            } else {
                                if(startTime==0) {
                                    startTime = System.nanoTime();
                                }else {
//                                        fInstruction.setText("Hold steady");
                                    setInstructionTextView(holdTimeInstruction);
                                    long finishTime = System.nanoTime();
                                    long timeElapsed = finishTime - startTime;
                                    Log.i(TAG, "onSuccess: " + timeElapsed);
                                    if (timeElapsed / 1000000 >= (holdTimeForCapture)*1000) {
//                                            fInstruction.setText(faceScanningMsg);
                                        setInstructionTextView(faceScanningMsg);
                                        //    if (face.getRightEyeOpenProbability() > 0.7 && face.getLeftEyeOpenProbability() > 0.7) {
                                        isDetected = true;
                                        handler.onFaceDetected(face, originalCameraImage);
                                    }
                                }
//                                fInstruction.setText(faceScanningMsg);
                            /*    setInstructionTextView(faceScanningMsg);
                                isDetected = true;
                                handler.onFaceDetected(face, originalCameraImage);*/
                            }
                        } else {
                            startTime=0;
                            setFalse();
                            //  fInstruction.setText("Face is too far.Move closer.");
//                            fInstruction.setText(faceInstruction2);
                            setInstructionTextView(faceInstruction2);
                        }
                    } else {
                        startTime=0;
                        setFalse();
                        if (compareArea(face, graphicOverlay, "greater")) {
                            //  fInstruction.setText("Face is too close.Move far.");
                         //   fInstruction.setText(faceInstruction3);
                            setInstructionTextView(faceInstruction3);
                        } else {
                            // fInstruction.setText("Position face within the oval frame.");
                           // fInstruction.setText(faceInstruction1);
                            setInstructionTextView(faceInstruction1);
                        }
                    }
                }

        }
        graphicOverlay.postInvalidate();
    }

    public void setFalse() {
        openRightEye = false;
        openLeftEye = false;
        closeRightEye = false;
        closeLeftEye = false;
    }

    public void checkEyeProbability(FirebaseVisionFace face) {
        if (face.getLeftEyeOpenProbability() > 0.7) {
            openLeftEye = true;
        }
        if (face.getLeftEyeOpenProbability() < 0.3) {
            closeLeftEye = true;
        }
        if (face.getRightEyeOpenProbability() > 0.7) {
            openRightEye = true;
        }
        if (face.getRightEyeOpenProbability() < 0.3) {
            closeRightEye = true;
        }

    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed " + e);
    }

    public interface onFaceDetectionHandler {
        void onFaceDetected(FirebaseVisionFace face, Bitmap firebaseImgBmp);
    }

    public boolean ovalContainsFace(FirebaseVisionFace face, GraphicOverlay graphicOverlay) {
        float x = graphicOverlay.translateX(face.getBoundingBox().centerX());
        float y = graphicOverlay.translateY(face.getBoundingBox().centerY());
        RectF ovalRect = new RectF(ovalFrame.getLeft(), ovalFrame.getTop(), ovalFrame.getRight(), ovalFrame.getBottom());
        float xOffset = graphicOverlay.scaleX(face.getBoundingBox().width() / 2.0f);
        float yOffset = graphicOverlay.scaleY(face.getBoundingBox().height() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        RectF rect = new RectF(left, top, right, bottom);
        return (ovalRect.contains(rect));
    }

    public boolean compareArea(FirebaseVisionFace face, GraphicOverlay graphicOverlay, String checker) {
        boolean bool = false;
        float x = graphicOverlay.translateX(face.getBoundingBox().centerX());
        float y = graphicOverlay.translateY(face.getBoundingBox().centerY());
        RectF ovalRect = new RectF(ovalFrame.getLeft(), ovalFrame.getTop(), ovalFrame.getRight(), ovalFrame.getBottom());
        float xOffset = graphicOverlay.scaleX(face.getBoundingBox().width() / 2.0f);
        float yOffset = graphicOverlay.scaleY(face.getBoundingBox().height() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        RectF rect = new RectF(left, top, right, bottom);
        float ovalArea = (ovalRect.width() * ovalRect.height());
        float boxArea = (rect.width() * rect.height());
        if (checker.equalsIgnoreCase("greater")) {
            bool = boxArea > (ovalArea);
        } else if (checker.equalsIgnoreCase("greaterThan0.5")) {
            bool = (boxArea >= (ovalArea * 0.5));
        }
        return bool;
    }
}
