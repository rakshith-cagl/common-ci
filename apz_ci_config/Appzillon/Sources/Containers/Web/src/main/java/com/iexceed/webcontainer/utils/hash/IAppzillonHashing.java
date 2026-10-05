package com.iexceed.webcontainer.utils.hash;

/**
 * 
 * @author arthanarisamy
 *
 */
import com.iexceed.webcontainer.utils.json.JSONException;
import com.iexceed.webcontainer.utils.json.JSONObject;
public interface IAppzillonHashing {

	/**
	 * 
	 * @param json
	 * @param lString
	 * @return
	 * @throws JSONException
	 */
	JSONObject generateHashedPin(JSONObject json, String lString) throws JSONException;
}
