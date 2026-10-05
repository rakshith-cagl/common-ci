package com.iexceed.appzillon.jsonutils;

import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONUtils {
    private static final com.iexceed.appzillon.logging.Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, JSONUtils.class.getName());

    private JSONUtils() {

    }

    public static String extractJsonString(String inputJson, String nodeName) {

        String outputString = null;

        try {
            JSONObject jo = new JSONObject(inputJson).getJSONObject(nodeName);
            outputString = jo.toString();
        } catch (JSONException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e);
        }

        return outputString;
    }

    public static String getJsonValueFromKey(String inputJson, String key) {

        String outputString = null;

        try {
            outputString = new JSONObject(inputJson).getString(key);
        } catch (JSONException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e);
        }
        return outputString;

    }

    public static String getJsonValueFromObject(JSONObject inputJson, String key) {
        String outputString = null;
        try {
            if (inputJson.has(key)) {
                outputString = inputJson.getString(key);
            }
        } catch (JSONException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e);
        }
        return outputString;

    }

    public static Map<String, String> getJsonHashMap(String inputJson) {
        Map<String, String> outMap = new HashMap<>();
        try {
            JSONObject jsonObj = new JSONObject(inputJson);
            Iterator<?> jsonNames = jsonObj.keys();
            while (jsonNames.hasNext()) {
                String name = (String) jsonNames.next();
                String value = jsonObj.get(name).toString();
                if (value == null) {
                    value = "";
                }
                outMap.put(name, value);
            }
        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        } catch (Exception e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.EXCEPTION, e);

        }
        return outMap;
    }

    public static JSONObject getJsonStringFromMap(Map<String, String> inputMap) {
        JSONObject lJsonObject = null;
        lJsonObject = new JSONObject(inputMap);
        return lJsonObject;
    }

    public static String getColStringFromBody(String pUpdate, String pName) {

        String scolString1 = "";
        try {
            JSONObject bodyobject = new JSONObject(pUpdate);
            Iterator<?> ite = bodyobject.keys();
            String sname = "";
            while (ite.hasNext()) {
                sname = ite.next().toString();
                if (sname.equals(pName)) {
                    scolString1 = bodyobject.get(sname).toString();
                }
            }

        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return scolString1;

    }

    public static String getOutputString(JSONArray recArray,
                                         Map<String, String> headerMap, String pName) {

        JSONObject bodyObj = new JSONObject();
        JSONObject outputObj = new JSONObject();

        try {
            bodyObj.put(pName, recArray);
            outputObj.put(ServerConstants.MESSAGE_HEADER, headerMap);
            outputObj.put(ServerConstants.MESSAGE_BODY, bodyObj);
        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return outputObj.toString();
    }

    /*
     * concatArray method returns JsonArray by concatenating
     * list of JsonArrays in arrs array
     * Added by Samy on 11-07-2013
     * Reviewed by Siddarth
     */
    public static JSONArray concatArray(JSONArray... arrs) {
        JSONArray result = new JSONArray();
        try {
            for (JSONArray arr : arrs) {
                for (int i = 0; i < arr.length(); i++) {
                    result.put(arr.get(i));
                }
            }
        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return result;
    }

    /*
     * putJSonObj method helps in converting
     * a json array to json object added to a node
     * Added by Samy on 11-07-2013
     * Reviewed by Siddarth
     */
    public static JSONObject putJSonObj(JSONObject responseObj, String nodeName, JSONArray inputArr) {
        try {
            responseObj.put(nodeName, inputArr);
        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return responseObj;
    }

    /*
     * putJSonObj method helps in adding
     * a nodevalue with nodeKey to json object
     * Added by Samy on 30-07-2013
     * Reviewed by Siddarth
     */
    public static JSONObject putJSonObj(JSONObject responseObj, String nodeName, String nodeValue) {
        try {
            responseObj.put(nodeName, nodeValue);

        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return responseObj;
    }
    /*
     * putJSonObj method helps in adding
     * a nodevalue with nodeKey to json object
     * Added by Samy on 10-10-2013
     * Reviewed by Siddarth
     */

    public static JSONObject putJSonObj(JSONObject responseObj, String nodeName, Object nodeValue) {
        try {
            responseObj.put(nodeName, nodeValue);

        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return responseObj;
    }
    /*
     * getOutputString method helps in converting
     * a json array to json object added to a node
     * Added by Samy on 11-07-2013
     * Reviewed by Siddarth
     */

    public static String getOutputString(JSONObject bodyJson,
                                         Map<String, String> headerMap) {
        JSONObject outputObj = new JSONObject();
        try {
            outputObj.put(ServerConstants.MESSAGE_HEADER, headerMap);
            outputObj.put(ServerConstants.MESSAGE_BODY, bodyJson);
        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return outputObj.toString();
    }

    /*
     * getJsonArrayFromString method helps in converting
     * a input JSON String to json Array
     * Added by Samy on 23-07-2013
     * Reviewed by Siddarth
     */
    public static JSONArray getJsonArrayFromString(String pInputJsonString) {
        JSONArray lRespJsonArray = null;
        try {
            lRespJsonArray = new JSONArray(pInputJsonString);

        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return lRespJsonArray;
    }

    public static JSONObject stringToJsonObj(String inputJson) {
        JSONObject json = null;
        try {
            json = new JSONObject(inputJson);
        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return json;
    }

    public static String[] jsonNodesToStringArr(JSONArray nodeNames) {
        String[] nodes = null;
        try {
            nodes = new String[nodeNames.length()];
            for (int i = 0; i < nodeNames.length(); i++) {
                nodes[i] = nodeNames.get(i).toString();
            }
        } catch (JSONException jsonEx) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, jsonEx);
        }
        return nodes;
    }

    public static String getKeyValue(String tempJSON, String node) {
        String lOutputString = "";
        try {
            JSONObject obj = new JSONObject(tempJSON);
            Iterator<?> it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();

                Object nodeValue = obj.get(key);
                if (node.equals(key)) {
                    lOutputString = nodeValue.toString();
                } else if (nodeValue instanceof JSONObject) {
                    lOutputString = getKeyValue(nodeValue.toString(), node);
                } else if (nodeValue instanceof JSONArray) {
                    JSONArray array = (JSONArray) nodeValue;
                    int j = -1;
                    int i = 0;

                    while (i < array.length()) {
                        Object object = array.get(i);
                        if (object instanceof JSONObject) {
                            JSONObject ob = array.getJSONObject(i);
                            if (Utils.isNullOrEmpty(lOutputString)) {
                                lOutputString = getKeyValue(ob.toString(), node);
                            } else {
                                String apend = getKeyValue(ob.toString(), node);
                                if (Utils.isNotNullOrEmpty(apend)) {
                                    lOutputString = lOutputString + "," + apend;
                                }

                            }

                        } else if (object instanceof JSONArray) {
                            array = (JSONArray) object;
                            j = i;
                            i = 0;
                            continue;
                        }
                        i++;
                        if (i == array.length() && j != -1) {
                            array = (JSONArray) nodeValue;
                            j++;
                            i = j;
                        }
                    }
                }
            }

        } catch (JSONException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + ServerConstants.JSON_EXCEPTION, e);
        }
        lOutputString = eliminateDuplicate(lOutputString);
        return lOutputString;
    }

    public static String eliminateDuplicate(String linputString) {
        String lOutputString = linputString;
        if (!linputString.contains("{") && !linputString.contains("[") && linputString.contains(",")) {
            String[] a = linputString.split(",");
            ArrayList<String> out = new ArrayList<>();
            int i = 0;
            while (i < a.length) {
                if (!out.contains(a[i])) {
                    out.add(a[i]);
                }
                i++;
            }
            i = 0;
            lOutputString = "";
            while (i < out.size() - 1) {
                lOutputString += out.get(i) + ",";
                i++;
            }
            lOutputString += out.get(i);
        }
        return lOutputString;
    }

    /*below method written by ripu for 2.2 changes
     * objective : convert json to Map
     * date : 28-05-2014
     */
    public static Map<String, Object> getHashMapFromJson(JSONObject pinputJson) throws JSONException {
        Map<String, Object> outmap = new HashMap<>();
        Iterator<?> jsonnames = pinputJson.keys();
        while (jsonnames.hasNext()) {
            String name = (String) jsonnames.next();
            String value = pinputJson.get(name).toString();
            if (value == null) {
                value = "";
            }
            outmap.put(name, value);
        }
        return outmap;
    }

    public static Map<String, Object> buildParamMap(JSONObject pinputJson) throws JSONException {
        Map<String, Object> outmap = new HashMap<>();
        Map<String, Object> localmap;
        JSONObject jsonObject;
        Iterator<?> jsonnames = pinputJson.keys();
        while (jsonnames.hasNext()) {
            String key = (String) jsonnames.next();
            Object nodeValue = pinputJson.get(key);
            if (nodeValue instanceof JSONObject) {
                jsonObject = (JSONObject) nodeValue;
                localmap = getHashMapFromJson(jsonObject);
                outmap.putAll(localmap);
            } else {
                outmap.put(key, getJsonValueFromKey(pinputJson.toString(), key));
            }
        }
        return outmap;
    }

}
