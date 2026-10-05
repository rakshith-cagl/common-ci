package com.iexceed.common;

import com.iexceed.appzillonapp.AppzillonMainScreen;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

public class OrderXMLHandler extends DefaultHandler {

	boolean currentElement = false;
	String currentValue = "";

	HashMap<String, String> appInfo;

	public void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException {

		currentElement = true;
		if (qName.equals("resources")) {
			appInfo = new HashMap<String, String>();
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		currentElement = false;		

		if (qName.equalsIgnoreCase(StringUtils.APP_NAME))
			
			appInfo.put(StringUtils.APP_NAME, currentValue.trim());
		
//		else if (qName.equalsIgnoreCase(StringUtils.TITLE_ACTIVITY_MAIN))
			
//			appInfo.put(StringUtils.TITLE_ACTIVITY_MAIN, currentValue.trim());
		
//		else if (qName.equalsIgnoreCase(StringUtils.APP_VERSION))
			
//			appInfo.put(StringUtils.APP_VERSION, currentValue.trim());
		
//		else if (qName.equalsIgnoreCase(StringUtils.APP_VENDOR))
			
	//		appInfo.put(StringUtils.APP_VENDOR, currentValue.trim());
		
//		else if (qName.equalsIgnoreCase(StringUtils.APP_ICON))
//			
//			appInfo.put(StringUtils.APP_ICON, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(StringUtils.FIRST_PAGE))
			
			appInfo.put(StringUtils.FIRST_PAGE, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(StringUtils.SERVER_URL))
			
			appInfo.put(StringUtils.SERVER_URL, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(StringUtils.GCM_SERVER_URL))
			
			appInfo.put(StringUtils.GCM_SERVER_URL, currentValue.trim());
		
		/* else if (qName.equalsIgnoreCase(StringUtils.GCM_SENDER_ID))
			
			appInfo.put(StringUtils.GCM_SENDER_ID, currentValue.trim()); */
		
//		else if (qName.equalsIgnoreCase(StringUtils.ENABLE_NAVIGATION_MODE))
//			
//			appInfo.put(StringUtils.ENABLE_NAVIGATION_MODE, currentValue.trim());
//		
		else if (qName.equalsIgnoreCase(StringUtils.GENERATE_OTP))
			
			appInfo.put(StringUtils.GENERATE_OTP, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(StringUtils.SERVER_TOKEN))
			
			appInfo.put(StringUtils.SERVER_TOKEN, currentValue.trim());
		
//		else if (qName.equalsIgnoreCase(StringUtils.SPLACH_ICON))
//			
//			appInfo.put(StringUtils.SPLACH_ICON, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(StringUtils.DEFAULT_LANG))
			
			appInfo.put(StringUtils.DEFAULT_LANG, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(StringUtils.APP_ID))
			
			appInfo.put(StringUtils.APP_ID, currentValue.trim());
		
//		else if (qName.equalsIgnoreCase(StringUtils.IS_OTA_ENABLED))
//			
//			appInfo.put(StringUtils.IS_OTA_ENABLED, currentValue.trim());
//		
//		else if (qName.equalsIgnoreCase(StringUtils.CONTAINER_APP))
//			
//			appInfo.put(StringUtils.CONTAINER_APP, currentValue.trim());
//		
//		else if (qName.equalsIgnoreCase(StringUtils.CONTAINER_MENU))
//			
//			appInfo.put(StringUtils.CONTAINER_MENU, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(AppzillonMainScreen.MAIN_APP_NAME))
			
			appInfo.put(AppzillonMainScreen.MAIN_APP_NAME, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(StringUtils.EXPIRY_DATE))
			
			appInfo.put(StringUtils.EXPIRY_DATE, currentValue.trim());
		
//		else if (qName.equalsIgnoreCase(StringUtils.APP_EXPIRY_MSG))
//			
//			appInfo.put(StringUtils.APP_EXPIRY_MSG, currentValue.trim());
//		
//		else if (qName.equalsIgnoreCase(StringUtils.APP_EXPIRY_MSG_OK))
//			
//			appInfo.put(StringUtils.APP_EXPIRY_MSG_OK, currentValue.trim());
		
		else if (qName.equalsIgnoreCase(StringUtils.EXPIRY_DATE_FORMAT))
			
			appInfo.put(StringUtils.EXPIRY_DATE_FORMAT, currentValue.trim());
		
//		else if (qName.equalsIgnoreCase(StringUtils.APP_NOT_FOUND))
//			
//			appInfo.put(StringUtils.APP_NOT_FOUND, currentValue.trim());
			
		else if (qName.equalsIgnoreCase(StringUtils.APP_IDLE_TIME_OUT))
				
			appInfo.put(StringUtils.APP_IDLE_TIME_OUT, currentValue.trim());

		currentValue = "";
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (currentElement) {
			currentValue = currentValue + new String(ch, start, length);
		}

	}

	public HashMap<String, String> getAppInfoMap() {
		return appInfo;
	}
}
