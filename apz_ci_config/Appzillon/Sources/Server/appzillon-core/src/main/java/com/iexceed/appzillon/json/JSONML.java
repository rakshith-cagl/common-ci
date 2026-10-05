package com.iexceed.appzillon.json;

/*
Copyright (c) 2008 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.util.HashMap;
import java.util.Map;

import static com.iexceed.appzillon.utils.Constants.*;

/**
 * This provides static methods to convert an XML text into a JSONArray or
 * JSONObject, and to covert a JSONArray or JSONObject into an XML text using
 * the JsonML transform.
 *
 * @author JSON.org
 * @version 2016-01-30
 */
public class JSONML {
    private JSONML() {
        //default constructor
    }

    /**
     * Parse XML values and store them in a JSONArray.
     *
     * @param xmlTokener  The XMLTokener containing the source string.
     * @param arrayForm   true if array form, false if object form.
     * @param ja          The JSONArray that is containing the current tag or null
     *                    if we are at the outermost level.
     * @param keepStrings Don't type-convert text nodes and attribute values
     * @return A JSONArray if the value is the outermost tag, otherwise null.
     * @throws com.iexceed.appzillon.json.JSONException
     */
    private static Object parse(
            XMLTokener xmlTokener,
            boolean arrayForm,
            com.iexceed.appzillon.json.JSONArray ja,
            boolean keepStrings
    ) throws com.iexceed.appzillon.json.JSONException {
        Object token;

        // Test for and skip past these forms:
        //      <!-- ... -->
        //      <![  ... ]]>
        //      <!   ...   >
        //      <?   ...  ?>

        while (true) {
            if (!xmlTokener.more()) {
                throw xmlTokener.syntaxError("Bad XML");
            }
            token = xmlTokener.nextContent();
            if (token == XML.LT) {
                token = xmlTokener.nextToken();
                Object token1 = validateBasedOnDataType(xmlTokener, arrayForm, ja, keepStrings, token);
                if (token1 != null) return token1;
            } else if (ja != null && token instanceof String stringVal) {
                Object tokenValue = keepStrings ? XML.unescape(stringVal) : XML.stringToValue((String) token);
                ja.put(tokenValue);
            }
        }
    }

    private static Object validateBasedOnDataType(XMLTokener x, boolean arrayForm, JSONArray ja, boolean keepStrings, Object token) {
        JSONArray newJa;
        JSONObject newJo;
        String tagName;
        if (token instanceof Character) {
            token = getTokenForCharacters(x, ja, token);
            if (token != null) return token;
            // Open tag <
        } else {
            if (!(token instanceof String)) {
                throw x.syntaxError("Bad tagName '" + token + "'.");
            }
            tagName = (String) token;
            newJa = new JSONArray();
            newJo = new JSONObject();
            prepareJSONArray(arrayForm, ja, newJa, newJo, tagName);
            token = getNextToken(x, arrayForm, keepStrings, newJo);

            if (arrayForm && newJo.length() > 0) {
                newJa.put(newJo);
            }
            Map<String, Object> param = new HashMap<>();
            param.put("x", x);
            param.put("arrayForm", arrayForm);
            param.put("ja", ja);
            param.put("keepStrings", keepStrings);
            param.put("newJa", newJa);
            param.put("newJo", newJo);
            param.put("token", token);
            param.put("tagName", tagName);
            Object newJa1 = getJSONArrayOrJSONObject(param);
            if (newJa1 != null) return newJa1;
        }
        return null;
    }

    private static Object getTokenForCharacters(XMLTokener x, JSONArray ja, Object token) {
        if (token == XML.SLASH) {
            return getTokenForSlash(x);
        } else if (token == XML.BANG) {
            getTokenForBang(x, ja);
        } else if (token == XML.QUEST) {
            // <?
            x.skipPast("?>");
        } else {
            throw x.syntaxError(MISSHAPED_TAG);
        }
        return null;
    }

