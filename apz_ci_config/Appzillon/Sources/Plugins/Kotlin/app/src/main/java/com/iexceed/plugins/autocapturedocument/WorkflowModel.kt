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

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.MainThread
import androidx.core.util.Preconditions
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text

//import static com.google.common.base.Preconditions.checkNotNull;
/** View model for handling application workflow based on camera preview.  */
class WorkflowModel constructor(application: Application?) : AndroidViewModel(application!!) {
    /**
     * State set of the application workflow.
     */
    enum class WorkflowState {
        NOT_STARTED, DETECTING, DETECTED, CONFIRMING, CONFIRMED, SEARCHING, SEARCHED
    }

    @JvmField
    val workflowState: MutableLiveData<WorkflowState> = MutableLiveData()
    @JvmField
    val objectToSearch: MutableLiveData<DetectionObject> = MutableLiveData()

    //  public final MutableLiveData<SearchedObject> searchedObject = new MutableLiveData<>();
    // public final MutableLiveData<FirebaseVisionBarcode> detectedBarcode = new MutableLiveData<>();
    private var fireText: Text? = null
    private var tessractText: String? = null
    private var fireImage: InputImage? = null
    private var matchedPercent: Float = 0f
    private val objectIdsToSearch: kotlin.collections.MutableSet<Int> = HashSet<Int>()
    private var isCameraLive: Boolean = false
    private var confirmedObject: DetectionObject? = null
    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        if ((workflowState != WorkflowState.CONFIRMED
                    && workflowState != WorkflowState.SEARCHING
                    && workflowState != WorkflowState.SEARCHED)
        ) {
            confirmedObject = null
        }
        this.workflowState.value = workflowState
    }

    @MainThread
    fun confirmingObject(`object`: DetectionObject, progress: Float, frameImage: InputImage?) {
        var isConfirmed: Boolean = false
        //    if(ApzAutoCapturePlugin.isTextDetection.equalsIgnoreCase("Y"))
//      isConfirmed = ProminentObjectProcessor.confirmText();
//    else
        isConfirmed = (java.lang.Float.compare(progress, 1f) == 0)
        if (isConfirmed) {
            confirmedObject = `object`
            /*    if (PreferenceUtils.isAutoSearchEnabled(getContext())) {
        setWorkflowState(WorkflowState.SEARCHING);
        triggerSearch(object);
      } else {*/setWorkflowState(WorkflowState.CONFIRMED)
            setFrameImage(frameImage)
            objectToSearch.setValue(`object`)
        } else {
            setWorkflowState(WorkflowState.CONFIRMING)
        }
    }

    @MainThread
    fun onSearchButtonClicked() {
        if (confirmedObject == null) {
            return
        }
        setWorkflowState(WorkflowState.SEARCHING)
        triggerSearch(confirmedObject!!)
    }

    @SuppressLint("RestrictedApi")
    private fun triggerSearch(`object`: DetectionObject) {
        val objectId: Int = Preconditions.checkNotNull(`object`.objectId)
        if (objectIdsToSearch.contains(objectId)) {
            // Already in searching.
            return
        }
        objectIdsToSearch.add(objectId)
        objectToSearch.setValue(`object`)
    }

    fun markCameraLive() {
        isCameraLive = true
        objectIdsToSearch.clear()
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }

    fun isCameraLive(): Boolean {
        return isCameraLive
    }

    fun getFireText(): Text? {
        return fireText
    }

    fun getTesseractText(): String? {
        return tessractText
    }

    fun getFireImage(): InputImage? {
        return fireImage
    }

    fun setFireText(fireText: Text?) {
        this.fireText = fireText
    }

    fun setMatchedPercent(matchedPercent: Float) {
        this.matchedPercent = matchedPercent
    }

    fun getMatchedPercent(): Float {
        return matchedPercent
    }

    fun setTessText(tesseractText: String?) {
        tessractText = tesseractText
    }

    fun setFrameImage(fireImage: InputImage?) {
        this.fireImage = fireImage
    }
}