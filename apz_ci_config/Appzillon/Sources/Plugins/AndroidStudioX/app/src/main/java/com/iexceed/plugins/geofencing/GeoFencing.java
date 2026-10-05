package com.iexceed.plugins.geofencing;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.currentlocation.CurrentLocation;
import com.iexceed.plugins.errorlog.ApzLogger;

public class GeoFencing extends ApzPlugin{
	
	private static ApzPlugin pluginObj;
	private String TAG = "GEO FENCING";
	private JSONObject result = new JSONObject();
	
	public GeoFencing(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new GeoFencing(webView, activity);
		}
		return pluginObj;
	}

	private void checkGeoValidity(JSONObject json) {
		Location presentLocation = CurrentLocation.getLocation(this.activity);
	
//		ApzLogger.i(TAG, "Location : "+presentLocation);
		if(presentLocation != null){
			
			String region = "";
			String cordinates = "";
			String radius = "";
			String countryList = "";
			boolean isLocValid = false;
			try {
				callbackId = json.getString("id");
				region = json.getString("region");
				if(region.equalsIgnoreCase("LATLONG")){
					cordinates = json.getString("coordinates");
					radius = json.getString("radius");
					isLocValid = isPresentLocationRestricted(presentLocation,cordinates,radius);
				}else if(region.equalsIgnoreCase("COUNTRY")){
					countryList = json.getString("CountryList");
					isLocValid = isPresentCountryRestricted(presentLocation,countryList);
				}else{
					isLocValid = true;
				}
	
			} catch (Exception e) {
				ApzLogger.e(TAG,e.toString());
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077", null, this.activity, this.webView, true);//Cannot be launched GEO_FENCING_ERROR
			}	
			geoFencingCallback(isLocValid);
			
			
		}else{
			
			ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-092", null, this.activity, this.webView, true);
		}
		
	}

	private void geoFencingCallback(final boolean isLocValid) {
		try{
			result.put("allowLocation", isLocValid);
		}catch(Exception ex){
			ApzLogger.e(TAG,ex.toString());
		}
		ApzPluginUtil.sendSuccess(this.callbackId, result, false, activity, webView, true);
		
	}

	private boolean isPresentCountryRestricted(Location presentLocation,
		String restrictedCountryList) throws Exception {
		Geocoder geocoder = new Geocoder(activity.getApplicationContext());
		List<Address> address;
		address = geocoder.getFromLocation(presentLocation.getLatitude(),presentLocation.getLongitude(), 1);
		String countryCode = address.get(0).getCountryCode();
		String ISOCode = "";
		if (countryCode != null) {
			Locale locale = new Locale("", countryCode);
			ISOCode = locale.getISO3Country();
		}

//		ApzLogger.i(TAG, "Country Code :" + countryCode + " ISOCode : " + ISOCode);

		if (restrictedCountryList.contains(ISOCode)) {
			ApzLogger.i(TAG, "Allowed country");
			return false;
		} else {
			ApzLogger.i(TAG, "Restricted country");
			return true;
		}
	}

	@SuppressLint("UseValueOf")
	private boolean isPresentLocationRestricted(Location presentLocation,String cordinates, String r) {
		int radius =  Integer.parseInt(r);
		String strLat = cordinates.split(",")[0];
		String strLongi = cordinates.split(",")[1];
		Location destinationLoc = new Location("dest");
		destinationLoc.setLatitude(new Double(strLat));
		destinationLoc.setLongitude(new Double(strLongi));
		int distance  = getdistance(presentLocation,destinationLoc);
//		ApzLogger.i(TAG, "Distance in meters :"+distance);
		if(distance>radius){
			return true;
		}else{
			return false;
		}
	}
	private int getdistance(Location pLoc,Location dLoc) {
		double latA = pLoc.getLatitude();
		double longA = pLoc.getLongitude();
		double latB = dLoc.getLatitude();
		double longB = dLoc.getLongitude();
		
		double d2r = Math.PI / 180;

		double dlong = (longA - longB) * d2r;
		double dlat = (latA - latB) * d2r;
		double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(latB * d2r)* Math.cos(latA * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367 * c;

		return (int) d;

	}
	  
	public static boolean isGeoFencing() {		
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		checkGeoValidity(params);
	}
}
