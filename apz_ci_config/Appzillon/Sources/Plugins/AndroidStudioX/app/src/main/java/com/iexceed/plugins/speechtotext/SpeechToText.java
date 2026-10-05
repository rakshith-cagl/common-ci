package com.iexceed.plugins.speechtotext;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.auditlog.AuditLog;
import com.iexceed.plugins.errorlog.ApzLogger;


public class SpeechToText<supportedLanguages> extends ApzPlugin implements RecognitionListener {

    private static ApzPlugin pluginObj;
    private static String lWords = "";
    private static String appendWords = "";
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String TAG = "VOICE";
    private boolean isRecording = false;
    private JSONObject mParams;
    private String action;
    private String lCode = "";
    private String isPauseRecognize = "";
    private Timer speechTimer = null;
    private List<String> supportedLanguages;

    private SpeechToText(WebView webView, ApzActivity activity) {
        super(webView, activity);
        speechTimer = new Timer(true);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new SpeechToText(webView, activity);
        }
        return pluginObj;
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    public static boolean isSpeechToTextPlugin() {
        return true;
    }

    public void startVoiceConversion(JSONObject voiceJson) {


        try {
            callbackId = voiceJson.getString("id");
            action = voiceJson.getString("action");
            lCode = voiceJson.getString("languageCode");
            isPauseRecognize = voiceJson.optString("pauseRecognize");
            lWords = "";
            appendWords = "";
            if (supportedLanguages != null) {

                if (!supportedLanguages.contains(lCode)) {
                    ApzPluginUtil.sendError(callbackId, "APZ-CNT-331", null,
                            this.activity, this.webView, true);
                } else {
                    if (action.equalsIgnoreCase("stop")) {
                        if (speech != null) {
                            isRecording = false;
                            speech.stopListening();
                            speech.destroy();
                        }
                        if (speechTimer != null) {
                            speechTimer.cancel();
                            speechTimer = null;
                        }
                        successCallback("STOPPED", "");
                    } else if (action.equalsIgnoreCase("start")) {

                        speechRecognizerMethod();
                        successCallbackKeepAlive("STARTED", "");
                       // Log.i(TAG, "startVoiceConversion: " + "   STARTED AFTER CALLBACK");
                        if (isPauseRecognize.equalsIgnoreCase("Y")) {
                            startSpeechTimer();
                        }


                    } else if (action.equalsIgnoreCase("resume")) {
                        speechRecognizerMethod();
                        successCallbackKeepAlive("RESUMED", "");
                       // Log.i(TAG, "startVoiceConversion: " + "   RESUMED AFTER CALLBACK");

                        if (isPauseRecognize.equalsIgnoreCase("Y")) {
                            startSpeechTimer();
                        }
                    }
                }
            }else{
                isRecording = false;
                if (speech != null) {
                    speech.stopListening();

                    speech.destroy();
                }
                ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,
                        this.activity, this.webView, true);
            }

        } catch (Exception e) {
            ApzLogger.e(TAG, e.toString());
            isRecording = false;
            if (speech != null) {
                speech.stopListening();

                speech.destroy();
            }
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,
                    this.activity, this.webView, true);
        }


    }


    public void startSpeechTimer() {
        try {
            if (speechTimer != null) {
                speechTimer.cancel();
                speechTimer = null;
            }
            speechTimer = new Timer(true);
           // Log.i(TAG, "startSpeechTimer: started");
            speechTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (speech != null) {
                        isRecording = false;
                        speech.destroy();
                    }

                    if (!appendWords.equalsIgnoreCase("") && !appendWords.equalsIgnoreCase(lWords)) {
                        appendWords = appendWords + " " + lWords;
                        successCallback("PAUSED", appendWords);
                    } else {

                        successCallback("PAUSED", lWords);
                    }
                  //  Log.i(TAG, "startSpeechTimer: STOPPED  ==== appendword == " + appendWords + "  lword " + lWords);
                }
            }, 2000);
        } catch (Exception e) {
           // Log.e(TAG, "startSpeechTimer: " + e);
        }
    }

    public void speechRecognizerMethod() {
        //speechrecognizer intent
        try {
            speech = SpeechRecognizer.createSpeechRecognizer(activity
                    .getApplicationContext());
            speech.setRecognitionListener(this);

            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    activity.getApplicationContext().getPackageName());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lCode);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_RESULTS, 5);
              recognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);

            if (!isRecording) {
             //   Log.i(TAG, "startVoiceConversion: " + "   startListening");
                if (appendWords.equalsIgnoreCase(""))
                    appendWords = lWords;
                if (!appendWords.equalsIgnoreCase("") && !appendWords.equalsIgnoreCase(lWords)) {
                    appendWords = appendWords + " " + lWords;
                }
                speech.startListening(recognizerIntent);
                isRecording = true;
            } else {
              //  Log.i(TAG, "startVoiceConversion: " + "   stopListening");
                speech.stopListening();
                isRecording = false;
            }
        } catch (Exception ex) {
          //  Log.e(TAG, "speechRecognizerMethod: " + ex);
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-203", null, this.activity,
                    this.webView, true);
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        lWords = "";
        ApzLogger.i(TAG, "onBeginningOfSpeech");
     //   Log.i(TAG, "*****************onBeginningOfSpeech " + appendWords);

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndOfSpeech() {
        ApzLogger.i(TAG, "onEndOfSpeech");
     //   Log.i(TAG, "*****************onEndOfSpeech " + appendWords);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        ApzLogger.i(TAG, "onEvent");

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        try {
          //  Log.i(TAG, "*****************onPartialResults");
            final JSONObject result = new JSONObject();
            final ArrayList<String> matches = partialResults
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
          //  Log.i(TAG, "startVoiceConversion: " + "onPartialResults   VALUE " + matches.get(0));
            if (isPauseRecognize.equalsIgnoreCase("Y")) {
                if (!matches.get(0).equalsIgnoreCase("")) {
                    lWords = matches.get(0);
                    startSpeechTimer();
                }
            } else {
                if (!matches.get(0).equalsIgnoreCase("")) {
                    lWords = matches.get(0);

                }
            }
            ApzLogger.i(TAG, "onPartialResults");
        } catch (Exception ex) {
            if (speech != null) {
                speech.stopListening();
                speech.destroy();
            }
          //  Log.e(TAG, "speechRecognizerMethod: " + ex);
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-203", null, this.activity,
                    this.webView, true);
        }

    }

    @Override
    public void onReadyForSpeech(Bundle params) {
       // Log.i(TAG, "***************** onReadyForSpeech " + appendWords);
        ApzLogger.i(TAG, "onReadyForSpeech");

    }

    @Override
    public void onResults(Bundle results) {
        try {
          //  Log.i(TAG, "***************** onResults");
            final JSONObject result = new JSONObject();
            final ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
           // Log.i(TAG, "startVoiceConversion: " + "onResults   VALUE " + matches.get(0));
            isRecording = false;
            speechRecognizerMethod();
        } catch (Exception ex) {
         //   Log.e(TAG, "onResults: " + ex);
            if (speech != null) {
                speech.stopListening();
                speech.destroy();
            }
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-203", null, this.activity,
                    this.webView, true);
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onError(int error) {
        ApzLogger.i(TAG, "error number " + error);
        try {
            String errorMessage = getErrorText(error);
         //   Log.i(TAG, "***************** onError: " + "   RESTART SPEECHA AFTER ERROR  000000000000  " + errorMessage);
            isRecording = false;
            if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                if (!isPauseRecognize.equalsIgnoreCase("Y")) {
                    if (appendWords.equalsIgnoreCase(""))
                        appendWords = lWords;
                    if (!appendWords.equalsIgnoreCase("") && !appendWords.equalsIgnoreCase(lWords)) {
                        appendWords = appendWords + " " + lWords;
                    }
                    successCallbackKeepAlive("VALUE", appendWords);
                    //Log.i(TAG, "startVoiceConversion: " + "   VALUE AFTER CALLBACK");
                } else {
                    speechRecognizerMethod();
                }


            }  else if (error == SpeechRecognizer.ERROR_SERVER) {
                if (speech != null) {
                    speech.stopListening();
                    speech.destroy();
                }
                if (speechTimer != null) {
                    speechTimer.cancel();
                    speechTimer = null;
                }

                  //  Log.e(TAG, "onError:device is offline");
                    ApzPluginUtil.sendError(callbackId, "APZ-CNT-332", null, this.activity,
                            this.webView, true);

            }else {
                if (speech != null) {
                    speech.stopListening();
                    speech.destroy();
                }
                if (speechTimer != null) {
                    speechTimer.cancel();
                    speechTimer = null;
                }
                ApzPluginUtil.sendError(callbackId, "APZ-CNT-203", null, this.activity,
                        this.webView, true);
            }
        } catch (Exception ex) {
          //  Log.e(TAG, "onError: " + ex);
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-203", null, this.activity,
                    this.webView, true);
        }
    }

    @Override
    public void execute(final JSONObject params) {
        mParams = params;
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestForPermission();
            } else {
                callSpeechToText();
            }
        } else {
            callSpeechToText();
        }


    }

    private void callSpeechToText() {
        /*get supported languages for the voice conversion */
        try {
            getSupportedLanguages();
        }catch (Exception e){
          //  Log.e(TAG, "callSpeechToText: " + e);
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-203", null, this.activity,
                    this.webView, true);
        }
    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, new String[]{
                        Manifest.permission.RECORD_AUDIO}, ApzPlugin.APZ_REQ_RECORD_AUDIO, new OnPermissionsResultHandler() {

                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_RECORD_AUDIO) {
                            boolean denied = false;
                            boolean never_ask_again = false;
                            for (String permission : permissions) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                    denied = true;
                                } else {
                                    if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                                        never_ask_again = true;
                                    }
                                }
                            }
                            if (never_ask_again) {
                                PermissionDeniedCallback();
                            } else if (denied) {
                                displayReconfirmationMessage();
                            } else {
                                callSpeechToText();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }
                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To record audio, grant permission for app to access microphone";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Permission Denied");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        requestForPermission();
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                PermissionDeniedCallback();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void PermissionDeniedCallback() {
        ApzPluginUtil.sendPermissionDenied("Audio record ", callbackId, this.activity, this.webView);
    }

    public void successCallback(final String lAction, final String lText) {
        final JSONObject result = new JSONObject();
        final Handler res = new Handler(Looper.getMainLooper());
        res.postDelayed(new Runnable() {
            @Override
            public void run() {
                AuditLog.sendToJSON();
                try {
                    result.put("text", lText);
                    result.put("action", lAction);
                } catch (JSONException e) {

                }
                ApzPluginUtil.sendSuccess(callbackId, result, false, activity,
                        webView, true);
            }
        }, 200);
    }

    public void successCallbackKeepAlive(final String lAction, final String lText) {
        final JSONObject result = new JSONObject();
        final Handler res = new Handler(Looper.getMainLooper());
        res.postDelayed(new Runnable() {
            @Override
            public void run() {
                AuditLog.sendToJSON();
                try {
                    result.put("text", lText);
                    result.put("action", lAction);
                } catch (JSONException e) {

                }
                ApzPluginUtil.sendSuccess(callbackId, result, true, activity,
                        webView, true);
            }
        }, 200);

    }

    public void getSupportedLanguages() {
        activity.sendOrderedBroadcast(RecognizerIntent.getVoiceDetailsIntent(activity.getApplicationContext()), null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = getResultExtras(true);
                supportedLanguages = extras.getStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
               // Log.i("Voice", "onReceive: " + supportedLanguages.size());
                /*start voice listener*/
                startVoiceConversion(mParams);
            }

        }, null, activity.RESULT_OK, null, null);
    }

}

