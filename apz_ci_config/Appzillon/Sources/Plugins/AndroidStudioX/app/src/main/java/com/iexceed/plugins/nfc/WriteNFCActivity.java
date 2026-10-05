package com.iexceed.plugins.nfc;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;



import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;
import android.widget.Toast;

import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.provider.Settings;

public class WriteNFCActivity extends Activity implements CreateNdefMessageCallback{

	private NfcAdapter mNfcAdapter;
    
	IntentFilter[] mWriteTagFilters; 
    
	public String nfcmessage;
    
	public String nfctype;
	
	public String payload;
	
	String TAG = "WriteNFCActivity";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		AuditLog.makeString("NFC","WriteNFCActivity");
		setContentView(R.layout.activity_nfcdevice);
        TextView mTextView = (TextView)findViewById(R.id.tv);
        mTextView.setText("Please bring the tag close to send messages");
        Intent writenfc = getIntent();
		nfcmessage = writenfc.getStringExtra("Content");
		nfctype = writenfc.getStringExtra("Type");
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);	
		if (mNfcAdapter == null) {
			Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
			this.startActivity(intent);
		}
		mNfcAdapter.setNdefPushMessageCallback(this, this);
	}
	
	 @Override
	 public void onResume() {
	     super.onResume();
	     setupForegroundDispatch(this, mNfcAdapter);	
	 }
	 
	 public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
//		 AuditLog.makeString("NFC","setupForegroundDispatch");
	    final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
	    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);	 
	    final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
	    IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
	    IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};	         
	    adapter.enableForegroundDispatch(activity, pendingIntent, writeTagFilters, null);
    }
	 
//Abhishek, bug id 5510, 3202 START
	
