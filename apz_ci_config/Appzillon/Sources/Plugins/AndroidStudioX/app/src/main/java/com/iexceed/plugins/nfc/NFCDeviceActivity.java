package com.iexceed.plugins.nfc;

import java.nio.charset.Charset;
import java.util.Locale;

import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord; 		
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

public class NFCDeviceActivity extends Activity {

	private static final int MESSAGE_SENT = 1;
	
	private NfcAdapter mNfcAdapter = null;
	
	private String payload = "";
	
	byte statusByte;
	
	public static String NFC_RESULT;
	
	private String nfctype;
	
	private String nfccon;
	
	private String nfccontent;

	private String TAG = "NFCDeviceActivity";
	
	private TextView mTextView;
	
	private boolean isReceived = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		AuditLog.makeString("NFC","NFCDeviceActivity");
		super.onCreate(savedInstanceState);
		
		//Abhishek, May 27 2015, Launch Activity with provided title options START
		String titleBarString = "NO"; // for 3.2 changes StringUtils.getString(StringUtils.ENABLE_NAVIGATION_MODE);
		
    	boolean enableNavigation = "YES".equalsIgnoreCase(titleBarString);
		if (enableNavigation) {
			requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		} else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		//Abhishek, May 27 2015, Launch Activity with provided title options END
		
		setContentView(R.layout.activity_nfcdevice);
		
		mTextView = (TextView)findViewById(R.id.tv);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Intent devnfc = getIntent();
		
		//Abhishek , May 27 2015, updated for Activity started by NFC received START
//		nfctype = devnfc.getStringExtra("Type");
//		nfccon = devnfc.getStringExtra("Content");
//		nfccontent = nfccon.toString();
//		if (mNfcAdapter == null) {
//			Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
//			this.startActivity(intent);
//		}
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(devnfc.getAction())) {
			isReceived = true;
			processIntent(devnfc);
		}else{

			String dMsg;
			if(devnfc.getStringExtra("device").equalsIgnoreCase("SEND")){
				nfctype = devnfc.getStringExtra("Type");
				nfccon = devnfc.getStringExtra("Content");
				nfccontent = nfccon.toString();
				dMsg = getResources().getString(R.string.device_nfc_send);
			}else{
				dMsg = getResources().getString(R.string.device_nfc_receive);
			}
			
			if (mNfcAdapter == null) {
				Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
				this.startActivity(intent);
			} else{
				mTextView.setText(dMsg);
			}
		
//			nfccontent = nfccon.toString();
//			if (mNfcAdapter == null) {
//				Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
//				this.startActivity(intent);
//			} else{
//				mTextView.setText("Please bring the devices closer in order to transmit.");
//			}
		}
		
		//Abhishek , May 27 2015, updated for Activity started by NFC received END
		
		mNfcAdapter.setNdefPushMessageCallback(mCreateNdefMessageCallback, this);
		mNfcAdapter.setOnNdefPushCompleteCallback(mOnNdefPushCompleteCallback,this);
	}

	private CreateNdefMessageCallback mCreateNdefMessageCallback = new CreateNdefMessageCallback() {

		@Override
		public NdefMessage createNdefMessage(NfcEvent arg0) {
			NdefMessage ndefMsg = null;
			NdefRecord URIRecord;
			if (nfctype.equalsIgnoreCase("MSG")) {
//Abhishek , 17 June 2015, For solving chinese issue in windows 8.1 on receive NFC START
//				ndefMsg = create_RTD_TEXT_NdefMessage(nfccontent);
//Abhishek , 26 June 2015, createTextRecord is working only for API level 21 and above, for WINDOWS it will read chinese START
				if(android.os.Build.VERSION.SDK_INT>20){
				URIRecord = NdefRecord.createTextRecord(null, nfccontent);
				ndefMsg = new NdefMessage(new NdefRecord[] { URIRecord });
				}else{
					ndefMsg = create_RTD_TEXT_NdefMessage(nfccontent);
				}
//Abhishek , 26 June 2015, createTextRecord is working only for API level 21 and above, for WINDOWS it will read chinese END
//Abhishek , 17 June 2015, For solving chinese issue in windows 8.1 on receive NFC END
			} else if (nfctype.equalsIgnoreCase("URL")) {
				// Abhishek Bug id : 3202, 5510 :: To check if user had entered URI properly
				//Abhishek, 29 May 2015, commented as it was not opening URL in browser of receiving Device START
				if ((nfccontent.startsWith("http://"))||(nfccontent.startsWith("www."))) {
					URIRecord = NdefRecord.createUri(nfccontent);
				} else
				//Abhishek, 29 May 2015, commented as it was not opening URL in browser of receiving Device END
				{
					// Create manually
				byte[] uriField = nfccontent.getBytes(Charset.forName("US-ASCII"));
				byte[] payload = new byte[uriField.length + 1];
				payload[0] = 0x01;
				System.arraycopy(uriField, 0, payload, 1, uriField.length);
				URIRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI,new byte[0], payload);
				}
				ndefMsg = new NdefMessage(new NdefRecord[] { URIRecord });
			}
			return ndefMsg;
		}
	};

	public static void setupForegroundDispatch(final Activity activity,	NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(),activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent mNfcPendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
		IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		IntentFilter[] mNdefExchangeFilters = new IntentFilter[] { ndefDetected };
		adapter.enableForegroundDispatch(activity, mNfcPendingIntent,mNdefExchangeFilters, null);
	}

	NdefMessage create_RTD_TEXT_NdefMessage(String inputText) {
//		Locale locale = new Locale("en", "US");
		Locale locale = getResources().getConfiguration().locale;
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
		
		//Abhishek 17 July 2015, send in UTF 8, to prevent Chinese message in Windows START
//		boolean encodeInUtf8 = false;
		boolean encodeInUtf8 = true;
		//Abhishek 17 July 2015, send in UTF 8, to prevent Chinese message in Windows END
		
		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		byte status = (byte) (utfBit + langBytes.length);

		byte[] textBytes = inputText.getBytes(utfEncoding);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,textBytes.length);

		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT, new byte[0], data);
		NdefMessage message = new NdefMessage(new NdefRecord[] { textRecord });
