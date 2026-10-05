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

import android.media.ExifInterface;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.nio.ByteBuffer;

import static com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.cardFrame;

/** Abstract base class of {@link FrameProcessor}. */
public abstract class FrameProcessorBase<T> implements FrameProcessor {

  private static final String TAG = "FrameProcessorBase";
  public static int preWidth=0;
  public static int preHeight=0;

  // To keep the latest frame and its metadata.
  @GuardedBy("this")
  private ByteBuffer latestFrame;

  @GuardedBy("this")
  private FrameMetadata latestFrameMetaData;

  // To keep the frame and metadata in process.
  @GuardedBy("this")
  private ByteBuffer processingFrame;

  @GuardedBy("this")
  private FrameMetadata processingFrameMetaData;

  @Override
  public synchronized void process(
          ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
    latestFrame = data;
    latestFrameMetaData = frameMetadata;
    if (processingFrame == null && processingFrameMetaData == null) {
      processLatestFrame(graphicOverlay);
    }
  }

  private synchronized void processLatestFrame(final GraphicOverlay graphicOverlay) {
    processingFrame = latestFrame;
    processingFrameMetaData = latestFrameMetaData;
    latestFrame = null;
    latestFrameMetaData = null;
    if (processingFrame != null && processingFrameMetaData != null) {
      FirebaseVisionImageMetadata metadata =
          new FirebaseVisionImageMetadata.Builder()
              .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
              .setWidth(processingFrameMetaData.width)
              .setHeight(processingFrameMetaData.height)
              .setRotation(processingFrameMetaData.rotation)
              .build();
      preWidth=processingFrameMetaData.width;
      preHeight=processingFrameMetaData.height;
      final FirebaseVisionImage image = FirebaseVisionImage.fromByteBuffer(processingFrame, metadata);

      final long startMs = SystemClock.elapsedRealtime();
      detectInImage(image)
          .addOnSuccessListener(
                  new OnSuccessListener<T>() {
                    @Override
                    public void onSuccess(T results) {
                      Log.d(TAG, "Latency is: " + (SystemClock.elapsedRealtime() - startMs));
                      FrameProcessorBase.this.onSuccess(image, results, graphicOverlay);
                      FrameProcessorBase.this.processLatestFrame(graphicOverlay);
                    }
                  })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
          });
    }
  }

  protected abstract Task<T> detectInImage(FirebaseVisionImage image);

  /** Be called when the detection succeeds. */
  protected abstract void onSuccess(
          FirebaseVisionImage image, T results, GraphicOverlay graphicOverlay);

  protected abstract void onFailure(Exception e);
}
