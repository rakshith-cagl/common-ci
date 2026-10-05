package com.iexceed.plugins.nfc;

import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.errorlog.ApzLogger;

import java.io.UnsupportedEncodingException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;
public class ReadNFCActivity extends Activity {

	public static final String TAG = "ReadNFCActivity";	 
    
	private NfcAdapter mNfcAdapter;
    
	public static final String MIME_TEXT_PLAIN = "text/plain";
   
	public static String nfctype;
    
	public static String NFC_RESULT;
    
    ProgressDialog pdialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApzLogger.i("NFC","ReadNFCActivity");
        setContentView(R.layout.activity_nfcdevice);
		TextView 	mTextView = (TextView)findViewById(R.id.tv);
		mTextView.setText("Please bring the tag close to receive messages");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent recnfc = getIntent();
		nfctype = recnfc.getStringExtra("Type"); 
        if (mNfcAdapter == null) {
			Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
			this.startActivity(intent);
		}
       handleIntent(getIntent());
        
    }

    @Override
    protected void onResume() {
//    	AuditLog.makeString("NFC","onresume");
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }
    
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
//    	AuditLog.makeString("NFC","setupForegroundDispatch");
    	final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0); 
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        ApzLogger.i("NFC","tagDetected");
        //Abhishek ,26 June 2015, added for launching URL into browser START
        if(nfctype.equalsIgnoreCase("URL")){
        	try {
            	tagDetected.addDataType("text/plain");
    		} catch (MalformedMimeTypeException e) {
    		}
        }
      //Abhishek ,26 June 2015, added for launching URL into browser END
        
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};         
        adapter.enableForegroundDispatch(activity, pendingIntent, writeTagFilters, null);
    }
    
    @Override
    protected void onPause() {  
//    	AuditLog.makeString("NFC","onPause");
        stopForegroundDispatch(this, mNfcAdapter);         
        super.onPause();
    }
    
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
//    	AuditLog.makeString("NFC","stopForegroundDispatch");
        adapter.disableForegroundDispatch(activity);
    }
    
    @Override
    protected void onNewIntent(Intent intent) { 
    	setIntent(intent);
        handleIntent(intent);
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void handleIntent(Intent intent) {
    	//Intent intent = getIntent();
    	ApzLogger.i(TAG, "handleIntent");
    	 String action = intent.getAction();
    	    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {	         
    	    	ApzLogger.i(TAG, "ACTION_TAG_DISCOVERED");
    	            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//    	            new NdefReaderTask().execute(tag);
    	            if(Build.VERSION.SDK_INT >= 11)
    	            	new NdefReaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,tag);
    	            else
    	            	new NdefReaderTask().execute(tag);
    	       
    	    } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
    	         
    	        // In case we would still use the Tech Discovered Intent
    	        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	        String[] techList = tag.getTechList();
    	        String searchedTech = Ndef.class.getName();
    	         
    	        for (String tech : techList) {
    	            if (searchedTech.equals(tech)) {
//    	                new NdefReaderTask().execute(tag); 
    	            	if(Build.VERSION.SDK_INT >= 11)
        	            	new NdefReaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,tag);
        	            else
        	            	new NdefReaderTask().execute(tag);
    	                break;
    	            }
    	        }
    	    }
    }
    
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
    	
    	@Override
    	protected void onPreExecute() {
    		ApzLogger.i(TAG, "onPreexecute");
    		pdialog = new ProgressDialog(ReadNFCActivity.this);
    		pdialog.setCancelable(false);
    		pdialog.setMessage("Loading ....");
    		if(!pdialog.isShowing())
    		pdialog.show();
    		super.onPreExecute();
    	}
    	 
        @Override
        protected String doInBackground(Tag... params) {
        	ApzLogger.i(TAG, "NdefReaderTask doInBackground");
            Tag tag = params[0];
            String result = null;
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
//            	Toast.makeText(getApplicationContext(), "NDEF is not supported by this Tag.", Toast.LENGTH_LONG).show();
                // NDEF is not supported by this Tag. 
                 result = null;
            }
            
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
            	if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN ) {
                    try {
						result = readText(ndefRecord);
                      //  return result;
                    } catch (UnsupportedEncodingException e) {
                        ApzLogger.e(TAG, "Unsupported Encoding"+ e.toString());
                   }
                }
            }
               
            return result;
        }
        
         
        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            byte[] payload = record.getPayload();     
            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";     
            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;             
            // Get the Text
            //String res = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
			// Abhishek to over come array out of index exception
            int offset=languageCodeLength + 1;
            int byteCount = payload.length - languageCodeLength - 1;
            String res = "";
            if(byteCount>0){
//            	res = new String(payload, offset,byteCount , textEncoding);
            	res = new String(payload, 0,payload.length , textEncoding);
            }            
//            ApzLogger.i("Abhishek","res : "+res);
            return res;
        }
        
        @Override
		protected void onPostExecute(String result) {
			if (result != null) {

				ApzLogger.i(TAG, "onPostExecute");
				result = result.trim();
				if (pdialog != null)
					pdialog.dismiss();
				if (result.startsWith("http://") || (result.startsWith("www."))) {
					Intent data = new Intent();
					data.setAction(Intent.ACTION_VIEW);
					data.setData(Uri.parse(result));
					try {
						startActivity(data);
						finish();
					} catch (ActivityNotFoundException e) {
						ApzLogger.e(TAG,e.toString());
						Toast.makeText(getApplicationContext(),
								"Error: Activity Not found", Toast.LENGTH_SHORT)
								.show();
						return;
					}
				} else {
					NFC_RESULT = result;
					setResult(RESULT_OK);
					finish();
				}
			} else {
				setResult(RESULT_CANCELED);
				finish();
			}

		}
   
    }

}
