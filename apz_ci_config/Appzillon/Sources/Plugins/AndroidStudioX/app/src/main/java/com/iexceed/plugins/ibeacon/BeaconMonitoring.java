package com.iexceed.plugins.ibeacon;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.webkit.WebView;

import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

public class BeaconMonitoring extends Service implements BeaconConsumer {

    protected static final String TAG = "BeaconMonitoring";

    private BeaconManager iBeaconManager;

    static WebView mWebView;

    static Activity activity;

    static String callBackId;

    String beaconId;

    Identifier lastIdentifier;

    boolean isPaused = false;

    public static boolean isBeaconRunning;


    public BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();

    private boolean mbBleStatusBefore = false;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
   // protected void onCreate(Bundle savedInstanceState) {
    public int onStartCommand(Intent intent, int flags, int startId) {
        // super.onCreate(savedInstanceState);
        try {
                isBeaconRunning = true;
                iBeaconManager = BeaconManager.getInstanceForApplication(this);
                iBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
                iBeaconManager.bind(this);
                verifyBluetooth();
                beaconId = intent.getStringExtra("BEACON_ID");

        } catch (UnsupportedOperationException e) {
            ApzLogger.e(TAG, e.toString());
            JSONObject json = new JSONObject();
            try {
                json.put("errorMessage", "Beacon is already running");
                json.put("errorCode", "APZ-CNT-253");
            } catch (JSONException e1) {
                ApzLogger.e(TAG, e.toString());
            }
            ApzPluginUtil.sendError(callBackId, "APZ-CNT-253", json, activity, mWebView, true);

        }
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        iBeaconManager.unbind(this);
        if (null != mBT && !this.mbBleStatusBefore) {
            mBT.disable();
        }
        if (mBT.isDiscovering()) {
            mBT.cancelDiscovery();
        }
        isBeaconRunning = false;
    }

