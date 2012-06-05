package com.ivyinfo.donkey.http.api;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Constant;

public class DonkeyGearmanMessage {

	private JSONObject json = new JSONObject();

	public static final String conf_create = "conf.create";
	public static final String conf_join = "conf.join";
	public static final String conf_unjoin = "conf.unjoin";
	public static final String conf_destroy = "conf.destroy";
	public static final String conn_mute = "conn.mute";
	public static final String conn_unmute = "conn.unmute";
	public static final String conf_announce = "conf.announce";
	public static final String conf_record = "conf.record";
	public static final String conf_stoprecord = "conf.stoprecord";
	public static final String conf_info_destroy = "conf.info.destroy";

	public static final String conn_cdr = "conn.cdr";

	public static final String conference_exception = "conference.exception";
	public static final String conference_sip_error = "conference.sip.error";
	public static final String conference_info_exception = "conference.info.exception";
	public static final String user_sip_error = "user.sip.error";
	public static final String user_exception = "user.exception";

	private DonkeyGearmanMessage(String eventType,
			SipApplicationSession sipAppSession) {
		String reqId = (String) sipAppSession.getAttribute(Constant.ReqID);
		String appId = (String) sipAppSession.getAttribute(Constant.AppID);
		String callBackURL = (String) sipAppSession
				.getAttribute(Constant.CallBackURL);
		String confSessionId = sipAppSession.getId();

		if (null == appId || null == callBackURL || null == confSessionId
				|| appId.length() <= 0 || callBackURL.length() <= 0
				|| confSessionId.length() <= 0) {
			throw new IllegalArgumentException();
		}

		put(Constant.AppID, appId);
		put(Constant.CallBackURL, callBackURL);
		put(Constant.ReqID, reqId);
		put(Constant.Type, eventType);
		put(Constant.Conference, confSessionId);
	}

	private void put(String key, Object value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Object get(String key) throws JSONException {
		return json.get(key);
	}

	public void remove(String key) {
		json.remove(key);
	}

	public String toJSON() {
		return json.toString();
	}

	public static DonkeyGearmanMessage DonkeyException(String exceptionName,
			String eventType, SipApplicationSession sipAppSession, String eMsg) {
		DonkeyGearmanMessage msg = new DonkeyGearmanMessage(eventType,
				sipAppSession);

		msg.put(Constant.Status, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		if (conference_exception.equals(exceptionName)
				|| conference_info_exception.equals(exceptionName)) {
			msg.put(Constant.Reason, eMsg);
		}
		if (conference_sip_error.equals(exceptionName)) {
			msg.put(Constant.Reason, Constant.SipError + ":" + eMsg);
		}

		return msg;
	}

	public static DonkeyGearmanMessage DonkeyUserException(
			String exceptionName, String eventType, SipSession session,
			String eMsg) {
		SipApplicationSession sipAppSession = session.getApplicationSession();
		DonkeyGearmanMessage msg = new DonkeyGearmanMessage(eventType,
				sipAppSession);
		msg.put(Constant.Status, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		String sipUri = (String) session
				.getAttribute(ConferenceManager.USER_SIP_URI);
		msg.put(Constant.SipURI, sipUri);
		msg.put(Constant.Attendee, session.getId());

		if (user_exception.equals(exceptionName)) {
			msg.put(Constant.Reason, eMsg);
		}
		if (user_sip_error.equals(exceptionName)) {
			msg.put(Constant.Reason, Constant.SipError + ":" + eMsg);
		}

		return msg;
	}

	public static DonkeyGearmanMessage DonkeyUserJoining(SipSession session) {
		return DonkeyUserJoin(session, false, null, null);
	}

	public static DonkeyGearmanMessage DonkeyUserJoin(SipSession session) {
		return DonkeyUserJoin(session, false, System.currentTimeMillis(), null);
	}

	public static DonkeyGearmanMessage DonkeyUserUnJoin(SipSession session,
			Long startTime, Long endTime) {
		return DonkeyUserJoin(session, true, startTime, endTime);
	}

	private static DonkeyGearmanMessage DonkeyUserJoin(SipSession session,
			boolean isUnjoin, Long startTime, Long endTime) {
		SipApplicationSession sipAppSession = session.getApplicationSession();
		String sipUri = (String) session
				.getAttribute(ConferenceManager.USER_SIP_URI);
		DonkeyGearmanMessage msg = null;

		if (isUnjoin) {
			msg = new DonkeyGearmanMessage(DonkeyGearmanMessage.conf_unjoin,
					sipAppSession);
			msg.put(Constant.CallStartTime, startTime);
			msg.put(Constant.CallEndTime, endTime);
			msg.put(Constant.Status, HttpServletResponse.SC_OK);
		}
		// has an user join
		else {
			msg = new DonkeyGearmanMessage(DonkeyGearmanMessage.conf_join,
					sipAppSession);

			if (null == startTime) {
				msg.put(Constant.Status, HttpServletResponse.SC_CONTINUE);
			} else {
				msg.put(Constant.Status, HttpServletResponse.SC_OK);
			}
		}

		if (msg != null) {
			msg.put(Constant.SipURI, sipUri);
			msg.put(Constant.Attendee, session.getId());
		}
		return msg;
	}

	public static DonkeyGearmanMessage ConferenceCreated(String eventType,
			SipApplicationSession sipAppSession) {
		DonkeyGearmanMessage msg = new DonkeyGearmanMessage(eventType,
				sipAppSession);
		msg.put(Constant.Status, HttpServletResponse.SC_CREATED);
		return msg;
	}

	public static DonkeyGearmanMessage ConferenceDestroied(String eventType,
			SipApplicationSession sipAppSession) {
		DonkeyGearmanMessage msg = new DonkeyGearmanMessage(eventType,
				sipAppSession);
		msg.put(Constant.Status, HttpServletResponse.SC_OK);
		return msg;
	}

	public static DonkeyGearmanMessage ConferenceRecorded(String eventType,
			SipApplicationSession sipAppSession, String recordPath,
			String recordLength) {
		DonkeyGearmanMessage msg = new DonkeyGearmanMessage(eventType,
				sipAppSession);
		msg.put(Constant.Status, HttpServletResponse.SC_CREATED);
		msg.put(Constant.RecordURL, recordPath);
		msg.put(Constant.RecordLength, recordLength);
		return msg;
	}

	public static DonkeyGearmanMessage ConnMute(String eventType,
			SipSession session) {
		SipApplicationSession sipAppSession = session.getApplicationSession();
		String sipUri = (String) session
				.getAttribute(ConferenceManager.USER_SIP_URI);
		DonkeyGearmanMessage msg = new DonkeyGearmanMessage(eventType,
				sipAppSession);
		msg.put(Constant.Status, HttpServletResponse.SC_OK);
		msg.put(Constant.Attendee, session.getId());
		msg.put(Constant.SipURI, sipUri);
		return msg;
	}

}
