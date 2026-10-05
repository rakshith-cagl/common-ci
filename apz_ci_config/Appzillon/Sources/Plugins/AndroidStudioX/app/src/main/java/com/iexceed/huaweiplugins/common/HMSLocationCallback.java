package com.iexceed.common;

import org.json.JSONObject;

public interface HMSLocationCallback {

    public void onLocationSuccess(JSONObject successJson);

    public void onLocationFailure(String error);

}
