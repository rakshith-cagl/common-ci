package com.iexceed.plugins.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.MediaUtils;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;
import android.os.ParcelFileDescriptor;

public class AudioPlugin extends ApzPlugin {

    private static   ParcelFileDescriptor externalFile;

    private String isAndroidQ = "";

    private static String EXTERNAL_AUDIO_DIRECTORY = "";

    private MediaRecorder mRecorder;

    private MediaPlayer mPlayer;

    private static String mAction;

    private boolean isPaused;

    private String mAudioFileLocation;

    private File mAudiofile;

    private final static String LOG_TAG = "AUDIO";

    private static String mBase64 = "";
    private static String mSampleRate = "";
    private static String mBitRate = "";
    private static String mChannel = "";
    private static String mDuration = "";


    private static ApzPlugin pluginObj;

    private String mCallbackId;

    private String mFileName;
    private String wavFileFormat;
    private Timer audioTimer = null;

    private JSONObject mJsonObject;

    private Activity mActivity;

    private WebView mWebview;

    private String[] permissions;
    private static int RECORDER_BPP = 16;
    private static String AUDIO_RECORDER_FOLDER = "Audio";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static int RECORDER_SAMPLERATE = 0;
    private static int RECORDER_CHANNELS = 0;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    short[] audioData;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;


    private static String output;

