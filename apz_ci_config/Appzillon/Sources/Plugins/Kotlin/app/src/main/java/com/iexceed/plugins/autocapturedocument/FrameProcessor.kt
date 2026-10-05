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
package com.iexceed.plugins.autocapturedocument

import com.google.mlkit.common.MlKitException

/** An interface to process the input camera frame and perform detection on it.  */
interface FrameProcessor {
    /** Processes the input frame with the underlying detector.  */
    @Throws(MlKitException::class)
    open fun process(
        data: java.nio.ByteBuffer?,
        frameMetadata: FrameMetadata?,
        graphicOverlay: GraphicOverlay?
    )
    /** Processes ByteBuffer image data, e.g. used for Camera1 live preview case.  */ /*  void processByteBuffer(
          ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay)
          throws MlKitException;*/
    /** Stops the underlying detector and release resources.  */
    open fun stop()
}