//		ApzLogger.i(TAG,"create_RTD_TEXT_NdefMessage : " + message);
		return message;

	}

	private OnNdefPushCompleteCallback mOnNdefPushCompleteCallback = new OnNdefPushCompleteCallback() {

		@Override
		public void onNdefPushComplete(NfcEvent arg0) {
			ApzLogger.i(TAG,"OnNdefPushCompleteCallback");
			mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
		}
	};

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				setResult(RESULT_OK);
				finish();
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		setupForegroundDispatch(this, mNfcAdapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			
			//Abhishek , May 27 2015, If Received message is not URL, or TYPE is not mentioned START
//			if (nfctype.equalsIgnoreCase("MSG")) {
//				processIntent(getIntent());
//			}else if(nfctype.equalsIgnoreCase("URL")){
//				processurlIntent(getIntent());
//			}
			
			//Abhishek, 29 May 2015, checking for null START
//			if(nfctype.equalsIgnoreCase("URL")){
			if(nfctype != null && nfctype.equalsIgnoreCase("URL")){
			//Abhishek, 29 May 2015, checking for null END
				processurlIntent(getIntent());
			}else{
				processIntent(getIntent());
			}
		}
		//Abhishek , May 27 2015, If Received message is not URL, or TYPE is not mentioned END

	}
	 void processurlIntent(Intent intent) {	  
		 ApzLogger.i(TAG,"processurlIntent");
    	 NdefMessage[] messages = getNdefMessages(getIntent());    	 
    	 for(int i=0;i<messages.length;i++){
        	 for(int j=0;j<messages[0].getRecords().length;j++){
        		 NdefRecord record = messages[i].getRecords()[j];
        		 payload=new String(record.getPayload(),1,record.getPayload().length-1,Charset.forName("UTF-8"));
        	 }        	 
        } 
    	   Intent data = new Intent();
       	   data.setAction(Intent.ACTION_VIEW);
       	   data.setData(Uri.parse("http://www."+payload));
       		try {
       	    	startActivity(data); 
       	    } catch (ActivityNotFoundException e) {
       	    		return;
       	    }   
    }

	void processIntent(Intent intent) {
		ApzLogger.i(TAG,"processIntent");
		NdefMessage[] messages = getNdefMessages(getIntent());
		for (int i = 0; i < messages.length; i++) {
			for (int j = 0; j < messages[0].getRecords().length; j++) {
				NdefRecord record = messages[i].getRecords()[j];
				statusByte = record.getPayload()[0];
				int languageCodeLength = statusByte & 0x3F; // mask value in  order to find language code length
				int isUTF8 = statusByte - languageCodeLength;
				if (isUTF8 == 0x00) {
					payload = new String(record.getPayload(),1 + languageCodeLength, record.getPayload().length - 1 - languageCodeLength,Charset.forName("UTF-8"));
					break;
				} else if (isUTF8 == -0x80) {
					payload = new String(record.getPayload(),1 + languageCodeLength, record.getPayload().length	- 1 - languageCodeLength,Charset.forName("UTF-16"));
//					ApzLogger.i(TAG,"newintent3" + payload.toString());
					break;

				}
			}
		}
		//Abhishek , May 27 2015, If NFC Received directly then show toast START
//		NFC_RESULT = payload;
//		setResult(RESULT_OK);
		if(isReceived){
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.start_nfc_receiver), Toast.LENGTH_SHORT).show();
		}else{
			NFC_RESULT = payload;
			setResult(RESULT_OK);
		}
		//Abhishek , May 27 2015, If NFC Received directly then show toast END
		finish();
	}

	NdefMessage[] getNdefMessages(Intent intent) {
		NdefMessage[] msgs = null;
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			ApzLogger.d(TAG, "getNdefMessages : Unknown intent.");
			finish();
		}

		return msgs;
	}

}