    @Override
    public void onBeaconServiceConnect() {
        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didExitRegion(Region arg0) {
                ApzLogger.i(TAG, "I no longer see an iBeacon");
                lastIdentifier = null;
                isPaused = false;
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String toPrint = "No Beacon";
                    }
                });
            }

            @Override
            public void didEnterRegion(final Region arg0) {
                ApzLogger.i(TAG, "I just saw an iBeacon for the firt time!");
                lastIdentifier = null;
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String toPrint = "Unique Id : " + arg0.getUniqueId();

                    }
                });
            }

            @Override
            public void didDetermineStateForRegion(final int state, final Region region) {
                ApzLogger.i(TAG, "State: " + state);

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String toPrint = "State" + state + " Unique Id : " + region.getUniqueId();
                    }
                });
            }
        });
        try {
            iBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));

        } catch (RemoteException e) {
            
        }

        iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (!isPaused) {
                                final Beacon firstBeacon = beacons.iterator().next();
                                String blueToothAddress = firstBeacon.getBluetoothAddress();
                                int typeCode = firstBeacon.getBeaconTypeCode();
                                String bluetoothName = firstBeacon.getBluetoothName();
                                List<Long> extraDataFileds = firstBeacon.getExtraDataFields();
                                List<Identifier> idetefiers = firstBeacon.getIdentifiers();
                                int manufactures = firstBeacon.getManufacturer();
                                int rssi = firstBeacon.getRssi();
                                int serviceuuid = firstBeacon.getServiceUuid();
                                int txPower = firstBeacon.getTxPower();
                                float distance = (float) firstBeacon.getDistance();
                                String proximity = "";
                                if (distance <= 0.5) {
                                    proximity = "immediate";
                                } else if (distance > 0.5 && distance <= 3.0) {
                                    proximity = "near";
                                } else if (distance > 3.1) {
                                    proximity = "far";
                                }
                                final String toPrint = "blueToothAddress : " + blueToothAddress + " , typeCode : " + typeCode + " , bluetoothName : " + bluetoothName + " , extraDataFileds : " + extraDataFileds + " , idetefiers : " + idetefiers + " , manufactures : " + manufactures + " , rssi : " + rssi + " , serviceuuid : " + serviceuuid + " , txPower : " + txPower + ", distance : " + distance + ", proximity : " + proximity;
                                Identifier id = idetefiers.get(0);
                                boolean isChanged = false;
                                if (lastIdentifier != null)
                                    isChanged = id.toString().equalsIgnoreCase(lastIdentifier.toString());
                                lastIdentifier = id;
                                if (id.toString().equalsIgnoreCase(beaconId) && !isChanged) {
                                    JSONObject json = new JSONObject();
                                    try {
                                        json.put("bluetoothAddress", blueToothAddress);
                                        json.put("typeCode", typeCode);
                                        json.put("bluetoothName", bluetoothName);
                                        json.put("extraDataFields", extraDataFileds);
                                        if(idetefiers.size()>0) {
                                            json.put("beaconUuid", idetefiers.get(0));
                                            json.put("major", idetefiers.get(1));
                                            json.put("minor", idetefiers.get(2));
                                        }
                                        json.put("manufacturers", manufactures);
                                        json.put("rssi", rssi);
                                        json.put("serviceUuid", serviceuuid);
                                        json.put("txPower", txPower);
                                        json.put("distance", distance);
                                        json.put("proximity", proximity);
                                        json.put("event", "beaconDetected");
                                        //  json.put("text", toPrint);
                                    } catch (JSONException e) {
                                        ApzLogger.e(TAG, e.toString());
                                    }
                                    ApzPluginUtil.sendSuccess(callBackId, json, true, activity, mWebView, true);
                                    ApzLogger.e(TAG, toPrint);
                                    isPaused = true;
                                }

                            } else {
                                ApzLogger.e(TAG, "isPaused");
                            }

                        }
                    });

                    ApzLogger.i(TAG, "first beacon: " + beacons.iterator().next().getIdentifiers().get(0) + " about " + beacons.iterator().next().getDistance() + " meters away.");
                }
            }

        });

        try {
            iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            ApzLogger.e(TAG, e.toString());
        }
    }


    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(activity).checkAvailability()) {
                new startBluetoothDeviceBackGroundTask().execute();
            }
            JSONObject json = new JSONObject();
            try {
                json.put("event", "started");
            } catch (JSONException e) {
            }
            ApzPluginUtil.sendSuccess(callBackId, json, true, activity, mWebView, true);
        } catch (RuntimeException e) {
            JSONObject json = new JSONObject();
            try {
                json.put("errorMessage", "Device does not support Beacon");
                json.put("errorCode", "APZ-CNT-252");
            } catch (JSONException e1) {
                ApzLogger.e(TAG, e1.toString());
            }
            ApzPluginUtil.sendError(callBackId, "APZ-CNT-252", json, activity, mWebView, true);
        }
    }

    private class startBluetoothDeviceBackGroundTask extends AsyncTask<String, String, Integer> {
        private static final int RET_BULETOOTH_IS_START = 0x0001;
        private static final int RET_BLUETOOTH_START_FAIL = 0x04;
        private static final int miWATI_TIME = 15;
        private static final int miSLEEP_TIME = 150;

        @Override
        public void onPreExecute() {
            mbBleStatusBefore = mBT.isEnabled();
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            int iWait = miWATI_TIME * 1000;
            /* BT isEnable */
            if (!mBT.isEnabled()) {
                mBT.enable();
                //Wait miSLEEP_TIME seconds, start the Bluetooth device before you start scanning
                while (iWait > 0) {
                    if (!mBT.isEnabled())
                        iWait -= miSLEEP_TIME;
                    else
                        break;
                    SystemClock.sleep(miSLEEP_TIME);
                }
                if (iWait < 0)
                    return RET_BLUETOOTH_START_FAIL;
            }
            return RET_BULETOOTH_IS_START;
        }

        @Override
        public void onPostExecute(Integer result) {

            if (RET_BLUETOOTH_START_FAIL == result) {
                Intent in = new Intent();
                in.putExtra("error", "Failed to start Bluetooth.");
            }
        }
    }

    public static void setWebView(WebView webView, Activity mActivity,String id) {
        mWebView = webView;
        activity = mActivity;
        callBackId = id;

    }
}

