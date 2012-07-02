package com.richitec.donkey.conference.message;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class NotifyMessage {
	
	public static final String appId = "appId";
	public static final String reqId = "reqId";
	public static final String conference = "conference";
	public static final String sipUri = "sipUri";
	
	public static final String type = "type";
	public static final String status = "status";
	public static final String method = "method";
	public static final String state = "state";
	
	public static final String EV_CONFERENCE_CREATE_SUCCESS = "conf.create.success";
	public static final String EV_CONFERENCE_CREATE_FAILED = "conf.create.failed";
	public static final String EV_CONFERENCE_DESTROY_SUCCESS = "conf.destroy.success";	
	public static final String EV_CONFERENCE_STATUS_CONFLICT = "conf.status.conflict";
	
	public static final String EV_ATTENDEE_CALL_ESTABLISHED = "attendee.call.established";
	public static final String EV_ATTENDEE_CALL_FAILED = "attendee.call.failed";
	public static final String EV_ATTENDEE_CALL_TERMINATED = "attendee.call.terminated";
	public static final String EV_ATTENDEE_STATUS_CONFLICT = "attendee.status.conflit";
	
	public static class Message {
		
		private JSONObject json;
		
		public Message(String eventType){
			json = new JSONObject();
			put(NotifyMessage.type, eventType);
		}
		
		public void put(String key, Object value) {
			try {
				json.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		public String toString(){
			return json.toString();
		}
	}

	public static class ConferenceCreateSuccess extends Message {
		public ConferenceCreateSuccess() {
			super(NotifyMessage.EV_CONFERENCE_CREATE_SUCCESS);
			put(NotifyMessage.status, HttpServletResponse.SC_CREATED);
		}
	}
	
	public static class ConferenceCreateError extends Message {
		public ConferenceCreateError() {
			super(NotifyMessage.EV_CONFERENCE_CREATE_FAILED);
			put(NotifyMessage.status, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}	
	
	public static class ConferenceDestroySuccess extends Message {
		public ConferenceDestroySuccess() {
			super(NotifyMessage.EV_CONFERENCE_DESTROY_SUCCESS);
			put(NotifyMessage.status, HttpServletResponse.SC_OK);
		}
	}
	
	public static class AttendeeCallEstablished extends Message {
		public AttendeeCallEstablished(String sipUri) {
			super(NotifyMessage.EV_ATTENDEE_CALL_ESTABLISHED);
			put(NotifyMessage.status, HttpServletResponse.SC_OK);
			put(NotifyMessage.sipUri, sipUri);
		}
	}
	
	public static class AttendeeCallFailed extends Message {
		public AttendeeCallFailed(String sipUri, int status) {
			super(NotifyMessage.EV_ATTENDEE_CALL_FAILED);
			put(NotifyMessage.status, status);
			put(NotifyMessage.sipUri, sipUri);
		}
	}
	
	public static class AttendeeCallTerminated extends Message {
		public AttendeeCallTerminated(String sipUri) {
			super(NotifyMessage.EV_ATTENDEE_CALL_TERMINATED);
			put(NotifyMessage.status, HttpServletResponse.SC_OK);
			put(NotifyMessage.sipUri, sipUri);
		}
	}
	
	public static class ConferenceStatusConflict extends Message {
		public ConferenceStatusConflict(String method, String sipUri, String state) {
			super(NotifyMessage.EV_CONFERENCE_STATUS_CONFLICT);
			put(NotifyMessage.status, HttpServletResponse.SC_CONFLICT);
			put(NotifyMessage.method, method);
			put(NotifyMessage.state, state);
			if (null != sipUri && sipUri.length()>0){
				put(NotifyMessage.sipUri, sipUri);
			}
		}
	}
	
	public static class AttendeeStatusConflict extends Message {
		public AttendeeStatusConflict(String method, String sipUri, String state){
			super(NotifyMessage.EV_ATTENDEE_STATUS_CONFLICT);
			put(NotifyMessage.status, HttpServletResponse.SC_CONFLICT);
			put(NotifyMessage.method, method);
			put(NotifyMessage.state, state);
		}
	}
}
