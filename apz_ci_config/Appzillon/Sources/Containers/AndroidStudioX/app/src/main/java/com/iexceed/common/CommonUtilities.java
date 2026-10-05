package com.iexceed.common;

import android.content.Context;
import android.content.Intent;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
    public final class CommonUtilities {
    
    static Context context;
    
    CommonUtilities(Context c){
    	context = c;
    }
    
    final String getGCMServerURL(){
    	return StringUtils.getString(StringUtils.SERVER_TOKEN);
    }
    
    /* final String getGCMSenderID(){    	
    	return StringUtils.getString(StringUtils.GCM_SENDER_ID);
    } */
    
    final static String getStaticGCMSenderID(){    	
    	//String file = "test.txt"; // res/raw/test.txt also work.
    	String senderID = "";
    	
    	/*try {
			FileReader fis = new FileReader(file);
			BufferedReader buf = new BufferedReader(fis);
			senderID = buf.readLine();
			
		} catch (FileNotFoundException e) {			
			
		} catch (IOException e) {
			
		}*/
    	System.out.println("Sending empty Sender ID");
    	return senderID;
    }
    
    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    //final String SERVER_URL = "http://192.168.1.211:8051/GCMServer";

    /**
     * Google API project id registered to use GCM.
     */
    //static final String SENDER_ID = "";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Azurite_GCM_SERVICE";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        final Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}