//	@Override
//	protected void onNewIntent(Intent intent) {
//		setIntent(intent);
//       // Tag writing mode
//	    if(nfctype.equalsIgnoreCase("MSG")){
////	    	AuditLog.makeString("NFC","MSG");
//		    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
//		        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//		        NdefRecord record = NdefRecord.createUri(nfcmessage);
//		        NdefMessage message = new NdefMessage(new NdefRecord[] { record });
//		        if (writeTag(message, detectedTag)) {
//		        	setResult(RESULT_OK);
//		            finish();
//		        } 
//		    }
//		}else if(nfctype.equalsIgnoreCase("URL")){
////				AuditLog.makeString("NFC","URL");
//		        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);		        
//		        byte[] uriField = nfcmessage.getBytes(Charset.forName("US-ASCII"));
//		        byte[] payload = new byte[uriField.length + 1];              //add 1 for the URI Prefix
//		        payload[0] = 0x01;                                      //prefixes http://www. to the URI
//		        System.arraycopy(uriField, 0, payload, 1, uriField.length);  //appends URI to payload
//		        NdefRecord URIRecord  = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
//		        NdefMessage newMessage= new NdefMessage(new NdefRecord[] { URIRecord });		           
//		        if (writeTag(newMessage, tag)) {
//		        	setResult(RESULT_OK);
//		            finish();
//		        } 
//			}
//		}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		NdefRecord URIRecord = null;
		// Tag writing mode
		if (nfctype.equalsIgnoreCase("MSG")) {
			if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
				URIRecord = NdefRecord.createUri(nfcmessage);
			}
		} else if (nfctype.equalsIgnoreCase("URL")) {

			// Abhishek Bug id : 3202 :: To check if user had entered URI properly
			if ((nfcmessage.startsWith("http://")) || (nfcmessage.startsWith("www."))) {
				URIRecord = NdefRecord.createUri(nfcmessage);
			} else
			{
				// Create manually
				byte[] uriField = nfcmessage.getBytes(Charset.forName("US-ASCII"));
				byte[] payload = new byte[uriField.length + 1]; // add 1 for the URI Prefix
				payload[0] = 0x01; // prefixes http://www. to the URI
				System.arraycopy(uriField, 0, payload, 1, uriField.length); // appends  URI to payload
				URIRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
			}

		}
		if (URIRecord != null) {
			ApzLogger.i("NFC", "URIRecord != null");
			NdefMessage newMessage = new NdefMessage( new NdefRecord[] { URIRecord });
			if (writeTag(newMessage, detectedTag)) {
				ApzLogger.i("NFC", "writeTag result ok");
				setResult(RESULT_OK);
				finish();
			}else{
				if(formatTag(newMessage, detectedTag)){
					setResult(RESULT_OK);
					finish();
				}else{
					setResult(RESULT_CANCELED);
					finish();
				}
			}
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

//Abhishek, bug id 5510, 3202 END
	
		public boolean writeTag(NdefMessage message, Tag tag) {
			ApzLogger.i("NFC","writeTag");
		    int size = message.toByteArray().length;
		    try {
		        Ndef ndef = Ndef.get(tag);
		        if (ndef != null) {
		        	ApzLogger.i("NFC","ndef is not null");
		            ndef.connect();
		            if (!ndef.isWritable()) {
						Toast.makeText(getApplicationContext(),	getResources().getString(R.string.tag_write_not_writable),Toast.LENGTH_SHORT).show();
		                return false;
		            }
		            if (ndef.getMaxSize() < size) {
						Toast.makeText(getApplicationContext(),	getResources().getString(R.string.tag_write_small),	Toast.LENGTH_SHORT).show();
		                return false;
		            }
		            try{
			            ndef.writeNdefMessage(message);
			            return true;
		            }catch (IOException e){
		            	ApzLogger.e(TAG,e.toString());
		            	return false;
		            }
		            
		        } else {
		            NdefFormatable format = NdefFormatable.get(tag);
		            if (format != null) {
		                try {
		                    format.connect();
		                    format.format(message);
		                    return true;
		                } catch (IOException e) {
		                	ApzLogger.e(TAG,e.toString());
		                    return false;
		                }
		            } else {
		                return false;
		            }
		        }
		    } catch (Exception e) {
		    	ApzLogger.e(TAG,e.toString());
		        return false;
		    }
		}
		
		private boolean formatTag(NdefMessage newMessage, Tag detectedTag) {
			NdefFormatable formatable = NdefFormatable.get(detectedTag);
			
			if (formatable != null) {
			      try {
			        formatable.connect();

			        try {
			          formatable.format(newMessage);
			          return true;
			        }
			        catch (Exception e) {
			          // let the user know the tag refused to format
			        	Toast.makeText(getApplicationContext(),	getResources().getString(R.string.tag_refused_format),	Toast.LENGTH_SHORT).show();
			        	return false;
			        }
			      }
			      catch (Exception e) {
			    	  ApzLogger.e(TAG,e.toString());
			    	  Toast.makeText(getApplicationContext(),	getResources().getString(R.string.tag_refused_connect),	Toast.LENGTH_SHORT).show();
			    	  return false;
			      }
			      finally {
			        try {
						formatable.close();
					} catch (IOException e) {
						ApzLogger.e(TAG,e.toString());
					}
			      }
			    }
			    else {
			      // let the user know the tag cannot be formatted
			    	Toast.makeText(getApplicationContext(),	getResources().getString(R.string.tag_cant_format),	Toast.LENGTH_SHORT).show();
			    	return false;
			    }
			
		}

		@Override
		public NdefMessage createNdefMessage(NfcEvent arg0) {
			ApzLogger.i("NFC","createNdefMessage");
			Locale locale= new Locale("en","US");
			byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
			
			boolean encodeInUtf8=false;
		    Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		    int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		    byte status = (byte) (utfBit + langBytes.length);
		   
		    String inputText =nfcmessage;
			byte[] textBytes = inputText .getBytes(utfEncoding);
		    
		    byte[] data = new byte[1 + langBytes.length + textBytes.length];
		    data[0] = (byte) status;
		    System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		    System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		    
		    NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT, new byte[0], data);
	       NdefMessage message= new NdefMessage(new NdefRecord[] { textRecord}); 
	       return message;
		}
		
		
		 void processIntent(Intent intent) {
			 ApzLogger.i("NFC","processIntent");
			 NdefMessage[] messages = getNdefMessages(getIntent());
			 	 
			 	 for(int i=0;i<messages.length;i++){
			     	 for(int j=0;j<messages[0].getRecords().length;j++){
			     		 NdefRecord record = messages[i].getRecords()[j];
			     		 payload=new String(record.getPayload(),1,record.getPayload().length-1,Charset.forName("UTF-8"));
			     	 }
			     }

			 }
		 

		 NdefMessage[] getNdefMessages(Intent intent) {
			 ApzLogger.i("NFC","getNdefMessages");
		 	 NdefMessage[] msgs = null;
		 	 if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
		 		 Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			        if (rawMsgs != null) {
			             msgs = new NdefMessage[rawMsgs.length];
			             for (int i = 0; i < rawMsgs.length; i++) {
			                 msgs[i] = (NdefMessage) rawMsgs[i];
			             }
			        } else {
			             byte[] empty = new byte[] {};
			             NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, empty, empty, empty);
			             NdefMessage msg = new NdefMessage(new NdefRecord[] {
			                 record
			             });
			             msgs = new NdefMessage[] {
			                 msg
			             };
		        }
			 	 }else {
			 		ApzLogger.e("PeertoPeer1 ", "Unknown intent.");
			             finish();
			         }
			      
			     return msgs;
		 }
}