    private AudioPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
        mWebview = webView;
        mActivity = activity;
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new AudioPlugin(webView, activity);
        }
        return pluginObj;
    }

    public void audioPlugin(JSONObject jsonObj) {

        String fileName = "";
        try {
            mCallbackId = jsonObj.getString("id");
            mAction = jsonObj.getString("action");
            mBase64 = jsonObj.optString("base64");
            mSampleRate = jsonObj.optString("samplingRate");
            mBitRate = jsonObj.optString("bitRate");
            mChannel = jsonObj.optString("channel");
            mDuration = jsonObj.optString("timeDuration");
            mFileName = jsonObj.getString("fileName");
            wavFileFormat = jsonObj.getString("wavFileFormat");
            mJsonObject = jsonObj;
            mAudioFileLocation = mJsonObject.getString("location");
            if (mSampleRate.equalsIgnoreCase(""))
                mSampleRate = "16000";
            if (mBitRate.equalsIgnoreCase(""))
                mBitRate = "16";
            if (mChannel.equalsIgnoreCase(""))
                mChannel = "mono";

            EXTERNAL_AUDIO_DIRECTORY =mActivity.getExternalFilesDir(null).getAbsolutePath();
            EXTERNAL_AUDIO_DIRECTORY=EXTERNAL_AUDIO_DIRECTORY.substring(0,EXTERNAL_AUDIO_DIRECTORY.lastIndexOf("Android"));
            EXTERNAL_AUDIO_DIRECTORY+= Environment.DIRECTORY_MUSIC+"/";
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                if(("N".equalsIgnoreCase(activity.getResources().getString(R.string.INTERNALSANDBOX))
                        || mAudioFileLocation.equalsIgnoreCase("external"))){
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ) {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestForPermission();
                        } else {
                            startAudioRecording();
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
                                != PackageManager.PERMISSION_GRANTED) {
                            permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_AUDIO};
                            requestForPermission();
                        } else {
                            startAudioRecording();
                        }
                    }
                } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions = new String[]{
                            Manifest.permission.RECORD_AUDIO};
                    requestForPermission();
                } else {
                    startAudioRecording();
                }

            } else {
                startAudioRecording();
            }
        } catch (final Exception e) {
            ApzLogger.e(LOG_TAG, e.toString());
            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null, this.activity, this.webView, true);
            return;
        }


    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_RECORD_AUDIO, new OnPermissionsResultHandler() {
                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_RECORD_AUDIO) {
                            boolean denied = false;
                            boolean never_ask_again = false;
                            for (String permission : permissions) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                    denied = true;
                                } else {
                                    if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                                        //callCamera();
                                    } else {
                                        never_ask_again = true;
                                    }
                                }
                            }
                            if (never_ask_again) {
                                PermissionDeniedCallback();
                            } else if (denied) {
                                displayReconfirmationMessage();
                            } else {
                                startAudioRecording();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }

                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To record audio,allow app to access by granting permissions";
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
        ApzPluginUtil.sendPermissionDenied("Audio", mCallbackId, this.activity, this.webView);
    }

    private void startAudioRecording() {
        try {
            if (mAction.equals("record")) {
                if (wavFileFormat.equalsIgnoreCase("N")) {
                    try {
                        isPaused = false;
                        mAudioFileLocation = mJsonObject.getString("location");
                        if (mFileName != null && mFileName.length() > 0 && mAudioFileLocation != null && mAudioFileLocation.length() > 0) {

                        } else {
                            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-031", null, this.activity, this.webView, true);
                            pluginObj = null;
                            return;
                        }
                    } catch (Exception e) {
                        ApzLogger.e(LOG_TAG, e.toString());
                        ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-031", null, this.activity, this.webView, true);
                        return;
                    }
                    startRecording(mFileName);
                } else {
                    startWavRecording();
                    //wav
                }
            } else if (mAction.equals("save")) {// for stop also playing also

                if (AppzillonMainScreen.playingState) {
                    releaseAudioPlayer();
                    JSONObject result = new JSONObject();
                    try {
                        result.put("event", "Paused");
                    } catch (Exception e) {
                    }
                    ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);
                    isPaused = false;

                } else {
                    if (wavFileFormat.equalsIgnoreCase("N")) {
                        stopRecording();
                    } else {
                        stopWavRecording(mFileName, mBase64);
                        //wav
                    }
                }

            } else if (mAction.equals("play")) {
                try {
                    mAudioFileLocation = mJsonObject.getString("location");
                } catch (Exception e) {
                    ApzLogger.e(LOG_TAG, e.toString());
                    ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-030", null, this.activity, this.webView, true);
                    return;
                }
                if (AppzillonMainScreen.recordingState) {
                    ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-029", null, this.activity, this.webView, true);//Save then Play
                    //audioFailure(mActivity.getResources().getString(R.string.audio_save_then_play));
                    return;
                }

                if (!isPaused)
                    startPlaying(mFileName);
                else
                    resumePlay();
            } else if (mAction.equals("pause")) {
                audioPause();
                //else
                //wav error
            } else {
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-082", null, this.activity, this.webView, true);//Invalid Action

            }
        } catch (Exception e) {
            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-082", null, this.activity, this.webView, true);
        }
    }

    private void resumePlay() {
        ApzLogger.i(LOG_TAG, "Resume Play");
        try {
            if (mPlayer != null) {
                mPlayer.start();
                isPaused = false;
                JSONObject result = new JSONObject();
                try {
                    result.put("event", "Playing");
                } catch (Exception e) {
                    // TODO Auto-generated catch block

                }
                ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);
            }
        } catch (IllegalStateException e) {
            ApzLogger.i(LOG_TAG, "Resume Failed");

            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-093", null, this.activity, this.webView, true);//Resume Failed
        }
    }

    private void audioPause() {
        ApzLogger.i(LOG_TAG, "Audio Pause");
        if (AppzillonMainScreen.playingState) {
            mPlayer.pause();
            isPaused = true;
            JSONObject result = new JSONObject();
            try {
                result.put("event", "Paused");
            } catch (Exception e) {
                // TODO Auto-generated catch block

            }
            ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);

        } else if (AppzillonMainScreen.recordingState) {
            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-029", null, this.activity, this.webView, true);//Save then play

        } else {
            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-033", null, this.activity, this.webView, true);//Media Player not running
        }
    }

    private final void startRecording(String fileName) {
        ApzLogger.i(LOG_TAG, "Start recording");
        // Log.i(LOG_TAG, "Start recording " + fileName);
        try {
            if (MediaUtils.isSDCardPresent()) {

                if (mAudioFileLocation.equalsIgnoreCase("external")) {
                    ContentValues values = new ContentValues(4);
                    values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (System.currentTimeMillis() / 1000));
                    values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
                        values.put(MediaStore.Audio.Media.DISPLAY_NAME,
                                fileName + ".mp3");
                        Uri audiouri = mActivity.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                        externalFile = mActivity.getContentResolver().openFileDescriptor(audiouri, "w");
                        isAndroidQ ="Y";
                    } else {
                        File sampleDir =new File(EXTERNAL_AUDIO_DIRECTORY);
                        sampleDir.mkdir();
                        mFileName = sampleDir.getAbsolutePath()+"/" + fileName + ".mp3";
                        isAndroidQ="N";
                    }
//          ApzLogger.d(LOG_TAG,"FILE PATH EXTERNAL rec"+mFileName);
                } else if (mAudioFileLocation.equalsIgnoreCase("default")) {
                    mFileName = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "audio";
//                    final File directory = new File(mFileName);
                    final File directory = AppzillonUtils.getApzFile(mFileName,null);
                    directory.mkdirs();
                    mFileName += "/" + fileName + ".mp3";
//				ApzLogger.d(LOG_TAG,"FILE PATH Audio"+ mFileName);
                } else {
                    //if location other than external or dafault
                    ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-278", null, this.activity, this.webView, true);//Location value can be external or default

                    return;
                }

            } else {
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-127", null, this.activity, this.webView, true);//Mount SD card
                return;
            }
            /**/
            try {
                if (AppzillonMainScreen.playingState) {
                    ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-029", null, this.activity, this.webView, true);//stop and then record
                    return;
                }
                if (!AppzillonMainScreen.recordingState) {
                    int sampling = Integer.parseInt(mSampleRate);
                    mRecorder = new MediaRecorder();
                    mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                        @Override
                        public void onInfo(MediaRecorder mr, int what, int extra) {
                            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                                //  Log.i(TAG, "onInfo: ++++++++++ media reached max duration");
                                stopRecording();
                            }

                        }
                    });
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    if (!mDuration.equalsIgnoreCase("")) {
                        mRecorder.setMaxDuration(Integer.parseInt(mDuration));
                    }
                    if(isAndroidQ.equalsIgnoreCase("Y")){
                        mRecorder.setOutputFile(externalFile.getFileDescriptor());
                    }else {
//    mRecorder.setOutputFile(mFileName);
                        mRecorder.setOutputFile(AppzillonUtils.validatePath(mFileName,null));
                    }
                    mRecorder.setAudioEncodingBitRate(Integer.parseInt(mBitRate));
                    if (mChannel.equalsIgnoreCase("mono")) {
                        mRecorder.setAudioChannels(1);
                    } else if (mChannel.equalsIgnoreCase("stereo")) {
                        mRecorder.setAudioChannels(2);
                    }

                    if (sampling == 8000) {
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mRecorder.setAudioSamplingRate(sampling);
                    } else if (sampling == 16000) {
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                        mRecorder.setAudioSamplingRate(sampling);
                    } else if (((sampling > 8000) && (sampling < 96000))) {
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mRecorder.setAudioSamplingRate(sampling);
                    } else {
                        mRecorder.setAudioSamplingRate(16000);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    }

                    mRecorder.prepare();
                    mRecorder.start();
                    AppzillonMainScreen.recordingState = true;
                    JSONObject result = new JSONObject();
                    try {
                        result.put("event", "Audio Recording Started");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block

                    }
                    ApzPluginUtil.sendSuccess(mCallbackId, result, true, this.activity, this.webView, true);

                    ApzLogger.i(LOG_TAG, "Audio Recording Started");
                } else {
                    ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-029", null, this.activity, this.webView, true);//Still Recording
                    return;
                }
            } catch (IOException e) {
                ApzLogger.e(LOG_TAG, "prepare() failed" + e.toString());
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-127", null, this.activity, this.webView, true);//sdard Unavailable
                return;
            } catch (Exception e) {
                //time duration and sampling rate mismatch causes Exception (check value)
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-095", null, this.activity, this.webView, true);
                ApzLogger.e(LOG_TAG, "AudioPlugin Exception: " + e.toString());
            }
        } catch (Exception e) {
            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-095", null, this.activity, this.webView, true);
            ApzLogger.e(LOG_TAG, "AudioPlugin Exception: " + e.toString());
        }
    }

    public final void stopRecording() {
        ApzLogger.i(LOG_TAG, "Stop Recording");
        // Log.i(LOG_TAG, "Stop Recording ");
        try {
            if (mRecorder != null) {
                releaseMediaRecorder();
                processAudioFile();
                if (mBase64.equalsIgnoreCase("Y")) {
                    final JSONObject jsonObj = new JSONObject();
                    JSONObject result = new JSONObject();
                    try {
                        result.put("text", getBase64String(mFileName));
                        result.put("event", "Audio Stopped and saved");
                        ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);

                    } catch (NotFoundException nfe) {
                        ApzLogger.e(LOG_TAG, nfe.getMessage());
                    } catch (Exception e) {
                    }
                } else {
                    JSONObject result = new JSONObject();
                    try {
                        result.put("event", "Audio Stopped and saved");
                    } catch (Exception ex) {
                        ex.getMessage();
                    }
                    ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);
                }
                return;
            }

        } catch (IllegalStateException i) {

            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-095", null, this.activity, this.webView, true);//Save Failed
        }
    }

    private String getBase64String(String selectedPath) {
        String base64Str = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(selectedPath));

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            fis.close();
            byte[] videoBytes = baos.toByteArray();
            base64Str = Base64.encodeToString(videoBytes, Base64.NO_WRAP);

        } catch (Exception e) {

        }
        return base64Str;
    }

    /**
     * Process the recorded audio
     */
    private void processAudioFile() {
        ApzLogger.i(LOG_TAG, "Process Audio");
        File audiofile = new File(mFileName);
        Uri newUri = null;
        if (isAndroidQ.equalsIgnoreCase("N")) {
            ContentValues values = new ContentValues(3);
            long current = System.currentTimeMillis();
            values.put(MediaStore.Audio.Media.TITLE, audiofile.getName());
            values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
            ContentResolver contentResolver = activity.getContentResolver();
            Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            newUri = contentResolver.insert(base, values);
            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        }
    }

    public void releaseMediaRecorder() throws IllegalStateException {
        ApzLogger.i(LOG_TAG, "Release Media Recorder");
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        AppzillonMainScreen.recordingState = false;
    }

    public final void startPlaying(String fileName) {
        try {
            ApzLogger.i(LOG_TAG, "Start Playing");
            if (AppzillonMainScreen.playingState) {
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-029", null, this.activity, this.webView, true);//Audio Play on
                return;
            }
            if (MediaUtils.isSDCardPresent()) {
                if (mAudioFileLocation.equalsIgnoreCase("external")) {
                    File sampleDir;
                    try {
                        if (wavFileFormat.equalsIgnoreCase("Y")) {
                            String filepath =EXTERNAL_AUDIO_DIRECTORY;
                            sampleDir = new File(filepath, AUDIO_RECORDER_FOLDER);
                            mAudiofile = new File(sampleDir, fileName + ".wav");
                        } else {
                            sampleDir = new File (EXTERNAL_AUDIO_DIRECTORY);
                            mAudiofile = new File(sampleDir, fileName + ".mp3");
                        }

                    } catch (NullPointerException e) {
                        ApzLogger.e(LOG_TAG, "sdcard access error");
                        return;
                    }
                    mFileName = mAudiofile.getAbsolutePath();
                    ApzLogger.d(LOG_TAG, "FILE PATH EXTERNAL Play" + mFileName);
                } else if (mAudioFileLocation.equalsIgnoreCase("default")) {
                    mFileName = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "audio";
//                    final File directory = new File(mFileName);
                    final File directory = AppzillonUtils.getApzFile(mFileName,null);
                    directory.mkdirs();
                    if (wavFileFormat.equalsIgnoreCase("Y"))
                        mFileName += "/" + fileName + ".wav";
                    else
                        mFileName += "/" + fileName + ".mp3";
//				ApzLogger.d("FILE PATH Play", mFileName);

                } else {
                    ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-278", null, this.activity, this.webView, true);//Location value can be external or default
                    return;
                }
            } else {
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-127", null, this.activity, this.webView, true);//SD Card unavailable
                return;
            }
            /* create mediaplayer when it is null */
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            }

            try {
//			ApzLogger.d(LOG_TAG, "Playing Recorded File" + mFileName);
//                final FileInputStream fileInputStream = new FileInputStream(mFileName);
                final FileInputStream fileInputStream = new FileInputStream(AppzillonUtils.validatePath(mFileName,null));
                mPlayer.setDataSource(fileInputStream.getFD());
                mPlayer.prepare();
                mPlayer.start();
                AppzillonMainScreen.playingState = true;
                mPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        JSONObject result = new JSONObject();
                        try {
                            result.put("event", "Play completed");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block

                        }
                        ApzPluginUtil.sendSuccess(mCallbackId, result, false, activity, webView, true);

                        //audioSuccess(mActivity.getResources().getString(R.string.audio_play_completed));
                        isPaused = false;
                        releaseAudioPlayer();
                    }
                });
                JSONObject result = new JSONObject();
                try {
                    result.put("event", "Playing");
                } catch (Exception e) {
                    // TODO Auto-generated catch block

                }
                ApzPluginUtil.sendSuccess(mCallbackId, result, true, this.activity, this.webView, true);
                ApzLogger.d(LOG_TAG, "Playing Recorded File");
            } catch (FileNotFoundException e) {
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-002", null, this.activity, this.webView, true);//File not found
                ApzLogger.e(LOG_TAG, "prepare() failed " + e.getMessage());
                releaseAudioPlayer();

                return;
            } catch (IOException e) {
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-127", null, this.activity, this.webView, true);//SD card unavailable
                ApzLogger.e(LOG_TAG, "prepare() failed " + e.getMessage());
                releaseAudioPlayer();
            }
        } catch (Exception e) {
            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-127", null, this.activity, this.webView, true);//SD card unavailable
            ApzLogger.e(LOG_TAG, "prepare() failed " + e.getMessage());
            releaseAudioPlayer();
        }

    }

    public void releaseAudioPlayer() throws IllegalStateException {
        ApzLogger.i(LOG_TAG, "releaseAudioPlayer");
        if (mPlayer != null) {
            AppzillonMainScreen.playingState = false;
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public final void stopPlaying() {
        ApzLogger.i(LOG_TAG, "stopPlaying");
        if (mPlayer != null) {
            releaseAudioPlayer();
            JSONObject result = new JSONObject();
            try {
                result.put("event", "Stopped");
            } catch (Exception e) {
            }
            ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, this.webView, true);
        }
    }

    public static boolean isAudioPlugin() {
        return true;
    }

    @Override
    public void execute(JSONObject params) {
        audioPlugin(params);
    }


///////////////////////////////////////////////////wav format classssssss//////////////

    private String getFilename() {
        try {
            File file = null;
            String filepath = "";
            if (mAudioFileLocation.equalsIgnoreCase("external")) {
                filepath = EXTERNAL_AUDIO_DIRECTORY;
                file = new File(filepath, AUDIO_RECORDER_FOLDER);
//				ApzLogger.d(LOG_TAG,"FILE PATH EXTERNAL rec"+mFileName);
            } else if (mAudioFileLocation.equalsIgnoreCase("default")) {
                filepath = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "audio";
                file = new File(filepath);
                file.mkdirs();
//				ApzLogger.d(LOG_TAG,"FILE PATH Audio"+ mFileName);
            } else {
                //if location other than external or dafault
                ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-278", null, this.activity, this.webView, true);//Location value can be external or default

            }


            if (!file.exists()) {
                file.mkdirs();
            }

            File tempFile = new File(filepath, output);

            if (tempFile.exists()) {
                try {
//                    new java.io.FileWriter(tempFile.getAbsolutePath(), false).close();
                    new java.io.FileWriter(AppzillonUtils.validatePath(tempFile.getAbsolutePath(),null), false).close();
                    tempFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return (file.getAbsolutePath() + "/" + output);
        } catch (Exception e) {
            return "";
        }
//        return (output);
    }

    private String getTempFilename() {
        String filepath = mActivity.getExternalFilesDir("").getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            try {
                new java.io.FileWriter(tempFile.getAbsolutePath(), false).close();
                tempFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }


    public void startWavRecording() {
        try {
            RECORDER_SAMPLERATE = Integer.parseInt(mSampleRate);
            if (mChannel.equalsIgnoreCase("mono")) {
                RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
            } else if (mChannel.equalsIgnoreCase("stereo")) {
                RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
            }
            RECORDER_BPP = Integer.parseInt(mBitRate);
            bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING) * 3;

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, bufferSize);

            int i = recorder.getState();
            if (i == 1) {
                if (!mDuration.equalsIgnoreCase(""))
                    startWavTimer(Integer.parseInt(mDuration));
                recorder.startRecording();
            }

            isRecording = true;
            JSONObject result = new JSONObject();
            try {
                result.put("event", "Audio Recording Started");
            } catch (Exception e) {
                // TODO Auto-generated catch block

            }
            ApzPluginUtil.sendSuccess(mCallbackId, result, true, this.activity, this.webView, true);

            ApzLogger.i(LOG_TAG, "Audio Recording Started");


            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    writeAudioDataToFile();
                }
            }, "AudioRecorder Thread");

            recordingThread.start();
        } catch (Exception e) {

            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-029", null, this.activity, this.webView, true);//Still Recording

        }
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read = 0;
        if (null != os) {
            while (isRecording) {
                read = recorder.read(data, 0, bufferSize);
                if (read > 0) {
                }

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startWavTimer(int duration) {
        try {
            if (audioTimer != null) {
                audioTimer.cancel();
                audioTimer = null;
            }
            audioTimer = new Timer(true);
            //   Log.i(TAG, "audioTimer: started");
            audioTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "audioTimer: stopped");
                    stopWavRecording(mFileName, mBase64);

                }
            }, duration);
        } catch (Exception e) {
            // Log.e(TAG, "startWavTimer: " + e);
        }
    }

    public void stopWavRecording(String fileName, String isBase64) {
        try {
            output = fileName + ".wav";
            if (null != recorder) {
                isRecording = false;

                int i = recorder.getState();
                if (i == 1)
                    recorder.stop();
                recorder.release();

                recorder = null;
                recordingThread = null;
            }
            AppzillonMainScreen.recordingState = false;
            copyWaveFile(getTempFilename(), getFilename());
            deleteTempFile();
            if (isBase64.equalsIgnoreCase("Y")) {
                final JSONObject jsonObj = new JSONObject();
                JSONObject result = new JSONObject();
                try {
                    result.put("text", getBase64String(getFilename()));
                    result.put("event", "Audio Stopped and saved");
                    ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, webView, true);

                } catch (Resources.NotFoundException nfe) {
                    // Log.e(TAG, "stopWavRecording: " + nfe);
                } catch (Exception e) {
                    //  Log.e(TAG, "stopWavRecording: " + e);
                }
            } else {
                JSONObject result = new JSONObject();
                try {
                    result.put("event", "Audio Stopped and saved");
                } catch (Exception ex) {
                    ex.getMessage();
                }
                ApzPluginUtil.sendSuccess(mCallbackId, result, false, this.activity, webView, true);
            }
        } catch (Exception e) {
            // Log.e(TAG, "stopWavRecording: " + e);
        }
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());
        try {
            new java.io.FileWriter(file.getAbsolutePath(), false).close();
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = ((RECORDER_CHANNELS == AudioFormat.CHANNEL_IN_MONO) ? 1 : 2);
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);

            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (((RECORDER_CHANNELS == AudioFormat.CHANNEL_IN_MONO) ? 1 : 2) * 16 / 8); // block align
        header[33] = 0;
        header[34] = (byte) RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
}
