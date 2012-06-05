package com.ivyinfo.donkey.http.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;
import com.ivyinfo.donkey.Constant;
import com.ivyinfo.donkey.http.api.DonkeyResponse;
import com.ivyinfo.donkey.http.api.DonkeyResponseMessage;
import com.ivyinfo.donkey.ms.MediaServerManager;
import com.ivyinfo.util.DonkeyUtil;

public abstract class DonkeyRequest implements IRequest {

	protected static MediaServerManager msManager = MediaServerManager
			.getInstance();

	@Override
	public abstract void execute(HttpServletRequest request,
			HttpServletResponse response);

	protected void cancelInviteRequest(SipSession session) throws IOException {
		SipServletRequest invite = (SipServletRequest) session
				.getAttribute(ConferenceManager.INVITE_USER_REQUEST);
		if (null != invite) {
			SipServletRequest cancel = invite.createCancel();
			cancel.send();
		}
	}

	protected SipSession checkAttendeeSession(SipSession session) {
		return DonkeyUtil.checkMatchedSession(session,
				ConferenceManager.INVITE_USER_REQUEST);
	}

	protected SipApplicationSession getSipApplicationSession(
			HttpServletRequest request, HttpServletResponse response) {
		String confSession = request.getParameter(Constant.Conference);
		if (null == confSession || confSession.length() <= 0) {
			DonkeyResponse.BadRequest(response);
			return null;
		}

		SipApplicationSession sipAppSession = ConferenceManager
				.getSipSessionsUtil().getApplicationSessionById(confSession);
		if (null == sipAppSession) {
			DonkeyResponse.NotFound(response,
					DonkeyResponseMessage.ConferenceSession(confSession));
			return null;
		}

		if (!sipAppSession.isValid()) {
			DonkeyResponse.Gone(response,
					DonkeyResponseMessage.ConferenceSession(confSession));
			return null;
		}

		return sipAppSession;
	}

	protected SipSession getSipSession(HttpServletRequest request,
			HttpServletResponse response) {
		SipApplicationSession sipAppSession = getSipApplicationSession(request,
				response);
		if (sipAppSession == null) {
			return null;
		}

		String attendeeSession = request.getParameter(Constant.Attendee);
		SipSession sipSession = (SipSession) sipAppSession
				.getSipSession(attendeeSession);

		if (null == sipSession) {
			DonkeyResponse.NotFound(response, DonkeyResponseMessage
					.AttendeeSession(sipAppSession.getId(), attendeeSession));
			return null;
		}

		if (!sipSession.isValid()) {
			DonkeyResponse.Gone(response, DonkeyResponseMessage
					.AttendeeSession(sipAppSession.getId(), attendeeSession));
			return null;
		}

		return sipSession;
	}

}