    private static Object getJSONArrayOrJSONObject(Map<String, Object> inputParam) {

        XMLTokener x = (XMLTokener) inputParam.get("x");
        boolean arrayForm = (boolean) inputParam.get("arrayForm");
        JSONArray ja = (JSONArray) inputParam.get("ja");
        boolean keepStrings = (boolean) inputParam.get("keepStrings");
        JSONArray newJa = (JSONArray) inputParam.get("newJa");
        JSONObject newJo = (JSONObject) inputParam.get("newJo");
        Object token = inputParam.get("token");
        String tagName = (String) inputParam.get("tagName");

        // Empty tag <.../>
        if (token == XML.SLASH) {
            exceptionIfNotGreaterThan(x, x.nextToken());
            if (ja == null) {
                return getJaOrJO(arrayForm, newJa, newJo);
            }
            // Content, between <...> and </...>
        } else {
            exceptionIfNotGreaterThan(x, token);
            if (isCloseTag(x, arrayForm, ja, keepStrings, newJa, newJo, tagName))
                return getJaOrJO(arrayForm, newJa, newJo);
        }
        return null;
    }

    private static boolean isCloseTag(XMLTokener x, boolean arrayForm, JSONArray ja, boolean keepStrings, JSONArray newJa, JSONObject newJo, String tagName) {
        String closeTag;
        closeTag = (String) parse(x, arrayForm, newJa, keepStrings);
        if (closeTag != null) {
            if (!closeTag.equals(tagName)) {
                throw x.syntaxError("Mismatched '" + tagName +
                        "' and '" + closeTag + "'");
            }
            if (!arrayForm && newJa.length() > 0) {
                newJo.put(CHILD_NODE, newJa);
            }
            if (ja == null) {
                return true;
            }
        }
        return false;
    }

    private static void exceptionIfNotGreaterThan(XMLTokener x, Object token) {
        if (token != XML.GT) {
            throw x.syntaxError(MISSHAPED_TAG);
        }
    }


    private static Object getJaOrJO(boolean arrayForm, JSONArray newJa, JSONObject newJo) {
        if (arrayForm) {
            return newJa;
        }
        return newJo;
    }

    private static Object getNextToken(XMLTokener x, boolean arrayForm, boolean keepStrings, JSONObject newJo) {
        Object token;
        String attribute;
        token = null;
        for (; ; ) {
            if (token == null) {
                token = x.nextToken();
            }
            if (token == null) {
                throw x.syntaxError(MISSHAPED_TAG);
            }
            if (!(token instanceof String)) {
                break;
            }

            // attribute = value
            attribute = (String) token;
            if (!arrayForm && (TAG_NAME.equals(attribute) || "childNode".equals(attribute))) {
                throw x.syntaxError("Reserved attribute.");
            }
            token = x.nextToken();
            token = accumulateNewJo(x, keepStrings, newJo, token, attribute);
        }
        return token;
    }

    private static Object accumulateNewJo(XMLTokener x, boolean keepStrings, JSONObject newJo, Object token, String attribute) {
        if (token == XML.EQ) {
            token = x.nextToken();
            if (!(token instanceof String)) {
                throw x.syntaxError("Missing value");
            }
            newJo.accumulate(attribute, keepStrings ? ((String) token) : XML.stringToValue((String) token));
            token = null;
        } else {
            newJo.accumulate(attribute, "");
        }
        return token;
    }

    private static void prepareJSONArray(boolean arrayForm, JSONArray ja, JSONArray newJa, JSONObject newJo, String tagName) {
        if (arrayForm) {
            newJa.put(tagName);
            if (ja != null) {
                ja.put(newJa);
            }
        } else {
            newJo.put(TAG_NAME, tagName);
            if (ja != null) {
                ja.put(newJo);
            }
        }
    }

    private static void getTokenForBang(XMLTokener x, JSONArray ja) {
        Object token;
        int i;
        char c;
        // <!
        c = x.next();
        if (c == '-') {
            validateForHyphen(x);
        } else if (c == '[') {
            validateForOpenSqureBrkt(x, ja);
        } else {
            i = 1;
            do {
                token = x.nextMeta();
                if (token == null) {
                    throw x.syntaxError("Missing '>' after '<!'.");
                } else if (token == XML.LT) {
                    i += 1;
                } else if (token == XML.GT) {
                    i -= 1;
                }
            } while (i > 0);
        }
    }

