package com.iexceed.common;

import org.json.JSONException;
import org.json.JSONObject;

public class Notification {
	private String msgID;
	private String time;
	private String message;
	private String readSts;

	
	public String getReadSts() {
		return readSts;
	}

	public void setReadSts(String readSts) {
		this.readSts = readSts;
	}

	public Notification(String l_id, String l_time, String l_msg,String l_readSts){
		this.msgID = l_id;
		this.time = l_time;
		this.message = l_msg;
		this.readSts=l_readSts;
	}

	public final String getMsgID() {
		return msgID;
	}

	public final void setMsgID(String msgID) {
		this.msgID = msgID;
	}

	public final String getTime() {
		return time;
	}

	public final void setTime(String time) {
		this.time = time;
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public JSONObject getJsonobject() throws JSONException{
		  JSONObject obj = new JSONObject();
	            obj.put("ID", msgID);
	            obj.put("MSG", message);
	            obj.put("Timestamp", time);
	            obj.put("read", readSts);
	        return obj;
	}
}
