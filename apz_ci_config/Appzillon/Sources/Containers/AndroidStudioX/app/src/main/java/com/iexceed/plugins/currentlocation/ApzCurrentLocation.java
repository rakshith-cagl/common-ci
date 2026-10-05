package com.iexceed.plugins.currentlocation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ApzLocationManager;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class ApzCurrentLocation extends ApzPlugin {

    private static ApzPlugin pluginObj;
    public static boolean hasLocationPermission;
    private String[] permissions;
    private JSONObject mJsonObj;

    private ApzCurrentLocation(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzCurrentLocation(webView, activity);
        }
        return pluginObj;
    }

    private void fetchCurrentLocation() {
        String latitude;
        String longitude;
        try {
            ApzLocationManager gps = new ApzLocationManager(activity);
            Location latlng = gps.getLocation();
            if (latlng != null) {
                final JSONObject successJson = new JSONObject();
                double latd = latlng.getLatitude();
                double lngd = latlng.getLongitude();
                double accuracy = latlng.getAccuracy();
                latitude = Double.toString(latlng.getLatitude());
                longitude = Double.toString(latlng.getLongitude());
                successJson.put("latitude", latitude);
                successJson.put("longitude", longitude);
                successJson.put("accuracy", Double.toString(accuracy));

                String formatToConvert = mJsonObj.optString("format");
                setFormattedLatLng(formatToConvert, latd, lngd, successJson);

                Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latd, lngd, 1);
                    Address obj = addresses.get(0);
                    successJson.put("address", obj.getAddressLine(0));
                } catch (Exception ignored) {}
                ApzPluginUtil.sendSuccess(callbackId,
                        successJson, false, activity,
                        webView, true);
            } else {
                final JSONObject eJson = new JSONObject();
                eJson.put("errorCode", "");
                eJson.put("errorMessage", "No providers available to fetch location");
                ApzPluginUtil.sendError(callbackId, "APZ-CNT-274",
                        eJson, activity,
                        webView, true);
            }
        } catch (Exception ignored) {}
    }


    @Override
    public void execute(JSONObject params) {
        try {
            this.callbackId = params.getString("id");
            this.mJsonObj = params;
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                requestForPermission();
            } else {
                fetchCurrentLocation();
            }

        } catch (Exception ignored) {}

    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_LOCATION, new OnPermissionsResultHandler() {
                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_LOCATION) {
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
                                fetchCurrentLocation();
                                hasLocationPermission = true;
                            }
                        } else {
                            PermissionDeniedCallback();
                        }

                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To access location ,allow app to access by granting requested permissions";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Permission Denied");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Allow", (dialog, id) -> {
                    dialog.cancel();
                    requestForPermission();
                }).setNegativeButton("Deny", (dialog, id) -> {
                    dialog.cancel();
                    PermissionDeniedCallback();
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void PermissionDeniedCallback() {
        ApzPluginUtil.sendPermissionDenied("Location", callbackId, this.activity, this.webView);
    }

    //Function used to fetch and set the formatted lat lng in the desired format.
    private void setFormattedLatLng(String latLngFormat, double latitude,
                                    double longitude, JSONObject jsonObject) {

        String formattedLatLng;

        if (latLngFormat.equalsIgnoreCase("DMS")) {
            formattedLatLng = getLatLongInDMSFormat(latitude, longitude);
        } else if (latLngFormat.equalsIgnoreCase("DD")) {
            formattedLatLng = getLatLngInDDFormat(latitude, longitude);
        } else if (latLngFormat.equalsIgnoreCase("DDM")) {
            formattedLatLng = getLatLngInDDMFormat(latitude, longitude);
        } else {
            addFormattedValuesToJson(jsonObject, "", "");
            return;
        }

        if (!formattedLatLng.isEmpty()) {
            if (formattedLatLng.contains(";")) {
                String[] formattedLatLngArray = formattedLatLng.split(";");
                addFormattedValuesToJson(jsonObject, formattedLatLngArray[0], formattedLatLngArray[1]);
            }
        }
    }

    //Function used to convert lat lng into Degrees Minutes Seconds (DMS) format.
    private String getLatLongInDMSFormat(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = longDegrees >= 0 ? "E" : "W";

            return Math.abs(latDegrees) + "° " + latMinutes + "' " + latSeconds
                    + "\" " + latDegree + ";" + Math.abs(longDegrees) + "° " + longMinutes
                    + "' " + longSeconds + "\" " + lonDegrees;
        } catch (Exception e) {
            ApzLogger.e(TAG, e.toString());
            return "";
        }
    }

    //Function to add formatted lat lng to the json for sending to JS side.
    private void addFormattedValuesToJson(JSONObject jsonObject, String lat, String lng) {
        try {
            jsonObject.put("formattedLatitude", lat);
            jsonObject.put("formattedLongitude", lng);
        } catch (JSONException ignored) {}
    }

    //Function used to convert Lat Long into Decimal Degrees (DD) format.
    private String getLatLngInDDFormat(Double latitude, Double longitude) {

        int latSeconds = Math.toIntExact(Math.round(latitude * 3600));
        int latDegrees = latSeconds / 3600;
        latSeconds = Math.abs(latSeconds % 3600);
        int latMinutes = latSeconds / 60;
        latSeconds %= 60;
        int longSeconds = Math.toIntExact(Math.round(longitude * 3600));
        int longDegrees = longSeconds / 3600;
        longSeconds = Math.abs(longSeconds % 3600);
        int longMinutes = longSeconds / 60;
        longSeconds %= 60;

        double longDD = longDegrees + Double.parseDouble(String.valueOf(longMinutes)) / 60 + (Double.parseDouble(String.valueOf(longSeconds)) / 3600);
        double latDD = latDegrees + (Double.parseDouble(String.valueOf(latMinutes)) / 60) + (Double.parseDouble(String.valueOf(latSeconds)) / 3600);

        return String.format("%.2f", latDD) + ";" + String.format("%.2f", longDD);
    }

    //Function used to convert the Lat Lng into Degrees Decimal Minutes (DDM) format.
    private String getLatLngInDDMFormat(Double latitude, Double longitude) {

        int latSeconds = Math.toIntExact(Math.round(latitude * 3600));
        int latDegrees = latSeconds / 3600;
        latSeconds = Math.abs(latSeconds % 3600);
        int latMinutes = latSeconds / 60;
        latSeconds %= 60;
        int longSeconds = Math.toIntExact(Math.round(longitude * 3600));
        int longDegrees = longSeconds / 3600;
        longSeconds = Math.abs(longSeconds % 3600);
        int longMinutes = longSeconds / 60;
        longSeconds %= 60;

        double longInDD = longDegrees + Double.parseDouble(String.valueOf(longMinutes)) / 60 + (Double.parseDouble(String.valueOf(longSeconds)) / 3600);
        double latInDD = latDegrees + (Double.parseDouble(String.valueOf(latMinutes)) / 60) + (Double.parseDouble(String.valueOf(latSeconds)) / 3600);

        String[] latitudeSplit = String.valueOf(latInDD).split("\\.");
        String[] longitudeSplit = String.valueOf(longInDD).split("\\.");

        String decimalFromLatitude = "0." + latitudeSplit[1];
        String decimalFromLongitude = "0." + longitudeSplit[1];

        double minutesInLatitude = Double.parseDouble(decimalFromLatitude) * 60;
        double minutesInLongitude = Double.parseDouble(decimalFromLongitude) * 60;

        String degreeInLatitude = latitudeSplit[0] + "° ";
        String degreeInLongitude = longitudeSplit[0] + "° ";

        String roundedMinutesInLatitude = String.format("%.2f", minutesInLatitude) + "' ";
        String roundedMinutesInLongitude = String.format("%.2f", minutesInLongitude) + "' ";

        String latitudeDirection = latInDD >= 0 ? "N" : "S";
        String longitudeDirection = longInDD >= 0 ? "E" : "W";


        return degreeInLatitude + roundedMinutesInLatitude + latitudeDirection +
                ";" + degreeInLongitude + roundedMinutesInLongitude + longitudeDirection;
    }


}

