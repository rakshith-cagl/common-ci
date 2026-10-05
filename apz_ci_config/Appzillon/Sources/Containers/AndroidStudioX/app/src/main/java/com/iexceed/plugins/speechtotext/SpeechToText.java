package com.iexceed.plugins.speechtotext;

import org.json.JSONObject;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class SpeechToText extends ApzPlugin implements RecognitionListener {

	public SpeechToText(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRmsChanged(float rmsdB) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResults(Bundle results) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub

	}

	public static boolean isSpeechToTextPlugin() {
		// TODO Auto-generated method stub
		return false;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		// TODO Auto-generated method stub
		return null;
	}}
