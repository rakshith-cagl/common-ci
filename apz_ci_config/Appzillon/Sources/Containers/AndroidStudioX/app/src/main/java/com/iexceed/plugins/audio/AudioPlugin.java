package com.iexceed.plugins.audio;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class AudioPlugin extends ApzPlugin{

	public AudioPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}
	public void releaseMediaRecorder(){}
	public void releaseAudioPlayer() {}
	public final void stopPlaying() {}

	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub
		
	}

	public static boolean isAudioPlugin() {
		// TODO Auto-generated method stub
		return false;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		// TODO Auto-generated method stub
		return null;
	}}