    private static void validateForHyphen(XMLTokener x) {
        if (x.next() == '-') {
            x.skipPast("-->");
        } else {
            x.back();
        }
    }

    private static void validateForOpenSqureBrkt(XMLTokener x, JSONArray ja) {
        Object token;
        token = x.nextToken();
        if (token.equals("CDATA") && x.next() == '[') {
            if (ja != null) {
                ja.put(x.nextCDATA());
            }
        } else {
            throw x.syntaxError("Expected 'CDATA['");
        }
    }

    private static Object getTokenForSlash(XMLTokener x) {
        Object token;
        // Close tag </
        token = x.nextToken();
        if (!(token instanceof String)) {
            throw new JSONException(
                    "Expected a closing name instead of '" +
                            token + "'.");
        }
        if (x.nextToken() != XML.GT) {
            throw x.syntaxError("Misshaped close tag");
        }
        return token;
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child tags.
     * Comments, prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
     *
     * @param string The source string.
     * @return A JSONArray containing the structured data from the XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a JSONArray
     */
    public static com.iexceed.appzillon.json.JSONArray toJSONArray(String string) throws com.iexceed.appzillon.json.JSONException {
        return (com.iexceed.appzillon.json.JSONArray) parse(new XMLTokener(string), true, null, false);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child tags.
     * As opposed to toJSONArray this method does not attempt to convert
     * any text node or attribute value to any type
     * but just leaves it as a string.
     * Comments, prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
     *
     * @param string      The source string.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONArray containing the structured data from the XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a JSONArray
     */
    public static com.iexceed.appzillon.json.JSONArray toJSONArray(String string, boolean keepStrings) throws com.iexceed.appzillon.json.JSONException {
        return (com.iexceed.appzillon.json.JSONArray) parse(new XMLTokener(string), true, null, keepStrings);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child content and tags.
     * As opposed to toJSONArray this method does not attempt to convert
     * any text node or attribute value to any type
     * but just leaves it as a string.
     * Comments, prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
     *
     * @param x           An XMLTokener.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONArray containing the structured data from the XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a JSONArray
     */
    public static com.iexceed.appzillon.json.JSONArray toJSONArray(XMLTokener x, boolean keepStrings) throws com.iexceed.appzillon.json.JSONException {
        return (com.iexceed.appzillon.json.JSONArray) parse(x, true, null, keepStrings);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONArray using the JsonML transform. Each XML tag is represented as
     * a JSONArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child content and tags.
     * Comments, prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
     *
     * @param x An XMLTokener.
     * @return A JSONArray containing the structured data from the XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a JSONArray
     */
    public static com.iexceed.appzillon.json.JSONArray toJSONArray(XMLTokener x) throws com.iexceed.appzillon.json.JSONException {
        return (com.iexceed.appzillon.json.JSONArray) parse(x, true, null, false);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a TAG_NAME property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a CHILD_NODE property which
     * will be an array of strings and JsonML JSONObjects.
     * <p>
     * Comments, prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
     *
     * @param string The XML source text.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(String string) throws com.iexceed.appzillon.json.JSONException {
        return (JSONObject) parse(new XMLTokener(string), false, null, false);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a TAG_NAME property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a CHILD_NODE property which
     * will be an array of strings and JsonML JSONObjects.
     * <p>
     * Comments, prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
     *
     * @param string      The XML source text.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(String string, boolean keepStrings) throws com.iexceed.appzillon.json.JSONException {
        return (JSONObject) parse(new XMLTokener(string), false, null, keepStrings);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a TAG_NAME property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a CHILD_NODE property which
     * will be an array of strings and JsonML JSONObjects.
     * <p>
     * Comments, prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
     *
     * @param x An XMLTokener of the XML source text.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(XMLTokener x) throws com.iexceed.appzillon.json.JSONException {
        return (JSONObject) parse(x, false, null, false);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a TAG_NAME property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a CHILD_NODE property which
     * will be an array of strings and JsonML JSONObjects.
     * <p>
     * Comments, prologs, DTDs, and <code>&lt;[ [ ]]></code> are ignored.
     *
     * @param x           An XMLTokener of the XML source text.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(XMLTokener x, boolean keepStrings) throws com.iexceed.appzillon.json.JSONException {
        return (JSONObject) parse(x, false, null, keepStrings);
    }


    /**
     * Reverse the JSONML transformation, making an XML text from a JSONArray.
     *
     * @param ja A JSONArray.
     * @return An XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a string
     */
    public static String toString(com.iexceed.appzillon.json.JSONArray ja) throws com.iexceed.appzillon.json.JSONException {
        int i;
        JSONObject jo;
        int length;
        Object object;
        StringBuilder sb = new StringBuilder();
        String tagName;

        // Emit <tagName

        tagName = ja.getString(0);
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);

        object = ja.opt(1);
        if (object instanceof JSONObject) {
            i = 2;
            jo = (JSONObject) object;
            // Emit the attributes
            // Don't use the new entrySet API to maintain Android support
            for (final String key : jo.keySet()) {
                final Object value = jo.opt(key);
                XML.noSpace(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(XML.escape(key));
                    sb.append('=');
                    sb.append('"');
                    sb.append(XML.escape(value.toString()));
                    sb.append('"');
                }
            }
        } else {
            i = 1;
        }

        // Emit content in body

        length = ja.length();
        if (i >= length) {
            sb.append('/');
            sb.append('>');
        } else {
            sb.append('>');
            iterateOverJSONArray(ja, i, length, sb);
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }

    private static void iterateOverJSONArray(JSONArray ja, int i, int length, StringBuilder sb) {
        Object object;
        do {
            object = ja.get(i);
            i += 1;
            if (object != null) {
                if (object instanceof String) {
                    sb.append(XML.escape(object.toString()));
                } else if (object instanceof JSONObject) {
                    sb.append(toString((JSONObject) object));
                } else if (object instanceof JSONArray) {
                    sb.append(toString((JSONArray) object));
                } else {
                    sb.append(object.toString());
                }
            }
        } while (i < length);
    }

    /**
     * Reverse the JSONML transformation, making an XML text from a JSONObject.
     * The JSONObject must contain a TAG_NAME property. If it has children,
     * then it must have a CHILD_NODE property containing an array of objects.
     * The other properties are attributes with string values.
     *
     * @param jo A JSONObject.
     * @return An XML string.
     * @throws com.iexceed.appzillon.json.JSONException Thrown on error converting to a string
     */
    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb = new StringBuilder();
        int i;
        com.iexceed.appzillon.json.JSONArray ja;
        int length;
        Object object;
        String tagName;

        //Emit <tagName
        tagName = jo.optString(TAG_NAME);
        if (tagName == null) {
            return XML.escape(jo.toString());
        }
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);

        iterateOverJSONOBJECT(jo, sb);

        //Emit content in body
        ja = jo.optJSONArray(CHILD_NODE);
        if (ja == null) {
            sb.append('/');
            sb.append('>');
        } else {
            sb.append('>');
            length = ja.length();
            for (i = 0; i < length; i += 1) {
                object = ja.get(i);
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(XML.escape(object.toString()));
                    } else if (object instanceof JSONObject) {
                        sb.append(toString((JSONObject) object));
                    } else if (object instanceof com.iexceed.appzillon.json.JSONArray) {
                        sb.append(toString((JSONArray) object));
                    } else {
                        sb.append(object.toString());
                    }
                }
            }
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }

    private static void iterateOverJSONOBJECT(JSONObject jo, StringBuilder sb) {
        Object value;
        //Emit the attributes

        // Don't use the new entrySet API to maintain Android support
        for (final String key : jo.keySet()) {
            if (!TAG_NAME.equals(key) && !CHILD_NODE.equals(key)) {
                XML.noSpace(key);
                value = jo.opt(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(XML.escape(key));
                    sb.append('=');
                    sb.append('"');
                    sb.append(XML.escape(value.toString()));
                    sb.append('"');
                }
            }
        }
    }
}
