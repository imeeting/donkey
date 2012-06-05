package com.ivyinfo.donkey.http.api;

import org.json.JSONException;
import org.json.JSONObject;

import com.ivyinfo.donkey.Constant;

public class DonkeyResponseMessage {
	
	private JSONObject json = new JSONObject();
	
	private DonkeyResponseMessage(){
		//
	}
	
	private void put(String key, Object value){
		try {
			json.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String toJSON(){
		return json.toString();
	}	
	
	public static DonkeyResponseMessage ConferenceSession(String confSession){
		DonkeyResponseMessage msg = new DonkeyResponseMessage();
		msg.put(Constant.Conference, confSession);
		return msg;
	}
	
	public static DonkeyResponseMessage AttendeeSession(String confSession, String attendeeSession){
		DonkeyResponseMessage msg = new DonkeyResponseMessage();
		msg.put(Constant.Conference, confSession);
		msg.put(Constant.Attendee, attendeeSession);
		return msg;
	}	
	
	public static DonkeyResponseMessage InternalException(Exception e){
		DonkeyResponseMessage msg = new DonkeyResponseMessage();
		msg.put(Constant.Reason, e.getMessage());
		return msg;
	}	
	
	public static DonkeyResponseMessage GeneralError(String errorInfo) {
		DonkeyResponseMessage msg = new DonkeyResponseMessage();
		msg.put(Constant.Reason, errorInfo);
		return msg;
	}
}
